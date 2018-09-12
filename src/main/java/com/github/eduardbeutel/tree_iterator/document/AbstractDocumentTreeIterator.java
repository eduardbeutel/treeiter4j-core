package com.github.eduardbeutel.tree_iterator.document;

import com.github.eduardbeutel.tree_iterator.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

public abstract class AbstractDocumentTreeIterator<Document, Node>
{

    private Conditions<Node> conditions = new Conditions<>(this);
    private Operations<Node> operations = new Operations<>(this);
    private Document document;
    private TraversalDirection direction;
    private List<Command> commands = new ArrayList<>();
    private Command currentCommand;
    private boolean currentCommandContainsWhenRoot = false;
    private CommandExecutor<Node> executor = new CommandExecutor<>();

    public static class Conditions<Node>
    {

        public PathCondition IS_ROOT_CONDITION = new PathCondition(path -> path.lastIndexOf('/') == 0);

        private AbstractDocumentTreeIterator iterator;

        private Conditions(AbstractDocumentTreeIterator iterator)
        {
            this.iterator = iterator;
        }

        public void execute()
        {
            iterator.clearLastCommandIfEmpty();
            try
            {
                iterator.iterate(iterator.getDocument());
            }
            catch (StopIterationException e)
            {
            }
        }

        public Operations<Node> when(Predicate<Node> predicate)
        {
            return iterator.addCondition(new NodeCondition<>(predicate)).getOperations();
        }

        public Operations<Node> whenNot(Predicate<Node> predicate)
        {
            return when(predicate.negate());
        }

        public Operations<Node> always()
        {
            return when(o -> true);
        }

        public Operations<Node> whenId(String id)
        {
            return iterator.addCondition(new IdCondition(PredicateCreator.stringEquals(id))).getOperations();
        }

        public Operations<Node> whenPath(String path)
        {
            return iterator.addCondition(new PathCondition(PredicateCreator.stringEquals(path))).getOperations();
        }

        public Operations<Node> whenIdMatches(String pattern)
        {
            return iterator.addCondition(new IdCondition(PredicateCreator.stringMatches(pattern))).getOperations();
        }

        public Operations<Node> whenPathMatches(String pattern)
        {
            return iterator.addCondition(new PathCondition(PredicateCreator.stringMatches(pattern))).getOperations();
        }

        public Operations<Node> whenLeaf()
        {
            return when(iterator::isLeaf);
        }

        public Operations<Node> whenNotLeaf()
        {
            return when(((Predicate<Node>) iterator::isLeaf).negate());
        }

        public Operations<Node> whenRoot()
        {
            return iterator.addRootCondition(IS_ROOT_CONDITION).getOperations();
        }

    }

    public static class Operations<Node>
    {

        private AbstractDocumentTreeIterator iterator;

        private Operations(AbstractDocumentTreeIterator iterator)
        {
            this.iterator = iterator;
        }

        public Conditions<Node> and()
        {
            return iterator.getConditions();
        }

        public Conditions<Node> then(Runnable runnable)
        {
            return then((node) -> runnable.run());
        }

        public Conditions<Node> then(Consumer<Node> consumer)
        {
            Consumer<IterationStep<Node>> executeConsumer = step -> consumer.accept(step.getNode());
            return thenForStep(executeConsumer);
        }

        public Conditions<Node> then(BiConsumer<Node, String> consumer)
        {
            Consumer<IterationStep<Node>> executeConsumer = step -> consumer.accept(step.getNode(), step.getId());
            return thenForStep(executeConsumer);
        }

        public Conditions<Node> then(TriConsumer<Node, String, String> consumer)
        {
            Consumer<IterationStep<Node>> executeConsumer = step -> consumer.accept(step.getNode(), step.getId(), step.getPath());
            return thenForStep(executeConsumer);
        }

        public Conditions<Node> collect(AtomicReference<Node> reference)
        {
            return then(node -> reference.set(node));
        }

        public Conditions<Node> collect(Collection<Node> collection)
        {
            return then(node -> collection.add(node));
        }

        public Conditions<Node> collectById(Map<String, Node> map)
        {
            return then((node, id) -> map.put(id, node));
        }

        public Conditions<Node> collectByPath(Map<String, Node> map)
        {
            return then((node, id, path) -> map.put(path, node));
        }

        public Conditions<Node> stop()
        {
            return then(() ->
            {
                throw new StopIterationException();
            });
        }

        public Conditions<Node> skip()
        {
            if (TraversalDirection.BOTTOM_UP == iterator.getDirection())
                throw new UnsupportedFeatureException("skip() can not be used in bottomUp() mode.");
            Consumer<IterationStep<Node>> markToSkip = step -> step.setSkip(true);
            return thenForStep(markToSkip);
        }

        public Conditions<Node> remove()
        {
            if (iterator.currentCommandContainsWhenRoot)
                throw new UnsupportedFeatureException("The root element can not be removed.");
            Consumer<IterationStep<Node>> markForRemoval = step -> step.setRemove(true);
            return thenForStep(markForRemoval);
        }

        public Conditions<Node> replace(Supplier<Node> supplier)
        {
            Consumer<IterationStep<Node>> markForReplacement = step -> step.setReplacement(supplier.get());
            return thenForStep(markForReplacement);
        }

        public Conditions<Node> replace(Function<Node, Node> function)
        {
            Consumer<IterationStep<Node>> markForReplacement = step -> step.setReplacement(function.apply(step.getNode()));
            return thenForStep(markForReplacement);
        }

        public Conditions<Node> replace(BiFunction<Node, String, Node> function)
        {
            Consumer<IterationStep<Node>> markForReplacement = step -> step.setReplacement(function.apply(step.getNode(), step.getId()));
            return thenForStep(markForReplacement);
        }

        public Conditions<Node> replace(TriFunction<Node, String, String, Node> function)
        {
            Consumer<IterationStep<Node>> markForReplacement = step -> step.setReplacement(function.apply(step.getNode(), step.getId(), step.getPath()));
            return thenForStep(markForReplacement);
        }

        protected Conditions<Node> thenForStep(Consumer<IterationStep<Node>> consumer)
        {
            return iterator.addOperation(consumer).getConditions();
        }

    }

    protected abstract void iterate(Document document);

    protected abstract boolean isLeaf(Node node);

    protected AbstractDocumentTreeIterator(Document document, TraversalDirection direction)
    {
        this.document = document;
        this.direction = direction;
    }

    protected AbstractDocumentTreeIterator<Document, Node> addCondition(Condition condition)
    {
        if (currentCommand == null) newCommand();
        currentCommand.addCondition(condition);
        return this;
    }

    protected AbstractDocumentTreeIterator<Document, Node> addRootCondition(Condition condition)
    {
        addCondition(condition);
        currentCommandContainsWhenRoot = true;
        return this;
    }

    protected AbstractDocumentTreeIterator<Document, Node> addOperation(Object operation)
    {
        currentCommand.setOperation(operation);
        newCommand();
        return this;
    }

    protected void newCommand()
    {
        Command command = new Command();
        commands.add(command);
        currentCommand = command;
        currentCommandContainsWhenRoot = false;
    }

    protected void clearLastCommandIfEmpty()
    {
        if (commands.isEmpty()) return;
        int lastIndex = commands.size() - 1;
        Command lastCommand = commands.get(lastIndex);
        if (lastCommand.getConditions().isEmpty()) commands.remove(lastIndex);
    }

    protected void executeCommands(IterationStep<Node> step)
    {
        for (Command command : getCommands())
        {
            getExecutor().execute(command, step);
            if (step.isSkip() || step.isRemove() || step.isReplace()) return;
        }
    }

    protected IterationStep<Node> createChildStep(IterationStep<Node> parentStep, Node child, String childId)
    {
        String childPath = parentStep.getPath() + "/" + childId;
        return new IterationStep<>(child, childId, childPath);
    }

    //

    protected Conditions<Node> getConditions()
    {
        return conditions;
    }

    protected Operations<Node> getOperations()
    {
        return operations;
    }

    protected Document getDocument()
    {
        return document;
    }

    protected List<Command> getCommands()
    {
        return commands;
    }

    protected CommandExecutor<Node> getExecutor()
    {
        return executor;
    }

    protected TraversalDirection getDirection()
    {
        return direction;
    }

}

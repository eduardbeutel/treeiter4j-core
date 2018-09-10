package com.github.eduardbeutel.tree_iterator.document;

import com.github.eduardbeutel.tree_iterator.core.*;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractDocumentTreeIterator<Document, Node>
{

    private Conditions<Node> conditions = new Conditions<>(this);
    private Operations<Node> operations = new Operations<>(this);
    private Document document;
    private TraversalDirection direction;
    private List<Command> commands = new ArrayList<>();
    private Command currentCommand;
    private CommandExecutor<Node> executor = new CommandExecutor<>();

    public static class Conditions<Node>
    {

        public BiPredicate<String,String> IS_ROOT_PREDICATE = (id,path) -> path.equals("/"+id);

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
            return iterator.addCondition(ConditionType.NODE, predicate).getOperations();
        }

        public Operations<Node> whenNot(Predicate<Node> predicate)
        {
            return when(predicate.negate());
        }

        public Operations<Node> always()
        {
            return when(PredicateCreator.always());
        }

        public Operations<Node> whenId(String id)
        {
            return iterator.addCondition(ConditionType.ID, PredicateCreator.stringEquals(id)).getOperations();
        }

        public Operations<Node> whenPath(String path)
        {
            return iterator.addCondition(ConditionType.PATH, PredicateCreator.stringEquals(path)).getOperations();
        }

        public Operations<Node> whenIdMatches(String pattern)
        {
            return iterator.addCondition(ConditionType.ID, PredicateCreator.stringMatches(pattern)).getOperations();
        }

        public Operations<Node> whenPathMatches(String pattern)
        {
            return iterator.addCondition(ConditionType.PATH, PredicateCreator.stringMatches(pattern)).getOperations();
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
            return iterator.addCondition(ConditionType.ID_PATH, IS_ROOT_PREDICATE).getOperations();
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

        public Conditions<Node> then(Consumer<Node> consumer)
        {
            return iterator.addOperation(OperationType.NODE_CONSUMER, consumer).getConditions();
        }

        public Conditions<Node> collect(AtomicReference<Node> reference)
        {
            return then(OperationCreator.setReference(reference));
        }

        public Conditions<Node> collect(Collection<Node> collection)
        {
            return then(OperationCreator.addToCollection(collection));
        }

        public Conditions<Node> stop()
        {
            return then(OperationCreator.throwException(new StopIterationException()));
        }

        public Conditions<Node> skip()
        {
            if(TraversalDirection.BOTTOM_UP == iterator.getDirection()) throw new UnsupportedFeatureException("skip() can not be used in bottomUp() mode.");
            Consumer<IterationStep<Node>> setSkipTrue = step -> step.setSkip(true);
            return iterator.addOperation(OperationType.STEP_CONSUMER, setSkipTrue).getConditions();
        }

        public Conditions<Node> remove()
        {
            Consumer<IterationStep<Node>> setRemoveTrue = step -> step.setRemove(true);
            return iterator.addOperation(OperationType.STEP_CONSUMER, setRemoveTrue).getConditions();
        }

        public Conditions<Node> replace(Supplier<Node> supplier)
        {
            Consumer<IterationStep<Node>> setReplacement = step -> step.setReplacement(supplier.get());
            return iterator.addOperation(OperationType.STEP_CONSUMER, setReplacement).getConditions();
        }

    }

    protected abstract void iterate(Document document);

    protected abstract boolean isLeaf(Node node);

    protected AbstractDocumentTreeIterator(Document document, TraversalDirection direction)
    {
        this.document = document;
        this.direction = direction;
    }

    protected AbstractDocumentTreeIterator<Document, Node> addCondition(ConditionType type, Object condition)
    {
        if (currentCommand == null) newCommand();
        currentCommand.addCondition(new Condition(type, condition));
        return this;
    }

    protected AbstractDocumentTreeIterator<Document, Node> addOperation(OperationType type, Object operation)
    {
        currentCommand.setOperation(new Operation(type, operation));
        newCommand();
        return this;
    }

    protected void newCommand()
    {
        Command command = new Command();
        currentCommand = command;
        commands.add(command);
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
            if(step.isSkip() || step.isRemove() || step.isReplace()) return;
        }
    }

    protected IterationStep<Node> createChildStep(IterationStep<Node> parentStep, Node child, String childId)
    {
        String childPath = parentStep.getPath() + "/" + childId;
        return new IterationStep<>(
                child,
                childId,
                childPath
        );
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

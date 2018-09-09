package com.github.eduardbeutel.tree_iterator.document;

import com.github.eduardbeutel.tree_iterator.core.OperationCreator;
import com.github.eduardbeutel.tree_iterator.core.PredicateCreator;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractDocumentTreeIterator<Document, Node>
{

    public final Predicate<Node> ALWAYS_PREDICATE = node -> true;
    public final BiPredicate<String,String> IS_ROOT_PREDICATE = (id,path) -> path.equals("/"+id);

    private Conditions<Node> conditions = new Conditions<>(this);
    private Operations<Node> operations = new Operations<>(this);
    private Document document;
    private List<Command> commands = new ArrayList<>();
    private Command currentCommand;
    private CommandExecutor<Node> executor = new CommandExecutor<>();

    public static class Conditions<Node>
    {

        private AbstractDocumentTreeIterator iterator;

        private Conditions(AbstractDocumentTreeIterator iterator)
        {
            this.iterator = iterator;
        }

        public void execute()
        {
            iterator.clearLastCommandIfEmpty();
            iterator.iterate(iterator.getDocument());
        }

        public Operations<Node> when(Predicate<Node> predicate)
        {
            return iterator.addCondition(ConditionType.NODE, predicate).getOperations();
        }

        public Operations<Node> whenNot(Predicate<Node> predicate)
        {
            return iterator.addCondition(ConditionType.NODE, predicate.negate()).getOperations();
        }

        public Operations<Node> always()
        {
            return iterator.addCondition(ConditionType.NODE, iterator.ALWAYS_PREDICATE).getOperations();
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
            return iterator.addCondition(ConditionType.ID_PATH, iterator.IS_ROOT_PREDICATE).getOperations();
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
            return iterator.addOperation(OperationType.NODE_CONSUMER, OperationCreator.setReference(reference)).getConditions();
        }

        public Conditions<Node> collect(Collection<Node> collection)
        {
            return iterator.addOperation(OperationType.NODE_CONSUMER, OperationCreator.addToCollection(collection)).getConditions();
        }

    }

    protected abstract void iterate(Document document);

    protected abstract boolean isLeaf(Node node);

    protected AbstractDocumentTreeIterator(Document document)
    {
        this.document = document;
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
        }
    }

    protected IterationStep<Node> createChildStep(IterationStep<Node> parentStep, Node child, String childId)
    {
        String childPath = parentStep.getPath() + "/" + childId;
        return new IterationStep<>(
                child,
                childId,
                childPath,
                parentStep.getNode()
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


}

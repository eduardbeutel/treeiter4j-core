package com.github.eduardbeutel.tree_iterator.document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class AbstractDocumentTreeIterator<Document, Node>
{

    public final Predicate<Node> ALWAYS_PREDICATE = node -> true;

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
            return iterator.addCondition(ConditionType.ID, id).getOperations();
        }

        public Operations<Node> whenPath(String path)
        {
            return iterator.addCondition(ConditionType.PATH, path).getOperations();
        }

        public Operations<Node> whenIdMatches(String pattern)
        {
            return iterator.addCondition(ConditionType.ID_PATTERN, Pattern.compile(pattern)).getOperations();
        }

        public Operations<Node> whenPathMatches(String pattern)
        {
            return iterator.addCondition(ConditionType.PATH_PATTERN, Pattern.compile(pattern)).getOperations();
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
            return iterator.addCondition(ConditionType.ROOT, null).getOperations();
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

    }

    protected abstract void iterate(Object object);

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

    protected void executeCommands(Node node, String id, String path)
    {
        for (Command command : getCommands())
        {
            getExecutor().execute(command, node, id, path);
        }
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

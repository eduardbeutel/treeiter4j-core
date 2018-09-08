package com.github.eduardbeutel.tree_iterator.document;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CommandExecutor<Node>
{

    /*
        args[0] = Node
        args[1] = Node Id as String
        args[2] = Node Path as String
   */
    void execute(Command command, Object... args)
    {
        if (args == null || args.length == 0) return;
        if (evaluateConditions(command, args))
        {
            executeOperation(command, args);
        }
    }

    protected boolean evaluateConditions(Command command, Object... args)
    {
        Node node = (Node) args[0];
        String id = (String) args[1];
        String path = (String) args[2];

        boolean result = true;
        for (Condition condition : command.getConditions())
        {
            switch (condition.getType())
            {
                case NODE:
                    result &= ((Predicate<Node>) condition.getObject()).test(node);
                    break;
                case ID:
                    result &= equals((String) condition.getObject(), id);
                    break;
                case PATH:
                    result &= equals((String) condition.getObject(), path);
                    break;
                case ID_PATTERN:
                    result &= matches((Pattern) condition.getObject(), id);
                    break;
                case PATH_PATTERN:
                    result &= matches((Pattern) condition.getObject(), path);
                    break;
                case ROOT:
                    result &= isRoot(id, path);
                    break;
            }
        }
        return result;
    }

    protected void executeOperation(Command command, Object... args)
    {
        Node node = (Node) args[0];
        switch (command.getOperation().getType())
        {
            case NODE_CONSUMER:
                ((Consumer<Node>) command.getOperation().getObject()).accept(node);
                break;
        }
    }

    protected boolean isRoot(String id, String path)
    {
        return ("/" + id).equals(path);
    }

    protected boolean matches(Pattern pattern, String path)
    {
        return pattern.matcher(path).matches();
    }

    protected boolean equals(String left, String right)
    {
        if (left == null && right != null) return false;
        if (left != null && right == null) return false;
        if (left == null && right == null) return true;
        return left.equals(right);
    }

}

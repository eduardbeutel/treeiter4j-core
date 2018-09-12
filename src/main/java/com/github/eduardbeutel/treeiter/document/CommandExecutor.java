package com.github.eduardbeutel.treeiter.document;

import com.github.eduardbeutel.treeiter.common.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandExecutor<Node>
{

    public void execute(Command command, IterationStep<Node> step)
    {
        if (evaluateConditions(command, step))
        {
            executeOperation(command, step);
        }
    }

    protected boolean evaluateConditions(Command command, IterationStep<Node> step)
    {
        boolean result = true;
        for (Condition condition : command.getConditions())
        {
            result &= evaluateCondition(condition, step);
        }
        return result;
    }

    protected boolean evaluateCondition(Condition condition, IterationStep<Node> step)
    {
        if (condition instanceof NodeCondition) return evaluateNodeCondition(condition, step.getNode());
        else if (condition instanceof IdCondition) return evaluateStringCondition(condition, step.getId());
        else if (condition instanceof PathCondition) return evaluateStringCondition(condition, step.getPath());
        else throw new UnsupportedFeatureException("Conditions of type " + condition.getClass().getName() + " are not supported");
    }

    private boolean evaluateStringCondition(Condition condition, String id)
    {
        return ((Predicate<String>) condition.getPredicate()).test(id);
    }

    private boolean evaluateNodeCondition(Condition condition, Node node)
    {
        return ((Predicate<Node>) condition.getPredicate()).test(node);
    }

    protected void executeOperation(Command command, IterationStep<Node> step)
    {
        ((Consumer<IterationStep<Node>>) command.getOperation()).accept(step);
    }


}

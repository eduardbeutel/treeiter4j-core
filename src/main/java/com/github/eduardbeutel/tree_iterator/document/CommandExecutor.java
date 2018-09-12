package com.github.eduardbeutel.tree_iterator.document;

import com.github.eduardbeutel.tree_iterator.core.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandExecutor<Node>
{

    /*
        args[0] = Node
        args[1] = Node Id as String
        args[2] = Node Path as String
   */
    void execute(Command command, IterationStep<Node> step)
    {
        if (step == null) return;
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
            switch (condition.getType())
            {
                case NODE:
                    result &= ((Predicate<Node>) condition.getObject()).test(step.getNode());
                    break;
                case ID:
                    result &= ((Predicate<String>) condition.getObject()).test(step.getId());
                    break;
                case PATH:
                    result &= ((Predicate<String>) condition.getObject()).test(step.getPath());
                    break;
            }
        }
        return result;
    }

    protected void executeOperation(Command command, IterationStep<Node> step)
    {
        ((Consumer<IterationStep<Node>>) command.getOperation()).accept(step);
    }


}

package com.github.eduardbeutel.tree_iterator.dom;

import com.github.eduardbeutel.tree_iterator.core.Condition;
import com.github.eduardbeutel.tree_iterator.core.UnsupportedFeatureException;
import com.github.eduardbeutel.tree_iterator.document.Command;
import com.github.eduardbeutel.tree_iterator.document.CommandExecutor;
import com.github.eduardbeutel.tree_iterator.document.IterationStep;
import org.junit.Test;

public class CommandExecutorTest
{

    @Test(expected = UnsupportedFeatureException.class)
    public void unsupportedConditionType_throwsException()
    {
        // given
        CommandExecutor<String> executor = new CommandExecutor<>();
        Condition<String> unsupportedCondition = new Condition<>( o -> true );
        Command command = new Command();
        command.getConditions().add(unsupportedCondition);

        // when
        executor.execute(command, null);

        // then -> exception
    }
}

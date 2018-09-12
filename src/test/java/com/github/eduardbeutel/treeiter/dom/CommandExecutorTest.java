package com.github.eduardbeutel.treeiter.dom;

import com.github.eduardbeutel.treeiter.common.Condition;
import com.github.eduardbeutel.treeiter.common.UnsupportedFeatureException;
import com.github.eduardbeutel.treeiter.document.Command;
import com.github.eduardbeutel.treeiter.document.CommandExecutor;
import org.junit.Test;

public class CommandExecutorTest
{

    @Test(expected = UnsupportedFeatureException.class)
    public void unsupportedConditionType_throwsException()
    {
        // given
        CommandExecutor<String> executor = new CommandExecutor<>();
        Condition<String> unsupportedCondition = new Condition<>(o -> true);
        Command command = new Command();
        command.getConditions().add(unsupportedCondition);

        // when
        executor.execute(command, null);

        // then -> exception
    }

}

package com.github.eduardbeutel.treeiter.common;

import java.util.function.Predicate;

public class IdCondition extends Condition<String>
{
    public IdCondition(Predicate<String> predicate)
    {
        super(predicate);
    }
}

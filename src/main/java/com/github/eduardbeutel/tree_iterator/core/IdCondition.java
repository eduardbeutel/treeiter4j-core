package com.github.eduardbeutel.tree_iterator.core;

import java.util.function.Predicate;

public class IdCondition extends Condition<String>
{
    public IdCondition(Predicate<String> predicate)
    {
        super(predicate);
    }
}

package com.github.eduardbeutel.tree_iterator.core;

import java.util.function.Predicate;

public class PathCondition extends Condition<String>
{
    public PathCondition(Predicate<String> predicate)
    {
        super(predicate);
    }
}

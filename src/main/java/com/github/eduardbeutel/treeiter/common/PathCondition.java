package com.github.eduardbeutel.treeiter.common;

import java.util.function.Predicate;

public class PathCondition extends Condition<String>
{
    public PathCondition(Predicate<String> predicate)
    {
        super(predicate);
    }
}

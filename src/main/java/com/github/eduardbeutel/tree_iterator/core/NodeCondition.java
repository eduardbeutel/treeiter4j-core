package com.github.eduardbeutel.tree_iterator.core;

import java.util.function.Predicate;

public class NodeCondition<Node> extends Condition<Node>
{
    public NodeCondition(Predicate<Node> predicate)
    {
        super(predicate);
    }
}

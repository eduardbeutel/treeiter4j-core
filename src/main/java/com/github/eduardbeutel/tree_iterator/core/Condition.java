package com.github.eduardbeutel.tree_iterator.core;

import java.util.function.Predicate;

public class Condition<T>
{

    private Predicate<T> predicate;

    public Condition(Predicate<T> predicate)
    {
        this.predicate = predicate;
    }

    public Predicate<T> getPredicate()
    {
        return predicate;
    }

}

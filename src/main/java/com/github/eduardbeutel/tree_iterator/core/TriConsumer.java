package com.github.eduardbeutel.tree_iterator.core;

@FunctionalInterface
public interface TriConsumer<T, U, V>
{
    void accept(T t, U u, V v);
}

package com.github.eduardbeutel.tree_iterator.core;

@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    R apply(T t, U u, V v);
}

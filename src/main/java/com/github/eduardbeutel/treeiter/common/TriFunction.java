package com.github.eduardbeutel.treeiter.common;

@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    R apply(T t, U u, V v);
}

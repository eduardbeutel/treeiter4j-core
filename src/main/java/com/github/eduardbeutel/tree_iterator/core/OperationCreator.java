package com.github.eduardbeutel.tree_iterator.core;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OperationCreator
{

    public static <T> Consumer<T> setReference(AtomicReference<T> reference)
    {
        return node -> reference.set(node);
    }

    public static <T> Consumer<T> addToCollection(Collection<T> collection)
    {
        return node -> collection.add(node);
    }

    public static <T> Consumer<T> throwException(RuntimeException e)
    {
        return node -> {throw e;};
    }

}

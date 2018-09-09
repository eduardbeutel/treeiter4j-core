package com.github.eduardbeutel.tree_iterator.core;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OperationCreator
{

    public static Consumer setReference(AtomicReference reference)
    {
        return node -> reference.set(node);
    }

    public static Consumer addToCollection(Collection collection)
    {
        return node -> collection.add(node);
    }

}

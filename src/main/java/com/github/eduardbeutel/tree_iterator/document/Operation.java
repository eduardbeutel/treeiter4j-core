package com.github.eduardbeutel.tree_iterator.document;

import com.github.eduardbeutel.tree_iterator.core.DynamicTypedObject;

public class Operation extends DynamicTypedObject<OperationType>
{

    public Operation(OperationType operationType, Object object)
    {
        super(operationType, object);
    }
}

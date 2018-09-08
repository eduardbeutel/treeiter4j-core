package com.github.eduardbeutel.tree_iterator.core;

public class DynamicTypedObject<Type extends Enum>
{

    private Type type;
    private Object object;

    public DynamicTypedObject(Type type, Object object)
    {
        this.type = type;
        this.object = object;
    }

    public Type getType()
    {
        return type;
    }

    public Object getObject()
    {
        return object;
    }
}

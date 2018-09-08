package com.github.eduardbeutel.tree_iterator.core;

import java.util.ArrayList;
import java.util.List;

public class GenericCommand<Condition, Operation>
{

    private List<Condition> conditions = new ArrayList<>();
    private Operation operation;

    public void addCondition(Condition condition)
    {
        conditions.add(condition);
    }

    public List<Condition> getConditions()
    {
        return conditions;
    }

    public Operation getOperation()
    {
        return operation;
    }

    public void setOperation(Operation operation)
    {
        this.operation = operation;
    }

}

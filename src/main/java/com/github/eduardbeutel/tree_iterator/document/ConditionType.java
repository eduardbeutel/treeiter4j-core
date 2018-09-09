package com.github.eduardbeutel.tree_iterator.document;

public enum ConditionType
{
    NODE, // Predicate<Node>
    ID, // Predicate<String>
    PATH, // Predicate<String>
    ID_PATH, // BiPredicate<String,String>
}

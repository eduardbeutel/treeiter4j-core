package com.github.eduardbeutel.tree_iterator.document;

public enum ConditionType
{
    NODE, // Predicate<Node>
    ID, // String
    PATH, // String
    ID_PATTERN, // String
    PATH_PATTERN, // String
    ROOT, // boolean
}

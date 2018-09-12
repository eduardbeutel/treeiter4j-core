package com.github.eduardbeutel.tree_iterator.document;

public enum OperationType
{
    NODE_CONSUMER, // Consumer<Node>
    NODE_ID_CONSUMER, // BiConsumer<Node,String>
    NODE_ID_PATH_CONSUMER, // TriConsumer<Node,String,String>
    STEP_CONSUMER // Consumer<IterationStep<Node>>
    ;
}

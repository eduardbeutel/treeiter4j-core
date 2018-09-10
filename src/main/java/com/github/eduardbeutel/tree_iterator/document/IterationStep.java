package com.github.eduardbeutel.tree_iterator.document;

public class IterationStep<Node>
{

    private Node node;
    private String id;
    private String path;
    private Node parent;

    private boolean delete = false;
    private boolean skip = false;

    public IterationStep(Node node, String id, String path, Node parent)
    {
        this.node = node;
        this.id = id;
        this.path = path;
        this.parent = parent;
    }

    public Node getNode()
    {
        return node;
    }

    public String getId()
    {
        return id;
    }

    public String getPath()
    {
        return path;
    }

    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }

    public boolean isSkip()
    {
        return skip;
    }
}

package com.github.eduardbeutel.tree_iterator.document;

public class IterationStep<Node>
{

    private Node node;
    private String id;
    private String path;
    private Node replacement;

    private boolean remove = false;
    private boolean skip = false;

    public IterationStep(Node node, String id, String path)
    {
        this.node = node;
        this.id = id;
        this.path = path;
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

    public boolean isRemove()
    {
        return remove;
    }

    public void setRemove(boolean remove)
    {
        this.remove = remove;
    }

    public boolean isReplace()
    {
        return (replacement != null);
    }

    public Node getReplacement()
    {
        return replacement;
    }

    public void setReplacement(Node replacement)
    {
        this.replacement = replacement;
    }

}

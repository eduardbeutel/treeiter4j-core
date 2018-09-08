package com.github.eduardbeutel.tree_iterator.document;

public class IterationStep<Node>
{

    private Node node;
    private String id;
    private String path;
    private Node parent;

    private boolean delete = false;
    private boolean ignore = false;
    private boolean skip = false;
    private boolean stop = false;

    public IterationStep(Node node, String id, String path, Node parent)
    {
        this.node = node;
        this.id = id;
        this.path = path;
        this.parent = parent;
    }

    public void setDelete(boolean delete)
    {
        this.delete = delete;
    }

    public void setIgnore(boolean ignore)
    {
        this.ignore = ignore;
    }

    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }

    public void setStop(boolean stop)
    {
        this.stop = stop;
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

    public Node getParent()
    {
        return parent;
    }

    public boolean isDelete()
    {
        return delete;
    }

    public boolean isIgnore()
    {
        return ignore;
    }

    public boolean isSkip()
    {
        return skip;
    }

    public boolean isStop()
    {
        return stop;
    }
}

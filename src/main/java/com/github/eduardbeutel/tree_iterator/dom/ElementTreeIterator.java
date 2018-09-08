package com.github.eduardbeutel.tree_iterator.dom;

import com.github.eduardbeutel.tree_iterator.core.TraversalDirection;
import com.github.eduardbeutel.tree_iterator.document.AbstractDocumentTreeIterator;
import com.github.eduardbeutel.tree_iterator.document.IterationStep;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementTreeIterator extends AbstractDocumentTreeIterator<Document, Element>
{

    private TraversalDirection direction;

    private ElementTreeIterator(Document document, TraversalDirection direction)
    {
        super(document);
        this.direction = direction;
    }

    public static Conditions<Element> topDown(Document document)
    {
        return new ElementTreeIterator(document, TraversalDirection.TOP_DOWN).getConditions();
    }

    public static Conditions<Element> bottomUp(Document document)
    {
        return new ElementTreeIterator(document, TraversalDirection.BOTTOM_UP).getConditions();
    }

    @Override
    protected void iterate(Document document)
    {
        IterationStep<Element> step = createFirstStep(document);
        iterateElement(step);
    }

    @Override
    protected boolean isLeaf(Element element)
    {
        int nrChildren = element.getChildNodes().getLength();
        for (int i = 0; i < nrChildren; i++)
        {
            Node childNode = element.getChildNodes().item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) return false;
        }
        return true;
    }

    protected void iterateElement(IterationStep<Element> step)
    {
        if(TraversalDirection.TOP_DOWN == direction) executeCommands(step);

        NodeList children = step.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element childElement = (Element) childNode;

            IterationStep<Element> nextStep = createChildStep(step,childElement,getId(childElement));
            iterateElement(nextStep);
        }

        if(TraversalDirection.BOTTOM_UP == direction) executeCommands(step);
    }

    protected String getId(Element element)
    {
        String id = element.getLocalName();
        if (id == null) throw new RuntimeException("Please use DocumentBuilderFactory.setNamespaceAware(true).");
        return id;
    }

    protected IterationStep<Element> createFirstStep(Document document)
    {
        Element rootElement = document.getDocumentElement();
        String rootId = getId(rootElement);
        String rootPath = "/" + rootId;
        return new IterationStep<>(rootElement, rootId, rootPath, null);
    }

}

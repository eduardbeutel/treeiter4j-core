package com.github.eduardbeutel.tree_iterator.dom;

import com.github.eduardbeutel.tree_iterator.core.TraversalDirection;
import com.github.eduardbeutel.tree_iterator.document.AbstractDocumentTreeIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
    protected void iterate(Object object)
    {
        Element rootElement = ((Document) object).getDocumentElement();
        String rootId = getId(rootElement);
        iterateElement(rootElement, rootId, "/" + rootId);
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

    protected void iterateElement(Element element, String id, String path)
    {
        if(TraversalDirection.TOP_DOWN == direction) executeCommands(element, id, path);

        int nrChildren = element.getChildNodes().getLength();
        for (int i = 0; i < nrChildren; i++)
        {
            Node childNode = element.getChildNodes().item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element childElement = (Element) childNode;

            String childId = getId(childElement);
            String childPath = path + "/" + childId;
            iterateElement(childElement, childId, childPath);
        }

        if(TraversalDirection.BOTTOM_UP == direction) executeCommands(element, id, path);
    }

    protected String getId(Element element)
    {
        String id = element.getLocalName();
        if (id == null) throw new RuntimeException("Please use DocumentBuilderFactory.setNamespaceAware(true).");
        return id;
    }

}

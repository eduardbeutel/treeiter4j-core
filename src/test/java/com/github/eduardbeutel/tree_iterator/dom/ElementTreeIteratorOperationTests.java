package com.github.eduardbeutel.tree_iterator.dom;

import com.github.eduardbeutel.tree_iterator.dom.ElementTreeIterator;
import com.github.eduardbeutel.tree_iterator.test.XmlUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class ElementTreeIteratorOperationTests
{

    @Before
    public void beforeOperationTests()
    {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void then()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book id=\"1\" />\n" +
                        "    <book/>\n" +
                        "    <book id=\"2\" />\n" +
                        "</library>"
        );

        // when
        ElementTreeIterator.topDown(document)
                .when(e -> "2".equals(e.getAttribute("id"))).then(e -> e.setTextContent("content"))
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book id=\"1\" />\n" +
                        "    <book/>\n" +
                        "    <book id=\"2\">content</book>\n" +
                        "</library>"
        );
        XMLAssert.assertXMLEqual(expectedDocument, document);
    }

    @Test
    public void collect_toReference()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book>\n" +
                        "        <title />\n" +
                        "        <author />\n" +
                        "    </book>\n" +
                        "</library>"
        );
        AtomicReference<Element> ref = new AtomicReference<Element>();

        // when
        ElementTreeIterator.topDown(document)
                .whenLeaf().collect(ref)
                .execute()
        ;

        // then
        assertEquals("author", ref.get().getLocalName());
    }

    @Test
    public void collect_toCollection()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book>\n" +
                        "        <title />\n" +
                        "        <author />\n" +
                        "    </book>\n" +
                        "</library>"
        );
        List<Element> leafs = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenLeaf().collect(leafs)
                .execute()
        ;

        // then
        assertEquals(2, leafs.size());
        assertEquals("title", leafs.get(0).getLocalName());
        assertEquals("author", leafs.get(1).getLocalName());
    }

}

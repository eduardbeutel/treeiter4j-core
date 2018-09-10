package com.github.eduardbeutel.tree_iterator.dom;

import com.github.eduardbeutel.tree_iterator.core.UnsupportedFeatureException;
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

    @Test
    public void stop()
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
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .always().then(e -> result.add(e.getLocalName()))
                .whenId("title").stop()
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book", "title"), result);
    }

    @Test
    public void skip()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book1>\n" +
                        "        <title1 />\n" +
                        "    </book1>\n" +
                        "    <book2>\n" +
                        "        <title2 />\n" +
                        "    </book2>\n" +
                        "    <book3>\n" +
                        "        <title3 />\n" +
                        "    </book3>\n" +
                        "</library>"
        );
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book2").skip()
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book1", "title1", "book3", "title3"), result);
    }

    @Test(expected = UnsupportedFeatureException.class)
    public void skip_usedInBottomUpMode_throwsException()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book1>\n" +
                        "        <title1 />\n" +
                        "    </book1>\n" +
                        "</library>"
        );
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.bottomUp(document)
                .whenId("book2").skip()
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then -> UnsupportedFeatureException
    }

    @Test
    public void remove_topDown()
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

        // when
        ElementTreeIterator.topDown(document)
                .whenId("book").remove()
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library></library>"
        );
        XMLAssert.assertXMLEqual("Actual: " + System.lineSeparator() + XmlUtils.prettyPrint(document),
                expectedDocument, document
        );
    }

    @Test
    public void remove_bottomUp()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <books>\n" +
                        "        <book>\n" +
                        "            <title />\n" +
                        "            <author />\n" +
                        "        </book>\n" +
                        "    </books>\n" +
                        "</library>"
        );

        // when
        ElementTreeIterator.bottomUp(document)
                .whenId("book").remove()
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library><books></books></library>"
        );
        XMLAssert.assertXMLEqual("Actual: " + System.lineSeparator() + XmlUtils.prettyPrint(document),
                expectedDocument, document
        );
    }

    @Test
    public void remove_root_hasNoEffect()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book />\n" +
                        "</library>"
        );

        // when
        ElementTreeIterator.topDown(document)
                .whenId("library").remove()
                .whenRoot().remove()
                .execute()
        ;

        // then
        Document expectedDocument = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book />\n" +
                        "</library>"
        );
        XMLAssert.assertXMLEqual("Actual: " + System.lineSeparator() + XmlUtils.prettyPrint(document),
                expectedDocument, document
        );
    }

}

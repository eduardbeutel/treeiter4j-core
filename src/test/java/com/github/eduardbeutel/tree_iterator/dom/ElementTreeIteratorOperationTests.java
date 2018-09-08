package com.github.eduardbeutel.tree_iterator.dom;

import com.github.eduardbeutel.tree_iterator.dom.ElementTreeIterator;
import com.github.eduardbeutel.tree_iterator.test.XmlUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

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

}

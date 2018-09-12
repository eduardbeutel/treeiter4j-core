package com.github.eduardbeutel.treeiter.dom;

import com.github.eduardbeutel.treeiter.test.XmlUtils;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ElementTreeIteratorTraversalTests
{

    @Test
    public void topDown()
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
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("library", "book", "title", "author"), result);
    }

    @Test
    public void bottomUp()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <books>\n" +
                        "        <book1>\n" +
                        "            <title1 />\n" +
                        "            <author1 />\n" +
                        "        </book1>\n" +
                        "        <book2>\n" +
                        "            <title2 />\n" +
                        "            <author2 />\n" +
                        "        </book2>\n" +
                        "    </books>   \n" +
                        "    <newspapers>\n" +
                        "        <newspaper1>\n" +
                        "            <name1 />\n" +
                        "            <date1 />\n" +
                        "        </newspaper1>\n" +
                        "        <newspaper2>\n" +
                        "            <name2 />\n" +
                        "            <date2 />\n" +
                        "        </newspaper2>\n" +
                        "    </newspapers>    \n" +
                        "</library>"
        );
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.bottomUp(document)
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList(
                "title1", "author1", "book1", "title2", "author2", "book2", "books",
                "name1", "date1", "newspaper1", "name2", "date2", "newspaper2", "newspapers",
                "library"
        ), result);
    }

}

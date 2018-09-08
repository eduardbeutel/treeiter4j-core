package com.github.eduardbeutel.tree_iterator.dom;

import com.github.eduardbeutel.tree_iterator.dom.ElementTreeIterator;
import com.github.eduardbeutel.tree_iterator.test.XmlUtils;
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
        assertEquals(Arrays.asList("library","book","title","author"), result);
    }

    @Test
    public void bottomUp()
    {
        // given
        Document document = XmlUtils.createDocument(
                "<library>\n" +
                        "    <book>\n" +
                        "        <title />\n" +
                        "        <author />\n" +
                        "    </book>\n" +
                        "    <newspaper>\n" +
                        "        <name />\n" +
                        "    </newspaper>\n" +
                        "</library>"
        );
        List<String> result = new ArrayList<>();

        // when
        ElementTreeIterator.bottomUp(document)
                .always().then(e -> result.add(e.getLocalName()))
                .execute()
        ;

        // then
        assertEquals(Arrays.asList("title","author","book","name","newspaper","library"), result);
    }

}

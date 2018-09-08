package com.github.eduardbeutel.tree_iterator.test;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class XmlUtils
{

    public static Document createDocument(String xml)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource input = new InputSource();
            input.setCharacterStream(new StringReader(xml));
            return builder.parse(input);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}

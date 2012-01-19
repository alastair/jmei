/*
    Copyright (c) 2011 Alastair Porter

    Permission is hereby granted, free of charge, to any person obtaining
    a copy of this software and associated documentation files (the
    "Software"), to deal in the Software without restriction, including
    without limitation the rights to use, copy, modify, merge, publish,
    distribute, sublicense, and/or sell copies of the Software, and to
    permit persons to whom the Software is furnished to do so, subject to
    the following conditions:

    The above copyright notice and this permission notice shall be
    included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
    LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
    OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
    WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.mcgill.music.ddmal.mei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A Factory for writing
 *
 */
public class MeiXmlWriter {

    private final MeiDocument meiDocument;
    private Document document;

    /**
     * Create a writer for the specified document.
     * @param doc
     *          the document to write.
     */
    private MeiXmlWriter(MeiDocument doc) {
        this.meiDocument = doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        document = builder.newDocument();
    }

    /**
     * Construct and XML document and render it to the given stream.
     * @param os
     *          the output stream to render the XML document to.
     */
    private void processDocument(OutputStream os) {
        try {
            Node rootNode = meiElementToNode(meiDocument.getRootElement());
            document.appendChild(rootNode);
            TransformerFactory transformFact = TransformerFactory.newInstance();
            Transformer transformer = transformFact.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        } catch (TransformerException e) {
        }
    }

    /**
     * Turn an MeiElement into a DOM node.
     */
    Node meiElementToNode(MeiElement e) {
        MeiNamespace ns = e.getNamespace();
        Element element;
        // Return a comment early, since it has no children or attributes
        if (e.getName().equals("#comment")) {
            return document.createComment(e.getValue());
        } else {
            element = document.createElementNS(ns.getHref(), e.getName());
        }
        element.setAttribute("xml:id", e.getId());
        // Attributes with or without namespaces
        // TODO: Currently namespaces prefixes are ns0, etc.
        for (MeiAttribute attr : e.getAttributes()) {
            MeiNamespace attrNs = attr.getNamespace();
            if (attrNs != null) {
                element.setAttributeNS(attrNs.getHref(), attr.getName(),
                        attr.getValue());
            } else {
                element.setAttribute(attr.getName(), attr.getValue());
            }
        }

        if (e.getValue() != null) {
            element.appendChild(document.createTextNode(e.getValue()));
        }
        // Add each child. Tail text is represented in XML as a text node
        // after the child, so add that if it exists.
        for (MeiElement ch : e.getChildren()) {
            element.appendChild(meiElementToNode(ch));
            if (ch.getTail() != null) {
                element.appendChild(document.createTextNode(ch.getTail()));
            }
        }
        return element;
    }

    public static String createDocument(MeiDocument doc) {
        MeiXmlWriter writer = new MeiXmlWriter(doc);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writer.processDocument(os);
        return os.toString();
    }

    public static void writeToFile(MeiDocument doc, File fp) throws FileNotFoundException {
        MeiXmlWriter writer = new MeiXmlWriter(doc);
        FileOutputStream fileOutputStream = new FileOutputStream(fp);
        writer.processDocument(fileOutputStream);
    }
}

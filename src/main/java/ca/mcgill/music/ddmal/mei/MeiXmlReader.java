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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A Factory for loading MEI files into an MeiDocument/MeiElement structure.
 * Use the {@link #loadFile(File)} or {@link #loadFile(String)} methods
 * to import a document.
 */
public class MeiXmlReader {

    private static String XML_ID_ATTRIBUTE = "xml:id";

    private DocumentBuilderFactory builderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;

    public static class MeiXmlReadException extends RuntimeException {
        private static final long serialVersionUID = -245505340878969726L;
        public MeiXmlReadException() {
            super();
        }
        public MeiXmlReadException(String reason) {
            super(reason);
        }
    }

    private MeiXmlReader(Reader reader) {
        try {
            builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            documentBuilder = builderFactory.newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(reader));
        } catch (ParserConfigurationException e) {
            throw new MeiXmlReadException();
        } catch (SAXException e) {
            throw new MeiXmlReadException();
        } catch (IOException e) {
            throw new MeiXmlReadException();
        }
    }

    private MeiXmlReader(String contents) {
        this(new StringReader(contents));
    }

    private MeiXmlReader(File file) throws FileNotFoundException {
        this(new FileReader(file));
    }

    // <foo><!--comment-->bar</foo> will put the text as a tail of the comment
    // this isn't very nice. Do we have getText too, that gets all text of foo?
    private MeiElement makeMeiElement(Node element) {
        // TODO: CDATA
        // Comments get a name #comment
        String nshref = element.getNamespaceURI();
        String nsprefix = element.getPrefix();
        MeiNamespace elns = new MeiNamespace(nshref, nsprefix);
        MeiElement e = new MeiElement(elns, element.getNodeName());
        if (element.getNodeType() == Node.COMMENT_NODE) {
            e.setValue(element.getNodeValue());
        }

        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                if (XML_ID_ATTRIBUTE.equals(item.getNodeName())) {
                    e.setId(item.getNodeValue());
                } else {
                    String attrns = item.getNamespaceURI();
                    String attrpre = item.getPrefix();
                    MeiNamespace atns = new MeiNamespace(attrns, attrpre);
                    MeiAttribute a = new MeiAttribute(atns, item.getNodeName(), item.getNodeValue());
                    e.addAttribute(a);
                }
            }
        }

        NodeList childNodes = element.getChildNodes();
        MeiElement lastElement = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.TEXT_NODE) {
                if (lastElement == null) {
                    e.setValue(item.getNodeValue());
                } else {
                    lastElement.setTail(item.getNodeValue());
                }
            } else {
                MeiElement child = makeMeiElement(item);
                e.addChild(child);
                lastElement = child;
            }
        }
        return e;
    }

    /**
     * Create an MeiDocument and populate it with the XML DOM.
     */
    private MeiDocument readDocument() {
        Element element = document.getDocumentElement();
        MeiElement root = makeMeiElement(element);
        if (!root.getNamespace().equals(MeiElement.DEFAULT_NAMESPACE)) {
            throw new MeiXmlReadException("Missing namespace");
        }
        if (!root.getName().equals("mei") && !root.getName().equals("meiCorpus")) {
            throw new MeiXmlReadException("Document must be <mei> or <meiCorups>");
        }
        String ver = root.getAttribute("meiversion");
        if (ver == null || !ver.equals(MeiDocument.MEI_VERSION)) {
            throw new MeiXmlReadException("Missing or invalid meiversion attribute");
        }

        MeiDocument ret = new MeiDocument();
        ret.setRootElement(root);
        return ret;
    }

    /**
     * Load an MEI file.
     * @param file
     *             The MEI file to load
     * @return
     *             an {@link MeiDocument} representing the MEI file.
     */
    public static MeiDocument loadFile(File file) {
        try {
            MeiXmlReader loader = new MeiXmlReader(file);
            MeiDocument doc = loader.readDocument();
            doc.setFilename(file.getName());
            return doc;
        } catch (FileNotFoundException e) {
            throw new MeiXmlReadException();
        }
    }

    /**
     * Load an MEI file.
     * @param filename
     *             A path to an MEI file to load
     * @return
     *             an {@link MeiDocument} representing the MEI file.
     */
    public static MeiDocument loadFile(String filename) {
        try {
            File fp = FileUtils.getFile(filename);
            MeiXmlReader loader = new MeiXmlReader(fp);
            MeiDocument doc = loader.readDocument();
            doc.setFilename(filename);
            return doc;
        } catch (FileNotFoundException e) {
            throw new MeiXmlReadException("Cannot find file " + filename);
        }
    }

    /**
     * Load an MEI document.
     * @param contents
     *             The MEI file to load
     * @return
     *             an {@link MeiDocument} representing the MEI file.
     */
    public static MeiDocument loadDocument(String contents) {
        MeiXmlReader loader = new MeiXmlReader(contents);
        MeiDocument doc = loader.readDocument();
        doc.setFilename("[fromstring]");
        return doc;
    }
}

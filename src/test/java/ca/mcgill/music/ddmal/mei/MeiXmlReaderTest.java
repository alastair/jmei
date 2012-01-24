package ca.mcgill.music.ddmal.mei;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;

import org.junit.Ignore;
import org.junit.Test;

import ca.mcgill.music.ddmal.mei.MeiXmlReader.MeiXmlReadException;

public class MeiXmlReaderTest {

    /**
     * Test for an invalid MEI document. Since we have no schema validation or
     * element classes, we can't do this yet.
     */
    @Ignore
    @Test
    public void testInvalidDoc() {

    }

    /**
     * Root element must be <mei:mei> or <mei:meiCorpus>
     */
    @Test
    public void testRootElement() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2012\"><foo>bar</foo></mei>";
        MeiDocument doc = MeiXmlReader.loadDocument(docText);
        assertThat(doc.getRootElement().getName(), is("mei"));

        docText = "<meiCorpus xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2012\"><foo>bar</foo></meiCorpus>";
        doc = MeiXmlReader.loadDocument(docText);
        assertThat(doc.getRootElement().getName(), is("meiCorpus"));

        docText = "<someOtherRoot xmlns=\"http://www.music-encoding.org/ns/mei\" />";
        try {
            doc = MeiXmlReader.loadDocument(docText);
            fail("Should have thrown an exception");
        } catch (MeiXmlReadException e) {
            assertThat(e.getMessage(), containsString("<mei>"));
        }
    }

    @Test
    public void testRootElementNamespace() {
        // Bad namespace
        String docText = "<mei meiversion=\"2012\"><foo>bar</foo></mei>";
        try {
            MeiXmlReader.loadDocument(docText);
            fail("Should have thrown an exception");
        } catch (MeiXmlReadException e) {
            assertThat(e.getMessage(), containsString("Missing namespace"));
        }
    }

    /**
     * Try and open a file that doesn't exist
     */
    @Test(expected = MeiXmlReadException.class)
    public void testNoFile() {
        MeiXmlReader.loadFile("nofile.mei");
    }

    /**
     * Open a file that does exist
     * @throws URISyntaxException
     */
    @Test
    public void testFile() throws URISyntaxException {
        URL url = getClass().getResource("/artic.mei");
        MeiDocument doc = MeiXmlReader.loadFile(url.getFile());
        assertThat(doc.getRootElement().getName(), is("mei"));

        doc = MeiXmlReader.loadFile(new File(url.toURI()));
        assertThat(doc.getRootElement().getName(), is("mei"));
    }

    /**
     * a document with no meiversion attribute, or a bad one
     */
    @Test
    public void testBadMeiVersion() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2011-04\" />";
        try {
            MeiXmlReader.loadDocument(docText);
            fail("Should have thrown an exception");
        } catch (MeiXmlReadException e) {
            assertThat(e.getMessage(), containsString("meiversion"));
        }

        docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" />";
        try {
            MeiXmlReader.loadDocument(docText);
            fail("Should have thrown an exception");
        } catch (MeiXmlReadException e) {
            assertThat(e.getMessage(), containsString("meiversion"));
        }
    }

    /**
     * Read comments
     */
    @Test
    public void testComment() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2012\"><foo>bar<!--woo--></foo></mei>";
        MeiDocument doc = MeiXmlReader.loadDocument(docText);
        MeiElement el = doc.getRootElement().getChildren().get(0).getChildren().get(0);

        assertThat(el.getName(), is("#comment"));
        assertThat(el.getValue(), is("woo"));
    }

    @Test
    public void testReadValue() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2012\"><foo>bar</foo></mei>";
        MeiDocument doc = MeiXmlReader.loadDocument(docText);
        MeiElement el = doc.getRootElement().getChildren().get(0);

        assertThat(el.getName(), is("foo"));
        assertThat(el.getValue(), is("bar"));
        assertThat(el.getTail(), is(nullValue()));
    }

    @Test
    public void testReadTail() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2012\"><foo/>bar</mei>";
        MeiDocument doc = MeiXmlReader.loadDocument(docText);

        MeiElement el = doc.getRootElement().getChildren().get(0);
        assertThat(el.getName(), is("foo"));
        assertThat(el.getValue(), is(nullValue()));
        assertThat(el.getTail(), is("bar"));
    }

    /**
     * Read the xml:id attribute specially
     */
    @Test
    public void testReadIdAttr() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2012\"><foo xml:id=\"myid\">bar</foo></mei>";
        MeiDocument doc = MeiXmlReader.loadDocument(docText);

        MeiElement el = doc.getRootElement().getChildren().get(0);
        assertThat(el.getId(), is("myid"));
    }

    /**
     * Element attributes
     */
    @Test
    public void testReadAttr() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\"2012\"><foo attr=\"baz\">bar</foo></mei>";
        MeiDocument doc = MeiXmlReader.loadDocument(docText);
        MeiElement el = doc.getRootElement().getChildren().get(0);
        assertThat(el.getAttribute("attr"), is("baz"));
    }

    /**
     * Element attributes with different namespaces
     */
    @Test
    public void testReadAttrNamespace() {
        String docText = "<mei xmlns=\"http://www.music-encoding.org/ns/mei\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                "meiversion=\"2012\"><foo xlink:href=\"urn:foo\">bar</foo></mei>";

        MeiDocument doc = MeiXmlReader.loadDocument(docText);
        MeiElement el = doc.getRootElement().getChildren().get(0);
        MeiAttribute a = el.getAttributes().get(0);
        assertThat(a.getName(), is("xlink:href"));
        assertThat(a.getValue(), is("urn:foo"));
        assertThat(a.getNamespace().getHref(), is("http://www.w3.org/1999/xlink"));
    }
}

package ca.mcgill.music.ddmal.mei;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class MeiXmlWriterTest {

    private String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
    private MeiDocument doc = new MeiDocument();
    private MeiElement root = new MeiElement("mei");

	@Test
	public void testFileOut() throws IOException {
        File fout = File.createTempFile("testmei", "");

        root.setId("anid");
        doc.setRootElement(root);

        String expected = xmlHeader + "<mei xml:id=\"anid\" xmlns=\"http://www.music-encoding.org/ns/mei\"/>\n";
        MeiXmlWriter.writeToFile(doc, fout);
        String writtenFile = IOUtils.toString(fout.toURI());
        assertThat(writtenFile, is(expected));
	}

	@Test
	public void testId() {
	    root.setId("anid");
	    doc.setRootElement(root);

	    String expected = xmlHeader + "<mei xml:id=\"anid\" xmlns=\"http://www.music-encoding.org/ns/mei\"/>\n";
	    assertThat(MeiXmlWriter.createDocument(doc), is(expected));
	}

	@Test
	public void testValue() {
	    MeiElement ch = new MeiElement("note");
	    ch.setValue("mynote");
	    ch.setId("id2");
	    root.addChild(ch);
	    root.setId("id1");
	    doc.setRootElement(root);

	    String expected = xmlHeader + "<mei xml:id=\"id1\" xmlns=\"http://www.music-encoding.org/ns/mei\">\n    <note xml:id=\"id2\">mynote</note>\n</mei>\n";
        assertThat(MeiXmlWriter.createDocument(doc), is(expected));
	}

	@Test
	public void testTail() {
        MeiElement ch = new MeiElement("note");
        ch.setValue("mynote");
        ch.setTail("atail");
        ch.setId("id2");
        root.addChild(ch);
        root.setId("id1");
        doc.setRootElement(root);

        String expected = xmlHeader + "<mei xml:id=\"id1\" xmlns=\"http://www.music-encoding.org/ns/mei\">\n    <note xml:id=\"id2\">mynote</note>atail</mei>\n";
        assertThat(MeiXmlWriter.createDocument(doc), is(expected));
	}

	@Test
	public void testComment() {
        MeiElement comm = new MeiElement("#comment");
        comm.setValue("a comment");
        root.addChild(comm);
        root.setId("id1");
        doc.setRootElement(root);

        String expected = xmlHeader + "<mei xml:id=\"id1\" xmlns=\"http://www.music-encoding.org/ns/mei\">\n    <!--a comment-->\n</mei>\n";
        assertThat(MeiXmlWriter.createDocument(doc), is(expected));
	}

	@Test
	public void testTreeWrite() {
        root.setId("r");
        MeiElement mu = new MeiElement("music");
        mu.setId("m");
        MeiElement score = new MeiElement("score");
        score.setId("s");
        MeiElement n1 = new MeiElement("note");
        n1.setId("n1");
        MeiElement n2 = new MeiElement("note");
        n2.setId("n2");
        root.addChild(mu);
        mu.addChild(score);
        score.addChild(n1);
        score.addChild(n2);
        doc.setRootElement(root);

        String expected = xmlHeader + "<mei xml:id=\"r\" xmlns=\"http://www.music-encoding.org/ns/mei\">\n" +
                "    <music xml:id=\"m\">\n        <score xml:id=\"s\">\n            <note xml:id=\"n1\"/>\n            <note xml:id=\"n2\"/>\n" +
                "        </score>\n    </music>\n</mei>\n";

        assertThat(MeiXmlWriter.createDocument(doc), is(expected));
	}

	@Test
	public void testAttr() {
        root.setId("anid");
        root.addAttribute("meiversion", "2012");
        doc.setRootElement(root);

        String expected = xmlHeader + "<mei meiversion=\"2012\" xml:id=\"anid\" xmlns=\"http://www.music-encoding.org/ns/mei\"/>\n";
        assertThat(MeiXmlWriter.createDocument(doc), is(expected));
	}

	@Test
	public void testAttrNamespace() {
        root.setId("id1");
        root.addAttribute("meiversion", "2012");
        MeiNamespace ns = new MeiNamespace("http://www.w3.org/1999/xlink", "xlink");
        MeiAttribute a = new MeiAttribute(ns, "title", "My image");
        MeiElement graphic = new MeiElement("graphic");
        graphic.addAttribute(a);
        graphic.setId("id2");
        doc.setRootElement(root);
        root.addChild(graphic);

        String expected = xmlHeader + "<mei meiversion=\"2012\" xml:id=\"id1\" xmlns=\"http://www.music-encoding.org/ns/mei\">\n" +
                "    <graphic xmlns:ns0=\"http://www.w3.org/1999/xlink\" ns0:title=\"My image\" xml:id=\"id2\"/>\n</mei>\n";
        assertThat(MeiXmlWriter.createDocument(doc), is(expected));
	}
}

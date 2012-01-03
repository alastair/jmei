package ca.mcgill.music.ddmal.mei;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.junit.Test;

import ca.mcgill.music.ddmal.mei.MeiAttribute;
import ca.mcgill.music.ddmal.mei.MeiNamespace;

public class MeiAttributeTest {

    @Test
    public void testConstructors() {
        MeiAttribute attr = new MeiAttribute("name", "val");
        assertThat(attr.getName(), is("name"));
        assertThat(attr.getValue(), is("val"));
        assertThat(attr.getNamespace(), is(new MeiNamespace(null, null)));

        MeiNamespace ns = new MeiNamespace("http://example.com", "ex");
        attr = new MeiAttribute(ns, "newname", "newval");
        assertThat(attr.getName(), is("newname"));
        assertThat(attr.getValue(), is("newval"));
        assertThat(attr.getNamespace(), is(ns));
    }

    @Test
    public void testToString() {
        MeiAttribute attr = new MeiAttribute("name", "val");
        assertThat(attr.toString(), is("name=val"));

        MeiNamespace ns = new MeiNamespace("http://example.com", "ex");
        attr = new MeiAttribute(ns, "newname", "newval");
        assertThat(attr.toString(), is("ex:newname=newval"));
    }

    @Test
    public void testEquals() {
        MeiAttribute attr = new MeiAttribute("name", "val");
        MeiAttribute newattr = new MeiAttribute("name", "val");
        assertThat(attr, is(equalTo(newattr)));

        MeiAttribute defaultExplicit = new MeiAttribute(new MeiNamespace(null, null), "name", "val");
        assertThat(defaultExplicit, is(equalTo(attr)));

        MeiNamespace ns = new MeiNamespace("http://example.com", "ex");
        attr = new MeiAttribute(ns, "newname", "newval");
        newattr = new MeiAttribute(ns, "newname", "newval");
        assertThat(attr, is(equalTo(newattr)));
    }

    @Test
    public void testNotEquals() {
        MeiAttribute attr = new MeiAttribute("name", "val");
        MeiAttribute newattr = new MeiAttribute("something", "else");
        assertThat(attr, is(not(equalTo(newattr))));

        MeiNamespace ns = new MeiNamespace("http://example.com", "ex");
        MeiAttribute explicitns = new MeiAttribute(ns, "name", "val");
        assertThat(attr, is(not(equalTo(explicitns))));
    }
}

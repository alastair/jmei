package ca.mcgill.music.ddmal.mei;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MeiElementTest {

    private MeiElement el;
    private MeiElement el2;

    @Before
    public void setupElement() {
        el = new MeiElement("e");
        el.addChild(new MeiElement("a"));
        el2 = new MeiElement("b");
        el.addChild(el2);
        el.addChild(new MeiElement("e"));
        el.addChild(new MeiElement("b"));
    }

    @Test
    public void testMakeElement() {
           MeiElement e = new MeiElement("element");
           assertThat(e.getName(), is("element"));
           assertThat(e.getId().length(), is(36));
    }

    @Test
    public void testMakeElementWithId() {
           MeiElement e = new MeiElement("e", "someid");
           assertThat(e.getId(), is("someid"));
    }

    @Test
    public void testNamespace() {
        MeiElement e = new MeiElement("element");
        assertThat(e.getNamespace(), is(MeiElement.DEFAULT_NAMESPACE));
    }

    @Test
    public void testOwnNamespace() {
        MeiNamespace ns = new MeiNamespace("href", "pre");
        MeiElement e = new MeiElement(ns, "el");

        assertThat(e.getNamespace().getHref(), is("href"));
        assertThat(e.getName(), is("el"));
    }

    @Test
    public void removeAttribute() {
        MeiElement e = new MeiElement("element");
        MeiAttribute attr = new MeiAttribute("key", "1");
        e.addAttribute(attr);
        e.addAttribute("mode", "2");
        assertThat(e.getAttributes().size(), is(2));
        MeiAttribute secondAttr = e.getAttributes().get(1);

        e.removeAttribute(attr);
        assertThat(e.getAttributes().size(), is(1));
        assertThat(e.getAttributes().get(0), is(secondAttr));
        e.removeAttribute(secondAttr);
        assertThat(e.getAttributes().size(), is(0));
    }

    @Test
    public void removeAttributeByName() {
        MeiElement e = new MeiElement("element");
        e.addAttribute("key", "1");
        e.addAttribute("mode", "2");
        assertThat(e.getAttributes().size(), is(2));
        e.removeAttributeByName("mode");
        assertThat(e.getAttributes().size(), is(1));
        assertThat(e.getAttributes().get(0).getName(), is("key"));
    }

    @Test
    public void getAttribute() {
        MeiElement e = new MeiElement("element");
        e.addAttribute("key", "1");
        e.addAttribute("mode", "2");

        assertThat(e.getAttribute("key"), is("1"));
    }

    @Test
    public void getNoAttribute() {
        MeiElement e = new MeiElement("element");
        e.addAttribute("key", "1");

        assertThat(e.getAttribute("foo"), is(nullValue()));
    }

    @Test
    public void addChild() {
        MeiElement e = new MeiElement("e");
        MeiElement f = new MeiElement("f");
        assertThat(f.getParent(), is(nullValue()));
        assertThat(e.getChildren().size(), is(0));
        e.addChild(f);
        assertThat(e.getChildren().size(), is(1));
        assertThat(e.getChildren().get(0), is(f));
        assertThat(f.getParent(), is(e));
    }

    @Test
    public void addChildBefore() {
        MeiElement h = new MeiElement("h");
        el.addChildBefore(el2, h);
        assertThat(el.getChildren().size(), is(5));
        assertThat(el.getChildren().get(1), is(h));
    }

    @Test
    public void getChildrenByName() {
        List<MeiElement> children = el.getChildrenByName("b");
        assertThat(children.size(), is(2));
    }

    @Test
    public void testRemoveChild() {
        el.removeChild(el2);
        assertThat(el.getChildren().size(), is(3));
        assertThat(el2.getParent(), is(nullValue()));
    }

    @Test
    // Remove an element that isn't in the child element
    public void removeWrongChild() {
        MeiElement f = new MeiElement("f");
        assertThat(el.getChildren().size(), is(4));
        el.removeChild(f);
        assertThat(el.getChildren().size(), is(4));
    }

    @Test
    // Remove a child that is in the array, but has a different parent?
    // XXX: This seems bad
    public void removeDoubleBadChild() {
        MeiElement f = new MeiElement("f");
        MeiElement g = new MeiElement("g");
        el.addChild(g);
        f.addChild(g);
        assertThat(el.getChildren().size(), is(5));
        el.removeChild(g);
        assertThat(el.getChildren().size(), is(5));
    }

    @Test
    public void testRemoveAllChildren() {
        assertThat(el.getChildren().size(), is(4));
        MeiElement got = el.getChildren().get(2);
        el.removeAllChildren();
        assertThat(el.getChildren().size(), is(0));
        assertThat(got.getParent(), is(nullValue()));
    }

    @Test
    public void testRemoveChildrenByName() {
        assertThat(el.getChildren().size(), is(4));
        List<MeiElement> bs = el.getDescendantsByName("b");
        el.removeChildrenByName("b");
        assertThat(el.getChildren().size(), is(2));
        assertThat(el.getChildren().get(0).getName(), is("a"));
        assertThat(el.getChildren().get(1).getName(), is("e"));
        assertThat(bs.get(0).getParent(), is(nullValue()));
    }

    @Test
    public void testGetDescendants() {
        MeiElement e = new MeiElement("e");
        MeiElement f = new MeiElement("f");
        MeiElement g = new MeiElement("g");
        MeiElement h = new MeiElement("h");
        MeiElement i = new MeiElement("i");
        MeiElement j = new MeiElement("j");
        MeiElement k = new MeiElement("k");
        MeiElement l = new MeiElement("l");

        e.addChild(f);
        e.addChild(g);
        g.addChild(h);
        g.addChild(i);
        h.addChild(j);
        j.addChild(k);
        j.addChild(l);

        assertThat(e.getDescendants().size(), is(7));
        assertThat(e.getDescendants().get(0), is(f));
        assertThat(e.getDescendants().get(1), is(g));
        assertThat(e.getDescendants().get(2), is(h));
        assertThat(e.getDescendants().get(3), is(j));
        assertThat(e.getDescendants().get(4), is(k));
        assertThat(e.getDescendants().get(5), is(l));
        assertThat(e.getDescendants().get(6), is(i));
    }

    @Test
    public void testGetDescendantsByName() {
        MeiElement e = new MeiElement("e");
        MeiElement f = new MeiElement("same");
        MeiElement g = new MeiElement("g");
        MeiElement h = new MeiElement("h");
        MeiElement i = new MeiElement("same");
        MeiElement j = new MeiElement("same");
        MeiElement k = new MeiElement("k");
        MeiElement l = new MeiElement("same");

        e.addChild(f);
        e.addChild(g);
        g.addChild(h);
        g.addChild(i);
        h.addChild(j);
        j.addChild(k);
        j.addChild(l);

        assertThat(e.getDescendantsByName("same").size(), is(4));
        assertThat(e.getDescendantsByName("same").get(0), is(f));
        assertThat(e.getDescendantsByName("same").get(1), is(j));
        assertThat(e.getDescendantsByName("same").get(2), is(l));
        assertThat(e.getDescendantsByName("same").get(3), is(i));
    }

    @Test
    public void testGetPeers() {
        MeiElement e = new MeiElement("e");
        MeiElement p1 = new MeiElement("p1");
        MeiElement p2 = new MeiElement("p2");
        MeiElement p3 = new MeiElement("p3");
        MeiElement p4 = new MeiElement("p4");

        e.addChild(p1);
        e.addChild(p2);
        e.addChild(p3);
        e.addChild(p4);

        assertThat(e.getChildren().size(), is(4));

        assertThat(p1.getPeers().size(), is(3));
        // Check that we don't delete 'p1' from children
        assertThat(e.getChildren().size(), is(4));

        assertThat(p3.getPeers().get(2), is(p4));
    }

    @Test
    public void testGetAncestor() {
        MeiElement e = new MeiElement("mine");
        assertThat(e.getAncestor("blah"), is(nullValue()));

        MeiElement f = new MeiElement("f");
        e.addChild(f);
        assertThat(f.getAncestor("mine"), is(e));

        MeiElement g = new MeiElement("g");
        f.addChild(g);
        assertThat(g.getAncestor("mine"), is(e));
        assertThat(g.getAncestor("f"), is(f));
        assertThat(g.getAncestor("foo"), is(nullValue()));
    }

}

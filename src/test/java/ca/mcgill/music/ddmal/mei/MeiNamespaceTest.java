package ca.mcgill.music.ddmal.mei;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsNot.not;

import org.junit.Test;

import ca.mcgill.music.ddmal.mei.MeiNamespace;

public class MeiNamespaceTest {

    @Test
    public void testCreate() {
        MeiNamespace ns = new MeiNamespace("href", "prefix");
        assertThat(ns.getHref(), is("href"));
        assertThat(ns.getPrefix(), is("prefix"));
    }

    @Test
    public void testToString() {
        MeiNamespace ns = new MeiNamespace("http://example.com", "prefix");
        assertThat(ns.toString(), is("{http://example.com}prefix"));
    }

    @Test
    public void testEquals() {
        MeiNamespace one = new MeiNamespace("href", "pre");
        MeiNamespace two = new MeiNamespace("href", "pre");

        assertThat(one, equalTo(two));

        // Same href, different prefix is equal
        MeiNamespace diffp = new MeiNamespace("href", "proo");
        assertThat(one, equalTo(diffp));

        MeiNamespace diffh = new MeiNamespace("hroof", "pre");
        assertThat(one, is(not(equalTo(diffh))));

        MeiNamespace diffboth = new MeiNamespace("example.com", "example");
        assertThat(one, is(not(equalTo(diffboth))));
    }

}

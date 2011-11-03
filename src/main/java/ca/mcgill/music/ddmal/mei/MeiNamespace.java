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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A namespace to differentiate elements and attributes with the same names.
 * When represented in XML, the href is declared on the root element.
 * e.g. <mei xmlns:prefix="href">.
 */
public class MeiNamespace {
    private final String prefix;
    private final String href;

    /**
     * Create a namespace with a given href and prefix.
     * @param href
     *          the specified href
     * @param prefix
     *          the specified prefix
     */
    public MeiNamespace(String href, String prefix) {
        this.prefix = prefix;
        this.href = href;
    }

    /**
     * Get the namespace's prefix.
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the namespace's href.
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * Compare this namespace to another object.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        MeiNamespace rhs = (MeiNamespace) obj;
        return new EqualsBuilder()
                .append(prefix, rhs.prefix)
                .append(href, rhs.href)
                .isEquals();
    }

    /**
     * Overridden hashcode.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
          .append(prefix)
          .append(href)
          .toHashCode();
      }

    /**
     * Get a string representation of this namespace.
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append("{")
                .append(href)
                .append("}")
                .append(prefix)
                .toString();
    }
}

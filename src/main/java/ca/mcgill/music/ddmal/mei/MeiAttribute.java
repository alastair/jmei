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
 * A description of a characteristic of an {@link MeiElement}.
 * The attribute has a name and a value.
 * An attribute can contain an optional namespace if it is not part of the
 * main MEI namespace.
 */
public class MeiAttribute {

    /** The namespace of this attribute. */
    private final MeiNamespace namespace;
    private final String name;
    private String value;

    /**
     * Create an attribute with a specified namespace.
     * @param namespace
     *          the namespace of the attribute
     * @param name
     *          the name of the attribute
     * @param value
     *          the value of the attribute
     */
    public MeiAttribute(MeiNamespace namespace, String name, String value) {
        this.namespace = namespace;
        this.name = name;
        this.value = value;
    }

    /**
     * Create an attribute with the default namespace.
     * @param name
     *          the name of the attribute
     * @param value
     *          the value of the attribute
     */
    public MeiAttribute(String name, String value) {
        this(MeiDocument.DEFAULT_NAMESPACE, name, value);
    }

    /**
     * Get the value of this attribute.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of this attribute.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the namespace of this attribute.
     */
    public MeiNamespace getNamespace() {
        return namespace;
    }

    /**
     * Get the name of this attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Compare this attribute to another object.
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
        MeiAttribute rhs = (MeiAttribute) obj;
        return new EqualsBuilder()
                .append(name, rhs.name)
                .append(value, rhs.value)
                .append(namespace, rhs.namespace)
                .isEquals();
    }

    /**
     * Overridden hashcode.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
          .append(name)
          .append(value)
          .append(namespace)
          .toHashCode();
      }

    /**
     * Get a string representation of this attribute.
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append(namespace.getPrefix())
                .append(":")
                .append(name)
                .append("=")
                .append(value)
                .toString();
    }

}

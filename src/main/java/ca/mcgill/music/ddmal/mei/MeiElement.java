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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MeiElement {
    public static final MeiNamespace DEFAULT_NAMESPACE = new MeiNamespace("http://www.music-encoding.org/ns/mei", "mei");

    /** The unique identifier of this element. */
    private String id;
    /** The tag name of this element. */
    private final String name;
    /** The parent element of this one. */
    private MeiElement parent;
    /** The text value of this tag. e.g., &lt;tag>value&lt;/tag> */
    private String value;
    /** The tail of this tag. e.g., &lt;tag>value&lt;/tag>tail */
    private String tail;
    /** The namespace of this element. */
    private MeiNamespace namespace;

    /** The document that this element is part of. */
    private MeiDocument document;


    /** Key/value attributes attached to this element. */
    private List<MeiAttribute> attributes;
    /** An ordered list of all child elements. */
    private List<MeiElement> children;

    public MeiDocument getDocument() {
        return document;
    }

    public void setDocument(MeiDocument document) {
        this.document = document;
        if (document != null) {
            document.addToMap(this);
        }
        for (MeiElement c : children) {
            c.setDocument(document);
        }
    }

    /**
     * Make a new element with a given name and id.
     * This method should only be used internally
     * @param name
     * @param id
     */
    private MeiElement(MeiNamespace namespace, String name, String id) {
        this.namespace = namespace;
        this.name = name;
        this.id = id;
        this.children = new ArrayList<MeiElement>();
        this.attributes = new ArrayList<MeiAttribute>();
    }

    /**
     * Make a new element with a given name and id.
     * This method should only be used internally
     * @param name
     * @param id
     */
    /* package */ MeiElement(String name, String id) {
        this(DEFAULT_NAMESPACE, name, id);
    }

    /**
     * Make a new element with a given name and id.
     * This method should only be used internally
     * @param name
     * @param id
     */
    /* package */ MeiElement(MeiNamespace ns, String name) {
        this(ns, name, UUID.randomUUID().toString());
    }

    /**
     * Create a new element.
     * @param name
     *          the name of the element tag.
     */
    public MeiElement(String name) {
        this(DEFAULT_NAMESPACE, name, UUID.randomUUID().toString());
    }

    public MeiNamespace getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    /* package */ void setId(String id) {
        this.id = id;
    }

    public MeiElement getParent() {
        return parent;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public String getTail() {
        return tail;
    }

    /**
     * Add an attribute to this element
     * @param attribute
     *          the attribute to add
     */
    public void addAttribute(MeiAttribute attribute) {
        attributes.add(attribute);
    }

    public void addAttribute(String name, String value) {
        MeiAttribute attribute = new MeiAttribute(name, value);
        attributes.add(attribute);
    }

    /**
     * Add the specified list of attributes to this element.
     * @param attributes
     *          the list to append to the current list of elements
     */
    public void addAllAttributes(List<MeiAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    /**
     * Replace all attributes on this element with the specified list.
     * @param attributes
     *          the list of attributes to replace all current attributes
     */
    public void setAttributes(List<MeiAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Remove the attribute with a specified name.
     * @param name
     *          the name of attribute to remove
     */
    public void removeAttributeByName(String name) {
        List<MeiAttribute> remove = new ArrayList<MeiAttribute>();
        for (MeiAttribute attribute : attributes) {
            if (attribute.getName().equals(name)) {
                remove.add(attribute);
            }
        }
        attributes.removeAll(remove);
    }

    /**
     * Remove a specific attribute from this element.
     * @param attribute
     *          the attribute to remove
     */
    public void removeAttribute(MeiAttribute attribute) {
        this.attributes.remove(attribute);
    }

    /**
     * Remove all attributes from this element.
     */
    public void removeAllAttributes() {
        attributes.clear();
    }

    /**
     * Get the value of an attribute with a given name.
     * @param attrName
     *             the name of the attribute
     * @return
     *             the value of the attribute, or null if it doesn't exist.
     */
    public String getAttribute(String attrName) {
        for (MeiAttribute attribute : attributes) {
            if (attribute.getName().equals(attrName)) {
                return attribute.getValue();
            }
        }
        return null;
    }

    /**
     * Get all attributes from this element.
     * @return
     */
    public List<MeiAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Add a child element. This element will be set as the parent of the
     * given child
     * @param child
     */
    public void addChild(MeiElement child) {
        // XXX: If this child belongs to someone else, should we remove it?
        if (children.indexOf(child) >= 0) {
            // XXX: What's going on here?
        }
        child.setDocument(document);
        child.parent = this;
        this.children.add(child);
    }

    /**
     * Add a child element
     * @param before
     * @param child
     */
    public void addChildBefore(MeiElement before, MeiElement child) {
        int pos = this.children.indexOf(before);
        child.parent = this;
        child.setDocument(document);
        if (pos >= 0) {
            this.children.add(pos, child);
        } else {
            this.children.add(child);
        }
    }

    public List<MeiElement> getChildren() {
        return children;
    }

    /**
     * Get all children that have the specified name.
     * @param name
     * @return
     */
    public List<MeiElement> getChildrenByName(String name) {
        List<MeiElement> ret = new ArrayList<MeiElement>();
        for (MeiElement c : children) {
            if (c.getName().equals(name)) {
                ret.add(c);
            }
        }
        return ret;
    }

    public void removeChild(MeiElement child) {
        int location = children.indexOf(child);
        if (location >= 0 && child.parent == this) {
            child.parent = null;
            children.remove(child);
        }
    }

    /**
     * Delete all children from this element.
     */
    public void removeAllChildren() {
        for (MeiElement e : children) {
            e.parent = null;
        }
        children = new ArrayList<MeiElement>();
    }

    /**
     * Remove all children elements with a given name.
     * @param name
     *          the name of child elements to remove
     */
    public void removeChildrenByName(String name) {
        List<MeiElement> namedChildren = getChildrenByName(name);
        for (MeiElement e : namedChildren) {
            e.parent = null;
        }
        children.removeAll(namedChildren);
    }

    /**
     * Get all elements that are descended from this one.
     * @return
     *          A list of descendant elements, in postfix traversal order
     */
    public List<MeiElement> getDescendants() {
        List<MeiElement> ret = new ArrayList<MeiElement>();
        for (MeiElement e : children) {
            ret.add(e);
            ret.addAll(e.getDescendants());
        }
        return ret;
    }

    /**
     * Get all descendant elements that have a given tag name
     * @param matchName
     * @return
     */
    public List<MeiElement> getDescendantsByName(String matchName) {
        List<MeiElement> ret = new ArrayList<MeiElement>();
        for (MeiElement e : children) {
            if (e.getName().equals(matchName)) {
                ret.add(e);
            }
            ret.addAll(e.getDescendantsByName(matchName));
        }
        return ret;
    }

    /**
     * Get a list of peers of this element (not including this one).
     * Peers are defined as this element's parent's children. If this element
     * does not have a parent, an empty list is returned.
     * @return
     */
    public List<MeiElement> getPeers() {
        if (parent == null) {
            return new ArrayList<MeiElement>();
        }
        List<MeiElement> peers = new ArrayList<MeiElement>(parent.getChildren());
        Collections.copy(parent.getChildren(), peers);
        peers.remove(this);
        return peers;
    }

    /**
     * Get the first ancestor element that has the name specified.
     * @param name
     * @return
     */
    public MeiElement getAncestor(String name) {
        if (parent == null) {
            return null;
        }
        if (parent.name.equals(name)) {
            return parent;
        } else {
            return parent.getAncestor(name);
        }
    }

    /**
     * Compare this element to another object.
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
        MeiElement rhs = (MeiElement) obj;
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(name, rhs.name)
                .append(parent, rhs.parent)
                .append(value, rhs.value)
                .append(tail, rhs.tail)
                .append(children, rhs.children)
                .append(attributes, rhs.attributes)
                .isEquals();
    }

    /**
     * Overridden hashcode.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(name)
                .append(parent)
                .append(value)
                .append(tail)
                .append(children)
                .append(attributes)
                .toHashCode();
    }

    /**
     * Get a string representation of this namespace.
     */
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder()
                .append("<")
                .append(name)
                .append(" xml:id=").append(id);
        for (MeiAttribute attr : attributes) {
            ret.append(" ").append(attr);
        }
        ret.append(">");
        return ret.toString();
    }

}

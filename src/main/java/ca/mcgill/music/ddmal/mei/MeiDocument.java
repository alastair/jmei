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
import java.util.List;

/**
 * A Document. It contains a root element, and some helper methods.
 */
public class MeiDocument {
    public static final String MEI_VERSION = "2012";

    private MeiElement rootElement;
    private String filename;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public MeiElement getRootElement() {
        return rootElement;
    }

    public void setRootElement(MeiElement rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Get the element in the document tree with the id specified.
     * @param id
     * @return
     *      the element with the requested ID, or null if no element exists.
     */
    public MeiElement getElementById(String id) {
        if (rootElement == null) {
            return null;
        }
        return checkElementForId(rootElement, id);
    }

    private MeiElement checkElementForId(MeiElement element, String id) {
        if (element.getId().equals(id)) {
            return element;
        }
        for (MeiElement e : element.getChildren()) {
            MeiElement got = checkElementForId(e, id);
            if (got != null) {
                return got;
            }
        }
        return null;
    }

    /**
     * Get all elements in this document with a given tag name.
     * @param name
     * @return
     */
    public List<MeiElement> getElementsByName(String name) {
        if (rootElement != null) {
            return rootElement.getDescendantsByName(name);
        } else {
            return new ArrayList<MeiElement>();
        }
    }
}

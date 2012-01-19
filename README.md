jMEI
====

Java bindings for the Music Encoding Initiative (MEI).  MEI is a document
schema for encoding many different musical notation formats.  More information
can be found at http://music-encoding.org

Author
======

Written by Alastair Porter, based on ideas from
[libmei](https://github.com/DDMAL/libmei)


Compilation / Installation
==========================

jMEI uses Maven. To compile, simply

    mvn package
    
and grab the file built to ```target/```

If you want to use this file in another maven project you can run
```mvn install``` and use the following dependency block:

        <dependency>
            <groupId>ca.mcgill.music.ddmal</groupId>
            <artifactId>jmei</artifactId>
            <version>0.1</version>
        </dependency>

Usage
=====

jMEI closely follows the XML DOM, so provides a number of object creation and
manipulation methods that are inspired from XML.

See the example file in src/main/example for a running demo of these features.

Create a Document, and add a root element to it

    MeiDocument d = new MeiDocument();
    MeiElement root = new MeiElement("mei");
    d.setRootElement(root);

Create elements and add attributes to them

    MeiElement note = new MeiElement("note");
    note.addAttribute("pname", "c");

    // If you need to add an attribute with a different namespace,
    // you can do it manually
    MeiNamespace ns = new MeiNamespace("http://www.w3.org/1999/xlink", "xlink");
    MeiAttribute a = new MeiAttribute(ns, "title", "My image");
    MeiElement graphic = new MeiElement("graphic");
    graphic.addAttribute(a);

Add elements as children

    root.addChild(note);
    root.addChild(graphic);

Access ID information

    // jmee automatically makes an ID for you. It's stored in the xml:id
    //  attribute of the tag.
    MeiElement n1 = new MeiElement("note");
    String n1Id = n1.getId();
    MeiElement n2 = new MeiElement("note");
    String n2Id = n2.getId();

    MeiElement tie = new MeiElement("tie");
    tie.addAttribute("startid", n1Id);
    tie.addAttribute("endid", n2Id);

You can navigate the tree

    // Find by id - given a document like this:
    //  <note xml:id="anote" />
    //  <note xml:id="anothernote"/>
    //  <tie xml:id="atie" startid="anote" endid="anothernote">
    // you can ask for specific elements in the document
    MeiElement tie = document.getElementById("atie");
    MeiElement note1 = document.getElementById(tie.getAttribute("startid"));
  
    // Find a surrounding element (e.g. a note's parent staff):
    //  <staff>
    //    ...
    //      <note xml:id="anote" pname="d" />
    //  </staff>
    MeiElement note = document.getElementById("anote");
    MeiElement staff = note.getAncestor("staff");

Read MEI documents from XML

    File f = new File("example.mei");
    MeiDocument doc = MeiXmlImport.import(f);
    // Any xml:id that was set in the XML file is now stored
    // as the id field on an MeiElement
    MeiElement note = doc.getElementById("mynote");
    note.getId(); // is "mynote"

Future plans
============

* Tie objects into XML tree so that standard navigation tools
  (e.g. xpath) can be used
* Create an Enum for tag types to prevent invalid tags from being created
* Only let valid attributes and children be added to elements
* Validate against the MEI schema
* Automatically create a valid skeleton document (e.g. with meiversion attribute)
* Don't use xml:id when not needed
* Map of elements in the document for getById. Needs to auto-update
  when elements are added and removed.

License
=======

jMEI is released under the MIT License

Bug Reports / Patches
=====================

Issues and patches are welcome on GitHub: https://github.com/alastair/jmei
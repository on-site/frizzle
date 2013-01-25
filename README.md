Frizzle, a Java wrapper for Sizzle
==================================

Overview
--------

Frizzle is a Java wrapper for [Sizzle][sizzle], so that you can use
CSS-style selectors to work on Java DOM objects. If you've done any DOM
programming at all, the advantage of this (over using plain DOM) is
obvious. :-)

Frizzle was first written because we needed to handle big XML documents
that are far too slow to process using XPath. (And no, not a fan of the
["detach from parent"][slow-xpath] trick that some people talk about.)

Example
-------

    Context.enter();
    try {
        Frizzle frizzle = new Frizzle(doc);
        for (Element element : frizzle.select("div[class~=test]")) {
            String text = frizzle.getText(element);
            String id = frizzle.attr(element, "id");
            // ...
        }
    } finally {
        Context.exit();
    }

Frizzle does not wrap the complete set of Sizzle methods, nor does it
currently offer any access to Sizzle's extension points (e.g., custom
pseudo-selectors). They should be straightforward to add, should you
need to use them.

Dependencies
------------

In order to run, you will need to copy `sizzle.js` into the same
location as the compiled Frizzle class files.

Additionally, this project requires the following libraries:

* [Rhino][rhino]
* [Guava][guava]

The tests use [TestNG][testng].

(Sorry, we don't use Maven at work and I have no experience with it,
so you'll have to manually fulfil dependencies for now. I wish it were
as easy as writing a `Gemfile`.)

Future directions
-----------------

+ Enable running Sizzle's full test suite, to verify that the compat
  layer works totally correctly.
+ Make it work with [Nashorn][nashorn].
+ Make the API even easier to use (suggestions welcome).

Contact and licensing
---------------------

Frizzle is maintained by [Chris Jester-Young][cky].

All the code is licensed under the same terms as Sizzle itself.

[sizzle]: http://sizzlejs.com/
[slow-xpath]: http://blog.astradele.com/2006/02/24/slow-xpath-evaluation-for-large-xml-documents-in-java-15/
[rhino]: http://www.mozilla.org/rhino/
[guava]: http://code.google.com/p/guava-libraries/
[testng]: http://www.testng.org/
[nashorn]: http://openjdk.java.net/projects/nashorn/
[cky]: http://github.com/cky

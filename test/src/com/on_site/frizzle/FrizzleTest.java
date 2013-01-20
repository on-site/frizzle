/*
 * Copyright (c) 2013 On-Site.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES, OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.on_site.frizzle;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.on_site.util.DOMUtil;
import org.mozilla.javascript.Context;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test case for {@link Frizzle}. Just some basic tests I was testing
 * with, until we can actually run the official Sizzle tests from
 * within our build.
 *
 * @author Chris K. Jester-Young
 */
public class FrizzleTest {
    private Document testDoc;
    private Frizzle frizzle;

    @BeforeMethod
    public void setUp() {
        Context.enter();
        testDoc = getTestDoc();
        frizzle = new Frizzle(testDoc);
    }

    @AfterMethod
    public void tearDown() {
        frizzle = null;
        testDoc = null;
        Context.exit();
    }

    private static Document getTestDoc() {
        Document doc = DOMUtil.documentFromString(
                "<html>" +
                "  <head class=\"toplevel\">" +
                "    <title>Testing 1 2 3</title>" +
                "  </head>" +
                "  <body class=\"toplevel\">" +
                "    <p id=\"hello\">Hello, world!</p>" +
                "    <p id=\"goodbye\">Goodbye, world!</p>" +
                "  </body>" +
                "</html>");
        return doc;
    }

    private enum GetTagName implements Function<Element, String> {
        INSTANCE;

        @Override
        public String apply(Element elem) {
            return elem.getTagName();
        }
    }

    private static class GetTagAttr implements Function<Element, String> {
        private final String attr;

        public GetTagAttr(String attr) {
            this.attr = attr;
        }

        @Override
        public String apply(Element elem) {
            return elem.getAttribute(attr);
        }
    }

    private void assertTags(String expr, String... tags) {
        Element[] elements = frizzle.select(expr);
        Assert.assertEquals(
                Lists.transform(ImmutableList.copyOf(elements), GetTagName.INSTANCE),
                ImmutableList.copyOf(tags));
    }

    private void assertText(String expr, String text) {
        Element[] elements = frizzle.select(expr);
        Assert.assertEquals(frizzle.getText(elements), text);
    }

    private void assertAttr(String expr, String attr, String... values) {
        Element[] elements = frizzle.select(expr);
        Assert.assertEquals(
                Lists.transform(ImmutableList.copyOf(elements), new GetTagAttr(attr)),
                ImmutableList.copyOf(values));
    }

    @Test
    public void testSelectAll() {
        assertTags("*", "html", "head", "title", "body", "p", "p");
    }

    @Test
    public void testSelectSingleElement() {
        assertTags("title", "title");
    }

    @Test
    public void testSelectMultiElement() {
        assertTags("p", "p", "p");
    }

    @Test
    public void testSelectPseudoMatch() {
        assertTags("html:root", "html");
    }

    @Test
    public void testSelectPseudoNoMatch() {
        assertTags("head:root");
    }

    @Test
    public void testNegate() {
        assertTags(":not(:root)", "head", "title", "body", "p", "p");
    }

    @Test
    public void testDescendents() {
        assertTags("html *", "head", "title", "body", "p", "p");
    }

    @Test
    public void testChildren() {
        assertTags("html > *", "head", "body");
    }

    /*
     * The class/id tests below cannot use .class and #id syntax, since
     * those only apply for HTML (and our document, despite looking like
     * HTML, is actually parsed as XML). We use [class~=foo] and [id=bar]
     * in place of .foo and #bar.
     */

    @Test
    public void testClass() {
        assertTags("[class~=toplevel]", "head", "body");
    }

    @Test
    public void testClassElement() {
        assertTags("body[class~=toplevel]", "body");
    }

    @Test
    public void testId() {
        assertTags("[id=hello]", "p");
    }

    @Test
    public void testIdElement() {
        assertTags("p[id=goodbye]", "p");
    }

    @Test
    public void testIdMulti() {
        assertTags("[id=hello], [id=goodbye]", "p", "p");
    }

    @Test
    public void testTitleText() {
        assertText("title", "Testing 1 2 3");
    }

    @Test
    public void testIdText() {
        assertText("[id=hello]", "Hello, world!");
    }

    @Test
    public void testMultiText() {
        assertText("p", "Hello, world!Goodbye, world!");
    }

    @Test
    public void testChildText() {
        assertText("body", "    Hello, world!    Goodbye, world!  ");
    }

    @Test
    public void testSingleAttr() {
        assertAttr("body", "class", "toplevel");
    }

    @Test
    public void testMultiAttr() {
        assertAttr("p", "id", "hello", "goodbye");
    }

    @Test
    public void testContainsMiss() {
        Element title = frizzle.select("title")[0];
        Element body = frizzle.select("body")[0];
        Assert.assertEquals(frizzle.contains(body, title), false);
    }

    @Test
    public void testContainsHit() {
        Element body = frizzle.select("body")[0];
        Element p = frizzle.select("p")[0];
        Assert.assertEquals(frizzle.contains(body, p), true);
    }
}

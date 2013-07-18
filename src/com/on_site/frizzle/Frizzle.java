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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Closer;
import com.on_site.util.ContextCloseable;

import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Wrapper for the <a href="http://sizzlejs.com/">Sizzle</a> library.
 * Each instance of this class can only be used on a single thread.
 *
 * @author Chris K. Jester-Young
 */
public class Frizzle {
    private static final Script SIZZLE_SCRIPT = compileSizzle();

    private final Set<String> pseudos = Sets.newHashSet();
    private final Scriptable toplevel;
    private final Function sizzle;
    private Context cx;
    private WrapFactory savedFactory;

    private static Script compileSizzle() {
        try {
            Closer closer = Closer.create();
            try {
                Context cx = closer.register(new ContextCloseable()).getContext();
                URL sizzlejs = Frizzle.class.getResource("sizzle.js");
                Reader in = closer.register(new InputStreamReader(
                        sizzlejs.openStream(), Charsets.UTF_8));
                return cx.compileReader(in, sizzlejs.toString(), 1, null);
            } catch (Throwable t) {
                throw closer.rethrow(t);
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private void enterContext() {
        cx = Context.enter();
        savedFactory = cx.getWrapFactory();
        cx.setWrapFactory(new DOMWrapFactory());
    }

    private void exitContext() {
        cx.setWrapFactory(savedFactory);
        Context.exit();
    }

    private Object toJS(Object javaObject) {
        return Context.javaToJS(javaObject, toplevel);
    }

    public Frizzle(Document doc) {
        enterContext();
        try {
            this.toplevel = cx.initStandardObjects();
            Scriptable window = cx.newObject(toplevel);
            window.put("document", window, toJS(doc));
            toplevel.put("window", toplevel, window);
            SIZZLE_SCRIPT.exec(cx, toplevel);
            this.sizzle = (Function) window.get("Sizzle", window);
        } finally {
            exitContext();
        }
    }

    public void createPseudo(String name, Pseudo pseudo) {
        enterContext();
        try {
            Scriptable selectors = (Scriptable) sizzle.get("selectors", sizzle);
            Function createPseudo = (Function) selectors.get("createPseudo", selectors);
            Object object = createPseudo.call(cx, toplevel, selectors, new Object[] { pseudo.toJS() });
            Scriptable pseudos = (Scriptable) selectors.get("pseudos", selectors);
            pseudos.put(name, pseudos, object);
            this.pseudos.add(name);
        } finally {
            exitContext();
        }
    }

    public boolean hasPseudo(String name) {
        return pseudos.contains(name);
    }

    public Element[] select(String selector) {
        enterContext();
        try {
            return (Element[]) Context.jsToJava(
                    sizzle.call(cx, toplevel, null, new Object[] {selector}),
                    Element[].class);
        } finally {
            exitContext();
        }
    }

    public Element[] select(String selector, Element context) {
        enterContext();
        try {
            return (Element[]) Context.jsToJava(
                    sizzle.call(cx, toplevel, null, new Object[] {selector,
                            toJS(context)}),
                    Element[].class);
        } finally {
            exitContext();
        }
    }

    public Element[] select(String selector, Document context) {
        enterContext();
        try {
            return (Element[]) Context.jsToJava(
                    sizzle.call(cx, toplevel, null, new Object[] {selector,
                            toJS(context)}),
                    Element[].class);
        } finally {
            exitContext();
        }
    }

    public boolean matchesSelector(Element element, String selector) {
        enterContext();
        try {
            return (Boolean) Context.jsToJava(
                    ((Function) sizzle.get("matchesSelector", sizzle))
                        .call(cx, toplevel, sizzle, new Object[] {toJS(element),
                                selector}),
                    Boolean.class);
        } finally {
            exitContext();
        }
    }

    public Element[] matches(String selector, NodeList elements) {
        enterContext();
        try {
            return (Element[]) Context.jsToJava(((Function) sizzle.get("matches", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {selector, toJS(elements)}),
                    Element[].class);
        } finally {
            exitContext();
        }
    }

    public Element[] matches(String selector, Element[] elements) {
        enterContext();
        try {
            return (Element[]) Context.jsToJava(((Function) sizzle.get("matches", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {selector, toJS(elements)}),
                    Element[].class);
        } finally {
            exitContext();
        }
    }

    public boolean contains(Element parent, Element child) {
        enterContext();
        try {
            return (Boolean) Context.jsToJava(((Function) sizzle.get("contains", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {toJS(parent), toJS(child)}),
                    Boolean.class);
        } finally {
            exitContext();
        }
    }

    public String getText(Element elem) {
        enterContext();
        try {
            return (String) Context.jsToJava(((Function) sizzle.get("getText", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {elem}), String.class);
        } finally {
            exitContext();
        }
    }

    public String getText(NodeList elems) {
        enterContext();
        try {
            return (String) Context.jsToJava(((Function) sizzle.get("getText", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {elems}), String.class);
        } finally {
            exitContext();
        }
    }

    public String getText(Element[] elems) {
        /*
         * NativeJavaArray doesn't like having nonexisting properties
         * (like nodeType) accessed on it, so traverse elements manually
         * just like Sizzle.getText actually does.
         */
        enterContext();
        try {
            StringBuilder sb = new StringBuilder();
            for (Element elem : elems) {
                sb.append(Context.jsToJava(((Function) sizzle.get("getText", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {elem}), String.class));
            }
            return sb.toString();
        } finally {
            exitContext();
        }
    }

    public String attr(Element elem, String name) {
        enterContext();
        try {
            return (String) Context.jsToJava(((Function) sizzle.get("attr", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {elem, name}), String.class);
        } finally {
            exitContext();
        }
    }
}

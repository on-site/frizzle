/*
 * Copyright (c) 2013, 2014, 2016 On-Site.com.
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
import com.on_site.util.ContextCloseable;

import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
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

    private static Script compileSizzle() {
        URL sizzlejs = Frizzle.class.getResource("sizzle.js");
        try (Reader in = new InputStreamReader(sizzlejs.openStream(), Charsets.UTF_8);
                ContextCloseable cc = new ContextCloseable()) {
            return cc.getContext().compileReader(in, sizzlejs.toString(), 1, null);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private Object toJS(Object javaObject) {
        return Context.javaToJS(javaObject, toplevel);
    }

    public Frizzle(Document doc) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            this.toplevel = cx.initSafeStandardObjects();
            toplevel.put("document", toplevel, toJS(doc));
            toplevel.put("window", toplevel, toplevel);
            SIZZLE_SCRIPT.exec(cx, toplevel);
            this.sizzle = (Function) toplevel.get("Sizzle", toplevel);
        }
    }

    public void createPseudo(String name, Pseudo pseudo) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            Scriptable selectors = (Scriptable) sizzle.get("selectors", sizzle);
            Function createPseudo = (Function) selectors.get("createPseudo", selectors);
            Object object = createPseudo.call(cx, toplevel, selectors, new Object[] { pseudo.toJS() });
            Scriptable pseudos = (Scriptable) selectors.get("pseudos", selectors);
            pseudos.put(name, pseudos, object);
            this.pseudos.add(name);
        }
    }

    public boolean hasPseudo(String name) {
        return pseudos.contains(name);
    }

    public Element[] select(String selector) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (Element[]) Context.jsToJava(
                    sizzle.call(cx, toplevel, null, new Object[] {selector}),
                    Element[].class);
        }
    }

    public Element[] select(String selector, Element context) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (Element[]) Context.jsToJava(
                    sizzle.call(cx, toplevel, null, new Object[] {selector,
                            toJS(context)}),
                    Element[].class);
        }
    }

    public Element[] select(String selector, Document context) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (Element[]) Context.jsToJava(
                    sizzle.call(cx, toplevel, null, new Object[] {selector,
                            toJS(context)}),
                    Element[].class);
        }
    }

    public boolean matchesSelector(Element element, String selector) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (Boolean) Context.jsToJava(
                    ((Function) sizzle.get("matchesSelector", sizzle))
                        .call(cx, toplevel, sizzle, new Object[] {toJS(element),
                                selector}),
                    Boolean.class);
        }
    }

    public Element[] matches(String selector, NodeList elements) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (Element[]) Context.jsToJava(((Function) sizzle.get("matches", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {selector, toJS(elements)}),
                    Element[].class);
        }
    }

    public Element[] matches(String selector, Element[] elements) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (Element[]) Context.jsToJava(((Function) sizzle.get("matches", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {selector, toJS(elements)}),
                    Element[].class);
        }
    }

    public boolean contains(Element parent, Element child) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (Boolean) Context.jsToJava(((Function) sizzle.get("contains", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {toJS(parent), toJS(child)}),
                    Boolean.class);
        }
    }

    public String getText(Element elem) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (String) Context.jsToJava(((Function) sizzle.get("getText", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {toJS(elem)}), String.class);
        }
    }

    public String getText(NodeList elems) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (String) Context.jsToJava(((Function) sizzle.get("getText", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {toJS(elems)}), String.class);
        }
    }

    public String getText(Element[] elems) {
        /*
         * NativeJavaArray doesn't like having nonexisting properties
         * (like nodeType) accessed on it, so traverse elements manually
         * just like Sizzle.getText actually does.
         */
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            StringBuilder sb = new StringBuilder();
            for (Element elem : elems) {
                sb.append(Context.jsToJava(((Function) sizzle.get("getText", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {toJS(elem)}), String.class));
            }
            return sb.toString();
        }
    }

    public String attr(Element elem, String name) {
        try (ContextCloseable cc = new WrappedContextCloseable()) {
            Context cx = cc.getContext();
            return (String) Context.jsToJava(((Function) sizzle.get("attr", sizzle))
                    .call(cx, toplevel, sizzle, new Object[] {toJS(elem), name}), String.class);
        }
    }
}

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

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.on_site.util.DOMUtil;
import com.on_site.util.NodeListIterable;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A specialisation of {@link NativeJavaObject} with special behaviour
 * for DOM nodes. In particular, the {@code id}, {@code className}, and
 * {@code innerHTML} properties are added.
 *
 * @author Chris K. Jester-Young
 */
public class NativeDOMNode extends NativeDOMCommon {
    private static final ImmutableMap<String, String> PROPERTY_TO_ATTRIBUTE
            = ImmutableMap.of("id", "id",
                              "className", "class");

    private final Node elem;

    public NativeDOMNode(Scriptable scope, Node elem) {
        super(scope, elem, Node.class,
                elem instanceof NodeList ? new NodeListIterable((NodeList) elem) : null);
        this.elem = elem;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return elem instanceof Element && PROPERTY_TO_ATTRIBUTE.containsKey(name)
                || name.equals("innerHTML") || super.has(name, start);
    }

    @Override
    public Object get(String name, Scriptable start) {
        if (elem instanceof Element) {
            if (PROPERTY_TO_ATTRIBUTE.containsKey(name)) {
                return getAttr(PROPERTY_TO_ATTRIBUTE.get(name));
            }
        }
        if (name.equals("innerHTML")) {
            return getInnerHtml();
        }
        return super.get(name, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (elem instanceof Element) {
            if (PROPERTY_TO_ATTRIBUTE.containsKey(name)) {
                setAttr(PROPERTY_TO_ATTRIBUTE.get(name), value.toString());
                return;
            }
        }
        if (name.equals("innerHTML")) {
            setInnerHtml(value.toString());
            return;
        }
        super.put(name, start, value);
    }

    @Override
    protected ToStringHelper getToStringHelper() {
        return super.getToStringHelper()
                .add("element", elem);
    }

    private String getAttr(String name) {
        return ((Element) elem).getAttribute(name);
    }

    private void setAttr(String name, String value) {
        ((Element) elem).setAttribute(name, value);
    }

    private String getInnerHtml() {
        Document doc = elem.getOwnerDocument();
        DocumentFragment frag = doc.createDocumentFragment();
        for (Node node : new NodeListIterable(elem.getChildNodes())) {
            frag.appendChild(node.cloneNode(false));
        }
        return DOMUtil.stringFromNode(frag);
    }

    private void setInnerHtml(String value) {
        /*
         * This is all shades of broken, but that's because I can't find
         * any usable implementation of parseWithContext. :-(
         */
        Document tempDoc = DOMUtil.documentFromString("<root>" + value + "</root>");
        Document doc = elem.getOwnerDocument();
        DocumentFragment frag = doc.createDocumentFragment();
        for (Node node : new NodeListIterable(tempDoc.getDocumentElement().getChildNodes())) {
            frag.appendChild(doc.importNode(node, true));
        }

        for (Node node : new NodeListIterable(elem.getChildNodes())) {
            elem.removeChild(node);
        }
        elem.appendChild(frag);
    }
}

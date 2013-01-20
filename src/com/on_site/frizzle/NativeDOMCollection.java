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

import java.util.AbstractList;

import com.on_site.util.NodeListIterable;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A specialisation of {@link NativeJavaObject} with special behaviour
 * for DOM collections. In particular, the array getter is aliased to
 * the {@code item} method.
 *
 * @author Chris K. Jester-Young
 */
public class NativeDOMCollection extends NativeDOMCommon {
    public NativeDOMCollection(Scriptable scope, final DOMStringList list) {
        super(scope, list, DOMStringList.class, new AbstractList<String>() {
            @Override public String get(int index) {return list.item(index);}
            @Override public int size() {return list.getLength();}
        });
    }

    public NativeDOMCollection(Scriptable scope, final DOMImplementationList list) {
        super(scope, list, DOMImplementationList.class, new AbstractList<DOMImplementation>() {
            @Override public DOMImplementation get(int index) {return list.item(index);}
            @Override public int size() {return list.getLength();}
        });
    }

    public NativeDOMCollection(Scriptable scope, NodeList list) {
        super(scope, list, NodeList.class, new NodeListIterable(list));
    }

    public NativeDOMCollection(Scriptable scope, final NamedNodeMap map) {
        super(scope, map, NamedNodeMap.class, new AbstractList<Node>() {
            @Override public Node get(int index) {return map.item(index);}
            @Override public int size() {return map.getLength();}
        });
    }
}

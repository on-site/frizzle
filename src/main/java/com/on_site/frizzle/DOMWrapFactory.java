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

import com.on_site.frizzle.debug.LoggingDOMCollection;
import com.on_site.frizzle.debug.LoggingDOMNode;
import com.on_site.frizzle.debug.LoggingJavaObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMWrapFactory extends WrapFactory {
    private static final boolean DEBUG = Boolean.getBoolean("com.on_site.frizzle.debug");

    public DOMWrapFactory() {
        setJavaPrimitiveWrap(false);
    }

    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
            Object javaObject, Class<?> staticType) {
        return DEBUG ? wrapWithLogging(scope, javaObject, staticType)
                : wrapWithoutLogging(scope, javaObject, staticType);
    }

    private static Scriptable wrapWithLogging(Scriptable scope,
            Object javaObject, Class<?> staticType) {
        if (javaObject instanceof Node) {
            return new LoggingDOMNode(scope, (Node) javaObject);
        }
        if (javaObject instanceof DOMStringList) {
            return new LoggingDOMCollection(scope, (DOMStringList) javaObject);
        }
        if (javaObject instanceof DOMImplementationList) {
            return new LoggingDOMCollection(scope, (DOMImplementationList) javaObject);
        }
        if (javaObject instanceof NodeList) {
            return new LoggingDOMCollection(scope, (NodeList) javaObject);
        }
        if (javaObject instanceof NamedNodeMap) {
            return new LoggingDOMCollection(scope, (NamedNodeMap) javaObject);
        }
        return new LoggingJavaObject(scope, javaObject, staticType);
    }

    private static Scriptable wrapWithoutLogging(Scriptable scope,
            Object javaObject, Class<?> staticType) {
        if (javaObject instanceof Node) {
            return new NativeDOMNode(scope, (Node) javaObject);
        }
        if (javaObject instanceof DOMStringList) {
            return new NativeDOMCollection(scope, (DOMStringList) javaObject);
        }
        if (javaObject instanceof DOMImplementationList) {
            return new NativeDOMCollection(scope, (DOMImplementationList) javaObject);
        }
        if (javaObject instanceof NodeList) {
            return new NativeDOMCollection(scope, (NodeList) javaObject);
        }
        if (javaObject instanceof NamedNodeMap) {
            return new NativeDOMCollection(scope, (NamedNodeMap) javaObject);
        }
        return new NativeJavaObject(scope, javaObject, staticType);
    }
}

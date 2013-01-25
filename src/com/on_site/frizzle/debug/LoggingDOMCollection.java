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

package com.on_site.frizzle.debug;

import com.google.common.base.Objects;
import com.on_site.frizzle.NativeDOMCollection;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * A version of {@link NativeDOMCollection} with extensive logging.
 *
 * @author Chris K. Jester-Young
 */
public class LoggingDOMCollection extends NativeDOMCollection {
    private class Delegate implements LoggingMixin.Delegate {
        @Override
        public boolean has(String name, Scriptable start) {
            return LoggingDOMCollection.super.has(name, start);
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return LoggingDOMCollection.super.has(index, start);
        }

        @Override
        public Object get(String name, Scriptable start) {
            return LoggingDOMCollection.super.get(name, start);
        }

        @Override
        public Object get(int index, Scriptable start) {
            return LoggingDOMCollection.super.get(index, start);
        }

        @Override
        public void put(String name, Scriptable start, Object value) {
            LoggingDOMCollection.super.put(name, start, value);
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
            LoggingDOMCollection.super.put(index, start, value);
        }

        @Override
        public void delete(String name) {
            LoggingDOMCollection.super.delete(name);
        }

        @Override
        public void delete(int index) {
            LoggingDOMCollection.super.delete(index);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("this", LoggingDOMCollection.this)
                    .toString();
        }
    }

    private static final Logger LOGGER = Logger.getLogger(LoggingDOMCollection.class);
    private final LoggingMixin mixin;

    private void logConstruct(Class<?> collClass) {
        LOGGER.info(String.format("%s.<init>([%s]%s)", this, collClass.getName(), getCollection()));
    }

    public LoggingDOMCollection(Scriptable scope, DOMStringList list) {
        super(scope, list);
        logConstruct(list.getClass());
        mixin = createMixin();
    }

    public LoggingDOMCollection(Scriptable scope, DOMImplementationList list) {
        super(scope, list);
        logConstruct(list.getClass());
        mixin = createMixin();
    }

    public LoggingDOMCollection(Scriptable scope, NodeList list) {
        super(scope, list);
        logConstruct(list.getClass());
        mixin = createMixin();
    }

    public LoggingDOMCollection(Scriptable scope, NamedNodeMap map) {
        super(scope, map);
        logConstruct(map.getClass());
        mixin = createMixin();
    }

    private LoggingMixin createMixin() {
        return new LoggingMixin(new Delegate(), LOGGER);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return mixin.has(name, start);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return mixin.has(index, start);
    }

    @Override
    public Object get(String name, Scriptable start) {
        return mixin.get(name, start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        return mixin.get(index, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        mixin.put(name, start, value);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        mixin.put(index, start, value);
    }

    @Override
    public void delete(String name) {
        mixin.delete(name);
    }

    @Override
    public void delete(int index) {
        mixin.delete(index);
    }
}

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
import com.on_site.frizzle.NativeDOMNode;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Node;

/**
 * A version of {@link NativeDOMNode} with extensive logging.
 *
 * @author Chris K. Jester-Young
 */
public class LoggingDOMNode extends NativeDOMNode {
    private class Delegate implements LoggingMixin.Delegate {
        @Override
        public boolean has(String name, Scriptable start) {
            return LoggingDOMNode.super.has(name, start);
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return LoggingDOMNode.super.has(index, start);
        }

        @Override
        public Object get(String name, Scriptable start) {
            return LoggingDOMNode.super.get(name, start);
        }

        @Override
        public Object get(int index, Scriptable start) {
            return LoggingDOMNode.super.get(index, start);
        }

        @Override
        public void put(String name, Scriptable start, Object value) {
            LoggingDOMNode.super.put(name, start, value);
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
            LoggingDOMNode.super.put(index, start, value);
        }

        @Override
        public void delete(String name) {
            LoggingDOMNode.super.delete(name);
        }

        @Override
        public void delete(int index) {
            LoggingDOMNode.super.delete(index);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("this", LoggingDOMNode.this)
                    .toString();
        }
    }

    private static final Logger LOGGER = Logger.getLogger(LoggingDOMNode.class);
    private final LoggingMixin mixin;

    public LoggingDOMNode(Scriptable scope, Node elem) {
        super(scope, elem);
        LOGGER.info(String.format("%s.<init>(%s)", this, elem));
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

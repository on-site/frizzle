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
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * A version of {@link NativeJavaObject} with extensive logging.
 *
 * @author Chris K. Jester-Young
 */
public class LoggingJavaObject extends NativeJavaObject {
    private class Delegate implements LoggingMixin.Delegate {
        @Override
        public boolean has(String name, Scriptable start) {
            return LoggingJavaObject.super.has(name, start);
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return LoggingJavaObject.super.has(index, start);
        }

        @Override
        public Object get(String name, Scriptable start) {
            return LoggingJavaObject.super.get(name, start);
        }

        @Override
        public Object get(int index, Scriptable start) {
            return LoggingJavaObject.super.get(index, start);
        }

        @Override
        public void put(String name, Scriptable start, Object value) {
            LoggingJavaObject.super.put(name, start, value);
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
            LoggingJavaObject.super.put(index, start, value);
        }

        @Override
        public void delete(String name) {
            LoggingJavaObject.super.delete(name);
        }

        @Override
        public void delete(int index) {
            LoggingJavaObject.super.delete(index);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("this", LoggingJavaObject.this)
                    .toString();
        }
    }

    private static final Logger LOGGER = Logger.getLogger(LoggingJavaObject.class);
    private final LoggingMixin mixin;

    private void logConstruct(Object javaObject, Class<?> staticType) {
        LOGGER.info(String.format("%s.<init>([%s]%s)", this,
                staticType == null ? "?" : staticType.getName(), javaObject));
    }

    public LoggingJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
        logConstruct(javaObject, staticType);
        mixin = createMixin();
    }

    public LoggingJavaObject(Scriptable scope, Object javaObject, Class<?> staticType,
            boolean isAdapter) {
        super(scope, javaObject, staticType, isAdapter);
        logConstruct(javaObject, staticType);
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("javaObject", javaObject)
                .toString();
    }
}

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

import java.util.Arrays;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * A version of {@link Function} with extensive logging.
 *
 * @author Chris K. Jester-Young
 */
public class LoggingFunction extends ForwardingFunction {
    private class Delegate implements LoggingMixin.Delegate {
        @Override
        public boolean has(String name, Scriptable start) {
            return LoggingFunction.super.has(name, start);
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return LoggingFunction.super.has(index, start);
        }

        @Override
        public Object get(String name, Scriptable start) {
            return LoggingFunction.super.get(name, start);
        }

        @Override
        public Object get(int index, Scriptable start) {
            return LoggingFunction.super.get(index, start);
        }

        @Override
        public void put(String name, Scriptable start, Object value) {
            LoggingFunction.super.put(name, start, value);
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
            LoggingFunction.super.put(index, start, value);
        }

        @Override
        public void delete(String name) {
            LoggingFunction.super.delete(name);
        }

        @Override
        public void delete(int index) {
            LoggingFunction.super.delete(index);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("this", LoggingFunction.this)
                    .toString();
        }
    }

    private final String name;
    private final Function func;
    private final LoggingMixin mixin;

    public LoggingFunction(String name, Function func) {
        this.name = name;
        this.func = func;
        this.mixin = createMixin();
    }

    private LoggingMixin createMixin() {
        return new LoggingMixin(new Delegate());
    }

    @Override
    protected Function delegate() {
        return func;
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
    public boolean has(String name, Scriptable start) {
        return mixin.has(name, start);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return mixin.has(index, start);
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
    public Object call(Context cx, Scriptable scope,
            Scriptable thisObj, Object[] args) {
        Optional<Object> value = null;
        try {
            value = Optional.fromNullable(func.call(cx, scope, thisObj, args));
            return LoggingMixin.instrument(Object.class, name + "()", value);
        } finally {
            String lhs = thisObj == null ? name : thisObj + name;
            if (value == null) {
                LoggingMixin.log("{0}({1}) => abrupt", lhs, printableArgs(args));
            } else {
                LoggingMixin.log("{0}({1}) => {2}", lhs, printableArgs(args),
                        value.orNull());
            }
        }
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope,
            Object[] args) {
        Optional<Scriptable> value = null;
        try {
            value = Optional.fromNullable(func.construct(cx, scope, args));
            return LoggingMixin.instrument(Scriptable.class, "new " + name + "()", value);
        } finally {
            if (value == null) {
                LoggingMixin.log("new {0}({1}) => abrupt", name, printableArgs(args));
            } else {
                LoggingMixin.log("new {0}({1}) => {2}", name, printableArgs(args),
                        value.orNull());
            }
        }
    }

    private static String printableArgs(Object[] args) {
        return Joiner.on(", ").join(Lists.transform(Arrays.asList(args),
                new com.google.common.base.Function<Object, String>() {
            @Override
            public String apply(Object obj) {
                return obj instanceof String ? '"' + (String) obj + '"'
                        : String.valueOf(obj);
            }
        }));
    }
}

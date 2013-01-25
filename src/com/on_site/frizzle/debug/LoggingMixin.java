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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * A "mixin" used for doing logging operations. Now, Java doesn't
 * actually have mixins, so this is about as close as we can get.
 *
 * @author Chris K. Jester-Young
 */
class LoggingMixin {
    private static final Logger LOGGER = Logger.getLogger("com.on_site.frizzle");

    public interface Delegate {
        boolean has(String name, Scriptable start);
        boolean has(int index, Scriptable start);
        Object get(String name, Scriptable start);
        Object get(int index, Scriptable start);
        void put(String name, Scriptable start, Object value);
        void put(int index, Scriptable start, Object value);
        void delete(String name);
        void delete(int index);
    }

    private final Delegate delegate;

    public static void log(String format, Object... args) {
        /*
         * Grab the calling class and method (excluding anything from
         * this class); otherwise the log will always show this method,
         * which is silly.
         */
        String callingClass = null;
        String callingMethod = null;
        for (StackTraceElement frame : new Throwable().getStackTrace()) {
            if (frame.getClassName() != LoggingMixin.class.getName()) {
                callingClass = frame.getClassName();
                callingMethod = frame.getMethodName();
                break;
            }
        }
        LOGGER.logp(Level.INFO, callingClass, callingMethod, format, args);
    }

    public LoggingMixin(Delegate delegate) {
        this.delegate = delegate;
    }

    public boolean has(String name, Scriptable start) {
        Boolean value = null;
        try {
            return value = delegate.has(name, start);
        } finally {
            if (value == null) {
                log("\"{0}\" in {1} => abrupt", name, start);
            } else {
                log("\"{0}\" in {1} => {2}", name, start, value);
            }
        }
    }

    public boolean has(int index, Scriptable start) {
        Boolean value = null;
        try {
            return value = delegate.has(index, start);
        } finally {
            if (value == null) {
                log("{0} in {1} => abrupt", index, start);
            } else {
                log("{0} in {1} => {2}", index, start, value);
            }
        }
    }

    public Object get(String name, Scriptable start) {
        Optional<Object> value = null;
        try {
            value = Optional.fromNullable(delegate.get(name, start));
            return instrument(Object.class, "." + name, value);
        } finally {
            if (value == null) {
                log("{1}.{0} => abrupt", name, start);
            } else {
                if (!(value.orNull() instanceof Function)) {
                    log("{1}.{0} => {2}", name, start, value.orNull());
                }
            }
        }
    }

    public Object get(int index, Scriptable start) {
        Optional<Object> value = null;
        try {
            value = Optional.fromNullable(delegate.get(index, start));
            return instrument(Object.class, "[" + index + "]", value);
        } finally {
            if (value == null) {
                log("{1}[{0}] => abrupt", index, start);
            } else {
                if (!(value.orNull() instanceof Function)) {
                    log("{1}[{0}] => {2}", index, start, value.orNull());
                }
            }
        }
    }

    public void put(String name, Scriptable start, Object value) {
        log("{1}.{0} = {2}", name, start, value);
        delegate.put(name, start, value);
    }

    public void put(int index, Scriptable start, Object value) {
        log("{1}[{0}] = {2}", index, start, value);
        delegate.put(index, start, value);
    }

    public void delete(String name) {
        log("delete {1}.{0}", name, this);
        delegate.delete(name);
    }

    public void delete(int index) {
        log("delete {1}[{0}]", index, this);
        delegate.delete(index);
    }

    static <T> T instrument(Class<T> typeKey, String name, Optional<T> value) {
        T thing = value.orNull();
        if (thing instanceof Function) {
            return typeKey.cast(new LoggingFunction(name, (Function) thing));
        }
        return thing;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("delegate", delegate)
                .toString();
    }
}

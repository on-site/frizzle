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
import com.google.common.base.Optional;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * A "mixin" used for doing logging operations. Now, Java doesn't
 * actually have mixins, so this is about as close as we can get.
 *
 * @author Chris K. Jester-Young
 */
class LoggingMixin {
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
    private final Logger logger;

    public LoggingMixin(Delegate delegate, Logger logger) {
        this.delegate = delegate;
        this.logger = logger;
    }

    public boolean has(String name, Scriptable start) {
        Boolean value = null;
        try {
            return value = delegate.has(name, start);
        } finally {
            if (value == null) {
                logger.info(String.format("\"%s\" in %s => abrupt", name, start));
            } else {
                logger.info(String.format("\"%s\" in %s => %s", name, start, value));
            }
        }
    }

    public boolean has(int index, Scriptable start) {
        Boolean value = null;
        try {
            return value = delegate.has(index, start);
        } finally {
            if (value == null) {
                logger.info(String.format("%d in %s => abrupt", index, start));
            } else {
                logger.info(String.format("%d in %s => %s", index, start, value));
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
                logger.info(String.format("%s.%s => abrupt", start, name));
            } else {
                if (!(value.orNull() instanceof Function)) {
                    logger.info(String.format("%s.%s => %s", start, name, value.orNull()));
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
                logger.info(String.format("%s[%d] => abrupt", start, index));
            } else {
                if (!(value.orNull() instanceof Function)) {
                    logger.info(String.format("%s[%d] => %s", start, index, value.orNull()));
                }
            }
        }
    }

    public void put(String name, Scriptable start, Object value) {
        logger.info(String.format("%s.%s = %s", start, name, value));
        delegate.put(name, start, value);
    }

    public void put(int index, Scriptable start, Object value) {
        logger.info(String.format("%s[%d] = %s", start, index, value));
        delegate.put(index, start, value);
    }

    public void delete(String name) {
        logger.info(String.format("delete %s.%s", this, name));
        delegate.delete(name);
    }

    public void delete(int index) {
        logger.info(String.format("delete %s[%d]", this, index));
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
                .add("logger", logger)
                .toString();
    }
}

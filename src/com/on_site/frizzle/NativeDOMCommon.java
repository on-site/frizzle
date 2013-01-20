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

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * Common class for {@link NativeDOMNode} and {@link NativeDOMCollection}.
 *
 * @author Chris K. Jester-Young
 */
public abstract class NativeDOMCommon extends NativeJavaObject {
    private final List<?> collection;

    protected NativeDOMCommon(Scriptable scope, Object javaObject,
            Class<?> staticType, List<?> collection) {
        super(scope, javaObject, staticType);
        this.collection = collection;
    }

    protected List<?> getCollection() {
        return collection;
    }

    @Override
    public boolean has(int index, Scriptable start) {
        if (collection != null) {
            return index >= 0 && index < collection.size();
        }
        return super.has(index, start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        if (collection != null) {
            return Context.javaToJS(collection.get(index), parent);
        }
        return super.get(index, start);
    }

    @Override
    public String toString() {
        return getToStringHelper().toString();
    }

    protected ToStringHelper getToStringHelper() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("javaObject", javaObject)
                .add("staticType", staticType.getSimpleName())
                .add("values", collection);
    }
}

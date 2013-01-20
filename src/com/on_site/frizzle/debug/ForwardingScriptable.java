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

import com.google.common.collect.ForwardingObject;
import org.mozilla.javascript.Scriptable;

public abstract class ForwardingScriptable extends ForwardingObject implements Scriptable {
    @Override
    protected abstract Scriptable delegate();

    @Override
    public String getClassName() {
        return delegate().getClassName();
    }

    @Override
    public Object get(String name, Scriptable start) {
        return delegate().get(name, start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        return delegate().get(index, start);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return delegate().has(name,  start);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return delegate().has(index, start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        delegate().put(name, start, value);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        delegate().put(index, start, value);
    }

    @Override
    public void delete(String name) {
        delegate().delete(name);
    }

    @Override
    public void delete(int index) {
        delegate().delete(index);
    }

    @Override
    public Scriptable getPrototype() {
        return delegate().getPrototype();
    }

    @Override
    public void setPrototype(Scriptable prototype) {
        delegate().setPrototype(prototype);
    }

    @Override
    public Scriptable getParentScope() {
        return delegate().getParentScope();
    }

    @Override
    public void setParentScope(Scriptable parent) {
        delegate().setParentScope(parent);
    }

    @Override
    public Object[] getIds() {
        return delegate().getIds();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return delegate().getDefaultValue(hint);
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return delegate().hasInstance(instance);
    }
}

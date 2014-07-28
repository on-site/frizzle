/*
 * Copyright (c) 2014 On-Site.com.
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

import org.mozilla.javascript.WrapFactory;

import com.on_site.util.ContextCloseable;

/**
 * A {@link ContextCloseable} implmentation that also sets the context
 * up with {@link DOMWrapFactory}.
 *
 * @author Chris Jester-Young
 */
public class WrappedContextCloseable extends ContextCloseable {
    private final WrapFactory savedFactory;

    public WrappedContextCloseable() {
        savedFactory = cx.getWrapFactory();
        cx.setWrapFactory(new DOMWrapFactory());
    }

    @Override
    public void close() {
        cx.setWrapFactory(savedFactory);
        super.close();
    }
}

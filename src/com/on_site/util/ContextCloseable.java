package com.on_site.util;

import java.io.Closeable;

import org.mozilla.javascript.Context;

/**
 * "RAII" wrapper for {@link Context}.
 *
 * @author Chris K. Jester-Young
 */
public class ContextCloseable implements Closeable {
    private final Context cx;

    public ContextCloseable() {
        cx = Context.enter();
    }

    @Override
    public void close() {
        Context.exit();
    }

    public Context getContext() {
        return cx;
    }
}

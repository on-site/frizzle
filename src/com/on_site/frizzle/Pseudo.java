package com.on_site.frizzle;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Element;

/**
 * This class allows custom pseudo selectors to be created via the
 * Frizzle.createPseudo method.  The caller need only implement the
 * apply method.
 *
 * @author Mike Virata-Stone
 */
public abstract class Pseudo {
    /**
     * This method implements the logic of the pseudo selector.  It is
     * called with each element along with the argument passed to the
     * selector (if one was given).
     *
     * @param element The element being processed against this pseudo
     * selector.
     * @param argument The argument passed to the pseudo selector, or
     * null if none was supplied.
     * @return Whether or not the selector should apply to the given
     * element.
     */
    public abstract boolean apply(Element element, String argument);

    protected BaseFunction toJS() {
        return new Outer();
    }

    private class Outer extends BaseFunction {
        private Outer() {
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            String arg = null;

            if (args.length > 0 && args[0] instanceof String) {
                arg = (String) args[0];
            }

            return new Inner(arg);
        }
    }

    private class Inner extends BaseFunction {
        private final String argument;

        private Inner(String argument) {
            this.argument = argument;
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            Element element = (Element) Context.jsToJava(args[0], Element.class);
            return apply(element, argument);
        }
    }
}

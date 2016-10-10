package com.on_site.frizzle;

import com.google.common.base.Predicate;

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
     * first called with the argument passed to the selector (or null
     * if none was given).  The predicate returned is then called with
     * each element and used to determine which element to keep.
     *
     * @param argument The argument passed to the pseudo selector, or
     * null if none was supplied.
     * @return A predicate used to determine which element should be
     * selected by this pseudo selector.
     */
    public abstract Predicate<Element> apply(String argument);

    BaseFunction toJS() {
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

            return new Inner(apply(arg));
        }
    }

    private class Inner extends BaseFunction {
        private final Predicate<Element> predicate;

        private Inner(Predicate<Element> predicate) {
            this.predicate = predicate;
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            Element element = (Element) Context.jsToJava(args[0], Element.class);
            return predicate.apply(element);
        }
    }
}

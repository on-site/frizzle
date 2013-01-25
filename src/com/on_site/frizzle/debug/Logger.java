package com.on_site.frizzle.debug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simple reflection wrapper around log4j using just the log4j methods
 * that are currently needed by Frizzle.
 *
 * @author Mike Virata-Stone
 */
public class Logger {
    private static Method getLogger;
    private static Method info;
    private Object logger;
    private Method infoMethod;

    // Try to grab the 2 methods needed to construct a logger from
    // log4j, but fail silently if there is an error.
    static {
        try {
            Class<?> log4jLogger = Class.forName("org.apache.log4j.Logger");

            try {
                getLogger = log4jLogger.getDeclaredMethod("getLogger", Class.class);
                info = log4jLogger.getMethod("info", Object.class);
            } catch (NoSuchMethodException e) {
            }
        } catch (ClassNotFoundException e) {
        }
    }

    /**
     * Retrieve a new Logger for the given class.
     *
     * @param clazz The class the logger will be logging for.
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    /**
     * Construct a new logger for the given class.
     *
     * @param clazz The class the logger will be logging for.
     */
    public Logger(Class<?> clazz) {
        logger = getLog4jLogger(clazz);
    }

    /**
     * Retrieve the log4j logger for the given class, if log4j classes
     * can be found.  If there was an error while attempting to link
     * against log4j, this method will always return null, otherwise
     * it is equivalent to calling
     * <code>org.apache.log4j.Logger.getLogger(clazz)</code>.
     *
     * @param clazz The class to retrieve the log4j logger for.
     * @return A log4j logger, or null if log4j couldn't be loaded.
     */
    private static Object getLog4jLogger(Class<?> clazz) {
        if (getLogger == null || info == null) {
            return null;
        }

        try {
            return getLogger.invoke(null, clazz);
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Log the given message, if a log4j logger could be retrieved
     * when this Logger was created.
     *
     * @param message The message to attempt to log.
     */
    public void info(Object message) {
        try {
            if (logger != null && info != null) {
                info.invoke(logger, message);
            }
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }
}

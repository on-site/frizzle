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

package com.on_site.util;

import com.google.common.collect.ImmutableMap;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

/**
 * Simple DOM-related utilities for converting DOM trees to/from XML
 * strings.
 *
 * @author Chris K. Jester-Young
 */
public final class DOMUtil {
    private static final DOMImplementationLS LS;

    static {
        DOMImplementationRegistry reg;
        try {
            reg = DOMImplementationRegistry.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        LS = (DOMImplementationLS) reg.getDOMImplementation("LS 3.0");
    }

    private DOMUtil() {
        /* Disable instantiation for static class. */
    }

    public static Document documentFromLSInput(LSInput input) throws DOMException,
            LSException {
        return documentFromLSInput(input, null);
    }

    public static Document documentFromLSInput(LSInput input, Map<String, ?> config) throws DOMException,
            LSException {
        LSParser parser = LS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
        setupConfig(parser.getDomConfig(), config);
        return parser.parse(input);
    }

    public static Document documentFromStream(InputStream xml) throws DOMException,
            LSException {
        return documentFromStream(xml, null);
    }

    public static Document documentFromStream(InputStream xml, Map<String, ?> config) throws DOMException,
            LSException {
        LSInput input = LS.createLSInput();
        input.setByteStream(xml);
        return documentFromLSInput(input, config);
    }

    public static Document documentFromReader(Reader xml) throws DOMException,
            LSException {
        return documentFromReader(xml, null);
    }

    public static Document documentFromReader(Reader xml, Map<String, ?> config) throws DOMException,
            LSException {
        LSInput input = LS.createLSInput();
        input.setCharacterStream(xml);
        return documentFromLSInput(input, config);
    }

    public static Document documentFromString(String xml) throws DOMException,
            LSException {
        return documentFromString(xml, null);
    }

    public static Document documentFromString(String xml, Map<String, ?> config) throws DOMException,
            LSException {
        LSInput input = LS.createLSInput();
        input.setStringData(xml);
        return documentFromLSInput(input, config);
    }

    public static String stringFromNode(Node node) throws DOMException,
            LSException {
        return stringFromNode(node, false);
    }

    public static String stringFromNode(Node node, boolean pretty)
            throws DOMException, LSException {
        return stringFromNode(node, ImmutableMap.of("xml-declaration", Boolean.FALSE, "format-pretty-print", pretty));
    }

    public static String stringFromNode(Node node, Map<String, ?> config)
            throws DOMException, LSException {
        LSSerializer serial = LS.createLSSerializer();
        setupConfig(serial.getDomConfig(), config);
        return serial.writeToString(node);
    }

    private static void setupConfig(DOMConfiguration domConfig, Map<String, ?> config) {
        if (config == null) {
            return;
        }

        for (Map.Entry<String, ?> entry : config.entrySet()) {
            domConfig.setParameter(entry.getKey(), entry.getValue());
        }
    }
}

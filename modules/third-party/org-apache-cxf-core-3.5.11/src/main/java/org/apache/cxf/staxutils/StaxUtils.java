/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.staxutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.PropertyUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.common.util.SystemPropertyAction;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.message.Message;

public final class StaxUtils {
    // System properties for defaults, but also contextual properties usable
    // for StaxInInterceptor
    public static final String MAX_CHILD_ELEMENTS =
        "org.apache.cxf.stax.maxChildElements";
    public static final String MAX_ELEMENT_DEPTH =
        "org.apache.cxf.stax.maxElementDepth";
    public static final String MAX_ATTRIBUTE_COUNT =
        "org.apache.cxf.stax.maxAttributeCount";
    public static final String MAX_ATTRIBUTE_SIZE =
        "org.apache.cxf.stax.maxAttributeSize";
    public static final String MAX_TEXT_LENGTH =
        "org.apache.cxf.stax.maxTextLength";
    public static final String MIN_TEXT_SEGMENT =
        "org.apache.cxf.stax.minTextSegment";
    public static final String MAX_ELEMENT_COUNT =
        "org.apache.cxf.stax.maxElementCount";
    public static final String MAX_XML_CHARACTERS =
        "org.apache.cxf.stax.maxXMLCharacters";

    public static final String ALLOW_INSECURE_PARSER =
        "org.apache.cxf.stax.allowInsecureParser";

    private static final String INNER_ELEMENT_COUNT_SYSTEM_PROP =
        "org.apache.cxf.staxutils.innerElementCountThreshold";
    private static final String INNER_ELEMENT_LEVEL_SYSTEM_PROP =
        "org.apache.cxf.staxutils.innerElementLevelThreshold";
    private static final String AUTO_CLOSE_INPUT_SOURCE_PROP =
        "org.apache.cxf.staxutils.autoCloseInputSource";

    private static final Logger LOG = LogUtils.getL7dLogger(StaxUtils.class);

    private static final Queue<XMLInputFactory> NS_AWARE_INPUT_FACTORY_POOL;
    private static final XMLInputFactory SAFE_INPUT_FACTORY;
    private static final Queue<XMLOutputFactory> OUTPUT_FACTORY_POOL;
    private static final XMLOutputFactory SAFE_OUTPUT_FACTORY;

    private static final String XML_NS = "http://www.w3.org/2000/xmlns/";
    private static final String[] DEF_PREFIXES = new String[] {
        "ns1".intern(), "ns2".intern(), "ns3".intern(),
        "ns4".intern(), "ns5".intern(), "ns6".intern(),
        "ns7".intern(), "ns8".intern(), "ns9".intern()
    };

    private static final int MAX_ATTR_COUNT_VAL =
            getInteger(MAX_ATTRIBUTE_COUNT, 500);
    private static final int MAX_ATTR_SIZE_VAL =
            getInteger(MAX_ATTRIBUTE_SIZE, 64 * 1024); //64K per attribute, likely just "list" will hit
    private static final int MAX_TEXT_LENGTH_VAL =
            getInteger(MAX_TEXT_LENGTH, 128 * 1024 * 1024);  //128M - more than this should DEFINITELY use MTOM
    private static final int MIN_TEXT_SEGMENT_VAL =
            getInteger(MIN_TEXT_SEGMENT, 64); // Same default as woodstox
    // The MAX_XML_CHARACTERS should be aligned with MAX_TEXT_LENGTH (maximum contiguous length of any character
    // data segment)  
    private static final long MAX_XML_CHARS_VAL =
            getLong(MAX_XML_CHARACTERS, 256 * 1024 * 1024); //256Mb, maximum length of input document, in characters 
    private static final int PARSER_POOL_SIZE_VAL =
            getInteger("org.apache.cxf.staxutils.pool-size", 20);
    private static final boolean ALLOW_INSECURE_PARSER_VAL;
    private static final boolean AUTO_CLOSE_INPUT_SOURCE;

    // Here we check old names first and then new names for the threshold properties
    private static final int MAX_ELEMENT_DEPTH_VAL =
            getInteger(MAX_ELEMENT_DEPTH, getInteger(INNER_ELEMENT_LEVEL_SYSTEM_PROP, 100));
    private static final int MAX_CHILD_ELEMENTS_VAL =
            getInteger(MAX_CHILD_ELEMENTS, getInteger(INNER_ELEMENT_COUNT_SYSTEM_PROP, 50000));
    // The MAX_ELEMENT_COUNT should be aligned with MAX_CHILD_ELEMENTS
    private static final long MAX_ELEMENT_COUNT_VAL =
            getLong(MAX_ELEMENT_COUNT, Math.max(100L * MAX_CHILD_ELEMENTS_VAL, 50000L));

    // Variables from Woodstox
    private static final String P_MAX_ATTRIBUTES_PER_ELEMENT = "com.ctc.wstx.maxAttributesPerElement";
    private static final String P_MAX_ATTRIBUTE_SIZE = "com.ctc.wstx.maxAttributeSize";
    private static final String P_MAX_TEXT_LENGTH = "com.ctc.wstx.maxTextLength";
    private static final String P_MAX_ELEMENT_COUNT = "com.ctc.wstx.maxElementCount";
    private static final String P_MAX_CHARACTERS = "com.ctc.wstx.maxCharacters";
    private static final String P_MAX_ELEMENT_DEPTH = "com.ctc.wstx.maxElementDepth";
    private static final String P_MAX_CHILDREN_PER_ELEMENT = "com.ctc.wstx.maxChildrenPerElement";
    private static final String P_MIN_TEXT_SEGMENT = "com.ctc.wstx.minTextSegment";


    static {
        NS_AWARE_INPUT_FACTORY_POOL = new ArrayBlockingQueue<>(PARSER_POOL_SIZE_VAL);
        OUTPUT_FACTORY_POOL = new ArrayBlockingQueue<>(PARSER_POOL_SIZE_VAL);

        String allowInsecureParser = SystemPropertyAction.getPropertyOrNull(ALLOW_INSECURE_PARSER);
        if (!StringUtils.isEmpty(allowInsecureParser)) {
            ALLOW_INSECURE_PARSER_VAL = "1".equals(allowInsecureParser) || Boolean.parseBoolean(allowInsecureParser);
        } else {
            ALLOW_INSECURE_PARSER_VAL = false;
        }
        
        String autoCloseInputSource = SystemPropertyAction.getPropertyOrNull(AUTO_CLOSE_INPUT_SOURCE_PROP);
        if (!StringUtils.isEmpty(autoCloseInputSource)) {
            AUTO_CLOSE_INPUT_SOURCE = "1".equals(autoCloseInputSource) || Boolean.parseBoolean(autoCloseInputSource);
        } else {
            AUTO_CLOSE_INPUT_SOURCE = false; /* set 'false' by default */
        }

        XMLInputFactory xif = null;
        try {
            xif = createXMLInputFactory(true);
            String xifClassName = xif.getClass().getName();
            if (!xifClassName.contains("ctc.wstx") && !xifClassName.contains("xml.xlxp")
                    && !xifClassName.contains("xml.xlxp2") && !xifClassName.contains("bea.core")) {
                xif = null;
            }
        } catch (Throwable t) {
            //ignore, can always drop down to the pooled factories
        }
        SAFE_INPUT_FACTORY = xif;

        XMLOutputFactory xof = null;
        try {
            xof = XMLOutputFactory.newInstance();
            String xofClassName = xof.getClass().getName();
            if (!xofClassName.contains("ctc.wstx") && !xofClassName.contains("xml.xlxp")
                && !xofClassName.contains("xml.xlxp2") && !xofClassName.contains("bea.core")) {
                xof = null;
            }
        } catch (Throwable t) {
            //ignore, can always drop down to the pooled factories
        }
        SAFE_OUTPUT_FACTORY = xof;

    }

    private StaxUtils() {
    }
    private static int getInteger(String prop, int def) {
        try {
            String s = SystemPropertyAction.getPropertyOrNull(prop);
            if (StringUtils.isEmpty(s)) {
                return def;
            }
            int i = Integer.parseInt(s);
            if (i < 0) {
                i = def;
            }
            return i;
        } catch (Throwable t) {
            //ignore
        }
        return def;
    }
    private static long getLong(String prop, long def) {
        try {
            String s = SystemPropertyAction.getPropertyOrNull(prop);
            if (StringUtils.isEmpty(s)) {
                return def;
            }
            long i = Long.parseLong(s);
            if (i < 0) {
                i = def;
            }
            return i;
        } catch (Throwable t) {
            //ignore
        }
        return def;
    }

    /**
     * Return a cached, namespace-aware, factory.
     */
    private static XMLInputFactory getXMLInputFactory() {
        if (SAFE_INPUT_FACTORY != null) {
            return SAFE_INPUT_FACTORY;
        }
        XMLInputFactory f = NS_AWARE_INPUT_FACTORY_POOL.poll();
        if (f == null) {
            f = createXMLInputFactory(true);
        }
        return f;
    }

    private static void returnXMLInputFactory(XMLInputFactory factory) {
        if (SAFE_INPUT_FACTORY != factory) {
            NS_AWARE_INPUT_FACTORY_POOL.offer(factory);
        }
    }

    private static XMLOutputFactory getXMLOutputFactory() {
        if (SAFE_OUTPUT_FACTORY != null) {
            return SAFE_OUTPUT_FACTORY;
        }
        XMLOutputFactory f = OUTPUT_FACTORY_POOL.poll();
        if (f == null) {
            f = XMLOutputFactory.newInstance();
        }
        return f;
    }

    private static void returnXMLOutputFactory(XMLOutputFactory factory) {
        if (SAFE_OUTPUT_FACTORY != factory) {
            OUTPUT_FACTORY_POOL.offer(factory);
        }
    }

    /**
     * Return a new factory so that the caller can set sticky parameters.
     * @param nsAware
     * @throws XMLStreamException
     */
    public static XMLInputFactory createXMLInputFactory(boolean nsAware) {
        XMLInputFactory factory = null;
        try {
            factory = XMLInputFactory.newInstance();
        } catch (Throwable t) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "XMLInputFactory.newInstance() failed with: ", t);
            }
        }
        if (factory == null || !setRestrictionProperties(factory)) {
            try {
                factory = createWoodstoxFactory();
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Cannot create Woodstox XMLInputFactory: ", t);
                }
            }

            if (factory == null) {
                throw new RuntimeException("Failed to create XMLInputFactory.");
            }

            if (!setRestrictionProperties(factory)) {
                if (ALLOW_INSECURE_PARSER_VAL) {
                    LOG.log(Level.WARNING, "INSECURE_PARSER_DETECTED", factory.getClass().getName());
                } else {
                    throw new RuntimeException("Cannot create a secure XMLInputFactory, "
                        + "you should either add woodstox or set " + ALLOW_INSECURE_PARSER
                        + " system property to true if an unsafe mode is acceptable.");
                }
            }
        }
        setProperty(factory, XMLInputFactory.IS_NAMESPACE_AWARE, nsAware);
        setProperty(factory, XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        setProperty(factory, XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        setProperty(factory, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        factory.setXMLResolver(new XMLResolver() {
            public Object resolveEntity(String publicID, String systemID,
                                        String baseURI, String namespace)
                throws XMLStreamException {
                throw new XMLStreamException("Reading external entities is disabled");
            }
        });

        return factory;
    }

    private static XMLInputFactory createWoodstoxFactory() {
        return WoodstoxHelper.createInputFactory();
    }

    public static XMLEventFactory createWoodstoxEventFactory() {
        return WoodstoxHelper.createEventFactory();
    }

    private static boolean setRestrictionProperties(XMLInputFactory factory) {
        //For now, we can only support Woodstox 4.2.x and newer as none of the other
        //stax parsers support these settings
        final boolean wstxMaxs = setProperty(factory, P_MAX_ATTRIBUTES_PER_ELEMENT, MAX_ATTR_COUNT_VAL)
                    && setProperty(factory, P_MAX_ATTRIBUTE_SIZE, MAX_ATTR_SIZE_VAL)
                    && setProperty(factory, P_MAX_CHILDREN_PER_ELEMENT, MAX_CHILD_ELEMENTS_VAL)
                    && setProperty(factory, P_MAX_ELEMENT_COUNT, MAX_ELEMENT_COUNT_VAL)
                    && setProperty(factory, P_MAX_ELEMENT_DEPTH, MAX_ELEMENT_DEPTH_VAL)
                    && setProperty(factory, P_MAX_CHARACTERS, MAX_XML_CHARS_VAL)
                    && setProperty(factory, P_MAX_TEXT_LENGTH, MAX_TEXT_LENGTH_VAL);
        return wstxMaxs
            && setProperty(factory, P_MIN_TEXT_SEGMENT, MIN_TEXT_SEGMENT_VAL);
    }

    private static boolean setProperty(XMLInputFactory f, String p, Object o) {
        try {
            f.setProperty(p,  o);
            return true;
        } catch (Throwable t) {
            //ignore
        }
        return false;
    }



    public static XMLStreamWriter createXMLStreamWriter(Writer out) {
        XMLOutputFactory factory = getXMLOutputFactory();
        try {
            return factory.createXMLStreamWriter(out);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamWriter", e);
        } finally {
            returnXMLOutputFactory(factory);
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out) {
        return createXMLStreamWriter(out, null);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) {
        XMLOutputFactory factory = getXMLOutputFactory();
        try {
            return factory.createXMLStreamWriter(out, encoding != null ? encoding : StandardCharsets.UTF_8.name());
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamWriter", e);
        } finally {
            returnXMLOutputFactory(factory);
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(Result r) {
        if (r instanceof DOMResult) {
            //use our own DOM writer to avoid issues with Sun's
            //version that doesn't support getNamespaceContext
            DOMResult dr = (DOMResult)r;
            Node nd = dr.getNode();
            if (nd instanceof Document) {
                return new W3CDOMStreamWriter((Document)nd);
            } else if (nd instanceof Element) {
                return new W3CDOMStreamWriter((Element)nd);
            } else if (nd instanceof DocumentFragment) {
                return new W3CDOMStreamWriter((DocumentFragment)nd);
            }
        }
        XMLOutputFactory factory = getXMLOutputFactory();
        try {
            return factory.createXMLStreamWriter(r);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamWriter", e);
        } finally {
            returnXMLOutputFactory(factory);
        }
    }

    public static XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) {
        XMLInputFactory factory = getXMLInputFactory();
        try {
            return factory.createFilteredReader(reader, filter);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cant' create XMLStreamReader", e);
        } finally {
            returnXMLInputFactory(factory);
        }
    }


    public static void nextEvent(XMLStreamReader dr) {
        try {
            dr.next();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    public static boolean toNextText(DepthXMLStreamReader reader) {
        if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
            return true;
        }

        try {
            int depth = reader.getDepth();
            int event = reader.getEventType();
            while (reader.getDepth() >= depth && reader.hasNext()) {
                if (event == XMLStreamConstants.CHARACTERS && reader.getDepth() == depth + 1) {
                    return true;
                }
                event = reader.next();
            }
            return false;
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }
    public static boolean toNextTag(XMLStreamReader reader) {
        try {
            // advance to first tag.
            int x = reader.getEventType();
            while (x != XMLStreamConstants.START_ELEMENT
                && x != XMLStreamConstants.END_ELEMENT
                && reader.hasNext()) {
                x = reader.next();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
        return true;
    }

    public static boolean toNextTag(DepthXMLStreamReader reader, QName endTag) {
        try {
            int depth = reader.getDepth();
            int event = reader.getEventType();
            while (reader.getDepth() >= depth && reader.hasNext()) {
                if (event == XMLStreamConstants.START_ELEMENT && reader.getName().equals(endTag)
                    && reader.getDepth() == depth + 1) {
                    return true;
                }
                event = reader.next();
            }
            return false;
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    public static void writeStartElement(XMLStreamWriter writer, String prefix, String name, String namespace)
        throws XMLStreamException {
        if (prefix == null) {
            prefix = "";
        }

        if (!namespace.isEmpty()) {
            writer.writeStartElement(prefix, name, namespace);
            if (!prefix.isEmpty()) {
                writer.writeNamespace(prefix, namespace);
                writer.setPrefix(prefix, namespace);
            } else {
                writer.writeDefaultNamespace(namespace);
                writer.setDefaultNamespace(namespace);
            }
        } else {
            writer.writeStartElement(name);
            writer.writeDefaultNamespace("");
            writer.setDefaultNamespace("");
        }
    }

    /**
     * Returns true if currently at the start of an element, otherwise move
     * forwards to the next element start and return true, otherwise false is
     * returned if the end of the stream is reached.
     */
    public static boolean skipToStartOfElement(XMLStreamReader in) throws XMLStreamException {
        for (int code = in.getEventType(); code != XMLStreamConstants.END_DOCUMENT; code = in.next()) {
            if (code == XMLStreamConstants.START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    public static boolean toNextElement(DepthXMLStreamReader dr) {
        if (dr.getEventType() == XMLStreamConstants.START_ELEMENT) {
            return true;
        }
        if (dr.getEventType() == XMLStreamConstants.END_ELEMENT) {
            return false;
        }
        try {
            int depth = dr.getDepth();

            for (int event = dr.getEventType(); dr.getDepth() >= depth && dr.hasNext(); event = dr.next()) {
                if (event == XMLStreamConstants.START_ELEMENT && dr.getDepth() == depth + 1) {
                    return true;
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    depth--;
                }
            }

            return false;
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    public static boolean skipToStartOfElement(DepthXMLStreamReader in) throws XMLStreamException {
        for (int code = in.getEventType(); code != XMLStreamConstants.END_DOCUMENT; code = in.next()) {
            if (code == XMLStreamConstants.START_ELEMENT) {
                return true;
            }
        }
        return false;
    }
    public static void copy(Source source, OutputStream os) throws XMLStreamException {
        XMLStreamWriter writer = createXMLStreamWriter(os);
        try {
            copy(source, writer);
        } finally {
            try {
                writer.flush();
            } catch (XMLStreamException ex) {
                //ignore
            }
            StaxUtils.close(writer);
        }
    }
    public static void copy(Source source, XMLStreamWriter writer) throws XMLStreamException {
        if (source instanceof StaxSource) {
            StaxSource ss = (StaxSource)source;
            if (ss.getXMLStreamReader() == null) {
                return;
            }
        } else if (source instanceof StAXSource) {
            StAXSource ss = (StAXSource)source;
            if (ss.getXMLStreamReader() == null) {
                return;
            }
        } else if (source instanceof SAXSource) {
            SAXSource ss = (SAXSource)source;
            InputSource src = ss.getInputSource();
            if (src == null || (src.getSystemId() == null && src.getPublicId() == null)) {
                if (ss.getXMLReader() != null) {
                    //OK - reader is OK.  We'll use that out
                    StreamWriterContentHandler ch = new StreamWriterContentHandler(writer);
                    XMLReader reader = ((SAXSource)source).getXMLReader();
                    reader.setContentHandler(ch);
                    try {
                        try {
                            reader.setFeature("http://xml.org/sax/features/namespaces", true);
                        } catch (Throwable t) {
                            //ignore
                        }
                        try {
                            reader.setProperty("http://xml.org/sax/properties/lexical-handler", ch);
                        } catch (Throwable t) {
                            //ignore
                        }
                        reader.parse(((SAXSource)source).getInputSource());
                        return;
                    } catch (Exception e) {
                        throw new XMLStreamException(e.getMessage(), e);
                    }
                } else if (ss.getInputSource() == null) {
                    //nothing to copy, just return
                    return;
                }
            }

        } else if (source instanceof StreamSource) {
            StreamSource ss = (StreamSource)source;
            if (ss.getInputStream() == null
                && ss.getReader() == null
                && ss.getSystemId() == null) {
                //nothing to copy, just return
                return;
            }
        }
        XMLStreamReader reader = createXMLStreamReader(source);
        copy(reader, writer);
        reader.close();
    }

    public static Document copy(Document doc)
        throws XMLStreamException, ParserConfigurationException {

        XMLStreamReader reader = createXMLStreamReader(doc);
        W3CDOMStreamWriter writer = new W3CDOMStreamWriter();
        copy(reader, writer);
        Document d = writer.getDocument();
        try {
            d.setDocumentURI(doc.getDocumentURI());
        } catch (Exception ex) {
            //ignore - probably not DOM level 3
        }
        return d;
    }
    public static void copy(Document doc, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(doc);
        copy(reader, writer);
    }
    public static void copy(Element node, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(node);
        copy(reader, writer);
    }

    public static void copy(XMLStreamReader reader, OutputStream os)
        throws XMLStreamException {
        XMLStreamWriter xsw = StaxUtils.createXMLStreamWriter(os);
        StaxUtils.copy(reader, xsw);
        xsw.close();
    }

    public static void writeTo(Node node, OutputStream os) throws XMLStreamException {
        copy(new DOMSource(node), os);
    }
    public static void writeTo(Node node, OutputStream os, int indent) throws XMLStreamException {
        if (indent > 0) {
            XMLStreamWriter writer = new PrettyPrintXMLStreamWriter(createXMLStreamWriter(os), indent);
            try {
                copy(new DOMSource(node), writer);
            } finally {
                writer.close();
            }
        } else {
            copy(new DOMSource(node), os);
        }
    }
    public static void writeTo(Node node, Writer os) throws XMLStreamException {
        writeTo(node, os, 0);
    }
    public static void writeTo(Node node, Writer os, int indent) throws XMLStreamException {
        XMLStreamWriter writer = createXMLStreamWriter(os);
        if (indent > 0) {
            writer = new PrettyPrintXMLStreamWriter(writer, indent);
        }
        try {
            copy(new DOMSource(node), writer);
        } finally {
            writer.close();
        }
    }


    /**
     * Copies the reader to the writer. The start and end document methods must
     * be handled on the writer manually.
     *
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    public static void copy(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        copy(reader, writer, false, false);
    }
    public static void copy(XMLStreamReader reader, XMLStreamWriter writer, boolean fragment)
        throws XMLStreamException {
        copy(reader, writer, fragment, false);
    }
    public static void copy(XMLStreamReader reader,
                            XMLStreamWriter writer,
                            boolean fragment,
                            boolean isThreshold) throws XMLStreamException {
        // number of elements read in
        int read = 0;
        int elementCount = 0;
        final Deque<Integer> countStack = new ArrayDeque<>();
        int event = reader.getEventType();

        while (reader.hasNext()) {
            switch (event) {
            case XMLStreamConstants.START_ELEMENT:
                read++;
                if (isThreshold) {
                    elementCount++;

                    if (MAX_ELEMENT_DEPTH_VAL != -1 && read >= MAX_ELEMENT_DEPTH_VAL) {
                        throw new DepthExceededStaxException("reach the innerElementLevelThreshold:"
                                                   + MAX_ELEMENT_DEPTH_VAL);
                    }
                    if (MAX_CHILD_ELEMENTS_VAL != -1 && elementCount >= MAX_CHILD_ELEMENTS_VAL) {
                        throw new DepthExceededStaxException("reach the innerElementCountThreshold:"
                                                   + MAX_CHILD_ELEMENTS_VAL);
                    }
                    countStack.push(elementCount);
                    elementCount = 0;
                }
                writeStartElement(reader, writer);
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (read > 0) {
                    writer.writeEndElement();
                }
                read--;
                if (read < 0 && fragment) {
                    return;
                }
                if (isThreshold && !countStack.isEmpty()) {
                    elementCount = countStack.pop();
                }
                break;
            case XMLStreamConstants.CHARACTERS:
            case XMLStreamConstants.SPACE:
                String s = reader.getText();
                if (s != null) {
                    writer.writeCharacters(s);
                }
                break;
            case XMLStreamConstants.COMMENT:
                writer.writeComment(reader.getText());
                break;
            case XMLStreamConstants.CDATA:
                writer.writeCData(reader.getText());
                break;
            case XMLStreamConstants.START_DOCUMENT:
            case XMLStreamConstants.END_DOCUMENT:
            case XMLStreamConstants.ATTRIBUTE:
            case XMLStreamConstants.NAMESPACE:
                break;
            default:
                break;
            }
            event = reader.next();
        }
    }

    private static void writeStartElement(XMLStreamReader reader, XMLStreamWriter writer)
        throws XMLStreamException {
        String uri = reader.getNamespaceURI();
        String prefix = reader.getPrefix();
        String local = reader.getLocalName();

        if (prefix == null) {
            prefix = "";
        }

        boolean writeElementNS = false;

        if (uri != null) {
            writeElementNS = true;
            Iterator<String> it = CastUtils.cast(writer.getNamespaceContext().getPrefixes(uri));
            if (!it.hasNext() && StringUtils.isEmpty(prefix) && StringUtils.isEmpty(uri)
                && StringUtils.isEmpty(writer.getNamespaceContext().getNamespaceURI(""))) {
                writeElementNS = false;
            }
            while (it.hasNext()) {
                String s = it.next();
                if (s == null) {
                    s = "";
                }
                if (s.equals(prefix)) {
                    writeElementNS = false;
                }
            }
        }

        // Write out the element name
        if (uri != null) {
            if (prefix.isEmpty() && StringUtils.isEmpty(uri)) {
                writer.writeStartElement(local);
            } else {
                writer.writeStartElement(prefix, local, uri);
            }
        } else {
            writer.writeStartElement(local);
        }

        // Write out the namespaces
        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            String nsURI = reader.getNamespaceURI(i);
            String nsPrefix = reader.getNamespacePrefix(i);
            if (nsPrefix == null) {
                nsPrefix = "";
            }
            if (nsURI == null) {
                nsURI = "";
            }
            if (nsPrefix.isEmpty()) {
                writer.writeDefaultNamespace(nsURI);
                writer.setDefaultNamespace(nsURI);
            } else {
                writer.writeNamespace(nsPrefix, nsURI);
                writer.setPrefix(nsPrefix, nsURI);
            }

            if (nsURI.equals(uri) && nsPrefix.equals(prefix)) {
                writeElementNS = false;
            }
        }

        // Check if the namespace still needs to be written.
        // We need this check because namespace writing works
        // different on Woodstox and the RI.
        if (writeElementNS) {
            if (prefix.isEmpty()) {
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            } else {
                writer.writeNamespace(prefix, uri);
                writer.setPrefix(prefix, uri);
            }
        }

        // Write out attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String ns = reader.getAttributeNamespace(i);
            String nsPrefix = reader.getAttributePrefix(i);
            if (ns == null || ns.isEmpty()) {
                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            } else if (nsPrefix == null || nsPrefix.isEmpty()) {
                writer.writeAttribute(reader.getAttributeNamespace(i), reader.getAttributeLocalName(i),
                                      reader.getAttributeValue(i));
            } else {
                Iterator<String> it = CastUtils.cast(writer.getNamespaceContext().getPrefixes(ns));
                boolean writeNs = true;
                while (it != null && it.hasNext()) {
                    String s = it.next();
                    if (s == null) {
                        s = "";
                    }
                    if (s.equals(nsPrefix)) {
                        writeNs = false;
                    }
                }
                if (writeNs) {
                    writer.writeNamespace(nsPrefix, ns);
                    writer.setPrefix(nsPrefix, ns);
                }
                writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), reader
                    .getAttributeLocalName(i), reader.getAttributeValue(i));
            }

        }
    }

    public static void writeDocument(Document d, XMLStreamWriter writer, boolean repairing)
        throws XMLStreamException {
        writeDocument(d, writer, true, repairing);
    }

    public static void writeDocument(Document d, XMLStreamWriter writer, boolean writeProlog,
                                     boolean repairing) throws XMLStreamException {
        if (writeProlog) {
            writer.writeStartDocument();
        }

        Node node = d.getFirstChild();
        while (node != null) {
            if (writeProlog || node.getNodeType() == Node.ELEMENT_NODE) {
                writeNode(node, writer, repairing);
            }
            node = node.getNextSibling();
        }

        if (writeProlog) {
            writer.writeEndDocument();
        }
    }

    /**
     * Writes an Element to an XMLStreamWriter. The writer must already have
     * started the document (via writeStartDocument()). Also, this probably
     * won't work with just a fragment of a document. The Element should be the
     * root element of the document.
     *
     * @param e
     * @param writer
     * @throws XMLStreamException
     */
    public static void writeElement(Element e, XMLStreamWriter writer, boolean repairing)
        throws XMLStreamException {
        writeElement(e, writer, repairing, true);
    }

    /**
     * Writes an Element to an XMLStreamWriter. The writer must already have
     * started the document (via writeStartDocument()). Also, this probably
     * won't work with just a fragment of a document. The Element should be the
     * root element of the document.
     *
     * @param e
     * @param writer
     * @param endElement true if the element should be ended
     * @throws XMLStreamException
     */
    public static void writeElement(Element e,
                                    XMLStreamWriter writer,
                                    boolean repairing,
                                    boolean endElement)
        throws XMLStreamException {
        String prefix = e.getPrefix();
        String ns = e.getNamespaceURI();
        String localName = e.getLocalName();

        if (prefix == null) {
            prefix = "";
        }
        if (localName == null) {
            localName = e.getNodeName();

            if (localName == null) {
                throw new IllegalStateException("Element's local name cannot be null!");
            }
        }

        String decUri = writer.getNamespaceContext().getNamespaceURI(prefix);
        boolean declareNamespace = decUri == null || !decUri.equals(ns);

        if (ns == null || ns.isEmpty()) {
            writer.writeStartElement(localName);
            if (StringUtils.isEmpty(decUri)) {
                declareNamespace = false;
            }
        } else {
            writer.writeStartElement(prefix, localName, ns);
        }

        for (Node attr : sortElementAttributes(e.getAttributes())) {

            String name = attr.getLocalName();
            String attrPrefix = attr.getPrefix();
            if (attrPrefix == null) {
                attrPrefix = "";
            }
            if (name == null) {
                name = attr.getNodeName();
            }

            if ("xmlns".equals(attrPrefix)) {
                writer.writeNamespace(name, attr.getNodeValue());
                writer.setPrefix(name, attr.getNodeValue());
                if (name.equals(prefix) && attr.getNodeValue().equals(ns)) {
                    declareNamespace = false;
                }
            } else {
                if ("xmlns".equals(name) && "".equals(attrPrefix)) {
                    writer.writeDefaultNamespace(attr.getNodeValue());
                    writer.setDefaultNamespace(attr.getNodeValue());
                    if (attr.getNodeValue().equals(ns)) {
                        declareNamespace = false;
                    } else if (StringUtils.isEmpty(attr.getNodeValue())
                        && StringUtils.isEmpty(ns)) {
                        declareNamespace = false;
                    }
                } else {
                    String attns = attr.getNamespaceURI();
                    String value = attr.getNodeValue();
                    if (attns == null || attns.isEmpty()) {
                        writer.writeAttribute(name, value);
                    } else if (attrPrefix.isEmpty()) {
                        writer.writeAttribute(attns, name, value);
                    } else {
                        if (repairing && writer.getNamespaceContext().getNamespaceURI(attrPrefix) == null) {
                            writer.writeNamespace(attrPrefix, attns);
                        }
                        writer.writeAttribute(attrPrefix, attns, name, value);
                    }
                }
            }
        }

        if (declareNamespace && repairing) {
            if (ns == null) {
                writer.writeNamespace(prefix, "");
                writer.setPrefix(prefix, "");
            } else {
                writer.writeNamespace(prefix, ns);
                writer.setPrefix(prefix, ns);
            }
        }

        Node nd = e.getFirstChild();
        while (nd != null) {
            writeNode(nd, writer, repairing);
            nd = nd.getNextSibling();
        }

        if (endElement) {
            writer.writeEndElement();
        }
    }

    private static List<Node> sortElementAttributes(NamedNodeMap attrs) {
        if (attrs.getLength() == 0) {
            return Collections.<Node> emptyList();
        }
        List<Node> sortedAttrs = new ArrayList<>(attrs.getLength());
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            String name = attr.getLocalName();
            if (name == null) {
                name = attr.getNodeName();
            }
            if ("xmlns".equals(attr.getPrefix()) || "xmlns".equals(name)) {
                sortedAttrs.add(0, attr);
            } else {
                sortedAttrs.add(attr);
            }
        }

        return sortedAttrs;
    }

    public static void writeNode(Node n, XMLStreamWriter writer, boolean repairing)
        throws XMLStreamException {

        switch (n.getNodeType()) {
        case Node.ELEMENT_NODE:
            writeElement((Element)n, writer, repairing);
            break;
        case Node.TEXT_NODE:
            writer.writeCharacters(((Text)n).getNodeValue());
            break;
        case Node.COMMENT_NODE:
            writer.writeComment(((Comment)n).getData());
            break;
        case Node.CDATA_SECTION_NODE:
            writer.writeCData(((CDATASection)n).getData());
            break;
        case Node.ENTITY_REFERENCE_NODE:
            writer.writeEntityRef(((EntityReference)n).getNodeValue());
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            ProcessingInstruction pi = (ProcessingInstruction)n;
            writer.writeProcessingInstruction(pi.getTarget(), pi.getData());
            break;
        case Node.DOCUMENT_NODE:
            writeDocument((Document)n, writer, repairing);
            break;
        case Node.DOCUMENT_FRAGMENT_NODE: {
            DocumentFragment frag = (DocumentFragment)n;
            Node child = frag.getFirstChild();
            while (child != null) {
                writeNode(child, writer, repairing);
                child = child.getNextSibling();
            }
            break;
        }
        case Node.DOCUMENT_TYPE_NODE:
            try {
                if (((DocumentType)n).getTextContent() != null) {
                    writer.writeDTD(((DocumentType)n).getTextContent());
                }
            } catch (UnsupportedOperationException ex) {
                //can we ignore?  DOM writers really don't allow this
                //as there isn't a way to write a DTD in dom
            }
            break;
        default:
            throw new IllegalStateException("Found type: " + n.getClass().getName());
        }
    }

    public static Document read(Source s) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(s);
        try {
            return read(reader);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }
    public static Document read(InputStream s) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(s);
        try {
            return read(reader);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }
    public static Document read(Reader s) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(s);
        try {
            return read(reader);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                //ignore
            }
        }
    }
    public static Document read(File is) throws XMLStreamException, IOException {
        try (InputStream fin = Files.newInputStream(is.toPath())) {
            return read(fin);
        }
    }
    public static Document read(InputSource s) throws XMLStreamException {
        XMLStreamReader reader = null;
        try {
            reader = createXMLStreamReader(s);
            return read(reader);
        } finally {
            StaxUtils.close(reader);
        }
    }
    public static Document read(XMLStreamReader reader) throws XMLStreamException {
        return read(reader, false);
    }
    public static Document read(XMLStreamReader reader, boolean recordLoc) throws XMLStreamException {
        Document doc = DOMUtils.createDocument();
        if (reader.getLocation().getSystemId() != null) {
            try {
                doc.setDocumentURI(reader.getLocation().getSystemId());
            } catch (Exception e) {
                //ignore - probably not DOM level 3
            }
        }
        readDocElements(doc, doc, reader, true, recordLoc);
        return doc;
    }

    public static Document read(DocumentBuilder builder, XMLStreamReader reader, boolean repairing)
        throws XMLStreamException {

        Document doc = builder == null ? DOMUtils.createDocument() : builder.newDocument();
        if (reader.getLocation().getSystemId() != null) {
            try {
                doc.setDocumentURI(reader.getLocation().getSystemId());
            } catch (Exception e) {
                //ignore - probably not DOM level 3
            }
        }
        readDocElements(doc, reader, repairing);
        return doc;
    }

    /**
     * @param parent
     */
    private static Document getDocument(Node parent) {
        return (parent instanceof Document) ? (Document)parent : parent.getOwnerDocument();
    }

    private static boolean isDeclared(Element e, String namespaceURI, String prefix) {
        while (e != null) {
            Attr att;
            if (prefix != null && !prefix.isEmpty()) {
                att = e.getAttributeNodeNS(XML_NS, prefix);
            } else {
                att = e.getAttributeNode("xmlns");
            }

            if (att != null && att.getNodeValue().equals(namespaceURI)) {
                return true;
            }

            if (e.getParentNode() instanceof Element) {
                e = (Element)e.getParentNode();
            } else if (StringUtils.isEmpty(prefix) && StringUtils.isEmpty(namespaceURI)) {
                //A document that probably doesn't have any namespace qualifies elements
                return true;
            } else {
                break;
            }
        }
        return false;
    }

    public static void readDocElements(Node parent, XMLStreamReader reader, boolean repairing)
        throws XMLStreamException {
        Document doc = getDocument(parent);
        readDocElements(doc, parent, reader, repairing, false);
    }

    public static void readDocElements(Node parent, XMLStreamReader reader, boolean repairing,
                                       boolean isThreshold)
        throws XMLStreamException {
        Document doc = getDocument(parent);
        readDocElements(doc, parent, reader, repairing, false, isThreshold);
    }

    /**
     * @param parent
     * @param reader
     * @throws XMLStreamException
     */
    public static void readDocElements(Document doc, Node parent,
                                       XMLStreamReader reader, boolean repairing, boolean recordLoc)
        throws XMLStreamException {
        readDocElements(doc, parent, reader, repairing, recordLoc, false);
    }

    /**
     * @param parent
     * @param reader
     * @throws XMLStreamException
     */
    public static void readDocElements(Document doc, Node parent,
                                       XMLStreamReader reader, boolean repairing, boolean recordLoc,
                                       boolean isThreshold)
        throws XMLStreamException {
        final Deque<Node> stack = new ArrayDeque<>();
        int event = reader.getEventType();
        int elementCount = 0;
        while (reader.hasNext()) {
            switch (event) {
            case XMLStreamConstants.START_ELEMENT: {
                elementCount++;
                Element e;
                if (!StringUtils.isEmpty(reader.getPrefix())) {
                    e = doc.createElementNS(reader.getNamespaceURI(),
                                            reader.getPrefix() + ':' + reader.getLocalName());
                } else {
                    e = doc.createElementNS(reader.getNamespaceURI(), reader.getLocalName());
                }
                e = (Element)parent.appendChild(e);
                recordLoc = addLocation(doc, e, reader, recordLoc);

                for (int ns = 0; ns < reader.getNamespaceCount(); ns++) {
                    String uri = reader.getNamespaceURI(ns);
                    String prefix = reader.getNamespacePrefix(ns);

                    declare(e, uri, prefix);
                }

                for (int att = 0; att < reader.getAttributeCount(); att++) {
                    String name = reader.getAttributeLocalName(att);
                    String prefix = reader.getAttributePrefix(att);
                    if (prefix != null && !prefix.isEmpty()) {
                        name = prefix + ':' + name;
                    }

                    Attr attr = doc.createAttributeNS(reader.getAttributeNamespace(att), name);
                    attr.setValue(reader.getAttributeValue(att));
                    e.setAttributeNode(attr);
                }

                if (repairing && !isDeclared(e, reader.getNamespaceURI(), reader.getPrefix())) {
                    declare(e, reader.getNamespaceURI(), reader.getPrefix());
                }
                stack.push(parent);
                if (isThreshold && MAX_ELEMENT_DEPTH_VAL != -1
                    && stack.size() >= MAX_ELEMENT_DEPTH_VAL) {
                    throw new DepthExceededStaxException("reach the innerElementLevelThreshold:"
                                               + MAX_ELEMENT_DEPTH_VAL);
                }
                if (isThreshold && MAX_CHILD_ELEMENTS_VAL != -1
                    && elementCount >= MAX_CHILD_ELEMENTS_VAL) {
                    throw new DepthExceededStaxException("reach the innerElementCountThreshold:"
                                               + MAX_CHILD_ELEMENTS_VAL);
                }
                parent = e;
                break;
            }
            case XMLStreamConstants.END_ELEMENT:
                if (stack.isEmpty()) {
                    return;
                }
                parent = stack.pop();
                if (parent instanceof Document || parent instanceof DocumentFragment) {
                    return;
                }
                break;
            case XMLStreamConstants.NAMESPACE:
                break;
            case XMLStreamConstants.ATTRIBUTE:
                break;
            case XMLStreamConstants.CHARACTERS:
                if (parent != null) {
                    recordLoc = addLocation(doc,
                                            parent.appendChild(doc.createTextNode(reader.getText())),
                                            reader, recordLoc);
                }
                break;
            case XMLStreamConstants.COMMENT:
                if (parent != null) {
                    parent.appendChild(doc.createComment(reader.getText()));
                }
                break;
            case XMLStreamConstants.CDATA:
                recordLoc = addLocation(doc,
                                        parent.appendChild(doc.createCDATASection(reader.getText())),
                                        reader, recordLoc);
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));
                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));
                break;
            default:
                break;
            }

            if (reader.hasNext()) {
                event = reader.next();
            }
        }
    }

    public static class StreamToDOMContext {
        private final Deque<Node> stack = new ArrayDeque<>();
        private int elementCount;
        private boolean repairing;
        private boolean recordLoc;
        private boolean threshold;

        public StreamToDOMContext(boolean repairing, boolean recordLoc, boolean threshold) {
            this.repairing = repairing;
            this.recordLoc = recordLoc;
            this.threshold = threshold;
        }

        public void setRecordLoc(boolean recordLoc) {
            this.recordLoc = recordLoc;
        }

        public boolean isRecordLoc() {
            return this.recordLoc;
        }

        public boolean isRepairing() {
            return this.repairing;
        }

        public boolean isThreshold() {
            return this.threshold;
        }

        public int incrementCount() {
            return ++elementCount;
        }

        public int decreaseCount() {
            return --elementCount;
        }

        public int getCount() {
            return elementCount;
        }

        public void pushToStack(Node node) {
            stack.push(node);
        }

        public Node popFromStack() {
            return stack.pop();
        }

        public int getStackSize() {
            return stack.size();
        }

        public boolean isStackEmpty() {
            return stack.isEmpty();
        }
    }

    public static void readDocElements(Document doc, Node parent, XMLStreamReader reader, StreamToDOMContext context)
        throws XMLStreamException {
        int event = reader.getEventType();
        while (reader.hasNext()) {
            switch (event) {
            case XMLStreamConstants.START_ELEMENT: {
                context.incrementCount();
                Element e;
                if (!StringUtils.isEmpty(reader.getPrefix())) {
                    e = doc.createElementNS(reader.getNamespaceURI(),
                                            reader.getPrefix() + ":" + reader.getLocalName());
                } else {
                    e = doc.createElementNS(reader.getNamespaceURI(), reader.getLocalName());
                }
                e = (Element)parent.appendChild(e);
                if (context.isRecordLoc()) {
                    context.setRecordLoc(addLocation(doc, e, reader.getLocation(), context.isRecordLoc()));
                }

                for (int ns = 0; ns < reader.getNamespaceCount(); ns++) {
                    String uri = reader.getNamespaceURI(ns);
                    String prefix = reader.getNamespacePrefix(ns);

                    declare(e, uri, prefix);
                }

                for (int att = 0; att < reader.getAttributeCount(); att++) {
                    String name = reader.getAttributeLocalName(att);
                    String prefix = reader.getAttributePrefix(att);
                    if (prefix != null && prefix.length() > 0) {
                        name = prefix + ":" + name;
                    }

                    Attr attr = doc.createAttributeNS(reader.getAttributeNamespace(att), name);
                    attr.setValue(reader.getAttributeValue(att));
                    e.setAttributeNode(attr);
                }

                if (context.isRepairing() && !isDeclared(e, reader.getNamespaceURI(), reader.getPrefix())) {
                    declare(e, reader.getNamespaceURI(), reader.getPrefix());
                }
                context.pushToStack(parent);
                if (context.isThreshold() && MAX_ELEMENT_DEPTH_VAL != -1
                    && context.getStackSize() >= MAX_ELEMENT_DEPTH_VAL) {
                    throw new DepthExceededStaxException("reach the innerElementLevelThreshold:"
                                               + MAX_ELEMENT_DEPTH_VAL);
                }
                if (context.isThreshold() && MAX_CHILD_ELEMENTS_VAL != -1
                    && context.getCount() >= MAX_CHILD_ELEMENTS_VAL) {
                    throw new DepthExceededStaxException("reach the innerElementCountThreshold:"
                                               + MAX_CHILD_ELEMENTS_VAL);
                }
                parent = e;
                break;
            }
            case XMLStreamConstants.END_ELEMENT:
                if (context.isStackEmpty()) {
                    return;
                }
                parent = context.popFromStack();
                if (parent instanceof Document || parent instanceof DocumentFragment) {
                    return;
                }
                break;
            case XMLStreamConstants.NAMESPACE:
                break;
            case XMLStreamConstants.ATTRIBUTE:
                break;
            case XMLStreamConstants.CHARACTERS:
                if (parent != null) {
                    context.setRecordLoc(addLocation(doc,
                                                     parent.appendChild(doc.createTextNode(reader.getText())),
                                                     reader.getLocation(), context.isRecordLoc()));
                }
                break;
            case XMLStreamConstants.COMMENT:
                if (parent != null) {
                    parent.appendChild(doc.createComment(reader.getText()));
                }
                break;
            case XMLStreamConstants.CDATA:
                context.setRecordLoc(addLocation(doc,
                                        parent.appendChild(doc.createCDATASection(reader.getText())),
                                        reader.getLocation(), context.isRecordLoc()));
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));
                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                parent.appendChild(doc.createProcessingInstruction(reader.getPITarget(), reader.getPIData()));
                break;
            default:
                break;
            }

            if (reader.hasNext()) {
                event = reader.next();
            }
        }
    }

    public static Node readDocElement(Document doc, Node parent, XMLEvent ev, StreamToDOMContext context)
        throws XMLStreamException {
        switch (ev.getEventType()) {
        case XMLStreamConstants.START_ELEMENT: {
            context.incrementCount();
            Element e;
            StartElement startElem = ev.asStartElement();
            QName name = startElem.getName();
            if (!StringUtils.isEmpty(name.getPrefix())) {
                e = doc.createElementNS(name.getNamespaceURI(),
                                        name.getPrefix() + ":" + name.getLocalPart());
            } else {
                e = doc.createElementNS(name.getNamespaceURI(), name.getLocalPart());
            }
            e = (Element)parent.appendChild(e);
            if (context.isRecordLoc()) {
                context.setRecordLoc(addLocation(doc, e, startElem.getLocation(), context.isRecordLoc()));
            }

            if (context.isRepairing() && !isDeclared(e, name.getNamespaceURI(), name.getPrefix())) {
                declare(e, name.getNamespaceURI(), name.getPrefix());
            }
            context.pushToStack(parent);
            if (context.isThreshold() && MAX_ELEMENT_DEPTH_VAL != -1
                && context.getStackSize() >= MAX_ELEMENT_DEPTH_VAL) {
                throw new DepthExceededStaxException("reach the innerElementLevelThreshold:"
                                           + MAX_ELEMENT_DEPTH_VAL);
            }
            if (context.isThreshold() && MAX_CHILD_ELEMENTS_VAL != -1
                && context.getCount() >= MAX_CHILD_ELEMENTS_VAL) {
                throw new DepthExceededStaxException("reach the innerElementCountThreshold:"
                                           + MAX_CHILD_ELEMENTS_VAL);
            }
            parent = e;
            break;
        }
        case XMLStreamConstants.END_ELEMENT:
            if (context.isStackEmpty()) {
                return parent;
            }
            parent = context.popFromStack();
            if (parent instanceof Document || parent instanceof DocumentFragment) {
                return parent;
            }
            break;
        case XMLStreamConstants.NAMESPACE:
            Namespace ns = (Namespace)ev;
            declare((Element)parent, ns.getNamespaceURI(), ns.getPrefix());
            break;
        case XMLStreamConstants.ATTRIBUTE:
            Attribute at = (Attribute)ev;
            QName qname = at.getName();
            String attName = qname.getLocalPart();
            String attPrefix = qname.getPrefix();
            if (attPrefix != null && attPrefix.length() > 0) {
                attName = attPrefix + ":" + attName;
            }
            Attr attr = doc.createAttributeNS(qname.getNamespaceURI(), attName);
            attr.setValue(at.getValue());
            ((Element)parent).setAttributeNode(attr);
            break;
        case XMLStreamConstants.CHARACTERS:
            Characters characters = ev.asCharacters();
            context.setRecordLoc(addLocation(doc,
                                             parent.appendChild(doc.createTextNode(characters.getData())),
                                             characters.getLocation(), context.isRecordLoc()));
            break;
        case XMLStreamConstants.COMMENT:
            parent.appendChild(doc.createComment(((javax.xml.stream.events.Comment)ev).getText()));
            break;
        case XMLStreamConstants.CDATA:
            Characters cdata = ev.asCharacters();
            context.setRecordLoc(addLocation(doc,
                                             parent.appendChild(doc.createCDATASection(cdata.getData())),
                                             cdata.getLocation(), context.isRecordLoc()));
            break;
        case XMLStreamConstants.PROCESSING_INSTRUCTION:
            parent.appendChild(doc.createProcessingInstruction(((ProcessingInstruction)ev).getTarget(),
                                                               ((ProcessingInstruction)ev).getData()));
            break;
        case XMLStreamConstants.ENTITY_REFERENCE:
            javax.xml.stream.events.EntityReference er = (javax.xml.stream.events.EntityReference)ev;
            parent.appendChild(doc.createEntityReference(er.getName()));
            break;
        default:
            break;
        }
        return parent;
    }

    private static boolean addLocation(Document doc, Node node,
                                       Location loc,
                                       boolean recordLoc) {
        if (recordLoc && loc != null && (loc.getColumnNumber() != 0 || loc.getLineNumber() != 0)) {
            try {
                final int charOffset = loc.getCharacterOffset();
                final int colNum = loc.getColumnNumber();
                final int linNum = loc.getLineNumber();
                final String pubId = loc.getPublicId() == null ? doc.getDocumentURI() : loc.getPublicId();
                final String sysId = loc.getSystemId() == null ? doc.getDocumentURI() : loc.getSystemId();
                Location loc2 = new Location() {
                    public int getCharacterOffset() {
                        return charOffset;
                    }
                    public int getColumnNumber() {
                        return colNum;
                    }
                    public int getLineNumber() {
                        return linNum;
                    }
                    public String getPublicId() {
                        return pubId;
                    }
                    public String getSystemId() {
                        return sysId;
                    }
                };
                node.setUserData("location", loc2, LocationUserDataHandler.INSTANCE);
            } catch (Throwable ex) {
                //possibly not DOM level 3, won't be able to record this then
                return false;
            }
        }
        return recordLoc;
    }

    private static boolean addLocation(Document doc, Node node,
                                    XMLStreamReader reader,
                                    boolean recordLoc) {
        return addLocation(doc, node, reader.getLocation(), recordLoc);
    }

    private static final class LocationUserDataHandler implements UserDataHandler {
        public static final LocationUserDataHandler INSTANCE = new LocationUserDataHandler();

        public void handle(short operation, String key, Object data, Node src, Node dst) {
            if (operation == NODE_CLONED) {
                dst.setUserData(key, data, this);
            }
        }
    }

    private static void declare(Element node, String uri, String prefix) {
        String qualname;
        if (prefix != null && prefix.length() > 0) {
            qualname = "xmlns:" + prefix;
        } else {
            qualname = "xmlns";
        }
        Attr attr = node.getOwnerDocument().createAttributeNS(XML_NS, qualname);
        attr.setValue(uri);
        node.setAttributeNodeNS(attr);
    }
    public static XMLStreamReader createXMLStreamReader(InputSource src) {
        String sysId = src.getSystemId() == null ? null : src.getSystemId();
        String pubId = src.getPublicId() == null ? null : src.getPublicId();
        if (src.getByteStream() != null) {
            final InputStream is = src.getByteStream();

            if (src.getEncoding() == null) {
                final StreamSource ss = new StreamSource(is, sysId);
                ss.setPublicId(pubId);
                
                final XMLStreamReader xmlStreamReader = createXMLStreamReader(ss);
                if (AUTO_CLOSE_INPUT_SOURCE) {
                    return new AutoCloseableXMLStreamReader(xmlStreamReader, is);
                } else {
                    return xmlStreamReader;
                }
            }
            
            return new AutoCloseableXMLStreamReader(createXMLStreamReader(is, src.getEncoding()), is);
        } else if (src.getCharacterStream() != null) {
            final Reader reader = src.getCharacterStream();
            final StreamSource ss = new StreamSource(reader, sysId);
            ss.setPublicId(pubId);
            final XMLStreamReader xmlStreamReader = createXMLStreamReader(ss);
            if (AUTO_CLOSE_INPUT_SOURCE) {
                return new AutoCloseableXMLStreamReader(xmlStreamReader, reader);
            } else {
                return xmlStreamReader;
            }
        } else {
            try {
                final URL url = new URL(sysId);
                final InputStream is = url.openStream();
                final StreamSource ss = new StreamSource(is, sysId);
                ss.setPublicId(pubId);
                return new AutoCloseableXMLStreamReader(createXMLStreamReader(ss), is);
            } catch (Exception ex) {
                //ignore - not a valid URL
            }
        }
        throw new IllegalArgumentException("InputSource must have a ByteStream or CharacterStream");
    }
    /**
     * @param in
     * @param encoding
     */
    public static XMLStreamReader createXMLStreamReader(InputStream in, String encoding) {
        XMLInputFactory factory = getXMLInputFactory();
        try {
            return factory.createXMLStreamReader(in, encoding != null ? encoding : StandardCharsets.UTF_8.name());
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        } finally {
            returnXMLInputFactory(factory);
        }
    }

    /**
     * @param in
     */
    public static XMLStreamReader createXMLStreamReader(InputStream in) {
        XMLInputFactory factory = getXMLInputFactory();
        try {
            return factory.createXMLStreamReader(in);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        } finally {
            returnXMLInputFactory(factory);
        }
    }
    public static XMLStreamReader createXMLStreamReader(String systemId, InputStream in) {
        XMLInputFactory factory = getXMLInputFactory();
        try {
            return factory.createXMLStreamReader(systemId, in);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        } finally {
            returnXMLInputFactory(factory);
        }
    }

    public static XMLStreamReader createXMLStreamReader(Element el) {
        return new W3CDOMStreamReader(el);
    }
    public static XMLStreamReader createXMLStreamReader(Document doc) {
        return new W3CDOMStreamReader(doc.getDocumentElement());
    }
    public static XMLStreamReader createXMLStreamReader(Element el, String sysId) {
        return new W3CDOMStreamReader(el, sysId);
    }
    public static XMLStreamReader createXMLStreamReader(Document doc, String sysId) {
        return new W3CDOMStreamReader(doc.getDocumentElement(), sysId);
    }

    public static XMLStreamReader createXMLStreamReader(Source source) {
        try {
            if (source instanceof DOMSource) {
                DOMSource ds = (DOMSource)source;
                Node nd = ds.getNode();
                Element el = null;
                if (nd instanceof Document) {
                    el = ((Document)nd).getDocumentElement();
                } else if (nd instanceof Element) {
                    el = (Element)nd;
                }

                if (null != el) {
                    return new W3CDOMStreamReader(el, source.getSystemId());
                }
            } else if (source instanceof StAXSource) {
                return ((StAXSource)source).getXMLStreamReader();
            } else if (source instanceof StaxSource) {
                return ((StaxSource)source).getXMLStreamReader();
            } else if (source instanceof SAXSource) {
                SAXSource ss = (SAXSource)source;
                if (ss.getXMLReader() == null) {
                    return createXMLStreamReader(((SAXSource)source).getInputSource());
                }
            }

            XMLInputFactory factory = getXMLInputFactory();
            try {
                XMLStreamReader reader = null;

                try {
                    reader = factory.createXMLStreamReader(source);
                } catch (UnsupportedOperationException e) {
                    //ignore
                }
                if (reader == null && source instanceof StreamSource) {
                    //createXMLStreamReader from Source is optional, we'll try and map it
                    StreamSource ss = (StreamSource)source;
                    if (ss.getInputStream() != null) {
                        reader = factory.createXMLStreamReader(ss.getSystemId(),
                                                               ss.getInputStream());
                    } else {
                        reader = factory.createXMLStreamReader(ss.getSystemId(),
                                                               ss.getReader());
                    }
                }
                return reader;
            } finally {
                returnXMLInputFactory(factory);
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        }
    }

    /**
     * @param reader
     */
    public static XMLStreamReader createXMLStreamReader(Reader reader) {
        XMLInputFactory factory = getXMLInputFactory();
        try {
            return factory.createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse stream.", e);
        } finally {
            returnXMLInputFactory(factory);
        }
    }

    /**
     * Reads a QName from the element text. Reader must be positioned at the
     * start tag.
     */
    public static QName readQName(XMLStreamReader reader) throws XMLStreamException {
        String value = reader.getElementText();
        if (value == null) {
            return null;
        }
        value = value.trim();

        int index = value.indexOf(':');

        if (index == -1) {
            return new QName(value);
        }

        String prefix = value.substring(0, index);
        String localName = value.substring(index + 1);
        String ns = reader.getNamespaceURI(prefix);

        if (!StringUtils.isEmpty(prefix) && ns == null) {
            throw new RuntimeException("Invalid QName in mapping: " + value);
        }

        if (ns == null) {
            return new QName(localName);
        }

        return new QName(ns, localName, prefix);
    }

    /**
     * Create a unique namespace uri/prefix combination.
     *
     * @return The namespace with the specified URI. If one doesn't exist, one
     *         is created.
     * @throws XMLStreamException
     */
    public static String getUniquePrefix(XMLStreamWriter writer, String namespaceURI, boolean declare)
        throws XMLStreamException {
        String prefix = writer.getPrefix(namespaceURI);
        if (prefix == null) {
            prefix = getUniquePrefix(writer);

            if (declare) {
                writer.setPrefix(prefix, namespaceURI);
                writer.writeNamespace(prefix, namespaceURI);
            }
        }
        return prefix;
    }
    public static String getUniquePrefix(XMLStreamWriter writer, String namespaceURI)
        throws XMLStreamException {
        return getUniquePrefix(writer, namespaceURI, false);
    }
    public static String getUniquePrefix(XMLStreamWriter writer) {
        NamespaceContext nc = writer.getNamespaceContext();
        if (nc == null) {
            return DEF_PREFIXES[0];
        }
        for (String t : DEF_PREFIXES) {
            String uri = nc.getNamespaceURI(t);
            if (StringUtils.isEmpty(uri)) {
                return t;
            }
        }

        int n = 10;
        while (true) {
            String nsPrefix = "ns" + n;
            String uri = nc.getNamespaceURI(nsPrefix);
            if (StringUtils.isEmpty(uri)) {
                return nsPrefix;
            }
            n++;
        }
    }


    public static void printXmlFragment(XMLStreamReader reader) {
        try {
            StringWriter sw = new StringWriter(1024);
            XMLStreamWriter writer = null;
            try {
                writer = new PrettyPrintXMLStreamWriter(createXMLStreamWriter(sw), 4);
                copy(reader, writer);
                writer.flush();
            } finally {
                StaxUtils.close(writer);
            }
            LOG.info(sw.toString());
        } catch (XMLStreamException e) {
            LOG.severe(e.getMessage());
        }
    }


    private static void writeStartElementEvent(XMLEvent event, XMLStreamWriter writer)
        throws XMLStreamException {
        StartElement start = event.asStartElement();
        QName name = start.getName();
        String nsURI = name.getNamespaceURI();
        String localName = name.getLocalPart();
        String prefix = name.getPrefix();

        if (prefix != null) {
            writer.writeStartElement(prefix, localName, nsURI);
        } else if (nsURI != null) {
            writer.writeStartElement(localName, nsURI);
        } else {
            writer.writeStartElement(localName);
        }
        Iterator<XMLEvent> it = CastUtils.cast(start.getNamespaces());
        while (it != null && it.hasNext()) {
            writeEvent(it.next(), writer);
        }

        it = CastUtils.cast(start.getAttributes());
        while (it != null && it.hasNext()) {
            writeAttributeEvent(it.next(), writer);
        }
    }
    private static void writeAttributeEvent(XMLEvent event, XMLStreamWriter writer)
        throws XMLStreamException {

        Attribute attr = (Attribute)event;
        QName name = attr.getName();
        String nsURI = name.getNamespaceURI();
        String localName = name.getLocalPart();
        String prefix = name.getPrefix();
        String value = attr.getValue();

        if (prefix != null) {
            writer.writeAttribute(prefix, nsURI, localName, value);
        } else if (nsURI != null) {
            writer.writeAttribute(nsURI, localName, value);
        } else {
            writer.writeAttribute(localName, value);
        }
    }

    public static void writeEvent(XMLEvent event, XMLStreamWriter writer)
        throws XMLStreamException {

        switch (event.getEventType()) {
        case XMLStreamConstants.START_ELEMENT:
            writeStartElementEvent(event, writer);
            break;
        case XMLStreamConstants.END_ELEMENT:
            writer.writeEndElement();
            break;
        case XMLStreamConstants.ATTRIBUTE:
            writeAttributeEvent(event, writer);
            break;
        case XMLStreamConstants.ENTITY_REFERENCE:
            writer.writeEntityRef(((javax.xml.stream.events.EntityReference)event).getName());
            break;
        case XMLStreamConstants.DTD:
            writer.writeDTD(((DTD)event).getDocumentTypeDeclaration());
            break;
        case XMLStreamConstants.PROCESSING_INSTRUCTION:
            if (((javax.xml.stream.events.ProcessingInstruction)event).getData() != null) {
                writer.writeProcessingInstruction(
                    ((javax.xml.stream.events.ProcessingInstruction)event).getTarget(),
                    ((javax.xml.stream.events.ProcessingInstruction)event).getData());
            } else {
                writer.writeProcessingInstruction(
                    ((javax.xml.stream.events.ProcessingInstruction)event).getTarget());
            }
            break;
        case XMLStreamConstants.NAMESPACE:
            if (((Namespace)event).isDefaultNamespaceDeclaration()) {
                writer.writeDefaultNamespace(((Namespace)event).getNamespaceURI());
                writer.setDefaultNamespace(((Namespace)event).getNamespaceURI());
            } else {
                writer.writeNamespace(((Namespace)event).getPrefix(),
                                      ((Namespace)event).getNamespaceURI());
                writer.setPrefix(((Namespace)event).getPrefix(),
                                 ((Namespace)event).getNamespaceURI());
            }
            break;
        case XMLStreamConstants.COMMENT:
            writer.writeComment(((javax.xml.stream.events.Comment)event).getText());
            break;
        case XMLStreamConstants.CHARACTERS:
        case XMLStreamConstants.SPACE:
            writer.writeCharacters(event.asCharacters().getData());
            break;
        case XMLStreamConstants.CDATA:
            writer.writeCData(event.asCharacters().getData());
            break;
        case XMLStreamConstants.START_DOCUMENT:
            if (((StartDocument)event).encodingSet()) {
                writer.writeStartDocument(((StartDocument)event).getCharacterEncodingScheme(),
                                          ((StartDocument)event).getVersion());

            } else {
                writer.writeStartDocument(((StartDocument)event).getVersion());
            }
            break;
        case XMLStreamConstants.END_DOCUMENT:
            writer.writeEndDocument();
            break;
        default:
            //shouldn't get here
        }
    }
    public static void print(Node node) {
        XMLStreamWriter writer = null;
        try {
            writer = createXMLStreamWriter(System.out);
            copy(new DOMSource(node), writer);
            writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            StaxUtils.close(writer);
        }
    }

    public static String toString(Source src) {
        StringWriter sw = new StringWriter(1024);
        XMLStreamWriter writer = null;
        try {
            writer = createXMLStreamWriter(sw);
            copy(src, writer);
            writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            StaxUtils.close(writer);
        }
        return sw.toString();
    }
    public static String toString(Node src) {
        return toString(new DOMSource(src));
    }
    public static String toString(Document doc) {
        StringWriter sw = new StringWriter(1024);
        XMLStreamWriter writer = null;
        try {
            writer = createXMLStreamWriter(sw);
            copy(doc, writer);
            writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            StaxUtils.close(writer);
        }
        return sw.toString();
    }
    public static String toString(Element el) {
        return toString(el, 0);
    }
    public static String toString(Element el, int indent) {
        StringWriter sw = new StringWriter(1024);
        XMLStreamWriter writer = null;
        try {
            writer = createXMLStreamWriter(sw);
            if (indent > 0) {
                writer = new PrettyPrintXMLStreamWriter(writer, indent);
            }
            copy(el, writer);
            writer.flush();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            StaxUtils.close(writer);
        }
        return sw.toString();
    }
    public static void close(XMLStreamReader reader) throws XMLStreamException {
        if (reader != null) {
            reader.close();
        }
    }

    public static void close(XMLStreamWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    public static boolean isSecureReader(XMLStreamReader reader, Message message) {
        if (reader instanceof DocumentDepthProperties) {
            return true;
        }
        try {
            if (reader.getProperty(P_MAX_CHILDREN_PER_ELEMENT) != null) {
                return true;
            }
        } catch (Exception ex) {
            //ignore
        }
        return false;
    }

    public static XMLStreamReader configureReader(XMLStreamReader xreader, Message message) throws XMLStreamException {
        Integer messageMaxChildElements = PropertyUtils.getInteger(message, MAX_CHILD_ELEMENTS);
        Integer messageMaxElementDepth = PropertyUtils.getInteger(message, MAX_ELEMENT_DEPTH);
        Integer messageMaxAttributeCount = PropertyUtils.getInteger(message, MAX_ATTRIBUTE_COUNT);
        Integer messageMaxAttributeSize = PropertyUtils.getInteger(message, MAX_ATTRIBUTE_SIZE);
        Integer messageMaxTextLength = PropertyUtils.getInteger(message, MAX_TEXT_LENGTH);
        Long messageMaxElementCount = PropertyUtils.getLong(message, MAX_ELEMENT_COUNT);
        Long messageMaxXMLCharacters = PropertyUtils.getLong(message, MAX_XML_CHARACTERS);
        return configureReader(xreader, messageMaxChildElements, messageMaxElementDepth,
                               messageMaxAttributeCount, messageMaxAttributeSize, messageMaxTextLength,
                               messageMaxElementCount, messageMaxXMLCharacters);
    }

    //CHECKSTYLE:OFF - lots of params to configure
    public static XMLStreamReader configureReader(XMLStreamReader reader, Integer maxChildElements,
                                       Integer maxElementDepth, Integer maxAttributeCount,
                                       Integer maxAttributeSize, Integer maxTextLength,
                                       Long maxElementCount, Long maxXMLCharacters)
        throws XMLStreamException {
        //CHECKSTYLE:ON

        // We currently ONLY support Woodstox 4.2.x for most of this other than a few things
        // that we can handle via a wrapper.
        try {
            DocumentDepthProperties p = null;
            if (maxChildElements != null) {
                try {
                    setProperty(reader, P_MAX_CHILDREN_PER_ELEMENT, maxChildElements);
                } catch (Throwable t) {
                    //we can handle this via a wrapper
                    p = new DocumentDepthProperties();
                    p.setInnerElementCountThreshold(maxChildElements);
                }
            }
            if (maxElementDepth != null) {
                try {
                    setProperty(reader, P_MAX_ELEMENT_DEPTH, maxElementDepth);
                } catch (Throwable t) {
                    //we can handle this via a wrapper
                    if (p == null) {
                        p = new DocumentDepthProperties();
                    }
                    p.setInnerElementLevelThreshold(maxElementDepth);
                }
            }
            if (maxAttributeCount != null) {
                setProperty(reader, P_MAX_ATTRIBUTES_PER_ELEMENT, maxAttributeCount);
            }
            if (maxAttributeSize != null) {
                setProperty(reader, P_MAX_ATTRIBUTE_SIZE, maxAttributeSize);
            }
            if (maxTextLength != null) {
                setProperty(reader, P_MAX_TEXT_LENGTH, maxTextLength);
            }
            if (maxElementCount != null) {
                try {
                    setProperty(reader, P_MAX_ELEMENT_COUNT, maxElementCount);
                } catch (Throwable t) {
                    //we can handle this via a wrapper
                    if (p == null) {
                        p = new DocumentDepthProperties();
                    }
                    p.setElementCountThreshold(maxElementCount.intValue());
                }
            }
            if (maxXMLCharacters != null) {
                setProperty(reader, P_MAX_CHARACTERS, maxXMLCharacters);
            }
            if (p != null) {
                reader = new DepthRestrictingStreamReader(reader, p);
            }
        } catch (ClassCastException cce) {
            //not an XMLStreamReader2
            if (ALLOW_INSECURE_PARSER_VAL) {
                LOG.warning("INSTANCE_NOT_XMLSTREAMREADER2");
            } else {
                throw new XMLStreamException(cce.getMessage(), cce);
            }
        } catch (IllegalArgumentException cce) {
            //not a property supported by this version of woodstox
            if (ALLOW_INSECURE_PARSER_VAL) {
                LOG.log(Level.WARNING, "SECURE_PROPERTY_NOT_SUPPORTED", cce.getMessage());
            } else {
                throw new XMLStreamException(cce.getMessage(), cce);
            }
        }
        return reader;
    }
    private static void setProperty(XMLStreamReader reader, String p, Object v) {
        WoodstoxHelper.setProperty(reader, p, v);
    }

    
}
/* @generated */

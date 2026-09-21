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

package org.apache.cxf.wsdl11;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.wsdl.BindingInput;
import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.common.util.CacheMap;
import org.apache.cxf.configuration.ConfiguredBeanLocator;
import org.apache.cxf.service.model.ServiceSchemaInfo;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.staxutils.XMLStreamReaderWrapper;
import org.apache.cxf.wsdl.WSDLConstants;
import org.apache.cxf.wsdl.WSDLExtensionLoader;
import org.apache.cxf.wsdl.WSDLManager;

/**
 * WSDLManagerImpl
 */
@NoJSR250Annotations(unlessNull = "bus")
public class WSDLManagerImpl implements WSDLManager {

    final ExtensionRegistry registry;
    final WSDLFactory factory;
    final Map<Object, Definition> definitionsMap;

    /**
     * The schemaCacheMap is used as a cache of SchemaInfo against the WSDLDefinitions.
     * The key is the same key that is used to hold the definition object into the definitionsMap
     */
    final Map<Object, ServiceSchemaInfo> schemaCacheMap;
    private boolean disableSchemaCache;

    private Bus bus;

    private XMLStreamReaderWrapper xmlStreamReaderWrapper;

    public WSDLManagerImpl() throws BusException {
        this(null);
    }
    private WSDLManagerImpl(Bus b) throws BusException {
        try {
            // This is needed to avoid security exceptions when running with a security manager
            if (System.getSecurityManager() == null) {
                factory = WSDLFactory.newInstance();
            } else {
                try {
                    factory = AccessController.doPrivileged(
                            (PrivilegedExceptionAction<WSDLFactory>) WSDLFactory::newInstance);
                } catch (PrivilegedActionException paex) {
                    throw new BusException(paex);
                }
            }
            registry = factory.newPopulatedExtensionRegistry();
            registry.registerSerializer(Types.class,
                                        WSDLConstants.QNAME_SCHEMA,
                                        new SchemaSerializer());
            // these will replace whatever may have already been registered
            // in these places, but there's no good way to check what was
            // there before.
            QName header = new QName(WSDLConstants.NS_SOAP, "header");
            registry.registerDeserializer(MIMEPart.class,
                                          header,
                                          registry.queryDeserializer(BindingInput.class, header));
            registry.registerSerializer(MIMEPart.class,
                                        header,
                                        registry.querySerializer(BindingInput.class, header));
            // get the original classname of the SOAPHeader
            // implementation that was stored in the registry.
            Class<? extends ExtensibilityElement> clazz =
                registry.createExtension(BindingInput.class, header).getClass();
            registry.mapExtensionTypes(MIMEPart.class, header, clazz);
            // register some known extension attribute types that are not recognized by the default registry
            addExtensionAttributeTypes(registry);
        } catch (WSDLException e) {
            throw new BusException(e);
        }
        definitionsMap = new CacheMap<>();
        schemaCacheMap = new CacheMap<>();

        setBus(b);
    }

    @Resource
    public final void setBus(Bus b) {
        bus = b;
        if (null != bus) {
            bus.setExtension(this, WSDLManager.class);
            ConfiguredBeanLocator loc = bus.getExtension(ConfiguredBeanLocator.class);
            if (loc != null) {
                loc.getBeansOfType(WSDLExtensionLoader.class);
            }
        }
    }

    public WSDLFactory getWSDLFactory() {
        return factory;
    }

    public Map<Object, Definition> getDefinitions() {
        synchronized (definitionsMap) {
            return Collections.unmodifiableMap(definitionsMap);
        }
    }

    protected Bus getBus() {
        return bus;
    }

    public ExtensionRegistry getExtensionRegistry() {
        return registry;
    }

    public Definition getDefinition(String url) throws WSDLException {
        synchronized (definitionsMap) {
            if (definitionsMap.containsKey(url)) {
                return definitionsMap.get(url);
            }
        }
        Definition def = loadDefinition(url);
        synchronized (definitionsMap) {
            definitionsMap.put(url, def);
        }
        return def;
    }

    public Definition getDefinition(final Element el) throws WSDLException {
        synchronized (definitionsMap) {
            if (definitionsMap.containsKey(el)) {
                return definitionsMap.get(el);
            }
        }
        final WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setExtensionRegistry(registry);

        final Definition def;

        // This is needed to avoid security exceptions when running with a security manager
        if (System.getSecurityManager() == null) {
            def = reader.readWSDL("", el);
        } else {
            try {
                def = AccessController.doPrivileged(
                        (PrivilegedExceptionAction<Definition>) () -> reader.readWSDL("", el));
            } catch (PrivilegedActionException paex) {
                throw new WSDLException(WSDLException.PARSER_ERROR, paex.getMessage(), paex);
            }
        }

        synchronized (definitionsMap) {
            definitionsMap.put(el, def);
        }
        return def;
    }


    public void addDefinition(Object key, Definition wsdl) {
        synchronized (definitionsMap) {
            definitionsMap.put(key, wsdl);
        }
    }

    protected Definition loadDefinition(String url) throws WSDLException {
        final WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setFeature("javax.wsdl.importDocuments", true);
        reader.setExtensionRegistry(registry);

        //we'll create a new String here to make sure the passed in key is not referenced in the loading of
        //the wsdl and thus would be held onto from the cached map from both the weak reference (key) and
        //from the strong reference (Definition).  For example, the Definition sometimes keeps the original
        //string as the documentBaseLocation which would result in it being held onto strongly
        //from the definition.  With this, the String the definition holds onto would be unique
        url = new String(url);
        CatalogWSDLLocator catLocator = new CatalogWSDLLocator(url, bus);
        final ResourceManagerWSDLLocator wsdlLocator = new ResourceManagerWSDLLocator(url,
                                                                                catLocator,
                                                                                bus);
        InputSource src = wsdlLocator.getBaseInputSource();
        final Definition def;
        if (src.getByteStream() != null || src.getCharacterStream() != null) {
            final Document doc;
            XMLStreamReader xmlReader = null;
            try {
                xmlReader = StaxUtils.createXMLStreamReader(src);
                if (xmlStreamReaderWrapper != null) {
                    xmlReader = xmlStreamReaderWrapper.wrap(xmlReader);
                }
                doc = StaxUtils.read(xmlReader, true);
                if (src.getSystemId() != null) {
                    try {
                        doc.setDocumentURI(new String(src.getSystemId()));
                    } catch (Exception e) {
                        //ignore - probably not DOM level 3
                    }
                }
            } catch (Exception e) {
                throw new WSDLException(WSDLException.PARSER_ERROR, e.getMessage(), e);
            } finally {
                try {
                    StaxUtils.close(xmlReader);
                } catch (XMLStreamException ex) {
                    throw new WSDLException(WSDLException.PARSER_ERROR, ex.getMessage(), ex);
                }
            }

            // This is needed to avoid security exceptions when running with a security manager
            if (System.getSecurityManager() == null) {
                def = reader.readWSDL(wsdlLocator, doc.getDocumentElement());
            } else {
                try {
                    def = AccessController.doPrivileged((PrivilegedExceptionAction<Definition>) () ->
                                    reader.readWSDL(wsdlLocator, doc.getDocumentElement()));
                } catch (PrivilegedActionException paex) {
                    throw new WSDLException(WSDLException.PARSER_ERROR, paex.getMessage(), paex);
                }
            }
        } else {
            if (System.getSecurityManager() == null) {
                def = reader.readWSDL(wsdlLocator);
            } else {
                try {
                    def = AccessController.doPrivileged((PrivilegedExceptionAction<Definition>) () ->
                                    reader.readWSDL(wsdlLocator));
                } catch (PrivilegedActionException paex) {
                    throw new WSDLException(WSDLException.PARSER_ERROR, paex.getMessage(), paex);
                }
            }
        }

        return def;
    }

    public void setXMLStreamReaderWrapper(XMLStreamReaderWrapper wrapper) {
        this.xmlStreamReaderWrapper = wrapper;
    }

    private void addExtensionAttributeTypes(ExtensionRegistry extreg) {
        // register types that are not of wsdl4j's default attribute type QName
        QName qn = new QName("http://www.w3.org/2006/05/addressing/wsdl", "Action");
        extreg.registerExtensionAttributeType(javax.wsdl.Input.class, qn, AttributeExtensible.STRING_TYPE);
        extreg.registerExtensionAttributeType(javax.wsdl.Output.class, qn, AttributeExtensible.STRING_TYPE);
        extreg.registerExtensionAttributeType(javax.wsdl.Fault.class, qn, AttributeExtensible.STRING_TYPE);
        qn = new QName("http://www.w3.org/2007/05/addressing/metadata", "Action");
        extreg.registerExtensionAttributeType(javax.wsdl.Input.class, qn, AttributeExtensible.STRING_TYPE);
        extreg.registerExtensionAttributeType(javax.wsdl.Output.class, qn, AttributeExtensible.STRING_TYPE);
        extreg.registerExtensionAttributeType(javax.wsdl.Fault.class, qn, AttributeExtensible.STRING_TYPE);
        qn = new QName("http://www.w3.org/2005/02/addressing/wsdl", "Action");
        extreg.registerExtensionAttributeType(javax.wsdl.Input.class, qn, AttributeExtensible.STRING_TYPE);
        extreg.registerExtensionAttributeType(javax.wsdl.Output.class, qn, AttributeExtensible.STRING_TYPE);
        extreg.registerExtensionAttributeType(javax.wsdl.Fault.class, qn, AttributeExtensible.STRING_TYPE);
    }

    public ServiceSchemaInfo getSchemasForDefinition(Definition wsdl) {
        if (disableSchemaCache) {
            return null;
        }
        synchronized (definitionsMap) {
            for (Map.Entry<Object, Definition> e : definitionsMap.entrySet()) {
                if (e.getValue() == wsdl) {
                    ServiceSchemaInfo info = schemaCacheMap.get(e.getKey());
                    if (info != null) {
                        return info;
                    }
                }
            }
        }
        return null;
    }

    public void putSchemasForDefinition(Definition wsdl, ServiceSchemaInfo schemas) {
        if (!disableSchemaCache) {
            synchronized (definitionsMap) {
                for (Map.Entry<Object, Definition> e : definitionsMap.entrySet()) {
                    if (e.getValue() == wsdl) {
                        schemaCacheMap.put(e.getKey(), schemas);
                    }
                }
            }
        }
    }

    public boolean isDisableSchemaCache() {
        return disableSchemaCache;
    }

    /**
     * There's a test that 'fails' by succeeding if the cache is operational.
     * @param disableSchemaCache
     */
    public void setDisableSchemaCache(boolean disableSchemaCache) {
        this.disableSchemaCache = disableSchemaCache;
    }

    @Override
    public void removeDefinition(Definition wsdl) {
        synchronized (definitionsMap) {
            List<Object> keys = new ArrayList<>();
            for (Map.Entry<Object, Definition> e : definitionsMap.entrySet()) {
                if (e.getValue() == wsdl) {
                    keys.add(e.getKey());
                }
            }
            for (Object o : keys) {
                definitionsMap.remove(o);
                schemaCacheMap.remove(o);
            }
        }
    }

    @Override
    public void removeDefinition(String url) {
        synchronized (definitionsMap) {
            Definition wsdl = definitionsMap.get(url);
            if (wsdl != null) {
                List<Object> keys = new ArrayList<>();
                for (Map.Entry<Object, Definition> e : definitionsMap.entrySet()) {
                    if (e.getValue() == wsdl) {
                        keys.add(e.getKey());
                    }
                }
                for (Object o : keys) {
                    definitionsMap.remove(o);
                    schemaCacheMap.remove(o);
                }
            }
        }
    }

}

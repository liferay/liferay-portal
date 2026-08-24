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

package org.apache.cxf.ws.addressing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import org.xml.sax.InputSource;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.jaxb.JAXBContextCache;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.xmlschema.LSInputImpl;
import org.apache.cxf.endpoint.EndpointResolverRegistry;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.helpers.LoadingByteArrayOutputStream;
import org.apache.cxf.resource.ExtendedURIResolver;
import org.apache.cxf.resource.ResourceManager;
import org.apache.cxf.service.model.SchemaInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.staxutils.W3CDOMStreamWriter;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.MultiplexDestination;
import org.apache.cxf.ws.addressing.wsdl.AttributedQNameType;
import org.apache.cxf.ws.addressing.wsdl.ServiceNameType;
import org.apache.ws.commons.schema.XmlSchema;

/**
 * Provides utility methods for obtaining endpoint references, wsdl definitions, etc.
 */
public final class EndpointReferenceUtils {

    /**
     * We want to load the schemas, including references to external schemas, into a SchemaFactory
     * to validate. There seem to be bugs in resolving inter-schema references in Xerces, so even when we are
     * handing the factory all the schemas, interrelated with &lt;import&gt; elements, we need
     * to also hand over extra copies (!) as character images when requested.
     *
     * To do this, we use the DOM representation kept in the SchemaInfo. This has the bonus
     * of benefiting from the use of the catalog resolver in there, which is missing from
     * the code in here.
     */
    private static final class SchemaLSResourceResolver implements LSResourceResolver {
        private final Map<String, byte[]> schemas;
        private final Set<String> done = new HashSet<>();
        private final ExtendedURIResolver resolver = new ExtendedURIResolver();
        private final Bus bus;

        private SchemaLSResourceResolver(Map<String, byte[]> schemas, Bus b) {
            this.schemas = schemas;
            this.bus = b;
        }

        public LSInput resolveResource(String type, String namespaceURI, String publicId,
                                       String systemId, String baseURI) {

            String newId = systemId;
            if (baseURI != null && systemId != null) {  //add additional systemId null check
                try {
                    URI uri = new URI(baseURI);
                    uri = uri.resolve(systemId);
                    newId = uri.toString();
                    if (newId.equals(systemId)) {
                        URL url = new URL(baseURI);
                        url = new URL(url, systemId);
                        newId = url.toExternalForm();
                    }
                } catch (IllegalArgumentException e) {
                    //ignore - systemId not a valid URI
                } catch (URISyntaxException e) {
                    //ignore - baseURI not a valid URI
                } catch (MalformedURLException e) {
                    //ignore - baseURI or systemId not a URL either
                }
            }
            LSInputImpl impl = null;
            if (done.contains(newId + ":" + namespaceURI)) {
                return null;
            }

            if (schemas.containsKey(newId + ":" + namespaceURI)) {
                byte[] ds = schemas.remove(newId + ":" + namespaceURI);
                impl = createInput(newId, ds);
                done.add(newId + ":" + namespaceURI);
            }
            if (impl == null && schemas.containsKey(newId + ":null")) {
                byte[] ds = schemas.get(newId + ":null");
                impl = createInput(newId, ds);
                done.add(newId + ":" + namespaceURI);
            }
            if (impl == null && bus != null && systemId != null) {
                ResourceManager rm = bus.getExtension(ResourceManager.class);
                URL url = rm == null ? null : rm.resolveResource(systemId, URL.class);
                if (url != null) {
                    newId = url.toString();
                    if (done.contains(newId + ":" + namespaceURI)) {
                        return null;
                    }
                    if (schemas.containsKey(newId + ":" + namespaceURI)) {
                        byte[] ds = schemas.remove(newId + ":" + namespaceURI);
                        impl = createInput(newId, ds);
                        done.add(newId + ":" + namespaceURI);
                    }
                }
            }
            if (impl == null) {
                for (Map.Entry<String, byte[]> ent : schemas.entrySet()) {
                    if (ent.getKey().endsWith(systemId + ":" + namespaceURI)) {
                        schemas.remove(ent.getKey());
                        impl = createInput(newId, ent.getValue());
                        done.add(newId + ":" + namespaceURI);
                        return impl;
                    }
                }
                // there can be multiple includes on the same namespace. This scenario is not envisioned yet.
                // hence the filename part is included as well.
                if (systemId != null) {
                    String systemIdFileName = systemId.substring(systemId.lastIndexOf('/') + 1);
                    for (Map.Entry<String, byte[]> ent : schemas.entrySet()) {
                        if (ent.getKey().endsWith(systemIdFileName + ":" + namespaceURI)) {
                            schemas.remove(ent.getKey());
                            impl = createInput(newId, ent.getValue());
                            done.add(newId + ":" + namespaceURI);
                            return impl;
                        }
                    }
                }
                // handle case where given systemId is null (so that
                // direct key lookup fails) by scanning through map
                // searching for a namespace match
                if (namespaceURI != null) {
                    for (Map.Entry<String, byte[]> ent : schemas.entrySet()) {
                        if (ent.getKey().endsWith(":" + namespaceURI)) {
                            schemas.remove(ent.getKey());
                            impl = createInput(newId, ent.getValue());
                            done.add(newId + ":" + namespaceURI);
                            return impl;
                        }
                    }
                }

                //REVIST - we need to get catalogs in here somehow  :-(
                if (systemId == null) {
                    systemId = publicId;
                }
                if (systemId != null) {
                    InputSource source = resolver.resolve(systemId, baseURI);
                    if (source != null) {
                        impl = new LSInputImpl();
                        impl.setByteStream(source.getByteStream());
                        impl.setSystemId(source.getSystemId());
                        impl.setPublicId(source.getPublicId());
                        return impl;
                    }
                }
                LOG.warning("Could not resolve Schema for " + systemId);
            }
            return impl;
        }
        private LSInputImpl createInput(String newId, byte[] value) {
            LSInputImpl impl = new LSInputImpl();
            impl.setSystemId(newId);
            impl.setBaseURI(newId);
            impl.setByteStream(new ByteArrayInputStream(value));
            return impl;
        }
    }

    public static final String ANONYMOUS_ADDRESS = "http://www.w3.org/2005/08/addressing/anonymous";

    private static final Logger LOG = LogUtils.getL7dLogger(EndpointReferenceUtils.class);

    private static final String NS_WSAW_2005 = "http://www.w3.org/2005/02/addressing/wsdl";
    private static final String WSDL_INSTANCE_NAMESPACE2 =
        "http://www.w3.org/2006/01/wsdl-instance";
    private static final String WSDL_INSTANCE_NAMESPACE =
            "http://www.w3.org/ns/wsdl-instance";

    private static final QName WSA_WSDL_NAMESPACE_NS =
        new QName("xmlns:" + JAXWSAConstants.WSAW_PREFIX);
    private static final String XML_SCHEMA_NAMESPACE =
        "http://www.w3.org/2001/XMLSchema";
    private static final String XML_SCHEMA_NAMESPACE_PREFIX = "xs";
    private static final QName XML_SCHEMA_NAMESPACE_NS =
        new QName("xmlns:" + XML_SCHEMA_NAMESPACE_PREFIX);
    private static final String XML_SCHEMA_INSTANCE_NAMESPACE =
        "http://www.w3.org/2001/XMLSchema-instance";
    private static final QName WSDL_LOCATION2 =
        new QName(WSDL_INSTANCE_NAMESPACE2, "wsdlLocation");
    private static final QName WSDL_LOCATION =
        new QName(WSDL_INSTANCE_NAMESPACE, "wsdlLocation");
    private static final QName XSI_TYPE =
        new QName(XML_SCHEMA_INSTANCE_NAMESPACE, "type", "xsi");

    private static final org.apache.cxf.ws.addressing.wsdl.ObjectFactory WSA_WSDL_OBJECT_FACTORY =
        new org.apache.cxf.ws.addressing.wsdl.ObjectFactory();


    private static final Set<Class<?>> ADDRESSING_CLASSES = new HashSet<>();
    private static final AtomicReference<Reference<JAXBContext>> ADDRESSING_CONTEXT
        = new AtomicReference<>(new SoftReference<JAXBContext>(null));
    static {
        ADDRESSING_CLASSES.add(WSA_WSDL_OBJECT_FACTORY.getClass());
        ADDRESSING_CLASSES.add(org.apache.cxf.ws.addressing.ObjectFactory.class);
    }

    private EndpointReferenceUtils() {
        // Utility class - never constructed
    }

    /**
     * Sets the service and port name of the provided endpoint reference.
     * @param ref the endpoint reference.
     * @param serviceName the name of service.
     * @param portName the port name.
     */
    public static void setServiceAndPortName(EndpointReferenceType ref,
                                             QName serviceName,
                                             String portName) {
        if (null != serviceName) {
            JAXBElement<ServiceNameType> jaxbElement = getServiceNameType(serviceName, portName);
            MetadataType mt = getSetMetadata(ref);

            mt.getAny().add(jaxbElement);
        }
    }


    public static MetadataType getSetMetadata(EndpointReferenceType ref) {
        MetadataType mt = ref.getMetadata();
        if (null == mt) {
            mt = new MetadataType();
            ref.setMetadata(mt);
        }
        return mt;
    }

    public static JAXBElement<ServiceNameType> getServiceNameType(QName serviceName, String portName) {
        ServiceNameType serviceNameType = WSA_WSDL_OBJECT_FACTORY.createServiceNameType();
        serviceNameType.setValue(serviceName);
        serviceNameType.setEndpointName(portName);
        serviceNameType.getOtherAttributes().put(WSA_WSDL_NAMESPACE_NS, JAXWSAConstants.NS_WSAW);
        serviceNameType.getOtherAttributes().put(XSI_TYPE,
                                                 JAXWSAConstants.WSAW_PREFIX + ":"
                                                 + serviceNameType.getClass().getSimpleName());
        return WSA_WSDL_OBJECT_FACTORY.createServiceName(serviceNameType);
    }

    /**
     * Gets the service name of the provided endpoint reference.
     * @param ref the endpoint reference.
     * @return the service name.
     */
    public static QName getServiceName(EndpointReferenceType ref, Bus bus) {
        MetadataType metadata = ref.getMetadata();
        if (metadata == null) {
            return null;
        }
        for (Object obj : metadata.getAny()) {
            if (obj instanceof Element) {
                Node node = (Element)obj;
                if ((node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAW)
                    || node.getNamespaceURI().equals(NS_WSAW_2005)
                    || node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAM))
                    && "ServiceName".equals(node.getLocalName())) {
                    String content = node.getTextContent();
                    String namespaceURI = node.getFirstChild().getNamespaceURI();
                    String service = content;
                    if (content.contains(":")) {
                        namespaceURI = getNameSpaceUri(node, content, namespaceURI);
                        service = getService(content);
                    } else {
                        Node nodeAttr = node.getAttributes().getNamedItem("xmlns");
                        namespaceURI = nodeAttr.getNodeValue();
                    }

                    return new QName(namespaceURI, service);
                }
            } else if (obj instanceof JAXBElement) {
                Object val = ((JAXBElement<?>)obj).getValue();
                if (val instanceof ServiceNameType) {
                    return ((ServiceNameType)val).getValue();
                }
            } else if (obj instanceof ServiceNameType) {
                return ((ServiceNameType)obj).getValue();
            }
        }
        return null;
    }

    /**
     * Gets the port name of the provided endpoint reference.
     * @param ref the endpoint reference.
     * @return the port name.
     */
    public static String getPortName(EndpointReferenceType ref) {
        MetadataType metadata = ref.getMetadata();
        if (metadata != null) {
            for (Object obj : metadata.getAny()) {
                if (obj instanceof Element) {
                    Node node = (Element)obj;
                    if ((node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAW)
                        || node.getNamespaceURI().equals(NS_WSAW_2005)
                        || node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAM))
                        && node.getNodeName().contains("ServiceName")) {
                        Node item = node.getAttributes().getNamedItem("EndpointName");
                        return item != null ? item.getTextContent() : null;
                    }
                } else if (obj instanceof JAXBElement) {
                    Object val = ((JAXBElement<?>)obj).getValue();
                    if (val instanceof ServiceNameType) {
                        return ((ServiceNameType)val).getEndpointName();
                    }
                } else if (obj instanceof ServiceNameType) {
                    return ((ServiceNameType)obj).getEndpointName();
                }
            }
        }
        return null;
    }

    public static QName getPortQName(EndpointReferenceType ref, Bus bus) {
        QName serviceName = getServiceName(ref, bus);
        return new QName(serviceName.getNamespaceURI(), getPortName(ref));
    }

    public static void setPortName(EndpointReferenceType ref, String portName) {
        MetadataType metadata = ref.getMetadata();
        if (metadata != null) {
            for (Object obj : metadata.getAny()) {
                if (obj instanceof Element) {
                    Element node = (Element)obj;
                    if (node.getNodeName().contains("ServiceName")
                        && (node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAW)
                        || node.getNamespaceURI().equals(NS_WSAW_2005)
                        || node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAM))) {
                        node.setAttribute(JAXWSAConstants.WSAM_ENDPOINT_NAME, portName);
                    }
                } else if (obj instanceof JAXBElement) {
                    Object val = ((JAXBElement<?>)obj).getValue();
                    if (val instanceof ServiceNameType) {
                        ((ServiceNameType)val).setEndpointName(portName);
                    }
                } else if (obj instanceof ServiceNameType) {
                    ((ServiceNameType)obj).setEndpointName(portName);
                }
            }
        }
    }

    public static void setInterfaceName(EndpointReferenceType ref, QName portTypeName) {
        if (null != portTypeName) {
            AttributedQNameType interfaceNameType =
                WSA_WSDL_OBJECT_FACTORY.createAttributedQNameType();

            interfaceNameType.setValue(portTypeName);
            interfaceNameType.getOtherAttributes().put(XML_SCHEMA_NAMESPACE_NS,
                                                       XML_SCHEMA_NAMESPACE);
            interfaceNameType.getOtherAttributes().put(XSI_TYPE,
                                                       XML_SCHEMA_NAMESPACE_PREFIX + ":"
                                                       + portTypeName.getClass().getSimpleName());

            JAXBElement<AttributedQNameType> jaxbElement =
                WSA_WSDL_OBJECT_FACTORY.createInterfaceName(interfaceNameType);

            MetadataType mt = getSetMetadata(ref);
            mt.getAny().add(jaxbElement);
        }
    }

    public static QName getInterfaceName(EndpointReferenceType ref, Bus bus) {
        MetadataType metadata = ref.getMetadata();
        if (metadata == null) {
            return null;
        }
        for (Object obj : metadata.getAny()) {
            if (obj instanceof Element) {
                Node node = (Element)obj;
                if ((node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAW)
                    || node.getNamespaceURI().equals(JAXWSAConstants.NS_WSAM))
                    && node.getNodeName().contains("InterfaceName")) {

                    String content = node.getTextContent();
                    String namespaceURI = node.getFirstChild().getNamespaceURI();
                    //String service = content;
                    if (content.contains(":")) {
                        namespaceURI = getNameSpaceUri(node, content, namespaceURI);
                        content = getService(content);
                    } else {
                        Node nodeAttr = node.getAttributes().getNamedItem("xmlns");
                        namespaceURI = nodeAttr.getNodeValue();
                    }

                    return new QName(namespaceURI, content);
                }
            } else if (obj instanceof JAXBElement) {
                Object val = ((JAXBElement<?>)obj).getValue();
                if (val instanceof AttributedQNameType) {
                    return ((AttributedQNameType)val).getValue();
                }
            } else if (obj instanceof AttributedQNameType) {
                return ((AttributedQNameType)obj).getValue();
            }
        }

        return null;
    }

    public static void setWSDLLocation(EndpointReferenceType ref, String... wsdlLocation) {

        MetadataType metadata = getSetMetadata(ref);

        //wsdlLocation attribute is a list of anyURI.
        metadata.getOtherAttributes().put(WSDL_LOCATION, String.join(" ", wsdlLocation).trim());
    }

    public static String getWSDLLocation(EndpointReferenceType ref) {
        String wsdlLocation = null;
        MetadataType metadata = ref.getMetadata();

        if (metadata != null) {
            wsdlLocation = metadata.getOtherAttributes().get(WSDL_LOCATION);
            if (wsdlLocation == null) {
                wsdlLocation = metadata.getOtherAttributes().get(WSDL_LOCATION2);
            }
        }

        if (null == wsdlLocation) {
            return null;
        }
        return wsdlLocation;
    }

    private static Schema createSchema(ServiceInfo serviceInfo, Bus b) {
        Schema schema = serviceInfo.getProperty(Schema.class.getName(), Schema.class);
        if (schema == null) {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Map<String, byte[]> schemaSourcesMap = new LinkedHashMap<>();
            Map<String, Source> schemaSourcesMap2 = new LinkedHashMap<>();

            XMLStreamWriter writer = null;
            try {
                for (SchemaInfo si : serviceInfo.getSchemas()) {
                    Element el = si.getElement();
                    String baseURI = null;
                    try {
                        baseURI = el.getBaseURI();
                    } catch (Exception ex) {
                        //ignore - not DOM level 3
                    }
                    if (baseURI == null) {
                        baseURI = si.getSystemId();
                    }
                    DOMSource ds = new DOMSource(el, baseURI);
                    schemaSourcesMap2.put(si.getSystemId() + ':' + si.getNamespaceURI(), ds);
                    LoadingByteArrayOutputStream out = new LoadingByteArrayOutputStream();
                    writer = StaxUtils.createXMLStreamWriter(out);
                    StaxUtils.copy(el, writer);
                    writer.flush();
                    schemaSourcesMap.put(si.getSystemId() + ':' + si.getNamespaceURI(), out.toByteArray());
                }


                for (XmlSchema sch : serviceInfo.getXmlSchemaCollection().getXmlSchemas()) {
                    if (sch.getSourceURI() != null
                        && !schemaSourcesMap.containsKey(sch.getSourceURI() + ':'
                                                         + sch.getTargetNamespace())) {

                        InputStream ins = null;
                        try {
                            URL url = new URL(sch.getSourceURI());
                            ins = url.openStream();
                        } catch (Exception e) {
                            //ignore, we'll just use what we have.  (though
                            //bugs in XmlSchema could make this less useful)
                        }

                        LoadingByteArrayOutputStream out = new LoadingByteArrayOutputStream();
                        if (ins == null) {
                            sch.write(out);
                        } else {
                            IOUtils.copyAndCloseInput(ins, out);
                        }

                        schemaSourcesMap.put(sch.getSourceURI() + ':'
                                             + sch.getTargetNamespace(), out.toByteArray());

                        Source source = new StreamSource(out.createInputStream(), sch.getSourceURI());
                        schemaSourcesMap2.put(sch.getSourceURI() + ':'
                                              + sch.getTargetNamespace(), source);
                    }
                }

                factory.setResourceResolver(new SchemaLSResourceResolver(schemaSourcesMap,
                        b != null ? b : BusFactory.getThreadDefaultBus(false)));
                schema = factory.newSchema(schemaSourcesMap2.values()
                                           .toArray(new Source[schemaSourcesMap2.size()]));


            } catch (Exception ex) {
                // Something not right with the schema from the wsdl.
                LOG.log(Level.WARNING, "SAXException for newSchema()", ex);
                for (SchemaInfo schemaInfo : serviceInfo.getSchemas()) {
                    String s = StaxUtils.toString(schemaInfo.getElement(), 4);
                    LOG.log(Level.INFO, "Schema for: " + schemaInfo.getNamespaceURI() + "\n" + s);
                }
            } finally {
                StaxUtils.close(writer);
            }
            serviceInfo.setProperty(Schema.class.getName(), schema);
        }
        return schema;
    }

    public static Schema getSchema(ServiceInfo serviceInfo) {
        return getSchema(serviceInfo, null);
    }
    public static Schema getSchema(ServiceInfo serviceInfo, Bus b) {
        if (serviceInfo == null) {
            return null;
        }
        Schema schema = serviceInfo.getProperty(Schema.class.getName(), Schema.class);
        if (schema == null && !serviceInfo.hasProperty(Schema.class.getName() + ".CHECKED")) {
            try {
                synchronized (serviceInfo) {
                    return createSchema(serviceInfo, b);
                }
            } finally {
                serviceInfo.setProperty(Schema.class.getName() + ".CHECKED", Boolean.TRUE);
            }
        }
        return schema;
    }


    /**
     * Get the address from the provided endpoint reference.
     * @param ref - the endpoint reference
     * @return String the address of the endpoint
     */
    public static String getAddress(EndpointReferenceType ref) {
        AttributedURIType a = ref.getAddress();
        if (null != a) {
            return a.getValue();
        }
        return null;
    }

    /**
     * Set the address of the provided endpoint reference.
     * @param ref - the endpoint reference
     * @param address - the address
     */
    public static void setAddress(EndpointReferenceType ref, String address) {
        AttributedURIType a = new AttributedURIType();
        a.setValue(address);
        ref.setAddress(a);
    }
    /**
     * Create an endpoint reference for the provided wsdl, service and portname.
     * @param wsdlUrl - url of the wsdl that describes the service.
     * @param serviceName - the <code>QName</code> of the service.
     * @param portName - the name of the port.
     * @return EndpointReferenceType - the endpoint reference
     */
    public static EndpointReferenceType getEndpointReference(URL wsdlUrl,
                                                             QName serviceName,
                                                             String portName) {
        EndpointReferenceType reference = new EndpointReferenceType();
        reference.setMetadata(new MetadataType());
        setServiceAndPortName(reference, serviceName, portName);
        setWSDLLocation(reference, wsdlUrl.toString());

        return reference;
    }


    /**
     * Create a duplicate endpoint reference sharing all atributes
     * @param ref the reference to duplicate
     * @return EndpointReferenceType - the duplicate endpoint reference
     */
    public static EndpointReferenceType duplicate(EndpointReferenceType ref) {
        EndpointReferenceType reference = new EndpointReferenceType();
        reference.setMetadata(ref.getMetadata());
        reference.getAny().addAll(ref.getAny());
        reference.setAddress(ref.getAddress());
        return reference;
    }

    /**
     * Create an endpoint reference for the provided address.
     * @param address - address URI
     * @return EndpointReferenceType - the endpoint reference
     */
    public static EndpointReferenceType getEndpointReference(String address) {
        EndpointReferenceType reference = new EndpointReferenceType();
        setAddress(reference, address);
        return reference;
    }

    public static EndpointReferenceType getEndpointReference(AttributedURIType address) {
        EndpointReferenceType reference = new EndpointReferenceType();
        reference.setAddress(address);
        return reference;
    }

    /**
     * Create an anonymous endpoint reference.
     * @return EndpointReferenceType - the endpoint reference
     */
    public static EndpointReferenceType getAnonymousEndpointReference() {
        final EndpointReferenceType reference = new EndpointReferenceType();
        setAddress(reference, ANONYMOUS_ADDRESS);
        return reference;
    }

    /**
     * Resolve logical endpoint reference via the Bus EndpointResolverRegistry.
     *
     * @param logical the abstract EPR to resolve
     * @return the resolved concrete EPR if appropriate, null otherwise
     */
    public static EndpointReferenceType resolve(EndpointReferenceType logical, Bus bus) {
        EndpointReferenceType physical = null;
        if (bus != null) {
            EndpointResolverRegistry registry =
                bus.getExtension(EndpointResolverRegistry.class);
            if (registry != null) {
                physical = registry.resolve(logical);
            }
        }
        return physical != null ? physical : logical;
    }


    /**
     * Renew logical endpoint reference via the Bus EndpointResolverRegistry.
     *
     * @param logical the original abstract EPR (if still available)
     * @param physical the concrete EPR to renew
     * @return the renewed concrete EPR if appropriate, null otherwise
     */
    public static EndpointReferenceType renew(EndpointReferenceType logical,
                                              EndpointReferenceType physical,
                                              Bus bus) {
        EndpointReferenceType renewed = null;
        if (bus != null) {
            EndpointResolverRegistry registry =
                bus.getExtension(EndpointResolverRegistry.class);
            if (registry != null) {
                renewed = registry.renew(logical, physical);
            }
        }
        return renewed != null ? renewed : physical;
    }

    /**
     * Mint logical endpoint reference via the Bus EndpointResolverRegistry.
     *
     * @param serviceName the given serviceName
     * @return the newly minted EPR if appropriate, null otherwise
     */
    public static EndpointReferenceType mint(QName serviceName, Bus bus) {
        EndpointReferenceType logical = null;
        if (bus != null) {
            EndpointResolverRegistry registry =
                bus.getExtension(EndpointResolverRegistry.class);
            if (registry != null) {
                logical = registry.mint(serviceName);
            }
        }
        return logical;
    }

    /**
     * Mint logical endpoint reference via the Bus EndpointResolverRegistry.
     *
     * @param physical the concrete template EPR
     * @return the newly minted EPR if appropriate, null otherwise
     */
    public static EndpointReferenceType mint(EndpointReferenceType physical, Bus bus) {
        EndpointReferenceType logical = null;
        if (bus != null) {
            EndpointResolverRegistry registry =
                bus.getExtension(EndpointResolverRegistry.class);
            if (registry != null) {
                logical = registry.mint(physical);
            }
        }
        return logical != null ? logical : physical;
    }

    private static String getNameSpaceUri(Node node, String content, String namespaceURI) {
        if (namespaceURI == null) {
            namespaceURI = node.lookupNamespaceURI(content.substring(0,
                                                                  content.indexOf(':')));
        }
        return namespaceURI;
    }

    private static String getService(String content) {
        return content.substring(content.indexOf(':') + 1, content.length());
    }

    /**
     * Obtain a multiplexed endpoint reference for the deployed service that contains the provided id
     * @param serviceQName identified the target service
     * @param portName identifies a particular port of the service, may be null
     * @param id that must be embedded in the returned reference
     * @param bus the current bus
     * @return a new reference or null if the target destination does not support destination mutiplexing
     */
    public static EndpointReferenceType getEndpointReferenceWithId(QName serviceQName,
                                                                   String portName,
                                                                   String id,
                                                                   Bus bus) {
        EndpointReferenceType epr = null;
        MultiplexDestination destination = getMatchingMultiplexDestination(serviceQName, portName, bus);
        if (null != destination) {
            epr = destination.getAddressWithId(id);
        }
        return epr;
    }

    /**
     * Obtain the id String from the endpoint reference of the current dispatch.
     * @param messageContext the current message context
     * @return the id embedded in the current endpoint reference or null if not found
     */
    public static String getEndpointReferenceId(Map<String, Object> messageContext) {
        String id = null;
        Destination destination = (Destination) messageContext.get(Destination.class.getName());
        if (destination instanceof MultiplexDestination) {
            id = ((MultiplexDestination) destination).getId(messageContext);
        }
        return id;
    }


    private static synchronized JAXBContext createContextForEPR() throws JAXBException {
        Reference<JAXBContext> rctx = ADDRESSING_CONTEXT.get();
        JAXBContext ctx = rctx.get();
        if (ctx == null) {
            ctx = JAXBContextCache.getCachedContextAndSchemas(ADDRESSING_CLASSES,
                                                              null, null, null,
                                                              true).getContext();
            ADDRESSING_CONTEXT.set(new SoftReference<JAXBContext>(ctx));
        }
        return ctx;
    }
    private static JAXBContext getJAXBContextForEPR() throws JAXBException {
        Reference<JAXBContext> rctx = ADDRESSING_CONTEXT.get();
        JAXBContext ctx = rctx.get();
        if (ctx == null) {
            ctx = createContextForEPR();
        }
        return ctx;
    }
    public static Source convertToXML(EndpointReferenceType epr) {
        try {
            Marshaller jm = getJAXBContextForEPR().createMarshaller();
            jm.setProperty(Marshaller.JAXB_FRAGMENT, true);
            QName qname = new QName("http://www.w3.org/2005/08/addressing", "EndpointReference");
            JAXBElement<EndpointReferenceType> jaxEle
                = new JAXBElement<>(qname, EndpointReferenceType.class, epr);


            W3CDOMStreamWriter writer = new W3CDOMStreamWriter();
            jm.marshal(jaxEle, writer);
            return new DOMSource(writer.getDocument());
        } catch (JAXBException e) {
            //ignore
        }
        return null;
    }


    private static MultiplexDestination getMatchingMultiplexDestination(QName serviceQName, String portName,
                                                                        Bus bus) {
        MultiplexDestination destination = null;
        ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
        if (null != serverRegistry) {
            List<Server> servers = serverRegistry.getServers();
            for (Server s : servers) {
                QName targetServiceQName = s.getEndpoint().getEndpointInfo().getService().getName();
                if (serviceQName.equals(targetServiceQName) && portNameMatches(s, portName)) {
                    Destination dest = s.getDestination();
                    if (dest instanceof MultiplexDestination) {
                        destination = (MultiplexDestination)dest;
                        break;
                    }
                }
            }
        } else {
            LOG.log(Level.WARNING,
                    "Failed to locate service matching " + serviceQName
                    + ", because the bus ServerRegistry extension provider is null");
        }
        return destination;
    }

    private static boolean portNameMatches(Server s, String portName) {
        return null == portName
            || portName.equals(s.getEndpoint().getEndpointInfo().getName().getLocalPart());
    }

}

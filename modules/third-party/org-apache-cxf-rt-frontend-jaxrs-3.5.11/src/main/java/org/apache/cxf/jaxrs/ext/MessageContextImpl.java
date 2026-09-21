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
package org.apache.cxf.jaxrs.ext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.apache.cxf.attachment.AttachmentDeserializer;
import org.apache.cxf.attachment.AttachmentImpl;
import org.apache.cxf.attachment.AttachmentUtil;
import org.apache.cxf.attachment.HeaderSizeExceededException;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.AttachmentOutInterceptor;
import org.apache.cxf.io.CacheSizeExceededException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.jaxrs.impl.ProvidersImpl;
import org.apache.cxf.jaxrs.interceptor.AttachmentInputInterceptor;
import org.apache.cxf.jaxrs.interceptor.AttachmentOutputInterceptor;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.message.MessageUtils;

public class MessageContextImpl implements MessageContext {

    private Message m;
    public MessageContextImpl(Message m) {
        this.m = m;
    }

    public Object get(Object key) {
        String keyValue = key.toString();
        if (MultipartBody.INBOUND_MESSAGE_ATTACHMENTS.equals(keyValue)
            || (MultipartBody.INBOUND_MESSAGE_ATTACHMENTS + ".embedded").equals(keyValue)) {
            try {
                return createAttachments(key.toString());
            } catch (CacheSizeExceededException e) {
                m.getExchange().put("cxf.io.cacheinput", Boolean.FALSE);
                throw new WebApplicationException(e, 413);
            } catch (HeaderSizeExceededException e) {
                throw new WebApplicationException(e, 413);
            }
        }
        if (keyValue.equals("WRITE-" + Message.ATTACHMENTS) && m.getExchange().getOutMessage() != null) {
            return m.getExchange().getOutMessage().get(Message.ATTACHMENTS);
        }

        Message currentMessage = getCurrentMessage();
        Object value = currentMessage.get(key);
        if (value == null) {
            if (Message.class.getName().equals(key)) {
                return currentMessage;
            }
            Exchange exchange = currentMessage.getExchange();
            if (exchange != null) {
                Message otherMessage = exchange.getInMessage() == currentMessage
                    ? exchange.getOutMessage() : exchange.getInMessage();
                if (otherMessage != null) {
                    value = otherMessage.get(key);
                }
                if (value == null) {
                    value = m.getExchange().get(key);
                }
            }
        }
        return value;
    }

    private Message getCurrentMessage() {
        Message currentMessage = JAXRSUtils.getCurrentMessage();
        if (currentMessage == null) {
            currentMessage = m;
        }
        return currentMessage;
    }

    public <T> T getContent(Class<T> format) {
        if (MessageUtils.isRequestor(m) && m.getExchange().getInMessage() != null) {
            Message inMessage = m.getExchange().getInMessage();
            return inMessage.getContent(format);
        }
        return m.getContent(format);
    }

    public Object getContextualProperty(Object key) {
        Object value = m.getContextualProperty(key.toString());
        if (value == null && key.getClass() == Class.class) {
            return m.getExchange().get((Class<?>)key);
        }
        return value;
    }

    public <T> T getContext(Class<T> contextClass) {
        return getContext(contextClass, contextClass);
    }

    protected <T> T getContext(Type genericType, Class<T> clazz) {
        return JAXRSUtils.createContextValue(m, genericType, clazz);
    }

    public <T, E> T getResolver(Class<T> resolverClazz, Class<E> resolveClazz) {
        if (ContextResolver.class == resolverClazz) {
            return resolverClazz.cast(getContext(resolveClazz, ContextResolver.class));
        }
        return null;
    }

    public Request getRequest() {
        return getContext(Request.class);
    }

    public HttpHeaders getHttpHeaders() {
        return getContext(HttpHeaders.class);
    }

    public Providers getProviders() {
        return getContext(Providers.class);
    }

    public SecurityContext getSecurityContext() {
        return getContext(SecurityContext.class);
    }

    public UriInfo getUriInfo() {
        return getContext(UriInfo.class);
    }

    public HttpServletRequest getHttpServletRequest() {
        try {
            return getContext(HttpServletRequest.class);
        } catch (Throwable t) {
            return null;
        }
    }

    public HttpServletResponse getHttpServletResponse() {
        return getContext(HttpServletResponse.class);
    }

    public ServletConfig getServletConfig() {
        return getContext(ServletConfig.class);
    }

    public ServletContext getServletContext() {
        return getContext(ServletContext.class);
    }

    public void put(Object key, Object value) {
        if (MultipartBody.OUTBOUND_MESSAGE_ATTACHMENTS.equals(key.toString())) {
            convertToAttachments(value);
        }
        Message currentMessage = getCurrentMessage();
        currentMessage.put(key.toString(), value);
        currentMessage.getExchange().put(key.toString(), value);

    }

    private void convertToAttachments(Object value) {
        List<?> handlers = (List<?>)value;
        List<org.apache.cxf.message.Attachment> atts =
            new ArrayList<>();

        for (int i = 1; i < handlers.size(); i++) {
            Attachment handler = (Attachment)handlers.get(i);
            AttachmentImpl att = new AttachmentImpl(handler.getContentId(), handler.getDataHandler());
            for (String key : handler.getHeaders().keySet()) {
                att.setHeader(key, handler.getHeader(key));
            }
            att.setXOP(false);
            atts.add(att);
        }
        Message outMessage = getOutMessage();
        outMessage.setAttachments(atts);
        outMessage.put(AttachmentOutInterceptor.WRITE_ATTACHMENTS, "true");
        Attachment root = (Attachment)handlers.get(0);

        String rootContentType = root.getContentType().toString();
        MultivaluedMap<String, String> rootHeaders = new MetadataMap<>(root.getHeaders(), true, false, true);
        if (!AttachmentUtil.isMtomEnabled(outMessage)) {
            rootHeaders.putSingle(Message.CONTENT_TYPE, rootContentType);
        }

        String messageContentType = outMessage.get(Message.CONTENT_TYPE).toString();
        int index = messageContentType.indexOf(";type");
        if (index > 0) {
            messageContentType = messageContentType.substring(0, index).trim();
        }
        AttachmentOutputInterceptor attInterceptor =
            new AttachmentOutputInterceptor(messageContentType, rootHeaders);

        outMessage.put(Message.CONTENT_TYPE, rootContentType);
        Map<String, List<String>> allHeaders =
            CastUtils.cast((Map<?, ?>)outMessage.get(Message.PROTOCOL_HEADERS));
        if (allHeaders != null) {
            allHeaders.remove(Message.CONTENT_TYPE);
        }
        attInterceptor.handleMessage(outMessage);
    }

    private Message getOutMessage() {

        Message message = m.getExchange().getOutMessage();
        if (message == null) {
            Endpoint ep = m.getExchange().getEndpoint();
            message = new org.apache.cxf.message.MessageImpl();
            message.setExchange(m.getExchange());
            message = ep.getBinding().createMessage(message);
            m.getExchange().setOutMessage(message);
        }

        return message;
    }

    private MultipartBody createAttachments(String propertyName) {
        Message inMessage = m.getExchange().getInMessage();
        boolean embeddedAttachment = inMessage.get("org.apache.cxf.multipart.embedded") != null;

        Object o = inMessage.get(propertyName);
        if (o != null) {
            return (MultipartBody)o;
        }

        if (embeddedAttachment) {
            inMessage = new MessageImpl();
            inMessage.setExchange(new ExchangeImpl());
            inMessage.put(AttachmentDeserializer.ATTACHMENT_DIRECTORY,
                m.getExchange().getInMessage().get(AttachmentDeserializer.ATTACHMENT_DIRECTORY));
            inMessage.put(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD,
                m.getExchange().getInMessage().get(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD));
            inMessage.put(AttachmentDeserializer.ATTACHMENT_MAX_SIZE,
                m.getExchange().getInMessage().get(AttachmentDeserializer.ATTACHMENT_MAX_SIZE));
            inMessage.put(AttachmentDeserializer.ATTACHMENT_MAX_HEADER_SIZE,
                m.getExchange().getInMessage().get(AttachmentDeserializer.ATTACHMENT_MAX_HEADER_SIZE));
            inMessage.setContent(InputStream.class,
                m.getExchange().getInMessage().get("org.apache.cxf.multipart.embedded.input"));
            inMessage.put(Message.CONTENT_TYPE,
                m.getExchange().getInMessage().get("org.apache.cxf.multipart.embedded.ctype").toString());
        }


        new AttachmentInputInterceptor().handleMessage(inMessage);

        List<Attachment> newAttachments = new LinkedList<>();
        try {
            Map<String, List<String>> headers
                = CastUtils.cast((Map<?, ?>)inMessage.get(AttachmentDeserializer.ATTACHMENT_PART_HEADERS));

            Attachment first = new Attachment(AttachmentUtil.createAttachment(
                                     inMessage.getContent(InputStream.class),
                                     headers,
                                     inMessage),
                                     new ProvidersImpl(inMessage));
            newAttachments.add(first);
        } catch (IOException ex) {
            throw ExceptionUtils.toInternalServerErrorException(ex, null);
        }


        Collection<org.apache.cxf.message.Attachment> childAttachments = inMessage.getAttachments();
        if (childAttachments == null) {
            childAttachments = Collections.emptyList();
        }
        for (org.apache.cxf.message.Attachment a : childAttachments) {
            newAttachments.add(new Attachment(a, new ProvidersImpl(inMessage)));
        }
        MediaType mt = embeddedAttachment
            ? (MediaType)inMessage.get("org.apache.cxf.multipart.embedded.ctype")
            : getHttpHeaders().getMediaType();
        MultipartBody body = new MultipartBody(newAttachments, mt, false);
        inMessage.put(propertyName, body);
        return body;
    }

}

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

package org.apache.cxf.jaxrs.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.jaxrs.provider.FormEncodingProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

public final class FormUtils {
    public static final String FORM_PARAMS_FROM_HTTP_PARAMS = "set.form.parameters.from.http.parameters";
    public static final String FORM_PARAM_MAP = "org.apache.cxf.form_data";
    public static final String FORM_PARAM_MAP_DECODED = "org.apache.cxf.form_data.decoded";

    private static final Logger LOG = LogUtils.getL7dLogger(FormUtils.class);
    private static final String MULTIPART_FORM_DATA_TYPE = "form-data";
    private static final String MAX_FORM_PARAM_COUNT = "maxFormParameterCount";
    private static final String CONTENT_DISPOSITION_FILES_PARAM = "files";
    private FormUtils() {

    }

    public static String formToString(Form form) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            FormUtils.writeMapToOutputStream(form.asMap(), bos, StandardCharsets.UTF_8.name(), false);
            return bos.toString(StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            // will not happen
        }
        return "";
    }

    public static void restoreForm(FormEncodingProvider<Form> provider,
                                   Form form,
                                   Message message)
        throws Exception {
        CachedOutputStream os = new CachedOutputStream();
        writeForm(provider, form, os);
        message.setContent(InputStream.class, os.getInputStream());
    }

    public static void writeForm(FormEncodingProvider<Form> provider,
                                 Form form, OutputStream os)
        throws Exception {
        provider.writeTo(form, Form.class, Form.class, new Annotation[]{},
                         MediaType.APPLICATION_FORM_URLENCODED_TYPE, new MetadataMap<String, Object>(), os);
    }

    public static Form readForm(FormEncodingProvider<Form> provider, Message message)
        throws Exception {
        return provider.readFrom(Form.class, Form.class,
                              new Annotation[]{}, MediaType.APPLICATION_FORM_URLENCODED_TYPE,
                              new MetadataMap<String, String>(),
                              message.getContent(InputStream.class));
    }

    public static void addPropertyToForm(MultivaluedMap<String, String> map, String name, Object value) {
        if (!"".equals(name)) {
            map.add(name, value.toString());
        } else {
            MultivaluedMap<String, Object> values =
                InjectionUtils.extractValuesFromBean(value, "");
            for (Map.Entry<String, List<Object>> entry : values.entrySet()) {
                for (Object v : entry.getValue()) {
                    map.add(entry.getKey(), v.toString());
                }
            }
        }
    }

    public static String readBody(InputStream is, String encoding) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(is, bos, 1024);
            return new String(bos.toByteArray(), encoding);
        } catch (Exception ex) {
            throw ExceptionUtils.toInternalServerErrorException(ex, null);
        }
    }

    public static void populateMapFromString(MultivaluedMap<String, String> params,
                                             Message m,
                                             String postBody,
                                             String enc,
                                             boolean decode) {
        if (StringUtils.isEmpty(postBody)) {
            return;
        }
        String[] parts = postBody.split("&");
        checkNumberOfParts(m, parts.length);
        for (String part : parts) {
            String[] keyValue = new String[2];
            int index = part.indexOf('=');
            if (index != -1) {
                keyValue[0] = part.substring(0, index);
                keyValue[1] = index + 1 < part.length() ? part.substring(index + 1) : "";
            } else {
                keyValue[0] = part;
                keyValue[1] = "";
            }
            String name = HttpUtils.urlDecode(keyValue[0], enc);
            if (decode) {
                params.add(name, HttpUtils.urlDecode(keyValue[1], enc));
            } else {
                params.add(name, keyValue[1]);
            }
        }

    }

    public static void populateMapFromStringOrHttpRequest(MultivaluedMap<String, String> params,
                                             Message m,
                                             String postBody,
                                             String enc,
                                             boolean decode) {
        HttpServletRequest request = (HttpServletRequest)m.get(AbstractHTTPDestination.HTTP_REQUEST);
        populateMapFromString(params, m, postBody, enc, decode, request);
        
    }
    
    public static void populateMapFromString(MultivaluedMap<String, String> params,
                                             Message m,
                                             String postBody,
                                             String enc,
                                             boolean decode,
                                             javax.servlet.http.HttpServletRequest request) {
        if (!StringUtils.isEmpty(postBody)) {
            populateMapFromString(params, m, postBody, enc, decode);
        } else if (request != null
            && MessageUtils.getContextualBoolean(m, FORM_PARAMS_FROM_HTTP_PARAMS, true)) {
            for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements();) {
                String paramName = en.nextElement();
                String[] values = request.getParameterValues(paramName);
                params.put(HttpUtils.urlDecode(paramName), Arrays.asList(values));
            }
            logRequestParametersIfNeeded(params, enc);
            // The form params extracted from the HttpServelRequest are already decoded
            m.put(FORM_PARAM_MAP_DECODED, true);
        }
    }

    public static void logRequestParametersIfNeeded(Map<String, List<String>> params, String enc) {
        if ((PhaseInterceptorChain.getCurrentMessage() == null)
            || (PhaseInterceptorChain.getCurrentMessage().getInterceptorChain() == null)) {
            return;
        }
        String chain = PhaseInterceptorChain.getCurrentMessage().getInterceptorChain().toString();
        if (chain.contains("LoggingInInterceptor")) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                writeMapToOutputStream(params, bos, enc, false);
                LOG.info(bos.toString(enc));
            } catch (IOException ex) {
                // ignore
            }
        }
    }

    public static void writeMapToOutputStream(Map<String, List<String>> map,
                                              OutputStream os,
                                              String enc,
                                              boolean encoded) throws IOException {
        for (Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, List<String>> entry = it.next();

            String key = entry.getKey();
            if (!encoded) {
                key = HttpUtils.urlEncode(key, enc);
            }
            for (Iterator<String> entryIterator = entry.getValue().iterator(); entryIterator.hasNext();) {
                os.write(key.getBytes(enc));
                os.write('=');

                String value = entryIterator.next();
                if (!encoded) {
                    value = HttpUtils.urlEncode(value, enc);
                }
                os.write(value.getBytes(enc));
                if (entryIterator.hasNext()) {
                    os.write('&');
                }
            }
            if (it.hasNext()) {
                os.write('&');
            }

        }
    }

    public static void populateMapFromMultipart(MultivaluedMap<String, String> params,
                                                MultipartBody body,
                                                Message m,
                                                boolean decode) {
        List<Attachment> atts = body.getAllAttachments();
        checkNumberOfParts(m, atts.size());
        for (Attachment a : atts) {
            ContentDisposition cd = a.getContentDisposition();
            if (cd != null && !MULTIPART_FORM_DATA_TYPE.equalsIgnoreCase(cd.getType())) {
                continue;
            }
            String cdName = cd == null ? null : cd.getParameter("name");
            String contentId = a.getContentId();
            String name = StringUtils.isEmpty(cdName) ? contentId : cdName.replace("\"", "").replace("'", "");
            if (StringUtils.isEmpty(name)) {
                throw ExceptionUtils.toBadRequestException(null, null);
            }
            if (CONTENT_DISPOSITION_FILES_PARAM.equals(name)) {
                // this is a reserved name in Content-Disposition for parts containing files
                continue;
            }
            try {
                String value = IOUtils.toString(a.getDataHandler().getInputStream());
                params.add(HttpUtils.urlDecode(name),
                           decode ? HttpUtils.urlDecode(value) : value);
            } catch (IllegalArgumentException ex) {
                LOG.warning("Illegal URL-encoded characters, make sure that no "
                    + "@FormParam and @Multipart annotations are mixed up");
                throw ExceptionUtils.toInternalServerErrorException(ex, null);
            } catch (IOException ex) {
                throw ExceptionUtils.toBadRequestException(null, null);
            }
        }
    }

    private static void checkNumberOfParts(Message m, int numberOfParts) {
        if (m == null || m.getExchange() == null || m.getExchange().getInMessage() == null) {
            return;
        }
        String maxPartsCountProp = (String)m.getExchange()
            .getInMessage().getContextualProperty(MAX_FORM_PARAM_COUNT);
        if (maxPartsCountProp == null) {
            return;
        }
        try {
            int maxPartsCount = Integer.parseInt(maxPartsCountProp);
            if (maxPartsCount != -1 && numberOfParts >= maxPartsCount) {
                throw new WebApplicationException(413);
            }
        } catch (NumberFormatException ex) {
            throw ExceptionUtils.toInternalServerErrorException(ex, null);
        }
    }

    public static boolean isFormPostRequest(Message m) {
        return MediaType.APPLICATION_FORM_URLENCODED.equals(m.get(Message.CONTENT_TYPE))
            && HttpMethod.POST.equals(m.get(Message.HTTP_REQUEST_METHOD));
    }
}

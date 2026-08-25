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

package org.apache.cxf.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataContentHandler;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.activation.URLDataSource;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.common.util.SystemPropertyAction;
import org.apache.cxf.helpers.FileUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;

public final class AttachmentUtil {
    // The default values for {@link AttachmentDataSource} content type in case when
    // "Content-Type" header is not present.
    public static final String ATTACHMENT_CONTENT_TYPE = "org.apache.cxf.attachment.content-type"; 

    // The xop:include "href" attribute (https://www.w3.org/TR/xop10/#xop_href) may include 
    // arbitrary URL which we should never follow (unless explicitly allowed).
    public static final String ATTACHMENT_XOP_FOLLOW_URLS_PROPERTY = "org.apache.cxf.attachment.xop.follow.urls";
    public static final String BODY_ATTACHMENT_ID = "root.message@cxf.apache.org";

    static final String BINARY = "binary";
    
    private static final Logger LOG = LogUtils.getL7dLogger(AttachmentUtil.class);

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final String ATT_UUID = UUID.randomUUID().toString();

    private static final Random BOUND_RANDOM = new Random();
    private static final CommandMap DEFAULT_COMMAND_MAP = CommandMap.getDefaultCommandMap();
    private static final MailcapCommandMap COMMAND_MAP = new EnhancedMailcapCommandMap();
    
    
    static final class EnhancedMailcapCommandMap extends MailcapCommandMap {
        @Override
        public synchronized DataContentHandler createDataContentHandler(
                String mimeType) {
            DataContentHandler dch = super.createDataContentHandler(mimeType);
            if (dch == null) {
                dch = DEFAULT_COMMAND_MAP.createDataContentHandler(mimeType);
            }
            return dch;
        }

        @Override
        public DataContentHandler createDataContentHandler(String mimeType,
                DataSource ds) {
            DataContentHandler dch = super.createDataContentHandler(mimeType);
            if (dch == null) {
                dch = DEFAULT_COMMAND_MAP.createDataContentHandler(mimeType, ds);
            }
            return dch;
        }

        @Override
        public synchronized CommandInfo[] getAllCommands(String mimeType) {
            CommandInfo[] commands = super.getAllCommands(mimeType);
            CommandInfo[] defaultCommands = DEFAULT_COMMAND_MAP.getAllCommands(mimeType);
            List<CommandInfo> cmdList = new ArrayList<>(Arrays.asList(commands));

            // Add CommandInfo which does not exist in current command map.
            for (CommandInfo defCmdInfo : defaultCommands) {
                String defCmdName = defCmdInfo.getCommandName();
                boolean cmdNameExist = false;
                for (CommandInfo cmdInfo : commands) {
                    if (cmdInfo.getCommandName().equals(defCmdName)) {
                        cmdNameExist = true;
                        break;
                    }
                }
                if (!cmdNameExist) {
                    cmdList.add(defCmdInfo);
                }
            }

            CommandInfo[] allCommandArray = new CommandInfo[0];
            return cmdList.toArray(allCommandArray);
        }

        @Override
        public synchronized CommandInfo getCommand(String mimeType, String cmdName) {
            CommandInfo cmdInfo = super.getCommand(mimeType, cmdName);
            if (cmdInfo == null) {
                cmdInfo = DEFAULT_COMMAND_MAP.getCommand(mimeType, cmdName);
            }
            return cmdInfo;
        }

        /**
         * Merge current mime types and default mime types.
         */
        @Override
        public synchronized String[] getMimeTypes() {
            String[] mimeTypes = super.getMimeTypes();
            String[] defMimeTypes = DEFAULT_COMMAND_MAP.getMimeTypes();
            Set<String> mimeTypeSet = new HashSet<>();
            Collections.addAll(mimeTypeSet, mimeTypes);
            Collections.addAll(mimeTypeSet, defMimeTypes);
            String[] mimeArray = new String[0];
            return mimeTypeSet.toArray(mimeArray);
        }
    }


    private AttachmentUtil() {

    }

    static {
        COMMAND_MAP.addMailcap("image/*;;x-java-content-handler="
                               + ImageDataContentHandler.class.getName());
    }

    public static CommandMap getCommandMap() {
        return COMMAND_MAP;
    }

    public static boolean isMtomEnabled(Message message) {
        return MessageUtils.getContextualBoolean(message, Message.MTOM_ENABLED, false);
    }

    public static void setStreamedAttachmentProperties(Message message, CachedOutputStream bos)
        throws IOException {
        Object directory = message.getContextualProperty(AttachmentDeserializer.ATTACHMENT_DIRECTORY);
        if (directory != null) {
            if (directory instanceof File) {
                bos.setOutputDir((File) directory);
            } else if (directory instanceof String) {
                bos.setOutputDir(new File((String) directory));
            } else {
                throw new IOException("The value set as " + AttachmentDeserializer.ATTACHMENT_DIRECTORY
                        + " should be either an instance of File or String");
            }
        }

        Object threshold = message.getContextualProperty(AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD);
        if (threshold != null) {
            if (threshold instanceof Number) {
                long t = ((Number) threshold).longValue();
                if (t >= 0) {
                    bos.setThreshold(t);
                } else {
                    LOG.warning("Threshold value overflowed long. Setting default value!");
                    bos.setThreshold(AttachmentDeserializer.THRESHOLD);
                }
            } else if (threshold instanceof String) {
                try {
                    bos.setThreshold(Long.parseLong((String) threshold));
                } catch (NumberFormatException e) {
                    throw new IOException("Provided threshold String is not a number", e);
                }
            } else {
                throw new IOException("The value set as " + AttachmentDeserializer.ATTACHMENT_MEMORY_THRESHOLD
                        + " should be either an instance of Number or String");
            }
        } else if (!CachedOutputStream.isThresholdSysPropSet()) {
            // Use the default AttachmentDeserializer Threshold only if there is no system property defined
            bos.setThreshold(AttachmentDeserializer.THRESHOLD);
        }

        Object maxSize = message.getContextualProperty(AttachmentDeserializer.ATTACHMENT_MAX_SIZE);
        if (maxSize == null) {
            maxSize = AttachmentDeserializer.DEFAULT_ATTACHMENT_MAX_SIZE;
        }
        if (maxSize instanceof Number) {
            long size = ((Number) maxSize).longValue();
            if (size >= 0) {
                bos.setMaxSize(size);
            } else {
                LOG.warning("The max size value is set to unlimited.");
            }
        } else if (maxSize instanceof String) {
            try {
                bos.setMaxSize(Long.parseLong((String) maxSize));
            } catch (NumberFormatException e) {
                throw new IOException("Provided max size String is not a number", e);
            }
        } else {
            throw new IOException("The value set as " + AttachmentDeserializer.ATTACHMENT_MAX_SIZE
                    + " should be either an instance of Number or String");
        }
    }

    public static String createContentID(String ns) throws UnsupportedEncodingException {
        // tend to change
        String cid = "cxf.apache.org";
        if (ns != null && !ns.isEmpty()) {
            try {
                URI uri = new URI(ns);
                String host = uri.getHost();
                if (host != null) {
                    cid = host;
                } else {
                    cid = ns;
                }
            } catch (Exception e) {
                cid = ns;
            }
        }
        return ATT_UUID + '-' + Integer.toString(COUNTER.incrementAndGet()) + '@'
            + URLEncoder.encode(cid, StandardCharsets.UTF_8.name());
    }

    public static String getUniqueBoundaryValue() {
        //generate a random UUID.
        //we don't need the cryptographically secure random uuid that
        //UUID.randomUUID() will produce.  Thus, use a faster
        //pseudo-random thing
        long leastSigBits;
        long mostSigBits;
        synchronized (BOUND_RANDOM) {
            mostSigBits = BOUND_RANDOM.nextLong();
            leastSigBits = BOUND_RANDOM.nextLong();
        }

        mostSigBits &= 0xFFFFFFFFFFFF0FFFL;  //clear version
        mostSigBits |= 0x0000000000004000L;  //set version

        leastSigBits &= 0x3FFFFFFFFFFFFFFFL; //clear the variant
        leastSigBits |= 0x8000000000000000L; //set to IETF variant

        UUID result = new UUID(mostSigBits, leastSigBits);

        return "uuid:" + result.toString();
    }

    public static Map<String, DataHandler> getDHMap(final Collection<Attachment> attachments) {
        Map<String, DataHandler> dataHandlers = null;
        if (attachments != null) {
            if (attachments instanceof LazyAttachmentCollection) {
                dataHandlers = ((LazyAttachmentCollection)attachments).createDataHandlerMap();
            } else {
                dataHandlers = new DHMap(attachments);
            }
        }
        return dataHandlers == null ? new LinkedHashMap<>() : dataHandlers;
    }

    static class DHMap extends AbstractMap<String, DataHandler> {
        final Collection<Attachment> list;
        DHMap(Collection<Attachment> l) {
            list = l;
        }
        public Set<Map.Entry<String, DataHandler>> entrySet() {
            return new AbstractSet<Map.Entry<String, DataHandler>>() {
                @Override
                public Iterator<Map.Entry<String, DataHandler>> iterator() {
                    final Iterator<Attachment> it = list.iterator();
                    return new Iterator<Map.Entry<String, DataHandler>>() {
                        public boolean hasNext() {
                            return it.hasNext();
                        }
                        public Map.Entry<String, DataHandler> next() {
                            final Attachment a = it.next();
                            return new Map.Entry<String, DataHandler>() {
                                @Override
                                public String getKey() {
                                    return a.getId();
                                }

                                @Override
                                public DataHandler getValue() {
                                    return a.getDataHandler();
                                }

                                @Override
                                public DataHandler setValue(DataHandler value) {
                                    return null;
                                }
                            };
                        }
                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return list.size();
                }
            };
        }
        
        @Override
        public DataHandler put(String key, DataHandler value) {
            Iterator<Attachment> i = list.iterator();
            DataHandler ret = null;
            while (i.hasNext()) {
                Attachment a = i.next();
                if (a.getId().equals(key)) {
                    i.remove();
                    ret = a.getDataHandler();
                    break;
                }
            }
            list.add(new AttachmentImpl(key, value));
            return ret;
        }
    }

    public static String cleanContentId(String id) {
        if (id != null) {
            if (id.startsWith("<")) {
                // strip <>
                id = id.substring(1, id.length() - 1);
            }
            // strip cid:
            if (id.startsWith("cid:")) {
                id = id.substring(4);
            }

            try {
                // urldecode
                id = URLDecoder.decode(id, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                //ignore, keep id as is
            }
        }
        if (id == null) {
            //no Content-ID, set cxf default ID
            id =  BODY_ATTACHMENT_ID;
        }
        return id;
    }

    static String getHeaderValue(List<String> v) {
        if (v != null && !v.isEmpty()) {
            return v.get(0);
        }
        return null;
    }
    static String getHeaderValue(List<String> v, String delim) {
        if (v != null && !v.isEmpty()) {
            return String.join(delim, v);
        }
        return null;
    }
    static String getHeader(Map<String, List<String>> headers, String h) {
        return getHeaderValue(headers.get(h));
    }
    static String getHeader(Map<String, List<String>> headers, String h, String delim) {
        return getHeaderValue(headers.get(h), delim);
    }

    /**
     * @deprecated use createAttachment(InputStream stream, Map<String, List<String>> headers, Message message)
     */
    public static Attachment createAttachment(InputStream stream, Map<String, List<String>> headers) 
            throws IOException {
        return createAttachment(stream, headers, null /* no Message */);
    }

    public static Attachment createAttachment(InputStream stream, Map<String, List<String>> headers, Message message)
            throws IOException {

        String id = cleanContentId(getHeader(headers, "Content-ID"));

        AttachmentImpl att = new AttachmentImpl(id);

        String ct = getHeader(headers, "Content-Type");
        if (StringUtils.isEmpty(ct)) {
            ct = MessageUtils.getContextualString(message, ATTACHMENT_CONTENT_TYPE, "application/octet-stream");
        }

        String cd = getHeader(headers, "Content-Disposition");
        String fileName = getContentDispositionFileName(cd);

        String encoding = null;

        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            String name = e.getKey();
            if ("Content-Transfer-Encoding".equalsIgnoreCase(name)) {
                encoding = getHeader(headers, name);
                if (BINARY.equalsIgnoreCase(encoding)) {
                    att.setXOP(true);
                }
            }
            att.setHeader(name, getHeaderValue(e.getValue()));
        }
        if (encoding == null) {
            encoding = BINARY;
        }
        InputStream ins = decode(stream, encoding);
        if (ins != stream) {
            headers.remove("Content-Transfer-Encoding");
        }
        DataSource source = new AttachmentDataSource(ct, ins);
        if (!StringUtils.isEmpty(fileName)) {
            ((AttachmentDataSource)source).setName(FileUtils.stripPath(fileName));
        }
        att.setDataHandler(new DataHandler(source));
        return att;
    }

    static String getContentDispositionFileName(String cd) {
        if (StringUtils.isEmpty(cd)) {
            return null;
        }
        ContentDisposition c = new ContentDisposition(cd);
        String s = c.getParameter("filename");
        if (s == null) {
            s = c.getParameter("name");
        }
        return s;
    }

    public static InputStream decode(InputStream in, String encoding) throws IOException {
        if (encoding == null) {
            return in;
        }
        encoding = encoding.toLowerCase();

        // some encodings are just pass-throughs, with no real decoding.
        if (BINARY.equals(encoding)
            || "7bit".equals(encoding)
            || "8bit".equals(encoding)) {
            return in;
        } else if ("base64".equals(encoding)) {
            return new Base64DecoderStream(in);
        } else if ("quoted-printable".equals(encoding)) {
            return new QuotedPrintableDecoderStream(in);
        } else {
            throw new IOException("Unknown encoding " + encoding);
        }
    }
    public static boolean isTypeSupported(String contentType, List<String> types) {
        if (contentType == null) {
            return false;
        }
        contentType = contentType.toLowerCase();
        for (String s : types) {
            if (contentType.indexOf(s) != -1) {
                return true;
            }
        }
        return false;
    }

    public static Attachment createMtomAttachment(boolean isXop, String mimeType, String elementNS,
                                                 byte[] data, int offset, int length, int threshold) {
        if (!isXop || length <= threshold) {
            return null;
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        ByteDataSource source = new ByteDataSource(data, offset, length);
        source.setContentType(mimeType);
        DataHandler handler = new DataHandler(source);

        String id;
        try {
            id = AttachmentUtil.createContentID(elementNS);
        } catch (UnsupportedEncodingException e) {
            throw new Fault(e);
        }
        AttachmentImpl att = new AttachmentImpl(id, handler);
        att.setXOP(isXop);
        return att;
    }

    public static Attachment createMtomAttachmentFromDH(
        boolean isXop, DataHandler handler, String elementNS, int threshold) {
        if (!isXop) {
            return null;
        }

        // The following is just wrong. Even if the DataHandler has a stream, we should still
        // apply the threshold.
        try {
            DataSource ds = handler.getDataSource();
            if (ds instanceof FileDataSource) {
                FileDataSource fds = (FileDataSource)ds;
                File file = fds.getFile();
                if (file.length() < threshold) {
                    return null;
                }
            } else if (ds.getClass().getName().endsWith("ObjectDataSource")) {
                Object o = handler.getContent();
                if (o instanceof String
                    && ((String)o).length() < threshold) {
                    return null;
                } else if (o instanceof byte[] && ((byte[])o).length < threshold) {
                    return null;
                }
            }
        } catch (IOException e1) {
        //      ignore, just do the normal attachment thing
        }

        String id;
        try {
            id = AttachmentUtil.createContentID(elementNS);
        } catch (UnsupportedEncodingException e) {
            throw new Fault(e);
        }
        AttachmentImpl att = new AttachmentImpl(id, handler);
        if (!StringUtils.isEmpty(handler.getName())) {
            //set Content-Disposition attachment header if filename isn't null
            String file = handler.getName();
            File f = new File(file);
            if (f.exists() && f.isFile()) {
                file = f.getName();
            }
            att.setHeader("Content-Disposition", "attachment;name=\"" + file + "\"");
        }
        att.setXOP(isXop);
        return att;
    }

    public static DataSource getAttachmentDataSource(String contentId, Collection<Attachment> atts) {
        //
        // RFC-2392 (https://datatracker.ietf.org/doc/html/rfc2392) says:
        //
        // A "cid" URL is converted to the corresponding Content-ID message
        // header [MIME] by removing the "cid:" prefix, converting the % encoded
        // character to their equivalent US-ASCII characters, and enclosing the
        // remaining parts with an angle bracket pair, "<" and ">".  
        //
        if (contentId.startsWith("cid:")) {
            try {
                contentId = URLDecoder.decode(contentId.substring(4), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException ue) {
                contentId = contentId.substring(4);
            }
            
            // href attribute information item: MUST be a valid URI per the cid: URI scheme (RFC 2392), 
            // for example:
            //
            //   <xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/me.png'/>
            // 
            // See please https://www.w3.org/TR/xop10/
            //
            if (contentId.indexOf("://") == -1) {
                return loadDataSource(contentId, atts);
            } else {
                try {
                    final boolean followUrls = Boolean.valueOf(SystemPropertyAction
                        .getProperty(ATTACHMENT_XOP_FOLLOW_URLS_PROPERTY, "false"));
                    if (followUrls) {
                        return new URLDataSource(new URL(contentId));
                    } else {
                        return loadDataSource(contentId, atts);
                    }
                } catch (MalformedURLException e) {
                    throw new Fault(e);
                }
            }
        } else {
            return loadDataSource(contentId, atts);
        }
    }

    private static DataSource loadDataSource(String contentId, Collection<Attachment> atts) {
        return new LazyDataSource(contentId, atts);
    }

}
/* @generated */

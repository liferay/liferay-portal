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
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataSource;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.common.util.SystemPropertyAction;
import org.apache.cxf.helpers.HttpHeaderHelper;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;

public class AttachmentDeserializer {
    public static final String ATTACHMENT_PART_HEADERS = AttachmentDeserializer.class.getName() + ".headers";

    /**
     * Allowed value is any instance of {@link File} or {@link String}.
     */
    public static final String ATTACHMENT_DIRECTORY = "attachment-directory";

    /**
     * The memory threshold of attachments. Allowed value is any instance of {@link Number} or {@link String}.
     * The default is {@link AttachmentDeserializer#THRESHOLD}.
     */
    public static final String ATTACHMENT_MEMORY_THRESHOLD = "attachment-memory-threshold";

    /**
     * The maximum size of the attachment. Allowed value is any of {@link Number} or {@link String}.
     */
    public static final String ATTACHMENT_MAX_SIZE = "attachment-max-size";

    /**
     * The maximum number of attachments permitted in a message. The default is 50.
     */
    public static final String ATTACHMENT_MAX_COUNT = "attachment-max-count";

    /**
     * The maximum MIME Header Length. The default is 300.
     */
    public static final String ATTACHMENT_MAX_HEADER_SIZE = "attachment-max-header-size";
    public static final int DEFAULT_MAX_HEADER_SIZE =
        SystemPropertyAction.getInteger("org.apache.cxf.attachment-max-header-size", 300);

    public static final int THRESHOLD = 1024 * 100; //100K (byte unit)

    private static final Pattern CONTENT_TYPE_BOUNDARY_PATTERN = Pattern.compile("boundary=\"?([^\";]*)");

    private static final Pattern INPUT_STREAM_BOUNDARY_PATTERN =
            Pattern.compile("^--(\\S*)$", Pattern.MULTILINE);

    private static final Logger LOG = LogUtils.getL7dLogger(AttachmentDeserializer.class);

    private static final int PUSHBACK_AMOUNT = 2048;

    private boolean lazyLoading = true;

    private PushbackInputStream stream;
    private int createCount;
    private int closedCount;
    private boolean closed;

    private byte[] boundary;

    private LazyAttachmentCollection attachments;

    private Message message;

    private InputStream body;

    private Set<DelegatingInputStream> loaded = new HashSet<>();
    private List<String> supportedTypes;

    private int maxHeaderLength = DEFAULT_MAX_HEADER_SIZE;

    public AttachmentDeserializer(Message message) {
        this(message, Collections.singletonList("multipart/related"));
    }

    public AttachmentDeserializer(Message message, List<String> supportedTypes) {
        this.message = message;
        this.supportedTypes = supportedTypes;

        // Get the maximum Header length from configuration
        maxHeaderLength = MessageUtils.getContextualInteger(message, ATTACHMENT_MAX_HEADER_SIZE,
                                                            DEFAULT_MAX_HEADER_SIZE);
    }

    public void initializeAttachments() throws IOException {
        initializeRootMessage();

        Object maxCountProperty = message.getContextualProperty(AttachmentDeserializer.ATTACHMENT_MAX_COUNT);
        int maxCount = 50;
        if (maxCountProperty != null) {
            if (maxCountProperty instanceof Integer) {
                maxCount = (Integer)maxCountProperty;
            } else {
                maxCount = Integer.parseInt((String)maxCountProperty);
            }
        }

        attachments = new LazyAttachmentCollection(this, maxCount);
        message.setAttachments(attachments);
    }

    protected void initializeRootMessage() throws IOException {
        String contentType = (String) message.get(Message.CONTENT_TYPE);

        if (contentType == null) {
            throw new IllegalStateException("Content-Type can not be empty!");
        }

        if (message.getContent(InputStream.class) == null) {
            throw new IllegalStateException("An InputStream must be provided!");
        }

        if (AttachmentUtil.isTypeSupported(contentType.toLowerCase(), supportedTypes)) {
            String boundaryString = findBoundaryFromContentType(contentType);
            if (null == boundaryString) {
                boundaryString = findBoundaryFromInputStream();
            }
            // If a boundary still wasn't found, throw an exception
            if (null == boundaryString) {
                throw new IOException("Couldn't determine the boundary from the message!");
            }
            boundary = boundaryString.getBytes(StandardCharsets.UTF_8);

            stream = new PushbackInputStream(message.getContent(InputStream.class), PUSHBACK_AMOUNT);
            if (!readTillFirstBoundary(stream, boundary)) {
                throw new IOException("Couldn't find MIME boundary: " + boundaryString);
            }

            Map<String, List<String>> ih = loadPartHeaders(stream);
            message.put(ATTACHMENT_PART_HEADERS, ih);
            String val = AttachmentUtil.getHeader(ih, "Content-Type", "; ");
            if (!StringUtils.isEmpty(val)) {
                String cs = HttpHeaderHelper.findCharset(val);
                if (!StringUtils.isEmpty(cs)) {
                    message.put(Message.ENCODING, HttpHeaderHelper.mapCharset(cs));
                }
            }
            val = AttachmentUtil.getHeader(ih, "Content-Transfer-Encoding");

            MimeBodyPartInputStream mmps = new MimeBodyPartInputStream(stream, boundary, PUSHBACK_AMOUNT);
            InputStream ins = AttachmentUtil.decode(mmps, val);
            if (ins != mmps) {
                ih.remove("Content-Transfer-Encoding");
            }
            body = new DelegatingInputStream(ins, this);
            createCount++;
            message.setContent(InputStream.class, body);
        }
    }

    private String findBoundaryFromContentType(String ct) {
        // Use regex to get the boundary and return null if it's not found
        Matcher m = CONTENT_TYPE_BOUNDARY_PATTERN.matcher(ct);
        return m.find() ? "--" + m.group(1) : null;
    }

    private String findBoundaryFromInputStream() throws IOException {
        InputStream is = message.getContent(InputStream.class);
        //boundary should definitely be in the first 2K;
        PushbackInputStream in = new PushbackInputStream(is, 4096);
        byte[] buf = new byte[2048];
        int i = in.read(buf);
        int len = i;
        while (i > 0 && len < buf.length) {
            i = in.read(buf, len, buf.length - len);
            if (i > 0) {
                len += i;
            }
        }
        String msg = IOUtils.newStringFromBytes(buf, 0, len);
        in.unread(buf, 0, len);

        // Reset the input stream since we'll need it again later
        message.setContent(InputStream.class, in);

        // Use regex to get the boundary and return null if it's not found
        Matcher m = INPUT_STREAM_BOUNDARY_PATTERN.matcher(msg);
        return m.find() ? "--" + m.group(1) : null;
    }

    public AttachmentImpl readNext() throws IOException {
        // Cache any mime parts that are currently being streamed
        cacheStreamedAttachments();
        if (closed) {
            return null;
        }

        int v = stream.read();
        if (v == -1) {
            return null;
        }
        stream.unread(v);

        Map<String, List<String>> headers = loadPartHeaders(stream);
        return (AttachmentImpl)createAttachment(headers);
    }

    private void cacheStreamedAttachments() throws IOException {
        if (body instanceof DelegatingInputStream
            && !((DelegatingInputStream) body).isClosed()) {

            cache((DelegatingInputStream) body);
        }

        List<Attachment> atts = new ArrayList<>(attachments.getLoadedAttachments());
        for (Attachment a : atts) {
            DataSource s = a.getDataHandler().getDataSource();
            if (s instanceof AttachmentDataSource) {
                AttachmentDataSource ads = (AttachmentDataSource)s;
                if (!ads.isCached()) {
                    ads.cache(message);
                }
            } else if (s.getInputStream() instanceof DelegatingInputStream) {
                cache((DelegatingInputStream) s.getInputStream());
            } else {
                //assume a normal stream that is already cached
            }
        }
    }

    private void cache(DelegatingInputStream input) throws IOException {
        if (loaded.contains(input)) {
            return;
        }
        loaded.add(input);
        InputStream origIn = input.getInputStream();
        try (CachedOutputStream out = new CachedOutputStream()) {
            AttachmentUtil.setStreamedAttachmentProperties(message, out);
            IOUtils.copy(input, out);
            input.setInputStream(out.getInputStream());
            origIn.close();
        }
    }

    /**
     * Move the read pointer to the begining of the first part read till the end
     * of first boundary
     *
     * @param pushbackInStream
     * @param boundary
     * @throws IOException
     */
    private static boolean readTillFirstBoundary(PushbackInputStream pushbackInStream,
        byte[] boundary) throws IOException {

        // work around a bug in PushBackInputStream where the buffer isn't
        // initialized
        // and available always returns 0.
        int value = pushbackInStream.read();
        pushbackInStream.unread(value);
        while (value != -1) {
            value = pushbackInStream.read();
            if ((byte) value == boundary[0]) {
                int boundaryIndex = 0;
                while (value != -1 && (boundaryIndex < boundary.length) && ((byte) value == boundary[boundaryIndex])) {

                    value = pushbackInStream.read();
                    if (value == -1) {
                        throw new IOException("Unexpected End while searching for first Mime Boundary");
                    }
                    boundaryIndex++;
                }
                if (boundaryIndex == boundary.length) {
                    // boundary found, read the newline
                    if (value == 13) {
                        pushbackInStream.read();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Create an Attachment from the MIME stream. If there is a previous attachment
     * that is not read, cache that attachment.
     *
     * @throws IOException
     */
    private Attachment createAttachment(Map<String, List<String>> headers) throws IOException {
        InputStream partStream =
            new DelegatingInputStream(new MimeBodyPartInputStream(stream, boundary, PUSHBACK_AMOUNT),
                                      this);
        createCount++;

        return AttachmentUtil.createAttachment(partStream, headers, message);
    }

    public boolean isLazyLoading() {
        return lazyLoading;
    }

    public void setLazyLoading(boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
    }

    public void markClosed(DelegatingInputStream delegatingInputStream) throws IOException {
        closedCount++;
        if (closedCount == createCount && !attachments.hasNext(false)) {
            int x = stream.read();
            while (x != -1) {
                x = stream.read();
            }
            stream.close();
            closed = true;
        }
    }
    /**
     *  Check for more attachment.
     *
     * @return whether there is more attachment or not.  It will not deserialize the next attachment.
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
        cacheStreamedAttachments();
        if (closed) {
            return false;
        }

        int v = stream.read();
        if (v == -1) {
            return false;
        }
        stream.unread(v);
        return true;
    }



    private Map<String, List<String>> loadPartHeaders(InputStream in) throws IOException {
        StringBuilder buffer = new StringBuilder(128);
        StringBuilder b = new StringBuilder(128);
        Map<String, List<String>> heads = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        // loop until we hit the end or a null line
        while (readLine(in, b)) {
            // lines beginning with white space get special handling
            char c = b.charAt(0);
            if (c == ' ' || c == '\t') {
                if (buffer.length() != 0) {
                    // preserve the line break and append the continuation
                    buffer.append("\r\n");
                    buffer.append(b);
                }
            } else {
                // if we have a line pending in the buffer, flush it
                if (buffer.length() > 0) {
                    addHeaderLine(heads, buffer);
                    buffer.setLength(0);
                }
                // add this to the accumulator
                buffer.append(b);
            }
        }

        // if we have a line pending in the buffer, flush it
        if (buffer.length() > 0) {
            addHeaderLine(heads, buffer);
        }
        return heads;
    }

    private boolean readLine(InputStream in, StringBuilder buffer) throws IOException {
        if (buffer.length() != 0) {
            buffer.setLength(0);
        }
        int c;

        while ((c = in.read()) != -1) {
            // a linefeed is a terminator, always.
            if (c == '\n') {
                break;
            } else if (c == '\r') {
                //just ignore the CR.  The next character SHOULD be an NL.  If not, we're
                //just going to discard this
                continue;
            } else {
                // just add to the buffer
                buffer.append((char)c);
            }

            if (buffer.length() > maxHeaderLength) {
                LOG.fine("The attachment header size has exceeded the configured parameter: " + maxHeaderLength);
                throw new HeaderSizeExceededException();
            }
        }

        // no characters found...this was either an eof or a null line.
        return buffer.length() != 0;
    }

    private void addHeaderLine(Map<String, List<String>> heads, StringBuilder line) {
        // null lines are a nop
        final int size = line.length();
        if (size == 0) {
            return;
        }
        int separator = line.indexOf(":");
        final String name;
        String value = "";
        if (separator == -1) {
            name = line.toString().trim();
        } else {
            name = line.substring(0, separator);
            // step past the separator.  Now we need to remove any leading white space characters.
            separator++;

            while (separator < size) {
                char ch = line.charAt(separator);
                if (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n') {
                    break;
                }
                separator++;
            }
            value = line.substring(separator);
        }
        List<String> v = heads.computeIfAbsent(name, k -> new ArrayList<>(1));
        v.add(value);
    }

}

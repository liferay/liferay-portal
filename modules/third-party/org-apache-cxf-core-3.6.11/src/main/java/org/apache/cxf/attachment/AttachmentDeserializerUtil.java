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

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;

final class AttachmentDeserializerUtil {
    /* Keep the log under AttachmentDeserializer */
    private static final Logger LOG = LogUtils.getL7dLogger(AttachmentDeserializer.class);

    private AttachmentDeserializerUtil() {
    }
    
    /**
     * Move the read pointer to the begining of the first part read till the end
     * of first boundary
     *
     * @param pushbackInStream
     * @param boundary
     * @throws IOException
     */
    static boolean readTillFirstBoundary(PushbackInputStream pushbackInStream,
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
                while (value != -1 
                    && boundaryIndex < boundary.length 
                    && (byte)value == boundary[boundaryIndex]) {

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


    static Map<String, List<String>> loadPartHeaders(InputStream in, int maxHeaderLength, 
            int maxHeadersCount) throws IOException {
        StringBuilder buffer = new StringBuilder(128);
        StringBuilder b = new StringBuilder(128);
        Map<String, List<String>> heads = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        // loop until we hit the end or a null line
        while (readLine(in, b, maxHeaderLength)) {
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
                    addHeaderLine(heads, buffer, maxHeadersCount);
                    buffer.setLength(0);
                }
                // add this to the accumulator
                buffer.append(b);
            }
        }

        // if we have a line pending in the buffer, flush it
        if (buffer.length() > 0) {
            addHeaderLine(heads, buffer, maxHeadersCount);
        }
        return heads;
    }

    private static boolean readLine(InputStream in, StringBuilder buffer, int maxHeaderLength) throws IOException {
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

    private static void addHeaderLine(Map<String, List<String>> heads, StringBuilder line, 
            int maxHeadersCount) throws IOException {
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
        
        if (heads.size() >= maxHeadersCount) {
            throw new IOException("The attachment contains more headers than are permitted");
        }
        List<String> v = heads.computeIfAbsent(name, k -> new ArrayList<>(1));
        v.add(value);
    }


}
/* @generated */

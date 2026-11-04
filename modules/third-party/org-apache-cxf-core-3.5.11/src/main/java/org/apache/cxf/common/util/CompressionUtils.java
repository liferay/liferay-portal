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
package org.apache.cxf.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class CompressionUtils {
    // Guards against decompression-bomb style inputs that expand to an unbounded size on the heap
    public static final long DEFAULT_MAX_INFLATED_SIZE =
        SystemPropertyAction.getInteger("org.apache.cxf.compression-max-inflated-size", 10 * 1024 * 1024);

    private CompressionUtils() {

    }
    public static InputStream inflate(byte[] deflatedToken)
        throws DataFormatException {
        return inflate(deflatedToken, true);
    }
    public static InputStream inflate(byte[] deflatedToken, boolean nowrap)
        throws DataFormatException {
        return inflate(deflatedToken, nowrap, DEFAULT_MAX_INFLATED_SIZE);
    }
    public static InputStream inflate(byte[] deflatedToken, boolean nowrap, long maxInflatedSize)
        throws DataFormatException {
        Inflater inflater = new Inflater(nowrap);
        inflater.setInput(deflatedToken);

        byte[] buffer = new byte[deflatedToken.length];
        int inflateLen;
        long totalInflated = 0;
        ByteArrayOutputStream inflatedToken = new ByteArrayOutputStream();
        while (!inflater.finished()) {
            inflateLen = inflater.inflate(buffer, 0, deflatedToken.length);
            if (inflateLen == 0 && !inflater.finished()) {
                if (inflater.needsInput()) {
                    throw new DataFormatException("Inflater can not inflate all the token bytes");
                }
                break;
            }

            totalInflated += inflateLen;
            if (totalInflated > maxInflatedSize) {
                throw new DataFormatException("Inflated data exceeds the maximum allowed size of "
                    + maxInflatedSize + " bytes");
            }

            inflatedToken.write(buffer, 0, inflateLen);
        }

        return new ByteArrayInputStream(inflatedToken.toByteArray());
    }

    public static byte[] deflate(byte[] tokenBytes) {
        return deflate(tokenBytes, true);
    }

    public static byte[] deflate(byte[] tokenBytes, boolean nowrap) {
        return deflate(tokenBytes, Deflater.DEFLATED, nowrap);
    }

    public static byte[] deflate(byte[] tokenBytes, int level, boolean nowrap) {
        Deflater compresser = new Deflater(level, nowrap);

        compresser.setInput(tokenBytes);
        compresser.finish();

        int tokenBytesLength = tokenBytes.length;
        byte[] output = new byte[Math.addExact(tokenBytesLength, tokenBytesLength)];

        int compressedDataLength = compresser.deflate(output);

        byte[] result = new byte[compressedDataLength];
        System.arraycopy(output, 0, result, 0, compressedDataLength);
        return result;
    }
}
/* @generated */

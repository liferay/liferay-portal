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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.InputSource;

public abstract class AbstractWrapperWSDLLocator implements WSDLLocator {
    private static final List<String> ALLOWED_SCHEMES;

    static {
        List<String> schemes = Arrays.asList("file", "http", "https", "classpath", "jar");
        ALLOWED_SCHEMES = Collections.unmodifiableList(schemes);
    }

    protected WSDLLocator parent;
    String wsdlUrl;
    InputSource last;
    String baseUri;
    String lastImport;
    boolean fromParent;

    public AbstractWrapperWSDLLocator(String wsdlUrl,
                                      WSDLLocator parent) {
        this.wsdlUrl = wsdlUrl;
        this.parent = parent;
    }

    public void close() {
        if (!fromParent) {
            try {
                if (last.getByteStream() != null) {
                    last.getByteStream().close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
        parent.close();
    }

    public abstract InputSource getInputSource();
    public abstract InputSource getInputSource(String parentLocation, String importLocation);

    public InputSource getBaseInputSource() {
        InputSource is = parent.getBaseInputSource();
        fromParent = true;
        if (is == null) {
            is = getInputSource();
            fromParent = false;
        } else {
            baseUri = is.getSystemId();
        }
        last = is;

        return is;
    }

    public String getBaseURI() {
        if (last == null) {
            getBaseInputSource();
            try {
                if (last.getByteStream() != null) {
                    last.getByteStream().close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
        return baseUri;
    }

    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        // Do a check on the scheme to see if it's anything that could be a security risk
        try {
            URI url = new URI(importLocation);
            if (!(url.getScheme() == null || ALLOWED_SCHEMES.contains(url.getScheme()))) {
                throw new IllegalArgumentException("The " + url.getScheme() + " URI scheme is not allowed");
            }
        } catch (URISyntaxException e) {
            // Just continue here as we might still be able to load it from the filesystem
        }

        InputSource src = parent.getImportInputSource(parentLocation, importLocation);
        lastImport = null;
        if (src == null || (src.getByteStream() == null && src.getCharacterStream() == null)) {
            src = getInputSource(parentLocation, importLocation);
            if (src != null) {
                lastImport = src.getSystemId();
            }
        }
        return src;
    }

    public String getLatestImportURI() {
        if (lastImport != null) {
            return lastImport;
        }
        return parent.getLatestImportURI();
    }

}

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
package org.apache.cxf.rs.security.oauth2.grants.code;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.jaxrs.json.basic.JsonMapObjectReaderWriter;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.AuthorizationRequestFilter;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJoseJwtConsumer;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rt.security.crypto.CryptoUtils;

public class JwtRequestCodeFilter extends OAuthJoseJwtConsumer implements AuthorizationRequestFilter {
    protected static final Logger LOG = LogUtils.getL7dLogger(JwtRequestCodeFilter.class);
    private static final String REQUEST_URI_CONTENT_TYPE = "application/oauth-authz-req+jwt";
    private static final String REQUEST_PARAM = "request";
    private static final String REQUEST_URI_PARAM = "request_uri";

    private boolean verifyWithClientCertificates;
    private String issuer;
    private JsonMapObjectReaderWriter jsonHandler = new JsonMapObjectReaderWriter();

    @Override
    public MultivaluedMap<String, String> process(MultivaluedMap<String, String> params,
                                                  UserSubject endUser,
                                                  Client client) {
        String requestToken = params.getFirst(REQUEST_PARAM);
        String requestUri = params.getFirst(REQUEST_URI_PARAM);

        if (requestToken == null) {
            if (isRequestUriValid(client, requestUri)) {
                requestToken = WebClient.create(requestUri).accept(REQUEST_URI_CONTENT_TYPE).get(String.class);
            }
        } else if (requestUri != null) {
            LOG.warning("It is not valid to specify both a request and request_uri value");
            throw new SecurityException();
        }

        if (requestToken != null) {
            JweDecryptionProvider theDecryptor = super.getInitializedDecryptionProvider(client.getClientSecret());
            JwsSignatureVerifier theSigVerifier = getInitializedSigVerifier(client);
            JwtToken jwt = getJwtToken(requestToken, theDecryptor, theSigVerifier);
            JwtClaims claims = jwt.getClaims();

            // Check issuer
            String iss = issuer != null ? issuer : client.getClientId();
            if (!iss.equals(claims.getIssuer())) {
                throw new SecurityException();
            }

            // Check client_id - if present it must match the client_id specified in the request
            if (claims.getClaim(OAuthConstants.CLIENT_ID) != null
                && !claims.getStringProperty(OAuthConstants.CLIENT_ID).equals(client.getClientId())) {
                throw new SecurityException();
            }

            // Check response_type - if present it must match the response_type specified in the request
            String tokenResponseType = (String)claims.getClaim(OAuthConstants.RESPONSE_TYPE);
            if (tokenResponseType != null
                && !tokenResponseType.equals(params.getFirst(OAuthConstants.RESPONSE_TYPE))) {
                throw new SecurityException();
            }

            MultivaluedMap<String, String> newParams = new MetadataMap<String, String>(params);
            Map<String, Object> claimsMap = claims.asMap();
            for (Map.Entry<String, Object> entry : claimsMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Map) {
                    Map<String, Object> map = CastUtils.cast((Map<?, ?>)value);
                    value = jsonHandler.toJson(map);
                } else if (value instanceof List) {
                    List<Object> list = CastUtils.cast((List<?>)value);
                    value = jsonHandler.toJson(list);
                }
                newParams.putSingle(key, value.toString());
            }
            return newParams;
        }
        return params;
    }
    /**
     * This method must be overridden to support request_uri. Take care to validate the request_uri properly,
     * as otherwise it could lead to a security problem
     * (https://tools.ietf.org/html/draft-ietf-oauth-jwsreq-30#section-10.4)
     * @param client the Client object
     * @param requestUri the request_uri parameter to validate
     * @return whether the requestUri is permitted or not
     */
    protected boolean isRequestUriValid(Client client, String requestUri) {
        return false;
    }
    protected JwsSignatureVerifier getInitializedSigVerifier(Client c) {
        if (verifyWithClientCertificates) {
            X509Certificate cert =
                (X509Certificate)CryptoUtils.decodeCertificate(c.getApplicationCertificates().get(0));
            return JwsUtils.getPublicKeySignatureVerifier(cert, SignatureAlgorithm.RS256);
        }
        return super.getInitializedSignatureVerifier(c.getClientSecret());
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    public void setVerifyWithClientCertificates(boolean verifyWithClientCertificates) {
        this.verifyWithClientCertificates = verifyWithClientCertificates;
    }

}
/* @generated */
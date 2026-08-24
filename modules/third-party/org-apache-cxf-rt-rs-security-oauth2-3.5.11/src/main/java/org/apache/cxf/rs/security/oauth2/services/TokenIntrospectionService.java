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
package org.apache.cxf.rs.security.oauth2.services;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.rs.security.jose.jwt.JoseJwtConsumer;
import org.apache.cxf.rs.security.jose.jwt.JwtException;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.TokenIntrospection;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

@Path("introspect")
public class TokenIntrospectionService {
    private static final Logger LOG = LogUtils.getL7dLogger(TokenIntrospectionService.class);
    private boolean blockUnsecureRequests;
    private boolean blockUnauthorizedRequests = true;
    private boolean reportExtraTokenProperties = true;
    private MessageContext mc;
    private OAuthDataProvider dataProvider;
    private JoseJwtConsumer jwtTokenConsumer;
    private boolean persistJwtEncoding = true;

    @POST
    @Produces({MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TokenIntrospection getTokenIntrospection(@Encoded MultivaluedMap<String, String> params) {
        checkSecurityContext();
        String tokenId = params.getFirst(OAuthConstants.TOKEN_ID);
        if (!persistJwtEncoding) {
            try {
                JoseJwtConsumer theConsumer = jwtTokenConsumer == null ? new JoseJwtConsumer() : jwtTokenConsumer;
                JwtToken token = theConsumer.getJwtToken(tokenId);
                tokenId = token.getClaims().getTokenId();
            } catch (JwtException ex) {
                return new TokenIntrospection(false);
            }
        }

        ServerAccessToken at = dataProvider.getAccessToken(tokenId);
        if (at == null || OAuthUtils.isExpired(at.getIssuedAt(), at.getExpiresIn())) {
            return new TokenIntrospection(false);
        }
        TokenIntrospection response = new TokenIntrospection(true);
        response.setClientId(at.getClient().getClientId());
        if (!at.getScopes().isEmpty()) {
            response.setScope(OAuthUtils.convertPermissionsToScope(at.getScopes()));
        }
        UserSubject userSubject = at.getSubject();
        if (userSubject != null) {
            response.setUsername(at.getSubject().getLogin());
            if (userSubject.getId() != null) {
                response.setSub(userSubject.getId());
            }
        }
        if (!StringUtils.isEmpty(at.getAudiences())) {
            response.setAud(at.getAudiences());
        }
        if (at.getIssuer() != null) {
            response.setIss(at.getIssuer());
        }

        response.setIat(at.getIssuedAt());
        if (at.getExpiresIn() > 0) {
            response.setExp(at.getIssuedAt() + at.getExpiresIn());
        }
        if (at.getNotBefore() > 0) {
            response.setNbf(at.getNotBefore());
        }
        response.setTokenType(at.getTokenType());

        if (reportExtraTokenProperties) {
            response.getExtensions().putAll(at.getExtraProperties());
        }

        return response;
    }

    private void checkSecurityContext() {
        SecurityContext sc = mc.getSecurityContext();
        if (!sc.isSecure() && blockUnsecureRequests) {
            LOG.warning("Unsecure HTTP, Transport Layer Security is recommended");
            ExceptionUtils.toNotAuthorizedException(null,  null);
        }
        if (sc.getUserPrincipal() == null && blockUnauthorizedRequests) {
            LOG.warning("Authenticated Principal is not available");
            ExceptionUtils.toNotAuthorizedException(null, null);
        }

    }

    public void setBlockUnsecureRequests(boolean blockUnsecureRequests) {
        this.blockUnsecureRequests = blockUnsecureRequests;
    }

    public void setBlockUnauthorizedRequests(boolean blockUnauthorizedRequests) {
        this.blockUnauthorizedRequests = blockUnauthorizedRequests;
    }

    public void setDataProvider(OAuthDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Context
    public void setMessageContext(MessageContext context) {
        this.mc = context;
    }

    public void setReportExtraTokenProperties(boolean reportExtraTokenProperties) {
        this.reportExtraTokenProperties = reportExtraTokenProperties;
    }

    public JoseJwtConsumer getJwtTokenConsumer() {
        return jwtTokenConsumer;
    }

    public void setJwtTokenConsumer(JoseJwtConsumer jwtTokenConsumer) {
        this.jwtTokenConsumer = jwtTokenConsumer;
    }

    public boolean isPersistJwtEncoding() {
        return persistJwtEncoding;
    }

    public void setPersistJwtEncoding(boolean persistJwtEncoding) {
        this.persistJwtEncoding = persistJwtEncoding;
    }

}

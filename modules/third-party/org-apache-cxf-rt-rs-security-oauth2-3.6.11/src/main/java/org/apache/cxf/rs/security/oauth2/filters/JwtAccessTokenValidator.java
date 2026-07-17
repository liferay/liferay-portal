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
package org.apache.cxf.rs.security.oauth2.filters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwt.JoseJwtConsumer;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtConstants;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenValidator;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.JwtTokenUtils;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

public class JwtAccessTokenValidator extends JoseJwtConsumer implements AccessTokenValidator {

    private static final String USERNAME_PROP = "username";

    private Map<String, String> jwtAccessTokenClaimMap;

    public List<String> getSupportedAuthorizationSchemes() {
        return Collections.singletonList(OAuthConstants.BEARER_AUTHORIZATION_SCHEME);
    }

    public AccessTokenValidation validateAccessToken(MessageContext mc,
                                                     String authScheme,
                                                     String authSchemeData,
                                                     MultivaluedMap<String, String> extraProps)
        throws OAuthServiceException {
        try {
            JwtToken token = super.getJwtToken(authSchemeData);
            return convertClaimsToValidation(token.getClaims());
        } catch (Exception ex) {
            throw new OAuthServiceException(ex);
        }
    }


    private AccessTokenValidation convertClaimsToValidation(JwtClaims claims) {
        AccessTokenValidation atv = new AccessTokenValidation();
        atv.setInitialValidationSuccessful(true);
        String clientId = claims.getStringProperty(OAuthConstants.CLIENT_ID);
        if (clientId != null) {
            atv.setClientId(clientId);
        }
        if (claims.getIssuedAt() != null) {
            atv.setTokenIssuedAt(claims.getIssuedAt());
        } else {
            atv.setTokenIssuedAt(OAuthUtils.getIssuedAt());
        }
        if (claims.getExpiryTime() != null) {
            atv.setTokenLifetime(claims.getExpiryTime() - atv.getTokenIssuedAt());
        }
        List<String> audiences = claims.getAudiences();
        if (audiences != null && !audiences.isEmpty()) {
            atv.setAudiences(claims.getAudiences());
        }
        if (claims.getIssuer() != null) {
            atv.setTokenIssuer(claims.getIssuer());
        }
        if (claims.getNotBefore() != null) {
            atv.setTokenNotBefore(claims.getNotBefore());
        }
        Object scope = claims.getClaim(OAuthConstants.SCOPE);
        if (scope != null) {
            String[] scopes = scope instanceof String
                ? scope.toString().split(" ") : CastUtils.cast((List<?>)scope).toArray(new String[]{});
            List<OAuthPermission> perms = new LinkedList<>();
            for (String s : scopes) {
                if (!StringUtils.isEmpty(s)) {
                    perms.add(new OAuthPermission(s.trim()));
                }
            }
            atv.setTokenScopes(perms);
        }
        String usernameClaimName =
            JwtTokenUtils.getClaimName(USERNAME_PROP, USERNAME_PROP, jwtAccessTokenClaimMap);
        String username = claims.getStringProperty(usernameClaimName);
        if (username != null) {
            UserSubject userSubject = new UserSubject(username);
            if (claims.getSubject() != null) {
                userSubject.setId(claims.getSubject());
            }
            atv.setTokenSubject(userSubject);
        } else if (claims.getSubject() != null) {
            atv.setTokenSubject(new UserSubject(claims.getSubject()));
        }
        Map<String, String> extraProperties = CastUtils.cast((Map<?, ?>)claims.getClaim("extra_properties"));
        if (extraProperties != null) {
            atv.getExtraProps().putAll(extraProperties);
        }

        Map<String, Object> cnfClaim = CastUtils.cast((Map<?, ?>)claims.getClaim(JwtConstants.CLAIM_CONFIRMATION));
        if (cnfClaim != null) {
            Object certCnf = cnfClaim.get(JoseConstants.HEADER_X509_THUMBPRINT_SHA256);
            if (certCnf != null) {
                atv.getExtraProps().put(JoseConstants.HEADER_X509_THUMBPRINT_SHA256, certCnf.toString());
            }
        }

        return atv;
    }

    public void setJwtAccessTokenClaimMap(Map<String, String> jwtAccessTokenClaimMap) {
        this.jwtAccessTokenClaimMap = jwtAccessTokenClaimMap;
    }

}

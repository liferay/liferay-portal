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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

/**
 * OAuth2 Token Revocation Service implementation
 */
@Path("/revoke")
public class TokenRevocationService extends AbstractTokenService {

    /**
     * Processes a token revocation request
     * @param params the form parameters representing the access token grant
     * @return Access Token or the error
     */
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public Response handleTokenRevocation(MultivaluedMap<String, String> params) {

        // Make sure the client is authenticated
        Client client = authenticateClientIfNeeded(params);
        String token = params.getFirst(OAuthConstants.TOKEN_ID);
        if (token == null) {
            return createErrorResponse(params, OAuthConstants.UNSUPPORTED_TOKEN_TYPE);
        }
        String tokenTypeHint = params.getFirst(OAuthConstants.TOKEN_TYPE_HINT);
        if (tokenTypeHint != null
            && !OAuthConstants.ACCESS_TOKEN.equals(tokenTypeHint)
            && !OAuthConstants.REFRESH_TOKEN.equals(tokenTypeHint)) {
            return createErrorResponseFromErrorCode(OAuthConstants.UNSUPPORTED_TOKEN_TYPE);
        }
        try {
            getDataProvider().revokeToken(client, getCallerUserSubject(client), token, tokenTypeHint);
        } catch (OAuthServiceException ex) {
            // Spec: The authorization server responds with HTTP status code 200 if the
            // token has been revoked successfully or if the client submitted an
            // invalid token
        }
        return Response.ok().build();
    }

    /**
     * Resolves the resource owner identity from the JAX-RS security context.
     * <p>
     * In standard OAuth2 flows the revocation endpoint is authenticated by the
     * <em>client</em> (HTTP Basic Auth, client_secret_post, mTLS, …), so
     * {@code SecurityContext.getUserPrincipal()} normally carries the client's own
     * name, not an end-user identity.  We must not mistake the client principal for
     * a resource-owner subject, or we would block every legitimate client-initiated
     * revocation (e.g. the canonical authorization-code flow where the client
     * revokes its own token on logout).
     * <p>
     * A non-null {@link UserSubject} is returned only when a distinct end-user
     * principal is present — i.e. the principal name differs from the authenticated
     * client's {@code clientId}.  In that case the data provider will verify that
     * the token belongs to that end-user (IDOR guard).  When the caller is the
     * client itself, {@code null} is returned and only the client-level check applies.
     */
    private UserSubject getCallerUserSubject(Client client) {
        SecurityContext sc = getMessageContext().getSecurityContext();
        java.security.Principal principal = sc.getUserPrincipal();
        if (principal == null || principal.getName() == null) {
            return null;
        }
        // Principal name matches the client id → this is the client authenticating
        // at the token endpoint, not an independent resource-owner identity.
        if (principal.getName().equals(client.getClientId())) {
            return null;
        }
        return new UserSubject(principal.getName());
    }
}
/* @generated */

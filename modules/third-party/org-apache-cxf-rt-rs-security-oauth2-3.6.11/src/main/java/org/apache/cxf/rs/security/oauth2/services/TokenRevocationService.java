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

import org.apache.cxf.rs.security.oauth2.common.Client;
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
            getDataProvider().revokeToken(client, token, tokenTypeHint);
        } catch (OAuthServiceException ex) {
            // Spec: The authorization server responds with HTTP status code 200 if the
            // token has been revoked successfully or if the client submitted an
            // invalid token
        }
        return Response.ok().build();
    }
}
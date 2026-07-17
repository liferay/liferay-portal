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

package org.apache.cxf.rs.security.oauth2.provider;

import java.util.List;

import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;

/**
 * OAuth provider responsible for persisting the information about
 * OAuth consumers, request and access tokens.
 */
public interface OAuthDataProvider {


    /**
     * Returns the previously registered third-party {@link Client}
     * @param clientId the client id
     * @return Client
     * @throws OAuthServiceException
     */
    Client getClient(String clientId) throws OAuthServiceException;

    /**
     * Create access token
     * @param accessToken the token registration info
     * @return AccessToken
     * @throws OAuthServiceException
     */
    ServerAccessToken createAccessToken(AccessTokenRegistration accessToken) throws OAuthServiceException;

    /**
     * Get access token
     * @param accessToken the token key
     * @return AccessToken
     * @throws OAuthServiceException
     */
    ServerAccessToken getAccessToken(String accessToken) throws OAuthServiceException;

    /**
     * Get preauthorized access token
     * @param client Client
     * @param requestedScopes the scopes requested by the client
     * @param subject End User subject
     * @return AccessToken access token
     * @throws OAuthServiceException
     */
    ServerAccessToken getPreauthorizedToken(Client client,
                                            List<String> requestedScopes,
                                            UserSubject subject,
                                            String grantType)
        throws OAuthServiceException;

    /**
     * Refresh access token
     * @param client the client
     * @param refreshToken refresh token key
     * @param requestedScopes the scopes requested by the client
     * @return AccessToken
     * @throws OAuthServiceException
     */
    ServerAccessToken refreshAccessToken(Client client,
                                         String refreshToken,
                                         List<String> requestedScopes)
        throws OAuthServiceException;

    /**
     * Return all access tokens associated with a given client
     * @param client the client
     * @param subject the user subject, can be null
     * @return list of access tokens
     * @throws OAuthServiceException
     */
    List<ServerAccessToken> getAccessTokens(Client client, UserSubject subject) throws OAuthServiceException;

    /**
     * Return all refresh tokens associated with a given client
     * @param client the client
     * @param subject the user subject, can be null
     * @return list of refresh tokens
     * @throws OAuthServiceException
     */
    List<RefreshToken> getRefreshTokens(Client client, UserSubject subject) throws OAuthServiceException;

    /**
     * Revokes a refresh or access token
     * @param client the client
     * @param tokenId token identifier
     * @param tokenTypeHint can be access_token or refresh_token or null
     * @throws OAuthServiceException
     */
    void revokeToken(Client client, String tokenId, String tokenTypeHint) throws OAuthServiceException;

    /**
     * Converts the requested scopes to the list of permissions.
     * The scopes are extracted from OAuth2 'scope' property which
     * if set may contain one or more space separated scope values
     *
     * @param requestedScopes the scopes
     * @return list of permissions
     */
    List<OAuthPermission> convertScopeToPermissions(Client client,
                                                    List<String> requestedScopes);
}

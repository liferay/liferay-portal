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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtConstants;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.JwtTokenUtils;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

public abstract class AbstractOAuthDataProvider implements OAuthDataProvider, ClientRegistrationProvider {
    private long accessTokenLifetime = 3600L;
    private long refreshTokenLifetime; // refresh tokens are eternal by default
    private boolean recycleRefreshTokens = true;
    private Object refreshTokenLock;
    private Map<String, OAuthPermission> permissionMap = new HashMap<>();
    private MessageContext messageContext;
    private List<String> defaultScopes;
    private List<String> requiredScopes;
    private List<String> invisibleToClientScopes;
    private boolean supportPreauthorizedTokens;

    private boolean useJwtFormatForAccessTokens;
    private boolean persistJwtEncoding = true;
    private OAuthJoseJwtProducer jwtAccessTokenProducer;
    private Map<String, String> jwtAccessTokenClaimMap;
    private ProviderAuthenticationStrategy authenticationStrategy;
    private String issuer;

    protected AbstractOAuthDataProvider() {
    }

    @Override
    public ServerAccessToken createAccessToken(AccessTokenRegistration reg)
        throws OAuthServiceException {
        ServerAccessToken at = doCreateAccessToken(reg);
        saveAccessToken(at);
        if (isRefreshTokenSupported(reg.getApprovedScope())) {
            createNewRefreshToken(at);
        }
        return at;
    }

    protected ServerAccessToken doCreateAccessToken(AccessTokenRegistration atReg) {
        ServerAccessToken at = doCreateAccessToken(
            atReg.getAudiences(), atReg.getClient(),
            atReg.getClientCodeVerifier(), atReg.getExtraProperties(),
            atReg.getGrantCode(), atReg.getGrantType(), atReg.getNonce(),
            atReg.getResponseType(),
            convertScopeToPermissions(
                    atReg.getClient(), atReg.getApprovedScope()),
            atReg.getSubject());

        if (messageContext != null) {
            String certCnf = (String)messageContext.get(JoseConstants.HEADER_X509_THUMBPRINT_SHA256);
            if (certCnf != null) {
                // At a later stage we will likely introduce a dedicated Confirmation bean (as it is used in POP etc)
                at.getExtraProperties().put(JoseConstants.HEADER_X509_THUMBPRINT_SHA256, certCnf);
            }
        }

        if (isUseJwtFormatForAccessTokens()) {
            convertToJWTAccessToken(at);
        }

        return at;
    }

    //CHECKSTYLE:OFF
    protected ServerAccessToken doCreateAccessToken(List<String> audiences, //NOPMD
                                                    Client client,
                                                    String clientCodeVerifier,
                                                    Map<String, String> extraProperties,
                                                    String grantCode,
                                                    String grantType,
                                                    String nonce,
                                                    String responseType,
                                                    List<OAuthPermission> scopes,
                                                    UserSubject userSubject) {
    //CHECKSTYLE:ON

        ServerAccessToken at =
            createNewAccessToken(client, userSubject);
        at.setAudiences(audiences);
        at.setGrantType(grantType);
        at.setScopes(scopes);
        at.setSubject(userSubject);
        at.setClientCodeVerifier(clientCodeVerifier);
        at.setNonce(nonce);
        at.setResponseType(responseType);
        at.setGrantCode(grantCode);
        at.getExtraProperties().putAll(extraProperties);

        return at;
    }

    protected JwtClaims createJwtAccessToken(ServerAccessToken at) {
        JwtClaims claims = new JwtClaims();
        claims.setTokenId(at.getTokenKey());

        // 'client_id' or 'cid', default client_id
        String clientIdClaimName =
            JwtTokenUtils.getClaimName(OAuthConstants.CLIENT_ID, OAuthConstants.CLIENT_ID,
                                             getJwtAccessTokenClaimMap());
        claims.setClaim(clientIdClaimName, at.getClient().getClientId());
        claims.setIssuedAt(at.getIssuedAt());
        if (at.getExpiresIn() > 0) {
            claims.setExpiryTime(at.getIssuedAt() + at.getExpiresIn());
        }
        UserSubject userSubject = at.getSubject();
        if (userSubject != null) {
            if (userSubject.getId() != null) {
                claims.setSubject(userSubject.getId());
            }

            // 'username' by default to be consistent with the token introspection response
            final String usernameProp = "username";
            String usernameClaimName =
                JwtTokenUtils.getClaimName(usernameProp, usernameProp, getJwtAccessTokenClaimMap());
            claims.setClaim(usernameClaimName, userSubject.getLogin());
        }
        if (at.getIssuer() != null) {
            claims.setIssuer(at.getIssuer());
        }
        if (!at.getScopes().isEmpty()) { // rfc8693, section 4.2
            claims.setClaim(OAuthConstants.SCOPE,
                OAuthUtils.convertListOfScopesToString(OAuthUtils.convertPermissionsToScopeList(at.getScopes())));
        }
        // OAuth2 resource indicators (resource server audience)
        if (!at.getAudiences().isEmpty()) {
            List<String> resourceAudiences = at.getAudiences();
            if (resourceAudiences.size() == 1) {
                claims.setAudience(resourceAudiences.get(0));
            } else {
                claims.setAudiences(resourceAudiences);
            }
        }
        if (!at.getExtraProperties().isEmpty()) {
            Map<String, String> actualExtraProps = new HashMap<>();
            for (Map.Entry<String, String> entry : at.getExtraProperties().entrySet()) {
                if (JoseConstants.HEADER_X509_THUMBPRINT_SHA256.equals(entry.getKey())) {
                    claims.setClaim(JwtConstants.CLAIM_CONFIRMATION,
                        Collections.singletonMap(JoseConstants.HEADER_X509_THUMBPRINT_SHA256,
                                                 entry.getValue()));
                } else {
                    actualExtraProps.put(entry.getKey(), entry.getValue());
                }
            }
            claims.setClaim("extra_properties", actualExtraProps);
        }
        // Can be used to check at RS/etc which grant was used to get this token issued
        if (at.getGrantType() != null) {
            claims.setClaim(OAuthConstants.GRANT_TYPE, at.getGrantType());
        }
        // Can be used to check the original code grant value which was removed from the storage
        // (and is no longer valid) when this token was issued; relevant only if the authorization
        // code flow was used
        if (at.getGrantCode() != null) {
            claims.setClaim(OAuthConstants.AUTHORIZATION_CODE_GRANT, at.getGrantCode());
        }
        // Can be used to link the clients (especially public ones) to this token
        // to have a knowledge which client instance is using this token - might be handy at the RS/etc
        if (at.getClientCodeVerifier() != null) {
            claims.setClaim(OAuthConstants.AUTHORIZATION_CODE_VERIFIER, at.getClientCodeVerifier());
        }
        if (at.getNonce() != null) {
            claims.setClaim(OAuthConstants.NONCE, at.getNonce());
        }
        return claims;
    }

    protected void convertToJWTAccessToken(ServerAccessToken at) {
        JwtClaims claims = createJwtAccessToken(at);
        String jose = processJwtAccessToken(claims);
        if (isPersistJwtEncoding()) {
            at.setTokenKey(jose);
        } else {
            at.setEncodedToken(jose);
        }
    }

    protected ServerAccessToken createNewAccessToken(Client client, UserSubject userSub) {
        BearerAccessToken token = new BearerAccessToken(client, accessTokenLifetime);
        if (getIssuer() != null) {
            token.setIssuer(getIssuer());
        }
        return token;
    }

    @Override
    public ServerAccessToken refreshAccessToken(Client client, String refreshTokenKey,
                                                List<String> restrictedScopes) throws OAuthServiceException {
        RefreshToken currentRefreshToken = recycleRefreshTokens
            ? revokeRefreshToken(client, refreshTokenKey) : getRefreshToken(refreshTokenKey);
        if (currentRefreshToken == null) {
            throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
        }
        if (OAuthUtils.isExpired(currentRefreshToken.getIssuedAt(), currentRefreshToken.getExpiresIn())) {
            if (!recycleRefreshTokens) {
                revokeRefreshToken(client, refreshTokenKey);
            }
            throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
        }
        if (recycleRefreshTokens) {
            revokeAccessTokens(client, currentRefreshToken);
        }

        ServerAccessToken at = doRefreshAccessToken(client, currentRefreshToken, restrictedScopes);
        saveAccessToken(at);
        if (recycleRefreshTokens) {
            createNewRefreshToken(at);
        } else {
            updateExistingRefreshToken(currentRefreshToken, at);
        }
        return at;
    }

    @Override
    public void revokeToken(Client client, String tokenKey, String tokenTypeHint) throws OAuthServiceException {
        ServerAccessToken accessToken = null;
        if (!OAuthConstants.REFRESH_TOKEN.equals(tokenTypeHint)) {
            accessToken = revokeAccessToken(client, tokenKey);
        }
        if (accessToken != null) {
            handleLinkedRefreshToken(client, accessToken);
        } else if (!OAuthConstants.ACCESS_TOKEN.equals(tokenTypeHint)) {
            RefreshToken currentRefreshToken = revokeRefreshToken(client, tokenKey);
            revokeAccessTokens(client, currentRefreshToken);
        }
    }

    protected void handleLinkedRefreshToken(Client client, ServerAccessToken accessToken) {
        if (accessToken != null && accessToken.getRefreshToken() != null) {
            RefreshToken rt = getRefreshToken(accessToken.getRefreshToken());
            if (rt == null) {
                return;
            }

            unlinkRefreshAccessToken(rt, accessToken.getTokenKey());
            if (rt.getAccessTokens().isEmpty()) {
                revokeRefreshToken(client, rt.getTokenKey());
            } else {
                saveRefreshToken(rt);
            }
        }

    }

    protected void revokeAccessTokens(Client client, RefreshToken currentRefreshToken) {
        if (currentRefreshToken != null) {
            for (String accessTokenKey : currentRefreshToken.getAccessTokens()) {
                revokeAccessToken(client, accessTokenKey);
            }
        }
    }

    protected void unlinkRefreshAccessToken(RefreshToken rt, String tokenKey) {
        List<String> accessTokenKeys = rt.getAccessTokens();
        for (int i = 0; i < accessTokenKeys.size(); i++) {
            if (accessTokenKeys.get(i).equals(tokenKey)) {
                accessTokenKeys.remove(i);
                break;
            }
        }
    }



    @Override
    public List<OAuthPermission> convertScopeToPermissions(Client client, List<String> requestedScopes) {
        checkRequestedScopes(client, requestedScopes);
        if (requestedScopes.isEmpty()) {
            return Collections.emptyList();
        }
        List<OAuthPermission> list = new ArrayList<>();
        for (String scope : requestedScopes) {
            convertSingleScopeToPermission(client, scope, list);
        }
        if (!list.isEmpty()) {
            return list;
        }
        throw new OAuthServiceException("Requested scopes can not be mapped");

    }

    protected void checkRequestedScopes(Client client, List<String> requestedScopes) {
        if (requiredScopes != null && !requestedScopes.containsAll(requiredScopes)) {
            throw new OAuthServiceException("Required scopes are missing");
        }
    }

    protected void convertSingleScopeToPermission(Client client,
                                                  String scope,
                                                  List<OAuthPermission> perms) {
        OAuthPermission permission = permissionMap.get(scope);
        if (permission == null) {
            throw new OAuthServiceException("Unexpected scope: " + scope);
        }
        perms.add(permission);
    }

    @Override
    public ServerAccessToken getPreauthorizedToken(Client client,
                                                   List<String> requestedScopes,
                                                   UserSubject sub,
                                                   String grantType) throws OAuthServiceException {
        if (!isSupportPreauthorizedTokens()) {
            return null;
        }

        ServerAccessToken token = null;
        for (ServerAccessToken at : getAccessTokens(client, sub)) {
            if (at.getClient().getClientId().equals(client.getClientId())
                && at.getGrantType().equals(grantType)
                && (sub == null && at.getSubject() == null
                || sub != null && at.getSubject().getLogin().equals(sub.getLogin()))) {
                if (!OAuthUtils.isExpired(at.getIssuedAt(), at.getExpiresIn())) {
                    token = at;
                } else {
                    revokeToken(client, at.getTokenKey(), OAuthConstants.ACCESS_TOKEN);
                }
                break;
            }
        }
        return token;

    }

    protected boolean isRefreshTokenSupported(List<String> theScopes) {
        return theScopes.contains(OAuthConstants.REFRESH_TOKEN_SCOPE);
    }

    protected String getCurrentRequestedGrantType() {
        return messageContext != null ? (String)messageContext.get(OAuthConstants.GRANT_TYPE) : null;
    }
    protected String getCurrentClientSecret() {
        return messageContext != null ? (String)messageContext.get(OAuthConstants.CLIENT_SECRET) : null;
    }
    protected MultivaluedMap<String, String> getCurrentTokenRequestParams() {
        if (messageContext != null) {
            @SuppressWarnings("unchecked")
            MultivaluedMap<String, String> params =
                (MultivaluedMap<String, String>)messageContext.get(OAuthConstants.TOKEN_REQUEST_PARAMS);
            return params;
        }
        return null;
    }
    protected RefreshToken updateExistingRefreshToken(RefreshToken rt, ServerAccessToken at) {
        synchronized (refreshTokenLock) {
            return updateRefreshToken(rt, at);
        }
    }
    protected RefreshToken updateRefreshToken(RefreshToken rt, ServerAccessToken at) {
        linkAccessTokenToRefreshToken(rt, at);
        saveRefreshToken(rt);
        linkRefreshTokenToAccessToken(rt, at);
        return rt;
    }
    protected RefreshToken createNewRefreshToken(ServerAccessToken at) {
        RefreshToken rt = doCreateNewRefreshToken(at);
        return updateRefreshToken(rt, at);
    }
    protected RefreshToken doCreateNewRefreshToken(ServerAccessToken at) {
        RefreshToken rt = new RefreshToken(at.getClient(), refreshTokenLifetime);
        if (at.getAudiences() != null) {
            rt.setAudiences(new ArrayList<>(at.getAudiences()));
        }
        rt.setGrantType(at.getGrantType());
        if (at.getScopes() != null) {
            rt.setScopes(new ArrayList<>(at.getScopes()));
        }
        rt.setGrantCode(at.getGrantCode());
        rt.setNonce(at.getNonce());
        rt.setSubject(at.getSubject());
        rt.setClientCodeVerifier(at.getClientCodeVerifier());
        return rt;
    }

    protected void linkAccessTokenToRefreshToken(RefreshToken rt, ServerAccessToken at) {
        if (!rt.getAccessTokens().contains(at.getTokenKey())) {
            rt.getAccessTokens().add(at.getTokenKey());
        }
    }
    protected void linkRefreshTokenToAccessToken(RefreshToken rt, ServerAccessToken at) {
        at.setRefreshToken(rt.getTokenKey());
    }

    protected ServerAccessToken doRefreshAccessToken(Client client,
                                                     RefreshToken oldRefreshToken,
                                                     List<String> restrictedScopes) {

        List<OAuthPermission> theNewScopes = null;

        if (restrictedScopes.isEmpty()) {
            theNewScopes = oldRefreshToken.getScopes() != null
                    ? new ArrayList<OAuthPermission>(oldRefreshToken.getScopes()) : null;
        } else {
            theNewScopes = convertScopeToPermissions(client, restrictedScopes);
            if (!oldRefreshToken.getScopes().containsAll(theNewScopes)) {
                throw new OAuthServiceException("Invalid scopes");
            }
        }

        ServerAccessToken at =
            doCreateAccessToken(
                oldRefreshToken.getAudiences() != null
                    ? new ArrayList<String>(oldRefreshToken.getAudiences()) : null,
                client, oldRefreshToken.getClientCodeVerifier(),
                oldRefreshToken.getExtraProperties(), oldRefreshToken.getGrantCode(),
                oldRefreshToken.getGrantType(), oldRefreshToken.getNonce(),
                null, theNewScopes, oldRefreshToken.getSubject());

        if (isUseJwtFormatForAccessTokens()) {
            convertToJWTAccessToken(at);
        }

        return at;
    }

    public void setAccessTokenLifetime(long accessTokenLifetime) {
        this.accessTokenLifetime = accessTokenLifetime;
    }

    public void setRefreshTokenLifetime(long refreshTokenLifetime) {
        this.refreshTokenLifetime = refreshTokenLifetime;
    }

    public void setRecycleRefreshTokens(boolean recycleRefreshTokens) {
        this.recycleRefreshTokens = recycleRefreshTokens;
        this.refreshTokenLock = recycleRefreshTokens ? null : new Object();
    }

    public boolean isRecycleRefreshTokens() {
        return this.recycleRefreshTokens;
    }

    public void init() {
        for (OAuthPermission perm : permissionMap.values()) {
            if (defaultScopes != null && defaultScopes.contains(perm.getPermission())) {
                perm.setDefaultPermission(true);
            }
            if (invisibleToClientScopes != null && invisibleToClientScopes.contains(perm.getPermission())) {
                perm.setInvisibleToClient(true);
            }
        }
    }

    public void close() {
    }

    public Map<String, OAuthPermission> getPermissionMap() {
        return permissionMap;
    }

    public void setPermissionMap(Map<String, OAuthPermission> permissionMap) {
        this.permissionMap = permissionMap;
    }

    public void setSupportedScopes(Map<String, String> scopes) {
        for (Map.Entry<String, String> entry : scopes.entrySet()) {
            OAuthPermission permission = new OAuthPermission(entry.getKey(), entry.getValue());
            permissionMap.put(entry.getKey(), permission);
        }
    }

    public MessageContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
        if (authenticationStrategy != null) {
            OAuthUtils.injectContextIntoOAuthProvider(messageContext, authenticationStrategy);
        }
    }

    protected void removeClientTokens(Client c) {
        List<RefreshToken> refreshTokens = getRefreshTokens(c, null);
        if (refreshTokens != null) {
            for (RefreshToken rt : refreshTokens) {
                revokeRefreshToken(c, rt.getTokenKey());
            }
        }
        List<ServerAccessToken> accessTokens = getAccessTokens(c, null);
        if (accessTokens != null) {
            for (ServerAccessToken at : accessTokens) {
                revokeAccessToken(c, at.getTokenKey());
            }
        }
    }

    @Override
    public Client removeClient(String clientId) {
        Client c = doGetClient(clientId);
        removeClientTokens(c);
        doRemoveClient(c);
        return c;
    }

    @Override
    public Client getClient(String clientId) {
        Client client = doGetClient(clientId);
        if (client != null) {
            return client;
        }

        String grantType = getCurrentRequestedGrantType();
        if (OAuthConstants.CLIENT_CREDENTIALS_GRANT.equals(grantType)) {
            String clientSecret = getCurrentClientSecret();
            if (clientSecret != null) {
                return createClientCredentialsClient(clientId, clientSecret);
            }
        }
        return null;
    }

    public void setAuthenticationStrategy(ProviderAuthenticationStrategy authenticationStrategy) {
        this.authenticationStrategy = authenticationStrategy;
    }

    protected boolean authenticateUnregisteredClient(String clientId, String clientSecret) {
        return authenticationStrategy != null
            && authenticationStrategy.authenticate(clientId, clientSecret);
    }

    protected Client createClientCredentialsClient(String clientId, String password) {
        if (authenticateUnregisteredClient(clientId, password)) {
            Client c = new Client(clientId, password, true);
            c.setAllowedGrantTypes(Collections.singletonList(OAuthConstants.CLIENT_CREDENTIALS_GRANT));
            return c;
        }
        return null;
    }

    protected ServerAccessToken revokeAccessToken(Client client, String accessTokenKey) {
        ServerAccessToken at = getAccessToken(accessTokenKey);
        if (at != null) {
            if (!at.getClient().getClientId().equals(client.getClientId())) {
                throw new OAuthServiceException(OAuthConstants.INVALID_GRANT);
            }
            doRevokeAccessToken(at);
        }
        return at;
    }
    protected RefreshToken revokeRefreshToken(Client client, String refreshTokenKey) {
        RefreshToken refreshToken = getRefreshToken(refreshTokenKey);
        if (refreshToken != null) {
            if (!refreshToken.getClient().getClientId().equals(client.getClientId())) {
                throw new OAuthServiceException(OAuthConstants.INVALID_GRANT);
            }
            doRevokeRefreshToken(refreshToken);
        }
        return refreshToken;
    }


    protected abstract void saveAccessToken(ServerAccessToken serverToken);
    protected abstract void saveRefreshToken(RefreshToken refreshToken);
    protected abstract void doRevokeAccessToken(ServerAccessToken accessToken);
    protected abstract void doRevokeRefreshToken(RefreshToken  refreshToken);
    protected abstract RefreshToken getRefreshToken(String refreshTokenKey);

    protected abstract Client doGetClient(String clientId);

    protected abstract void doRemoveClient(Client c);

    public List<String> getDefaultScopes() {
        return defaultScopes;
    }

    public void setDefaultScopes(List<String> defaultScopes) {
        this.defaultScopes = defaultScopes;
    }

    public List<String> getRequiredScopes() {
        return requiredScopes;
    }

    public void setRequiredScopes(List<String> requiredScopes) {
        this.requiredScopes = requiredScopes;
    }

    public List<String> getInvisibleToClientScopes() {
        return invisibleToClientScopes;
    }

    public void setInvisibleToClientScopes(List<String> invisibleToClientScopes) {
        this.invisibleToClientScopes = invisibleToClientScopes;
    }

    public boolean isSupportPreauthorizedTokens() {
        return supportPreauthorizedTokens;
    }

    public void setSupportPreauthorizedTokens(boolean supportPreauthorizedTokens) {
        this.supportPreauthorizedTokens = supportPreauthorizedTokens;
    }
    protected static boolean isClientMatched(Client c, UserSubject resourceOwner) {
        return resourceOwner == null
            || c.getResourceOwnerSubject() != null
                && c.getResourceOwnerSubject().getLogin().equals(resourceOwner.getLogin());
    }
    protected static boolean isTokenMatched(ServerAccessToken token, Client c, UserSubject sub) {
        if (token != null && (c == null || token.getClient().getClientId().equals(c.getClientId()))) {
            UserSubject tokenSub = token.getSubject();
            if (sub == null || tokenSub != null && tokenSub.getLogin().equals(sub.getLogin())) {
                return true;
            }
        }
        return false;
    }
    public void setClients(List<Client> clients) {
        for (Client c : clients) {
            setClient(c);
        }
    }

    public boolean isUseJwtFormatForAccessTokens() {
        return useJwtFormatForAccessTokens;
    }

    public void setUseJwtFormatForAccessTokens(boolean useJwtFormatForAccessTokens) {
        this.useJwtFormatForAccessTokens = useJwtFormatForAccessTokens;
    }

    public OAuthJoseJwtProducer getJwtAccessTokenProducer() {
        return jwtAccessTokenProducer;
    }

    public void setJwtAccessTokenProducer(OAuthJoseJwtProducer jwtAccessTokenProducer) {
        this.jwtAccessTokenProducer = jwtAccessTokenProducer;
    }

    protected String processJwtAccessToken(JwtClaims jwtCliams) {
        // It will JWS-sign (default) and/or JWE-encrypt
        OAuthJoseJwtProducer processor =
            getJwtAccessTokenProducer() == null ? new OAuthJoseJwtProducer() : getJwtAccessTokenProducer();
        return processor.processJwt(new JwtToken(jwtCliams));
    }

    public Map<String, String> getJwtAccessTokenClaimMap() {
        return jwtAccessTokenClaimMap;
    }

    public void setJwtAccessTokenClaimMap(Map<String, String> jwtAccessTokenClaimMap) {
        this.jwtAccessTokenClaimMap = jwtAccessTokenClaimMap;
    }

    public boolean isPersistJwtEncoding() {
        return persistJwtEncoding;
    }

    public void setPersistJwtEncoding(boolean persistJwtEncoding) {
        this.persistJwtEncoding = persistJwtEncoding;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}

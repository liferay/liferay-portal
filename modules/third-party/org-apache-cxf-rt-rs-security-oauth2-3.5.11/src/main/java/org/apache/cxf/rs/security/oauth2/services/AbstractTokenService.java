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

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.apache.cxf.rs.security.oauth2.provider.ClientIdProvider;
import org.apache.cxf.rs.security.oauth2.provider.ClientSecretVerifier;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.AuthorizationUtils;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.apache.cxf.security.transport.TLSSessionInfo;

public class AbstractTokenService extends AbstractOAuthService {
    private boolean canSupportPublicClients;
    private boolean writeCustomErrors;
    private ClientIdProvider clientIdProvider;
    private ClientSecretVerifier clientSecretVerifier;

    /**
     * Make sure the client is authenticated
     */
    protected Client authenticateClientIfNeeded(MultivaluedMap<String, String> params) {
        Client client = null;
        SecurityContext sc = getMessageContext().getSecurityContext();
        Principal principal = sc.getUserPrincipal();

        String clientId = retrieveClientId(params);
        if (principal == null) {
            if (clientId != null) {
                String clientSecret = params.getFirst(OAuthConstants.CLIENT_SECRET);
                if (clientSecret != null) {
                    client = getAndValidateClientFromIdAndSecret(clientId, clientSecret, params);
                    validateClientAuthenticationMethod(client, OAuthConstants.TOKEN_ENDPOINT_AUTH_POST);
                } else if (OAuthUtils.isMutualTls(sc, getTlsSessionInfo())) {
                    client = getClient(clientId, params);
                    checkCertificateBinding(client, getTlsSessionInfo());
                    validateClientAuthenticationMethod(client, OAuthConstants.TOKEN_ENDPOINT_AUTH_TLS);
                } else if (canSupportPublicClients) {
                    client = getValidClient(clientId, params);
                    if (!isValidPublicClient(client, clientId)) {
                        client = null;
                    } else {
                        validateClientAuthenticationMethod(client, OAuthConstants.TOKEN_ENDPOINT_AUTH_NONE);
                    }
                }
            }
        } else {
            if (clientId != null) {
                if (!clientId.equals(principal.getName())) {
                    reportInvalidClient();
                }

                client = (Client)getMessageContext().get(Client.class.getName());
                if (client == null) {
                    client = getClient(clientId, params);
                }
            } else if (principal.getName() != null) {
                client = getClient(principal.getName(), params);
            }
        }
        if (client == null) {
            client = getClientFromTLSCertificates(sc, getTlsSessionInfo(), params);
            if (client == null) {
                // Basic Authentication is expected by default
                client = getClientFromBasicAuthScheme(params);
            }
        }
        if (client == null) {
            reportInvalidClient();
        }
        return client;
    }

    protected void validateClientAuthenticationMethod(Client c, String authMethod) {
        if (c != null && c.getTokenEndpointAuthMethod() != null
            && !c.getTokenEndpointAuthMethod().equals(authMethod)) {
            reportInvalidClient(new OAuthError(OAuthConstants.UNAUTHORIZED_CLIENT));
        }
    }

    protected String retrieveClientId(MultivaluedMap<String, String> params) {
        String clientId = params.getFirst(OAuthConstants.CLIENT_ID);
        if (clientId == null) {
            clientId = (String)getMessageContext().get(OAuthConstants.CLIENT_ID);
        }
        if (clientId == null && clientIdProvider != null) {
            clientId = clientIdProvider.getClientId(getMessageContext());
        }
        return clientId;
    }

    // Get the Client and check the id and secret
    protected Client getAndValidateClientFromIdAndSecret(String clientId,
                                                         String providedClientSecret,
                                                         MultivaluedMap<String, String> params) {
        Client client = getClient(clientId, providedClientSecret, params);
        if (!client.getClientId().equals(clientId)) {
            reportInvalidClient();
        }
        if (!client.isConfidential()
            || !isConfidenatialClientSecretValid(client, providedClientSecret)) {
            reportInvalidClient();
        }
        return client;
    }
    protected boolean isConfidenatialClientSecretValid(Client client, String providedClientSecret) {
        if (clientSecretVerifier != null) {
            return clientSecretVerifier.validateClientSecret(client, providedClientSecret);
        }
        return client.getClientSecret() != null
            && providedClientSecret != null && client.getClientSecret().equals(providedClientSecret);
    }
    protected boolean isValidPublicClient(Client client, String clientId) {
        return canSupportPublicClients
            && !client.isConfidential()
            && client.getClientSecret() == null;
    }

    protected Client getClientFromBasicAuthScheme(MultivaluedMap<String, String> params) {
        Client client = null;
        String[] userInfo = AuthorizationUtils.getBasicAuthUserInfo(getMessageContext());
        if (userInfo != null && userInfo.length == 2) {
            client = getAndValidateClientFromIdAndSecret(userInfo[0], userInfo[1], params);
        }
        validateClientAuthenticationMethod(client, OAuthConstants.TOKEN_ENDPOINT_AUTH_BASIC);
        return client;
    }

    protected void checkCertificateBinding(Client client, TLSSessionInfo tlsSessionInfo) {
        String subjectDn = client.getProperties().get(OAuthConstants.TLS_CLIENT_AUTH_SUBJECT_DN);
        if (subjectDn == null && client.getApplicationCertificates().isEmpty()) {
            LOG.warning("Client \"" + client.getClientId() + "\" can not be bound to the TLS certificate");
            reportInvalidClient();
        }
        X509Certificate cert = OAuthUtils.getRootTLSCertificate(tlsSessionInfo);

        if (subjectDn != null
            && !subjectDn.equals(OAuthUtils.getSubjectDnFromTLSCertificates(cert))) {
            LOG.warning("Client \"" + client.getClientId() + "\" can not be bound to the TLS certificate");
            reportInvalidClient();
        }
        String issuerDn = client.getProperties().get(OAuthConstants.TLS_CLIENT_AUTH_ISSUER_DN);
        if (issuerDn != null
            && !issuerDn.equals(OAuthUtils.getIssuerDnFromTLSCertificates(cert))) {
            LOG.warning("Client \"" + client.getClientId() + "\" can not be bound to the TLS certificate");
            reportInvalidClient();
        }
        if (!client.getApplicationCertificates().isEmpty()) {
            compareTlsCertificates(tlsSessionInfo, client.getApplicationCertificates());
        }
        OAuthUtils.setCertificateThumbprintConfirmation(getMessageContext(), cert);
    }

    private TLSSessionInfo getTlsSessionInfo() {

        return (TLSSessionInfo)getMessageContext().get(TLSSessionInfo.class.getName());
    }


    protected Client getClientFromTLSCertificates(SecurityContext sc,
                                                  TLSSessionInfo tlsSessionInfo,
                                                  MultivaluedMap<String, String> params) {
        Client client = null;
        if (OAuthUtils.isMutualTls(sc, tlsSessionInfo)) {
            X509Certificate cert = OAuthUtils.getRootTLSCertificate(tlsSessionInfo);
            String subjectDn = OAuthUtils.getSubjectDnFromTLSCertificates(cert);
            if (!StringUtils.isEmpty(subjectDn)) {
                client = getClient(subjectDn, params);
                validateClientAuthenticationMethod(client, OAuthConstants.TOKEN_ENDPOINT_AUTH_TLS);
                // The certificates must be registered with the client and match TLS certificates
                // in case of the binding where Client's clientId is a subject distinguished name
                compareTlsCertificates(tlsSessionInfo, client.getApplicationCertificates());
                OAuthUtils.setCertificateThumbprintConfirmation(getMessageContext(), cert);
            }
        }
        return client;
    }

    protected void compareTlsCertificates(TLSSessionInfo tlsInfo,
                                          List<String> base64EncodedCerts) {
        if (!OAuthUtils.compareTlsCertificates(tlsInfo, base64EncodedCerts)) {
            reportInvalidClient();
        }
    }

    protected Response handleException(OAuthServiceException ex, String error) {
        OAuthError customError = ex.getError();
        if (writeCustomErrors && customError != null) {
            return createErrorResponseFromBean(customError);
        }
        return createErrorResponseFromBean(new OAuthError(error));
    }

    protected Response createErrorResponse(MultivaluedMap<String, String> params,
                                           String error) {
        return createErrorResponseFromBean(new OAuthError(error));
    }

    protected Response createErrorResponseFromErrorCode(String error) {
        return createErrorResponseFromBean(new OAuthError(error));
    }

    protected Response createErrorResponseFromBean(OAuthError errorBean) {
        return JAXRSUtils.toResponseBuilder(400).entity(errorBean).build();
    }

    /**
     * Get the {@link Client} reference
     * @param clientId the provided client id
     * @return Client the client reference
     * @throws {@link javax.ws.rs.WebApplicationException} if no matching Client is found
     */
    protected Client getClient(String clientId, MultivaluedMap<String, String> params) {
        return getClient(clientId, params.getFirst(OAuthConstants.CLIENT_SECRET), params);
    }

    protected Client getClient(String clientId, String clientSecret, MultivaluedMap<String, String> params) {
        if (clientId == null) {
            reportInvalidRequestError("Client ID is null");
            return null;
        }
        Client client = null;
        try {
            client = getValidClient(clientId, clientSecret, params);
        } catch (OAuthServiceException ex) {
            LOG.warning("No valid client found for clientId: " + clientId);
            if (ex.getError() != null) {
                reportInvalidClient(ex.getError());
                return null;
            }
        }
        if (client == null) {
            LOG.warning("No valid client found for clientId: " + clientId);
            reportInvalidClient();
        }
        return client;
    }

    protected void reportInvalidClient() {
        reportInvalidClient(new OAuthError(OAuthConstants.INVALID_CLIENT));
    }

    protected void reportInvalidClient(OAuthError error) {
        ResponseBuilder rb = JAXRSUtils.toResponseBuilder(401);
        throw ExceptionUtils.toNotAuthorizedException(null,
            rb.type(MediaType.APPLICATION_JSON_TYPE).entity(error).build());
    }

    public void setCanSupportPublicClients(boolean support) {
        this.canSupportPublicClients = support;
    }

    public boolean isCanSupportPublicClients() {
        return canSupportPublicClients;
    }

    public void setWriteCustomErrors(boolean writeCustomErrors) {
        this.writeCustomErrors = writeCustomErrors;
    }

    public void setClientIdProvider(ClientIdProvider clientIdProvider) {
        this.clientIdProvider = clientIdProvider;
    }

    public void setClientSecretVerifier(ClientSecretVerifier clientSecretVerifier) {
        this.clientSecretVerifier = clientSecretVerifier;
    }
}

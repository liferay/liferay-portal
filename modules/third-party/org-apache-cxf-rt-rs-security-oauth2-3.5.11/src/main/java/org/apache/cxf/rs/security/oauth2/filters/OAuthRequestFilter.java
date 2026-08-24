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

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.common.util.PropertyUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.provider.FormEncodingProvider;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.jaxrs.utils.FormUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.common.AuthenticationMethod;
import org.apache.cxf.rs.security.oauth2.common.OAuthContext;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.services.AbstractAccessTokenValidator;
import org.apache.cxf.rs.security.oauth2.utils.AuthorizationUtils;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.security.transport.TLSSessionInfo;

/**
 * JAX-RS OAuth2 filter which can be used to protect the end-user endpoints
 */
@Provider
@PreMatching
// Priorities.AUTHORIZATION also works
@Priority(Priorities.AUTHENTICATION)
public class OAuthRequestFilter extends AbstractAccessTokenValidator
    implements ContainerRequestFilter {
    private static final Logger LOG = LogUtils.getL7dLogger(OAuthRequestFilter.class);

    private boolean useUserSubject;
    private String audience;
    private String issuer;
    private boolean completeAudienceMatch;
    private boolean audienceIsEndpointAddress = true;
    private boolean checkFormData;
    private List<String> requiredScopes = Collections.emptyList();
    private boolean allPermissionsMatch;
    private boolean blockPublicClients;
    private AuthenticationMethod am;

    @Override
    public void filter(ContainerRequestContext context) {
        validateRequest(JAXRSUtils.getCurrentMessage());
    }

    protected void validateRequest(Message m) {
        if (isCorsRequest(m)) {
            return;
        }

        // Get the scheme and its data, Bearer only is supported by default
        // WWW-Authenticate with the list of supported schemes will be sent back
        // if the scheme is not accepted
        String[] authParts = getAuthorizationParts(m);
        if (authParts.length < 2) {
            throw ExceptionUtils.toForbiddenException(null, null);
        }
        String authScheme = authParts[0];
        String authSchemeData = authParts[1];

        // Get the access token
        AccessTokenValidation accessTokenV = getAccessTokenValidation(authScheme, authSchemeData, null);
        if (!accessTokenV.isInitialValidationSuccessful()) {
            AuthorizationUtils.throwAuthorizationFailure(supportedSchemes, realm);
        }
        // Check audiences
        String validAudience = validateAudiences(accessTokenV.getAudiences());

        // Check if token was issued by the supported issuer
        if (issuer != null && !issuer.equals(accessTokenV.getTokenIssuer())) {
            AuthorizationUtils.throwAuthorizationFailure(supportedSchemes, realm);
        }
        // Find the scopes which match the current request

        List<OAuthPermission> permissions = accessTokenV.getTokenScopes();
        List<OAuthPermission> matchingPermissions = new ArrayList<>();

        HttpServletRequest req = getMessageContext().getHttpServletRequest();
        for (OAuthPermission perm : permissions) {
            boolean uriOK = checkRequestURI(req, perm.getUris(), m);
            boolean verbOK = checkHttpVerb(req, perm.getHttpVerbs());
            boolean scopeOk = checkScopeProperty(perm.getPermission());
            if (uriOK && verbOK && scopeOk) {
                matchingPermissions.add(perm);
            }
        }

        if (!permissions.isEmpty() && matchingPermissions.isEmpty()
            || allPermissionsMatch && (matchingPermissions.size() != permissions.size())
            || !requiredScopes.isEmpty() && requiredScopes.size() != matchingPermissions.size()) {
            String message = "Client has no valid permissions";
            LOG.warning(message);
            throw ExceptionUtils.toForbiddenException(null, null);
        }

        if (accessTokenV.getClientIpAddress() != null) {
            String remoteAddress = getMessageContext().getHttpServletRequest().getRemoteAddr();
            if (remoteAddress == null || accessTokenV.getClientIpAddress().equals(remoteAddress)) {
                String message = "Client IP Address is invalid";
                LOG.warning(message);
                throw ExceptionUtils.toForbiddenException(null, null);
            }
        }
        if (blockPublicClients && !accessTokenV.isClientConfidential()) {
            String message = "Only Confidential Clients are supported";
            LOG.warning(message);
            throw ExceptionUtils.toForbiddenException(null, null);
        }
        if (am != null && !am.equals(accessTokenV.getTokenSubject().getAuthenticationMethod())) {
            String message = "The token has been authorized by the resource owner "
                + "using an unsupported authentication method";
            LOG.warning(message);
            throw ExceptionUtils.toNotAuthorizedException(null, null);

        }
        // Check Client Certificate Binding if any
        String certThumbprint = accessTokenV.getExtraProps().get(JoseConstants.HEADER_X509_THUMBPRINT_SHA256);
        if (certThumbprint != null) {
            TLSSessionInfo tlsInfo = getTlsSessionInfo();
            X509Certificate cert = tlsInfo == null ? null : OAuthUtils.getRootTLSCertificate(tlsInfo);
            if (cert == null || !OAuthUtils.compareCertificateThumbprints(cert, certThumbprint)) { 
                throw ExceptionUtils.toNotAuthorizedException(null, null);
            }
        }

        // Create the security context and make it available on the message
        SecurityContext sc = createSecurityContext(req, accessTokenV);
        m.put(SecurityContext.class, sc);

        // Also set the OAuthContext
        OAuthContext oauthContext = new OAuthContext(accessTokenV.getTokenSubject(),
                                                     accessTokenV.getClientSubject(),
                                                     matchingPermissions,
                                                     accessTokenV.getTokenGrantType());

        oauthContext.setClientId(accessTokenV.getClientId());
        oauthContext.setClientConfidential(accessTokenV.isClientConfidential());
        oauthContext.setTokenKey(accessTokenV.getTokenKey());
        oauthContext.setTokenAudience(validAudience);
        oauthContext.setTokenIssuer(accessTokenV.getTokenIssuer());
        oauthContext.setTokenRequestParts(authParts);
        oauthContext.setTokenExtraProperties(accessTokenV.getExtraProps());
        m.setContent(OAuthContext.class, oauthContext);
    }

    protected boolean checkHttpVerb(HttpServletRequest req, List<String> verbs) {
        if (!verbs.isEmpty()
            && !verbs.contains(req.getMethod())) {
            String message = "Invalid http verb";
            LOG.fine(message);
            return false;
        }
        return true;
    }

        
    protected boolean checkRequestURI(HttpServletRequest request, List<String> uris, Message m) {

        if (uris.isEmpty()) {
            return true;
        }
        String servletPath = request.getPathInfo();
        if (servletPath == null) {
            servletPath = (String)m.get(Message.PATH_INFO);
        }
        boolean foundValidScope = false;
        for (String uri : uris) {
            if (OAuthUtils.checkRequestURI(servletPath, uri)) {
                foundValidScope = true;
                break;
            }
        }
        if (!foundValidScope) {
            String message = "Invalid request URI: " + request.getRequestURL().toString();
            LOG.fine(message);
        }
        return foundValidScope;
    }
    protected boolean checkScopeProperty(String scope) {
        if (!requiredScopes.isEmpty()) {
            return requiredScopes.contains(scope);
        }
        return true;
    }
    public void setUseUserSubject(boolean useUserSubject) {
        this.useUserSubject = useUserSubject;
    }


    protected SecurityContext createSecurityContext(HttpServletRequest request,
                                                    AccessTokenValidation accessTokenV) {
        UserSubject resourceOwnerSubject = accessTokenV.getTokenSubject();
        UserSubject clientSubject = accessTokenV.getClientSubject();

        final UserSubject theSubject =
            OAuthRequestFilter.this.useUserSubject ? resourceOwnerSubject : clientSubject;

        return new SecurityContext() {

            public Principal getUserPrincipal() {
                return theSubject != null ? new SimplePrincipal(theSubject.getLogin()) : null;
            }

            public boolean isUserInRole(String role) {
                if (theSubject == null) {
                    return false;
                }
                return theSubject.getRoles().contains(role);
            }
        };
    }

    protected boolean isCorsRequest(Message m) {
        //Redirection-based flows (Implicit Grant Flow specifically) may have
        //the browser issuing CORS preflight OPTIONS request.
        //org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter can be
        //used to handle preflights but local preflights (to be handled by the service code)
        // will be blocked by this filter unless CORS filter has done the initial validation
        // and set a message "local_preflight" property to true
        return PropertyUtils.isTrue(m.get("local_preflight"));
    }

    protected String validateAudiences(List<String> audiences) {
        if (StringUtils.isEmpty(audiences) && audience == null) {
            return null;
        }
        if (audience != null) {
            if (audiences.contains(audience)) {
                return audience;
            }
            AuthorizationUtils.throwAuthorizationFailure(supportedSchemes, realm);
        }
        if (!audienceIsEndpointAddress) {
            return null;
        }
        String requestPath = (String)PhaseInterceptorChain.getCurrentMessage().get(Message.REQUEST_URL);
        for (String s : audiences) {
            boolean matched = completeAudienceMatch ? requestPath.equals(s) : requestPath.startsWith(s);
            if (matched) {
                return s;
            }
        }
        AuthorizationUtils.throwAuthorizationFailure(supportedSchemes, realm);
        return null;
    }

    public void setCheckFormData(boolean checkFormData) {
        this.checkFormData = checkFormData;
    }

    protected String[] getAuthorizationParts(Message m) {
        if (!checkFormData) {
            return AuthorizationUtils.getAuthorizationParts(getMessageContext(), supportedSchemes);
        }
        return new String[]{OAuthConstants.BEARER_AUTHORIZATION_SCHEME, getTokenFromFormData(m)};
    }

    protected String getTokenFromFormData(Message message) {
        String method = (String)message.get(Message.HTTP_REQUEST_METHOD);
        String type = (String)message.get(Message.CONTENT_TYPE);
        if (type != null && MediaType.APPLICATION_FORM_URLENCODED.startsWith(type)
            && method != null && (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT))) {
            try {
                FormEncodingProvider<Form> provider = new FormEncodingProvider<>(true);
                Form form = FormUtils.readForm(provider, message);
                MultivaluedMap<String, String> formData = form.asMap();
                String token = formData.getFirst(OAuthConstants.ACCESS_TOKEN);
                if (token != null) {
                    FormUtils.restoreForm(provider, form, message);
                    return token;
                }
            } catch (Exception ex) {
                // the exception will be thrown below
            }
        }
        AuthorizationUtils.throwAuthorizationFailure(supportedSchemes, realm);
        return null;
    }

    public void setRequiredScopes(List<String> requiredScopes) {
        this.requiredScopes = requiredScopes;
    }

    public void setAllPermissionsMatch(boolean allPermissionsMatch) {
        this.allPermissionsMatch = allPermissionsMatch;
    }

    public void setBlockPublicClients(boolean blockPublicClients) {
        this.blockPublicClients = blockPublicClients;
    }
    public void setTokenSubjectAuthenticationMethod(AuthenticationMethod method) {
        this.am = method;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public boolean isCompleteAudienceMatch() {
        return completeAudienceMatch;
    }

    public void setCompleteAudienceMatch(boolean completeAudienceMatch) {
        this.completeAudienceMatch = completeAudienceMatch;
    }

    public void setAudienceIsEndpointAddress(boolean audienceIsEndpointAddress) {
        this.audienceIsEndpointAddress = audienceIsEndpointAddress;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    private TLSSessionInfo getTlsSessionInfo() {
        return (TLSSessionInfo)getMessageContext().get(TLSSessionInfo.class.getName());
    }
}

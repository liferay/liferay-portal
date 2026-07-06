/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.container.request.filter;

import com.liferay.oauth2.provider.constants.OAuth2ApplicationConstants;
import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.rest.internal.constants.OAuth2ProviderRESTWebKeys;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.util.DynamicRegistrationAuditMessageUtil;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ProtectedPrincipal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.Priority;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.security.Principal;

import java.util.Date;

import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=DynamicRegistrationServiceContainerRequestFilter"
	},
	service = ContainerRequestFilter.class
)
@PreMatching
@Priority(Priorities.AUTHENTICATION)
@Provider
public class DynamicRegistrationServiceContainerRequestFilter
	implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		UriInfo uriInfo = containerRequestContext.getUriInfo();

		if (!StringUtil.startsWith(uriInfo.getPath(), "register")) {
			return;
		}

		Message message = JAXRSUtils.getCurrentMessage();

		HttpServletRequest httpServletRequest = (HttpServletRequest)message.get(
			AbstractHTTPDestination.HTTP_REQUEST);

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63416")) {
			containerRequestContext.abortWith(
				Response.status(
					Response.Status.NOT_FOUND
				).build());

			return;
		}

		User user = null;

		try {
			httpServletRequest.setAttribute(
				OAuth2ProviderRESTWebKeys.DYNAMIC_REGISTRATION_CLIENT_HOST,
				_normalizeHost(_getClientHost(httpServletRequest)));

			user = _authorize(
				httpServletRequest, httpServletRequest.getMethod());
		}
		catch (WebApplicationException webApplicationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(webApplicationException);
			}

			throw webApplicationException;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			String clientHost = GetterUtil.getString(
				httpServletRequest.getAttribute(
					OAuth2ProviderRESTWebKeys.DYNAMIC_REGISTRATION_CLIENT_HOST),
				_normalizeHost(_getClientHost(httpServletRequest)));

			DynamicRegistrationAuditMessageUtil.routeAuditMessage(
				_getAuthorizationFailureAuditMessage(
					clientHost, companyId, httpServletRequest));

			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		_setSecurityContext(containerRequestContext, httpServletRequest, user);
	}

	private User _authorize(
			HttpServletRequest httpServletRequest, String method)
		throws Exception {

		JwtToken jwtToken = _getJwtToken(httpServletRequest);

		if (jwtToken == null) {
			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		long currentTime = System.currentTimeMillis() / Time.SECOND;
		long expirationTime = GetterUtil.getLong(jwtToken.getClaim("exp"));

		if ((expirationTime > 0) && (currentTime > expirationTime)) {
			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		OAuth2Application oAuth2Application = null;

		String clientId = GetterUtil.getString(jwtToken.getClaim("client_id"));
		User user = _userLocalService.getUser(
			GetterUtil.getLong(jwtToken.getClaim("sub")));

		if (!Validator.isBlank(clientId)) {
			oAuth2Application =
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					user.getCompanyId(), clientId);
		}
		else {
			oAuth2Application =
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					GetterUtil.getLong(jwtToken.getClaim("application_id")));
		}

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			user);

		if ((oAuth2Application == null) ||
			!_oAuth2ApplicationModelResourcePermission.contains(
				permissionChecker, oAuth2Application,
				OAuth2ProviderActionKeys.REGISTER_APPLICATION)) {

			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		String actionId = StringPool.BLANK;

		if (StringUtil.equalsIgnoreCase(method, "DELETE")) {
			actionId = ActionKeys.DELETE;
		}
		else if (StringUtil.equalsIgnoreCase(method, "PUT")) {
			actionId = ActionKeys.UPDATE;
		}

		if (Validator.isNotNull(actionId) &&
			!_oAuth2ApplicationModelResourcePermission.contains(
				permissionChecker, oAuth2Application, actionId)) {

			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		boolean dynamicRegistrator = StringUtil.equalsIgnoreCase(
			OAuth2ApplicationConstants.NAME_DYNAMIC_REGISTRATOR,
			oAuth2Application.getName());

		if (!dynamicRegistrator &&
			StringUtil.equalsIgnoreCase(method, "POST")) {

			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		clientId = _getClientId(httpServletRequest);

		if (Validator.isNotNull(clientId) && !dynamicRegistrator &&
			!StringUtil.equalsIgnoreCase(
				clientId, oAuth2Application.getClientId())) {

			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		return user;
	}

	private AuditMessage _getAuthorizationFailureAuditMessage(
		String clientHost, long companyId,
		HttpServletRequest httpServletRequest) {

		return _getRejectAuditMessage(
			clientHost, companyId,
			OAuth2ProviderRESTEndpointConstants.ERROR_INVALID_TOKEN,
			"Authenticated registration authorization failed",
			httpServletRequest,
			OAuth2ProviderRESTEndpointConstants.
				DYNAMIC_REGISTRATION_MODE_AUTHENTICATED);
	}

	private String _getClientHost(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getRemoteAddr();
	}

	private String _getClientId(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		String clientId = requestURI.substring(
			requestURI.lastIndexOf(StringPool.SLASH) + 1);

		if (clientId.startsWith("id-")) {
			return clientId;
		}

		return null;
	}

	private JwtToken _getJwtToken(HttpServletRequest httpServletRequest) {
		String authorization = httpServletRequest.getHeader("Authorization");

		if (!StringUtil.startsWith(authorization, "Bearer ")) {
			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		String accessTokenContent = authorization.substring("Bearer ".length());

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(
					accessTokenContent);

		if (oAuth2Authorization != null) {
			JwtClaims jwtClaims = new JwtClaims();

			jwtClaims.setClaim(
				"application_id", oAuth2Authorization.getOAuth2ApplicationId());
			jwtClaims.setClaim("sub", oAuth2Authorization.getUserId());

			Date accessTokenExpirationDate =
				oAuth2Authorization.getAccessTokenExpirationDate();

			if (accessTokenExpirationDate != null) {
				jwtClaims.setExpiryTime(
					accessTokenExpirationDate.getTime() / Time.SECOND);
			}

			return new JwtToken(jwtClaims);
		}

		JwsJwtCompactConsumer jwsJwtCompactConsumer = new JwsJwtCompactConsumer(
			accessTokenContent);

		return jwsJwtCompactConsumer.getJwtToken();
	}

	private AuditMessage _getRejectAuditMessage(
		String clientHost, long companyId, String error,
		String errorDescription, HttpServletRequest httpServletRequest,
		String mode) {

		return new AuditMessage(
			0, companyId, 0, StringPool.BLANK, null,
			JSONUtil.put(
				"clientHost", clientHost
			).put(
				"error", error
			).put(
				"errorDescription", errorDescription
			).put(
				"mode", mode
			).put(
				"userAgent",
				GetterUtil.getString(httpServletRequest.getHeader("User-Agent"))
			),
			OAuth2Application.class.getName(), StringPool.BLANK,
			OAuth2ProviderRESTEndpointConstants.
				EVENT_TYPE_DYNAMIC_REGISTRATION_REJECT,
			StringPool.BLANK);
	}

	private String _normalizeHost(String host) {
		if (Validator.isBlank(host)) {
			return StringPool.BLANK;
		}

		host = host.trim();

		if (host.startsWith(StringPool.OPEN_BRACKET)) {
			int index = host.indexOf(StringPool.CLOSE_BRACKET);

			if (index > 1) {
				host = host.substring(1, index);
			}
		}
		else {
			int index = host.indexOf(StringPool.COLON);

			if ((index > 0) &&
				(host.indexOf(StringPool.COLON, index + 1) < 0)) {

				host = host.substring(0, index);
			}
		}

		return StringUtil.toLowerCase(host);
	}

	private void _setSecurityContext(
		ContainerRequestContext containerRequestContext,
		HttpServletRequest httpServletRequest, User user) {

		try {
			if (user.isGuestUser()) {
				return;
			}

			long userId = user.getUserId();

			containerRequestContext.setSecurityContext(
				new PortalCXFSecurityContext() {

					@Override
					public Principal getUserPrincipal() {
						return new ProtectedPrincipal(String.valueOf(userId));
					}

					@Override
					public boolean isSecure() {
						return _portal.isSecure(httpServletRequest);
					}

				});
		}
		catch (Exception exception) {
			_log.error("Unable to resolve authenticated user", exception);

			containerRequestContext.abortWith(
				Response.status(
					Response.Status.INTERNAL_SERVER_ERROR
				).build());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicRegistrationServiceContainerRequestFilter.class);

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.oauth2.provider.model.OAuth2Application)"
	)
	private ModelResourcePermission<OAuth2Application>
		_oAuth2ApplicationModelResourcePermission;

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	private abstract static class PortalCXFSecurityContext
		implements org.apache.cxf.security.SecurityContext, SecurityContext {

		@Override
		public String getAuthenticationScheme() {
			return "session";
		}

		@Override
		public boolean isUserInRole(String role) {
			return false;
		}

	}

}
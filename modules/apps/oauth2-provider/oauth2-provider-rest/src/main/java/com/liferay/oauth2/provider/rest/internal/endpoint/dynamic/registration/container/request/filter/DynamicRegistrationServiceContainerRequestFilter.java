/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.container.request.filter;

import com.liferay.oauth2.provider.constants.OAuth2ApplicationConstants;
import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.rest.internal.configuration.OAuth2DynamicRegistrationConfiguration;
import com.liferay.oauth2.provider.rest.internal.constants.OAuth2ProviderRESTWebKeys;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.util.DynamicRegistrationUtil;
import com.liferay.oauth2.provider.rest.internal.endpoint.util.OAuth2ErrorUtil;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.Serializable;

import java.security.Principal;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.osgi.service.component.annotations.Activate;
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

		boolean authenticatedRegistration = StringUtil.startsWith(
			httpServletRequest.getHeader("Authorization"), "Bearer ");

		try {
			if (!authenticatedRegistration &&
				StringUtil.equalsIgnoreCase(
					httpServletRequest.getMethod(), "POST")) {

				httpServletRequest.setAttribute(
					OAuth2ProviderRESTWebKeys.DYNAMIC_REGISTRATION_OPEN,
					Boolean.TRUE);

				user = _authorizeOpenRegistration(
					companyId, httpServletRequest);
			}
			else {
				httpServletRequest.setAttribute(
					OAuth2ProviderRESTWebKeys.DYNAMIC_REGISTRATION_CLIENT_HOST,
					_getClientHost(httpServletRequest, false));

				user = _authorize(
					httpServletRequest, httpServletRequest.getMethod());
			}
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
				_getClientHost(httpServletRequest, false));

			DynamicRegistrationUtil.routeAuditMessage(
				_getAuthorizationFailureAuditMessage(
					authenticatedRegistration, clientHost, companyId,
					httpServletRequest));

			if (authenticatedRegistration) {
				throw ExceptionUtils.toNotAuthorizedException(null, null);
			}

			throw ExceptionUtils.toInternalServerErrorException(null, null);
		}

		_setSecurityContext(containerRequestContext, httpServletRequest, user);
	}

	@Activate
	protected void activate() {
		_portalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM,
			DynamicRegistrationServiceContainerRequestFilter.class.getName());
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

	private User _authorizeOpenRegistration(
			long companyId, HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		OAuth2DynamicRegistrationConfiguration
			oAuth2DynamicRegistrationConfiguration =
				_configurationProvider.getCompanyConfiguration(
					OAuth2DynamicRegistrationConfiguration.class, companyId);

		String clientHost = _getClientHost(
			httpServletRequest,
			oAuth2DynamicRegistrationConfiguration.trustProxyHeaders());

		httpServletRequest.setAttribute(
			OAuth2ProviderRESTWebKeys.DYNAMIC_REGISTRATION_CLIENT_HOST,
			clientHost);

		if (oAuth2DynamicRegistrationConfiguration.
				requireInitialAccessToken()) {

			DynamicRegistrationUtil.routeAuditMessage(
				_getRejectAuditMessage(
					clientHost, companyId,
					OAuth2ProviderRESTEndpointConstants.ERROR_INVALID_TOKEN,
					"Initial access token is required", httpServletRequest,
					OAuth2ProviderRESTEndpointConstants.
						DYNAMIC_REGISTRATION_MODE_OPEN));

			throw ExceptionUtils.toNotAuthorizedException(null, null);
		}

		_validateOpenRegistrationHosts(
			oAuth2DynamicRegistrationConfiguration.allowedHosts(), clientHost,
			companyId, httpServletRequest);
		_validateOpenRegistrationRateLimit(
			clientHost, companyId, httpServletRequest,
			oAuth2DynamicRegistrationConfiguration.
				maximumNumberOfRegistrationsPerHour());

		User user = _userLocalService.fetchUserByScreenName(
			companyId, UserConstants.SCREEN_NAME_DEFAULT_SERVICE_ACCOUNT);

		if ((user == null) || !user.isActive()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"The default service account is unavailable for ",
						"company ", companyId));
			}

			DynamicRegistrationUtil.routeAuditMessage(
				_getAuthorizationFailureAuditMessage(
					false, clientHost, companyId, httpServletRequest));

			throw ExceptionUtils.toInternalServerErrorException(null, null);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Open registration accepted for company ", companyId,
					" from \"", clientHost, "\""));
		}

		return user;
	}

	private AuditMessage _getAuthorizationFailureAuditMessage(
		boolean authenticatedRegistration, String clientHost, long companyId,
		HttpServletRequest httpServletRequest) {

		if (authenticatedRegistration) {
			return _getRejectAuditMessage(
				clientHost, companyId,
				OAuth2ProviderRESTEndpointConstants.ERROR_INVALID_TOKEN,
				"Authenticated registration authorization failed",
				httpServletRequest,
				OAuth2ProviderRESTEndpointConstants.
					DYNAMIC_REGISTRATION_MODE_AUTHENTICATED);
		}

		return _getRejectAuditMessage(
			clientHost, companyId,
			OAuth2ProviderRESTEndpointConstants.ERROR_SERVER_ERROR,
			"Open registration authorization failed", httpServletRequest,
			OAuth2ProviderRESTEndpointConstants.DYNAMIC_REGISTRATION_MODE_OPEN);
	}

	private String _getClientHost(
		HttpServletRequest httpServletRequest, boolean trustProxyHeaders) {

		if (!trustProxyHeaders) {
			return _normalizeHost(httpServletRequest.getRemoteAddr());
		}

		String forwardedFor = httpServletRequest.getHeader(
			HttpHeaders.X_FORWARDED_FOR);

		if (Validator.isBlank(forwardedFor)) {
			return _normalizeHost(httpServletRequest.getRemoteAddr());
		}

		int index = forwardedFor.indexOf(',');

		if (index >= 0) {
			forwardedFor = forwardedFor.substring(0, index);
		}

		forwardedFor = forwardedFor.trim();

		if (!Validator.isBlank(forwardedFor)) {
			return _normalizeHost(forwardedFor);
		}

		return _normalizeHost(httpServletRequest.getRemoteAddr());
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

	private void _validateOpenRegistrationHosts(
		String[] allowedHosts, String clientHost, long companyId,
		HttpServletRequest httpServletRequest) {

		Set<String> normalizedAllowedHosts = new HashSet<>();

		for (String allowedHost :
				DynamicRegistrationUtil.parseAllowedValues(allowedHosts)) {

			normalizedAllowedHosts.add(_normalizeHost(allowedHost));
		}

		if (normalizedAllowedHosts.contains(StringPool.STAR) ||
			normalizedAllowedHosts.contains(clientHost)) {

			return;
		}

		String message =
			"Host " + clientHost + " is not allowed for open registration";

		DynamicRegistrationUtil.routeAuditMessage(
			_getRejectAuditMessage(
				clientHost, companyId, OAuthConstants.ACCESS_DENIED, message,
				httpServletRequest,
				OAuth2ProviderRESTEndpointConstants.
					DYNAMIC_REGISTRATION_MODE_OPEN));

		OAuth2ErrorUtil.reportInvalidRequestError(
			message, OAuthConstants.ACCESS_DENIED, Response.Status.FORBIDDEN);
	}

	private void _validateOpenRegistrationRateLimit(
		String clientHost, long companyId,
		HttpServletRequest httpServletRequest,
		int maximumNumberOfRegistrationsPerHour) {

		if (maximumNumberOfRegistrationsPerHour <= 0) {
			return;
		}

		long time = System.currentTimeMillis();

		long windowStart = (time / Time.HOUR) * Time.HOUR;

		long retryAfterSeconds = Math.max(
			((windowStart + Time.HOUR) - time) / Time.SECOND, 1);

		String key = companyId + StringPool.COLON + clientHost;

		int count;

		synchronized (_rateLimitLock) {
			RateLimitBucket rateLimitBucket = _portalCache.get(key);

			if ((rateLimitBucket == null) ||
				(rateLimitBucket._windowStart != windowStart)) {

				count = 1;
			}
			else {
				count = rateLimitBucket._count + 1;
			}

			if (count <= maximumNumberOfRegistrationsPerHour) {
				_portalCache.put(
					key, new RateLimitBucket(count, windowStart),
					(int)retryAfterSeconds);
			}
		}

		if (count <= maximumNumberOfRegistrationsPerHour) {
			return;
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Open registration from \"", clientHost, "\" for company ",
					companyId,
					" exceeded the rate limit and will be allowed again in ",
					retryAfterSeconds, " seconds"));
		}

		DynamicRegistrationUtil.routeAuditMessage(
			_getRejectAuditMessage(
				clientHost, companyId,
				OAuth2ProviderRESTEndpointConstants.ERROR_RATE_LIMITED,
				"Open registration rate limit exceeded for host " + clientHost,
				httpServletRequest,
				OAuth2ProviderRESTEndpointConstants.
					DYNAMIC_REGISTRATION_MODE_OPEN));

		throw new WebApplicationException(
			Response.status(
				Response.Status.TOO_MANY_REQUESTS
			).entity(
				JSONUtil.put(
					"error",
					OAuth2ProviderRESTEndpointConstants.ERROR_RATE_LIMITED
				).put(
					"error_description",
					StringBundler.concat(
						"Open registration rate limit exceeded for host ",
						clientHost, ". Retry after ", retryAfterSeconds,
						" seconds.")
				).toString()
			).header(
				"Retry-After", retryAfterSeconds
			).type(
				MediaType.APPLICATION_JSON
			).build());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicRegistrationServiceContainerRequestFilter.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

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

	private volatile PortalCache<String, RateLimitBucket> _portalCache;
	private final Object _rateLimitLock = new Object();

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

	private static final class RateLimitBucket implements Serializable {

		private RateLimitBucket(int count, long windowStart) {
			_count = count;
			_windowStart = windowStart;
		}

		private static final long serialVersionUID = 1L;

		private final int _count;
		private final long _windowStart;

	}

}
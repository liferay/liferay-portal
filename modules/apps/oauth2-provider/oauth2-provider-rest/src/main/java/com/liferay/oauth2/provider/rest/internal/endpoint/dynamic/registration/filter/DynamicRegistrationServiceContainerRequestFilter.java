/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.filter;

import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ProtectedPrincipal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.Priority;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.security.Principal;

import java.util.Base64;

import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
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

		User user;

		try {
			JSONObject authorizationJSONObject = _getAuthorizationJSONObject(
				message);

			String clientId = authorizationJSONObject.getString("client_id");

			long userId = authorizationJSONObject.getLong("sub");

			user = _getUser(userId);

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					user.getCompanyId(), clientId);

			if (!StringUtil.equalsIgnoreCase(_APPLICATION_NAME, clientId) &&
				!_containsOAuth2RegisterApplicationPermission(
					oAuth2Application, user)) {

				throw ExceptionUtils.toNotAuthorizedException(
					(Throwable)null, (Response)null);
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug("exception processing token: ", jsonException);
			}

			throw ExceptionUtils.toNotAuthorizedException(
				(Throwable)null, (Response)null);
		}
		catch (WebApplicationException webApplicationException) {
			if (_log.isDebugEnabled()) {
				_log.debug("webApplicationException ", webApplicationException);
			}

			throw ExceptionUtils.toNotAuthorizedException(
				(Throwable)null, (Response)null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Exception when retrieving permissions:", exception);
			}

			throw ExceptionUtils.toNotAuthorizedException(
				(Throwable)null, (Response)null);
		}

		try {
			if (!user.isGuestUser()) {
				long userId = user.getUserId();

				containerRequestContext.setSecurityContext(
					new PortalCXFSecurityContext() {

						@Override
						public Principal getUserPrincipal() {
							return new ProtectedPrincipal(
								String.valueOf(userId));
						}

						@Override
						public boolean isSecure() {
							return _portal.isSecure(_httpServletRequest);
						}

					});
			}
		}
		catch (Exception exception) {
			_log.error("Unable to resolve authenticated user", exception);
			containerRequestContext.abortWith(
				Response.status(
					Response.Status.INTERNAL_SERVER_ERROR
				).build());
		}
	}

	private boolean _containsOAuth2RegisterApplicationPermission(
			OAuth2Application oAuth2Application, User user)
		throws Exception {

		if (oAuth2Application == null) {
			return false;
		}

		return _oAuth2ApplicationModelResourcePermission.contains(
			_permissionCheckerFactory.create(user), oAuth2Application,
			OAuth2ProviderActionKeys.ACTION_REGISTER_APPLICATION);
	}

	private JSONObject _getAuthorizationJSONObject(Message message)
		throws JSONException, WebApplicationException {

		_httpServletRequest = (HttpServletRequest)message.get(
			AbstractHTTPDestination.HTTP_REQUEST);

		String authorizationHeader = _httpServletRequest.getHeader(
			"Authorization");

		if (Validator.isBlank(authorizationHeader)) {
			throw ExceptionUtils.toNotAuthorizedException(
				(Throwable)null, (Response)null);
		}

		String accessToken = authorizationHeader.split(StringPool.SPACE)[1];

		Base64.Decoder decoder = Base64.getUrlDecoder();
		String[] parts = accessToken.split("\\.");

		return _jsonFactory.createJSONObject(
			new String(decoder.decode(parts[1])));
	}

	private User _getUser(long userId) {
		try {
			return _userLocalService.getUser(userId);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private static final String _APPLICATION_NAME = "DynamicRegistrator";

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicRegistrationServiceContainerRequestFilter.class);

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.oauth2.provider.model.OAuth2Application)"
	)
	private ModelResourcePermission<OAuth2Application>
		_oAuth2ApplicationModelResourcePermission;

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
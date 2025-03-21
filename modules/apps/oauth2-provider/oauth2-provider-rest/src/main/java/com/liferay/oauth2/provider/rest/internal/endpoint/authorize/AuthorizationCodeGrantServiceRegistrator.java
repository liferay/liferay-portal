/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.authorize;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.OAuthRedirectionState;
import org.apache.cxf.rs.security.oauth2.common.OOBAuthorizationResponse;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	service = {}
)
public class AuthorizationCodeGrantServiceRegistrator {

	public static class LiferayAuthorizationCodeGrantService
		extends AuthorizationCodeGrantService {

		@Override
		public ServerAuthorizationCodeGrant getGrantRepresentation(
			OAuthRedirectionState oAuthRedirectionState, Client client,
			List<String> requestedScopesList, List<String> approvedScopesList,
			UserSubject userSubject, ServerAccessToken serverAccessToken) {

			ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
				super.getGrantRepresentation(
					oAuthRedirectionState, client, requestedScopesList,
					approvedScopesList, userSubject, serverAccessToken);

			String cookieName = _getCookieName(client.getClientId());

			String rememberDeviceContent = _getRememberDeviceContent(
				cookieName);

			if (rememberDeviceContent == null) {
				return serverAuthorizationCodeGrant;
			}

			long userId = GetterUtil.getLong(userSubject.getId());

			LiferayOAuthDataProvider liferayOAuthDataProvider =
				_getLiferayOAuthDataProvider();

			OAuth2Authorization oAuth2Authorization =
				liferayOAuthDataProvider.getOAuth2Authorization(
					client, rememberDeviceContent, userId);

			if ((oAuth2Authorization == null) ||
				!rememberDeviceContent.equals(
					oAuth2Authorization.getRememberDeviceContent())) {

				return serverAuthorizationCodeGrant;
			}

			liferayOAuthDataProvider.doRevokeAuthorization(oAuth2Authorization);

			Cookie cookie = _getCookie(cookieName);

			MessageContext messageContext = getMessageContext();

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL, cookie,
				messageContext.getHttpServletRequest(),
				messageContext.getHttpServletResponse());

			Map<String, String> extraProperties =
				serverAuthorizationCodeGrant.getExtraProperties();

			extraProperties.put(
				OAuth2ProviderRESTEndpointConstants.
					PROPERTY_KEY_REMEMBER_DEVICE,
				cookie.getValue());

			return serverAuthorizationCodeGrant;
		}

		@Override
		protected boolean canAuthorizationBeSkipped(
			MultivaluedMap<String, String> params, Client client,
			UserSubject userSubject, List<String> requestedScopesList,
			List<OAuthPermission> permissions) {

			if (MapUtil.getBoolean(
					client.getProperties(),
					OAuth2ProviderRESTEndpointConstants.
						PROPERTY_KEY_CLIENT_TRUSTED_APPLICATION)) {

				return true;
			}

			if (!MapUtil.getBoolean(
					client.getProperties(),
					OAuth2ProviderRESTEndpointConstants.
						PROPERTY_KEY_CLIENT_REMEMBER_DEVICE)) {

				return super.canAuthorizationBeSkipped(
					params, client, userSubject, requestedScopesList,
					permissions);
			}

			String rememberDeviceContent = _getRememberDeviceContent(
				_getCookieName(client.getClientId()));

			if (rememberDeviceContent == null) {
				return super.canAuthorizationBeSkipped(
					params, client, userSubject, requestedScopesList,
					permissions);
			}

			long userId = GetterUtil.getLong(userSubject.getId());

			LiferayOAuthDataProvider liferayOAuthDataProvider =
				_getLiferayOAuthDataProvider();

			OAuth2Authorization oAuth2Authorization =
				liferayOAuthDataProvider.getOAuth2Authorization(
					client, rememberDeviceContent, userId);

			if ((oAuth2Authorization != null) &&
				rememberDeviceContent.equals(
					oAuth2Authorization.getRememberDeviceContent())) {

				RefreshToken refreshToken =
					liferayOAuthDataProvider.getRefreshToken(
						oAuth2Authorization.getRefreshTokenContent());

				if ((refreshToken != null) &&
					!OAuthUtils.isExpired(
						refreshToken.getIssuedAt(),
						refreshToken.getExpiresIn())) {

					return true;
				}
			}

			return super.canAuthorizationBeSkipped(
				params, client, userSubject, requestedScopesList, permissions);
		}

		@Override
		protected Response deliverOOBResponse(
			OOBAuthorizationResponse oobAuthorizationResponse) {

			_log.error(
				"The parameter \"redirect_uri\" was not found in the request " +
					"for client " + oobAuthorizationResponse.getClientId());

			return Response.status(
				500
			).build();
		}

		@Override
		protected Client getClient(
			String clientId, MultivaluedMap<String, String> params) {

			try {
				Client client = getValidClient(clientId, params);

				if (client != null) {
					return client;
				}
			}
			catch (OAuthServiceException oAuthServiceException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to validate remote client",
						oAuthServiceException);
				}

				if (oAuthServiceException.getError() != null) {
					reportInvalidRequestError(
						oAuthServiceException.getError(), null);
				}
			}

			reportInvalidRequestError(
				new OAuthError(OAuthConstants.INVALID_CLIENT), null);

			return null;
		}

		@Override
		protected OAuthRedirectionState recreateRedirectionStateFromParams(
			MultivaluedMap<String, String> params) {

			OAuthRedirectionState oAuthRedirectionState =
				super.recreateRedirectionStateFromParams(params);

			LiferayOAuthDataProvider liferayOAuthDataProvider =
				_getLiferayOAuthDataProvider();

			Client client = liferayOAuthDataProvider.getClient(
				oAuthRedirectionState.getClientId());

			if (!MapUtil.getBoolean(
					client.getProperties(),
					OAuth2ProviderRESTEndpointConstants.
						PROPERTY_KEY_CLIENT_REMEMBER_DEVICE) ||
				!params.containsKey(
					"_com_liferay_oauth2_provider_web_internal_portlet_" +
						"OAuth2AuthorizePortlet_rememberDevice")) {

				return oAuthRedirectionState;
			}

			Cookie cookie = _getCookie(_getCookieName(client.getClientId()));

			MessageContext messageContext = getMessageContext();

			CookiesManagerUtil.addCookie(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL, cookie,
				messageContext.getHttpServletRequest(),
				messageContext.getHttpServletResponse());

			Map<String, String> extraProperties =
				oAuthRedirectionState.getExtraProperties();

			extraProperties.put(
				OAuth2ProviderRESTEndpointConstants.
					PROPERTY_KEY_REMEMBER_DEVICE,
				cookie.getValue());

			return oAuthRedirectionState;
		}

		private Cookie _getCookie(String cookieName) {
			UUID uuid = new UUID(
				SecureRandomUtil.nextLong(), SecureRandomUtil.nextLong());

			return new Cookie(cookieName, uuid.toString());
		}

		private LiferayOAuthDataProvider _getLiferayOAuthDataProvider() {
			return (LiferayOAuthDataProvider)getDataProvider();
		}

		private String _getRememberDeviceContent(String cookieName) {
			MessageContext messageContext = getMessageContext();

			HttpServletRequest httpServletRequest =
				messageContext.getHttpServletRequest();

			for (Cookie cookie : httpServletRequest.getCookies()) {
				if (Objects.equals(cookieName, cookie.getName())) {
					return cookie.getValue();
				}
			}

			return null;
		}

		@Context
		private UriInfo _uriInfo;

	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		OAuth2ProviderConfiguration oAuth2ProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				OAuth2ProviderConfiguration.class, properties);

		if (!oAuth2ProviderConfiguration.allowAuthorizationCodeGrant() &&
			!oAuth2ProviderConfiguration.allowAuthorizationCodePKCEGrant()) {

			return;
		}

		AuthorizationCodeGrantService authorizationCodeGrantService =
			new LiferayAuthorizationCodeGrantService();

		authorizationCodeGrantService.setCanSupportPublicClients(
			oAuth2ProviderConfiguration.allowAuthorizationCodePKCEGrant());
		authorizationCodeGrantService.setDataProvider(
			_liferayOAuthDataProvider);
		authorizationCodeGrantService.setSubjectCreator(_subjectCreator);

		_serviceRegistration = bundleContext.registerService(
			Object.class, authorizationCodeGrantService,
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.application.select",
				"(osgi.jaxrs.name=Liferay.OAuth2.Application)"
			).put(
				"osgi.jaxrs.name", "Liferay.Authorization.Code.Grant.Service"
			).put(
				"osgi.jaxrs.resource", true
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private static String _getCookieName(String clientId) {
		return OAuth2ProviderRESTEndpointConstants.
			COOKIE_NAME_REMEMBER_DEVICE_PREFIX.concat(clientId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthorizationCodeGrantServiceRegistrator.class);

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	private ServiceRegistration<Object> _serviceRegistration;

	@Reference
	private SubjectCreator _subjectCreator;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.liferay;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ProviderConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.redirect.OAuth2RedirectURIInterpolator;
import com.liferay.oauth2.provider.rest.internal.configuration.OAuth2AuthorizationServerConfiguration;
import com.liferay.oauth2.provider.rest.internal.endpoint.authorize.configuration.OAuth2AuthorizationFlowConfiguration;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProviderAccessor;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AbstractAuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.grants.jwt.Constants;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJoseJwtProducer;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Carlos Sierra Andrés
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.rest.internal.configuration.OAuth2AuthorizationServerConfiguration",
	service = LiferayOAuthDataProvider.class
)
public class LiferayOAuthDataProvider
	extends AbstractAuthorizationCodeDataProvider {

	public LiferayOAuthDataProvider() {
		setInvisibleToClientScopes(
			Collections.singletonList(OAuthConstants.REFRESH_TOKEN_SCOPE));
	}

	@Override
	public List<OAuthPermission> convertScopeToPermissions(
		Client client, List<String> requestedScopes) {

		List<String> invisibleToClientScopes = getInvisibleToClientScopes();

		return TransformUtil.transform(
			requestedScopes,
			requestedScope -> {
				OAuthPermission oAuthPermission = new OAuthPermission(
					requestedScope);

				if (invisibleToClientScopes.contains(
						oAuthPermission.getPermission())) {

					oAuthPermission.setInvisibleToClient(true);
				}

				return oAuthPermission;
			});
	}

	@Override
	public ServerAccessToken createAccessToken(
			AccessTokenRegistration accessTokenRegistration)
		throws OAuthServiceException {

		List<String> approvedScope = new ArrayList<>(
			accessTokenRegistration.getRequestedScope());

		if (approvedScope.isEmpty()) {
			Client client = accessTokenRegistration.getClient();

			approvedScope.addAll(client.getRegisteredScopes());
		}

		accessTokenRegistration.setApprovedScope(approvedScope);

		if (!_refreshTokenIncompatibleGrants.contains(
				accessTokenRegistration.getGrantType())) {

			approvedScope.add(OAuthConstants.REFRESH_TOKEN_SCOPE);
		}

		return super.createAccessToken(accessTokenRegistration);
	}

	@Override
	public ServerAuthorizationCodeGrant createCodeGrant(
			AuthorizationCodeRegistration authorizationCodeRegistration)
		throws OAuthServiceException {

		ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
			super.createCodeGrant(authorizationCodeRegistration);

		if (serverAuthorizationCodeGrant.getClientCodeChallengeMethod() ==
				null) {

			serverAuthorizationCodeGrant.setClientCodeChallengeMethod("S256");
		}

		serverAuthorizationCodeGrant.setExtraProperties(
			authorizationCodeRegistration.getExtraProperties());
		serverAuthorizationCodeGrant.setRequestedScopes(
			authorizationCodeRegistration.getRequestedScope());

		_serverAuthorizationCodeGrantProvider.putServerAuthorizationCodeGrant(
			serverAuthorizationCodeGrant);

		return serverAuthorizationCodeGrant;
	}

	@Override
	public void doRevokeAccessToken(ServerAccessToken accessToken) {
		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(
					accessToken.getTokenKey());

		if (oAuth2Authorization == null) {
			return;
		}

		oAuth2Authorization.setAccessTokenContent(
			OAuth2ProviderConstants.EXPIRED_TOKEN);

		_oAuth2AuthorizationLocalService.updateOAuth2Authorization(
			oAuth2Authorization);
	}

	public void doRevokeAuthorization(OAuth2Authorization oAuth2Authorization) {
		_oAuth2AuthorizationLocalService.deleteOAuth2Authorization(
			oAuth2Authorization);
	}

	@Override
	public void doRevokeRefreshToken(RefreshToken refreshToken) {
		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByRefreshTokenContent(
					refreshToken.getTokenKey());

		if (oAuth2Authorization == null) {
			return;
		}

		oAuth2Authorization.setRefreshTokenContent(
			OAuth2ProviderConstants.EXPIRED_TOKEN);

		_oAuth2AuthorizationLocalService.updateOAuth2Authorization(
			oAuth2Authorization);
	}

	public BearerTokenProvider.AccessToken fromCXFAccessToken(
		ServerAccessToken serverAccessToken) {

		OAuth2Application oAuth2Application = resolveOAuth2Application(
			serverAccessToken.getClient());
		UserSubject userSubject = serverAccessToken.getSubject();

		return new BearerTokenProvider.AccessToken(
			oAuth2Application, serverAccessToken.getAudiences(),
			serverAccessToken.getClientCodeVerifier(),
			serverAccessToken.getExpiresIn(),
			serverAccessToken.getExtraProperties(),
			serverAccessToken.getGrantCode(), serverAccessToken.getGrantType(),
			serverAccessToken.getIssuedAt(), serverAccessToken.getIssuer(),
			serverAccessToken.getNonce(), serverAccessToken.getParameters(),
			serverAccessToken.getRefreshToken(),
			serverAccessToken.getResponseType(),
			OAuthUtils.convertPermissionsToScopeList(
				serverAccessToken.getScopes()),
			serverAccessToken.getTokenKey(), serverAccessToken.getTokenType(),
			GetterUtil.getLong(userSubject.getId()), userSubject.getLogin());
	}

	public BearerTokenProvider.RefreshToken fromCXFRefreshToken(
		RefreshToken refreshToken) {

		OAuth2Application oAuth2Application = resolveOAuth2Application(
			refreshToken.getClient());
		UserSubject userSubject = refreshToken.getSubject();

		return new BearerTokenProvider.RefreshToken(
			oAuth2Application, refreshToken.getAudiences(),
			refreshToken.getClientCodeVerifier(), refreshToken.getExpiresIn(),
			refreshToken.getGrantType(), refreshToken.getIssuedAt(),
			OAuthUtils.convertPermissionsToScopeList(refreshToken.getScopes()),
			refreshToken.getTokenKey(), refreshToken.getTokenType(),
			GetterUtil.getLong(userSubject.getId()), userSubject.getLogin());
	}

	@Override
	public ServerAccessToken getAccessToken(String accessToken)
		throws OAuthServiceException {

		if (Validator.isBlank(accessToken)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" tried to use empty OAuth 2 access token"));
			}

			return null;
		}

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(accessToken);

		if (oAuth2Authorization == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" used unknown OAuth 2 token. Repeating report may be ",
						"a sign of a brute-force attack."));
			}

			return null;
		}

		if (OAuth2ProviderConstants.EXPIRED_TOKEN.equals(
				oAuth2Authorization.getAccessTokenContent())) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" tried to use expired or revoked OAuth 2 token for ",
						"Liferay OAuth 2 application ",
						oAuth2Authorization.getOAuth2ApplicationId(),
						" and user ", oAuth2Authorization.getUserId()));
			}

			return null;
		}

		try {
			return _populateAccessToken(oAuth2Authorization);
		}
		catch (PortalException portalException) {
			_log.error("Unable to populate access token", portalException);

			throw new OAuthServiceException(portalException);
		}
	}

	@Override
	public List<ServerAccessToken> getAccessTokens(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		throw new UnsupportedOperationException();
	}

	public BearerTokenProvider getBearerTokenProvider(
		long companyId, String clientId) {

		return _bearerTokenProviderAccessor.getBearerTokenProvider(
			companyId, clientId);
	}

	public Client getClient(OAuth2Application oAuth2Application) {
		return _populateClient(oAuth2Application, getMessageContext());
	}

	@Override
	public List<Client> getClients(UserSubject resourceOwner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ServerAuthorizationCodeGrant> getCodeGrants(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		return _serverAuthorizationCodeGrantProvider.
			getServerAuthorizationCodeGrants(client, subject);
	}

	@Override
	public long getGrantLifetime() {
		try {
			OAuth2AuthorizationFlowConfiguration
				oAuth2AuthorizationFlowConfiguration =
					_configurationProvider.getSystemConfiguration(
						OAuth2AuthorizationFlowConfiguration.class);

			return oAuth2AuthorizationFlowConfiguration.
				authorizationCodeGrantTTL();
		}
		catch (ConfigurationException configurationException) {
			throw new OAuthServiceException(
				"Unable to get system configuration: " +
					OAuth2AuthorizationFlowConfiguration.class.getName(),
				configurationException);
		}
	}

	public String getIssuer() {
		try {
			MessageContext messageContext = getMessageContext();

			return _portal.getHost(messageContext.getHttpServletRequest());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			Company company = _companyLocalService.fetchCompany(
				CompanyThreadLocal.getCompanyId());

			if (company != null) {
				return company.getWebId();
			}
		}

		return null;
	}

	@Override
	public OAuthJoseJwtProducer getJwtAccessTokenProducer() {
		return _oAuthJoseJwtProducerDCLSingleton.getSingleton(
			this::_createJwtAccessTokenProducer);
	}

	public OAuth2Authorization getOAuth2Authorization(
		Client client, String rememberDeviceContent, long userId) {

		long companyId = MapUtil.getLong(
			client.getProperties(),
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID);

		try {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.getOAuth2Application(
					companyId, client.getClientId());

			return _oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByRememberDeviceContent(
					userId, oAuth2Application.getOAuth2ApplicationId(),
					rememberDeviceContent);
		}
		catch (PortalException portalException) {
			throw new OAuthServiceException(portalException);
		}
	}

	@Override
	public RefreshToken getRefreshToken(String refreshTokenKey) {
		if (Validator.isBlank(refreshTokenKey)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" tried to use empty OAuth 2 refresh token"));
			}

			return null;
		}

		try {
			OAuth2Authorization oAuth2Authorization =
				_oAuth2AuthorizationLocalService.
					fetchOAuth2AuthorizationByRefreshTokenContent(
						refreshTokenKey);

			if (oAuth2Authorization == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Remote client ", _getRemoteIP(),
							" used unknown OAuth 2 refresh token. Repeating ",
							"report may be a sign of a brute force attack."));
				}

				return null;
			}

			if (OAuth2ProviderConstants.EXPIRED_TOKEN.equals(
					oAuth2Authorization.getRefreshTokenContent())) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Remote client ", _getRemoteIP(),
							" tried to use expired or revoked OAuth 2 refresh ",
							"token for Liferay OAuth 2 application ",
							oAuth2Authorization.getOAuth2ApplicationId(),
							" and user ", oAuth2Authorization.getUserId()));
				}

				return null;
			}

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.getOAuth2Application(
					oAuth2Authorization.getOAuth2ApplicationId());

			long expires = _toCXFTime(
				oAuth2Authorization.getRefreshTokenExpirationDate());
			long issuedAt = _toCXFTime(
				oAuth2Authorization.getRefreshTokenCreateDate());

			long lifetime = expires - issuedAt;

			RefreshToken refreshToken = new RefreshToken(
				_populateClient(oAuth2Application, getMessageContext()),
				refreshTokenKey, lifetime, issuedAt);

			refreshToken.setAccessTokens(
				Collections.singletonList(
					oAuth2Authorization.getAccessTokenContent()));
			refreshToken.setScopes(
				convertScopeToPermissions(
					refreshToken.getClient(),
					_oAuth2ApplicationScopeAliasesLocalService.
						getScopeAliasesList(
							oAuth2Authorization.
								getOAuth2ApplicationScopeAliasesId())));
			refreshToken.setSubject(
				_populateUserSubject(
					oAuth2Authorization.getCompanyId(),
					oAuth2Authorization.getUserId(),
					oAuth2Authorization.getUserName()));

			Map<String, String> extraProperties =
				refreshToken.getExtraProperties();

			extraProperties.put(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID,
				String.valueOf(oAuth2Authorization.getCompanyId()));

			return refreshToken;
		}
		catch (PortalException portalException) {
			throw new OAuthServiceException(portalException);
		}
	}

	@Override
	public List<RefreshToken> getRefreshTokens(
			Client client, UserSubject subject)
		throws OAuthServiceException {

		return null;
	}

	public ServerAuthorizationCodeGrant getServerAuthorizationCodeGrant(
		String code) {

		if (code == null) {
			return null;
		}

		return _serverAuthorizationCodeGrantProvider.
			getServerAuthorizationCodeGrant(code);
	}

	public UserSubject getUserSubject(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return null;
		}

		return _populateUserSubject(
			user.getCompanyId(), userId, user.getScreenName());
	}

	@Override
	public ServerAccessToken refreshAccessToken(
			Client client, String refreshTokenKey,
			List<String> restrictedScopes)
		throws OAuthServiceException {

		RefreshToken oldRefreshToken = getRefreshToken(refreshTokenKey);

		if (oldRefreshToken == null) {
			throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
		}

		if (OAuthUtils.isExpired(
				oldRefreshToken.getIssuedAt(),
				oldRefreshToken.getExpiresIn())) {

			doRevokeRefreshToken(oldRefreshToken);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" tried to use an expired OAuth 2 refresh token for ",
						"OAuth 2 client ID ", client.getClientId()));
			}

			throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
		}

		OAuth2Application oAuth2Application = resolveOAuth2Application(client);

		BearerTokenProvider bearerTokenProvider = getBearerTokenProvider(
			oAuth2Application.getCompanyId(), oAuth2Application.getClientId());

		if (!bearerTokenProvider.isValid(
				fromCXFRefreshToken(oldRefreshToken))) {

			doRevokeRefreshToken(oldRefreshToken);

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" tried to use an invalid OAuth 2 token for OAuth 2 ",
						"client ID ", client.getClientId()));
			}

			throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
		}

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByRefreshTokenContent(refreshTokenKey);

		if (oAuth2Authorization == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" used unknown OAuth 2 refresh token for OAuth 2 ",
						"client ID ", client.getClientId(),
						". Repeating report may be a sign of a brute force ",
						"attack."));
			}

			throw new OAuthServiceException(OAuthConstants.ACCESS_DENIED);
		}

		ServerAccessToken accessToken = doRefreshAccessToken(
			client, oldRefreshToken, Collections.emptyList());

		accessToken.setRefreshToken(oldRefreshToken.getTokenKey());

		RefreshToken newRefreshToken = doCreateNewRefreshToken(accessToken);

		try {
			OAuth2ProviderConfiguration oAuth2ProviderConfiguration =
				_configurationProvider.getSystemConfiguration(
					OAuth2ProviderConfiguration.class);

			if (oAuth2ProviderConfiguration.recycleRefreshToken()) {
				newRefreshToken.setTokenKey(oldRefreshToken.getTokenKey());
			}
		}
		catch (ConfigurationException configurationException) {
			throw new OAuthServiceException(
				"Unable to get system configuration: " +
					OAuth2ProviderConfiguration.class.getName(),
				configurationException);
		}

		List<String> accessTokens = newRefreshToken.getAccessTokens();

		accessTokens.add(accessToken.getTokenKey());

		try {
			_invokeTransactionally(
				() -> {
					saveAccessToken(accessToken);
					saveRefreshToken(newRefreshToken);
				});
		}
		catch (Throwable throwable) {
			throw new OAuthServiceException(throwable);
		}

		accessToken.setRefreshToken(newRefreshToken.getTokenKey());

		return accessToken;
	}

	@Override
	public ServerAuthorizationCodeGrant removeCodeGrant(String code)
		throws OAuthServiceException {

		if (code == null) {
			return null;
		}

		return _serverAuthorizationCodeGrantProvider.
			removeServerAuthorizationCodeGrant(code);
	}

	public OAuth2Application resolveOAuth2Application(Client client) {
		Map<String, String> properties = client.getProperties();

		long companyId = GetterUtil.getLong(
			properties.get(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID));

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				companyId, client.getClientId());

		if (oAuth2Application == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" tried to use a nonexistent OAuth 2 client ID ",
						client.getClientId()));
			}

			return null;
		}

		return oAuth2Application;
	}

	@Override
	public void setClient(Client client) {
		throw new UnsupportedOperationException();
	}

	public void updateRememberDeviceContent(
		String refreshTokenContent, String rememberDeviceContent) {

		_oAuth2AuthorizationLocalService.updateRememberDeviceContent(
			refreshTokenContent, rememberDeviceContent);
	}

	@Activate
	@SuppressWarnings("unchecked")
	protected void activate(Map<String, Object> properties) {
		Collections.addAll(
			_refreshTokenIncompatibleGrants, Constants.JWT_BEARER_GRANT,
			Constants.JWT_BEARER_GRANT,
			HttpUtils.urlEncode(
				OAuthConstants.CLIENT_CREDENTIALS_GRANT,
				StandardCharsets.UTF_8.name()));

		_oAuth2AuthorizationServerConfiguration =
			ConfigurableUtil.createConfigurable(
				OAuth2AuthorizationServerConfiguration.class, properties);

		_init();
	}

	@Override
	protected JwtClaims createJwtAccessToken(
		ServerAccessToken serverAccessToken) {

		// Fix a bug in CXF. Scopes in JWT claim should be a string.

		JwtClaims jwtClaims = super.createJwtAccessToken(serverAccessToken);

		List<OAuthPermission> oAuthPermissions = serverAccessToken.getScopes();

		if (!oAuthPermissions.isEmpty()) {
			jwtClaims.setClaim(
				OAuthConstants.SCOPE,
				OAuthUtils.convertPermissionsToScope(oAuthPermissions));
		}

		return jwtClaims;
	}

	@Override
	protected ServerAccessToken createNewAccessToken(
		Client client, UserSubject userSubject) {

		ServerAccessToken serverAccessToken = super.createNewAccessToken(
			client, userSubject);

		if (getIssuer() != null) {
			serverAccessToken.setIssuer(getIssuer());
		}

		return serverAccessToken;
	}

	@Override
	protected ServerAccessToken doCreateAccessToken(
		AccessTokenRegistration accessTokenRegistration) {

		ServerAccessToken serverAccessToken = _createOpaqueServerAccessToken(
			accessTokenRegistration.getAudiences(),
			accessTokenRegistration.getClient(),
			accessTokenRegistration.getClientCodeVerifier(),
			accessTokenRegistration.getGrantCode(),
			accessTokenRegistration.getGrantType(),
			accessTokenRegistration.getNonce(),
			accessTokenRegistration.getExtraProperties(),
			convertScopeToPermissions(
				accessTokenRegistration.getClient(),
				accessTokenRegistration.getApprovedScope()),
			accessTokenRegistration.getResponseType(),
			accessTokenRegistration.getSubject());

		MessageContext messageContext = getMessageContext();

		if (messageContext != null) {
			String x509 = (String)messageContext.get(
				JoseConstants.HEADER_X509_THUMBPRINT_SHA256);

			if (x509 != null) {
				Map<String, String> extraProperties =
					serverAccessToken.getExtraProperties();

				extraProperties.put(
					JoseConstants.HEADER_X509_THUMBPRINT_SHA256, x509);
			}
		}

		_customizeServerAccessToken(
			accessTokenRegistration.getExtraProperties(), serverAccessToken);

		if (isUseJwtFormatForAccessTokens()) {
			_convertToJWTAccessToken(serverAccessToken);
		}

		return serverAccessToken;
	}

	@Override
	protected RefreshToken doCreateNewRefreshToken(
		ServerAccessToken serverAccessToken) {

		RefreshToken cxfRefreshToken = super.doCreateNewRefreshToken(
			serverAccessToken);

		BearerTokenProvider.RefreshToken refreshToken = fromCXFRefreshToken(
			cxfRefreshToken);

		OAuth2Application oAuth2Application =
			refreshToken.getOAuth2Application();

		BearerTokenProvider bearerTokenProvider = getBearerTokenProvider(
			oAuth2Application.getCompanyId(), oAuth2Application.getClientId());

		bearerTokenProvider.onBeforeCreate(refreshToken);

		cxfRefreshToken.setAudiences(refreshToken.getAudiences());
		cxfRefreshToken.setClientCodeVerifier(
			refreshToken.getClientCodeVerifier());
		cxfRefreshToken.setExpiresIn(refreshToken.getExpiresIn());
		cxfRefreshToken.setGrantType(refreshToken.getGrantType());
		cxfRefreshToken.setIssuedAt(refreshToken.getIssuedAt());
		cxfRefreshToken.setScopes(
			convertScopeToPermissions(
				serverAccessToken.getClient(), refreshToken.getScopes()));
		cxfRefreshToken.setTokenKey(refreshToken.getTokenKey());
		cxfRefreshToken.setTokenType(refreshToken.getTokenType());

		UserSubject userSubject = cxfRefreshToken.getSubject();

		userSubject.setId(String.valueOf(refreshToken.getUserId()));
		userSubject.setLogin(refreshToken.getUserName());

		return cxfRefreshToken;
	}

	@Override
	protected Client doGetClient(String clientId) {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				CompanyThreadLocal.getCompanyId(), clientId);

		if (oAuth2Application == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Remote client ", _getRemoteIP(),
						" tried to use a nonexistent OAuth 2 client ID ",
						clientId));
			}

			return null;
		}

		MessageContext messageContext = getMessageContext();

		messageContext.put(OAuthConstants.CLIENT_ID, clientId);

		return _populateClient(oAuth2Application, messageContext);
	}

	@Override
	protected ServerAccessToken doRefreshAccessToken(
		Client client, RefreshToken oldRefreshToken,
		List<String> restrictedScopes) {

		List<OAuthPermission> oAuthPermissions = null;

		if (restrictedScopes.isEmpty()) {
			oAuthPermissions = (oldRefreshToken.getScopes() != null) ?
				new ArrayList<>(oldRefreshToken.getScopes()) : null;
		}
		else {
			oAuthPermissions = convertScopeToPermissions(
				client, restrictedScopes);

			List<OAuthPermission> originalScopes = oldRefreshToken.getScopes();

			if (!originalScopes.containsAll(oAuthPermissions)) {
				throw new OAuthServiceException("Invalid scopes");
			}
		}

		List<String> audiences = (oldRefreshToken.getAudiences() != null) ?
			new ArrayList<>(oldRefreshToken.getAudiences()) : null;

		ServerAccessToken serverAccessToken = _createOpaqueServerAccessToken(
			audiences, client, oldRefreshToken.getClientCodeVerifier(),
			oldRefreshToken.getGrantCode(), oldRefreshToken.getGrantType(),
			oldRefreshToken.getNonce(), oldRefreshToken.getExtraProperties(),
			oAuthPermissions, oldRefreshToken.getResponseType(),
			oldRefreshToken.getSubject());

		_customizeServerAccessToken(Collections.emptyMap(), serverAccessToken);

		if (isUseJwtFormatForAccessTokens()) {
			_convertToJWTAccessToken(serverAccessToken);
		}

		return serverAccessToken;
	}

	@Override
	protected void doRemoveClient(Client c) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String processJwtAccessToken(JwtClaims jwtClaims) {
		OAuthJoseJwtProducer oAuthJoseJwtProducer = getJwtAccessTokenProducer();

		// Fix a bug in CXF. See
		// https://datatracker.ietf.org/doc/html/rfc9068#section-2.1.

		JwsHeaders jwsHeaders = new JwsHeaders();

		jwsHeaders.setHeader("typ", "at+jwt");

		return oAuthJoseJwtProducer.processJwt(
			new JwtToken(jwsHeaders, jwtClaims));
	}

	@Override
	protected void saveAccessToken(ServerAccessToken serverAccessToken) {
		try {
			_invokeTransactionally(
				() -> _transactionalSaveServerAccessToken(serverAccessToken));
		}
		catch (Throwable throwable) {
			throw new OAuthServiceException(throwable);
		}
	}

	@Override
	protected void saveRefreshToken(RefreshToken refreshToken) {
		List<String> accessTokens = refreshToken.getAccessTokens();

		if (ListUtil.isEmpty(accessTokens)) {
			throw new OAuthServiceException("Unable to find granted token");
		}

		Iterator<String> iterator = accessTokens.iterator();

		String accessTokenContent = iterator.next();

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(
					accessTokenContent);

		oAuth2Authorization.setRefreshTokenContent(refreshToken.getTokenKey());

		Date createDate = _toDate(refreshToken.getIssuedAt());

		oAuth2Authorization.setRefreshTokenCreateDate(createDate);

		Date expirationDate = _toDate(
			refreshToken.getIssuedAt() + refreshToken.getExpiresIn());

		oAuth2Authorization.setRefreshTokenExpirationDate(expirationDate);

		_oAuth2AuthorizationLocalService.updateOAuth2Authorization(
			oAuth2Authorization);
	}

	private void _convertToJWTAccessToken(ServerAccessToken serverAccessToken) {
		String jose = processJwtAccessToken(
			createJwtAccessToken(serverAccessToken));

		if (isPersistJwtEncoding()) {
			serverAccessToken.setTokenKey(jose);
		}
		else {
			serverAccessToken.setEncodedToken(jose);
		}
	}

	private OAuthJoseJwtProducer _createJwtAccessTokenProducer() {
		OAuthJoseJwtProducer oAuthJoseJwtProducer = new OAuthJoseJwtProducer();

		oAuthJoseJwtProducer.setSignatureProvider(
			JwsUtils.getSignatureProvider(
				JwkUtils.readJwkKey(
					_oAuth2AuthorizationServerConfiguration.
						jwtAccessTokenSigningJSONWebKey())));

		return oAuthJoseJwtProducer;
	}

	private ServerAccessToken _createOpaqueServerAccessToken(
		List<String> audiences, Client client, String clientCodeVerifier,
		String grantCode, String grantType, String nonce,
		Map<String, String> properties, List<OAuthPermission> oAuthPermissions,
		String responseType, UserSubject userSubject) {

		ServerAccessToken serverAccessToken = createNewAccessToken(
			client, userSubject);

		Map<String, String> extraProperties =
			serverAccessToken.getExtraProperties();

		extraProperties.putAll(properties);

		serverAccessToken.setAudiences(audiences);
		serverAccessToken.setClientCodeVerifier(clientCodeVerifier);
		serverAccessToken.setGrantCode(grantCode);
		serverAccessToken.setGrantType(grantType);
		serverAccessToken.setNonce(nonce);
		serverAccessToken.setResponseType(responseType);
		serverAccessToken.setScopes(oAuthPermissions);
		serverAccessToken.setSubject(userSubject);

		return serverAccessToken;
	}

	private void _customizeServerAccessToken(
		Map<String, String> extraProperties,
		ServerAccessToken serverAccessToken) {

		BearerTokenProvider.AccessToken accessToken = fromCXFAccessToken(
			serverAccessToken);

		UserSubject userSubject = serverAccessToken.getSubject();

		userSubject.setId(String.valueOf(accessToken.getUserId()));
		userSubject.setLogin(accessToken.getUserName());

		OAuth2Application oAuth2Application =
			accessToken.getOAuth2Application();

		BearerTokenProvider bearerTokenProvider = getBearerTokenProvider(
			oAuth2Application.getCompanyId(), oAuth2Application.getClientId());

		bearerTokenProvider.onBeforeCreate(accessToken);

		serverAccessToken.setAudiences(accessToken.getAudiences());
		serverAccessToken.setClientCodeVerifier(
			accessToken.getClientCodeVerifier());
		serverAccessToken.setExpiresIn(accessToken.getExpiresIn());
		serverAccessToken.setExtraProperties(accessToken.getExtraProperties());
		serverAccessToken.setGrantCode(accessToken.getGrantCode());
		serverAccessToken.setGrantType(accessToken.getGrantType());
		serverAccessToken.setIssuedAt(accessToken.getIssuedAt());
		serverAccessToken.setIssuer(accessToken.getIssuer());
		serverAccessToken.setNonce(accessToken.getNonce());

		Map<String, String> accessTokenParameters = accessToken.getParameters();

		accessTokenParameters.putAll(extraProperties);

		serverAccessToken.setParameters(accessTokenParameters);

		serverAccessToken.setRefreshToken(accessToken.getRefreshToken());
		serverAccessToken.setResponseType(accessToken.getResponseType());
		serverAccessToken.setScopes(
			convertScopeToPermissions(
				serverAccessToken.getClient(), accessToken.getScopes()));
		serverAccessToken.setTokenKey(accessToken.getTokenKey());
		serverAccessToken.setTokenType(accessToken.getTokenType());
	}

	private Collection<LiferayOAuth2Scope> _getLiferayOAuth2Scopes(
		long oAuth2ApplicationScopeAliasesId, List<String> scopeAliases) {

		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
			_oAuth2ApplicationScopeAliasesLocalService.
				fetchOAuth2ApplicationScopeAliases(
					oAuth2ApplicationScopeAliasesId);

		if (oAuth2ApplicationScopeAliases == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
				oAuth2ApplicationScopeAliasesId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			oAuth2ScopeGrant -> {
				if (Collections.disjoint(
						oAuth2ScopeGrant.getScopeAliasesList(), scopeAliases)) {

					return null;
				}

				LiferayOAuth2Scope liferayOAuth2Scope =
					_scopeLocator.getLiferayOAuth2Scope(
						oAuth2ScopeGrant.getCompanyId(),
						oAuth2ScopeGrant.getApplicationName(),
						oAuth2ScopeGrant.getScope());

				Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
					_scopeLocator.getLiferayOAuth2Scopes(
						oAuth2ApplicationScopeAliases.getCompanyId());

				if ((liferayOAuth2Scope == null) ||
					!liferayOAuth2Scopes.contains(liferayOAuth2Scope)) {

					return null;
				}

				return liferayOAuth2Scope;
			});
	}

	private String _getRemoteIP() {
		MessageContext messageContext = getMessageContext();

		HttpServletRequest httpServletRequest =
			messageContext.getHttpServletRequest();

		return httpServletRequest.getRemoteAddr() + " - " +
			httpServletRequest.getRemoteHost();
	}

	private User _getUser(UserSubject userSubject) throws Exception {
		Map<String, String> properties = userSubject.getProperties();

		long companyId = GetterUtil.getLong(
			properties.get(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID));

		String subject = properties.get("UUID");

		if (subject != null) {
			return _userLocalService.getUserByUuidAndCompanyId(
				subject, companyId);
		}

		subject = properties.get(CompanyConstants.AUTH_TYPE_EA);

		if (subject != null) {
			return _userLocalService.getUserByEmailAddress(companyId, subject);
		}

		subject = properties.get(CompanyConstants.AUTH_TYPE_SN);

		if (subject != null) {
			return _userLocalService.getUserByScreenName(companyId, subject);
		}

		return _userLocalService.getUser(
			GetterUtil.getLong(userSubject.getId()));
	}

	private void _init() {
		setUseJwtFormatForAccessTokens(
			_oAuth2AuthorizationServerConfiguration.issueJWTAccessToken());
	}

	private void _invokeTransactionally(Runnable runnable) throws Throwable {
		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class<?>[] {Exception.class}),
			() -> {
				runnable.run();

				return null;
			});
	}

	private ServerAccessToken _populateAccessToken(
			OAuth2Authorization oAuth2Authorization)
		throws PortalException {

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				oAuth2Authorization.getOAuth2ApplicationId());

		if (oAuth2Application == null) {
			throw new SystemException(
				"No application found for authorization " +
					oAuth2Authorization);
		}

		Client client = getClient(oAuth2Application.getClientId());

		long expires = _toCXFTime(
			oAuth2Authorization.getAccessTokenExpirationDate());
		long issuedAt = _toCXFTime(
			oAuth2Authorization.getAccessTokenCreateDate());

		long lifetime = expires - issuedAt;

		ServerAccessToken serverAccessToken = new BearerAccessToken(
			client, oAuth2Authorization.getAccessTokenContent(), lifetime,
			issuedAt);

		serverAccessToken.setSubject(
			_populateUserSubject(
				oAuth2Authorization.getCompanyId(),
				oAuth2Authorization.getUserId(),
				oAuth2Authorization.getUserName()));

		List<OAuthPermission> oAuth2Permissions = convertScopeToPermissions(
			client,
			_oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
				oAuth2Authorization.getOAuth2ApplicationScopeAliasesId()));

		serverAccessToken.setScopes(oAuth2Permissions);

		Map<String, String> extraProperties =
			serverAccessToken.getExtraProperties();

		extraProperties.put(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID,
			String.valueOf(oAuth2Authorization.getCompanyId()));

		return serverAccessToken;
	}

	private Client _populateClient(
		OAuth2Application oAuth2Application, MessageContext messageContext) {

		String clientSecret = oAuth2Application.getClientSecret();

		if (Validator.isBlank(clientSecret)) {
			clientSecret = null;
		}

		String clientAuthenticationMethod =
			oAuth2Application.getClientAuthenticationMethod();

		Client client = new Client(
			oAuth2Application.getClientId(), clientSecret,
			!clientAuthenticationMethod.equals(
				OAuthConstants.TOKEN_ENDPOINT_AUTH_NONE),
			oAuth2Application.getName(), oAuth2Application.getHomePageURL());

		List<String> clientGrantTypes = client.getAllowedGrantTypes();

		try {
			OAuth2ProviderConfiguration oAuth2ProviderConfiguration =
				_configurationProvider.getSystemConfiguration(
					OAuth2ProviderConfiguration.class);

			for (GrantType allowedGrantType :
					oAuth2Application.getAllowedGrantTypesList()) {

				if (oAuth2ProviderConfiguration.allowAuthorizationCodeGrant() &&
					(allowedGrantType == GrantType.AUTHORIZATION_CODE)) {

					clientGrantTypes.add(
						OAuthConstants.AUTHORIZATION_CODE_GRANT);
				}
				else if (oAuth2ProviderConfiguration.
							allowAuthorizationCodePKCEGrant() &&
						 (allowedGrantType ==
							 GrantType.AUTHORIZATION_CODE_PKCE)) {

					clientGrantTypes.add(
						OAuthConstants.AUTHORIZATION_CODE_GRANT);
					clientGrantTypes.add(
						OAuth2ProviderRESTEndpointConstants.
							AUTHORIZATION_CODE_PKCE_GRANT);
				}
				else if (oAuth2ProviderConfiguration.
							allowClientCredentialsGrant() &&
						 (allowedGrantType == GrantType.CLIENT_CREDENTIALS)) {

					clientGrantTypes.add(
						OAuthConstants.CLIENT_CREDENTIALS_GRANT);
				}
				else if (oAuth2ProviderConfiguration.allowJWTBearerGrant() &&
						 (allowedGrantType == GrantType.JWT_BEARER)) {

					clientGrantTypes.add(Constants.JWT_BEARER_GRANT);
					clientGrantTypes.add(
						HttpUtils.urlEncode(
							Constants.JWT_BEARER_GRANT,
							StandardCharsets.UTF_8.name()));
				}
				else if (oAuth2ProviderConfiguration.
							allowResourceOwnerPasswordCredentialsGrant() &&
						 (allowedGrantType ==
							 GrantType.RESOURCE_OWNER_PASSWORD)) {

					clientGrantTypes.add(OAuthConstants.RESOURCE_OWNER_GRANT);
				}
				else if (oAuth2ProviderConfiguration.allowRefreshTokenGrant() &&
						 (allowedGrantType == GrantType.REFRESH_TOKEN)) {

					clientGrantTypes.add(OAuthConstants.REFRESH_TOKEN_GRANT);
				}
				else {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unknown or disabled grant type " +
								allowedGrantType);
					}
				}
			}
		}
		catch (ConfigurationException configurationException) {
			throw new OAuthServiceException(
				"Unable to get system configuration: " +
					OAuth2ProviderConfiguration.class.getName(),
				configurationException);
		}

		// CXF considers no allowed grant types as allow all

		if (clientGrantTypes.isEmpty()) {
			clientGrantTypes.add(StringPool.BLANK);
		}

		client.setApplicationDescription(oAuth2Application.getDescription());

		if (oAuth2Application.getOAuth2ApplicationScopeAliasesId() > 0) {
			client.setRegisteredScopes(
				_oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
					oAuth2Application.getOAuth2ApplicationScopeAliasesId()));
		}

		HttpServletRequest httpServletRequest = null;

		if (messageContext != null) {
			httpServletRequest = messageContext.getHttpServletRequest();
		}

		client.setRedirectUris(
			OAuth2RedirectURIInterpolator.interpolateRedirectURIsList(
				httpServletRequest, oAuth2Application.getRedirectURIsList(),
				_portal));
		client.setSubject(
			_populateUserSubject(
				oAuth2Application.getCompanyId(),
				oAuth2Application.getClientCredentialUserId(),
				oAuth2Application.getClientCredentialUserName()));

		if (!clientAuthenticationMethod.equals(
				OAuthConstants.TOKEN_ENDPOINT_AUTH_POST)) {

			client.setTokenEndpointAuthMethod(clientAuthenticationMethod);
		}

		Map<String, String> properties = client.getProperties();

		properties.put(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID,
			String.valueOf(oAuth2Application.getCompanyId()));
		properties.put(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_FEATURES,
			oAuth2Application.getFeatures());
		properties.put(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_JWKS,
			oAuth2Application.getJwks());
		properties.put(
			OAuth2ProviderRESTEndpointConstants.
				PROPERTY_KEY_CLIENT_REMEMBER_DEVICE,
			String.valueOf(oAuth2Application.isRememberDevice()));
		properties.put(
			OAuth2ProviderRESTEndpointConstants.
				PROPERTY_KEY_CLIENT_TRUSTED_APPLICATION,
			String.valueOf(oAuth2Application.isTrustedApplication()));

		for (String feature : oAuth2Application.getFeaturesList()) {
			properties.put(
				OAuth2ProviderRESTEndpointConstants.
					PROPERTY_KEY_CLIENT_FEATURE_PREFIX + feature,
				feature);
		}

		return client;
	}

	private UserSubject _populateUserSubject(
		long companyId, long userId, String userName) {

		UserSubject userSubject = new UserSubject(
			userName, String.valueOf(userId));

		Map<String, String> properties = userSubject.getProperties();

		properties.put(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID,
			String.valueOf(companyId));

		return userSubject;
	}

	private long _toCXFTime(Date dateCreated) {
		return dateCreated.getTime() / 1000;
	}

	private Date _toDate(long issuedAt) {
		return new Date(issuedAt * 1000);
	}

	private void _transactionalSaveServerAccessToken(
		ServerAccessToken serverAccessToken) {

		Date createDate = _toDate(serverAccessToken.getIssuedAt());
		Date expirationDate = _toDate(
			serverAccessToken.getIssuedAt() + serverAccessToken.getExpiresIn());

		if (serverAccessToken.getRefreshToken() != null) {
			OAuth2Authorization oAuth2Authorization =
				_oAuth2AuthorizationLocalService.
					fetchOAuth2AuthorizationByRefreshTokenContent(
						serverAccessToken.getRefreshToken());

			oAuth2Authorization.setAccessTokenContent(
				serverAccessToken.getTokenKey());
			oAuth2Authorization.setAccessTokenCreateDate(createDate);
			oAuth2Authorization.setAccessTokenExpirationDate(expirationDate);

			_oAuth2AuthorizationLocalService.updateOAuth2Authorization(
				oAuth2Authorization);

			return;
		}

		Client client = serverAccessToken.getClient();

		OAuth2Application oAuth2Application = resolveOAuth2Application(client);

		long userId = 0;
		String userName = StringPool.BLANK;

		if (serverAccessToken.getSubject() != null) {
			try {
				User user = _getUser(serverAccessToken.getSubject());

				userId = user.getUserId();

				userName = user.getFullName();
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		Map<String, String> properties = client.getProperties();

		String remoteAddr = properties.get(
			OAuth2ProviderRESTEndpointConstants.
				PROPERTY_KEY_CLIENT_REMOTE_ADDR);
		String remoteHost = properties.get(
			OAuth2ProviderRESTEndpointConstants.
				PROPERTY_KEY_CLIENT_REMOTE_HOST);

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.addOAuth2Authorization(
				oAuth2Application.getCompanyId(), userId, userName,
				oAuth2Application.getOAuth2ApplicationId(),
				oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
				serverAccessToken.getTokenKey(), createDate, expirationDate,
				remoteHost, remoteAddr, null, null, null);

		List<String> scopeAliasesList =
			OAuthUtils.convertPermissionsToScopeList(
				serverAccessToken.getScopes());

		try {
			_oAuth2ScopeGrantLocalService.grantLiferayOAuth2Scopes(
				oAuth2Authorization.getOAuth2AuthorizationId(),
				_getLiferayOAuth2Scopes(
					oAuth2Authorization.getOAuth2ApplicationScopeAliasesId(),
					scopeAliasesList));
		}
		catch (PortalException portalException) {
			_log.error("Unable to find authorization " + oAuth2Authorization);

			throw new OAuthServiceException(
				"Unable to grant scope for token", portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayOAuthDataProvider.class);

	private static final Set<String> _refreshTokenIncompatibleGrants =
		new HashSet<>();

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile BearerTokenProviderAccessor _bearerTokenProviderAccessor;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	private OAuth2AuthorizationServerConfiguration
		_oAuth2AuthorizationServerConfiguration;

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	private final DCLSingleton<OAuthJoseJwtProducer>
		_oAuthJoseJwtProducerDCLSingleton = new DCLSingleton<>();

	@Reference
	private Portal _portal;

	@Reference
	private ScopeLocator _scopeLocator;

	@Reference
	private ServerAuthorizationCodeGrantProvider
		_serverAuthorizationCodeGrantProvider;

	@Reference
	private UserLocalService _userLocalService;

}
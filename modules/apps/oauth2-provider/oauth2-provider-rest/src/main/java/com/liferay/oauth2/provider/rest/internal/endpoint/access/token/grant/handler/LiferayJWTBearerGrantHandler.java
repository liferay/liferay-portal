/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.grant.handler;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.rest.internal.configuration.OAuth2InAssertionConfiguration;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jwk.PublicKeyUse;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.jwt.Constants;
import org.apache.cxf.rs.security.oauth2.grants.jwt.JwtBearerGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	service = AccessTokenGrantHandler.class
)
public class LiferayJWTBearerGrantHandler extends BaseAccessTokenGrantHandler {

	@Override
	public List<String> getSupportedGrantTypes() {
		AccessTokenGrantHandler accessTokenGrantHandler =
			_getAccessTokenGrantHandler();

		return accessTokenGrantHandler.getSupportedGrantTypes();
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new OAuth2InAssertionManagedServiceFactory(),
			MapUtil.singletonDictionary(
				org.osgi.framework.Constants.SERVICE_PID,
				"com.liferay.oauth2.provider.rest.internal.configuration." +
					"OAuth2InAssertionConfiguration"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Override
	protected ServerAccessToken doCreateAccessToken(
		Client client, MultivaluedMap<String, String> params) {

		AccessTokenGrantHandler accessTokenGrantHandler =
			_getAccessTokenGrantHandler();

		return accessTokenGrantHandler.createAccessToken(client, params);
	}

	@Override
	protected boolean hasPermission(
		Client client, MultivaluedMap<String, String> multivaluedMap) {

		if (multivaluedMap.getFirst(Constants.CLIENT_GRANT_ASSERTION_PARAM) !=
				null) {

			return true;
		}

		return false;
	}

	@Override
	protected boolean isGrantHandlerEnabled() {
		return _oAuth2ProviderConfiguration.allowJWTBearerGrant();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_jwsSignatureVerifiers.put(
			CompanyConstants.SYSTEM, Collections.emptyMap());
		_oAuth2ProviderConfiguration = ConfigurableUtil.createConfigurable(
			OAuth2ProviderConfiguration.class, properties);
		_userAuthTypes.put(CompanyConstants.SYSTEM, Collections.emptyMap());
	}

	private AccessTokenGrantHandler _getAccessTokenGrantHandler() {
		CustomJWTBearerGrantHandler customJWTBearerGrantHandler =
			new CustomJWTBearerGrantHandler();

		customJWTBearerGrantHandler.setDataProvider(_liferayOAuthDataProvider);

		return customJWTBearerGrantHandler;
	}

	private JwsSignatureVerifier _getJWSSignatureVerifier(
			long companyId, String issuer, String kid)
		throws IllegalArgumentException {

		StringBundler sb = new StringBundler(12);

		Map<String, Map<String, JwsSignatureVerifier>> jwsSignatureVerifiers =
			_jwsSignatureVerifiers.getOrDefault(
				companyId, _jwsSignatureVerifiers.get(CompanyConstants.SYSTEM));

		if (jwsSignatureVerifiers == null) {
			sb.append("No JWS signature keys in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		Map<String, JwsSignatureVerifier> kidsJWSSignatureVerifiers =
			jwsSignatureVerifiers.get(issuer);

		if (kidsJWSSignatureVerifiers == null) {
			sb.append("No JWS signature keys for issuer: ");
			sb.append(issuer);
			sb.append(", in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		if (!kidsJWSSignatureVerifiers.containsKey(kid)) {
			sb.append("No JWS signature key of KID: ");
			sb.append(kid);
			sb.append(", for issuer: ");
			sb.append(issuer);
			sb.append(", in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		return kidsJWSSignatureVerifiers.get(kid);
	}

	private String _getUserAuthType(long companyId, String issuer)
		throws IllegalArgumentException {

		StringBundler sb = new StringBundler(6);

		Map<String, String> userAuthTypes = _userAuthTypes.getOrDefault(
			companyId, _userAuthTypes.get(CompanyConstants.SYSTEM));

		if (userAuthTypes == null) {
			sb.append("No user auth types in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		if (!userAuthTypes.containsKey(issuer)) {
			sb.append("No user auth type for issuer: ");
			sb.append(issuer);
			sb.append(", in company: ");
			sb.append(companyId);

			throw new IllegalArgumentException(sb.toString());
		}

		return userAuthTypes.get(issuer);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayJWTBearerGrantHandler.class);

	private final Map<String, Dictionary<String, ?>>
		_configurationPidsProperties = new ConcurrentHashMap<>();
	private final Map<Long, Map<String, Map<String, JwsSignatureVerifier>>>
		_jwsSignatureVerifiers = new ConcurrentHashMap<>();

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	private volatile OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;
	private final Map<Long, Map<String, String>> _userAuthTypes =
		new ConcurrentHashMap<>();

	private class CustomJWTBearerGrantHandler extends JwtBearerGrantHandler {

		@Override
		public ServerAccessToken createAccessToken(
				Client client, MultivaluedMap<String, String> multivaluedMap)
			throws OAuthServiceException {

			String assertion = multivaluedMap.getFirst(
				Constants.CLIENT_GRANT_ASSERTION_PARAM);

			Map<String, String> clientProperties = client.getProperties();

			long companyId = GetterUtil.getLong(
				clientProperties.get(
					OAuth2ProviderRESTEndpointConstants.
						PROPERTY_KEY_COMPANY_ID));

			try {
				JwsJwtCompactConsumer jwsJwtCompactConsumer = getJwsReader(
					assertion);

				JwtToken jwtToken = jwsJwtCompactConsumer.getJwtToken();

				JwtClaims jwtClaims = jwtToken.getClaims();
				JwsHeaders jwsHeaders = jwtToken.getJwsHeaders();

				if (StringUtil.equals(
						jwsHeaders.getAlgorithm(),
						SignatureAlgorithm.RS256.getJwaName())) {

					_initGrantHandler(companyId, jwtClaims, jwsHeaders);
				}

				validateSignature(
					new JwsHeaders(jwsHeaders),
					jwsJwtCompactConsumer.getUnsignedEncodedSequence(),
					jwsJwtCompactConsumer.getDecodedSignature());

				validateClaims(client, jwtClaims);

				return doCreateAccessToken(
					client,
					_createUserSubject(
						companyId, jwtClaims.getIssuer(),
						jwtClaims.getSubject()),
					Constants.JWT_BEARER_GRANT,
					OAuthUtils.parseScope(
						multivaluedMap.getFirst(OAuthConstants.SCOPE)));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to create access token", exception);
				}

				throw new OAuthServiceException(exception);
			}
		}

		private UserSubject _createUserSubject(
			long companyId, String issuer, String subject) {

			String userAuthType = null;

			try {
				userAuthType = _getUserAuthType(companyId, issuer);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				if (_log.isWarnEnabled()) {
					_log.warn(illegalArgumentException);
				}

				throw new OAuthServiceException(OAuthConstants.INVALID_GRANT);
			}

			UserSubject userSubject = new UserSubject(StringPool.BLANK);

			if (userAuthType.equals(CompanyConstants.AUTH_TYPE_ID)) {

				// Compatibility with existing design

				userSubject.setId(subject);

				return userSubject;
			}

			Map<String, String> properties = userSubject.getProperties();

			properties.put(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID,
				String.valueOf(companyId));
			properties.put(userAuthType, subject);

			return userSubject;
		}

		private void _initGrantHandler(
			long companyId, JwtClaims jwtClaims, JwsHeaders jwsHeaders) {

			JwsSignatureVerifier jwsSignatureVerifier = null;

			try {
				jwsSignatureVerifier = _getJWSSignatureVerifier(
					companyId, jwtClaims.getIssuer(), jwsHeaders.getKeyId());
			}
			catch (IllegalArgumentException illegalArgumentException) {
				if (_log.isWarnEnabled()) {
					_log.warn(illegalArgumentException);
				}

				throw new OAuthServiceException(OAuthConstants.INVALID_GRANT);
			}

			setJwsVerifier(jwsSignatureVerifier);
		}

	}

	private class OAuth2InAssertionManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			Dictionary<String, ?> properties =
				_configurationPidsProperties.remove(pid);

			long companyId = GetterUtil.getLong(properties.get("companyId"));

			if (companyId == CompanyConstants.SYSTEM) {
				_rebuild();
			}
			else {
				_rebuild(companyId);
			}
		}

		@Override
		public String getName() {
			return StringPool.BLANK;
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> properties)
			throws ConfigurationException {

			Dictionary<String, ?> oldProperties =
				_configurationPidsProperties.put(pid, properties);

			long companyId = GetterUtil.getLong(
				properties.get("companyId"), CompanyConstants.SYSTEM);

			if (companyId == CompanyConstants.SYSTEM) {
				_rebuild();

				return;
			}

			if (oldProperties != null) {
				long oldCompanyId = GetterUtil.getLong(
					oldProperties.get("companyId"));

				if (oldCompanyId == CompanyConstants.SYSTEM) {
					_rebuild();

					return;
				}

				if (oldCompanyId != companyId) {
					_rebuild(oldCompanyId);
				}
			}

			_rebuild(companyId);
		}

		private <U, V> void _addDefaults(Map<U, V> map, Map<U, V> defaultsMap) {
			if (defaultsMap != null) {
				defaultsMap.forEach(map::putIfAbsent);
			}
		}

		private void _rebuild() {
			_rebuild(CompanyConstants.SYSTEM);

			for (Long key : _jwsSignatureVerifiers.keySet()) {
				if (key == CompanyConstants.SYSTEM) {
					continue;
				}

				_rebuild(key);
			}
		}

		private void _rebuild(long companyId) {
			Map<String, Map<String, JwsSignatureVerifier>>
				jwsSignatureVerifiers = new HashMap<>();
			Map<String, String> userAuthTypes = new HashMap<>();

			for (Dictionary<String, ?> properties :
					_configurationPidsProperties.values()) {

				if (companyId != GetterUtil.getLong(
						properties.get("companyId"))) {

					continue;
				}

				OAuth2InAssertionConfiguration oAuth2InAssertionConfiguration =
					ConfigurableUtil.createConfigurable(
						OAuth2InAssertionConfiguration.class, properties);

				String issuer = oAuth2InAssertionConfiguration.issuer();

				if (jwsSignatureVerifiers.containsKey(issuer)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Duplicate issuer name ", issuer, " will be ",
								"discarded. Check your OAuth configuration."));
					}

					continue;
				}

				jwsSignatureVerifiers.put(issuer, new HashMap<>());

				userAuthTypes.put(
					issuer, oAuth2InAssertionConfiguration.userAuthType());

				Map<String, JwsSignatureVerifier> kidsJWSSignatureVerifiers =
					jwsSignatureVerifiers.get(issuer);

				JsonWebKeys jsonWebKeys = JwkUtils.readJwkSet(
					oAuth2InAssertionConfiguration.signatureJSONWebKeySet());

				for (JsonWebKey jsonWebKey : jsonWebKeys.getKeys()) {
					PublicKeyUse publicKeyUse = jsonWebKey.getPublicKeyUse();

					if ((publicKeyUse != null) &&
						(publicKeyUse.compareTo(PublicKeyUse.ENCRYPT) == 0)) {

						if (_log.isInfoEnabled()) {
							_log.info(
								"Encryption key " + jsonWebKey.getKeyId());
						}

						continue;
					}

					if (kidsJWSSignatureVerifiers.containsKey(
							jsonWebKey.getKeyId())) {

						if (_log.isWarnEnabled()) {
							_log.warn(
								StringBundler.concat(
									"Duplicate assertion signature key ",
									jsonWebKey.getKeyId(),
									" will be discarded. Check your OAuth ",
									"configuration."));
						}

						continue;
					}

					kidsJWSSignatureVerifiers.put(
						jsonWebKey.getKeyId(),
						JwsUtils.getSignatureVerifier(jsonWebKey));
				}
			}

			if (companyId != CompanyConstants.SYSTEM) {
				_addDefaults(
					jwsSignatureVerifiers,
					_jwsSignatureVerifiers.get(CompanyConstants.SYSTEM));
				_addDefaults(
					userAuthTypes, _userAuthTypes.get(CompanyConstants.SYSTEM));
			}

			_jwsSignatureVerifiers.put(companyId, jwsSignatureVerifiers);
			_userAuthTypes.put(companyId, userAuthTypes);
		}

	}

}
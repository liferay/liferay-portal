/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.configuration.instance.lifecycle;

import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.EveryNodeEveryStartup;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.internal.util.OpenIdConnectProviderUtil;

import java.net.URI;

import java.security.MessageDigest;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * A workaround to convert OIDC provider configuration during grace period. An
 * upgrade will replace this workaround when grace period ends. Also helps
 * backward compatible with a popular custom code during grace period.
 *
 * @author     Arthur Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @review
 */
@Component(service = PortalInstanceLifecycleListener.class)
@Deprecated
public class OpenIdConnectProviderPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener
	implements EveryNodeEveryStartup {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!_clusterMasterExecutor.isMaster()) {
			return;
		}

		_properties.forEach(
			(pid, properties) -> {
				if (GetterUtil.getLong(properties.get("companyId")) ==
						CompanyConstants.SYSTEM) {

					_updateOAuthClientEntry(
						company.getCompanyId(), null, properties);
				}
			});
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new OpenIdConnectProviderManagedServiceFactory(),
			MapUtil.singletonDictionary(
				Constants.SERVICE_PID,
				new String[] {
					"com.liferay.portal.security.sso.openid.connect.internal." +
						"configuration.OpenIdConnectProviderConfiguration",
					"com.liferay.portal.security.sso.openid.connect.internal." +
						"configuration.OpenIdConnectProviderConfiguration." +
							"scoped"
				}));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	private void _addOAuthClientEntry(
			Dictionary<String, ?> properties, long guestUserId)
		throws Exception {

		_oAuthClientEntryLocalService.addOAuthClientEntry(
			guestUserId, _generateAuthRequestParametersJSON(properties),
			_updateOAuthClientASLocalMetadata(guestUserId, properties),
			_generateCustomClaimsJSON(properties),
			_generateInfoJSON(properties),
			GetterUtil.getLong(
				properties.get("discoveryEndpointCacheInMillis")),
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			_generateTokenRequestParametersJSON(properties));
	}

	private String _deleteOAuthClientASLocalMetadata(
			Dictionary<String, ?> properties)
		throws Exception {

		String discoveryEndpoint = _getPropertyAsString(
			"discoveryEndpoint", properties);

		if (Validator.isNotNull(discoveryEndpoint)) {
			return discoveryEndpoint;
		}

		String localWellKnownURI = _generateLocalWellKnownURI(
			_getPropertyAsString("issuerURL", properties),
			_getPropertyAsString("tokenEndpoint", properties));

		_oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(localWellKnownURI);

		return localWellKnownURI;
	}

	private void _deleteOAuthClientEntries(Dictionary<String, ?> properties) {
		try {
			_companyLocalService.forEachCompanyId(
				companyId -> _deleteOAuthClientEntry(companyId, properties));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private void _deleteOAuthClientEntry(
		long companyId, Dictionary<String, ?> properties) {

		OpenIdConnectProviderUtil.removeOAuthClientEntryIdsByCompanyId(
			companyId);

		try {
			String authServerWellKnownURI = _deleteOAuthClientASLocalMetadata(
				properties);

			_oAuthClientEntryLocalService.deleteOAuthClientEntry(
				companyId, authServerWellKnownURI,
				_getPropertyAsString("openIdConnectClientId", properties));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private String _generateAuthRequestParametersJSON(
		Dictionary<String, ?> properties) {

		JSONObject requestParametersJSONObject =
			_generateRequestParametersJSONObject(
				"customAuthorizationRequestParameters", properties);

		return requestParametersJSONObject.put(
			"response_type", "code"
		).toString();
	}

	private String _generateClientName(Dictionary<String, ?> properties) {
		String providerName = _getPropertyAsString("providerName", properties);

		if (Validator.isNull(providerName)) {
			return null;
		}

		return _CLIENT_TO + providerName;
	}

	private String _generateCustomClaimsJSON(Dictionary<String, ?> properties) {
		JSONObject customClaimsJSONObject = _jsonFactory.createJSONObject();

		String[] customClaims = GetterUtil.getStringValues(
			properties.get("customClaims"));

		for (String customClaim : customClaims) {
			if (customClaim.isEmpty()) {
				continue;
			}

			String[] parts = customClaim.split(StringPool.EQUAL);

			customClaimsJSONObject.put(parts[0], parts[1]);
		}

		return customClaimsJSONObject.toString();
	}

	private String _generateInfoJSON(Dictionary<String, ?> properties) {
		return JSONUtil.put(
			"client_id",
			_getPropertyAsString("openIdConnectClientId", properties)
		).put(
			"client_name", _generateClientName(properties)
		).put(
			"client_secret",
			_getPropertyAsString("openIdConnectClientSecret", properties)
		).put(
			"grant_types",
			JSONUtil.putAll("authorization_code", "refresh_token")
		).put(
			"id_token_signed_response_alg",
			_getPropertyAsString("registeredIdTokenSigningAlg", properties)
		).put(
			"response_types", JSONUtil.put("code")
		).put(
			"scope", _getPropertyAsString("scopes", properties)
		).toString();
	}

	private String _generateLocalWellKnownURI(
			String issuer, String tokenEndpoint)
		throws Exception {

		URI issuerURI = URI.create(issuer);
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");

		return StringBundler.concat(
			issuerURI.getScheme(), "://", issuerURI.getAuthority(),
			"/.well-known/openid-configuration", issuerURI.getPath(), '/',
			Base64.encodeToURL(messageDigest.digest(tokenEndpoint.getBytes())),
			"/local");
	}

	private String _generateMetadataJSON(Dictionary<String, ?> properties) {
		return JSONUtil.put(
			"authorization_endpoint",
			_getPropertyAsString("authorizationEndpoint", properties)
		).put(
			"id_token_signing_alg_values_supported",
			_getPropertyAsJSONArray("idTokenSigningAlgValues", properties)
		).put(
			"issuer", _getPropertyAsString("issuerURL", properties)
		).put(
			"jwks_uri", _getPropertyAsString("jwksURI", properties)
		).put(
			"scopes_supported", _getPropertyAsJSONArray("scopes", properties)
		).put(
			"subject_types_supported",
			_getPropertyAsJSONArray("subjectTypes", properties)
		).put(
			"token_endpoint", _getPropertyAsString("tokenEndpoint", properties)
		).put(
			"userinfo_endpoint",
			_getPropertyAsString("userInfoEndpoint", properties)
		).toString();
	}

	private JSONObject _generateRequestParametersJSONObject(
		String key, Dictionary<String, ?> properties) {

		JSONObject requestParametersJSONObject = JSONUtil.put(
			"scope", _getPropertyAsString("scopes", properties));

		String[] parameters = GetterUtil.getStringValues(properties.get(key));

		if (parameters.length < 1) {
			return requestParametersJSONObject;
		}

		for (String parameter : parameters) {
			String[] parameterArray = parameter.split("=");

			if (parameterArray.length != 2) {
				if (_log.isDebugEnabled()) {
					_log.debug("Parameter: " + parameter + " is not valid");
				}
			}
			else if (parameterArray[0].equals("resource")) {
				JSONArray valuesJSONArray =
					requestParametersJSONObject.getJSONArray(parameterArray[0]);

				if (valuesJSONArray != null) {
					for (String value : parameterArray[1].split(" ")) {
						valuesJSONArray.put(value);
					}
				}
				else {
					requestParametersJSONObject.put(
						parameterArray[0],
						JSONUtil.putAll(
							(Object[])parameterArray[1].split(" ")));
				}
			}
			else {
				JSONObject customRequestParametersJSONObject =
					requestParametersJSONObject.getJSONObject(
						"custom_request_parameters");

				if (customRequestParametersJSONObject == null) {
					customRequestParametersJSONObject =
						_jsonFactory.createJSONObject();

					requestParametersJSONObject.put(
						"custom_request_parameters",
						customRequestParametersJSONObject);
				}

				JSONArray valuesJSONArray =
					customRequestParametersJSONObject.getJSONArray(
						parameterArray[0]);

				if (valuesJSONArray != null) {
					for (String value : parameterArray[1].split(" ")) {
						valuesJSONArray.put(value);
					}
				}
				else {
					customRequestParametersJSONObject.put(
						parameterArray[0],
						JSONUtil.putAll(
							(Object[])parameterArray[1].split(" ")));
				}
			}
		}

		return requestParametersJSONObject;
	}

	private String _generateTokenRequestParametersJSON(
		Dictionary<String, ?> properties) {

		JSONObject requestParametersJSONObject =
			_generateRequestParametersJSONObject(
				"customTokenRequestParameters", properties);

		return requestParametersJSONObject.put(
			"grant_type", "authorization_code"
		).toString();
	}

	private JSONArray _getPropertyAsJSONArray(
		String key, Dictionary<String, ?> properties) {

		if (properties.get(key) == null) {
			return null;
		}

		String[] values = null;

		if (key.equals("scopes")) {
			String scopes = _getPropertyAsString("scopes", properties);

			values = scopes.split(" ");
		}
		else {
			values = GetterUtil.getStringValues(properties.get(key));
		}

		if (values.length < 1) {
			return null;
		}

		return JSONUtil.putAll((Object[])values);
	}

	private String _getPropertyAsString(
		String key, Dictionary<String, ?> properties) {

		String value = (String)properties.get(key);

		if ((value == null) || value.equals("")) {
			return null;
		}

		return value;
	}

	private String _updateOAuthClientASLocalMetadata(
			long guestUserId, Dictionary<String, ?> properties)
		throws Exception {

		String discoveryEndpoint = _getPropertyAsString(
			"discoveryEndpoint", properties);

		if (Validator.isNotNull(discoveryEndpoint)) {
			return discoveryEndpoint;
		}

		String localWellKnownURI = _generateLocalWellKnownURI(
			_getPropertyAsString("issuerURL", properties),
			_getPropertyAsString("tokenEndpoint", properties));

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				fetchOAuthClientASLocalMetadata(localWellKnownURI);

		if (oAuthClientASLocalMetadata == null) {
			_oAuthClientASLocalMetadataLocalService.
				addOAuthClientASLocalMetadata(
					guestUserId, _generateMetadataJSON(properties),
					"openid-configuration");
		}
		else {
			_oAuthClientASLocalMetadataLocalService.
				updateOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId(),
					_generateMetadataJSON(properties), "openid-configuration");
		}

		return localWellKnownURI;
	}

	private void _updateOAuthClientEntry(
		long companyId, Dictionary<String, ?> oldProperties,
		Dictionary<String, ?> properties) {

		long guestUserId = 0;

		try {
			guestUserId = _userLocalService.getGuestUserId(companyId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get guest user ID for company " + companyId,
					portalException);
			}
		}

		try {
			if (oldProperties != null) {
				String oldAuthServerWellKnownURI =
					_updateOAuthClientASLocalMetadata(
						guestUserId, oldProperties);

				OAuthClientEntry oldOAuthClientEntry =
					_oAuthClientEntryLocalService.fetchOAuthClientEntry(
						companyId, oldAuthServerWellKnownURI,
						_getPropertyAsString(
							"openIdConnectClientId", oldProperties));

				if (oldOAuthClientEntry != null) {
					_oAuthClientEntryLocalService.updateOAuthClientEntry(
						oldOAuthClientEntry.getOAuthClientEntryId(),
						_generateAuthRequestParametersJSON(properties),
						_updateOAuthClientASLocalMetadata(
							guestUserId, properties),
						_generateCustomClaimsJSON(properties),
						_generateInfoJSON(properties),
						GetterUtil.getLong(
							properties.get("discoveryEndpointCacheInMillis")),
						oldOAuthClientEntry.getOIDCUserInfoMapperJSON(),
						_generateTokenRequestParametersJSON(properties));
				}
				else {
					_addOAuthClientEntry(properties, guestUserId);
				}
			}
			else {
				_addOAuthClientEntry(properties, guestUserId);
			}

			OpenIdConnectProviderUtil.removeOAuthClientEntryIdsByCompanyId(
				companyId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to update OAuth client entry", exception);
			}
		}
	}

	private static final String _CLIENT_TO = "Client to ";

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectProviderPortalInstanceLifecycleListener.class);

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	private final Map<String, Dictionary<String, ?>> _properties =
		new ConcurrentHashMap<>();
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

	private class OpenIdConnectProviderManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			Dictionary<String, ?> properties = _properties.remove(pid);

			long companyId = GetterUtil.getLong(properties.get("companyId"));

			if (companyId == CompanyConstants.SYSTEM) {
				_deleteOAuthClientEntries(properties);
			}
			else {
				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							companyId)) {

					_deleteOAuthClientEntry(companyId, properties);
				}
			}
		}

		@Override
		public String getName() {
			return "OpenId Connect Provider Managed Service Factory";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> properties) {
			long companyId = GetterUtil.getLong(properties.get("companyId"));

			Dictionary<String, ?> oldProperties = _properties.put(
				pid, properties);

			if (companyId == CompanyConstants.SYSTEM) {
				try {
					_companyLocalService.forEachCompanyId(
						curCompanyId -> _updateOAuthClientEntry(
							curCompanyId, oldProperties, properties));
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				return;
			}

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				_updateOAuthClientEntry(companyId, oldProperties, properties);
			}
		}

	}

}
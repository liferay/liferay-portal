/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.web.internal.display.context;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Christian Moura
 */
public class OpenIdConnectProviderConfigurationDisplayContext {

	public OpenIdConnectProviderConfigurationDisplayContext(
			ConfigurationAdmin configurationAdmin,
			ExpandoColumnLocalService expandoColumnLocalService,
			HttpServletRequest httpServletRequest)
		throws InvalidSyntaxException, IOException {

		_expandoColumnLocalService = expandoColumnLocalService;

		String filterString = null;

		if (Objects.equals(
				PortalUtil.getPortletId(httpServletRequest),
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {

			filterString = StringBundler.concat(
				"(&(companyId=", PortalUtil.getCompanyId(httpServletRequest),
				")(service.pid=", httpServletRequest.getParameter("pid"), "))");
		}
		else {
			filterString =
				"(service.pid=" + httpServletRequest.getParameter("pid") + ")";
		}

		Configuration[] configurations = configurationAdmin.listConfigurations(
			filterString);

		if (configurations != null) {
			_properties = configurations[0].getProperties();
		}
		else {
			String[] defaultValue = {StringPool.BLANK};

			_properties = HashMapDictionaryBuilder.<String, Object>put(
				"customAuthorizationRequestParameters", defaultValue
			).put(
				"customClaims", defaultValue
			).put(
				"customTokenRequestParameters", defaultValue
			).put(
				"discoveryEndpointCacheInMillis",
				OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT
			).put(
				"idTokenSigningAlgValues", new String[] {"RS256"}
			).put(
				"scopes", "openid email profile"
			).put(
				"subjectTypes", defaultValue
			).put(
				"tokenConnectionTimeout", 1000
			).build();
		}
	}

	public String getAuthorizationEndpoint() {
		return GetterUtil.getString(_properties.get("authorizationEndpoint"));
	}

	public String[] getCustomAuthorizationRequestParameters() {
		return GetterUtil.getStringValues(
			_properties.get("customAuthorizationRequestParameters"));
	}

	public int[] getCustomAuthorizationRequestParametersIndexes() {
		return _createIndexes(
			ArrayUtil.getLength(getCustomAuthorizationRequestParameters()));
	}

	public int[] getCustomClaimsIndexes() {
		return _createIndexes(
			ArrayUtil.getLength(
				GetterUtil.getStringValues(_properties.get("customClaims"))));
	}

	public String[] getCustomClaimsKeys() {
		String[] customClaims = GetterUtil.getStringValues(
			_properties.get("customClaims"));

		String[] customClaimsKeys = new String[customClaims.length];

		for (int i = 0; i < customClaims.length; i++) {
			String[] customClaim = customClaims[i].split(StringPool.EQUAL);

			customClaimsKeys[i] = customClaim[0];
		}

		return customClaimsKeys;
	}

	public String[] getCustomClaimsValues() {
		String[] customClaims = GetterUtil.getStringValues(
			_properties.get("customClaims"));

		String[] customClaimsValues = new String[customClaims.length];

		for (int i = 0; i < customClaimsValues.length; i++) {
			String[] customClaim = customClaims[i].split(StringPool.EQUAL);

			if (customClaim.length > 1) {
				customClaimsValues[i] = customClaim[1];
			}
			else {
				customClaimsValues[i] = StringPool.BLANK;
			}
		}

		return customClaimsValues;
	}

	public String[] getCustomTokenRequestParameters() {
		return GetterUtil.getStringValues(
			_properties.get("customTokenRequestParameters"));
	}

	public int[] getCustomTokenRequestParametersIndexes() {
		return _createIndexes(
			ArrayUtil.getLength(getCustomTokenRequestParameters()));
	}

	public String getDiscoveryEndpoint() {
		return GetterUtil.getString(_properties.get("discoveryEndpoint"));
	}

	public long getDiscoveryEndpointCacheInMillis() {
		return GetterUtil.getLong(
			_properties.get("discoveryEndpointCacheInMillis"));
	}

	public List<ExpandoColumn> getExpandoColumns() {
		return _expandoColumnLocalService.getDefaultTableColumns(
			CompanyThreadLocal.getCompanyId(), User.class.getName());
	}

	public String[] getIdTokenSigningAlgValues() {
		return GetterUtil.getStringValues(
			_properties.get("idTokenSigningAlgValues"));
	}

	public int[] getIdTokenSigningAlgValuesIndexes() {
		return _createIndexes(
			ArrayUtil.getLength(getIdTokenSigningAlgValues()));
	}

	public String getIssuerURL() {
		return GetterUtil.getString(_properties.get("issuerURL"));
	}

	public String getJwksURI() {
		return GetterUtil.getString(_properties.get("jwksURI"));
	}

	public String getOpenIdConnectClientId() {
		return GetterUtil.getString(_properties.get("openIdConnectClientId"));
	}

	public String getOpenIdConnectClientSecret() {
		return GetterUtil.getString(
			_properties.get("openIdConnectClientSecret"));
	}

	public String getProviderName() {
		return GetterUtil.getString(_properties.get("providerName"));
	}

	public String getRegisteredIdTokenSigningAlg() {
		return GetterUtil.getString(
			_properties.get("registeredIdTokenSigningAlg"));
	}

	public String getScopes() {
		return GetterUtil.getString(_properties.get("scopes"));
	}

	public String[] getSubjectTypes() {
		return GetterUtil.getStringValues(_properties.get("subjectTypes"));
	}

	public int[] getSubjectTypesIndexes() {
		return _createIndexes(ArrayUtil.getLength(getSubjectTypes()));
	}

	public int getTokenConnectionTimeout() {
		return GetterUtil.getInteger(_properties.get("tokenConnectionTimeout"));
	}

	public String getTokenEndpoint() {
		return GetterUtil.getString(_properties.get("tokenEndpoint"));
	}

	public String getUserInfoEndpoint() {
		return GetterUtil.getString(_properties.get("userInfoEndpoint"));
	}

	private int[] _createIndexes(int size) {
		int[] indexes = new int[size];

		for (int i = 0; i < size; i++) {
			indexes[i] = i;
		}

		return indexes;
	}

	private final ExpandoColumnLocalService _expandoColumnLocalService;
	private final Dictionary<String, Object> _properties;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.sso.openid.connect.web.internal.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.web.internal.display.context.OpenIdConnectProviderConfigurationDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(service = ConfigurationFormRenderer.class)
public class OpenIdConnectProviderConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return "com.liferay.portal.security.sso.openid.connect.internal." +
			"configuration.OpenIdConnectProviderConfiguration";
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"authorizationEndpoint",
			ParamUtil.getString(httpServletRequest, "authorizationEndpoint")
		).put(
			"customAuthorizationRequestParameters",
			_getStringValues(
				httpServletRequest, false,
				"customAuthorizationRequestParameters")
		).put(
			"customClaims",
			_getStringValues(httpServletRequest, true, "customClaims")
		).put(
			"customTokenRequestParameters",
			_getStringValues(
				httpServletRequest, false, "customTokenRequestParameters")
		).put(
			"discoveryEndpoint",
			ParamUtil.getString(httpServletRequest, "discoveryEndpoint")
		).put(
			"discoveryEndpointCacheInMillis",
			ParamUtil.getLong(
				httpServletRequest, "discoveryEndpointCacheInMillis")
		).put(
			"idTokenSigningAlgValues",
			_getStringValues(
				httpServletRequest, false, "idTokenSigningAlgValues")
		).put(
			"issuerURL", ParamUtil.getString(httpServletRequest, "issuerURL")
		).put(
			"jwksURI", ParamUtil.getString(httpServletRequest, "jwksURI")
		).put(
			"openIdConnectClientId",
			ParamUtil.getString(httpServletRequest, "openIdConnectClientId")
		).put(
			"openIdConnectClientSecret",
			ParamUtil.getString(httpServletRequest, "openIdConnectClientSecret")
		).put(
			"providerName",
			ParamUtil.getString(httpServletRequest, "providerName")
		).put(
			"registeredIdTokenSigningAlg",
			ParamUtil.getString(
				httpServletRequest, "registeredIdTokenSigningAlg")
		).put(
			"scopes", ParamUtil.getString(httpServletRequest, "scopes")
		).put(
			"subjectTypes",
			_getStringValues(httpServletRequest, false, "subjectTypes")
		).put(
			"tokenConnectionTimeout",
			ParamUtil.getInteger(httpServletRequest, "tokenConnectionTimeout")
		).put(
			"tokenEndpoint",
			ParamUtil.getString(httpServletRequest, "tokenEndpoint")
		).put(
			"userInfoEndpoint",
			ParamUtil.getString(httpServletRequest, "userInfoEndpoint")
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/open_id_connect_configuration.jsp");

			httpServletRequest.setAttribute(
				OpenIdConnectWebKeys.
					OPEN_ID_CONNECT_PROVIDER_CONFIGURATION_DISPLAY_CONTEXT,
				new OpenIdConnectProviderConfigurationDisplayContext(
					_configurationAdmin, _expandoColumnLocalService,
					httpServletRequest));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render /open_id_connect_configuration.jsp",
				exception);
		}
	}

	private String _getKeyValueParameterValue(
		HttpServletRequest httpServletRequest, int index, String paramName) {

		String key = ParamUtil.getString(
			httpServletRequest, paramName + "Key-" + index);

		if (key.isEmpty()) {
			return StringPool.BLANK;
		}

		String value = ParamUtil.getString(
			httpServletRequest, paramName + "Value-" + index);

		if (value.isEmpty()) {
			return StringPool.BLANK;
		}

		return key + StringPool.EQUAL + value;
	}

	private String[] _getStringValues(
		HttpServletRequest httpServletRequest, boolean keyValueParam,
		String paramName) {

		List<String> values = new ArrayList<>();

		int[] indexes = ParamUtil.getIntegerValues(
			httpServletRequest, paramName + "Indexes");

		for (int index : indexes) {
			String value = null;

			if (keyValueParam) {
				value = _getKeyValueParameterValue(
					httpServletRequest, index, paramName);
			}
			else {
				value = ParamUtil.getString(
					httpServletRequest, paramName + StringPool.DASH + index);
			}

			if (value.isEmpty()) {
				continue;
			}

			values.add(value);
		}

		if (values.isEmpty()) {
			values.add(StringPool.BLANK);
		}

		return values.toArray(String[]::new);
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.sso.openid.connect.web)"
	)
	private ServletContext _servletContext;

}
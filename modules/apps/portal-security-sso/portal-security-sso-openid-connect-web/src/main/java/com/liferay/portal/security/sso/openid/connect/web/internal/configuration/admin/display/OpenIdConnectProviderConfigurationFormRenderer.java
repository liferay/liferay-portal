/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.sso.openid.connect.web.internal.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.web.internal.display.context.OpenIdConnectProviderConfigurationDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
			"authorizationEndPoint",
			ParamUtil.getString(httpServletRequest, "authorizationEndPoint")
		).put(
			"customAuthorizationRequestParameters",
			_populateParamValues(
				httpServletRequest, "customAuthorizationRequestParameters")
		).put(
			"customTokenRequestParameters",
			_populateParamValues(
				httpServletRequest, "customTokenRequestParameters")
		).put(
			"discoveryEndPoint",
			ParamUtil.getString(httpServletRequest, "discoveryEndPoint")
		).put(
			"discoveryEndPointCacheInMillis",
			ParamUtil.getLong(
				httpServletRequest, "discoveryEndPointCacheInMillis")
		).put(
			"idTokenSigningAlgValues",
			_populateParamValues(httpServletRequest, "idTokenSigningAlgValues")
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
			_populateParamValues(httpServletRequest, "subjectTypes")
		).put(
			"tokenConnectionTimeout",
			ParamUtil.getInteger(httpServletRequest, "tokenConnectionTimeout")
		).put(
			"tokenEndPoint",
			ParamUtil.getString(httpServletRequest, "tokenEndPoint")
		).put(
			"userInfoEndPoint",
			ParamUtil.getString(httpServletRequest, "userInfoEndPoint")
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			OpenIdConnectProviderConfigurationDisplayContext
				openIdConnectProviderConfigurationDisplayContext =
					new OpenIdConnectProviderConfigurationDisplayContext(
						_configurationAdmin,
						httpServletRequest.getParameter("pid"));

			httpServletRequest.setAttribute(
				OpenIdConnectWebKeys.
					OPEN_ID_CONNECT_PROVIDER_CONFIGURATION_DISPLAY_CONTEXT,
				openIdConnectProviderConfigurationDisplayContext);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/portal_settings/open_id_connect_configuration.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render /portal_settings" +
					"/open_id_connect_configuration.jsp",
				exception);
		}
	}

	private String[] _populateParamValues(
		HttpServletRequest httpServletRequest, String paramName) {

		int[] indexes = ParamUtil.getIntegerValues(
			httpServletRequest, paramName + "Indexes");

		String[] values = new String[indexes.length];

		for (int i = 0; i < values.length; i++) {
			values[i] = ParamUtil.getString(
				httpServletRequest, paramName + "-" + indexes[i]);
		}

		return values;
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.sso.openid.connect.web)"
	)
	private ServletContext _servletContext;

}
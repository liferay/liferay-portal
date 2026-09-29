/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.configuration;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rodrigo Guedes de Souza
 */
@Component(service = ConfigurationFormRenderer.class)
public class SearchResultsPortletInstanceConfigurationRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return SearchResultsPortletInstanceConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"displayStyle",
			ParamUtil.getString(httpServletRequest, "displayStyle")
		).put(
			"displayStyleGroupExternalReferenceCode",
			ParamUtil.getString(
				httpServletRequest, "displayStyleGroupExternalReferenceCode")
		).put(
			"displayStyleGroupId",
			ParamUtil.getLong(httpServletRequest, "displayStyleGroupId")
		).put(
			"limitResultCountAccuracy",
			_isLimitResultCountAccuracy(httpServletRequest)
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			SearchResultsPortletInstanceConfiguration.class.getName(),
			_getSearchResultsPortletInstanceConfiguration(httpServletRequest));

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/search/results/portlet_instance_configuration.jsp");
	}

	private SearchResultsPortletInstanceConfiguration
		_getSearchResultsPortletInstanceConfiguration(
			HttpServletRequest httpServletRequest) {

		try {
			if (Objects.equals(
					_portal.getPortletId(httpServletRequest),
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _configurationProvider.getCompanyConfiguration(
					SearchResultsPortletInstanceConfiguration.class,
					themeDisplay.getCompanyId());
			}

			return _configurationProvider.getSystemConfiguration(
				SearchResultsPortletInstanceConfiguration.class);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private boolean _isLimitResultCountAccuracy(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-98858")) {

			SearchResultsPortletInstanceConfiguration
				searchResultsPortletInstanceConfiguration =
					_getSearchResultsPortletInstanceConfiguration(
						httpServletRequest);

			return searchResultsPortletInstanceConfiguration.
				limitResultCountAccuracy();
		}

		return ParamUtil.getBoolean(
			httpServletRequest, "limitResultCountAccuracy");
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.search.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}
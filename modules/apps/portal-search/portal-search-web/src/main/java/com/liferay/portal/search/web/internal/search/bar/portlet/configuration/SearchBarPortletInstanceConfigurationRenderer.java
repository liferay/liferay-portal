/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.bar.portlet.configuration;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.rest.configuration.SearchSuggestionsCompanyConfiguration;
import com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletInstanceConfigurationDisplayContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = ConfigurationFormRenderer.class)
public class SearchBarPortletInstanceConfigurationRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return SearchBarPortletInstanceConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"displayStyle",
			ParamUtil.getString(httpServletRequest, "displayStyle")
		).put(
			"displayStyleGroupId",
			ParamUtil.getLong(httpServletRequest, "displayStyleGroupId")
		).put(
			"enableSuggestions",
			ParamUtil.getBoolean(httpServletRequest, "enableSuggestions")
		).put(
			"suggestionsContributorConfigurations",
			StringUtil.split(
				ParamUtil.getString(
					httpServletRequest, "suggestionsContributorConfigurations"),
				CharPool.PIPE)
		).put(
			"suggestionsDisplayThreshold",
			ParamUtil.getInteger(
				httpServletRequest, "suggestionsDisplayThreshold")
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		SearchBarPortletInstanceConfigurationDisplayContext
			searchBarPortletInstanceConfigurationDisplayContext =
				new SearchBarPortletInstanceConfigurationDisplayContext();

		SearchBarPortletInstanceConfiguration
			searchBarPortletInstanceConfiguration = _getConfiguration(
				httpServletRequest,
				SearchBarPortletInstanceConfiguration.class);

		searchBarPortletInstanceConfigurationDisplayContext.setDisplayStyle(
			searchBarPortletInstanceConfiguration.displayStyle());
		searchBarPortletInstanceConfigurationDisplayContext.
			setDisplayStyleGroupId(
				searchBarPortletInstanceConfiguration.displayStyleGroupId());
		searchBarPortletInstanceConfigurationDisplayContext.
			setEnableSuggestions(
				searchBarPortletInstanceConfiguration.enableSuggestions());
		searchBarPortletInstanceConfigurationDisplayContext.
			setSuggestionsContributorConfigurations(
				searchBarPortletInstanceConfiguration.
					suggestionsContributorConfigurations());
		searchBarPortletInstanceConfigurationDisplayContext.
			setSuggestionsDisplayThreshold(
				searchBarPortletInstanceConfiguration.
					suggestionsDisplayThreshold());

		SearchSuggestionsCompanyConfiguration
			searchSuggestionsCompanyConfiguration = _getConfiguration(
				httpServletRequest,
				SearchSuggestionsCompanyConfiguration.class);

		searchBarPortletInstanceConfigurationDisplayContext.
			setSuggestionsConfigurationVisible(
				searchSuggestionsCompanyConfiguration.
					enableSuggestionsEndpoint());

		httpServletRequest.setAttribute(
			SearchBarPortletInstanceConfigurationDisplayContext.class.getName(),
			searchBarPortletInstanceConfigurationDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/search/bar/portlet_instance_configuration.jsp");
	}

	private <T> T _getConfiguration(
		HttpServletRequest httpServletRequest, Class<T> clazz) {

		String portletId = _portal.getPortletId(httpServletRequest);

		try {
			if (Objects.equals(
					portletId,
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _configurationProvider.getCompanyConfiguration(
					clazz, themeDisplay.getCompanyId());
			}

			return _configurationProvider.getSystemConfiguration(clazz);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
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
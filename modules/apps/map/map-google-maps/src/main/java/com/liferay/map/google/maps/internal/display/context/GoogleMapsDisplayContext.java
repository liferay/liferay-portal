/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.google.maps.internal.display.context;

import com.liferay.map.constants.MapProviderWebKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class GoogleMapsDisplayContext {

	public GoogleMapsDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public String getCompanyGoogleMapsAPIKey() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletPreferences companyPortletPreferences =
			PrefsPropsUtil.getPreferences(themeDisplay.getCompanyId());

		return companyPortletPreferences.getValue("googleMapsAPIKey", null);
	}

	public String getConfigurationPrefix() {
		if (Validator.isNull(_configurationPrefix)) {
			_configurationPrefix = GetterUtil.getString(
				_httpServletRequest.getAttribute(
					MapProviderWebKeys.MAP_PROVIDER_CONFIGURATION_PREFIX),
				"TypeSettingsProperties");
		}

		return _configurationPrefix;
	}

	public String getGoogleMapsAPIKey() {
		if (_googleMapsAPIKey != null) {
			return _googleMapsAPIKey;
		}

		Group group = _getGroup();

		if (group == null) {
			_googleMapsAPIKey = getCompanyGoogleMapsAPIKey();

			return _googleMapsAPIKey;
		}

		_googleMapsAPIKey = GetterUtil.getString(
			group.getTypeSettingsProperty("googleMapsAPIKey"),
			getCompanyGoogleMapsAPIKey());

		return _googleMapsAPIKey;
	}

	private Group _getGroup() {
		Group group = (Group)_httpServletRequest.getAttribute("site.liveGroup");

		if (group != null) {
			return group;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		group = themeDisplay.getScopeGroup();

		if (!group.isControlPanel()) {
			return group;
		}

		return null;
	}

	private String _configurationPrefix;
	private String _googleMapsAPIKey;
	private final HttpServletRequest _httpServletRequest;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Kiana Suetani
 */
public class GooglePageSpeedConfigurationDisplayContext {

	public GooglePageSpeedConfigurationDisplayContext(
		HttpServletRequest httpServletRequest) {

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getViewProps() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"backURL", _getBackURL()
		).put(
			"domainsURL", "/o/seo-studio/domains"
		).put(
			"instancesURL", "/o/seo-studio/instances"
		).build();
	}

	private String _getBackURL() throws PortalException {
		Layout layout = LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
			_themeDisplay.getScopeGroupId(), false, "/configurations");

		if (layout == null) {
			return StringPool.BLANK;
		}

		return PortalUtil.getLayoutFullURL(layout, _themeDisplay);
	}

	private final ThemeDisplay _themeDisplay;

}
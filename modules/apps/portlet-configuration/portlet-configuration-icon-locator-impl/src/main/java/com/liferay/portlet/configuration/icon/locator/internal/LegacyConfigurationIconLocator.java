/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.icon.locator.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.configuration.icon.locator.PortletConfigurationIconLocator;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(service = PortletConfigurationIconLocator.class)
public class LegacyConfigurationIconLocator
	implements PortletConfigurationIconLocator {

	@Override
	public List<String> getDefaultViews(String portletId) {
		return Collections.emptyList();
	}

	@Override
	public String getPath(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getRootPortletId();

		Portlet portlet = _portletLocalService.getPortletById(portletId);

		if (portlet == null) {
			return StringPool.BLANK;
		}

		Map<String, String> initParams = portlet.getInitParams();

		boolean alwaysDisplayDefaultConfigurationIcons = GetterUtil.getBoolean(
			initParams.get("always-display-default-configuration-icons"));

		if (alwaysDisplayDefaultConfigurationIcons) {
			return StringPool.DASH;
		}

		return StringPool.BLANK;
	}

	@Reference
	private PortletLocalService _portletLocalService;

}
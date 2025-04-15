/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.sharing.web.internal.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortletConfigurationIcon.class)
public class FacebookPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "add-to-facebook");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletPreferences portletPreferences =
			portletDisplay.getPortletPreferences();

		String lfrFacebookAPIKey = portletPreferences.getValue(
			"lfrFacebookApiKey", StringPool.BLANK);

		return "http://www.facebook.com/add.php?api_key=" + lfrFacebookAPIKey +
			"&ref=pd";
	}

	@Override
	public double getWeight() {
		return 4.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletPreferences portletPreferences =
			portletDisplay.getPortletPreferences();

		String lfrFacebookAPIKey = portletPreferences.getValue(
			"lfrFacebookApiKey", StringPool.BLANK);
		String lfrFacebookCanvasPageURL = portletPreferences.getValue(
			"lfrFacebookCanvasPageUrl", StringPool.BLANK);
		boolean facebookShowAddAppLink = GetterUtil.getBoolean(
			portletPreferences.getValue("lfrFacebookShowAddAppLink", null),
			true);

		if (Validator.isNull(lfrFacebookCanvasPageURL) ||
			Validator.isNull(lfrFacebookAPIKey)) {

			facebookShowAddAppLink = false;
		}

		return facebookShowAddAppLink;
	}

	@Reference
	private Language _language;

}
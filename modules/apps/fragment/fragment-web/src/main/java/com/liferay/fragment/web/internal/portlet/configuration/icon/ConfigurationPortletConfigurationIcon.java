/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.configuration.icon;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
	service = PortletConfigurationIcon.class
)
public class ConfigurationPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "cog";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "configuration");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(portletResponse)
		).setMVCPath(
			"/configuration/icon/configuration.jsp"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setBackURL(
			themeDisplay.getURLCurrent()
		).buildString();
	}

	@Override
	public double getWeight() {
		return 101;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		return true;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}
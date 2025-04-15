/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.sharing.web.internal.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.sharing.web.internal.constants.PortletConfigurationSharingPortletKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortletConfigurationIcon.class)
public class NetvibesPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "add-to-netvibes");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
				portletRequest,
				PortletConfigurationSharingPortletKeys.
					PORTLET_CONFIGURATION_SHARING,
				PortletRequest.RENDER_PHASE);

			portletURL.setParameter(
				"netvibesURL", getWidgetURL(portletRequest));
			portletURL.setWindowState(LiferayWindowState.POP_UP);

			return portletURL.toString();
		}
		catch (Exception exception) {
			_log.error(exception);

			return StringPool.BLANK;
		}
	}

	@Override
	public double getWeight() {
		return 2.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletPreferences portletPreferences =
			portletDisplay.getPortletPreferences();

		return GetterUtil.getBoolean(
			portletPreferences.getValue(
				"lfrNetvibesShowAddAppLink", StringPool.BLANK));
	}

	@Override
	public boolean isUseDialog() {
		return true;
	}

	protected String getWidgetURL(PortletRequest portletRequest)
		throws PortalException {

		Portlet portlet = (Portlet)portletRequest.getAttribute(
			WebKeys.RENDER_PORTLET);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _portal.getWidgetURL(portlet, themeDisplay);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NetvibesPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}
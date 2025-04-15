/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortletConfigurationIcon.class)
public class ConfigurationPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getCssClass() {
		return "portlet-configuration portlet-configuration-icon";
	}

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

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		try {
			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					portletRequest,
					PortletConfigurationApplicationType.PortletConfiguration.
						CLASS_NAME,
					PortletProvider.Action.VIEW)
			).setMVCPath(
				"/edit_configuration.jsp"
			).setRedirect(
				themeDisplay.getURLCurrent()
			).setPortletResource(
				portletDisplay.getId()
			).setParameter(
				"portletConfiguration", true
			).setParameter(
				"resourcePrimKey",
				() -> {
					Portlet portlet = (Portlet)portletRequest.getAttribute(
						WebKeys.RENDER_PORTLET);

					return PortletPermissionUtil.getPrimaryKey(
						themeDisplay.getPlid(), portlet.getPortletId());
				}
			).setParameter(
				"returnToFullPageURL", themeDisplay.getURLCurrent()
			).setParameter(
				"settingsScope",
				() -> {
					String settingsScope = (String)portletRequest.getAttribute(
						WebKeys.SETTINGS_SCOPE);

					return ParamUtil.get(
						portletRequest, "settingsScope", settingsScope);
				}
			).buildString();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	@Override
	public double getWeight() {
		return 16.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isEmbeddedPersonalApplication() ||
			!layout.isTypeControlPanel()) {

			return false;
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.isShowConfigurationIcon();
	}

	@Override
	public boolean isShowInEditMode(PortletRequest portletRequest) {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationPortletConfigurationIcon.class);

	@Reference
	private Language _language;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.configuration.icon;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaime León Rosado
 */
@Component(service = PortletConfigurationIcon.class)
public class ExportPortletConfigurationIcon
	extends BaseExportImportPortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "export";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "export");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					portletRequest, ExportImportConfiguration.class.getName(),
					PortletProvider.Action.VIEW)
			).setMVCPath(
				"/revamp/export/view_export.jsp"
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
				"settingsScope",
				() -> {
					String settingsScope = (String)portletRequest.getAttribute(
						WebKeys.SETTINGS_SCOPE);

					return ParamUtil.get(
						portletRequest, "settingsScope", settingsScope);
				}
			).setWindowState(
				LiferayWindowState.MAXIMIZED
			).buildString();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportPortletConfigurationIcon.class);

	@Reference
	private Language _language;

}
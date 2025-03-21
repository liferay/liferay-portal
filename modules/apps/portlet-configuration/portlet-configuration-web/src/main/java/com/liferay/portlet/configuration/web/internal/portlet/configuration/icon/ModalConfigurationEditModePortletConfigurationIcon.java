/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.EditModePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = EditModePortletConfigurationIcon.class)
public class ModalConfigurationEditModePortletConfigurationIcon
	implements EditModePortletConfigurationIcon {

	@Override
	public String getIcon() {
		return "cog";
	}

	@Override
	public int getPortletConfigurationIconGroup() {
		return PORTLET_CONFIGURATION_ICON_GROUP_CONFIGURATION;
	}

	@Override
	public String getTitle(HttpServletRequest httpServletRequest) {
		return _language.get(httpServletRequest, "configuration");
	}

	@Override
	public String getURL(
		HttpServletRequest httpServletRequest, String portletResource) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Portlet portlet = _portletLocalService.getPortletById(
			themeDisplay.getCompanyId(), portletResource);

		try {
			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest,
					PortletConfigurationApplicationType.PortletConfiguration.
						CLASS_NAME,
					PortletProvider.Action.VIEW)
			).setMVCPath(
				() -> {
					if (portlet.getConfigurationActionInstance() != null) {
						return "/edit_configuration.jsp";
					}

					return "/edit_sharing.jsp";
				}
			).setPortletResource(
				portletResource
			).setParameter(
				"portletConfiguration", true
			).setParameter(
				"resourcePrimKey",
				PortletPermissionUtil.getPrimaryKey(
					themeDisplay.getPlid(), portletResource)
			).setParameter(
				"settingsScope",
				() -> {
					if (portlet.getConfigurationActionInstance() != null) {
						String settingsScope =
							(String)httpServletRequest.getAttribute(
								WebKeys.SETTINGS_SCOPE);

						settingsScope = ParamUtil.get(
							httpServletRequest, "settingsScope", settingsScope);

						if (Validator.isNotNull(settingsScope)) {
							return settingsScope;
						}
					}

					return null;
				}
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public boolean isShow(
		HttpServletRequest httpServletRequest, String portletResource) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Portlet portlet = _portletLocalService.getPortletById(
			themeDisplay.getCompanyId(), portletResource);

		try {
			if (PortletPermissionUtil.contains(
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
					portlet, ActionKeys.CONFIGURATION)) {

				return true;
			}

			if (!portlet.isPreferencesCompanyWide() &&
				portlet.isPreferencesUniquePerLayout() &&
				LayoutPermissionUtil.contains(
					themeDisplay.getPermissionChecker(),
					themeDisplay.getLayout(), ActionKeys.CUSTOMIZE)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ModalConfigurationEditModePortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private PortletLocalService _portletLocalService;

}
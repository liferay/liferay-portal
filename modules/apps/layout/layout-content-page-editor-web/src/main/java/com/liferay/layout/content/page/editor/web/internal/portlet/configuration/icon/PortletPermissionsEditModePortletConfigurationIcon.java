/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = EditModePortletConfigurationIcon.class)
public class PortletPermissionsEditModePortletConfigurationIcon
	implements EditModePortletConfigurationIcon {

	@Override
	public int getPortletConfigurationIconGroup() {
		return PORTLET_CONFIGURATION_ICON_GROUP_CONFIGURATION;
	}

	@Override
	public String getTitle(HttpServletRequest httpServletRequest) {
		return _language.get(httpServletRequest, "permissions");
	}

	@Override
	public String getURL(
		HttpServletRequest httpServletRequest, String portletResource) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest,
					PortletConfigurationApplicationType.PortletConfiguration.
						CLASS_NAME,
					PortletProvider.Action.VIEW)
			).setMVCPath(
				"/edit_permissions.jsp"
			).setPortletResource(
				portletResource
			).setParameter(
				"portletConfiguration", true
			).setParameter(
				"resourcePrimKey",
				PortletPermissionUtil.getPrimaryKey(
					themeDisplay.getPlid(), portletResource)
			).setParameter(
				"returnToFullPageURL",
				ParamUtil.getString(httpServletRequest, "returnToFullPageURL")
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
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
					themeDisplay.getLayout(), portlet.getPortletId(),
					ActionKeys.PERMISSIONS)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPermissionsEditModePortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private PortletLocalService _portletLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"panel.app.order:Integer=200",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_PUBLICATIONS
	},
	service = PanelApp.class
)
public class PublicationsPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CTPortletKeys.PUBLICATIONS;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = super.getPortletURL(httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_ctSettingsConfigurationHelper.isEnabled(
				themeDisplay.getCompanyId())) {

			portletURL.setParameter(
				"mvcRenderCommandName", "/change_tracking/view_settings");
		}

		return portletURL;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (PortletPermissionUtil.contains(
				permissionChecker, CTPortletKeys.PUBLICATIONS,
				ActionKeys.CONFIGURATION) ||
			(_ctSettingsConfigurationHelper.isEnabled(group.getCompanyId()) &&
			 PortletPermissionUtil.contains(
				 permissionChecker, CTPortletKeys.PUBLICATIONS,
				 ActionKeys.VIEW))) {

			return true;
		}

		return false;
	}

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference(
		target = "(jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS + ")"
	)
	private Portlet _portlet;

}
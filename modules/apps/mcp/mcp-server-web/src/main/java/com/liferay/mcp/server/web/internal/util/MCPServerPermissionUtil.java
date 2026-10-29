/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.util;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.definition.security.permission.resource.ObjectDefinitionPortletResourcePermissionRegistryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jorge González
 */
public class MCPServerPermissionUtil {

	public static FDSActionDropdownItem getPermissionsFDSActionDropdownItem(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		return new FDSActionDropdownItem(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					httpServletRequest,
					"com_liferay_portlet_configuration_web_portlet_" +
						"PortletConfigurationPortlet",
					ActionRequest.RENDER_PHASE)
			).setMVCPath(
				"/edit_permissions.jsp"
			).setRedirect(
				PortalUtil.getCurrentURL(httpServletRequest)
			).setParameter(
				"modelResource", objectDefinition.getClassName()
			).setParameter(
				"modelResourceDescription", "{name}"
			).setParameter(
				"resourcePrimKey", "{id}"
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString(),
			"password-policies", "permissions",
			LanguageUtil.get(httpServletRequest, "permissions"), "get",
			"permissions", "modal-permissions");
	}

	public static boolean hasAddObjectEntryPermission(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		PortletResourcePermission portletResourcePermission =
			ObjectDefinitionPortletResourcePermissionRegistryUtil.getService(
				objectDefinition.getResourceName());
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), 0,
			ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/site_settings/edit_menu_access_configuration"
	},
	service = MVCActionCommand.class
)
public class EditMenuAccessConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		GroupPermissionUtil.check(
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroup(),
			ActionKeys.UPDATE);

		String[] roleSearchContainerPrimaryKeys = ParamUtil.getStringValues(
			actionRequest, "roleSearchContainerPrimaryKeys");

		List<String> roleIds = new ArrayList<>();

		for (String roleId : roleSearchContainerPrimaryKeys) {
			Role role = _roleLocalService.fetchRole(Long.valueOf(roleId));

			if (role != null) {
				roleIds.add(roleId);
			}
		}

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			themeDisplay.getScopeGroupId(), ArrayUtil.toStringArray(roleIds),
			ParamUtil.getBoolean(actionRequest, "showControlMenuByRole"));
	}

	@Reference
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

	@Reference
	private RoleLocalService _roleLocalService;

}
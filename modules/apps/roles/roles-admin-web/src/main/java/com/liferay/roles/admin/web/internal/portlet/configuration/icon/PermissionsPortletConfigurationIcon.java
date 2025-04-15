/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.portlet.configuration.icon;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + RolesAdminPortletKeys.ROLES_ADMIN,
		"path=/edit_role.jsp", "path=/edit_role_assignments.jsp",
		"path=/edit_role_permissions.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class PermissionsPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "permissions");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String url = StringPool.BLANK;

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			Role role = _roleService.fetchRole(_getRoleId(portletRequest));

			int[] roleTypes = {role.getType()};

			if (role.getType() != RoleConstants.TYPE_REGULAR) {
				roleTypes = new int[] {
					RoleConstants.TYPE_REGULAR, role.getType()
				};
			}

			url = PermissionsURLTag.doTag(
				StringPool.BLANK, Role.class.getName(),
				themeDisplay.getScopeGroupName(), null,
				String.valueOf(_getRoleId(portletRequest)),
				LiferayWindowState.POP_UP.toString(), roleTypes,
				themeDisplay.getRequest());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return url;
	}

	@Override
	public double getWeight() {
		return 103;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long roleId = _getRoleId(portletRequest);

			Role role = _roleService.fetchRole(roleId);

			String roleName = role.getName();

			if (!roleName.equals(RoleConstants.OWNER) &&
				RolePermissionUtil.contains(
					themeDisplay.getPermissionChecker(), roleId,
					ActionKeys.PERMISSIONS)) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public boolean isUseDialog() {
		return true;
	}

	private long _getRoleId(PortletRequest portletRequest) {
		return ParamUtil.getLong(
			_portal.getHttpServletRequest(portletRequest), "roleId");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PermissionsPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RoleService _roleService;

}
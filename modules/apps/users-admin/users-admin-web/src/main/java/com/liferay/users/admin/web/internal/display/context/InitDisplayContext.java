/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.admin.kernel.util.PortalMyAccountApplicationType;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
public class InitDisplayContext {

	public InitDisplayContext(
		HttpServletRequest httpServletRequest, String portletName) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		String myAccountPortletId = PortletProviderUtil.getPortletId(
			PortalMyAccountApplicationType.MyAccount.CLASS_NAME,
			PortletProvider.Action.VIEW);

		if (portletName.equals(myAccountPortletId)) {
			_filterManageableGroups = false;
			_filterManageableOrganizations = false;
			_filterManageableRoles = false;
			_filterManageableUserGroupRoles = false;
			_filterManageableUserGroups = false;
		}
		else if (permissionChecker.isCompanyAdmin()) {
			_filterManageableGroups = false;
			_filterManageableOrganizations = false;
			_filterManageableRoles = true;
			_filterManageableUserGroups = false;
			_filterManageableUserGroupRoles = true;
		}
		else {
			if (permissionChecker.hasPermission(
					null, Organization.class.getName(),
					Organization.class.getName(), ActionKeys.VIEW)) {

				_filterManageableOrganizations = false;
			}
			else {
				_filterManageableOrganizations = true;
			}

			_filterManageableGroups = true;
			_filterManageableRoles = true;
			_filterManageableUserGroupRoles = true;
			_filterManageableUserGroups = true;
		}
	}

	public boolean isFilterManageableGroups() {
		return _filterManageableGroups;
	}

	public boolean isFilterManageableOrganizations() {
		return _filterManageableOrganizations;
	}

	public boolean isFilterManageableRoles() {
		return _filterManageableRoles;
	}

	public boolean isFilterManageableUserGroupRoles() {
		return _filterManageableUserGroupRoles;
	}

	public boolean isFilterManageableUserGroups() {
		return _filterManageableUserGroups;
	}

	private final boolean _filterManageableGroups;
	private final boolean _filterManageableOrganizations;
	private final boolean _filterManageableRoles;
	private final boolean _filterManageableUserGroupRoles;
	private final boolean _filterManageableUserGroups;

}
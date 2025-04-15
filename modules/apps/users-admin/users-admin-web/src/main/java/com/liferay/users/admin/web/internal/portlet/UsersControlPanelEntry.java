/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Zsolt Berentey
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN
	},
	service = ControlPanelEntry.class
)
public class UsersControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasPermissionImplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		List<UserGroupRole> userGroupRoles =
			_userGroupRoleLocalService.getUserGroupRoles(
				permissionChecker.getUserId());

		for (UserGroupRole userGroupRole : userGroupRoles) {
			Role role = userGroupRole.getRole();

			String roleName = role.getName();

			if (roleName.equals(RoleConstants.ORGANIZATION_ADMINISTRATOR) ||
				roleName.equals(RoleConstants.ORGANIZATION_OWNER)) {

				return true;
			}
		}

		List<Organization> organizations =
			_organizationLocalService.getUserOrganizations(
				permissionChecker.getUserId());

		for (Organization organization : organizations) {
			if (OrganizationPermissionUtil.contains(
					permissionChecker, organization,
					ActionKeys.MANAGE_SUBORGANIZATIONS) ||
				OrganizationPermissionUtil.contains(
					permissionChecker, organization, ActionKeys.MANAGE_USERS) ||
				OrganizationPermissionUtil.contains(
					permissionChecker, organization,
					ActionKeys.UPDATE_SUBORGANIZATIONS)) {

				return true;
			}

			/*if (OrganizationPermissionUtil.contains(
					permissionChecker, organization.getOrganizationId(),
					ActionKeys.VIEW)) {

				return true;
			}*/
		}

		return super.hasPermissionImplicitlyGranted(
			permissionChecker, group, portlet);
	}

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}
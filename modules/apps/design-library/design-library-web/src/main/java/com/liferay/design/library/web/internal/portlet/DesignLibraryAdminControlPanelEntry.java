/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Prates
 */
@Component(
	property = "jakarta.portlet.name=" + DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN,
	service = ControlPanelEntry.class
)
public class DesignLibraryAdminControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasPermissionImplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		Set<Long> designLibraryRoleIds = _getDesignLibraryRoleIds(
			group.getCompanyId());

		List<UserGroupRole> userGroupRoles =
			_userGroupRoleLocalService.getUserGroupRoles(
				permissionChecker.getUserId());

		for (UserGroupRole userGroupRole : userGroupRoles) {
			if (!designLibraryRoleIds.contains(userGroupRole.getRoleId())) {
				continue;
			}

			DepotEntry depotEntry =
				_depotEntryLocalService.fetchGroupDepotEntry(
					userGroupRole.getGroupId());

			if ((depotEntry != null) &&
				(depotEntry.getType() == DepotConstants.TYPE_DESIGN_LIBRARY)) {

				return true;
			}
		}

		return super.hasPermissionImplicitlyGranted(
			permissionChecker, group, portlet);
	}

	private Set<Long> _getDesignLibraryRoleIds(long companyId) {
		Set<Long> designLibraryRoleIds = new HashSet<>();

		for (String roleName : DepotRolesConstants.DESIGN_LIBRARY_ROLE_NAMES) {
			Role role = _roleLocalService.fetchRole(companyId, roleName);

			if (role != null) {
				designLibraryRoleIds.add(role.getRoleId());
			}
		}

		return designLibraryRoleIds;
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}
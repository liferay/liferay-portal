/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.security.permission.contributor;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.contributor.RoleCollection;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.site.cms.site.initializer.internal.constants.CMSRolesConstants;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Veronica Gonzalez
 */
@Component(service = RoleContributor.class)
public class CMSSpaceAdministratorRoleContributor implements RoleContributor {

	@Override
	public void contribute(RoleCollection roleCollection) {
		if (!roleCollection.isSignedIn() ||
			(roleCollection.getGroupId() <= 0)) {

			return;
		}

		Group group = _groupLocalService.fetchGroup(
			roleCollection.getGroupId());

		if ((group == null) ||
			!Objects.equals(group.getGroupKey(), GroupConstants.CMS)) {

			return;
		}

		Role role = _roleLocalService.fetchRoleByExternalReferenceCode(
			CMSRolesConstants.CMS_SPACE_ADMINISTRATOR_EXTERNAL_REFERENCE_CODE,
			roleCollection.getCompanyId());

		if ((role == null) || roleCollection.hasRoleId(role.getRoleId()) ||
			!_isSpaceAdministrator(roleCollection)) {

			return;
		}

		roleCollection.addRoleId(role.getRoleId());
	}

	private boolean _hasUserGroupRole(long groupId, Role role, long userId) {
		if (role == null) {
			return false;
		}

		return _userGroupRoleLocalService.hasUserGroupRole(
			userId, groupId, role.getRoleId(), true);
	}

	private boolean _isSpaceAdministrator(RoleCollection roleCollection) {
		User user = roleCollection.getUser();

		if (roleCollection.getCompanyId() != user.getCompanyId()) {
			return false;
		}

		Role assetLibraryAdministratorRole = _roleLocalService.fetchRole(
			roleCollection.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);
		Role assetLibraryOwnerRole = _roleLocalService.fetchRole(
			roleCollection.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_OWNER);

		for (long groupId :
				_depotEntryLocalService.getDepotEntryGroupIds(
					roleCollection.getCompanyId(), user.getUserId(),
					DepotConstants.TYPE_SPACE)) {

			if (_hasUserGroupRole(
					groupId, assetLibraryAdministratorRole, user.getUserId()) ||
				_hasUserGroupRole(
					groupId, assetLibraryOwnerRole, user.getUserId())) {

				return true;
			}
		}

		return false;
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}
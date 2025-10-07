/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.security.permission.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.security.permission.contributor.RoleCollection;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 * @author Roberto Díaz
 */
@Component(service = RoleContributor.class)
public class DepotRoleContributor implements RoleContributor {

	@Override
	public void contribute(RoleCollection roleCollection) {
		try {
			if (roleCollection.getGroupId() <= 0) {
				return;
			}

			Group group = _groupLocalService.getGroup(
				roleCollection.getGroupId());

			if (group.isDepot()) {
				UserBag userBag = roleCollection.getUserBag();

				if (userBag.hasUserGroup(group) ||
					_hasInheritedMemberships(group.getGroupId(), userBag)) {

					_addRoleId(
						roleCollection,
						DepotRolesConstants.ASSET_LIBRARY_MEMBER);
				}

				List<DepotEntryGroupRel> depotEntryGroupRels =
					_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
						_depotEntryLocalService.getGroupDepotEntry(
							group.getGroupId()));

				for (DepotEntryGroupRel depotEntryGroupRel :
						depotEntryGroupRels) {

					if (userBag.hasUserGroup(
							_groupLocalService.getGroup(
								depotEntryGroupRel.getToGroupId()))) {

						_addRoleId(
							roleCollection,
							DepotRolesConstants.
								ASSET_LIBRARY_CONNECTED_SITE_MEMBER);

						break;
					}
				}
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _addRoleId(RoleCollection roleCollection, String roleName)
		throws PortalException {

		Role role = _roleLocalService.getRole(
			roleCollection.getCompanyId(), roleName);

		roleCollection.addRoleId(role.getRoleId());
	}

	private boolean _hasInheritedMemberships(long groupId, UserBag userBag) {
		List<Organization> organizations =
			_organizationLocalService.getGroupOrganizations(groupId);

		for (Organization organization : organizations) {
			if (userBag.hasUserOrg(organization)) {
				return true;
			}
		}

		List<UserGroup> groupUserGroups =
			_userGroupLocalService.getGroupUserGroups(groupId);

		List<UserGroup> userUserGroups =
			_userGroupLocalService.getUserUserGroups(userBag.getUserId());

		for (UserGroup groupUserGroup : groupUserGroups) {
			if (userUserGroups.contains(groupUserGroup)) {
				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotRoleContributor.class);

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}
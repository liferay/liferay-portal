/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.security.permission.PermissionCacheUtil;

/**
 * @author Adolfo Pérez
 */
public class PermissionUtil {

	public static boolean hasCMSAdministratorRole(long companyId, long userId)
		throws PortalException {

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			userId, companyId, RoleConstants.CMS_ADMINISTRATOR);

		if (value == null) {
			value = RoleLocalServiceUtil.hasUserRole(
				userId, companyId, RoleConstants.CMS_ADMINISTRATOR, true);

			PermissionCacheUtil.putUserPrimaryKeyRole(
				userId, companyId, RoleConstants.CMS_ADMINISTRATOR, value);
		}

		return value;
	}

	public static boolean isDepotGroupAdminOrOwner(long companyId, long userId)
		throws PortalException {

		Role assetLibraryAdministratorRole = RoleLocalServiceUtil.getRole(
			companyId, DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);
		Role assetLibraryOwnerRole = RoleLocalServiceUtil.getRole(
			companyId, DepotRolesConstants.ASSET_LIBRARY_OWNER);

		for (UserGroupRole userGroupRole :
				UserGroupRoleLocalServiceUtil.getUserGroupRoles(userId)) {

			long roleId = userGroupRole.getRoleId();

			if ((roleId == assetLibraryAdministratorRole.getRoleId()) ||
				(roleId == assetLibraryOwnerRole.getRoleId())) {

				return true;
			}
		}

		return false;
	}

}
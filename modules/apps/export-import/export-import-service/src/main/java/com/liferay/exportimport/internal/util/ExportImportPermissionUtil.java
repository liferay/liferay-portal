/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Kocsis
 */
public class ExportImportPermissionUtil {

	public static final String ROLE_TEAM_PREFIX = "ROLE_TEAM_,*";

	public static void deleteResourcePermissions(
			long companyId, String resourceName, String resourcePrimKey,
			Collection<Long> roleIds)
		throws PortalException {

		for (long roleId : roleIds) {
			ResourcePermission resourcePermission =
				ResourcePermissionLocalServiceUtil.fetchResourcePermission(
					companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
					resourcePrimKey, roleId);

			if (resourcePermission != null) {
				ResourcePermissionLocalServiceUtil.deleteResourcePermission(
					resourcePermission.getResourcePermissionId());
			}
		}
	}

	public static Map<Long, Set<String>> getRoleIdsToActionIds(
		long companyId, String resourceName, long resourcePK) {

		return getRoleIdsToActionIds(
			companyId, resourceName, String.valueOf(resourcePK));
	}

	public static Map<Long, Set<String>> getRoleIdsToActionIds(
		long companyId, String resourceName, String resourcePK) {

		List<String> existingActionIds =
			ResourceActionsUtil.getModelResourceActions(resourceName);

		Map<Long, Set<String>> existingRoleIdsToActionIds = new HashMap<>();

		try {
			existingRoleIdsToActionIds = getRoleIdsToActionIds(
				companyId, resourceName, resourcePK, existingActionIds);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return existingRoleIdsToActionIds;
	}

	public static Map<Long, Set<String>> getRoleIdsToActionIds(
			long companyId, String className, String primKey,
			List<String> actionIds)
		throws PortalException {

		return ResourcePermissionLocalServiceUtil.
			getAvailableResourcePermissionActionIds(
				companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
				primKey, actionIds);
	}

	public static String getTeamRoleName(String roleName) {
		return ROLE_TEAM_PREFIX + roleName;
	}

	public static boolean isTeamRoleName(String roleName) {
		return roleName.startsWith(ROLE_TEAM_PREFIX);
	}

	public static Map<Long, String[]>
		mergeImportedPermissionsWithExistingPermissions(
			Map<Long, Set<String>> existingRoleIdsToActionIds,
			Map<Long, String[]> importedRoleIdsToActionIds) {

		Map<Long, String[]> mergedRoleIdsToActionIds = new HashMap<>();

		for (Map.Entry<Long, Set<String>> roleIdsToActionIds :
				existingRoleIdsToActionIds.entrySet()) {

			long roleId = roleIdsToActionIds.getKey();

			String[] actionIds = importedRoleIdsToActionIds.remove(roleId);

			if (actionIds != null) {
				mergedRoleIdsToActionIds.put(roleId, actionIds);
			}
		}

		mergedRoleIdsToActionIds.putAll(importedRoleIdsToActionIds);

		return mergedRoleIdsToActionIds;
	}

	public static void updateResourcePermissions(
			long companyId, long groupId, String resourceName, long resourcePK,
			Map<Long, String[]> roleIdsToActionIds)
		throws PortalException {

		updateResourcePermissions(
			companyId, groupId, resourceName, String.valueOf(resourcePK),
			roleIdsToActionIds);
	}

	public static void updateResourcePermissions(
			long companyId, long groupId, String resourceName,
			String resourcePK, Map<Long, String[]> roleIdsToActionIds)
		throws PortalException {

		List<ResourcePermission> resourcePermissions =
			ResourcePermissionLocalServiceUtil.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
				resourcePK);

		Set<Long> roleIds = new HashSet<>();

		for (ResourcePermission resourcePermission : resourcePermissions) {
			if (!roleIdsToActionIds.containsKey(
					resourcePermission.getRoleId())) {

				roleIds.add(resourcePermission.getRoleId());
			}
		}

		if (!roleIds.isEmpty()) {
			deleteResourcePermissions(
				companyId, resourceName, resourcePK, roleIds);
		}

		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
			resourcePK, roleIdsToActionIds);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportPermissionUtil.class);

}
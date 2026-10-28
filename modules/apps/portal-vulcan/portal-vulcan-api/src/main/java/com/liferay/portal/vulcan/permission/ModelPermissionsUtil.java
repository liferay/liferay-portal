/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;

import java.util.List;

/**
 * @author Javier Gamarra
 */
public class ModelPermissionsUtil {

	public static ModelPermissions toModelPermissions(
			long companyId, Permission[] permissions, long primKey,
			String resourceName,
			ResourceActionLocalService resourceActionLocalService,
			ResourcePermissionLocalService resourcePermissionLocalService,
			RoleLocalService roleLocalService)
		throws PortalException {

		ModelPermissions modelPermissions = toModelPermissions(
			companyId, permissions, resourceName, roleLocalService);

		if (modelPermissions == null) {
			return null;
		}

		List<ResourceAction> resourceActions =
			resourceActionLocalService.getResourceActions(resourceName);

		for (Permission permission : permissions) {
			String[] actionIds = permission.getActionIds();

			if (actionIds.length > 0) {
				continue;
			}

			Role role = _getOrAddRole(companyId, permission, roleLocalService);

			for (ResourceAction resourceAction : resourceActions) {
				resourcePermissionLocalService.removeResourcePermission(
					companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(primKey), role.getRoleId(),
					resourceAction.getActionId());
			}
		}

		return modelPermissions;
	}

	public static ModelPermissions toModelPermissions(
			long companyId, Permission[] permissions, String resourceName,
			RoleLocalService roleLocalService)
		throws PortalException {

		if (permissions == null) {
			return null;
		}

		ModelPermissions modelPermissions = ModelPermissionsFactory.create(
			new String[0], new String[0], resourceName);

		for (Permission permission : permissions) {
			_getOrAddRole(companyId, permission, roleLocalService);

			String[] actionIds = permission.getActionIds();

			if (actionIds.length == 0) {
				continue;
			}

			modelPermissions.addRolePermissions(
				permission.getRoleName(), actionIds);
		}

		return modelPermissions;
	}

	private static Role _getOrAddRole(
			long companyId, Permission permission,
			RoleLocalService roleLocalService)
		throws PortalException {

		if (permission.getRoleExternalReferenceCode() != null) {
			return roleLocalService.getOrAddEmptyRole(
				permission.getRoleExternalReferenceCode(), companyId,
				PrincipalThreadLocal.getUserId(), null, 0,
				permission.getRoleName(),
				RoleConstants.getLabelType(permission.getRoleType()));
		}

		return roleLocalService.getRole(companyId, permission.getRoleName());
	}

}
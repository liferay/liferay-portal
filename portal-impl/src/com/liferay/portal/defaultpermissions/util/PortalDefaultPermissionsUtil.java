/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.util;

import com.liferay.portal.kernel.defaultpermissions.configuration.manager.PortalDefaultPermissionsConfigurationManagerUtil;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResource;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResourceRegistry;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResourceRegistryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class PortalDefaultPermissionsUtil {

	public static void setModelDefaultPermissions(
			AuditedModel auditedModel, long companyId, long groupId,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, String[]> defaultPermissions =
			PortalDefaultPermissionsConfigurationManagerUtil.
				getDefaultPermissions(
					companyId, groupId, auditedModel.getModelClassName());

		if ((defaultPermissions == null) || defaultPermissions.isEmpty()) {
			return;
		}

		ModelPermissions modelPermissions =
			serviceContext.getModelPermissions();

		if (modelPermissions == null) {
			modelPermissions = ModelPermissionsFactory.create(
				auditedModel.getModelClassName());
		}

		for (Map.Entry<String, String[]> entry :
				defaultPermissions.entrySet()) {

			modelPermissions.addRolePermissions(
				entry.getKey(), entry.getValue());
		}

		serviceContext.setModelPermissions(modelPermissions);

		ResourceLocalServiceUtil.updateModelResources(
			auditedModel, serviceContext);

		PortalDefaultPermissionsModelResourceRegistry
			portalDefaultPermissionsModelResourceRegistry =
				PortalDefaultPermissionsModelResourceRegistryUtil.
					getPortalDefaultPermissionsModelResourceRegistry();

		PortalDefaultPermissionsModelResource
			portalDefaultPermissionsModelResource =
				portalDefaultPermissionsModelResourceRegistry.
					getPortalDefaultPermissionsModelResource(
						auditedModel.getModelClassName());

		if (portalDefaultPermissionsModelResource.
				isAllowOverridePermissions()) {

			_removeResource(
				auditedModel, companyId,
				ResourceActionsUtil.getModelResourceGuestDefaultActions(
					auditedModel.getModelClassName()),
				defaultPermissions, RoleConstants.GUEST);
			_removeResource(
				auditedModel, companyId,
				ResourceActionsUtil.getModelResourceOwnerDefaultActions(
					auditedModel.getModelClassName()),
				defaultPermissions, RoleConstants.OWNER);
			_removeResource(
				auditedModel, companyId,
				ResourceActionsUtil.getModelResourceGroupDefaultActions(
					auditedModel.getModelClassName()),
				defaultPermissions, RoleConstants.SITE_MEMBER);
		}
	}

	private static void _removeResource(
			AuditedModel auditedModel, long companyId,
			List<String> defaultActions,
			Map<String, String[]> defaultPermissions, String roleName)
		throws PortalException {

		List<String> actionIds = Arrays.asList(
			defaultPermissions.getOrDefault(roleName, new String[0]));

		Role role = RoleLocalServiceUtil.getRole(companyId, roleName);

		for (String actionId : defaultActions) {
			if (ListUtil.isNotEmpty(actionIds) &&
				actionIds.contains(actionId)) {

				continue;
			}

			ResourceLocalServiceUtil.removeResource(
				auditedModel.getCompanyId(), auditedModel.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(auditedModel.getPrimaryKeyObj()),
				role.getRoleId(), actionId);
		}
	}

}
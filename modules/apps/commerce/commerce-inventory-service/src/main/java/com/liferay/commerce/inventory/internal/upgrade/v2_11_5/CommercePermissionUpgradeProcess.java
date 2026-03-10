/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.internal.upgrade.v2_11_5;

import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Crescenzo Rega
 */
public class CommercePermissionUpgradeProcess extends UpgradeProcess {

	public CommercePermissionUpgradeProcess(
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		ResourceAction resourceAction =
			_resourceActionLocalService.fetchResourceAction(
				CommerceInventoryConstants.RESOURCE_NAME, "MANAGE_INVENTORY");

		if (resourceAction == null) {
			return;
		}

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					CommerceInventoryConstants.RESOURCE_NAME)) {

			if (_resourcePermissionLocalService.hasActionId(
					resourcePermission, resourceAction) &&
				(resourcePermission.getScope() !=
					ResourceConstants.SCOPE_INDIVIDUAL)) {

				_addResourcePermission(
					ActionKeys.DELETE, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					String.valueOf(resourcePermission.getCompanyId()),
					resourcePermission.getRoleId(),
					ResourceConstants.SCOPE_COMPANY);
				_addResourcePermission(
					ActionKeys.PERMISSIONS, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					String.valueOf(resourcePermission.getCompanyId()),
					resourcePermission.getRoleId(),
					ResourceConstants.SCOPE_COMPANY);
				_addResourcePermission(
					ActionKeys.UPDATE, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					String.valueOf(resourcePermission.getCompanyId()),
					resourcePermission.getRoleId(),
					ResourceConstants.SCOPE_COMPANY);
				_addResourcePermission(
					ActionKeys.VIEW, resourcePermission.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					String.valueOf(resourcePermission.getCompanyId()),
					resourcePermission.getRoleId(),
					ResourceConstants.SCOPE_COMPANY);
				_addResourcePermission(
					CommerceInventoryActionKeys.VIEW_INVENTORIES,
					resourcePermission.getCompanyId(),
					resourcePermission.getName(),
					resourcePermission.getPrimKey(),
					resourcePermission.getRoleId(),
					resourcePermission.getScope());
			}
		}
	}

	private void _addResourcePermission(
			String actionId, long companyId, String name, String primKey,
			long roleId, int scope)
		throws Exception {

		if (!_resourcePermissionLocalService.hasResourcePermission(
				companyId, name, scope, primKey, roleId, actionId)) {

			_resourcePermissionLocalService.addResourcePermission(
				companyId, name, scope, primKey, roleId, actionId);
		}
	}

	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}
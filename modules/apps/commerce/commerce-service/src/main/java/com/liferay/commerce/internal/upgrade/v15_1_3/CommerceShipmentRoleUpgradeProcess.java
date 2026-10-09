/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_3;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceShipmentRoleUpgradeProcess extends UpgradeProcess {

	public CommerceShipmentRoleUpgradeProcess(
		CompanyLocalService companyLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_companyLocalService = companyLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				try {
					_addResourcePermission(
						company.getCompanyId(),
						CommerceShipment.class.getName(),
						GroupConstants.DEFAULT_PARENT_GROUP_ID,
						AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
						ResourceConstants.SCOPE_GROUP_TEMPLATE,
						ActionKeys.VIEW);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	private void _addResourcePermission(
			long companyId, String name, long primKey, String roleName,
			int scope, String... actionIds)
		throws Exception {

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		if (role == null) {
			return;
		}

		for (String actionId : actionIds) {
			if (_resourcePermissionLocalService.hasResourcePermission(
					companyId, name, scope, String.valueOf(primKey),
					role.getRoleId(), actionId)) {

				continue;
			}

			_resourcePermissionLocalService.addResourcePermission(
				companyId, name, scope, String.valueOf(primKey),
				role.getRoleId(), actionId);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShipmentRoleUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}
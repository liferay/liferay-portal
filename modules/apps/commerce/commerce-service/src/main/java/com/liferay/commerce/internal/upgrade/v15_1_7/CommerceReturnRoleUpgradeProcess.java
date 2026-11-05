/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_7;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.Arrays;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceReturnRoleUpgradeProcess extends UpgradeProcess {

	public CommerceReturnRoleUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				try {
					long companyId = company.getCompanyId();

					_addResourcePermission(
						companyId, "CommerceReturn", companyId,
						AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
						ResourceConstants.SCOPE_COMPANY,
						ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE_DISCUSSION,
						ActionKeys.UPDATE_DISCUSSION);
					_addResourcePermission(
						companyId, "CommerceReturnItem",
						GroupConstants.DEFAULT_PARENT_GROUP_ID,
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
						ResourceConstants.SCOPE_GROUP_TEMPLATE,
						ActionKeys.ADD_DISCUSSION);
					_addResourcePermission(
						companyId, "CommerceReturnItem",
						GroupConstants.DEFAULT_PARENT_GROUP_ID,
						AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
						ResourceConstants.SCOPE_GROUP_TEMPLATE,
						ActionKeys.ADD_DISCUSSION);
					_addResourcePermission(
						companyId, "CommerceReturnItem",
						GroupConstants.DEFAULT_PARENT_GROUP_ID,
						AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
						ResourceConstants.SCOPE_GROUP_TEMPLATE,
						ActionKeys.ADD_DISCUSSION);
					_addResourcePermission(
						companyId, "CommerceReturnItem", companyId,
						AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
						ResourceConstants.SCOPE_COMPANY,
						ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE_DISCUSSION,
						ActionKeys.UPDATE_DISCUSSION);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	private void _addResourcePermission(
			long companyId, String objectDefinitionName, long primKey,
			String roleName, int scope, String... actionIds)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, objectDefinitionName);

		if (objectDefinition == null) {
			return;
		}

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		if (role == null) {
			return;
		}

		_resourceActionLocalService.checkResourceActions(
			objectDefinition.getClassName(), Arrays.asList(actionIds));

		for (String actionId : actionIds) {
			if (_resourcePermissionLocalService.hasResourcePermission(
					companyId, objectDefinition.getClassName(), scope,
					String.valueOf(primKey), role.getRoleId(), actionId)) {

				continue;
			}

			_resourcePermissionLocalService.addResourcePermission(
				companyId, objectDefinition.getClassName(), scope,
				String.valueOf(primKey), role.getRoleId(), actionId);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceReturnRoleUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.internal.upgrade.v2_6_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Crescenzo Rega
 */
public class CommercePermissionUpgradeProcess extends UpgradeProcess {

	public CommercePermissionUpgradeProcess(
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select resourcePermissionId, companyId, roleId from ",
					"ResourcePermission where name = 'com.liferay.commerce.",
					"inventory.model.CommerceInventoryWarehouse' and scope = ",
					"4"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long roleId = resultSet.getLong("roleId");

				Role role = _roleLocalService.fetchRole(
					resultSet.getLong("companyId"), RoleConstants.GUEST);

				if ((role != null) && (roleId == role.getRoleId())) {
					_resourcePermissionLocalService.deleteResourcePermission(
						resultSet.getLong("resourcePermissionId"));
				}

				role = _roleLocalService.fetchRole(
					resultSet.getLong("companyId"), RoleConstants.SITE_MEMBER);

				if ((role != null) && (roleId == role.getRoleId())) {
					_resourcePermissionLocalService.deleteResourcePermission(
						resultSet.getLong("resourcePermissionId"));
				}

				role = _roleLocalService.fetchRole(
					resultSet.getLong("companyId"), RoleConstants.USER);

				if ((role == null) || (roleId != role.getRoleId())) {
					continue;
				}

				_resourcePermissionLocalService.deleteResourcePermission(
					resultSet.getLong("resourcePermissionId"));
			}
		}
	}

	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}
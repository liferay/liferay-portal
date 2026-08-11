/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_3_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Collections;

/**
 * @author Manuele Castro
 */
public class ObjectDefinitionClassNameResourcePermissionUpgradeProcess
	extends UpgradeProcess {

	public ObjectDefinitionClassNameResourcePermissionUpgradeProcess(
		ResourceActionLocalService resourceActionLocalService) {

		_resourceActionLocalService = resourceActionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ObjectDefinition.className, ObjectField.name from ",
					"ObjectField inner join ObjectDefinition on ",
					"ObjectDefinition.objectDefinitionId = ",
					"ObjectField.objectDefinitionId where ",
					"ObjectField.businessType = ? and ObjectDefinition.status ",
					"= ?"))) {

			preparedStatement.setString(
				1, ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);
			preparedStatement.setInt(2, WorkflowConstants.STATUS_APPROVED);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					_upgradeAttachmentObjectField(
						resultSet.getString("className"),
						resultSet.getString("name"));
				}
			}
		}
	}

	private String _getAttachmentDownloadActionKey(String name) {
		return StringBundler.concat(
			ActionKeys.DOWNLOAD, StringPool.UNDERLINE,
			TextFormatter.format(name, TextFormatter.R));
	}

	private void _updateResourcePermissions(
			long bitwiseValue, String name, long viewBitwiseValue)
		throws Exception {

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select resourcePermissionId, actionIds from ",
						"ResourcePermission where ctCollectionId = 0 and ",
						"name = ? and scope = ?"));
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update ResourcePermission set actionIds = ? where " +
						"ctCollectionId = 0 and resourcePermissionId = ?")) {

			selectPreparedStatement.setString(1, name);
			selectPreparedStatement.setInt(
				2, ResourceConstants.SCOPE_INDIVIDUAL);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long actionIds = resultSet.getLong("actionIds");

					if (((actionIds & viewBitwiseValue) == 0) ||
						((actionIds & bitwiseValue) != 0)) {

						continue;
					}

					updatePreparedStatement.setLong(
						1, actionIds | bitwiseValue);
					updatePreparedStatement.setLong(
						2, resultSet.getLong("resourcePermissionId"));

					updatePreparedStatement.addBatch();
				}
			}

			updatePreparedStatement.executeBatch();
		}
	}

	private void _upgradeAttachmentObjectField(String className, String name)
		throws Exception {

		String actionId = _getAttachmentDownloadActionKey(name);

		_resourceActionLocalService.checkResourceActions(
			className, Collections.singletonList(actionId));

		ResourceAction resourceAction =
			_resourceActionLocalService.fetchResourceAction(
				className, actionId);

		ResourceAction viewResourceAction =
			_resourceActionLocalService.fetchResourceAction(
				className, ActionKeys.VIEW);

		if ((resourceAction == null) || (viewResourceAction == null)) {
			return;
		}

		_updateResourcePermissions(
			resourceAction.getBitwiseValue(), className,
			viewResourceAction.getBitwiseValue());
	}

	private final ResourceActionLocalService _resourceActionLocalService;

}

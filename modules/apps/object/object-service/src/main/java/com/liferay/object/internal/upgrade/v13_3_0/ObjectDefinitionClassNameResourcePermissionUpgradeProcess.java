/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_3_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.TextFormatter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Manuele Castro
 */
public class ObjectDefinitionClassNameResourcePermissionUpgradeProcess
	extends UpgradeProcess {

	public ObjectDefinitionClassNameResourcePermissionUpgradeProcess(
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select objectDefinitionId, name from ObjectField where " +
					"businessType = 'Attachment'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long objectDefinitionId = resultSet.getLong(1);

				String objectDefinitionClassName =
					(String)_getObjectDefinitionTableColumnValue(
						objectDefinitionId, "className");

				Integer objectDefinitionStatus =
					(Integer)_getObjectDefinitionTableColumnValue(
						objectDefinitionId, "status");

				if ((objectDefinitionClassName != null) &&
					(objectDefinitionStatus != null) &&
					(objectDefinitionStatus == 0)) {

					String objectFieldActionId = StringBundler.concat(
						ActionKeys.DOWNLOAD, StringPool.UNDERLINE,
						TextFormatter.format(
							resultSet.getString(2), TextFormatter.R));

					ResourceAction resourceAction =
						_resourceActionLocalService.fetchResourceAction(
							objectDefinitionClassName, objectFieldActionId);

					if (resourceAction == null) {
						_addResourceAction(
							objectFieldActionId,
							_getNextBitwiseValue(objectDefinitionClassName),
							objectDefinitionClassName);

						resourceAction =
							_resourceActionLocalService.fetchResourceAction(
								objectDefinitionClassName, objectFieldActionId);
					}

					_updateObjectDefinitionClassNameResourcePermissions(
						resourceAction.getBitwiseValue(),
						objectDefinitionClassName, objectFieldActionId);
				}
			}
		}
	}

	private void _addResourceAction(
		String actionId, long bitwiseValue, String name) {

		if (bitwiseValue <= 1) {
			return;
		}

		long resourceActionId = increment(ResourceAction.class.getName());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into ResourceAction (mvccVersion, ",
					"resourceActionId, name, actionId, bitwiseValue) values ",
					"(?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, resourceActionId);
			preparedStatement.setString(3, name);
			preparedStatement.setString(4, actionId);
			preparedStatement.setLong(5, bitwiseValue);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to add ResourceAction for " + actionId, exception);
			}
		}
	}

	private long _getNextBitwiseValue(String resourceActionName)
		throws Exception {

		long nextBitwiseValue = 1;

		long combinedBitwiseValues = 0;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select bitwiseValue from ResourceAction where name = '" +
					resourceActionName + "'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				combinedBitwiseValues |= resultSet.getLong(1);
			}
		}

		while ((combinedBitwiseValues & nextBitwiseValue) != 0) {
			nextBitwiseValue <<= 1;
		}

		return nextBitwiseValue;
	}

	private Object _getObjectDefinitionTableColumnValue(
		long objectDefinitionId, String columnName) {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", columnName,
					" from ObjectDefinition where objectDefinitionId = ",
					objectDefinitionId));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getObject(1);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to select ObjectDefinition table value for ",
						"the following column ", columnName),
					exception);
			}
		}

		return null;
	}

	private void _updateObjectDefinitionClassNameResourcePermissions(
		long bitwiseValue, String name, String resourceActionId) {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select resourcePermissionId, actionIds from ",
					"ResourcePermission where name = '", name, "' and scope = ",
					ResourceConstants.SCOPE_INDIVIDUAL));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				ResourcePermission resourcePermission =
					_resourcePermissionLocalService.fetchResourcePermission(
						resultSet.getLong(1));

				if ((resourcePermission != null) &&
					resourcePermission.isViewActionId() &&
					!resourcePermission.hasActionId(resourceActionId)) {

					_updateResourcePermission(
						resultSet.getLong(1),
						resultSet.getLong(2) + bitwiseValue);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to update ResourcePermission for " + name,
					exception);
			}
		}
	}

	private void _updateResourcePermission(
			long resourcePermissionId, long bitwiseValue)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update ResourcePermission set actionIds = ? where " +
					"resourcePermissionId = ?")) {

			preparedStatement.setLong(1, bitwiseValue);
			preparedStatement.setLong(2, resourcePermissionId);

			preparedStatement.executeUpdate();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectDefinitionClassNameResourcePermissionUpgradeProcess.class);

	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}

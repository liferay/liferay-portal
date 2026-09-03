/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.data.cleanup.internal.verify.util.PostUpgradeDataCleanupProcessUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.upgrade.data.cleanup.ResourcePermissionDataCleanupPreupgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Luis Ortiz
 */
public class ResourcePermissionPostupgradeDataCleanupProcess
	implements PostUpgradeDataCleanupProcess {

	public ResourcePermissionPostupgradeDataCleanupProcess(
		Connection connection,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_connection = connection;
		_resourcePermissionLocalService = resourcePermissionLocalService;

		_dbInspector = new DBInspector(connection);
	}

	@Override
	public void cleanUp() throws Exception {
		if (!PostUpgradeDataCleanupProcessUtil.isEveryLiferayBundleActive()) {
			if (_log.isWarnEnabled() && CompanyThreadLocal.isDefaultCompany()) {
				_log.warn(
					StringBundler.concat(
						ResourcePermissionPostupgradeDataCleanupProcess.class.
							getSimpleName(),
						" cannot be executed because there are modules that ",
						"are inactive"));
			}

			return;
		}

		String escapeClause = "";

		DB db = DBManagerUtil.getDB();

		if ((db.getDBType() == DBType.DB2) ||
			(db.getDBType() == DBType.ORACLE) ||
			(db.getDBType() == DBType.SQLSERVER)) {

			escapeClause = "escape '\\' ";
		}

		Set<String> portletIds = new HashSet<>();

		for (Portlet portlet : PortletLocalServiceUtil.getPortlets()) {
			portletIds.add(portlet.getPortletId());
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				StringBundler.concat(
					"select count(1) as count, name from ResourcePermission ",
					"where name like 'com\\_liferay\\_%' ", escapeClause,
					"and scope = ? group by name"))) {

			preparedStatement.setInt(1, ResourceConstants.SCOPE_INDIVIDUAL);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String name = resultSet.getString("name");

					if (portletIds.contains(name)) {
						continue;
					}

					_resourcePermissionLocalService.deleteResourcePermissions(
						name);

					DataCleanupLoggingUtil.logDelete(
						_log, resultSet.getLong("count"),
						_dbInspector.normalizeName("ResourcePermission"),
						StringBundler.concat("\"", name, "\" was not found"));
				}
			}
		}

		UpgradeProcess upgradeProcess =
			new ResourcePermissionDataCleanupPreupgradeProcess();

		upgradeProcess.upgrade();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourcePermissionPostupgradeDataCleanupProcess.class);

	private final Connection _connection;
	private final DBInspector _dbInspector;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}
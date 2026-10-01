/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.permission.ResourceActionsImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Luis Ortiz
 */
public class ResourcePermissionDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		if (!dbInspector.hasTable("ResourcePermission") ||
			!dbInspector.hasColumn("ResourcePermission", "primKeyId")) {

			return;
		}

		Map<String, List<String>> namesMap = new TreeMap<>();

		Set<String> liferayTableNames = DBResourceUtil.getLiferayTableNames(
			connection);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select distinct name from ResourcePermission where name " +
					"like 'com.liferay.%' and primKeyId != 0 and primKeyId " +
						"is not null and scope = ?")) {

			preparedStatement.setLong(1, ResourceConstants.SCOPE_INDIVIDUAL);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				ResourceActionsImpl resourceActionsImpl =
					new ResourceActionsImpl();

				while (resultSet.next()) {
					String name = resultSet.getString("name");

					String[] classNames = StringUtil.split(
						name,
						resourceActionsImpl.getCompositeModelNameSeparator());

					String tableName = null;

					if (classNames.length == 1) {
						tableName =
							DataCleanupPreupgradeProcessUtil.getTableName(
								connection, dbInspector, classNames[0]);
					}
					else {
						for (String className : classNames) {
							tableName =
								DataCleanupPreupgradeProcessUtil.getTableName(
									connection, dbInspector, className);

							if (StringUtil.startsWith(tableName, "DDM")) {
								break;
							}

							tableName = null;
						}
					}

					if ((tableName == null) ||
						(!dbInspector.isObjectTable(tableName) &&
						 !liferayTableNames.contains(tableName))) {

						if (_log.isDebugEnabled()) {
							_log.debug(
								StringBundler.concat(
									"Skipping class name ", name,
									" because its associated table was not ",
									"found or it does not belong to Liferay"));
						}

						continue;
					}

					if (!dbInspector.hasTable(tableName)) {
						if (PropsValues.DATABASE_PARTITION_ENABLED &&
							!CompanyThreadLocal.isDefaultCompany() &&
							dbInspector.hasView(tableName)) {

							if (_log.isInfoEnabled()) {
								_log.info(
									StringBundler.concat(
										"Skipping class name ", name,
										" because ", tableName,
										" is a view in a secondary partition"));
							}
						}
						else if (_log.isWarnEnabled()) {
							_log.warn("Table " + tableName + " does not exist");
						}

						continue;
					}

					List<String> names = namesMap.computeIfAbsent(
						tableName, key -> new ArrayList<>());

					names.add(name);
				}
			}
		}

		for (Map.Entry<String, List<String>> entry : namesMap.entrySet()) {
			String tableName = entry.getKey();

			String primaryKeyColumnName = "resourcePrimKey";

			if (!dbInspector.hasColumn(tableName, primaryKeyColumnName)) {
				primaryKeyColumnName =
					DataCleanupPreupgradeProcessUtil.getPrimaryKeyColumnName(
						connection, dbInspector, tableName);
			}

			if (primaryKeyColumnName == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Skipping table " + tableName +
							" because it does not have a primary key");
				}

				continue;
			}

			upgrade(
				new TableOrphanReferencesDataCleanupPreupgradeProcess(
					null,
					StringBundler.concat(
						"[$SOURCE_TABLE_ALIAS$].scope = ",
						ResourceConstants.SCOPE_INDIVIDUAL,
						" and [$SOURCE_TABLE_ALIAS$].name in ('",
						StringUtil.merge(entry.getValue(), "', '"), "')"),
					"primKeyId", "ResourcePermission", primaryKeyColumnName,
					tableName));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourcePermissionDataCleanupPreupgradeProcess.class);

}
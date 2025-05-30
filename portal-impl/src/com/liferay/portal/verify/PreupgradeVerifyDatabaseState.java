/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.io.InputStream;

import java.net.URL;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Jorge Avalos
 */
public class PreupgradeVerifyDatabaseState extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		_checkDatabaseState();
	}

	private void _checkDatabaseState() throws Exception {
		if (StartupHelperUtil.isDBNew() ||
			(PortalUpgradeProcess.getCurrentSchemaVersion(connection) !=
				PortalUpgradeProcess.getLatestSchemaVersion())) {

			return;
		}

		Set<String> preupgradedServiceTables =
			UpgradeProcessUtil.getPreupgradedServiceTables(connection);

		if (preupgradedServiceTables.isEmpty()) {
			return;
		}

		Set<String> databaseTables = _getDatabaseTables();

		Set<String> serviceTables = _fetchModuleTables();

		serviceTables.removeAll(preupgradedServiceTables);

		if (!databaseTables.containsAll(serviceTables)) {
			Set<String> missingTables = new HashSet<>(serviceTables);

			missingTables.removeAll(databaseTables);

			throw new Exception(
				"Missing tables detected:\n" + missingTables +
					"\nPlease resolve these tables to continue the upgrade");
		}

		Set<String> incompleteTables = new HashSet<>(databaseTables);

		incompleteTables.retainAll(serviceTables);

		if (!incompleteTables.isEmpty()) {
			throw new Exception(
				"Incomplete tables detected:\n" + incompleteTables +
					"\nPlease remove these tables to continue the upgrade");
		}
	}

	private Set<String> _fetchModuleTables() throws Exception {
		Set<String> tableNames = new HashSet<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (!symbolicName.startsWith("com.liferay") ||
				!symbolicName.contains("service")) {

				continue;
			}

			URL url = bundle.getResource("/META-INF/sql/tables.sql");

			if (url == null) {
				continue;
			}

			try (InputStream inputStream = url.openStream()) {
				Matcher matcher = _createTablePattern.matcher(
					StringUtil.read(inputStream));

				String tableName = _dbInspector.normalizeName(matcher.group(1));

				while (matcher.find()) {
					tableNames.add(tableName);
				}
			}
		}

		return tableNames;
	}

	private Set<String> _getDatabaseTables() throws Exception {
		Set<String> tableNames = new HashSet<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		_dbInspector = new DBInspector(connection);

		try (ResultSet resultSet = databaseMetaData.getTables(
				_dbInspector.getCatalog(), _dbInspector.getSchema(), null,
				new String[] {"TABLE"})) {

			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");

				tableNames.add(_dbInspector.normalizeName(tableName));
			}
		}

		return tableNames;
	}

	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\S*) \\(");
	private static DBInspector _dbInspector;

}
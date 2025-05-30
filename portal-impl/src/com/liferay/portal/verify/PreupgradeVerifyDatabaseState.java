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
			PortalUpgradeProcess.isInLatestSchemaVersion(connection)) {

			return;
		}

		Set<String> preupgradedServiceTables =
			UpgradeProcessUtil.getPreupgradedServiceTables(connection);

		if (preupgradedServiceTables.isEmpty()) {
			return;
		}

		DBInspector dbInspector = new DBInspector(connection);

		Set<String> databaseTables = new HashSet<>(dbInspector.getTableNames(null));

		if (!databaseTables.containsAll(preupgradedServiceTables)) {
			Set<String> missingTables = new HashSet<>(preupgradedServiceTables);

			missingTables.removeAll(databaseTables);

			throw new Exception(
				"Missing tables detected:\n" + missingTables +
					"\nPlease fix these tables to continue the upgrade");
		}

		Set<String> targetVersionTables = _fetchTargetVersionTables();

		targetVersionTables.removeAll(preupgradedServiceTables);

		Set<String> previousUpgradeStaleTables = new HashSet<>(databaseTables);

		previousUpgradeStaleTables.retainAll(targetVersionTables);

		if (!previousUpgradeStaleTables.isEmpty()) {
			throw new Exception(
				"Stale tables from a previous upgrade detected:\n" + previousUpgradeStaleTables +
					"\nPlease remove these tables to continue the upgrade");
		}
	}

	private Set<String> _fetchTargetVersionTables() throws Exception {
		Set<String> tableNames = new HashSet<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		DBInspector dbInspector = new DBInspector(connection);

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
				while (matcher.find()) {
					tableNames.add(dbInspector.normalizeName(matcher.group(1)));
				}
			}
		}
		return tableNames;
	}



	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\S*) \\(");

}
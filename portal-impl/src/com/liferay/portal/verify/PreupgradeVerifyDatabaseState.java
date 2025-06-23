/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jorge Avalos
 */
public class PreupgradeVerifyDatabaseState extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		if (StartupHelperUtil.isDBNew() ||
			PortalUpgradeProcess.isInLatestSchemaVersion(connection) ||
			(PortalUpgradeProcess.getCurrentState(connection) !=
				ReleaseConstants.STATE_GOOD)) {

			return;
		}

		Set<String> serviceComponentPortalTableNames =
			DBResourceUtil.getServiceComponentPortalTableNames(connection);

		Set<String> serviceComponentTableNames =
			DBResourceUtil.getServiceComponentModuleTableNames(connection);

		serviceComponentTableNames.addAll(serviceComponentPortalTableNames);

		if (serviceComponentTableNames.isEmpty()) {
			return;
		}

		DBInspector dbInspector = new DBInspector(connection);

		Set<String> databaseTables = new HashSet<>(
			dbInspector.getTableNames(null));

		if (!databaseTables.containsAll(serviceComponentTableNames)) {
			Set<String> missingTables = new HashSet<>(
				serviceComponentTableNames);

			missingTables.removeAll(databaseTables);

			throw new VerifyException(
				"Missing tables detected: " + new TreeSet<>(missingTables));
		}

		if (serviceComponentPortalTableNames.isEmpty()) {
			return;
		}

		Set<String> targetVersionNewTables = DBResourceUtil.getModuleTableNames(
			connection);

		targetVersionNewTables.addAll(
			DBResourceUtil.getPortalTableNames(connection));

		targetVersionNewTables.removeAll(serviceComponentTableNames);

		Set<String> previousUpgradeStaleTables = new HashSet<>(databaseTables);

		previousUpgradeStaleTables.retainAll(targetVersionNewTables);

		if (!previousUpgradeStaleTables.isEmpty()) {
			throw new VerifyException(
				"Stale tables from a previous upgrade detected: " +
					new TreeSet<>(previousUpgradeStaleTables));
		}
	}

}
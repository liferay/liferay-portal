/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.sql.Connection;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jorge Avalos
 */
public class PreupgradeVerifyDatabaseState extends PreupgradeVerifyProcess {

	public void verify() throws VerifyException {
		try {
			try (Connection connection = getConnection()) {
				if (StartupHelperUtil.isDBNew() ||
					PortalUpgradeProcess.isInLatestSchemaVersion(connection) ||
					(PortalUpgradeProcess.getCurrentState(connection) !=
						ReleaseConstants.STATE_GOOD)) {

					return;
				}
			}
		}
		catch (Exception exception) {
			throw new VerifyException(exception);
		}

		super.verify();
	}

	@Override
	protected void doVerify() throws Exception {
		Set<String> serviceComponentPortalTableNames =
			DBResourceUtil.getServiceComponentPortalTableNames(connection);

		Set<String> serviceComponentTableNames =
			DBResourceUtil.getServiceComponentModuleTableNames(connection);

		serviceComponentTableNames.addAll(serviceComponentPortalTableNames);

		if (serviceComponentTableNames.isEmpty()) {
			return;
		}

		DBInspector dbInspector = new DBInspector(connection);

		Set<String> databaseTableNames = new HashSet<>(
			dbInspector.getTableNames(null));

		if (!databaseTableNames.containsAll(serviceComponentTableNames)) {
			Set<String> missingTableNames = ConcurrentHashMap.newKeySet();

			missingTableNames.addAll(serviceComponentTableNames);

			missingTableNames.removeAll(databaseTableNames);

			Set<String> viewNames = _removeViewNames(
				dbInspector, missingTableNames);

			if (!missingTableNames.isEmpty()) {
				throw new VerifyException(
					"Missing tables detected: " +
						new TreeSet<>(missingTableNames));
			}

			viewNames.removeIf(
				viewName -> {
					try {
						return dbInspector.hasView(viewName);
					}
					catch (Exception exception) {
						throw new SystemException(exception);
					}
				});

			if (!viewNames.isEmpty()) {
				throw new VerifyException(
					StringBundler.concat(
						"Missing views detected: ",
						new TreeSet<>(
							viewNames
						).toString(),
						" in company ",
						String.valueOf(
							CompanyThreadLocal.getNonsystemCompanyId())));
			}

			if (!missingTableNames.isEmpty()) {
				throw new VerifyException(
					"Missing tables detected: " +
						new TreeSet<>(missingTableNames));
			}
		}

		if (serviceComponentPortalTableNames.isEmpty()) {
			return;
		}

		Set<String> targetVersionNewTableNames =
			DBResourceUtil.getModuleTableNames(connection);

		targetVersionNewTableNames.addAll(
			DBResourceUtil.getPortalTableNames(connection));

		targetVersionNewTableNames.removeAll(serviceComponentTableNames);

		Set<String> previousUpgradeStaleTableNames = new HashSet<>(
			databaseTableNames);

		previousUpgradeStaleTableNames.retainAll(targetVersionNewTableNames);

		if (!previousUpgradeStaleTableNames.isEmpty()) {
			throw new VerifyException(
				"Stale tables from a previous upgrade detected: " +
					new TreeSet<>(previousUpgradeStaleTableNames));
		}
	}

	private Set<String> _removeViewNames(
			DBInspector dbInspector, Set<String> missingTableNames)
		throws Exception {

		Set<String> viewNames = new HashSet<>();

		if (CompanyThreadLocal.getNonsystemCompanyId() ==
				PortalInstancePool.getDefaultCompanyId()) {

			return viewNames;
		}

		for (String missingTableName : missingTableNames) {
			if (dbInspector.isControlTable(missingTableName)) {
				missingTableNames.remove(missingTableName);
				viewNames.add(missingTableName);
			}
		}

		return viewNames;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jorge Avalos
 */
public class PreupgradeVerifyDatabaseState extends PreupgradeVerifyProcess {

	public PreupgradeVerifyDatabaseState() {
		_falsePositive74UpgradeDroppedTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		_falsePositive74UpgradeDroppedTableNames.addAll(
			Set.of(
				"Account_", "AccountGroupAccountEntryRel",
				"AssetEntries_AssetCategories", "BlogsStatsUser",
				"CAccountGroupCAccountRel", "CommerceAccount",
				"CommerceAccountGroup", "CommerceAccountGroupRel",
				"CommerceAccountOrganizationRel", "CommerceAccountUserRel",
				"CommerceAddress", "CommerceCountry", "CommerceRegion",
				"MBStatsUser", "OrgGroupRole", "RemoteAppEntry"));
	}

	public void verify() throws VerifyException {
		try {
			try (Connection connection = getConnection()) {
				if (StartupHelperUtil.isDBNew() ||
					PortalUpgradeProcess.isInCompatibleSchemaVersion(
						connection) ||
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

		if (ListUtil.isNotEmpty(_verifyMessages)) {
			for (String verifyMessage : _verifyMessages) {
				_log.error(verifyMessage);
			}

			throw new VerifyException(
				StringUtil.merge(_verifyMessages, StringPool.COMMA_AND_SPACE));
		}
	}

	@Override
	protected void doVerify() throws Exception {
		Set<String> serviceComponentPortalTableNames =
			DBResourceUtil.getServiceComponentPortalTableNames(connection);

		Set<String> tableNames =
			DBResourceUtil.getServiceComponentModuleTableNames(connection);

		tableNames.addAll(serviceComponentPortalTableNames);

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				try {
					tableNames.addAll(
						DBResourceUtil.getNonserviceBuilderTableNames(
							companyId));
				}
				catch (PortalException portalException) {
					_log.error(
						"Unable to get table names for company " + companyId,
						portalException);
				}
			});

		if (tableNames.isEmpty()) {
			return;
		}

		DBInspector dbInspector = new DBInspector(connection);

		Set<String> databaseTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		databaseTableNames.addAll(dbInspector.getTableNames(null));

		if (!databaseTableNames.containsAll(tableNames)) {
			Set<String> missingTableNames = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);

			missingTableNames.addAll(tableNames);

			missingTableNames.removeAll(databaseTableNames);
			missingTableNames.removeAll(
				_falsePositive74UpgradeDroppedTableNames);

			Set<String> viewNames = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);

			if (!CompanyThreadLocal.isDefaultCompany()) {
				viewNames.addAll(
					dbInspector.getControlTableNames(missingTableNames));

				missingTableNames.removeAll(viewNames);
			}

			if (!missingTableNames.isEmpty()) {
				String prefix = (missingTableNames.size() == 1) ?
					"A missing table was detected" :
						"Missing tables were detected";

				if (PropsValues.DATABASE_PARTITION_ENABLED) {
					prefix = StringBundler.concat(
						prefix, " for company ",
						CompanyThreadLocal.getNonsystemCompanyId());
				}

				_verifyMessages.add(
					StringBundler.concat(
						prefix, StringPool.COLON, StringPool.SPACE,
						new TreeSet<>(
							TransformUtil.transform(
								missingTableNames,
								dbInspector::normalizeName))));
			}

			Set<String> databaseViewNames = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);

			databaseViewNames.addAll(dbInspector.getViewNames(null));

			viewNames.removeAll(databaseViewNames);

			if (!viewNames.isEmpty()) {
				String prefix = (viewNames.size() == 1) ?
					"A missing view was detected for company " :
						"Missing views were detected for company ";

				_verifyMessages.add(
					StringBundler.concat(
						prefix, CompanyThreadLocal.getNonsystemCompanyId(),
						StringPool.COLON, StringPool.SPACE,
						new TreeSet<>(viewNames)));
			}
		}

		if (serviceComponentPortalTableNames.isEmpty()) {
			return;
		}

		Set<String> targetVersionNewTableNames =
			DBResourceUtil.getModuleTableNames();

		targetVersionNewTableNames.addAll(DBResourceUtil.getPortalTableNames());

		targetVersionNewTableNames.removeAll(tableNames);

		Set<String> previousUpgradeStaleTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		previousUpgradeStaleTableNames.addAll(databaseTableNames);

		previousUpgradeStaleTableNames.retainAll(targetVersionNewTableNames);

		if (!previousUpgradeStaleTableNames.isEmpty()) {
			String prefix = (previousUpgradeStaleTableNames.size() == 1) ?
				"A stale table was detected" : "Stale tables were detected";

			if (PropsValues.DATABASE_PARTITION_ENABLED) {
				prefix = StringBundler.concat(
					prefix, " for company ",
					CompanyThreadLocal.getNonsystemCompanyId());
			}

			_verifyMessages.add(
				StringBundler.concat(
					prefix, StringPool.COLON, StringPool.SPACE,
					new TreeSet<>(previousUpgradeStaleTableNames)));
		}

		_verifyColumns(dbInspector);
	}

	private void _verifyColumns(DBInspector dbInspector) throws Exception {
		Map<String, List<String>> columnDefinitionsMap =
			DBResourceUtil.getServiceComponentPortalColumnDefinitionsMap(
				connection);

		if (columnDefinitionsMap.isEmpty()) {
			return;
		}

		columnDefinitionsMap.putAll(
			DBResourceUtil.getServiceComponentModuleColumnDefinitionsMap(
				connection));

		Map<String, List<String>> mismatchedColumnDefinitionsMap =
			new ConcurrentSkipListMap<>();
		Map<String, List<String>> missingColumnNames =
			new ConcurrentSkipListMap<>();

		processConcurrently(
			columnDefinitionsMap,
			entry -> {
				if (!dbInspector.hasTable(entry.getKey())) {
					return;
				}

				for (String columnDefinition : entry.getValue()) {
					int index = columnDefinition.indexOf(StringPool.SPACE);

					String columnName = columnDefinition.substring(0, index);
					String columnType = columnDefinition.substring(index + 1);

					if (!dbInspector.hasColumn(entry.getKey(), columnName)) {
						missingColumnNames.computeIfAbsent(
							entry.getKey(), tableName -> new ArrayList<>()
						).add(
							columnName
						);
					}
					else if (!dbInspector.hasColumnType(
								entry.getKey(), columnName, columnType)) {

						mismatchedColumnDefinitionsMap.computeIfAbsent(
							entry.getKey(), tableName -> new ArrayList<>()
						).add(
							columnDefinition
						);
					}
				}
			},
			null);

		String messageSuffix = StringPool.BLANK;

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			String partitionName = DBPartitionUtil.getPartitionName(
				CompanyThreadLocal.getNonsystemCompanyId());

			messageSuffix = " in " + partitionName;
		}

		if (_log.isWarnEnabled()) {
			for (Map.Entry<String, List<String>> entry :
					mismatchedColumnDefinitionsMap.entrySet()) {

				if (dbInspector.hasView(entry.getKey())) {
					continue;
				}

				for (String columnDefinition : entry.getValue()) {
					int index = columnDefinition.indexOf(StringPool.SPACE);

					String columnName = columnDefinition.substring(0, index);
					String columnType = columnDefinition.substring(index + 1);

					_log.warn(
						StringBundler.concat(
							"Column ", dbInspector.normalizeName(columnName),
							" is not defined as ", columnType, " for ",
							dbInspector.normalizeName(entry.getKey()),
							messageSuffix));
				}
			}
		}

		StringBundler sb = new StringBundler();

		for (Map.Entry<String, List<String>> entry :
				missingColumnNames.entrySet()) {

			for (String columnName : entry.getValue()) {
				sb.append(
					StringBundler.concat(
						"Column ", dbInspector.normalizeName(columnName),
						" is missing for ",
						dbInspector.normalizeName(entry.getKey()),
						messageSuffix));
				sb.append(StringPool.NEW_LINE);
			}
		}

		if (sb.length() != 0) {
			_verifyMessages.add(sb.toString());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PreupgradeVerifyDatabaseState.class);

	private final Set<String> _falsePositive74UpgradeDroppedTableNames;
	private final List<String> _verifyMessages = new CopyOnWriteArrayList<>();

}
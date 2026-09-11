/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ReleaseLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Mariano Álvaro Sáiz
 */
public class PostupgradeVerifyDatabaseState extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		Map<String, String> tablesServletContextNames =
			_tablesServletContextNamesDCLSingleton.getSingleton(
				DBResourceUtil::getTablesServletContextNames);

		Set<String> expectedTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		expectedTableNames.addAll(tablesServletContextNames.keySet());

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				try {
					expectedTableNames.addAll(
						DBResourceUtil.getNonserviceBuilderTableNames(
							companyId));
				}
				catch (PortalException portalException) {
					_log.error(
						"Unable to get table names for company " + companyId,
						portalException);
				}
			});

		DBInspector dbInspector = new DBInspector(connection);

		Set<String> expectedViewNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		if (!CompanyThreadLocal.isDefaultCompany()) {
			expectedViewNames.addAll(
				dbInspector.getControlTableNames(expectedTableNames));

			expectedTableNames.removeAll(expectedViewNames);
		}

		Set<String> databaseTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		databaseTableNames.addAll(dbInspector.getTableNames(null));

		Set<String> missingTableNames = _asymmetricDifference(
			expectedTableNames, databaseTableNames);

		Set<String> databaseViewNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		databaseViewNames.addAll(dbInspector.getViewNames(null));

		Set<String> missingViewNames = _asymmetricDifference(
			expectedViewNames, databaseViewNames);

		Map<String, List<String>> errorMessagesMap = new TreeMap<>();

		_addMessages(
			errorMessagesMap,
			TransformUtil.transform(
				missingTableNames, dbInspector::normalizeName),
			"Missing tables were detected", tablesServletContextNames);
		_addMessages(
			errorMessagesMap, missingViewNames, "Missing views were detected",
			tablesServletContextNames);

		Map<String, String>
			historicalServiceComponentTablesServletContextNames =
				_historicalServiceComponentTablesServletContextNamesDCLSingleton.
					getSingleton(
						() -> {
							try {
								return DBResourceUtil.
									getHistoricalServiceComponentTablesServletContextNames(
										connection);
							}
							catch (Exception exception) {
								return ReflectionUtil.throwException(exception);
							}
						});

		Set<String> customTableNames = _asymmetricDifference(
			databaseTableNames, expectedTableNames);

		Set<String> staleTableNames = _intersect(
			customTableNames,
			historicalServiceComponentTablesServletContextNames.keySet());

		customTableNames.removeAll(staleTableNames);

		Set<String> customViewNames = _asymmetricDifference(
			databaseViewNames, expectedViewNames);

		Set<String> staleViewNames = _intersect(
			customViewNames,
			historicalServiceComponentTablesServletContextNames.keySet());

		customViewNames.removeAll(staleViewNames);

		Map<String, List<String>> warnMessagesMap = new TreeMap<>();

		_addMessages(
			warnMessagesMap, staleTableNames, "Stale tables were detected",
			historicalServiceComponentTablesServletContextNames);
		_addMessages(
			warnMessagesMap, staleViewNames, "Stale views were detected",
			historicalServiceComponentTablesServletContextNames);

		_verifyColumns(
			databaseTableNames, dbInspector, errorMessagesMap, warnMessagesMap);

		Set<String> servletContextNames = new TreeSet<>(
			errorMessagesMap.keySet());

		servletContextNames.addAll(warnMessagesMap.keySet());

		for (String servletContextName : servletContextNames) {
			if (!servletContextName.isEmpty()) {
				Release release = ReleaseLocalServiceUtil.fetchRelease(
					servletContextName);

				if ((release != null) &&
					(release.getState() != ReleaseConstants.STATE_GOOD)) {

					_log.error(
						StringBundler.concat(
							"Module ", servletContextName,
							" has release state ",
							_getReleaseStateLabel(release.getState()),
							", schema version ", release.getSchemaVersion()));
				}
			}

			for (String message :
					errorMessagesMap.getOrDefault(
						servletContextName, Collections.emptyList())) {

				_log.error(message);
			}

			if (_log.isWarnEnabled()) {
				for (String message :
						warnMessagesMap.getOrDefault(
							servletContextName, Collections.emptyList())) {

					_log.warn(message);
				}
			}
		}

		if (!_log.isInfoEnabled()) {
			return;
		}

		if (!customTableNames.isEmpty()) {
			_log.info(
				_getMessage(
					customTableNames,
					"Custom or untracked tables were detected",
					StringPool.BLANK));
		}

		if (!customViewNames.isEmpty()) {
			_log.info(
				_getMessage(
					customViewNames, "Custom or untracked views were detected",
					StringPool.BLANK));
		}
	}

	private static Map<String, List<String>> _getColumnDefinitionsMap() {
		Map<String, List<String>> columnDefinitionsMap =
			DBResourceUtil.getPortalColumnDefinitionsMap();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (!symbolicName.startsWith("com.liferay") ||
				(!BundleUtil.isLiferayRequireSchemaVersionBundle(bundle) &&
				 !BundleUtil.isLiferayServiceBundle(bundle))) {

				continue;
			}

			columnDefinitionsMap.putAll(
				DBResourceUtil.getModuleColumnDefinitionsMap(bundle));
		}

		return columnDefinitionsMap;
	}

	private void _addColumnMessages(
		Map<String, List<String>> columnMessagesMap,
		Map<String, List<String>> messagesMap,
		Map<String, String> tablesServletContextNames) {

		for (Map.Entry<String, List<String>> entry :
				columnMessagesMap.entrySet()) {

			List<String> messages = messagesMap.computeIfAbsent(
				tablesServletContextNames.get(entry.getKey()),
				key -> new ArrayList<>());

			messages.addAll(entry.getValue());
		}
	}

	private void _addMessages(
		Map<String, List<String>> messagesMap, Collection<String> names,
		String prefix, Map<String, String> servletContextNames) {

		Map<String, List<String>> namesMap = MapUtil.toPartitionMap(
			ListUtil.fromCollection(names),
			name -> servletContextNames.getOrDefault(name, StringPool.BLANK));

		for (Map.Entry<String, List<String>> entry : namesMap.entrySet()) {
			String servletContextName = entry.getKey();

			List<String> messages = messagesMap.computeIfAbsent(
				servletContextName, key -> new ArrayList<>());

			messages.add(
				_getMessage(entry.getValue(), prefix, servletContextName));
		}
	}

	private Set<String> _asymmetricDifference(
		Collection<String> collection1, Collection<String> collection2) {

		Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		names.addAll(collection1);
		names.removeAll(collection2);

		return names;
	}

	private String _getMessage(
		Collection<String> names, String prefix, String servletContextName) {

		Set<String> sortedNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		sortedNames.addAll(names);

		return StringBundler.concat(
			_getMessage(prefix, servletContextName), StringPool.COLON,
			StringPool.SPACE, sortedNames);
	}

	private String _getMessage(String message, String servletContextName) {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			message = StringBundler.concat(
				message, " for company ",
				CompanyThreadLocal.getNonsystemCompanyId());
		}

		if (!servletContextName.isEmpty()) {
			message = StringBundler.concat(
				message, " in module ", servletContextName);
		}

		return message;
	}

	private String _getReleaseStateLabel(int state) {
		if (state == ReleaseConstants.STATE_UPGRADE_FAILURE) {
			return "upgrade failure";
		}

		if (state == ReleaseConstants.STATE_VERIFY_FAILURE) {
			return "verify failure";
		}

		return String.valueOf(state);
	}

	private Set<String> _intersect(
		Collection<String> collection1, Collection<String> collection2) {

		Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		names.addAll(collection1);
		names.retainAll(collection2);

		return names;
	}

	private void _verifyColumns(
			Set<String> databaseTableNames, DBInspector dbInspector,
			Map<String, List<String>> errorMessagesMap,
			Map<String, List<String>> warnMessagesMap)
		throws Exception {

		Map<String, List<String>> errorColumnMessagesMap =
			new ConcurrentSkipListMap<>();
		Map<String, String> tablesServletContextNames =
			_tablesServletContextNamesDCLSingleton.getSingleton(
				DBResourceUtil::getTablesServletContextNames);
		Map<String, List<String>> warnColumnMessagesMap =
			new ConcurrentSkipListMap<>();

		processConcurrently(
			_columnDefinitionsMapDCLSingleton.getSingleton(
				PostupgradeVerifyDatabaseState::_getColumnDefinitionsMap),
			entry -> {
				String tableName = entry.getKey();

				String servletContextName = tablesServletContextNames.get(
					tableName);

				if ((servletContextName == null) ||
					!databaseTableNames.contains(tableName)) {

					return;
				}

				Set<String> databaseColumnNames = new TreeSet<>(
					String.CASE_INSENSITIVE_ORDER);

				try (ResultSet resultSet = dbInspector.getColumnsResultSet(
						tableName)) {

					while (resultSet.next()) {
						databaseColumnNames.add(
							resultSet.getString("COLUMN_NAME"));
					}
				}

				String normalizedTableName = dbInspector.normalizeName(
					tableName);

				Set<String> expectedColumnNames = new TreeSet<>(
					String.CASE_INSENSITIVE_ORDER);

				for (String columnDefinition : entry.getValue()) {
					int index = columnDefinition.indexOf(StringPool.SPACE);

					String columnName = columnDefinition.substring(0, index);

					expectedColumnNames.add(columnName);

					if (!databaseColumnNames.contains(columnName)) {
						continue;
					}

					String columnType = columnDefinition.substring(index + 1);

					if (!dbInspector.isSupportedColumnType(columnType) ||
						dbInspector.hasColumnType(
							tableName, columnName, columnType)) {

						continue;
					}

					List<String> messages =
						warnColumnMessagesMap.computeIfAbsent(
							tableName, key -> new ArrayList<>());

					messages.add(
						_getMessage(
							StringBundler.concat(
								"Column ",
								dbInspector.normalizeName(columnName),
								" is not defined as ", columnType, " for ",
								normalizedTableName, " table"),
							servletContextName));
				}

				Set<String> missingColumnNames = _asymmetricDifference(
					expectedColumnNames, databaseColumnNames);

				if (!missingColumnNames.isEmpty()) {
					List<String> messages =
						errorColumnMessagesMap.computeIfAbsent(
							tableName, key -> new ArrayList<>());

					messages.add(
						_getMessage(
							TransformUtil.transform(
								missingColumnNames, dbInspector::normalizeName),
							StringBundler.concat(
								"Missing columns were detected for ",
								normalizedTableName, " table"),
							servletContextName));
				}

				Set<String> staleColumnNames = _asymmetricDifference(
					databaseColumnNames, expectedColumnNames);

				if (!staleColumnNames.isEmpty()) {
					List<String> messages =
						warnColumnMessagesMap.computeIfAbsent(
							tableName, key -> new ArrayList<>());

					messages.add(
						_getMessage(
							TransformUtil.transform(
								staleColumnNames, dbInspector::normalizeName),
							StringBundler.concat(
								"Stale columns were detected for ",
								normalizedTableName, " table"),
							servletContextName));
				}
			},
			null);

		_addColumnMessages(
			errorColumnMessagesMap, errorMessagesMap,
			tablesServletContextNames);
		_addColumnMessages(
			warnColumnMessagesMap, warnMessagesMap, tablesServletContextNames);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PostupgradeVerifyDatabaseState.class);

	private static final DCLSingleton<Map<String, List<String>>>
		_columnDefinitionsMapDCLSingleton = new DCLSingleton<>();
	private static final DCLSingleton<Map<String, String>>
		_historicalServiceComponentTablesServletContextNamesDCLSingleton =
			new DCLSingleton<>();
	private static final DCLSingleton<Map<String, String>>
		_tablesServletContextNamesDCLSingleton = new DCLSingleton<>();

}
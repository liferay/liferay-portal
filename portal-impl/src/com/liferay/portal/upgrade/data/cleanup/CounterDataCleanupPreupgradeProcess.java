/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Luis Ortiz
 */
public class CounterDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_liferayTableNames = DBResourceUtil.getLiferayTableNames(connection);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select name, currentId from Counter");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			DBInspector dbInspector = new DBInspector(connection);
			List<String> excludedTableNames = new ArrayList<>();
			String kernelCounterName = "";

			while (resultSet.next()) {
				String counterName = resultSet.getString("name");

				if (counterName.equals(Company.class.getName()) &&
					!PropsValues.COMPANY_PREDICTABLE_COMPANY_IDS_ENABLED) {

					_deleteCounter(counterName);

					continue;
				}

				if (counterName.equals(Counter.class.getName()) ||
					counterName.equals("com.liferay.counter.model.Counter")) {

					kernelCounterName = counterName;

					continue;
				}

				long counterValue = resultSet.getLong("currentId");

				Matcher matcher = _layoutSpecificCounterNamePattern.matcher(
					counterName);

				if (matcher.matches()) {
					long groupId = GetterUtil.getLong(matcher.group(2));
					boolean privateLayout = matcher.group(
						3
					).equalsIgnoreCase(
						"true"
					);

					_checkLayoutSpecificCounter(
						counterName, counterValue, dbInspector, groupId,
						privateLayout);

					continue;
				}

				String tableName =
					DataCleanupPreupgradeProcessUtil.getTableName(
						connection, dbInspector, counterName);

				if (tableName == null) {
					tableName = StringUtil.extractLast(counterName, '.');
				}

				if (tableName == null) {
					tableName = counterName;
				}

				if (!dbInspector.isObjectTable(tableName) &&
					!_liferayTableNames.contains(tableName)) {

					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Skipping counter ", counterName,
								" because its associated table does not ",
								"belong to Liferay"));
					}

					continue;
				}

				if (!dbInspector.hasTable(tableName)) {
					if (_log.isWarnEnabled()) {
						_log.warn("Table " + tableName + " does not exist");
					}

					continue;
				}

				_checkSpecificCounter(
					counterName, counterValue, dbInspector, tableName);

				if (!counterName.equals(DLFileEntry.class.getName())) {
					excludedTableNames.add(
						dbInspector.normalizeName(tableName));
				}
			}

			_checkKernelCounter(
				kernelCounterName, dbInspector, excludedTableNames);
		}
	}

	private void _checkKernelCounter(
			String counterName, DBInspector dbInspector,
			List<String> excludedTableNames)
		throws Exception {

		long latestCounterValue = 0L;
		String maxValueTableName = null;

		Map<String, Long> maxValues = new ConcurrentHashMap<>();

		List<String> tableNames = dbInspector.getTableNames(null);

		tableNames.remove(dbInspector.normalizeName("Company"));
		tableNames.remove(dbInspector.normalizeName("Counter"));
		tableNames.removeAll(excludedTableNames);

		processConcurrently(
			tableNames.toArray(new String[0]),
			tableName -> {
				if (!dbInspector.isObjectTable(tableName) &&
					!_liferayTableNames.contains(tableName)) {

					return;
				}

				String columnName =
					DataCleanupPreupgradeProcessUtil.getPrimaryKeyColumnName(
						connection, dbInspector, tableName);

				if ((columnName == null) ||
					!dbInspector.isNumeric(tableName, columnName)) {

					return;
				}

				long maxValue = _getMaxValue(
					columnName, dbInspector, tableName);

				if (maxValue > 0) {
					maxValues.put(tableName, maxValue);
				}
			},
			"Unable to compute the maximum primary key value");

		for (Map.Entry<String, Long> entry : maxValues.entrySet()) {
			long maxValue = entry.getValue();

			if (maxValue > latestCounterValue) {
				latestCounterValue = maxValue;
				maxValueTableName = entry.getKey();
			}
		}

		long counterValue = 0L;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select currentId from Counter where name = ?")) {

			preparedStatement.setString(1, counterName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					counterValue = resultSet.getLong("currentId");
				}
			}
		}

		if (counterValue < latestCounterValue) {
			CounterLocalServiceUtil.reset(counterName, latestCounterValue);

			if (_log.isInfoEnabled()) {
				_log.info(
					_getLogMessage(
						counterName, latestCounterValue, maxValueTableName));
			}
		}
	}

	private void _checkLayoutSpecificCounter(
			String counterName, long counterValue, DBInspector dbInspector,
			long groupId, boolean privateLayout)
		throws Exception {

		if (!dbInspector.hasTable("Layout")) {
			_log.error("The Layout table does not exist");

			return;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select max(layoutId) as layoutId from Layout where ",
						"groupId = ? and privateLayout = ",
						privateLayout ? "[$TRUE$]" : "[$FALSE$]")))) {

			preparedStatement.setLong(1, groupId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					long maxValue = resultSet.getLong("layoutId");

					if (resultSet.wasNull()) {
						_deleteCounter(counterName);

						return;
					}

					if (counterValue >= maxValue) {
						return;
					}

					CounterLocalServiceUtil.reset(counterName, maxValue);

					if (_log.isInfoEnabled()) {
						_log.info(_getLogMessage(counterName, maxValue, null));
					}
				}
			}
		}
	}

	private void _checkSpecificCounter(
			String counterName, long counterValue, DBInspector dbInspector,
			String tableName)
		throws Exception {

		String columnName =
			DataCleanupPreupgradeProcessUtil.getPrimaryKeyColumnName(
				connection, dbInspector, tableName);

		if (counterName.equals(DLFileEntry.class.getName())) {
			columnName = "name";
		}

		if (columnName == null) {
			return;
		}

		long maxValue = _getMaxValue(columnName, dbInspector, tableName);

		if (counterValue >= maxValue) {
			return;
		}

		CounterLocalServiceUtil.reset(counterName, maxValue);

		if (_log.isInfoEnabled()) {
			_log.info(_getLogMessage(counterName, maxValue, null));
		}
	}

	private void _deleteCounter(String counterName) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from Counter where name = ?")) {

			preparedStatement.setString(1, counterName);

			preparedStatement.executeUpdate();

			if (_log.isInfoEnabled()) {
				_log.info(
					"Deleted counter " + counterName + " because it is unused");
			}
		}
	}

	private String _getLogMessage(
		String counterName, long countervalue, String tableName) {

		return StringBundler.concat(
			"Counter ", counterName, " has been reset to value ", countervalue,
			(tableName != null) ? " due to table " + tableName : "");
	}

	private long _getMaxValue(
			String columnName, DBInspector dbInspector, String tableName)
		throws Exception {

		if (!dbInspector.isNumeric(tableName, columnName)) {
			DB db = DBManagerUtil.getDB();

			String sql = _getMaxValueSQL(columnName, db.getDBType(), tableName);

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(sql);

				ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.next()) {
					long maxValue = resultSet.getLong(columnName);

					if (!resultSet.wasNull()) {
						return maxValue;
					}
				}
			}

			return 0L;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select max(", columnName, ") as ", columnName, " from ",
					tableName));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getLong(columnName);
			}
		}

		return 0L;
	}

	private String _getMaxValueSQL(
		String columnName, DBType dbType, String tableName) {

		if (dbType == DBType.DB2) {
			return StringBundler.concat(
				"select max(cast(case when regexp_like(", columnName,
				", '^[0-9]{1,18}$') then ", columnName, " end as bigint)) as ",
				columnName, " from ", tableName);
		}
		else if ((dbType == DBType.MARIADB) || (dbType == DBType.MYSQL)) {
			return StringBundler.concat(
				"select max(cast(", columnName, " as unsigned)) as ",
				columnName, " from ", tableName, " where ", columnName,
				" regexp '^[0-9]{1,18}$'");
		}
		else if (dbType == DBType.ORACLE) {
			return StringBundler.concat(
				"select max(to_number(", columnName, ")) as ", columnName,
				" from ", tableName, " where regexp_like(", columnName,
				", '^[0-9]{1,18}$')");
		}
		else if (dbType == DBType.POSTGRESQL) {
			return StringBundler.concat(
				"select max(cast(", columnName, " as bigint)) as ", columnName,
				" from ", tableName, " where ", columnName,
				" ~ '^[0-9]{1,18}$'");
		}
		else if (dbType == DBType.SQLSERVER) {
			return StringBundler.concat(
				"select max(try_cast(", columnName, " as bigint)) as ",
				columnName, " from ", tableName);
		}

		throw new UnsupportedOperationException(
			"Unsupported database type: " + dbType);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CounterDataCleanupPreupgradeProcess.class);

	private static final Pattern _layoutSpecificCounterNamePattern =
		Pattern.compile("^([a-zA-Z0-9_.]+)#(\\d+)#(true|false)$");

	private Set<String> _liferayTableNames;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.jdbc.spring.DataSourceFactoryBean;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeTable;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.upgrade.util.UpgradeTableFactoryUtil;
import com.liferay.portal.upgrade.v7_0_0.util.ClassNameTable;
import com.liferay.portal.upgrade.v7_0_0.util.ClusterGroupTable;
import com.liferay.portal.upgrade.v7_0_0.util.CompanyTable;
import com.liferay.portal.upgrade.v7_0_0.util.CounterTable;
import com.liferay.portal.upgrade.v7_0_0.util.CountryTable;
import com.liferay.portal.upgrade.v7_0_0.util.PortalPreferencesTable;
import com.liferay.portal.upgrade.v7_0_0.util.RegionTable;
import com.liferay.portal.upgrade.v7_0_0.util.ReleaseTable;
import com.liferay.portal.upgrade.v7_0_0.util.ResourceActionTable;
import com.liferay.portal.upgrade.v7_0_0.util.ServiceComponentTable;
import com.liferay.portal.upgrade.v7_0_0.util.VirtualHostTable;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * @author Manuel de la Peña
 */
public class UpgradeSharding extends UpgradeProcess {

	protected void copyCompanyTable(
			Connection sourceConnection, Connection targetConnection,
			String shardName)
		throws Exception {

		copyControlTable(
			sourceConnection, targetConnection, CompanyTable.TABLE_NAME,
			CompanyTable.TABLE_COLUMNS, CompanyTable.TABLE_SQL_CREATE,
			CompanyTable.TABLE_SQL_ADD_INDEXES);

		String companyIdsString = ListUtil.toString(
			getCompanyIds(shardName), StringPool.NULL, StringPool.COMMA);

		runSQL(
			sourceConnection,
			"delete from Company where companyId in (" + companyIdsString +
				")");

		runSQL(
			targetConnection,
			"delete from Company where companyId not in (" + companyIdsString +
				")");
	}

	protected void copyControlTable(
			Connection sourceConnection, Connection targetConnection,
			String tableName, Object[][] columns, String createSQL,
			String[] indexesSQL)
		throws Exception {

		try {
			if (hasRows(targetConnection, tableName)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Control table ", tableName, " should not contain ",
							"data in a nondefault shard"));
				}
			}

			dropTable(targetConnection, tableName);
		}
		catch (SQLException sqlException) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to drop control table " + tableName +
						" because it  does not exist in the target shard",
					sqlException);
			}
		}

		UpgradeTable upgradeTable = UpgradeTableFactoryUtil.getUpgradeTable(
			tableName, columns);

		upgradeTable.setCreateSQL(createSQL);
		upgradeTable.setIndexesSQL(indexesSQL);

		upgradeTable.copyTable(sourceConnection, targetConnection);
	}

	protected void copyControlTables(List<String> shardNames) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<String> uniqueShardNames = ListUtil.unique(shardNames);

			if (uniqueShardNames.size() == 1) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Skip copying of control tables because all " +
							"companies are located in the same shard");
				}

				return;
			}

			String defaultShardName = GetterUtil.getString(
				PropsUtil.get("shard.default.name"), "default");

			for (String uniqueShardName : uniqueShardNames) {
				if (!uniqueShardName.equals(defaultShardName)) {
					copyControlTables(uniqueShardName);
				}
			}
		}
	}

	protected void copyControlTables(String shardName) throws Exception {
		DataSourceFactoryBean dataSourceFactoryBean =
			new DataSourceFactoryBean();

		dataSourceFactoryBean.setPropertyPrefix("jdbc." + shardName + ".");

		DataSource dataSource = dataSourceFactoryBean.createInstance();

		try (Connection targetConnection = dataSource.getConnection()) {
			copyCompanyTable(connection, targetConnection, shardName);
			copyControlTable(
				connection, targetConnection, ClassNameTable.TABLE_NAME,
				ClassNameTable.TABLE_COLUMNS, ClassNameTable.TABLE_SQL_CREATE,
				ClassNameTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, ClusterGroupTable.TABLE_NAME,
				ClusterGroupTable.TABLE_COLUMNS,
				ClusterGroupTable.TABLE_SQL_CREATE,
				ClusterGroupTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, CounterTable.TABLE_NAME,
				CounterTable.TABLE_COLUMNS, CounterTable.TABLE_SQL_CREATE,
				CounterTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, CountryTable.TABLE_NAME,
				CountryTable.TABLE_COLUMNS, CountryTable.TABLE_SQL_CREATE,
				CountryTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, PortalPreferencesTable.TABLE_NAME,
				PortalPreferencesTable.TABLE_COLUMNS,
				PortalPreferencesTable.TABLE_SQL_CREATE,
				PortalPreferencesTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, RegionTable.TABLE_NAME,
				RegionTable.TABLE_COLUMNS, RegionTable.TABLE_SQL_CREATE,
				RegionTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, ReleaseTable.TABLE_NAME,
				ReleaseTable.TABLE_COLUMNS, ReleaseTable.TABLE_SQL_CREATE,
				ReleaseTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, ResourceActionTable.TABLE_NAME,
				ResourceActionTable.TABLE_COLUMNS,
				ResourceActionTable.TABLE_SQL_CREATE,
				ResourceActionTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, ServiceComponentTable.TABLE_NAME,
				ServiceComponentTable.TABLE_COLUMNS,
				ServiceComponentTable.TABLE_SQL_CREATE,
				ServiceComponentTable.TABLE_SQL_ADD_INDEXES);
			copyControlTable(
				connection, targetConnection, VirtualHostTable.TABLE_NAME,
				VirtualHostTable.TABLE_COLUMNS,
				VirtualHostTable.TABLE_SQL_CREATE,
				VirtualHostTable.TABLE_SQL_ADD_INDEXES);
		}
		catch (Exception exception) {
			_log.error("Unable to copy control tables", exception);
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		List<String> shardNames = getShardNames();

		if (shardNames.size() <= 1) {
			return;
		}

		copyControlTables(shardNames);
	}

	protected void dropTable(Connection connection, String tableName)
		throws IOException, SQLException {

		runSQL(connection, "DROP_TABLE_IF_EXISTS(" + tableName + ")");

		if (_log.isDebugEnabled()) {
			_log.debug("Deleted table " + tableName);
		}
	}

	protected List<Long> getCompanyIds(String shardName) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select classPK from Shard where name = ?")) {

			preparedStatement.setString(1, shardName);

			List<Long> companyIds = new ArrayList<>();

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					companyIds.add(resultSet.getLong("classPK"));
				}
			}

			return companyIds;
		}
	}

	protected List<String> getShardNames() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select name from Shard");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<String> shardNames = new ArrayList<>();

			while (resultSet.next()) {
				shardNames.add(resultSet.getString("name"));
			}

			return shardNames;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeSharding.class);

}
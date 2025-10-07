/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.upgrade.v2_0_0;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Rafael Praxedes
 */
public class SchemaUpgradeProcess extends UpgradeProcess {

	public SchemaUpgradeProcess(CounterLocalService counterLocalService) {
		_counterLocalService = counterLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			String template = StringUtil.read(
				SchemaUpgradeProcess.class.getResourceAsStream(
					"dependencies/update.sql"));

			runSQLTemplate(template, false);

			if (!hasTable("WorkflowMetricsSLADefinition")) {
				return;
			}

			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						"select WorkflowMetricsSLADefinition.* from " +
							"WorkflowMetricsSLADefinition");
				ResultSet resultSet = preparedStatement1.executeQuery();
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"insert into WMSLADefinition (mvccVersion, uuid_, ",
							"wmSLADefinitionId, groupId, companyId, userId, ",
							"userName, createDate, modifiedDate, active_, ",
							"calendarKey, description, duration, name, ",
							"pauseNodeKeys, processId, processVersion, ",
							"startNodeKeys, stopNodeKeys, version, status, ",
							"statusByUserId, statusByUserName, statusDate) ",
							"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
							"?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"));
				PreparedStatement preparedStatement3 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"insert into WMSLADefinitionVersion (mvccVersion, ",
							"uuid_, wmSLADefinitionVersionId, groupId, ",
							"companyId, userId, userName, createDate, ",
							"modifiedDate, active_, calendarKey, description, ",
							"duration, name, pauseNodeKeys, processId, ",
							"processVersion, startNodeKeys, stopNodeKeys, ",
							"version, wmSLADefinitionId, status, ",
							"statusByUserId, statusByUserName, statusDate) ",
							"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
							"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

				while (resultSet.next()) {
					preparedStatement2.setLong(
						1, resultSet.getLong("mvccVersion"));
					preparedStatement2.setString(
						2, resultSet.getString("uuid_"));
					preparedStatement2.setLong(
						3, resultSet.getLong("workflowMetricsSLADefinitionId"));
					preparedStatement2.setLong(4, resultSet.getLong("groupId"));
					preparedStatement2.setLong(
						5, resultSet.getLong("companyId"));
					preparedStatement2.setLong(6, resultSet.getLong("userId"));
					preparedStatement2.setString(
						7, resultSet.getString("userName"));
					preparedStatement2.setTimestamp(
						8, resultSet.getTimestamp("createDate"));
					preparedStatement2.setTimestamp(
						9, resultSet.getTimestamp("modifiedDate"));
					preparedStatement2.setBoolean(10, true);
					preparedStatement2.setString(
						11, resultSet.getString("calendarKey"));
					preparedStatement2.setString(
						12, resultSet.getString("description"));
					preparedStatement2.setLong(
						13, resultSet.getLong("duration"));
					preparedStatement2.setString(
						14, resultSet.getString("name"));
					preparedStatement2.setString(
						15, resultSet.getString("pauseNodeKeys"));
					preparedStatement2.setLong(
						16, resultSet.getLong("processId"));
					preparedStatement2.setString(
						17, resultSet.getString("processVersion"));
					preparedStatement2.setString(
						18, resultSet.getString("startNodeKeys"));
					preparedStatement2.setString(
						19, resultSet.getString("stopNodeKeys"));
					preparedStatement2.setString(20, _DEFAULT_VERSION);
					preparedStatement2.setInt(21, resultSet.getInt("status"));
					preparedStatement2.setLong(22, resultSet.getLong("userId"));
					preparedStatement2.setString(
						23, resultSet.getString("userName"));
					preparedStatement2.setTimestamp(
						24, resultSet.getTimestamp("modifiedDate"));

					preparedStatement2.addBatch();

					preparedStatement3.setLong(1, 0);
					preparedStatement3.setString(2, PortalUUIDUtil.generate());
					preparedStatement3.setLong(
						3, _counterLocalService.increment());
					preparedStatement3.setLong(4, resultSet.getLong("groupId"));
					preparedStatement3.setLong(
						5, resultSet.getLong("companyId"));
					preparedStatement3.setLong(6, resultSet.getLong("userId"));
					preparedStatement3.setString(
						7, resultSet.getString("userName"));
					preparedStatement3.setTimestamp(
						8, resultSet.getTimestamp("createDate"));
					preparedStatement3.setTimestamp(
						9, resultSet.getTimestamp("modifiedDate"));
					preparedStatement3.setBoolean(10, true);
					preparedStatement3.setString(
						11, resultSet.getString("calendarKey"));
					preparedStatement3.setString(
						12, resultSet.getString("description"));
					preparedStatement3.setLong(
						13, resultSet.getLong("duration"));
					preparedStatement3.setString(
						14, resultSet.getString("name"));
					preparedStatement3.setString(
						15, resultSet.getString("pauseNodeKeys"));
					preparedStatement3.setLong(
						16, resultSet.getLong("processId"));
					preparedStatement3.setString(
						17, resultSet.getString("processVersion"));
					preparedStatement3.setString(
						18, resultSet.getString("startNodeKeys"));
					preparedStatement3.setString(
						19, resultSet.getString("stopNodeKeys"));
					preparedStatement3.setString(20, _DEFAULT_VERSION);
					preparedStatement3.setLong(
						21,
						resultSet.getLong("workflowMetricsSLADefinitionId"));
					preparedStatement3.setInt(22, resultSet.getInt("status"));
					preparedStatement3.setLong(23, resultSet.getLong("userId"));
					preparedStatement3.setString(
						24, resultSet.getString("userName"));
					preparedStatement3.setTimestamp(
						25, resultSet.getTimestamp("modifiedDate"));

					preparedStatement3.addBatch();
				}

				preparedStatement2.executeBatch();

				preparedStatement3.executeBatch();
			}
		}
	}

	private static final String _DEFAULT_VERSION = "1.0";

	private final CounterLocalService _counterLocalService;

}
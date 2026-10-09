/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v1_4_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Inácio Nery
 */
public class KaleoDefinitionVersionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeKaleoDefinitionVersion();

		_removeDuplicateKaleoDefinitions();
		_removeStartKaleoNodeId();
	}

	private void _addBatch(
			PreparedStatement preparedStatement, long kaleoDefinitionId,
			long kaleoDefinitionVersionId)
		throws SQLException {

		preparedStatement.setLong(1, kaleoDefinitionVersionId);
		preparedStatement.setLong(2, kaleoDefinitionId);

		preparedStatement.addBatch();
	}

	private String _getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

	private void _removeDuplicateKaleoDefinitions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select companyId, name, MAX(version) as version from " +
					"KaleoDefinition group by companyId, name");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"delete from KaleoDefinition where companyId = ? and " +
						"name = ? and version < ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");
				String name = resultSet.getString("name");
				int version = resultSet.getInt("version");

				preparedStatement2.setLong(1, companyId);
				preparedStatement2.setString(2, name);
				preparedStatement2.setInt(3, version);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _removeStartKaleoNodeId() throws Exception {
		alterTableDropColumn("KaleoDefinition", "startKaleoNodeId");
	}

	private void _upgradeKaleoDefinitionVersion() throws Exception {
		List<PreparedStatement> preparedStatements = new ArrayList<>(17);

		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select * from KaleoDefinition kd where not exists ",
					"(select 1 from KaleoDefinitionVersion kdv where kdv.name ",
					"= kd.name and kdv.companyId = kd.companyId)"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into KaleoDefinitionVersion ",
						"(kaleoDefinitionVersionId, groupId, companyId, ",
						"userId, userName, statusByUserId, statusByUserName, ",
						"statusDate, createDate, modifiedDate, name, title, ",
						"description, content, version, startKaleoNodeId, ",
						"status) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
						"?, ?, ?, ?, ? )"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			for (String tableName : _TABLE_NAMES) {
				if (!hasColumn(tableName, "kaleoDefinitionId")) {
					continue;
				}

				preparedStatements.add(
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						StringBundler.concat(
							"update ", tableName,
							" set kaleoDefinitionVersionId = ? where ",
							"kaleoDefinitionId = ? ")));
			}

			while (resultSet.next()) {
				long kaleoDefinitionId = resultSet.getLong("kaleoDefinitionId");
				long groupId = resultSet.getLong("groupId");
				long companyId = resultSet.getLong("companyId");
				long userId = resultSet.getLong("userId");
				String userName = resultSet.getString("userName");
				Timestamp createDate = resultSet.getTimestamp("createDate");
				Timestamp modifiedDate = resultSet.getTimestamp("modifiedDate");
				String name = resultSet.getString("name");
				String title = resultSet.getString("title");
				String description = resultSet.getString("description");
				String content = resultSet.getString("content");
				int version = resultSet.getInt("version");
				long startKaleoNodeId = resultSet.getLong("startKaleoNodeId");

				long kaleoDefinitionVersionId = increment();

				preparedStatement2.setLong(1, kaleoDefinitionVersionId);

				preparedStatement2.setLong(2, groupId);
				preparedStatement2.setLong(3, companyId);
				preparedStatement2.setLong(4, userId);
				preparedStatement2.setString(5, userName);
				preparedStatement2.setLong(6, userId);
				preparedStatement2.setString(7, userName);
				preparedStatement2.setTimestamp(8, modifiedDate);
				preparedStatement2.setTimestamp(9, createDate);
				preparedStatement2.setTimestamp(10, modifiedDate);
				preparedStatement2.setString(11, name);
				preparedStatement2.setString(12, title);
				preparedStatement2.setString(13, description);
				preparedStatement2.setString(14, content);
				preparedStatement2.setString(15, _getVersion(version));
				preparedStatement2.setLong(16, startKaleoNodeId);
				preparedStatement2.setInt(
					17, WorkflowConstants.STATUS_APPROVED);

				preparedStatement2.addBatch();

				for (PreparedStatement preparedStatement : preparedStatements) {
					_addBatch(
						preparedStatement, kaleoDefinitionId,
						kaleoDefinitionVersionId);
				}
			}

			preparedStatement2.executeBatch();

			for (PreparedStatement preparedStatement : preparedStatements) {
				preparedStatement.executeBatch();
			}
		}
		finally {
			for (PreparedStatement preparedStatement : preparedStatements) {
				DataAccess.cleanUp(preparedStatement);
			}
		}
	}

	private static final String[] _TABLE_NAMES = {
		"KaleoAction", "KaleoCondition", "KaleoInstance", "KaleoInstanceToken",
		"KaleoLog", "KaleoNode", "KaleoNotification",
		"KaleoNotificationRecipient", "KaleoTask", "KaleoTaskAssignment",
		"KaleoTaskAssignmentInstance", "KaleoTaskForm", "KaleoTaskFormInstance",
		"KaleoTaskInstanceToken", "KaleoTimer", "KaleoTimerInstanceToken",
		"KaleoTransition"
	};

}
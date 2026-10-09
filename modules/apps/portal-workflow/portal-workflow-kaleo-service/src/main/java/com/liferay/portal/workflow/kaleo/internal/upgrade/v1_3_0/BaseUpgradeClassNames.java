/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v1_3_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Map;

/**
 * @author Marcellus Tavares
 */
public abstract class BaseUpgradeClassNames extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updateClassName("KaleoInstance", "className");
		updateClassName("KaleoInstanceToken", "className");
		updateClassName("KaleoLog", "currentAssigneeClassName");
		updateClassName("KaleoLog", "previousAssigneeClassName");
		updateClassName("KaleoNotificationRecipient", "recipientClassName");
		updateClassName("KaleoTaskAssignment", "assigneeClassName");
		updateClassName("KaleoTaskAssignmentInstance", "assigneeClassName");
		updateClassName("KaleoTaskInstanceToken", "className");

		_updateWorkflowContextEntryClassName(
			"KaleoInstance", "kaleoInstanceId");
		_updateWorkflowContextEntryClassName("KaleoLog", "kaleoLogId");
		_updateWorkflowContextEntryClassName(
			"KaleoTaskInstanceToken", "kaleoTaskInstanceTokenId");
		_updateWorkflowContextEntryClassName(
			"KaleoTimerInstanceToken", "kaleoTimerInstanceTokenId");
	}

	protected abstract String getWhereClause();

	protected abstract void updateClassName(
		String tableName, String columnName);

	protected abstract Map<String, Serializable> updateWorkflowContext(
		String workflowContextJSON);

	protected void updateWorkflowContext(
			String tableName, String primaryKeyName, long primaryKeyValue,
			String workflowContext)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName, " set workflowContext = ? where ",
					primaryKeyName, " = ?"))) {

			preparedStatement.setString(1, workflowContext);
			preparedStatement.setLong(2, primaryKeyValue);

			preparedStatement.executeUpdate();
		}
	}

	private void _updateWorkflowContextEntryClassName(
			String tableName, String primaryKeyName)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(tableName);

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", primaryKeyName, ", workflowContext from ",
					tableName, getWhereClause()));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String workflowContextJSON = resultSet.getString(
					"workflowContext");

				if (Validator.isNull(workflowContextJSON)) {
					continue;
				}

				Map<String, Serializable> workflowContext =
					updateWorkflowContext(workflowContextJSON);

				if (workflowContext == null) {
					continue;
				}

				long primaryKeyValue = resultSet.getLong(primaryKeyName);

				updateWorkflowContext(
					tableName, primaryKeyName, primaryKeyValue,
					WorkflowContextUtil.convert(workflowContext));
			}
		}
	}

}
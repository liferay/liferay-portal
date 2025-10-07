/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.upgrade.v2_0_1;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocalManager;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jorge Avalos
 */
public class BackgroundTaskCompanyIdUpgradeProcess extends UpgradeProcess {

	public BackgroundTaskCompanyIdUpgradeProcess(
		BackgroundTaskThreadLocalManager backgroundTaskThreadLocalManager) {

		_backgroundTaskThreadLocalManager = backgroundTaskThreadLocalManager;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			processConcurrently(
				"select backgroundTaskId, taskContextMap from BackgroundTask",
				"update BackgroundTask set taskContextMap = ? where " +
					"backgroundTaskId = ?",
				resultSet -> new Object[] {
					resultSet.getLong("backgroundTaskId"),
					resultSet.getString("taskContextMap")
				},
				(values, preparedStatement) -> {
					String taskContextMapValue = (String)values[1];

					if (taskContextMapValue != null) {
						if (_isEmpty(taskContextMapValue)) {
							Map<String, Serializable> taskContextMap =
								new HashMap<>();

							_backgroundTaskThreadLocalManager.
								serializeThreadLocals(taskContextMap);

							preparedStatement.setString(
								1, JSONFactoryUtil.serialize(taskContextMap));

							preparedStatement.setLong(2, (Long)values[0]);

							preparedStatement.addBatch();

							return;
						}

						Map<String, Serializable> taskContextMap =
							(Map<String, Serializable>)
								JSONFactoryUtil.deserialize(
									taskContextMapValue);

						taskContextMap.remove("companyId");

						Map<String, Serializable> threadLocalValues =
							(Map<String, Serializable>)taskContextMap.get(
								"threadLocalValues");

						if (threadLocalValues != null) {
							threadLocalValues.remove("companyId");
						}

						preparedStatement.setString(
							1, JSONFactoryUtil.serialize(taskContextMap));

						preparedStatement.setLong(2, (Long)values[0]);

						preparedStatement.addBatch();
					}
				},
				null);
		}
	}

	private boolean _isEmpty(String json) {
		if (Validator.isNull(json) || Objects.equals(json, "[]") ||
			Objects.equals(json, "{}")) {

			return true;
		}

		return false;
	}

	private final BackgroundTaskThreadLocalManager
		_backgroundTaskThreadLocalManager;

}
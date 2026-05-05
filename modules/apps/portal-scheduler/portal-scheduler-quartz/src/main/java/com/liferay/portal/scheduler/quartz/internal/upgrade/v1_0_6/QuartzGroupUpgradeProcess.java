/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_6;

import com.liferay.petra.io.ProtectedClassLoaderObjectInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.InputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.quartz.JobDataMap;

/**
 * @author Mariano Álvaro Sáiz
 */
public class QuartzGroupUpgradeProcess extends UpgradeProcess {

	public QuartzGroupUpgradeProcess(
		CompanyLocalService companyLocalService, JSONFactory jsonFactory) {

		_companyLocalService = companyLocalService;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Map<String, Long> companyIds = new HashMap<>();
		Map<String, String> jobGroups = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select job_name, job_group, job_data from " +
					"QUARTZ_JOB_DETAILS where job_name like '%@%' and " +
						"job_group not like '%@%'");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				JobDataMap jobDataMap = _deserializeJobDataMap(
					resultSet.getBinaryStream("job_data"));

				String jobName = resultSet.getString("job_name");

				_loadCompanyId(companyIds, jobName, jobDataMap);

				if (companyIds.containsKey(jobName)) {
					jobGroups.put(jobName, resultSet.getString("job_group"));
				}
			}
		}

		Set<String> deleteJobNames = new HashSet<>();
		Set<String> renamedJobGroups = _loadRenamedJobGroups();

		for (Map.Entry<String, Long> entry : companyIds.entrySet()) {
			String jobName = entry.getKey();

			String newJobGroup = StringBundler.concat(
				jobGroups.get(jobName), StringPool.AT, entry.getValue());

			if (renamedJobGroups.contains(newJobGroup)) {
				deleteJobNames.add(jobName);
			}
		}

		Set<String> jobNames = companyIds.keySet();

		jobNames.removeAll(deleteJobNames);

		_updateTables(
			companyIds, jobGroups, "job_group", "job_name",
			new String[] {
				"QUARTZ_FIRED_TRIGGERS", "QUARTZ_JOB_DETAILS", "QUARTZ_TRIGGERS"
			});

		_updateTables(
			companyIds, jobGroups, "trigger_group", "trigger_name",
			_QUARTZ_TRIGGER_TABLE_NAMES);

		_deleteFromTables(
			deleteJobNames, jobGroups, "trigger_group", "trigger_name",
			_QUARTZ_TRIGGER_TABLE_NAMES);
		_deleteFromTables(
			deleteJobNames, jobGroups, "job_group", "job_name",
			new String[] {"QUARTZ_JOB_DETAILS"});
	}

	private void _deleteFromTables(
			Set<String> jobNames, Map<String, String> jobGroups,
			String groupColumnName, String nameColumnName, String[] tableNames)
		throws Exception {

		for (String tableName : tableNames) {
			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						StringBundler.concat(
							"delete from ", tableName, " where ",
							nameColumnName, " = ? and ", groupColumnName,
							" = ?"))) {

				for (String jobName : jobNames) {
					preparedStatement.setString(1, jobName);
					preparedStatement.setString(2, jobGroups.get(jobName));

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}
	}

	private JobDataMap _deserializeJobDataMap(InputStream inputStream)
		throws Exception {

		try (ProtectedClassLoaderObjectInputStream
				protectedClassLoaderObjectInputStream =
					new ProtectedClassLoaderObjectInputStream(
						inputStream,
						QuartzGroupUpgradeProcess.class.getClassLoader())) {

			return (JobDataMap)
				protectedClassLoaderObjectInputStream.readObject();
		}
	}

	private void _getCompanyId(
			Map<String, Long> companyIds, String jobName, String tableName,
			String columnId, long columnValue)
		throws Exception {

		_companyLocalService.forEachCompany(
			company -> {
				if (companyIds.containsKey(jobName)) {
					return;
				}

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							StringBundler.concat(
								"select companyId from ", tableName, " where ",
								columnId, " = ?"))) {

					preparedStatement.setLong(1, columnValue);

					try (ResultSet resultSet =
							preparedStatement.executeQuery()) {

						if (resultSet.next()) {
							companyIds.put(
								jobName, resultSet.getLong("companyId"));
						}
					}
				}
			});
	}

	private void _loadCompanyId(
			Map<String, Long> companyIds, String jobName, JobDataMap jobDataMap)
		throws Exception {

		String destinationName = jobDataMap.getString(
			SchedulerEngine.DESTINATION_NAME);

		if (!_supportedDestinationNames.contains(destinationName)) {
			return;
		}

		Message message = (Message)_jsonFactory.deserialize(
			jobDataMap.getString(SchedulerEngine.MESSAGE));

		if (message.contains("companyId")) {
			companyIds.put(jobName, message.getLong("companyId"));

			return;
		}

		if (destinationName.equals("liferay/dispatch/executor")) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				(String)message.getPayload());

			long dispatchTriggerId = jsonObject.getLong("dispatchTriggerId");

			_getCompanyId(
				companyIds, jobName, "DispatchTrigger", "dispatchTriggerId",
				dispatchTriggerId);
		}
	}

	private Set<String> _loadRenamedJobGroups() throws Exception {
		Set<String> renamedJobGroups = new HashSet<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select job_group from QUARTZ_JOB_DETAILS where job_name " +
					"like '%@%' and job_group like '%@%'");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				renamedJobGroups.add(resultSet.getString("job_group"));
			}
		}

		return renamedJobGroups;
	}

	private void _updateTables(
			Map<String, Long> companyIds, Map<String, String> jobGroups,
			String groupColumnName, String nameColumnName, String[] tableNames)
		throws Exception {

		for (String tableName : tableNames) {
			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						StringBundler.concat(
							"update ", tableName, " set ", groupColumnName,
							" = ? where ", nameColumnName, " = ? and ",
							groupColumnName, " = ?"))) {

				for (Map.Entry<String, Long> entry : companyIds.entrySet()) {
					String jobName = entry.getKey();

					String jobGroup = jobGroups.get(jobName);

					String newJobGroup = StringBundler.concat(
						jobGroup, StringPool.AT, entry.getValue());

					preparedStatement.setString(1, newJobGroup);

					preparedStatement.setString(2, jobName);
					preparedStatement.setString(3, jobGroup);

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}
	}

	private static final String[] _QUARTZ_TRIGGER_TABLE_NAMES = {
		"QUARTZ_BLOB_TRIGGERS", "QUARTZ_CRON_TRIGGERS", "QUARTZ_FIRED_TRIGGERS",
		"QUARTZ_SIMPLE_TRIGGERS", "QUARTZ_SIMPROP_TRIGGERS", "QUARTZ_TRIGGERS"
	};

	private static final Set<String> _supportedDestinationNames =
		SetUtil.fromArray(
			"destination.workflow_timer", "liferay/dispatch/executor",
			"liferay/message_boards_mailing_list");

	private final CompanyLocalService _companyLocalService;
	private final JSONFactory _jsonFactory;

}
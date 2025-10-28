/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_1;

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
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.InputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;

/**
 * @author Kevin Lee
 */
public class QuartzUpgradeProcess extends UpgradeProcess {

	public QuartzUpgradeProcess(
		CompanyLocalService companyLocalService, JSONFactory jsonFactory) {

		_companyLocalService = companyLocalService;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Map<String, Long> companyIds = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select job_name, job_data from QUARTZ_JOB_DETAILS where " +
					"job_name not like '%@%'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				JobDataMap jobDataMap = _deserializeJobDataMap(
					resultSet.getBinaryStream("job_data"));

				_loadCompanyId(
					companyIds, resultSet.getString("job_name"), jobDataMap);
			}
		}

		_updateTables(
			companyIds, "job_name",
			new String[] {
				"QUARTZ_FIRED_TRIGGERS", "QUARTZ_JOB_DETAILS", "QUARTZ_TRIGGERS"
			});

		_updateTables(
			companyIds, "trigger_name",
			new String[] {
				"QUARTZ_BLOB_TRIGGERS", "QUARTZ_CRON_TRIGGERS",
				"QUARTZ_FIRED_TRIGGERS", "QUARTZ_SIMPLE_TRIGGERS",
				"QUARTZ_SIMPROP_TRIGGERS", "QUARTZ_TRIGGERS"
			});
	}

	private JobDataMap _deserializeJobDataMap(InputStream inputStream)
		throws Exception {

		try (ProtectedClassLoaderObjectInputStream
				protectedClassLoaderObjectInputStream =
					new ProtectedClassLoaderObjectInputStream(
						inputStream,
						QuartzUpgradeProcess.class.getClassLoader())) {

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

		Message message = (Message)_jsonFactory.deserialize(
			jobDataMap.getString(SchedulerEngine.MESSAGE));

		if (message.contains("companyId")) {
			companyIds.put(jobName, message.getLong("companyId"));

			return;
		}

		String destinationName = jobDataMap.getString(
			SchedulerEngine.DESTINATION_NAME);

		if (destinationName.equals("liferay/ct_collection_scheduled_publish")) {
			_getCompanyId(
				companyIds, jobName, "CTCollection", "ctCollectionId",
				message.getLong("ctCollectionId"));
		}
		else if (destinationName.equals("liferay/dispatch/executor")) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				(String)message.getPayload());

			long dispatchTriggerId = jsonObject.getLong("dispatchTriggerId");

			_getCompanyId(
				companyIds, jobName, "DispatchTrigger", "dispatchTriggerId",
				dispatchTriggerId);
		}
		else if (destinationName.equals("liferay/layouts_local_publisher") ||
				 destinationName.equals("liferay/layouts_remote_publisher")) {

			_getCompanyId(
				companyIds, jobName, "ExportImportConfiguration",
				"exportImportConfigurationId",
				GetterUtil.getLong(message.getPayload()));
		}
	}

	private void _updateTables(
			Map<String, Long> companyIds, String columnName,
			String[] tableNames)
		throws Exception {

		for (String tableName : tableNames) {
			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						StringBundler.concat(
							"update ", tableName, " set ", columnName,
							" = ? where ", columnName, " = ?"))) {

				for (Map.Entry<String, Long> entry : companyIds.entrySet()) {
					String newJobName = StringBundler.concat(
						entry.getKey(), StringPool.AT, entry.getValue());

					preparedStatement.setString(1, newJobName);

					preparedStatement.setString(2, entry.getKey());

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}
	}

	private final CompanyLocalService _companyLocalService;
	private final JSONFactory _jsonFactory;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.upgrade.v2_15_0;

import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * @author Kiana Suetani
 */
public class CTCollectionScheduledDateUpgradeProcess extends UpgradeProcess {

	public CTCollectionScheduledDateUpgradeProcess(
		SchedulerEngineHelper schedulerEngineHelper) {

		_schedulerEngineHelper = schedulerEngineHelper;
	}

	@Override
	protected void doUpgrade() throws Exception {
		List<SchedulerResponse> schedulerResponses =
			_schedulerEngineHelper.getScheduledJobs(
				CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
				StorageType.PERSISTED);

		if (schedulerResponses.isEmpty()) {
			return;
		}

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update CTCollection set scheduledDate = ? where " +
						"ctCollectionId = ? and status = ?")) {

			for (SchedulerResponse schedulerResponse : schedulerResponses) {
				Date date = _schedulerEngineHelper.getStartDate(
					schedulerResponse);

				if (date == null) {
					continue;
				}

				String jobName = schedulerResponse.getJobName();

				long ctCollectionId = GetterUtil.getLong(
					jobName.substring(0, jobName.indexOf(StringPool.AT)));

				preparedStatement.setTimestamp(
					1, new Timestamp(date.getTime()));
				preparedStatement.setLong(2, ctCollectionId);
				preparedStatement.setInt(3, WorkflowConstants.STATUS_SCHEDULED);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CTCollection", "scheduledDate DATE null")
		};
	}

	private final SchedulerEngineHelper _schedulerEngineHelper;

}
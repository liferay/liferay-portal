/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.util;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.exception.DispatchTriggerSchedulerException;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.ClusterableContextThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author Matija Petanjek
 */
public class DispatchTriggerSchedulerUtil {

	public static void addLocalSchedulerJob(
			DispatchTrigger dispatchTrigger,
			SchedulerEngineHelper schedulerEngineHelper, String timeZoneId,
			TriggerFactory triggerFactory)
		throws DispatchTriggerSchedulerException {

		ClusterableContextThreadLocal.putThreadLocalContext(
			SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, false);

		try {
			addSchedulerJob(
				dispatchTrigger, schedulerEngineHelper, StorageType.MEMORY,
				timeZoneId, triggerFactory);
		}
		finally {
			ClusterableContextThreadLocal.putThreadLocalContext(
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, true);
		}
	}

	public static void addSchedulerJob(
			DispatchTrigger dispatchTrigger,
			SchedulerEngineHelper schedulerEngineHelper,
			StorageType storageType, String timeZoneId,
			TriggerFactory triggerFactory)
		throws DispatchTriggerSchedulerException {

		Date date = new Date();

		Date endDate = dispatchTrigger.getEndDate();
		Date startDate = dispatchTrigger.getStartDate();

		if ((startDate != null) && startDate.before(date) &&
			((endDate == null) ||
			 (startDate.before(endDate) && endDate.after(date)))) {

			startDate = date;
		}

		Message message = new Message();

		message.put("companyId", dispatchTrigger.getCompanyId());

		message.setPayload(_getPayload(dispatchTrigger.getDispatchTriggerId()));

		Trigger trigger = triggerFactory.createTrigger(
			_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
			startDate, endDate, dispatchTrigger.getCronExpression(),
			TimeZone.getTimeZone(timeZoneId));

		try {
			schedulerEngineHelper.schedule(
				trigger, storageType, null,
				DispatchConstants.EXECUTOR_DESTINATION_NAME, message);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Scheduler entry created for dispatch trigger " +
						dispatchTrigger.getDispatchTriggerId());
			}
		}
		catch (SchedulerException schedulerException) {
			throw new DispatchTriggerSchedulerException(
				"Unable to create scheduler entry for dispatch trigger " +
					dispatchTrigger.getDispatchTriggerId(),
				schedulerException);
		}
	}

	public static void deleteLocalSchedulerJob(
		DispatchTrigger dispatchTrigger,
		SchedulerEngineHelper schedulerEngineHelper) {

		ClusterableContextThreadLocal.putThreadLocalContext(
			SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, false);

		try {
			schedulerEngineHelper.delete(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				StorageType.MEMORY);
		}
		catch (SchedulerException schedulerException) {
			_log.error(
				"Unable to delete scheduler entry for dispatch trigger " +
					dispatchTrigger.getDispatchTriggerId(),
				schedulerException);
		}
		finally {
			ClusterableContextThreadLocal.putThreadLocalContext(
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, true);
		}
	}

	public static void deleteSchedulerJob(
		DispatchTrigger dispatchTrigger,
		SchedulerEngineHelper schedulerEngineHelper, StorageType storageType) {

		try {
			String jobName = _getJobName(dispatchTrigger);
			String groupName = _getGroupName(dispatchTrigger);

			schedulerEngineHelper.delete(jobName, groupName, storageType);

			SchedulerResponse scheduledJob =
				schedulerEngineHelper.getScheduledJob(
					jobName, groupName, storageType);

			while (scheduledJob != null) {
				scheduledJob = schedulerEngineHelper.getScheduledJob(
					jobName, groupName, storageType);
			}
		}
		catch (SchedulerException schedulerException) {
			_log.error(
				"Unable to delete scheduler entry for dispatch trigger " +
					dispatchTrigger.getDispatchTriggerId(),
				schedulerException);
		}
	}

	public static Date getNextFireDate(
			DispatchTrigger dispatchTrigger,
			SchedulerEngineHelper schedulerEngineHelper,
			StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse =
			schedulerEngineHelper.getScheduledJob(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				storageType);

		if (schedulerResponse == null) {
			return null;
		}

		return schedulerEngineHelper.getNextFireDate(schedulerResponse);
	}

	public static Date getPreviousFireDate(
			DispatchTrigger dispatchTrigger,
			SchedulerEngineHelper schedulerEngineHelper,
			StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse =
			schedulerEngineHelper.getScheduledJob(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				storageType);

		if (schedulerResponse == null) {
			return null;
		}

		return schedulerEngineHelper.getPreviousFireDate(schedulerResponse);
	}

	public static boolean hasSchedulerJob(
			DispatchTrigger dispatchTrigger,
			SchedulerEngineHelper schedulerEngineHelper,
			StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse =
			schedulerEngineHelper.getScheduledJob(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				storageType);

		if (schedulerResponse == null) {
			return false;
		}

		return true;
	}

	private static String _getGroupName(DispatchTrigger dispatchTrigger) {
		return StringBundler.concat(
			"DISPATCH_GROUP_",
			String.format("%07d", dispatchTrigger.getDispatchTriggerId()),
			StringPool.AT, dispatchTrigger.getCompanyId());
	}

	private static String _getJobName(DispatchTrigger dispatchTrigger) {
		return StringBundler.concat(
			"DISPATCH_JOB_",
			String.format("%07d", dispatchTrigger.getDispatchTriggerId()),
			StringPool.AT, dispatchTrigger.getCompanyId());
	}

	private static String _getPayload(long dispatchTriggerId) {
		return StringBundler.concat(
			"{\"dispatchTriggerId\": ", dispatchTriggerId, "}");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchTriggerSchedulerUtil.class);

}
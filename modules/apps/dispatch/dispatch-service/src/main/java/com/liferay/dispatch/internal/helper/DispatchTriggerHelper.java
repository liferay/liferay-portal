/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.helper;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.exception.DispatchTriggerSchedulerException;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;

import java.util.Date;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(service = DispatchTriggerHelper.class)
public class DispatchTriggerHelper {

	public void addSchedulerJob(
			DispatchTrigger dispatchTrigger, StorageType storageType,
			String timeZoneId)
		throws DispatchTriggerSchedulerException {

		Date date = new Date();

		Date endDate = dispatchTrigger.getEndDate();
		Date startDate = dispatchTrigger.getStartDate();

		if ((startDate != null) && startDate.before(date) &&
			((endDate == null) ||
			 (startDate.before(endDate) && endDate.after(date)))) {

			startDate = date;
		}

		Trigger trigger = _triggerFactory.createTrigger(
			_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
			startDate, endDate, dispatchTrigger.getCronExpression(),
			TimeZone.getTimeZone(timeZoneId));

		Message message = new Message();

		message.put("companyId", dispatchTrigger.getCompanyId());

		message.setPayload(_getPayload(dispatchTrigger.getDispatchTriggerId()));

		try {
			_schedulerEngineHelper.schedule(
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

	public void deleteSchedulerJob(
		DispatchTrigger dispatchTrigger, StorageType storageType) {

		try {
			String jobName = _getJobName(dispatchTrigger);
			String groupName = _getGroupName(dispatchTrigger);

			_schedulerEngineHelper.delete(jobName, groupName, storageType);

			SchedulerResponse scheduledJob =
				_schedulerEngineHelper.getScheduledJob(
					jobName, groupName, storageType);

			while (scheduledJob != null) {
				scheduledJob = _schedulerEngineHelper.getScheduledJob(
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

	public Date getNextFireDate(
			DispatchTrigger dispatchTrigger, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				storageType);

		if (schedulerResponse == null) {
			return null;
		}

		return _schedulerEngineHelper.getNextFireDate(schedulerResponse);
	}

	public Date getPreviousFireDate(
			DispatchTrigger dispatchTrigger, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
				storageType);

		if (schedulerResponse == null) {
			return null;
		}

		return _schedulerEngineHelper.getPreviousFireDate(schedulerResponse);
	}

	private String _getGroupName(DispatchTrigger dispatchTrigger) {
		return StringBundler.concat(
			"DISPATCH_GROUP_",
			String.format("%07d", dispatchTrigger.getDispatchTriggerId()),
			StringPool.AT, dispatchTrigger.getCompanyId());
	}

	private String _getJobName(DispatchTrigger dispatchTrigger) {
		return StringBundler.concat(
			"DISPATCH_JOB_",
			String.format("%07d", dispatchTrigger.getDispatchTriggerId()),
			StringPool.AT, dispatchTrigger.getCompanyId());
	}

	private String _getPayload(long dispatchTriggerId) {
		return StringBundler.concat(
			"{\"dispatchTriggerId\": ", dispatchTriggerId, "}");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchTriggerHelper.class);

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private TriggerFactory _triggerFactory;

}
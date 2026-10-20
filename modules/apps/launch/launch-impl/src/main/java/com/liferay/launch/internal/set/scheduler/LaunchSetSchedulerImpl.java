/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.scheduler;

import com.liferay.launch.internal.constants.LaunchDestinationNames;
import com.liferay.launch.internal.set.scheduler.util.LaunchSetSchedulerUtil;
import com.liferay.launch.set.scheduler.LaunchSetScheduler;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(service = LaunchSetScheduler.class)
public class LaunchSetSchedulerImpl implements LaunchSetScheduler {

	@Override
	public Date fetchScheduledDate(long launchSetId) throws PortalException {
		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(launchSetId);

		if (launchSetObjectEntry == null) {
			return null;
		}

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				LaunchSetSchedulerUtil.getJobName(launchSetObjectEntry),
				LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH,
				StorageType.PERSISTED);

		if (schedulerResponse == null) {
			return null;
		}

		return _schedulerEngineHelper.getStartDate(schedulerResponse);
	}

	@Override
	public void schedulePublish(long launchSetId, long userId, Date date)
		throws PortalException {

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(launchSetId);

		_objectEntryService.checkModelResourcePermission(
			launchSetObjectEntry, ActionKeys.UPDATE);

		if (launchSetObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_DRAFT) {

			throw new PortalException(
				"Launch set " + launchSetId + " must be open to be scheduled");
		}

		if ((date == null) || !date.after(new Date())) {
			throw new PortalException(
				"Launch set " + launchSetId +
					" must be scheduled for a future date");
		}

		ServiceContext serviceContext = _createServiceContext(
			launchSetObjectEntry, userId);

		_updateScheduledDate(
			launchSetObjectEntry, userId, date, serviceContext);

		_objectEntryLocalService.updateStatus(
			userId, launchSetId, WorkflowConstants.STATUS_SCHEDULED,
			serviceContext);

		Message message = new Message();

		message.put("companyId", launchSetObjectEntry.getCompanyId());
		message.put("launchSetId", launchSetId);
		message.put("userId", userId);

		_schedulerEngineHelper.schedule(
			_triggerFactory.createTrigger(
				LaunchSetSchedulerUtil.getJobName(launchSetObjectEntry),
				LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH, date, null,
				0, null),
			StorageType.PERSISTED, String.valueOf(launchSetId),
			LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH, message);
	}

	@Override
	public void unschedulePublish(long launchSetId) throws PortalException {
		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(launchSetId);

		if ((launchSetObjectEntry == null) ||
			(launchSetObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_SCHEDULED)) {

			return;
		}

		_objectEntryService.checkModelResourcePermission(
			launchSetObjectEntry, ActionKeys.UPDATE);

		long userId = PrincipalThreadLocal.getUserId();

		ServiceContext serviceContext = _createServiceContext(
			launchSetObjectEntry, userId);

		_objectEntryLocalService.updateStatus(
			userId, launchSetId, WorkflowConstants.STATUS_DRAFT,
			serviceContext);

		_updateScheduledDate(
			launchSetObjectEntry, userId, null, serviceContext);

		_schedulerEngineHelper.delete(
			LaunchSetSchedulerUtil.getJobName(launchSetObjectEntry),
			LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH,
			StorageType.PERSISTED);
	}

	private ServiceContext _createServiceContext(
		ObjectEntry launchSetObjectEntry, long userId) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(launchSetObjectEntry.getCompanyId());
		serviceContext.setUserId(userId);
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return serviceContext;
	}

	private void _updateScheduledDate(
			ObjectEntry launchSetObjectEntry, long userId, Date date,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> values = launchSetObjectEntry.getValues();

		values.put("scheduledDate", date);

		_objectEntryLocalService.updateObjectEntry(
			userId, launchSetObjectEntry.getObjectEntryId(),
			launchSetObjectEntry.getObjectEntryFolderId(), values,
			serviceContext);
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private TriggerFactory _triggerFactory;

}
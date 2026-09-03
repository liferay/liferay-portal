/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.impl;

import com.liferay.dispatch.exception.DispatchTriggerDispatchTaskExecutorTypeException;
import com.liferay.dispatch.exception.DispatchTriggerEndDateException;
import com.liferay.dispatch.exception.DispatchTriggerNameException;
import com.liferay.dispatch.exception.DispatchTriggerStartDateException;
import com.liferay.dispatch.exception.DuplicateDispatchTriggerException;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.internal.helper.DispatchTriggerHelper;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.base.DispatchTriggerLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalInstances;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@Component(
	property = "model.class.name=com.liferay.dispatch.model.DispatchTrigger",
	service = AopService.class
)
public class DispatchTriggerLocalServiceImpl
	extends DispatchTriggerLocalServiceBaseImpl {

	@Override
	public DispatchTrigger addDispatchTrigger(
			String externalReferenceCode, long userId,
			DispatchTaskExecutor dispatchTaskExecutor,
			String dispatchTaskExecutorType,
			UnicodeProperties dispatchTaskSettingsUnicodeProperties,
			String name, boolean system)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(
			0, user.getCompanyId(), dispatchTaskExecutor,
			dispatchTaskExecutorType, name);

		DispatchTrigger dispatchTrigger = dispatchTriggerPersistence.create(
			counterLocalService.increment());

		dispatchTrigger.setExternalReferenceCode(externalReferenceCode);
		dispatchTrigger.setCompanyId(user.getCompanyId());
		dispatchTrigger.setUserId(user.getUserId());
		dispatchTrigger.setUserName(user.getFullName());
		dispatchTrigger.setDispatchTaskExecutorType(dispatchTaskExecutorType);
		dispatchTrigger.setDispatchTaskSettingsUnicodeProperties(
			dispatchTaskSettingsUnicodeProperties);
		dispatchTrigger.setName(name);
		dispatchTrigger.setSystem(system);

		dispatchTrigger = dispatchTriggerPersistence.update(dispatchTrigger);

		_resourceLocalService.addResources(
			user.getCompanyId(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			user.getUserId(), DispatchTrigger.class.getName(),
			dispatchTrigger.getDispatchTriggerId(), false, true, true);

		return dispatchTrigger;
	}

	@Override
	public DispatchTrigger addDispatchTrigger(
			String externalReferenceCode, long userId,
			String dispatchTaskExecutorType,
			UnicodeProperties dispatchTaskSettingsUnicodeProperties,
			String name, boolean system)
		throws PortalException {

		return addDispatchTrigger(
			externalReferenceCode, userId, null, dispatchTaskExecutorType,
			dispatchTaskSettingsUnicodeProperties, name, system);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public DispatchTrigger deleteDispatchTrigger(
			DispatchTrigger dispatchTrigger)
		throws PortalException {

		if (dispatchTrigger.isSystem() &&
			!PortalInstances.isCurrentCompanyInDeletionProcess() &&
			!PortalRunMode.isTestMode()) {

			return dispatchTrigger;
		}

		_dispatchLogLocalService.deleteDispatchLogs(
			dispatchTrigger.getDispatchTriggerId());

		dispatchTriggerPersistence.remove(dispatchTrigger);

		_resourceLocalService.deleteResource(
			dispatchTrigger, ResourceConstants.SCOPE_INDIVIDUAL);

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode());

		_dispatchTriggerHelper.deleteSchedulerJob(
			dispatchTrigger, dispatchTaskClusterMode.getStorageType());

		return dispatchTrigger;
	}

	@Override
	public DispatchTrigger deleteDispatchTrigger(long dispatchTriggerId)
		throws PortalException {

		return deleteDispatchTrigger(
			dispatchTriggerPersistence.findByPrimaryKey(dispatchTriggerId));
	}

	@Override
	public DispatchTrigger fetchDispatchTrigger(long companyId, String name) {
		return dispatchTriggerPersistence.fetchByC_N(companyId, name);
	}

	@Override
	public Date fetchNextFireDate(long dispatchTriggerId) {
		try {
			return getNextFireDate(dispatchTriggerId);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to resolve next fire date for dispatch trigger ID " +
					dispatchTriggerId,
				portalException);
		}

		return null;
	}

	@Override
	public Date fetchPreviousFireDate(long dispatchTriggerId) {
		DispatchTrigger dispatchTrigger =
			dispatchTriggerPersistence.fetchByPrimaryKey(dispatchTriggerId);

		if (dispatchTrigger == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to fetch dispatch trigger ID " + dispatchTriggerId);
			}

			return null;
		}

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode());

		try {
			return _dispatchTriggerHelper.getPreviousFireDate(
				dispatchTrigger, dispatchTaskClusterMode.getStorageType());
		}
		catch (SchedulerException schedulerException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to fetch previous fire date for dispatch ",
						"trigger ID ", dispatchTriggerId),
					schedulerException);
			}
		}

		return null;
	}

	@Override
	public List<DispatchTrigger> getActiveDispatchTriggers(
		List<DispatchTaskClusterMode> dispatchTaskClusterModes) {

		return dispatchTriggerPersistence.findByA_DTCM(
			true,
			TransformUtil.transformToIntArray(
				dispatchTaskClusterModes, DispatchTaskClusterMode::getMode));
	}

	@Override
	public DispatchTrigger getDispatchTrigger(long dispatchTriggerId)
		throws PortalException {

		return dispatchTriggerPersistence.findByPrimaryKey(dispatchTriggerId);
	}

	@Override
	public List<DispatchTrigger> getDispatchTriggers(boolean active) {
		return dispatchTriggerPersistence.findByActive(active);
	}

	@Override
	public List<DispatchTrigger> getDispatchTriggers(
		boolean active, DispatchTaskClusterMode dispatchTaskClusterMode) {

		return dispatchTriggerPersistence.findByA_DTCM(
			active, dispatchTaskClusterMode.getMode());
	}

	@Override
	public List<DispatchTrigger> getDispatchTriggers(
		long companyId, int start, int end) {

		return dispatchTriggerPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public int getDispatchTriggersCount(long companyId) {
		return dispatchTriggerPersistence.countByCompanyId(companyId);
	}

	@Override
	public Date getNextFireDate(long dispatchTriggerId) throws PortalException {
		DispatchTrigger dispatchTrigger =
			dispatchTriggerPersistence.findByPrimaryKey(dispatchTriggerId);

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode());

		return _dispatchTriggerHelper.getNextFireDate(
			dispatchTrigger, dispatchTaskClusterMode.getStorageType());
	}

	@Override
	public Date getPreviousFireDate(long dispatchTriggerId)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			dispatchTriggerPersistence.findByPrimaryKey(dispatchTriggerId);

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode());

		return _dispatchTriggerHelper.getPreviousFireDate(
			dispatchTrigger, dispatchTaskClusterMode.getStorageType());
	}

	@Override
	public List<DispatchTrigger> getUserDispatchTriggers(
		long companyId, long userId, int start, int end) {

		return dispatchTriggerPersistence.findByC_U(
			companyId, userId, start, end);
	}

	@Override
	public int getUserDispatchTriggersCount(long companyId, long userId) {
		return dispatchTriggerPersistence.countByC_U(companyId, userId);
	}

	@Override
	public DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId, boolean active, String cronExpression,
			DispatchTaskClusterMode dispatchTaskClusterMode, int endDateMonth,
			int endDateDay, int endDateYear, int endDateHour, int endDateMinute,
			boolean neverEnd, boolean overlapAllowed, int startDateMonth,
			int startDateDay, int startDateYear, int startDateHour,
			int startDateMinute, String timeZoneId)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			dispatchTriggerPersistence.fetchByPrimaryKey(dispatchTriggerId);

		if ((dispatchTaskClusterMode == DispatchTaskClusterMode.ALL_NODES) &&
			_dispatchTaskExecutorRegistry.isClusterModeSingle(
				dispatchTrigger.getDispatchTaskExecutorType())) {

			dispatchTaskClusterMode =
				DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED;
		}

		dispatchTrigger.setActive(active);
		dispatchTrigger.setCronExpression(cronExpression);

		if (neverEnd) {
			dispatchTrigger.setEndDate(null);
		}
		else {
			dispatchTrigger.setEndDate(
				_getUTCDate(
					_portal.getDate(
						endDateMonth, endDateDay, endDateYear, endDateHour,
						endDateMinute, DispatchTriggerEndDateException.class),
					timeZoneId));
		}

		DispatchTaskClusterMode oldDispatchTaskClusterMode =
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode());

		dispatchTrigger.setDispatchTaskClusterMode(
			dispatchTaskClusterMode.getMode());
		dispatchTrigger.setOverlapAllowed(overlapAllowed);
		dispatchTrigger.setStartDate(
			_getUTCDate(
				_portal.getDate(
					startDateMonth, startDateDay, startDateYear, startDateHour,
					startDateMinute, DispatchTriggerStartDateException.class),
				timeZoneId));
		dispatchTrigger.setTimeZoneId(timeZoneId);

		dispatchTrigger = dispatchTriggerPersistence.update(dispatchTrigger);

		_dispatchTriggerHelper.deleteSchedulerJob(
			dispatchTrigger, oldDispatchTaskClusterMode.getStorageType());

		if (active) {
			_dispatchTriggerHelper.addSchedulerJob(
				dispatchTrigger, dispatchTaskClusterMode.getStorageType(),
				timeZoneId);
		}

		return dispatchTrigger;
	}

	@Override
	public DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId,
			UnicodeProperties taskSettingsUnicodeProperties, String name)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			dispatchTriggerPersistence.findByPrimaryKey(dispatchTriggerId);

		_validate(
			dispatchTriggerId, dispatchTrigger.getCompanyId(), null,
			dispatchTrigger.getDispatchTaskExecutorType(), name);

		dispatchTrigger.setDispatchTaskSettingsUnicodeProperties(
			taskSettingsUnicodeProperties);
		dispatchTrigger.setName(name);

		return dispatchTriggerPersistence.update(dispatchTrigger);
	}

	private Date _getUTCDate(Date date, String timeZoneId) {
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

		return new Date(date.getTime() - timeZone.getOffset(date.getTime()));
	}

	private void _validate(
			long dispatchTriggerId, long companyId,
			DispatchTaskExecutor dispatchTaskExecutor,
			String dispatchTaskExecutorType, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new DispatchTriggerNameException(
				"Dispatch trigger name is null for company " + companyId);
		}

		DispatchTrigger dispatchTrigger = dispatchTriggerPersistence.fetchByC_N(
			companyId, name);

		if ((dispatchTrigger != null) &&
			(dispatchTrigger.getDispatchTriggerId() != dispatchTriggerId)) {

			throw new DuplicateDispatchTriggerException(
				StringBundler.concat(
					"Dispatch trigger name \"", name,
					"\" already exists for company ", companyId));
		}

		if (dispatchTaskExecutor == null) {
			dispatchTaskExecutor =
				_dispatchTaskExecutorRegistry.fetchDispatchTaskExecutor(
					dispatchTaskExecutorType);
		}

		if (dispatchTaskExecutor == null) {
			throw new DispatchTriggerDispatchTaskExecutorTypeException(
				StringBundler.concat(
					"Unknown dispatch task executor type \"",
					dispatchTaskExecutorType, "\""));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchTriggerLocalServiceImpl.class);

	@Reference
	private DispatchLogLocalService _dispatchLogLocalService;

	@Reference
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@Reference
	private DispatchTriggerHelper _dispatchTriggerHelper;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
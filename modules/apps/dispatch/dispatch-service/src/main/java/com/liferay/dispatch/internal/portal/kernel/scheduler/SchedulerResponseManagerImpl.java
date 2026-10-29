/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.portal.kernel.scheduler;

import com.liferay.dispatch.scheduler.SchedulerResponseManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(service = SchedulerResponseManager.class)
public class SchedulerResponseManagerImpl implements SchedulerResponseManager {

	@Override
	public Date getNextFireDate(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				jobName, groupName, storageType);

		if (schedulerResponse == null) {
			return null;
		}

		return _schedulerEngineHelper.getNextFireDate(schedulerResponse);
	}

	@Override
	public List<SchedulerResponse> getSchedulerResponses(
		PermissionChecker permissionChecker, int start, int end) {

		List<SchedulerResponse> schedulerResponses = _getSchedulerResponses(
			permissionChecker);

		Collections.sort(
			schedulerResponses,
			Comparator.comparing(
				schedulerResponse -> getSimpleJobName(
					schedulerResponse.getJobName())));

		return schedulerResponses.subList(
			start, Math.min(end, schedulerResponses.size()));
	}

	@Override
	public int getSchedulerResponsesCount(PermissionChecker permissionChecker) {
		List<SchedulerResponse> schedulerResponses = _getSchedulerResponses(
			permissionChecker);

		return schedulerResponses.size();
	}

	@Override
	public String getSimpleJobName(String jobName) {
		return jobName.substring(jobName.lastIndexOf(StringPool.PERIOD) + 1);
	}

	@Override
	public TriggerState getTriggerState(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				jobName, groupName, storageType);

		if (schedulerResponse == null) {
			return null;
		}

		return _schedulerEngineHelper.getJobState(schedulerResponse);
	}

	@Override
	public void pause(
			PermissionChecker permissionChecker, String jobName,
			String groupName, StorageType storageType)
		throws PortalException {

		_checkPermission(permissionChecker, jobName, groupName, storageType);

		_schedulerEngineHelper.pause(jobName, groupName, storageType);
	}

	@Override
	public void resume(
			PermissionChecker permissionChecker, String jobName,
			String groupName, StorageType storageType)
		throws PortalException {

		_checkPermission(permissionChecker, jobName, groupName, storageType);

		_schedulerEngineHelper.resume(jobName, groupName, storageType);
	}

	@Override
	public void run(
			PermissionChecker permissionChecker, String jobName,
			String groupName, StorageType storageType)
		throws PortalException {

		_checkPermission(permissionChecker, jobName, groupName, storageType);

		_schedulerEngineHelper.run(
			permissionChecker.getCompanyId(), jobName, groupName, storageType);
	}

	private void _checkPermission(
			PermissionChecker permissionChecker, String jobName,
			String groupName, StorageType storageType)
		throws PortalException {

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				jobName, groupName, storageType);

		if (schedulerResponse == null) {
			throw new SchedulerException(
				StringBundler.concat(
					"Unable to find scheduled job {groupName=", groupName,
					", jobName=", jobName, "}"));
		}

		if (!_hasPermission(permissionChecker, schedulerResponse)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, ActionKeys.UPDATE);
		}
	}

	private List<SchedulerResponse> _getSchedulerResponses(
		PermissionChecker permissionChecker) {

		List<SchedulerResponse> schedulerResponses = null;

		try {
			schedulerResponses = _schedulerEngineHelper.getScheduledJobs();
		}
		catch (SchedulerException schedulerException) {
			_log.error("Unable to get scheduler responses", schedulerException);

			return Collections.emptyList();
		}

		return ListUtil.filter(
			schedulerResponses,
			schedulerResponse -> _hasPermission(
				permissionChecker, schedulerResponse));
	}

	private boolean _hasPermission(
		PermissionChecker permissionChecker,
		SchedulerResponse schedulerResponse) {

		String jobName = schedulerResponse.getJobName();

		if (jobName.startsWith("DISPATCH_JOB_")) {
			return false;
		}

		Message message = schedulerResponse.getMessage();

		long companyId = message.getLong("companyId");

		if (companyId == CompanyConstants.SYSTEM) {
			return permissionChecker.isOmniadmin();
		}

		if (companyId == permissionChecker.getCompanyId()) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SchedulerResponseManagerImpl.class);

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

}
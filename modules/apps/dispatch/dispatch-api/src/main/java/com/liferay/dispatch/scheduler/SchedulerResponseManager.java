/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.scheduler;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import java.util.Date;
import java.util.List;

/**
 * @author Matija Petanjek
 */
public interface SchedulerResponseManager {

	public Date getNextFireDate(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException;

	public List<SchedulerResponse> getSchedulerResponses(
		PermissionChecker permissionChecker, int start, int end);

	public int getSchedulerResponsesCount(PermissionChecker permissionChecker);

	public String getSimpleJobName(String jobName);

	public TriggerState getTriggerState(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException;

	public void pause(
			PermissionChecker permissionChecker, String jobName,
			String groupName, StorageType storageType)
		throws PortalException;

	public void resume(
			PermissionChecker permissionChecker, String jobName,
			String groupName, StorageType storageType)
		throws PortalException;

	public void run(
			PermissionChecker permissionChecker, String jobName,
			String groupName, StorageType storageType)
		throws PortalException;

}
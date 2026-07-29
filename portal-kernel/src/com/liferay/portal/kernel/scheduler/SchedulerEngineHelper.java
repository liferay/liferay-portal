/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.scheduler;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;

import java.util.Date;
import java.util.List;

/**
 * @author Michael C. Han
 */
public interface SchedulerEngineHelper {

	public void addScriptingJob(
			Trigger trigger, StorageType storageType, String description,
			String language, String script)
		throws SchedulerException;

	public void delete(String groupName, StorageType storageType)
		throws SchedulerException;

	public void delete(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException;

	public Date getEndDate(SchedulerResponse schedulerResponse);

	public TriggerState getJobState(SchedulerResponse schedulerResponse);

	public Date getNextFireDate(SchedulerResponse schedulerResponse);

	public Date getPreviousFireDate(SchedulerResponse schedulerResponse);

	public SchedulerResponse getScheduledJob(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException;

	public List<SchedulerResponse> getScheduledJobs() throws SchedulerException;

	public List<SchedulerResponse> getScheduledJobs(StorageType storageType)
		throws SchedulerException;

	public List<SchedulerResponse> getScheduledJobs(
			String groupName, StorageType storageType)
		throws SchedulerException;

	public Date getStartDate(SchedulerResponse schedulerResponse);

	public void pause(String jobName, String groupName, StorageType storageType)
		throws SchedulerException;

	public void resume(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException;

	public void run(
			long companyId, String jobName, String groupName,
			StorageType storageType)
		throws SchedulerException;

	public void schedule(
			Trigger trigger, StorageType storageType, String description,
			String destinationName, Message message)
		throws SchedulerException;

	public void schedule(
			Trigger trigger, StorageType storageType, String description,
			String destinationName, Object payload)
		throws SchedulerException;

}
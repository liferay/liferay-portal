/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.scheduler;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;

import java.util.Date;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class SchedulerEngineHelperUtil {

	public static void addScriptingJob(
			Trigger trigger, StorageType storageType, String description,
			String language, String script)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.addScriptingJob(
			trigger, storageType, description, language, script);
	}

	public static void delete(String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.delete(groupName, storageType);
	}

	public static void delete(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.delete(jobName, groupName, storageType);
	}

	public static Date getEndDate(SchedulerResponse schedulerResponse) {
		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		return schedulerEngineHelper.getEndDate(schedulerResponse);
	}

	public static SchedulerResponse getScheduledJob(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		return schedulerEngineHelper.getScheduledJob(
			jobName, groupName, storageType);
	}

	public static List<SchedulerResponse> getScheduledJobs()
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		return schedulerEngineHelper.getScheduledJobs();
	}

	public static List<SchedulerResponse> getScheduledJobs(
			StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		return schedulerEngineHelper.getScheduledJobs(storageType);
	}

	public static List<SchedulerResponse> getScheduledJobs(
			String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		return schedulerEngineHelper.getScheduledJobs(groupName, storageType);
	}

	public static Date getStartDate(SchedulerResponse schedulerResponse) {
		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		return schedulerEngineHelper.getStartDate(schedulerResponse);
	}

	public static void pause(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.pause(jobName, groupName, storageType);
	}

	public static void resume(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.resume(jobName, groupName, storageType);
	}

	public static void run(
			long companyId, String jobName, String groupName,
			StorageType storageType)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.run(companyId, jobName, groupName, storageType);
	}

	public static void schedule(
			Trigger trigger, StorageType storageType, String description,
			String destinationName, Message message)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.schedule(
			trigger, storageType, description, destinationName, message);
	}

	public static void schedule(
			Trigger trigger, StorageType storageType, String description,
			String destinationName, Object payload)
		throws SchedulerException {

		SchedulerEngineHelper schedulerEngineHelper =
			_schedulerEngineHelperSnapshot.get();

		schedulerEngineHelper.schedule(
			trigger, storageType, description, destinationName, payload);
	}

	private static final Snapshot<SchedulerEngineHelper>
		_schedulerEngineHelperSnapshot = new Snapshot<>(
			SchedulerEngineHelperUtil.class, SchedulerEngineHelper.class);

}
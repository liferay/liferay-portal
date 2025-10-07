/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.scheduler;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.trash.service.TrashEntryLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides a scheduled task to empty the Recycly Bin when the maximum Recycle
 * Bin entry age has been exceeded. The maximum Recycle Bin entry age is defined
 * by the <code>trash.entries.max.age</code> property (in minutes). The
 * scheduled task uses the <code>trash.entry.check.interval</code> property to
 * define the execution interval (in minutes).
 *
 * @author Eudaldo Alonso
 */
@Component(enabled = false, service = SchedulerJobConfiguration.class)
public class CheckEntrySchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return _trashEntryLocalService::checkEntries;
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			PropsValues.TRASH_ENTRY_CHECK_INTERVAL, TimeUnit.MINUTE);
	}

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

}
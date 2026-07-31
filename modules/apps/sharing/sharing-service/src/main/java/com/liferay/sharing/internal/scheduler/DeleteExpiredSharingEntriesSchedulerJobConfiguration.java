/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.internal.scheduler;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.sharing.internal.configuration.SharingSystemConfiguration;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.sharing.internal.configuration.SharingSystemConfiguration",
	service = SchedulerJobConfiguration.class
)
public class DeleteExpiredSharingEntriesSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return _sharingEntryLocalService::deleteExpiredEntries;
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		SharingSystemConfiguration sharingSystemConfiguration =
			ConfigurableUtil.createConfigurable(
				SharingSystemConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			sharingSystemConfiguration.expiredSharingEntriesCheckInterval(),
			TimeUnit.MINUTE);
	}

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	private volatile TriggerConfiguration _triggerConfiguration;

}
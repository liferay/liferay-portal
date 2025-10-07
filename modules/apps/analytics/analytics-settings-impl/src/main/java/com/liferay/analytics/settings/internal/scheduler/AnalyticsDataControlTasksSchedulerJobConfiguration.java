/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.scheduler;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.analytics.settings.data.control.tasks.UsersDataControlTasks;
import com.liferay.analytics.settings.internal.client.AnalyticsCloudClient;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = SchedulerJobConfiguration.class)
public class AnalyticsDataControlTasksSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			Map<Long, AnalyticsConfiguration> analyticsConfigurations =
				_analyticsConfigurationRegistry.getAnalyticsConfigurations();

			if (analyticsConfigurations.isEmpty()) {
				return;
			}

			for (Map.Entry<Long, AnalyticsConfiguration> entry :
					analyticsConfigurations.entrySet()) {

				Set<String> emailAddresses =
					_usersDataControlTasks.getEmailAddresses(entry.getKey());

				if (emailAddresses.isEmpty()) {
					continue;
				}

				_usersDataControlTasks.clean(entry.getKey());

				_analyticsCloudClient.createDataControlTasks(
					entry.getValue(), emailAddresses,
					SetUtil.fromArray(new String[] {"DELETE", "SUPPRESS"}));
			}
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			1, TimeUnit.MINUTE);
	}

	@Activate
	protected void activate() {
		_analyticsCloudClient = new AnalyticsCloudClient(_http);
	}

	private AnalyticsCloudClient _analyticsCloudClient;

	@Reference
	private AnalyticsConfigurationRegistry _analyticsConfigurationRegistry;

	@Reference
	private Http _http;

	@Reference
	private UsersDataControlTasks _usersDataControlTasks;

}
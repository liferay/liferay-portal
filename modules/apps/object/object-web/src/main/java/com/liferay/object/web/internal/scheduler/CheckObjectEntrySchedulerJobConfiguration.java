/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.scheduler;

import com.liferay.object.configuration.ObjectEntryScheduleConfiguration;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jhosseph Gonzalez
 */
@Component(
	configurationPid = "com.liferay.object.configuration.ObjectEntryScheduleConfiguration",
	service = SchedulerJobConfiguration.class
)
public class CheckObjectEntrySchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeConsumer<Long, Exception>
		getCompanyJobExecutorUnsafeConsumer() {

		return companyId -> _objectEntryLocalService.checkObjectEntries(
			companyId);
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _objectEntryLocalService.checkObjectEntries(
				companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_objectEntryScheduleConfiguration = ConfigurableUtil.createConfigurable(
			ObjectEntryScheduleConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			_objectEntryScheduleConfiguration.checkInterval(), TimeUnit.MINUTE);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	private volatile ObjectEntryScheduleConfiguration
		_objectEntryScheduleConfiguration;
	private TriggerConfiguration _triggerConfiguration;

}
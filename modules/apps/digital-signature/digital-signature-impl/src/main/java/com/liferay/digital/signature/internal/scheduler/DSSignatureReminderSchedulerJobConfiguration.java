/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.scheduler;

import com.liferay.digital.signature.request.DSRequestManager;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Kim
 */
@Component(
	configurationPid = "com.liferay.digital.signature.configuration.DigitalSignatureConfiguration",
	service = SchedulerJobConfiguration.class
)
public class DSSignatureReminderSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _dsRequestManager.sendSignatureReminders(companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			GetterUtil.getInteger(
				properties.get("signatureReminderCheckInterval"), 3),
			TimeUnit.DAY);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DSRequestManager _dsRequestManager;

	private TriggerConfiguration _triggerConfiguration;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.scheduler;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zsolt Balogh
 */
@Component(
	configurationPid = "com.liferay.osb.patcher.configuration.PatcherConfiguration",
	service = SchedulerJobConfiguration.class
)
public class PatcherBuildSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> {
				PatcherUtil.processOSBPatcherMessageQueue(companyId);

				PatcherUtil.processOSBPatcherStatusFiles(
					companyId,
					_patcherConfiguration.patcherStatusBuildJenkinsPath());

				PatcherUtil.processOSBPatcherStatusFiles(
					companyId,
					_patcherConfiguration.patcherStatusBuildJenkinsTestPath());

				PatcherUtil.processOSBPatcherStatusFiles(
					companyId, _patcherConfiguration.patcherStatusBuildPath());
			});
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			"*/4 * * * * ? *");
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_patcherConfiguration = ConfigurableUtil.createConfigurable(
			PatcherConfiguration.class, properties);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	private PatcherConfiguration _patcherConfiguration;

}
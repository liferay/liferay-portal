/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.scheduler;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.k8s.agent.custodian.VirtualInstanceCustodian;
import com.liferay.portal.k8s.agent.internal.util.CompanyConfigMapUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(service = SchedulerJobConfiguration.class)
public class VirtualHostSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			List<Company> companies = _companyLocalService.getCompanies();

			VirtualInstanceCustodian virtualInstanceCustodian =
				_virtualInstanceCustodianSnapshot.get();

			virtualInstanceCustodian.clean(
				ListUtil.toList(companies, Company::getWebId));

			companies.forEach(
				company -> CompanyConfigMapUtil.modifyConfigMap(
					company, _portalK8sConfigMapModifierSnapshot.get(),
					_virtualHostLocalService));
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			2, TimeUnit.MINUTE);
	}

	private static final Snapshot<PortalK8sConfigMapModifier>
		_portalK8sConfigMapModifierSnapshot = new Snapshot<>(
			VirtualHostSchedulerJobConfiguration.class,
			PortalK8sConfigMapModifier.class, null, true);
	private static final Snapshot<VirtualInstanceCustodian>
		_virtualInstanceCustodianSnapshot = new Snapshot<>(
			VirtualHostSchedulerJobConfiguration.class,
			VirtualInstanceCustodian.class, null, true);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private VirtualHostLocalService _virtualHostLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.multiple.internal;

import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(service = {})
public class ClusterSchedulerEngineConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		SchedulerEngine schedulerEngine = _schedulerEngine;

		if (_clusterLink.isEnabled()) {
			ClusterSchedulerEngine clusterSchedulerEngine =
				new ClusterSchedulerEngine(schedulerEngine, _triggerFactory);

			clusterSchedulerEngine.setClusterExecutor(_clusterExecutor);
			clusterSchedulerEngine.setClusterMasterExecutor(
				_clusterMasterExecutor);

			_serviceRegistration = bundleContext.registerService(
				IdentifiableOSGiService.class, clusterSchedulerEngine,
				new HashMapDictionary<String, Object>());

			schedulerEngine = ClusterableProxyFactory.createClusterableProxy(
				clusterSchedulerEngine);
		}

		_schedulerEngineServiceRegistration = bundleContext.registerService(
			SchedulerEngine.class, schedulerEngine,
			HashMapDictionaryBuilder.<String, Object>put(
				"scheduler.engine.proxy", Boolean.TRUE
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		if (_schedulerEngineServiceRegistration != null) {
			_schedulerEngineServiceRegistration.unregister();
		}
	}

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private ClusterLink _clusterLink;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference(target = "(scheduler.engine.proxy=false)")
	private SchedulerEngine _schedulerEngine;

	private volatile ServiceRegistration<SchedulerEngine>
		_schedulerEngineServiceRegistration;
	private ServiceRegistration<IdentifiableOSGiService> _serviceRegistration;

	@Reference
	private TriggerFactory _triggerFactory;

}
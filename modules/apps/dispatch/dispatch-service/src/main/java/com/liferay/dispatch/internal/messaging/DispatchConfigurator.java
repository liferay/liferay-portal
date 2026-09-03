/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.messaging;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.exception.DispatchTriggerSchedulerException;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.internal.helper.DispatchTriggerHelper;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.cluster.BaseClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(service = {})
public class DispatchConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (_clusterMasterExecutor.isEnabled()) {
			_dispatchClusterMasterTokenTransitionListener =
				new DispatchClusterMasterTokenTransitionListener();

			_clusterMasterExecutor.addClusterMasterTokenTransitionListener(
				_dispatchClusterMasterTokenTransitionListener);
		}

		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
				DispatchConstants.EXECUTOR_DESTINATION_NAME);

		destinationConfiguration.setMaximumQueueSize(_MAXIMUM_QUEUE_SIZE);
		destinationConfiguration.setRejectedExecutionHandler(
			new ThreadPoolExecutor.CallerRunsPolicy() {

				@Override
				public void rejectedExecution(
					Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"The current thread will handle the request " +
								"because the graph walker's task queue is at " +
									"its maximum capacity");
					}

					super.rejectedExecution(runnable, threadPoolExecutor);
				}

			});

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		_serviceRegistration = bundleContext.registerService(
			Destination.class, destination,
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", destination.getName()
			).build());

		_addScheduledJobs(_getDispatchTaskClusterModes());
	}

	@Deactivate
	protected void deactivate() {
		_deleteScheduledJobs(_getDispatchTaskClusterModes());

		_serviceRegistration.unregister();

		if (_clusterMasterExecutor.isEnabled()) {
			_clusterMasterExecutor.removeClusterMasterTokenTransitionListener(
				_dispatchClusterMasterTokenTransitionListener);
		}
	}

	private void _addScheduledJobs(
		List<DispatchTaskClusterMode> dispatchTaskClusterModes) {

		for (DispatchTrigger dispatchTrigger :
				_dispatchTriggerLocalService.getActiveDispatchTriggers(
					dispatchTaskClusterModes)) {

			DispatchTaskClusterMode dispatchTaskClusterMode =
				DispatchTaskClusterMode.valueOf(
					dispatchTrigger.getDispatchTaskClusterMode());

			try {
				_dispatchTriggerHelper.addSchedulerJob(
					dispatchTrigger, dispatchTaskClusterMode.getStorageType(),
					dispatchTrigger.getTimeZoneId());
			}
			catch (DispatchTriggerSchedulerException
						dispatchTriggerSchedulerException) {

				_log.error(dispatchTriggerSchedulerException);
			}
		}
	}

	private void _deleteScheduledJobs(
		List<DispatchTaskClusterMode> dispatchTaskClusterModes) {

		for (DispatchTrigger dispatchTrigger :
				_dispatchTriggerLocalService.getActiveDispatchTriggers(
					dispatchTaskClusterModes)) {

			DispatchTaskClusterMode dispatchTaskClusterMode =
				DispatchTaskClusterMode.valueOf(
					dispatchTrigger.getDispatchTaskClusterMode());

			_dispatchTriggerHelper.deleteSchedulerJob(
				dispatchTrigger, dispatchTaskClusterMode.getStorageType());
		}
	}

	private List<DispatchTaskClusterMode> _getDispatchTaskClusterModes() {
		if (_clusterMasterExecutor.isMaster()) {
			return Arrays.asList(
				DispatchTaskClusterMode.ALL_NODES,
				DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED,
				DispatchTaskClusterMode.SINGLE_NODE_PERSISTED);
		}

		return Collections.singletonList(DispatchTaskClusterMode.ALL_NODES);
	}

	private static final int _MAXIMUM_QUEUE_SIZE = 100;

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchConfigurator.class);

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private DestinationFactory _destinationFactory;

	private ClusterMasterTokenTransitionListener
		_dispatchClusterMasterTokenTransitionListener;

	@Reference
	private DispatchTriggerHelper _dispatchTriggerHelper;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	private ServiceRegistration<Destination> _serviceRegistration;

	private class DispatchClusterMasterTokenTransitionListener
		extends BaseClusterMasterTokenTransitionListener {

		@Override
		protected void doMasterTokenAcquired() throws Exception {
			_addScheduledJobs(
				Arrays.asList(
					DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED,
					DispatchTaskClusterMode.SINGLE_NODE_PERSISTED));
		}

		@Override
		protected void doMasterTokenReleased() throws Exception {
		}

	}

}
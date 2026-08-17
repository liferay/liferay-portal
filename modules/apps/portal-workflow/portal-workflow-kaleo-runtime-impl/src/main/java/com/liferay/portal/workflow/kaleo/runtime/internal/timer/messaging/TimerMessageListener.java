/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.timer.messaging;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.WorkflowEngine;
import com.liferay.portal.workflow.kaleo.runtime.constants.KaleoRuntimeDestinationNames;
import com.liferay.portal.workflow.kaleo.runtime.util.SchedulerUtil;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerInstanceTokenLocalService;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "destination.name=" + KaleoRuntimeDestinationNames.WORKFLOW_TIMER,
	service = MessageListener.class
)
public class TimerMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
				KaleoRuntimeDestinationNames.WORKFLOW_TIMER);

		destinationConfiguration.setMaximumQueueSize(_MAXIMUM_QUEUE_SIZE);

		RejectedExecutionHandler rejectedExecutionHandler =
			new ThreadPoolExecutor.CallerRunsPolicy() {

				@Override
				public void rejectedExecution(
					Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"The current thread will handle the request " +
								"because the workflow timer task queue is at " +
									"its maximum capacity");
					}

					super.rejectedExecution(runnable, threadPoolExecutor);
				}

			};

		destinationConfiguration.setRejectedExecutionHandler(
			rejectedExecutionHandler);

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		_serviceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", destination.getName()));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		long kaleoTimerInstanceTokenId = message.getLong(
			"kaleoTimerInstanceTokenId");

		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			_kaleoTimerInstanceTokenLocalService.fetchKaleoTimerInstanceToken(
				kaleoTimerInstanceTokenId);

		if (kaleoTimerInstanceToken == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to find Kaleo timer instance token " +
						kaleoTimerInstanceTokenId);
			}

			SchedulerEngineHelperUtil.delete(
				SchedulerUtil.getGroupName(
					message.getLong("companyId"), kaleoTimerInstanceTokenId),
				StorageType.PERSISTED);

			return;
		}

		if (kaleoTimerInstanceToken.isCompleted()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping completed Kaleo timer instance token " +
						kaleoTimerInstanceTokenId);
			}

			SchedulerEngineHelperUtil.delete(
				SchedulerUtil.getGroupName(
					kaleoTimerInstanceToken.getCompanyId(),
					kaleoTimerInstanceTokenId),
				StorageType.PERSISTED);

			return;
		}

		Map<String, Serializable> workflowContext = WorkflowContextUtil.convert(
			kaleoTimerInstanceToken.getWorkflowContext());

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		try {
			_workflowEngine.executeTimerWorkflowInstance(
				kaleoTimerInstanceTokenId, serviceContext, workflowContext);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to execute scheduled job. Unregistering job " +
						message,
					exception);
			}

			SchedulerEngineHelperUtil.delete(
				SchedulerUtil.getGroupName(
					kaleoTimerInstanceToken.getCompanyId(),
					kaleoTimerInstanceTokenId),
				StorageType.PERSISTED);
		}
	}

	private static final int _MAXIMUM_QUEUE_SIZE = 200;

	private static final Log _log = LogFactoryUtil.getLog(
		TimerMessageListener.class);

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private KaleoTimerInstanceTokenLocalService
		_kaleoTimerInstanceTokenLocalService;

	private ServiceRegistration<Destination> _serviceRegistration;

	@Reference
	private WorkflowEngine _workflowEngine;

}
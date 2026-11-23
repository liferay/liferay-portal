/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal;

import com.liferay.portal.background.task.configuration.BackgroundTaskManagerConfiguration;
import com.liferay.portal.background.task.internal.messaging.BackgroundTaskMessageListener;
import com.liferay.portal.background.task.internal.messaging.BackgroundTaskStatusMessageListener;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutorRegistry;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistry;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocalManager;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vendel Toreki
 */
@Component(
	configurationPid = "com.liferay.portal.background.task.configuration.BackgroundTaskManagerConfiguration",
	service = {}
)
public class BackgroundTaskMessagingConfigurator {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		BackgroundTaskManagerConfiguration backgroundTaskManagerConfiguration =
			ConfigurableUtil.createConfigurable(
				BackgroundTaskManagerConfiguration.class, properties);

		_registerMessaging(
			bundleContext, DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
			DestinationNames.BACKGROUND_TASK,
			backgroundTaskManagerConfiguration.workersCoreSize(),
			backgroundTaskManagerConfiguration.workersMaxSize(),
			new BackgroundTaskMessageListener(
				_backgroundTaskExecutorRegistry, _backgroundTaskLocalService,
				_backgroundTaskStatusRegistry,
				_backgroundTaskThreadLocalManager, _lockManager, _messageBus));

		_registerMessaging(
			bundleContext, DestinationConfiguration.DESTINATION_TYPE_SERIAL,
			DestinationNames.BACKGROUND_TASK_STATUS, 1, 1,
			new BackgroundTaskStatusMessageListener(
				_backgroundTaskExecutorRegistry, _backgroundTaskLocalService,
				_backgroundTaskStatusRegistry, _lockManager, _roleLocalService,
				_userLocalService, _userNotificationEventLocalService));
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private void _registerMessaging(
		BundleContext bundleContext, String destinationType,
		String destinationName, int workersCoreSize, int workersMaxSize,
		MessageListener messageListener) {

		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(destinationType, destinationName);

		destinationConfiguration.setWorkersCoreSize(workersCoreSize);
		destinationConfiguration.setWorkersMaxSize(workersMaxSize);

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		Dictionary<String, String> dictionary = MapUtil.singletonDictionary(
			"destination.name", destination.getName());

		_serviceRegistrations.add(
			bundleContext.registerService(
				Destination.class, destination, dictionary));

		_serviceRegistrations.add(
			bundleContext.registerService(
				MessageListener.class, messageListener, dictionary));
	}

	@Reference
	private BackgroundTaskExecutorRegistry _backgroundTaskExecutorRegistry;

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private BackgroundTaskStatusRegistry _backgroundTaskStatusRegistry;

	@Reference
	private BackgroundTaskThreadLocalManager _backgroundTaskThreadLocalManager;

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private LockManager _lockManager;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private RoleLocalService _roleLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
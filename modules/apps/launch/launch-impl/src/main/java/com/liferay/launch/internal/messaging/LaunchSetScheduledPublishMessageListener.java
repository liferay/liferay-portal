/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.messaging;

import com.liferay.launch.internal.background.task.LaunchSetScheduledPublishBackgroundTaskExecutor;
import com.liferay.launch.internal.constants.LaunchDestinationNames;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = "destination.name=" + LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH,
	service = MessageListener.class
)
public class LaunchSetScheduledPublishMessageListener
	extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		Destination destination = _destinationFactory.createDestination(
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_SYNCHRONOUS,
				LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH));

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
		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				message.getLong("launchSetId"));

		if ((launchSetObjectEntry == null) ||
			(launchSetObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_SCHEDULED)) {

			return;
		}

		_backgroundTaskManager.addBackgroundTask(
			message.getLong("userId"), BackgroundTaskConstants.GROUP_ID_DEFAULT,
			String.valueOf(launchSetObjectEntry.getObjectEntryId()), null,
			LaunchSetScheduledPublishBackgroundTaskExecutor.class,
			HashMapBuilder.<String, Serializable>put(
				"launchSetId", launchSetObjectEntry.getObjectEntryId()
			).build(),
			null);
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	private ServiceRegistration<Destination> _serviceRegistration;

}
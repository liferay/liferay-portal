/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.MessageBusUtil;

import java.util.Dictionary;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jonathan McCann
 */
@Component(
	property = "model.class.name=com.liferay.portal.messaging.internal.configuration.DestinationWorkerConfiguration",
	service = ConfigurationModelListener.class
)
public class DestinationWorkerConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(
		String pid, Dictionary<String, Object> properties) {

		String destinationName = (String)properties.get("destinationName");

		Destination destination = MessageBusUtil.getDestination(
			destinationName);

		if ((destination != null) &&
			Objects.equals(
				destination.getDestinationType(),
				DestinationConfiguration.DESTINATION_TYPE_SERIAL)) {

			properties.put("workerCoreSize", _WORKER_CORE_SIZE);
			properties.put("workerMaxSize", _WORKER_MAX_SIZE);
		}
	}

	private static final int _WORKER_CORE_SIZE = 1;

	private static final int _WORKER_MAX_SIZE = 1;

}
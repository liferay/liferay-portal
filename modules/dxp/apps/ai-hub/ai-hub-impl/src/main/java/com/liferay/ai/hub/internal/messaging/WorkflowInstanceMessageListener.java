/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.messaging;

import com.liferay.ai.hub.internal.agent.util.AgentUtil;
import com.liferay.ai.hub.internal.audit.constants.AIHubEventTypes;
import com.liferay.ai.hub.internal.constants.AIHubDestinationNames;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.kaleo.runtime.constants.WorkflowInstanceDestinationNames;

import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
@Component(
	property = "destination.name=" + WorkflowInstanceDestinationNames.WORKFLOW_INSTANCE,
	service = MessageListener.class
)
public class WorkflowInstanceMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		Destination destination = _destinationFactory.createDestination(
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
				WorkflowInstanceDestinationNames.WORKFLOW_INSTANCE));

		_destinationServiceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", destination.getName()));
	}

	@Deactivate
	protected void deactivate() {
		_destinationServiceRegistration.unregister();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		if (message.contains("exception")) {
			AgentUtil.completeExceptionally(message);
		}
		else {
			AgentUtil.complete(message);
		}

		message.put(
			"additionalInformation",
			JSONUtil.put(
				"duration",
				() -> {
					Date messageCreateDate = (Date)message.get("createDate");

					WorkflowInstance workflowInstance =
						_workflowInstanceManager.getWorkflowInstance(
							message.getLong("companyId"),
							message.getLong("workflowInstanceId"));

					Date workflowInstanceStartDate =
						workflowInstance.getStartDate();

					return messageCreateDate.getTime() -
						workflowInstanceStartDate.getTime();
				}
			).put(
				"exceptionMessage",
				() -> {
					if (!message.contains("exception")) {
						return null;
					}

					Exception exception = (Exception)message.get("exception");

					return exception.getMessage();
				}
			));
		message.put(
			"eventType",
			message.contains("exception") ?
				AIHubEventTypes.AI_HUB_AGENT_INSTANCE_COMPLETE_EXCEPTIONALLY :
					AIHubEventTypes.AI_HUB_AGENT_INSTANCE_COMPLETE);

		_messageBus.sendMessage(
			AIHubDestinationNames.AI_HUB_AGENT_INSTANCE, message);
	}

	@Reference
	private DestinationFactory _destinationFactory;

	private ServiceRegistration<Destination> _destinationServiceRegistration;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}
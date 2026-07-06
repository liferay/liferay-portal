/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.workflow.kaleo.definition.constants.WorkflowDefinitionDestinationNames;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kenneth Chang
 */
@Component(service = ModelListener.class)
public class KaleoDefinitionModelListener
	extends BaseModelListener<KaleoDefinition> {

	@Override
	public void onAfterCreate(KaleoDefinition kaleoDefinition)
		throws ModelListenerException {

		_sendMessage("ADD", kaleoDefinition, null);
	}

	@Override
	public void onAfterRemove(KaleoDefinition kaleoDefinition)
		throws ModelListenerException {

		_sendMessage("DELETE", kaleoDefinition, null);
	}

	@Override
	public void onAfterUpdate(
			KaleoDefinition originalKaleoDefinition,
			KaleoDefinition kaleoDefinition)
		throws ModelListenerException {

		_sendMessage("UPDATE", kaleoDefinition, originalKaleoDefinition);
	}

	private void _sendMessage(
		String eventType, KaleoDefinition kaleoDefinition,
		KaleoDefinition originalKaleoDefinition) {

		if (kaleoDefinition == null) {
			return;
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				try {
					Message message = new Message();

					message.put("createDate", new Date());
					message.put("eventType", eventType);
					message.put("kaleoDefinition", kaleoDefinition);
					message.put("name", kaleoDefinition.getName());
					message.put(
						"originalKaleoDefinition", originalKaleoDefinition);
					message.put("scope", kaleoDefinition.getScope());

					ServiceContext serviceContext =
						ServiceContextThreadLocal.getServiceContext();

					if (serviceContext == null) {
						serviceContext = new ServiceContext();
					}

					serviceContext.setAddGroupPermissions(true);
					serviceContext.setAddGuestPermissions(true);
					serviceContext.setCompanyId(kaleoDefinition.getCompanyId());
					serviceContext.setScopeGroupId(
						kaleoDefinition.getGroupId());
					serviceContext.setUserId(kaleoDefinition.getUserId());

					message.put("serviceContext", serviceContext);

					message.put("version", kaleoDefinition.getVersion());

					_messageBus.sendMessage(
						WorkflowDefinitionDestinationNames.WORKFLOW_DEFINITION,
						message);
				}
				catch (Exception exception) {
					throw new ModelListenerException(exception);
				}

				return null;
			});
	}

	@Reference
	private MessageBus _messageBus;

}
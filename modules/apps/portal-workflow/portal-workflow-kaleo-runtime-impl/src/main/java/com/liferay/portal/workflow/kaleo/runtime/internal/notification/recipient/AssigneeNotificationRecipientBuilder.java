/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.notification.recipient;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.notification.recipient.NotificationRecipientBuilder;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "recipient.type=ASSIGNEES",
	service = NotificationRecipientBuilder.class
)
public class AssigneeNotificationRecipientBuilder
	implements NotificationRecipientBuilder {

	@Override
	public void processKaleoNotificationRecipient(
			Set<NotificationRecipient> notificationRecipients,
			KaleoNotificationRecipient kaleoNotificationRecipient,
			NotificationReceptionType notificationReceptionType,
			ExecutionContext executionContext)
		throws Exception {

		_addAssignedRecipients(
			notificationRecipients, notificationReceptionType,
			executionContext);
	}

	@Override
	public void processKaleoTaskAssignmentInstance(
			Set<NotificationRecipient> notificationRecipients,
			KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance,
			NotificationReceptionType notificationReceptionType,
			ExecutionContext executionContext)
		throws Exception {

		_addAssignedRecipients(
			notificationRecipients, notificationReceptionType,
			executionContext);
	}

	private void _addAssignedRecipients(
			Set<NotificationRecipient> notificationRecipients,
			NotificationReceptionType notificationReceptionType,
			ExecutionContext executionContext)
		throws Exception {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			executionContext.getKaleoTaskInstanceToken();

		if (kaleoTaskInstanceToken == null) {
			return;
		}

		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances =
			kaleoTaskInstanceToken.getKaleoTaskAssignmentInstances();

		for (KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance :
				kaleoTaskAssignmentInstances) {

			NotificationRecipientBuilder notificationRecipientBuilder =
				_roleNotificationRecipientBuilder;

			String assigneeClassName =
				kaleoTaskAssignmentInstance.getAssigneeClassName();

			if (assigneeClassName.equals(User.class.getName())) {
				notificationRecipientBuilder =
					_userNotificationRecipientBuilder;
			}

			notificationRecipientBuilder.processKaleoTaskAssignmentInstance(
				notificationRecipients, kaleoTaskAssignmentInstance,
				notificationReceptionType, executionContext);
		}
	}

	@Reference(target = "(recipient.type=ROLE)")
	private NotificationRecipientBuilder _roleNotificationRecipientBuilder;

	@Reference(target = "(recipient.type=USER)")
	private NotificationRecipientBuilder _userNotificationRecipientBuilder;

}
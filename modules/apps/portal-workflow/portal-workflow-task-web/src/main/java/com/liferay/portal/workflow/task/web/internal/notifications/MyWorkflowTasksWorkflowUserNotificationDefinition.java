/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.notifications;

import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.workflow.constants.MyWorkflowTasksConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sarai Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
	service = UserNotificationDefinition.class
)
public class MyWorkflowTasksWorkflowUserNotificationDefinition
	extends UserNotificationDefinition {

	public MyWorkflowTasksWorkflowUserNotificationDefinition() {
		super(
			PortletKeys.MY_WORKFLOW_TASK, 0,
			MyWorkflowTasksConstants.NOTIFICATION_TYPE_MY_WORKFLOW_TASKS,
			"receive-a-notification-when-someone-interacts-with-a-workflow");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"email", UserNotificationDeliveryConstants.TYPE_EMAIL, true,
				true));
		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}
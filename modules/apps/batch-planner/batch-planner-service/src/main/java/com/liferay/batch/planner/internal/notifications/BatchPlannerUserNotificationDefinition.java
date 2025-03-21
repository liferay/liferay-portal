/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.internal.notifications;

import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Igor Beslic
 */
@Component(
	property = "jakarta.portlet.name=" + BatchPlannerPortletKeys.BATCH_PLANNER,
	service = UserNotificationDefinition.class
)
public class BatchPlannerUserNotificationDefinition
	extends UserNotificationDefinition {

	public BatchPlannerUserNotificationDefinition() {
		super(
			BatchPlannerPortletKeys.BATCH_PLANNER, 0,
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			"receive-a-notification-when-batch-plan-finishes");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}
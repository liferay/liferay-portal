/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.notification;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.internal.util.NotificationMessageUtil;
import com.liferay.portal.workflow.kaleo.runtime.notification.BaseNotificationSender;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationSender;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = NotificationSender.class)
public class UserNotificationMessageSender
	extends BaseNotificationSender implements NotificationSender {

	@Override
	public String getNotificationType() {
		return "user-notification";
	}

	@Override
	protected void doSendNotification(
			Map<NotificationReceptionType, Set<NotificationRecipient>>
				notificationRecipients,
			String defaultSubject, String notificationMessage,
			ExecutionContext executionContext)
		throws Exception {

		JSONObject jsonObject = NotificationMessageUtil.createMessageJSONObject(
			executionContext, notificationMessage);

		for (Map.Entry<NotificationReceptionType, Set<NotificationRecipient>>
				entry : notificationRecipients.entrySet()) {

			for (NotificationRecipient notificationRecipient :
					getDeliverableNotificationRecipients(
						entry.getValue(),
						UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

				long ctCollectionId =
					CTCollectionThreadLocal.getCTCollectionId();

				if (ctCollectionId > 0) {
					User user = _userLocalService.fetchUser(
						notificationRecipient.getUserId());

					PermissionChecker permissionChecker =
						_permissionCheckerFactory.create(user);

					if (!_modelResourcePermission.contains(
							permissionChecker, ctCollectionId,
							ActionKeys.VIEW)) {

						continue;
					}
				}

				_userNotificationEventLocalService.sendUserNotificationEvents(
					notificationRecipient.getUserId(),
					PortletKeys.MY_WORKFLOW_TASK,
					UserNotificationDeliveryConstants.TYPE_WEBSITE, jsonObject);
			}
		}
	}

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection> _modelResourcePermission;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.impl;

import com.liferay.notification.constants.NotificationActionKeys;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.service.base.NotificationQueueEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Paulo Albuquerque
 */
@Component(
	property = {
		"json.web.service.context.name=notification",
		"json.web.service.context.path=NotificationQueueEntry"
	},
	service = AopService.class
)
public class NotificationQueueEntryServiceImpl
	extends NotificationQueueEntryServiceBaseImpl {

	@Override
	public NotificationQueueEntry addNotificationQueueEntry(
			NotificationContext notificationContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			NotificationActionKeys.ADD_NOTIFICATION_QUEUE_ENTRY);

		return _notificationQueueEntryLocalService.addNotificationQueueEntry(
			notificationContext);
	}

	@Override
	public NotificationQueueEntry deleteNotificationQueueEntry(
			long notificationQueueEntryId)
		throws PortalException {

		_notificationQueueEntryModelResourcePermission.check(
			getPermissionChecker(), notificationQueueEntryId,
			ActionKeys.DELETE);

		return _notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntryId);
	}

	@Override
	public NotificationQueueEntry getNotificationQueueEntry(
			long notificationQueueEntryId)
		throws PortalException {

		_notificationQueueEntryModelResourcePermission.check(
			getPermissionChecker(), notificationQueueEntryId, ActionKeys.VIEW);

		return notificationQueueEntryPersistence.findByPrimaryKey(
			notificationQueueEntryId);
	}

	@Override
	public NotificationQueueEntry resendNotificationQueueEntry(
			long notificationQueueEntryId)
		throws PortalException {

		_notificationQueueEntryModelResourcePermission.check(
			getPermissionChecker(), notificationQueueEntryId,
			ActionKeys.UPDATE);

		return notificationQueueEntryLocalService.resendNotificationQueueEntry(
			notificationQueueEntryId);
	}

	@Reference
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.notification.model.NotificationQueueEntry)"
	)
	private ModelResourcePermission<NotificationQueueEntry>
		_notificationQueueEntryModelResourcePermission;

	@Reference(
		target = "(resource.name=" + NotificationConstants.RESOURCE_NAME_NOTIFICATION_QUEUE + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.impl;

import com.liferay.commerce.notification.constants.CommerceNotificationActionKeys;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.service.base.CommerceNotificationQueueEntryServiceBaseImpl;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceNotificationQueueEntry"
	},
	service = AopService.class
)
@Deprecated
public class CommerceNotificationQueueEntryServiceImpl
	extends CommerceNotificationQueueEntryServiceBaseImpl {

	@Override
	public void deleteCommerceNotificationQueueEntry(
			long commerceNotificationQueueEntryId)
		throws PortalException {

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntryPersistence.findByPrimaryKey(
				commerceNotificationQueueEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), commerceNotificationQueueEntry.getGroupId(),
			CommerceNotificationActionKeys.
				DELETE_COMMERCE_NOTIFICATION_QUEUE_ENTRY);

		commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueue(commerceNotificationQueueEntry);
	}

	@Override
	public List<CommerceNotificationQueueEntry>
			getCommerceNotificationQueueEntries(
				long groupId, int start, int end,
				OrderByComparator<CommerceNotificationQueueEntry>
					orderByComparator)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			CommerceNotificationActionKeys.
				VIEW_COMMERCE_NOTIFICATION_QUEUE_ENTRIES);

		return commerceNotificationQueueEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceNotificationQueueEntriesCount(long groupId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			CommerceNotificationActionKeys.
				VIEW_COMMERCE_NOTIFICATION_QUEUE_ENTRIES);

		return commerceNotificationQueueEntryPersistence.countByGroupId(
			groupId);
	}

	@Override
	public CommerceNotificationQueueEntry resendCommerceNotificationQueueEntry(
			long commerceNotificationQueueEntryId)
		throws PortalException {

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			commerceNotificationQueueEntryPersistence.findByPrimaryKey(
				commerceNotificationQueueEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), commerceNotificationQueueEntry.getGroupId(),
			CommerceNotificationActionKeys.
				RESEND_COMMERCE_NOTIFICATION_QUEUE_ENTRY);

		return commerceNotificationQueueEntryLocalService.
			resendCommerceNotificationQueueEntry(
				commerceNotificationQueueEntryId);
	}

	@Reference(
		target = "(resource.name=" + CPConstants.RESOURCE_NAME_CHANNEL + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}
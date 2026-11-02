/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.test.util;

import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateLocalServiceUtil;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationQueueEntryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * @author Luca Pellizzon
 */
public class CommerceNotificationTestUtil {

	public static CommerceNotificationTemplate addCommerceNotificationTemplate(
			String name, String description, String from, String to,
			String type, ServiceContext serviceContext)
		throws PortalException {

		return CommerceNotificationTemplateLocalServiceUtil.
			addCommerceNotificationTemplate(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				name, description, from, RandomTestUtil.randomLocaleStringMap(),
				to, null, null, type, true,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), serviceContext);
	}

	public static CommerceNotificationTemplate addCommerceNotificationTemplate(
			String name, String description, String from, String to, String cc,
			String bcc, String type, ServiceContext serviceContext)
		throws PortalException {

		return CommerceNotificationTemplateLocalServiceUtil.
			addCommerceNotificationTemplate(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				name, description, from, RandomTestUtil.randomLocaleStringMap(),
				to, cc, bcc, type, true, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), serviceContext);
	}

	public static CommerceNotificationTemplate addNotificationTemplate(
			String to, String notificationType, ServiceContext serviceContext)
		throws PortalException {

		return addCommerceNotificationTemplate(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), to, notificationType,
			serviceContext);
	}

	public static CommerceNotificationTemplate addNotificationTemplate(
			String to, String cc, String bcc, String notificationType,
			ServiceContext serviceContext)
		throws PortalException {

		return addCommerceNotificationTemplate(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), to, cc, bcc, notificationType,
			serviceContext);
	}

	public static List<CommerceNotificationQueueEntry>
			getCommerceNotificationQueueEntries(
				long groupId, int start, int end,
				OrderByComparator<CommerceNotificationQueueEntry>
					orderByComparator)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommerceNotificationQueueEntryUtil.getPersistence(),
			commerceNotificationQueueEntryPersistence ->
				commerceNotificationQueueEntryPersistence.findByGroupId(
					groupId, start, end, orderByComparator));
	}

	public static int getCommerceNotificationQueueEntriesCount(long groupId)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommerceNotificationQueueEntryUtil.getPersistence(),
			commerceNotificationQueueEntryPersistence ->
				commerceNotificationQueueEntryPersistence.countByGroupId(
					groupId));
	}

}
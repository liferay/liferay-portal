/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.test.util;

import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationQueueEntryUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class CommerceNotificationQueueEntryTestUtil {

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
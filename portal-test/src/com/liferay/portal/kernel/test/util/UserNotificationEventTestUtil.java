/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.persistence.UserNotificationEventUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class UserNotificationEventTestUtil {

	public static List<UserNotificationEvent> getUserNotificationEvents(
			long userId)
		throws Exception {

		return TransactionTestUtil.invoke(
			UserNotificationEventUtil.getPersistence(),
			userNotificationEventPersistence ->
				userNotificationEventPersistence.findByUserId(userId));
	}

}
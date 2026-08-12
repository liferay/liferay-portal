/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.type;

import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Feliphe Marinho
 */
public interface NotificationType {

	public NotificationQueueEntry createNotificationQueueEntry(
		User user, String body, NotificationContext notificationContext,
		String subject);

	public List<NotificationRecipientSetting>
		createNotificationRecipientSettings(
			long notificationRecipientId, Object[] recipients, User user);

	public default Map<String, String> evaluateNotificationRecipientSettings(
			long companyId, NotificationContext notificationContext,
			Map<String, Object> notificationRecipientSettings)
		throws PortalException {

		return Collections.emptyMap();
	}

	public default Set<String> getAllowedNotificationRecipientSettingsNames() {
		return Collections.emptySet();
	}

	public default String getFromName(
		NotificationQueueEntry notificationQueueEntry) {

		return "-";
	}

	public default String getRecipientSummary(
		NotificationQueueEntry notificationQueueEntry) {

		return "-";
	}

	public String getType();

	public String getTypeLanguageKey();

	public default void resendNotification(
			NotificationQueueEntry notificationQueueEntry)
		throws PortalException {
	}

	public default void resendNotifications(int status, String type)
		throws PortalException {
	}

	public void sendNotification(NotificationContext notificationContext)
		throws PortalException;

	public void sendNotification(NotificationQueueEntry notificationQueueEntry)
		throws PortalException;

	public Object[] toRecipients(
		List<NotificationRecipientSetting> notificationRecipientSettings);

	public void validateNotificationQueueEntry(
			NotificationContext notificationContext)
		throws PortalException;

	public void validateNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.test.util;

import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationQueueEntryLocalServiceUtil;
import com.liferay.notification.service.NotificationRecipientLocalServiceUtil;
import com.liferay.notification.service.NotificationTemplateLocalServiceUtil;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Murilo Stodolni
 */
public class NotificationTemplateUtil {

	public static NotificationContext createNotificationContext(
			List<NotificationRecipientSetting> notificationRecipientSettings,
			String type)
		throws PortalException {

		return createNotificationContext(
			TestPropsValues.getUser(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), notificationRecipientSettings,
			RandomTestUtil.randomString(), type);
	}

	public static NotificationContext createNotificationContext(String type)
		throws Exception {

		return createNotificationContext(
			TestPropsValues.getUser(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), type);
	}

	public static NotificationContext createNotificationContext(User user) {
		return createNotificationContext(
			user, null, NotificationConstants.TYPE_USER_NOTIFICATION);
	}

	public static NotificationContext createNotificationContext(
		User user, long objectDefinitionId, String body, String description,
		String editorType,
		List<NotificationRecipientSetting> notificationRecipientSettings,
		String subject, String type, List<Long> attachmentObjectFieldIds) {

		NotificationContext notificationContext = new NotificationContext();

		notificationContext.setAttachmentObjectFieldIds(
			attachmentObjectFieldIds);
		notificationContext.setNotificationQueueEntry(
			createNotificationQueueEntry(user, body, subject, type));
		notificationContext.setNotificationRecipient(
			NotificationRecipientLocalServiceUtil.createNotificationRecipient(
				RandomTestUtil.randomInt()));
		notificationContext.setNotificationRecipientSettings(
			notificationRecipientSettings);
		notificationContext.setNotificationTemplate(
			createNotificationTemplate(
				user.getUserId(), objectDefinitionId, body, description,
				editorType, subject, type));
		notificationContext.setType(type);

		return notificationContext;
	}

	public static NotificationContext createNotificationContext(
		User user, String description, String type) {

		return createNotificationContext(
			user, RandomTestUtil.randomString(), description,
			RandomTestUtil.randomString(), type);
	}

	public static NotificationContext createNotificationContext(
		User user, String body, String description,
		List<NotificationRecipientSetting> notificationRecipientSettings,
		String subject, String type) {

		return createNotificationContext(
			user, 0, body, description,
			NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
			notificationRecipientSettings, subject, type,
			Collections.emptyList());
	}

	public static NotificationContext createNotificationContext(
		User user, String body, String description, String subject,
		String type) {

		List<NotificationRecipientSetting> notificationRecipientSettings =
			new ArrayList<>();

		if (type.equals(NotificationConstants.TYPE_EMAIL)) {
			notificationRecipientSettings = Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						NotificationRecipientSettingConstants.NAME_FROM,
						"[%CURRENT_USER_EMAIL_ADDRESS%]"),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						NotificationRecipientSettingConstants.NAME_FROM_NAME,
						LocalizedMapUtil.getLocalizedMap(
							"[%CURRENT_USER_FIRST_NAME%]")),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						NotificationRecipientSettingConstants.NAME_TO,
						"test@liferay.com"));
		}
		else if (type.equals(NotificationConstants.TYPE_USER_NOTIFICATION)) {
			notificationRecipientSettings = Collections.singletonList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						NotificationRecipientSettingConstants.
							NAME_USER_SCREEN_NAME,
						user.getScreenName()));
		}

		return createNotificationContext(
			user, body, description, notificationRecipientSettings, subject,
			type);
	}

	public static NotificationQueueEntry createNotificationQueueEntry(
		User user, String body, String subject, String type) {

		NotificationQueueEntry notificationQueueEntry =
			NotificationQueueEntryLocalServiceUtil.createNotificationQueueEntry(
				RandomTestUtil.randomInt());

		notificationQueueEntry.setUserId(user.getUserId());
		notificationQueueEntry.setUserName(user.getFullName());
		notificationQueueEntry.setBody(body);
		notificationQueueEntry.setSubject(subject);
		notificationQueueEntry.setType(type);
		notificationQueueEntry.setStatus(
			NotificationQueueEntryConstants.STATUS_UNSENT);

		return notificationQueueEntry;
	}

	public static NotificationTemplate createNotificationTemplate(
		long userId, long objectDefinitionId, String body, String description,
		String editorType, String subject, String type) {

		NotificationTemplate notificationTemplate =
			NotificationTemplateLocalServiceUtil.createNotificationTemplate(
				RandomTestUtil.randomInt());

		notificationTemplate.setUserId(userId);
		notificationTemplate.setObjectDefinitionId(objectDefinitionId);
		notificationTemplate.setBody(body);
		notificationTemplate.setDescription(description);
		notificationTemplate.setEditorType(editorType);
		notificationTemplate.setName(RandomTestUtil.randomString());
		notificationTemplate.setSubject(subject);
		notificationTemplate.setType(type);

		return notificationTemplate;
	}

}
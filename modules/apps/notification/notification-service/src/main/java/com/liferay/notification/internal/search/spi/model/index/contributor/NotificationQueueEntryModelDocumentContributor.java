/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.search.spi.model.index.contributor;

import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Paulo Albuquerque
 */
@Component(
	property = "indexer.class.name=com.liferay.notification.model.NotificationQueueEntry",
	service = ModelDocumentContributor.class
)
public class NotificationQueueEntryModelDocumentContributor
	implements ModelDocumentContributor<NotificationQueueEntry> {

	@Override
	public void contribute(
		Document document, NotificationQueueEntry notificationQueueEntry) {

		document.addDate(Field.SENT_DATE, notificationQueueEntry.getSentDate());

		Map<String, Object> notificationRecipientSettingsMap =
			NotificationRecipientSettingUtil.
				getNotificationRecipientSettingsMap(notificationQueueEntry);

		document.addKeyword(
			"fromName",
			String.valueOf(notificationRecipientSettingsMap.get("fromName")));
		document.addText(
			"fromName",
			String.valueOf(notificationRecipientSettingsMap.get("from")));

		document.addKeyword("subject", notificationQueueEntry.getSubject());
		document.addText("subject", notificationQueueEntry.getSubject());
	}

}
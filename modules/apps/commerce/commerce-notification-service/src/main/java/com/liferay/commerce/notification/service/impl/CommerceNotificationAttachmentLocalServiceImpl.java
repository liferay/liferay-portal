/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.impl;

import com.liferay.commerce.notification.service.base.CommerceNotificationAttachmentLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = "model.class.name=com.liferay.commerce.notification.model.CommerceNotificationAttachment",
	service = AopService.class
)
@Deprecated
public class CommerceNotificationAttachmentLocalServiceImpl
	extends CommerceNotificationAttachmentLocalServiceBaseImpl {

	@Override
	public void deleteCommerceNotificationAttachments(
		long commerceNotificationQueueEntryId) {

		commerceNotificationAttachmentPersistence.
			removeByCommerceNotificationQueueEntryId(
				commerceNotificationQueueEntryId);
	}

}
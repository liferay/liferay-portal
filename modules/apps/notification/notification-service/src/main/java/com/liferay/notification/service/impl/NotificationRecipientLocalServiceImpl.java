/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.impl;

import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.service.base.NotificationRecipientLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = "model.class.name=com.liferay.notification.model.NotificationRecipient",
	service = AopService.class
)
public class NotificationRecipientLocalServiceImpl
	extends NotificationRecipientLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationRecipient addNotificationRecipient(
			long userId, long classNameId, long classPK)
		throws PortalException {

		NotificationRecipient notificationRecipient =
			notificationRecipientPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		notificationRecipient.setCompanyId(user.getCompanyId());
		notificationRecipient.setUserId(user.getUserId());
		notificationRecipient.setUserName(user.getFullName());

		notificationRecipient.setClassNameId(classNameId);
		notificationRecipient.setClassPK(classPK);

		return notificationRecipientPersistence.update(notificationRecipient);
	}

	@Override
	public NotificationRecipient getNotificationRecipientByClassPK(
		long classPK) {

		return notificationRecipientPersistence.fetchByClassPK(classPK);
	}

	@Reference
	private UserLocalService _userLocalService;

}
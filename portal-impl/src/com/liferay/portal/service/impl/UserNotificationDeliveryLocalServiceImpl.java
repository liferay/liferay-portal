/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDelivery;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.service.base.UserNotificationDeliveryLocalServiceBaseImpl;

/**
 * @author Jonathan Lee
 */
public class UserNotificationDeliveryLocalServiceImpl
	extends UserNotificationDeliveryLocalServiceBaseImpl {

	@Override
	public UserNotificationDelivery addUserNotificationDelivery(
			long userId, String portletId, long classNameId,
			int notificationType, int deliveryType, boolean deliver)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		long userNotificationDeliveryId = counterLocalService.increment();

		UserNotificationDelivery userNotificationDelivery =
			userNotificationDeliveryPersistence.create(
				userNotificationDeliveryId);

		userNotificationDelivery.setCompanyId(user.getCompanyId());
		userNotificationDelivery.setUserId(user.getUserId());
		userNotificationDelivery.setPortletId(portletId);
		userNotificationDelivery.setClassNameId(classNameId);
		userNotificationDelivery.setNotificationType(notificationType);
		userNotificationDelivery.setDeliveryType(deliveryType);
		userNotificationDelivery.setDeliver(deliver);

		return userNotificationDeliveryPersistence.update(
			userNotificationDelivery);
	}

	@Override
	public void deleteUserNotificationDeliveries(long userId) {
		userNotificationDeliveryPersistence.removeByUserId(userId);
	}

	@Override
	public UserNotificationDelivery fetchUserNotificationDelivery(
		long userId, String portletId, long classNameId, int notificationType,
		int deliveryType) {

		return userNotificationDeliveryPersistence.fetchByU_P_C_N_D(
			userId, portletId, classNameId, notificationType, deliveryType);
	}

	@Override
	public UserNotificationDelivery getUserNotificationDelivery(
			long userId, String portletId, long classNameId,
			int notificationType, int deliveryType, boolean deliver)
		throws PortalException {

		UserNotificationDelivery userNotificationDelivery =
			userNotificationDeliveryPersistence.fetchByU_P_C_N_D(
				userId, portletId, classNameId, notificationType, deliveryType);

		if (userNotificationDelivery != null) {
			return userNotificationDelivery;
		}

		return userNotificationDeliveryLocalService.addUserNotificationDelivery(
			userId, portletId, classNameId, notificationType, deliveryType,
			deliver);
	}

	@Override
	public UserNotificationDelivery updateUserNotificationDelivery(
		long userNotificationDeliveryId, boolean deliver) {

		UserNotificationDelivery userNotificationDelivery =
			fetchUserNotificationDelivery(userNotificationDeliveryId);

		userNotificationDelivery.setDeliver(deliver);

		return userNotificationDeliveryPersistence.update(
			userNotificationDelivery);
	}

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.impl;

import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.announcements.kernel.model.AnnouncementsEntryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portlet.announcements.service.base.AnnouncementsDeliveryLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class AnnouncementsDeliveryLocalServiceImpl
	extends AnnouncementsDeliveryLocalServiceBaseImpl {

	@Override
	public AnnouncementsDelivery addUserDelivery(long userId, String type)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		long deliveryId = counterLocalService.increment();

		AnnouncementsDelivery delivery =
			announcementsDeliveryPersistence.create(deliveryId);

		delivery.setCompanyId(user.getCompanyId());
		delivery.setUserId(user.getUserId());
		delivery.setType(type);
		delivery.setEmail(false);
		delivery.setSms(false);
		delivery.setWebsite(true);

		try {
			delivery = announcementsDeliveryPersistence.update(delivery);
		}
		catch (SystemException systemException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Add failed, fetch {userId=", userId, ", type=", type,
						"}"));
			}

			delivery = announcementsDeliveryPersistence.fetchByU_T(
				userId, type, false);

			if (delivery == null) {
				throw systemException;
			}
		}

		return delivery;
	}

	@Override
	public void deleteDeliveries(long userId) {
		List<AnnouncementsDelivery> deliveries =
			announcementsDeliveryPersistence.findByUserId(userId);

		for (AnnouncementsDelivery delivery : deliveries) {
			deleteDelivery(delivery);
		}
	}

	@Override
	public void deleteDelivery(AnnouncementsDelivery delivery) {
		announcementsDeliveryPersistence.remove(delivery);
	}

	@Override
	public void deleteDelivery(long deliveryId) throws PortalException {
		AnnouncementsDelivery delivery =
			announcementsDeliveryPersistence.findByPrimaryKey(deliveryId);

		deleteDelivery(delivery);
	}

	@Override
	public List<AnnouncementsDelivery> getUserDeliveries(long userId)
		throws PortalException {

		List<AnnouncementsDelivery> deliveries = new ArrayList<>(
			AnnouncementsEntryConstants.TYPES.length);

		for (String type : AnnouncementsEntryConstants.TYPES) {
			deliveries.add(getUserDelivery(userId, type));
		}

		return deliveries;
	}

	@Override
	public AnnouncementsDelivery getUserDelivery(long userId, String type)
		throws PortalException {

		AnnouncementsDelivery delivery =
			announcementsDeliveryPersistence.fetchByU_T(userId, type);

		if (delivery == null) {
			delivery = announcementsDeliveryLocalService.addUserDelivery(
				userId, type);
		}

		return delivery;
	}

	@Override
	public AnnouncementsDelivery updateDelivery(
			long userId, String type, boolean email, boolean sms)
		throws PortalException {

		AnnouncementsDelivery delivery = getUserDelivery(userId, type);

		delivery.setEmail(email);
		delivery.setSms(sms);
		delivery.setWebsite(true);

		return announcementsDeliveryPersistence.update(delivery);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsDeliveryLocalServiceImpl.class);

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}
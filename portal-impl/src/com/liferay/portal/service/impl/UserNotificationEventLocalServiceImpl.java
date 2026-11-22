/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.base.UserNotificationEventLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Edward Han
 * @author Brian Wing Shun Chan
 */
@CTAware
public class UserNotificationEventLocalServiceImpl
	extends UserNotificationEventLocalServiceBaseImpl {

	@Override
	public UserNotificationEvent addUserNotificationEvent(
			long userId, boolean delivered, boolean actionRequired,
			NotificationEvent notificationEvent)
		throws PortalException {

		JSONObject payloadJSONObject = notificationEvent.getPayload();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUuid(notificationEvent.getUuid());

		return userNotificationEventLocalService.addUserNotificationEvent(
			userId, notificationEvent.getType(),
			notificationEvent.getTimestamp(),
			notificationEvent.getDeliveryType(),
			notificationEvent.getDeliverBy(), delivered,
			payloadJSONObject.toString(), actionRequired,
			notificationEvent.isArchived(), serviceContext);
	}

	@Override
	public UserNotificationEvent addUserNotificationEvent(
			long userId, NotificationEvent notificationEvent)
		throws PortalException {

		return userNotificationEventLocalService.addUserNotificationEvent(
			userId, true, false, notificationEvent);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public UserNotificationEvent addUserNotificationEvent(
			long userId, String type, long timestamp, int deliveryType,
			long deliverBy, boolean delivered, String payload,
			boolean actionRequired, boolean archived,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		long userNotificationEventId = counterLocalService.increment();

		UserNotificationEvent userNotificationEvent =
			userNotificationEventPersistence.create(userNotificationEventId);

		userNotificationEvent.setUuid(serviceContext.getUuid());
		userNotificationEvent.setCompanyId(user.getCompanyId());
		userNotificationEvent.setUserId(userId);
		userNotificationEvent.setType(type);
		userNotificationEvent.setTimestamp(timestamp);
		userNotificationEvent.setDeliveryType(deliveryType);
		userNotificationEvent.setDeliverBy(deliverBy);
		userNotificationEvent.setDelivered(delivered);
		userNotificationEvent.setPayload(payload);
		userNotificationEvent.setActionRequired(actionRequired);
		userNotificationEvent.setArchived(archived);

		return userNotificationEventPersistence.update(userNotificationEvent);
	}

	@Override
	public UserNotificationEvent addUserNotificationEvent(
			long userId, String type, long timestamp, int deliveryType,
			long deliverBy, String payload, boolean archived,
			ServiceContext serviceContext)
		throws PortalException {

		return userNotificationEventLocalService.addUserNotificationEvent(
			userId, type, timestamp, deliveryType, deliverBy, true, payload,
			false, archived, serviceContext);
	}

	@Override
	public void deleteUserNotificationEvents(long userId)
		throws PortalException {

		List<UserNotificationEvent> userNotificationEvents =
			userNotificationEventPersistence.findByUserId(userId);

		for (UserNotificationEvent userNotificationEvent :
				userNotificationEvents) {

			userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());
		}
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return userNotificationEventPersistence.findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end, orderByComparator);
	}

	@Override
	public int getArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		return userNotificationEventPersistence.countByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived);
	}

	@Override
	public int getDeliveredArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return userNotificationEventPersistence.countByU_DT_D_A(
			userId, deliveryType, delivered, archived);
	}

	@Override
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return userNotificationEventPersistence.findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired, start, end,
			orderByComparator);
	}

	@Override
	public int getDeliveredUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return userNotificationEventPersistence.countByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired);
	}

	@Override
	public List<UserNotificationEvent> getTypeNotificationEvents(String type) {
		return userNotificationEventPersistence.findByType(type);
	}

	@Override
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return userNotificationEventPersistence.findByUserId(
			userId, start, end, orderByComparator);
	}

	@Override
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, String type, long timestamp, boolean delivered) {

		return userNotificationEventPersistence.findByU_T_GteT_D(
			userId, type, timestamp, delivered);
	}

	@Override
	public int getUserNotificationEventsCount(long userId) {
		return userNotificationEventPersistence.countByUserId(userId);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return userNotificationEventPersistence.countByU_DT_D_A(
			userId, deliveryType, delivered, archived);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return userNotificationEventPersistence.countByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived);
	}

	@Override
	public UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType, boolean delivered,
			boolean actionRequired, JSONObject notificationEventJSONObject)
		throws PortalException {

		NotificationEvent notificationEvent = new NotificationEvent(
			System.currentTimeMillis(), portletId, notificationEventJSONObject);

		notificationEvent.setDeliveryType(deliveryType);

		UserNotificationEvent userNotificationEvent = addUserNotificationEvent(
			userId, delivered, actionRequired, notificationEvent);

		if (deliveryType == UserNotificationDeliveryConstants.TYPE_PUSH) {
			sendPushNotification(notificationEvent);
		}

		return userNotificationEvent;
	}

	@Override
	public UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType,
			boolean actionRequired, JSONObject notificationEventJSONObject)
		throws PortalException {

		return userNotificationEventLocalService.sendUserNotificationEvents(
			userId, portletId, deliveryType, true, actionRequired,
			notificationEventJSONObject);
	}

	@Override
	public UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType,
			JSONObject notificationEventJSONObject)
		throws PortalException {

		return userNotificationEventLocalService.sendUserNotificationEvents(
			userId, portletId, deliveryType, false,
			notificationEventJSONObject);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public UserNotificationEvent updateUserNotificationEvent(
		String uuid, long companyId, boolean archive) {

		List<UserNotificationEvent> userNotificationEvents =
			userNotificationEventPersistence.findByUuid_C(uuid, companyId);

		if (userNotificationEvents.isEmpty()) {
			return null;
		}

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		userNotificationEvent.setArchived(archive);

		return userNotificationEventPersistence.update(userNotificationEvent);
	}

	protected void sendPushNotification(NotificationEvent notificationEvent) {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				Message message = new Message();

				message.setPayload(notificationEvent.getPayload());

				MessageBusUtil.sendMessage(_PUSH_NOTIFICATION, message);

				return null;
			});
	}

	private static final String _PUSH_NOTIFICATION =
		"liferay/push_notification";

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}
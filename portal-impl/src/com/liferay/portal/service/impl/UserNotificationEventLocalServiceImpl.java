/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	public List<UserNotificationEvent> addUserNotificationEvents(
			long userId, Collection<NotificationEvent> notificationEvents)
		throws PortalException {

		List<UserNotificationEvent> userNotificationEvents = new ArrayList<>(
			notificationEvents.size());

		for (NotificationEvent notificationEvent : notificationEvents) {
			UserNotificationEvent userNotificationEvent =
				userNotificationEventLocalService.addUserNotificationEvent(
					userId, notificationEvent);

			userNotificationEvents.add(userNotificationEvent);
		}

		return userNotificationEvents;
	}

	@Override
	public void archiveUserNotificationEvents(
			long userId, int deliveryType, boolean actionRequired)
		throws PortalException {

		int userNotificationEventsCount =
			getArchivedUserNotificationEventsCount(
				userId, deliveryType, true, actionRequired, false);

		IntervalActionProcessor<Void> intervalActionProcessor =
			new IntervalActionProcessor<>(userNotificationEventsCount);

		intervalActionProcessor.setPerformIntervalActionMethod(
			(start, end) -> {
				List<UserNotificationEvent> userNotificationEvents =
					getArchivedUserNotificationEvents(
						userId, deliveryType, true, actionRequired, false,
						start, end);

				for (UserNotificationEvent userNotificationEvent :
						userNotificationEvents) {

					userNotificationEvent.setArchived(true);

					userNotificationEventLocalService.
						updateUserNotificationEvent(userNotificationEvent);
				}

				return null;
			});

		intervalActionProcessor.performIntervalActions();
	}

	@Override
	public void deleteUserNotificationEvent(String uuid, long companyId)
		throws PortalException {

		List<UserNotificationEvent> userNotificationEvents =
			userNotificationEventPersistence.findByUuid_C(uuid, companyId);

		for (UserNotificationEvent userNotificationEvent :
				userNotificationEvents) {

			userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());
		}

		userNotificationEventPersistence.removeByUuid_C(uuid, companyId);
	}

	@Override
	public void deleteUserNotificationEvents(
			Collection<String> uuids, long companyId)
		throws PortalException {

		for (String uuid : uuids) {
			userNotificationEventLocalService.deleteUserNotificationEvent(
				uuid, companyId);
		}
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
		long userId, boolean archived) {

		return userNotificationEventPersistence.findByU_A(userId, archived);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, boolean actionRequired, boolean archived) {

		return userNotificationEventPersistence.findByU_AR_A(
			userId, actionRequired, archived);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, boolean actionRequired, boolean archived, int start,
		int end) {

		return userNotificationEventPersistence.findByU_AR_A(
			userId, actionRequired, archived, start, end);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, boolean archived, int start, int end) {

		return userNotificationEventPersistence.findByU_A(
			userId, archived, start, end);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean archived) {

		return userNotificationEventPersistence.findByU_DT_A(
			userId, deliveryType, archived);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean actionRequired,
		boolean archived) {

		return userNotificationEventPersistence.findByU_DT_AR_A(
			userId, deliveryType, actionRequired, archived);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end) {

		return userNotificationEventPersistence.findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end);
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
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean actionRequired, boolean archived,
		int start, int end) {

		return userNotificationEventPersistence.findByU_DT_AR_A(
			userId, deliveryType, actionRequired, archived, start, end);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean actionRequired, boolean archived,
		int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return userNotificationEventPersistence.findByU_DT_AR_A(
			userId, deliveryType, actionRequired, archived, start, end,
			orderByComparator);
	}

	@Override
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean archived, int start, int end) {

		return userNotificationEventPersistence.findByU_DT_A(
			userId, deliveryType, archived, start, end);
	}

	@Override
	public int getArchivedUserNotificationEventsCount(
		long userId, boolean archived) {

		return userNotificationEventPersistence.countByU_A(userId, archived);
	}

	@Override
	public int getArchivedUserNotificationEventsCount(
		long userId, boolean actionRequired, boolean archived) {

		return userNotificationEventPersistence.countByU_AR_A(
			userId, actionRequired, archived);
	}

	@Override
	public int getArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean archived) {

		return userNotificationEventPersistence.countByU_DT_A(
			userId, deliveryType, archived);
	}

	@Override
	public int getArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean actionRequired,
		boolean archived) {

		return userNotificationEventPersistence.countByU_DT_AR_A(
			userId, deliveryType, actionRequired, archived);
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
		long userId, boolean delivered) {

		return userNotificationEventPersistence.findByU_D(userId, delivered);
	}

	@Override
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, boolean delivered, boolean actionRequired) {

		return userNotificationEventPersistence.findByU_D_AR(
			userId, delivered, actionRequired);
	}

	@Override
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, boolean delivered, boolean actionRequired, int start,
		int end) {

		return userNotificationEventPersistence.findByU_D_AR(
			userId, delivered, actionRequired, start, end);
	}

	@Override
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, boolean delivered, int start, int end) {

		return userNotificationEventPersistence.findByU_D(
			userId, delivered, start, end);
	}

	@Override
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered) {

		return userNotificationEventPersistence.findByU_DT_D(
			userId, deliveryType, delivered);
	}

	@Override
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return userNotificationEventPersistence.findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired);
	}

	@Override
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end) {

		return userNotificationEventPersistence.findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired, start, end);
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
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered, int start, int end) {

		return userNotificationEventPersistence.findByU_DT_D(
			userId, deliveryType, delivered, start, end);
	}

	@Override
	public int getDeliveredUserNotificationEventsCount(
		long userId, boolean delivered) {

		return userNotificationEventPersistence.countByU_D(userId, delivered);
	}

	@Override
	public int getDeliveredUserNotificationEventsCount(
		long userId, boolean delivered, boolean actionRequired) {

		return userNotificationEventPersistence.countByU_D_AR(
			userId, delivered, actionRequired);
	}

	@Override
	public int getDeliveredUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered) {

		return userNotificationEventPersistence.countByU_DT_D(
			userId, deliveryType, delivered);
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
	public List<UserNotificationEvent> getUserNotificationEvents(long userId) {
		return userNotificationEventPersistence.findByUserId(userId);
	}

	@Override
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int deliveryType) {

		return userNotificationEventPersistence.findByU_DT(
			userId, deliveryType);
	}

	@Override
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int start, int end) {

		return userNotificationEventPersistence.findByUserId(
			userId, start, end);
	}

	@Override
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int deliveryType, int start, int end) {

		return userNotificationEventPersistence.findByU_DT(
			userId, deliveryType, start, end);
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
	public int getUserNotificationEventsCount(long userId, int deliveryType) {
		return userNotificationEventPersistence.countByU_DT(
			userId, deliveryType);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return userNotificationEventPersistence.countByU_DT_D_A(
			userId, deliveryType, delivered, archived);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, String type, int deliveryType, boolean delivered) {

		return userNotificationEventPersistence.countByU_T_DT_D(
			userId, type, deliveryType, delivered);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return userNotificationEventPersistence.countByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, String type, Map<String, String> payloadParameters) {

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserNotificationEvent.class, getClassLoader());

		Property userIdProperty = PropertyFactoryUtil.forName("userId");

		dynamicQuery.add(userIdProperty.eq(userId));

		Property typeProperty = PropertyFactoryUtil.forName("type");

		dynamicQuery.add(typeProperty.eq(type));

		Property payloadProperty = PropertyFactoryUtil.forName("payload");

		for (Map.Entry<String, String> payloadParameter :
				payloadParameters.entrySet()) {

			dynamicQuery.add(
				payloadProperty.like(
					StringBundler.concat(
						"%\"", payloadParameter.getKey(), "\":\"",
						payloadParameter.getValue(), "\"%")));
		}

		return (int)dynamicQueryCount(dynamicQuery);
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

	@Override
	public List<UserNotificationEvent> updateUserNotificationEvents(
		Collection<String> uuids, long companyId, boolean archive) {

		List<UserNotificationEvent> userNotificationEvents = new ArrayList<>();

		for (String uuid : uuids) {
			userNotificationEvents.add(
				userNotificationEventLocalService.updateUserNotificationEvent(
					uuid, companyId, archive));
		}

		return userNotificationEvents;
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
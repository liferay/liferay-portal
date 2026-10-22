/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for UserNotificationEvent. This utility wraps
 * <code>com.liferay.portal.service.impl.UserNotificationEventLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserNotificationEventLocalService
 * @generated
 */
public class UserNotificationEventLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserNotificationEventLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static UserNotificationEvent addUserNotificationEvent(
			long userId, boolean delivered, boolean actionRequired,
			com.liferay.portal.kernel.notifications.NotificationEvent
				notificationEvent)
		throws PortalException {

		return getService().addUserNotificationEvent(
			userId, delivered, actionRequired, notificationEvent);
	}

	public static UserNotificationEvent addUserNotificationEvent(
			long userId,
			com.liferay.portal.kernel.notifications.NotificationEvent
				notificationEvent)
		throws PortalException {

		return getService().addUserNotificationEvent(userId, notificationEvent);
	}

	public static UserNotificationEvent addUserNotificationEvent(
			long userId, String type, long timestamp, int deliveryType,
			long deliverBy, boolean delivered, String payload,
			boolean actionRequired, boolean archived,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserNotificationEvent(
			userId, type, timestamp, deliveryType, deliverBy, delivered,
			payload, actionRequired, archived, serviceContext);
	}

	public static UserNotificationEvent addUserNotificationEvent(
			long userId, String type, long timestamp, int deliveryType,
			long deliverBy, String payload, boolean archived,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserNotificationEvent(
			userId, type, timestamp, deliveryType, deliverBy, payload, archived,
			serviceContext);
	}

	/**
	 * Adds the user notification event to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEvent the user notification event
	 * @return the user notification event that was added
	 */
	public static UserNotificationEvent addUserNotificationEvent(
		UserNotificationEvent userNotificationEvent) {

		return getService().addUserNotificationEvent(userNotificationEvent);
	}

	public static List<UserNotificationEvent> addUserNotificationEvents(
			long userId,
			java.util.Collection
				<com.liferay.portal.kernel.notifications.NotificationEvent>
					notificationEvents)
		throws PortalException {

		return getService().addUserNotificationEvents(
			userId, notificationEvents);
	}

	public static void archiveUserNotificationEvents(
			long userId, int deliveryType, boolean actionRequired)
		throws PortalException {

		getService().archiveUserNotificationEvents(
			userId, deliveryType, actionRequired);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user notification event with the primary key. Does not add the user notification event to the database.
	 *
	 * @param userNotificationEventId the primary key for the new user notification event
	 * @return the new user notification event
	 */
	public static UserNotificationEvent createUserNotificationEvent(
		long userNotificationEventId) {

		return getService().createUserNotificationEvent(
			userNotificationEventId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the user notification event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event that was removed
	 * @throws PortalException if a user notification event with the primary key could not be found
	 */
	public static UserNotificationEvent deleteUserNotificationEvent(
			long userNotificationEventId)
		throws PortalException {

		return getService().deleteUserNotificationEvent(
			userNotificationEventId);
	}

	public static void deleteUserNotificationEvent(String uuid, long companyId)
		throws PortalException {

		getService().deleteUserNotificationEvent(uuid, companyId);
	}

	/**
	 * Deletes the user notification event from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEvent the user notification event
	 * @return the user notification event that was removed
	 */
	public static UserNotificationEvent deleteUserNotificationEvent(
		UserNotificationEvent userNotificationEvent) {

		return getService().deleteUserNotificationEvent(userNotificationEvent);
	}

	public static void deleteUserNotificationEvents(
			java.util.Collection<String> uuids, long companyId)
		throws PortalException {

		getService().deleteUserNotificationEvents(uuids, companyId);
	}

	public static void deleteUserNotificationEvents(long userId)
		throws PortalException {

		getService().deleteUserNotificationEvents(userId);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static UserNotificationEvent fetchUserNotificationEvent(
		long userNotificationEventId) {

		return getService().fetchUserNotificationEvent(userNotificationEventId);
	}

	/**
	 * Returns the user notification event with the matching UUID and company.
	 *
	 * @param uuid the user notification event's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent
		fetchUserNotificationEventByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchUserNotificationEventByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end) {

		return getService().getArchivedUserNotificationEvents(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end);
	}

	public static List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getService().getArchivedUserNotificationEvents(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end, orderByComparator);
	}

	public static int getArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		return getService().getArchivedUserNotificationEventsCount(
			userId, deliveryType, delivered, actionRequired, archived);
	}

	public static int getDeliveredArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return getService().getDeliveredArchivedUserNotificationEventsCount(
			userId, deliveryType, delivered, archived);
	}

	public static List<UserNotificationEvent>
		getDeliveredUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired) {

		return getService().getDeliveredUserNotificationEvents(
			userId, deliveryType, delivered, actionRequired);
	}

	public static List<UserNotificationEvent>
		getDeliveredUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, int start, int end) {

		return getService().getDeliveredUserNotificationEvents(
			userId, deliveryType, delivered, actionRequired, start, end);
	}

	public static List<UserNotificationEvent>
		getDeliveredUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, int start, int end,
			OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getService().getDeliveredUserNotificationEvents(
			userId, deliveryType, delivered, actionRequired, start, end,
			orderByComparator);
	}

	public static int getDeliveredUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return getService().getDeliveredUserNotificationEventsCount(
			userId, deliveryType, delivered, actionRequired);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<UserNotificationEvent> getTypeNotificationEvents(
		String type) {

		return getService().getTypeNotificationEvents(type);
	}

	/**
	 * Returns the user notification event with the primary key.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event
	 * @throws PortalException if a user notification event with the primary key could not be found
	 */
	public static UserNotificationEvent getUserNotificationEvent(
			long userNotificationEventId)
		throws PortalException {

		return getService().getUserNotificationEvent(userNotificationEventId);
	}

	/**
	 * Returns the user notification event with the matching UUID and company.
	 *
	 * @param uuid the user notification event's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user notification event
	 * @throws PortalException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent
			getUserNotificationEventByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getUserNotificationEventByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the user notification events.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of user notification events
	 */
	public static List<UserNotificationEvent> getUserNotificationEvents(
		int start, int end) {

		return getService().getUserNotificationEvents(start, end);
	}

	public static List<UserNotificationEvent> getUserNotificationEvents(
		long userId) {

		return getService().getUserNotificationEvents(userId);
	}

	public static List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int start, int end) {

		return getService().getUserNotificationEvents(userId, start, end);
	}

	public static List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getService().getUserNotificationEvents(
			userId, start, end, orderByComparator);
	}

	public static List<UserNotificationEvent> getUserNotificationEvents(
		long userId, String type, long timestamp, boolean delivered) {

		return getService().getUserNotificationEvents(
			userId, type, timestamp, delivered);
	}

	/**
	 * Returns the number of user notification events.
	 *
	 * @return the number of user notification events
	 */
	public static int getUserNotificationEventsCount() {
		return getService().getUserNotificationEventsCount();
	}

	public static int getUserNotificationEventsCount(long userId) {
		return getService().getUserNotificationEventsCount(userId);
	}

	public static int getUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return getService().getUserNotificationEventsCount(
			userId, deliveryType, delivered, archived);
	}

	public static int getUserNotificationEventsCount(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return getService().getUserNotificationEventsCount(
			userId, type, deliveryType, delivered, archived);
	}

	public static int getUserNotificationEventsCount(
		long userId, String type, Map<String, String> payloadParameters) {

		return getService().getUserNotificationEventsCount(
			userId, type, payloadParameters);
	}

	public static UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType, boolean delivered,
			boolean actionRequired,
			com.liferay.portal.kernel.json.JSONObject
				notificationEventJSONObject)
		throws PortalException {

		return getService().sendUserNotificationEvents(
			userId, portletId, deliveryType, delivered, actionRequired,
			notificationEventJSONObject);
	}

	public static UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType,
			boolean actionRequired,
			com.liferay.portal.kernel.json.JSONObject
				notificationEventJSONObject)
		throws PortalException {

		return getService().sendUserNotificationEvents(
			userId, portletId, deliveryType, actionRequired,
			notificationEventJSONObject);
	}

	public static UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType,
			com.liferay.portal.kernel.json.JSONObject
				notificationEventJSONObject)
		throws PortalException {

		return getService().sendUserNotificationEvents(
			userId, portletId, deliveryType, notificationEventJSONObject);
	}

	public static UserNotificationEvent updateUserNotificationEvent(
		String uuid, long companyId, boolean archive) {

		return getService().updateUserNotificationEvent(
			uuid, companyId, archive);
	}

	/**
	 * Updates the user notification event in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEvent the user notification event
	 * @return the user notification event that was updated
	 */
	public static UserNotificationEvent updateUserNotificationEvent(
		UserNotificationEvent userNotificationEvent) {

		return getService().updateUserNotificationEvent(userNotificationEvent);
	}

	public static List<UserNotificationEvent> updateUserNotificationEvents(
		java.util.Collection<String> uuids, long companyId, boolean archive) {

		return getService().updateUserNotificationEvents(
			uuids, companyId, archive);
	}

	public static UserNotificationEventLocalService getService() {
		return _service;
	}

	public static void setService(UserNotificationEventLocalService service) {
		_service = service;
	}

	private static volatile UserNotificationEventLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-573044822
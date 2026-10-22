/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link UserNotificationEventLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserNotificationEventLocalService
 * @generated
 */
public class UserNotificationEventLocalServiceWrapper
	implements ServiceWrapper<UserNotificationEventLocalService>,
			   UserNotificationEventLocalService {

	public UserNotificationEventLocalServiceWrapper() {
		this(null);
	}

	public UserNotificationEventLocalServiceWrapper(
		UserNotificationEventLocalService userNotificationEventLocalService) {

		_userNotificationEventLocalService = userNotificationEventLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			addUserNotificationEvent(
				long userId, boolean delivered, boolean actionRequired,
				com.liferay.portal.kernel.notifications.NotificationEvent
					notificationEvent)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.addUserNotificationEvent(
			userId, delivered, actionRequired, notificationEvent);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			addUserNotificationEvent(
				long userId,
				com.liferay.portal.kernel.notifications.NotificationEvent
					notificationEvent)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.addUserNotificationEvent(
			userId, notificationEvent);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			addUserNotificationEvent(
				long userId, String type, long timestamp, int deliveryType,
				long deliverBy, boolean delivered, String payload,
				boolean actionRequired, boolean archived,
				ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.addUserNotificationEvent(
			userId, type, timestamp, deliveryType, deliverBy, delivered,
			payload, actionRequired, archived, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			addUserNotificationEvent(
				long userId, String type, long timestamp, int deliveryType,
				long deliverBy, String payload, boolean archived,
				ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.addUserNotificationEvent(
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
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
		addUserNotificationEvent(
			com.liferay.portal.kernel.model.UserNotificationEvent
				userNotificationEvent) {

		return _userNotificationEventLocalService.addUserNotificationEvent(
			userNotificationEvent);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
			addUserNotificationEvents(
				long userId,
				java.util.Collection
					<com.liferay.portal.kernel.notifications.NotificationEvent>
						notificationEvents)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.addUserNotificationEvents(
			userId, notificationEvents);
	}

	@Override
	public void archiveUserNotificationEvents(
			long userId, int deliveryType, boolean actionRequired)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userNotificationEventLocalService.archiveUserNotificationEvents(
			userId, deliveryType, actionRequired);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new user notification event with the primary key. Does not add the user notification event to the database.
	 *
	 * @param userNotificationEventId the primary key for the new user notification event
	 * @return the new user notification event
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
		createUserNotificationEvent(long userNotificationEventId) {

		return _userNotificationEventLocalService.createUserNotificationEvent(
			userNotificationEventId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			deleteUserNotificationEvent(long userNotificationEventId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.deleteUserNotificationEvent(
			userNotificationEventId);
	}

	@Override
	public void deleteUserNotificationEvent(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userNotificationEventLocalService.deleteUserNotificationEvent(
			uuid, companyId);
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
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
		deleteUserNotificationEvent(
			com.liferay.portal.kernel.model.UserNotificationEvent
				userNotificationEvent) {

		return _userNotificationEventLocalService.deleteUserNotificationEvent(
			userNotificationEvent);
	}

	@Override
	public void deleteUserNotificationEvents(
			java.util.Collection<String> uuids, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userNotificationEventLocalService.deleteUserNotificationEvents(
			uuids, companyId);
	}

	@Override
	public void deleteUserNotificationEvents(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userNotificationEventLocalService.deleteUserNotificationEvents(userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userNotificationEventLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userNotificationEventLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userNotificationEventLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _userNotificationEventLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _userNotificationEventLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _userNotificationEventLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _userNotificationEventLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _userNotificationEventLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
		fetchUserNotificationEvent(long userNotificationEventId) {

		return _userNotificationEventLocalService.fetchUserNotificationEvent(
			userNotificationEventId);
	}

	/**
	 * Returns the user notification event with the matching UUID and company.
	 *
	 * @param uuid the user notification event's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
		fetchUserNotificationEventByUuidAndCompanyId(
			String uuid, long companyId) {

		return _userNotificationEventLocalService.
			fetchUserNotificationEventByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userNotificationEventLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getArchivedUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, boolean archived, int start, int end) {

		return _userNotificationEventLocalService.
			getArchivedUserNotificationEvents(
				userId, deliveryType, delivered, actionRequired, archived,
				start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getArchivedUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, boolean archived, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.UserNotificationEvent>
					orderByComparator) {

		return _userNotificationEventLocalService.
			getArchivedUserNotificationEvents(
				userId, deliveryType, delivered, actionRequired, archived,
				start, end, orderByComparator);
	}

	@Override
	public int getArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		return _userNotificationEventLocalService.
			getArchivedUserNotificationEventsCount(
				userId, deliveryType, delivered, actionRequired, archived);
	}

	@Override
	public int getDeliveredArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return _userNotificationEventLocalService.
			getDeliveredArchivedUserNotificationEventsCount(
				userId, deliveryType, delivered, archived);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getDeliveredUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired) {

		return _userNotificationEventLocalService.
			getDeliveredUserNotificationEvents(
				userId, deliveryType, delivered, actionRequired);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getDeliveredUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, int start, int end) {

		return _userNotificationEventLocalService.
			getDeliveredUserNotificationEvents(
				userId, deliveryType, delivered, actionRequired, start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getDeliveredUserNotificationEvents(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.UserNotificationEvent>
					orderByComparator) {

		return _userNotificationEventLocalService.
			getDeliveredUserNotificationEvents(
				userId, deliveryType, delivered, actionRequired, start, end,
				orderByComparator);
	}

	@Override
	public int getDeliveredUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return _userNotificationEventLocalService.
			getDeliveredUserNotificationEventsCount(
				userId, deliveryType, delivered, actionRequired);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userNotificationEventLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userNotificationEventLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getTypeNotificationEvents(String type) {

		return _userNotificationEventLocalService.getTypeNotificationEvents(
			type);
	}

	/**
	 * Returns the user notification event with the primary key.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event
	 * @throws PortalException if a user notification event with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			getUserNotificationEvent(long userNotificationEventId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.getUserNotificationEvent(
			userNotificationEventId);
	}

	/**
	 * Returns the user notification event with the matching UUID and company.
	 *
	 * @param uuid the user notification event's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user notification event
	 * @throws PortalException if a matching user notification event could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			getUserNotificationEventByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.
			getUserNotificationEventByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getUserNotificationEvents(int start, int end) {

		return _userNotificationEventLocalService.getUserNotificationEvents(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getUserNotificationEvents(long userId) {

		return _userNotificationEventLocalService.getUserNotificationEvents(
			userId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getUserNotificationEvents(long userId, int start, int end) {

		return _userNotificationEventLocalService.getUserNotificationEvents(
			userId, start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getUserNotificationEvents(
			long userId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.UserNotificationEvent>
					orderByComparator) {

		return _userNotificationEventLocalService.getUserNotificationEvents(
			userId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		getUserNotificationEvents(
			long userId, String type, long timestamp, boolean delivered) {

		return _userNotificationEventLocalService.getUserNotificationEvents(
			userId, type, timestamp, delivered);
	}

	/**
	 * Returns the number of user notification events.
	 *
	 * @return the number of user notification events
	 */
	@Override
	public int getUserNotificationEventsCount() {
		return _userNotificationEventLocalService.
			getUserNotificationEventsCount();
	}

	@Override
	public int getUserNotificationEventsCount(long userId) {
		return _userNotificationEventLocalService.
			getUserNotificationEventsCount(userId);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return _userNotificationEventLocalService.
			getUserNotificationEventsCount(
				userId, deliveryType, delivered, archived);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return _userNotificationEventLocalService.
			getUserNotificationEventsCount(
				userId, type, deliveryType, delivered, archived);
	}

	@Override
	public int getUserNotificationEventsCount(
		long userId, String type,
		java.util.Map<String, String> payloadParameters) {

		return _userNotificationEventLocalService.
			getUserNotificationEventsCount(userId, type, payloadParameters);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			sendUserNotificationEvents(
				long userId, String portletId, int deliveryType,
				boolean delivered, boolean actionRequired,
				com.liferay.portal.kernel.json.JSONObject
					notificationEventJSONObject)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.sendUserNotificationEvents(
			userId, portletId, deliveryType, delivered, actionRequired,
			notificationEventJSONObject);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			sendUserNotificationEvents(
				long userId, String portletId, int deliveryType,
				boolean actionRequired,
				com.liferay.portal.kernel.json.JSONObject
					notificationEventJSONObject)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.sendUserNotificationEvents(
			userId, portletId, deliveryType, actionRequired,
			notificationEventJSONObject);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
			sendUserNotificationEvents(
				long userId, String portletId, int deliveryType,
				com.liferay.portal.kernel.json.JSONObject
					notificationEventJSONObject)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationEventLocalService.sendUserNotificationEvents(
			userId, portletId, deliveryType, notificationEventJSONObject);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
		updateUserNotificationEvent(
			String uuid, long companyId, boolean archive) {

		return _userNotificationEventLocalService.updateUserNotificationEvent(
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
	@Override
	public com.liferay.portal.kernel.model.UserNotificationEvent
		updateUserNotificationEvent(
			com.liferay.portal.kernel.model.UserNotificationEvent
				userNotificationEvent) {

		return _userNotificationEventLocalService.updateUserNotificationEvent(
			userNotificationEvent);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserNotificationEvent>
		updateUserNotificationEvents(
			java.util.Collection<String> uuids, long companyId,
			boolean archive) {

		return _userNotificationEventLocalService.updateUserNotificationEvents(
			uuids, companyId, archive);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userNotificationEventLocalService.getBasePersistence();
	}

	@Override
	public UserNotificationEventLocalService getWrappedService() {
		return _userNotificationEventLocalService;
	}

	@Override
	public void setWrappedService(
		UserNotificationEventLocalService userNotificationEventLocalService) {

		_userNotificationEventLocalService = userNotificationEventLocalService;
	}

	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-115692072
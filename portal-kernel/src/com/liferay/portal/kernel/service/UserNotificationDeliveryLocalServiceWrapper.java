/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link UserNotificationDeliveryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserNotificationDeliveryLocalService
 * @generated
 */
public class UserNotificationDeliveryLocalServiceWrapper
	implements ServiceWrapper<UserNotificationDeliveryLocalService>,
			   UserNotificationDeliveryLocalService {

	public UserNotificationDeliveryLocalServiceWrapper() {
		this(null);
	}

	public UserNotificationDeliveryLocalServiceWrapper(
		UserNotificationDeliveryLocalService
			userNotificationDeliveryLocalService) {

		_userNotificationDeliveryLocalService =
			userNotificationDeliveryLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
			addUserNotificationDelivery(
				long userId, String portletId, long classNameId,
				int notificationType, int deliveryType, boolean deliver)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationDeliveryLocalService.
			addUserNotificationDelivery(
				userId, portletId, classNameId, notificationType, deliveryType,
				deliver);
	}

	/**
	 * Adds the user notification delivery to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationDeliveryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationDelivery the user notification delivery
	 * @return the user notification delivery that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
		addUserNotificationDelivery(
			com.liferay.portal.kernel.model.UserNotificationDelivery
				userNotificationDelivery) {

		return _userNotificationDeliveryLocalService.
			addUserNotificationDelivery(userNotificationDelivery);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationDeliveryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new user notification delivery with the primary key. Does not add the user notification delivery to the database.
	 *
	 * @param userNotificationDeliveryId the primary key for the new user notification delivery
	 * @return the new user notification delivery
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
		createUserNotificationDelivery(long userNotificationDeliveryId) {

		return _userNotificationDeliveryLocalService.
			createUserNotificationDelivery(userNotificationDeliveryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationDeliveryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteUserNotificationDeliveries(long userId) {
		_userNotificationDeliveryLocalService.deleteUserNotificationDeliveries(
			userId);
	}

	/**
	 * Deletes the user notification delivery with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationDeliveryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationDeliveryId the primary key of the user notification delivery
	 * @return the user notification delivery that was removed
	 * @throws PortalException if a user notification delivery with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
			deleteUserNotificationDelivery(long userNotificationDeliveryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationDeliveryLocalService.
			deleteUserNotificationDelivery(userNotificationDeliveryId);
	}

	/**
	 * Deletes the user notification delivery from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationDeliveryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationDelivery the user notification delivery
	 * @return the user notification delivery that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
		deleteUserNotificationDelivery(
			com.liferay.portal.kernel.model.UserNotificationDelivery
				userNotificationDelivery) {

		return _userNotificationDeliveryLocalService.
			deleteUserNotificationDelivery(userNotificationDelivery);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userNotificationDeliveryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userNotificationDeliveryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userNotificationDeliveryLocalService.dynamicQuery();
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

		return _userNotificationDeliveryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationDeliveryModelImpl</code>.
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

		return _userNotificationDeliveryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationDeliveryModelImpl</code>.
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

		return _userNotificationDeliveryLocalService.dynamicQuery(
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

		return _userNotificationDeliveryLocalService.dynamicQueryCount(
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

		return _userNotificationDeliveryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
		fetchUserNotificationDelivery(long userNotificationDeliveryId) {

		return _userNotificationDeliveryLocalService.
			fetchUserNotificationDelivery(userNotificationDeliveryId);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
		fetchUserNotificationDelivery(
			long userId, String portletId, long classNameId,
			int notificationType, int deliveryType) {

		return _userNotificationDeliveryLocalService.
			fetchUserNotificationDelivery(
				userId, portletId, classNameId, notificationType, deliveryType);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userNotificationDeliveryLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userNotificationDeliveryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userNotificationDeliveryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationDeliveryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns a range of all the user notification deliveries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationDeliveryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user notification deliveries
	 * @param end the upper bound of the range of user notification deliveries (not inclusive)
	 * @return the range of user notification deliveries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.kernel.model.UserNotificationDelivery>
			getUserNotificationDeliveries(int start, int end) {

		return _userNotificationDeliveryLocalService.
			getUserNotificationDeliveries(start, end);
	}

	/**
	 * Returns the number of user notification deliveries.
	 *
	 * @return the number of user notification deliveries
	 */
	@Override
	public int getUserNotificationDeliveriesCount() {
		return _userNotificationDeliveryLocalService.
			getUserNotificationDeliveriesCount();
	}

	/**
	 * Returns the user notification delivery with the primary key.
	 *
	 * @param userNotificationDeliveryId the primary key of the user notification delivery
	 * @return the user notification delivery
	 * @throws PortalException if a user notification delivery with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
			getUserNotificationDelivery(long userNotificationDeliveryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationDeliveryLocalService.
			getUserNotificationDelivery(userNotificationDeliveryId);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
			getUserNotificationDelivery(
				long userId, String portletId, long classNameId,
				int notificationType, int deliveryType, boolean deliver)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userNotificationDeliveryLocalService.
			getUserNotificationDelivery(
				userId, portletId, classNameId, notificationType, deliveryType,
				deliver);
	}

	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
		updateUserNotificationDelivery(
			long userNotificationDeliveryId, boolean deliver) {

		return _userNotificationDeliveryLocalService.
			updateUserNotificationDelivery(userNotificationDeliveryId, deliver);
	}

	/**
	 * Updates the user notification delivery in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationDeliveryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationDelivery the user notification delivery
	 * @return the user notification delivery that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.UserNotificationDelivery
		updateUserNotificationDelivery(
			com.liferay.portal.kernel.model.UserNotificationDelivery
				userNotificationDelivery) {

		return _userNotificationDeliveryLocalService.
			updateUserNotificationDelivery(userNotificationDelivery);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userNotificationDeliveryLocalService.getBasePersistence();
	}

	@Override
	public UserNotificationDeliveryLocalService getWrappedService() {
		return _userNotificationDeliveryLocalService;
	}

	@Override
	public void setWrappedService(
		UserNotificationDeliveryLocalService
			userNotificationDeliveryLocalService) {

		_userNotificationDeliveryLocalService =
			userNotificationDeliveryLocalService;
	}

	private UserNotificationDeliveryLocalService
		_userNotificationDeliveryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:460150652
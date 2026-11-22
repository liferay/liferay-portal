/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PushNotificationsDeviceLocalService}.
 *
 * @author Bruno Farache
 * @see PushNotificationsDeviceLocalService
 * @generated
 */
public class PushNotificationsDeviceLocalServiceWrapper
	implements PushNotificationsDeviceLocalService,
			   ServiceWrapper<PushNotificationsDeviceLocalService> {

	public PushNotificationsDeviceLocalServiceWrapper() {
		this(null);
	}

	public PushNotificationsDeviceLocalServiceWrapper(
		PushNotificationsDeviceLocalService
			pushNotificationsDeviceLocalService) {

		_pushNotificationsDeviceLocalService =
			pushNotificationsDeviceLocalService;
	}

	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
			addPushNotificationsDevice(
				long userId, String platform, String token)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _pushNotificationsDeviceLocalService.addPushNotificationsDevice(
			userId, platform, token);
	}

	/**
	 * Adds the push notifications device to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PushNotificationsDeviceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param pushNotificationsDevice the push notifications device
	 * @return the push notifications device that was added
	 */
	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
		addPushNotificationsDevice(
			com.liferay.push.notifications.model.PushNotificationsDevice
				pushNotificationsDevice) {

		return _pushNotificationsDeviceLocalService.addPushNotificationsDevice(
			pushNotificationsDevice);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _pushNotificationsDeviceLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new push notifications device with the primary key. Does not add the push notifications device to the database.
	 *
	 * @param pushNotificationsDeviceId the primary key for the new push notifications device
	 * @return the new push notifications device
	 */
	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
		createPushNotificationsDevice(long pushNotificationsDeviceId) {

		return _pushNotificationsDeviceLocalService.
			createPushNotificationsDevice(pushNotificationsDeviceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _pushNotificationsDeviceLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the push notifications device with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PushNotificationsDeviceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param pushNotificationsDeviceId the primary key of the push notifications device
	 * @return the push notifications device that was removed
	 * @throws PortalException if a push notifications device with the primary key could not be found
	 */
	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
			deletePushNotificationsDevice(long pushNotificationsDeviceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _pushNotificationsDeviceLocalService.
			deletePushNotificationsDevice(pushNotificationsDeviceId);
	}

	/**
	 * Deletes the push notifications device from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PushNotificationsDeviceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param pushNotificationsDevice the push notifications device
	 * @return the push notifications device that was removed
	 */
	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
		deletePushNotificationsDevice(
			com.liferay.push.notifications.model.PushNotificationsDevice
				pushNotificationsDevice) {

		return _pushNotificationsDeviceLocalService.
			deletePushNotificationsDevice(pushNotificationsDevice);
	}

	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
			deletePushNotificationsDevice(String token)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _pushNotificationsDeviceLocalService.
			deletePushNotificationsDevice(token);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _pushNotificationsDeviceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _pushNotificationsDeviceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _pushNotificationsDeviceLocalService.dynamicQuery();
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

		return _pushNotificationsDeviceLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.push.notifications.model.impl.PushNotificationsDeviceModelImpl</code>.
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

		return _pushNotificationsDeviceLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.push.notifications.model.impl.PushNotificationsDeviceModelImpl</code>.
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

		return _pushNotificationsDeviceLocalService.dynamicQuery(
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

		return _pushNotificationsDeviceLocalService.dynamicQueryCount(
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

		return _pushNotificationsDeviceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
		fetchPushNotificationsDevice(long pushNotificationsDeviceId) {

		return _pushNotificationsDeviceLocalService.
			fetchPushNotificationsDevice(pushNotificationsDeviceId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _pushNotificationsDeviceLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _pushNotificationsDeviceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _pushNotificationsDeviceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _pushNotificationsDeviceLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the push notifications device with the primary key.
	 *
	 * @param pushNotificationsDeviceId the primary key of the push notifications device
	 * @return the push notifications device
	 * @throws PortalException if a push notifications device with the primary key could not be found
	 */
	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
			getPushNotificationsDevice(long pushNotificationsDeviceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _pushNotificationsDeviceLocalService.getPushNotificationsDevice(
			pushNotificationsDeviceId);
	}

	/**
	 * Returns a range of all the push notifications devices.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.push.notifications.model.impl.PushNotificationsDeviceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of push notifications devices
	 * @param end the upper bound of the range of push notifications devices (not inclusive)
	 * @return the range of push notifications devices
	 */
	@Override
	public java.util.List
		<com.liferay.push.notifications.model.PushNotificationsDevice>
			getPushNotificationsDevices(int start, int end) {

		return _pushNotificationsDeviceLocalService.getPushNotificationsDevices(
			start, end);
	}

	@Override
	public java.util.List
		<com.liferay.push.notifications.model.PushNotificationsDevice>
			getPushNotificationsDevices(
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.push.notifications.model.
						PushNotificationsDevice> orderByComparator) {

		return _pushNotificationsDeviceLocalService.getPushNotificationsDevices(
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of push notifications devices.
	 *
	 * @return the number of push notifications devices
	 */
	@Override
	public int getPushNotificationsDevicesCount() {
		return _pushNotificationsDeviceLocalService.
			getPushNotificationsDevicesCount();
	}

	@Override
	public void sendPushNotification(
			long[] toUserIds,
			com.liferay.portal.kernel.json.JSONObject payloadJSONObject)
		throws com.liferay.portal.kernel.exception.PortalException {

		_pushNotificationsDeviceLocalService.sendPushNotification(
			toUserIds, payloadJSONObject);
	}

	@Override
	public void sendPushNotification(
			String platform, java.util.List<String> tokens,
			com.liferay.portal.kernel.json.JSONObject payloadJSONObject)
		throws com.liferay.portal.kernel.exception.PortalException {

		_pushNotificationsDeviceLocalService.sendPushNotification(
			platform, tokens, payloadJSONObject);
	}

	/**
	 * Updates the push notifications device in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PushNotificationsDeviceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param pushNotificationsDevice the push notifications device
	 * @return the push notifications device that was updated
	 */
	@Override
	public com.liferay.push.notifications.model.PushNotificationsDevice
		updatePushNotificationsDevice(
			com.liferay.push.notifications.model.PushNotificationsDevice
				pushNotificationsDevice) {

		return _pushNotificationsDeviceLocalService.
			updatePushNotificationsDevice(pushNotificationsDevice);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _pushNotificationsDeviceLocalService.getBasePersistence();
	}

	@Override
	public PushNotificationsDeviceLocalService getWrappedService() {
		return _pushNotificationsDeviceLocalService;
	}

	@Override
	public void setWrappedService(
		PushNotificationsDeviceLocalService
			pushNotificationsDeviceLocalService) {

		_pushNotificationsDeviceLocalService =
			pushNotificationsDeviceLocalService;
	}

	private PushNotificationsDeviceLocalService
		_pushNotificationsDeviceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1241995209
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.push.notifications.model.PushNotificationsDevice;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PushNotificationsDevice. This utility wraps
 * <code>com.liferay.push.notifications.service.impl.PushNotificationsDeviceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Bruno Farache
 * @see PushNotificationsDeviceLocalService
 * @generated
 */
public class PushNotificationsDeviceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.push.notifications.service.impl.PushNotificationsDeviceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static PushNotificationsDevice addPushNotificationsDevice(
			long userId, String platform, String token)
		throws PortalException {

		return getService().addPushNotificationsDevice(userId, platform, token);
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
	public static PushNotificationsDevice addPushNotificationsDevice(
		PushNotificationsDevice pushNotificationsDevice) {

		return getService().addPushNotificationsDevice(pushNotificationsDevice);
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
	 * Creates a new push notifications device with the primary key. Does not add the push notifications device to the database.
	 *
	 * @param pushNotificationsDeviceId the primary key for the new push notifications device
	 * @return the new push notifications device
	 */
	public static PushNotificationsDevice createPushNotificationsDevice(
		long pushNotificationsDeviceId) {

		return getService().createPushNotificationsDevice(
			pushNotificationsDeviceId);
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
	public static PushNotificationsDevice deletePushNotificationsDevice(
			long pushNotificationsDeviceId)
		throws PortalException {

		return getService().deletePushNotificationsDevice(
			pushNotificationsDeviceId);
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
	public static PushNotificationsDevice deletePushNotificationsDevice(
		PushNotificationsDevice pushNotificationsDevice) {

		return getService().deletePushNotificationsDevice(
			pushNotificationsDevice);
	}

	public static PushNotificationsDevice deletePushNotificationsDevice(
			String token)
		throws PortalException {

		return getService().deletePushNotificationsDevice(token);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.push.notifications.model.impl.PushNotificationsDeviceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.push.notifications.model.impl.PushNotificationsDeviceModelImpl</code>.
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

	public static PushNotificationsDevice fetchPushNotificationsDevice(
		long pushNotificationsDeviceId) {

		return getService().fetchPushNotificationsDevice(
			pushNotificationsDeviceId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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

	/**
	 * Returns the push notifications device with the primary key.
	 *
	 * @param pushNotificationsDeviceId the primary key of the push notifications device
	 * @return the push notifications device
	 * @throws PortalException if a push notifications device with the primary key could not be found
	 */
	public static PushNotificationsDevice getPushNotificationsDevice(
			long pushNotificationsDeviceId)
		throws PortalException {

		return getService().getPushNotificationsDevice(
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
	public static List<PushNotificationsDevice> getPushNotificationsDevices(
		int start, int end) {

		return getService().getPushNotificationsDevices(start, end);
	}

	public static List<PushNotificationsDevice> getPushNotificationsDevices(
		int start, int end,
		OrderByComparator<PushNotificationsDevice> orderByComparator) {

		return getService().getPushNotificationsDevices(
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of push notifications devices.
	 *
	 * @return the number of push notifications devices
	 */
	public static int getPushNotificationsDevicesCount() {
		return getService().getPushNotificationsDevicesCount();
	}

	public static void sendPushNotification(
			long[] toUserIds,
			com.liferay.portal.kernel.json.JSONObject payloadJSONObject)
		throws PortalException {

		getService().sendPushNotification(toUserIds, payloadJSONObject);
	}

	public static void sendPushNotification(
			String platform, List<String> tokens,
			com.liferay.portal.kernel.json.JSONObject payloadJSONObject)
		throws PortalException {

		getService().sendPushNotification(platform, tokens, payloadJSONObject);
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
	public static PushNotificationsDevice updatePushNotificationsDevice(
		PushNotificationsDevice pushNotificationsDevice) {

		return getService().updatePushNotificationsDevice(
			pushNotificationsDevice);
	}

	public static PushNotificationsDeviceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<PushNotificationsDeviceLocalService>
		_serviceSnapshot = new Snapshot<>(
			PushNotificationsDeviceLocalServiceUtil.class,
			PushNotificationsDeviceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:367389541
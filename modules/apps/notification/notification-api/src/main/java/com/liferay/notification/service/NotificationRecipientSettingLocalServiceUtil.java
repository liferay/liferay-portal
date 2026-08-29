/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for NotificationRecipientSetting. This utility wraps
 * <code>com.liferay.notification.service.impl.NotificationRecipientSettingLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Gabriel Albuquerque
 * @see NotificationRecipientSettingLocalService
 * @generated
 */
public class NotificationRecipientSettingLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.notification.service.impl.NotificationRecipientSettingLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static NotificationRecipientSetting addNotificationRecipientSetting(
			long userId, long notificationRecipientId, String name,
			Object value)
		throws PortalException {

		return getService().addNotificationRecipientSetting(
			userId, notificationRecipientId, name, value);
	}

	/**
	 * Adds the notification recipient setting to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSetting the notification recipient setting
	 * @return the notification recipient setting that was added
	 */
	public static NotificationRecipientSetting addNotificationRecipientSetting(
		NotificationRecipientSetting notificationRecipientSetting) {

		return getService().addNotificationRecipientSetting(
			notificationRecipientSetting);
	}

	/**
	 * Creates a new notification recipient setting with the primary key. Does not add the notification recipient setting to the database.
	 *
	 * @param notificationRecipientSettingId the primary key for the new notification recipient setting
	 * @return the new notification recipient setting
	 */
	public static NotificationRecipientSetting
		createNotificationRecipientSetting(
			long notificationRecipientSettingId) {

		return getService().createNotificationRecipientSetting(
			notificationRecipientSettingId);
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
	 * Deletes the notification recipient setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting that was removed
	 * @throws PortalException if a notification recipient setting with the primary key could not be found
	 */
	public static NotificationRecipientSetting
			deleteNotificationRecipientSetting(
				long notificationRecipientSettingId)
		throws PortalException {

		return getService().deleteNotificationRecipientSetting(
			notificationRecipientSettingId);
	}

	/**
	 * Deletes the notification recipient setting from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSetting the notification recipient setting
	 * @return the notification recipient setting that was removed
	 */
	public static NotificationRecipientSetting
		deleteNotificationRecipientSetting(
			NotificationRecipientSetting notificationRecipientSetting) {

		return getService().deleteNotificationRecipientSetting(
			notificationRecipientSetting);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientSettingModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientSettingModelImpl</code>.
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

	public static NotificationRecipientSetting
		fetchNotificationRecipientSetting(long notificationRecipientSettingId) {

		return getService().fetchNotificationRecipientSetting(
			notificationRecipientSettingId);
	}

	public static NotificationRecipientSetting
		fetchNotificationRecipientSetting(
			long notificationRecipientId, String name) {

		return getService().fetchNotificationRecipientSetting(
			notificationRecipientId, name);
	}

	/**
	 * Returns the notification recipient setting with the matching UUID and company.
	 *
	 * @param uuid the notification recipient setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient setting, or <code>null</code> if a matching notification recipient setting could not be found
	 */
	public static NotificationRecipientSetting
		fetchNotificationRecipientSettingByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchNotificationRecipientSettingByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the notification recipient setting with the primary key.
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting
	 * @throws PortalException if a notification recipient setting with the primary key could not be found
	 */
	public static NotificationRecipientSetting getNotificationRecipientSetting(
			long notificationRecipientSettingId)
		throws PortalException {

		return getService().getNotificationRecipientSetting(
			notificationRecipientSettingId);
	}

	/**
	 * Returns the notification recipient setting with the matching UUID and company.
	 *
	 * @param uuid the notification recipient setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient setting
	 * @throws PortalException if a matching notification recipient setting could not be found
	 */
	public static NotificationRecipientSetting
			getNotificationRecipientSettingByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getNotificationRecipientSettingByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the notification recipient settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of notification recipient settings
	 * @param end the upper bound of the range of notification recipient settings (not inclusive)
	 * @return the range of notification recipient settings
	 */
	public static List<NotificationRecipientSetting>
		getNotificationRecipientSettings(int start, int end) {

		return getService().getNotificationRecipientSettings(start, end);
	}

	public static List<NotificationRecipientSetting>
		getNotificationRecipientSettings(long notificationRecipientId) {

		return getService().getNotificationRecipientSettings(
			notificationRecipientId);
	}

	/**
	 * Returns the number of notification recipient settings.
	 *
	 * @return the number of notification recipient settings
	 */
	public static int getNotificationRecipientSettingsCount() {
		return getService().getNotificationRecipientSettingsCount();
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

	public static NotificationRecipientSetting
		updateNotificationRecipientSetting(
			long notificationRecipientId, String name, Object value) {

		return getService().updateNotificationRecipientSetting(
			notificationRecipientId, name, value);
	}

	/**
	 * Updates the notification recipient setting in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSetting the notification recipient setting
	 * @return the notification recipient setting that was updated
	 */
	public static NotificationRecipientSetting
		updateNotificationRecipientSetting(
			NotificationRecipientSetting notificationRecipientSetting) {

		return getService().updateNotificationRecipientSetting(
			notificationRecipientSetting);
	}

	public static NotificationRecipientSettingLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<NotificationRecipientSettingLocalService>
		_serviceSnapshot = new Snapshot<>(
			NotificationRecipientSettingLocalServiceUtil.class,
			NotificationRecipientSettingLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-631969434
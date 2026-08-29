/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link NotificationRecipientSettingLocalService}.
 *
 * @author Gabriel Albuquerque
 * @see NotificationRecipientSettingLocalService
 * @generated
 */
public class NotificationRecipientSettingLocalServiceWrapper
	implements NotificationRecipientSettingLocalService,
			   ServiceWrapper<NotificationRecipientSettingLocalService> {

	public NotificationRecipientSettingLocalServiceWrapper() {
		this(null);
	}

	public NotificationRecipientSettingLocalServiceWrapper(
		NotificationRecipientSettingLocalService
			notificationRecipientSettingLocalService) {

		_notificationRecipientSettingLocalService =
			notificationRecipientSettingLocalService;
	}

	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
			addNotificationRecipientSetting(
				long userId, long notificationRecipientId, String name,
				Object value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(
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
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		addNotificationRecipientSetting(
			com.liferay.notification.model.NotificationRecipientSetting
				notificationRecipientSetting) {

		return _notificationRecipientSettingLocalService.
			addNotificationRecipientSetting(notificationRecipientSetting);
	}

	/**
	 * Creates a new notification recipient setting with the primary key. Does not add the notification recipient setting to the database.
	 *
	 * @param notificationRecipientSettingId the primary key for the new notification recipient setting
	 * @return the new notification recipient setting
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		createNotificationRecipientSetting(
			long notificationRecipientSettingId) {

		return _notificationRecipientSettingLocalService.
			createNotificationRecipientSetting(notificationRecipientSettingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientSettingLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
			deleteNotificationRecipientSetting(
				long notificationRecipientSettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientSettingLocalService.
			deleteNotificationRecipientSetting(notificationRecipientSettingId);
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
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		deleteNotificationRecipientSetting(
			com.liferay.notification.model.NotificationRecipientSetting
				notificationRecipientSetting) {

		return _notificationRecipientSettingLocalService.
			deleteNotificationRecipientSetting(notificationRecipientSetting);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientSettingLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _notificationRecipientSettingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _notificationRecipientSettingLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _notificationRecipientSettingLocalService.dynamicQuery();
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

		return _notificationRecipientSettingLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _notificationRecipientSettingLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _notificationRecipientSettingLocalService.dynamicQuery(
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

		return _notificationRecipientSettingLocalService.dynamicQueryCount(
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

		return _notificationRecipientSettingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		fetchNotificationRecipientSetting(long notificationRecipientSettingId) {

		return _notificationRecipientSettingLocalService.
			fetchNotificationRecipientSetting(notificationRecipientSettingId);
	}

	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		fetchNotificationRecipientSetting(
			long notificationRecipientId, String name) {

		return _notificationRecipientSettingLocalService.
			fetchNotificationRecipientSetting(notificationRecipientId, name);
	}

	/**
	 * Returns the notification recipient setting with the matching UUID and company.
	 *
	 * @param uuid the notification recipient setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient setting, or <code>null</code> if a matching notification recipient setting could not be found
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		fetchNotificationRecipientSettingByUuidAndCompanyId(
			String uuid, long companyId) {

		return _notificationRecipientSettingLocalService.
			fetchNotificationRecipientSettingByUuidAndCompanyId(
				uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _notificationRecipientSettingLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _notificationRecipientSettingLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _notificationRecipientSettingLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the notification recipient setting with the primary key.
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting
	 * @throws PortalException if a notification recipient setting with the primary key could not be found
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
			getNotificationRecipientSetting(long notificationRecipientSettingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientSettingLocalService.
			getNotificationRecipientSetting(notificationRecipientSettingId);
	}

	/**
	 * Returns the notification recipient setting with the matching UUID and company.
	 *
	 * @param uuid the notification recipient setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient setting
	 * @throws PortalException if a matching notification recipient setting could not be found
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
			getNotificationRecipientSettingByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientSettingLocalService.
			getNotificationRecipientSettingByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List
		<com.liferay.notification.model.NotificationRecipientSetting>
			getNotificationRecipientSettings(int start, int end) {

		return _notificationRecipientSettingLocalService.
			getNotificationRecipientSettings(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.notification.model.NotificationRecipientSetting>
			getNotificationRecipientSettings(long notificationRecipientId) {

		return _notificationRecipientSettingLocalService.
			getNotificationRecipientSettings(notificationRecipientId);
	}

	/**
	 * Returns the number of notification recipient settings.
	 *
	 * @return the number of notification recipient settings
	 */
	@Override
	public int getNotificationRecipientSettingsCount() {
		return _notificationRecipientSettingLocalService.
			getNotificationRecipientSettingsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _notificationRecipientSettingLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientSettingLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		updateNotificationRecipientSetting(
			long notificationRecipientId, String name, Object value) {

		return _notificationRecipientSettingLocalService.
			updateNotificationRecipientSetting(
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
	@Override
	public com.liferay.notification.model.NotificationRecipientSetting
		updateNotificationRecipientSetting(
			com.liferay.notification.model.NotificationRecipientSetting
				notificationRecipientSetting) {

		return _notificationRecipientSettingLocalService.
			updateNotificationRecipientSetting(notificationRecipientSetting);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _notificationRecipientSettingLocalService.getBasePersistence();
	}

	@Override
	public NotificationRecipientSettingLocalService getWrappedService() {
		return _notificationRecipientSettingLocalService;
	}

	@Override
	public void setWrappedService(
		NotificationRecipientSettingLocalService
			notificationRecipientSettingLocalService) {

		_notificationRecipientSettingLocalService =
			notificationRecipientSettingLocalService;
	}

	private NotificationRecipientSettingLocalService
		_notificationRecipientSettingLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-663938303
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link NotificationRecipientLocalService}.
 *
 * @author Gabriel Albuquerque
 * @see NotificationRecipientLocalService
 * @generated
 */
public class NotificationRecipientLocalServiceWrapper
	implements NotificationRecipientLocalService,
			   ServiceWrapper<NotificationRecipientLocalService> {

	public NotificationRecipientLocalServiceWrapper() {
		this(null);
	}

	public NotificationRecipientLocalServiceWrapper(
		NotificationRecipientLocalService notificationRecipientLocalService) {

		_notificationRecipientLocalService = notificationRecipientLocalService;
	}

	@Override
	public com.liferay.notification.model.NotificationRecipient
			addNotificationRecipient(
				long userId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientLocalService.addNotificationRecipient(
			userId, classNameId, classPK);
	}

	/**
	 * Adds the notification recipient to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipient the notification recipient
	 * @return the notification recipient that was added
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
		addNotificationRecipient(
			com.liferay.notification.model.NotificationRecipient
				notificationRecipient) {

		return _notificationRecipientLocalService.addNotificationRecipient(
			notificationRecipient);
	}

	/**
	 * Creates a new notification recipient with the primary key. Does not add the notification recipient to the database.
	 *
	 * @param notificationRecipientId the primary key for the new notification recipient
	 * @return the new notification recipient
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
		createNotificationRecipient(long notificationRecipientId) {

		return _notificationRecipientLocalService.createNotificationRecipient(
			notificationRecipientId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the notification recipient with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientId the primary key of the notification recipient
	 * @return the notification recipient that was removed
	 * @throws PortalException if a notification recipient with the primary key could not be found
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
			deleteNotificationRecipient(long notificationRecipientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientLocalService.deleteNotificationRecipient(
			notificationRecipientId);
	}

	/**
	 * Deletes the notification recipient from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipient the notification recipient
	 * @return the notification recipient that was removed
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
		deleteNotificationRecipient(
			com.liferay.notification.model.NotificationRecipient
				notificationRecipient) {

		return _notificationRecipientLocalService.deleteNotificationRecipient(
			notificationRecipient);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _notificationRecipientLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _notificationRecipientLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _notificationRecipientLocalService.dynamicQuery();
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

		return _notificationRecipientLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientModelImpl</code>.
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

		return _notificationRecipientLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientModelImpl</code>.
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

		return _notificationRecipientLocalService.dynamicQuery(
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

		return _notificationRecipientLocalService.dynamicQueryCount(
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

		return _notificationRecipientLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.notification.model.NotificationRecipient
		fetchNotificationRecipient(long notificationRecipientId) {

		return _notificationRecipientLocalService.fetchNotificationRecipient(
			notificationRecipientId);
	}

	/**
	 * Returns the notification recipient with the matching UUID and company.
	 *
	 * @param uuid the notification recipient's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient, or <code>null</code> if a matching notification recipient could not be found
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
		fetchNotificationRecipientByUuidAndCompanyId(
			String uuid, long companyId) {

		return _notificationRecipientLocalService.
			fetchNotificationRecipientByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _notificationRecipientLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _notificationRecipientLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _notificationRecipientLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the notification recipient with the primary key.
	 *
	 * @param notificationRecipientId the primary key of the notification recipient
	 * @return the notification recipient
	 * @throws PortalException if a notification recipient with the primary key could not be found
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
			getNotificationRecipient(long notificationRecipientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientLocalService.getNotificationRecipient(
			notificationRecipientId);
	}

	@Override
	public com.liferay.notification.model.NotificationRecipient
		getNotificationRecipientByClassPK(long classPK) {

		return _notificationRecipientLocalService.
			getNotificationRecipientByClassPK(classPK);
	}

	/**
	 * Returns the notification recipient with the matching UUID and company.
	 *
	 * @param uuid the notification recipient's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient
	 * @throws PortalException if a matching notification recipient could not be found
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
			getNotificationRecipientByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientLocalService.
			getNotificationRecipientByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the notification recipients.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of notification recipients
	 * @param end the upper bound of the range of notification recipients (not inclusive)
	 * @return the range of notification recipients
	 */
	@Override
	public java.util.List<com.liferay.notification.model.NotificationRecipient>
		getNotificationRecipients(int start, int end) {

		return _notificationRecipientLocalService.getNotificationRecipients(
			start, end);
	}

	/**
	 * Returns the number of notification recipients.
	 *
	 * @return the number of notification recipients
	 */
	@Override
	public int getNotificationRecipientsCount() {
		return _notificationRecipientLocalService.
			getNotificationRecipientsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _notificationRecipientLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notificationRecipientLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the notification recipient in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipient the notification recipient
	 * @return the notification recipient that was updated
	 */
	@Override
	public com.liferay.notification.model.NotificationRecipient
		updateNotificationRecipient(
			com.liferay.notification.model.NotificationRecipient
				notificationRecipient) {

		return _notificationRecipientLocalService.updateNotificationRecipient(
			notificationRecipient);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _notificationRecipientLocalService.getBasePersistence();
	}

	@Override
	public NotificationRecipientLocalService getWrappedService() {
		return _notificationRecipientLocalService;
	}

	@Override
	public void setWrappedService(
		NotificationRecipientLocalService notificationRecipientLocalService) {

		_notificationRecipientLocalService = notificationRecipientLocalService;
	}

	private NotificationRecipientLocalService
		_notificationRecipientLocalService;

}
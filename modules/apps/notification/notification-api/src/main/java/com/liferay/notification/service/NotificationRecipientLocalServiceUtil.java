/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.notification.model.NotificationRecipient;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for NotificationRecipient. This utility wraps
 * <code>com.liferay.notification.service.impl.NotificationRecipientLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Gabriel Albuquerque
 * @see NotificationRecipientLocalService
 * @generated
 */
public class NotificationRecipientLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.notification.service.impl.NotificationRecipientLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static NotificationRecipient addNotificationRecipient(
			long userId, long classNameId, long classPK)
		throws PortalException {

		return getService().addNotificationRecipient(
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
	public static NotificationRecipient addNotificationRecipient(
		NotificationRecipient notificationRecipient) {

		return getService().addNotificationRecipient(notificationRecipient);
	}

	/**
	 * Creates a new notification recipient with the primary key. Does not add the notification recipient to the database.
	 *
	 * @param notificationRecipientId the primary key for the new notification recipient
	 * @return the new notification recipient
	 */
	public static NotificationRecipient createNotificationRecipient(
		long notificationRecipientId) {

		return getService().createNotificationRecipient(
			notificationRecipientId);
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
	public static NotificationRecipient deleteNotificationRecipient(
			long notificationRecipientId)
		throws PortalException {

		return getService().deleteNotificationRecipient(
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
	public static NotificationRecipient deleteNotificationRecipient(
		NotificationRecipient notificationRecipient) {

		return getService().deleteNotificationRecipient(notificationRecipient);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientModelImpl</code>.
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

	public static NotificationRecipient fetchNotificationRecipient(
		long notificationRecipientId) {

		return getService().fetchNotificationRecipient(notificationRecipientId);
	}

	/**
	 * Returns the notification recipient with the matching UUID and company.
	 *
	 * @param uuid the notification recipient's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient, or <code>null</code> if a matching notification recipient could not be found
	 */
	public static NotificationRecipient
		fetchNotificationRecipientByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchNotificationRecipientByUuidAndCompanyId(
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
	 * Returns the notification recipient with the primary key.
	 *
	 * @param notificationRecipientId the primary key of the notification recipient
	 * @return the notification recipient
	 * @throws PortalException if a notification recipient with the primary key could not be found
	 */
	public static NotificationRecipient getNotificationRecipient(
			long notificationRecipientId)
		throws PortalException {

		return getService().getNotificationRecipient(notificationRecipientId);
	}

	public static NotificationRecipient getNotificationRecipientByClassPK(
		long classPK) {

		return getService().getNotificationRecipientByClassPK(classPK);
	}

	/**
	 * Returns the notification recipient with the matching UUID and company.
	 *
	 * @param uuid the notification recipient's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient
	 * @throws PortalException if a matching notification recipient could not be found
	 */
	public static NotificationRecipient
			getNotificationRecipientByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getNotificationRecipientByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<NotificationRecipient> getNotificationRecipients(
		int start, int end) {

		return getService().getNotificationRecipients(start, end);
	}

	/**
	 * Returns the number of notification recipients.
	 *
	 * @return the number of notification recipients
	 */
	public static int getNotificationRecipientsCount() {
		return getService().getNotificationRecipientsCount();
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
	 * Updates the notification recipient in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipient the notification recipient
	 * @return the notification recipient that was updated
	 */
	public static NotificationRecipient updateNotificationRecipient(
		NotificationRecipient notificationRecipient) {

		return getService().updateNotificationRecipient(notificationRecipient);
	}

	public static NotificationRecipientLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<NotificationRecipientLocalService>
		_serviceSnapshot = new Snapshot<>(
			NotificationRecipientLocalServiceUtil.class,
			NotificationRecipientLocalService.class);

}
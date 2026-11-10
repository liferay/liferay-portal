/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceNotificationQueueEntry. This utility wraps
 * <code>com.liferay.commerce.notification.service.impl.CommerceNotificationQueueEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationQueueEntryLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceNotificationQueueEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.notification.service.impl.CommerceNotificationQueueEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce notification queue entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntry the commerce notification queue entry
	 * @return the commerce notification queue entry that was added
	 */
	public static CommerceNotificationQueueEntry
		addCommerceNotificationQueueEntry(
			CommerceNotificationQueueEntry commerceNotificationQueueEntry) {

		return getService().addCommerceNotificationQueueEntry(
			commerceNotificationQueueEntry);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), this method will be replaced
	 */
	@Deprecated
	public static CommerceNotificationQueueEntry
			addCommerceNotificationQueueEntry(
				long userId, long groupId, long commerceNotificationTemplateId,
				String from, String fromName, String to, String toName,
				String cc, String bcc, String subject, String body,
				double priority)
		throws PortalException {

		return getService().addCommerceNotificationQueueEntry(
			userId, groupId, commerceNotificationTemplateId, from, fromName, to,
			toName, cc, bcc, subject, body, priority);
	}

	public static CommerceNotificationQueueEntry
			addCommerceNotificationQueueEntry(
				long userId, long groupId, String className, long classPK,
				long commerceNotificationTemplateId, String from,
				String fromName, String to, String toName, String cc,
				String bcc, String subject, String body, double priority)
		throws PortalException {

		return getService().addCommerceNotificationQueueEntry(
			userId, groupId, className, classPK, commerceNotificationTemplateId,
			from, fromName, to, toName, cc, bcc, subject, body, priority);
	}

	/**
	 * Creates a new commerce notification queue entry with the primary key. Does not add the commerce notification queue entry to the database.
	 *
	 * @param commerceNotificationQueueEntryId the primary key for the new commerce notification queue entry
	 * @return the new commerce notification queue entry
	 */
	public static CommerceNotificationQueueEntry
		createCommerceNotificationQueueEntry(
			long commerceNotificationQueueEntryId) {

		return getService().createCommerceNotificationQueueEntry(
			commerceNotificationQueueEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static CommerceNotificationQueueEntry
			deleteCommerceNotificationQueue(
				CommerceNotificationQueueEntry commerceNotificationQueueEntry)
		throws PortalException {

		return getService().deleteCommerceNotificationQueue(
			commerceNotificationQueueEntry);
	}

	public static void deleteCommerceNotificationQueueEntries(
			java.util.Date sentDate)
		throws PortalException {

		getService().deleteCommerceNotificationQueueEntries(sentDate);
	}

	public static void deleteCommerceNotificationQueueEntries(long groupId)
		throws PortalException {

		getService().deleteCommerceNotificationQueueEntries(groupId);
	}

	/**
	 * Deletes the commerce notification queue entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntry the commerce notification queue entry
	 * @return the commerce notification queue entry that was removed
	 */
	public static CommerceNotificationQueueEntry
		deleteCommerceNotificationQueueEntry(
			CommerceNotificationQueueEntry commerceNotificationQueueEntry) {

		return getService().deleteCommerceNotificationQueueEntry(
			commerceNotificationQueueEntry);
	}

	/**
	 * Deletes the commerce notification queue entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntryId the primary key of the commerce notification queue entry
	 * @return the commerce notification queue entry that was removed
	 * @throws PortalException if a commerce notification queue entry with the primary key could not be found
	 */
	public static CommerceNotificationQueueEntry
			deleteCommerceNotificationQueueEntry(
				long commerceNotificationQueueEntryId)
		throws PortalException {

		return getService().deleteCommerceNotificationQueueEntry(
			commerceNotificationQueueEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryModelImpl</code>.
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

	public static CommerceNotificationQueueEntry
		fetchCommerceNotificationQueueEntry(
			long commerceNotificationQueueEntryId) {

		return getService().fetchCommerceNotificationQueueEntry(
			commerceNotificationQueueEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommerceNotificationQueueEntry>
		getCommerceNotificationQueueEntries(boolean sent) {

		return getService().getCommerceNotificationQueueEntries(sent);
	}

	/**
	 * Returns a range of all the commerce notification queue entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @return the range of commerce notification queue entries
	 */
	public static List<CommerceNotificationQueueEntry>
		getCommerceNotificationQueueEntries(int start, int end) {

		return getService().getCommerceNotificationQueueEntries(start, end);
	}

	public static List<CommerceNotificationQueueEntry>
		getCommerceNotificationQueueEntries(
			long groupId, String className, long classPK, boolean sent,
			int start, int end,
			OrderByComparator<CommerceNotificationQueueEntry>
				orderByComparator) {

		return getService().getCommerceNotificationQueueEntries(
			groupId, className, classPK, sent, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce notification queue entries.
	 *
	 * @return the number of commerce notification queue entries
	 */
	public static int getCommerceNotificationQueueEntriesCount() {
		return getService().getCommerceNotificationQueueEntriesCount();
	}

	public static int getCommerceNotificationQueueEntriesCount(
		long groupId, String className, long classPK, boolean sent) {

		return getService().getCommerceNotificationQueueEntriesCount(
			groupId, className, classPK, sent);
	}

	/**
	 * Returns the commerce notification queue entry with the primary key.
	 *
	 * @param commerceNotificationQueueEntryId the primary key of the commerce notification queue entry
	 * @return the commerce notification queue entry
	 * @throws PortalException if a commerce notification queue entry with the primary key could not be found
	 */
	public static CommerceNotificationQueueEntry
			getCommerceNotificationQueueEntry(
				long commerceNotificationQueueEntryId)
		throws PortalException {

		return getService().getCommerceNotificationQueueEntry(
			commerceNotificationQueueEntryId);
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

	public static CommerceNotificationQueueEntry
			resendCommerceNotificationQueueEntry(
				long commerceNotificationQueueEntryId)
		throws PortalException {

		return getService().resendCommerceNotificationQueueEntry(
			commerceNotificationQueueEntryId);
	}

	public static void sendCommerceNotificationQueueEntries() throws Exception {
		getService().sendCommerceNotificationQueueEntries();
	}

	public static void updateCommerceNotificationQueueEntriesTemplateIds(
		long commerceNotificationTemplateId) {

		getService().updateCommerceNotificationQueueEntriesTemplateIds(
			commerceNotificationTemplateId);
	}

	/**
	 * Updates the commerce notification queue entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntry the commerce notification queue entry
	 * @return the commerce notification queue entry that was updated
	 */
	public static CommerceNotificationQueueEntry
		updateCommerceNotificationQueueEntry(
			CommerceNotificationQueueEntry commerceNotificationQueueEntry) {

		return getService().updateCommerceNotificationQueueEntry(
			commerceNotificationQueueEntry);
	}

	public static CommerceNotificationQueueEntry updateSent(
			long commerceNotificationQueueEntryId, boolean sent)
		throws PortalException {

		return getService().updateSent(commerceNotificationQueueEntryId, sent);
	}

	public static CommerceNotificationQueueEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceNotificationQueueEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceNotificationQueueEntryLocalServiceUtil.class,
			CommerceNotificationQueueEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1478914833
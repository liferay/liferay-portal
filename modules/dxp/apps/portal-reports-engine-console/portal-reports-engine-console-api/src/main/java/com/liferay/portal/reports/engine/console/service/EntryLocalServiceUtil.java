/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.reports.engine.console.model.Entry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for Entry. This utility wraps
 * <code>com.liferay.portal.reports.engine.console.service.impl.EntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see EntryLocalService
 * @generated
 */
public class EntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.reports.engine.console.service.impl.EntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entry the entry
	 * @return the entry that was added
	 */
	public static Entry addEntry(Entry entry) {
		return getService().addEntry(entry);
	}

	public static Entry addEntry(
			long userId, long groupId, long definitionId, String format,
			boolean schedulerRequest, java.util.Date startDate,
			java.util.Date endDate, boolean repeating, String recurrence,
			String emailNotifications, String emailDelivery, String portletId,
			String pageURL, String reportName, String reportParameters,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addEntry(
			userId, groupId, definitionId, format, schedulerRequest, startDate,
			endDate, repeating, recurrence, emailNotifications, emailDelivery,
			portletId, pageURL, reportName, reportParameters, serviceContext);
	}

	public static void addEntryResources(
			Entry entry, boolean addCommunityPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addEntryResources(
			entry, addCommunityPermissions, addGuestPermissions);
	}

	public static void addEntryResources(
			Entry entry, String[] communityPermissions,
			String[] guestPermissions)
		throws PortalException {

		getService().addEntryResources(
			entry, communityPermissions, guestPermissions);
	}

	/**
	 * Creates a new entry with the primary key. Does not add the entry to the database.
	 *
	 * @param entryId the primary key for the new entry
	 * @return the new entry
	 */
	public static Entry createEntry(long entryId) {
		return getService().createEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteAttachment(long companyId, String fileName)
		throws PortalException {

		getService().deleteAttachment(companyId, fileName);
	}

	/**
	 * Deletes the entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entry the entry
	 * @return the entry that was removed
	 * @throws PortalException
	 */
	public static Entry deleteEntry(Entry entry) throws PortalException {
		return getService().deleteEntry(entry);
	}

	/**
	 * Deletes the entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the entry
	 * @return the entry that was removed
	 * @throws PortalException if a entry with the primary key could not be found
	 */
	public static Entry deleteEntry(long entryId) throws PortalException {
		return getService().deleteEntry(entryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.EntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.EntryModelImpl</code>.
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

	public static Entry fetchEntry(long entryId) {
		return getService().fetchEntry(entryId);
	}

	public static void generateReport(long entryId) throws PortalException {
		getService().generateReport(entryId);
	}

	public static void generateReport(long entryId, String reportName)
		throws PortalException {

		getService().generateReport(entryId, reportName);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static String[] getAttachmentsFileNames(Entry entry) {
		return getService().getAttachmentsFileNames(entry);
	}

	/**
	 * Returns a range of all the entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.EntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of entries
	 * @param end the upper bound of the range of entries (not inclusive)
	 * @return the range of entries
	 */
	public static List<Entry> getEntries(int start, int end) {
		return getService().getEntries(start, end);
	}

	/**
	 * Returns the number of entries.
	 *
	 * @return the number of entries
	 */
	public static int getEntriesCount() {
		return getService().getEntriesCount();
	}

	/**
	 * Returns the entry with the primary key.
	 *
	 * @param entryId the primary key of the entry
	 * @return the entry
	 * @throws PortalException if a entry with the primary key could not be found
	 */
	public static Entry getEntry(long entryId) throws PortalException {
		return getService().getEntry(entryId);
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

	public static void sendEmails(
			long entryId, String fileName, String[] emailAddresses,
			boolean notification)
		throws PortalException {

		getService().sendEmails(
			entryId, fileName, emailAddresses, notification);
	}

	public static void unscheduleEntry(long entryId) throws PortalException {
		getService().unscheduleEntry(entryId);
	}

	/**
	 * Updates the entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entry the entry
	 * @return the entry that was updated
	 */
	public static Entry updateEntry(Entry entry) {
		return getService().updateEntry(entry);
	}

	public static void updateEntry(
			long entryId, String reportName, byte[] reportResults)
		throws PortalException {

		getService().updateEntry(entryId, reportName, reportResults);
	}

	public static void updateEntryStatus(
			long entryId,
			com.liferay.portal.reports.engine.console.status.ReportStatus
				status,
			String errorMessage)
		throws PortalException {

		getService().updateEntryStatus(entryId, status, errorMessage);
	}

	public static EntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<EntryLocalService> _serviceSnapshot =
		new Snapshot<>(EntryLocalServiceUtil.class, EntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-516966523
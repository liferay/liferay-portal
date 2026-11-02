/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.reading.time.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.reading.time.model.ReadingTimeEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ReadingTimeEntry. This utility wraps
 * <code>com.liferay.reading.time.service.impl.ReadingTimeEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ReadingTimeEntryLocalService
 * @generated
 */
public class ReadingTimeEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.reading.time.service.impl.ReadingTimeEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ReadingTimeEntry addReadingTimeEntry(
		long groupId, long classNameId, long classPK,
		java.time.Duration readingTimeDuration) {

		return getService().addReadingTimeEntry(
			groupId, classNameId, classPK, readingTimeDuration);
	}

	/**
	 * Adds the reading time entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ReadingTimeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param readingTimeEntry the reading time entry
	 * @return the reading time entry that was added
	 */
	public static ReadingTimeEntry addReadingTimeEntry(
		ReadingTimeEntry readingTimeEntry) {

		return getService().addReadingTimeEntry(readingTimeEntry);
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
	 * Creates a new reading time entry with the primary key. Does not add the reading time entry to the database.
	 *
	 * @param readingTimeEntryId the primary key for the new reading time entry
	 * @return the new reading time entry
	 */
	public static ReadingTimeEntry createReadingTimeEntry(
		long readingTimeEntryId) {

		return getService().createReadingTimeEntry(readingTimeEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static ReadingTimeEntry deleteReadingTimeEntry(
		com.liferay.portal.kernel.model.GroupedModel groupedModel) {

		return getService().deleteReadingTimeEntry(groupedModel);
	}

	/**
	 * Deletes the reading time entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ReadingTimeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param readingTimeEntryId the primary key of the reading time entry
	 * @return the reading time entry that was removed
	 * @throws PortalException if a reading time entry with the primary key could not be found
	 */
	public static ReadingTimeEntry deleteReadingTimeEntry(
			long readingTimeEntryId)
		throws PortalException {

		return getService().deleteReadingTimeEntry(readingTimeEntryId);
	}

	/**
	 * Deletes the reading time entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ReadingTimeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param readingTimeEntry the reading time entry
	 * @return the reading time entry that was removed
	 */
	public static ReadingTimeEntry deleteReadingTimeEntry(
		ReadingTimeEntry readingTimeEntry) {

		return getService().deleteReadingTimeEntry(readingTimeEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.reading.time.model.impl.ReadingTimeEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.reading.time.model.impl.ReadingTimeEntryModelImpl</code>.
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

	public static ReadingTimeEntry fetchOrAddReadingTimeEntry(
		com.liferay.portal.kernel.model.GroupedModel groupedModel) {

		return getService().fetchOrAddReadingTimeEntry(groupedModel);
	}

	public static ReadingTimeEntry fetchReadingTimeEntry(
		com.liferay.portal.kernel.model.GroupedModel groupedModel) {

		return getService().fetchReadingTimeEntry(groupedModel);
	}

	public static ReadingTimeEntry fetchReadingTimeEntry(
		long readingTimeEntryId) {

		return getService().fetchReadingTimeEntry(readingTimeEntryId);
	}

	public static ReadingTimeEntry fetchReadingTimeEntry(
		long groupId, long classNameId, long classPK) {

		return getService().fetchReadingTimeEntry(
			groupId, classNameId, classPK);
	}

	/**
	 * Returns the reading time entry matching the UUID and group.
	 *
	 * @param uuid the reading time entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching reading time entry, or <code>null</code> if a matching reading time entry could not be found
	 */
	public static ReadingTimeEntry fetchReadingTimeEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchReadingTimeEntryByUuidAndGroupId(
			uuid, groupId);
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
	 * Returns a range of all the reading time entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.reading.time.model.impl.ReadingTimeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of reading time entries
	 * @param end the upper bound of the range of reading time entries (not inclusive)
	 * @return the range of reading time entries
	 */
	public static List<ReadingTimeEntry> getReadingTimeEntries(
		int start, int end) {

		return getService().getReadingTimeEntries(start, end);
	}

	/**
	 * Returns all the reading time entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the reading time entries
	 * @param companyId the primary key of the company
	 * @return the matching reading time entries, or an empty list if no matches were found
	 */
	public static List<ReadingTimeEntry>
		getReadingTimeEntriesByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getReadingTimeEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of reading time entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the reading time entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of reading time entries
	 * @param end the upper bound of the range of reading time entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching reading time entries, or an empty list if no matches were found
	 */
	public static List<ReadingTimeEntry>
		getReadingTimeEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<ReadingTimeEntry> orderByComparator) {

		return getService().getReadingTimeEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of reading time entries.
	 *
	 * @return the number of reading time entries
	 */
	public static int getReadingTimeEntriesCount() {
		return getService().getReadingTimeEntriesCount();
	}

	/**
	 * Returns the reading time entry with the primary key.
	 *
	 * @param readingTimeEntryId the primary key of the reading time entry
	 * @return the reading time entry
	 * @throws PortalException if a reading time entry with the primary key could not be found
	 */
	public static ReadingTimeEntry getReadingTimeEntry(long readingTimeEntryId)
		throws PortalException {

		return getService().getReadingTimeEntry(readingTimeEntryId);
	}

	/**
	 * Returns the reading time entry matching the UUID and group.
	 *
	 * @param uuid the reading time entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching reading time entry
	 * @throws PortalException if a matching reading time entry could not be found
	 */
	public static ReadingTimeEntry getReadingTimeEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getReadingTimeEntryByUuidAndGroupId(uuid, groupId);
	}

	public static ReadingTimeEntry updateReadingTimeEntry(
		com.liferay.portal.kernel.model.GroupedModel groupedModel) {

		return getService().updateReadingTimeEntry(groupedModel);
	}

	public static ReadingTimeEntry updateReadingTimeEntry(
		long groupId, long classNameId, long classPK,
		java.time.Duration readingTimeDuration) {

		return getService().updateReadingTimeEntry(
			groupId, classNameId, classPK, readingTimeDuration);
	}

	/**
	 * Updates the reading time entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ReadingTimeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param readingTimeEntry the reading time entry
	 * @return the reading time entry that was updated
	 */
	public static ReadingTimeEntry updateReadingTimeEntry(
		ReadingTimeEntry readingTimeEntry) {

		return getService().updateReadingTimeEntry(readingTimeEntry);
	}

	public static ReadingTimeEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ReadingTimeEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			ReadingTimeEntryLocalServiceUtil.class,
			ReadingTimeEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:357203666
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.trash.model.TrashEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for TrashEntry. This utility wraps
 * <code>com.liferay.trash.service.impl.TrashEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see TrashEntryLocalService
 * @generated
 */
public class TrashEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.trash.service.impl.TrashEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Moves an entry to trash.
	 *
	 * @param userId the primary key of the user removing the entity
	 * @param groupId the primary key of the entry's group
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @param classUuid the UUID of the entity's class
	 * @param referrerClassName the referrer class name used to add a deletion
	 {@link SystemEvent}
	 * @param status the status of the entity prior to being moved to trash
	 * @param statusOVPs the primary keys and statuses of any of the entry's
	 versions (e.g., {@link
	 com.liferay.portlet.documentlibrary.model.DLFileVersion})
	 * @param typeSettingsUnicodeProperties the type settings properties
	 * @return the trashEntry
	 */
	public static TrashEntry addTrashEntry(
			long userId, long groupId, String className, long classPK,
			String classUuid, String referrerClassName, int status,
			List<com.liferay.portal.kernel.util.ObjectValuePair<Long, Integer>>
				statusOVPs,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties)
		throws PortalException {

		return getService().addTrashEntry(
			userId, groupId, className, classPK, classUuid, referrerClassName,
			status, statusOVPs, typeSettingsUnicodeProperties);
	}

	/**
	 * Adds the trash entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was added
	 */
	public static TrashEntry addTrashEntry(TrashEntry trashEntry) {
		return getService().addTrashEntry(trashEntry);
	}

	public static void checkEntries() throws PortalException {
		getService().checkEntries();
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
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param entryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	public static TrashEntry createTrashEntry(long entryId) {
		return getService().createTrashEntry(entryId);
	}

	public static void deleteEntries(long groupId) {
		getService().deleteEntries(groupId);
	}

	public static void deleteEntries(
		long groupId, boolean deleteTrashedModels) {

		getService().deleteEntries(groupId, deleteTrashedModels);
	}

	/**
	 * Deletes the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	public static TrashEntry deleteEntry(long entryId) {
		return getService().deleteEntry(entryId);
	}

	/**
	 * Deletes the trash entry with the entity class name and primary key.
	 *
	 * @param className the class name of entity
	 * @param classPK the primary key of the entry
	 * @return the trash entry with the entity class name and primary key
	 */
	public static TrashEntry deleteEntry(String className, long classPK) {
		return getService().deleteEntry(className, classPK);
	}

	public static TrashEntry deleteEntry(TrashEntry trashEntry) {
		return getService().deleteEntry(trashEntry);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteTrashEntries(long companyId, String className) {
		getService().deleteTrashEntries(companyId, className);
	}

	/**
	 * Deletes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws PortalException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry deleteTrashEntry(long entryId)
		throws PortalException {

		return getService().deleteTrashEntry(entryId);
	}

	/**
	 * Deletes the trash entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was removed
	 */
	public static TrashEntry deleteTrashEntry(TrashEntry trashEntry) {
		return getService().deleteTrashEntry(trashEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
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

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the entry
	 * @return the trash entry with the primary key
	 */
	public static TrashEntry fetchEntry(long entryId) {
		return getService().fetchEntry(entryId);
	}

	/**
	 * Returns the trash entry with the entity class name and primary key.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	public static TrashEntry fetchEntry(String className, long classPK) {
		return getService().fetchEntry(className, classPK);
	}

	public static TrashEntry fetchTrashEntry(long entryId) {
		return getService().fetchTrashEntry(entryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the trash entries with the matching group ID.
	 *
	 * @param groupId the primary key of the group
	 * @return the trash entries with the group ID
	 */
	public static List<TrashEntry> getEntries(long groupId) {
		return getService().getEntries(groupId);
	}

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of trash entries to return
	 * @param end the upper bound of the range of trash entries to return (not
	 inclusive)
	 * @return the range of matching trash entries
	 */
	public static List<TrashEntry> getEntries(
		long groupId, int start, int end) {

		return getService().getEntries(groupId, start, end);
	}

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of trash entries to return
	 * @param end the upper bound of the range of trash entries to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the trash entries
	 (optionally <code>null</code>)
	 * @return the range of matching trash entries ordered by comparator
	 <code>orderByComparator</code>
	 */
	public static List<TrashEntry> getEntries(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getService().getEntries(groupId, start, end, orderByComparator);
	}

	public static List<TrashEntry> getEntries(long groupId, String className) {
		return getService().getEntries(groupId, className);
	}

	/**
	 * Returns the number of trash entries with the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of matching trash entries
	 */
	public static int getEntriesCount(long groupId) {
		return getService().getEntriesCount(groupId);
	}

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	public static TrashEntry getEntry(long entryId) throws PortalException {
		return getService().getEntry(entryId);
	}

	/**
	 * Returns the entry with the entity class name and primary key.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	public static TrashEntry getEntry(String className, long classPK)
		throws PortalException {

		return getService().getEntry(className, classPK);
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
	 * Returns a range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of trash entries
	 */
	public static List<TrashEntry> getTrashEntries(int start, int end) {
		return getService().getTrashEntries(start, end);
	}

	/**
	 * Returns the number of trash entries.
	 *
	 * @return the number of trash entries
	 */
	public static int getTrashEntriesCount() {
		return getService().getTrashEntriesCount();
	}

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws PortalException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry getTrashEntry(long entryId)
		throws PortalException {

		return getService().getTrashEntry(entryId);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long groupId, long userId, String keywords, int start,
		int end, com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
			companyId, groupId, userId, keywords, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<TrashEntry> searchTrashEntries(
			long companyId, long groupId, long userId, String keywords,
			int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return getService().searchTrashEntries(
			companyId, groupId, userId, keywords, start, end, sort);
	}

	/**
	 * Updates the trash entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was updated
	 */
	public static TrashEntry updateTrashEntry(TrashEntry trashEntry) {
		return getService().updateTrashEntry(trashEntry);
	}

	public static TrashEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<TrashEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			TrashEntryLocalServiceUtil.class, TrashEntryLocalService.class);

}
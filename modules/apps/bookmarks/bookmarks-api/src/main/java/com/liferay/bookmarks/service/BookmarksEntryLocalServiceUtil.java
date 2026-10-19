/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for BookmarksEntry. This utility wraps
 * <code>com.liferay.bookmarks.service.impl.BookmarksEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see BookmarksEntryLocalService
 * @generated
 */
public class BookmarksEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.bookmarks.service.impl.BookmarksEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the bookmarks entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param bookmarksEntry the bookmarks entry
	 * @return the bookmarks entry that was added
	 */
	public static BookmarksEntry addBookmarksEntry(
		BookmarksEntry bookmarksEntry) {

		return getService().addBookmarksEntry(bookmarksEntry);
	}

	public static BookmarksEntry addEntry(
			long userId, long groupId, long folderId, String name, String url,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addEntry(
			userId, groupId, folderId, name, url, description, serviceContext);
	}

	/**
	 * Creates a new bookmarks entry with the primary key. Does not add the bookmarks entry to the database.
	 *
	 * @param entryId the primary key for the new bookmarks entry
	 * @return the new bookmarks entry
	 */
	public static BookmarksEntry createBookmarksEntry(long entryId) {
		return getService().createBookmarksEntry(entryId);
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
	 * Deletes the bookmarks entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param bookmarksEntry the bookmarks entry
	 * @return the bookmarks entry that was removed
	 */
	public static BookmarksEntry deleteBookmarksEntry(
		BookmarksEntry bookmarksEntry) {

		return getService().deleteBookmarksEntry(bookmarksEntry);
	}

	/**
	 * Deletes the bookmarks entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the bookmarks entry
	 * @return the bookmarks entry that was removed
	 * @throws PortalException if a bookmarks entry with the primary key could not be found
	 */
	public static BookmarksEntry deleteBookmarksEntry(long entryId)
		throws PortalException {

		return getService().deleteBookmarksEntry(entryId);
	}

	public static void deleteEntries(long groupId, long folderId)
		throws PortalException {

		getService().deleteEntries(groupId, folderId);
	}

	public static void deleteEntries(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		getService().deleteEntries(groupId, folderId, includeTrashedEntries);
	}

	public static BookmarksEntry deleteEntry(BookmarksEntry entry)
		throws PortalException {

		return getService().deleteEntry(entry);
	}

	public static BookmarksEntry deleteEntry(long entryId)
		throws PortalException {

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksEntryModelImpl</code>.
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

	public static BookmarksEntry fetchBookmarksEntry(long entryId) {
		return getService().fetchBookmarksEntry(entryId);
	}

	/**
	 * Returns the bookmarks entry matching the UUID and group.
	 *
	 * @param uuid the bookmarks entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks entry, or <code>null</code> if a matching bookmarks entry could not be found
	 */
	public static BookmarksEntry fetchBookmarksEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchBookmarksEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the bookmarks entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bookmarks entries
	 * @param end the upper bound of the range of bookmarks entries (not inclusive)
	 * @return the range of bookmarks entries
	 */
	public static List<BookmarksEntry> getBookmarksEntries(int start, int end) {
		return getService().getBookmarksEntries(start, end);
	}

	/**
	 * Returns all the bookmarks entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks entries
	 * @param companyId the primary key of the company
	 * @return the matching bookmarks entries, or an empty list if no matches were found
	 */
	public static List<BookmarksEntry> getBookmarksEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getBookmarksEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of bookmarks entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of bookmarks entries
	 * @param end the upper bound of the range of bookmarks entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching bookmarks entries, or an empty list if no matches were found
	 */
	public static List<BookmarksEntry> getBookmarksEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BookmarksEntry> orderByComparator) {

		return getService().getBookmarksEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of bookmarks entries.
	 *
	 * @return the number of bookmarks entries
	 */
	public static int getBookmarksEntriesCount() {
		return getService().getBookmarksEntriesCount();
	}

	/**
	 * Returns the bookmarks entry with the primary key.
	 *
	 * @param entryId the primary key of the bookmarks entry
	 * @return the bookmarks entry
	 * @throws PortalException if a bookmarks entry with the primary key could not be found
	 */
	public static BookmarksEntry getBookmarksEntry(long entryId)
		throws PortalException {

		return getService().getBookmarksEntry(entryId);
	}

	/**
	 * Returns the bookmarks entry matching the UUID and group.
	 *
	 * @param uuid the bookmarks entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks entry
	 * @throws PortalException if a matching bookmarks entry could not be found
	 */
	public static BookmarksEntry getBookmarksEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getBookmarksEntryByUuidAndGroupId(uuid, groupId);
	}

	public static List<BookmarksEntry> getEntries(
		long groupId, long folderId, int start, int end) {

		return getService().getEntries(groupId, folderId, start, end);
	}

	public static List<BookmarksEntry> getEntries(
		long groupId, long folderId, int status, int start, int end) {

		return getService().getEntries(groupId, folderId, status, start, end);
	}

	public static List<BookmarksEntry> getEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<BookmarksEntry> orderByComparator) {

		return getService().getEntries(
			groupId, folderId, status, start, end, orderByComparator);
	}

	public static List<BookmarksEntry> getEntries(
		long groupId, long folderId, int start, int end,
		OrderByComparator<BookmarksEntry> orderByComparator) {

		return getService().getEntries(
			groupId, folderId, start, end, orderByComparator);
	}

	public static int getEntriesCount(long groupId, long folderId) {
		return getService().getEntriesCount(groupId, folderId);
	}

	public static int getEntriesCount(long groupId, long folderId, int status) {
		return getService().getEntriesCount(groupId, folderId, status);
	}

	public static BookmarksEntry getEntry(long entryId) throws PortalException {
		return getService().getEntry(entryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static int getFoldersEntriesCount(
		long groupId, List<Long> folderIds) {

		return getService().getFoldersEntriesCount(groupId, folderIds);
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

	public static BookmarksEntry moveEntry(long entryId, long parentFolderId)
		throws PortalException {

		return getService().moveEntry(entryId, parentFolderId);
	}

	public static BookmarksEntry moveEntryFromTrash(
			long userId, long entryId, long parentFolderId)
		throws PortalException {

		return getService().moveEntryFromTrash(userId, entryId, parentFolderId);
	}

	public static BookmarksEntry moveEntryToTrash(
			long userId, BookmarksEntry entry)
		throws PortalException {

		return getService().moveEntryToTrash(userId, entry);
	}

	public static BookmarksEntry moveEntryToTrash(long userId, long entryId)
		throws PortalException {

		return getService().moveEntryToTrash(userId, entryId);
	}

	public static BookmarksEntry openEntry(long userId, BookmarksEntry entry) {
		return getService().openEntry(userId, entry);
	}

	public static BookmarksEntry openEntry(long userId, long entryId)
		throws PortalException {

		return getService().openEntry(userId, entryId);
	}

	public static BookmarksEntry restoreEntryFromTrash(
			long userId, long entryId)
		throws PortalException {

		return getService().restoreEntryFromTrash(userId, entryId);
	}

	public static com.liferay.portal.kernel.search.Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws PortalException {

		return getService().search(
			groupId, userId, creatorUserId, status, start, end);
	}

	public static void setTreePaths(
			long folderId, String treePath, boolean reindex)
		throws PortalException {

		getService().setTreePaths(folderId, treePath, reindex);
	}

	public static void subscribeEntry(long userId, long entryId)
		throws PortalException {

		getService().subscribeEntry(userId, entryId);
	}

	public static void unsubscribeEntry(long userId, long entryId)
		throws PortalException {

		getService().unsubscribeEntry(userId, entryId);
	}

	public static void updateAsset(
			long userId, BookmarksEntry entry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, entry, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	/**
	 * Updates the bookmarks entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param bookmarksEntry the bookmarks entry
	 * @return the bookmarks entry that was updated
	 */
	public static BookmarksEntry updateBookmarksEntry(
		BookmarksEntry bookmarksEntry) {

		return getService().updateBookmarksEntry(bookmarksEntry);
	}

	public static BookmarksEntry updateEntry(
			long userId, long entryId, long groupId, long folderId, String name,
			String url, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateEntry(
			userId, entryId, groupId, folderId, name, url, description,
			serviceContext);
	}

	public static BookmarksEntry updateStatus(
			long userId, BookmarksEntry entry, int status)
		throws PortalException {

		return getService().updateStatus(userId, entry, status);
	}

	public static BookmarksEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<BookmarksEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			BookmarksEntryLocalServiceUtil.class,
			BookmarksEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1918355130
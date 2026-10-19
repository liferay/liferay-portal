/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service;

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for BookmarksFolder. This utility wraps
 * <code>com.liferay.bookmarks.service.impl.BookmarksFolderLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see BookmarksFolderLocalService
 * @generated
 */
public class BookmarksFolderLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.bookmarks.service.impl.BookmarksFolderLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the bookmarks folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param bookmarksFolder the bookmarks folder
	 * @return the bookmarks folder that was added
	 */
	public static BookmarksFolder addBookmarksFolder(
		BookmarksFolder bookmarksFolder) {

		return getService().addBookmarksFolder(bookmarksFolder);
	}

	public static BookmarksFolder addFolder(
			long userId, long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFolder(
			userId, parentFolderId, name, description, serviceContext);
	}

	/**
	 * Creates a new bookmarks folder with the primary key. Does not add the bookmarks folder to the database.
	 *
	 * @param folderId the primary key for the new bookmarks folder
	 * @return the new bookmarks folder
	 */
	public static BookmarksFolder createBookmarksFolder(long folderId) {
		return getService().createBookmarksFolder(folderId);
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
	 * Deletes the bookmarks folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param bookmarksFolder the bookmarks folder
	 * @return the bookmarks folder that was removed
	 */
	public static BookmarksFolder deleteBookmarksFolder(
		BookmarksFolder bookmarksFolder) {

		return getService().deleteBookmarksFolder(bookmarksFolder);
	}

	/**
	 * Deletes the bookmarks folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param folderId the primary key of the bookmarks folder
	 * @return the bookmarks folder that was removed
	 * @throws PortalException if a bookmarks folder with the primary key could not be found
	 */
	public static BookmarksFolder deleteBookmarksFolder(long folderId)
		throws PortalException {

		return getService().deleteBookmarksFolder(folderId);
	}

	public static BookmarksFolder deleteFolder(BookmarksFolder folder)
		throws PortalException {

		return getService().deleteFolder(folder);
	}

	public static BookmarksFolder deleteFolder(
			BookmarksFolder folder, boolean includeTrashedEntries)
		throws PortalException {

		return getService().deleteFolder(folder, includeTrashedEntries);
	}

	public static BookmarksFolder deleteFolder(long folderId)
		throws PortalException {

		return getService().deleteFolder(folderId);
	}

	public static BookmarksFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws PortalException {

		return getService().deleteFolder(folderId, includeTrashedEntries);
	}

	public static void deleteFolders(long groupId) throws PortalException {
		getService().deleteFolders(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksFolderModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksFolderModelImpl</code>.
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

	public static BookmarksFolder fetchBookmarksFolder(long folderId) {
		return getService().fetchBookmarksFolder(folderId);
	}

	/**
	 * Returns the bookmarks folder matching the UUID and group.
	 *
	 * @param uuid the bookmarks folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	public static BookmarksFolder fetchBookmarksFolderByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchBookmarksFolderByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the bookmarks folder with the primary key.
	 *
	 * @param folderId the primary key of the bookmarks folder
	 * @return the bookmarks folder
	 * @throws PortalException if a bookmarks folder with the primary key could not be found
	 */
	public static BookmarksFolder getBookmarksFolder(long folderId)
		throws PortalException {

		return getService().getBookmarksFolder(folderId);
	}

	/**
	 * Returns the bookmarks folder matching the UUID and group.
	 *
	 * @param uuid the bookmarks folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks folder
	 * @throws PortalException if a matching bookmarks folder could not be found
	 */
	public static BookmarksFolder getBookmarksFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getBookmarksFolderByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the bookmarks folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @return the range of bookmarks folders
	 */
	public static List<BookmarksFolder> getBookmarksFolders(
		int start, int end) {

		return getService().getBookmarksFolders(start, end);
	}

	/**
	 * Returns all the bookmarks folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks folders
	 * @param companyId the primary key of the company
	 * @return the matching bookmarks folders, or an empty list if no matches were found
	 */
	public static List<BookmarksFolder> getBookmarksFoldersByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getBookmarksFoldersByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of bookmarks folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of bookmarks folders
	 * @param end the upper bound of the range of bookmarks folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching bookmarks folders, or an empty list if no matches were found
	 */
	public static List<BookmarksFolder> getBookmarksFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator) {

		return getService().getBookmarksFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of bookmarks folders.
	 *
	 * @return the number of bookmarks folders
	 */
	public static int getBookmarksFoldersCount() {
		return getService().getBookmarksFoldersCount();
	}

	public static List<BookmarksFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return getService().getCompanyFolders(companyId, start, end);
	}

	public static int getCompanyFoldersCount(long companyId) {
		return getService().getCompanyFoldersCount(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static BookmarksFolder getFolder(long folderId)
		throws PortalException {

		return getService().getFolder(folderId);
	}

	public static List<BookmarksFolder> getFolders(long groupId) {
		return getService().getFolders(groupId);
	}

	public static List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId) {

		return getService().getFolders(groupId, parentFolderId);
	}

	public static List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int start, int end) {

		return getService().getFolders(groupId, parentFolderId, start, end);
	}

	public static List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end) {

		return getService().getFolders(
			groupId, parentFolderId, status, start, end);
	}

	public static List<Object> getFoldersAndEntries(
		long groupId, long folderId) {

		return getService().getFoldersAndEntries(groupId, folderId);
	}

	public static List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status) {

		return getService().getFoldersAndEntries(groupId, folderId, status);
	}

	public static List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<?> orderByComparator) {

		return getService().getFoldersAndEntries(
			groupId, folderId, status, start, end, orderByComparator);
	}

	public static int getFoldersAndEntriesCount(
		long groupId, long folderId, int status) {

		return getService().getFoldersAndEntriesCount(
			groupId, folderId, status);
	}

	public static int getFoldersCount(long groupId, long parentFolderId) {
		return getService().getFoldersCount(groupId, parentFolderId);
	}

	public static int getFoldersCount(
		long groupId, long parentFolderId, int status) {

		return getService().getFoldersCount(groupId, parentFolderId, status);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<BookmarksFolder> getNoAssetFolders() {
		return getService().getNoAssetFolders();
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

	public static void getSubfolderIds(
		List<Long> folderIds, long groupId, long folderId) {

		getService().getSubfolderIds(folderIds, groupId, folderId);
	}

	public static void mergeFolders(long folderId, long parentFolderId)
		throws PortalException {

		getService().mergeFolders(folderId, parentFolderId);
	}

	public static BookmarksFolder moveFolder(long folderId, long parentFolderId)
		throws PortalException {

		return getService().moveFolder(folderId, parentFolderId);
	}

	public static BookmarksFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId)
		throws PortalException {

		return getService().moveFolderFromTrash(
			userId, folderId, parentFolderId);
	}

	public static BookmarksFolder moveFolderToTrash(long userId, long folderId)
		throws PortalException {

		return getService().moveFolderToTrash(userId, folderId);
	}

	public static void rebuildTree(long companyId) throws PortalException {
		getService().rebuildTree(companyId);
	}

	public static void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException {

		getService().rebuildTree(
			companyId, parentFolderId, parentTreePath, reindex);
	}

	public static BookmarksFolder restoreFolderFromTrash(
			long userId, long folderId)
		throws PortalException {

		return getService().restoreFolderFromTrash(userId, folderId);
	}

	public static void subscribeFolder(long userId, long groupId, long folderId)
		throws PortalException {

		getService().subscribeFolder(userId, groupId, folderId);
	}

	public static void unsubscribeFolder(
			long userId, long groupId, long folderId)
		throws PortalException {

		getService().unsubscribeFolder(userId, groupId, folderId);
	}

	public static void updateAsset(
			long userId, BookmarksFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, folder, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	/**
	 * Updates the bookmarks folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BookmarksFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param bookmarksFolder the bookmarks folder
	 * @return the bookmarks folder that was updated
	 */
	public static BookmarksFolder updateBookmarksFolder(
		BookmarksFolder bookmarksFolder) {

		return getService().updateBookmarksFolder(bookmarksFolder);
	}

	public static BookmarksFolder updateFolder(
			long userId, long folderId, long parentFolderId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFolder(
			userId, folderId, parentFolderId, name, description,
			serviceContext);
	}

	public static BookmarksFolder updateStatus(
			long userId, BookmarksFolder folder, int status)
		throws PortalException {

		return getService().updateStatus(userId, folder, status);
	}

	public static BookmarksFolderLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<BookmarksFolderLocalService>
		_serviceSnapshot = new Snapshot<>(
			BookmarksFolderLocalServiceUtil.class,
			BookmarksFolderLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1737965882
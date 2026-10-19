/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service;

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link BookmarksFolderLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see BookmarksFolderLocalService
 * @generated
 */
public class BookmarksFolderLocalServiceWrapper
	implements BookmarksFolderLocalService,
			   ServiceWrapper<BookmarksFolderLocalService> {

	public BookmarksFolderLocalServiceWrapper() {
		this(null);
	}

	public BookmarksFolderLocalServiceWrapper(
		BookmarksFolderLocalService bookmarksFolderLocalService) {

		_bookmarksFolderLocalService = bookmarksFolderLocalService;
	}

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
	@Override
	public BookmarksFolder addBookmarksFolder(BookmarksFolder bookmarksFolder) {
		return _bookmarksFolderLocalService.addBookmarksFolder(bookmarksFolder);
	}

	@Override
	public BookmarksFolder addFolder(
			long userId, long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.addFolder(
			userId, parentFolderId, name, description, serviceContext);
	}

	/**
	 * Creates a new bookmarks folder with the primary key. Does not add the bookmarks folder to the database.
	 *
	 * @param folderId the primary key for the new bookmarks folder
	 * @return the new bookmarks folder
	 */
	@Override
	public BookmarksFolder createBookmarksFolder(long folderId) {
		return _bookmarksFolderLocalService.createBookmarksFolder(folderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public BookmarksFolder deleteBookmarksFolder(
		BookmarksFolder bookmarksFolder) {

		return _bookmarksFolderLocalService.deleteBookmarksFolder(
			bookmarksFolder);
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
	@Override
	public BookmarksFolder deleteBookmarksFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.deleteBookmarksFolder(folderId);
	}

	@Override
	public BookmarksFolder deleteFolder(BookmarksFolder folder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.deleteFolder(folder);
	}

	@Override
	public BookmarksFolder deleteFolder(
			BookmarksFolder folder, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.deleteFolder(
			folder, includeTrashedEntries);
	}

	@Override
	public BookmarksFolder deleteFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.deleteFolder(folderId);
	}

	@Override
	public BookmarksFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.deleteFolder(
			folderId, includeTrashedEntries);
	}

	@Override
	public void deleteFolders(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderLocalService.deleteFolders(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _bookmarksFolderLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _bookmarksFolderLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _bookmarksFolderLocalService.dynamicQuery();
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

		return _bookmarksFolderLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _bookmarksFolderLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _bookmarksFolderLocalService.dynamicQuery(
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

		return _bookmarksFolderLocalService.dynamicQueryCount(dynamicQuery);
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

		return _bookmarksFolderLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public BookmarksFolder fetchBookmarksFolder(long folderId) {
		return _bookmarksFolderLocalService.fetchBookmarksFolder(folderId);
	}

	/**
	 * Returns the bookmarks folder matching the UUID and group.
	 *
	 * @param uuid the bookmarks folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder fetchBookmarksFolderByUuidAndGroupId(
		String uuid, long groupId) {

		return _bookmarksFolderLocalService.
			fetchBookmarksFolderByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _bookmarksFolderLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the bookmarks folder with the primary key.
	 *
	 * @param folderId the primary key of the bookmarks folder
	 * @return the bookmarks folder
	 * @throws PortalException if a bookmarks folder with the primary key could not be found
	 */
	@Override
	public BookmarksFolder getBookmarksFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.getBookmarksFolder(folderId);
	}

	/**
	 * Returns the bookmarks folder matching the UUID and group.
	 *
	 * @param uuid the bookmarks folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks folder
	 * @throws PortalException if a matching bookmarks folder could not be found
	 */
	@Override
	public BookmarksFolder getBookmarksFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.getBookmarksFolderByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<BookmarksFolder> getBookmarksFolders(
		int start, int end) {

		return _bookmarksFolderLocalService.getBookmarksFolders(start, end);
	}

	/**
	 * Returns all the bookmarks folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks folders
	 * @param companyId the primary key of the company
	 * @return the matching bookmarks folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<BookmarksFolder>
		getBookmarksFoldersByUuidAndCompanyId(String uuid, long companyId) {

		return _bookmarksFolderLocalService.
			getBookmarksFoldersByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<BookmarksFolder>
		getBookmarksFoldersByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<BookmarksFolder>
				orderByComparator) {

		return _bookmarksFolderLocalService.
			getBookmarksFoldersByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of bookmarks folders.
	 *
	 * @return the number of bookmarks folders
	 */
	@Override
	public int getBookmarksFoldersCount() {
		return _bookmarksFolderLocalService.getBookmarksFoldersCount();
	}

	@Override
	public java.util.List<BookmarksFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return _bookmarksFolderLocalService.getCompanyFolders(
			companyId, start, end);
	}

	@Override
	public int getCompanyFoldersCount(long companyId) {
		return _bookmarksFolderLocalService.getCompanyFoldersCount(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _bookmarksFolderLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public BookmarksFolder getFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.getFolder(folderId);
	}

	@Override
	public java.util.List<BookmarksFolder> getFolders(long groupId) {
		return _bookmarksFolderLocalService.getFolders(groupId);
	}

	@Override
	public java.util.List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId) {

		return _bookmarksFolderLocalService.getFolders(groupId, parentFolderId);
	}

	@Override
	public java.util.List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int start, int end) {

		return _bookmarksFolderLocalService.getFolders(
			groupId, parentFolderId, start, end);
	}

	@Override
	public java.util.List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end) {

		return _bookmarksFolderLocalService.getFolders(
			groupId, parentFolderId, status, start, end);
	}

	@Override
	public java.util.List<Object> getFoldersAndEntries(
		long groupId, long folderId) {

		return _bookmarksFolderLocalService.getFoldersAndEntries(
			groupId, folderId);
	}

	@Override
	public java.util.List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status) {

		return _bookmarksFolderLocalService.getFoldersAndEntries(
			groupId, folderId, status);
	}

	@Override
	public java.util.List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<?> orderByComparator) {

		return _bookmarksFolderLocalService.getFoldersAndEntries(
			groupId, folderId, status, start, end, orderByComparator);
	}

	@Override
	public int getFoldersAndEntriesCount(
		long groupId, long folderId, int status) {

		return _bookmarksFolderLocalService.getFoldersAndEntriesCount(
			groupId, folderId, status);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return _bookmarksFolderLocalService.getFoldersCount(
			groupId, parentFolderId);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId, int status) {
		return _bookmarksFolderLocalService.getFoldersCount(
			groupId, parentFolderId, status);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _bookmarksFolderLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<BookmarksFolder> getNoAssetFolders() {
		return _bookmarksFolderLocalService.getNoAssetFolders();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _bookmarksFolderLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void getSubfolderIds(
		java.util.List<Long> folderIds, long groupId, long folderId) {

		_bookmarksFolderLocalService.getSubfolderIds(
			folderIds, groupId, folderId);
	}

	@Override
	public void mergeFolders(long folderId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderLocalService.mergeFolders(folderId, parentFolderId);
	}

	@Override
	public BookmarksFolder moveFolder(long folderId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.moveFolder(
			folderId, parentFolderId);
	}

	@Override
	public BookmarksFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.moveFolderFromTrash(
			userId, folderId, parentFolderId);
	}

	@Override
	public BookmarksFolder moveFolderToTrash(long userId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.moveFolderToTrash(userId, folderId);
	}

	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderLocalService.rebuildTree(companyId);
	}

	@Override
	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderLocalService.rebuildTree(
			companyId, parentFolderId, parentTreePath, reindex);
	}

	@Override
	public BookmarksFolder restoreFolderFromTrash(long userId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.restoreFolderFromTrash(
			userId, folderId);
	}

	@Override
	public void subscribeFolder(long userId, long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderLocalService.subscribeFolder(userId, groupId, folderId);
	}

	@Override
	public void unsubscribeFolder(long userId, long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderLocalService.unsubscribeFolder(
			userId, groupId, folderId);
	}

	@Override
	public void updateAsset(
			long userId, BookmarksFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderLocalService.updateAsset(
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
	@Override
	public BookmarksFolder updateBookmarksFolder(
		BookmarksFolder bookmarksFolder) {

		return _bookmarksFolderLocalService.updateBookmarksFolder(
			bookmarksFolder);
	}

	@Override
	public BookmarksFolder updateFolder(
			long userId, long folderId, long parentFolderId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.updateFolder(
			userId, folderId, parentFolderId, name, description,
			serviceContext);
	}

	@Override
	public BookmarksFolder updateStatus(
			long userId, BookmarksFolder folder, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderLocalService.updateStatus(
			userId, folder, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _bookmarksFolderLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<BookmarksFolder> getCTPersistence() {
		return _bookmarksFolderLocalService.getCTPersistence();
	}

	@Override
	public Class<BookmarksFolder> getModelClass() {
		return _bookmarksFolderLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<BookmarksFolder>, R, E>
				updateUnsafeFunction)
		throws E {

		return _bookmarksFolderLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public BookmarksFolderLocalService getWrappedService() {
		return _bookmarksFolderLocalService;
	}

	@Override
	public void setWrappedService(
		BookmarksFolderLocalService bookmarksFolderLocalService) {

		_bookmarksFolderLocalService = bookmarksFolderLocalService;
	}

	private BookmarksFolderLocalService _bookmarksFolderLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1547893449
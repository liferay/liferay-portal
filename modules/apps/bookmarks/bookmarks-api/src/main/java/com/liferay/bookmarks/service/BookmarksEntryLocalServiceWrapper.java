/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link BookmarksEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see BookmarksEntryLocalService
 * @generated
 */
public class BookmarksEntryLocalServiceWrapper
	implements BookmarksEntryLocalService,
			   ServiceWrapper<BookmarksEntryLocalService> {

	public BookmarksEntryLocalServiceWrapper() {
		this(null);
	}

	public BookmarksEntryLocalServiceWrapper(
		BookmarksEntryLocalService bookmarksEntryLocalService) {

		_bookmarksEntryLocalService = bookmarksEntryLocalService;
	}

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
	@Override
	public BookmarksEntry addBookmarksEntry(BookmarksEntry bookmarksEntry) {
		return _bookmarksEntryLocalService.addBookmarksEntry(bookmarksEntry);
	}

	@Override
	public BookmarksEntry addEntry(
			long userId, long groupId, long folderId, String name, String url,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.addEntry(
			userId, groupId, folderId, name, url, description, serviceContext);
	}

	/**
	 * Creates a new bookmarks entry with the primary key. Does not add the bookmarks entry to the database.
	 *
	 * @param entryId the primary key for the new bookmarks entry
	 * @return the new bookmarks entry
	 */
	@Override
	public BookmarksEntry createBookmarksEntry(long entryId) {
		return _bookmarksEntryLocalService.createBookmarksEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public BookmarksEntry deleteBookmarksEntry(BookmarksEntry bookmarksEntry) {
		return _bookmarksEntryLocalService.deleteBookmarksEntry(bookmarksEntry);
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
	@Override
	public BookmarksEntry deleteBookmarksEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.deleteBookmarksEntry(entryId);
	}

	@Override
	public void deleteEntries(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksEntryLocalService.deleteEntries(groupId, folderId);
	}

	@Override
	public void deleteEntries(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksEntryLocalService.deleteEntries(
			groupId, folderId, includeTrashedEntries);
	}

	@Override
	public BookmarksEntry deleteEntry(BookmarksEntry entry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.deleteEntry(entry);
	}

	@Override
	public BookmarksEntry deleteEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.deleteEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _bookmarksEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _bookmarksEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _bookmarksEntryLocalService.dynamicQuery();
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

		return _bookmarksEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _bookmarksEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _bookmarksEntryLocalService.dynamicQuery(
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

		return _bookmarksEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _bookmarksEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public BookmarksEntry fetchBookmarksEntry(long entryId) {
		return _bookmarksEntryLocalService.fetchBookmarksEntry(entryId);
	}

	/**
	 * Returns the bookmarks entry matching the UUID and group.
	 *
	 * @param uuid the bookmarks entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks entry, or <code>null</code> if a matching bookmarks entry could not be found
	 */
	@Override
	public BookmarksEntry fetchBookmarksEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _bookmarksEntryLocalService.fetchBookmarksEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _bookmarksEntryLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<BookmarksEntry> getBookmarksEntries(
		int start, int end) {

		return _bookmarksEntryLocalService.getBookmarksEntries(start, end);
	}

	/**
	 * Returns all the bookmarks entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks entries
	 * @param companyId the primary key of the company
	 * @return the matching bookmarks entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<BookmarksEntry> getBookmarksEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _bookmarksEntryLocalService.
			getBookmarksEntriesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<BookmarksEntry> getBookmarksEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BookmarksEntry>
			orderByComparator) {

		return _bookmarksEntryLocalService.
			getBookmarksEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of bookmarks entries.
	 *
	 * @return the number of bookmarks entries
	 */
	@Override
	public int getBookmarksEntriesCount() {
		return _bookmarksEntryLocalService.getBookmarksEntriesCount();
	}

	/**
	 * Returns the bookmarks entry with the primary key.
	 *
	 * @param entryId the primary key of the bookmarks entry
	 * @return the bookmarks entry
	 * @throws PortalException if a bookmarks entry with the primary key could not be found
	 */
	@Override
	public BookmarksEntry getBookmarksEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.getBookmarksEntry(entryId);
	}

	/**
	 * Returns the bookmarks entry matching the UUID and group.
	 *
	 * @param uuid the bookmarks entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks entry
	 * @throws PortalException if a matching bookmarks entry could not be found
	 */
	@Override
	public BookmarksEntry getBookmarksEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.getBookmarksEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.List<BookmarksEntry> getEntries(
		long groupId, long folderId, int start, int end) {

		return _bookmarksEntryLocalService.getEntries(
			groupId, folderId, start, end);
	}

	@Override
	public java.util.List<BookmarksEntry> getEntries(
		long groupId, long folderId, int status, int start, int end) {

		return _bookmarksEntryLocalService.getEntries(
			groupId, folderId, status, start, end);
	}

	@Override
	public java.util.List<BookmarksEntry> getEntries(
		long groupId, long folderId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BookmarksEntry>
			orderByComparator) {

		return _bookmarksEntryLocalService.getEntries(
			groupId, folderId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<BookmarksEntry> getEntries(
		long groupId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BookmarksEntry>
			orderByComparator) {

		return _bookmarksEntryLocalService.getEntries(
			groupId, folderId, start, end, orderByComparator);
	}

	@Override
	public int getEntriesCount(long groupId, long folderId) {
		return _bookmarksEntryLocalService.getEntriesCount(groupId, folderId);
	}

	@Override
	public int getEntriesCount(long groupId, long folderId, int status) {
		return _bookmarksEntryLocalService.getEntriesCount(
			groupId, folderId, status);
	}

	@Override
	public BookmarksEntry getEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.getEntry(entryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _bookmarksEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public int getFoldersEntriesCount(
		long groupId, java.util.List<Long> folderIds) {

		return _bookmarksEntryLocalService.getFoldersEntriesCount(
			groupId, folderIds);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _bookmarksEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _bookmarksEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public BookmarksEntry moveEntry(long entryId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.moveEntry(entryId, parentFolderId);
	}

	@Override
	public BookmarksEntry moveEntryFromTrash(
			long userId, long entryId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.moveEntryFromTrash(
			userId, entryId, parentFolderId);
	}

	@Override
	public BookmarksEntry moveEntryToTrash(long userId, BookmarksEntry entry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.moveEntryToTrash(userId, entry);
	}

	@Override
	public BookmarksEntry moveEntryToTrash(long userId, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.moveEntryToTrash(userId, entryId);
	}

	@Override
	public BookmarksEntry openEntry(long userId, BookmarksEntry entry) {
		return _bookmarksEntryLocalService.openEntry(userId, entry);
	}

	@Override
	public BookmarksEntry openEntry(long userId, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.openEntry(userId, entryId);
	}

	@Override
	public BookmarksEntry restoreEntryFromTrash(long userId, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.restoreEntryFromTrash(
			userId, entryId);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.search(
			groupId, userId, creatorUserId, status, start, end);
	}

	@Override
	public void setTreePaths(long folderId, String treePath, boolean reindex)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksEntryLocalService.setTreePaths(folderId, treePath, reindex);
	}

	@Override
	public void subscribeEntry(long userId, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksEntryLocalService.subscribeEntry(userId, entryId);
	}

	@Override
	public void unsubscribeEntry(long userId, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksEntryLocalService.unsubscribeEntry(userId, entryId);
	}

	@Override
	public void updateAsset(
			long userId, BookmarksEntry entry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksEntryLocalService.updateAsset(
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
	@Override
	public BookmarksEntry updateBookmarksEntry(BookmarksEntry bookmarksEntry) {
		return _bookmarksEntryLocalService.updateBookmarksEntry(bookmarksEntry);
	}

	@Override
	public BookmarksEntry updateEntry(
			long userId, long entryId, long groupId, long folderId, String name,
			String url, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.updateEntry(
			userId, entryId, groupId, folderId, name, url, description,
			serviceContext);
	}

	@Override
	public BookmarksEntry updateStatus(
			long userId, BookmarksEntry entry, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksEntryLocalService.updateStatus(userId, entry, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _bookmarksEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<BookmarksEntry> getCTPersistence() {
		return _bookmarksEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<BookmarksEntry> getModelClass() {
		return _bookmarksEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<BookmarksEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _bookmarksEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public BookmarksEntryLocalService getWrappedService() {
		return _bookmarksEntryLocalService;
	}

	@Override
	public void setWrappedService(
		BookmarksEntryLocalService bookmarksEntryLocalService) {

		_bookmarksEntryLocalService = bookmarksEntryLocalService;
	}

	private BookmarksEntryLocalService _bookmarksEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:253502695
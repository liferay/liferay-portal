/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service;

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for BookmarksFolder. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see BookmarksFolderLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface BookmarksFolderLocalService
	extends BaseLocalService, CTService<BookmarksFolder>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.bookmarks.service.impl.BookmarksFolderLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the bookmarks folder local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link BookmarksFolderLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public BookmarksFolder addBookmarksFolder(BookmarksFolder bookmarksFolder);

	public BookmarksFolder addFolder(
			long userId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new bookmarks folder with the primary key. Does not add the bookmarks folder to the database.
	 *
	 * @param folderId the primary key for the new bookmarks folder
	 * @return the new bookmarks folder
	 */
	@Transactional(enabled = false)
	public BookmarksFolder createBookmarksFolder(long folderId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public BookmarksFolder deleteBookmarksFolder(
		BookmarksFolder bookmarksFolder);

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
	@Indexable(type = IndexableType.DELETE)
	public BookmarksFolder deleteBookmarksFolder(long folderId)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public BookmarksFolder deleteFolder(BookmarksFolder folder)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public BookmarksFolder deleteFolder(
			BookmarksFolder folder, boolean includeTrashedEntries)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public BookmarksFolder deleteFolder(long folderId) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public BookmarksFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws PortalException;

	public void deleteFolders(long groupId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksFolder fetchBookmarksFolder(long folderId);

	/**
	 * Returns the bookmarks folder matching the UUID and group.
	 *
	 * @param uuid the bookmarks folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks folder, or <code>null</code> if a matching bookmarks folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksFolder fetchBookmarksFolderByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the bookmarks folder with the primary key.
	 *
	 * @param folderId the primary key of the bookmarks folder
	 * @return the bookmarks folder
	 * @throws PortalException if a bookmarks folder with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksFolder getBookmarksFolder(long folderId)
		throws PortalException;

	/**
	 * Returns the bookmarks folder matching the UUID and group.
	 *
	 * @param uuid the bookmarks folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks folder
	 * @throws PortalException if a matching bookmarks folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksFolder getBookmarksFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getBookmarksFolders(int start, int end);

	/**
	 * Returns all the bookmarks folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks folders
	 * @param companyId the primary key of the company
	 * @return the matching bookmarks folders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getBookmarksFoldersByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getBookmarksFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BookmarksFolder> orderByComparator);

	/**
	 * Returns the number of bookmarks folders.
	 *
	 * @return the number of bookmarks folders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBookmarksFoldersCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getCompanyFolders(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyFoldersCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksFolder getFolder(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getFolders(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getFolders(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndEntries(long groupId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<?> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndEntriesCount(
		long groupId, long folderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long groupId, long parentFolderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksFolder> getNoAssetFolders();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void getSubfolderIds(
		List<Long> folderIds, long groupId, long folderId);

	public void mergeFolders(long folderId, long parentFolderId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksFolder moveFolder(long folderId, long parentFolderId)
		throws PortalException;

	public BookmarksFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksFolder moveFolderToTrash(long userId, long folderId)
		throws PortalException;

	public void rebuildTree(long companyId) throws PortalException;

	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksFolder restoreFolderFromTrash(long userId, long folderId)
		throws PortalException;

	public void subscribeFolder(long userId, long groupId, long folderId)
		throws PortalException;

	public void unsubscribeFolder(long userId, long groupId, long folderId)
		throws PortalException;

	public void updateAsset(
			long userId, BookmarksFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public BookmarksFolder updateBookmarksFolder(
		BookmarksFolder bookmarksFolder);

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksFolder updateFolder(
			long userId, long folderId, long parentFolderId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException;

	public BookmarksFolder updateStatus(
			long userId, BookmarksFolder folder, int status)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<BookmarksFolder> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<BookmarksFolder> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<BookmarksFolder>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1409271081
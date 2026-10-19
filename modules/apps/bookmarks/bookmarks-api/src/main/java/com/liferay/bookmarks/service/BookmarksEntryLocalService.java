/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service;

import com.liferay.bookmarks.model.BookmarksEntry;
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
import com.liferay.portal.kernel.search.Hits;
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
 * Provides the local service interface for BookmarksEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see BookmarksEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface BookmarksEntryLocalService
	extends BaseLocalService, CTService<BookmarksEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.bookmarks.service.impl.BookmarksEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the bookmarks entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link BookmarksEntryLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry addBookmarksEntry(BookmarksEntry bookmarksEntry);

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry addEntry(
			long userId, long groupId, long folderId, String name, String url,
			String description, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new bookmarks entry with the primary key. Does not add the bookmarks entry to the database.
	 *
	 * @param entryId the primary key for the new bookmarks entry
	 * @return the new bookmarks entry
	 */
	@Transactional(enabled = false)
	public BookmarksEntry createBookmarksEntry(long entryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public BookmarksEntry deleteBookmarksEntry(BookmarksEntry bookmarksEntry);

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
	@Indexable(type = IndexableType.DELETE)
	public BookmarksEntry deleteBookmarksEntry(long entryId)
		throws PortalException;

	public void deleteEntries(long groupId, long folderId)
		throws PortalException;

	public void deleteEntries(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public BookmarksEntry deleteEntry(BookmarksEntry entry)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public BookmarksEntry deleteEntry(long entryId) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.bookmarks.model.impl.BookmarksEntryModelImpl</code>.
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
	public BookmarksEntry fetchBookmarksEntry(long entryId);

	/**
	 * Returns the bookmarks entry matching the UUID and group.
	 *
	 * @param uuid the bookmarks entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks entry, or <code>null</code> if a matching bookmarks entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksEntry fetchBookmarksEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksEntry> getBookmarksEntries(int start, int end);

	/**
	 * Returns all the bookmarks entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the bookmarks entries
	 * @param companyId the primary key of the company
	 * @return the matching bookmarks entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksEntry> getBookmarksEntriesByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksEntry> getBookmarksEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BookmarksEntry> orderByComparator);

	/**
	 * Returns the number of bookmarks entries.
	 *
	 * @return the number of bookmarks entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBookmarksEntriesCount();

	/**
	 * Returns the bookmarks entry with the primary key.
	 *
	 * @param entryId the primary key of the bookmarks entry
	 * @return the bookmarks entry
	 * @throws PortalException if a bookmarks entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksEntry getBookmarksEntry(long entryId)
		throws PortalException;

	/**
	 * Returns the bookmarks entry matching the UUID and group.
	 *
	 * @param uuid the bookmarks entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching bookmarks entry
	 * @throws PortalException if a matching bookmarks entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksEntry getBookmarksEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksEntry> getEntries(
		long groupId, long folderId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksEntry> getEntries(
		long groupId, long folderId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksEntry> getEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<BookmarksEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BookmarksEntry> getEntries(
		long groupId, long folderId, int start, int end,
		OrderByComparator<BookmarksEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getEntriesCount(long groupId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getEntriesCount(long groupId, long folderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BookmarksEntry getEntry(long entryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersEntriesCount(long groupId, List<Long> folderIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry moveEntry(long entryId, long parentFolderId)
		throws PortalException;

	public BookmarksEntry moveEntryFromTrash(
			long userId, long entryId, long parentFolderId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry moveEntryToTrash(long userId, BookmarksEntry entry)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry moveEntryToTrash(long userId, long entryId)
		throws PortalException;

	@Transactional(enabled = false)
	public BookmarksEntry openEntry(long userId, BookmarksEntry entry);

	public BookmarksEntry openEntry(long userId, long entryId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry restoreEntryFromTrash(long userId, long entryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws PortalException;

	public void setTreePaths(long folderId, String treePath, boolean reindex)
		throws PortalException;

	public void subscribeEntry(long userId, long entryId)
		throws PortalException;

	public void unsubscribeEntry(long userId, long entryId)
		throws PortalException;

	public void updateAsset(
			long userId, BookmarksEntry entry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry updateBookmarksEntry(BookmarksEntry bookmarksEntry);

	@Indexable(type = IndexableType.REINDEX)
	public BookmarksEntry updateEntry(
			long userId, long entryId, long groupId, long folderId, String name,
			String url, String description, ServiceContext serviceContext)
		throws PortalException;

	public BookmarksEntry updateStatus(
			long userId, BookmarksEntry entry, int status)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<BookmarksEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<BookmarksEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<BookmarksEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1442334514
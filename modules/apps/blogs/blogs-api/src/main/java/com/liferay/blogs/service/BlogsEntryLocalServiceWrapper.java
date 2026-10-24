/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link BlogsEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see BlogsEntryLocalService
 * @generated
 */
public class BlogsEntryLocalServiceWrapper
	implements BlogsEntryLocalService, ServiceWrapper<BlogsEntryLocalService> {

	public BlogsEntryLocalServiceWrapper() {
		this(null);
	}

	public BlogsEntryLocalServiceWrapper(
		BlogsEntryLocalService blogsEntryLocalService) {

		_blogsEntryLocalService = blogsEntryLocalService;
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			addAttachmentFileEntry(
				String externalReferenceCode, long userId, long groupId,
				String fileName, String mimeType,
				java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addAttachmentFileEntry(
			externalReferenceCode, userId, groupId, fileName, mimeType,
			inputStream);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.Folder
			addAttachmentsFolder(long userId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addAttachmentsFolder(userId, groupId);
	}

	/**
	 * Adds the blogs entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BlogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param blogsEntry the blogs entry
	 * @return the blogs entry that was added
	 */
	@Override
	public BlogsEntry addBlogsEntry(BlogsEntry blogsEntry) {
		return _blogsEntryLocalService.addBlogsEntry(blogsEntry);
	}

	@Override
	public void addCoverImage(
			long entryId,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				imageSelector)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.addCoverImage(entryId, imageSelector);
	}

	@Override
	public BlogsEntry addEntry(
			long userId, String title, String content,
			java.util.Date displayDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addEntry(
			userId, title, content, displayDate, serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			long userId, String title, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addEntry(
			userId, title, content, serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			long userId, String title, String subtitle, String description,
			String content, java.util.Date displayDate, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addEntry(
			userId, title, subtitle, description, content, displayDate,
			allowPingbacks, allowTrackbacks, trackbacks, coverImageCaption,
			coverImageImageSelector, smallImageImageSelector, serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			long userId, String title, String subtitle, String description,
			String content, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addEntry(
			userId, title, subtitle, description, content, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			allowPingbacks, allowTrackbacks, trackbacks, coverImageCaption,
			coverImageImageSelector, smallImageImageSelector, serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			String externalReferenceCode, long userId, String title,
			String subtitle, String urlTitle, String description,
			String content, java.util.Date displayDate, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addEntry(
			externalReferenceCode, userId, title, subtitle, urlTitle,
			description, content, displayDate, allowPingbacks, allowTrackbacks,
			trackbacks, coverImageCaption, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			String externalReferenceCode, long userId, String title,
			String subtitle, String urlTitle, String description,
			String content, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addEntry(
			externalReferenceCode, userId, title, subtitle, urlTitle,
			description, content, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute, allowPingbacks,
			allowTrackbacks, trackbacks, coverImageCaption,
			coverImageImageSelector, smallImageImageSelector, serviceContext);
	}

	@Override
	public void addEntryResources(
			BlogsEntry entry, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.addEntryResources(
			entry, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			BlogsEntry entry,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.addEntryResources(entry, modelPermissions);
	}

	@Override
	public void addEntryResources(
			long entryId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.addEntryResources(
			entryId, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			long entryId,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.addEntryResources(entryId, modelPermissions);
	}

	@Override
	public long addOriginalImageFileEntry(
			long userId, long groupId, long entryId,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				imageSelector)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.addOriginalImageFileEntry(
			userId, groupId, entryId, imageSelector);
	}

	@Override
	public void addSmallImage(
			long entryId,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				imageSelector)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.addSmallImage(entryId, imageSelector);
	}

	@Override
	public void checkEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.checkEntries();
	}

	/**
	 * Creates a new blogs entry with the primary key. Does not add the blogs entry to the database.
	 *
	 * @param entryId the primary key for the new blogs entry
	 * @return the new blogs entry
	 */
	@Override
	public BlogsEntry createBlogsEntry(long entryId) {
		return _blogsEntryLocalService.createBlogsEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteAttachmentFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.deleteAttachmentFileEntry(fileEntryId);
	}

	/**
	 * Deletes the blogs entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BlogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param blogsEntry the blogs entry
	 * @return the blogs entry that was removed
	 */
	@Override
	public BlogsEntry deleteBlogsEntry(BlogsEntry blogsEntry) {
		return _blogsEntryLocalService.deleteBlogsEntry(blogsEntry);
	}

	/**
	 * Deletes the blogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BlogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry that was removed
	 * @throws PortalException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry deleteBlogsEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.deleteBlogsEntry(entryId);
	}

	@Override
	public void deleteEntries(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.deleteEntries(groupId);
	}

	@Override
	public BlogsEntry deleteEntry(BlogsEntry entry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.deleteEntry(entry);
	}

	@Override
	public void deleteEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.deleteEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _blogsEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _blogsEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _blogsEntryLocalService.dynamicQuery();
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

		return _blogsEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
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

		return _blogsEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
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

		return _blogsEntryLocalService.dynamicQuery(
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

		return _blogsEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _blogsEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.Folder
		fetchAttachmentsFolder(long userId, long groupId) {

		return _blogsEntryLocalService.fetchAttachmentsFolder(userId, groupId);
	}

	@Override
	public BlogsEntry fetchBlogsEntry(long entryId) {
		return _blogsEntryLocalService.fetchBlogsEntry(entryId);
	}

	@Override
	public BlogsEntry fetchBlogsEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _blogsEntryLocalService.fetchBlogsEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the blogs entry matching the UUID and group.
	 *
	 * @param uuid the blogs entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry fetchBlogsEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _blogsEntryLocalService.fetchBlogsEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public BlogsEntry fetchEntry(long groupId, String urlTitle) {
		return _blogsEntryLocalService.fetchEntry(groupId, urlTitle);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _blogsEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			getAttachmentFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getAttachmentFileEntry(fileEntryId);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			getAttachmentFileEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.
			getAttachmentFileEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns a range of all the blogs entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of blogs entries
	 */
	@Override
	public java.util.List<BlogsEntry> getBlogsEntries(int start, int end) {
		return _blogsEntryLocalService.getBlogsEntries(start, end);
	}

	/**
	 * Returns all the blogs entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the blogs entries
	 * @param companyId the primary key of the company
	 * @return the matching blogs entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<BlogsEntry> getBlogsEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _blogsEntryLocalService.getBlogsEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of blogs entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the blogs entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching blogs entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<BlogsEntry> getBlogsEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<BlogsEntry>
			orderByComparator) {

		return _blogsEntryLocalService.getBlogsEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of blogs entries.
	 *
	 * @return the number of blogs entries
	 */
	@Override
	public int getBlogsEntriesCount() {
		return _blogsEntryLocalService.getBlogsEntriesCount();
	}

	/**
	 * Returns the blogs entry with the primary key.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry
	 * @throws PortalException if a blogs entry with the primary key could not be found
	 */
	@Override
	public BlogsEntry getBlogsEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getBlogsEntry(entryId);
	}

	@Override
	public BlogsEntry getBlogsEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getBlogsEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the blogs entry matching the UUID and group.
	 *
	 * @param uuid the blogs entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching blogs entry
	 * @throws PortalException if a matching blogs entry could not be found
	 */
	@Override
	public BlogsEntry getBlogsEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getBlogsEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.List<BlogsEntry> getCompanyEntries(
		long companyId, java.util.Date displayDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getCompanyEntries(
			companyId, displayDate, queryDefinition);
	}

	@Override
	public int getCompanyEntriesCount(
		long companyId, java.util.Date displayDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getCompanyEntriesCount(
			companyId, displayDate, queryDefinition);
	}

	@Override
	public BlogsEntry[] getEntriesPrevAndNext(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getEntriesPrevAndNext(entryId);
	}

	@Override
	public BlogsEntry getEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getEntry(entryId);
	}

	@Override
	public BlogsEntry getEntry(long groupId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getEntry(groupId, urlTitle);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _blogsEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<BlogsEntry> getGroupEntries(
		long groupId, java.util.Date displayDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getGroupEntries(
			groupId, displayDate, queryDefinition);
	}

	@Override
	public java.util.List<BlogsEntry> getGroupEntries(
		long groupId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getGroupEntries(
			groupId, queryDefinition);
	}

	@Override
	public int getGroupEntriesCount(
		long groupId, java.util.Date displayDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getGroupEntriesCount(
			groupId, displayDate, queryDefinition);
	}

	@Override
	public int getGroupEntriesCount(
		long groupId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getGroupEntriesCount(
			groupId, queryDefinition);
	}

	@Override
	public java.util.List<BlogsEntry> getGroupsEntries(
		long companyId, long groupId, java.util.Date displayDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getGroupsEntries(
			companyId, groupId, displayDate, queryDefinition);
	}

	@Override
	public java.util.List<BlogsEntry> getGroupUserEntries(
		long groupId, long userId, java.util.Date displayDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getGroupUserEntries(
			groupId, userId, displayDate, queryDefinition);
	}

	@Override
	public int getGroupUserEntriesCount(
		long groupId, long userId, java.util.Date displayDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<BlogsEntry>
			queryDefinition) {

		return _blogsEntryLocalService.getGroupUserEntriesCount(
			groupId, userId, displayDate, queryDefinition);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _blogsEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _blogsEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public String getUniqueUrlTitle(BlogsEntry entry) {
		return _blogsEntryLocalService.getUniqueUrlTitle(entry);
	}

	@Override
	public void moveEntriesToTrash(long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.moveEntriesToTrash(groupId, userId);
	}

	/**
	 * Moves the blogs entry to the recycle bin. Social activity counters for
	 * this entry get disabled.
	 *
	 * @param userId the primary key of the user moving the blogs entry
	 * @param entry the blogs entry to be moved
	 * @return the moved blogs entry
	 */
	@Override
	public BlogsEntry moveEntryToTrash(long userId, BlogsEntry entry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.moveEntryToTrash(userId, entry);
	}

	/**
	 * Moves the blogs entry with the ID to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the blogs entry
	 * @param entryId the primary key of the blogs entry to be moved
	 * @return the moved blogs entry
	 */
	@Override
	public BlogsEntry moveEntryToTrash(long userId, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.moveEntryToTrash(userId, entryId);
	}

	/**
	 * Restores the blogs entry with the ID from the recycle bin. Social
	 * activity counters for this entry get activated.
	 *
	 * @param userId the primary key of the user restoring the blogs entry
	 * @param entryId the primary key of the blogs entry to be restored
	 * @return the restored blogs entry from the recycle bin
	 */
	@Override
	public BlogsEntry restoreEntryFromTrash(long userId, long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.restoreEntryFromTrash(userId, entryId);
	}

	@Override
	public void subscribe(long userId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.subscribe(userId, groupId);
	}

	@Override
	public void unsubscribe(long userId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.unsubscribe(userId, groupId);
	}

	@Override
	public void updateAsset(
			long userId, BlogsEntry entry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.updateAsset(
			userId, entry, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	/**
	 * Updates the blogs entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BlogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param blogsEntry the blogs entry
	 * @return the blogs entry that was updated
	 */
	@Override
	public BlogsEntry updateBlogsEntry(BlogsEntry blogsEntry) {
		return _blogsEntryLocalService.updateBlogsEntry(blogsEntry);
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.updateEntry(
			userId, entryId, title, content, serviceContext);
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String description, String content, java.util.Date displayDate,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.updateEntry(
			userId, entryId, title, subtitle, description, content, displayDate,
			allowPingbacks, allowTrackbacks, trackbacks, coverImageCaption,
			coverImageImageSelector, smallImageImageSelector, serviceContext);
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String description, String content, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.updateEntry(
			userId, entryId, title, subtitle, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String urlTitle, String description, String content,
			java.util.Date displayDate, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.updateEntry(
			userId, entryId, title, subtitle, urlTitle, description, content,
			displayDate, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String urlTitle, String description, String content,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				coverImageImageSelector,
			com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector
				smallImageImageSelector,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.updateEntry(
			userId, entryId, title, subtitle, urlTitle, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	@Override
	public void updateEntryResources(
			BlogsEntry entry,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.updateEntryResources(entry, modelPermissions);
	}

	@Override
	public void updateEntryResources(
			BlogsEntry entry, String[] groupPermissions,
			String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_blogsEntryLocalService.updateEntryResources(
			entry, groupPermissions, guestPermissions);
	}

	@Override
	public BlogsEntry updateStatus(
			long userId, long entryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _blogsEntryLocalService.updateStatus(
			userId, entryId, status, serviceContext, workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _blogsEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<BlogsEntry> getCTPersistence() {
		return _blogsEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<BlogsEntry> getModelClass() {
		return _blogsEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<BlogsEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _blogsEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public BlogsEntryLocalService getWrappedService() {
		return _blogsEntryLocalService;
	}

	@Override
	public void setWrappedService(
		BlogsEntryLocalService blogsEntryLocalService) {

		_blogsEntryLocalService = blogsEntryLocalService;
	}

	private BlogsEntryLocalService _blogsEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1807758802
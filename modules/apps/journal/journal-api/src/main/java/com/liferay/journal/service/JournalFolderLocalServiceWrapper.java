/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link JournalFolderLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see JournalFolderLocalService
 * @generated
 */
public class JournalFolderLocalServiceWrapper
	implements JournalFolderLocalService,
			   ServiceWrapper<JournalFolderLocalService> {

	public JournalFolderLocalServiceWrapper() {
		this(null);
	}

	public JournalFolderLocalServiceWrapper(
		JournalFolderLocalService journalFolderLocalService) {

		_journalFolderLocalService = journalFolderLocalService;
	}

	@Override
	public JournalFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.addFolder(
			externalReferenceCode, userId, groupId, parentFolderId, name,
			description, serviceContext);
	}

	/**
	 * Adds the journal folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFolder the journal folder
	 * @return the journal folder that was added
	 */
	@Override
	public JournalFolder addJournalFolder(JournalFolder journalFolder) {
		return _journalFolderLocalService.addJournalFolder(journalFolder);
	}

	/**
	 * Creates a new journal folder with the primary key. Does not add the journal folder to the database.
	 *
	 * @param folderId the primary key for the new journal folder
	 * @return the new journal folder
	 */
	@Override
	public JournalFolder createJournalFolder(long folderId) {
		return _journalFolderLocalService.createJournalFolder(folderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public JournalFolder deleteFolder(JournalFolder folder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.deleteFolder(folder);
	}

	@Override
	public JournalFolder deleteFolder(
			JournalFolder folder, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.deleteFolder(
			folder, includeTrashedEntries);
	}

	@Override
	public JournalFolder deleteFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.deleteFolder(folderId);
	}

	@Override
	public JournalFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.deleteFolder(
			folderId, includeTrashedEntries);
	}

	@Override
	public void deleteFolders(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.deleteFolders(groupId);
	}

	/**
	 * Deletes the journal folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFolder the journal folder
	 * @return the journal folder that was removed
	 */
	@Override
	public JournalFolder deleteJournalFolder(JournalFolder journalFolder) {
		return _journalFolderLocalService.deleteJournalFolder(journalFolder);
	}

	/**
	 * Deletes the journal folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder that was removed
	 * @throws PortalException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder deleteJournalFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.deleteJournalFolder(folderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _journalFolderLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _journalFolderLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _journalFolderLocalService.dynamicQuery();
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

		return _journalFolderLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFolderModelImpl</code>.
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

		return _journalFolderLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFolderModelImpl</code>.
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

		return _journalFolderLocalService.dynamicQuery(
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

		return _journalFolderLocalService.dynamicQueryCount(dynamicQuery);
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

		return _journalFolderLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public JournalFolder fetchFolder(long folderId) {
		return _journalFolderLocalService.fetchFolder(folderId);
	}

	@Override
	public JournalFolder fetchFolder(
		long groupId, long parentFolderId, String name) {

		return _journalFolderLocalService.fetchFolder(
			groupId, parentFolderId, name);
	}

	@Override
	public JournalFolder fetchFolder(long groupId, String name) {
		return _journalFolderLocalService.fetchFolder(groupId, name);
	}

	@Override
	public JournalFolder fetchJournalFolder(long folderId) {
		return _journalFolderLocalService.fetchJournalFolder(folderId);
	}

	@Override
	public JournalFolder fetchJournalFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _journalFolderLocalService.
			fetchJournalFolderByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the journal folder matching the UUID and group.
	 *
	 * @param uuid the journal folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal folder, or <code>null</code> if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder fetchJournalFolderByUuidAndGroupId(
		String uuid, long groupId) {

		return _journalFolderLocalService.fetchJournalFolderByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _journalFolderLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<JournalFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return _journalFolderLocalService.getCompanyFolders(
			companyId, start, end);
	}

	@Override
	public int getCompanyFoldersCount(long companyId) {
		return _journalFolderLocalService.getCompanyFoldersCount(companyId);
	}

	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getDDMStructures(
				long[] groupIds, long folderId, int restrictionType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.getDDMStructures(
			groupIds, folderId, restrictionType);
	}

	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getDDMStructures(
				long[] groupIds, long folderId, int restrictionType,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMStructure>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.getDDMStructures(
			groupIds, folderId, restrictionType, orderByComparator);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _journalFolderLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public JournalFolder getFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.getFolder(folderId);
	}

	@Override
	public java.util.List<JournalFolder> getFolders(long groupId) {
		return _journalFolderLocalService.getFolders(groupId);
	}

	@Override
	public java.util.List<JournalFolder> getFolders(
		long groupId, long parentFolderId) {

		return _journalFolderLocalService.getFolders(groupId, parentFolderId);
	}

	@Override
	public java.util.List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status) {

		return _journalFolderLocalService.getFolders(
			groupId, parentFolderId, status);
	}

	@Override
	public java.util.List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int start, int end) {

		return _journalFolderLocalService.getFolders(
			groupId, parentFolderId, start, end);
	}

	@Override
	public java.util.List<JournalFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end) {

		return _journalFolderLocalService.getFolders(
			groupId, parentFolderId, status, start, end);
	}

	@Override
	public java.util.List<Object> getFoldersAndArticles(
		long groupId, long folderId) {

		return _journalFolderLocalService.getFoldersAndArticles(
			groupId, folderId);
	}

	@Override
	public java.util.List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status) {

		return _journalFolderLocalService.getFoldersAndArticles(
			groupId, folderId, status);
	}

	@Override
	public java.util.List<Object> getFoldersAndArticles(
		long groupId, long folderId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<?> orderByComparator) {

		return _journalFolderLocalService.getFoldersAndArticles(
			groupId, folderId, status, start, end, orderByComparator);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, java.util.List<Long> folderIds, int status) {

		return _journalFolderLocalService.getFoldersAndArticlesCount(
			groupId, folderIds, status);
	}

	@Override
	public int getFoldersAndArticlesCount(
		long groupId, long folderId, int status) {

		return _journalFolderLocalService.getFoldersAndArticlesCount(
			groupId, folderId, status);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return _journalFolderLocalService.getFoldersCount(
			groupId, parentFolderId);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId, int status) {
		return _journalFolderLocalService.getFoldersCount(
			groupId, parentFolderId, status);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _journalFolderLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public long getInheritedWorkflowFolderId(long folderId)
		throws com.liferay.journal.exception.NoSuchFolderException {

		return _journalFolderLocalService.getInheritedWorkflowFolderId(
			folderId);
	}

	/**
	 * Returns the journal folder with the primary key.
	 *
	 * @param folderId the primary key of the journal folder
	 * @return the journal folder
	 * @throws PortalException if a journal folder with the primary key could not be found
	 */
	@Override
	public JournalFolder getJournalFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.getJournalFolder(folderId);
	}

	@Override
	public JournalFolder getJournalFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.
			getJournalFolderByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the journal folder matching the UUID and group.
	 *
	 * @param uuid the journal folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal folder
	 * @throws PortalException if a matching journal folder could not be found
	 */
	@Override
	public JournalFolder getJournalFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.getJournalFolderByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the journal folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @return the range of journal folders
	 */
	@Override
	public java.util.List<JournalFolder> getJournalFolders(int start, int end) {
		return _journalFolderLocalService.getJournalFolders(start, end);
	}

	/**
	 * Returns all the journal folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal folders
	 * @param companyId the primary key of the company
	 * @return the matching journal folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<JournalFolder> getJournalFoldersByUuidAndCompanyId(
		String uuid, long companyId) {

		return _journalFolderLocalService.getJournalFoldersByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of journal folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of journal folders
	 * @param end the upper bound of the range of journal folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching journal folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<JournalFolder> getJournalFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalFolder>
			orderByComparator) {

		return _journalFolderLocalService.getJournalFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of journal folders.
	 *
	 * @return the number of journal folders
	 */
	@Override
	public int getJournalFoldersCount() {
		return _journalFolderLocalService.getJournalFoldersCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _journalFolderLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public long getOverridedDDMStructuresFolderId(long folderId)
		throws com.liferay.journal.exception.NoSuchFolderException {

		return _journalFolderLocalService.getOverridedDDMStructuresFolderId(
			folderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void getSubfolderIds(
		java.util.List<Long> folderIds, long groupId, long folderId) {

		_journalFolderLocalService.getSubfolderIds(
			folderIds, groupId, folderId);
	}

	@Override
	public String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name,
		int count) {

		return _journalFolderLocalService.getUniqueFolderName(
			uuid, groupId, parentFolderId, name, count);
	}

	@Override
	public JournalFolder moveFolder(
			long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.moveFolder(
			folderId, parentFolderId, serviceContext);
	}

	@Override
	public JournalFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.moveFolderFromTrash(
			userId, folderId, parentFolderId, serviceContext);
	}

	@Override
	public JournalFolder moveFolderToTrash(long userId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.moveFolderToTrash(userId, folderId);
	}

	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.rebuildTree(companyId);
	}

	@Override
	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.rebuildTree(
			companyId, parentFolderId, parentTreePath, reindex);
	}

	@Override
	public void restoreFolderFromTrash(long userId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.restoreFolderFromTrash(userId, folderId);
	}

	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			searchDDMStructures(
				long companyId, long[] groupIds, long folderId,
				int restrictionType, String keywords, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMStructure>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.searchDDMStructures(
			companyId, groupIds, folderId, restrictionType, keywords, start,
			end, orderByComparator);
	}

	@Override
	public void subscribe(long userId, long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.subscribe(userId, groupId, folderId);
	}

	@Override
	public void unsubscribe(long userId, long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.unsubscribe(userId, groupId, folderId);
	}

	@Override
	public void updateAsset(
			long userId, JournalFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.updateAsset(
			userId, folder, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	@Override
	public JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, boolean mergeWithParentFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.updateFolder(
			userId, groupId, folderId, parentFolderId, name, description,
			mergeWithParentFolder, serviceContext);
	}

	@Override
	public JournalFolder updateFolder(
			long userId, long groupId, long folderId, long parentFolderId,
			String name, String description, long[] ddmStructureIds,
			int restrictionType, boolean mergeWithParentFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.updateFolder(
			userId, groupId, folderId, parentFolderId, name, description,
			ddmStructureIds, restrictionType, mergeWithParentFolder,
			serviceContext);
	}

	@Override
	public void updateFolderDDMStructures(
			JournalFolder folder, long[] ddmStructureIdsArray)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.updateFolderDDMStructures(
			folder, ddmStructureIdsArray);
	}

	/**
	 * Updates the journal folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFolder the journal folder
	 * @return the journal folder that was updated
	 */
	@Override
	public JournalFolder updateJournalFolder(JournalFolder journalFolder) {
		return _journalFolderLocalService.updateJournalFolder(journalFolder);
	}

	@Override
	public JournalFolder updateStatus(
			long userId, JournalFolder folder, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalFolderLocalService.updateStatus(userId, folder, status);
	}

	@Override
	public void validateFolderDDMStructures(long folderId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalFolderLocalService.validateFolderDDMStructures(
			folderId, parentFolderId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _journalFolderLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<JournalFolder> getCTPersistence() {
		return _journalFolderLocalService.getCTPersistence();
	}

	@Override
	public Class<JournalFolder> getModelClass() {
		return _journalFolderLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalFolder>, R, E>
				updateUnsafeFunction)
		throws E {

		return _journalFolderLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public JournalFolderLocalService getWrappedService() {
		return _journalFolderLocalService;
	}

	@Override
	public void setWrappedService(
		JournalFolderLocalService journalFolderLocalService) {

		_journalFolderLocalService = journalFolderLocalService;
	}

	private JournalFolderLocalService _journalFolderLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1618042301
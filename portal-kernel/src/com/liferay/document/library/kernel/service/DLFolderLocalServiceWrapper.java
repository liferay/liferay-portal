/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DLFolderLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLFolderLocalService
 * @generated
 */
public class DLFolderLocalServiceWrapper
	implements DLFolderLocalService, ServiceWrapper<DLFolderLocalService> {

	public DLFolderLocalServiceWrapper() {
		this(null);
	}

	public DLFolderLocalServiceWrapper(
		DLFolderLocalService dlFolderLocalService) {

		_dlFolderLocalService = dlFolderLocalService;
	}

	@Override
	public boolean addDLFileEntryTypeDLFolder(
		long fileEntryTypeId, DLFolder dlFolder) {

		return _dlFolderLocalService.addDLFileEntryTypeDLFolder(
			fileEntryTypeId, dlFolder);
	}

	@Override
	public boolean addDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		return _dlFolderLocalService.addDLFileEntryTypeDLFolder(
			fileEntryTypeId, folderId);
	}

	@Override
	public boolean addDLFileEntryTypeDLFolders(
		long fileEntryTypeId, java.util.List<DLFolder> dlFolders) {

		return _dlFolderLocalService.addDLFileEntryTypeDLFolders(
			fileEntryTypeId, dlFolders);
	}

	@Override
	public boolean addDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		return _dlFolderLocalService.addDLFileEntryTypeDLFolders(
			fileEntryTypeId, folderIds);
	}

	/**
	 * Adds the document library folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was added
	 */
	@Override
	public DLFolder addDLFolder(DLFolder dlFolder) {
		return _dlFolderLocalService.addDLFolder(dlFolder);
	}

	@Override
	public DLFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, boolean mountPoint, long parentFolderId,
			String name, String description, boolean hidden,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.addFolder(
			externalReferenceCode, userId, groupId, repositoryId, mountPoint,
			parentFolderId, name, description, hidden, serviceContext);
	}

	@Override
	public void clearDLFileEntryTypeDLFolders(long fileEntryTypeId) {
		_dlFolderLocalService.clearDLFileEntryTypeDLFolders(fileEntryTypeId);
	}

	/**
	 * Creates a new document library folder with the primary key. Does not add the document library folder to the database.
	 *
	 * @param folderId the primary key for the new document library folder
	 * @return the new document library folder
	 */
	@Override
	public DLFolder createDLFolder(long folderId) {
		return _dlFolderLocalService.createDLFolder(folderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteAllByGroup(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFolderLocalService.deleteAllByGroup(groupId);
	}

	@Override
	public void deleteAllByRepository(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFolderLocalService.deleteAllByRepository(repositoryId);
	}

	@Override
	public void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId, DLFolder dlFolder) {

		_dlFolderLocalService.deleteDLFileEntryTypeDLFolder(
			fileEntryTypeId, dlFolder);
	}

	@Override
	public void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		_dlFolderLocalService.deleteDLFileEntryTypeDLFolder(
			fileEntryTypeId, folderId);
	}

	@Override
	public void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId, java.util.List<DLFolder> dlFolders) {

		_dlFolderLocalService.deleteDLFileEntryTypeDLFolders(
			fileEntryTypeId, dlFolders);
	}

	@Override
	public void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		_dlFolderLocalService.deleteDLFileEntryTypeDLFolders(
			fileEntryTypeId, folderIds);
	}

	/**
	 * Deletes the document library folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was removed
	 */
	@Override
	public DLFolder deleteDLFolder(DLFolder dlFolder) {
		return _dlFolderLocalService.deleteDLFolder(dlFolder);
	}

	/**
	 * Deletes the document library folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder that was removed
	 * @throws PortalException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder deleteDLFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.deleteDLFolder(folderId);
	}

	@Override
	public DLFolder deleteFolder(DLFolder dlFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.deleteFolder(dlFolder);
	}

	@Override
	public DLFolder deleteFolder(
			DLFolder dlFolder, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.deleteFolder(
			dlFolder, includeTrashedEntries);
	}

	@Override
	public DLFolder deleteFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.deleteFolder(folderId);
	}

	@Override
	public DLFolder deleteFolder(long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.deleteFolder(
			folderId, includeTrashedEntries);
	}

	@Override
	public DLFolder deleteFolder(
			long userId, long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.deleteFolder(
			userId, folderId, includeTrashedEntries);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlFolderLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlFolderLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlFolderLocalService.dynamicQuery();
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

		return _dlFolderLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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

		return _dlFolderLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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

		return _dlFolderLocalService.dynamicQuery(
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

		return _dlFolderLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dlFolderLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DLFolder fetchDLFolder(long folderId) {
		return _dlFolderLocalService.fetchDLFolder(folderId);
	}

	@Override
	public DLFolder fetchDLFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library folder matching the UUID and group.
	 *
	 * @param uuid the document library folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Override
	public DLFolder fetchDLFolderByUuidAndGroupId(String uuid, long groupId) {
		return _dlFolderLocalService.fetchDLFolderByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public DLFolder fetchFolder(long folderId) {
		return _dlFolderLocalService.fetchFolder(folderId);
	}

	@Override
	public DLFolder fetchFolder(
		long groupId, long parentFolderId, String name) {

		return _dlFolderLocalService.fetchFolder(groupId, parentFolderId, name);
	}

	@Override
	public DLFolder fetchFolder(String uuid, long groupId) {
		return _dlFolderLocalService.fetchFolder(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlFolderLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<DLFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return _dlFolderLocalService.getCompanyFolders(companyId, start, end);
	}

	@Override
	public int getCompanyFoldersCount(long companyId) {
		return _dlFolderLocalService.getCompanyFoldersCount(companyId);
	}

	@Override
	public java.util.List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId) {

		return _dlFolderLocalService.getDLFileEntryTypeDLFolders(
			fileEntryTypeId);
	}

	@Override
	public java.util.List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId, int start, int end) {

		return _dlFolderLocalService.getDLFileEntryTypeDLFolders(
			fileEntryTypeId, start, end);
	}

	@Override
	public java.util.List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return _dlFolderLocalService.getDLFileEntryTypeDLFolders(
			fileEntryTypeId, start, end, orderByComparator);
	}

	@Override
	public int getDLFileEntryTypeDLFoldersCount(long fileEntryTypeId) {
		return _dlFolderLocalService.getDLFileEntryTypeDLFoldersCount(
			fileEntryTypeId);
	}

	/**
	 * Returns the fileEntryTypeIds of the document library file entry types associated with the document library folder.
	 *
	 * @param folderId the folderId of the document library folder
	 * @return long[] the fileEntryTypeIds of document library file entry types associated with the document library folder
	 */
	@Override
	public long[] getDLFileEntryTypePrimaryKeys(long folderId) {
		return _dlFolderLocalService.getDLFileEntryTypePrimaryKeys(folderId);
	}

	/**
	 * Returns the document library folder with the primary key.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder
	 * @throws PortalException if a document library folder with the primary key could not be found
	 */
	@Override
	public DLFolder getDLFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.getDLFolder(folderId);
	}

	@Override
	public DLFolder getDLFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.getDLFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library folder matching the UUID and group.
	 *
	 * @param uuid the document library folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library folder
	 * @throws PortalException if a matching document library folder could not be found
	 */
	@Override
	public DLFolder getDLFolderByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.getDLFolderByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the document library folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of document library folders
	 */
	@Override
	public java.util.List<DLFolder> getDLFolders(int start, int end) {
		return _dlFolderLocalService.getDLFolders(start, end);
	}

	/**
	 * Returns all the document library folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library folders
	 * @param companyId the primary key of the company
	 * @return the matching document library folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DLFolder> getDLFoldersByUuidAndCompanyId(
		String uuid, long companyId) {

		return _dlFolderLocalService.getDLFoldersByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of document library folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DLFolder> getDLFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return _dlFolderLocalService.getDLFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library folders.
	 *
	 * @return the number of document library folders
	 */
	@Override
	public int getDLFoldersCount() {
		return _dlFolderLocalService.getDLFoldersCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _dlFolderLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<Object> getFileEntriesAndFileShortcuts(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return _dlFolderLocalService.getFileEntriesAndFileShortcuts(
			groupId, folderId, queryDefinition);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return _dlFolderLocalService.getFileEntriesAndFileShortcutsCount(
			groupId, folderId, queryDefinition);
	}

	@Override
	public DLFolder getFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.getFolder(folderId);
	}

	@Override
	public DLFolder getFolder(long groupId, long parentFolderId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.getFolder(groupId, parentFolderId, name);
	}

	@Override
	public long getFolderId(long companyId, long folderId) {
		return _dlFolderLocalService.getFolderId(companyId, folderId);
	}

	@Override
	public java.util.List<DLFolder> getFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return _dlFolderLocalService.getFolders(
			groupId, mountPoint, treePath, hidden);
	}

	@Override
	public java.util.List<DLFolder> getFolders(
		long groupId, long parentFolderId) {

		return _dlFolderLocalService.getFolders(groupId, parentFolderId);
	}

	@Override
	public java.util.List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders) {

		return _dlFolderLocalService.getFolders(
			groupId, parentFolderId, includeMountfolders);
	}

	@Override
	public java.util.List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return _dlFolderLocalService.getFolders(
			groupId, parentFolderId, includeMountfolders, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return _dlFolderLocalService.getFolders(
			groupId, parentFolderId, includeMountfolders, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<DLFolder> getFolders(
		long groupId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return _dlFolderLocalService.getFolders(
			groupId, parentFolderId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFolder> getFolders(
		long classNameId, String treePath) {

		return _dlFolderLocalService.getFolders(classNameId, treePath);
	}

	@Override
	public java.util.List<Object> getFoldersAndFileEntriesAndFileShortcuts(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return _dlFolderLocalService.getFoldersAndFileEntriesAndFileShortcuts(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return _dlFolderLocalService.
			getFoldersAndFileEntriesAndFileShortcutsCount(
				groupId, folderId, mimeTypes, includeMountFolders,
				queryDefinition);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return _dlFolderLocalService.getFoldersCount(groupId, parentFolderId);
	}

	@Override
	public int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders) {

		return _dlFolderLocalService.getFoldersCount(
			groupId, parentFolderId, includeMountfolders);
	}

	@Override
	public int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status) {

		return _dlFolderLocalService.getFoldersCount(
			groupId, parentFolderId, includeMountfolders, status);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFoldersCount(long, long, boolean, int)}
	 */
	@Deprecated
	@Override
	public int getFoldersCount(
		long groupId, long parentFolderId, int status,
		boolean includeMountfolders) {

		return _dlFolderLocalService.getFoldersCount(
			groupId, parentFolderId, status, includeMountfolders);
	}

	@Override
	public long getFolderSize(long companyId, long groupId, String treePath) {
		return _dlFolderLocalService.getFolderSize(
			companyId, groupId, treePath);
	}

	@Override
	public java.util.List<Long> getGroupFolderIds(
		long groupId, long parentFolderId) {

		return _dlFolderLocalService.getGroupFolderIds(groupId, parentFolderId);
	}

	@Override
	public void getGroupSubfolderIds(
		java.util.List<Long> folderIds, long groupId, long folderId) {

		_dlFolderLocalService.getGroupSubfolderIds(
			folderIds, groupId, folderId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlFolderLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public DLFolder getMountFolder(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.getMountFolder(repositoryId);
	}

	@Override
	public java.util.List<DLFolder> getMountFolders(
		long groupId, long parentFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFolder>
			orderByComparator) {

		return _dlFolderLocalService.getMountFolders(
			groupId, parentFolderId, start, end, orderByComparator);
	}

	@Override
	public int getMountFoldersCount(long groupId, long parentFolderId) {
		return _dlFolderLocalService.getMountFoldersCount(
			groupId, parentFolderId);
	}

	@Override
	public java.util.List<DLFolder> getNoAssetFolders() {
		return _dlFolderLocalService.getNoAssetFolders();
	}

	@Override
	public java.util.List<DLFolder> getNotInTrashFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return _dlFolderLocalService.getNotInTrashFolders(
			groupId, mountPoint, treePath, hidden);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlFolderLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<Long> getRepositoryFolderIds(
		long repositoryId, long parentFolderId) {

		return _dlFolderLocalService.getRepositoryFolderIds(
			repositoryId, parentFolderId);
	}

	@Override
	public java.util.List<DLFolder> getRepositoryFolders(
		long repositoryId, int start, int end) {

		return _dlFolderLocalService.getRepositoryFolders(
			repositoryId, start, end);
	}

	@Override
	public int getRepositoryFoldersCount(long repositoryId) {
		return _dlFolderLocalService.getRepositoryFoldersCount(repositoryId);
	}

	@Override
	public void getRepositorySubfolderIds(
		java.util.List<Long> folderIds, long repositoryId, long folderId) {

		_dlFolderLocalService.getRepositorySubfolderIds(
			folderIds, repositoryId, folderId);
	}

	@Override
	public String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name,
		int count) {

		return _dlFolderLocalService.getUniqueFolderName(
			uuid, groupId, parentFolderId, name, count);
	}

	@Override
	public boolean hasDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		return _dlFolderLocalService.hasDLFileEntryTypeDLFolder(
			fileEntryTypeId, folderId);
	}

	@Override
	public boolean hasDLFileEntryTypeDLFolders(long fileEntryTypeId) {
		return _dlFolderLocalService.hasDLFileEntryTypeDLFolders(
			fileEntryTypeId);
	}

	@Override
	public boolean hasFolderLock(long userId, long folderId) {
		return _dlFolderLocalService.hasFolderLock(userId, folderId);
	}

	@Override
	public boolean hasInheritableLock(long folderId) {
		return _dlFolderLocalService.hasInheritableLock(folderId);
	}

	@Override
	public com.liferay.portal.kernel.lock.Lock lockFolder(
			long userId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.lockFolder(userId, folderId);
	}

	@Override
	public com.liferay.portal.kernel.lock.Lock lockFolder(
			long userId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.lockFolder(
			userId, folderId, owner, inheritable, expirationTime);
	}

	@Override
	public DLFolder moveFolder(
			long userId, long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.moveFolder(
			userId, folderId, parentFolderId, serviceContext);
	}

	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFolderLocalService.rebuildTree(companyId);
	}

	@Override
	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFolderLocalService.rebuildTree(
			companyId, parentFolderId, parentTreePath, reindex);
	}

	@Override
	public void setDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		_dlFolderLocalService.setDLFileEntryTypeDLFolders(
			fileEntryTypeId, folderIds);
	}

	@Override
	public void unlockFolder(
			long groupId, long parentFolderId, String name, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFolderLocalService.unlockFolder(
			groupId, parentFolderId, name, lockUuid);
	}

	@Override
	public void unlockFolder(long folderId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFolderLocalService.unlockFolder(folderId, lockUuid);
	}

	/**
	 * Updates the document library folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was updated
	 */
	@Override
	public DLFolder updateDLFolder(DLFolder dlFolder) {
		return _dlFolderLocalService.updateDLFolder(dlFolder);
	}

	@Override
	public DLFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			long defaultFileEntryTypeId, java.util.List<Long> fileEntryTypeIds,
			int restrictionType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.updateFolder(
			folderId, parentFolderId, name, description, defaultFileEntryTypeId,
			fileEntryTypeIds, restrictionType, serviceContext);
	}

	@Override
	public DLFolder updateFolder(
			long folderId, String name, String description,
			long defaultFileEntryTypeId, java.util.List<Long> fileEntryTypeIds,
			int restrictionType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.updateFolder(
			folderId, name, description, defaultFileEntryTypeId,
			fileEntryTypeIds, restrictionType, serviceContext);
	}

	@Override
	public DLFolder updateFolderAndFileEntryTypes(
			long userId, long folderId, long parentFolderId, String name,
			String description, long defaultFileEntryTypeId,
			java.util.List<Long> fileEntryTypeIds, int restrictionType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.updateFolderAndFileEntryTypes(
			userId, folderId, parentFolderId, name, description,
			defaultFileEntryTypeId, fileEntryTypeIds, restrictionType,
			serviceContext);
	}

	@Override
	public void updateLastPostDate(long folderId, java.util.Date lastPostDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFolderLocalService.updateLastPostDate(folderId, lastPostDate);
	}

	@Override
	public DLFolder updateStatus(
			long userId, long folderId, int status,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.updateStatus(
			userId, folderId, status, workflowContext, serviceContext);
	}

	@Override
	public boolean verifyInheritableLock(long folderId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFolderLocalService.verifyInheritableLock(folderId, lockUuid);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlFolderLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DLFolder> getCTPersistence() {
		return _dlFolderLocalService.getCTPersistence();
	}

	@Override
	public Class<DLFolder> getModelClass() {
		return _dlFolderLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFolder>, R, E> updateUnsafeFunction)
		throws E {

		return _dlFolderLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DLFolderLocalService getWrappedService() {
		return _dlFolderLocalService;
	}

	@Override
	public void setWrappedService(DLFolderLocalService dlFolderLocalService) {
		_dlFolderLocalService = dlFolderLocalService;
	}

	private DLFolderLocalService _dlFolderLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:502399817
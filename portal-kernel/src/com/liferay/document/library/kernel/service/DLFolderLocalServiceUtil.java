/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for DLFolder. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLFolderLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFolderLocalService
 * @generated
 */
public class DLFolderLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFolderLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addDLFileEntryTypeDLFolder(
		long fileEntryTypeId, DLFolder dlFolder) {

		return getService().addDLFileEntryTypeDLFolder(
			fileEntryTypeId, dlFolder);
	}

	public static boolean addDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		return getService().addDLFileEntryTypeDLFolder(
			fileEntryTypeId, folderId);
	}

	public static boolean addDLFileEntryTypeDLFolders(
		long fileEntryTypeId, List<DLFolder> dlFolders) {

		return getService().addDLFileEntryTypeDLFolders(
			fileEntryTypeId, dlFolders);
	}

	public static boolean addDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		return getService().addDLFileEntryTypeDLFolders(
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
	public static DLFolder addDLFolder(DLFolder dlFolder) {
		return getService().addDLFolder(dlFolder);
	}

	public static DLFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, boolean mountPoint, long parentFolderId,
			String name, String description, boolean hidden,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFolder(
			externalReferenceCode, userId, groupId, repositoryId, mountPoint,
			parentFolderId, name, description, hidden, serviceContext);
	}

	public static void clearDLFileEntryTypeDLFolders(long fileEntryTypeId) {
		getService().clearDLFileEntryTypeDLFolders(fileEntryTypeId);
	}

	/**
	 * Creates a new document library folder with the primary key. Does not add the document library folder to the database.
	 *
	 * @param folderId the primary key for the new document library folder
	 * @return the new document library folder
	 */
	public static DLFolder createDLFolder(long folderId) {
		return getService().createDLFolder(folderId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteAllByGroup(long groupId) throws PortalException {
		getService().deleteAllByGroup(groupId);
	}

	public static void deleteAllByRepository(long repositoryId)
		throws PortalException {

		getService().deleteAllByRepository(repositoryId);
	}

	public static void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId, DLFolder dlFolder) {

		getService().deleteDLFileEntryTypeDLFolder(fileEntryTypeId, dlFolder);
	}

	public static void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		getService().deleteDLFileEntryTypeDLFolder(fileEntryTypeId, folderId);
	}

	public static void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId, List<DLFolder> dlFolders) {

		getService().deleteDLFileEntryTypeDLFolders(fileEntryTypeId, dlFolders);
	}

	public static void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		getService().deleteDLFileEntryTypeDLFolders(fileEntryTypeId, folderIds);
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
	public static DLFolder deleteDLFolder(DLFolder dlFolder) {
		return getService().deleteDLFolder(dlFolder);
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
	public static DLFolder deleteDLFolder(long folderId)
		throws PortalException {

		return getService().deleteDLFolder(folderId);
	}

	public static DLFolder deleteFolder(DLFolder dlFolder)
		throws PortalException {

		return getService().deleteFolder(dlFolder);
	}

	public static DLFolder deleteFolder(
			DLFolder dlFolder, boolean includeTrashedEntries)
		throws PortalException {

		return getService().deleteFolder(dlFolder, includeTrashedEntries);
	}

	public static DLFolder deleteFolder(long folderId) throws PortalException {
		return getService().deleteFolder(folderId);
	}

	public static DLFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws PortalException {

		return getService().deleteFolder(folderId, includeTrashedEntries);
	}

	public static DLFolder deleteFolder(
			long userId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		return getService().deleteFolder(
			userId, folderId, includeTrashedEntries);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
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

	public static DLFolder fetchDLFolder(long folderId) {
		return getService().fetchDLFolder(folderId);
	}

	public static DLFolder fetchDLFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchDLFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library folder matching the UUID and group.
	 *
	 * @param uuid the document library folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	public static DLFolder fetchDLFolderByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDLFolderByUuidAndGroupId(uuid, groupId);
	}

	public static DLFolder fetchFolder(long folderId) {
		return getService().fetchFolder(folderId);
	}

	public static DLFolder fetchFolder(
		long groupId, long parentFolderId, String name) {

		return getService().fetchFolder(groupId, parentFolderId, name);
	}

	public static DLFolder fetchFolder(String uuid, long groupId) {
		return getService().fetchFolder(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<DLFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return getService().getCompanyFolders(companyId, start, end);
	}

	public static int getCompanyFoldersCount(long companyId) {
		return getService().getCompanyFoldersCount(companyId);
	}

	public static List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId) {

		return getService().getDLFileEntryTypeDLFolders(fileEntryTypeId);
	}

	public static List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId, int start, int end) {

		return getService().getDLFileEntryTypeDLFolders(
			fileEntryTypeId, start, end);
	}

	public static List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return getService().getDLFileEntryTypeDLFolders(
			fileEntryTypeId, start, end, orderByComparator);
	}

	public static int getDLFileEntryTypeDLFoldersCount(long fileEntryTypeId) {
		return getService().getDLFileEntryTypeDLFoldersCount(fileEntryTypeId);
	}

	/**
	 * Returns the fileEntryTypeIds of the document library file entry types associated with the document library folder.
	 *
	 * @param folderId the folderId of the document library folder
	 * @return long[] the fileEntryTypeIds of document library file entry types associated with the document library folder
	 */
	public static long[] getDLFileEntryTypePrimaryKeys(long folderId) {
		return getService().getDLFileEntryTypePrimaryKeys(folderId);
	}

	/**
	 * Returns the document library folder with the primary key.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder
	 * @throws PortalException if a document library folder with the primary key could not be found
	 */
	public static DLFolder getDLFolder(long folderId) throws PortalException {
		return getService().getDLFolder(folderId);
	}

	public static DLFolder getDLFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getDLFolderByExternalReferenceCode(
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
	public static DLFolder getDLFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDLFolderByUuidAndGroupId(uuid, groupId);
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
	public static List<DLFolder> getDLFolders(int start, int end) {
		return getService().getDLFolders(start, end);
	}

	/**
	 * Returns all the document library folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library folders
	 * @param companyId the primary key of the company
	 * @return the matching document library folders, or an empty list if no matches were found
	 */
	public static List<DLFolder> getDLFoldersByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDLFoldersByUuidAndCompanyId(uuid, companyId);
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
	public static List<DLFolder> getDLFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return getService().getDLFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library folders.
	 *
	 * @return the number of document library folders
	 */
	public static int getDLFoldersCount() {
		return getService().getDLFoldersCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<Object> getFileEntriesAndFileShortcuts(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getService().getFileEntriesAndFileShortcuts(
			groupId, folderId, queryDefinition);
	}

	public static int getFileEntriesAndFileShortcutsCount(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getService().getFileEntriesAndFileShortcutsCount(
			groupId, folderId, queryDefinition);
	}

	public static DLFolder getFolder(long folderId) throws PortalException {
		return getService().getFolder(folderId);
	}

	public static DLFolder getFolder(
			long groupId, long parentFolderId, String name)
		throws PortalException {

		return getService().getFolder(groupId, parentFolderId, name);
	}

	public static long getFolderId(long companyId, long folderId) {
		return getService().getFolderId(companyId, folderId);
	}

	public static List<DLFolder> getFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return getService().getFolders(groupId, mountPoint, treePath, hidden);
	}

	public static List<DLFolder> getFolders(long groupId, long parentFolderId) {
		return getService().getFolders(groupId, parentFolderId);
	}

	public static List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders) {

		return getService().getFolders(
			groupId, parentFolderId, includeMountfolders);
	}

	public static List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return getService().getFolders(
			groupId, parentFolderId, includeMountfolders, status, start, end,
			orderByComparator);
	}

	public static List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		return getService().getFolders(
			groupId, parentFolderId, includeMountfolders, start, end,
			orderByComparator);
	}

	public static List<DLFolder> getFolders(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return getService().getFolders(
			groupId, parentFolderId, start, end, orderByComparator);
	}

	public static List<DLFolder> getFolders(long classNameId, String treePath) {
		return getService().getFolders(classNameId, treePath);
	}

	public static List<Object> getFoldersAndFileEntriesAndFileShortcuts(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getService().getFoldersAndFileEntriesAndFileShortcuts(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getService().getFoldersAndFileEntriesAndFileShortcutsCount(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static int getFoldersCount(long groupId, long parentFolderId) {
		return getService().getFoldersCount(groupId, parentFolderId);
	}

	public static int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders) {

		return getService().getFoldersCount(
			groupId, parentFolderId, includeMountfolders);
	}

	public static int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status) {

		return getService().getFoldersCount(
			groupId, parentFolderId, includeMountfolders, status);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFoldersCount(long, long, boolean, int)}
	 */
	@Deprecated
	public static int getFoldersCount(
		long groupId, long parentFolderId, int status,
		boolean includeMountfolders) {

		return getService().getFoldersCount(
			groupId, parentFolderId, status, includeMountfolders);
	}

	public static long getFolderSize(
		long companyId, long groupId, String treePath) {

		return getService().getFolderSize(companyId, groupId, treePath);
	}

	public static List<Long> getGroupFolderIds(
		long groupId, long parentFolderId) {

		return getService().getGroupFolderIds(groupId, parentFolderId);
	}

	public static void getGroupSubfolderIds(
		List<Long> folderIds, long groupId, long folderId) {

		getService().getGroupSubfolderIds(folderIds, groupId, folderId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static DLFolder getMountFolder(long repositoryId)
		throws PortalException {

		return getService().getMountFolder(repositoryId);
	}

	public static List<DLFolder> getMountFolders(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return getService().getMountFolders(
			groupId, parentFolderId, start, end, orderByComparator);
	}

	public static int getMountFoldersCount(long groupId, long parentFolderId) {
		return getService().getMountFoldersCount(groupId, parentFolderId);
	}

	public static List<DLFolder> getNoAssetFolders() {
		return getService().getNoAssetFolders();
	}

	public static List<DLFolder> getNotInTrashFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return getService().getNotInTrashFolders(
			groupId, mountPoint, treePath, hidden);
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

	public static List<Long> getRepositoryFolderIds(
		long repositoryId, long parentFolderId) {

		return getService().getRepositoryFolderIds(
			repositoryId, parentFolderId);
	}

	public static List<DLFolder> getRepositoryFolders(
		long repositoryId, int start, int end) {

		return getService().getRepositoryFolders(repositoryId, start, end);
	}

	public static int getRepositoryFoldersCount(long repositoryId) {
		return getService().getRepositoryFoldersCount(repositoryId);
	}

	public static void getRepositorySubfolderIds(
		List<Long> folderIds, long repositoryId, long folderId) {

		getService().getRepositorySubfolderIds(
			folderIds, repositoryId, folderId);
	}

	public static String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name,
		int count) {

		return getService().getUniqueFolderName(
			uuid, groupId, parentFolderId, name, count);
	}

	public static boolean hasDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId) {

		return getService().hasDLFileEntryTypeDLFolder(
			fileEntryTypeId, folderId);
	}

	public static boolean hasDLFileEntryTypeDLFolders(long fileEntryTypeId) {
		return getService().hasDLFileEntryTypeDLFolders(fileEntryTypeId);
	}

	public static boolean hasFolderLock(long userId, long folderId) {
		return getService().hasFolderLock(userId, folderId);
	}

	public static boolean hasInheritableLock(long folderId) {
		return getService().hasInheritableLock(folderId);
	}

	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			long userId, long folderId)
		throws PortalException {

		return getService().lockFolder(userId, folderId);
	}

	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			long userId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException {

		return getService().lockFolder(
			userId, folderId, owner, inheritable, expirationTime);
	}

	public static DLFolder moveFolder(
			long userId, long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFolder(
			userId, folderId, parentFolderId, serviceContext);
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

	public static void setDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds) {

		getService().setDLFileEntryTypeDLFolders(fileEntryTypeId, folderIds);
	}

	public static void unlockFolder(
			long groupId, long parentFolderId, String name, String lockUuid)
		throws PortalException {

		getService().unlockFolder(groupId, parentFolderId, name, lockUuid);
	}

	public static void unlockFolder(long folderId, String lockUuid)
		throws PortalException {

		getService().unlockFolder(folderId, lockUuid);
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
	public static DLFolder updateDLFolder(DLFolder dlFolder) {
		return getService().updateDLFolder(dlFolder);
	}

	public static DLFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFolder(
			folderId, parentFolderId, name, description, defaultFileEntryTypeId,
			fileEntryTypeIds, restrictionType, serviceContext);
	}

	public static DLFolder updateFolder(
			long folderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFolder(
			folderId, name, description, defaultFileEntryTypeId,
			fileEntryTypeIds, restrictionType, serviceContext);
	}

	public static DLFolder updateFolderAndFileEntryTypes(
			long userId, long folderId, long parentFolderId, String name,
			String description, long defaultFileEntryTypeId,
			List<Long> fileEntryTypeIds, int restrictionType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFolderAndFileEntryTypes(
			userId, folderId, parentFolderId, name, description,
			defaultFileEntryTypeId, fileEntryTypeIds, restrictionType,
			serviceContext);
	}

	public static void updateLastPostDate(
			long folderId, java.util.Date lastPostDate)
		throws PortalException {

		getService().updateLastPostDate(folderId, lastPostDate);
	}

	public static DLFolder updateStatus(
			long userId, long folderId, int status,
			Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, folderId, status, workflowContext, serviceContext);
	}

	public static boolean verifyInheritableLock(long folderId, String lockUuid)
		throws PortalException {

		return getService().verifyInheritableLock(folderId, lockUuid);
	}

	public static DLFolderLocalService getService() {
		return _service;
	}

	public static void setService(DLFolderLocalService service) {
		_service = service;
	}

	private static volatile DLFolderLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1240963105
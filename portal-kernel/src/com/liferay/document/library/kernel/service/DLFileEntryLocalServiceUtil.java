/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for DLFileEntry. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLFileEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryLocalService
 * @generated
 */
public class DLFileEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFileEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the document library file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntry the document library file entry
	 * @return the document library file entry that was added
	 */
	public static DLFileEntry addDLFileEntry(DLFileEntry dlFileEntry) {
		return getService().addDLFileEntry(dlFileEntry);
	}

	public static DLFileEntry addFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, long fileEntryTypeId,
			Map<String, com.liferay.dynamic.data.mapping.kernel.DDMFormValues>
				ddmFormValuesMap,
			java.io.File file, InputStream inputStream, long size,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntry(
			externalReferenceCode, userId, groupId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			fileEntryTypeId, ddmFormValuesMap, file, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);
	}

	public static com.liferay.document.library.kernel.model.DLFileVersion
			cancelCheckOut(long userId, long fileEntryId)
		throws PortalException {

		return getService().cancelCheckOut(userId, fileEntryId);
	}

	public static void checkFileEntries(long companyId, long checkInterval)
		throws PortalException {

		getService().checkFileEntries(companyId, checkInterval);
	}

	public static void checkInFileEntry(
			long userId, long fileEntryId,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease
				dlVersionNumberIncrease,
			String changeLog,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().checkInFileEntry(
			userId, fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);
	}

	public static void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().checkInFileEntry(
			userId, fileEntryId, lockUuid, serviceContext);
	}

	public static DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().checkOutFileEntry(
			userId, fileEntryId, fileEntryTypeId, serviceContext);
	}

	public static DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId, String owner,
			long expirationTime,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().checkOutFileEntry(
			userId, fileEntryId, fileEntryTypeId, owner, expirationTime,
			serviceContext);
	}

	public static DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().checkOutFileEntry(
			userId, fileEntryId, serviceContext);
	}

	public static DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, String owner, long expirationTime,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().checkOutFileEntry(
			userId, fileEntryId, owner, expirationTime, serviceContext);
	}

	public static void convertExtraSettings(String[] keys)
		throws PortalException {

		getService().convertExtraSettings(keys);
	}

	public static DLFileEntry copyFileEntry(
			long userId, long groupId, long repositoryId,
			long sourceFileEntryId, long targetFolderId, String fileName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyFileEntry(
			userId, groupId, repositoryId, sourceFileEntryId, targetFolderId,
			fileName, serviceContext);
	}

	public static void copyFileEntryMetadata(
			long companyId, long fileEntryTypeId, long fileEntryId,
			long sourceFileVersionId, long targetFileVersionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().copyFileEntryMetadata(
			companyId, fileEntryTypeId, fileEntryId, sourceFileVersionId,
			targetFileVersionId, serviceContext);
	}

	/**
	 * Creates a new document library file entry with the primary key. Does not add the document library file entry to the database.
	 *
	 * @param fileEntryId the primary key for the new document library file entry
	 * @return the new document library file entry
	 */
	public static DLFileEntry createDLFileEntry(long fileEntryId) {
		return getService().createDLFileEntry(fileEntryId);
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
	 * Deletes the document library file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntry the document library file entry
	 * @return the document library file entry that was removed
	 */
	public static DLFileEntry deleteDLFileEntry(DLFileEntry dlFileEntry) {
		return getService().deleteDLFileEntry(dlFileEntry);
	}

	/**
	 * Deletes the document library file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry that was removed
	 * @throws PortalException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry deleteDLFileEntry(long fileEntryId)
		throws PortalException {

		return getService().deleteDLFileEntry(fileEntryId);
	}

	public static void deleteFileEntries(long groupId, long folderId)
		throws PortalException {

		getService().deleteFileEntries(groupId, folderId);
	}

	public static void deleteFileEntries(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		getService().deleteFileEntries(
			groupId, folderId, includeTrashedEntries);
	}

	public static void deleteFileEntries(
			long companyId, long classNameId, long classPK)
		throws PortalException {

		getService().deleteFileEntries(companyId, classNameId, classPK);
	}

	public static DLFileEntry deleteFileEntry(DLFileEntry dlFileEntry)
		throws PortalException {

		return getService().deleteFileEntry(dlFileEntry);
	}

	public static DLFileEntry deleteFileEntry(long fileEntryId)
		throws PortalException {

		return getService().deleteFileEntry(fileEntryId);
	}

	public static DLFileEntry deleteFileEntry(long userId, long fileEntryId)
		throws PortalException {

		return getService().deleteFileEntry(userId, fileEntryId);
	}

	public static DLFileEntry deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static DLFileEntry deleteFileVersion(
			long userId, long fileEntryId, String version)
		throws PortalException {

		return getService().deleteFileVersion(userId, fileEntryId, version);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRepositoryFileEntries(long repositoryId)
		throws PortalException {

		getService().deleteRepositoryFileEntries(repositoryId);
	}

	public static void deleteRepositoryFileEntries(
			long repositoryId, long folderId)
		throws PortalException {

		getService().deleteRepositoryFileEntries(repositoryId, folderId);
	}

	public static void deleteRepositoryFileEntries(
			long repositoryId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		getService().deleteRepositoryFileEntries(
			repositoryId, folderId, includeTrashedEntries);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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

	public static DLFileEntry fetchDLFileEntry(long fileEntryId) {
		return getService().fetchDLFileEntry(fileEntryId);
	}

	public static DLFileEntry fetchDLFileEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchDLFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry matching the UUID and group.
	 *
	 * @param uuid the document library file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchDLFileEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDLFileEntryByUuidAndGroupId(uuid, groupId);
	}

	public static DLFileEntry fetchFileEntry(
		long groupId, long folderId, String title) {

		return getService().fetchFileEntry(groupId, folderId, title);
	}

	public static DLFileEntry fetchFileEntry(String uuid, long groupId) {
		return getService().fetchFileEntry(uuid, groupId);
	}

	public static DLFileEntry fetchFileEntryByAnyImageId(long imageId) {
		return getService().fetchFileEntryByAnyImageId(imageId);
	}

	public static DLFileEntry fetchFileEntryByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return getService().fetchFileEntryByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static DLFileEntry fetchFileEntryByFileName(
		long groupId, long folderId, String fileName) {

		return getService().fetchFileEntryByFileName(
			groupId, folderId, fileName);
	}

	public static DLFileEntry fetchFileEntryByName(
		long groupId, long folderId, String name) {

		return getService().fetchFileEntryByName(groupId, folderId, name);
	}

	public static void forEachFileEntry(
			long companyId, java.util.function.Consumer<DLFileEntry> consumer,
			long maximumSize, String[] mimeTypes)
		throws PortalException {

		getService().forEachFileEntry(
			companyId, consumer, maximumSize, mimeTypes);
	}

	public static void forEachFileEntry(
			long companyId, long classNameId,
			java.util.function.Consumer<DLFileEntry> consumer, long maximumSize,
			String[] mimeTypes)
		throws PortalException {

		getService().forEachFileEntry(
			companyId, classNameId, consumer, maximumSize, mimeTypes);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<DLFileEntry> getDDMStructureFileEntries(
		long groupId, long[] ddmStructureIds) {

		return getService().getDDMStructureFileEntries(
			groupId, ddmStructureIds);
	}

	public static List<DLFileEntry> getDDMStructureFileEntries(
		long[] ddmStructureIds) {

		return getService().getDDMStructureFileEntries(ddmStructureIds);
	}

	/**
	 * Returns a range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of document library file entries
	 */
	public static List<DLFileEntry> getDLFileEntries(int start, int end) {
		return getService().getDLFileEntries(start, end);
	}

	/**
	 * Returns all the document library file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entries
	 * @param companyId the primary key of the company
	 * @return the matching document library file entries, or an empty list if no matches were found
	 */
	public static List<DLFileEntry> getDLFileEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDLFileEntriesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of document library file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library file entries, or an empty list if no matches were found
	 */
	public static List<DLFileEntry> getDLFileEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getService().getDLFileEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file entries.
	 *
	 * @return the number of document library file entries
	 */
	public static int getDLFileEntriesCount() {
		return getService().getDLFileEntriesCount();
	}

	/**
	 * Returns the document library file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws PortalException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry getDLFileEntry(long fileEntryId)
		throws PortalException {

		return getService().getDLFileEntry(fileEntryId);
	}

	public static DLFileEntry getDLFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getDLFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry matching the UUID and group.
	 *
	 * @param uuid the document library file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry
	 * @throws PortalException if a matching document library file entry could not be found
	 */
	public static DLFileEntry getDLFileEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDLFileEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<DLFileEntry> getExtraSettingsFileEntries(
		int start, int end) {

		return getService().getExtraSettingsFileEntries(start, end);
	}

	public static int getExtraSettingsFileEntriesCount() {
		return getService().getExtraSettingsFileEntriesCount();
	}

	public static InputStream getFileAsStream(long fileEntryId, String version)
		throws PortalException {

		return getService().getFileAsStream(fileEntryId, version);
	}

	public static InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter)
		throws PortalException {

		return getService().getFileAsStream(
			fileEntryId, version, incrementCounter);
	}

	public static InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter,
			int increment)
		throws PortalException {

		return getService().getFileAsStream(
			fileEntryId, version, incrementCounter, increment);
	}

	public static List<DLFileEntry> getFileEntries(int start, int end) {
		return getService().getFileEntries(start, end);
	}

	public static List<DLFileEntry> getFileEntries(
		long groupId, long folderId) {

		return getService().getFileEntries(groupId, folderId);
	}

	public static List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getService().getFileEntries(
			groupId, folderId, status, start, end, orderByComparator);
	}

	public static List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getService().getFileEntries(
			groupId, folderId, start, end, orderByComparator);
	}

	public static List<DLFileEntry> getFileEntries(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return getService().getFileEntries(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	public static List<DLFileEntry> getFileEntries(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return getService().getFileEntries(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	public static List<DLFileEntry> getFileEntries(long folderId, String name) {
		return getService().getFileEntries(folderId, name);
	}

	public static List<DLFileEntry> getFileEntriesByClassNameIdAndTreePath(
		long classNameId, String treePath) {

		return getService().getFileEntriesByClassNameIdAndTreePath(
			classNameId, treePath);
	}

	public static int getFileEntriesCount() {
		return getService().getFileEntriesCount();
	}

	public static int getFileEntriesCount(long groupId, long folderId) {
		return getService().getFileEntriesCount(groupId, folderId);
	}

	public static int getFileEntriesCount(
		long groupId, long folderId, int status) {

		return getService().getFileEntriesCount(groupId, folderId, status);
	}

	public static int getFileEntriesCount(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return getService().getFileEntriesCount(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	public static int getFileEntriesCount(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return getService().getFileEntriesCount(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	public static DLFileEntry getFileEntry(long fileEntryId)
		throws PortalException {

		return getService().getFileEntry(fileEntryId);
	}

	public static DLFileEntry getFileEntry(
			long groupId, long folderId, String title)
		throws PortalException {

		return getService().getFileEntry(groupId, folderId, title);
	}

	public static DLFileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static DLFileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws PortalException {

		return getService().getFileEntryByFileName(groupId, folderId, fileName);
	}

	public static DLFileEntry getFileEntryByName(
			long groupId, long folderId, String name)
		throws PortalException {

		return getService().getFileEntryByName(groupId, folderId, name);
	}

	public static DLFileEntry getFileEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getFileEntryByUuidAndGroupId(uuid, groupId);
	}

	public static Map<Long, Long> getFileEntryTypeIds(
		long companyId, long[] groupIds, String treePath) {

		return getService().getFileEntryTypeIds(companyId, groupIds, treePath);
	}

	public static List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end) {

		return getService().getGroupFileEntries(groupId, start, end);
	}

	public static List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getService().getGroupFileEntries(
			groupId, start, end, orderByComparator);
	}

	public static List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end) {

		return getService().getGroupFileEntries(groupId, userId, start, end);
	}

	public static List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getService().getGroupFileEntries(
			groupId, userId, start, end, orderByComparator);
	}

	public static List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long rootFolderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getService().getGroupFileEntries(
			groupId, userId, rootFolderId, start, end, orderByComparator);
	}

	public static List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long repositoryId, long rootFolderId,
		int start, int end, OrderByComparator<DLFileEntry> orderByComparator) {

		return getService().getGroupFileEntries(
			groupId, userId, repositoryId, rootFolderId, start, end,
			orderByComparator);
	}

	public static int getGroupFileEntriesCount(long groupId) {
		return getService().getGroupFileEntriesCount(groupId);
	}

	public static int getGroupFileEntriesCount(long groupId, long userId) {
		return getService().getGroupFileEntriesCount(groupId, userId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<DLFileEntry> getNoAssetFileEntries() {
		return getService().getNoAssetFileEntries();
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

	public static List<DLFileEntry> getRepositoryFileEntries(
		long repositoryId, int start, int end) {

		return getService().getRepositoryFileEntries(repositoryId, start, end);
	}

	public static int getRepositoryFileEntriesCount(long repositoryId) {
		return getService().getRepositoryFileEntriesCount(repositoryId);
	}

	public static String getUniqueTitle(
			long groupId, long folderId, long fileEntryId, String title,
			String extension)
		throws PortalException {

		return getService().getUniqueTitle(
			groupId, folderId, fileEntryId, title, extension);
	}

	public static boolean hasExtraSettings() {
		return getService().hasExtraSettings();
	}

	public static boolean hasFileEntryLock(long userId, long fileEntryId)
		throws PortalException {

		return getService().hasFileEntryLock(userId, fileEntryId);
	}

	public static boolean hasFileEntryLock(
		long userId, long fileEntryId, long folderId) {

		return getService().hasFileEntryLock(userId, fileEntryId, folderId);
	}

	public static void incrementViewCounter(
		DLFileEntry dlFileEntry, int increment) {

		getService().incrementViewCounter(dlFileEntry, increment);
	}

	public static boolean isFileEntryCheckedOut(long fileEntryId) {
		return getService().isFileEntryCheckedOut(fileEntryId);
	}

	public static com.liferay.portal.kernel.lock.Lock lockFileEntry(
			long userId, long fileEntryId)
		throws PortalException {

		return getService().lockFileEntry(userId, fileEntryId);
	}

	public static DLFileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFileEntry(
			userId, fileEntryId, newFolderId, serviceContext);
	}

	public static void rebuildTree(long companyId) throws PortalException {
		getService().rebuildTree(companyId);
	}

	public static void revertFileEntry(
			long userId, long fileEntryId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().revertFileEntry(
			userId, fileEntryId, version, serviceContext);
	}

	public static com.liferay.portal.kernel.search.Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws PortalException {

		return getService().search(
			groupId, userId, creatorUserId, status, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
			long groupId, long userId, long creatorUserId, long folderId,
			String[] mimeTypes, int status, int start, int end)
		throws PortalException {

		return getService().search(
			groupId, userId, creatorUserId, folderId, mimeTypes, status, start,
			end);
	}

	public static void setTreePaths(
			long folderId, String treePath, boolean reindex)
		throws PortalException {

		getService().setTreePaths(folderId, treePath, reindex);
	}

	public static void unlockFileEntry(long fileEntryId) {
		getService().unlockFileEntry(fileEntryId);
	}

	/**
	 * Updates the document library file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntry the document library file entry
	 * @return the document library file entry that was updated
	 */
	public static DLFileEntry updateDLFileEntry(DLFileEntry dlFileEntry) {
		return getService().updateDLFileEntry(dlFileEntry);
	}

	public static DLFileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease
				dlVersionNumberIncrease,
			long fileEntryTypeId,
			Map<String, com.liferay.dynamic.data.mapping.kernel.DDMFormValues>
				ddmFormValuesMap,
			java.io.File file, InputStream inputStream, long size,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, fileEntryTypeId,
			ddmFormValuesMap, file, inputStream, size, displayDate,
			expirationDate, reviewDate, serviceContext);
	}

	public static DLFileEntry updateFileEntryType(
			long userId, long fileEntryId, long fileEntryTypeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileEntryType(
			userId, fileEntryId, fileEntryTypeId, serviceContext);
	}

	public static DLFileEntry updateStatus(
			long userId, DLFileEntry dlFileEntry,
			com.liferay.document.library.kernel.model.DLFileVersion
				dlFileVersion,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, dlFileEntry, dlFileVersion, status, serviceContext,
			workflowContext);
	}

	public static DLFileEntry updateStatus(
			long userId, long fileVersionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, fileVersionId, status, serviceContext, workflowContext);
	}

	public static void validateFile(
			long groupId, long folderId, long fileEntryId, String fileName,
			String title)
		throws PortalException {

		getService().validateFile(
			groupId, folderId, fileEntryId, fileName, title);
	}

	public static boolean verifyFileEntryCheckOut(
			long fileEntryId, String lockUuid)
		throws PortalException {

		return getService().verifyFileEntryCheckOut(fileEntryId, lockUuid);
	}

	public static boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws PortalException {

		return getService().verifyFileEntryLock(fileEntryId, lockUuid);
	}

	public static DLFileEntryLocalService getService() {
		return _service;
	}

	public static void setService(DLFileEntryLocalService service) {
		_service = service;
	}

	private static volatile DLFileEntryLocalService _service;

}
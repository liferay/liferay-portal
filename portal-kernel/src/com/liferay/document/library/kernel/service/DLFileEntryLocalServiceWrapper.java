/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DLFileEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryLocalService
 * @generated
 */
public class DLFileEntryLocalServiceWrapper
	implements DLFileEntryLocalService,
			   ServiceWrapper<DLFileEntryLocalService> {

	public DLFileEntryLocalServiceWrapper() {
		this(null);
	}

	public DLFileEntryLocalServiceWrapper(
		DLFileEntryLocalService dlFileEntryLocalService) {

		_dlFileEntryLocalService = dlFileEntryLocalService;
	}

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
	@Override
	public DLFileEntry addDLFileEntry(DLFileEntry dlFileEntry) {
		return _dlFileEntryLocalService.addDLFileEntry(dlFileEntry);
	}

	@Override
	public DLFileEntry addFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, long fileEntryTypeId,
			java.util.Map
				<String, com.liferay.dynamic.data.mapping.kernel.DDMFormValues>
					ddmFormValuesMap,
			java.io.File file, java.io.InputStream inputStream, long size,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.addFileEntry(
			externalReferenceCode, userId, groupId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			fileEntryTypeId, ddmFormValuesMap, file, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);
	}

	@Override
	public com.liferay.document.library.kernel.model.DLFileVersion
			cancelCheckOut(long userId, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.cancelCheckOut(userId, fileEntryId);
	}

	@Override
	public void checkFileEntries(long companyId, long checkInterval)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.checkFileEntries(companyId, checkInterval);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease
				dlVersionNumberIncrease,
			String changeLog,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.checkInFileEntry(
			userId, fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.checkInFileEntry(
			userId, fileEntryId, lockUuid, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.checkOutFileEntry(
			userId, fileEntryId, fileEntryTypeId, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId, String owner,
			long expirationTime,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.checkOutFileEntry(
			userId, fileEntryId, fileEntryTypeId, owner, expirationTime,
			serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.checkOutFileEntry(
			userId, fileEntryId, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, String owner, long expirationTime,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.checkOutFileEntry(
			userId, fileEntryId, owner, expirationTime, serviceContext);
	}

	@Override
	public void convertExtraSettings(String[] keys)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.convertExtraSettings(keys);
	}

	@Override
	public DLFileEntry copyFileEntry(
			long userId, long groupId, long repositoryId,
			long sourceFileEntryId, long targetFolderId, String fileName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.copyFileEntry(
			userId, groupId, repositoryId, sourceFileEntryId, targetFolderId,
			fileName, serviceContext);
	}

	@Override
	public void copyFileEntryMetadata(
			long companyId, long fileEntryTypeId, long fileEntryId,
			long sourceFileVersionId, long targetFileVersionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.copyFileEntryMetadata(
			companyId, fileEntryTypeId, fileEntryId, sourceFileVersionId,
			targetFileVersionId, serviceContext);
	}

	/**
	 * Creates a new document library file entry with the primary key. Does not add the document library file entry to the database.
	 *
	 * @param fileEntryId the primary key for the new document library file entry
	 * @return the new document library file entry
	 */
	@Override
	public DLFileEntry createDLFileEntry(long fileEntryId) {
		return _dlFileEntryLocalService.createDLFileEntry(fileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public DLFileEntry deleteDLFileEntry(DLFileEntry dlFileEntry) {
		return _dlFileEntryLocalService.deleteDLFileEntry(dlFileEntry);
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
	@Override
	public DLFileEntry deleteDLFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.deleteDLFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileEntries(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.deleteFileEntries(groupId, folderId);
	}

	@Override
	public void deleteFileEntries(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.deleteFileEntries(
			groupId, folderId, includeTrashedEntries);
	}

	@Override
	public void deleteFileEntries(
			long companyId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.deleteFileEntries(
			companyId, classNameId, classPK);
	}

	@Override
	public DLFileEntry deleteFileEntry(DLFileEntry dlFileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
	}

	@Override
	public DLFileEntry deleteFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.deleteFileEntry(fileEntryId);
	}

	@Override
	public DLFileEntry deleteFileEntry(long userId, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.deleteFileEntry(userId, fileEntryId);
	}

	@Override
	public DLFileEntry deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.deleteFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public DLFileEntry deleteFileVersion(
			long userId, long fileEntryId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.deleteFileVersion(
			userId, fileEntryId, version);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRepositoryFileEntries(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.deleteRepositoryFileEntries(repositoryId);
	}

	@Override
	public void deleteRepositoryFileEntries(long repositoryId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.deleteRepositoryFileEntries(
			repositoryId, folderId);
	}

	@Override
	public void deleteRepositoryFileEntries(
			long repositoryId, long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.deleteRepositoryFileEntries(
			repositoryId, folderId, includeTrashedEntries);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlFileEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlFileEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlFileEntryLocalService.dynamicQuery();
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

		return _dlFileEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _dlFileEntryLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _dlFileEntryLocalService.dynamicQuery(
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

		return _dlFileEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dlFileEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DLFileEntry fetchDLFileEntry(long fileEntryId) {
		return _dlFileEntryLocalService.fetchDLFileEntry(fileEntryId);
	}

	@Override
	public DLFileEntry fetchDLFileEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _dlFileEntryLocalService.fetchDLFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry matching the UUID and group.
	 *
	 * @param uuid the document library file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Override
	public DLFileEntry fetchDLFileEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _dlFileEntryLocalService.fetchDLFileEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public DLFileEntry fetchFileEntry(
		long groupId, long folderId, String title) {

		return _dlFileEntryLocalService.fetchFileEntry(
			groupId, folderId, title);
	}

	@Override
	public DLFileEntry fetchFileEntry(String uuid, long groupId) {
		return _dlFileEntryLocalService.fetchFileEntry(uuid, groupId);
	}

	@Override
	public DLFileEntry fetchFileEntryByAnyImageId(long imageId) {
		return _dlFileEntryLocalService.fetchFileEntryByAnyImageId(imageId);
	}

	@Override
	public DLFileEntry fetchFileEntryByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return _dlFileEntryLocalService.fetchFileEntryByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	@Override
	public DLFileEntry fetchFileEntryByFileName(
		long groupId, long folderId, String fileName) {

		return _dlFileEntryLocalService.fetchFileEntryByFileName(
			groupId, folderId, fileName);
	}

	@Override
	public DLFileEntry fetchFileEntryByName(
		long groupId, long folderId, String name) {

		return _dlFileEntryLocalService.fetchFileEntryByName(
			groupId, folderId, name);
	}

	@Override
	public void forEachFileEntry(
			long companyId, java.util.function.Consumer<DLFileEntry> consumer,
			long maximumSize, String[] mimeTypes)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.forEachFileEntry(
			companyId, consumer, maximumSize, mimeTypes);
	}

	@Override
	public void forEachFileEntry(
			long companyId, long classNameId,
			java.util.function.Consumer<DLFileEntry> consumer, long maximumSize,
			String[] mimeTypes)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.forEachFileEntry(
			companyId, classNameId, consumer, maximumSize, mimeTypes);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlFileEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<DLFileEntry> getDDMStructureFileEntries(
		long groupId, long[] ddmStructureIds) {

		return _dlFileEntryLocalService.getDDMStructureFileEntries(
			groupId, ddmStructureIds);
	}

	@Override
	public java.util.List<DLFileEntry> getDDMStructureFileEntries(
		long[] ddmStructureIds) {

		return _dlFileEntryLocalService.getDDMStructureFileEntries(
			ddmStructureIds);
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
	@Override
	public java.util.List<DLFileEntry> getDLFileEntries(int start, int end) {
		return _dlFileEntryLocalService.getDLFileEntries(start, end);
	}

	/**
	 * Returns all the document library file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entries
	 * @param companyId the primary key of the company
	 * @return the matching document library file entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DLFileEntry> getDLFileEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _dlFileEntryLocalService.getDLFileEntriesByUuidAndCompanyId(
			uuid, companyId);
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
	@Override
	public java.util.List<DLFileEntry> getDLFileEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return _dlFileEntryLocalService.getDLFileEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file entries.
	 *
	 * @return the number of document library file entries
	 */
	@Override
	public int getDLFileEntriesCount() {
		return _dlFileEntryLocalService.getDLFileEntriesCount();
	}

	/**
	 * Returns the document library file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws PortalException if a document library file entry with the primary key could not be found
	 */
	@Override
	public DLFileEntry getDLFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getDLFileEntry(fileEntryId);
	}

	@Override
	public DLFileEntry getDLFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getDLFileEntryByExternalReferenceCode(
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
	@Override
	public DLFileEntry getDLFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getDLFileEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _dlFileEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<DLFileEntry> getExtraSettingsFileEntries(
		int start, int end) {

		return _dlFileEntryLocalService.getExtraSettingsFileEntries(start, end);
	}

	@Override
	public int getExtraSettingsFileEntriesCount() {
		return _dlFileEntryLocalService.getExtraSettingsFileEntriesCount();
	}

	@Override
	public java.io.InputStream getFileAsStream(long fileEntryId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileAsStream(fileEntryId, version);
	}

	@Override
	public java.io.InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileAsStream(
			fileEntryId, version, incrementCounter);
	}

	@Override
	public java.io.InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter,
			int increment)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileAsStream(
			fileEntryId, version, incrementCounter, increment);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(int start, int end) {
		return _dlFileEntryLocalService.getFileEntries(start, end);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
		long groupId, long folderId) {

		return _dlFileEntryLocalService.getFileEntries(groupId, folderId);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return _dlFileEntryLocalService.getFileEntries(
			groupId, folderId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return _dlFileEntryLocalService.getFileEntries(
			groupId, folderId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
		long groupId, long userId, java.util.List<Long> repositoryIds,
		java.util.List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return _dlFileEntryLocalService.getFileEntries(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
		long groupId, long userId, java.util.List<Long> folderIds,
		String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return _dlFileEntryLocalService.getFileEntries(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
		long folderId, String name) {

		return _dlFileEntryLocalService.getFileEntries(folderId, name);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntriesByClassNameIdAndTreePath(
		long classNameId, String treePath) {

		return _dlFileEntryLocalService.getFileEntriesByClassNameIdAndTreePath(
			classNameId, treePath);
	}

	@Override
	public int getFileEntriesCount() {
		return _dlFileEntryLocalService.getFileEntriesCount();
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId) {
		return _dlFileEntryLocalService.getFileEntriesCount(groupId, folderId);
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId, int status) {
		return _dlFileEntryLocalService.getFileEntriesCount(
			groupId, folderId, status);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long userId, java.util.List<Long> repositoryIds,
		java.util.List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return _dlFileEntryLocalService.getFileEntriesCount(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long userId, java.util.List<Long> folderIds,
		String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<DLFileEntry>
			queryDefinition) {

		return _dlFileEntryLocalService.getFileEntriesCount(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	@Override
	public DLFileEntry getFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileEntry(fileEntryId);
	}

	@Override
	public DLFileEntry getFileEntry(long groupId, long folderId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileEntry(groupId, folderId, title);
	}

	@Override
	public DLFileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public DLFileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileEntryByFileName(
			groupId, folderId, fileName);
	}

	@Override
	public DLFileEntry getFileEntryByName(
			long groupId, long folderId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileEntryByName(
			groupId, folderId, name);
	}

	@Override
	public DLFileEntry getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getFileEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.Map<Long, Long> getFileEntryTypeIds(
		long companyId, long[] groupIds, String treePath) {

		return _dlFileEntryLocalService.getFileEntryTypeIds(
			companyId, groupIds, treePath);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end) {

		return _dlFileEntryLocalService.getGroupFileEntries(
			groupId, start, end);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return _dlFileEntryLocalService.getGroupFileEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end) {

		return _dlFileEntryLocalService.getGroupFileEntries(
			groupId, userId, start, end);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return _dlFileEntryLocalService.getGroupFileEntries(
			groupId, userId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long rootFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return _dlFileEntryLocalService.getGroupFileEntries(
			groupId, userId, rootFolderId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long repositoryId, long rootFolderId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
			orderByComparator) {

		return _dlFileEntryLocalService.getGroupFileEntries(
			groupId, userId, repositoryId, rootFolderId, start, end,
			orderByComparator);
	}

	@Override
	public int getGroupFileEntriesCount(long groupId) {
		return _dlFileEntryLocalService.getGroupFileEntriesCount(groupId);
	}

	@Override
	public int getGroupFileEntriesCount(long groupId, long userId) {
		return _dlFileEntryLocalService.getGroupFileEntriesCount(
			groupId, userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlFileEntryLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<DLFileEntry> getNoAssetFileEntries() {
		return _dlFileEntryLocalService.getNoAssetFileEntries();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlFileEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<DLFileEntry> getRepositoryFileEntries(
		long repositoryId, int start, int end) {

		return _dlFileEntryLocalService.getRepositoryFileEntries(
			repositoryId, start, end);
	}

	@Override
	public int getRepositoryFileEntriesCount(long repositoryId) {
		return _dlFileEntryLocalService.getRepositoryFileEntriesCount(
			repositoryId);
	}

	@Override
	public String getUniqueTitle(
			long groupId, long folderId, long fileEntryId, String title,
			String extension)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.getUniqueTitle(
			groupId, folderId, fileEntryId, title, extension);
	}

	@Override
	public boolean hasExtraSettings() {
		return _dlFileEntryLocalService.hasExtraSettings();
	}

	@Override
	public boolean hasFileEntryLock(long userId, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.hasFileEntryLock(userId, fileEntryId);
	}

	@Override
	public boolean hasFileEntryLock(
		long userId, long fileEntryId, long folderId) {

		return _dlFileEntryLocalService.hasFileEntryLock(
			userId, fileEntryId, folderId);
	}

	@Override
	public void incrementViewCounter(DLFileEntry dlFileEntry, int increment) {
		_dlFileEntryLocalService.incrementViewCounter(dlFileEntry, increment);
	}

	@Override
	public boolean isFileEntryCheckedOut(long fileEntryId) {
		return _dlFileEntryLocalService.isFileEntryCheckedOut(fileEntryId);
	}

	@Override
	public com.liferay.portal.kernel.lock.Lock lockFileEntry(
			long userId, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.lockFileEntry(userId, fileEntryId);
	}

	@Override
	public DLFileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.moveFileEntry(
			userId, fileEntryId, newFolderId, serviceContext);
	}

	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.rebuildTree(companyId);
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.revertFileEntry(
			userId, fileEntryId, version, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.search(
			groupId, userId, creatorUserId, status, start, end);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
			long groupId, long userId, long creatorUserId, long folderId,
			String[] mimeTypes, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.search(
			groupId, userId, creatorUserId, folderId, mimeTypes, status, start,
			end);
	}

	@Override
	public void setTreePaths(long folderId, String treePath, boolean reindex)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.setTreePaths(folderId, treePath, reindex);
	}

	@Override
	public void unlockFileEntry(long fileEntryId) {
		_dlFileEntryLocalService.unlockFileEntry(fileEntryId);
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
	@Override
	public DLFileEntry updateDLFileEntry(DLFileEntry dlFileEntry) {
		return _dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);
	}

	@Override
	public DLFileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease
				dlVersionNumberIncrease,
			long fileEntryTypeId,
			java.util.Map
				<String, com.liferay.dynamic.data.mapping.kernel.DDMFormValues>
					ddmFormValuesMap,
			java.io.File file, java.io.InputStream inputStream, long size,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, fileEntryTypeId,
			ddmFormValuesMap, file, inputStream, size, displayDate,
			expirationDate, reviewDate, serviceContext);
	}

	@Override
	public DLFileEntry updateFileEntryType(
			long userId, long fileEntryId, long fileEntryTypeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.updateFileEntryType(
			userId, fileEntryId, fileEntryTypeId, serviceContext);
	}

	@Override
	public DLFileEntry updateStatus(
			long userId, DLFileEntry dlFileEntry,
			com.liferay.document.library.kernel.model.DLFileVersion
				dlFileVersion,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.updateStatus(
			userId, dlFileEntry, dlFileVersion, status, serviceContext,
			workflowContext);
	}

	@Override
	public DLFileEntry updateStatus(
			long userId, long fileVersionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.updateStatus(
			userId, fileVersionId, status, serviceContext, workflowContext);
	}

	@Override
	public void validateFile(
			long groupId, long folderId, long fileEntryId, String fileName,
			String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryLocalService.validateFile(
			groupId, folderId, fileEntryId, fileName, title);
	}

	@Override
	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.verifyFileEntryCheckOut(
			fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryLocalService.verifyFileEntryLock(
			fileEntryId, lockUuid);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlFileEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DLFileEntry> getCTPersistence() {
		return _dlFileEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<DLFileEntry> getModelClass() {
		return _dlFileEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFileEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _dlFileEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DLFileEntryLocalService getWrappedService() {
		return _dlFileEntryLocalService;
	}

	@Override
	public void setWrappedService(
		DLFileEntryLocalService dlFileEntryLocalService) {

		_dlFileEntryLocalService = dlFileEntryLocalService;
	}

	private DLFileEntryLocalService _dlFileEntryLocalService;

}
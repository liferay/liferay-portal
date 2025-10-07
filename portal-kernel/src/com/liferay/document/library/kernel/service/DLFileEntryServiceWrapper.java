/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DLFileEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryService
 * @generated
 */
public class DLFileEntryServiceWrapper
	implements DLFileEntryService, ServiceWrapper<DLFileEntryService> {

	public DLFileEntryServiceWrapper() {
		this(null);
	}

	public DLFileEntryServiceWrapper(DLFileEntryService dlFileEntryService) {
		_dlFileEntryService = dlFileEntryService;
	}

	@Override
	public DLFileEntry addFileEntry(
			String externalReferenceCode, long groupId, long repositoryId,
			long folderId, String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			long fileEntryTypeId,
			java.util.Map
				<String, com.liferay.dynamic.data.mapping.kernel.DDMFormValues>
					ddmFormValuesMap,
			java.io.File file, java.io.InputStream inputStream, long size,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.addFileEntry(
			externalReferenceCode, groupId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			fileEntryTypeId, ddmFormValuesMap, file, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);
	}

	@Override
	public com.liferay.document.library.kernel.model.DLFileVersion
			cancelCheckOut(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.cancelCheckOut(fileEntryId);
	}

	@Override
	public void checkInFileEntry(
			long fileEntryId,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease
				dlVersionNumberIncrease,
			String changeLog,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryService.checkInFileEntry(
			fileEntryId, dlVersionNumberIncrease, changeLog, serviceContext);
	}

	@Override
	public void checkInFileEntry(
			long fileEntryId, String lockUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryService.checkInFileEntry(
			fileEntryId, lockUuid, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long fileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.checkOutFileEntry(
			fileEntryId, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long fileEntryId, String owner, long expirationTime,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.checkOutFileEntry(
			fileEntryId, owner, expirationTime, serviceContext);
	}

	@Override
	public DLFileEntry copyFileEntry(
			long groupId, long repositoryId, long sourceFileEntryId,
			long targetFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.copyFileEntry(
			groupId, repositoryId, sourceFileEntryId, targetFolderId,
			serviceContext);
	}

	@Override
	public void deleteFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryService.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileEntry(long groupId, long folderId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryService.deleteFileEntry(groupId, folderId, title);
	}

	@Override
	public void deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryService.deleteFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public void deleteFileVersion(long fileEntryId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryService.deleteFileVersion(fileEntryId, version);
	}

	@Override
	public DLFileEntry fetchFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.fetchFileEntry(fileEntryId);
	}

	@Override
	public DLFileEntry fetchFileEntry(long groupId, long folderId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.fetchFileEntry(groupId, folderId, title);
	}

	@Override
	public DLFileEntry fetchFileEntryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.fetchFileEntryByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	@Override
	public DLFileEntry fetchFileEntryByImageId(long imageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.fetchFileEntryByImageId(imageId);
	}

	@Override
	public java.io.InputStream getFileAsStream(long fileEntryId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileAsStream(fileEntryId, version);
	}

	@Override
	public java.io.InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileAsStream(
			fileEntryId, version, incrementCounter);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
			long groupId, double score, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntries(groupId, score, start, end);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
			long groupId, long folderId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntries(
			groupId, folderId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
			long groupId, long folderId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntries(
			groupId, folderId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
			long groupId, long folderId, long fileEntryTypeId, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntries(
			groupId, folderId, fileEntryTypeId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
			long groupId, long folderId, String[] mimeTypes, int status,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntries(
			groupId, folderId, mimeTypes, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getFileEntries(
			long groupId, long folderId, String[] mimeTypes, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntries(
			groupId, folderId, mimeTypes, start, end, orderByComparator);
	}

	@Override
	public int getFileEntriesCount(long groupId, double score)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntriesCount(groupId, score);
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId) {
		return _dlFileEntryService.getFileEntriesCount(groupId, folderId);
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId, int status) {
		return _dlFileEntryService.getFileEntriesCount(
			groupId, folderId, status);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long folderId, long fileEntryTypeId) {

		return _dlFileEntryService.getFileEntriesCount(
			groupId, folderId, fileEntryTypeId);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long folderId, String[] mimeTypes) {

		return _dlFileEntryService.getFileEntriesCount(
			groupId, folderId, mimeTypes);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long folderId, String[] mimeTypes, int status) {

		return _dlFileEntryService.getFileEntriesCount(
			groupId, folderId, mimeTypes, status);
	}

	@Override
	public DLFileEntry getFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntry(fileEntryId);
	}

	@Override
	public DLFileEntry getFileEntry(long groupId, long folderId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntry(groupId, folderId, title);
	}

	@Override
	public DLFileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public DLFileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntryByFileName(
			groupId, folderId, fileName);
	}

	@Override
	public DLFileEntry getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getFileEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.lock.Lock getFileEntryLock(
		long fileEntryId) {

		return _dlFileEntryService.getFileEntryLock(fileEntryId);
	}

	@Override
	public int getFoldersFileEntriesCount(
		long groupId, java.util.List<Long> folderIds, int status) {

		return _dlFileEntryService.getFoldersFileEntriesCount(
			groupId, folderIds, status);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getGroupFileEntries(
			groupId, userId, rootFolderId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
			long groupId, long userId, long repositoryId, long rootFolderId,
			String[] mimeTypes, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getGroupFileEntries(
			groupId, userId, repositoryId, rootFolderId, mimeTypes, status,
			start, end, orderByComparator);
	}

	@Override
	public java.util.List<DLFileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getGroupFileEntries(
			groupId, userId, rootFolderId, mimeTypes, status, start, end,
			orderByComparator);
	}

	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getGroupFileEntriesCount(
			groupId, userId, rootFolderId);
	}

	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long repositoryId, long rootFolderId,
			String[] mimeTypes, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getGroupFileEntriesCount(
			groupId, userId, repositoryId, rootFolderId, mimeTypes, status);
	}

	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.getGroupFileEntriesCount(
			groupId, userId, rootFolderId, mimeTypes, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlFileEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public boolean hasFileEntryLock(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.hasFileEntryLock(fileEntryId);
	}

	@Override
	public boolean isFileEntryCheckedOut(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.isFileEntryCheckedOut(fileEntryId);
	}

	@Override
	public DLFileEntry moveFileEntry(
			long fileEntryId, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.moveFileEntry(
			fileEntryId, newFolderId, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.lock.Lock refreshFileEntryLock(
			String lockUuid, long companyId, long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.refreshFileEntryLock(
			lockUuid, companyId, expirationTime);
	}

	@Override
	public void revertFileEntry(
			long fileEntryId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryService.revertFileEntry(
			fileEntryId, version, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
			long groupId, long creatorUserId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.search(
			groupId, creatorUserId, status, start, end);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
			long groupId, long creatorUserId, long folderId, String[] mimeTypes,
			int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.search(
			groupId, creatorUserId, folderId, mimeTypes, status, start, end);
	}

	@Override
	public DLFileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
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

		return _dlFileEntryService.updateFileEntry(
			fileEntryId, sourceFileName, mimeType, title, urlTitle, description,
			changeLog, dlVersionNumberIncrease, fileEntryTypeId,
			ddmFormValuesMap, file, inputStream, size, displayDate,
			expirationDate, reviewDate, serviceContext);
	}

	@Override
	public DLFileEntry updateStatus(
			long userId, long fileVersionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.updateStatus(
			userId, fileVersionId, status, serviceContext, workflowContext);
	}

	@Override
	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.verifyFileEntryCheckOut(
			fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryService.verifyFileEntryLock(fileEntryId, lockUuid);
	}

	@Override
	public DLFileEntryService getWrappedService() {
		return _dlFileEntryService;
	}

	@Override
	public void setWrappedService(DLFileEntryService dlFileEntryService) {
		_dlFileEntryService = dlFileEntryService;
	}

	private DLFileEntryService _dlFileEntryService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DLAppHelperLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLAppHelperLocalService
 * @generated
 */
public class DLAppHelperLocalServiceWrapper
	implements DLAppHelperLocalService,
			   ServiceWrapper<DLAppHelperLocalService> {

	public DLAppHelperLocalServiceWrapper() {
		this(null);
	}

	public DLAppHelperLocalServiceWrapper(
		DLAppHelperLocalService dlAppHelperLocalService) {

		_dlAppHelperLocalService = dlAppHelperLocalService;
	}

	@Override
	public void addFolder(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.addFolder(userId, folder, serviceContext);
	}

	@Override
	public void cancelCheckOut(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				sourceFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				destinationFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				draftFileVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.cancelCheckOut(
			userId, fileEntry, sourceFileVersion, destinationFileVersion,
			draftFileVersion, serviceContext);
	}

	@Override
	public void cancelCheckOuts(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.cancelCheckOuts(groupId);
	}

	@Override
	public void checkAssetEntry(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.checkAssetEntry(
			userId, fileEntry, fileVersion);
	}

	@Override
	public void deleteFileEntry(
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.deleteFileEntry(fileEntry);
	}

	@Override
	public void deleteFolder(
			com.liferay.portal.kernel.repository.model.Folder folder)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.deleteFolder(folder);
	}

	@Override
	public void deleteRepositoryFileEntries(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.deleteRepositoryFileEntries(repositoryId);
	}

	@Override
	public long getCheckedOutFileEntriesCount(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.getCheckedOutFileEntriesCount(groupId);
	}

	@Override
	public void getFileAsStream(
		long userId,
		com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
		boolean incrementCounter) {

		_dlAppHelperLocalService.getFileAsStream(
			userId, fileEntry, incrementCounter);
	}

	@Override
	public java.util.List
		<com.liferay.document.library.kernel.model.DLFileShortcut>
			getGroupFileShortcuts(long groupId) {

		return _dlAppHelperLocalService.getGroupFileShortcuts(groupId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.repository.model.FileEntry>
		getNoAssetFileEntries() {

		return _dlAppHelperLocalService.getNoAssetFileEntries();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlAppHelperLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public void moveDependentsToTrash(
			com.liferay.document.library.kernel.model.DLFolder dlFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.moveDependentsToTrash(dlFolder);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			moveFileEntryFromTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
				long newFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.moveFileEntryFromTrash(
			userId, fileEntry, newFolderId, serviceContext);
	}

	/**
	 * Moves the file entry to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the file entry
	 * @param fileEntry the file entry to be moved
	 * @return the moved file entry
	 */
	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			moveFileEntryToTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.moveFileEntryToTrash(userId, fileEntry);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileShortcut
			moveFileShortcutFromTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileShortcut
					fileShortcut,
				long newFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.moveFileShortcutFromTrash(
			userId, fileShortcut, newFolderId, serviceContext);
	}

	/**
	 * Moves the file shortcut to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the file shortcut
	 * @param fileShortcut the file shortcut to be moved
	 * @return the moved file shortcut
	 */
	@Override
	public com.liferay.portal.kernel.repository.model.FileShortcut
			moveFileShortcutToTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileShortcut
					fileShortcut)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.moveFileShortcutToTrash(
			userId, fileShortcut);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.Folder
			moveFolderFromTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.Folder folder,
				long parentFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.moveFolderFromTrash(
			userId, folder, parentFolderId, serviceContext);
	}

	/**
	 * Moves the folder to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the folder
	 * @param folder the folder to be moved
	 * @return the moved folder
	 */
	@Override
	public com.liferay.portal.kernel.repository.model.Folder moveFolderToTrash(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.moveFolderToTrash(userId, folder);
	}

	@Override
	public void reindex(long companyId, java.util.List<Long> dlFileEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.reindex(companyId, dlFileEntryIds);
	}

	@Override
	public void restoreDependentsFromTrash(
			com.liferay.document.library.kernel.model.DLFolder dlFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.restoreDependentsFromTrash(dlFolder);
	}

	@Override
	public void restoreFileEntryFromTrash(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.restoreFileEntryFromTrash(userId, fileEntry);
	}

	@Override
	public void restoreFileEntryFromTrash(
			long userId, long newFolderId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.restoreFileEntryFromTrash(
			userId, newFolderId, fileEntry);
	}

	@Override
	public void restoreFileShortcutFromTrash(
			long userId,
			com.liferay.portal.kernel.repository.model.FileShortcut
				fileShortcut)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.restoreFileShortcutFromTrash(
			userId, fileShortcut);
	}

	@Override
	public void restoreFolderFromTrash(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.restoreFolderFromTrash(userId, folder);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetEntry updateAsset(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion,
			long assetClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.updateAsset(
			userId, fileEntry, fileVersion, assetClassPK);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetEntry updateAsset(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.updateAsset(
			userId, fileEntry, fileVersion, serviceContext);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetEntry updateAsset(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlAppHelperLocalService.updateAsset(
			userId, folder, assetCategoryIds, assetTagNames, assetLinkEntryIds);
	}

	@Override
	public void updateFileEntry(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				sourceFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				destinationFileVersion,
			long assetClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.updateFileEntry(
			userId, fileEntry, sourceFileVersion, destinationFileVersion,
			assetClassPK);
	}

	@Override
	public void updateFileEntry(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				sourceFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				destinationFileVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.updateFileEntry(
			userId, fileEntry, sourceFileVersion, destinationFileVersion,
			serviceContext);
	}

	@Override
	public void updateFolder(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.updateFolder(userId, folder, serviceContext);
	}

	@Override
	public void updateStatus(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				latestFileVersion,
			int oldStatus, int newStatus,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlAppHelperLocalService.updateStatus(
			userId, fileEntry, latestFileVersion, oldStatus, newStatus,
			serviceContext, workflowContext);
	}

	@Override
	public DLAppHelperLocalService getWrappedService() {
		return _dlAppHelperLocalService;
	}

	@Override
	public void setWrappedService(
		DLAppHelperLocalService dlAppHelperLocalService) {

		_dlAppHelperLocalService = dlAppHelperLocalService;
	}

	private DLAppHelperLocalService _dlAppHelperLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1679531892
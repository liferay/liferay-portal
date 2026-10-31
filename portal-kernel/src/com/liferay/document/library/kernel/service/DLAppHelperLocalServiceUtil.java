/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for DLAppHelper. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLAppHelperLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLAppHelperLocalService
 * @generated
 */
public class DLAppHelperLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLAppHelperLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addFolder(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addFolder(userId, folder, serviceContext);
	}

	public static void cancelCheckOut(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				sourceFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				destinationFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				draftFileVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().cancelCheckOut(
			userId, fileEntry, sourceFileVersion, destinationFileVersion,
			draftFileVersion, serviceContext);
	}

	public static void cancelCheckOuts(long groupId) throws PortalException {
		getService().cancelCheckOuts(groupId);
	}

	public static void checkAssetEntry(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion)
		throws PortalException {

		getService().checkAssetEntry(userId, fileEntry, fileVersion);
	}

	public static void deleteFileEntry(
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		getService().deleteFileEntry(fileEntry);
	}

	public static void deleteFolder(
			com.liferay.portal.kernel.repository.model.Folder folder)
		throws PortalException {

		getService().deleteFolder(folder);
	}

	public static void deleteRepositoryFileEntries(long repositoryId)
		throws PortalException {

		getService().deleteRepositoryFileEntries(repositoryId);
	}

	public static long getCheckedOutFileEntriesCount(long groupId)
		throws PortalException {

		return getService().getCheckedOutFileEntriesCount(groupId);
	}

	public static void getFileAsStream(
		long userId,
		com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
		boolean incrementCounter) {

		getService().getFileAsStream(userId, fileEntry, incrementCounter);
	}

	public static List<com.liferay.document.library.kernel.model.DLFileShortcut>
		getGroupFileShortcuts(long groupId) {

		return getService().getGroupFileShortcuts(groupId);
	}

	public static List<com.liferay.portal.kernel.repository.model.FileEntry>
		getNoAssetFileEntries() {

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

	public static void moveDependentsToTrash(
			com.liferay.document.library.kernel.model.DLFolder dlFolder)
		throws PortalException {

		getService().moveDependentsToTrash(dlFolder);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			moveFileEntryFromTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
				long newFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFileEntryFromTrash(
			userId, fileEntry, newFolderId, serviceContext);
	}

	/**
	 * Moves the file entry to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the file entry
	 * @param fileEntry the file entry to be moved
	 * @return the moved file entry
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			moveFileEntryToTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		return getService().moveFileEntryToTrash(userId, fileEntry);
	}

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			moveFileShortcutFromTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileShortcut
					fileShortcut,
				long newFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFileShortcutFromTrash(
			userId, fileShortcut, newFolderId, serviceContext);
	}

	/**
	 * Moves the file shortcut to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the file shortcut
	 * @param fileShortcut the file shortcut to be moved
	 * @return the moved file shortcut
	 */
	public static com.liferay.portal.kernel.repository.model.FileShortcut
			moveFileShortcutToTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.FileShortcut
					fileShortcut)
		throws PortalException {

		return getService().moveFileShortcutToTrash(userId, fileShortcut);
	}

	public static com.liferay.portal.kernel.repository.model.Folder
			moveFolderFromTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.Folder folder,
				long parentFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFolderFromTrash(
			userId, folder, parentFolderId, serviceContext);
	}

	/**
	 * Moves the folder to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the folder
	 * @param folder the folder to be moved
	 * @return the moved folder
	 */
	public static com.liferay.portal.kernel.repository.model.Folder
			moveFolderToTrash(
				long userId,
				com.liferay.portal.kernel.repository.model.Folder folder)
		throws PortalException {

		return getService().moveFolderToTrash(userId, folder);
	}

	public static void reindex(long companyId, List<Long> dlFileEntryIds)
		throws PortalException {

		getService().reindex(companyId, dlFileEntryIds);
	}

	public static void restoreDependentsFromTrash(
			com.liferay.document.library.kernel.model.DLFolder dlFolder)
		throws PortalException {

		getService().restoreDependentsFromTrash(dlFolder);
	}

	public static void restoreFileEntryFromTrash(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		getService().restoreFileEntryFromTrash(userId, fileEntry);
	}

	public static void restoreFileEntryFromTrash(
			long userId, long newFolderId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		getService().restoreFileEntryFromTrash(userId, newFolderId, fileEntry);
	}

	public static void restoreFileShortcutFromTrash(
			long userId,
			com.liferay.portal.kernel.repository.model.FileShortcut
				fileShortcut)
		throws PortalException {

		getService().restoreFileShortcutFromTrash(userId, fileShortcut);
	}

	public static void restoreFolderFromTrash(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder)
		throws PortalException {

		getService().restoreFolderFromTrash(userId, folder);
	}

	public static com.liferay.asset.kernel.model.AssetEntry updateAsset(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion,
			long assetClassPK)
		throws PortalException {

		return getService().updateAsset(
			userId, fileEntry, fileVersion, assetClassPK);
	}

	public static com.liferay.asset.kernel.model.AssetEntry updateAsset(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAsset(
			userId, fileEntry, fileVersion, serviceContext);
	}

	public static com.liferay.asset.kernel.model.AssetEntry updateAsset(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds)
		throws PortalException {

		return getService().updateAsset(
			userId, folder, assetCategoryIds, assetTagNames, assetLinkEntryIds);
	}

	public static void updateFileEntry(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				sourceFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				destinationFileVersion,
			long assetClassPK)
		throws PortalException {

		getService().updateFileEntry(
			userId, fileEntry, sourceFileVersion, destinationFileVersion,
			assetClassPK);
	}

	public static void updateFileEntry(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				sourceFileVersion,
			com.liferay.portal.kernel.repository.model.FileVersion
				destinationFileVersion,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().updateFileEntry(
			userId, fileEntry, sourceFileVersion, destinationFileVersion,
			serviceContext);
	}

	public static void updateFolder(
			long userId,
			com.liferay.portal.kernel.repository.model.Folder folder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().updateFolder(userId, folder, serviceContext);
	}

	public static void updateStatus(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion
				latestFileVersion,
			int oldStatus, int newStatus,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		getService().updateStatus(
			userId, fileEntry, latestFileVersion, oldStatus, newStatus,
			serviceContext, workflowContext);
	}

	public static DLAppHelperLocalService getService() {
		return _service;
	}

	public static void setService(DLAppHelperLocalService service) {
		_service = service;
	}

	private static volatile DLAppHelperLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-964732356
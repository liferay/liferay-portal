/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portlet.documentlibrary.service.base.DLTrashServiceBaseImpl;

/**
 * @author Adolfo Pérez
 */
public class DLTrashServiceImpl extends DLTrashServiceBaseImpl {

	/**
	 * Moves the file entry from a trashed folder to the new folder.
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @param  newFolderId the primary key of the new folder
	 * @param  serviceContext the service context to be applied
	 * @return the file entry
	 */
	@Override
	public FileEntry moveFileEntryFromTrash(
			long fileEntryId, long newFolderId, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntry, ActionKeys.UPDATE);

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), fileEntry.getGroupId(), newFolderId,
			ActionKeys.ADD_DOCUMENT);

		Folder destinationFolder = null;

		if (newFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			destinationFolder = repository.getFolder(newFolderId);
		}

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		return trashCapability.moveFileEntryFromTrash(
			getUserId(), fileEntry, destinationFolder, serviceContext);
	}

	/**
	 * Moves the file entry with the primary key to the trash portlet.
	 *
	 * @param  fileEntryId the primary key of the file entry
	 * @return the file entry
	 */
	@Override
	public FileEntry moveFileEntryToTrash(long fileEntryId)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntry, ActionKeys.DELETE);

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		return trashCapability.moveFileEntryToTrash(getUserId(), fileEntry);
	}

	/**
	 * Moves the file shortcut from a trashed folder to the new folder.
	 *
	 * @param  fileShortcutId the primary key of the file shortcut
	 * @param  newFolderId the primary key of the new folder
	 * @param  serviceContext the service context to be applied
	 * @return the file shortcut
	 */
	@Override
	public FileShortcut moveFileShortcutFromTrash(
			long fileShortcutId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<FileShortcut>
			fileShortcutModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					FileShortcut.class.getName());

		Repository repository =
			RepositoryProviderUtil.getFileShortcutRepository(fileShortcutId);

		FileShortcut fileShortcut = repository.getFileShortcut(fileShortcutId);

		fileShortcutModelResourcePermission.check(
			getPermissionChecker(), fileShortcut, ActionKeys.UPDATE);

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), fileShortcut.getGroupId(), newFolderId,
			ActionKeys.ADD_SHORTCUT);

		Folder destinationFolder = null;

		if (newFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			destinationFolder = repository.getFolder(newFolderId);
		}

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		return trashCapability.moveFileShortcutFromTrash(
			getUserId(), fileShortcut, destinationFolder, serviceContext);
	}

	/**
	 * Moves the file shortcut with the primary key to the trash portlet.
	 *
	 * @param  fileShortcutId the primary key of the file shortcut
	 * @return the file shortcut
	 */
	@Override
	public FileShortcut moveFileShortcutToTrash(long fileShortcutId)
		throws PortalException {

		ModelResourcePermission<FileShortcut>
			fileShortcutModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					FileShortcut.class.getName());

		Repository repository =
			RepositoryProviderUtil.getFileShortcutRepository(fileShortcutId);

		FileShortcut fileShortcut = repository.getFileShortcut(fileShortcutId);

		fileShortcutModelResourcePermission.check(
			getPermissionChecker(), fileShortcut, ActionKeys.DELETE);

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		return trashCapability.moveFileShortcutToTrash(
			getUserId(), fileShortcut);
	}

	/**
	 * Moves the folder with the primary key from the trash portlet to the new
	 * parent folder with the primary key.
	 *
	 * @param  folderId the primary key of the folder
	 * @param  parentFolderId the primary key of the new parent folder
	 * @param  serviceContext the service context to be applied
	 * @return the file entry
	 */
	@Override
	public Folder moveFolderFromTrash(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<Folder> folderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				Folder.class.getName());

		Repository repository = RepositoryProviderUtil.getFolderRepository(
			folderId);

		Folder folder = repository.getFolder(folderId);

		folderModelResourcePermission.check(
			getPermissionChecker(), folder, ActionKeys.UPDATE);

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), folder.getGroupId(), parentFolderId,
			ActionKeys.ADD_FOLDER);

		Folder destinationFolder = null;

		if (parentFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			destinationFolder = repository.getFolder(parentFolderId);
		}

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		return trashCapability.moveFolderFromTrash(
			getUserId(), folder, destinationFolder, serviceContext);
	}

	/**
	 * Moves the folder with the primary key to the trash portlet.
	 *
	 * @param  folderId the primary key of the folder
	 * @return the file entry
	 */
	@Override
	public Folder moveFolderToTrash(long folderId) throws PortalException {
		ModelResourcePermission<Folder> folderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				Folder.class.getName());

		Repository repository = RepositoryProviderUtil.getFolderRepository(
			folderId);

		Folder folder = repository.getFolder(folderId);

		folderModelResourcePermission.check(
			getPermissionChecker(), folder, ActionKeys.DELETE);

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		return trashCapability.moveFolderToTrash(getUserId(), folder);
	}

	/**
	 * Restores the file entry with the primary key from the trash portlet.
	 *
	 * @param fileEntryId the primary key of the file entry
	 */
	@Override
	public void restoreFileEntryFromTrash(long fileEntryId)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		Repository repository = RepositoryProviderUtil.getFileEntryRepository(
			fileEntryId);

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntry, ActionKeys.DELETE);

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		trashCapability.restoreFileEntryFromTrash(getUserId(), fileEntry);
	}

	/**
	 * Restores the file shortcut with the primary key from the trash portlet.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 */
	@Override
	public void restoreFileShortcutFromTrash(long fileShortcutId)
		throws PortalException {

		ModelResourcePermission<FileShortcut>
			fileShortcutModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					FileShortcut.class.getName());

		Repository repository =
			RepositoryProviderUtil.getFileShortcutRepository(fileShortcutId);

		FileShortcut fileShortcut = repository.getFileShortcut(fileShortcutId);

		fileShortcutModelResourcePermission.check(
			getPermissionChecker(), fileShortcut, ActionKeys.DELETE);

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		trashCapability.restoreFileShortcutFromTrash(getUserId(), fileShortcut);
	}

	/**
	 * Restores the folder with the primary key from the trash portlet.
	 *
	 * @param folderId the primary key of the folder
	 */
	@Override
	public void restoreFolderFromTrash(long folderId) throws PortalException {
		ModelResourcePermission<Folder> folderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				Folder.class.getName());

		Repository repository = RepositoryProviderUtil.getFolderRepository(
			folderId);

		Folder folder = repository.getFolder(folderId);

		folderModelResourcePermission.check(
			getPermissionChecker(), folder, ActionKeys.DELETE);

		TrashCapability trashCapability = repository.getCapability(
			TrashCapability.class);

		trashCapability.restoreFolderFromTrash(getUserId(), folder);
	}

}
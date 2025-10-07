/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.repository.proxy;

import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.InputStream;

import java.util.Date;
import java.util.List;

/**
 * @author Mika Koivisto
 */
public class LocalRepositoryProxyBean
	extends RepositoryModelProxyBean implements LocalRepository {

	public LocalRepositoryProxyBean(
		LocalRepository localRepository, ClassLoader classLoader) {

		super(classLoader);

		_localRepository = localRepository;
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _localRepository.addFileEntry(
			externalReferenceCode, userId, folderId, sourceFileName, mimeType,
			title, urlTitle, description, changeLog, file, displayDate,
			expirationDate, reviewDate, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _localRepository.addFileEntry(
			externalReferenceCode, userId, folderId, sourceFileName, mimeType,
			title, urlTitle, description, changeLog, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileShortcut addFileShortcut(
			String externalReferenceCode, long userId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException {

		FileShortcut fileShortcut = _localRepository.addFileShortcut(
			externalReferenceCode, userId, folderId, toFileEntryId,
			serviceContext);

		return newFileShortcutProxyBean(fileShortcut);
	}

	@Override
	public Folder addFolder(
			String externalReferenceCode, long userId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		Folder folder = _localRepository.addFolder(
			externalReferenceCode, userId, parentFolderId, name, description,
			serviceContext);

		return newFolderProxyBean(folder);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException {

		_localRepository.checkInFileEntry(
			userId, fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException {

		_localRepository.checkInFileEntry(
			userId, fileEntryId, lockUuid, serviceContext);
	}

	@Override
	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _localRepository.copyFileEntry(
			userId, groupId, fileEntryId, destFolderId, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public void deleteAll() throws PortalException {
		_localRepository.deleteAll();
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		_localRepository.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		_localRepository.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId) throws PortalException {
		_localRepository.deleteFileShortcuts(toFileEntryId);
	}

	@Override
	public void deleteFileVersion(long fileVersionId) throws PortalException {
		_localRepository.deleteFileVersion(fileVersionId);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		_localRepository.deleteFolder(folderId);
	}

	@Override
	public FileEntry fetchFileEntry(long fileEntryId) throws PortalException {
		FileEntry fileEntry = _localRepository.fetchFileEntry(fileEntryId);

		if (fileEntry == null) {
			return null;
		}

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileEntry fetchFileEntry(long folderId, String title)
		throws PortalException {

		return newFileEntryProxyBean(
			_localRepository.fetchFileEntry(folderId, title));
	}

	@Override
	public <T extends Capability> T getCapability(Class<T> capabilityClass) {
		return _localRepository.getCapability(capabilityClass);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return _localRepository.getFileEntries(
			folderId, status, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return _localRepository.getFileEntries(
			folderId, start, end, orderByComparator);
	}

	@Override
	public List<RepositoryEntry> getFileEntriesAndFileShortcuts(
			long folderId, int status, int start, int end)
		throws PortalException {

		return _localRepository.getFileEntriesAndFileShortcuts(
			folderId, status, start, end);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(long folderId, int status)
		throws PortalException {

		return _localRepository.getFileEntriesAndFileShortcutsCount(
			folderId, status);
	}

	@Override
	public int getFileEntriesCount(long folderId) throws PortalException {
		return _localRepository.getFileEntriesCount(folderId);
	}

	@Override
	public int getFileEntriesCount(long folderId, int status)
		throws PortalException {

		return _localRepository.getFileEntriesCount(folderId, status);
	}

	@Override
	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		return newFileEntryProxyBean(
			_localRepository.getFileEntry(fileEntryId));
	}

	@Override
	public FileEntry getFileEntry(long folderId, String title)
		throws PortalException {

		return newFileEntryProxyBean(
			_localRepository.getFileEntry(folderId, title));
	}

	@Override
	public FileEntry getFileEntryByFileName(long folderId, String fileName)
		throws PortalException {

		return newFileEntryProxyBean(
			_localRepository.getFileEntryByFileName(folderId, fileName));
	}

	@Override
	public FileEntry getFileEntryByUuid(String uuid) throws PortalException {
		FileEntry fileEntry = _localRepository.getFileEntryByUuid(uuid);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		return newFileShortcutProxyBean(
			_localRepository.getFileShortcut(fileShortcutId));
	}

	@Override
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		return newFileVersionProxyBean(
			_localRepository.getFileVersion(fileVersionId));
	}

	@Override
	public Folder getFolder(long folderId) throws PortalException {
		return newFolderProxyBean(_localRepository.getFolder(folderId));
	}

	@Override
	public Folder getFolder(long parentFolderId, String name)
		throws PortalException {

		return _localRepository.getFolder(parentFolderId, name);
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, boolean includeMountFolders, int start,
			int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return _localRepository.getFolders(
			parentFolderId, includeMountFolders, start, end, orderByComparator);
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, int status, boolean includeMountFolders,
			int start, int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return _localRepository.getFolders(
			parentFolderId, status, includeMountFolders, start, end,
			orderByComparator);
	}

	@Override
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, boolean includeMountFolders, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		return _localRepository.getFoldersAndFileEntriesAndFileShortcuts(
			folderId, status, includeMountFolders, start, end,
			orderByComparator);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, boolean includeMountFolders)
		throws PortalException {

		return _localRepository.getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, includeMountFolders);
	}

	@Override
	public int getFoldersCount(long parentFolderId, boolean includeMountfolders)
		throws PortalException {

		return _localRepository.getFoldersCount(
			parentFolderId, includeMountfolders);
	}

	@Override
	public int getFoldersCount(
			long parentFolderId, int status, boolean includeMountfolders)
		throws PortalException {

		return _localRepository.getFoldersCount(
			parentFolderId, status, includeMountfolders);
	}

	@Override
	public List<FileEntry> getRepositoryFileEntries(
			long userId, long rootFolderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return _localRepository.getRepositoryFileEntries(
			userId, rootFolderId, start, end, orderByComparator);
	}

	@Override
	public long getRepositoryId() {
		return _localRepository.getRepositoryId();
	}

	@Override
	public <T extends Capability> boolean isCapabilityProvided(
		Class<T> capabilityClass) {

		return _localRepository.isCapabilityProvided(capabilityClass);
	}

	@Override
	public FileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _localRepository.moveFileEntry(
			userId, fileEntryId, newFolderId, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public Folder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		Folder folder = _localRepository.moveFolder(
			userId, folderId, parentFolderId, serviceContext);

		return newFolderProxyBean(folder);
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		_localRepository.revertFileEntry(
			userId, fileEntryId, version, serviceContext);
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			File file, Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _localRepository.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, file, displayDate,
			expirationDate, reviewDate, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _localRepository.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		FileShortcut fileShortcut = _localRepository.updateFileShortcut(
			userId, fileShortcutId, folderId, toFileEntryId, serviceContext);

		return newFileShortcutProxyBean(fileShortcut);
	}

	@Override
	public void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws PortalException {

		_localRepository.updateFileShortcuts(
			oldToFileEntryId, newToFileEntryId);
	}

	@Override
	public Folder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		return _localRepository.updateFolder(
			folderId, parentFolderId, name, description, serviceContext);
	}

	private final LocalRepository _localRepository;

}
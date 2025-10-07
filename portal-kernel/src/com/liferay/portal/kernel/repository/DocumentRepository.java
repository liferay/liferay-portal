/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.repository;

import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.capabilities.CapabilityProvider;
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
 * @author Iván Zaera
 */
public interface DocumentRepository extends CapabilityProvider {

	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException;

	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException;

	public FileShortcut addFileShortcut(
			String externalReferenceCode, long userId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException;

	public Folder addFolder(
			String externalReferenceCode, long userId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException;

	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException;

	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException;

	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	public void deleteAll() throws PortalException;

	public void deleteFileEntry(long fileEntryId) throws PortalException;

	public void deleteFileShortcut(long fileShortcutId) throws PortalException;

	public void deleteFileShortcuts(long toFileEntryId) throws PortalException;

	public void deleteFileVersion(long fileVersionId) throws PortalException;

	public void deleteFolder(long folderId) throws PortalException;

	public default FileEntry fetchFileEntry(long fileEntryId)
		throws PortalException {

		return getFileEntry(fileEntryId);
	}

	public default FileEntry fetchFileEntry(long folderId, String title)
		throws PortalException {

		return getFileEntry(folderId, title);
	}

	public default FileEntry fetchFileEntryByExternalReferenceCode(
		String externalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public default FileShortcut fetchFileShortcut(long fileShortcutId) {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public default FileShortcut fetchFileShortcutByExternalReferenceCode(
		String externalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public default Folder fetchFolderByExternalReferenceCode(
		String externalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public List<FileEntry> getFileEntries(
			long folderId, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	public List<FileEntry> getFileEntries(
			long folderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	public default List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getFileEntries(folderId, status, start, end, orderByComparator);
	}

	public List<RepositoryEntry> getFileEntriesAndFileShortcuts(
			long folderId, int status, int start, int end)
		throws PortalException;

	public int getFileEntriesAndFileShortcutsCount(long folderId, int status)
		throws PortalException;

	public int getFileEntriesCount(long folderId) throws PortalException;

	public int getFileEntriesCount(long folderId, int status)
		throws PortalException;

	public default int getFileEntriesCount(
			long folderId, String[] mimeTypes, int status)
		throws PortalException {

		return getFileEntriesCount(folderId, status);
	}

	public FileEntry getFileEntry(long fileEntryId) throws PortalException;

	public FileEntry getFileEntry(long folderId, String title)
		throws PortalException;

	public default FileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public default FileEntry getFileEntryByFileName(
			long folderId, String fileName)
		throws PortalException {

		return getFileEntry(folderId, fileName);
	}

	public FileEntry getFileEntryByUuid(String uuid) throws PortalException;

	public FileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException;

	public default FileShortcut getFileShortcutByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException;

	public Folder getFolder(long folderId) throws PortalException;

	public Folder getFolder(long parentFolderId, String name)
		throws PortalException;

	public default Folder getFolderByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public List<Folder> getFolders(
			long parentFolderId, boolean includeMountFolders, int start,
			int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException;

	public List<Folder> getFolders(
			long parentFolderId, int status, boolean includeMountFolders,
			int start, int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException;

	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, boolean includeMountFolders, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException;

	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, boolean includeMountFolders)
		throws PortalException;

	public int getFoldersCount(long parentFolderId, boolean includeMountfolders)
		throws PortalException;

	public int getFoldersCount(
			long parentFolderId, int status, boolean includeMountfolders)
		throws PortalException;

	public List<FileEntry> getRepositoryFileEntries(
			long userId, long rootFolderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	public long getRepositoryId();

	public FileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	public Folder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException;

	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			File file, Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException;

	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException;

	public FileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException;

	public void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws PortalException;

	public Folder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException;

}
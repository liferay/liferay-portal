/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.repository;

import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.portal.kernel.exception.PortalException;
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
 * This class is designed for third party repository implementations. Since the
 * paradigm of remote and local services exists only within Liferay, the
 * assumption is that all permission checking will be delegated to the specific
 * repository.
 *
 * There are also many calls within this class that pass in a user ID as a
 * parameter. These methods should only be called for administration of Liferay
 * repositories and are hence not supported in all third party repositories.
 * This includes moving between document library hooks and LAR import/export.
 * Calling these methods will throw an
 * <code>UnsupportedOperationException</code>.
 *
 * @author Alexander Chow
 */
public class DefaultLocalRepositoryImpl implements LocalRepository {

	public DefaultLocalRepositoryImpl(Repository repository) {
		_repository = repository;
	}

	@Override
	public FileEntry addFileEntry(
		String externalReferenceCode, long userId, long folderId,
		String sourceFileName, String mimeType, String title, String urlTitle,
		String description, String changeLog, File file, Date displayDate,
		Date expirationDate, Date reviewDate, ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FileEntry addFileEntry(
		String externalReferenceCode, long userId, long folderId,
		String sourceFileName, String mimeType, String title, String urlTitle,
		String description, String changeLog, InputStream inputStream,
		long size, Date displayDate, Date expirationDate, Date reviewDate,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FileShortcut addFileShortcut(
		String externalReferenceCode, long userId, long folderId,
		long toFileEntryId, ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Folder addFolder(
		String externalReferenceCode, long userId, long parentFolderId,
		String name, String description, ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException {

		_repository.checkInFileEntry(
			userId, fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException {

		_repository.checkInFileEntry(
			userId, fileEntryId, lockUuid, serviceContext);
	}

	@Override
	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		return _repository.copyFileEntry(
			userId, groupId, fileEntryId, destFolderId, serviceContext);
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		_repository.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		_repository.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId) throws PortalException {
		_repository.deleteFileShortcuts(toFileEntryId);
	}

	@Override
	public void deleteFileVersion(long fileVersionId) throws PortalException {
		_repository.deleteFileVersion(fileVersionId);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		_repository.deleteFolder(folderId);
	}

	@Override
	public FileEntry fetchFileEntry(long fileEntryId) throws PortalException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileEntry fetchFileEntry(long folderId, String title)
		throws PortalException {

		return _repository.fetchFileEntry(folderId, title);
	}

	@Override
	public <T extends Capability> T getCapability(Class<T> capabilityClass) {
		return _repository.getCapability(capabilityClass);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return _repository.getFileEntries(
			folderId, status, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return _repository.getFileEntries(
			folderId, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return _repository.getFileEntries(
			folderId, mimeTypes, status, start, end, orderByComparator);
	}

	@Override
	public List<RepositoryEntry> getFileEntriesAndFileShortcuts(
			long folderId, int status, int start, int end)
		throws PortalException {

		return _repository.getFileEntriesAndFileShortcuts(
			folderId, status, start, end);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(long folderId, int status)
		throws PortalException {

		return _repository.getFileEntriesAndFileShortcutsCount(
			folderId, status);
	}

	@Override
	public int getFileEntriesCount(long folderId) throws PortalException {
		return _repository.getFileEntriesCount(folderId);
	}

	@Override
	public int getFileEntriesCount(long folderId, int status)
		throws PortalException {

		return _repository.getFileEntriesCount(folderId, status);
	}

	@Override
	public int getFileEntriesCount(
			long folderId, String[] mimeTypes, int status)
		throws PortalException {

		return _repository.getFileEntriesCount(folderId, mimeTypes, status);
	}

	@Override
	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		return _repository.getFileEntry(fileEntryId);
	}

	@Override
	public FileEntry getFileEntry(long folderId, String title)
		throws PortalException {

		return _repository.getFileEntry(folderId, title);
	}

	@Override
	public FileEntry getFileEntryByFileName(long folderId, String fileName)
		throws PortalException {

		return _repository.getFileEntryByFileName(folderId, fileName);
	}

	@Override
	public FileEntry getFileEntryByUuid(String uuid) throws PortalException {
		return _repository.getFileEntryByUuid(uuid);
	}

	@Override
	public FileShortcut getFileShortcut(long fileShortcutId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		return _repository.getFileVersion(fileVersionId);
	}

	@Override
	public Folder getFolder(long folderId) throws PortalException {
		return _repository.getFolder(folderId);
	}

	@Override
	public Folder getFolder(long parentFolderId, String name)
		throws PortalException {

		return _repository.getFolder(parentFolderId, name);
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, boolean includeMountFolders, int start,
			int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return _repository.getFolders(
			parentFolderId, includeMountFolders, start, end, orderByComparator);
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, int status, boolean includeMountFolders,
			int start, int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return _repository.getFolders(
			parentFolderId, status, includeMountFolders, start, end,
			orderByComparator);
	}

	@Override
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, boolean includeMountFolders, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		return _repository.getFoldersAndFileEntriesAndFileShortcuts(
			folderId, status, includeMountFolders, start, end,
			orderByComparator);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, boolean includeMountFolders)
		throws PortalException {

		return _repository.getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, includeMountFolders);
	}

	@Override
	public int getFoldersCount(long parentFolderId, boolean includeMountfolders)
		throws PortalException {

		return _repository.getFoldersCount(parentFolderId, includeMountfolders);
	}

	@Override
	public int getFoldersCount(
			long parentFolderId, int status, boolean includeMountfolders)
		throws PortalException {

		return _repository.getFoldersCount(
			parentFolderId, status, includeMountfolders);
	}

	@Override
	public List<FileEntry> getRepositoryFileEntries(
			long userId, long rootFolderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return _repository.getRepositoryFileEntries(
			userId, rootFolderId, start, end, orderByComparator);
	}

	@Override
	public long getRepositoryId() {
		return _repository.getRepositoryId();
	}

	@Override
	public <T extends Capability> boolean isCapabilityProvided(
		Class<T> capabilityClass) {

		return _repository.isCapabilityProvided(capabilityClass);
	}

	@Override
	public FileEntry moveFileEntry(
		long userId, long fileEntryId, long newFolderId,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Folder moveFolder(
		long userId, long folderId, long parentFolderId,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		_repository.revertFileEntry(
			userId, fileEntryId, version, serviceContext);
	}

	@Override
	public FileEntry updateFileEntry(
		long userId, long fileEntryId, String sourceFileName, String mimeType,
		String title, String urlTitle, String description, String changeLog,
		DLVersionNumberIncrease dlVersionNumberIncrease, File file,
		Date displayDate, Date expirationDate, Date reviewDate,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FileEntry updateFileEntry(
		long userId, long fileEntryId, String sourceFileName, String mimeType,
		String title, String urlTitle, String description, String changeLog,
		DLVersionNumberIncrease dlVersionNumberIncrease,
		InputStream inputStream, long size, Date displayDate,
		Date expirationDate, Date reviewDate, ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public FileShortcut updateFileShortcut(
		long userId, long fileShortcutId, long folderId, long toFileEntryId,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void updateFileShortcuts(
		long oldToFileEntryId, long newToFileEntryId) {

		throw new UnsupportedOperationException();
	}

	@Override
	public Folder updateFolder(
		long folderId, long parentFolderId, String name, String description,
		ServiceContext serviceContext) {

		throw new UnsupportedOperationException();
	}

	private final Repository _repository;

}
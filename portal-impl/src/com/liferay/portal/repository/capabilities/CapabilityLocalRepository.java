/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.capabilities;

import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.capabilities.CapabilityProvider;
import com.liferay.portal.kernel.repository.event.RepositoryEventTrigger;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
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
 * @author Adolfo Pérez
 */
public class CapabilityLocalRepository
	extends BaseCapabilityRepository<LocalRepository>
	implements LocalRepository {

	public CapabilityLocalRepository(
		LocalRepository localRepository, CapabilityProvider capabilityProvider,
		RepositoryEventTrigger repositoryEventTrigger) {

		super(localRepository, capabilityProvider);

		_repositoryEventTrigger = repositoryEventTrigger;
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.addFileEntry(
			externalReferenceCode, userId, folderId, sourceFileName, mimeType,
			title, urlTitle, description, changeLog, file, displayDate,
			expirationDate, reviewDate, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.addFileEntry(
			externalReferenceCode, userId, folderId, sourceFileName, mimeType,
			title, urlTitle, description, changeLog, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public FileShortcut addFileShortcut(
			String externalReferenceCode, long userId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileShortcut fileShortcut = localRepository.addFileShortcut(
			externalReferenceCode, userId, folderId, toFileEntryId,
			serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, FileShortcut.class, fileShortcut);

		return fileShortcut;
	}

	@Override
	public Folder addFolder(
			String externalReferenCode, long userId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		Folder folder = localRepository.addFolder(
			externalReferenCode, userId, parentFolderId, name, description,
			serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, Folder.class, folder);

		return folder;
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		localRepository.checkInFileEntry(
			userId, fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class,
			localRepository.getFileEntry(fileEntryId));
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		localRepository.checkInFileEntry(
			userId, fileEntryId, lockUuid, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class,
			localRepository.getFileEntry(fileEntryId));
	}

	@Override
	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.copyFileEntry(
			userId, groupId, fileEntryId, destFolderId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public void deleteAll() throws PortalException {
		LocalRepository localRepository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, LocalRepository.class,
			localRepository);

		localRepository.deleteAll();
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		LocalRepository localRepository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileEntry.class,
			localRepository.getFileEntry(fileEntryId));

		localRepository.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		LocalRepository localRepository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileShortcut.class,
			localRepository.getFileShortcut(fileShortcutId));

		localRepository.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId) throws PortalException {
		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.getFileEntry(toFileEntryId);

		List<FileShortcut> fileShortcuts = fileEntry.getFileShortcuts();

		for (FileShortcut fileShortcut : fileShortcuts) {
			_repositoryEventTrigger.trigger(
				RepositoryEventType.Delete.class, FileShortcut.class,
				fileShortcut);
		}

		localRepository.deleteFileShortcuts(toFileEntryId);
	}

	@Override
	public void deleteFileVersion(long fileVersionId) throws PortalException {
		LocalRepository localRepository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileVersion.class,
			localRepository.getFileVersion(fileVersionId));

		localRepository.deleteFileVersion(fileVersionId);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		LocalRepository localRepository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, Folder.class,
			localRepository.getFolder(folderId));

		localRepository.deleteFolder(folderId);
	}

	@Override
	public FileEntry fetchFileEntry(long fileEntryId) throws PortalException {
		return getRepository().fetchFileEntry(fileEntryId);
	}

	@Override
	public FileEntry fetchFileEntry(long folderId, String title)
		throws PortalException {

		return getRepository().fetchFileEntry(folderId, title);
	}

	@Override
	public FileEntry fetchFileEntryByExternalReferenceCode(
		String externalReferenceCode) {

		return getRepository().fetchFileEntryByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public FileShortcut fetchFileShortcut(long fileShortcutId) {
		return getRepository().fetchFileShortcut(fileShortcutId);
	}

	@Override
	public FileShortcut fetchFileShortcutByExternalReferenceCode(
		String externalReferenceCode) {

		return getRepository().fetchFileShortcutByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public Folder fetchFolderByExternalReferenceCode(
		String externalReferenceCode) {

		return getRepository().fetchFolderByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getRepository().getFileEntries(
			folderId, status, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getRepository().getFileEntries(
			folderId, start, end, orderByComparator);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getRepository().getFileEntries(
			folderId, mimeTypes, status, start, end, orderByComparator);
	}

	@Override
	public List<RepositoryEntry> getFileEntriesAndFileShortcuts(
			long folderId, int status, int start, int end)
		throws PortalException {

		return getRepository().getFileEntriesAndFileShortcuts(
			folderId, status, start, end);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(long folderId, int status)
		throws PortalException {

		return getRepository().getFileEntriesAndFileShortcutsCount(
			folderId, status);
	}

	@Override
	public int getFileEntriesCount(long folderId) throws PortalException {
		return getRepository().getFileEntriesCount(folderId);
	}

	@Override
	public int getFileEntriesCount(long folderId, int status)
		throws PortalException {

		return getRepository().getFileEntriesCount(folderId, status);
	}

	@Override
	public int getFileEntriesCount(
			long folderId, String[] mimeTypes, int status)
		throws PortalException {

		return getRepository().getFileEntriesCount(folderId, mimeTypes, status);
	}

	@Override
	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		return getRepository().getFileEntry(fileEntryId);
	}

	@Override
	public FileEntry getFileEntry(long folderId, String title)
		throws PortalException {

		return getRepository().getFileEntry(folderId, title);
	}

	@Override
	public FileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		return getRepository().getFileEntryByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public FileEntry getFileEntryByFileName(long folderId, String fileName)
		throws PortalException {

		return getRepository().getFileEntryByFileName(folderId, fileName);
	}

	@Override
	public FileEntry getFileEntryByUuid(String uuid) throws PortalException {
		return getRepository().getFileEntryByUuid(uuid);
	}

	@Override
	public FileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		return getRepository().getFileShortcut(fileShortcutId);
	}

	@Override
	public FileShortcut getFileShortcutByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		return getRepository().getFileShortcutByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		return getRepository().getFileVersion(fileVersionId);
	}

	@Override
	public Folder getFolder(long folderId) throws PortalException {
		return getRepository().getFolder(folderId);
	}

	@Override
	public Folder getFolder(long parentFolderId, String name)
		throws PortalException {

		return getRepository().getFolder(parentFolderId, name);
	}

	@Override
	public Folder getFolderByExternalReferenceCode(String externalReferenceCode)
		throws PortalException {

		return getRepository().getFolderByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, boolean includeMountFolders, int start,
			int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return getRepository().getFolders(
			parentFolderId, includeMountFolders, start, end, orderByComparator);
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, int status, boolean includeMountFolders,
			int start, int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return getRepository().getFolders(
			parentFolderId, status, includeMountFolders, start, end,
			orderByComparator);
	}

	@Override
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, boolean includeMountFolders, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		return getRepository().getFoldersAndFileEntriesAndFileShortcuts(
			folderId, status, includeMountFolders, start, end,
			orderByComparator);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, boolean includeMountFolders)
		throws PortalException {

		return getRepository().getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, includeMountFolders);
	}

	@Override
	public int getFoldersCount(long parentFolderId, boolean includeMountfolders)
		throws PortalException {

		return getRepository().getFoldersCount(
			parentFolderId, includeMountfolders);
	}

	@Override
	public int getFoldersCount(
			long parentFolderId, int status, boolean includeMountfolders)
		throws PortalException {

		return getRepository().getFoldersCount(
			parentFolderId, status, includeMountfolders);
	}

	@Override
	public List<FileEntry> getRepositoryFileEntries(
			long userId, long rootFolderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getRepository().getRepositoryFileEntries(
			userId, rootFolderId, start, end, orderByComparator);
	}

	@Override
	public long getRepositoryId() {
		return getRepository().getRepositoryId();
	}

	@Override
	public FileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.moveFileEntry(
			userId, fileEntryId, newFolderId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Move.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public Folder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		Folder folder = localRepository.moveFolder(
			userId, folderId, parentFolderId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Move.class, Folder.class, folder);

		return folder;
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		localRepository.revertFileEntry(
			userId, fileEntryId, version, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class,
			getFileEntry(fileEntryId));
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			File file, Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, file, displayDate,
			expirationDate, reviewDate, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public FileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileShortcut fileShortcut = localRepository.updateFileShortcut(
			userId, fileShortcutId, folderId, toFileEntryId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileShortcut.class, fileShortcut);

		return fileShortcut;
	}

	@Override
	public void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		FileEntry fileEntry = localRepository.getFileEntry(oldToFileEntryId);

		fileEntry.getFileShortcuts();

		localRepository.updateFileShortcuts(oldToFileEntryId, newToFileEntryId);
	}

	@Override
	public Folder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		LocalRepository localRepository = getRepository();

		Folder folder = localRepository.updateFolder(
			folderId, parentFolderId, name, description, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, Folder.class, folder);

		return folder;
	}

	private final RepositoryEventTrigger _repositoryEventTrigger;

}
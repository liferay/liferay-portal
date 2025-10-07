/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.capabilities;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.capabilities.CapabilityProvider;
import com.liferay.portal.kernel.repository.event.RepositoryEventTrigger;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.InputStream;

import java.util.Date;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class CapabilityRepository
	extends BaseCapabilityRepository<Repository> implements Repository {

	public CapabilityRepository(
		Repository repository, CapabilityProvider capabilityProvider,
		RepositoryEventTrigger repositoryEventTrigger) {

		super(repository, capabilityProvider);

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

		Repository repository = getRepository();

		FileEntry fileEntry = repository.addFileEntry(
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

		Repository repository = getRepository();

		FileEntry fileEntry = repository.addFileEntry(
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

		Repository repository = getRepository();

		FileShortcut fileShortcut = repository.addFileShortcut(
			externalReferenceCode, userId, folderId, toFileEntryId,
			serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, FileShortcut.class, fileShortcut);

		return fileShortcut;
	}

	@Override
	public Folder addFolder(
			String externalReferenceCode, long userId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		Folder folder = repository.addFolder(
			externalReferenceCode, userId, parentFolderId, name, description,
			serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, Folder.class, folder);

		return folder;
	}

	@Override
	public FileVersion cancelCheckOut(long fileEntryId) throws PortalException {
		Repository repository = getRepository();

		FileVersion fileVersion = repository.cancelCheckOut(fileEntryId);

		if (fileVersion != null) {
			_repositoryEventTrigger.trigger(
				RepositoryEventType.Update.class, FileEntry.class,
				fileVersion.getFileEntry());
		}

		return fileVersion;
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		repository.checkInFileEntry(
			userId, fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class,
			repository.getFileEntry(fileEntryId));
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		repository.checkInFileEntry(
			userId, fileEntryId, lockUuid, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class,
			repository.getFileEntry(fileEntryId));
	}

	@Override
	public FileEntry checkOutFileEntry(
			long fileEntryId, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		FileEntry fileEntry = repository.checkOutFileEntry(
			fileEntryId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public FileEntry checkOutFileEntry(
			long fileEntryId, String owner, long expirationTime,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		FileEntry fileEntry = repository.checkOutFileEntry(
			fileEntryId, owner, expirationTime, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		FileEntry fileEntry = repository.copyFileEntry(
			userId, groupId, fileEntryId, destFolderId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Add.class, FileEntry.class, fileEntry);

		return fileEntry;
	}

	@Override
	public void deleteAll() throws PortalException {
		Repository repository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, Repository.class, repository);

		repository.deleteAll();
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		Repository repository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileEntry.class,
			repository.getFileEntry(fileEntryId));

		repository.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileEntry(long folderId, String title)
		throws PortalException {

		Repository repository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileEntry.class,
			repository.getFileEntry(folderId, title));

		repository.deleteFileEntry(folderId, title);
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		Repository repository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileShortcut.class,
			repository.getFileShortcut(fileShortcutId));

		repository.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId) throws PortalException {
		Repository repository = getRepository();

		FileEntry fileEntry = repository.getFileEntry(toFileEntryId);

		List<FileShortcut> fileShortcuts = fileEntry.getFileShortcuts();

		for (FileShortcut fileShortcut : fileShortcuts) {
			_repositoryEventTrigger.trigger(
				RepositoryEventType.Delete.class, FileShortcut.class,
				fileShortcut);
		}

		repository.deleteFileShortcuts(toFileEntryId);
	}

	@Override
	public void deleteFileVersion(long fileVersionId) throws PortalException {
		Repository repository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileVersion.class,
			repository.getFileVersion(fileVersionId));

		repository.deleteFileVersion(fileVersionId);
	}

	@Override
	public void deleteFileVersion(long fileEntryId, String version)
		throws PortalException {

		Repository repository = getRepository();

		FileEntry fileEntry = repository.getFileEntry(fileEntryId);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class, fileEntry);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, FileVersion.class,
			fileEntry.getFileVersion(version));

		repository.deleteFileVersion(fileEntryId, version);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		Repository repository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, Folder.class,
			repository.getFolder(folderId));

		repository.deleteFolder(folderId);
	}

	@Override
	public void deleteFolder(long parentFolderId, String name)
		throws PortalException {

		Repository repository = getRepository();

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Delete.class, Folder.class,
			repository.getFolder(parentFolderId, name));

		repository.deleteFolder(parentFolderId, name);
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
			long folderId, long fileEntryTypeId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getRepository().getFileEntries(
			folderId, fileEntryTypeId, start, end, orderByComparator);
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
	public List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getRepository().getFileEntries(
			folderId, mimeTypes, start, end, orderByComparator);
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
	public int getFileEntriesAndFileShortcutsCount(
			long folderId, int status, String[] mimeTypes)
		throws PortalException {

		return getRepository().getFileEntriesAndFileShortcutsCount(
			folderId, status, mimeTypes);
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
	public int getFileEntriesCount(long folderId, long fileEntryTypeId)
		throws PortalException {

		return getRepository().getFileEntriesCount(folderId, fileEntryTypeId);
	}

	@Override
	public int getFileEntriesCount(long folderId, String[] mimeTypes)
		throws PortalException {

		return getRepository().getFileEntriesCount(folderId, mimeTypes);
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
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, boolean includeOwner, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		return getRepository().getFoldersAndFileEntriesAndFileShortcuts(
			folderId, status, mimeTypes, includeMountFolders, includeOwner,
			start, end, orderByComparator);
	}

	@Override
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		return getRepository().getFoldersAndFileEntriesAndFileShortcuts(
			folderId, status, mimeTypes, includeMountFolders, start, end,
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
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders)
		throws PortalException {

		return getRepository().getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, mimeTypes, includeMountFolders);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, boolean includeOwner)
		throws PortalException {

		return getRepository().getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, mimeTypes, includeMountFolders, includeOwner);
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
	public int getFoldersFileEntriesCount(List<Long> folderIds, int status)
		throws PortalException {

		return getRepository().getFoldersFileEntriesCount(folderIds, status);
	}

	@Override
	public List<Folder> getMountFolders(
			long parentFolderId, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return getRepository().getMountFolders(
			parentFolderId, start, end, orderByComparator);
	}

	@Override
	public int getMountFoldersCount(long parentFolderId)
		throws PortalException {

		return getRepository().getMountFoldersCount(parentFolderId);
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
	public List<FileEntry> getRepositoryFileEntries(
			long userId, long rootFolderId, String[] mimeTypes, int status,
			int start, int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return getRepository().getRepositoryFileEntries(
			userId, rootFolderId, mimeTypes, status, start, end,
			orderByComparator);
	}

	@Override
	public int getRepositoryFileEntriesCount(long userId, long rootFolderId)
		throws PortalException {

		return getRepository().getRepositoryFileEntriesCount(
			userId, rootFolderId);
	}

	@Override
	public int getRepositoryFileEntriesCount(
			long userId, long rootFolderId, String[] mimeTypes, int status)
		throws PortalException {

		return getRepository().getRepositoryFileEntriesCount(
			userId, rootFolderId, mimeTypes, status);
	}

	@Override
	public List<FileShortcut> getRepositoryFileShortcuts(long groupId)
		throws PortalException {

		return getRepository().getRepositoryFileShortcuts(groupId);
	}

	@Override
	public long getRepositoryId() {
		return getRepository().getRepositoryId();
	}

	@Override
	public void getSubfolderIds(List<Long> folderIds, long folderId)
		throws PortalException {

		getRepository().getSubfolderIds(folderIds, folderId);
	}

	@Override
	public List<Long> getSubfolderIds(long folderId, boolean recurse)
		throws PortalException {

		return getRepository().getSubfolderIds(folderId, recurse);
	}

	@Override
	public Lock lockFolder(long folderId) throws PortalException {
		return getRepository().lockFolder(folderId);
	}

	@Override
	public Lock lockFolder(
			long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException {

		return getRepository().lockFolder(
			folderId, owner, inheritable, expirationTime);
	}

	@Override
	public FileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		FileEntry fileEntry = repository.moveFileEntry(
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

		Repository repository = getRepository();

		Folder folder = repository.moveFolder(
			userId, folderId, parentFolderId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Move.class, Folder.class, folder);

		return folder;
	}

	@Override
	public Lock refreshFileEntryLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		return getRepository().refreshFileEntryLock(
			lockUuid, companyId, expirationTime);
	}

	@Override
	public Lock refreshFolderLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		return getRepository().refreshFolderLock(
			lockUuid, companyId, expirationTime);
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		repository.revertFileEntry(
			userId, fileEntryId, version, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileEntry.class,
			getFileEntry(fileEntryId));
	}

	@Override
	public Hits search(long creatorUserId, int status, int start, int end)
		throws PortalException {

		return getRepository().search(creatorUserId, status, start, end);
	}

	@Override
	public Hits search(
			long creatorUserId, long folderId, String[] mimeTypes, int status,
			int start, int end)
		throws PortalException {

		return getRepository().search(
			creatorUserId, folderId, mimeTypes, status, start, end);
	}

	@Override
	public Hits search(SearchContext searchContext) throws SearchException {
		return getRepository().search(searchContext);
	}

	@Override
	public Hits search(SearchContext searchContext, Query query)
		throws SearchException {

		return getRepository().search(searchContext, query);
	}

	@Override
	public void unlockFolder(long folderId, String lockUuid)
		throws PortalException {

		getRepository().unlockFolder(folderId, lockUuid);
	}

	@Override
	public void unlockFolder(long parentFolderId, String name, String lockUuid)
		throws PortalException {

		getRepository().unlockFolder(parentFolderId, name, lockUuid);
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			File file, Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		FileEntry fileEntry = repository.updateFileEntry(
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

		Repository repository = getRepository();

		FileEntry fileEntry = repository.updateFileEntry(
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

		Repository repository = getRepository();

		FileShortcut fileShortcut = repository.updateFileShortcut(
			userId, fileShortcutId, folderId, toFileEntryId, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, FileShortcut.class, fileShortcut);

		return fileShortcut;
	}

	@Override
	public void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws PortalException {

		Repository repository = getRepository();

		FileEntry oldToFileEntry = repository.getFileEntry(oldToFileEntryId);

		List<FileShortcut> fileShortcuts = oldToFileEntry.getFileShortcuts();

		for (FileShortcut fileShortcut : fileShortcuts) {
			_repositoryEventTrigger.trigger(
				RepositoryEventType.Update.class, FileShortcut.class,
				fileShortcut);
		}

		repository.updateFileShortcuts(oldToFileEntryId, newToFileEntryId);
	}

	@Override
	public Folder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		Folder folder = repository.updateFolder(
			folderId, parentFolderId, name, description, serviceContext);

		_repositoryEventTrigger.trigger(
			RepositoryEventType.Update.class, Folder.class, folder);

		return folder;
	}

	@Override
	public Folder updateFolder(
			long folderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		Repository repository = getRepository();

		Folder folder = repository.updateFolder(
			folderId, name, description, serviceContext);

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_repositoryEventTrigger.trigger(
				RepositoryEventType.Update.class, Folder.class, folder);
		}

		return folder;
	}

	@Override
	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid)
		throws PortalException {

		return getRepository().verifyFileEntryCheckOut(fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws PortalException {

		return getRepository().verifyFileEntryLock(fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyInheritableLock(long folderId, String lockUuid)
		throws PortalException {

		return getRepository().verifyInheritableLock(folderId, lockUuid);
	}

	private final RepositoryEventTrigger _repositoryEventTrigger;

}
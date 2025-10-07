/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.cmis.internal;

import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.repository.proxy.RepositoryModelProxyBean;
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
public class RepositoryProxyBean
	extends RepositoryModelProxyBean implements Repository {

	public RepositoryProxyBean(Repository repository, ClassLoader classLoader) {
		super(classLoader);

		_repository = repository;
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _repository.addFileEntry(
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

		FileEntry fileEntry = _repository.addFileEntry(
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

		FileShortcut fileShortcut = _repository.addFileShortcut(
			externalReferenceCode, userId, folderId, toFileEntryId,
			serviceContext);

		return newFileShortcutProxyBean(fileShortcut);
	}

	@Override
	public Folder addFolder(
			String externalReferenceCode, long userId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		Folder folder = _repository.addFolder(
			externalReferenceCode, userId, parentFolderId, name, description,
			serviceContext);

		return newFolderProxyBean(folder);
	}

	@Override
	public FileVersion cancelCheckOut(long fileEntryId) throws PortalException {
		FileVersion fileVersion = _repository.cancelCheckOut(fileEntryId);

		return newFileVersionProxyBean(fileVersion);
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
	public FileEntry checkOutFileEntry(
			long fileEntryId, ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _repository.checkOutFileEntry(
			fileEntryId, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileEntry checkOutFileEntry(
			long fileEntryId, String owner, long expirationTime,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _repository.checkOutFileEntry(
			fileEntryId, owner, expirationTime, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _repository.copyFileEntry(
			userId, groupId, fileEntryId, destFolderId, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public void deleteAll() throws PortalException {
		_repository.deleteAll();
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		_repository.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileEntry(long folderId, String title)
		throws PortalException {

		_repository.deleteFileEntry(folderId, title);
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
	public void deleteFileVersion(long fileEntryId, String version)
		throws PortalException {

		_repository.deleteFileVersion(fileEntryId, version);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		_repository.deleteFolder(folderId);
	}

	@Override
	public void deleteFolder(long parentFolderId, String name)
		throws PortalException {

		_repository.deleteFolder(parentFolderId, name);
	}

	@Override
	public FileEntry fetchFileEntry(long fileEntryId) throws PortalException {
		FileEntry fileEntry = _repository.fetchFileEntry(fileEntryId);

		if (fileEntry == null) {
			return fileEntry;
		}

		return newFileEntryProxyBean(fileEntry);
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

		return toFileEntryProxyBeans(
			_repository.getFileEntries(
				folderId, status, start, end, orderByComparator));
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return toFileEntryProxyBeans(
			_repository.getFileEntries(
				folderId, start, end, orderByComparator));
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, long fileEntryTypeId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return toFileEntryProxyBeans(
			_repository.getFileEntries(
				folderId, fileEntryTypeId, start, end, orderByComparator));
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return toFileEntryProxyBeans(
			_repository.getFileEntries(
				folderId, mimeTypes, status, start, end, orderByComparator));
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return toFileEntryProxyBeans(
			_repository.getFileEntries(
				folderId, mimeTypes, start, end, orderByComparator));
	}

	@Override
	public List<RepositoryEntry> getFileEntriesAndFileShortcuts(
			long folderId, int status, int start, int end)
		throws PortalException {

		return toObjectProxyBeans(
			_repository.getFileEntriesAndFileShortcuts(
				folderId, status, start, end));
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(long folderId, int status)
		throws PortalException {

		return _repository.getFileEntriesAndFileShortcutsCount(
			folderId, status);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(
			long folderId, int status, String[] mimeTypes)
		throws PortalException {

		return _repository.getFileEntriesAndFileShortcutsCount(
			folderId, status, mimeTypes);
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
	public int getFileEntriesCount(long folderId, long fileEntryTypeId)
		throws PortalException {

		return _repository.getFileEntriesCount(folderId, fileEntryTypeId);
	}

	@Override
	public int getFileEntriesCount(long folderId, String[] mimeTypes)
		throws PortalException {

		return _repository.getFileEntriesCount(folderId, mimeTypes);
	}

	@Override
	public int getFileEntriesCount(
			long folderId, String[] mimeTypes, int status)
		throws PortalException {

		return _repository.getFileEntriesCount(folderId, mimeTypes, status);
	}

	@Override
	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		return newFileEntryProxyBean(_repository.getFileEntry(fileEntryId));
	}

	@Override
	public FileEntry getFileEntry(long folderId, String title)
		throws PortalException {

		return newFileEntryProxyBean(_repository.getFileEntry(folderId, title));
	}

	@Override
	public FileEntry getFileEntryByUuid(String uuid) throws PortalException {
		return newFileEntryProxyBean(_repository.getFileEntryByUuid(uuid));
	}

	@Override
	public FileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		return newFileShortcutProxyBean(
			_repository.getFileShortcut(fileShortcutId));
	}

	@Override
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		return newFileVersionProxyBean(
			_repository.getFileVersion(fileVersionId));
	}

	@Override
	public Folder getFolder(long folderId) throws PortalException {
		return newFolderProxyBean(_repository.getFolder(folderId));
	}

	@Override
	public Folder getFolder(long parentFolderId, String name)
		throws PortalException {

		return newFolderProxyBean(_repository.getFolder(parentFolderId, name));
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, boolean includeMountFolders, int start,
			int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return toFolderProxyBeans(
			_repository.getFolders(
				parentFolderId, includeMountFolders, start, end,
				orderByComparator));
	}

	@Override
	public List<Folder> getFolders(
			long parentFolderId, int status, boolean includeMountFolders,
			int start, int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return toFolderProxyBeans(
			_repository.getFolders(
				parentFolderId, status, includeMountFolders, start, end,
				orderByComparator));
	}

	@Override
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, boolean includeMountFolders, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		return toObjectProxyBeans(
			_repository.getFoldersAndFileEntriesAndFileShortcuts(
				folderId, status, includeMountFolders, start, end,
				orderByComparator));
	}

	@Override
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
			long folderId, int status, String[] mimetypes,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		return toObjectProxyBeans(
			_repository.getFoldersAndFileEntriesAndFileShortcuts(
				folderId, status, mimetypes, includeMountFolders, start, end,
				orderByComparator));
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, boolean includeMountFolders)
		throws PortalException {

		return _repository.getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, includeMountFolders);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long folderId, int status, String[] mimetypes,
			boolean includeMountFolders)
		throws PortalException {

		return _repository.getFoldersAndFileEntriesAndFileShortcutsCount(
			folderId, status, mimetypes, includeMountFolders);
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
	public int getFoldersFileEntriesCount(List<Long> folderIds, int status)
		throws PortalException {

		return _repository.getFoldersFileEntriesCount(folderIds, status);
	}

	@Override
	public List<Folder> getMountFolders(
			long parentFolderId, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException {

		return toFolderProxyBeans(
			_repository.getMountFolders(
				parentFolderId, start, end, orderByComparator));
	}

	@Override
	public int getMountFoldersCount(long parentFolderId)
		throws PortalException {

		return _repository.getMountFoldersCount(parentFolderId);
	}

	@Override
	public List<FileEntry> getRepositoryFileEntries(
			long userId, long rootFolderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return toFileEntryProxyBeans(
			_repository.getRepositoryFileEntries(
				userId, rootFolderId, start, end, orderByComparator));
	}

	@Override
	public List<FileEntry> getRepositoryFileEntries(
			long userId, long rootFolderId, String[] mimeTypes, int status,
			int start, int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		return toFileEntryProxyBeans(
			_repository.getRepositoryFileEntries(
				userId, rootFolderId, mimeTypes, status, start, end,
				orderByComparator));
	}

	@Override
	public int getRepositoryFileEntriesCount(long userId, long rootFolderId)
		throws PortalException {

		return _repository.getRepositoryFileEntriesCount(userId, rootFolderId);
	}

	@Override
	public int getRepositoryFileEntriesCount(
			long userId, long rootFolderId, String[] mimeTypes, int status)
		throws PortalException {

		return _repository.getRepositoryFileEntriesCount(
			userId, rootFolderId, mimeTypes, status);
	}

	@Override
	public List<FileShortcut> getRepositoryFileShortcuts(long groupId)
		throws PortalException {

		return _repository.getRepositoryFileShortcuts(groupId);
	}

	@Override
	public long getRepositoryId() {
		return _repository.getRepositoryId();
	}

	@Override
	public void getSubfolderIds(List<Long> folderIds, long folderId)
		throws PortalException {

		_repository.getSubfolderIds(folderIds, folderId);
	}

	@Override
	public List<Long> getSubfolderIds(long folderId, boolean recurse)
		throws PortalException {

		return _repository.getSubfolderIds(folderId, recurse);
	}

	@Override
	public <T extends Capability> boolean isCapabilityProvided(
		Class<T> capabilityClass) {

		return _repository.isCapabilityProvided(capabilityClass);
	}

	@Override
	public Lock lockFolder(long folderId) throws PortalException {
		return _repository.lockFolder(folderId);
	}

	@Override
	public Lock lockFolder(
			long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException {

		return _repository.lockFolder(
			folderId, owner, inheritable, expirationTime);
	}

	@Override
	public FileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _repository.moveFileEntry(
			userId, fileEntryId, newFolderId, serviceContext);

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public Folder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		Folder folder = _repository.moveFolder(
			userId, folderId, parentFolderId, serviceContext);

		return newFolderProxyBean(folder);
	}

	@Override
	public Lock refreshFileEntryLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		return _repository.refreshFileEntryLock(
			lockUuid, companyId, expirationTime);
	}

	@Override
	public Lock refreshFolderLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		return _repository.refreshFolderLock(
			lockUuid, companyId, expirationTime);
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
	public Hits search(long creatorUserId, int status, int start, int end)
		throws PortalException {

		return _repository.search(creatorUserId, status, start, end);
	}

	@Override
	public Hits search(
			long creatorUserId, long folderId, String[] mimeTypes, int status,
			int start, int end)
		throws PortalException {

		return _repository.search(
			creatorUserId, folderId, mimeTypes, status, start, end);
	}

	@Override
	public Hits search(SearchContext searchContext) throws SearchException {
		return _repository.search(searchContext);
	}

	@Override
	public Hits search(SearchContext searchContext, Query query)
		throws SearchException {

		return _repository.search(searchContext, query);
	}

	@Override
	public void unlockFolder(long folderId, String lockUuid)
		throws PortalException {

		_repository.unlockFolder(folderId, lockUuid);
	}

	@Override
	public void unlockFolder(long parentFolderId, String name, String lockUuid)
		throws PortalException {

		_repository.unlockFolder(parentFolderId, name, lockUuid);
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			File file, Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		FileEntry fileEntry = _repository.updateFileEntry(
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

		FileEntry fileEntry = _repository.updateFileEntry(
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

		FileShortcut fileShortcut = _repository.updateFileShortcut(
			userId, fileShortcutId, folderId, toFileEntryId, serviceContext);

		return newFileShortcutProxyBean(fileShortcut);
	}

	@Override
	public void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws PortalException {

		_repository.updateFileShortcuts(oldToFileEntryId, newToFileEntryId);
	}

	@Override
	public Folder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		Folder folder = _repository.updateFolder(
			folderId, parentFolderId, name, description, serviceContext);

		return newFolderProxyBean(folder);
	}

	@Override
	public Folder updateFolder(
			long folderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		Folder folder = _repository.updateFolder(
			folderId, name, description, serviceContext);

		return newFolderProxyBean(folder);
	}

	@Override
	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid)
		throws PortalException {

		return _repository.verifyFileEntryCheckOut(fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws PortalException {

		return _repository.verifyFileEntryLock(fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyInheritableLock(long folderId, String lockUuid)
		throws PortalException {

		return _repository.verifyInheritableLock(folderId, lockUuid);
	}

	private final Repository _repository;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.liferayrepository;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.RepositoryService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileShortcut;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portlet.documentlibrary.util.RepositoryModelUtil;
import com.liferay.portlet.documentlibrary.util.comparator.DLFileEntryOrderByComparator;
import com.liferay.portlet.documentlibrary.util.comparator.DLFolderOrderByComparator;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Chow
 */
public class LiferayLocalRepository
	extends LiferayRepositoryBase implements LocalRepository {

	public LiferayLocalRepository(
		RepositoryLocalService repositoryLocalService,
		RepositoryService repositoryService,
		DLAppHelperLocalService dlAppHelperLocalService,
		DLFileEntryLocalService dlFileEntryLocalService,
		DLFileEntryService dlFileEntryService,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
		DLFileShortcutLocalService dlFileShortcutLocalService,
		DLFileShortcutService dlFileShortcutService,
		DLFileVersionLocalService dlFileVersionLocalService,
		DLFileVersionService dlFileVersionService,
		DLFolderLocalService dlFolderLocalService,
		DLFolderService dlFolderService,
		ResourceLocalService resourceLocalService, long groupId,
		long repositoryId, long dlFolderId) {

		super(
			repositoryLocalService, repositoryService, dlAppHelperLocalService,
			dlFileEntryLocalService, dlFileEntryService,
			dlFileEntryTypeLocalService, dlFileShortcutLocalService,
			dlFileShortcutService, dlFileVersionLocalService,
			dlFileVersionService, dlFolderLocalService, dlFolderService,
			resourceLocalService, groupId, repositoryId, dlFolderId);
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException {

		long fileEntryTypeId = ParamUtil.getLong(
			serviceContext, "fileEntryTypeId",
			getDefaultFileEntryTypeId(serviceContext, folderId));

		Map<String, DDMFormValues> ddmFormValuesMap = getDDMFormValuesMap(
			serviceContext, fileEntryTypeId);

		long size = 0;

		if (file != null) {
			size = file.length();
		}

		DLFileEntry dlFileEntry = dlFileEntryLocalService.addFileEntry(
			externalReferenceCode, userId, getGroupId(), getRepositoryId(),
			toFolderId(folderId), sourceFileName, mimeType, title, urlTitle,
			description, changeLog, fileEntryTypeId, ddmFormValuesMap, file,
			null, size, null, null, null, serviceContext);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry addFileEntry(
			String externalReferenceCode, long userId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		long fileEntryTypeId = ParamUtil.getLong(
			serviceContext, "fileEntryTypeId",
			getDefaultFileEntryTypeId(serviceContext, folderId));

		Map<String, DDMFormValues> ddmFormValuesMap = getDDMFormValuesMap(
			serviceContext, fileEntryTypeId);

		DLFileEntry dlFileEntry = dlFileEntryLocalService.addFileEntry(
			externalReferenceCode, userId, getGroupId(), getRepositoryId(),
			toFolderId(folderId), sourceFileName, mimeType, title, urlTitle,
			description, changeLog, fileEntryTypeId, ddmFormValuesMap, null,
			inputStream, size, displayDate, expirationDate, reviewDate,
			serviceContext);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileShortcut addFileShortcut(
			String externalReferenceCode, long userId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException {

		DLFileShortcut dlFileShortcut =
			dlFileShortcutLocalService.addFileShortcut(
				externalReferenceCode, userId, getGroupId(), getRepositoryId(),
				toFolderId(folderId), toFileEntryId, serviceContext);

		return new LiferayFileShortcut(dlFileShortcut);
	}

	@Override
	public Folder addFolder(
			String externalReferenceCode, long userId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		boolean mountPoint = ParamUtil.getBoolean(serviceContext, "mountPoint");

		DLFolder dlFolder = dlFolderLocalService.addFolder(
			externalReferenceCode, userId, getGroupId(), getRepositoryId(),
			mountPoint, toFolderId(parentFolderId), name, description, false,
			serviceContext);

		return new LiferayFolder(dlFolder);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException {

		dlFileEntryLocalService.checkInFileEntry(
			userId, fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);
	}

	@Override
	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException {

		dlFileEntryLocalService.checkInFileEntry(
			userId, fileEntryId, lockUuid, serviceContext);
	}

	@Override
	public FileEntry copyFileEntry(
			long userId, long groupId, long fileEntryId, long destFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryLocalService.copyFileEntry(
			userId, groupId, getRepositoryId(), fileEntryId,
			toFolderId(destFolderId), null, serviceContext);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public void deleteAll() throws PortalException {
		dlFolderLocalService.deleteAllByRepository(getRepositoryId());
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		dlFileEntryLocalService.deleteFileEntry(fileEntryId);
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		dlFileShortcutLocalService.deleteFileShortcut(fileShortcutId);
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId) throws PortalException {
		dlFileShortcutLocalService.deleteFileShortcuts(toFileEntryId);
	}

	@Override
	public void deleteFileVersion(long fileVersionId) throws PortalException {
		dlFileVersionLocalService.deleteDLFileVersion(fileVersionId);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		DLFolder dlFolder = dlFolderLocalService.fetchFolder(folderId);

		if (dlFolder != null) {
			dlFolderLocalService.deleteFolder(folderId);
		}
	}

	@Override
	public FileEntry fetchFileEntry(long fileEntryId) throws PortalException {
		DLFileEntry dlFileEntry = dlFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry == null) {
			return null;
		}

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry fetchFileEntry(long folderId, String title) {
		DLFileEntry dlFileEntry = dlFileEntryLocalService.fetchFileEntry(
			getGroupId(), toFolderId(folderId), title);

		if (dlFileEntry == null) {
			return null;
		}

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry fetchFileEntryByExternalReferenceCode(
		String externalReferenceCode) {

		DLFileEntry dlFileEntry =
			dlFileEntryLocalService.fetchFileEntryByExternalReferenceCode(
				getGroupId(), externalReferenceCode);

		if (dlFileEntry != null) {
			return new LiferayFileEntry(dlFileEntry);
		}

		return null;
	}

	@Override
	public FileShortcut fetchFileShortcut(long fileShortcutId) {
		DLFileShortcut dlFileShortcut =
			dlFileShortcutLocalService.fetchDLFileShortcut(fileShortcutId);

		if (dlFileShortcut != null) {
			return new LiferayFileShortcut(dlFileShortcut);
		}

		return null;
	}

	@Override
	public FileShortcut fetchFileShortcutByExternalReferenceCode(
		String externalReferenceCode) {

		DLFileShortcut dlFileShortcut =
			dlFileShortcutLocalService.
				fetchDLFileShortcutByExternalReferenceCode(
					externalReferenceCode, getGroupId());

		if (dlFileShortcut != null) {
			return new LiferayFileShortcut(dlFileShortcut);
		}

		return null;
	}

	@Override
	public Folder fetchFolderByExternalReferenceCode(
		String externalReferenceCode) {

		DLFolder dlFolder =
			dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				externalReferenceCode, getGroupId());

		if (dlFolder != null) {
			return new LiferayFolder(dlFolder);
		}

		return null;
	}

	@Override
	public List<FileEntry> getFileEntries(
		long folderId, int status, int start, int end,
		OrderByComparator<FileEntry> orderByComparator) {

		List<DLFileEntry> dlFileEntries =
			dlFileEntryLocalService.getFileEntries(
				getGroupId(), toFolderId(folderId), status, start, end,
				DLFileEntryOrderByComparator.getOrderByComparator(
					orderByComparator));

		return RepositoryModelUtil.toFileEntries(dlFileEntries);
	}

	@Override
	public List<FileEntry> getFileEntries(
		long folderId, int start, int end,
		OrderByComparator<FileEntry> orderByComparator) {

		List<DLFileEntry> dlFileEntries =
			dlFileEntryLocalService.getFileEntries(
				getGroupId(), toFolderId(folderId), start, end,
				DLFileEntryOrderByComparator.getOrderByComparator(
					orderByComparator));

		return RepositoryModelUtil.toFileEntries(dlFileEntries);
	}

	@Override
	public List<FileEntry> getFileEntries(
			long folderId, String[] mimeTypes, int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		List<Long> folderIds = new ArrayList<>(1);

		folderIds.add(folderId);

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			status, start, end,
			DLFileEntryOrderByComparator.getOrderByComparator(
				orderByComparator));

		try {
			List<DLFileEntry> dlFileEntries =
				dlFileEntryLocalService.getFileEntries(
					getGroupId(), 0, new ArrayList<>(), toFolderIds(folderIds),
					mimeTypes, queryDefinition);

			return RepositoryModelUtil.toFileEntries(dlFileEntries);
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	@Override
	public List<RepositoryEntry> getFileEntriesAndFileShortcuts(
		long folderId, int status, int start, int end) {

		QueryDefinition<RepositoryEntry> queryDefinition =
			new QueryDefinition<>(status, start, end, null);

		List<Object> dlFileEntriesAndFileShortcuts =
			dlFolderLocalService.getFileEntriesAndFileShortcuts(
				getGroupId(), toFolderId(folderId), queryDefinition);

		return RepositoryModelUtil.toRepositoryEntries(
			dlFileEntriesAndFileShortcuts);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(long folderId, int status) {
		QueryDefinition<RepositoryEntry> queryDefinition =
			new QueryDefinition<>(status);

		return dlFolderLocalService.getFileEntriesAndFileShortcutsCount(
			getGroupId(), toFolderId(folderId), queryDefinition);
	}

	@Override
	public int getFileEntriesCount(long folderId) {
		return dlFileEntryLocalService.getFileEntriesCount(
			getGroupId(), toFolderId(folderId));
	}

	@Override
	public int getFileEntriesCount(long folderId, int status) {
		return dlFileEntryLocalService.getFileEntriesCount(
			getGroupId(), toFolderId(folderId), status);
	}

	@Override
	public int getFileEntriesCount(
			long folderId, String[] mimeTypes, int status)
		throws PortalException {

		List<Long> folderIds = new ArrayList<>(1);

		folderIds.add(folderId);

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			status);

		try {
			return dlFileEntryLocalService.getFileEntriesCount(
				getGroupId(), 0, new ArrayList<>(), toFolderIds(folderIds),
				mimeTypes, queryDefinition);
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	@Override
	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		DLFileEntry dlFileEntry = dlFileEntryLocalService.getFileEntry(
			fileEntryId);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry getFileEntry(long folderId, String title)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryLocalService.getFileEntry(
			getGroupId(), toFolderId(folderId), title);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		DLFileEntry dlFileEntry =
			dlFileEntryLocalService.getFileEntryByExternalReferenceCode(
				externalReferenceCode, getGroupId());

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry getFileEntryByFileName(long folderId, String fileName)
		throws PortalException {

		DLFileEntry dlFileEntry =
			dlFileEntryLocalService.getFileEntryByFileName(
				getGroupId(), toFolderId(folderId), fileName);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry getFileEntryByUuid(String uuid) throws PortalException {
		DLFileEntry dlFileEntry =
			dlFileEntryLocalService.getFileEntryByUuidAndGroupId(
				uuid, getGroupId());

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		return new LiferayFileShortcut(
			dlFileShortcutLocalService.getDLFileShortcut(fileShortcutId));
	}

	@Override
	public FileShortcut getFileShortcutByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		return new LiferayFileShortcut(
			dlFileShortcutLocalService.getDLFileShortcutByExternalReferenceCode(
				externalReferenceCode, getGroupId()));
	}

	@Override
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		DLFileVersion dlFileVersion = dlFileVersionLocalService.getFileVersion(
			fileVersionId);

		return new LiferayFileVersion(dlFileVersion);
	}

	@Override
	public Folder getFolder(long folderId) throws PortalException {
		DLFolder dlFolder = dlFolderLocalService.getFolder(
			toFolderId(folderId));

		return new LiferayFolder(dlFolder);
	}

	@Override
	public Folder getFolder(long parentFolderId, String name)
		throws PortalException {

		DLFolder dlFolder = dlFolderLocalService.getFolder(
			getGroupId(), toFolderId(parentFolderId), name);

		return new LiferayFolder(dlFolder);
	}

	@Override
	public Folder getFolderByExternalReferenceCode(String externalReferenceCode)
		throws PortalException {

		return new LiferayFolder(
			dlFolderLocalService.getDLFolderByExternalReferenceCode(
				externalReferenceCode, getGroupId()));
	}

	@Override
	public List<Folder> getFolders(
		long parentFolderId, boolean includeMountfolders, int start, int end,
		OrderByComparator<Folder> orderByComparator) {

		return getFolders(
			parentFolderId, WorkflowConstants.STATUS_APPROVED,
			includeMountfolders, start, end, orderByComparator);
	}

	@Override
	public List<Folder> getFolders(
		long parentFolderId, int status, boolean includeMountfolders, int start,
		int end, OrderByComparator<Folder> orderByComparator) {

		List<DLFolder> dlFolders = dlFolderLocalService.getFolders(
			getGroupId(), toFolderId(parentFolderId), includeMountfolders,
			status, start, end,
			DLFolderOrderByComparator.getOrderByComparator(orderByComparator));

		return RepositoryModelUtil.toFolders(dlFolders);
	}

	@Override
	public List<RepositoryEntry> getFoldersAndFileEntriesAndFileShortcuts(
		long folderId, int status, boolean includeMountFolders, int start,
		int end, OrderByComparator<?> orderByComparator) {

		QueryDefinition<Object> queryDefinition = new QueryDefinition<>(
			status, start, end, (OrderByComparator<Object>)orderByComparator);

		List<Object> dlFoldersAndDLFileEntriesAndDLFileShortcuts =
			dlFolderLocalService.getFoldersAndFileEntriesAndFileShortcuts(
				getGroupId(), toFolderId(folderId), null, includeMountFolders,
				queryDefinition);

		return RepositoryModelUtil.toRepositoryEntries(
			dlFoldersAndDLFileEntriesAndDLFileShortcuts);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
		long folderId, int status, boolean includeMountFolders) {

		QueryDefinition<Object> queryDefinition = new QueryDefinition<>(status);

		return dlFolderLocalService.
			getFoldersAndFileEntriesAndFileShortcutsCount(
				getGroupId(), toFolderId(folderId), null, includeMountFolders,
				queryDefinition);
	}

	@Override
	public int getFoldersCount(
		long parentFolderId, boolean includeMountfolders) {

		return getFoldersCount(
			parentFolderId, WorkflowConstants.STATUS_APPROVED,
			includeMountfolders);
	}

	@Override
	public int getFoldersCount(
		long parentFolderId, int status, boolean includeMountfolders) {

		return dlFolderLocalService.getFoldersCount(
			getGroupId(), toFolderId(parentFolderId), includeMountfolders,
			status);
	}

	@Override
	public List<FileEntry> getRepositoryFileEntries(
		long userId, long rootFolderId, int start, int end,
		OrderByComparator<FileEntry> orderByComparator) {

		List<DLFileEntry> dlFileEntries =
			dlFileEntryLocalService.getGroupFileEntries(
				getGroupId(), 0, getRepositoryId(), toFolderId(rootFolderId),
				start, end,
				DLFileEntryOrderByComparator.getOrderByComparator(
					orderByComparator));

		return RepositoryModelUtil.toFileEntries(dlFileEntries);
	}

	@Override
	public FileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryLocalService.moveFileEntry(
			userId, fileEntryId, toFolderId(newFolderId), serviceContext);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public Folder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		DLFolder dlFolder = dlFolderLocalService.moveFolder(
			userId, toFolderId(folderId), toFolderId(parentFolderId),
			serviceContext);

		return new LiferayFolder(dlFolder);
	}

	@Override
	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		dlFileEntryLocalService.revertFileEntry(
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

		long fileEntryTypeId = ParamUtil.getLong(
			serviceContext, "fileEntryTypeId", -1L);

		Map<String, DDMFormValues> ddmFormValuesMap = getDDMFormValuesMap(
			serviceContext, fileEntryTypeId);

		long size = 0;

		if (file != null) {
			size = file.length();
		}

		DLFileEntry dlFileEntry = dlFileEntryLocalService.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, fileEntryTypeId,
			ddmFormValuesMap, file, null, size, displayDate, expirationDate,
			reviewDate, serviceContext);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		long fileEntryTypeId = ParamUtil.getLong(
			serviceContext, "fileEntryTypeId", -1L);

		Map<String, DDMFormValues> ddmFormValuesMap = getDDMFormValuesMap(
			serviceContext, fileEntryTypeId);

		DLFileEntry dlFileEntry = dlFileEntryLocalService.updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, fileEntryTypeId,
			ddmFormValuesMap, null, inputStream, size, displayDate,
			expirationDate, reviewDate, serviceContext);

		return new LiferayFileEntry(dlFileEntry);
	}

	@Override
	public FileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileShortcut dlFileShortcut =
			dlFileShortcutLocalService.updateFileShortcut(
				userId, fileShortcutId, getRepositoryId(), toFolderId(folderId),
				toFileEntryId, serviceContext);

		return new LiferayFileShortcut(dlFileShortcut);
	}

	@Override
	public void updateFileShortcuts(
		long oldToFileEntryId, long newToFileEntryId) {

		dlFileShortcutLocalService.updateFileShortcuts(
			oldToFileEntryId, newToFileEntryId);
	}

	@Override
	public Folder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		long defaultFileEntryTypeId = ParamUtil.getLong(
			serviceContext, "defaultFileEntryTypeId");
		SortedArrayList<Long> fileEntryTypeIds = getLongList(
			serviceContext, "dlFileEntryTypesSearchContainerPrimaryKeys");
		int restrictionType = ParamUtil.getInteger(
			serviceContext, "restrictionType");

		DLFolder dlFolder = dlFolderLocalService.updateFolder(
			toFolderId(folderId), toFolderId(parentFolderId), name, description,
			defaultFileEntryTypeId, fileEntryTypeIds, restrictionType,
			serviceContext);

		return new LiferayFolder(dlFolder);
	}

	public UnicodeProperties updateRepository(
		UnicodeProperties typeSettingsUnicodeProperties) {

		return typeSettingsUnicodeProperties;
	}

}
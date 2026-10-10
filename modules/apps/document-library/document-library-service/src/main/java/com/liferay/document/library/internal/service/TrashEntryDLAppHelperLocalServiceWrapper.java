/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.service;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceWrapper;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.comparator.DLFileVersionVersionComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.RepositoryEventTriggerCapability;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.repository.event.TrashRepositoryEventType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryModel;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileShortcut;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portlet.documentlibrary.util.DLAppUtil;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ServiceWrapper.class)
public class TrashEntryDLAppHelperLocalServiceWrapper
	extends DLAppHelperLocalServiceWrapper {

	@Override
	public void moveDependentsToTrash(DLFolder dlFolder)
		throws PortalException {

		trashOrRestoreFolder(dlFolder, true);
	}

	@Override
	public FileEntry moveFileEntryFromTrash(
			long userId, FileEntry fileEntry, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		boolean hasLock = _dlFileEntryLocalService.hasFileEntryLock(
			userId, fileEntry.getFileEntryId(), fileEntry.getFolderId());

		if (!hasLock) {
			_dlFileEntryLocalService.lockFileEntry(
				userId, fileEntry.getFileEntryId());
		}

		try {
			return doMoveFileEntryFromTrash(
				userId, fileEntry, newFolderId, serviceContext);
		}
		finally {
			if (!hasLock) {
				_dlFileEntryLocalService.unlockFileEntry(
					fileEntry.getFileEntryId());
			}
		}
	}

	/**
	 * Moves the file entry to the recycle bin.
	 *
	 * @param  userId the primary key of the user moving the file entry
	 * @param  fileEntry the file entry to be moved
	 * @return the moved file entry
	 */
	@Override
	public FileEntry moveFileEntryToTrash(long userId, FileEntry fileEntry)
		throws PortalException {

		boolean hasLock = _dlFileEntryLocalService.hasFileEntryLock(
			userId, fileEntry.getFileEntryId(), fileEntry.getFolderId());

		if (!hasLock) {
			_dlFileEntryLocalService.lockFileEntry(
				userId, fileEntry.getFileEntryId());
		}

		try {
			if (fileEntry.isCheckedOut()) {
				_dlFileEntryLocalService.cancelCheckOut(
					userId, fileEntry.getFileEntryId());
			}

			return doMoveFileEntryToTrash(userId, fileEntry);
		}
		finally {
			if (!hasLock) {
				_dlFileEntryLocalService.unlockFileEntry(
					fileEntry.getFileEntryId());
			}
		}
	}

	@Override
	public FileShortcut moveFileShortcutFromTrash(
			long userId, FileShortcut fileShortcut, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		DLFileShortcut dlFileShortcut = (DLFileShortcut)fileShortcut.getModel();

		if (!dlFileShortcut.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(dlFileShortcut)) {
			restoreFileShortcutFromTrash(userId, fileShortcut);
		}
		else {

			// File shortcut

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				DLFileShortcutConstants.getClassName(),
				fileShortcut.getFileShortcutId());

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			_dlFileShortcutLocalService.updateStatus(
				userId, fileShortcut.getFileShortcutId(), status,
				new ServiceContext());

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Social

			JSONObject extraDataJSONObject = JSONUtil.put(
				"title", fileShortcut.getToTitle());

			SocialActivityManagerUtil.addActivity(
				userId, fileShortcut,
				SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
				extraDataJSONObject.toString(), 0);
		}

		return _dlAppLocalService.updateFileShortcut(
			userId, fileShortcut.getFileShortcutId(), newFolderId,
			fileShortcut.getToFileEntryId(), serviceContext);
	}

	/**
	 * Moves the file shortcut to the recycle bin.
	 *
	 * @param  userId the primary key of the user moving the file shortcut
	 * @param  fileShortcut the file shortcut to be moved
	 * @return the moved file shortcut
	 */
	@Override
	public FileShortcut moveFileShortcutToTrash(
			long userId, FileShortcut fileShortcut)
		throws PortalException {

		// File shortcut

		DLFileShortcut dlFileShortcut = (DLFileShortcut)fileShortcut.getModel();

		if (dlFileShortcut.isInTrash()) {
			throw new TrashEntryException();
		}

		int oldStatus = dlFileShortcut.getStatus();

		dlFileShortcut = _dlFileShortcutLocalService.updateStatus(
			userId, fileShortcut.getFileShortcutId(),
			WorkflowConstants.STATUS_IN_TRASH, new ServiceContext());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", _trashHelper.getOriginalTitle(fileShortcut.getToTitle()));

		SocialActivityManagerUtil.addActivity(
			userId, fileShortcut, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		// Trash

		_trashEntryLocalService.addTrashEntry(
			userId, fileShortcut.getGroupId(),
			DLFileShortcutConstants.getClassName(),
			fileShortcut.getFileShortcutId(), fileShortcut.getUuid(), null,
			oldStatus, null, null);

		return new LiferayFileShortcut(dlFileShortcut);
	}

	@Override
	public Folder moveFolderFromTrash(
			long userId, Folder folder, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		boolean hasLock = _dlFolderLocalService.hasFolderLock(
			userId, folder.getFolderId());

		Lock lock = null;

		if (!hasLock) {
			lock = _dlFolderLocalService.lockFolder(
				userId, folder.getFolderId());
		}

		try {
			return doMoveFolderFromTrash(
				userId, folder, parentFolderId, serviceContext);
		}
		finally {
			if (!hasLock) {
				_dlFolderLocalService.unlockFolder(
					folder.getFolderId(), lock.getUuid());
			}
		}
	}

	/**
	 * Moves the folder to the recycle bin.
	 *
	 * @param  userId the primary key of the user moving the folder
	 * @param  folder the folder to be moved
	 * @return the moved folder
	 */
	@Override
	public Folder moveFolderToTrash(long userId, Folder folder)
		throws PortalException {

		boolean hasLock = _dlFolderLocalService.hasFolderLock(
			userId, folder.getFolderId());

		Lock lock = null;

		if (!hasLock) {
			lock = _dlFolderLocalService.lockFolder(
				userId, folder.getFolderId());
		}

		try {
			return doMoveFolderToTrash(userId, folder);
		}
		finally {
			if (!hasLock) {
				_dlFolderLocalService.unlockFolder(
					folder.getFolderId(), lock.getUuid());
			}
		}
	}

	@Override
	public void restoreDependentsFromTrash(DLFolder dlFolder)
		throws PortalException {

		trashOrRestoreFolder(dlFolder, false);
	}

	@Override
	public void restoreFileEntryFromTrash(long userId, FileEntry fileEntry)
		throws PortalException {

		restoreFileEntryFromTrash(userId, fileEntry.getFolderId(), fileEntry);
	}

	@Override
	public void restoreFileEntryFromTrash(
			long userId, long newFolderId, FileEntry fileEntry)
		throws PortalException {

		// File entry

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		if (!dlFileEntry.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		FileVersion fileVersion = fileEntry.getFileVersion();

		if (!DLAppHelperThreadLocal.isEnabled()) {
			_dlFileEntryLocalService.updateStatus(
				userId, fileVersion.getFileVersionId(),
				WorkflowConstants.STATUS_APPROVED, new ServiceContext(),
				new HashMap<String, Serializable>());

			return;
		}

		String originalTitle = _trashHelper.getOriginalTitle(
			dlFileEntry.getTitle());

		String title = _dlFileEntryLocalService.getUniqueTitle(
			dlFileEntry.getGroupId(), newFolderId, dlFileEntry.getFileEntryId(),
			originalTitle, dlFileEntry.getExtension());

		String originalFileName = _trashHelper.getOriginalTitle(
			dlFileEntry.getTitle(), "fileName");

		String fileName = originalFileName;

		if (!StringUtil.equals(title, originalTitle)) {
			fileName = DLUtil.getSanitizedFileName(
				title, DLAppUtil.getExtension(title, originalFileName));
		}

		dlFileEntry.setFileName(fileName);
		dlFileEntry.setTitle(title);

		dlFileEntry = _dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

		DLFileVersion dlFileVersion = (DLFileVersion)fileVersion.getModel();

		dlFileVersion.setFileName(fileName);
		dlFileVersion.setTitle(title);

		dlFileVersion = _dlFileVersionLocalService.updateDLFileVersion(
			dlFileVersion);

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		int status = trashEntry.getStatus();

		List<TrashVersion> trashVersions =
			_trashVersionLocalService.getVersions(trashEntry.getEntryId());

		for (TrashVersion trashVersion : trashVersions) {
			if (trashVersion.getClassPK() == dlFileVersion.getFileVersionId()) {
				status = trashVersion.getStatus();

				break;
			}
		}

		_dlFileEntryLocalService.updateStatus(
			userId, dlFileEntry, dlFileVersion, status, new ServiceContext(),
			new HashMap<>());

		// File shortcut

		_dlFileShortcutLocalService.enableFileShortcuts(
			fileEntry.getFileEntryId());

		// Sync

		triggerRepositoryEvent(
			fileEntry.getRepositoryId(),
			TrashRepositoryEventType.EntryRestored.class, FileEntry.class,
			fileEntry);

		// Trash

		for (TrashVersion trashVersion : trashVersions) {
			DLFileVersion trashDLFileVersion =
				_dlFileVersionLocalService.getDLFileVersion(
					trashVersion.getClassPK());

			trashDLFileVersion.setStatus(trashVersion.getStatus());

			_dlFileVersionLocalService.updateDLFileVersion(trashDLFileVersion);
		}

		_trashEntryLocalService.deleteEntry(trashEntry.getEntryId());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", fileEntry.getTitle());

		SocialActivityManagerUtil.addActivity(
			userId, fileEntry, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);
	}

	@Override
	public void restoreFileShortcutFromTrash(
			long userId, FileShortcut fileShortcut)
		throws PortalException {

		DLFileShortcut dlFileShortcut = (DLFileShortcut)fileShortcut.getModel();

		if (!dlFileShortcut.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		// File shortcut

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			DLFileShortcutConstants.getClassName(),
			fileShortcut.getFileShortcutId());

		_dlFileShortcutLocalService.updateStatus(
			userId, fileShortcut.getFileShortcutId(), trashEntry.getStatus(),
			new ServiceContext());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", fileShortcut.getToTitle());

		SocialActivityManagerUtil.addActivity(
			userId, fileShortcut,
			SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);

		// Trash

		_trashEntryLocalService.deleteEntry(trashEntry.getEntryId());
	}

	@Override
	public void restoreFolderFromTrash(long userId, Folder folder)
		throws PortalException {

		// Folder

		DLFolder dlFolder = (DLFolder)folder.getModel();

		if (!dlFolder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		String originalName = _trashHelper.getOriginalTitle(dlFolder.getName());

		dlFolder.setName(
			_dlFolderLocalService.getUniqueFolderName(
				folder.getUuid(), folder.getGroupId(),
				folder.getParentFolderId(), originalName, 2));

		dlFolder = _dlFolderLocalService.updateDLFolder(dlFolder);

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			DLFolder.class.getName(), dlFolder.getFolderId());

		_dlFolderLocalService.updateStatus(
			userId, folder.getFolderId(), trashEntry.getStatus(),
			new HashMap<String, Serializable>(), new ServiceContext());

		// Folders, file entries, and file shortcuts

		restoreDependentsFromTrash(dlFolder);

		// Sync

		triggerRepositoryEvent(
			folder.getRepositoryId(),
			TrashRepositoryEventType.EntryRestored.class, Folder.class, folder);

		// Trash

		_trashEntryLocalService.deleteEntry(trashEntry.getEntryId());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", folder.getName());

		SocialActivityManagerUtil.addActivity(
			userId, folder, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);
	}

	protected FileEntry doMoveFileEntryFromTrash(
			long userId, FileEntry fileEntry, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		// File entry

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		if (!dlFileEntry.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(dlFileEntry)) {
			restoreFileEntryFromTrash(userId, newFolderId, fileEntry);

			if (fileEntry.getFolderId() != newFolderId) {
				fileEntry = _dlAppLocalService.moveFileEntry(
					userId, fileEntry.getFileEntryId(), newFolderId,
					serviceContext);
			}

			// Indexer

			Indexer<DLFileEntry> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(DLFileEntry.class);

			indexer.reindex((DLFileEntry)fileEntry.getModel());

			return fileEntry;
		}

		List<DLFileVersion> dlFileVersions =
			_dlFileVersionLocalService.getFileVersions(
				fileEntry.getFileEntryId(), WorkflowConstants.STATUS_IN_TRASH);

		dlFileVersions = ListUtil.sort(
			dlFileVersions, DLFileVersionVersionComparator.getInstance(false));

		FileVersion fileVersion = new LiferayFileVersion(dlFileVersions.get(0));

		TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
			DLFileVersion.class.getName(), fileVersion.getFileVersionId());

		int oldStatus = WorkflowConstants.STATUS_APPROVED;

		if (trashVersion != null) {
			oldStatus = trashVersion.getStatus();
		}

		_dlFileEntryLocalService.updateStatus(
			userId, dlFileEntry, dlFileVersions.get(0), oldStatus,
			serviceContext, new HashMap<>());

		// File versions

		for (DLFileVersion dlFileVersion : dlFileVersions) {

			// File version

			trashVersion = _trashVersionLocalService.fetchVersion(
				DLFileVersion.class.getName(),
				dlFileVersion.getFileVersionId());

			oldStatus = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				oldStatus = trashVersion.getStatus();
			}

			dlFileVersion.setStatus(oldStatus);

			_dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}
		}

		if (DLAppHelperThreadLocal.isEnabled()) {

			// File shortcut

			_dlFileShortcutLocalService.enableFileShortcuts(
				fileEntry.getFileEntryId());
		}

		// App helper

		fileEntry = _dlAppLocalService.moveFileEntry(
			userId, fileEntry.getFileEntryId(), newFolderId, serviceContext);

		// Sync

		triggerRepositoryEvent(
			fileEntry.getRepositoryId(),
			TrashRepositoryEventType.EntryRestored.class, FileEntry.class,
			fileEntry);

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", fileEntry.getTitle());

		SocialActivityManagerUtil.addActivity(
			userId, fileEntry, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);

		// Indexer

		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			DLFileEntry.class);

		indexer.reindex((DLFileEntry)fileEntry.getModel());

		return fileEntry;
	}

	protected FileEntry doMoveFileEntryToTrash(long userId, FileEntry fileEntry)
		throws PortalException {

		if (fileEntry.isInTrash()) {
			throw new TrashEntryException();
		}

		List<DLFileVersion> dlFileVersions =
			_dlFileVersionLocalService.getFileVersions(
				fileEntry.getFileEntryId(), WorkflowConstants.STATUS_ANY);

		dlFileVersions = ListUtil.sort(
			dlFileVersions, DLFileVersionVersionComparator.getInstance(false));

		List<ObjectValuePair<Long, Integer>>
			dlFileVersionStatusObjectValuePairs = new ArrayList<>();

		if ((dlFileVersions != null) && !dlFileVersions.isEmpty()) {
			dlFileVersionStatusObjectValuePairs = getDLFileVersionStatuses(
				dlFileVersions);
		}

		FileVersion fileVersion = fileEntry.getLatestFileVersion(true);

		_dlFileEntryLocalService.updateStatus(
			userId, fileVersion.getFileVersionId(),
			WorkflowConstants.STATUS_IN_TRASH, new ServiceContext(),
			new HashMap<String, Serializable>());

		if (DLAppHelperThreadLocal.isEnabled()) {

			// File shortcut

			_dlFileShortcutLocalService.disableFileShortcuts(
				fileEntry.getFileEntryId());

			// Sync

			triggerRepositoryEvent(
				fileEntry.getRepositoryId(),
				TrashRepositoryEventType.EntryTrashed.class, FileEntry.class,
				fileEntry);
		}

		// Trash

		dlFileVersions = _dlFileVersionLocalService.getFileVersions(
			fileEntry.getFileEntryId(), WorkflowConstants.STATUS_ANY);

		for (DLFileVersion curDLFileVersion : dlFileVersions) {
			curDLFileVersion.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			_dlFileVersionLocalService.updateDLFileVersion(curDLFileVersion);
		}

		if (!DLAppHelperThreadLocal.isEnabled()) {
			return fileEntry;
		}

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileVersion oldDLFileVersion = (DLFileVersion)fileVersion.getModel();

		int oldDLFileVersionStatus = oldDLFileVersion.getStatus();

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, dlFileEntry.getGroupId(),
			DLFileEntryConstants.getClassName(), dlFileEntry.getFileEntryId(),
			dlFileEntry.getUuid(), dlFileEntry.getClassName(),
			oldDLFileVersionStatus, dlFileVersionStatusObjectValuePairs,
			UnicodePropertiesBuilder.put(
				"fileName", dlFileEntry.getFileName()
			).put(
				"title", dlFileEntry.getTitle()
			).build());

		String trashTitle = _trashHelper.getTrashTitle(trashEntry.getEntryId());

		dlFileEntry.setFileName(trashTitle);
		dlFileEntry.setTitle(trashTitle);

		dlFileEntry = _dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

		// Indexer

		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			DLFileEntry.class);

		indexer.reindex(dlFileEntry);

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", _trashHelper.getOriginalTitle(fileEntry.getTitle()));

		SocialActivityManagerUtil.addActivity(
			userId, fileEntry, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		// Workflow

		int oldStatus = fileVersion.getStatus();

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				fileVersion.getCompanyId(), fileVersion.getGroupId(),
				DLFileEntryConstants.getClassName(),
				fileVersion.getFileVersionId());
		}

		return fileEntry;
	}

	protected Folder doMoveFolderFromTrash(
			long userId, Folder folder, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		DLFolder dlFolder = (DLFolder)folder.getModel();

		if (!dlFolder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(dlFolder)) {
			restoreFolderFromTrash(userId, folder);
		}
		else {

			// Folder

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				DLFolder.class.getName(), dlFolder.getFolderId());

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			_dlFolderLocalService.updateStatus(
				userId, folder.getFolderId(), status,
				new HashMap<String, Serializable>(), new ServiceContext());

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Folders, file entries, and file shortcuts

			restoreDependentsFromTrash(dlFolder);

			// Sync

			triggerRepositoryEvent(
				folder.getRepositoryId(),
				TrashRepositoryEventType.EntryRestored.class, Folder.class,
				folder);

			// Social

			JSONObject extraDataJSONObject = JSONUtil.put(
				"title", folder.getName());

			SocialActivityManagerUtil.addActivity(
				userId, folder, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
				extraDataJSONObject.toString(), 0);
		}

		return _dlAppLocalService.moveFolder(
			userId, folder.getFolderId(), parentFolderId, serviceContext);
	}

	protected Folder doMoveFolderToTrash(long userId, Folder folder)
		throws PortalException {

		// Folder

		DLFolder dlFolder = (DLFolder)folder.getModel();

		if (dlFolder.isInTrash()) {
			throw new TrashEntryException();
		}

		dlFolder = _dlFolderLocalService.updateStatus(
			userId, folder.getFolderId(), WorkflowConstants.STATUS_IN_TRASH,
			new HashMap<String, Serializable>(), new ServiceContext());

		// Trash

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, dlFolder.getGroupId(), DLFolderConstants.getClassName(),
			dlFolder.getFolderId(), dlFolder.getUuid(), null,
			WorkflowConstants.STATUS_APPROVED, null,
			UnicodePropertiesBuilder.put(
				"title", dlFolder.getName()
			).build());

		dlFolder.setName(_trashHelper.getTrashTitle(trashEntry.getEntryId()));

		dlFolder = _dlFolderLocalService.updateDLFolder(dlFolder);

		// Folders, file entries, and file shortcuts

		moveDependentsToTrash(dlFolder);

		// Sync

		triggerRepositoryEvent(
			folder.getRepositoryId(),
			TrashRepositoryEventType.EntryTrashed.class, Folder.class, folder);

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", folder.getName());

		SocialActivityManagerUtil.addActivity(
			userId, folder, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		// Indexer

		Indexer<DLFolder> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			DLFolder.class);

		indexer.reindex(dlFolder);

		return new LiferayFolder(dlFolder);
	}

	protected List<ObjectValuePair<Long, Integer>> getDLFileVersionStatuses(
		List<DLFileVersion> dlFileVersions) {

		return TransformUtil.transform(
			dlFileVersions,
			dlFileVersion -> {
				int status = dlFileVersion.getStatus();

				if (status == WorkflowConstants.STATUS_PENDING) {
					status = WorkflowConstants.STATUS_DRAFT;
				}

				return new ObjectValuePair<>(
					dlFileVersion.getFileVersionId(), status);
			});
	}

	protected void trashOrRestoreFolder(DLFolder dlFolder, boolean moveToTrash)
		throws PortalException {

		TrashEntry trashEntry = null;

		if (moveToTrash) {
			trashEntry = _trashEntryLocalService.getEntry(
				DLFolderConstants.getClassName(), dlFolder.getFolderId());
		}

		long dlFileEntryClassNameId = _classNameLocalService.getClassNameId(
			DLFileEntry.class);

		List<DLFileEntry> dlFileEntries =
			_dlFileEntryLocalService.getFileEntriesByClassNameIdAndTreePath(
				dlFileEntryClassNameId, dlFolder.getTreePath());

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			_assetEntryLocalService.updateVisible(
				DLFileEntry.class.getName(), dlFileEntry.getFileEntryId(),
				!moveToTrash);
		}

		long dlFolderClassNameId = _classNameLocalService.getClassNameId(
			DLFolder.class);

		List<DLFolder> dlFolders = _dlFolderLocalService.getFolders(
			dlFolderClassNameId, dlFolder.getTreePath());

		for (DLFolder curDLFolder : dlFolders) {
			_assetEntryLocalService.updateVisible(
				DLFolder.class.getName(), curDLFolder.getFolderId(),
				!moveToTrash);
		}

		if (moveToTrash) {
			dlFolders = _dlFolderLocalService.getNotInTrashFolders(
				dlFolder.getGroupId(), false,
				_customSQL.keywords(
					dlFolder.getTreePath(), WildcardMode.TRAILING)[0],
				false);
		}
		else {
			dlFolders = _dlFolderLocalService.getFolders(
				dlFolder.getGroupId(), false,
				_customSQL.keywords(
					dlFolder.getTreePath(), WildcardMode.TRAILING)[0],
				false);
		}

		if (!dlFolders.contains(dlFolder)) {
			dlFolders = new ArrayList<>(dlFolders);

			dlFolders.add(dlFolder);
		}

		for (DLFolder childDLFolder : dlFolders) {
			trashOrRestoreFolder(
				dlFolder, childDLFolder, moveToTrash, trashEntry);
		}
	}

	protected void trashOrRestoreFolder(
			DLFolder dlFolder, DLFolder childDLFolder, boolean moveToTrash,
			TrashEntry trashEntry)
		throws PortalException {

		List<Long> dlFileEntryIds = new ArrayList<>();

		List<DLFileEntry> dlFileEntries =
			_dlFileEntryLocalService.getFileEntries(
				childDLFolder.getGroupId(), childDLFolder.getFolderId());

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			if (moveToTrash) {
				if (_trashHelper.isInTrashExplicitly(dlFileEntry)) {
					continue;
				}
			}
			else if (!_trashHelper.isInTrashImplicitly(dlFileEntry)) {
				continue;
			}

			// File shortcut

			_dlFileShortcutLocalService.updateFileShortcutsActive(
				dlFileEntry.getFileEntryId(), !moveToTrash);

			// File versions

			List<DLFileVersion> dlFileVersions = null;

			if (moveToTrash) {
				dlFileVersions = _dlFileVersionLocalService.getFileVersions(
					dlFileEntry.getFileEntryId(), WorkflowConstants.STATUS_ANY);
			}
			else {
				dlFileVersions = _dlFileVersionLocalService.getFileVersions(
					dlFileEntry.getFileEntryId(),
					WorkflowConstants.STATUS_IN_TRASH);
			}

			for (DLFileVersion dlFileVersion : dlFileVersions) {

				// File version

				if (moveToTrash) {
					int oldStatus = dlFileVersion.getStatus();

					dlFileVersion.setStatus(WorkflowConstants.STATUS_IN_TRASH);

					dlFileVersion =
						_dlFileVersionLocalService.updateDLFileVersion(
							dlFileVersion);

					// Trash

					if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
						int newStatus = oldStatus;

						if (oldStatus == WorkflowConstants.STATUS_PENDING) {
							newStatus = WorkflowConstants.STATUS_DRAFT;
						}

						_trashVersionLocalService.addTrashVersion(
							trashEntry.getEntryId(),
							DLFileVersion.class.getName(),
							dlFileVersion.getFileVersionId(), newStatus, null);
					}

					// Workflow

					if (oldStatus == WorkflowConstants.STATUS_PENDING) {
						_workflowInstanceLinkLocalService.
							deleteWorkflowInstanceLink(
								dlFileVersion.getCompanyId(),
								dlFileVersion.getGroupId(),
								DLFileEntryConstants.getClassName(),
								dlFileVersion.getFileVersionId());
					}
				}
				else {
					TrashVersion trashVersion =
						_trashVersionLocalService.fetchVersion(
							DLFileVersion.class.getName(),
							dlFileVersion.getFileVersionId());

					int oldStatus = WorkflowConstants.STATUS_APPROVED;

					if (trashVersion != null) {
						oldStatus = trashVersion.getStatus();
					}

					dlFileVersion.setStatus(oldStatus);

					_dlFileVersionLocalService.updateDLFileVersion(
						dlFileVersion);

					// Trash

					if (trashVersion != null) {
						_trashVersionLocalService.deleteTrashVersion(
							trashVersion);
					}
				}
			}

			dlFileEntryIds.add(dlFileEntry.getFileEntryId());
		}

		if (!dlFileEntryIds.isEmpty()) {
			_dlAppHelperLocalService.reindex(
				dlFolder.getCompanyId(), dlFileEntryIds);
		}

		List<DLFileShortcut> dlFileShortcuts =
			_dlFileShortcutLocalService.getFileShortcuts(
				childDLFolder.getGroupId(), childDLFolder.getFolderId());

		for (DLFileShortcut dlFileShortcut : dlFileShortcuts) {
			if (moveToTrash) {
				if (_trashHelper.isInTrashExplicitly(dlFileShortcut)) {
					continue;
				}

				int oldStatus = dlFileShortcut.getStatus();

				dlFileShortcut.setStatus(WorkflowConstants.STATUS_IN_TRASH);

				dlFileShortcut =
					_dlFileShortcutLocalService.updateDLFileShortcut(
						dlFileShortcut);

				// Trash

				if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
					_trashVersionLocalService.addTrashVersion(
						trashEntry.getEntryId(),
						DLFileShortcutConstants.getClassName(),
						dlFileShortcut.getFileShortcutId(), oldStatus, null);
				}
			}
			else {
				if (!_trashHelper.isInTrashImplicitly(dlFileShortcut)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						DLFileShortcutConstants.getClassName(),
						dlFileShortcut.getFileShortcutId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				dlFileShortcut.setStatus(oldStatus);

				_dlFileShortcutLocalService.updateDLFileShortcut(
					dlFileShortcut);

				if (trashVersion != null) {
					_trashVersionLocalService.deleteTrashVersion(trashVersion);
				}
			}
		}

		if (childDLFolder.equals(dlFolder)) {
			return;
		}

		if (moveToTrash) {
			if (_trashHelper.isInTrashExplicitly(childDLFolder)) {
				return;
			}

			int oldStatus = childDLFolder.getStatus();

			childDLFolder.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			childDLFolder = _dlFolderLocalService.updateDLFolder(childDLFolder);

			// Trash

			if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
				_trashVersionLocalService.addTrashVersion(
					trashEntry.getEntryId(), DLFolder.class.getName(),
					childDLFolder.getFolderId(), oldStatus, null);
			}
		}
		else {
			if (!_trashHelper.isInTrashImplicitly(childDLFolder)) {
				return;
			}

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				DLFolder.class.getName(), childDLFolder.getFolderId());

			int oldStatus = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				oldStatus = trashVersion.getStatus();
			}

			childDLFolder.setStatus(oldStatus);

			childDLFolder = _dlFolderLocalService.updateDLFolder(childDLFolder);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}
		}

		// Indexer

		Indexer<DLFolder> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			DLFolder.class);

		indexer.reindex(childDLFolder);
	}

	protected <T extends RepositoryModel<T>> void triggerRepositoryEvent(
			long repositoryId,
			Class<? extends RepositoryEventType> repositoryEventType,
			Class<T> modelClass, T target)
		throws PortalException {

		Repository repository = RepositoryProviderUtil.getRepository(
			repositoryId);

		if (repository.isCapabilityProvided(
				RepositoryEventTriggerCapability.class)) {

			RepositoryEventTriggerCapability repositoryEventTriggerCapability =
				repository.getCapability(
					RepositoryEventTriggerCapability.class);

			repositoryEventTriggerCapability.trigger(
				repositoryEventType, modelClass, target);
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileShortcutLocalService _dlFileShortcutLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}
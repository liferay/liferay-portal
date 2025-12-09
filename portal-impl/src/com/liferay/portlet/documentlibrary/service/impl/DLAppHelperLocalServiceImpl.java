/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.asset.kernel.manager.AssetLinkManagerUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileVersionPersistence;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.RepositoryEventTriggerCapability;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.repository.event.WorkflowRepositoryEventType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryModel;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portlet.documentlibrary.service.base.DLAppHelperLocalServiceBaseImpl;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Provides the local service helper for the document library application.
 *
 * @author Alexander Chow
 */
public class DLAppHelperLocalServiceImpl
	extends DLAppHelperLocalServiceBaseImpl {

	@Override
	public void addFolder(
			long userId, Folder folder, ServiceContext serviceContext)
		throws PortalException {

		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		updateAsset(
			userId, folder, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());
	}

	@Override
	public void cancelCheckOut(
			long userId, FileEntry fileEntry, FileVersion sourceFileVersion,
			FileVersion destinationFileVersion, FileVersion draftFileVersion,
			ServiceContext serviceContext)
		throws PortalException {

		if (draftFileVersion == null) {
			return;
		}

		AssetEntry draftAssetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntryConstants.getClassName(),
			draftFileVersion.getPrimaryKey());

		if (draftAssetEntry != null) {
			_assetEntryLocalService.deleteEntry(draftAssetEntry);
		}
	}

	@Override
	public void cancelCheckOuts(long groupId) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			getCancelCheckOutsActionableDynamicQuery(groupId);

		actionableDynamicQuery.performActions();
	}

	@Override
	public void checkAssetEntry(
			long userId, FileEntry fileEntry, FileVersion fileVersion)
		throws PortalException {

		AssetEntry fileEntryAssetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		long[] assetCategoryIds = new long[0];
		String[] assetTagNames = new String[0];

		long fileEntryTypeId = getFileEntryTypeId(fileEntry);

		if (fileEntryAssetEntry == null) {
			fileEntryAssetEntry = _assetEntryLocalService.updateEntry(
				userId, fileEntry.getGroupId(), fileEntry.getCreateDate(),
				fileEntry.getModifiedDate(),
				DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId(),
				fileEntry.getUuid(), fileEntryTypeId, assetCategoryIds,
				assetTagNames, true, false, null, null, null,
				fileEntry.getExpirationDate(), fileEntry.getMimeType(),
				fileEntry.getTitle(), fileEntry.getDescription(), null, null,
				null, 0, 0, null);
		}

		AssetEntry fileVersionAssetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntryConstants.getClassName(),
			fileVersion.getFileVersionId());

		if ((fileVersionAssetEntry != null) || fileVersion.isApproved()) {
			return;
		}

		String version = fileVersion.getVersion();

		if (version.equals(DLFileEntryConstants.VERSION_DEFAULT)) {
			return;
		}

		assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());
		assetTagNames = _assetTagLocalService.getTagNames(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		fileVersionAssetEntry = _assetEntryLocalService.updateEntry(
			userId, fileEntry.getGroupId(), fileEntry.getCreateDate(),
			fileEntry.getModifiedDate(), DLFileEntryConstants.getClassName(),
			fileVersion.getFileVersionId(), fileEntry.getUuid(),
			fileEntryTypeId, assetCategoryIds, assetTagNames, true, false, null,
			null, null, fileEntry.getExpirationDate(), fileEntry.getMimeType(),
			fileEntry.getTitle(), fileEntry.getDescription(), null, null, null,
			0, 0, null);

		AssetLinkManagerUtil.updateLinks(
			AssetLinkManagerUtil.getDirectLinksIds(
				fileEntryAssetEntry.getEntryId()),
			fileVersionAssetEntry.getEntryId(), userId);
	}

	@Override
	public void deleteFileEntry(FileEntry fileEntry) throws PortalException {
		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		_deleteFileEntry(fileEntry.getFileEntryId());
	}

	@Override
	public void deleteFolder(Folder folder) throws PortalException {
		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		// Asset

		_assetEntryLocalService.deleteEntry(
			DLFolderConstants.getClassName(), folder.getFolderId());
	}

	@Override
	public void deleteRepositoryFileEntries(long repositoryId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_dlFileEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq("repositoryId", repositoryId)));
		actionableDynamicQuery.setPerformActionMethod(
			(DLFileEntry dlFileEntry) -> _deleteFileEntry(
				dlFileEntry.getFileEntryId()));
	}

	@Override
	public long getCheckedOutFileEntriesCount(long groupId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			getCancelCheckOutsActionableDynamicQuery(groupId);

		return actionableDynamicQuery.performCount();
	}

	@Override
	public void getFileAsStream(
		long userId, FileEntry fileEntry, boolean incrementCounter) {

		if (!incrementCounter) {
			return;
		}

		// File read count

		_assetEntryLocalService.incrementViewCounter(
			fileEntry.getCompanyId(), userId,
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId(), 1);

		List<DLFileShortcut> fileShortcuts =
			_dlFileShortcutPersistence.findByToFileEntryId(
				fileEntry.getFileEntryId());

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			_assetEntryLocalService.incrementViewCounter(
				fileEntry.getCompanyId(), userId,
				DLFileShortcutConstants.getClassName(),
				fileShortcut.getFileShortcutId(), 1);
		}
	}

	@Override
	public List<DLFileShortcut> getFileShortcuts(
		long groupId, long folderId, boolean active, int status) {

		return _dlFileShortcutPersistence.findByG_F_A_S(
			groupId, folderId, active, status);
	}

	@Override
	public int getFileShortcutsCount(
		long groupId, long folderId, boolean active, int status) {

		return _dlFileShortcutPersistence.countByG_F_A_S(
			groupId, folderId, active, status);
	}

	@Override
	public List<DLFileShortcut> getGroupFileShortcuts(long groupId) {
		return _dlFileShortcutPersistence.findByGroupId(groupId);
	}

	@Override
	public List<FileEntry> getNoAssetFileEntries() {
		return null;
	}

	@Override
	public void moveDependentsToTrash(DLFolder dlFolder)
		throws PortalException {
	}

	@Override
	public FileEntry moveFileEntryFromTrash(
			long userId, FileEntry fileEntry, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		return null;
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

		return null;
	}

	@Override
	public FileShortcut moveFileShortcutFromTrash(
			long userId, FileShortcut fileShortcut, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		return null;
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

		return null;
	}

	@Override
	public Folder moveFolderFromTrash(
			long userId, Folder folder, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		return null;
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

		return null;
	}

	@Async
	@Override
	public void reindex(long companyId, List<Long> dlFileEntryIds)
		throws PortalException {

		IntervalActionProcessor<Void> intervalActionProcessor =
			new IntervalActionProcessor<>(dlFileEntryIds.size());

		intervalActionProcessor.setPerformIntervalActionMethod(
			(start, end) -> {
				List<Long> sublist = dlFileEntryIds.subList(start, end);

				Indexer<DLFileEntry> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(DLFileEntry.class);

				IndexableActionableDynamicQuery
					indexableActionableDynamicQuery =
						_dlFileEntryLocalService.
							getIndexableActionableDynamicQuery();

				indexableActionableDynamicQuery.setAddCriteriaMethod(
					dynamicQuery -> {
						Property dlFileEntryId = PropertyFactoryUtil.forName(
							"fileEntryId");

						dynamicQuery.add(dlFileEntryId.in(sublist));
					});
				indexableActionableDynamicQuery.setCompanyId(companyId);
				indexableActionableDynamicQuery.setPerformActionMethod(
					(DLFileEntry dlFileEntry) ->
						indexableActionableDynamicQuery.addDocuments(
							indexer.getDocument(dlFileEntry)));

				indexableActionableDynamicQuery.performActions();

				intervalActionProcessor.incrementStart(sublist.size());

				return null;
			});

		intervalActionProcessor.performIntervalActions();
	}

	@Override
	public void restoreDependentsFromTrash(DLFolder dlFolder)
		throws PortalException {
	}

	@Override
	public void restoreFileEntryFromTrash(long userId, FileEntry fileEntry)
		throws PortalException {
	}

	@Override
	public void restoreFileEntryFromTrash(
			long userId, long newFolderId, FileEntry fileEntry)
		throws PortalException {
	}

	@Override
	public void restoreFileShortcutFromTrash(
			long userId, FileShortcut fileShortcut)
		throws PortalException {
	}

	@Override
	public void restoreFolderFromTrash(long userId, Folder folder)
		throws PortalException {
	}

	@Override
	public AssetEntry updateAsset(
			long userId, FileEntry fileEntry, FileVersion fileVersion,
			long assetClassPK)
		throws PortalException {

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			DLFileEntryConstants.getClassName(), assetClassPK);
		String[] assetTagNames = _assetTagLocalService.getTagNames(
			DLFileEntryConstants.getClassName(), assetClassPK);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntryConstants.getClassName(), assetClassPK);

		long[] assetLinkIds = null;

		if (assetEntry != null) {
			assetLinkIds = AssetLinkManagerUtil.getDirectLinksIds(
				assetEntry.getEntryId());
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAssetCategoryIds(assetCategoryIds);
		serviceContext.setAssetLinkEntryIds(assetLinkIds);
		serviceContext.setAssetTagNames(assetTagNames);

		return updateAsset(userId, fileEntry, fileVersion, serviceContext);
	}

	@Override
	public AssetEntry updateAsset(
			long userId, FileEntry fileEntry, FileVersion fileVersion,
			ServiceContext serviceContext)
		throws PortalException {

		AssetEntry assetEntry = null;

		boolean visible = false;

		boolean addDraftAssetEntry = false;

		if (fileEntry instanceof LiferayFileEntry) {
			DLFileVersion dlFileVersion = (DLFileVersion)fileVersion.getModel();

			if (dlFileVersion.isApproved()) {
				visible = true;
			}

			if (!dlFileVersion.isApproved() && !dlFileVersion.isScheduled()) {
				String version = dlFileVersion.getVersion();

				if (!version.equals(DLFileEntryConstants.VERSION_DEFAULT)) {
					addDraftAssetEntry = true;
				}
			}
		}
		else {
			visible = true;
		}

		long fileEntryTypeId = getFileEntryTypeId(fileEntry);

		if (addDraftAssetEntry) {
			if (serviceContext.getAssetCategoryIds() == null) {
				serviceContext.setAssetCategoryIds(
					_assetCategoryLocalService.getCategoryIds(
						DLFileEntryConstants.getClassName(),
						fileEntry.getFileEntryId()));
			}

			if (serviceContext.getAssetTagNames() == null) {
				serviceContext.setAssetTagNames(
					_assetTagLocalService.getTagNames(
						DLFileEntryConstants.getClassName(),
						fileEntry.getFileEntryId()));
			}

			if (serviceContext.getAssetLinkEntryIds() == null) {
				AssetEntry previousAssetEntry =
					_assetEntryLocalService.getEntry(
						DLFileEntryConstants.getClassName(),
						fileEntry.getFileEntryId());

				serviceContext.setAssetLinkEntryIds(
					AssetLinkManagerUtil.getRelatedDirectLinksIds(
						previousAssetEntry.getEntryId()));
			}

			assetEntry = _assetEntryLocalService.updateEntry(
				userId, fileEntry.getGroupId(), fileEntry.getCreateDate(),
				fileEntry.getModifiedDate(),
				DLFileEntryConstants.getClassName(),
				fileVersion.getFileVersionId(), fileEntry.getUuid(),
				fileEntryTypeId, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames(), true, false, null, null,
				null, fileEntry.getExpirationDate(), fileEntry.getMimeType(),
				fileEntry.getTitle(), fileEntry.getDescription(), null, null,
				null, 0, 0, null, serviceContext);
		}
		else {
			Date publishDate = null;

			if (visible) {
				publishDate = fileEntry.getCreateDate();
			}

			assetEntry = _assetEntryLocalService.updateEntry(
				userId, fileEntry.getGroupId(), fileEntry.getCreateDate(),
				fileEntry.getModifiedDate(),
				DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId(),
				fileEntry.getUuid(), fileEntryTypeId,
				serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames(), true, visible, null, null,
				publishDate, fileEntry.getExpirationDate(),
				fileEntry.getMimeType(), fileEntry.getTitle(),
				fileEntry.getDescription(), null, null, null, 0, 0, null,
				serviceContext);

			List<DLFileShortcut> dlFileShortcuts =
				_dlFileShortcutPersistence.findByToFileEntryId(
					fileEntry.getFileEntryId());

			for (DLFileShortcut dlFileShortcut : dlFileShortcuts) {
				_assetEntryLocalService.updateEntry(
					userId, dlFileShortcut.getGroupId(),
					dlFileShortcut.getCreateDate(),
					dlFileShortcut.getModifiedDate(),
					DLFileShortcutConstants.getClassName(),
					dlFileShortcut.getFileShortcutId(),
					dlFileShortcut.getUuid(), fileEntryTypeId,
					serviceContext.getAssetCategoryIds(),
					serviceContext.getAssetTagNames(), true, true, null, null,
					dlFileShortcut.getCreateDate(), null,
					fileEntry.getMimeType(), fileEntry.getTitle(),
					fileEntry.getDescription(), null, null, null, 0, 0, null,
					serviceContext);
			}
		}

		AssetLinkManagerUtil.updateLinks(
			serviceContext.getAssetLinkEntryIds(), assetEntry.getEntryId(),
			userId);

		return assetEntry;
	}

	@Override
	public AssetEntry updateAsset(
			long userId, Folder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException {

		boolean visible = false;

		if (folder instanceof LiferayFolder) {
			DLFolder dlFolder = (DLFolder)folder.getModel();

			if (dlFolder.isApproved() && !dlFolder.isHidden() &&
				!dlFolder.isInHiddenFolder()) {

				visible = true;
			}
		}
		else {
			visible = true;
		}

		Date publishDate = null;

		if (visible) {
			publishDate = folder.getCreateDate();
		}

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, folder.getGroupId(), folder.getCreateDate(),
			folder.getModifiedDate(), DLFolderConstants.getClassName(),
			folder.getFolderId(), folder.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, visible, null, null, publishDate, null, null,
			folder.getName(), folder.getDescription(), null, null, null, 0, 0,
			null);

		AssetLinkManagerUtil.updateLinks(
			assetLinkEntryIds, assetEntry.getEntryId(), userId);

		return assetEntry;
	}

	@Override
	public void updateFileEntry(
			long userId, FileEntry fileEntry, FileVersion sourceFileVersion,
			FileVersion destinationFileVersion, long assetClassPK)
		throws PortalException {

		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		boolean updateAsset = true;

		if (fileEntry instanceof LiferayFileEntry) {
			String version = fileEntry.getVersion();

			if (version.equals(destinationFileVersion.getVersion())) {
				updateAsset = false;
			}
		}

		if (updateAsset) {
			updateAsset(
				userId, fileEntry, destinationFileVersion, assetClassPK);
		}
	}

	@Override
	public void updateFileEntry(
			long userId, FileEntry fileEntry, FileVersion sourceFileVersion,
			FileVersion destinationFileVersion, ServiceContext serviceContext)
		throws PortalException {

		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		if (Objects.equals(serviceContext.getCommand(), Constants.REVERT)) {
			List<AssetCategory> assetCategories =
				_assetCategoryLocalService.getCategories(
					DLFileEntryConstants.getClassName(),
					fileEntry.getFileEntryId());

			List<Long> assetCategoryIds = ListUtil.toList(
				assetCategories, AssetCategory.CATEGORY_ID_ACCESSOR);

			serviceContext.setAssetCategoryIds(
				ArrayUtil.toLongArray(assetCategoryIds));
		}

		updateAsset(userId, fileEntry, destinationFileVersion, serviceContext);
	}

	@Override
	public void updateFolder(
			long userId, Folder folder, ServiceContext serviceContext)
		throws PortalException {

		updateAsset(
			userId, folder, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());
	}

	@Override
	public void updateStatus(
			long userId, FileEntry fileEntry, FileVersion latestFileVersion,
			int oldStatus, int newStatus, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		if (!DLAppHelperThreadLocal.isEnabled()) {
			return;
		}

		if (newStatus == WorkflowConstants.STATUS_APPROVED) {

			// Asset

			String latestFileVersionVersion = latestFileVersion.getVersion();

			if (latestFileVersionVersion.equals(fileEntry.getVersion())) {
				if (!latestFileVersionVersion.equals(
						DLFileEntryConstants.VERSION_DEFAULT)) {

					AssetEntry draftAssetEntry =
						_assetEntryLocalService.fetchEntry(
							DLFileEntryConstants.getClassName(),
							latestFileVersion.getPrimaryKey());

					if (draftAssetEntry != null) {
						long fileEntryTypeId = getFileEntryTypeId(fileEntry);

						long[] assetCategoryIds =
							draftAssetEntry.getCategoryIds();
						String[] assetTagNames = draftAssetEntry.getTagNames();

						AssetEntry assetEntry =
							_assetEntryLocalService.updateEntry(
								userId, fileEntry.getGroupId(),
								fileEntry.getCreateDate(),
								fileEntry.getModifiedDate(),
								DLFileEntryConstants.getClassName(),
								fileEntry.getFileEntryId(), fileEntry.getUuid(),
								fileEntryTypeId, assetCategoryIds,
								assetTagNames, true, true, null, null,
								fileEntry.getCreateDate(),
								fileEntry.getExpirationDate(),
								draftAssetEntry.getMimeType(),
								fileEntry.getTitle(),
								fileEntry.getDescription(), null, null, null, 0,
								0, null);

						AssetLinkManagerUtil.updateLinks(
							AssetLinkManagerUtil.getRelatedDirectLinksIds(
								assetEntry.getEntryId()),
							assetEntry.getEntryId(), userId);

						_assetEntryLocalService.deleteEntry(draftAssetEntry);
					}
				}

				AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
					DLFileEntryConstants.getClassName(),
					fileEntry.getFileEntryId());

				if (assetEntry != null) {
					_assetEntryLocalService.updateEntry(
						assetEntry.getClassName(), assetEntry.getClassPK(),
						assetEntry.getCreateDate(),
						assetEntry.getExpirationDate(), assetEntry.isListable(),
						true);
				}
			}

			// Sync

			String event = GetterUtil.getString(workflowContext.get("event"));

			if (Validator.isNotNull(event)) {
				triggerRepositoryEvent(
					fileEntry.getRepositoryId(),
					getWorkflowRepositoryEventTypeClass(event), FileEntry.class,
					fileEntry);
			}

			if ((oldStatus != WorkflowConstants.STATUS_IN_TRASH) &&
				!fileEntry.isInTrash()) {

				// Social

				Date activityCreateDate = latestFileVersion.getModifiedDate();
				int activityType = DLActivityKeys.UPDATE_FILE_ENTRY;

				if (event.equals(DLSyncConstants.EVENT_ADD)) {
					activityCreateDate = latestFileVersion.getCreateDate();
					activityType = DLActivityKeys.ADD_FILE_ENTRY;
				}

				JSONObject extraDataJSONObject = JSONUtil.put(
					"title", fileEntry.getTitle());

				SocialActivityManagerUtil.addUniqueActivity(
					latestFileVersion.getStatusByUserId(), activityCreateDate,
					fileEntry, activityType, extraDataJSONObject.toString(), 0);
			}
		}
		else {

			// Asset

			boolean visible = false;

			if (newStatus != WorkflowConstants.STATUS_IN_TRASH) {
				List<DLFileVersion> approvedFileVersions =
					_dlFileVersionPersistence.findByF_S(
						fileEntry.getFileEntryId(),
						WorkflowConstants.STATUS_APPROVED);

				if (!approvedFileVersions.isEmpty()) {
					visible = true;
				}
			}

			_assetEntryLocalService.updateVisible(
				DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId(),
				visible);
		}
	}

	protected ActionableDynamicQuery getCancelCheckOutsActionableDynamicQuery(
		long groupId) {

		ActionableDynamicQuery fileEntryActionableDynamicQuery =
			_dlFileEntryLocalService.getActionableDynamicQuery();

		fileEntryActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property fileEntryIdProperty = PropertyFactoryUtil.forName(
					"fileEntryId");

				DynamicQuery fileVersionDynamicQuery =
					DynamicQueryFactoryUtil.forClass(
						DLFileVersion.class, "dlFileVersion",
						PortalClassLoaderUtil.getClassLoader());

				fileVersionDynamicQuery.setProjection(
					ProjectionFactoryUtil.property("fileEntryId"));

				fileVersionDynamicQuery.add(
					RestrictionsFactoryUtil.eqProperty(
						"dlFileVersion.fileEntryId", "this.fileEntryId"));

				Property versionProperty = PropertyFactoryUtil.forName(
					"version");

				fileVersionDynamicQuery.add(
					versionProperty.eq(
						DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION));

				dynamicQuery.add(
					fileEntryIdProperty.in(fileVersionDynamicQuery));
			});
		fileEntryActionableDynamicQuery.setGroupId(groupId);
		fileEntryActionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<DLFileEntry>)
				dlFileEntry -> _dlAppService.cancelCheckOut(
					dlFileEntry.getFileEntryId()));

		return fileEntryActionableDynamicQuery;
	}

	protected long getFileEntryTypeId(FileEntry fileEntry) {
		if (fileEntry instanceof LiferayFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			return dlFileEntry.getFileEntryTypeId();
		}

		return 0;
	}

	protected Class<? extends WorkflowRepositoryEventType>
		getWorkflowRepositoryEventTypeClass(String syncEvent) {

		if (syncEvent.equals(DLSyncConstants.EVENT_ADD)) {
			return WorkflowRepositoryEventType.Add.class;
		}
		else if (syncEvent.equals(DLSyncConstants.EVENT_UPDATE)) {
			return WorkflowRepositoryEventType.Update.class;
		}

		throw new IllegalArgumentException(
			String.format("Unsupported sync event %s", syncEvent));
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

	private void _deleteFileEntry(long fileEntryId) throws PortalException {

		// File shortcuts

		_dlFileShortcutLocalService.deleteFileShortcuts(fileEntryId);

		// Asset

		_assetEntryLocalService.deleteEntry(
			DLFileEntryConstants.getClassName(), fileEntryId);

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			DLFileEntryConstants.getClassName(), fileEntryId);
	}

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = AssetTagLocalService.class)
	private AssetTagLocalService _assetTagLocalService;

	@BeanReference(type = DLAppService.class)
	private DLAppService _dlAppService;

	@BeanReference(type = DLFileEntryLocalService.class)
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@BeanReference(type = DLFileShortcutLocalService.class)
	private DLFileShortcutLocalService _dlFileShortcutLocalService;

	@BeanReference(type = DLFileShortcutPersistence.class)
	private DLFileShortcutPersistence _dlFileShortcutPersistence;

	@BeanReference(type = DLFileVersionPersistence.class)
	private DLFileVersionPersistence _dlFileVersionPersistence;

	@BeanReference(type = RatingsStatsLocalService.class)
	private RatingsStatsLocalService _ratingsStatsLocalService;

	/**
	 * @see com.liferay.document.library.sync.constants.DLSyncConstants
	 */
	private class DLSyncConstants {

		public static final String EVENT_ADD = "add";

		public static final String EVENT_UPDATE = "update";

	}

}
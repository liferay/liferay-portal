/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.RepositoryPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.documentlibrary.service.base.DLFileShortcutLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DLFileShortcutLocalServiceImpl
	extends DLFileShortcutLocalServiceBaseImpl {

	@Override
	public DLFileShortcut addFileShortcut(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		// File shortcut

		User user = _userPersistence.findByPrimaryKey(userId);

		folderId = getFolderId(user.getCompanyId(), folderId);

		validate(user, groupId, folderId, toFileEntryId);

		long fileShortcutId = counterLocalService.increment();

		DLFileShortcut fileShortcut = dlFileShortcutPersistence.create(
			fileShortcutId);

		fileShortcut.setUuid(serviceContext.getUuid());
		fileShortcut.setExternalReferenceCode(externalReferenceCode);
		fileShortcut.setGroupId(groupId);
		fileShortcut.setCompanyId(user.getCompanyId());
		fileShortcut.setUserId(user.getUserId());
		fileShortcut.setUserName(user.getFullName());
		fileShortcut.setRepositoryId(repositoryId);
		fileShortcut.setFolderId(folderId);
		fileShortcut.setToFileEntryId(toFileEntryId);
		fileShortcut.setTreePath(fileShortcut.buildTreePath());
		fileShortcut.setActive(true);
		fileShortcut.setStatus(WorkflowConstants.STATUS_APPROVED);
		fileShortcut.setStatusByUserId(userId);
		fileShortcut.setStatusByUserName(user.getFullName());
		fileShortcut.setStatusDate(new Date());

		fileShortcut = dlFileShortcutPersistence.update(fileShortcut);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addFileShortcutResources(
				fileShortcut, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addFileShortcutResources(
				fileShortcut, serviceContext.getModelPermissions());
		}

		// Folder

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_dlFolderLocalService.updateLastPostDate(
				folderId, fileShortcut.getModifiedDate());
		}

		// Asset

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(toFileEntryId);

		copyAssetTags(fileEntry, serviceContext);

		updateAsset(
			userId, fileShortcut,
			_assetCategoryLocalService.getCategoryIds(
				DLFileEntryConstants.getClassName(),
				fileEntry.getFileEntryId()),
			serviceContext.getAssetTagNames());

		return fileShortcut;
	}

	@Override
	public void addFileShortcutResources(
			DLFileShortcut fileShortcut, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			fileShortcut.getCompanyId(), fileShortcut.getGroupId(),
			fileShortcut.getUserId(), DLFileShortcutConstants.getClassName(),
			fileShortcut.getFileShortcutId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addFileShortcutResources(
			DLFileShortcut fileShortcut, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			fileShortcut.getCompanyId(), fileShortcut.getGroupId(),
			fileShortcut.getUserId(), DLFileShortcutConstants.getClassName(),
			fileShortcut.getFileShortcutId(), modelPermissions);
	}

	@Override
	public void addFileShortcutResources(
			long fileShortcutId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		addFileShortcutResources(
			fileShortcut, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addFileShortcutResources(
			long fileShortcutId, ModelPermissions modelPermissions)
		throws PortalException {

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		addFileShortcutResources(fileShortcut, modelPermissions);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteFileShortcut(DLFileShortcut fileShortcut)
		throws PortalException {

		// File shortcut

		dlFileShortcutPersistence.remove(fileShortcut);

		// Resources

		_resourceLocalService.deleteResource(
			fileShortcut.getCompanyId(), DLFileShortcutConstants.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			fileShortcut.getFileShortcutId());

		// Asset

		_assetEntryLocalService.deleteEntry(
			DLFileShortcutConstants.getClassName(),
			fileShortcut.getFileShortcutId());
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId) throws PortalException {
		DLFileShortcut fileShortcut =
			dlFileShortcutLocalService.getDLFileShortcut(fileShortcutId);

		dlFileShortcutLocalService.deleteFileShortcut(fileShortcut);
	}

	@Override
	public void deleteFileShortcut(String externalReferenceCode, long groupId)
		throws PortalException {

		dlFileShortcutLocalService.deleteFileShortcut(
			dlFileShortcutPersistence.findByERC_G(
				externalReferenceCode, groupId));
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId) throws PortalException {
		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByToFileEntryId(toFileEntryId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			dlFileShortcutLocalService.deleteFileShortcut(fileShortcut);
		}
	}

	@Override
	public void deleteFileShortcuts(long groupId, long folderId)
		throws PortalException {

		deleteFileShortcuts(groupId, folderId, true);
	}

	@Override
	public void deleteFileShortcuts(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByG_F(groupId, folderId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			TrashHelper trashHelper = _trashHelperSnapshot.get();

			if (includeTrashedEntries ||
				!trashHelper.isInTrashExplicitly(fileShortcut)) {

				dlFileShortcutLocalService.deleteFileShortcut(fileShortcut);
			}
		}
	}

	@Override
	public void deleteRepositoryFileShortcuts(long repositoryId)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			dlFileShortcutLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq("repositoryId", repositoryId)));

		long groupId = repositoryId;

		Repository repository = _repositoryPersistence.fetchByPrimaryKey(
			repositoryId);

		if (repository != null) {
			groupId = repository.getGroupId();
		}

		actionableDynamicQuery.setGroupId(groupId);

		actionableDynamicQuery.setPerformActionMethod(
			(DLFileShortcut fileShortcut) ->
				dlFileShortcutLocalService.deleteFileShortcut(fileShortcut));

		actionableDynamicQuery.performActions();
	}

	@Override
	public void disableFileShortcuts(long toFileEntryId) {
		updateFileShortcutsActive(toFileEntryId, false);
	}

	@Override
	public void enableFileShortcuts(long toFileEntryId) {
		updateFileShortcutsActive(toFileEntryId, true);
	}

	@Override
	public DLFileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException {

		return dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);
	}

	@Override
	public List<DLFileShortcut> getFileShortcuts(long toFileEntryId) {
		return dlFileShortcutPersistence.findByToFileEntryId(toFileEntryId);
	}

	@Override
	public List<DLFileShortcut> getFileShortcuts(long groupId, long folderId) {
		return dlFileShortcutPersistence.findByG_F(groupId, folderId);
	}

	@Override
	public List<DLFileShortcut> getFileShortcuts(
		long groupId, long folderId, boolean active, int status, int start,
		int end) {

		return dlFileShortcutPersistence.findByG_F_A_S(
			groupId, folderId, active, status, start, end);
	}

	@Override
	public int getFileShortcutsCount(
		long groupId, long folderId, boolean active, int status) {

		return dlFileShortcutPersistence.countByG_F_A_S(
			groupId, folderId, active, status);
	}

	@Override
	public List<DLFileShortcut> getGroupFileShortcuts(long groupId) {
		return dlFileShortcutPersistence.findByGroupId(groupId);
	}

	@Override
	public void rebuildTree(long companyId) throws PortalException {
		_dlFolderLocalService.rebuildTree(companyId);
	}

	@Override
	public void setTreePaths(long folderId, String treePath)
		throws PortalException {

		if (treePath == null) {
			throw new IllegalArgumentException("Tree path is null");
		}

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
					DLFolder dlFolder = _dlFolderLocalService.fetchDLFolder(
						folderId);

					if (dlFolder != null) {
						Property groupIdProperty = PropertyFactoryUtil.forName(
							"groupId");

						dynamicQuery.add(
							groupIdProperty.eq(dlFolder.getGroupId()));
					}
				}

				Property folderIdProperty = PropertyFactoryUtil.forName(
					"folderId");

				dynamicQuery.add(folderIdProperty.eq(folderId));

				Property treePathProperty = PropertyFactoryUtil.forName(
					"treePath");

				dynamicQuery.add(
					RestrictionsFactoryUtil.or(
						treePathProperty.isNull(),
						treePathProperty.ne(treePath)));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(DLFileShortcut dlFileShortcut) -> {
				dlFileShortcut.setTreePath(treePath);

				updateDLFileShortcut(dlFileShortcut);
			});

		actionableDynamicQuery.performActions();
	}

	@Override
	public void updateAsset(
			long userId, DLFileShortcut fileShortcut, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			fileShortcut.getToFileEntryId());

		_assetEntryLocalService.updateEntry(
			userId, fileShortcut.getGroupId(), fileShortcut.getCreateDate(),
			fileShortcut.getModifiedDate(),
			DLFileShortcutConstants.getClassName(),
			fileShortcut.getFileShortcutId(), fileShortcut.getUuid(), 0,
			assetCategoryIds, assetTagNames, true, false, null, null,
			fileShortcut.getCreateDate(), null, fileEntry.getMimeType(),
			fileEntry.getTitle(), fileEntry.getDescription(), null, null, null,
			0, 0, null);
	}

	@Override
	public DLFileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long repositoryId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException {

		// File shortcut

		User user = _userPersistence.findByPrimaryKey(userId);

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		validate(user, fileShortcut.getGroupId(), folderId, toFileEntryId);

		fileShortcut.setFolderId(folderId);
		fileShortcut.setToFileEntryId(toFileEntryId);
		fileShortcut.setTreePath(fileShortcut.buildTreePath());

		fileShortcut = dlFileShortcutPersistence.update(fileShortcut);

		// Folder

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_dlFolderLocalService.updateLastPostDate(
				folderId, fileShortcut.getModifiedDate());
		}

		// Asset

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(toFileEntryId);

		copyAssetTags(fileEntry, serviceContext);

		updateAsset(
			userId, fileShortcut,
			_assetCategoryLocalService.getCategoryIds(
				DLFileEntryConstants.getClassName(),
				fileEntry.getFileEntryId()),
			serviceContext.getAssetTagNames());

		return fileShortcut;
	}

	@Override
	public void updateFileShortcuts(
		long oldToFileEntryId, long newToFileEntryId) {

		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByToFileEntryId(oldToFileEntryId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			fileShortcut.setToFileEntryId(newToFileEntryId);

			dlFileShortcutPersistence.update(fileShortcut);
		}
	}

	@Override
	public void updateFileShortcutsActive(long toFileEntryId, boolean active) {
		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByToFileEntryId(toFileEntryId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			fileShortcut.setActive(active);

			dlFileShortcutPersistence.update(fileShortcut);
		}
	}

	@Override
	public DLFileShortcut updateStatus(
			long userId, long fileShortcutId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		fileShortcut.setStatus(status);
		fileShortcut.setStatusByUserId(user.getUserId());
		fileShortcut.setStatusByUserName(user.getFullName());
		fileShortcut.setStatusDate(serviceContext.getModifiedDate(null));

		return dlFileShortcutPersistence.update(fileShortcut);
	}

	protected void copyAssetTags(
			FileEntry fileEntry, ServiceContext serviceContext)
		throws PortalException {

		String[] assetTagNames = _assetTagLocalService.getTagNames(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		_assetTagLocalService.checkTags(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			assetTagNames);

		serviceContext.setAssetTagNames(assetTagNames);
	}

	protected long getFolderId(long companyId, long folderId) {
		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			// Ensure folder exists and belongs to the proper company

			DLFolder dlFolder = _dlFolderPersistence.fetchByPrimaryKey(
				folderId);

			if ((dlFolder == null) || (companyId != dlFolder.getCompanyId())) {
				folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}
		}

		return folderId;
	}

	protected void validate(
			User user, long groupId, long folderId, long toFileEntryId)
		throws PortalException {

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder parentDLFolder = _dlFolderPersistence.findByPrimaryKey(
				folderId);

			if ((groupId != parentDLFolder.getGroupId()) ||
				parentDLFolder.isInTrash()) {

				throw new NoSuchFolderException();
			}
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(toFileEntryId);

		if (user.getCompanyId() != fileEntry.getCompanyId()) {
			throw new NoSuchFileEntryException(
				"{fileEntryId=" + toFileEntryId + "}");
		}
	}

	private static final Snapshot<TrashHelper> _trashHelperSnapshot =
		new Snapshot<>(DLFileShortcutLocalServiceImpl.class, TrashHelper.class);

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = AssetTagLocalService.class)
	private AssetTagLocalService _assetTagLocalService;

	@BeanReference(type = DLAppLocalService.class)
	private DLAppLocalService _dlAppLocalService;

	@BeanReference(type = DLFolderLocalService.class)
	private DLFolderLocalService _dlFolderLocalService;

	@BeanReference(type = DLFolderPersistence.class)
	private DLFolderPersistence _dlFolderPersistence;

	@BeanReference(type = RepositoryPersistence.class)
	private RepositoryPersistence _repositoryPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}
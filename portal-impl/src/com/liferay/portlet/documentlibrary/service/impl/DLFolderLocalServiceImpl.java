/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.FolderNameException;
import com.liferay.document.library.kernel.exception.InvalidFolderException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.exception.RequiredFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.document.library.kernel.util.comparator.FolderIdComparator;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.DateOverrideIncrement;
import com.liferay.portal.kernel.lock.ExpiredLockException;
import com.liferay.portal.kernel.lock.InvalidLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.lock.NoSuchLockException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.UndeployedExternalRepositoryException;
import com.liferay.portal.kernel.repository.event.RepositoryEventTrigger;
import com.liferay.portal.kernel.repository.event.RepositoryEventType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WebDAVPropsLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.RepositoryPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.WebDAVPropsPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.tree.TreeModelTasksAdapter;
import com.liferay.portal.kernel.tree.TreePathUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.model.LiferayFolder;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.portlet.documentlibrary.lar.FileEntryUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;
import com.liferay.portlet.documentlibrary.service.base.DLFolderLocalServiceBaseImpl;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class DLFolderLocalServiceImpl extends DLFolderLocalServiceBaseImpl {

	@Override
	public DLFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, boolean mountPoint, long parentFolderId,
			String name, String description, boolean hidden,
			ServiceContext serviceContext)
		throws PortalException {

		// Folder

		User user = _userPersistence.findByPrimaryKey(userId);
		parentFolderId = getParentFolderId(
			groupId, repositoryId, parentFolderId);
		Date date = new Date();

		validateFolder(groupId, parentFolderId, name);

		long folderId = counterLocalService.increment();

		DLFolder dlFolder = dlFolderPersistence.create(folderId);

		dlFolder.setUuid(serviceContext.getUuid());
		dlFolder.setExternalReferenceCode(externalReferenceCode);
		dlFolder.setGroupId(groupId);
		dlFolder.setCompanyId(user.getCompanyId());
		dlFolder.setUserId(user.getUserId());
		dlFolder.setUserName(user.getFullName());
		dlFolder.setRepositoryId(repositoryId);
		dlFolder.setMountPoint(mountPoint);
		dlFolder.setParentFolderId(parentFolderId);
		dlFolder.setTreePath(dlFolder.buildTreePath());
		dlFolder.setName(name);
		dlFolder.setDescription(description);
		dlFolder.setLastPostDate(date);
		dlFolder.setHidden(hidden);
		dlFolder.setRestrictionType(DLFolderConstants.RESTRICTION_TYPE_INHERIT);
		dlFolder.setExpandoBridgeAttributes(serviceContext);

		dlFolder = dlFolderPersistence.update(dlFolder);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addFolderResources(
				dlFolder, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			if (serviceContext.isDeriveDefaultPermissions()) {
				serviceContext.deriveDefaultPermissions(
					repositoryId, DLFolderConstants.getClassName());
			}

			addFolderResources(dlFolder, serviceContext.getModelPermissions());
		}

		// Parent folder

		if (parentFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			dlFolderLocalService.updateLastPostDate(parentFolderId, date);
		}

		return dlFolder;
	}

	@Override
	public void deleteAllByGroup(long groupId) throws PortalException {
		Group group = _groupLocalService.getGroup(groupId);

		List<DLFolder> dlFolders = dlFolderPersistence.findByG_P(
			groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		for (DLFolder dlFolder : dlFolders) {
			dlFolderLocalService.deleteFolder(dlFolder);
		}

		_dlFileEntryLocalService.deleteFileEntries(
			groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_dlFileEntryTypeLocalService.deleteFileEntryTypes(groupId);

		_dlFileShortcutLocalService.deleteFileShortcuts(
			groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		DLStoreUtil.deleteDirectory(
			group.getCompanyId(), groupId, StringPool.BLANK);
	}

	@Override
	public void deleteAllByRepository(long repositoryId)
		throws PortalException {

		Repository repository = _repositoryPersistence.fetchByPrimaryKey(
			repositoryId);

		long groupId = repositoryId;

		if (repository != null) {
			groupId = repository.getGroupId();
		}

		Group group = _groupLocalService.getGroup(groupId);

		RepositoryEventTrigger repositoryEventTrigger =
			RepositoryUtil.getRepositoryEventTrigger(repositoryId);

		List<DLFolder> dlFolders = dlFolderPersistence.findByRepositoryId(
			repositoryId);

		for (DLFolder dlFolder : dlFolders) {
			deleteFolderDependencies(dlFolder, true);

			repositoryEventTrigger.trigger(
				RepositoryEventType.Delete.class, Folder.class,
				new LiferayFolder(dlFolder));

			Indexer<DLFolder> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				DLFolder.class);

			indexer.delete(dlFolder);
		}

		if (repository != null) {
			_dlFileEntryLocalService.deleteRepositoryFileEntries(
				repository.getRepositoryId());
		}
		else {
			_dlFileEntryLocalService.deleteRepositoryFileEntries(groupId);

			_dlFileShortcutLocalService.deleteRepositoryFileShortcuts(groupId);
		}

		DLStoreUtil.deleteDirectory(
			group.getCompanyId(), repositoryId, StringPool.BLANK);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFolder deleteFolder(DLFolder dlFolder) throws PortalException {
		return deleteFolder(dlFolder, true);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFolder deleteFolder(
			DLFolder dlFolder, boolean includeTrashedEntries)
		throws PortalException {

		deleteSubfolders(dlFolder, includeTrashedEntries);

		deleteFolderDependencies(dlFolder, includeTrashedEntries);

		return dlFolder;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public DLFolder deleteFolder(long folderId) throws PortalException {
		return dlFolderLocalService.deleteFolder(folderId, true);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public DLFolder deleteFolder(long folderId, boolean includeTrashedEntries)
		throws PortalException {

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		return dlFolderLocalService.deleteFolder(
			dlFolder, includeTrashedEntries);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public DLFolder deleteFolder(
			long userId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		if (hasFolderLock(userId, folderId)) {
			return deleteFolder(folderId, includeTrashedEntries);
		}

		Lock lock = lockFolder(
			userId, folderId, null, false, DLFolderImpl.LOCK_EXPIRATION_TIME);

		try {
			return deleteFolder(folderId, includeTrashedEntries);
		}
		finally {
			unlockFolder(folderId, lock.getUuid());
		}
	}

	@Override
	public DLFolder fetchFolder(long folderId) {
		return dlFolderPersistence.fetchByPrimaryKey(folderId);
	}

	@Override
	public DLFolder fetchFolder(
		long groupId, long parentFolderId, String name) {

		return dlFolderPersistence.fetchByG_P_N(groupId, parentFolderId, name);
	}

	@Override
	public DLFolder fetchFolder(String uuid, long groupId) {
		return dlFolderPersistence.fetchByUUID_G(uuid, groupId);
	}

	@Override
	public List<DLFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return dlFolderPersistence.findByCompanyId(companyId, start, end);
	}

	@Override
	public int getCompanyFoldersCount(long companyId) {
		return dlFolderPersistence.countByCompanyId(companyId);
	}

	@Override
	public List<Object> getFileEntriesAndFileShortcuts(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return dlFolderFinder.findFE_FS_ByG_F(
			groupId, folderId, queryDefinition);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return dlFolderFinder.countFE_FS_ByG_F(
			groupId, folderId, queryDefinition);
	}

	@Override
	public DLFolder getFolder(long folderId) throws PortalException {
		return dlFolderPersistence.findByPrimaryKey(folderId);
	}

	@Override
	public DLFolder getFolder(long groupId, long parentFolderId, String name)
		throws PortalException {

		return dlFolderPersistence.findByG_P_N(groupId, parentFolderId, name);
	}

	@Override
	public long getFolderId(long companyId, long folderId) {
		return getFolderId(dlFolderPersistence, companyId, folderId);
	}

	@Override
	public List<DLFolder> getFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return dlFolderPersistence.findByG_M_LikeT_H(
			groupId, mountPoint, treePath, hidden);
	}

	@Override
	public List<DLFolder> getFolders(long groupId, long parentFolderId) {
		return getFolders(groupId, parentFolderId, true);
	}

	@Override
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders) {

		if (includeMountfolders) {
			return dlFolderPersistence.findByG_P(groupId, parentFolderId);
		}

		return dlFolderPersistence.findByG_M_P_H(
			groupId, false, parentFolderId, false);
	}

	@Override
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		if (includeMountfolders) {
			return dlFolderPersistence.findByG_P_H_S(
				groupId, parentFolderId, false, status, start, end,
				orderByComparator);
		}

		return dlFolderPersistence.findByG_M_P_H_S(
			groupId, false, parentFolderId, false, status, start, end,
			orderByComparator);
	}

	@Override
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int start, int end, OrderByComparator<DLFolder> orderByComparator) {

		if (includeMountfolders) {
			return dlFolderPersistence.findByG_P(
				groupId, parentFolderId, start, end, orderByComparator);
		}

		return dlFolderPersistence.findByG_M_P_H(
			groupId, false, parentFolderId, false, start, end,
			orderByComparator);
	}

	@Override
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return getFolders(
			groupId, parentFolderId, true, start, end, orderByComparator);
	}

	@Override
	public List<DLFolder> getFolders(long classNameId, String treePath) {
		return dlFolderFinder.findF_ByC_T(classNameId, treePath);
	}

	@Override
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return dlFolderFinder.findF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return dlFolderFinder.countF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return getFoldersCount(groupId, parentFolderId, true);
	}

	@Override
	public int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders) {

		if (includeMountfolders) {
			return dlFolderPersistence.countByG_P(groupId, parentFolderId);
		}

		return dlFolderPersistence.countByG_M_P_H(
			groupId, false, parentFolderId, false);
	}

	@Override
	public int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return getFoldersCount(
				groupId, parentFolderId, includeMountfolders);
		}
		else if (includeMountfolders) {
			return dlFolderPersistence.countByG_P_H_S(
				groupId, parentFolderId, false, status);
		}

		return dlFolderPersistence.countByG_M_P_H_S(
			groupId, false, parentFolderId, false, status);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getFoldersCount(long, long, boolean, int)}
	 */
	@Deprecated
	@Override
	public int getFoldersCount(
		long groupId, long parentFolderId, int status,
		boolean includeMountfolders) {

		return getFoldersCount(
			groupId, parentFolderId, includeMountfolders, status);
	}

	@Override
	public long getFolderSize(long companyId, long groupId, String treePath) {
		List<Long> result = dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.sum(
					DLFileEntryTable.INSTANCE.size
				).as(
					"SUM_VALUE"
				)
			).from(
				DLFileEntryTable.INSTANCE
			).where(
				DLFileEntryTable.INSTANCE.companyId.eq(
					companyId
				).and(
					DLFileEntryTable.INSTANCE.groupId.eq(groupId)
				).and(
					DLFileEntryTable.INSTANCE.treePath.like(
						treePath.concat(StringPool.PERCENT))
				)
			));

		if (result.get(0) == null) {
			return 0;
		}

		return result.get(0);
	}

	@Override
	public List<Long> getGroupFolderIds(long groupId, long parentFolderId) {
		List<Long> folderIds = new ArrayList<>();

		folderIds.add(parentFolderId);

		getGroupSubfolderIds(folderIds, groupId, parentFolderId);

		return folderIds;
	}

	@Override
	public void getGroupSubfolderIds(
		List<Long> folderIds, long groupId, long folderId) {

		List<DLFolder> dlFolders = dlFolderPersistence.findByG_P(
			groupId, folderId);

		for (DLFolder dlFolder : dlFolders) {
			folderIds.add(dlFolder.getFolderId());

			getGroupSubfolderIds(
				folderIds, dlFolder.getGroupId(), dlFolder.getFolderId());
		}
	}

	@Override
	public DLFolder getMountFolder(long repositoryId) throws PortalException {
		return dlFolderPersistence.findByR_M(repositoryId, true);
	}

	@Override
	public List<DLFolder> getMountFolders(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator) {

		return dlFolderPersistence.findByG_M_P_H(
			groupId, true, parentFolderId, false, start, end,
			orderByComparator);
	}

	@Override
	public int getMountFoldersCount(long groupId, long parentFolderId) {
		return dlFolderPersistence.countByG_M_P_H(
			groupId, true, parentFolderId, false);
	}

	@Override
	public List<DLFolder> getNoAssetFolders() {
		return dlFolderFinder.findF_ByNoAssets();
	}

	@Override
	public List<DLFolder> getNotInTrashFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden) {

		return dlFolderPersistence.findByG_M_LikeT_H_NotS(
			groupId, mountPoint, treePath, hidden,
			WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public List<Long> getRepositoryFolderIds(
		long repositoryId, long parentFolderId) {

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(parentFolderId);

		getRepositorySubfolderIds(folderIds, repositoryId, parentFolderId);

		return folderIds;
	}

	@Override
	public List<DLFolder> getRepositoryFolders(
		long repositoryId, int start, int end) {

		return dlFolderPersistence.findByRepositoryId(repositoryId, start, end);
	}

	@Override
	public int getRepositoryFoldersCount(long repositoryId) {
		return dlFolderPersistence.countByRepositoryId(repositoryId);
	}

	@Override
	public void getRepositorySubfolderIds(
		List<Long> folderIds, long repositoryId, long folderId) {

		List<DLFolder> dlFolders = dlFolderPersistence.findByR_P(
			repositoryId, folderId);

		for (DLFolder dlFolder : dlFolders) {
			folderIds.add(dlFolder.getFolderId());

			getRepositorySubfolderIds(
				folderIds, dlFolder.getRepositoryId(), dlFolder.getFolderId());
		}
	}

	@Override
	public String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name,
		int count) {

		DLFolder dlFolder = dlFolderLocalService.fetchFolder(
			groupId, parentFolderId, name);

		if (dlFolder == null) {
			FileEntry fileEntry = FileEntryUtil.fetchByR_F_T(
				groupId, parentFolderId, name);

			if (fileEntry == null) {
				return name;
			}
		}
		else if (Validator.isNotNull(uuid) && uuid.equals(dlFolder.getUuid())) {
			return name;
		}

		name = StringUtil.appendParentheticalSuffix(name, count);

		return getUniqueFolderName(
			uuid, groupId, parentFolderId, name, ++count);
	}

	@Override
	public boolean hasFolderLock(long userId, long folderId) {
		return LockManagerUtil.hasLock(
			userId, DLFolder.class.getName(), folderId);
	}

	@Override
	public boolean hasInheritableLock(long folderId) {
		Lock lock = LockManagerUtil.fetchLock(
			DLFolder.class.getName(), folderId);

		if (lock == null) {
			return false;
		}

		return lock.isInheritable();
	}

	@Override
	public Lock lockFolder(long userId, long folderId) throws PortalException {
		return lockFolder(
			userId, folderId, null, false, DLFolderImpl.LOCK_EXPIRATION_TIME);
	}

	@Override
	public Lock lockFolder(
			long userId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException {

		if ((expirationTime <= 0) ||
			(expirationTime > DLFolderImpl.LOCK_EXPIRATION_TIME)) {

			expirationTime = DLFolderImpl.LOCK_EXPIRATION_TIME;
		}

		return LockManagerUtil.lock(
			userId, DLFolder.class.getName(), folderId, owner, inheritable,
			expirationTime);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DLFolder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		parentFolderId = getParentFolderId(dlFolder, parentFolderId);

		if (dlFolder.getParentFolderId() == parentFolderId) {
			return dlFolder;
		}

		boolean hasLock = hasFolderLock(userId, folderId);

		Lock lock = null;

		if (!hasLock) {

			// Lock

			lock = lockFolder(userId, folderId);
		}

		try {
			validateFolder(
				dlFolder.getFolderId(), dlFolder.getGroupId(), parentFolderId,
				dlFolder.getName());

			dlFolder.setParentFolderId(parentFolderId);
			dlFolder.setTreePath(dlFolder.buildTreePath());
			dlFolder.setExpandoBridgeAttributes(serviceContext);

			dlFolder = dlFolderPersistence.update(dlFolder);

			rebuildTree(
				dlFolder.getCompanyId(), folderId, dlFolder.getTreePath(),
				true);

			return dlFolder;
		}
		finally {
			if (!hasLock) {

				// Unlock

				unlockFolder(folderId, lock.getUuid());
			}
		}
	}

	@Override
	public void rebuildTree(long companyId) throws PortalException {
		rebuildTree(
			companyId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringPool.SLASH, false);
	}

	@Override
	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException {

		TreePathUtil.rebuildTree(
			companyId, parentFolderId, parentTreePath,
			new TreeModelTasksAdapter<DLFolder>() {

				@Override
				public List<DLFolder> findTreeModels(
					long previousId, long companyId, long parentPrimaryKey,
					int size) {

					return dlFolderPersistence.findByGtF_C_P(
						previousId, companyId, parentPrimaryKey,
						QueryUtil.ALL_POS, size,
						FolderIdComparator.getInstance(true));
				}

				@Override
				public void rebuildDependentModelsTreePaths(
						long parentPrimaryKey, String treePath)
					throws PortalException {

					_dlFileEntryLocalService.setTreePaths(
						parentPrimaryKey, treePath, false);
					_dlFileShortcutLocalService.setTreePaths(
						parentPrimaryKey, treePath);
					_dlFileVersionLocalService.setTreePaths(
						parentPrimaryKey, treePath);
				}

			});
	}

	@Override
	public void unlockFolder(
			long groupId, long parentFolderId, String name, String lockUuid)
		throws PortalException {

		DLFolder dlFolder = getFolder(groupId, parentFolderId, name);

		unlockFolder(dlFolder.getFolderId(), lockUuid);
	}

	@Override
	public void unlockFolder(long folderId, String lockUuid)
		throws PortalException {

		if (Validator.isNotNull(lockUuid)) {
			Lock lock = LockManagerUtil.fetchLock(
				DLFolder.class.getName(), folderId);

			if ((lock != null) && !lockUuid.equals(lock.getUuid())) {
				throw new InvalidLockException("UUIDs do not match");
			}
		}

		LockManagerUtil.unlock(DLFolder.class.getName(), folderId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DLFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException {

		boolean hasLock = hasFolderLock(serviceContext.getUserId(), folderId);

		Lock lock = null;

		if (!hasLock) {

			// Lock

			lock = lockFolder(
				serviceContext.getUserId(), folderId, null, false,
				DLFolderImpl.LOCK_EXPIRATION_TIME);
		}

		try {

			// File entry types

			DLFolder dlFolder = null;

			Set<Long> originalFileEntryTypeIds = new HashSet<>();

			if (folderId > DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				originalFileEntryTypeIds = getFileEntryTypeIds(
					dlFolderPersistence.getDLFileEntryTypes(folderId));

				dlFolder = dlFolderLocalService.updateFolderAndFileEntryTypes(
					serviceContext.getUserId(), folderId, parentFolderId, name,
					description, defaultFileEntryTypeId, fileEntryTypeIds,
					restrictionType, serviceContext);

				if (!ExportImportThreadLocal.isImportInProcess()) {
					_dlFileEntryTypeLocalService.cascadeFileEntryTypes(
						serviceContext.getUserId(), dlFolder);
				}
			}

			// Workflow definitions

			_updateWorkflowDefinitionLinks(
				folderId, fileEntryTypeIds, restrictionType, serviceContext,
				originalFileEntryTypeIds);

			return dlFolder;
		}
		finally {
			if (!hasLock) {

				// Unlock

				unlockFolder(folderId, lock.getUuid());
			}
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DLFolder updateFolder(
			long folderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException {

		long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = getDLFolder(folderId);

			parentFolderId = dlFolder.getParentFolderId();
		}

		return updateFolder(
			folderId, parentFolderId, name, description, defaultFileEntryTypeId,
			fileEntryTypeIds, restrictionType, serviceContext);
	}

	@Override
	public DLFolder updateFolderAndFileEntryTypes(
			long userId, long folderId, long parentFolderId, String name,
			String description, long defaultFileEntryTypeId,
			List<Long> fileEntryTypeIds, int restrictionType,
			ServiceContext serviceContext)
		throws PortalException {

		if ((restrictionType ==
				DLFolderConstants.
					RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) &&
			fileEntryTypeIds.isEmpty()) {

			throw new RequiredFileEntryTypeException(
				"File entry type IDs is empty");
		}

		boolean hasLock = hasFolderLock(userId, folderId);

		Lock lock = null;

		if (!hasLock) {

			// Lock

			lock = lockFolder(
				userId, folderId, null, false,
				DLFolderImpl.LOCK_EXPIRATION_TIME);
		}

		try {

			// Folder

			if (restrictionType == DLFolderConstants.RESTRICTION_TYPE_INHERIT) {
				fileEntryTypeIds = Collections.emptyList();
			}

			DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

			parentFolderId = getParentFolderId(dlFolder, parentFolderId);

			validateFolder(
				folderId, dlFolder.getGroupId(), parentFolderId, name);

			long oldParentFolderId = dlFolder.getParentFolderId();

			if (oldParentFolderId != parentFolderId) {
				dlFolder.setParentFolderId(parentFolderId);
				dlFolder.setTreePath(dlFolder.buildTreePath());
			}

			dlFolder.setName(name);
			dlFolder.setDescription(description);
			dlFolder.setDefaultFileEntryTypeId(defaultFileEntryTypeId);
			dlFolder.setRestrictionType(restrictionType);
			dlFolder.setExpandoBridgeAttributes(serviceContext);

			dlFolder = dlFolderPersistence.update(dlFolder);

			// File entry types

			if (fileEntryTypeIds != null) {
				_dlFileEntryTypeLocalService.updateFolderFileEntryTypes(
					dlFolder, fileEntryTypeIds, defaultFileEntryTypeId,
					serviceContext);
			}

			if (oldParentFolderId != parentFolderId) {
				rebuildTree(
					dlFolder.getCompanyId(), folderId, dlFolder.getTreePath(),
					true);
			}

			return dlFolder;
		}
		finally {
			if (!hasLock) {

				// Unlock

				unlockFolder(folderId, lock.getUuid());
			}
		}
	}

	@BufferedIncrement(
		configuration = "DLFolderEntry",
		incrementClass = DateOverrideIncrement.class
	)
	@Override
	public void updateLastPostDate(long folderId, Date lastPostDate)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess() ||
			(folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ||
			(lastPostDate == null)) {

			return;
		}

		DLFolder dlFolder = dlFolderPersistence.fetchByPrimaryKey(folderId);

		if ((dlFolder == null) ||
			lastPostDate.before(dlFolder.getLastPostDate()) ||
			(!CTCollectionThreadLocal.isProductionMode() &&
			 dlFolder.isInHiddenFolder())) {

			return;
		}

		dlFolder.setModifiedDate(dlFolder.getModifiedDate());
		dlFolder.setLastPostDate(lastPostDate);

		dlFolderPersistence.update(dlFolder);
	}

	@Override
	public DLFolder updateStatus(
			long userId, long folderId, int status,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		// Folder

		User user = _userPersistence.findByPrimaryKey(userId);

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		int oldStatus = dlFolder.getStatus();

		dlFolder.setStatus(status);
		dlFolder.setStatusByUserId(user.getUserId());
		dlFolder.setStatusByUserName(user.getFullName());
		dlFolder.setStatusDate(new Date());

		dlFolder = dlFolderPersistence.update(dlFolder);

		// Asset

		if (status == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateVisible(
				DLFolder.class.getName(), dlFolder.getFolderId(), true);
		}
		else if (status == WorkflowConstants.STATUS_IN_TRASH) {
			_assetEntryLocalService.updateVisible(
				DLFolder.class.getName(), dlFolder.getFolderId(), false);
		}

		// Indexer

		if (((status == WorkflowConstants.STATUS_APPROVED) ||
			 (status == WorkflowConstants.STATUS_IN_TRASH) ||
			 (oldStatus == WorkflowConstants.STATUS_IN_TRASH)) &&
			((serviceContext == null) || serviceContext.isIndexingEnabled())) {

			Indexer<DLFolder> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				DLFolder.class);

			indexer.reindex(dlFolder);
		}

		return dlFolder;
	}

	@Override
	public boolean verifyInheritableLock(long folderId, String lockUuid)
		throws PortalException {

		boolean verified = false;

		try {
			Lock lock = LockManagerUtil.getLock(
				DLFolder.class.getName(), folderId);

			if (!lock.isInheritable()) {
				throw new NoSuchLockException("{folderId=" + folderId + "}");
			}

			String uuid = lock.getUuid();

			if (uuid.equals(lockUuid)) {
				verified = true;
			}
		}
		catch (ExpiredLockException expiredLockException) {
			throw new NoSuchLockException(expiredLockException);
		}

		return verified;
	}

	protected static long getFolderId(
		DLFolderPersistence dlFolderPersistence, long companyId,
		long folderId) {

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			// Ensure folder exists and belongs to the proper company

			DLFolder dlFolder = dlFolderPersistence.fetchByPrimaryKey(folderId);

			if ((dlFolder == null) || (companyId != dlFolder.getCompanyId())) {
				folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}
		}

		return folderId;
	}

	protected void addFolderResources(
			DLFolder dlFolder, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			dlFolder.getCompanyId(), dlFolder.getGroupId(),
			dlFolder.getUserId(), DLFolder.class.getName(),
			dlFolder.getFolderId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	protected void addFolderResources(
			DLFolder dlFolder, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			dlFolder.getCompanyId(), dlFolder.getGroupId(),
			dlFolder.getUserId(), DLFolder.class.getName(),
			dlFolder.getFolderId(), modelPermissions);
	}

	protected void addFolderResources(
			long folderId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		addFolderResources(dlFolder, addGroupPermissions, addGuestPermissions);
	}

	protected void addFolderResources(
			long folderId, ModelPermissions modelPermissions)
		throws PortalException {

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		addFolderResources(dlFolder, modelPermissions);
	}

	protected void deleteFolderDependencies(
			DLFolder dlFolder, boolean includeTrashedEntries)
		throws PortalException {

		// WebDAVProps

		WebDAVProps webDAVProps = _webDAVPropsPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(DLFolder.class),
			dlFolder.getFolderId());

		if (webDAVProps != null) {
			_webDAVPropsLocalService.deleteWebDAVProps(webDAVProps);
		}

		// File entries

		_dlFileEntryLocalService.deleteFileEntries(
			dlFolder.getGroupId(), dlFolder.getFolderId(),
			includeTrashedEntries);

		// File entry types

		List<Long> fileEntryTypeIds = new ArrayList<>();

		for (DLFileEntryType dlFileEntryType :
				_dlFileEntryTypeLocalService.getDLFolderDLFileEntryTypes(
					dlFolder.getFolderId())) {

			fileEntryTypeIds.add(dlFileEntryType.getFileEntryTypeId());
		}

		if (fileEntryTypeIds.isEmpty()) {
			fileEntryTypeIds.add(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL);
		}

		_dlFileEntryTypeLocalService.unsetFolderFileEntryTypes(
			dlFolder.getFolderId());

		// File shortcuts

		_dlFileShortcutLocalService.deleteFileShortcuts(
			dlFolder.getGroupId(), dlFolder.getFolderId(),
			includeTrashedEntries);

		// Expando

		_expandoRowLocalService.deleteRows(dlFolder.getFolderId());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			DLFolder.class.getName(), dlFolder.getFolderId());

		// Folder

		dlFolderPersistence.remove(dlFolder);

		// Resources

		_resourceLocalService.deleteResource(
			dlFolder.getCompanyId(), DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, dlFolder.getFolderId());

		// Directory

		if (includeTrashedEntries) {
			DLStoreUtil.deleteDirectory(
				dlFolder.getCompanyId(), dlFolder.getFolderId(),
				StringPool.BLANK);
		}

		// Workflow

		for (long fileEntryTypeId : fileEntryTypeIds) {
			WorkflowDefinitionLink workflowDefinitionLink =
				_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
					dlFolder.getCompanyId(), dlFolder.getGroupId(),
					DLFolder.class.getName(), dlFolder.getFolderId(),
					fileEntryTypeId);

			if (workflowDefinitionLink != null) {
				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(workflowDefinitionLink);
			}
		}
	}

	protected void deleteSubfolders(
			DLFolder dlFolder, boolean includeTrashedEntries)
		throws PortalException {

		try {
			RepositoryEventTrigger repositoryEventTrigger =
				RepositoryUtil.getRepositoryEventTrigger(
					dlFolder.getRepositoryId());

			List<DLFolder> dlFolders = dlFolderPersistence.findByG_P(
				dlFolder.getGroupId(), dlFolder.getFolderId());

			for (DLFolder curDLFolder : dlFolders) {
				TrashHelper trashHelper = _trashHelperSnapshot.get();

				if (includeTrashedEntries ||
					!trashHelper.isInTrashExplicitly(curDLFolder)) {

					repositoryEventTrigger.trigger(
						RepositoryEventType.Delete.class, Folder.class,
						new LiferayFolder(curDLFolder));

					dlFolderLocalService.deleteFolder(
						curDLFolder, includeTrashedEntries);
				}
			}
		}
		catch (UndeployedExternalRepositoryException
					undeployedExternalRepositoryException) {

			if (_log.isWarnEnabled()) {
				_log.warn(undeployedExternalRepositoryException);
			}
		}
	}

	protected Set<Long> getFileEntryTypeIds(
		List<DLFileEntryType> dlFileEntryTypes) {

		Set<Long> fileEntryTypeIds = new HashSet<>();

		for (DLFileEntryType dlFileEntryType : dlFileEntryTypes) {
			fileEntryTypeIds.add(dlFileEntryType.getFileEntryTypeId());
		}

		return fileEntryTypeIds;
	}

	protected long getParentFolderId(DLFolder dlFolder, long parentFolderId)
		throws PortalException {

		parentFolderId = getParentFolderId(
			dlFolder.getGroupId(), dlFolder.getRepositoryId(), parentFolderId);

		if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return parentFolderId;
		}

		if (dlFolder.getFolderId() == parentFolderId) {
			throw new InvalidFolderException(
				InvalidFolderException.CANNOT_MOVE_INTO_ITSELF, parentFolderId);
		}

		List<Long> subfolderIds = new ArrayList<>();

		getGroupSubfolderIds(
			subfolderIds, dlFolder.getGroupId(), dlFolder.getFolderId());

		if (subfolderIds.contains(parentFolderId)) {
			throw new InvalidFolderException(
				InvalidFolderException.CANNOT_MOVE_INTO_CHILD_FOLDER,
				parentFolderId);
		}

		return parentFolderId;
	}

	protected long getParentFolderId(
			long groupId, long repositoryId, long parentFolderId)
		throws PortalException {

		if (parentFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return parentFolderId;
		}

		DLFolder parentDLFolder = dlFolderPersistence.findByPrimaryKey(
			parentFolderId);

		if (parentDLFolder.getGroupId() != groupId) {
			throw new NoSuchFolderException(
				String.format(
					"No folder exists with the primary key %s in group %s",
					parentFolderId, groupId));
		}

		if ((parentDLFolder.getRepositoryId() != repositoryId) &&
			(parentDLFolder.getRepositoryId() != groupId)) {

			Repository repository = _repositoryPersistence.findByPrimaryKey(
				repositoryId);

			if (repository.getGroupId() != parentDLFolder.getGroupId()) {
				throw new NoSuchFolderException(
					String.format(
						"No folder exists with the primary key %s in " +
							"repository %s",
						parentFolderId, repositoryId));
			}
		}

		return parentDLFolder.getFolderId();
	}

	protected void validateFolder(
			long folderId, long groupId, long parentFolderId, String name)
		throws PortalException {

		validateFolderName(name);

		DLValidatorUtil.validateDirectoryName(name);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchFileEntry(
			groupId, parentFolderId, name);

		if (dlFileEntry != null) {
			throw new DuplicateFileEntryException(
				"A file entry already exists with title " + name);
		}

		DLFolder dlFolder = dlFolderPersistence.fetchByG_P_N(
			groupId, parentFolderId, name);

		if ((dlFolder != null) && (dlFolder.getFolderId() != folderId)) {
			throw new DuplicateFolderNameException(
				"A folder already exists with name " + name);
		}
	}

	protected void validateFolder(
			long groupId, long parentFolderId, String name)
		throws PortalException {

		long folderId = 0;

		validateFolder(folderId, groupId, parentFolderId, name);
	}

	protected void validateFolderName(String folderName)
		throws PortalException {

		if (folderName.contains(StringPool.SLASH)) {
			throw new FolderNameException(
				StringBundler.concat(
					"Folder name ", folderName,
					" is invalid because it contains a /"));
		}
	}

	private void _updateWorkflowDefinitionLinks(
			long folderId, List<Long> fileEntryTypeIds, int restrictionType,
			ServiceContext serviceContext, Set<Long> originalFileEntryTypeIds)
		throws PortalException {

		if (!GetterUtil.getBoolean(
				serviceContext.getAttribute("updateWorkflowDefinitionLinks"),
				true)) {

			return;
		}

		List<ObjectValuePair<Long, String>> workflowDefinitionOVPs =
			new ArrayList<>();

		if (restrictionType ==
				DLFolderConstants.
					RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) {

			workflowDefinitionOVPs.add(
				new ObjectValuePair<Long, String>(
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL,
					StringPool.BLANK));

			for (long fileEntryTypeId : fileEntryTypeIds) {
				String workflowDefinition = ParamUtil.getString(
					serviceContext, "workflowDefinition" + fileEntryTypeId);

				workflowDefinitionOVPs.add(
					new ObjectValuePair<Long, String>(
						fileEntryTypeId, workflowDefinition));
			}
		}
		else if (restrictionType ==
					DLFolderConstants.RESTRICTION_TYPE_INHERIT) {

			if (originalFileEntryTypeIds.isEmpty()) {
				originalFileEntryTypeIds.add(
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL);
			}

			for (long originalFileEntryTypeId : originalFileEntryTypeIds) {
				workflowDefinitionOVPs.add(
					new ObjectValuePair<Long, String>(
						originalFileEntryTypeId, StringPool.BLANK));
			}
		}
		else if (restrictionType ==
					DLFolderConstants.RESTRICTION_TYPE_WORKFLOW) {

			String workflowDefinition = ParamUtil.getString(
				serviceContext,
				"workflowDefinition" +
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL);

			workflowDefinitionOVPs.add(
				new ObjectValuePair<Long, String>(
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL,
					workflowDefinition));

			for (long originalFileEntryTypeId : originalFileEntryTypeIds) {
				workflowDefinitionOVPs.add(
					new ObjectValuePair<Long, String>(
						originalFileEntryTypeId, StringPool.BLANK));
			}
		}

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLinks(
			serviceContext.getUserId(), serviceContext.getCompanyId(),
			serviceContext.getScopeGroupId(), DLFolder.class.getName(),
			folderId, workflowDefinitionOVPs);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderLocalServiceImpl.class);

	private static final Snapshot<TrashHelper> _trashHelperSnapshot =
		new Snapshot<>(DLFolderLocalServiceImpl.class, TrashHelper.class);

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = DLFileEntryLocalService.class)
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@BeanReference(type = DLFileEntryTypeLocalService.class)
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@BeanReference(type = DLFileShortcutLocalService.class)
	private DLFileShortcutLocalService _dlFileShortcutLocalService;

	@BeanReference(type = DLFileVersionLocalService.class)
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@BeanReference(type = ExpandoRowLocalService.class)
	private ExpandoRowLocalService _expandoRowLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = RatingsStatsLocalService.class)
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@BeanReference(type = RepositoryPersistence.class)
	private RepositoryPersistence _repositoryPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = WebDAVPropsLocalService.class)
	private WebDAVPropsLocalService _webDAVPropsLocalService;

	@BeanReference(type = WebDAVPropsPersistence.class)
	private WebDAVPropsPersistence _webDAVPropsPersistence;

	@BeanReference(type = WorkflowDefinitionLinkLocalService.class)
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}
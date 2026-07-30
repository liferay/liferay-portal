/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderTable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.documentlibrary.DLGroupServiceSettings;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;
import com.liferay.portlet.documentlibrary.service.base.DLFolderServiceBaseImpl;
import com.liferay.ratings.kernel.model.RatingsEntryTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class DLFolderServiceImpl extends DLFolderServiceBaseImpl {

	@Override
	public DLFolder addFolder(
			String externalReferenceCode, long groupId, long repositoryId,
			boolean mountPoint, long parentFolderId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<DLFolder>getModelResourcePermission(DLFolder.class.getName()),
			getPermissionChecker(), groupId, parentFolderId,
			ActionKeys.ADD_FOLDER);

		return dlFolderLocalService.addFolder(
			externalReferenceCode, getUserId(), groupId, repositoryId,
			mountPoint, parentFolderId, name, description, false,
			serviceContext);
	}

	@Override
	public void deleteFolder(long folderId) throws PortalException {
		deleteFolder(folderId, true);
	}

	@Override
	public void deleteFolder(long folderId, boolean includeTrashedEntries)
		throws PortalException {

		ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				DLFolder.class.getName());

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		dlFolderModelResourcePermission.check(
			getPermissionChecker(), dlFolder, ActionKeys.DELETE);

		dlFolderLocalService.deleteFolder(
			getUserId(), folderId, includeTrashedEntries);
	}

	@Override
	public void deleteFolder(long groupId, long parentFolderId, String name)
		throws PortalException {

		DLFolder dlFolder = getFolder(groupId, parentFolderId, name);

		deleteFolder(dlFolder.getFolderId());
	}

	@Override
	public DLFolder getDLFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				DLFolder.class.getName());

		DLFolder dlFolder = dlFolderPersistence.findByERC_G(
			externalReferenceCode, groupId);

		dlFolderModelResourcePermission.check(
			getPermissionChecker(), dlFolder, ActionKeys.VIEW);

		return dlFolder;
	}

	@Override
	public List<Object> getFileEntriesAndFileShortcuts(
			long groupId, long folderId, int status, int start, int end)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, null);

		return dlFolderFinder.filterFindFE_FS_ByG_F(
			groupId, folderId, queryDefinition);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, int status)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return 0;
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return dlFolderFinder.filterCountFE_FS_ByG_F(
			groupId, folderId, queryDefinition);
	}

	@Override
	public int getFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes, int status)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return 0;
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return dlFolderFinder.filterCountFE_FS_ByG_F_M(
			groupId, folderId, mimeTypes, queryDefinition);
	}

	@Override
	public DLFolder getFolder(long folderId) throws PortalException {
		ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				DLFolder.class.getName());

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		dlFolderModelResourcePermission.check(
			getPermissionChecker(), dlFolder, ActionKeys.VIEW);

		return dlFolder;
	}

	@Override
	public DLFolder getFolder(long groupId, long parentFolderId, String name)
		throws PortalException {

		ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				DLFolder.class.getName());

		DLFolder dlFolder = dlFolderPersistence.findByG_P_N(
			groupId, parentFolderId, name);

		dlFolderModelResourcePermission.check(
			getPermissionChecker(), dlFolder, ActionKeys.VIEW);

		return dlFolder;
	}

	@Override
	public List<Long> getFolderIds(long groupId, long folderId)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		List<Long> folderIds = getSubfolderIds(groupId, folderId, true);

		folderIds.add(0, folderId);

		return folderIds;
	}

	@Override
	public List<DLFolder> getFolders(
			long groupId, double score, int start, int end)
		throws PortalException {

		return dlFolderPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				DLFolderTable.INSTANCE
			).from(
				DLFolderTable.INSTANCE
			).innerJoinON(
				RatingsEntryTable.INSTANCE,
				RatingsEntryTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						DLFolder.class.getName())
				).and(
					RatingsEntryTable.INSTANCE.classPK.eq(
						DLFolderTable.INSTANCE.folderId)
				)
			).where(
				DLFolderTable.INSTANCE.groupId.eq(
					groupId
				).and(
					RatingsEntryTable.INSTANCE.userId.eq(getUserId())
				).and(
					RatingsEntryTable.INSTANCE.score.gte(score)
				).and(
					InlineSQLHelperUtil.getPermissionWherePredicate(
						DLFolder.class, DLFolderTable.INSTANCE.folderId)
				)
			).orderBy(
				RatingsEntryTable.INSTANCE.modifiedDate.descending()
			).limit(
				start, end
			));
	}

	@Override
	public List<DLFolder> getFolders(
			long groupId, long parentFolderId, boolean includeMountfolders,
			int status, int start, int end,
			OrderByComparator<DLFolder> orderByComparator)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, parentFolderId,
				ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		if (includeMountfolders) {
			return dlFolderPersistence.filterFindByG_P_H_S(
				groupId, parentFolderId, false, status, start, end,
				orderByComparator);
		}

		return dlFolderPersistence.filterFindByG_M_P_H_S(
			groupId, false, parentFolderId, false, status, start, end,
			orderByComparator);
	}

	@Override
	public List<DLFolder> getFolders(
			long groupId, long parentFolderId, int start, int end,
			OrderByComparator<DLFolder> orderByComparator)
		throws PortalException {

		return getFolders(
			groupId, parentFolderId, true, WorkflowConstants.STATUS_APPROVED,
			start, end, orderByComparator);
	}

	@Override
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, boolean includeMountFolders,
			int status, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, (OrderByComparator<Object>)orderByComparator);

		return dlFolderFinder.filterFindF_FE_FS_ByG_F_M_M(
			groupId, folderId, null, includeMountFolders, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, int status, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, (OrderByComparator<Object>)orderByComparator);

		return dlFolderFinder.filterFindF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, QueryDefinition<?> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		return dlFolderFinder.filterFindF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, String[] mimeTypes,
			long fileEntryTypeId, boolean includeMountFolders, int status,
			int start, int end, OrderByComparator<?> orderByComparator)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, (OrderByComparator<Object>)orderByComparator);

		return dlFolderFinder.filterFindF_FE_FS_ByG_F_M_FETI_M(
			groupId, folderId, mimeTypes, fileEntryTypeId, includeMountFolders,
			queryDefinition);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, int status,
			boolean includeMountFolders)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return 0;
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return dlFolderFinder.filterCountF_FE_FS_ByG_F_M_M(
			groupId, folderId, null, includeMountFolders, queryDefinition);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getFoldersAndFileEntriesAndFileShortcutsCount(long, long,
	 *             String[], boolean, int)}
	 */
	@Deprecated
	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders)
		throws PortalException {

		return getFoldersAndFileEntriesAndFileShortcutsCount(
			groupId, folderId, mimeTypes, includeMountFolders, status);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, int status)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return 0;
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return dlFolderFinder.filterCountF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, QueryDefinition<?> queryDefinition)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return 0;
		}

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return dlFolderFinder.filterCountF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	@Override
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes,
			long fileEntryTypeId, boolean includeMountFolders, int status)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return 0;
		}

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return dlFolderFinder.filterCountF_FE_FS_ByG_F_M_FETI_M(
			groupId, folderId, mimeTypes, fileEntryTypeId, includeMountFolders,
			queryDefinition);
	}

	@Override
	public int getFoldersCount(long groupId, double score)
		throws PortalException {

		return dlFolderPersistence.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				DLFolderTable.INSTANCE.folderId
			).from(
				DLFolderTable.INSTANCE
			).innerJoinON(
				RatingsEntryTable.INSTANCE,
				RatingsEntryTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						DLFolder.class.getName())
				).and(
					RatingsEntryTable.INSTANCE.classPK.eq(
						DLFolderTable.INSTANCE.folderId)
				)
			).where(
				DLFolderTable.INSTANCE.groupId.eq(
					groupId
				).and(
					RatingsEntryTable.INSTANCE.userId.eq(getUserId())
				).and(
					RatingsEntryTable.INSTANCE.score.gte(score)
				).and(
					InlineSQLHelperUtil.getPermissionWherePredicate(
						DLFolder.class, DLFolderTable.INSTANCE.folderId)
				)
			));
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId)
		throws PortalException {

		return getFoldersCount(
			groupId, parentFolderId, true, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getFoldersCount(
			long groupId, long parentFolderId, boolean includeMountfolders,
			int status)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, parentFolderId,
				ActionKeys.VIEW)) {

			return 0;
		}

		if (includeMountfolders) {
			return dlFolderPersistence.filterCountByG_P_H_S(
				groupId, parentFolderId, false, status);
		}

		return dlFolderPersistence.filterCountByG_M_P_H_S(
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
			boolean includeMountfolders)
		throws PortalException {

		return getFoldersCount(
			groupId, parentFolderId, includeMountfolders, status);
	}

	@Override
	public List<DLFolder> getMountFolders(
			long groupId, long parentFolderId, int start, int end,
			OrderByComparator<DLFolder> orderByComparator)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, parentFolderId,
				ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		DLGroupServiceSettings dlGroupServiceSettings =
			DLGroupServiceSettings.getInstance(groupId);

		if (dlGroupServiceSettings.isShowHiddenMountFolders()) {
			return dlFolderPersistence.filterFindByG_M_P(
				groupId, true, parentFolderId, start, end, orderByComparator);
		}

		return dlFolderPersistence.filterFindByG_M_P_H(
			groupId, true, parentFolderId, false, start, end,
			orderByComparator);
	}

	@Override
	public int getMountFoldersCount(long groupId, long parentFolderId)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, parentFolderId,
				ActionKeys.VIEW)) {

			return 0;
		}

		return dlFolderPersistence.filterCountByG_M_P_H(
			groupId, true, parentFolderId, false);
	}

	@Override
	public void getSubfolderIds(
			List<Long> folderIds, long groupId, long folderId, boolean recurse)
		throws PortalException {

		if (!ModelResourcePermissionUtil.contains(
				ModelResourcePermissionRegistryUtil.
					<DLFolder>getModelResourcePermission(
						DLFolder.class.getName()),
				getPermissionChecker(), groupId, folderId, ActionKeys.VIEW)) {

			return;
		}

		List<DLFolder> dlFolders = dlFolderPersistence.filterFindByG_P_H_S(
			groupId, folderId, false, WorkflowConstants.STATUS_APPROVED);

		for (DLFolder dlFolder : dlFolders) {
			if (dlFolder.isInHiddenFolder() || dlFolder.isInTrash()) {
				continue;
			}

			folderIds.add(dlFolder.getFolderId());

			if (recurse) {
				getSubfolderIds(
					folderIds, dlFolder.getGroupId(), dlFolder.getFolderId(),
					recurse);
			}
		}
	}

	@Override
	public List<Long> getSubfolderIds(
			long groupId, long folderId, boolean recurse)
		throws PortalException {

		List<Long> folderIds = new ArrayList<>();

		getSubfolderIds(folderIds, groupId, folderId, recurse);

		return folderIds;
	}

	@Override
	public boolean hasFolderLock(long folderId) throws PortalException {
		return LockManagerUtil.hasLock(
			getUserId(), DLFolder.class.getName(), folderId);
	}

	@Override
	public boolean hasInheritableLock(long folderId) throws PortalException {
		return dlFolderLocalService.hasInheritableLock(folderId);
	}

	@Override
	public boolean isFolderLocked(long folderId) {
		return LockManagerUtil.isLocked(DLFolder.class.getName(), folderId);
	}

	@Override
	public Lock lockFolder(long folderId) throws PortalException {
		return lockFolder(
			folderId, null, false, DLFolderImpl.LOCK_EXPIRATION_TIME);
	}

	@Override
	public Lock lockFolder(
			long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException {

		ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				DLFolder.class.getName());

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		dlFolderModelResourcePermission.check(
			getPermissionChecker(), dlFolder, ActionKeys.UPDATE);

		return dlFolderLocalService.lockFolder(
			getUserId(), folderId, owner, inheritable, expirationTime);
	}

	@Override
	public DLFolder moveFolder(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				DLFolder.class.getName());

		PermissionChecker permissionChecker = getPermissionChecker();

		DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

		dlFolderModelResourcePermission.check(
			permissionChecker, dlFolder, ActionKeys.UPDATE);

		ModelResourcePermissionUtil.check(
			dlFolderModelResourcePermission, permissionChecker,
			serviceContext.getScopeGroupId(), parentFolderId,
			ActionKeys.ADD_FOLDER);

		return dlFolderLocalService.moveFolder(
			getUserId(), folderId, parentFolderId, serviceContext);
	}

	@Override
	public Lock refreshFolderLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		return LockManagerUtil.refresh(lockUuid, companyId, expirationTime);
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

		DLFolder dlFolder = dlFolderPersistence.fetchByPrimaryKey(folderId);

		if (dlFolder != null) {
			ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFolder.class.getName());

			dlFolderModelResourcePermission.check(
				getPermissionChecker(), dlFolder, ActionKeys.UPDATE);
		}

		dlFolderLocalService.unlockFolder(folderId, lockUuid);
	}

	@Override
	public DLFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<DLFolder> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.
				<DLFolder>getModelResourcePermission(DLFolder.class.getName());
		PermissionChecker permissionChecker = getPermissionChecker();

		if (ModelResourcePermissionUtil.contains(
				modelResourcePermission, permissionChecker,
				serviceContext.getScopeGroupId(), folderId,
				ActionKeys.ADVANCED_UPDATE) ||
			ModelResourcePermissionUtil.contains(
				modelResourcePermission, permissionChecker,
				serviceContext.getScopeGroupId(), folderId,
				ActionKeys.UPDATE)) {

			serviceContext.setUserId(getUserId());

			return dlFolderLocalService.updateFolder(
				folderId, parentFolderId, name, description,
				defaultFileEntryTypeId, fileEntryTypeIds, restrictionType,
				serviceContext);
		}

		throw new PrincipalException.MustHavePermission(
			permissionChecker, DLFolder.class.getName(), folderId,
			ActionKeys.ADVANCED_UPDATE, ActionKeys.UPDATE);
	}

	@Override
	public DLFolder updateFolder(
			long folderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<DLFolder> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.
				<DLFolder>getModelResourcePermission(DLFolder.class.getName());
		PermissionChecker permissionChecker = getPermissionChecker();

		if (ModelResourcePermissionUtil.contains(
				modelResourcePermission, permissionChecker,
				serviceContext.getScopeGroupId(), folderId,
				ActionKeys.ADVANCED_UPDATE) ||
			ModelResourcePermissionUtil.contains(
				modelResourcePermission, permissionChecker,
				serviceContext.getScopeGroupId(), folderId,
				ActionKeys.UPDATE)) {

			serviceContext.setUserId(getUserId());

			return dlFolderLocalService.updateFolder(
				folderId, name, description, defaultFileEntryTypeId,
				fileEntryTypeIds, restrictionType, serviceContext);
		}

		throw new PrincipalException.MustHavePermission(
			permissionChecker, DLFolder.class.getName(), folderId,
			ActionKeys.ADVANCED_UPDATE, ActionKeys.UPDATE);
	}

	@Override
	public boolean verifyInheritableLock(long folderId, String lockUuid)
		throws PortalException {

		return dlFolderLocalService.verifyInheritableLock(folderId, lockUuid);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.exception.FileEntryLockException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.base.DLFileEntryServiceBaseImpl;
import com.liferay.ratings.kernel.model.RatingsEntryTable;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides the remote service for accessing, adding, checking in/out, deleting,
 * locking/unlocking, moving, reverting, updating, and verifying document
 * library file entries. Its methods include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class DLFileEntryServiceImpl extends DLFileEntryServiceBaseImpl {

	@Override
	public DLFileEntry addFileEntry(
			String externalReferenceCode, long groupId, long repositoryId,
			long folderId, String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			long fileEntryTypeId, Map<String, DDMFormValues> ddmFormValuesMap,
			File file, InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.ADD_DOCUMENT);

		return dlFileEntryLocalService.addFileEntry(
			externalReferenceCode, getUserId(), groupId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			fileEntryTypeId, ddmFormValuesMap, file, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);
	}

	@Override
	public DLFileVersion cancelCheckOut(long fileEntryId)
		throws PortalException {

		boolean locked = LockManagerUtil.isLocked(
			DLFileEntry.class.getName(), fileEntryId);

		if (locked && !hasFileEntryLock(fileEntryId) &&
			!_hasOverrideCheckoutPermission(fileEntryId)) {

			throw new PrincipalException.MustHavePermission(
				getUserId(), DLFileEntry.class.getName(), fileEntryId,
				ActionKeys.OVERRIDE_CHECKOUT);
		}

		return dlFileEntryLocalService.cancelCheckOut(getUserId(), fileEntryId);
	}

	@Override
	public void checkInFileEntry(
			long fileEntryId, DLVersionNumberIncrease dlVersionNumberIncrease,
			String changeLog, ServiceContext serviceContext)
		throws PortalException {

		boolean locked = LockManagerUtil.isLocked(
			DLFileEntry.class.getName(), fileEntryId);

		if (locked && !hasFileEntryLock(fileEntryId)) {
			throw new FileEntryLockException.MustOwnLock();
		}

		dlFileEntryLocalService.checkInFileEntry(
			getUserId(), fileEntryId, dlVersionNumberIncrease, changeLog,
			serviceContext);
	}

	@Override
	public void checkInFileEntry(
			long fileEntryId, String lockUuid, ServiceContext serviceContext)
		throws PortalException {

		boolean locked = LockManagerUtil.isLocked(
			DLFileEntryConstants.getClassName(), fileEntryId);

		if (locked && !hasFileEntryLock(fileEntryId)) {
			throw new FileEntryLockException.MustOwnLock();
		}

		dlFileEntryLocalService.checkInFileEntry(
			getUserId(), fileEntryId, lockUuid, serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long fileEntryId, ServiceContext serviceContext)
		throws PortalException {

		return checkOutFileEntry(
			fileEntryId, null, DLFileEntryImpl.LOCK_EXPIRATION_TIME,
			serviceContext);
	}

	@Override
	public DLFileEntry checkOutFileEntry(
			long fileEntryId, String owner, long expirationTime,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.UPDATE);

		return dlFileEntryLocalService.checkOutFileEntry(
			getUserId(), fileEntryId, owner, expirationTime, serviceContext);
	}

	@Override
	public DLFileEntry copyFileEntry(
			long groupId, long repositoryId, long sourceFileEntryId,
			long targetFolderId, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), sourceFileEntryId, ActionKeys.VIEW);

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, targetFolderId,
			ActionKeys.ADD_DOCUMENT);

		return dlFileEntryLocalService.copyFileEntry(
			getUserId(), groupId, repositoryId, sourceFileEntryId,
			targetFolderId, null, serviceContext);
	}

	@Override
	public void deleteFileEntry(long fileEntryId) throws PortalException {
		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.DELETE);

		dlFileEntryLocalService.deleteFileEntry(getUserId(), fileEntryId);
	}

	@Override
	public void deleteFileEntry(long groupId, long folderId, String title)
		throws PortalException {

		DLFileEntry dlFileEntry = getFileEntry(groupId, folderId, title);

		deleteFileEntry(dlFileEntry.getFileEntryId());
	}

	@Override
	public void deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		ModelResourcePermission<DLFileEntry>
			dlFileEntryModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntry.class.getName());

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByERC_G(
			externalReferenceCode, groupId);

		dlFileEntryModelResourcePermission.check(
			getPermissionChecker(), dlFileEntry, ActionKeys.DELETE);

		dlFileEntryLocalService.deleteFileEntry(dlFileEntry);
	}

	@Override
	public void deleteFileVersion(long fileEntryId, String version)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.DELETE);

		dlFileEntryLocalService.deleteFileVersion(
			getUserId(), fileEntryId, version);
	}

	@Override
	public DLFileEntry fetchFileEntry(long fileEntryId) throws PortalException {
		DLFileEntry dlFileEntry = dlFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry != null) {
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(DLFileEntry.class.getName());

			dlFileEntryModelResourcePermission.check(
				getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);
		}

		return dlFileEntry;
	}

	@Override
	public DLFileEntry fetchFileEntry(long groupId, long folderId, String title)
		throws PortalException {

		DLFileEntry dlFileEntry = dlFileEntryLocalService.fetchFileEntry(
			groupId, folderId, title);

		if (dlFileEntry != null) {
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(DLFileEntry.class.getName());

			dlFileEntryModelResourcePermission.check(
				getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);
		}

		return dlFileEntry;
	}

	@Override
	public DLFileEntry fetchFileEntryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		DLFileEntry dlFileEntry =
			dlFileEntryLocalService.fetchFileEntryByExternalReferenceCode(
				groupId, externalReferenceCode);

		if (dlFileEntry != null) {
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(DLFileEntry.class.getName());

			dlFileEntryModelResourcePermission.check(
				getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);
		}

		return dlFileEntry;
	}

	@Override
	public DLFileEntry fetchFileEntryByImageId(long imageId)
		throws PortalException {

		DLFileEntry dlFileEntry =
			dlFileEntryLocalService.fetchFileEntryByAnyImageId(imageId);

		if (dlFileEntry != null) {
			ModelResourcePermission<DLFileEntry>
				dlFileEntryModelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(DLFileEntry.class.getName());

			dlFileEntryModelResourcePermission.check(
				getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);
		}

		return dlFileEntry;
	}

	@Override
	public InputStream getFileAsStream(long fileEntryId, String version)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.DOWNLOAD);

		return dlFileEntryLocalService.getFileAsStream(fileEntryId, version);
	}

	@Override
	public InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.DOWNLOAD);

		return dlFileEntryLocalService.getFileAsStream(
			fileEntryId, version, incrementCounter);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
			long groupId, double score, int start, int end)
		throws PortalException {

		return dlFileEntryPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				DLFileEntryTable.INSTANCE
			).from(
				DLFileEntryTable.INSTANCE
			).innerJoinON(
				RatingsEntryTable.INSTANCE,
				RatingsEntryTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						DLFileEntry.class.getName())
				).and(
					RatingsEntryTable.INSTANCE.classPK.eq(
						DLFileEntryTable.INSTANCE.fileEntryId)
				)
			).where(
				DLFileEntryTable.INSTANCE.groupId.eq(
					groupId
				).and(
					RatingsEntryTable.INSTANCE.userId.eq(getUserId())
				).and(
					RatingsEntryTable.INSTANCE.score.gte(score)
				).and(
					InlineSQLHelperUtil.getPermissionWherePredicate(
						DLFileEntry.class,
						DLFileEntryTable.INSTANCE.fileEntryId)
				)
			).orderBy(
				RatingsEntryTable.INSTANCE.modifiedDate.descending()
			).limit(
				start, end
			));
	}

	@Override
	public List<DLFileEntry> getFileEntries(
			long groupId, long folderId, int status, int start, int end,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.VIEW);

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folderId);

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			status, false, start, end, orderByComparator);

		return dlFileEntryFinder.filterFindByG_F(
			groupId, folderIds, queryDefinition);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
			long groupId, long folderId, int start, int end,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		return getFileEntries(
			groupId, folderId, WorkflowConstants.STATUS_APPROVED, start, end,
			orderByComparator);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
			long groupId, long folderId, long fileEntryTypeId, int start,
			int end, OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.VIEW);

		return dlFileEntryPersistence.filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, orderByComparator);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
			long groupId, long folderId, String[] mimeTypes, int status,
			int start, int end,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.VIEW);

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folderId);

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return dlFileEntryFinder.filterFindByG_U_F_M(
			groupId, 0, folderIds, mimeTypes, queryDefinition);
	}

	@Override
	public List<DLFileEntry> getFileEntries(
			long groupId, long folderId, String[] mimeTypes, int start, int end,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			getPermissionChecker(), groupId, folderId, ActionKeys.VIEW);

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folderId);

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_IN_TRASH, true, start, end,
			orderByComparator);

		return dlFileEntryFinder.filterFindByG_U_F_M(
			groupId, 0, folderIds, mimeTypes, queryDefinition);
	}

	@Override
	public int getFileEntriesCount(long groupId, double score)
		throws PortalException {

		return dlFileEntryPersistence.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				DLFileEntryTable.INSTANCE.fileEntryId
			).from(
				DLFileEntryTable.INSTANCE
			).innerJoinON(
				RatingsEntryTable.INSTANCE,
				RatingsEntryTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(
						DLFileEntry.class.getName())
				).and(
					RatingsEntryTable.INSTANCE.classPK.eq(
						DLFileEntryTable.INSTANCE.fileEntryId)
				)
			).where(
				DLFileEntryTable.INSTANCE.groupId.eq(
					groupId
				).and(
					RatingsEntryTable.INSTANCE.userId.eq(getUserId())
				).and(
					RatingsEntryTable.INSTANCE.score.gte(score)
				).and(
					InlineSQLHelperUtil.getPermissionWherePredicate(
						DLFileEntry.class,
						DLFileEntryTable.INSTANCE.fileEntryId)
				)
			));
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId) {
		return getFileEntriesCount(
			groupId, folderId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getFileEntriesCount(long groupId, long folderId, int status) {
		return dlFileEntryFinder.filterCountByG_F(
			groupId, ListUtil.fromArray(folderId),
			new QueryDefinition<>(status));
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long folderId, long fileEntryTypeId) {

		return dlFileEntryPersistence.filterCountByG_F_F(
			groupId, folderId, fileEntryTypeId);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long folderId, String[] mimeTypes) {

		return getFileEntriesCount(
			groupId, folderId, mimeTypes, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getFileEntriesCount(
		long groupId, long folderId, String[] mimeTypes, int status) {

		return dlFileEntryFinder.filterCountByG_U_F_M(
			groupId, 0, ListUtil.fromArray(folderId), mimeTypes,
			new QueryDefinition<>(status));
	}

	@Override
	public DLFileEntry getFileEntry(long fileEntryId) throws PortalException {
		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.VIEW);

		return dlFileEntryLocalService.getFileEntry(fileEntryId);
	}

	@Override
	public DLFileEntry getFileEntry(long groupId, long folderId, String title)
		throws PortalException {

		ModelResourcePermission<DLFileEntry>
			dlFileEntryModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntry.class.getName());

		DLFileEntry dlFileEntry = dlFileEntryLocalService.getFileEntry(
			groupId, folderId, title);

		dlFileEntryModelResourcePermission.check(
			getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);

		return dlFileEntry;
	}

	@Override
	public DLFileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		ModelResourcePermission<DLFileEntry>
			dlFileEntryModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntry.class.getName());

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByERC_G(
			externalReferenceCode, groupId);

		dlFileEntryModelResourcePermission.check(
			getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);

		return dlFileEntry;
	}

	@Override
	public DLFileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws PortalException {

		ModelResourcePermission<DLFileEntry>
			dlFileEntryModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntry.class.getName());

		DLFileEntry dlFileEntry =
			dlFileEntryLocalService.getFileEntryByFileName(
				groupId, folderId, fileName);

		dlFileEntryModelResourcePermission.check(
			getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);

		return dlFileEntry;
	}

	@Override
	public DLFileEntry getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		ModelResourcePermission<DLFileEntry>
			dlFileEntryModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntry.class.getName());

		DLFileEntry dlFileEntry = dlFileEntryPersistence.findByUUID_G(
			uuid, groupId);

		dlFileEntryModelResourcePermission.check(
			getPermissionChecker(), dlFileEntry, ActionKeys.VIEW);

		return dlFileEntry;
	}

	@Override
	public Lock getFileEntryLock(long fileEntryId) {
		try {
			return LockManagerUtil.getLock(
				DLFileEntry.class.getName(), fileEntryId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	@Override
	public int getFoldersFileEntriesCount(
		long groupId, List<Long> folderIds, int status) {

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			status);

		if (folderIds.size() <= DBManagerUtil.getDBMaxParameters()) {
			return dlFileEntryFinder.filterCountByG_F(
				groupId, folderIds, queryDefinition);
		}

		int start = 0;
		int end = DBManagerUtil.getDBMaxParameters();

		int filesCount = dlFileEntryFinder.filterCountByG_F(
			groupId, folderIds.subList(start, end), queryDefinition);

		List<Long> sublist = folderIds.subList(start, end);

		sublist.clear();

		filesCount += getFoldersFileEntriesCount(groupId, folderIds, status);

		return filesCount;
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, int start, int end,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		List<Long> folderIds = _dlFolderService.getFolderIds(
			groupId, rootFolderId);

		if (folderIds.isEmpty()) {
			return Collections.emptyList();
		}

		if (userId <= 0) {
			return dlFileEntryPersistence.filterFindByG_F(
				groupId, ArrayUtil.toLongArray(folderIds), start, end,
				orderByComparator);
		}

		return dlFileEntryPersistence.filterFindByG_U_F(
			groupId, userId, ArrayUtil.toLongArray(folderIds), start, end,
			orderByComparator);
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
			long groupId, long userId, long repositoryId, long rootFolderId,
			String[] mimeTypes, int status, int start, int end,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		List<Long> repositoryIds = new ArrayList<>();

		if (repositoryId != 0) {
			repositoryIds.add(repositoryId);
		}

		QueryDefinition<DLFileEntry> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		if (rootFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return dlFileEntryFinder.filterFindByG_U_R_F_M(
				groupId, userId, repositoryIds, new ArrayList<>(), mimeTypes,
				queryDefinition);
		}

		List<Long> folderIds = _dlFolderService.getFolderIds(
			groupId, rootFolderId);

		if (folderIds.isEmpty()) {
			return Collections.emptyList();
		}

		return dlFileEntryFinder.filterFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	@Override
	public List<DLFileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status, int start, int end,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws PortalException {

		return getGroupFileEntries(
			groupId, userId, 0, rootFolderId, mimeTypes, status, start, end,
			orderByComparator);
	}

	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId)
		throws PortalException {

		List<Long> folderIds = _dlFolderService.getFolderIds(
			groupId, rootFolderId);

		if (folderIds.isEmpty()) {
			return 0;
		}

		if (userId <= 0) {
			return dlFileEntryPersistence.filterCountByG_F(
				groupId, ArrayUtil.toLongArray(folderIds));
		}

		return dlFileEntryPersistence.filterCountByG_U_F(
			groupId, userId, ArrayUtil.toLongArray(folderIds));
	}

	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long repositoryId, long rootFolderId,
			String[] mimeTypes, int status)
		throws PortalException {

		List<Long> repositoryIds = new ArrayList<>();

		if (repositoryId != 0) {
			repositoryIds.add(repositoryId);
		}

		if (rootFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return dlFileEntryFinder.filterCountByG_U_R_F_M(
				groupId, userId, repositoryIds, new ArrayList<>(), mimeTypes,
				new QueryDefinition<>(status));
		}

		List<Long> folderIds = _dlFolderService.getFolderIds(
			groupId, rootFolderId);

		if (folderIds.isEmpty()) {
			return 0;
		}

		return dlFileEntryFinder.filterCountByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			new QueryDefinition<>(status));
	}

	@Override
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status)
		throws PortalException {

		return getGroupFileEntriesCount(
			groupId, userId, 0, rootFolderId, mimeTypes, status);
	}

	@Override
	public boolean hasFileEntryLock(long fileEntryId) throws PortalException {
		return dlFileEntryLocalService.hasFileEntryLock(
			getUserId(), fileEntryId);
	}

	@Override
	public boolean isFileEntryCheckedOut(long fileEntryId)
		throws PortalException {

		return dlFileEntryLocalService.isFileEntryCheckedOut(fileEntryId);
	}

	@Override
	public DLFileEntry moveFileEntry(
			long fileEntryId, long newFolderId, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		PermissionChecker permissionChecker = getPermissionChecker();

		fileEntryModelResourcePermission.check(
			permissionChecker, fileEntryId, ActionKeys.UPDATE);

		ModelResourcePermissionUtil.check(
			ModelResourcePermissionRegistryUtil.
				<Folder>getModelResourcePermission(Folder.class.getName()),
			permissionChecker, serviceContext.getScopeGroupId(), newFolderId,
			ActionKeys.ADD_DOCUMENT);

		return dlFileEntryLocalService.moveFileEntry(
			getUserId(), fileEntryId, newFolderId, serviceContext);
	}

	@Override
	public Lock refreshFileEntryLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException {

		return LockManagerUtil.refresh(lockUuid, companyId, expirationTime);
	}

	@Override
	public void revertFileEntry(
			long fileEntryId, String version, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.UPDATE);

		dlFileEntryLocalService.revertFileEntry(
			getUserId(), fileEntryId, version, serviceContext);
	}

	@Override
	public Hits search(
			long groupId, long creatorUserId, int status, int start, int end)
		throws PortalException {

		return dlFileEntryLocalService.search(
			groupId, getUserId(), creatorUserId, status, start, end);
	}

	@Override
	public Hits search(
			long groupId, long creatorUserId, long folderId, String[] mimeTypes,
			int status, int start, int end)
		throws PortalException {

		return dlFileEntryLocalService.search(
			groupId, getUserId(), creatorUserId, folderId, mimeTypes, status,
			start, end);
	}

	@Override
	public DLFileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease,
			long fileEntryTypeId, Map<String, DDMFormValues> ddmFormValuesMap,
			File file, InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.UPDATE);

		if (LockManagerUtil.isLocked(
				DLFileEntryConstants.getClassName(), fileEntryId)) {

			boolean hasLock = LockManagerUtil.hasLock(
				getUserId(), DLFileEntry.class.getName(), fileEntryId);

			if (!hasLock) {
				throw new FileEntryLockException.MustOwnLock();
			}
		}

		return dlFileEntryLocalService.updateFileEntry(
			getUserId(), fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, fileEntryTypeId,
			ddmFormValuesMap, file, inputStream, size, displayDate,
			expirationDate, reviewDate, serviceContext);
	}

	@Override
	public DLFileEntry updateStatus(
			long userId, long fileVersionId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		DLFileVersion dlFileVersion = _dlFileVersionLocalService.getFileVersion(
			fileVersionId);

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), dlFileVersion.getFileEntryId(),
			ActionKeys.UPDATE);

		return dlFileEntryLocalService.updateStatus(
			userId, fileVersionId, status, serviceContext, workflowContext);
	}

	@Override
	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid)
		throws PortalException {

		return dlFileEntryLocalService.verifyFileEntryCheckOut(
			fileEntryId, lockUuid);
	}

	@Override
	public boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws PortalException {

		return dlFileEntryLocalService.verifyFileEntryLock(
			fileEntryId, lockUuid);
	}

	private boolean _hasOverrideCheckoutPermission(long fileEntryId)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		return fileEntryModelResourcePermission.contains(
			getPermissionChecker(), fileEntryId, ActionKeys.OVERRIDE_CHECKOUT);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryServiceImpl.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = DLFileVersionLocalService.class)
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@BeanReference(type = DLFolderService.class)
	private DLFolderService _dlFolderService;

}
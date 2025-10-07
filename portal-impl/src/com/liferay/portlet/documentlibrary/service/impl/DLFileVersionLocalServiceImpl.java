/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.util.comparator.DLFileVersionVersionComparator;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.documentlibrary.service.base.DLFileVersionLocalServiceBaseImpl;

import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DLFileVersionLocalServiceImpl
	extends DLFileVersionLocalServiceBaseImpl {

	@Override
	public DLFileVersion fetchLatestFileVersion(
		long fileEntryId, boolean excludeWorkingCopy) {

		return fetchLatestFileVersion(
			fileEntryId, excludeWorkingCopy, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public DLFileVersion fetchLatestFileVersion(
		long fileEntryId, boolean excludeWorkingCopy, int status) {

		List<DLFileVersion> dlFileVersions = null;

		if (status == WorkflowConstants.STATUS_ANY) {
			dlFileVersions = dlFileVersionPersistence.findByFileEntryId(
				fileEntryId);
		}
		else {
			dlFileVersions = dlFileVersionPersistence.findByF_S(
				fileEntryId, status);
		}

		if (dlFileVersions.isEmpty()) {
			return null;
		}

		dlFileVersions = ListUtil.copy(dlFileVersions);

		Collections.sort(
			dlFileVersions, DLFileVersionVersionComparator.getInstance(false));

		DLFileVersion dlFileVersion = dlFileVersions.get(0);

		String version = dlFileVersion.getVersion();

		if (excludeWorkingCopy &&
			version.equals(DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION)) {

			return dlFileVersions.get(1);
		}

		return dlFileVersion;
	}

	@Override
	public DLFileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		return dlFileVersionPersistence.findByPrimaryKey(fileVersionId);
	}

	@Override
	public DLFileVersion getFileVersion(long fileEntryId, String version)
		throws PortalException {

		return dlFileVersionPersistence.findByF_V(fileEntryId, version);
	}

	@Override
	public DLFileVersion getFileVersionByUuidAndGroupId(
		String uuid, long groupId) {

		return dlFileVersionPersistence.fetchByUUID_G(uuid, groupId);
	}

	@Override
	public List<DLFileVersion> getFileVersions(long fileEntryId, int status) {
		List<DLFileVersion> dlFileVersions = null;

		if (status == WorkflowConstants.STATUS_ANY) {
			dlFileVersions = dlFileVersionPersistence.findByFileEntryId(
				fileEntryId);
		}
		else {
			dlFileVersions = dlFileVersionPersistence.findByF_S(
				fileEntryId, status);
		}

		dlFileVersions = ListUtil.copy(dlFileVersions);

		Collections.sort(
			dlFileVersions, DLFileVersionVersionComparator.getInstance(false));

		return dlFileVersions;
	}

	@Override
	public List<DLFileVersion> getFileVersions(
		long fileEntryId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return dlFileVersionPersistence.findByFileEntryId(
				fileEntryId, start, end,
				DLFileVersionVersionComparator.getInstance(false));
		}

		return dlFileVersionPersistence.findByF_S(
			fileEntryId, status, start, end,
			DLFileVersionVersionComparator.getInstance(false));
	}

	@Override
	public int getFileVersionsCount(long fileEntryId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return dlFileVersionPersistence.countByFileEntryId(fileEntryId);
		}

		return dlFileVersionPersistence.countByF_S(fileEntryId, status);
	}

	@Override
	public int getFileVersionsCount(long companyId, String storeUUID) {
		return dlFileVersionPersistence.countByC_SU(companyId, storeUUID);
	}

	@Override
	public DLFileVersion getLatestFileVersion(
			long fileEntryId, boolean excludeWorkingCopy)
		throws PortalException {

		DLFileVersion dlFileVersion = fetchLatestFileVersion(
			fileEntryId, excludeWorkingCopy);

		if (dlFileVersion == null) {
			throw new NoSuchFileVersionException(
				"No file versions found for fileEntryId " + fileEntryId);
		}

		return dlFileVersion;
	}

	@Override
	public DLFileVersion getLatestFileVersion(long userId, long fileEntryId)
		throws PortalException {

		boolean excludeWorkingCopy = true;

		if (_dlFileEntryLocalService.isFileEntryCheckedOut(fileEntryId)) {
			excludeWorkingCopy = !_dlFileEntryLocalService.hasFileEntryLock(
				userId, fileEntryId);
		}

		return getLatestFileVersion(fileEntryId, excludeWorkingCopy);
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
			(DLFileVersion dlFileVersion) -> {
				dlFileVersion.setTreePath(treePath);

				updateDLFileVersion(dlFileVersion);
			});

		actionableDynamicQuery.performActions();
	}

	@BeanReference(type = DLFileEntryLocalService.class)
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@BeanReference(type = DLFolderLocalService.class)
	private DLFolderLocalService _dlFolderLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portlet.documentlibrary.service.base.DLFileVersionServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DLFileVersionServiceImpl extends DLFileVersionServiceBaseImpl {

	@Override
	public DLFileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		DLFileVersion fileVersion = dlFileVersionPersistence.findByPrimaryKey(
			fileVersionId);

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileVersion.getFileEntryId(),
			ActionKeys.VIEW);

		return fileVersion;
	}

	@Override
	public List<DLFileVersion> getFileVersions(long fileEntryId, int status)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.VIEW);

		return dlFileVersionLocalService.getFileVersions(fileEntryId, status);
	}

	@Override
	public int getFileVersionsCount(long fileEntryId, int status)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.VIEW);

		return dlFileVersionPersistence.countByF_S(fileEntryId, status);
	}

	@Override
	public DLFileVersion getLatestFileVersion(long fileEntryId)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.VIEW);

		return dlFileVersionLocalService.getLatestFileVersion(
			getGuestOrUserId(), fileEntryId);
	}

	@Override
	public DLFileVersion getLatestFileVersion(
			long fileEntryId, boolean excludeWorkingCopy)
		throws PortalException {

		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntryId, ActionKeys.VIEW);

		return dlFileVersionLocalService.getLatestFileVersion(
			fileEntryId, excludeWorkingCopy);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.security.permission.resource;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

/**
 * @author Preston Crary
 */
public class DLFileEntryPermission {

	public static void check(
			PermissionChecker permissionChecker, FileEntry fileEntry,
			String actionId)
		throws PortalException {

		ModelResourcePermission<FileEntry> modelResourcePermission =
			_fileEntryModelResourcePermissionSnapshot.get();

		modelResourcePermission.check(permissionChecker, fileEntry, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, FileEntry fileEntry,
			String actionId)
		throws PortalException {

		ModelResourcePermission<FileEntry> modelResourcePermission =
			_fileEntryModelResourcePermissionSnapshot.get();

		return modelResourcePermission.contains(
			permissionChecker, fileEntry, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long fileEntryId,
			String actionId)
		throws PortalException {

		ModelResourcePermission<FileEntry> modelResourcePermission =
			_fileEntryModelResourcePermissionSnapshot.get();

		return modelResourcePermission.contains(
			permissionChecker, fileEntryId, actionId);
	}

	private static final Snapshot<ModelResourcePermission<FileEntry>>
		_fileEntryModelResourcePermissionSnapshot = new Snapshot<>(
			DLFileEntryPermission.class,
			Snapshot.cast(ModelResourcePermission.class),
			"(model.class.name=com.liferay.portal.kernel.repository.model." +
				"FileEntry)");

}
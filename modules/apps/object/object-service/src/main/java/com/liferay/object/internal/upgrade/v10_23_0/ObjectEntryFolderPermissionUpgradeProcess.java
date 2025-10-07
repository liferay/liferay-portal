/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_23_0;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.List;
import java.util.Objects;

/**
 * @author Mario Gomes
 */
public class ObjectEntryFolderPermissionUpgradeProcess extends UpgradeProcess {

	public ObjectEntryFolderPermissionUpgradeProcess(
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Override
	protected void doUpgrade() throws PortalException {
		ResourceAction objectEntryFolderResourceAction =
			_resourceActionLocalService.fetchResourceAction(
				ObjectEntryFolder.class.getName(), ActionKeys.ADD_FOLDER);

		if (objectEntryFolderResourceAction == null) {
			return;
		}

		long bitwiseValue = objectEntryFolderResourceAction.getBitwiseValue();

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				ObjectEntryFolder.class.getName());

		for (ResourcePermission resourcePermission : resourcePermissions) {
			if (Objects.equals(
					resourcePermission.getPrimKey(),
					ObjectEntryFolder.class.getName())) {

				continue;
			}

			long actionIds = resourcePermission.getActionIds();

			if ((actionIds & bitwiseValue) == bitwiseValue) {
				_resourcePermissionLocalService.addResourcePermission(
					resourcePermission.getCompanyId(),
					ObjectConstants.RESOURCE_NAME_OBJECT_ENTRY_FOLDER,
					resourcePermission.getScope(),
					resourcePermission.getPrimKey(),
					resourcePermission.getRoleId(),
					ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER);

				_resourcePermissionLocalService.removeResourcePermission(
					resourcePermission.getCompanyId(),
					resourcePermission.getName(), resourcePermission.getScope(),
					resourcePermission.getPrimKey(),
					resourcePermission.getRoleId(), ActionKeys.ADD_FOLDER);
			}
		}

		_resourceActionLocalService.deleteResourceAction(
			objectEntryFolderResourceAction);
	}

	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.entry.folder.util.ObjectEntryFolderUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=permission.object",
	service = BulkSelectionAction.class
)
public class PermissionObjectBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		String className = null;
		long companyId = 0L;
		long groupId = 0L;
		long resourceId = 0L;
		String resourceName = null;

		if (object instanceof DepotEntry) {
			DepotEntry depotEntry = (DepotEntry)object;

			className = depotEntry.getModelClassName();
			companyId = depotEntry.getCompanyId();
			groupId = depotEntry.getGroupId();
			resourceId = depotEntry.getDepotEntryId();
			resourceName = depotEntry.getModelClassName();
		}
		else if (object instanceof ObjectEntry) {
			ObjectEntry objectObjectEntry = (ObjectEntry)object;

			long rootObjectEntryFolderId =
				ObjectEntryFolderUtil.getRootObjectEntryFolderId(
					objectObjectEntry.getObjectEntryFolderId());

			if (rootObjectEntryFolderId == 0) {
				return;
			}

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.getObjectEntryFolder(
					rootObjectEntryFolderId);

			className = objectEntryFolder.getExternalReferenceCode();

			companyId = objectObjectEntry.getCompanyId();
			groupId = objectObjectEntry.getGroupId();
			resourceId = objectObjectEntry.getObjectEntryId();
			resourceName = objectObjectEntry.getModelClassName();
		}
		else {
			ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)object;

			className = objectEntryFolder.getModelClassName();
			companyId = objectEntryFolder.getCompanyId();
			groupId = objectEntryFolder.getGroupId();
			resourceId = objectEntryFolder.getObjectEntryFolderId();
			resourceName = objectEntryFolder.getModelClassName();
		}

		_permissionService.checkPermission(groupId, resourceName, resourceId);

		ModelPermissions modelPermissions =
			ModelPermissionsUtil.toModelPermissions(
				companyId,
				_getPermissions(
					(Map<String, Serializable>)inputMap.get("permissions"),
					className),
				resourceId, resourceName, _resourceActionLocalService,
				_resourcePermissionLocalService, _roleLocalService);

		String roleKey = (String)inputMap.get("roleKey");

		if (Validator.isBlank(roleKey)) {
			Collection<String> roleNames = modelPermissions.getRoleNames();

			for (ResourcePermission resourcePermission :
					_resourcePermissionLocalService.getResourcePermissions(
						companyId, resourceName,
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(resourceId))) {

				Role role = _roleLocalService.fetchRole(
					resourcePermission.getRoleId());

				if ((role == null) || roleNames.contains(role.getName())) {
					continue;
				}

				for (ResourceAction resourceAction :
						_resourceActionLocalService.getResourceActions(
							resourceName)) {

					_resourcePermissionLocalService.removeResourcePermission(
						companyId, resourceName,
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(resourceId), role.getRoleId(),
						resourceAction.getActionId());
				}
			}

			_resourcePermissionLocalService.updateResourcePermissions(
				companyId, groupId, resourceName, String.valueOf(resourceId),
				modelPermissions);
		}
		else {
			Role role = _roleLocalService.fetchRole(companyId, roleKey);

			if (role != null) {
				_resourcePermissionLocalService.setResourcePermissions(
					companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(resourceId), role.getRoleId(),
					modelPermissions.getActionIds(role.getName()));
			}
		}
	}

	private Permission[] _getPermissions(
		Map<String, Serializable> map, String key) {

		return (Permission[])map.getOrDefault(key, new Permission[0]);
	}

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}
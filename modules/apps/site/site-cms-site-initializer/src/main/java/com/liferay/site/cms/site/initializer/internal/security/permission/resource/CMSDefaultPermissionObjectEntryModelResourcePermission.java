/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.security.permission.resource;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Stefano Motta
 */
public class CMSDefaultPermissionObjectEntryModelResourcePermission
	implements ModelResourcePermission<ObjectEntry> {

	public CMSDefaultPermissionObjectEntryModelResourcePermission(
		String className, DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		Snapshot<ModelResourcePermission<ObjectEntry>>
			modelResourcePermissionSnapshot,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ObjectEntryLocalService objectEntryLocalService) {

		_className = className;
		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;
		_modelResourcePermissionSnapshot = modelResourcePermissionSnapshot;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long primaryKey,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, primaryKey, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, _className, primaryKey, actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, ObjectEntry objectEntry,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, objectEntry, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, _className, objectEntry.getObjectEntryId(),
				actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long primaryKey,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			_objectEntryLocalService.getObjectEntry(primaryKey), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, ObjectEntry objectEntry,
			String actionId)
		throws PortalException {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (permissionChecker.hasOwnerPermission(
				permissionChecker.getCompanyId(),
				objectDefinition.getClassName(),
				objectEntry.getHeadObjectEntryId(), objectEntry.getUserId(),
				actionId) ||
			permissionChecker.hasPermission(
				objectEntry.getGroupId(), objectDefinition.getClassName(),
				objectEntry.getHeadObjectEntryId(), actionId)) {

			return true;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		if (Objects.equals(
				values.get("className"), DepotEntry.class.getName())) {

			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				GetterUtil.getString(values.get("classExternalReferenceCode")),
				objectEntry.getCompanyId());

			if (group != null) {
				DepotEntry depotEntry =
					_depotEntryLocalService.getGroupDepotEntry(
						group.getGroupId());

				if (permissionChecker.hasPermission(
						depotEntry.getGroupId(), DepotEntry.class.getName(),
						depotEntry.getDepotEntryId(), ActionKeys.PERMISSIONS)) {

					return true;
				}
			}
		}
		else if (Objects.equals(
					values.get("className"),
					ObjectEntryFolder.class.getName())) {

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.
					fetchObjectEntryFolderByExternalReferenceCode(
						GetterUtil.getString(
							values.get("classExternalReferenceCode")),
						GetterUtil.getLong(values.get("depotGroupId")),
						objectEntry.getCompanyId());

			if ((objectEntryFolder != null) &&
				permissionChecker.hasPermission(
					objectEntryFolder.getGroupId(),
					ObjectEntryFolder.class.getName(),
					objectEntryFolder.getObjectEntryFolderId(),
					ActionKeys.PERMISSIONS)) {

				return true;
			}
		}

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			_modelResourcePermissionSnapshot.get();

		if (modelResourcePermission == null) {
			return false;
		}

		return modelResourcePermission.contains(
			permissionChecker, objectEntry, actionId);
	}

	@Override
	public String getModelName() {
		return _className;
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			_modelResourcePermissionSnapshot.get();

		if (modelResourcePermission == null) {
			return null;
		}

		return modelResourcePermission.getPortletResourcePermission();
	}

	private final String _className;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final Snapshot<ModelResourcePermission<ObjectEntry>>
		_modelResourcePermissionSnapshot;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;

}
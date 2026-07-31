/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.site.cms.site.initializer.util.RoleUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = ServiceWrapper.class)
public class CMSPermissionsObjectDefinitionLocalServiceWrapper
	extends ObjectDefinitionLocalServiceWrapper {

	@Override
	public ObjectDefinition publishCustomObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException {

		return _setResourcePermissions(
			super.publishCustomObjectDefinition(userId, objectDefinitionId));
	}

	@Override
	public ObjectDefinition publishSystemObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException {

		return _setResourcePermissions(
			super.publishSystemObjectDefinition(userId, objectDefinitionId));
	}

	private void _addCMSDefaultPermissionResourcePermission(
			ObjectDefinition objectDefinition)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(
			objectDefinition.getCompanyId(), RoleConstants.USER);

		if (role == null) {
			return;
		}

		_resourcePermissionLocalService.addResourcePermission(
			objectDefinition.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(objectDefinition.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);
	}

	private void _addResourcePermission(
		ObjectDefinition objectDefinition, String roleName) {

		try {
			Role role = _roleLocalService.fetchRole(
				objectDefinition.getCompanyId(), roleName);

			if (role == null) {
				return;
			}

			_resourcePermissionLocalService.addResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _setCMSAdministratorResourcePermissions(
		ObjectDefinition objectDefinition) {

		try {
			Role role = RoleUtil.getOrAddCMSAdministratorRole(
				objectDefinition.getCompanyId(), objectDefinition.getUserId());

			_resourcePermissionLocalService.addResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);

			_resourcePermissionLocalService.setResourcePermissions(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(),
				new String[] {
					ActionKeys.DELETE, ActionKeys.PERMISSIONS,
					ActionKeys.UPDATE, ActionKeys.VIEW
				});
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _setObjectDefinitionResourcePermissions(
		ObjectDefinition objectDefinition, String roleName) {

		try {
			Role role = _roleLocalService.fetchRole(
				objectDefinition.getCompanyId(), roleName);

			if (role == null) {
				return;
			}

			_resourcePermissionLocalService.setResourcePermissions(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				role.getRoleId(), new String[] {ActionKeys.VIEW});
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private ObjectDefinition _setResourcePermissions(
			ObjectDefinition objectDefinition)
		throws PortalException {

		if (Objects.equals(
				objectDefinition.getExternalReferenceCode(),
				"L_CMS_DEFAULT_PERMISSION")) {

			_addCMSDefaultPermissionResourcePermission(objectDefinition);
		}

		String objectFolderExternalReferenceCode =
			objectDefinition.getObjectFolderExternalReferenceCode();

		if (!Objects.equals(
				objectFolderExternalReferenceCode,
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES) &&
			!Objects.equals(
				objectFolderExternalReferenceCode,
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES)) {

			return objectDefinition;
		}

		_addResourcePermission(
			objectDefinition, DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);
		_addResourcePermission(
			objectDefinition,
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);
		_setCMSAdministratorResourcePermissions(objectDefinition);
		_setObjectDefinitionResourcePermissions(
			objectDefinition, RoleConstants.GUEST);
		_setObjectDefinitionResourcePermissions(
			objectDefinition, RoleConstants.USER);

		return objectDefinition;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSPermissionsObjectDefinitionLocalServiceWrapper.class);

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}
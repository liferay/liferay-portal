/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;

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
	public ObjectDefinition publishSystemObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException {

		ObjectDefinition objectDefinition = super.publishSystemObjectDefinition(
			userId, objectDefinitionId);

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

		try {
			Role role = _getOrAddCMSAdministratorRoleAndPermissions(
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

			_setResourcePermissions(objectDefinition, RoleConstants.GUEST);
			_setResourcePermissions(objectDefinition, RoleConstants.USER);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return objectDefinition;
	}

	private Role _getOrAddCMSAdministratorRoleAndPermissions(
			long companyId, long userId)
		throws Exception {

		String name = RoleConstants.CMS_ADMINISTRATOR;

		Role role = _roleLocalService.fetchRole(companyId, name);

		if (role != null) {
			return role;
		}

		return _roleLocalService.addRole(
			null, userId, null, 0, name, null, null, RoleConstants.TYPE_REGULAR,
			null, null);
	}

	private void _setResourcePermissions(
			ObjectDefinition objectDefinition, String roleName)
		throws PortalException {

		Role role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), roleName);

		_resourcePermissionLocalService.setResourcePermissions(
			objectDefinition.getCompanyId(), ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition.getObjectDefinitionId()),
			role.getRoleId(), new String[] {ActionKeys.VIEW});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSPermissionsObjectDefinitionLocalServiceWrapper.class);

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}
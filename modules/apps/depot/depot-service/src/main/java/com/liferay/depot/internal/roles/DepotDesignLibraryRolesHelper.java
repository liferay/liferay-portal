/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.roles;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

/**
 * @author Gabriel Prates
 */
public class DepotDesignLibraryRolesHelper {

	public DepotDesignLibraryRolesHelper(
		Language language, ResourceLocalService resourceLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService, UserLocalService userLocalService) {

		_language = language;
		_resourceLocalService = resourceLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_userLocalService = userLocalService;
	}

	public void setupDesignLibraryRoles(long companyId) throws PortalException {
		Role administratorRole = _getOrCreateRole(
			companyId, DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR);

		List<String> administratorResourceActions =
			ResourceActionsUtil.getResourceActions(DepotEntry.class.getName());

		administratorResourceActions.remove(ActionKeys.ASSIGN_USER_ROLES);

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, DepotEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			administratorRole.getRoleId(),
			administratorResourceActions.toArray(new String[0]));

		_resourcePermissionLocalService.addResourcePermission(
			companyId, _ASSET_TAGS_RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			administratorRole.getRoleId(), ActionKeys.MANAGE_TAG);

		Role contentReviewerRole = _getOrCreateRole(
			companyId, DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER);

		_resourcePermissionLocalService.addResourcePermission(
			companyId, _ASSET_TAGS_RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			contentReviewerRole.getRoleId(), ActionKeys.MANAGE_TAG);

		Role memberRole = _getOrCreateRole(
			companyId, DepotRolesConstants.DESIGN_LIBRARY_MEMBER);

		_resourcePermissionLocalService.addResourcePermission(
			companyId, DepotEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			memberRole.getRoleId(), ActionKeys.VIEW);

		for (String name : DepotRolesConstants.DESIGN_LIBRARY_ROLE_NAMES) {
			Role role = _getOrCreateRole(companyId, name);

			_resourceLocalService.addResources(
				companyId, 0, 0, Role.class.getName(), role.getRoleId(), false,
				false, false);

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, Role.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(role.getRoleId()), administratorRole.getRoleId(),
				new String[] {ActionKeys.VIEW});
		}
	}

	private Role _getOrCreateRole(long companyId, String name)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(companyId, name);

		if (role != null) {
			return role;
		}

		boolean addResource = PermissionThreadLocal.isAddResource();

		try {
			PermissionThreadLocal.setAddResource(false);

			User user = _userLocalService.getGuestUser(companyId);

			return _roleLocalService.addRole(
				RoleConstants.toSystemRoleExternalReferenceCode(name),
				user.getUserId(), null, 0, name,
				DepotRoleUtil.getTitleMap(_language, name),
				DepotRoleUtil.getDescriptionMap(companyId, _language, name),
				RoleConstants.TYPE_DEPOT,
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY, null);
		}
		finally {
			PermissionThreadLocal.setAddResource(addResource);
		}
	}

	private static final String _ASSET_TAGS_RESOURCE_NAME =
		"com.liferay.asset.tags";

	private final Language _language;
	private final ResourceLocalService _resourceLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private final UserLocalService _userLocalService;

}
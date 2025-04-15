/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.permission;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;

import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Javier Gamarra
 */
public class PermissionUtil {

	public static void checkPermission(
			String actionId, GroupLocalService groupLocalService,
			String resourceName, long resourceId, Long siteId)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		Group group = groupLocalService.fetchGroup(siteId);

		if ((group != null) && group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		if (!permissionChecker.hasPermission(
				group, resourceName, resourceId, actionId)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, resourceName, siteId, actionId);
		}
	}

	/**
	 * Changes made here must also be made in
	 * base_resource_impl.ftl#_getPermissions to ensure consistent behavior.
	 */
	public static Collection<Permission> getPermissions(
			long companyId, List<ResourceAction> resourceActions,
			long resourceId, String resourceName, String[] roleNames)
		throws Exception {

		Map<String, Permission> permissions = new HashMap<>();

		int count =
			ResourcePermissionLocalServiceUtil.getResourcePermissionsCount(
				companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(resourceId));

		if (count == 0) {
			ResourceLocalServiceUtil.addResources(
				companyId, resourceId, 0, resourceName,
				String.valueOf(resourceId), false, true, true);
		}

		List<String> actionIds = TransformUtil.transform(
			resourceActions, ResourceAction::getActionId);

		Set<Role> roles = new HashSet<>();

		Set<ResourcePermission> resourcePermissions = new HashSet<>();

		resourcePermissions.addAll(
			ResourcePermissionLocalServiceUtil.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId)));
		resourcePermissions.addAll(
			ResourcePermissionLocalServiceUtil.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_GROUP,
				String.valueOf(GroupThreadLocal.getGroupId())));
		resourcePermissions.addAll(
			ResourcePermissionLocalServiceUtil.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_GROUP_TEMPLATE,
				"0"));
		resourcePermissions.addAll(
			ResourcePermissionLocalServiceUtil.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(resourceId)));

		if (roleNames != null) {
			for (String roleName : roleNames) {
				roles.add(RoleLocalServiceUtil.getRole(companyId, roleName));
			}
		}
		else {
			for (ResourcePermission resourcePermission : resourcePermissions) {
				roles.add(
					RoleLocalServiceUtil.getRole(
						resourcePermission.getRoleId()));
			}
		}

		for (Role role : roles) {
			Set<String> actionsIdsSet = new HashSet<>();

			for (Resource resource :
					TransformUtil.transform(
						resourcePermissions,
						resourcePermission ->
							ResourceLocalServiceUtil.getResource(
								resourcePermission.getCompanyId(),
								resourcePermission.getName(),
								resourcePermission.getScope(),
								resourcePermission.getPrimKey()))) {

				actionsIdsSet.addAll(
					ResourcePermissionLocalServiceUtil.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_COMPANY,
							String.valueOf(resource.getCompanyId()),
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					ResourcePermissionLocalServiceUtil.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_GROUP,
							String.valueOf(GroupThreadLocal.getGroupId()),
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					ResourcePermissionLocalServiceUtil.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					ResourcePermissionLocalServiceUtil.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							resource.getScope(), resource.getPrimKey(),
							role.getRoleId(), actionIds));
			}

			if (actionsIdsSet.isEmpty()) {
				continue;
			}

			permissions.put(
				role.getName(),
				new Permission() {
					{
						actionIds = actionsIdsSet.toArray(new String[0]);
						roleExternalReferenceCode =
							role.getExternalReferenceCode();
						roleName = role.getName();
						roleType = role.getTypeLabel();
					}
				});
		}

		return permissions.values();
	}

	public static List<ResourcePermission> getResourcePermissions(
			long companyId, long resourceId, String resourceName,
			ResourcePermissionLocalService resourcePermissionLocalService)
		throws PortalException {

		_checkResources(
			companyId, resourceId, resourceName,
			resourcePermissionLocalService);

		return resourcePermissionLocalService.getResourcePermissions(
			companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(resourceId));
	}

	public static List<Role> getRoles(
			Company company, RoleLocalService roleLocalService,
			String[] roleNames)
		throws PortalException {

		List<Role> roles = new ArrayList<>();

		List<String> invalidRoleNames = new ArrayList<>();

		for (String roleName : roleNames) {
			try {
				roles.add(
					roleLocalService.getRole(company.getCompanyId(), roleName));
			}
			catch (NoSuchRoleException noSuchRoleException) {
				if (_log.isDebugEnabled()) {
					_log.debug(roleName, noSuchRoleException);
				}

				invalidRoleNames.add(roleName);
			}
		}

		if (!invalidRoleNames.isEmpty()) {
			throw new BadRequestException(
				"Invalid roles: " + ArrayUtil.toStringArray(invalidRoleNames));
		}

		return roles;
	}

	public static Permission toPermission(
		List<ResourceAction> resourceActions,
		ResourcePermission resourcePermission, Role role) {

		Set<String> actionsIdsSet = new HashSet<>();

		long actionIds = resourcePermission.getActionIds();

		for (ResourceAction resourceAction : resourceActions) {
			long bitwiseValue = resourceAction.getBitwiseValue();

			if ((actionIds & bitwiseValue) == bitwiseValue) {
				actionsIdsSet.add(resourceAction.getActionId());
			}
		}

		return new Permission() {
			{
				actionIds = actionsIdsSet.toArray(new String[0]);
				roleExternalReferenceCode = role.getExternalReferenceCode();
				roleName = role.getName();
				roleType = role.getTypeLabel();
			}
		};
	}

	public static Permission toPermission(
			Long companyId, Long id, List<ResourceAction> resourceActions,
			String resourceName,
			ResourcePermissionLocalService resourcePermissionLocalService,
			Role role)
		throws PortalException {

		_checkResources(
			companyId, id, resourceName, resourcePermissionLocalService);

		ResourcePermission resourcePermission =
			resourcePermissionLocalService.fetchResourcePermission(
				companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(id), role.getRoleId());

		if (resourcePermission == null) {
			return null;
		}

		return toPermission(resourceActions, resourcePermission, role);
	}

	private static void _checkResources(
			long companyId, long resourceId, String resourceName,
			ResourcePermissionLocalService resourcePermissionLocalService)
		throws PortalException {

		int count = resourcePermissionLocalService.getResourcePermissionsCount(
			companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(resourceId));

		if (count == 0) {
			ResourceLocalServiceUtil.addResources(
				companyId, resourceId, 0, resourceName,
				String.valueOf(resourceId), false, true, true);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(PermissionUtil.class);

}
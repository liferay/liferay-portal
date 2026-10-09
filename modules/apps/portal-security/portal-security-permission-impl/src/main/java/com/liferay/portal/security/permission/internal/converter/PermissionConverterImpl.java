/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.internal.converter;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Permission;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionConversionFilter;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.model.impl.PermissionImpl;
import com.liferay.portal.security.permission.converter.PermissionConverter;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = PermissionConverter.class)
public class PermissionConverterImpl implements PermissionConverter {

	@Override
	public List<Permission> convertPermissions(Role role) {
		return convertPermissions(role, null);
	}

	@Override
	public List<Permission> convertPermissions(
		Role role, PermissionConversionFilter permissionConversionFilter) {

		int[] scopes = new int[0];

		if (role.getType() == RoleConstants.TYPE_REGULAR) {
			scopes = new int[] {
				ResourceConstants.SCOPE_COMPANY, ResourceConstants.SCOPE_GROUP
			};
		}
		else if ((role.getType() == RoleConstants.TYPE_DEPOT) ||
				 (role.getType() == RoleConstants.TYPE_ORGANIZATION) ||
				 (role.getType() == RoleConstants.TYPE_PROVIDER) ||
				 (role.getType() == RoleConstants.TYPE_SITE)) {

			scopes = new int[] {ResourceConstants.SCOPE_GROUP_TEMPLATE};
		}
		else if (role.getType() == RoleConstants.TYPE_ACCOUNT) {
			scopes = new int[] {
				ResourceConstants.SCOPE_COMPANY, ResourceConstants.SCOPE_GROUP,
				ResourceConstants.SCOPE_GROUP_TEMPLATE
			};
		}
		else if (role.getType() == RoleConstants.TYPE_PUBLICATIONS) {
			scopes = new int[] {
				ResourceConstants.SCOPE_COMPANY,
				ResourceConstants.SCOPE_GROUP_TEMPLATE
			};
		}

		List<Permission> permissions = new ArrayList<>();

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getRoleResourcePermissions(
				role.getRoleId(), scopes, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (ResourcePermission resourcePermission : resourcePermissions) {
			if ((permissionConversionFilter != null) &&
				!permissionConversionFilter.accept(role, resourcePermission)) {

				continue;
			}

			List<ResourceAction> resourceActions =
				_resourceActionLocalService.getResourceActions(
					resourcePermission.getName());

			for (ResourceAction resourceAction : resourceActions) {
				if (!_resourcePermissionLocalService.hasActionId(
						resourcePermission, resourceAction)) {

					continue;
				}

				Permission permission = new PermissionImpl();

				permission.setName(resourcePermission.getName());
				permission.setScope(resourcePermission.getScope());
				permission.setPrimKey(resourcePermission.getPrimKey());
				permission.setActionId(resourceAction.getActionId());

				permissions.add(permission);
			}
		}

		return permissions;
	}

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}
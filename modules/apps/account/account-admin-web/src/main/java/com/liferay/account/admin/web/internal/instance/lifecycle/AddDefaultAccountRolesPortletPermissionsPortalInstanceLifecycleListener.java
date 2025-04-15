/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.instance.lifecycle;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "service.ranking:Integer=200",
	service = PortalInstanceLifecycleListener.class
)
public class
	AddDefaultAccountRolesPortletPermissionsPortalInstanceLifecycleListener
		extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		Role role = _roleLocalService.fetchRole(
			company.getCompanyId(),
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);

		if (role == null) {
			return;
		}

		_checkResourcePermissions(
			company.getCompanyId(),
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			HashMapBuilder.put(
				AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				new String[] {ActionKeys.ACCESS_IN_CONTROL_PANEL}
			).put(
				AccountPortletKeys.ACCOUNT_USERS_ADMIN,
				new String[] {
					AccountActionKeys.ASSIGN_ACCOUNTS,
					ActionKeys.ACCESS_IN_CONTROL_PANEL
				}
			).build());
	}

	private void _checkResourcePermissions(
			long companyId, String roleName,
			Map<String, String[]> resourceActionsMap)
		throws Exception {

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		for (Map.Entry<String, String[]> entry :
				resourceActionsMap.entrySet()) {

			for (String resourceAction : entry.getValue()) {
				String resourceName = entry.getKey();

				ResourcePermission resourcePermission =
					_resourcePermissionLocalService.fetchResourcePermission(
						companyId, resourceName,
						ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
						role.getRoleId());

				if ((resourcePermission == null) ||
					!resourcePermission.hasActionId(resourceAction)) {

					_resourcePermissionLocalService.addResourcePermission(
						companyId, resourceName,
						ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
						role.getRoleId(), resourceAction);
				}
			}
		}
	}

	@Reference(
		target = "(jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_USERS_ADMIN + ")"
	)
	private Portlet _accountUsersAdminPortlet;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.account.service)(&(release.schema.version>=1.0.2)))"
	)
	private Release _release;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}
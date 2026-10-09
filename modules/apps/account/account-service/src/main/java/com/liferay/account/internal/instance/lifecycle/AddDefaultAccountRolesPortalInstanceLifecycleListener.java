/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.instance.lifecycle;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "service.ranking:Integer=300",
	service = PortalInstanceLifecycleListener.class
)
public class AddDefaultAccountRolesPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (_checkAccountRole(
				company,
				AccountRoleConstants.
					REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR)) {

			_checkResourcePermissions(
				company.getCompanyId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
				_accountAdministratorResourceActionsMap,
				_accountMemberResourceActionsMap);
		}

		if (_checkRole(
				company,
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER)) {

			_checkResourcePermissions(
				company.getCompanyId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
				_accountManagerResourceActionsMap,
				_accountMemberResourceActionsMap);
		}

		if (_checkAccountRole(
				company,
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER)) {

			_checkResourcePermissions(
				company.getCompanyId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER,
				_accountMemberResourceActionsMap);
		}
	}

	private boolean _checkAccountRole(Company company, String roleName)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			company.getCompanyId(), roleName);

		if (role != null) {
			if (MapUtil.isEmpty(role.getDescriptionMap())) {
				role.setDescriptionMap(
					AccountRoleConstants.roleDescriptionsMap.get(
						role.getName()));

				_roleLocalService.updateRole(role);
			}

			return false;
		}

		User guestUser = company.getGuestUser();

		_accountRoleLocalService.addAccountRole(
			RoleConstants.toSystemRoleExternalReferenceCode(roleName),
			guestUser.getUserId(), AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			roleName, null,
			AccountRoleConstants.roleDescriptionsMap.get(roleName));

		return true;
	}

	private void _checkResourcePermissions(
			long companyId, String roleName,
			Map<String, String[]>... resourceActionsMaps)
		throws Exception {

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		for (Map<String, String[]> resourceActionsMap : resourceActionsMaps) {
			for (Map.Entry<String, String[]> entry :
					resourceActionsMap.entrySet()) {

				for (String resourceAction : entry.getValue()) {
					String resourceName = entry.getKey();

					ResourcePermission resourcePermission =
						_resourcePermissionLocalService.fetchResourcePermission(
							companyId, resourceName,
							ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
							role.getRoleId());

					if ((resourcePermission != null) &&
						resourcePermission.hasActionId(resourceAction)) {

						continue;
					}

					_resourcePermissionLocalService.addResourcePermission(
						companyId, resourceName,
						ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
						role.getRoleId(), resourceAction);
				}
			}
		}
	}

	private boolean _checkRole(Company company, String roleName)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			company.getCompanyId(), roleName);

		if (role == null) {
			User guestUser = company.getGuestUser();

			_roleLocalService.addRole(
				RoleConstants.toSystemRoleExternalReferenceCode(roleName),
				guestUser.getUserId(), null, 0,
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER, null,
				AccountRoleConstants.roleDescriptionsMap.get(
					AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER),
				RoleConstants.TYPE_ORGANIZATION, null, null);

			return true;
		}

		return false;
	}

	private static final Map<String, String[]>
		_accountAdministratorResourceActionsMap = HashMapBuilder.put(
			AccountEntry.class.getName(),
			new String[] {
				AccountActionKeys.ADD_USER, AccountActionKeys.ASSIGN_USERS,
				AccountActionKeys.MANAGE_ADDRESSES,
				AccountActionKeys.UNASSIGN_USERS,
				AccountActionKeys.UPDATE_USERS,
				AccountActionKeys.VIEW_ADDRESSES,
				AccountActionKeys.VIEW_ACCOUNT_ROLES,
				AccountActionKeys.VIEW_ORGANIZATIONS,
				AccountActionKeys.VIEW_USERS, ActionKeys.UPDATE
			}
		).put(
			AccountRole.class.getName(), new String[] {ActionKeys.VIEW}
		).build();
	private static final Map<String, String[]>
		_accountManagerResourceActionsMap = HashMapBuilder.put(
			AccountEntry.class.getName(),
			new String[] {
				AccountActionKeys.ADD_USER, AccountActionKeys.ASSIGN_USERS,
				AccountActionKeys.MANAGE_ADDRESSES,
				AccountActionKeys.UNASSIGN_USERS,
				AccountActionKeys.UPDATE_USERS,
				AccountActionKeys.VIEW_ACCOUNT_ROLES,
				AccountActionKeys.VIEW_ADDRESSES,
				AccountActionKeys.VIEW_ORGANIZATIONS,
				AccountActionKeys.VIEW_USERS, ActionKeys.UPDATE
			}
		).put(
			AccountRole.class.getName(), new String[] {ActionKeys.VIEW}
		).put(
			Organization.class.getName(),
			new String[] {
				AccountActionKeys.MANAGE_ACCOUNTS,
				AccountActionKeys.MANAGE_SUBORGANIZATIONS_ACCOUNTS
			}
		).build();
	private static final Map<String, String[]>
		_accountMemberResourceActionsMap = HashMapBuilder.put(
			AccountEntry.class.getName(), new String[] {ActionKeys.VIEW}
		).build();

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.account.service)(&(release.schema.version>=1.0.2)))"
	)
	private Release _release;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}
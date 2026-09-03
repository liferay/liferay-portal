/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.instance.lifecycle;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.security.fips.constants.FIPSPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class FIPSPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		long companyId = company.getCompanyId();

		User user = _userLocalService.addDefaultServiceAccountUser(companyId);

		_addCryptoOfficerPasswordPolicy(companyId, user);

		_addCryptoOfficerResourcePermissions(companyId);
		_addCryptoOfficerRole(companyId, user);
	}

	private void _addCryptoOfficerPasswordPolicy(long companyId, User user)
		throws Exception {

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicy(
				companyId, FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER);

		if (passwordPolicy != null) {
			return;
		}

		PasswordPolicy defaultPasswordPolicy =
			_passwordPolicyLocalService.getDefaultPasswordPolicy(companyId);

		_passwordPolicyLocalService.addPasswordPolicy(
			user.getUserId(), false,
			FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER,
			FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER, true, true,
			defaultPasswordPolicy.getMinAge(),
			defaultPasswordPolicy.isCheckSyntax(),
			defaultPasswordPolicy.isAllowDictionaryWords(),
			defaultPasswordPolicy.getMinAlphanumeric(),
			defaultPasswordPolicy.getMinLength(),
			defaultPasswordPolicy.getMinLowerCase(),
			defaultPasswordPolicy.getMinNumbers(),
			defaultPasswordPolicy.getMinSymbols(),
			defaultPasswordPolicy.getMinUpperCase(),
			defaultPasswordPolicy.getRegex(), defaultPasswordPolicy.isHistory(),
			defaultPasswordPolicy.getHistoryCount(),
			defaultPasswordPolicy.isExpireable(),
			defaultPasswordPolicy.getMaxAge(),
			defaultPasswordPolicy.getWarningTime(),
			defaultPasswordPolicy.getGraceLimit(), true, 10,
			defaultPasswordPolicy.getLockoutDuration(),
			defaultPasswordPolicy.getResetFailureCount(),
			defaultPasswordPolicy.getResetTicketMaxAge(), new ServiceContext());
	}

	private void _addCryptoOfficerResourcePermissions(long companyId)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.CRYPTO_OFFICER);

		if (role == null) {
			return;
		}

		_addResourcePermission(
			ActionKeys.ACCESS_IN_CONTROL_PANEL, companyId,
			_fipsAdminPortlet.getPortletName(), role);
		_addResourcePermission(
			ActionKeys.VIEW, companyId, _fipsAdminPortlet.getPortletName(),
			role);
		_addResourcePermission(
			ActionKeys.VIEW_CONTROL_PANEL, companyId, PortletKeys.PORTAL, role);
	}

	private void _addCryptoOfficerRole(long companyId, User user)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.CRYPTO_OFFICER);

		if (role != null) {
			return;
		}

		_roleLocalService.addRole(
			null, user.getUserId(), null, 0, RoleConstants.CRYPTO_OFFICER, null,
			null, RoleConstants.TYPE_REGULAR, null, null);
	}

	private void _addResourcePermission(
			String actionKey, long companyId, String name, Role role)
		throws Exception {

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId, name, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId), role.getRoleId());

		if ((resourcePermission != null) &&
			resourcePermission.hasActionId(actionKey)) {

			return;
		}

		_resourcePermissionLocalService.addResourcePermission(
			companyId, name, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(companyId), role.getRoleId(), actionKey);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + FIPSPortletKeys.FIPS_ADMIN + ")"
	)
	private Portlet _fipsAdminPortlet;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.instance.lifecycle;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.fips.constants.FIPSConstants;

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

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
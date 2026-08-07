/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.crypto.officer.model.listener;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredRoleException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.util.PortalInstances;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(service = ModelListener.class)
public class RoleModelListener extends BaseModelListener<Role> {

	@Override
	public void onAfterRemoveAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		if (!PropsValues.FIPS_ENABLED ||
			!Objects.equals(User.class.getName(), associationClassName)) {

			return;
		}

		Role role = _roleLocalService.fetchRole((long)classPK);

		if ((role == null) ||
			!StringUtil.equals(RoleConstants.CRYPTO_OFFICER, role.getName())) {

			return;
		}

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicy(
				role.getCompanyId(),
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER);

		if (passwordPolicy == null) {
			return;
		}

		_userLocalService.unsetPasswordPolicyUsers(
			passwordPolicy.getPasswordPolicyId(),
			new long[] {(long)associationClassPK});
	}

	@Override
	public void onBeforeAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		if (!PropsValues.FIPS_ENABLED ||
			!Objects.equals(User.class.getName(), associationClassName)) {

			return;
		}

		Role role = _roleLocalService.fetchRole((long)classPK);
		long userId = (long)associationClassPK;

		try {
			_checkRole(role, userId);

			_addPasswordPolicyUser(
				_roleLocalService.fetchRole((long)classPK),
				(long)associationClassPK);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onBeforeRemove(Role role) throws ModelListenerException {
		if (PortalInstances.isCurrentCompanyInDeletionProcess() ||
			!PropsValues.FIPS_ENABLED ||
			!StringUtil.equals(RoleConstants.CRYPTO_OFFICER, role.getName())) {

			return;
		}

		throw new ModelListenerException(
			new RequiredRoleException(
				"Role \"" + role.getName() +
					"\" cannot be removed in FIPS mode"));
	}

	private void _addPasswordPolicyUser(Role role, long userId)
		throws PortalException {

		if ((role == null) ||
			!StringUtil.equals(RoleConstants.CRYPTO_OFFICER, role.getName())) {

			return;
		}

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicy(
				role.getCompanyId(),
				FIPSConstants.PASSWORD_POLICY_NAME_CRYPTO_OFFICER);

		if (passwordPolicy == null) {
			return;
		}

		_userLocalService.addPasswordPolicyUsers(
			passwordPolicy.getPasswordPolicyId(), new long[] {userId});

		User user = _userLocalService.getUser(userId);

		if ((user.getLastLoginDate() == null) &&
			Validator.isNotNull(user.getPassword()) &&
			!user.isPasswordReset()) {

			_userLocalService.updatePasswordReset(userId, true);
		}
	}

	private void _checkRole(Role role, long userId) throws PortalException {
		if (role == null) {
			return;
		}

		if ((StringUtil.equals(RoleConstants.ADMINISTRATOR, role.getName()) &&
			 _userLocalService.hasRoleUser(
				 role.getCompanyId(), RoleConstants.CRYPTO_OFFICER, userId,
				 false)) ||
			(StringUtil.equals(RoleConstants.CRYPTO_OFFICER, role.getName()) &&
			 _userLocalService.hasRoleUser(
				 role.getCompanyId(), RoleConstants.ADMINISTRATOR, userId,
				 false))) {

			throw new ModelListenerException(
				new RoleAssignmentException(
					StringBundler.concat(
						"A user cannot be assigned to roles \"",
						RoleConstants.ADMINISTRATOR, "\" and \"",
						RoleConstants.CRYPTO_OFFICER,
						"\" at the same time in FIPS mode")));
		}
	}

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
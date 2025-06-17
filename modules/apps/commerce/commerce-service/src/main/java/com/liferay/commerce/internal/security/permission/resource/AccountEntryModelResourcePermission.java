/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.security.permission.resource;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceAccountActionKeys;
import com.liferay.commerce.internal.util.AccountEntryUtil;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"model.class.name=com.liferay.account.model.AccountEntry",
		"service.ranking:Integer=100"
	},
	service = ModelResourcePermission.class
)
public class AccountEntryModelResourcePermission
	implements ModelResourcePermission<AccountEntry> {

	@Override
	public void check(
			PermissionChecker permissionChecker, AccountEntry accountEntry,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, accountEntry, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(),
				accountEntry.getAccountEntryId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long accountEntryId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, accountEntryId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(), accountEntryId,
				actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, AccountEntry accountEntry,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker, accountEntry.getAccountEntryId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long accountEntryId,
			String actionId)
		throws PortalException {

		if (_accountEntryModelResourcePermission.contains(
				permissionChecker, accountEntryId, actionId) ||
			(Objects.equals(
				actionId, CommerceAccountActionKeys.VIEW_CHANNEL_DEFAULTS) &&
			 _accountEntryModelResourcePermission.contains(
				 permissionChecker, accountEntryId,
				 CommerceAccountActionKeys.MANAGE_CHANNEL_DEFAULTS))) {

			return true;
		}

		if (accountEntryId == 0) {
			return permissionChecker.hasPermission(
				null, AccountEntry.class.getName(), 0,
				CommerceAccountActionKeys.
					MANAGE_AVAILABLE_ACCOUNTS_VIA_USER_CHANNEL_REL);
		}

		if (_permissions.contains(actionId) &&
			_accountEntryModelResourcePermission.contains(
				permissionChecker, accountEntryId,
				CommerceAccountActionKeys.
					MANAGE_AVAILABLE_ACCOUNTS_VIA_USER_CHANNEL_REL)) {

			User user = permissionChecker.getUser();

			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
				null;

			long commerceChannelId = AccountEntryUtil.getCommerceChannelId(
				CommerceContextThreadLocal.get(),
				CommerceGroupThreadLocal.get());

			if (commerceChannelId > 0) {
				commerceChannelAccountEntryRel =
					_commerceChannelAccountEntryRelLocalService.
						fetchCommerceChannelAccountEntryRel(
							accountEntryId, User.class.getName(),
							user.getUserId(), commerceChannelId,
							CommerceChannelAccountEntryRelConstants.TYPE_USER);
			}

			if (commerceChannelAccountEntryRel != null) {
				return true;
			}

			commerceChannelAccountEntryRel =
				_commerceChannelAccountEntryRelLocalService.
					fetchCommerceChannelAccountEntryRel(
						accountEntryId, User.class.getName(), user.getUserId(),
						0, CommerceChannelAccountEntryRelConstants.TYPE_USER);

			if (commerceChannelAccountEntryRel != null) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getModelName() {
		return AccountEntry.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _accountEntryModelResourcePermission.
			getPortletResourcePermission();
	}

	private static final List<String> _permissions = Arrays.asList(
		"MANAGE_ADDRESSES", "MANAGE_USERS", "UPDATE", "VIEW",
		"VIEW_ACCOUNT_GROUPS", "VIEW_ACCOUNT_ROLES", "VIEW_ADDRESSES",
		"VIEW_USERS");

	@Reference(
		target = "(component.name=com.liferay.account.internal.security.permission.resource.AccountEntryModelResourcePermission)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

}
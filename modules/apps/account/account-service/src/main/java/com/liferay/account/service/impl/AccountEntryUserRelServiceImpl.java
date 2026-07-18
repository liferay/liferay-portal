/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.base.AccountEntryUserRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Brian Wing Shun Chan
 * @see    AccountEntryUserRelServiceBaseImpl
 */
@Component(
	property = {
		"json.web.service.context.name=account",
		"json.web.service.context.path=AccountEntryUserRel"
	},
	service = AopService.class
)
public class AccountEntryUserRelServiceImpl
	extends AccountEntryUserRelServiceBaseImpl {

	@Override
	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!_modelResourcePermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.ADD_USER) ||
			!_modelResourcePermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.ASSIGN_USERS)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(), accountEntryId,
				AccountActionKeys.ASSIGN_USERS, AccountActionKeys.ADD_USER);
		}

		return accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	@Override
	public AccountEntryUserRel addAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress, long[] accountRoleIds,
			String userExternalReferenceCode, ServiceContext serviceContext)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.ASSIGN_USERS);

		User user = null;

		if (Validator.isNotNull(userExternalReferenceCode)) {
			user = _userLocalService.fetchUserByExternalReferenceCode(
				userExternalReferenceCode, serviceContext.getCompanyId());
		}

		if (user == null) {
			user = _userLocalService.fetchUserByEmailAddress(
				serviceContext.getCompanyId(), emailAddress);
		}

		if (user == null) {
			_modelResourcePermission.check(
				getPermissionChecker(), accountEntryId,
				AccountActionKeys.ADD_USER);
		}

		return accountEntryUserRelLocalService.
			addAccountEntryUserRelByEmailAddress(
				accountEntryId, emailAddress, accountRoleIds,
				userExternalReferenceCode, serviceContext);
	}

	@Override
	public void addAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.ASSIGN_USERS);

		accountEntryUserRelLocalService.addAccountEntryUserRels(
			accountEntryId, accountUserIds);
	}

	@Override
	public AccountEntryUserRel addPersonTypeAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!_modelResourcePermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.ADD_USER) ||
			!_modelResourcePermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.ASSIGN_USERS)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(), accountEntryId,
				AccountActionKeys.ASSIGN_USERS, AccountActionKeys.ADD_USER);
		}

		return accountEntryUserRelLocalService.addPersonTypeAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	@Override
	public Ticket addUserInvitationTicket(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			User inviter, ServiceContext serviceContext)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.INVITE_USER);

		return accountEntryUserRelLocalService.addUserInvitationTicket(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	@Override
	public void deleteAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UNASSIGN_USERS);

		accountEntryUserRelLocalService.deleteAccountEntryUserRelByEmailAddress(
			accountEntryId, emailAddress);
	}

	@Override
	public void deleteAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UNASSIGN_USERS);

		accountEntryUserRelLocalService.deleteAccountEntryUserRels(
			accountEntryId, accountUserIds);
	}

	@Override
	public AccountEntryUserRel fetchAccountEntryUserRel(
			long accountEntryUserRelId)
		throws PortalException {

		AccountEntryUserRel accountEntryUserRel =
			accountEntryUserRelPersistence.fetchByPrimaryKey(
				accountEntryUserRelId);

		if (accountEntryUserRel != null) {
			_modelResourcePermission.check(
				getPermissionChecker(), accountEntryUserRel.getAccountEntryId(),
				AccountActionKeys.VIEW_USERS);
		}

		return accountEntryUserRel;
	}

	@Override
	public AccountEntryUserRel fetchAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.VIEW_USERS);

		return accountEntryUserRelPersistence.fetchByAEI_AUI(
			accountEntryId, accountUserId);
	}

	@Override
	public AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.VIEW_USERS);

		return accountEntryUserRelPersistence.findByAEI_AUI(
			accountEntryId, accountUserId);
	}

	@Override
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountEntryId(
			long accountEntryId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.VIEW_USERS);

		return accountEntryUserRelLocalService.
			getAccountEntryUserRelsByAccountEntryId(accountEntryId);
	}

	@Override
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountEntryId(
			long accountEntryId, int start, int end)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.VIEW_USERS);

		return accountEntryUserRelLocalService.
			getAccountEntryUserRelsByAccountEntryId(accountEntryId, start, end);
	}

	@Override
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountUserId(
			long accountUserId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountUserId,
			AccountActionKeys.VIEW_USERS);

		return accountEntryUserRelLocalService.
			getAccountEntryUserRelsByAccountUserId(accountUserId);
	}

	@Override
	public long getAccountEntryUserRelsCountByAccountEntryId(
			long accountEntryId)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.VIEW_USERS);

		return accountEntryUserRelLocalService.
			getAccountEntryUserRelsCountByAccountEntryId(accountEntryId);
	}

	@Override
	public void inviteUser(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			User inviter, ServiceContext serviceContext)
		throws PortalException {

		_modelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.INVITE_USER);

		accountEntryUserRelLocalService.inviteUser(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	@Override
	public void setPersonTypeAccountEntryUser(long accountEntryId, long userId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!_modelResourcePermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.ASSIGN_USERS) ||
			!_modelResourcePermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.UNASSIGN_USERS)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(), accountEntryId,
				AccountActionKeys.ASSIGN_USERS,
				AccountActionKeys.UNASSIGN_USERS);
		}

		accountEntryUserRelLocalService.setPersonTypeAccountEntryUser(
			accountEntryId, userId);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_modelResourcePermission;

	@Reference
	private UserLocalService _userLocalService;

}
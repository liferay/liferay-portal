/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AccountEntryUserRelService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryUserRelService
 * @generated
 */
public class AccountEntryUserRelServiceWrapper
	implements AccountEntryUserRelService,
			   ServiceWrapper<AccountEntryUserRelService> {

	public AccountEntryUserRelServiceWrapper() {
		this(null);
	}

	public AccountEntryUserRelServiceWrapper(
		AccountEntryUserRelService accountEntryUserRelService) {

		_accountEntryUserRelService = accountEntryUserRelService;
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.addAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
			addAccountEntryUserRelByEmailAddress(
				long accountEntryId, String emailAddress, long[] accountRoleIds,
				String userExternalReferenceCode,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.addAccountEntryUserRelByEmailAddress(
			accountEntryId, emailAddress, accountRoleIds,
			userExternalReferenceCode, serviceContext);
	}

	@Override
	public void addAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelService.addAccountEntryUserRels(
			accountEntryId, accountUserIds);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
			addPersonTypeAccountEntryUserRel(
				long accountEntryId, long creatorUserId, String screenName,
				String emailAddress, java.util.Locale locale, String firstName,
				String middleName, String lastName, long prefixListTypeId,
				long suffixListTypeId, String jobTitle,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.addPersonTypeAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.Ticket addUserInvitationTicket(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			com.liferay.portal.kernel.model.User inviter,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.addUserInvitationTicket(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	@Override
	public void deleteAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelService.deleteAccountEntryUserRelByEmailAddress(
			accountEntryId, emailAddress);
	}

	@Override
	public void deleteAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelService.deleteAccountEntryUserRels(
			accountEntryId, accountUserIds);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
			fetchAccountEntryUserRel(long accountEntryUserRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.fetchAccountEntryUserRel(
			accountEntryUserRelId);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
			fetchAccountEntryUserRel(long accountEntryId, long accountUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.fetchAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.getAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntryUserRel>
			getAccountEntryUserRelsByAccountEntryId(long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.
			getAccountEntryUserRelsByAccountEntryId(accountEntryId);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntryUserRel>
			getAccountEntryUserRelsByAccountEntryId(
				long accountEntryId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.
			getAccountEntryUserRelsByAccountEntryId(accountEntryId, start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntryUserRel>
			getAccountEntryUserRelsByAccountUserId(long accountUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.
			getAccountEntryUserRelsByAccountUserId(accountUserId);
	}

	@Override
	public long getAccountEntryUserRelsCountByAccountEntryId(
			long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelService.
			getAccountEntryUserRelsCountByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountEntryUserRelService.getOSGiServiceIdentifier();
	}

	@Override
	public void inviteUser(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			com.liferay.portal.kernel.model.User inviter,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelService.inviteUser(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	@Override
	public void setPersonTypeAccountEntryUser(long accountEntryId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelService.setPersonTypeAccountEntryUser(
			accountEntryId, userId);
	}

	@Override
	public void updateAccountEntryUserRels(
			long[] addAccountEntryIds, long[] deleteAccountEntryIds,
			long accountUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelService.updateAccountEntryUserRels(
			addAccountEntryIds, deleteAccountEntryIds, accountUserId);
	}

	@Override
	public AccountEntryUserRelService getWrappedService() {
		return _accountEntryUserRelService;
	}

	@Override
	public void setWrappedService(
		AccountEntryUserRelService accountEntryUserRelService) {

		_accountEntryUserRelService = accountEntryUserRelService;
	}

	private AccountEntryUserRelService _accountEntryUserRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-90987801
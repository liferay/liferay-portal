/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AccountEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryService
 * @generated
 */
public class AccountEntryServiceWrapper
	implements AccountEntryService, ServiceWrapper<AccountEntryService> {

	public AccountEntryServiceWrapper() {
		this(null);
	}

	public AccountEntryServiceWrapper(AccountEntryService accountEntryService) {
		_accountEntryService = accountEntryService;
	}

	@Override
	public void activateAccountEntries(long[] accountEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryService.activateAccountEntries(accountEntryIds);
	}

	@Override
	public com.liferay.account.model.AccountEntry activateAccountEntry(
			long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.activateAccountEntry(accountEntryId);
	}

	@Override
	public com.liferay.account.model.AccountEntry addAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String email, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.addAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, domains, email, logoBytes, taxIdNumber, type, status,
			serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountEntry addOrUpdateAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String emailAddress, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.addOrUpdateAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, domains, emailAddress, logoBytes, taxIdNumber, type,
			status, serviceContext);
	}

	@Override
	public void deactivateAccountEntries(long[] accountEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryService.deactivateAccountEntries(accountEntryIds);
	}

	@Override
	public com.liferay.account.model.AccountEntry deactivateAccountEntry(
			long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.deactivateAccountEntry(accountEntryId);
	}

	@Override
	public void deleteAccountEntries(long[] accountEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryService.deleteAccountEntries(accountEntryIds);
	}

	@Override
	public void deleteAccountEntry(long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryService.deleteAccountEntry(accountEntryId);
	}

	@Override
	public com.liferay.account.model.AccountEntry fetchAccountEntry(
			long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.fetchAccountEntry(accountEntryId);
	}

	@Override
	public com.liferay.account.model.AccountEntry
			fetchAccountEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.fetchAccountEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntry>
			getAccountEntries(
				long companyId, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.account.model.AccountEntry> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.getAccountEntries(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public com.liferay.account.model.AccountEntry getAccountEntry(
			long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.getAccountEntry(accountEntryId);
	}

	@Override
	public com.liferay.account.model.AccountEntry
			getAccountEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.getAccountEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.account.model.AccountEntry getOrAddEmptyAccountEntry(
			String externalReferenceCode, String name, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.getOrAddEmptyAccountEntry(
			externalReferenceCode, name, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountEntry> searchAccountEntries(
				String keywords, java.util.LinkedHashMap<String, Object> params,
				int cur, int delta, String orderByField, boolean reverse)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.searchAccountEntries(
			keywords, params, cur, delta, orderByField, reverse);
	}

	@Override
	public com.liferay.account.model.AccountEntry updateAccountEntry(
			com.liferay.account.model.AccountEntry accountEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.updateAccountEntry(accountEntry);
	}

	@Override
	public com.liferay.account.model.AccountEntry updateAccountEntry(
			String externalReferenceCode, long accountEntryId,
			long parentAccountEntryId, String name, String description,
			boolean deleteLogo, String[] domains, String emailAddress,
			byte[] logoBytes, String taxIdNumber, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.updateAccountEntry(
			externalReferenceCode, accountEntryId, parentAccountEntryId, name,
			description, deleteLogo, domains, emailAddress, logoBytes,
			taxIdNumber, status, serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountEntry updateDefaultBillingAddressId(
			long accountEntryId, long addressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.updateDefaultBillingAddressId(
			accountEntryId, addressId);
	}

	@Override
	public com.liferay.account.model.AccountEntry
			updateDefaultShippingAddressId(long accountEntryId, long addressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.updateDefaultShippingAddressId(
			accountEntryId, addressId);
	}

	@Override
	public com.liferay.account.model.AccountEntry updateDomains(
			long accountEntryId, String[] domains)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.updateDomains(accountEntryId, domains);
	}

	@Override
	public com.liferay.account.model.AccountEntry updateExternalReferenceCode(
			long accountEntryId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.updateExternalReferenceCode(
			accountEntryId, externalReferenceCode);
	}

	@Override
	public com.liferay.account.model.AccountEntry updateRestrictMembership(
			long accountEntryId, boolean restrictMembership)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryService.updateRestrictMembership(
			accountEntryId, restrictMembership);
	}

	@Override
	public AccountEntryService getWrappedService() {
		return _accountEntryService;
	}

	@Override
	public void setWrappedService(AccountEntryService accountEntryService) {
		_accountEntryService = accountEntryService;
	}

	private AccountEntryService _accountEntryService;

}
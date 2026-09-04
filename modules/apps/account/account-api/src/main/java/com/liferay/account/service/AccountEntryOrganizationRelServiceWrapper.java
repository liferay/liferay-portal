/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AccountEntryOrganizationRelService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryOrganizationRelService
 * @generated
 */
public class AccountEntryOrganizationRelServiceWrapper
	implements AccountEntryOrganizationRelService,
			   ServiceWrapper<AccountEntryOrganizationRelService> {

	public AccountEntryOrganizationRelServiceWrapper() {
		this(null);
	}

	public AccountEntryOrganizationRelServiceWrapper(
		AccountEntryOrganizationRelService accountEntryOrganizationRelService) {

		_accountEntryOrganizationRelService =
			accountEntryOrganizationRelService;
	}

	@Override
	public com.liferay.account.model.AccountEntryOrganizationRel
			addAccountEntryOrganizationRel(
				long accountEntryId, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryOrganizationRelService.
			addAccountEntryOrganizationRel(accountEntryId, organizationId);
	}

	@Override
	public void addAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryOrganizationRelService.addAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	@Override
	public void deleteAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryOrganizationRelService.deleteAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	@Override
	public void deleteAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryOrganizationRelService.deleteAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	@Override
	public com.liferay.account.model.AccountEntryOrganizationRel
			fetchAccountEntryOrganizationRel(long accountEntryOrganizationRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryOrganizationRelService.
			fetchAccountEntryOrganizationRel(accountEntryOrganizationRelId);
	}

	@Override
	public com.liferay.account.model.AccountEntryOrganizationRel
			fetchAccountEntryOrganizationRel(
				long accountEntryId, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryOrganizationRelService.
			fetchAccountEntryOrganizationRel(accountEntryId, organizationId);
	}

	@Override
	public com.liferay.account.model.AccountEntryOrganizationRel
			getAccountEntryOrganizationRel(
				long accountEntryId, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryOrganizationRelService.
			getAccountEntryOrganizationRel(accountEntryId, organizationId);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntryOrganizationRel>
			getAccountEntryOrganizationRels(
				long accountEntryId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryOrganizationRelService.
			getAccountEntryOrganizationRels(accountEntryId, start, end);
	}

	@Override
	public int getAccountEntryOrganizationRelsCount(long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryOrganizationRelService.
			getAccountEntryOrganizationRelsCount(accountEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountEntryOrganizationRelService.getOSGiServiceIdentifier();
	}

	@Override
	public void setAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryOrganizationRelService.setAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	@Override
	public AccountEntryOrganizationRelService getWrappedService() {
		return _accountEntryOrganizationRelService;
	}

	@Override
	public void setWrappedService(
		AccountEntryOrganizationRelService accountEntryOrganizationRelService) {

		_accountEntryOrganizationRelService =
			accountEntryOrganizationRelService;
	}

	private AccountEntryOrganizationRelService
		_accountEntryOrganizationRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1957536323
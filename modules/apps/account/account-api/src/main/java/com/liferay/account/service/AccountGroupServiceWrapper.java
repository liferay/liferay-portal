/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AccountGroupService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountGroupService
 * @generated
 */
public class AccountGroupServiceWrapper
	implements AccountGroupService, ServiceWrapper<AccountGroupService> {

	public AccountGroupServiceWrapper() {
		this(null);
	}

	public AccountGroupServiceWrapper(AccountGroupService accountGroupService) {
		_accountGroupService = accountGroupService;
	}

	@Override
	public com.liferay.account.model.AccountGroup addAccountGroup(
			String externalReferenceCode, long userId, String description,
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.addAccountGroup(
			externalReferenceCode, userId, description, name, serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountGroup deleteAccountGroup(
			long accountGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.deleteAccountGroup(accountGroupId);
	}

	@Override
	public void deleteAccountGroups(long[] accountGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountGroupService.deleteAccountGroups(accountGroupIds);
	}

	@Override
	public com.liferay.account.model.AccountGroup fetchAccountGroup(
			long accountGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.fetchAccountGroup(accountGroupId);
	}

	@Override
	public com.liferay.account.model.AccountGroup
			fetchAccountGroupByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.fetchAccountGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.account.model.AccountGroup getAccountGroup(
			long accountGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.getAccountGroup(accountGroupId);
	}

	@Override
	public com.liferay.account.model.AccountGroup
			getAccountGroupByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.getAccountGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroup>
			getAccountGroupsByAccountEntryId(
				long accountEntryId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.getAccountGroupsByAccountEntryId(
			accountEntryId, start, end);
	}

	@Override
	public int getAccountGroupsCountByAccountEntryId(long accountEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.getAccountGroupsCountByAccountEntryId(
			accountEntryId);
	}

	@Override
	public com.liferay.account.model.AccountGroup getOrAddEmptyAccountGroup(
			String externalReferenceCode, String name)
		throws Exception {

		return _accountGroupService.getOrAddEmptyAccountGroup(
			externalReferenceCode, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountGroupService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountGroup> searchAccountGroups(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.account.model.AccountGroup> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.searchAccountGroups(
			companyId, keywords, start, end, orderByComparator);
	}

	@Override
	public com.liferay.account.model.AccountGroup updateAccountGroup(
			String externalReferenceCode, long accountGroupId,
			String description, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.updateAccountGroup(
			externalReferenceCode, accountGroupId, description, name,
			serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountGroup updateExternalReferenceCode(
			long accountGroupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupService.updateExternalReferenceCode(
			accountGroupId, externalReferenceCode);
	}

	@Override
	public AccountGroupService getWrappedService() {
		return _accountGroupService;
	}

	@Override
	public void setWrappedService(AccountGroupService accountGroupService) {
		_accountGroupService = accountGroupService;
	}

	private AccountGroupService _accountGroupService;

}
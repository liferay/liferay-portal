/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AccountRoleService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountRoleService
 * @generated
 */
public class AccountRoleServiceWrapper
	implements AccountRoleService, ServiceWrapper<AccountRoleService> {

	public AccountRoleServiceWrapper() {
		this(null);
	}

	public AccountRoleServiceWrapper(AccountRoleService accountRoleService) {
		_accountRoleService = accountRoleService;
	}

	@Override
	public com.liferay.account.model.AccountRole addAccountRole(
			String externalReferenceCode, long accountEntryId, String name,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleService.addAccountRole(
			externalReferenceCode, accountEntryId, name, titleMap,
			descriptionMap);
	}

	@Override
	public void associateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleService.associateUser(
			accountEntryId, accountRoleId, userId);
	}

	@Override
	public void associateUser(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleService.associateUser(
			accountEntryId, accountRoleIds, userId);
	}

	@Override
	public com.liferay.account.model.AccountRole deleteAccountRole(
			com.liferay.account.model.AccountRole accountRole)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleService.deleteAccountRole(accountRole);
	}

	@Override
	public com.liferay.account.model.AccountRole deleteAccountRole(
			long accountRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleService.deleteAccountRole(accountRoleId);
	}

	@Override
	public com.liferay.account.model.AccountRole getAccountRoleByRoleId(
			long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleService.getAccountRoleByRoleId(roleId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountRoleService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountRole> searchAccountRoles(
				long companyId, long[] accountEntryIds, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator<?>
					orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleService.searchAccountRoles(
			companyId, accountEntryIds, keywords, params, start, end,
			orderByComparator);
	}

	@Override
	public void setUserAccountRoles(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleService.setUserAccountRoles(
			accountEntryId, accountRoleIds, userId);
	}

	@Override
	public void unassociateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleService.unassociateUser(
			accountEntryId, accountRoleId, userId);
	}

	@Override
	public com.liferay.account.model.AccountRole updateAccountRole(
			long accountRoleId, String name,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleService.updateAccountRole(
			accountRoleId, name, titleMap, descriptionMap);
	}

	@Override
	public AccountRoleService getWrappedService() {
		return _accountRoleService;
	}

	@Override
	public void setWrappedService(AccountRoleService accountRoleService) {
		_accountRoleService = accountRoleService;
	}

	private AccountRoleService _accountRoleService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1608862234
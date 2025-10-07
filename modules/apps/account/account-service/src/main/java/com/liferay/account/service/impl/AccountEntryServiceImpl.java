/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.base.AccountEntryServiceBaseImpl;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.AddressService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=account",
		"json.web.service.context.path=AccountEntry"
	},
	service = AopService.class
)
public class AccountEntryServiceImpl extends AccountEntryServiceBaseImpl {

	@Override
	public void activateAccountEntries(long[] accountEntryIds)
		throws PortalException {

		for (long accountEntryId : accountEntryIds) {
			activateAccountEntry(accountEntryId);
		}
	}

	@Override
	public AccountEntry activateAccountEntry(long accountEntryId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		_accountEntryModelResourcePermission.check(
			permissionChecker, accountEntryId, ActionKeys.UPDATE);

		return _withServiceContext(
			() -> accountEntryLocalService.activateAccountEntry(accountEntryId),
			permissionChecker.getUserId());
	}

	@Override
	public AccountEntry addAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String email, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			ServiceContext serviceContext)
		throws PortalException {

		PortalPermissionUtil.check(
			getPermissionChecker(), AccountActionKeys.ADD_ACCOUNT_ENTRY);

		return accountEntryLocalService.addAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, _getManageableDomains(0L, domains), email, logoBytes,
			taxIdNumber, type, status, serviceContext);
	}

	@Override
	public AccountEntry addOrUpdateAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String emailAddress, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		AccountEntry accountEntry =
			accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, permissionChecker.getCompanyId());

		long accountEntryId = 0;

		if (accountEntry == null) {
			PortalPermissionUtil.check(
				permissionChecker, AccountActionKeys.ADD_ACCOUNT_ENTRY);
		}
		else {
			_accountEntryModelResourcePermission.check(
				permissionChecker, permissionChecker.getCompanyId(),
				ActionKeys.UPDATE);

			accountEntryId = accountEntry.getAccountEntryId();
		}

		return accountEntryLocalService.addOrUpdateAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, _getManageableDomains(accountEntryId, domains),
			emailAddress, logoBytes, taxIdNumber, type, status, serviceContext);
	}

	@Override
	public void deactivateAccountEntries(long[] accountEntryIds)
		throws PortalException {

		for (long accountEntryId : accountEntryIds) {
			deactivateAccountEntry(accountEntryId);
		}
	}

	@Override
	public AccountEntry deactivateAccountEntry(long accountEntryId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		_accountEntryModelResourcePermission.check(
			permissionChecker, accountEntryId, ActionKeys.DEACTIVATE);

		return _withServiceContext(
			() -> accountEntryLocalService.deactivateAccountEntry(
				accountEntryId),
			permissionChecker.getUserId());
	}

	@Override
	public void deleteAccountEntries(long[] accountEntryIds)
		throws PortalException {

		for (long accountEntryId : accountEntryIds) {
			deleteAccountEntry(accountEntryId);
		}
	}

	@Override
	public void deleteAccountEntry(long accountEntryId) throws PortalException {
		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, ActionKeys.DELETE);

		accountEntryLocalService.deleteAccountEntry(accountEntryId);
	}

	@Override
	public AccountEntry fetchAccountEntry(long accountEntryId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, ActionKeys.VIEW);

		return accountEntryLocalService.fetchAccountEntry(accountEntryId);
	}

	@Override
	public AccountEntry fetchAccountEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		AccountEntry accountEntry =
			accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (accountEntry != null) {
			_accountEntryModelResourcePermission.check(
				getPermissionChecker(), accountEntry.getAccountEntryId(),
				ActionKeys.VIEW);
		}

		return accountEntry;
	}

	@Override
	public List<AccountEntry> getAccountEntries(
			long companyId, int status, int start, int end,
			OrderByComparator<AccountEntry> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, AccountEntry.class.getName(), companyId,
				ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(), 0,
				ActionKeys.VIEW);
		}

		return accountEntryLocalService.getAccountEntries(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public AccountEntry getAccountEntry(long accountEntryId)
		throws PortalException {

		AccountEntry accountEntry = accountEntryLocalService.getAccountEntry(
			accountEntryId);

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, ActionKeys.VIEW);

		return accountEntry;
	}

	@Override
	public AccountEntry getAccountEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		AccountEntry accountEntry =
			accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				externalReferenceCode, companyId);

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntry.getAccountEntryId(),
			ActionKeys.VIEW);

		return accountEntry;
	}

	public AccountEntry getOrAddEmptyAccountEntry(
			String externalReferenceCode, String name, String type)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		AccountEntry accountEntry = fetchAccountEntryByExternalReferenceCode(
			externalReferenceCode, permissionChecker.getCompanyId());

		if (accountEntry != null) {
			return accountEntry;
		}

		PortalPermissionUtil.check(
			permissionChecker, AccountActionKeys.ADD_ACCOUNT_ENTRY);

		return accountEntryLocalService.getOrAddEmptyAccountEntry(
			externalReferenceCode, permissionChecker.getCompanyId(),
			permissionChecker.getUserId(), name, type);
	}

	@Override
	public BaseModelSearchResult<AccountEntry> searchAccountEntries(
			String keywords, LinkedHashMap<String, Object> params, int cur,
			int delta, String orderByField, boolean reverse)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (params == null) {
			params = new LinkedHashMap<>();
		}

		params.put("permissionUserId", permissionChecker.getUserId());

		return accountEntryLocalService.searchAccountEntries(
			permissionChecker.getCompanyId(), keywords, params, cur, delta,
			orderByField, reverse);
	}

	@Override
	public AccountEntry updateAccountEntry(AccountEntry accountEntry)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntry, ActionKeys.UPDATE);

		if (accountEntry.getDefaultBillingAddressId() > 0) {
			_validateAddressId(accountEntry.getDefaultBillingAddressId());
		}

		if (accountEntry.getDefaultShippingAddressId() > 0) {
			_validateAddressId(accountEntry.getDefaultShippingAddressId());
		}

		if (!_accountEntryModelResourcePermission.contains(
				getPermissionChecker(), accountEntry.getAccountEntryId(),
				AccountActionKeys.MANAGE_DOMAINS)) {

			AccountEntry originalAccountEntry =
				accountEntryLocalService.getAccountEntry(
					accountEntry.getAccountEntryId());

			accountEntry.setDomains(originalAccountEntry.getDomains());
			accountEntry.setRestrictMembership(
				originalAccountEntry.isRestrictMembership());
		}

		return accountEntryLocalService.updateAccountEntry(accountEntry);
	}

	@Override
	public AccountEntry updateAccountEntry(
			String externalReferenceCode, long accountEntryId,
			long parentAccountEntryId, String name, String description,
			boolean deleteLogo, String[] domains, String emailAddress,
			byte[] logoBytes, String taxIdNumber, int status,
			ServiceContext serviceContext)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, ActionKeys.UPDATE);

		return accountEntryLocalService.updateAccountEntry(
			externalReferenceCode, accountEntryId, parentAccountEntryId, name,
			description, deleteLogo,
			_getManageableDomains(accountEntryId, domains), emailAddress,
			logoBytes, taxIdNumber, status, serviceContext);
	}

	@Override
	public AccountEntry updateDefaultBillingAddressId(
			long accountEntryId, long addressId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, ActionKeys.UPDATE);

		_validateAddressId(addressId);

		return updateDefaultBillingAddressId(accountEntryId, addressId);
	}

	@Override
	public AccountEntry updateDefaultShippingAddressId(
			long accountEntryId, long addressId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, ActionKeys.UPDATE);

		_validateAddressId(addressId);

		return updateDefaultShippingAddressId(accountEntryId, addressId);
	}

	@Override
	public AccountEntry updateDomains(long accountEntryId, String[] domains)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.MANAGE_DOMAINS);

		return accountEntryLocalService.updateDomains(accountEntryId, domains);
	}

	@Override
	public AccountEntry updateExternalReferenceCode(
			long accountEntryId, String externalReferenceCode)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId, ActionKeys.UPDATE);

		return accountEntryLocalService.updateExternalReferenceCode(
			accountEntryId, externalReferenceCode);
	}

	@Override
	public AccountEntry updateRestrictMembership(
			long accountEntryId, boolean restrictMembership)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.MANAGE_DOMAINS);

		return accountEntryLocalService.updateRestrictMembership(
			accountEntryId, restrictMembership);
	}

	private String[] _getManageableDomains(
			long accountEntryId, String[] domains)
		throws PortalException {

		if (_accountEntryModelResourcePermission.contains(
				getPermissionChecker(), accountEntryId,
				AccountActionKeys.MANAGE_DOMAINS)) {

			return domains;
		}

		return null;
	}

	private void _validateAddressId(long addressId) throws PortalException {
		if (addressId > 0) {
			_addressService.getAddress(addressId);
		}
	}

	private AccountEntry _withServiceContext(
			UnsafeSupplier<AccountEntry, PortalException> unsafeSupplier,
			long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(userId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return unsafeSupplier.get();
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AddressService _addressService;

}
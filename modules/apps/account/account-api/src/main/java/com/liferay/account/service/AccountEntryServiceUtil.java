/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for AccountEntry. This utility wraps
 * <code>com.liferay.account.service.impl.AccountEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryService
 * @generated
 */
public class AccountEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void activateAccountEntries(long[] accountEntryIds)
		throws PortalException {

		getService().activateAccountEntries(accountEntryIds);
	}

	public static AccountEntry activateAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().activateAccountEntry(accountEntryId);
	}

	public static AccountEntry addAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String email, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, domains, email, logoBytes, taxIdNumber, type, status,
			serviceContext);
	}

	public static AccountEntry addOrUpdateAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String emailAddress, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, domains, emailAddress, logoBytes, taxIdNumber, type,
			status, serviceContext);
	}

	public static void deactivateAccountEntries(long[] accountEntryIds)
		throws PortalException {

		getService().deactivateAccountEntries(accountEntryIds);
	}

	public static AccountEntry deactivateAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().deactivateAccountEntry(accountEntryId);
	}

	public static void deleteAccountEntries(long[] accountEntryIds)
		throws PortalException {

		getService().deleteAccountEntries(accountEntryIds);
	}

	public static void deleteAccountEntry(long accountEntryId)
		throws PortalException {

		getService().deleteAccountEntry(accountEntryId);
	}

	public static AccountEntry fetchAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().fetchAccountEntry(accountEntryId);
	}

	public static AccountEntry fetchAccountEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchAccountEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<AccountEntry> getAccountEntries(
			long companyId, int status, int start, int end,
			OrderByComparator<AccountEntry> orderByComparator)
		throws PortalException {

		return getService().getAccountEntries(
			companyId, status, start, end, orderByComparator);
	}

	public static AccountEntry getAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().getAccountEntry(accountEntryId);
	}

	public static AccountEntry getAccountEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getAccountEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static AccountEntry getOrAddEmptyAccountEntry(
			String externalReferenceCode, String name, String type)
		throws PortalException {

		return getService().getOrAddEmptyAccountEntry(
			externalReferenceCode, name, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AccountEntry> searchAccountEntries(
				String keywords, java.util.LinkedHashMap<String, Object> params,
				int cur, int delta, String orderByField, boolean reverse)
			throws PortalException {

		return getService().searchAccountEntries(
			keywords, params, cur, delta, orderByField, reverse);
	}

	public static AccountEntry updateAccountEntry(AccountEntry accountEntry)
		throws PortalException {

		return getService().updateAccountEntry(accountEntry);
	}

	public static AccountEntry updateAccountEntry(
			String externalReferenceCode, long accountEntryId,
			long parentAccountEntryId, String name, String description,
			boolean deleteLogo, String[] domains, String emailAddress,
			byte[] logoBytes, String taxIdNumber, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAccountEntry(
			externalReferenceCode, accountEntryId, parentAccountEntryId, name,
			description, deleteLogo, domains, emailAddress, logoBytes,
			taxIdNumber, status, serviceContext);
	}

	public static AccountEntry updateDefaultBillingAddressId(
			long accountEntryId, long addressId)
		throws PortalException {

		return getService().updateDefaultBillingAddressId(
			accountEntryId, addressId);
	}

	public static AccountEntry updateDefaultShippingAddressId(
			long accountEntryId, long addressId)
		throws PortalException {

		return getService().updateDefaultShippingAddressId(
			accountEntryId, addressId);
	}

	public static AccountEntry updateDomains(
			long accountEntryId, String[] domains)
		throws PortalException {

		return getService().updateDomains(accountEntryId, domains);
	}

	public static AccountEntry updateExternalReferenceCode(
			long accountEntryId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			accountEntryId, externalReferenceCode);
	}

	public static AccountEntry updateRestrictMembership(
			long accountEntryId, boolean restrictMembership)
		throws PortalException {

		return getService().updateRestrictMembership(
			accountEntryId, restrictMembership);
	}

	public static AccountEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountEntryService> _serviceSnapshot =
		new Snapshot<>(
			AccountEntryServiceUtil.class, AccountEntryService.class);

}
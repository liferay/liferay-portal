/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AccountEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AccountEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the account entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AccountEntryServiceUtil} if injection and service tracking are not available.
	 */
	public void activateAccountEntries(long[] accountEntryIds)
		throws PortalException;

	public AccountEntry activateAccountEntry(long accountEntryId)
		throws PortalException;

	public AccountEntry addAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String email, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public AccountEntry addOrUpdateAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String emailAddress, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public void deactivateAccountEntries(long[] accountEntryIds)
		throws PortalException;

	public AccountEntry deactivateAccountEntry(long accountEntryId)
		throws PortalException;

	public void deleteAccountEntries(long[] accountEntryIds)
		throws PortalException;

	public void deleteAccountEntry(long accountEntryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntry fetchAccountEntry(long accountEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntry fetchAccountEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntry> getAccountEntries(
			long companyId, int status, int start, int end,
			OrderByComparator<AccountEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntry getAccountEntry(long accountEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntry getAccountEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntry getOrAddEmptyAccountEntry(
			String externalReferenceCode, String name, String type)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AccountEntry> searchAccountEntries(
			String keywords, LinkedHashMap<String, Object> params, int cur,
			int delta, String orderByField, boolean reverse)
		throws PortalException;

	public AccountEntry updateAccountEntry(AccountEntry accountEntry)
		throws PortalException;

	public AccountEntry updateAccountEntry(
			String externalReferenceCode, long accountEntryId,
			long parentAccountEntryId, String name, String description,
			boolean deleteLogo, String[] domains, String emailAddress,
			byte[] logoBytes, String taxIdNumber, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public AccountEntry updateDefaultBillingAddressId(
			long accountEntryId, long addressId)
		throws PortalException;

	public AccountEntry updateDefaultShippingAddressId(
			long accountEntryId, long addressId)
		throws PortalException;

	public AccountEntry updateDomains(long accountEntryId, String[] domains)
		throws PortalException;

	public AccountEntry updateExternalReferenceCode(
			long accountEntryId, String externalReferenceCode)
		throws PortalException;

	public AccountEntry updateRestrictMembership(
			long accountEntryId, boolean restrictMembership)
		throws PortalException;

}
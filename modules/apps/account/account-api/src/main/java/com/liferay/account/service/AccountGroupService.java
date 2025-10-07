/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountGroup;
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

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AccountGroup. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountGroupServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AccountGroupService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountGroupServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the account group remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AccountGroupServiceUtil} if injection and service tracking are not available.
	 */
	public AccountGroup addAccountGroup(
			String externalReferenceCode, long userId, String description,
			String name, ServiceContext serviceContext)
		throws PortalException;

	public AccountGroup deleteAccountGroup(long accountGroupId)
		throws PortalException;

	public void deleteAccountGroups(long[] accountGroupIds)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountGroup fetchAccountGroup(long accountGroupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountGroup fetchAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountGroup getAccountGroup(long accountGroupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountGroup getAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountGroup> getAccountGroupsByAccountEntryId(
			long accountEntryId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAccountGroupsCountByAccountEntryId(long accountEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountGroup getOrAddEmptyAccountGroup(
			String externalReferenceCode, String name)
		throws Exception;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AccountGroup> searchAccountGroups(
			long companyId, String keywords, int start, int end,
			OrderByComparator<AccountGroup> orderByComparator)
		throws PortalException;

	public AccountGroup updateAccountGroup(
			String externalReferenceCode, long accountGroupId,
			String description, String name, ServiceContext serviceContext)
		throws PortalException;

	public AccountGroup updateExternalReferenceCode(
			long accountGroupId, String externalReferenceCode)
		throws PortalException;

}
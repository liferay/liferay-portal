/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountRole;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AccountRole. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountRoleServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AccountRoleService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountRoleServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the account role remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AccountRoleServiceUtil} if injection and service tracking are not available.
	 */
	public AccountRole addAccountRole(
			String externalReferenceCode, long accountEntryId, String name,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap)
		throws PortalException;

	public void associateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException;

	public void associateUser(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException;

	public AccountRole deleteAccountRole(AccountRole accountRole)
		throws PortalException;

	public AccountRole deleteAccountRole(long accountRoleId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountRole getAccountRoleByRoleId(long roleId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AccountRole> searchAccountRoles(
			long companyId, long[] accountEntryIds, String keywords,
			LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException;

	public void setUserAccountRoles(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException;

	public void unassociateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException;

	public AccountRole updateAccountRole(
			long accountRoleId, String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-767151962
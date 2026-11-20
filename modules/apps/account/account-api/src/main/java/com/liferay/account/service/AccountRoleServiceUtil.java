/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountRole;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Map;

/**
 * Provides the remote service utility for AccountRole. This utility wraps
 * <code>com.liferay.account.service.impl.AccountRoleServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountRoleService
 * @generated
 */
public class AccountRoleServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountRoleServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AccountRole addAccountRole(
			String externalReferenceCode, long accountEntryId, String name,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap)
		throws PortalException {

		return getService().addAccountRole(
			externalReferenceCode, accountEntryId, name, titleMap,
			descriptionMap);
	}

	public static void associateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		getService().associateUser(accountEntryId, accountRoleId, userId);
	}

	public static void associateUser(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException {

		getService().associateUser(accountEntryId, accountRoleIds, userId);
	}

	public static AccountRole deleteAccountRole(AccountRole accountRole)
		throws PortalException {

		return getService().deleteAccountRole(accountRole);
	}

	public static AccountRole deleteAccountRole(long accountRoleId)
		throws PortalException {

		return getService().deleteAccountRole(accountRoleId);
	}

	public static AccountRole getAccountRoleByRoleId(long roleId)
		throws PortalException {

		return getService().getAccountRoleByRoleId(roleId);
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
		<AccountRole> searchAccountRoles(
				long companyId, long[] accountEntryIds, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, OrderByComparator<?> orderByComparator)
			throws PortalException {

		return getService().searchAccountRoles(
			companyId, accountEntryIds, keywords, params, start, end,
			orderByComparator);
	}

	public static void setUserAccountRoles(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException {

		getService().setUserAccountRoles(
			accountEntryId, accountRoleIds, userId);
	}

	public static void unassociateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		getService().unassociateUser(accountEntryId, accountRoleId, userId);
	}

	public static AccountRole updateAccountRole(
			long accountRoleId, String name,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap)
		throws PortalException {

		return getService().updateAccountRole(
			accountRoleId, name, titleMap, descriptionMap);
	}

	public static AccountRoleService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountRoleService> _serviceSnapshot =
		new Snapshot<>(AccountRoleServiceUtil.class, AccountRoleService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1658161794
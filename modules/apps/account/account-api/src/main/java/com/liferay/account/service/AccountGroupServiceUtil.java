/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountGroup;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for AccountGroup. This utility wraps
 * <code>com.liferay.account.service.impl.AccountGroupServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountGroupService
 * @generated
 */
public class AccountGroupServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountGroupServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AccountGroup addAccountGroup(
			String externalReferenceCode, long userId, String description,
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountGroup(
			externalReferenceCode, userId, description, name, serviceContext);
	}

	public static AccountGroup deleteAccountGroup(long accountGroupId)
		throws PortalException {

		return getService().deleteAccountGroup(accountGroupId);
	}

	public static void deleteAccountGroups(long[] accountGroupIds)
		throws PortalException {

		getService().deleteAccountGroups(accountGroupIds);
	}

	public static AccountGroup fetchAccountGroup(long accountGroupId)
		throws PortalException {

		return getService().fetchAccountGroup(accountGroupId);
	}

	public static AccountGroup fetchAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchAccountGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static AccountGroup getAccountGroup(long accountGroupId)
		throws PortalException {

		return getService().getAccountGroup(accountGroupId);
	}

	public static AccountGroup getAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getAccountGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<AccountGroup> getAccountGroupsByAccountEntryId(
			long accountEntryId, int start, int end)
		throws PortalException {

		return getService().getAccountGroupsByAccountEntryId(
			accountEntryId, start, end);
	}

	public static int getAccountGroupsCountByAccountEntryId(long accountEntryId)
		throws PortalException {

		return getService().getAccountGroupsCountByAccountEntryId(
			accountEntryId);
	}

	public static AccountGroup getOrAddEmptyAccountGroup(
			String externalReferenceCode, String name)
		throws Exception {

		return getService().getOrAddEmptyAccountGroup(
			externalReferenceCode, name);
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
		<AccountGroup> searchAccountGroups(
				long companyId, String keywords, int start, int end,
				OrderByComparator<AccountGroup> orderByComparator)
			throws PortalException {

		return getService().searchAccountGroups(
			companyId, keywords, start, end, orderByComparator);
	}

	public static AccountGroup updateAccountGroup(
			String externalReferenceCode, long accountGroupId,
			String description, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAccountGroup(
			externalReferenceCode, accountGroupId, description, name,
			serviceContext);
	}

	public static AccountGroup updateExternalReferenceCode(
			long accountGroupId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			accountGroupId, externalReferenceCode);
	}

	public static AccountGroupService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountGroupService> _serviceSnapshot =
		new Snapshot<>(
			AccountGroupServiceUtil.class, AccountGroupService.class);

}
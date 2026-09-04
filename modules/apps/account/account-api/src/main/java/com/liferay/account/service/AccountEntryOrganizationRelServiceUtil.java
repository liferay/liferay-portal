/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for AccountEntryOrganizationRel. This utility wraps
 * <code>com.liferay.account.service.impl.AccountEntryOrganizationRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryOrganizationRelService
 * @generated
 */
public class AccountEntryOrganizationRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryOrganizationRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AccountEntryOrganizationRel addAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		return getService().addAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	public static void addAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		getService().addAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	public static void deleteAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		getService().deleteAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	public static void deleteAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		getService().deleteAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	public static AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
			long accountEntryOrganizationRelId)
		throws PortalException {

		return getService().fetchAccountEntryOrganizationRel(
			accountEntryOrganizationRelId);
	}

	public static AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		return getService().fetchAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	public static AccountEntryOrganizationRel getAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		return getService().getAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	public static List<AccountEntryOrganizationRel>
			getAccountEntryOrganizationRels(
				long accountEntryId, int start, int end)
		throws PortalException {

		return getService().getAccountEntryOrganizationRels(
			accountEntryId, start, end);
	}

	public static int getAccountEntryOrganizationRelsCount(long accountEntryId)
		throws PortalException {

		return getService().getAccountEntryOrganizationRelsCount(
			accountEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static void setAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		getService().setAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	public static AccountEntryOrganizationRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountEntryOrganizationRelService>
		_serviceSnapshot = new Snapshot<>(
			AccountEntryOrganizationRelServiceUtil.class,
			AccountEntryOrganizationRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:990917079
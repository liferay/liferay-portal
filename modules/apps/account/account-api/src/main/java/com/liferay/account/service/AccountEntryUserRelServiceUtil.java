/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for AccountEntryUserRel. This utility wraps
 * <code>com.liferay.account.service.impl.AccountEntryUserRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryUserRelService
 * @generated
 */
public class AccountEntryUserRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryUserRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	public static AccountEntryUserRel addAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress, long[] accountRoleIds,
			String userExternalReferenceCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountEntryUserRelByEmailAddress(
			accountEntryId, emailAddress, accountRoleIds,
			userExternalReferenceCode, serviceContext);
	}

	public static void addAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		getService().addAccountEntryUserRels(accountEntryId, accountUserIds);
	}

	public static AccountEntryUserRel addPersonTypeAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addPersonTypeAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	public static com.liferay.portal.kernel.model.Ticket
			addUserInvitationTicket(
				long accountEntryId, long[] accountRoleIds, String emailAddress,
				com.liferay.portal.kernel.model.User inviter,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserInvitationTicket(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	public static void deleteAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress)
		throws PortalException {

		getService().deleteAccountEntryUserRelByEmailAddress(
			accountEntryId, emailAddress);
	}

	public static void deleteAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		getService().deleteAccountEntryUserRels(accountEntryId, accountUserIds);
	}

	public static AccountEntryUserRel fetchAccountEntryUserRel(
			long accountEntryUserRelId)
		throws PortalException {

		return getService().fetchAccountEntryUserRel(accountEntryUserRelId);
	}

	public static AccountEntryUserRel fetchAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		return getService().fetchAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	public static AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		return getService().getAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	public static List<AccountEntryUserRel>
			getAccountEntryUserRelsByAccountEntryId(long accountEntryId)
		throws PortalException {

		return getService().getAccountEntryUserRelsByAccountEntryId(
			accountEntryId);
	}

	public static List<AccountEntryUserRel>
			getAccountEntryUserRelsByAccountEntryId(
				long accountEntryId, int start, int end)
		throws PortalException {

		return getService().getAccountEntryUserRelsByAccountEntryId(
			accountEntryId, start, end);
	}

	public static List<AccountEntryUserRel>
			getAccountEntryUserRelsByAccountUserId(long accountUserId)
		throws PortalException {

		return getService().getAccountEntryUserRelsByAccountUserId(
			accountUserId);
	}

	public static long getAccountEntryUserRelsCountByAccountEntryId(
			long accountEntryId)
		throws PortalException {

		return getService().getAccountEntryUserRelsCountByAccountEntryId(
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

	public static void inviteUser(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			com.liferay.portal.kernel.model.User inviter,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().inviteUser(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	public static void setPersonTypeAccountEntryUser(
			long accountEntryId, long userId)
		throws PortalException {

		getService().setPersonTypeAccountEntryUser(accountEntryId, userId);
	}

	public static void updateAccountEntryUserRels(
			long[] addAccountEntryIds, long[] deleteAccountEntryIds,
			long accountUserId)
		throws PortalException {

		getService().updateAccountEntryUserRels(
			addAccountEntryIds, deleteAccountEntryIds, accountUserId);
	}

	public static AccountEntryUserRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountEntryUserRelService> _serviceSnapshot =
		new Snapshot<>(
			AccountEntryUserRelServiceUtil.class,
			AccountEntryUserRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1357081817
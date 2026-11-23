/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;
import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AccountEntryUserRel. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryUserRelServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AccountEntryUserRelService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryUserRelServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the account entry user rel remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AccountEntryUserRelServiceUtil} if injection and service tracking are not available.
	 */
	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			ServiceContext serviceContext)
		throws PortalException;

	public AccountEntryUserRel addAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress, long[] accountRoleIds,
			String userExternalReferenceCode, ServiceContext serviceContext)
		throws PortalException;

	public void addAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException;

	public AccountEntryUserRel addPersonTypeAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			ServiceContext serviceContext)
		throws PortalException;

	public Ticket addUserInvitationTicket(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			User inviter, ServiceContext serviceContext)
		throws PortalException;

	public void deleteAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress)
		throws PortalException;

	public void deleteAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryUserRel fetchAccountEntryUserRel(
			long accountEntryUserRelId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryUserRel fetchAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountEntryId(
			long accountEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountEntryId(
			long accountEntryId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountUserId(
			long accountUserId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getAccountEntryUserRelsCountByAccountEntryId(
			long accountEntryId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public void inviteUser(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			User inviter, ServiceContext serviceContext)
		throws PortalException;

	public void setPersonTypeAccountEntryUser(long accountEntryId, long userId)
		throws PortalException;

	public void updateAccountEntryUserRels(
			long[] addAccountEntryIds, long[] deleteAccountEntryIds,
			long accountUserId)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1889984070
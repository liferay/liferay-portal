/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AccountEntryOrganizationRel. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryOrganizationRelServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AccountEntryOrganizationRelService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryOrganizationRelServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the account entry organization rel remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AccountEntryOrganizationRelServiceUtil} if injection and service tracking are not available.
	 */
	public AccountEntryOrganizationRel addAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException;

	public void addAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException;

	public void deleteAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException;

	public void deleteAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
			long accountEntryOrganizationRelId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountEntryOrganizationRel getAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AccountEntryOrganizationRel> getAccountEntryOrganizationRels(
			long accountEntryId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAccountEntryOrganizationRelsCount(long accountEntryId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public void setAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1046664682
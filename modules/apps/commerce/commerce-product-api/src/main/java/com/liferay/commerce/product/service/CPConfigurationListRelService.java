/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CPConfigurationListRel. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see CPConfigurationListRelServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CPConfigurationListRelService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationListRelServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cp configuration list rel remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CPConfigurationListRelServiceUtil} if injection and service tracking are not available.
	 */
	public CPConfigurationListRel addCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws PortalException;

	public CPConfigurationListRel deleteCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws PortalException;

	public void deleteCPConfigurationListRels(long cpConfigurationListId)
		throws PortalException;

	public void deleteCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPConfigurationListRel fetchCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPConfigurationListRel> getAccountEntryCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAccountEntryCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPConfigurationListRel> getAccountGroupCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAccountGroupCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPConfigurationListRel>
			getCommerceOrderTypeCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderTypeCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPConfigurationListRel getCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId, int start, int end,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId, int start, int end,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPConfigurationListRelsCount(long cpConfigurationListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPConfigurationListRelsCount(
			String className, long cpConfigurationListId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

}
// LIFERAY-SERVICE-BUILDER-HASH:-2114435550
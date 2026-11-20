/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
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
 * Provides the remote service interface for CommercePricingClassCPDefinitionRel. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassCPDefinitionRelServiceUtil
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
public interface CommercePricingClassCPDefinitionRelService
	extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.pricing.service.impl.CommercePricingClassCPDefinitionRelServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce pricing class cp definition rel remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommercePricingClassCPDefinitionRelServiceUtil} if injection and service tracking are not available.
	 */
	public CommercePricingClassCPDefinitionRel
			addCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId,
				ServiceContext serviceContext)
		throws PortalException;

	public CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				CommercePricingClassCPDefinitionRel
					commercePricingClassCPDefinitionRel)
		throws PortalException;

	public CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				long commercePricingClassCPDefinitionRelId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePricingClassCPDefinitionRel
			fetchCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePricingClassCPDefinitionRel
			getCommercePricingClassCPDefinitionRel(
				long commercePricingClassCPDefinitionRelId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePricingClassCPDefinitionRel>
			getCommercePricingClassCPDefinitionRelByClassId(
				long commercePricingClassId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePricingClassCPDefinitionRel>
			getCommercePricingClassCPDefinitionRels(
				long commercePricingClassId, int start, int end,
				OrderByComparator<CommercePricingClassCPDefinitionRel>
					orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePricingClassCPDefinitionRelsCount(
			long commercePricingClassId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePricingClassCPDefinitionRelsCount(
		long commercePricingClassId, String name, String languageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getCPDefinitionIds(long commercePricingClassId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePricingClassCPDefinitionRel>
			searchByCommercePricingClassId(
				long commercePricingClassId, String name, String languageId,
				int start, int end)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:809586090
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceAvailabilityEstimate;
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
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CommerceAvailabilityEstimate. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimateServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceAvailabilityEstimateService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceAvailabilityEstimateServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce availability estimate remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceAvailabilityEstimateServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceAvailabilityEstimate addCommerceAvailabilityEstimate(
			String externalReferenceCode, Map<Locale, String> titleMap,
			double priority, ServiceContext serviceContext)
		throws PortalException;

	public void deleteCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceAvailabilityEstimate
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceAvailabilityEstimate getCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAvailabilityEstimate> getCommerceAvailabilityEstimates(
			long companyId, int start, int end,
			OrderByComparator<CommerceAvailabilityEstimate> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceAvailabilityEstimatesCount(long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceAvailabilityEstimate
			getOrAddEmptyCommerceAvailabilityEstimate(
				String externalReferenceCode)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public CommerceAvailabilityEstimate updateCommerceAvailabilityEstimate(
			String externalReferenceCode, long commerceAvailabilityEstimateId,
			Map<Locale, String> titleMap, double priority,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2052027770
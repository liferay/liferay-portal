/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.math.BigDecimal;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CPConfigurationEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see CPConfigurationEntryServiceUtil
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
public interface CPConfigurationEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cp configuration entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CPConfigurationEntryServiceUtil} if injection and service tracking are not available.
	 */
	public CPConfigurationEntry addCPConfigurationEntry(
			String externalReferenceCode, long groupId, long classNameId,
			long classPK, long cpConfigurationListId, long cpTaxCategoryId,
			String allowedOrderQuantities, boolean backOrders,
			long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			BigDecimal maxOrderQuantity, BigDecimal minOrderQuantity,
			BigDecimal minStockQuantity, BigDecimal multipleOrderQuantity,
			boolean purchasable, boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException;

	public void deleteCPConfigurationEntry(long cpConfigurationEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPConfigurationEntry getCPConfigurationEntry(
			long cpConfigurationEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPConfigurationEntry getCPConfigurationEntry(
			long classNameId, long classPK, long cpConfigurationListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPConfigurationEntry getCPConfigurationEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public CPConfigurationEntry updateCPConfigurationEntry(
			String externalReferenceCode, long cpConfigurationEntryId,
			long cpTaxCategoryId, String allowedOrderQuantities,
			boolean backOrders, long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			BigDecimal maxOrderQuantity, BigDecimal minOrderQuantity,
			BigDecimal minStockQuantity, BigDecimal multipleOrderQuantity,
			boolean purchasable, boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException;

}
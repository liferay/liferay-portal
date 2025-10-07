/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the remote service utility for CPConfigurationEntry. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPConfigurationEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPConfigurationEntryService
 * @generated
 */
public class CPConfigurationEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPConfigurationEntry addCPConfigurationEntry(
			String externalReferenceCode, long groupId, long classNameId,
			long classPK, long cpConfigurationListId, long cpTaxCategoryId,
			String allowedOrderQuantities, boolean backOrders,
			long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			java.math.BigDecimal maxOrderQuantity,
			java.math.BigDecimal minOrderQuantity,
			java.math.BigDecimal minStockQuantity,
			java.math.BigDecimal multipleOrderQuantity, boolean purchasable,
			boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException {

		return getService().addCPConfigurationEntry(
			externalReferenceCode, groupId, classNameId, classPK,
			cpConfigurationListId, cpTaxCategoryId, allowedOrderQuantities,
			backOrders, commerceAvailabilityEstimateId,
			cpDefinitionInventoryEngine, depth, displayAvailability,
			displayStockQuantity, freeShipping, height, lowStockActivity,
			maxOrderQuantity, minOrderQuantity, minStockQuantity,
			multipleOrderQuantity, purchasable, shippable, shippingExtraPrice,
			shipSeparately, taxExempt, weight, width);
	}

	public static void deleteCPConfigurationEntry(long cpConfigurationEntryId)
		throws PortalException {

		getService().deleteCPConfigurationEntry(cpConfigurationEntryId);
	}

	public static CPConfigurationEntry getCPConfigurationEntry(
			long cpConfigurationEntryId)
		throws PortalException {

		return getService().getCPConfigurationEntry(cpConfigurationEntryId);
	}

	public static CPConfigurationEntry getCPConfigurationEntry(
			long classNameId, long classPK, long cpConfigurationListId)
		throws PortalException {

		return getService().getCPConfigurationEntry(
			classNameId, classPK, cpConfigurationListId);
	}

	public static CPConfigurationEntry
			getCPConfigurationEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPConfigurationEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CPConfigurationEntry updateCPConfigurationEntry(
			String externalReferenceCode, long cpConfigurationEntryId,
			long cpTaxCategoryId, String allowedOrderQuantities,
			boolean backOrders, long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			java.math.BigDecimal maxOrderQuantity,
			java.math.BigDecimal minOrderQuantity,
			java.math.BigDecimal minStockQuantity,
			java.math.BigDecimal multipleOrderQuantity, boolean purchasable,
			boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, double weight,
			double width)
		throws PortalException {

		return getService().updateCPConfigurationEntry(
			externalReferenceCode, cpConfigurationEntryId, cpTaxCategoryId,
			allowedOrderQuantities, backOrders, commerceAvailabilityEstimateId,
			cpDefinitionInventoryEngine, depth, displayAvailability,
			displayStockQuantity, freeShipping, height, lowStockActivity,
			maxOrderQuantity, minOrderQuantity, minStockQuantity,
			multipleOrderQuantity, purchasable, shippable, shippingExtraPrice,
			shipSeparately, taxExempt, weight, width);
	}

	public static CPConfigurationEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPConfigurationEntryService>
		_serviceSnapshot = new Snapshot<>(
			CPConfigurationEntryServiceUtil.class,
			CPConfigurationEntryService.class);

}
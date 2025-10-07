/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.model.CPDAvailabilityEstimate;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.service.CPDAvailabilityEstimateService;
import com.liferay.commerce.service.CPDefinitionInventoryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

/**
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
public class ProductConfigurationUtil {

	public static String getAllowedOrderQuantities(
		BigDecimal[] allowedOrderQuantities,
		String defaultAllowedOrderQuantities) {

		if (allowedOrderQuantities != null) {
			return StringUtil.merge(allowedOrderQuantities, StringPool.SPACE);
		}

		if (Validator.isNull(defaultAllowedOrderQuantities)) {
			return null;
		}

		return defaultAllowedOrderQuantities;
	}

	public static void updateCPDAvailabilityEstimate(
			CPDAvailabilityEstimateService cpdAvailabilityEstimateService,
			ProductConfiguration productConfiguration, long cpDefinitionId)
		throws PortalException {

		long commerceAvailabilityEstimateId = 0;

		CPDAvailabilityEstimate cpdAvailabilityEstimate =
			cpdAvailabilityEstimateService.
				fetchCPDAvailabilityEstimateByCPDefinitionId(cpDefinitionId);

		if (cpdAvailabilityEstimate != null) {
			commerceAvailabilityEstimateId =
				cpdAvailabilityEstimate.getCommerceAvailabilityEstimateId();
		}

		cpdAvailabilityEstimateService.updateCPDAvailabilityEstimate(
			0, cpDefinitionId,
			GetterUtil.get(
				productConfiguration.getAvailabilityEstimateId(),
				commerceAvailabilityEstimateId));
	}

	public static CPDefinitionInventory updateCPDefinitionInventory(
			CPDefinitionInventoryService cpDefinitionInventoryService,
			ProductConfiguration productConfiguration, long cpDefinitionId)
		throws PortalException {

		CPDefinitionInventory cpDefinitionInventory =
			cpDefinitionInventoryService.
				fetchCPDefinitionInventoryByCPDefinitionId(cpDefinitionId);

		return cpDefinitionInventoryService.updateCPDefinitionInventory(
			cpDefinitionInventory.getCPDefinitionInventoryId(),
			GetterUtil.get(
				productConfiguration.getInventoryEngine(),
				cpDefinitionInventory.getCPDefinitionInventoryEngine()),
			productConfiguration.getLowStockAction(),
			GetterUtil.get(
				productConfiguration.getDisplayAvailability(),
				cpDefinitionInventory.isDisplayAvailability()),
			GetterUtil.get(
				productConfiguration.getDisplayStockQuantity(),
				cpDefinitionInventory.isDisplayStockQuantity()),
			BigDecimalUtil.get(
				productConfiguration.getMinStockQuantity(),
				cpDefinitionInventory.getMinStockQuantity()),
			GetterUtil.get(
				productConfiguration.getAllowBackOrder(),
				cpDefinitionInventory.isBackOrders()),
			BigDecimalUtil.get(
				productConfiguration.getMinOrderQuantity(),
				cpDefinitionInventory.getMinOrderQuantity()),
			BigDecimalUtil.get(
				productConfiguration.getMaxOrderQuantity(),
				cpDefinitionInventory.getMaxOrderQuantity()),
			getAllowedOrderQuantities(
				productConfiguration.getAllowedOrderQuantities(),
				cpDefinitionInventory.getAllowedOrderQuantities()),
			BigDecimalUtil.get(
				productConfiguration.getMultipleOrderQuantity(),
				cpDefinitionInventory.getMultipleOrderQuantity()));
	}

}
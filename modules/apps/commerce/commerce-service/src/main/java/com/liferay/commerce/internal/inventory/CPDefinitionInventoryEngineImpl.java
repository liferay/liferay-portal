/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.inventory;

import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.model.CPDAvailabilityEstimate;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.service.CPDAvailabilityEstimateLocalService;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.math.BigDecimal;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"cp.definition.inventory.engine.key=" + CPDefinitionInventoryEngineImpl.KEY,
		"cp.definition.inventory.engine.priority:Integer=1"
	},
	service = CPDefinitionInventoryEngine.class
)
public class CPDefinitionInventoryEngineImpl
	implements CPDefinitionInventoryEngine {

	public static final String KEY = "default";

	@Override
	public String[] getAllowedOrderQuantities(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return new String[0];
			}

			return ArrayUtil.toStringArray(
				cpDefinitionInventory.getAllowedOrderQuantitiesArray());
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return ArrayUtil.toStringArray(
			cpConfigurationEntry.getAllowedOrderQuantitiesArray());
	}

	@Override
	public String getAvailabilityEstimate(
			long cpConfigurationListId, CPInstance cpInstance, Locale locale)
		throws PortalException {

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CPDAvailabilityEstimate cpDefinitionAvailabilityEstimate =
			_cpdAvailabilityEstimateLocalService.
				fetchCPDAvailabilityEstimateByCProductId(
					cpDefinition.getCProductId());

		if (cpDefinitionAvailabilityEstimate == null) {
			return StringPool.BLANK;
		}

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			cpDefinitionAvailabilityEstimate.getCommerceAvailabilityEstimate();

		if (commerceAvailabilityEstimate == null) {
			return StringPool.BLANK;
		}

		return commerceAvailabilityEstimate.getTitle(locale);
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, KEY);
	}

	@Override
	public BigDecimal getMaxOrderQuantity(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return CPDefinitionInventoryConstants.
					DEFAULT_MAX_ORDER_QUANTITY;
			}

			return cpDefinitionInventory.getMaxOrderQuantity();
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return cpConfigurationEntry.getMaxOrderQuantity();
	}

	@Override
	public BigDecimal getMinOrderQuantity(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return CPDefinitionInventoryConstants.
					DEFAULT_MIN_ORDER_QUANTITY;
			}

			return cpDefinitionInventory.getMinOrderQuantity();
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return cpConfigurationEntry.getMinOrderQuantity();
	}

	@Override
	public BigDecimal getMinStockQuantity(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return BigDecimal.ZERO;
			}

			return cpDefinitionInventory.getMinStockQuantity();
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return cpConfigurationEntry.getMinStockQuantity();
	}

	@Override
	public BigDecimal getMultipleOrderQuantity(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return CPDefinitionInventoryConstants.
					DEFAULT_MULTIPLE_ORDER_QUANTITY;
			}

			return cpDefinitionInventory.getMultipleOrderQuantity();
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return cpConfigurationEntry.getMultipleOrderQuantity();
	}

	@Override
	public boolean isBackOrderAllowed(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return false;
			}

			return cpDefinitionInventory.isBackOrders();
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return cpConfigurationEntry.isBackOrders();
	}

	@Override
	public boolean isDisplayAvailability(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return false;
			}

			return cpDefinitionInventory.isDisplayAvailability();
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return cpConfigurationEntry.isDisplayAvailability();
	}

	@Override
	public boolean isDisplayStockQuantity(
			long cpConfigurationListId, CPInstance cpInstance)
		throws PortalException {

		if (cpConfigurationListId == 0) {
			CPDefinitionInventory cpDefinitionInventory =
				_cpDefinitionInventoryLocalService.
					fetchCPDefinitionInventoryByCPDefinitionId(
						cpInstance.getCPDefinitionId());

			if (cpDefinitionInventory == null) {
				return false;
			}

			return cpDefinitionInventory.isDisplayStockQuantity();
		}

		CPConfigurationEntry cpConfigurationEntry = _fetchCPConfigurationEntry(
			cpConfigurationListId, cpInstance.getCPDefinition());

		return cpConfigurationEntry.isDisplayStockQuantity();
	}

	private CPConfigurationEntry _fetchCPConfigurationEntry(
			long cpConfigurationListId, CPDefinition cpDefinition)
		throws PortalException {

		CPConfigurationEntry cpConfigurationEntry =
			cpDefinition.fetchCPConfigurationEntry(cpConfigurationListId);

		if (cpConfigurationEntry == null) {
			cpConfigurationEntry =
				cpDefinition.fetchMasterCPConfigurationEntry();
		}

		return cpConfigurationEntry;
	}

	@Reference
	private CPDAvailabilityEstimateLocalService
		_cpdAvailabilityEstimateLocalService;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private Language _language;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CPDAvailabilityEstimate;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.service.CPDAvailabilityEstimateLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.model.CPDefinitionInventory",
	service = DTOConverter.class
)
public class ProductConfigurationDTOConverter
	implements DTOConverter<CPDefinitionInventory, ProductConfiguration> {

	@Override
	public String getContentType() {
		return ProductConfiguration.class.getSimpleName();
	}

	@Override
	public ProductConfiguration toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			(Long)dtoConverterContext.getId());

		ProductConfigurationDTOConverterContext
			productConfigurationDTOConverterContext =
				(ProductConfigurationDTOConverterContext)dtoConverterContext;

		CommerceContext commerceContext =
			productConfigurationDTOConverterContext.getCommerceContext();

		CPConfigurationEntry cpConfigurationEntry =
			cpDefinition.fetchCPConfigurationEntry(
				commerceContext.getCPConfigurationListId(
					cpDefinition.getGroupId()));

		return new ProductConfiguration() {
			{
				setAllowBackOrder(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						return cpConfigurationEntry.isBackOrders();
					});
				setAllowedOrderQuantities(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						return cpConfigurationEntry.
							getAllowedOrderQuantitiesArray();
					});
				setAvailabilityEstimateId(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						return cpConfigurationEntry.
							getCommerceAvailabilityEstimateId();
					});
				setAvailabilityEstimateName(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						CPDAvailabilityEstimate cpdAvailabilityEstimate =
							_cpdAvailabilityEstimateLocalService.
								fetchCPDAvailabilityEstimateByCProductId(
									cpDefinition.getCProductId());

						if (cpdAvailabilityEstimate == null) {
							return null;
						}

						CommerceAvailabilityEstimate
							commerceAvailabilityEstimate =
								cpdAvailabilityEstimate.
									getCommerceAvailabilityEstimate();

						if (commerceAvailabilityEstimate == null) {
							return null;
						}

						return commerceAvailabilityEstimate.getTitle(
							dtoConverterContext.getLocale());
					});
				setInventoryEngine(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						return cpConfigurationEntry.
							getCPDefinitionInventoryEngine();
					});
				setMaxOrderQuantity(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						return BigDecimalUtil.stripTrailingZeros(
							cpConfigurationEntry.getMaxOrderQuantity());
					});
				setMinOrderQuantity(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						return BigDecimalUtil.stripTrailingZeros(
							cpConfigurationEntry.getMinOrderQuantity());
					});
				setMultipleOrderQuantity(
					() -> {
						if (cpConfigurationEntry == null) {
							return null;
						}

						return BigDecimalUtil.stripTrailingZeros(
							cpConfigurationEntry.getMultipleOrderQuantity());
					});
			}
		};
	}

	@Reference
	private CPDAvailabilityEstimateLocalService
		_cpdAvailabilityEstimateLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

}
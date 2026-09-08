/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSubscriptionConfiguration;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true", "dto.class.name=ProductSubscriptionConfiguration"
	},
	service = DTOConverter.class
)
public class ProductSubscriptionConfigurationDTOConverter
	implements DTOConverter<CPDefinition, ProductSubscriptionConfiguration> {

	@Override
	public String getContentType() {
		return ProductSubscriptionConfiguration.class.getSimpleName();
	}

	@Override
	public ProductSubscriptionConfiguration toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			(Long)dtoConverterContext.getId());

		return new ProductSubscriptionConfiguration() {
			{
				setDeliverySubscriptionEnable(
					cpDefinition::isDeliverySubscriptionEnabled);
				setDeliverySubscriptionLength(
					cpDefinition::getDeliverySubscriptionLength);
				setDeliverySubscriptionNumberOfLength(
					cpDefinition::getDeliveryMaxSubscriptionCycles);
				setDeliverySubscriptionType(
					() ->
						ProductSubscriptionConfiguration.
							DeliverySubscriptionType.create(
								cpDefinition.getDeliverySubscriptionType()));
				setDeliverySubscriptionTypeSettings(
					cpDefinition::
						getDeliverySubscriptionTypeSettingsUnicodeProperties);
				setEnable(cpDefinition::isSubscriptionEnabled);
				setLength(cpDefinition::getSubscriptionLength);
				setNumberOfLength(cpDefinition::getMaxSubscriptionCycles);
				setSubscriptionType(
					() ->
						ProductSubscriptionConfiguration.SubscriptionType.
							create(cpDefinition.getSubscriptionType()));
				setSubscriptionTypeSettings(
					cpDefinition::getSubscriptionTypeSettingsUnicodeProperties);
			}
		};
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

}
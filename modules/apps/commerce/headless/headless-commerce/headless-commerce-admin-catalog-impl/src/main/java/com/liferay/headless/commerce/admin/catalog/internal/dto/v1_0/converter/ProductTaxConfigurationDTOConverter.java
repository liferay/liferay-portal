/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=ProductTaxConfiguration",
	service = DTOConverter.class
)
public class ProductTaxConfigurationDTOConverter
	implements DTOConverter<CPDefinition, ProductTaxConfiguration> {

	@Override
	public String getContentType() {
		return ProductTaxConfiguration.class.getSimpleName();
	}

	@Override
	public ProductTaxConfiguration toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			(Long)dtoConverterContext.getId());

		return new ProductTaxConfiguration() {
			{
				setId(cpDefinition::getCPTaxCategoryId);
				setTaxable(() -> !cpDefinition.isTaxExempt());
				setTaxCategory(
					() -> _getTaxCategory(
						cpDefinition.getCPTaxCategory(),
						dtoConverterContext.getLocale()));
				setTaxCategoryExternalReferenceCode(
					() -> {
						CPTaxCategory cpTaxCategory =
							cpDefinition.getCPTaxCategory();

						if (cpTaxCategory == null) {
							return null;
						}

						return cpTaxCategory.getExternalReferenceCode();
					});
			}
		};
	}

	private String _getTaxCategory(CPTaxCategory cpTaxCategory, Locale locale) {
		if (cpTaxCategory == null) {
			return null;
		}

		return cpTaxCategory.getName(locale);
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

}
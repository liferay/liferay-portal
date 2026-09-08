/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProduct;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.pricing.model.CommercePriceModifierRel-Product"
	},
	service = DTOConverter.class
)
public class PriceModifierProductDTOConverter
	implements DTOConverter<CommercePriceModifierRel, PriceModifierProduct> {

	@Override
	public String getContentType() {
		return PriceModifierProduct.class.getSimpleName();
	}

	@Override
	public PriceModifierProduct toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceModifierRel commercePriceModifierRel =
			_commercePriceModifierRelService.getCommercePriceModifierRel(
				(Long)dtoConverterContext.getId());

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			commercePriceModifierRel.getClassPK());

		CProduct cProduct = cpDefinition.getCProduct();

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		return new PriceModifierProduct() {
			{
				setActions(dtoConverterContext::getActions);
				setPriceModifierExternalReferenceCode(
					commercePriceModifier::getExternalReferenceCode);
				setPriceModifierId(
					commercePriceModifier::getCommercePriceModifierId);
				setPriceModifierProductId(
					commercePriceModifierRel::getCommercePriceModifierRelId);
				setProductExternalReferenceCode(
					cProduct::getExternalReferenceCode);
				setProductId(cProduct::getCProductId);
			}
		};
	}

	@Reference
	private CommercePriceModifierRelService _commercePriceModifierRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

}
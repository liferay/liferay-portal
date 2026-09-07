/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProduct;
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
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscountRel-Product"
	},
	service = DTOConverter.class
)
public class DiscountProductDTOConverter
	implements DTOConverter<CommerceDiscountRel, DiscountProduct> {

	@Override
	public String getContentType() {
		return DiscountProduct.class.getSimpleName();
	}

	@Override
	public DiscountProduct toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscountRel commerceDiscountRel =
			_commerceDiscountRelService.getCommerceDiscountRel(
				(Long)dtoConverterContext.getId());

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			commerceDiscountRel.getClassPK());

		CProduct cProduct = cpDefinition.getCProduct();

		CommerceDiscount commerceDiscount =
			commerceDiscountRel.getCommerceDiscount();

		return new DiscountProduct() {
			{
				setActions(dtoConverterContext::getActions);
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
				setDiscountProductId(
					commerceDiscountRel::getCommerceDiscountRelId);
				setProductExternalReferenceCode(
					cProduct::getExternalReferenceCode);
				setProductId(cProduct::getCProductId);
			}
		};
	}

	@Reference
	private CommerceDiscountRelService _commerceDiscountRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

}
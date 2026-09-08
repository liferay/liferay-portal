/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountSku;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.util.UnicodeProperties;
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
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscountRel-Sku"
	},
	service = DTOConverter.class
)
public class DiscountSkuDTOConverter
	implements DTOConverter<CommerceDiscountRel, DiscountSku> {

	@Override
	public String getContentType() {
		return DiscountSku.class.getSimpleName();
	}

	@Override
	public DiscountSku toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscountRel commerceDiscountRel =
			_commerceDiscountRelService.getCommerceDiscountRel(
				(Long)dtoConverterContext.getId());

		CommerceDiscount commerceDiscount =
			commerceDiscountRel.getCommerceDiscount();

		CPInstance cpInstance = _cpInstanceService.getCPInstance(
			commerceDiscountRel.getClassPK());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		return new DiscountSku() {
			{
				setActions(dtoConverterContext::getActions);
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
				setDiscountSkuId(commerceDiscountRel::getCommerceDiscountRelId);
				setProductId(cpDefinition::getCPDefinitionId);
				setProductName(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getNameMap()));
				setSkuExternalReferenceCode(
					cpInstance::getExternalReferenceCode);
				setSkuId(cpInstance::getCPInstanceId);
				setUnitOfMeasureKey(
					() -> _getUnitOfMeasureKey(
						commerceDiscountRel.
							getTypeSettingsUnicodeProperties()));
			}
		};
	}

	private String _getUnitOfMeasureKey(
		UnicodeProperties typeSettingsUnicodeProperties) {

		if ((typeSettingsUnicodeProperties == null) ||
			!typeSettingsUnicodeProperties.containsKey("unitOfMeasureKey")) {

			return null;
		}

		return typeSettingsUnicodeProperties.getProperty("unitOfMeasureKey");
	}

	@Reference
	private CommerceDiscountRelService _commerceDiscountRelService;

	@Reference
	private CPInstanceService _cpInstanceService;

}
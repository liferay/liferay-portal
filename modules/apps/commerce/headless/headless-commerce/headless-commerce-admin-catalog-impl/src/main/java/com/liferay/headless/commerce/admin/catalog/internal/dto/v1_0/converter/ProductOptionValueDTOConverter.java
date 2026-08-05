/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.product.model.CPDefinitionOptionValueRel",
	service = DTOConverter.class
)
public class ProductOptionValueDTOConverter
	implements DTOConverter<CPDefinitionOptionValueRel, ProductOptionValue> {

	@Override
	public String getContentType() {
		return ProductOptionValue.class.getSimpleName();
	}

	@Override
	public ProductOptionValue toDTO(
			DTOConverterContext dtoConverterContext,
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws Exception {

		CPInstance cpInstance = cpDefinitionOptionValueRel.fetchCPInstance();

		return new ProductOptionValue() {
			{
				setDeltaPrice(cpDefinitionOptionValueRel::getPrice);
				setExternalReferenceCode(
					cpDefinitionOptionValueRel::getExternalReferenceCode);
				setId(
					cpDefinitionOptionValueRel::
						getCPDefinitionOptionValueRelId);
				setKey(cpDefinitionOptionValueRel::getKey);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinitionOptionValueRel.getNameMap()));
				setPreselected(cpDefinitionOptionValueRel::isPreselected);
				setPriority(cpDefinitionOptionValueRel::getPriority);
				setQuantity(cpDefinitionOptionValueRel::getQuantity);
				setSkuExternalReferenceCode(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getExternalReferenceCode();
					});
				setSkuId(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getCPInstanceId();
					});
				setUnitOfMeasureKey(
					cpDefinitionOptionValueRel::getUnitOfMeasureKey);
			}
		};
	}

}
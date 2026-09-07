/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.core.util.LanguageUtils;
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
		"dto.class.name=com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue"
	},
	service = DTOConverter.class
)
public class ProductSpecificationDTOConverter
	implements DTOConverter
		<CPDefinitionSpecificationOptionValue, ProductSpecification> {

	@Override
	public String getContentType() {
		return ProductSpecification.class.getSimpleName();
	}

	@Override
	public ProductSpecification toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValue(
						(Long)dtoConverterContext.getId());

		CPSpecificationOption cpSpecificationOption =
			cpDefinitionSpecificationOptionValue.getCPSpecificationOption();

		return new ProductSpecification() {
			{
				setExternalReferenceCode(
					cpDefinitionSpecificationOptionValue::
						getExternalReferenceCode);
				setId(
					cpDefinitionSpecificationOptionValue::
						getCPDefinitionSpecificationOptionValueId);
				setKey(cpDefinitionSpecificationOptionValue::getKey);
				setLabel(
					() -> LanguageUtils.getLanguageIdMap(
						cpSpecificationOption.getTitleMap()));
				setOptionCategoryExternalReferenceCode(
					() -> {
						CPOptionCategory cpOptionCategory =
							cpSpecificationOption.getCPOptionCategory();

						if (cpOptionCategory == null) {
							return null;
						}

						return cpOptionCategory.getExternalReferenceCode();
					});
				setOptionCategoryId(
					cpDefinitionSpecificationOptionValue::
						getCPOptionCategoryId);
				setPriority(cpDefinitionSpecificationOptionValue::getPriority);
				setProductId(
					() -> {
						CPDefinition cpDefinition =
							cpDefinitionSpecificationOptionValue.
								getCPDefinition();

						return cpDefinition.getCProductId();
					});
				setSpecificationExternalReferenceCode(
					cpSpecificationOption::getExternalReferenceCode);
				setSpecificationId(
					cpSpecificationOption::getCPSpecificationOptionId);
				setSpecificationKey(cpSpecificationOption::getKey);
				setSpecificationPriority(cpSpecificationOption::getPriority);
				setValue(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinitionSpecificationOptionValue.getValueMap()));
				setVisible(cpDefinitionSpecificationOptionValue::isVisible);
			}
		};
	}

	@Reference
	private CPDefinitionSpecificationOptionValueService
		_cpDefinitionSpecificationOptionValueService;

}
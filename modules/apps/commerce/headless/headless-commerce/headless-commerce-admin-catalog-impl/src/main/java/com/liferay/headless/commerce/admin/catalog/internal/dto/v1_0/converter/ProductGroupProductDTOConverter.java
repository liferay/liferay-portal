/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroupProduct;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel"
	},
	service = DTOConverter.class
)
public class ProductGroupProductDTOConverter
	implements DTOConverter
		<CommercePricingClassCPDefinitionRel, ProductGroupProduct> {

	@Override
	public String getContentType() {
		return ProductGroupProduct.class.getSimpleName();
	}

	@Override
	public ProductGroupProduct toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				_commercePricingClassCPDefinitionRelService.
					getCommercePricingClassCPDefinitionRel(
						(Long)dtoConverterContext.getId());

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			commercePricingClassCPDefinitionRel.getCPDefinitionId());

		CProduct cProduct = cpDefinition.getCProduct();

		CommercePricingClass commercePricingClass =
			commercePricingClassCPDefinitionRel.getCommercePricingClass();

		Locale locale = dtoConverterContext.getLocale();

		String languageId = _language.getLanguageId(locale);

		return new ProductGroupProduct() {
			{
				setId(
					commercePricingClassCPDefinitionRel::
						getCommercePricingClassCPDefinitionRelId);
				setProductExternalReferenceCode(
					cProduct::getExternalReferenceCode);
				setProductGroupExternalReferenceCode(
					commercePricingClass::getExternalReferenceCode);
				setProductGroupId(
					commercePricingClass::getCommercePricingClassId);
				setProductId(cProduct::getCProductId);
				setProductName(() -> cpDefinition.getName(languageId));
				setSku(() -> _getSku(cpDefinition, locale));
			}
		};
	}

	private String _getSku(CPDefinition cpDefinition, Locale locale) {
		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		if (cpInstances.isEmpty()) {
			return StringPool.BLANK;
		}

		if (cpInstances.size() > 1) {
			return _language.get(locale, "multiple-skus");
		}

		CPInstance cpInstance = cpInstances.get(0);

		return cpInstance.getSku();
	}

	@Reference
	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private Language _language;

}
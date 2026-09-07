/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroup;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Commerce.Admin.Catalog",
		"default=true",
		"dto.class.name=com.liferay.commerce.pricing.model.CommercePricingClass",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class ProductGroupDTOConverter
	implements DTOConverter<CommercePricingClass, ProductGroup> {

	@Override
	public String getContentType() {
		return ProductGroup.class.getSimpleName();
	}

	@Override
	public ProductGroup toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePricingClass commercePricingClass =
			_commercePricingClassService.getCommercePricingClass(
				(Long)dtoConverterContext.getId());

		ExpandoBridge expandoBridge = commercePricingClass.getExpandoBridge();

		return new ProductGroup() {
			{
				setCustomFields(expandoBridge::getAttributes);
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						commercePricingClass.getDescriptionMap()));
				setExternalReferenceCode(
					commercePricingClass::getExternalReferenceCode);
				setId(commercePricingClass::getCommercePricingClassId);
				setProductsCount(
					() -> _getProductsCount(
						commercePricingClass.getCommercePricingClassId()));
				setTitle(
					() -> LanguageUtils.getLanguageIdMap(
						commercePricingClass.getTitleMap()));
			}
		};
	}

	private int _getProductsCount(long commercePricingClassId)
		throws Exception {

		return _commercePricingClassCPDefinitionRelService.
			getCommercePricingClassCPDefinitionRelsCount(
				commercePricingClassId);
	}

	@Reference
	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

}
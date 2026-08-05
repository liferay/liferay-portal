/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.product.model.CPTaxCategory",
	service = DTOConverter.class
)
public class TaxCategoryDTOConverter
	implements DTOConverter<CPTaxCategory, TaxCategory> {

	@Override
	public String getContentType() {
		return Channel.class.getSimpleName();
	}

	@Override
	public TaxCategory toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPTaxCategory cpTaxCategory = _cpTaxCategoryService.getCPTaxCategory(
			(Long)dtoConverterContext.getId());

		return new TaxCategory() {
			{
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpTaxCategory.getDescriptionMap()));
				setExternalReferenceCode(
					cpTaxCategory::getExternalReferenceCode);
				setId(cpTaxCategory::getCPTaxCategoryId);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpTaxCategory.getNameMap()));
			}
		};
	}

	@Reference
	private CPTaxCategoryService _cpTaxCategoryService;

}
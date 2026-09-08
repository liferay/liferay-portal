/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionCategory;
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
		"dto.class.name=com.liferay.commerce.product.model.CPOptionCategory"
	},
	service = DTOConverter.class
)
public class OptionCategoryDTOConverter
	implements DTOConverter<CPOptionCategory, OptionCategory> {

	@Override
	public String getContentType() {
		return OptionCategory.class.getSimpleName();
	}

	@Override
	public OptionCategory toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPOptionCategory cpOptionCategory =
			_cpOptionCategoryService.getCPOptionCategory(
				(Long)dtoConverterContext.getId());

		return new OptionCategory() {
			{
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpOptionCategory.getDescriptionMap()));
				setExternalReferenceCode(
					cpOptionCategory::getExternalReferenceCode);
				setId(cpOptionCategory::getCPOptionCategoryId);
				setKey(cpOptionCategory::getKey);
				setPriority(cpOptionCategory::getPriority);
				setTitle(
					() -> LanguageUtils.getLanguageIdMap(
						cpOptionCategory.getTitleMap()));
			}
		};
	}

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

}
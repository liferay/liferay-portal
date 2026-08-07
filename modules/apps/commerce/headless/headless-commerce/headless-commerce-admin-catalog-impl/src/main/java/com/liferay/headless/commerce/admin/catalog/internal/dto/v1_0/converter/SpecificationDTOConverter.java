/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionCategory;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification",
	service = DTOConverter.class
)
public class SpecificationDTOConverter
	implements DTOConverter<CPSpecificationOption, Specification> {

	@Override
	public String getContentType() {
		return Specification.class.getSimpleName();
	}

	@Override
	public Specification toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPSpecificationOption cpSpecificationOption =
			_cpSpecificationOptionService.getCPSpecificationOption(
				(Long)dtoConverterContext.getId());

		return new Specification() {
			{
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpSpecificationOption.getDescriptionMap()));
				setExternalReferenceCode(
					cpSpecificationOption::getExternalReferenceCode);
				setFacetable(cpSpecificationOption::isFacetable);
				setId(cpSpecificationOption::getCPSpecificationOptionId);
				setKey(cpSpecificationOption::getKey);
				setListTypeDefinitionExternalReferenceCodes(
					() -> TransformUtil.transformToArray(
						cpSpecificationOption.getListTypeDefinitions(),
						ListTypeDefinition::getExternalReferenceCode,
						String.class));
				setListTypeDefinitionId(
					() -> {
						for (ListTypeDefinition listTypeDefinition :
								cpSpecificationOption.
									getListTypeDefinitions()) {

							return listTypeDefinition.getListTypeDefinitionId();
						}

						return null;
					});
				setListTypeDefinitionIds(
					() -> TransformUtil.transformToArray(
						cpSpecificationOption.getListTypeDefinitions(),
						ListTypeDefinition::getListTypeDefinitionId,
						Long.class));
				setOptionCategory(
					() -> {
						CPOptionCategory cpOptionCategory =
							cpSpecificationOption.getCPOptionCategory();

						if (cpOptionCategory == null) {
							return null;
						}

						return _optionCategoryDTOConverter.toDTO(
							new DefaultDTOConverterContext(
								cpOptionCategory.getCPOptionCategoryId(),
								dtoConverterContext.getLocale()));
					});
				setPriority(cpSpecificationOption::getPriority);
				setTitle(
					() -> LanguageUtils.getLanguageIdMap(
						cpSpecificationOption.getTitleMap()));
				setVisible(cpSpecificationOption::isVisible);
			}
		};
	}

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

	@Reference(target = DTOConverterConstants.OPTION_CATEGORY_DTO_CONVERTER)
	private DTOConverter<CPOptionCategory, OptionCategory>
		_optionCategoryDTOConverter;

}
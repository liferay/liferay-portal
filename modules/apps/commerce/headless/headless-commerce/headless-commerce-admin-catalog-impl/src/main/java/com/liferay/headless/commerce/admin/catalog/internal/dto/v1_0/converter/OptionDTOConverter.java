/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.product.model.CPOption"
	},
	service = DTOConverter.class
)
public class OptionDTOConverter implements DTOConverter<CPOption, Option> {

	@Override
	public String getContentType() {
		return Option.class.getSimpleName();
	}

	@Override
	public Option toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPOption cpOption = _cpOptionService.getCPOption(
			(Long)dtoConverterContext.getId());

		return new Option() {
			{
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> NestedFieldsSupplier.supply(
						"creator",
						fieldName -> CreatorUtil.toCreator(
							_portal,
							_userLocalService.fetchUser(
								cpOption.getUserId()))));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPOption.class.getName(), cpOption.getCPOptionId(),
						cpOption.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(cpOption::getCreateDate);
				setDateModified(cpOption::getModifiedDate);
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpOption.getDescriptionMap()));
				setExternalReferenceCode(cpOption::getExternalReferenceCode);
				setFacetable(cpOption::isFacetable);
				setFieldType(
					() -> Option.FieldType.create(
						cpOption.getCommerceOptionTypeKey()));
				setId(cpOption::getCPOptionId);
				setKey(cpOption::getKey);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpOption.getNameMap()));
				setRequired(cpOption::isRequired);
				setSkuContributor(cpOption::isSkuContributor);
			}
		};
	}

	@Reference
	private CPOptionService _cpOptionService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
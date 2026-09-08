/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.CPOptionValueService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
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
		"dto.class.name=com.liferay.commerce.product.model.CPOptionValue"
	},
	service = DTOConverter.class
)
public class OptionValueDTOConverter
	implements DTOConverter<CPOptionValue, OptionValue> {

	@Override
	public String getContentType() {
		return OptionValue.class.getSimpleName();
	}

	@Override
	public OptionValue toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPOptionValue cpOptionValue = _cpOptionValueService.getCPOptionValue(
			(Long)dtoConverterContext.getId());

		return new OptionValue() {
			{
				setActions(dtoConverterContext::getActions);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPOptionValue.class.getName(),
						cpOptionValue.getCPOptionValueId(),
						cpOptionValue.getCompanyId(),
						dtoConverterContext.getLocale()));
				setExternalReferenceCode(
					cpOptionValue::getExternalReferenceCode);
				setId(cpOptionValue::getCPOptionValueId);
				setKey(cpOptionValue::getKey);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpOptionValue.getNameMap()));
				setPriority(cpOptionValue::getPriority);
			}
		};
	}

	@Reference
	private CPOptionValueService _cpOptionValueService;

}
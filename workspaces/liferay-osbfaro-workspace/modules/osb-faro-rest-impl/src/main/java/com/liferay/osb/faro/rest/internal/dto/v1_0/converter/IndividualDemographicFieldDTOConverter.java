/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualDemographicField;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroEnumUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.Field",
	service = DTOConverter.class
)
public class IndividualDemographicFieldDTOConverter
	implements DTOConverter<Field, IndividualDemographicField> {

	@Override
	public String getContentType() {
		return IndividualDemographicField.class.getSimpleName();
	}

	@Override
	public IndividualDemographicField toDTO(
		DTOConverterContext dtoConverterContext, Field field) {

		if (field == null) {
			return null;
		}

		return new IndividualDemographicField() {
			{
				setFieldType(
					() -> FaroEnumUtil.create(
						FieldType::create,
						StringUtil.toUpperCase(field.getFieldType())));
				setLabel(field::getLabel);
				setName(field::getName);
				setValue(field::getValue);
			}
		};
	}

}
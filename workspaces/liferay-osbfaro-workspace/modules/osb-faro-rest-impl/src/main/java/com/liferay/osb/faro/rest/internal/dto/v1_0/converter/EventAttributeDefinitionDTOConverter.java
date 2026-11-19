/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.EventAttributeDefinition;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroEnumUtil;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventAttributeDefinitionsPageResponse;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Caio Pinheiro
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventAttributeDefinitionsPageResponse$EventAttributeDefinition",
	service = DTOConverter.class
)
public class EventAttributeDefinitionDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinition,
		 EventAttributeDefinition> {

	@Override
	public String getContentType() {
		return EventAttributeDefinition.class.getSimpleName();
	}

	@Override
	public EventAttributeDefinition toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinition eventAttributeDefinition) {

		if (eventAttributeDefinition == null) {
			return null;
		}

		return new EventAttributeDefinition() {
			{
				setDataType(
					() -> FaroEnumUtil.create(
						DataType::create,
						eventAttributeDefinition.getDataType()));
				setDescription(eventAttributeDefinition::getDescription);
				setDisplayName(eventAttributeDefinition::getDisplayName);
				setId(eventAttributeDefinition::getId);
				setName(eventAttributeDefinition::getName);
				setSampleValue(eventAttributeDefinition::getSampleValue);
				setType(
					() -> FaroEnumUtil.create(
						Type::create, eventAttributeDefinition.getType()));
			}
		};
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.EventDefinition;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroEnumUtil;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventDefinitionsPageResponse;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Caio Pinheiro
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventDefinitionsPageResponse$EventDefinition",
	service = DTOConverter.class
)
public class EventDefinitionDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition,
		 EventDefinition> {

	@Override
	public String getContentType() {
		return EventDefinition.class.getSimpleName();
	}

	@Override
	public EventDefinition toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition
			eventDefinition) {

		if (eventDefinition == null) {
			return null;
		}

		return new EventDefinition() {
			{
				setDescription(eventDefinition::getDescription);
				setDisplayName(eventDefinition::getDisplayName);
				setHidden(eventDefinition::isHidden);
				setId(eventDefinition::getId);
				setName(eventDefinition::getName);
				setType(
					() -> FaroEnumUtil.create(
						Type::create, eventDefinition.getType()));
			}
		};
	}

}
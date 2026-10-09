/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Event;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventsPageResponse;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventsPageResponse$Event",
	service = DTOConverter.class
)
public class EventDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupChannelEventsPageResponse.Event, Event> {

	@Override
	public String getContentType() {
		return Event.class.getSimpleName();
	}

	@Override
	public Event toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupChannelEventsPageResponse.Event event) {

		if (event == null) {
			return null;
		}

		return new Event() {
			{
				setApplicationId(event::getApplicationId);
				setAssetTitle(event::getAssetTitle);
				setAttributes(() -> _getAttributes(event.getProperties()));
				setCanonicalUrl(event::getCanonicalUrl);
				setCreateDate(event::getCreateDate);
				setIndividualId(event::getEmailAddressHashed);
				setName(event::getName);
				setPageDescription(event::getPageDescription);
				setPageKeywords(event::getPageKeywords);
				setPageTitle(event::getPageTitle);
				setReferrer(event::getReferrer);
				setUrl(event::getUrl);
			}
		};
	}

	private Map<String, String> _getAttributes(
		List<GetWorkspaceGroupChannelEventsPageResponse.Property> properties) {

		if (ListUtil.isEmpty(properties)) {
			return null;
		}

		Map<String, String> attributes = new LinkedHashMap<>(properties.size());

		for (GetWorkspaceGroupChannelEventsPageResponse.Property property :
				properties) {

			String name = property.getName();

			if (name == null) {
				continue;
			}

			attributes.put(name, property.getValue());
		}

		return attributes;
	}

}
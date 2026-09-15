/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Event;
import com.liferay.osb.faro.rest.dto.v1_0.UserSession;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventsPageResponse;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelUserSessionsPageResponse;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelUserSessionsPageResponse$UserSession",
	service = DTOConverter.class
)
public class UserSessionDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession,
		 UserSession> {

	@Override
	public String getContentType() {
		return UserSession.class.getSimpleName();
	}

	@Override
	public UserSession toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession
			userSession) {

		if (userSession == null) {
			return null;
		}

		return new UserSession() {
			{
				setBecameKnown(userSession::getBecameKnown);
				setBrowserName(userSession::getBrowserName);
				setCompleteDate(userSession::getCompleteDate);
				setCreateDate(userSession::getCreateDate);
				setDeviceType(userSession::getDeviceType);
				setEvents(
					() -> _toEvents(
						dtoConverterContext, userSession.getEvents()));
			}
		};
	}

	private Event[] _toEvents(
		DTOConverterContext dtoConverterContext,
		List<GetWorkspaceGroupChannelEventsPageResponse.Event> events) {

		if (events == null) {
			return null;
		}

		return TransformUtil.transformToArray(
			events,
			event -> _eventDTOConverter.toDTO(dtoConverterContext, event),
			Event.class);
	}

	@Reference(
		target = "(dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventsPageResponse$Event)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelEventsPageResponse.Event, Event>
			_eventDTOConverter;

}
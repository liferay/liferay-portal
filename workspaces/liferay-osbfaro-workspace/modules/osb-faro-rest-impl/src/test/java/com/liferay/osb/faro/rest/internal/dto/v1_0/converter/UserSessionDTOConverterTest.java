/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Event;
import com.liferay.osb.faro.rest.dto.v1_0.UserSession;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventsPageResponse;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelUserSessionsPageResponse;

import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class UserSessionDTOConverterTest {

	@Before
	public void setUp() throws Exception {
		Field field = UserSessionDTOConverter.class.getDeclaredField(
			"_eventDTOConverter");

		field.setAccessible(true);

		field.set(_userSessionDTOConverter, new EventDTOConverter());
	}

	@Test
	public void testToDTOConvertsNestedEvents() {
		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession
			userSession =
				new GetWorkspaceGroupChannelUserSessionsPageResponse.
					UserSession();

		userSession.setBecameKnown(Boolean.TRUE);
		userSession.setBrowserName("Firefox");

		Date completeDate = new Date(1720000900000L);
		Date createDate = new Date(1720000000000L);

		userSession.setCompleteDate(completeDate);
		userSession.setCreateDate(createDate);
		userSession.setDeviceType("Desktop");

		GetWorkspaceGroupChannelEventsPageResponse.Event event =
			new GetWorkspaceGroupChannelEventsPageResponse.Event();

		event.setAssetTitle("Pricing");
		event.setCanonicalUrl("https://acme.example/pricing");
		event.setCreateDate(createDate);
		event.setName("pageViewed");

		userSession.setEvents(Arrays.asList(event));

		UserSession userSessionDTO = _userSessionDTOConverter.toDTO(
			new FaroDTOConverterContext(false, "individual-1", null),
			userSession);

		Assert.assertEquals(Boolean.TRUE, userSessionDTO.getBecameKnown());
		Assert.assertEquals("Firefox", userSessionDTO.getBrowserName());
		Assert.assertEquals(completeDate, userSessionDTO.getCompleteDate());
		Assert.assertEquals(createDate, userSessionDTO.getCreateDate());
		Assert.assertEquals("Desktop", userSessionDTO.getDeviceType());

		Event[] events = userSessionDTO.getEvents();

		Assert.assertEquals(Arrays.toString(events), 1, events.length);
		Assert.assertEquals("Pricing", events[0].getAssetTitle());
		Assert.assertEquals(
			"https://acme.example/pricing", events[0].getCanonicalUrl());
		Assert.assertEquals("pageViewed", events[0].getName());
	}

	@Test
	public void testToDTOKeepsEventsNullWhenAbsent() {
		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession
			userSession =
				new GetWorkspaceGroupChannelUserSessionsPageResponse.
					UserSession();

		UserSession userSessionDTO = _userSessionDTOConverter.toDTO(
			new FaroDTOConverterContext(false, "individual-1", null),
			userSession);

		Assert.assertNull(userSessionDTO.getEvents());

		userSession.setEvents(Collections.emptyList());

		userSessionDTO = _userSessionDTOConverter.toDTO(
			new FaroDTOConverterContext(false, "individual-1", null),
			userSession);

		Event[] events = userSessionDTO.getEvents();

		Assert.assertEquals(Arrays.toString(events), 0, events.length);
	}

	@Test
	public void testToDTOReturnsNullForNullUserSession() {
		Assert.assertNull(
			_userSessionDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private final UserSessionDTOConverter _userSessionDTOConverter =
		new UserSessionDTOConverter();

}
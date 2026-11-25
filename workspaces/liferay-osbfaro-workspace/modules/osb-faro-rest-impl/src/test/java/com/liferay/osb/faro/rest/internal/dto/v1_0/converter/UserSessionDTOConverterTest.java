/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Event;
import com.liferay.osb.faro.rest.dto.v1_0.UserSession;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventsPageResponse;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelUserSessionsPageResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
	public void testToDTO() {
		Assert.assertNull(
			_userSessionDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));

		_testToDTOWithEvents();
		_testToDTOWithoutEvents();
	}

	private GetWorkspaceGroupChannelEventsPageResponse.Event _createEvent(
		String assetTitle, String canonicalURL, Date createDate, String name) {

		GetWorkspaceGroupChannelEventsPageResponse.Event event =
			new GetWorkspaceGroupChannelEventsPageResponse.Event();

		event.setAssetTitle(assetTitle);
		event.setCanonicalUrl(canonicalURL);
		event.setCreateDate(createDate);
		event.setName(name);

		return event;
	}

	private void _testToDTOWithEvents() {
		String assetTitle = RandomTestUtil.randomString();
		boolean becameKnown = RandomTestUtil.randomBoolean();
		String browserName = RandomTestUtil.randomString();
		String canonicalURL = RandomTestUtil.randomString();
		Date completeDate = RandomTestUtil.nextDate();
		Date createDate = RandomTestUtil.nextDate();
		String deviceType = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		UserSession userSession = _toDTO(
			becameKnown, browserName, completeDate, createDate, deviceType,
			Arrays.asList(
				_createEvent(assetTitle, canonicalURL, createDate, name)));

		Assert.assertEquals(becameKnown, userSession.getBecameKnown());
		Assert.assertEquals(browserName, userSession.getBrowserName());
		Assert.assertEquals(completeDate, userSession.getCompleteDate());
		Assert.assertEquals(createDate, userSession.getCreateDate());
		Assert.assertEquals(deviceType, userSession.getDeviceType());

		Event[] events = userSession.getEvents();

		Assert.assertEquals(Arrays.toString(events), 1, events.length);

		Event event = events[0];

		Assert.assertEquals(assetTitle, event.getAssetTitle());
		Assert.assertEquals(canonicalURL, event.getCanonicalUrl());
		Assert.assertEquals(name, event.getName());
	}

	private void _testToDTOWithoutEvents() {
		UserSession userSession = _toDTO(null);

		Assert.assertNull(userSession.getEvents());

		userSession = _toDTO(Collections.emptyList());

		Event[] events = userSession.getEvents();

		Assert.assertEquals(Arrays.toString(events), 0, events.length);
	}

	private UserSession _toDTO(
		Boolean becameKnown, String browserName, Date completeDate,
		Date createDate, String deviceType,
		List<GetWorkspaceGroupChannelEventsPageResponse.Event> events) {

		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession
			userSession =
				new GetWorkspaceGroupChannelUserSessionsPageResponse.
					UserSession();

		userSession.setBecameKnown(becameKnown);
		userSession.setBrowserName(browserName);
		userSession.setCompleteDate(completeDate);
		userSession.setCreateDate(createDate);
		userSession.setDeviceType(deviceType);
		userSession.setEvents(events);

		return _userSessionDTOConverter.toDTO(
			new FaroDTOConverterContext(
				false, RandomTestUtil.randomString(), null),
			userSession);
	}

	private UserSession _toDTO(
		List<GetWorkspaceGroupChannelEventsPageResponse.Event> events) {

		return _toDTO(null, null, null, null, null, events);
	}

	private final UserSessionDTOConverter _userSessionDTOConverter =
		new UserSessionDTOConverter();

}
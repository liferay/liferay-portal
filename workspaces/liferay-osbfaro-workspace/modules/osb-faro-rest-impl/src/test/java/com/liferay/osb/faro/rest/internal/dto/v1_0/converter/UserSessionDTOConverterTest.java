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
		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession
			responseUserSession =
				new GetWorkspaceGroupChannelUserSessionsPageResponse.
					UserSession();

		responseUserSession.setBecameKnown(RandomTestUtil.randomBoolean());
		responseUserSession.setBrowserName(RandomTestUtil.randomString());
		responseUserSession.setCompleteDate(RandomTestUtil.nextDate());
		responseUserSession.setCreateDate(RandomTestUtil.nextDate());
		responseUserSession.setDeviceType(RandomTestUtil.randomString());

		GetWorkspaceGroupChannelEventsPageResponse.Event responseEvent =
			new GetWorkspaceGroupChannelEventsPageResponse.Event();

		responseEvent.setAssetTitle(RandomTestUtil.randomString());
		responseEvent.setCanonicalUrl(RandomTestUtil.randomString());
		responseEvent.setCreateDate(responseUserSession.getCreateDate());
		responseEvent.setName(RandomTestUtil.randomString());

		responseUserSession.setEvents(Arrays.asList(responseEvent));

		UserSession userSession = _toDTO(responseUserSession);

		Assert.assertEquals(
			responseUserSession.isBecameKnown(), userSession.getBecameKnown());
		Assert.assertEquals(
			responseUserSession.getBrowserName(), userSession.getBrowserName());
		Assert.assertEquals(
			responseUserSession.getCompleteDate(),
			userSession.getCompleteDate());
		Assert.assertEquals(
			responseUserSession.getCreateDate(), userSession.getCreateDate());
		Assert.assertEquals(
			responseUserSession.getDeviceType(), userSession.getDeviceType());

		Event[] events = userSession.getEvents();

		Assert.assertEquals(Arrays.toString(events), 1, events.length);

		Event event = events[0];

		Assert.assertEquals(
			responseEvent.getAssetTitle(), event.getAssetTitle());
		Assert.assertEquals(
			responseEvent.getCanonicalUrl(), event.getCanonicalUrl());
		Assert.assertEquals(responseEvent.getName(), event.getName());

		responseUserSession =
			new GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession();

		userSession = _toDTO(responseUserSession);

		Assert.assertNull(userSession.getEvents());

		responseUserSession.setEvents(Collections.emptyList());

		userSession = _toDTO(responseUserSession);

		events = userSession.getEvents();

		Assert.assertEquals(Arrays.toString(events), 0, events.length);

		Assert.assertNull(
			_userSessionDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private UserSession _toDTO(
		GetWorkspaceGroupChannelUserSessionsPageResponse.UserSession
			responseUserSession) {

		return _userSessionDTOConverter.toDTO(
			new FaroDTOConverterContext(
				false, RandomTestUtil.randomString(), null),
			responseUserSession);
	}

	private final UserSessionDTOConverter _userSessionDTOConverter =
		new UserSessionDTOConverter();

}
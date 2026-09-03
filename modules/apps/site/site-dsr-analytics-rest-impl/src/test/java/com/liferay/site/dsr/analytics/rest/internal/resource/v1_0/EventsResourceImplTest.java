/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.Events;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Tancredi Covioli
 */
public class EventsResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetEvents() throws Exception {
		EventsResourceImpl eventsResourceImpl = new EventsResourceImpl();

		ReflectionTestUtil.setFieldValue(eventsResourceImpl, "_http", _http);

		try (MockedStatic<DSRRoomUtil> dsrRoomUtilMockedStatic =
				Mockito.mockStatic(DSRRoomUtil.class)) {

			dsrRoomUtilMockedStatic.when(
				() -> DSRRoomUtil.getGroupIds(Mockito.any(), Mockito.any())
			).thenReturn(
				new String[0]
			);

			Events events = eventsResourceImpl.getEvents(
				new String[] {"1"}, null, null, null, null, null, null, null,
				null);

			Assert.assertNull(events.getEventEntries());

			Mockito.verifyNoInteractions(_http);
		}
	}

	private final Http _http = Mockito.mock(Http.class);

}
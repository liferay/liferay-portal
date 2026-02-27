/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefano Motta
 */
public class InviteMemberDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		Mockito.when(
			_group.getDisplayURL(Mockito.any(), Mockito.anyBoolean())
		).thenReturn(
			"groupDisplayURL"
		);

		Mockito.when(
			_group.getDisplayURL(_themeDisplay)
		).thenReturn(
			"groupDisplayURL"
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getPortalURL(_themeDisplay)
		).thenReturn(
			"portalURL"
		);

		_portletURLFactoryUtilMockedStatic.when(
			() -> PortletURLFactoryUtil.create(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			new MockLiferayPortletURL()
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.ENGLISH
		);

		Mockito.when(
			_themeDisplay.getPathMain()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_themeDisplay.getPortalURL()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_themeDisplay.getPortletDisplay()
		).thenReturn(
			Mockito.mock(PortletDisplay.class)
		);
	}

	@After
	public void tearDown() {
		_portalUtilMockedStatic.close();
		_portletURLFactoryUtilMockedStatic.close();
	}

	@Test
	public void testGetBackURL() throws PortalException {
		InviteMemberDisplayContext inviteMemberDisplayContext =
			new InviteMemberDisplayContext(
				RandomTestUtil.randomString() + "@liferay.com", _group,
				_getMockHttpServletRequest(), RandomTestUtil.randomLong(),
				_themeDisplay, RandomTestUtil.randomString());

		Assert.assertEquals(
			"portalURL", inviteMemberDisplayContext.getBackURL());
	}

	@Test
	public void testGetEmailAddress() {
		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";

		InviteMemberDisplayContext inviteMemberDisplayContext =
			new InviteMemberDisplayContext(
				emailAddress, _group, _getMockHttpServletRequest(),
				RandomTestUtil.randomLong(), _themeDisplay,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			emailAddress, inviteMemberDisplayContext.getEmailAddress());
	}

	@Test
	public void testGetRedirect() throws PortalException {
		InviteMemberDisplayContext inviteMemberDisplayContext =
			new InviteMemberDisplayContext(
				RandomTestUtil.randomString() + "@liferay.com", _group,
				_getMockHttpServletRequest(), RandomTestUtil.randomLong(),
				_themeDisplay, RandomTestUtil.randomString());

		Assert.assertEquals(
			"http//localhost/test?param_redirect=groupDisplayURL/onboarding",
			inviteMemberDisplayContext.getRedirect());
	}

	@Test
	public void testGetRoomId() {
		long roomId = RandomTestUtil.randomLong();

		InviteMemberDisplayContext inviteMemberDisplayContext =
			new InviteMemberDisplayContext(
				RandomTestUtil.randomString() + "@liferay.com", _group,
				_getMockHttpServletRequest(), roomId, _themeDisplay,
				RandomTestUtil.randomString());

		Assert.assertEquals(roomId, inviteMemberDisplayContext.getRoomId());
	}

	@Test
	public void testGetTicketKey() {
		String ticketKey = RandomTestUtil.randomString();

		InviteMemberDisplayContext inviteMemberDisplayContext =
			new InviteMemberDisplayContext(
				RandomTestUtil.randomString() + "@liferay.com", _group,
				_getMockHttpServletRequest(), RandomTestUtil.randomLong(),
				_themeDisplay, ticketKey);

		Assert.assertEquals(
			ticketKey, inviteMemberDisplayContext.getTicketKey());
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		return mockHttpServletRequest;
	}

	private final Group _group = Mockito.mock(Group.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final MockedStatic<PortletURLFactoryUtil>
		_portletURLFactoryUtilMockedStatic = Mockito.mockStatic(
			PortletURLFactoryUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
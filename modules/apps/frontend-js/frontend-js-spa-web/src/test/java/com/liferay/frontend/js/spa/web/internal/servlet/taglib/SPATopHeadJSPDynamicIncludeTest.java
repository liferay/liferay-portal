/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.spa.web.internal.servlet.taglib;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Iván Zaera Avellón
 */
public class SPATopHeadJSPDynamicIncludeTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-104833")
	public void testIsClearScreensCache() {
		String portletId = RandomTestUtil.randomString();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(portletId);

		MockHttpSession mockHttpSession = new MockHttpSession();

		mockHttpServletRequest.setSession(mockHttpSession);

		// No last portlet ID

		Assert.assertFalse(_isClearScreensCache(mockHttpServletRequest));

		// The last portlet ID is the current portlet ID

		mockHttpSession.setAttribute(
			WebKeys.SINGLE_PAGE_APPLICATION_LAST_PORTLET_ID, portletId);

		Assert.assertFalse(_isClearScreensCache(mockHttpServletRequest));

		// The last portlet ID is another portlet ID

		mockHttpSession.setAttribute(
			WebKeys.SINGLE_PAGE_APPLICATION_LAST_PORTLET_ID,
			RandomTestUtil.randomString());

		Assert.assertTrue(_isClearScreensCache(mockHttpServletRequest));
	}

	@Test
	@TestInfo("LPD-104833")
	public void testIsClearScreensCacheWithoutHttpSession() {

		// The clear cache request attribute is set

		MockHttpServletRequest mockHttpServletRequest = Mockito.spy(
			_getMockHttpServletRequest(RandomTestUtil.randomString()));

		mockHttpServletRequest.setAttribute(
			WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);

		Assert.assertTrue(_isClearScreensCache(mockHttpServletRequest));

		_assertHttpSessionNotCreated(mockHttpServletRequest);

		// There is no portlet ID

		mockHttpServletRequest = Mockito.spy(new MockHttpServletRequest());

		Assert.assertFalse(_isClearScreensCache(mockHttpServletRequest));

		_assertHttpSessionNotCreated(mockHttpServletRequest);

		// There is a portlet ID

		mockHttpServletRequest = Mockito.spy(
			_getMockHttpServletRequest(RandomTestUtil.randomString()));

		Assert.assertFalse(_isClearScreensCache(mockHttpServletRequest));

		_assertHttpSessionNotCreated(mockHttpServletRequest);
	}

	private void _assertHttpSessionNotCreated(
		MockHttpServletRequest mockHttpServletRequest) {

		Mockito.verify(
			mockHttpServletRequest, Mockito.never()
		).getSession();

		Assert.assertNull(mockHttpServletRequest.getSession(false));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		String portletId) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_p_id", portletId);

		return mockHttpServletRequest;
	}

	private boolean _isClearScreensCache(
		MockHttpServletRequest mockHttpServletRequest) {

		return ReflectionTestUtil.invoke(
			new SPATopHeadJSPDynamicInclude(), "_isClearScreensCache",
			new Class<?>[] {HttpServletRequest.class}, mockHttpServletRequest);
	}

}
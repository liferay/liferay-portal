/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.absoluteredirects.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.absoluteredirects.AbsoluteRedirectsFilter;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class HttpsInitialAbsoluteRedirectsFilterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "SESSION_ENABLE_PHISHING_PROTECTION",
			_SESSION_ENABLE_PHISHING_PROTECTION);
	}

	@Test
	public void testDoesNotForceSessionCreationWhenPhishingProtectionIsEnabled()
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "SESSION_ENABLE_PHISHING_PROTECTION", true);

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest();

		_absoluteRedirectsFilter.doFilterTry(
			mockHttpServletRequest, new MockHttpServletResponse());

		Assert.assertNull(mockHttpServletRequest.getSession(false));
	}

	@Test
	public void testPinsHTTPSInitialOnFirstRequestWhenPhishingProtectionIsDisabled()
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "SESSION_ENABLE_PHISHING_PROTECTION", false);

		MockHttpServletRequest insecureMockHttpServletRequest =
			_createMockHttpServletRequest();

		insecureMockHttpServletRequest.setSecure(false);

		_absoluteRedirectsFilter.doFilterTry(
			insecureMockHttpServletRequest, new MockHttpServletResponse());

		HttpSession httpSession = insecureMockHttpServletRequest.getSession(
			false);

		Assert.assertNotNull(httpSession);
		Assert.assertEquals(
			Boolean.FALSE, httpSession.getAttribute(WebKeys.HTTPS_INITIAL));

		MockHttpServletRequest secureMockHttpServletRequest =
			_createMockHttpServletRequest();

		secureMockHttpServletRequest.setSecure(true);
		secureMockHttpServletRequest.setSession(httpSession);

		_absoluteRedirectsFilter.doFilterTry(
			secureMockHttpServletRequest, new MockHttpServletResponse());

		Assert.assertEquals(
			Boolean.FALSE, httpSession.getAttribute(WebKeys.HTTPS_INITIAL));
	}

	private MockHttpServletRequest _createMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setServerName("localhost");
		mockHttpServletRequest.setRequestURI("/web/guest");

		return mockHttpServletRequest;
	}

	private static final boolean _SESSION_ENABLE_PHISHING_PROTECTION =
		PropsValues.SESSION_ENABLE_PHISHING_PROTECTION;

	private final AbsoluteRedirectsFilter _absoluteRedirectsFilter =
		new AbsoluteRedirectsFilter();

}
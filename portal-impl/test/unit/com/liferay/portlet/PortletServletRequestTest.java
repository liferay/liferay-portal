/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.internal.RenderRequestImpl;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eric Yan
 */
public class PortletServletRequestTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void test() {
		try (SafeCloseable safeCloseable =
				LocaleThreadLocal.setDefaultLocaleWithSafeCloseable(
					LocaleUtil.GERMANY)) {

			Assert.assertEquals(
				String.valueOf(
					Arrays.asList(
						784111777000L, 784111777000L, 784111777000L, -1L)),
				String.valueOf(
					Arrays.asList(
						_getDateHeader("Sun Nov  6 08:49:37 1994"),
						_getDateHeader("Sun, 06 Nov 1994 08:49:37 GMT"),
						_getDateHeader("Sunday, 06-Nov-94 08:49:37 GMT"),
						_getDateHeader(null))));

			try {
				_getDateHeader(String.valueOf(RandomTestUtil.randomInt()));

				Assert.fail();
			}
			catch (IllegalArgumentException illegalArgumentException) {
			}

			try {
				_getDateHeader(RandomTestUtil.randomString());

				Assert.fail();
			}
			catch (IllegalArgumentException illegalArgumentException) {
			}
		}
	}

	private long _getDateHeader(String ifModifiedSince) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (ifModifiedSince != null) {
			mockHttpServletRequest.addHeader(
				HttpHeaders.IF_MODIFIED_SINCE, ifModifiedSince);
		}

		PortletServletRequest portletServletRequest = new PortletServletRequest(
			mockHttpServletRequest, new RenderRequestImpl(), null, null, null,
			null, false, true);

		return portletServletRequest.getDateHeader(
			HttpHeaders.IF_MODIFIED_SINCE);
	}

}
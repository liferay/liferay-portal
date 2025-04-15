/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.ToolDependencies;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mika Koivisto
 */
public class InvokerFilterTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ToolDependencies.wireCaches();
	}

	@Test
	public void testGetURIWithDoubleSlash() {
		InvokerFilter invokerFilter = new InvokerFilter();

		Assert.assertEquals(
			"/c/portal/login",
			invokerFilter.getURI(
				invokerFilter.getOriginalRequestURI(
					new MockHttpServletRequest(
						HttpMethods.GET,
						"/c///portal/%2e/login;jsessionid=ae01b0f2af." +
							"worker1"))));

		Assert.assertEquals(
			"/c/portal/login",
			invokerFilter.getURI(
				invokerFilter.getOriginalRequestURI(
					new MockHttpServletRequest(
						HttpMethods.GET,
						"/c///portal/%2e/../login;jsessionid=ae01b0f2af." +
							"worker1"))));
	}

	@Test
	public void testGetURIWithJSessionId() {
		InvokerFilter invokerFilter = new InvokerFilter();

		Assert.assertEquals(
			"/c/portal/login",
			invokerFilter.getURI(
				invokerFilter.getOriginalRequestURI(
					new MockHttpServletRequest(
						HttpMethods.GET,
						"/c/portal/login;jsessionid=ae01b0f2af.worker1"))));
	}

	@Test
	public void testLongURLsWithPath() throws Exception {
		testLongURL("/c/portal/login/");
	}

	@Test
	public void testLongURLsWithPathParameters() throws Exception {
		testLongURL("/c/portal/login/;");
	}

	@Test
	public void testLongURLsWithQueryString() throws Exception {
		testLongURL("/c/portal/login?param=");
	}

	protected void testLongURL(String urlPrefix) throws Exception {
		InvokerFilter invokerFilter = new InvokerFilter();

		int invokerFilterUriMaxLength = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.INVOKER_FILTER_URI_MAX_LENGTH));

		char[] chars = new char[invokerFilterUriMaxLength];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = '0';
		}

		String payload = urlPrefix.concat(new String(chars));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethods.GET, payload);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		MockFilterChain mockFilterChain = new MockFilterChain();

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				InvokerFilter.class.getName(), Level.WARNING)) {

			invokerFilter.doFilter(
				mockHttpServletRequest, mockHttpServletResponse,
				mockFilterChain);

			Assert.assertEquals(
				HttpServletResponse.SC_REQUEST_URI_TOO_LONG,
				mockHttpServletResponse.getStatus());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(message.startsWith("Rejected " + urlPrefix));
		}
	}

}
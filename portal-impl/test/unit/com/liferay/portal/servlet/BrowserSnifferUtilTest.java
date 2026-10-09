/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 * @author Iliyan Peych
 */
public class BrowserSnifferUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsAndroid() throws IOException {
		assertBrowser(
			"Android",
			mockHttpServletRequest -> Assert.assertTrue(
				BrowserSnifferUtil.isAndroid(mockHttpServletRequest)));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.USER_AGENT,
			"Safari 6, 6.0, 536.26, mozilla/5.0 (ipad; cpu os 6_0 like mac " +
				"os x) applewebkit/536.26 (khtml, like gecko) version/6.0 " +
					"mobile/10a5355d safari/8536.25");

		Assert.assertFalse(
			BrowserSnifferUtil.isAndroid(mockHttpServletRequest));
	}

	@Test
	public void testIsEdge() throws IOException {
		assertBrowser(
			"## Edge",
			mockHttpServletRequest -> {
				Assert.assertFalse(
					BrowserSnifferUtil.isChrome(mockHttpServletRequest));
				Assert.assertTrue(
					BrowserSnifferUtil.isEdge(mockHttpServletRequest));
				Assert.assertFalse(
					BrowserSnifferUtil.isGecko(mockHttpServletRequest));
				Assert.assertFalse(
					BrowserSnifferUtil.isMozilla(mockHttpServletRequest));
				Assert.assertFalse(
					BrowserSnifferUtil.isWebKit(mockHttpServletRequest));
			});

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.USER_AGENT,
			"opera/9.80 (windows nt 6.0) presto/2.12.388 version/12.14");

		Assert.assertFalse(BrowserSnifferUtil.isEdge(mockHttpServletRequest));
	}

	@Test
	public void testIsIe() throws IOException {
		assertBrowser(
			"## IE",
			mockHttpServletRequest -> Assert.assertTrue(
				BrowserSnifferUtil.isIe(mockHttpServletRequest)));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.USER_AGENT,
			"Opera 12 var1, 12.14, 9.80, opera/9.80 (windows nt 6.0) " +
				"presto/2.12.388 version/12.14");

		Assert.assertFalse(BrowserSnifferUtil.isIe(mockHttpServletRequest));
	}

	@Test
	public void testIsMobile() throws IOException {
		assertBrowser(
			"Mobile",
			mockHttpServletRequest -> Assert.assertTrue(
				BrowserSnifferUtil.isMobile(mockHttpServletRequest)));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.USER_AGENT,
			"IE 6 var4, , 6.0, mozilla/5.0 (compatible; msie 6.0; windows nt " +
				"5.1)");

		Assert.assertFalse(BrowserSnifferUtil.isMobile(mockHttpServletRequest));
	}

	@Test
	public void testParseVersion() throws IOException {
		try (UnsyncBufferedReader unsyncBufferedReader =
				getResourceAsUnsyncBufferedReader()) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty() || (line.charAt(0) == CharPool.POUND)) {
					continue;
				}

				String[] parts = StringUtil.split(line, CharPool.COMMA);

				if (parts.length < 4) {
					continue;
				}

				String userAgent = parts[3].trim();

				if (parts.length == 5) {
					userAgent += ", " + parts[4].trim();
				}

				Assert.assertEquals(
					parts[0].trim() + " version", parts[1].trim(),
					BrowserSnifferUtil.parseVersion(
						userAgent,
						ReflectionTestUtil.getFieldValue(
							BrowserSnifferUtil.class, "_VERSION_LEADINGS"),
						ReflectionTestUtil.getFieldValue(
							BrowserSnifferUtil.class, "_VERSION_SEPARATORS")));

				Assert.assertEquals(
					parts[0].trim() + " revision", parts[2].trim(),
					BrowserSnifferUtil.parseVersion(
						userAgent,
						ReflectionTestUtil.getFieldValue(
							BrowserSnifferUtil.class, "_REVISION_LEADINGS"),
						ReflectionTestUtil.getFieldValue(
							BrowserSnifferUtil.class, "_REVISION_SEPARATORS")));
			}
		}
	}

	protected void assertBrowser(
			String key, Consumer<MockHttpServletRequest> requestConsumer)
		throws IOException {

		try (UnsyncBufferedReader unsyncBufferedReader =
				getResourceAsUnsyncBufferedReader()) {

			boolean matches = false;
			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty()) {
					continue;
				}

				if (line.contains(key)) {
					matches = true;

					continue;
				}

				if (matches && (line.charAt(0) == CharPool.POUND)) {
					break;
				}

				if (!matches) {
					continue;
				}

				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				mockHttpServletRequest.addHeader(HttpHeaders.USER_AGENT, line);

				requestConsumer.accept(mockHttpServletRequest);
			}
		}
	}

	protected UnsyncBufferedReader getResourceAsUnsyncBufferedReader() {
		Class<?> clazz = getClass();

		return new UnsyncBufferedReader(
			new InputStreamReader(
				clazz.getResourceAsStream("dependencies/user_agents.csv")));
	}

}
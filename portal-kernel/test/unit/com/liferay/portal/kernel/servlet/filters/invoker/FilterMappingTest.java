/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mika Koivisto
 * @author Leon Chi
 */
public class FilterMappingTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstructor() {
		_assertFilterMapping(
			null, null, null, null, new String[0], new HashSet<>());
		_assertFilterMapping(
			_TEST_FILTER_NAME, _dummyFilter, _TEST_URL_REGEX_IGNORE_PATTERN,
			_TEST_URL_REGEX_PATTERN, new String[] {_TEST_URL_PATTERN},
			EnumSet.of(Dispatcher.ASYNC));
	}

	@Test
	public void testIsMatch() {
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " has a pattern match with /test" +
					"/login.jsp",
				_dummyFilter.getClass() + " has a regex match with /test" +
					"/login.jsp"
			},
			() -> _testIsMatch(
				true, Dispatcher.ASYNC, "/test/login.jsp",
				Collections.singletonList(_TEST_URL_PATTERN)));
		_testWithLog(
			new String[0],
			() -> _testIsMatch(
				false, Dispatcher.REQUEST, "/test/login.jsp",
				Collections.singletonList(_TEST_URL_PATTERN)));
		_testWithLog(
			new String[0],
			() -> _testIsMatch(
				false, Dispatcher.ASYNC, null,
				Collections.singletonList(_TEST_URL_PATTERN)));
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " does not have a pattern match " +
					"with /login"
			},
			() -> _testIsMatch(
				false, Dispatcher.ASYNC, "/login",
				Collections.singletonList(_TEST_URL_PATTERN)));
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " has a pattern match with /test" +
					"/login.css",
				_dummyFilter.getClass() + " does not have a regex match with " +
					"/test/login.css"
			},
			() -> _testIsMatch(
				false, Dispatcher.ASYNC, "/test/login.css",
				Collections.singletonList(_TEST_URL_PATTERN)));
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " does not have a pattern match " +
					"with /test/login.jsp"
			},
			() -> _testIsMatch(
				false, Dispatcher.ASYNC, "/test/login.jsp",
				Collections.emptyList()));
	}

	@Test
	public void testIsMatchURLPattern() {
		FilterMapping filterMapping = new FilterMapping(
			null, null, ProxyFactory.newDummyInstance(FilterConfig.class),
			Collections.emptyList(), EnumSet.of(Dispatcher.REQUEST));

		Assert.assertTrue(
			_getIsMatchURLPatternMessage(
				true, "/test/login.jsp", "/test/login.jsp"),
			filterMapping.isMatchURLPattern(
				"/test/login.jsp", "/test/login.jsp"));
		Assert.assertTrue(
			_getIsMatchURLPatternMessage(true, "/test/login", "/*"),
			filterMapping.isMatchURLPattern("/test/login", "/*"));
		Assert.assertTrue(
			_getIsMatchURLPatternMessage(true, "/test/login", "/test/*"),
			filterMapping.isMatchURLPattern("/test/login", "/test/*"));
		Assert.assertTrue(
			_getIsMatchURLPatternMessage(true, "/test", "/test/*"),
			filterMapping.isMatchURLPattern("/test", "/test/*"));
		Assert.assertTrue(
			_getIsMatchURLPatternMessage(true, "/test/login.jsp", "*.jsp"),
			filterMapping.isMatchURLPattern("/test/login.jsp", "*.jsp"));
		Assert.assertFalse(
			_getIsMatchURLPatternMessage(false, "/c/test/login", "/test/*"),
			filterMapping.isMatchURLPattern("/c/test/login", "/test/*"));
		Assert.assertFalse(
			_getIsMatchURLPatternMessage(
				false, "/test/login.css", "/test/login.jsp"),
			filterMapping.isMatchURLPattern(
				"/test/login.css", "/test/login.jsp"));
		Assert.assertFalse(
			_getIsMatchURLPatternMessage(false, "login.jsp", "*.jsp"),
			filterMapping.isMatchURLPattern("login.jsp", "*.jsp"));
		Assert.assertFalse(
			_getIsMatchURLPatternMessage(false, "/test/login.css", "*.jsp"),
			filterMapping.isMatchURLPattern("/test/login.css", "*.jsp"));
	}

	@Test
	public void testIsMatchURLRegexPattern() {
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " has a regex match with /test" +
					"/login.jsp"
			},
			() -> _testIsMatchURLRegexPattern(
				true, "/test/login.jsp", null,
				new TestFilterConfig(null, null)));
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " has a regex match with /test" +
					"/login.jsp?key=value"
			},
			() -> _testIsMatchURLRegexPattern(
				true, "/test/login.jsp", "key=value",
				new TestFilterConfig(null, null)));
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " does not have a regex match with " +
					"/test/ignore/login.jsp?key=value"
			},
			() -> _testIsMatchURLRegexPattern(
				false, "/test/ignore/login.jsp", "key=value",
				new TestFilterConfig(
					_TEST_URL_REGEX_IGNORE_PATTERN, _TEST_URL_REGEX_PATTERN)));
		_testWithLog(
			new String[] {
				_dummyFilter.getClass() + " does not have a regex match with " +
					"/test/login.css"
			},
			() -> _testIsMatchURLRegexPattern(
				false, "/test/login.css", null,
				new TestFilterConfig(null, _TEST_URL_REGEX_PATTERN)));
	}

	@Test
	public void testReplaceFilter() {
		FilterMapping filterMapping = new FilterMapping(
			_TEST_FILTER_NAME, null,
			new TestFilterConfig(
				_TEST_URL_REGEX_IGNORE_PATTERN, _TEST_URL_REGEX_PATTERN),
			Collections.singletonList(_TEST_URL_PATTERN),
			EnumSet.of(Dispatcher.ASYNC));

		_assertFilterMapping(
			_TEST_FILTER_NAME, _dummyFilter, _TEST_URL_REGEX_IGNORE_PATTERN,
			_TEST_URL_REGEX_PATTERN, new String[] {_TEST_URL_PATTERN},
			EnumSet.of(Dispatcher.ASYNC),
			filterMapping.replaceFilter(_dummyFilter));
	}

	@SuppressWarnings("deprecation")
	private void _assertFilterMapping(
		String expectedFilterName, Filter expectedFilter,
		String expectedUrlRegexIgnore, String expectedUrlRegex,
		String[] expectedUrlPattern, Set<Dispatcher> dispatchers) {

		Set<Dispatcher> expectedDispatchers = new HashSet<>(dispatchers);

		if (expectedDispatchers.isEmpty()) {
			expectedDispatchers = EnumSet.of(Dispatcher.REQUEST);
		}

		_assertFilterMapping(
			expectedFilterName, expectedFilter, expectedUrlRegexIgnore,
			expectedUrlRegex, expectedUrlPattern, expectedDispatchers,
			new FilterMapping(
				expectedFilterName, expectedFilter,
				new TestFilterConfig(expectedUrlRegexIgnore, expectedUrlRegex),
				Arrays.asList(expectedUrlPattern), dispatchers));
	}

	private void _assertFilterMapping(
		String expectedFilterName, Filter expectedFilter,
		String expectedUrlRegexIgnore, String expectedUrlRegex,
		String[] expectedUrlPattern, Set<Dispatcher> expectedDispatchers,
		FilterMapping filterMapping) {

		Assert.assertSame(expectedFilterName, filterMapping.getFilterName());
		Assert.assertSame(expectedFilter, filterMapping.getFilter());
		Assert.assertArrayEquals(
			expectedUrlPattern,
			ReflectionTestUtil.getFieldValue(filterMapping, "_urlPatterns"));
		Assert.assertEquals(
			expectedDispatchers,
			ReflectionTestUtil.getFieldValue(filterMapping, "_dispatchers"));

		_assertURLRegex(
			expectedUrlRegexIgnore,
			ReflectionTestUtil.getFieldValue(
				filterMapping, "_urlRegexIgnorePattern"));
		_assertURLRegex(
			expectedUrlRegex,
			ReflectionTestUtil.getFieldValue(
				filterMapping, "_urlRegexPattern"));
	}

	private void _assertURLRegex(String expectedURLRegex, Pattern pattern) {
		if (expectedURLRegex == null) {
			Assert.assertNull(pattern);
		}
		else {
			Assert.assertEquals(expectedURLRegex, pattern.pattern());
		}
	}

	private String _getIsMatchURLPatternMessage(
		boolean returnValue, String uri, String urlPattern) {

		StringBundler sb = new StringBundler(7);

		sb.append(
			"FilterMapping.isMatchURLPattern(String, String) should return ");
		sb.append(returnValue);
		sb.append(" when uri \"");
		sb.append(uri);

		if (returnValue) {
			sb.append("\" matches pattern ");
		}
		else {
			sb.append("\" does not match pattern \"");
		}

		sb.append(urlPattern);
		sb.append("\"");

		return sb.toString();
	}

	private void _testIsMatch(
		boolean expectedResult, Dispatcher dispatcher, String uri,
		List<String> urlPatterns) {

		FilterMapping filterMapping = new FilterMapping(
			_TEST_FILTER_NAME, _dummyFilter,
			new TestFilterConfig(
				_TEST_URL_REGEX_IGNORE_PATTERN, _TEST_URL_REGEX_PATTERN),
			urlPatterns, EnumSet.of(Dispatcher.ASYNC));

		Assert.assertEquals(
			expectedResult,
			filterMapping.isMatch(
				new MockHttpServletRequest(HttpMethods.GET, uri), dispatcher,
				uri));
	}

	private void _testIsMatchURLRegexPattern(
		boolean expectedResult, String uri, String queryString,
		FilterConfig filterConfig) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethods.GET, uri);

		mockHttpServletRequest.setQueryString(queryString);

		FilterMapping filterMapping = new FilterMapping(
			_TEST_FILTER_NAME, _dummyFilter, filterConfig,
			Collections.emptyList(), EnumSet.of(Dispatcher.REQUEST));

		Assert.assertEquals(
			expectedResult,
			filterMapping.isMatchURLRegexPattern(mockHttpServletRequest, uri));
	}

	private void _testWithLog(String[] expectedMessages, Runnable runnable) {
		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				FilterMapping.class.getName(), Level.INFO)) {

			runnable.run();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(
				"logRecords should be empty because the log level is INFO " +
					"but contains " + logEntries,
				logEntries.isEmpty());

			logCapture.resetPriority(Level.FINE.toString());

			runnable.run();

			Assert.assertEquals(
				logEntries.toString(), expectedMessages.length,
				logEntries.size());

			for (int i = 0; i < logEntries.size(); i++) {
				LogEntry logEntry = logEntries.get(i);

				Assert.assertEquals(expectedMessages[i], logEntry.getMessage());
			}
		}
	}

	private static final String _TEST_FILTER_NAME = "testFilterName";

	private static final String _TEST_URL_PATTERN = "/test/*";

	private static final String _TEST_URL_REGEX_IGNORE_PATTERN = ".+/ignore/*";

	private static final String _TEST_URL_REGEX_PATTERN = ".+\\.jsp";

	private final Filter _dummyFilter = ProxyFactory.newDummyInstance(
		Filter.class);

	private class TestFilterConfig implements FilterConfig {

		@Override
		public String getFilterName() {
			return null;
		}

		@Override
		public String getInitParameter(String parameterName) {
			if (Objects.equals(parameterName, "url-regex-pattern")) {
				return _urlRegexPattern;
			}

			if (Objects.equals(parameterName, "url-regex-ignore-pattern")) {
				return _urlRegexIgnorePattern;
			}

			return null;
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			return null;
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

		private TestFilterConfig(
			String urlRegexIgnorePattern, String urlRegexPattern) {

			_urlRegexIgnorePattern = urlRegexIgnorePattern;
			_urlRegexPattern = urlRegexPattern;
		}

		private final String _urlRegexIgnorePattern;
		private final String _urlRegexPattern;

	}

}
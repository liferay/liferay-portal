/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.rule.logging;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.MethodTestRule;
import com.liferay.portal.search.test.util.logging.ExpectedLog;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import org.junit.Assert;
import org.junit.runner.Description;

/**
 * @author André de Oliveira
 */
public class ExpectedLogMethodTestRule extends MethodTestRule<Void> {

	public static final ExpectedLogMethodTestRule INSTANCE =
		new ExpectedLogMethodTestRule();

	public void verify() {
		if (!_matcherBuilder.isAnythingExpected()) {
			return;
		}

		Assert.assertThat(getlogEntries(), _matcherBuilder.build());
	}

	@Override
	protected void afterMethod(
		Description description, Void unused, Object target) {

		ExpectedLog expectedLog = description.getAnnotation(ExpectedLog.class);

		if (expectedLog == null) {
			return;
		}

		try {
			verify();
		}
		finally {
			closeCaptureHandler();
		}
	}

	@Override
	protected Void beforeMethod(Description description, Object target) {
		ExpectedLog expectedLog = description.getAnnotation(ExpectedLog.class);

		if (expectedLog == null) {
			return null;
		}

		_matcherBuilder.clear();

		Class<?> clazz = expectedLog.expectedClass();

		ExpectedLog.Level level = expectedLog.expectedLevel();

		_configure(clazz.getName(), level.getName());

		_matcherBuilder.add(
			LogOutputMatcher.hasMessage(
				CoreMatchers.containsString(expectedLog.expectedLog())));

		return null;
	}

	protected void closeCaptureHandler() {
		if (_logCapture == null) {
			return;
		}

		_logCapture.close();

		_logCapture = null;
	}

	protected List<LogEntry> getlogEntries() {
		if (_logCapture != null) {
			return _logCapture.getLogEntries();
		}

		return Collections.emptyList();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	protected List<LogRecord> getLogRecords() {
		return Collections.emptyList();
	}

	protected void openCaptureHandler(String name, String level) {
		_logCapture = LoggerTestUtil.configureLog4JLogger(name, level);
	}

	protected static class LogOutputMatcher<T extends List<LogEntry>>
		extends TypeSafeMatcher<T> {

		@Factory
		public static <T extends List<LogEntry>> Matcher<T> hasMessage(
			Matcher<String> matcher) {

			return new LogOutputMatcher<>(matcher);
		}

		public LogOutputMatcher(Matcher<String> matcher) {
			this.matcher = matcher;
		}

		@Override
		public void describeTo(org.hamcrest.Description description) {
			description.appendText("log output with message ");

			description.appendDescriptionOf(matcher);
		}

		@Override
		protected void describeMismatchSafely(
			T logRecords, org.hamcrest.Description description) {

			description.appendText("log output ");

			matcher.describeMismatch(toString(logRecords), description);
		}

		@Override
		protected boolean matchesSafely(T logEntries) {
			return matcher.matches(toString(logEntries));
		}

		protected String toString(T logEntries) {
			StringBundler sb = new StringBundler(logEntries.size());

			for (LogEntry logEntry : logEntries) {
				sb.append(logEntry.getMessage());
			}

			return sb.toString();
		}

		protected final Matcher<String> matcher;

	}

	protected class MatcherBuilder<T> {

		protected void add(Matcher<T> matcher) {
			matchers.add(matcher);
		}

		protected Matcher<?> build() {
			if (matchers.size() == 1) {
				return matchers.get(0);
			}

			return CoreMatchers.allOf(new ArrayList<>((List)matchers));
		}

		protected void clear() {
			matchers.clear();
		}

		protected boolean isAnythingExpected() {
			return !matchers.isEmpty();
		}

		protected final List<Matcher<T>> matchers = new ArrayList<>();

	}

	private void _configure(String name, String level) {
		if ((name == null) || (level == null)) {
			return;
		}

		closeCaptureHandler();

		openCaptureHandler(name, level);
	}

	private LogCapture _logCapture;
	private final MatcherBuilder _matcherBuilder = new MatcherBuilder();

}
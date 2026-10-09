/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.test.rule.AbstractTestRule;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.message.Message;

import org.junit.Assert;
import org.junit.runner.Description;

/**
 * @author Shuyang Zhou
 */
public class LogAssertionTestRule
	extends AbstractTestRule<List<LogCapture>, List<LogCapture>> {

	public static final LogAssertionTestRule INSTANCE =
		new LogAssertionTestRule();

	public static void caughtFailure(Error error) {
		Thread currentThread = Thread.currentThread();

		if (currentThread == _thread) {
			throw error;
		}

		Error previousError = _concurrentFailures.put(currentThread, error);

		if (previousError != null) {
			error.addSuppressed(previousError);
		}
	}

	public static void endAssert(
		List<ExpectedLogs> expectedLogsList, List<LogCapture> logCaptures) {

		uninstallLog4jAppender();
		uninstallJdk14Handler();

		StringBundler sb = new StringBundler();

		for (LogCapture logCapture : logCaptures) {
			try {
				for (LogEntry logEntry : logCapture.getLogEntries()) {
					String message = logEntry.getMessage();

					if (isExpected(expectedLogsList, message)) {
						continue;
					}

					sb.append(message);
					sb.append("\n\n");
				}
			}
			finally {
				logCapture.close();
			}
		}

		if (sb.index() != 0) {
			sb.setIndex(sb.index() - 1);

			Assert.fail(sb.toString());
		}

		Thread.setDefaultUncaughtExceptionHandler(_uncaughtExceptionHandler);

		_thread = null;

		try {
			for (Map.Entry<Thread, Error> entry :
					_concurrentFailures.entrySet()) {

				Thread thread = entry.getKey();
				Error error = entry.getValue();

				UnsyncStringWriter unsyncStringWriter =
					new UnsyncStringWriter();

				error.printStackTrace(
					new UnsyncPrintWriter(unsyncStringWriter));

				sb.append("Thread ");
				sb.append(thread);
				sb.append(" caught concurrent failure: ");
				sb.append(error);
				sb.append("\n");
				sb.append(unsyncStringWriter.toString());
				sb.append("\n\n");
			}

			if (sb.index() != 0) {
				sb.setIndex(sb.index() - 1);

				Assert.fail(sb.toString());
			}
		}
		finally {
			_concurrentFailures.clear();
		}
	}

	public static List<LogCapture> startAssert(
		List<ExpectedLogs> expectedLogsList) {

		_thread = Thread.currentThread();

		_uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

		Thread.setDefaultUncaughtExceptionHandler(
			new LogAssertionUncaughtExceptionHandler(
				_uncaughtExceptionHandler));

		List<LogCapture> logCaptures = new ArrayList<>(expectedLogsList.size());

		for (ExpectedLogs expectedLogs : expectedLogsList) {
			Class<?> clazz = expectedLogs.loggerClass();

			logCaptures.add(
				LoggerTestUtil.configureLog4JLogger(
					clazz.getName(), expectedLogs.level()));
		}

		installJdk14Handler();
		installLog4jAppender();

		return logCaptures;
	}

	@Override
	public void afterClass(
		Description description, List<LogCapture> logCaptures) {

		ExpectedMultipleLogs expectedMultipleLogs = description.getAnnotation(
			ExpectedMultipleLogs.class);

		List<ExpectedLogs> expectedLogsList = new ArrayList<>();

		if (expectedMultipleLogs == null) {
			ExpectedLogs expectedLogs = description.getAnnotation(
				ExpectedLogs.class);

			if (expectedLogs != null) {
				expectedLogsList.add(expectedLogs);
			}
		}
		else {
			Collections.addAll(
				expectedLogsList, expectedMultipleLogs.expectedMultipleLogs());
		}

		endAssert(expectedLogsList, logCaptures);
	}

	@Override
	public void afterMethod(
		Description description, List<LogCapture> logCaptures, Object target) {

		afterClass(description, logCaptures);
	}

	@Override
	public List<LogCapture> beforeClass(Description description) {
		ExpectedMultipleLogs expectedMultipleLogs = description.getAnnotation(
			ExpectedMultipleLogs.class);

		List<ExpectedLogs> expectedLogsList = new ArrayList<>();

		if (expectedMultipleLogs == null) {
			ExpectedLogs expectedLogs = description.getAnnotation(
				ExpectedLogs.class);

			if (expectedLogs != null) {
				expectedLogsList.add(expectedLogs);
			}
		}
		else {
			Collections.addAll(
				expectedLogsList, expectedMultipleLogs.expectedMultipleLogs());
		}

		return startAssert(expectedLogsList);
	}

	@Override
	public List<LogCapture> beforeMethod(
		Description description, Object target) {

		return beforeClass(description);
	}

	protected static void installJdk14Handler() {
		Logger logger = Logger.getLogger(StringPool.BLANK);

		logger.removeHandler(_logHandler);

		logger.addHandler(_logHandler);
	}

	protected static void installLog4jAppender() {
		org.apache.logging.log4j.core.Logger logger =
			(org.apache.logging.log4j.core.Logger)LogManager.getRootLogger();

		logger.removeAppender(_logAppender);

		logger.addAppender(_logAppender);
	}

	protected static boolean isExpected(
		List<ExpectedLogs> expectedLogsList, String renderedMessage) {

		for (ExpectedLogs expectedLogs : expectedLogsList) {
			for (ExpectedLog expectedLog : expectedLogs.expectedLogs()) {
				ExpectedDBType expectedDBType = expectedLog.expectedDBType();

				if ((expectedDBType != ExpectedDBType.NONE) &&
					(expectedDBType.getDBType() != DBManagerUtil.getDBType())) {

					continue;
				}

				ExpectedType expectedType = expectedLog.expectedType();

				if (expectedType == ExpectedType.CONTAINS) {
					if (renderedMessage.contains(expectedLog.expectedLog())) {
						return true;
					}
				}
				else if (expectedType == ExpectedType.EXACT) {
					if (renderedMessage.equals(expectedLog.expectedLog())) {
						return true;
					}
				}
				else if (expectedType == ExpectedType.POSTFIX) {
					if (renderedMessage.endsWith(expectedLog.expectedLog())) {
						return true;
					}
				}
				else if (expectedType == ExpectedType.PREFIX) {
					if (renderedMessage.startsWith(expectedLog.expectedLog())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected static void uninstallJdk14Handler() {
		Logger logger = Logger.getLogger(StringPool.BLANK);

		logger.removeHandler(_logHandler);
	}

	protected static void uninstallLog4jAppender() {
		org.apache.logging.log4j.core.Logger logger =
			(org.apache.logging.log4j.core.Logger)LogManager.getRootLogger();

		logger.removeAppender(_logAppender);
	}

	private LogAssertionTestRule() {
	}

	private static final Map<Thread, Error> _concurrentFailures =
		new ConcurrentHashMap<>();
	private static final LogAppender _logAppender = new LogAppender();
	private static final LogHandler _logHandler = new LogHandler();
	private static volatile Thread _thread;
	private static volatile Thread.UncaughtExceptionHandler
		_uncaughtExceptionHandler;

	private static class LogAppender extends AbstractAppender {

		@Override
		public void append(LogEvent logEvent) {
			Level level = logEvent.getLevel();

			if (level.equals(Level.ERROR) || level.equals(Level.FATAL)) {
				StringBundler sb = new StringBundler(6);

				sb.append("{level=");
				sb.append(logEvent.getLevel());
				sb.append(", loggerName=");
				sb.append(logEvent.getLoggerName());
				sb.append(", message=");

				Message message = logEvent.getMessage();

				sb.append(message.getFormattedMessage());

				LogAssertionTestRule.caughtFailure(
					new AssertionError(sb.toString(), logEvent.getThrown()));
			}
		}

		private LogAppender() {
			super(StringUtil.randomString(), null, null, true, null);

			start();
		}

	}

	private static class LogHandler extends Handler {

		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(LogRecord logRecord) {
			java.util.logging.Level level = logRecord.getLevel();

			if (level.equals(java.util.logging.Level.SEVERE)) {
				LogAssertionTestRule.caughtFailure(
					new AssertionError(
						StringBundler.concat(
							"{level=", logRecord.getLevel(), ", loggerName=",
							logRecord.getLoggerName(), ", message=",
							logRecord.getMessage()),
						logRecord.getThrown()));
			}
		}

	}

}
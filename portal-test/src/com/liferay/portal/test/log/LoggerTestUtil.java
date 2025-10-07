/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.log;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogWrapper;
import com.liferay.portal.kernel.log4j.Log4JUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.message.Message;

/**
 * @author Shuyang Zhou
 */
public class LoggerTestUtil {

	public static final String ALL = String.valueOf(Level.ALL);

	public static final String DEBUG = String.valueOf(Level.DEBUG);

	public static final String ERROR = String.valueOf(Level.ERROR);

	public static final String FATAL = String.valueOf(Level.FATAL);

	public static final String INFO = String.valueOf(Level.INFO);

	public static final String OFF = String.valueOf(Level.OFF);

	public static final String TRACE = String.valueOf(Level.TRACE);

	public static final String WARN = String.valueOf(Level.WARN);

	public static LogCapture configureLog4JLogger(
		String name, String priority) {

		Log log = LogFactoryUtil.getLog(name);

		while (log instanceof LogWrapper) {
			LogWrapper logWrapper = (LogWrapper)log;

			log = logWrapper.getWrappedLog();
		}

		Logger logger = null;

		try {
			logger = ReflectionTestUtil.getFieldValue(log, "_logger");
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Log " + name + " is not a Log4j logger");
		}

		Log4JLogCapture log4JLogCapture = new Log4JLogCapture(logger, priority);

		log4JLogCapture.start();

		logger.addAppender(log4JLogCapture);

		return log4JLogCapture;
	}

	static {

		// See LPS-32051 and LPS-32471

		LogFactoryUtil.getLog(LoggerTestUtil.class);
	}

	private static class Log4JLogCapture
		extends AbstractAppender implements LogCapture {

		@Override
		public void append(LogEvent logEvent) {
			Message message = logEvent.getMessage();

			_logEntries.add(
				new LogEntry(
					message.getFormattedMessage(),
					String.valueOf(logEvent.getLevel()), logEvent.getThrown()));
		}

		@Override
		public void close() {
			_logger.removeAppender(this);

			stop();

			_loggerConfig.setAdditive(_additive);

			Log4JUtil.setLevel(_logger.getName(), _level.toString(), false);
		}

		@Override
		public List<LogEntry> getLogEntries() {
			return _logEntries;
		}

		@Override
		public List<LogEntry> resetPriority(String priority) {
			_logEntries.clear();

			_logger.setLevel(Level.toLevel(priority));

			return _logEntries;
		}

		private Log4JLogCapture(Logger logger, String priority) {
			super(StringUtil.randomString(), null, null, true, null);

			_logger = logger;

			_level = logger.getLevel();

			Log4JUtil.setLevel(logger.getName(), priority, false);

			_loggerConfig = logger.get();

			_additive = _loggerConfig.isAdditive();

			_loggerConfig.setAdditive(false);
		}

		private final boolean _additive;
		private final Level _level;
		private final List<LogEntry> _logEntries = new CopyOnWriteArrayList<>();
		private final Logger _logger;
		private final LoggerConfig _loggerConfig;

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.log;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class SanitizerLogWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		char[] chars = new char[128];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char)i;
		}

		_message = new String(chars);

		String sanitizedMessageSuffix = " [Sanitized]";

		_messageChars =
			new char[chars.length + sanitizedMessageSuffix.length()];

		for (int i = 0; i < chars.length; i++) {
			if ((i == 9) || ((i >= 32) && (i != 127))) {
				_messageChars[i] = (char)i;
			}
			else {
				_messageChars[i] = CharPool.UNDERLINE;
			}
		}

		System.arraycopy(
			sanitizedMessageSuffix.toCharArray(), 0, _messageChars,
			chars.length, sanitizedMessageSuffix.length());

		_systemProperties = new Properties(System.getProperties());

		System.setProperty("log.sanitizer.enabled", "true");
		System.setProperty("log.sanitizer.escape.html.enabled", "false");
		System.setProperty("log.sanitizer.replacement.character", "95");

		StringBundler sb = new StringBundler(191);

		sb.append("9,");

		for (int i = 32; i <= 126; i++) {
			sb.append(i);
			sb.append(",");
		}

		System.setProperty("log.sanitizer.whitelist.characters", sb.toString());

		ReflectionTestUtil.setFieldValue(
			SanitizerLogWrapper.class, "_LOG_SANITIZER_ENABLED", true);

		SanitizerLogWrapper.init();
	}

	@AfterClass
	public static void tearDownClass() {
		System.setProperties(_systemProperties);
	}

	@Before
	public void setUp() {
		String loggerName = "test.logger";

		_logCapture = LoggerTestUtil.configureLog4JLogger(
			loggerName, LoggerTestUtil.ALL);

		LogFactory logFactory = LogFactoryUtil.getLogFactory();

		_log = new SanitizerLogWrapper(logFactory.getLog(loggerName));
	}

	@After
	public void tearDown() {
		_logCapture.close();
	}

	@Test
	public void testAllowCRLF() {
		Exception exception = new NullPointerException();

		char[] expectedMessageWithCRLFChars = new char[_messageChars.length];

		System.arraycopy(
			_messageChars, 0, expectedMessageWithCRLFChars, 0,
			expectedMessageWithCRLFChars.length);

		expectedMessageWithCRLFChars[CharPool.NEW_LINE] = CharPool.NEW_LINE;
		expectedMessageWithCRLFChars[CharPool.RETURN] = CharPool.RETURN;

		Log log = SanitizerLogWrapper.allowCRLF(_log);

		log.debug(_message);
		log.debug(_message, exception);
		log.error(_message);
		log.error(_message, exception);
		log.fatal(_message);
		log.fatal(_message, exception);
		log.info(_message);
		log.info(_message, exception);
		log.trace(_message);
		log.trace(_message, exception);
		log.warn(_message);
		log.warn(_message, exception);

		List<LogEntry> logEntries = _logCapture.getLogEntries();

		Assert.assertNotNull(logEntries);
		Assert.assertEquals(logEntries.toString(), 12, logEntries.size());

		for (LogEntry logEntry : logEntries) {
			String message = logEntry.getMessage();

			Assert.assertTrue(
				message.startsWith(SanitizerLogWrapper.CRLF_WARNING));

			int messageWithCRLFCharsLength =
				message.length() - SanitizerLogWrapper.CRLF_WARNING.length();

			char[] messageWithCRLFChars = new char[messageWithCRLFCharsLength];

			message.getChars(
				SanitizerLogWrapper.CRLF_WARNING.length(), message.length(),
				messageWithCRLFChars, 0);

			Assert.assertArrayEquals(
				expectedMessageWithCRLFChars, messageWithCRLFChars);
		}

		logEntries.clear();

		_log.debug(_message);
		_log.debug(_message, exception);
		_log.error(_message);
		_log.error(_message, exception);
		_log.fatal(_message);
		_log.fatal(_message, exception);
		_log.info(_message);
		_log.info(_message, exception);
		_log.trace(_message);
		_log.trace(_message, exception);
		_log.warn(_message);
		_log.warn(_message, exception);

		Assert.assertNotNull(logEntries);
		Assert.assertEquals(logEntries.toString(), 12, logEntries.size());

		for (LogEntry logEntry : logEntries) {
			String message = logEntry.getMessage();

			char[] messageChars = message.toCharArray();

			Assert.assertArrayEquals(_messageChars, messageChars);
		}
	}

	@Test
	public void testInvalidCharactersInExceptionMessage() {
		Exception exception = new Exception(
			new RuntimeException(new NullPointerException(_message)));

		String exceptionPrefix =
			"java.lang.Exception: java.lang.RuntimeException: " +
				"java.lang.NullPointerException: ";

		_log.debug(exception);
		_log.debug(null, exception);
		_log.error(exception);
		_log.error(null, exception);
		_log.fatal(exception);
		_log.fatal(null, exception);
		_log.info(exception);
		_log.info(null, exception);
		_log.trace(exception);
		_log.trace(null, exception);
		_log.warn(exception);
		_log.warn(null, exception);

		List<LogEntry> logEntries = _logCapture.getLogEntries();

		Assert.assertNotNull(logEntries);
		Assert.assertEquals(logEntries.toString(), 12, logEntries.size());

		for (LogEntry logEntry : logEntries) {
			Throwable throwable = logEntry.getThrowable();

			String throwableMessage = throwable.getMessage();

			Assert.assertTrue(throwableMessage.startsWith(exceptionPrefix));

			char[] messageChars =
				new char[throwableMessage.length() - exceptionPrefix.length()];

			throwableMessage.getChars(
				exceptionPrefix.length(), throwableMessage.length(),
				messageChars, 0);

			Assert.assertArrayEquals(_messageChars, messageChars);
		}
	}

	@Test
	public void testInvalidCharactersInLogMessage() {
		Exception exception = new NullPointerException();

		_log.debug(_message);
		_log.debug(_message, exception);
		_log.error(_message);
		_log.error(_message, exception);
		_log.fatal(_message);
		_log.fatal(_message, exception);
		_log.info(_message);
		_log.info(_message, exception);
		_log.trace(_message);
		_log.trace(_message, exception);
		_log.warn(_message);
		_log.warn(_message, exception);

		List<LogEntry> logEntries = _logCapture.getLogEntries();

		Assert.assertNotNull(logEntries);
		Assert.assertEquals(logEntries.toString(), 12, logEntries.size());

		for (LogEntry logEntry : logEntries) {
			String message = logEntry.getMessage();

			char[] messageChars = message.toCharArray();

			Assert.assertArrayEquals(_messageChars, messageChars);
		}
	}

	private static Log _log;

	private static String _message;
	private static char[] _messageChars;
	private static Properties _systemProperties;

	private LogCapture _logCapture;

}
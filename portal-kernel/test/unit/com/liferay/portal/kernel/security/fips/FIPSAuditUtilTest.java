/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.internal.log4j.FIPSLog4jUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.Time;

import java.time.Instant;

import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 * @author Rafael Praxedes
 */
public class FIPSAuditUtilTest {

	@BeforeClass
	public static void setUpClass() {
		_logManagerMockedStatic.when(
			() -> LogManager.getLogger(FIPSLog4jUtil.class)
		).thenReturn(
			_logger
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_logManagerMockedStatic.close();
	}

	@Before
	public void setUp() {
		Mockito.reset(_logger);

		Mockito.when(
			_logger.isEnabled(Level.INFO)
		).thenReturn(
			true
		);

		_safeCloseable = PropsValuesTestUtil.swapWithSafeCloseable(
			"FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID", RandomTestUtil.randomString());

		_mockLogManager(null);
	}

	@After
	public void tearDown() {
		_safeCloseable.close();
	}

	@Test
	public void testWriteFormatsTimestampInUTC() {
		TimeZone timeZone = TimeZone.getDefault();

		TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

		FIPSAuditUtil.write(
			new FIPSAuditEvent(
				RandomTestUtil.randomString(), FIPSAuditEvent.Severity.INFO));

		Map<String, Object> fipsAuditLogEntry = _getLastFIPSAuditLogEntry();

		Instant instant = Instant.parse(
			String.valueOf(fipsAuditLogEntry.get("timestamp")));

		Assert.assertTrue(
			Math.abs(System.currentTimeMillis() - instant.toEpochMilli()) <
				Time.MINUTE);

		TimeZone.setDefault(timeZone);
	}

	@Test
	public void testWriteNormalizesAFieldTimestampInAnArray() {
		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			RandomTestUtil.randomString(), FIPSAuditEvent.Severity.INFO);

		fipsAuditEvent.put(
			"provider-timestamps",
			new Instant[] {Instant.parse("2026-05-06T14:19:23.471Z")});

		FIPSAuditUtil.write(fipsAuditEvent);

		Map<String, Object> fipsAuditLogEntry = _getLastFIPSAuditLogEntry();

		Map<?, ?> fields = (Map<?, ?>)fipsAuditLogEntry.get("fields");

		Assert.assertEquals(
			Collections.singletonList("2026-05-06T14:19:23.471Z"),
			fields.get("provider-timestamps"));
	}

	@Test
	public void testWriteThrowsWhenAppenderIsMissing() {
		_testWriteThrows();
	}

	@Test
	public void testWriteThrowsWhenLayoutIsNotTheNDJSONLayout() {
		RollingFileAppender rollingFileAppender = Mockito.mock(
			RollingFileAppender.class);

		Mockito.doReturn(
			Mockito.mock(Layout.class)
		).when(
			rollingFileAppender
		).getLayout();

		_mockLogManager(rollingFileAppender);

		_testWriteThrows();
	}

	@Test
	public void testWriteThrowsWhenLoggerIsDisabled() {
		Mockito.when(
			_logger.isEnabled(Level.INFO)
		).thenReturn(
			false
		);

		_testWriteThrows();
	}

	private Map<String, Object> _getLastFIPSAuditLogEntry() {
		ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(
			Message.class);

		Mockito.verify(
			_logger, Mockito.atLeastOnce()
		).log(
			Mockito.eq(Level.INFO), Mockito.eq(FIPSLog4jUtil.getMarker()),
			argumentCaptor.capture()
		);

		ObjectMessage objectMessage = (ObjectMessage)argumentCaptor.getValue();

		return (Map<String, Object>)objectMessage.getParameter();
	}

	private void _mockLogManager(RollingFileAppender rollingFileAppender) {
		Configuration configuration = Mockito.mock(Configuration.class);

		Mockito.when(
			configuration.getAppender("FIPS_AUDIT_FILE")
		).thenReturn(
			rollingFileAppender
		);

		LoggerContext loggerContext = Mockito.mock(LoggerContext.class);

		Mockito.when(
			loggerContext.getConfiguration()
		).thenReturn(
			configuration
		);

		_logManagerMockedStatic.when(
			() -> LogManager.getContext(false)
		).thenReturn(
			loggerContext
		);
	}

	private void _testWriteThrows() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable("FIPS_ENABLED", true);
			MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(
					ServerDetector.class, Mockito.CALLS_REAL_METHODS)) {

			serverDetectorMockedStatic.when(
				ServerDetector::getServerId
			).thenReturn(
				RandomTestUtil.randomString()
			);

			Assert.assertThrows(
				IllegalStateException.class,
				() -> FIPSAuditUtil.write(
					new FIPSAuditEvent(
						RandomTestUtil.randomString(),
						FIPSAuditEvent.Severity.INFO)));
		}
	}

	private static final Logger _logger = Mockito.mock(Logger.class);

	private static final MockedStatic<LogManager> _logManagerMockedStatic =
		Mockito.mockStatic(LogManager.class, Mockito.CALLS_REAL_METHODS);

	private SafeCloseable _safeCloseable;

}
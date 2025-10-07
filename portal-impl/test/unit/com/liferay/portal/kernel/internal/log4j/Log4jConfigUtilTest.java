/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Hai Yu
 */
public class Log4jConfigUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testCompanyWebIdConsoleAppender() throws Exception {
		PrintStream systemOutPrintStream = System.out;

		try (MockedStatic<CompanyThreadLocal> companyThreadLocalMockedStatic =
				Mockito.mockStatic(CompanyThreadLocal.class);
			MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic =
				Mockito.mockStatic(PortalInstancePool.class);
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(byteArrayOutputStream)) {

			System.setOut(printStream);

			Logger logger = (Logger)LogManager.getLogger(
				StringUtil.randomString());

			Log4jConfigUtil.configureLog4J(
				_generateXMLConfigurationContent(
					logger.getName(), _PRIORITY_INFO,
					_APPENDER_TYPE_COMPANY_WEB_ID_CONSOLE));

			_assertAppenders(logger, _APPENDER_TYPE_COMPANY_WEB_ID_CONSOLE);

			long companyId = RandomTestUtil.randomLong();

			portalInstancePoolMockedStatic.when(
				() -> PortalInstancePool.getWebId(companyId)
			).thenReturn(
				"test.com"
			);

			_testCompanyWebIdConsoleAppender(
				new long[0], byteArrayOutputStream, 0L,
				companyThreadLocalMockedStatic, "Test Log Message", logger,
				"Test Log Message", portalInstancePoolMockedStatic);
			_testCompanyWebIdConsoleAppender(
				new long[0], byteArrayOutputStream, companyId,
				companyThreadLocalMockedStatic, "Test Log Message", logger,
				"Test Log Message", portalInstancePoolMockedStatic);
			_testCompanyWebIdConsoleAppender(
				new long[] {companyId}, byteArrayOutputStream, companyId,
				companyThreadLocalMockedStatic, "test.com Test Log Message",
				logger, "Test Log Message", portalInstancePoolMockedStatic);
		}
		finally {
			System.setOut(systemOutPrintStream);
		}
	}

	@Test
	public void testConfigureLog4J() {
		String loggerName = StringUtil.randomString();

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		Map<String, String> priorities = Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_ALL));

		Assert.assertEquals(
			priorities, Collections.singletonMap(loggerName, _PRIORITY_ALL));

		_assertPriority(logger, _PRIORITY_ALL);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_DEBUG));

		_assertPriority(logger, _PRIORITY_DEBUG);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_ERROR));

		_assertPriority(logger, _PRIORITY_ERROR);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_FATAL));

		_assertPriority(logger, _PRIORITY_FATAL);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_INFO));

		_assertPriority(logger, _PRIORITY_INFO);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_OFF));

		_assertPriority(logger, _PRIORITY_OFF);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_TRACE));

		_assertPriority(logger, _PRIORITY_TRACE);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_WARN));

		_assertPriority(logger, _PRIORITY_WARN);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, "FAKE_PRIORITY"));

		_assertPriority(logger, _PRIORITY_ERROR);
	}

	@Test
	public void testConfigureLog4JWithAppender() {
		String loggerName = StringUtil.randomString();

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_ERROR));

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		_assertAppenders(logger);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _PRIORITY_ERROR, _APPENDER_TYPE_CONSOLE));

		_assertAppenders(logger, _APPENDER_TYPE_CONSOLE);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _PRIORITY_ERROR, _APPENDER_TYPE_NULL));

		_assertAppenders(logger, _APPENDER_TYPE_CONSOLE, _APPENDER_TYPE_NULL);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _PRIORITY_ERROR, _APPENDER_TYPE_CONSOLE));

		_assertAppenders(logger, _APPENDER_TYPE_CONSOLE, _APPENDER_TYPE_NULL);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _PRIORITY_ERROR, _APPENDER_TYPE_CONSOLE,
				_APPENDER_TYPE_NULL));

		_assertAppenders(logger, _APPENDER_TYPE_CONSOLE, _APPENDER_TYPE_NULL);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _PRIORITY_ERROR, _APPENDER_TYPE_CONSOLE,
				_APPENDER_TYPE_NULL),
			_APPENDER_TYPE_NULL);

		_assertAppenders(logger, _APPENDER_TYPE_CONSOLE, _APPENDER_TYPE_NULL);
	}

	@Test
	public void testConfigureLog4JWithException() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				Log4jConfigUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Log4jConfigUtil.configureLog4J(null);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(NullPointerException.class, throwable.getClass());

			String xmlContent = _generateXMLConfigurationContent(
				StringUtil.randomString(), _PRIORITY_INFO);

			xmlContent = StringUtil.removeSubstring(
				xmlContent, "strict=\"true\"");

			Log4jConfigUtil.configureLog4J(xmlContent);

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"<Configuration> strict attribute requires true",
				logEntry.getMessage());

			Log4jConfigUtil.configureLog4J(
				_generateLog4j1XMLConfigurationContent());

			Assert.assertEquals(logEntries.toString(), 3, logEntries.size());

			logEntry = logEntries.get(2);

			Assert.assertEquals(
				"Log4J 2 <Configuration> is required", logEntry.getMessage());
		}
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testGetCompanyLogDirectoryWithCompanyLogDisabled()
		throws Exception {

		PropsUtil.set(PropsKeys.COMPANY_LOG_ENABLED, "false");

		_testGetCompanyLogDirectory(false);
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testGetCompanyLogDirectoryWithCompanyLogEnabled()
		throws Exception {

		PropsUtil.set(PropsKeys.COMPANY_LOG_ENABLED, "true");

		_testGetCompanyLogDirectory(true);
	}

	@Test
	public void testGetJDKLevel() {
		Assert.assertEquals(
			"FINE",
			String.valueOf(Log4jConfigUtil.getJDKLevel(_PRIORITY_DEBUG)));
		Assert.assertEquals(
			"SEVERE",
			String.valueOf(Log4jConfigUtil.getJDKLevel(_PRIORITY_ERROR)));
		Assert.assertEquals(
			"INFO",
			String.valueOf(Log4jConfigUtil.getJDKLevel(_PRIORITY_INFO)));
		Assert.assertEquals(
			"WARNING",
			String.valueOf(Log4jConfigUtil.getJDKLevel(_PRIORITY_WARN)));
	}

	@Test
	public void testGetPriorities() {
		String loggerName = StringUtil.randomString();

		Map<String, String> priorities = Log4jConfigUtil.getPriorities();

		Assert.assertFalse(priorities.containsKey(loggerName));

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_WARN));

		priorities = Log4jConfigUtil.getPriorities();

		Assert.assertEquals(
			"The priority should be WARN by configuration", _PRIORITY_WARN,
			priorities.get(loggerName));

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, null));

		priorities = Log4jConfigUtil.getPriorities();

		Assert.assertEquals(
			"The level should use its parent level(root logger level is Error)",
			_PRIORITY_ERROR, priorities.get(loggerName));
	}

	@Test
	public void testMisc() {
		new Log4jConfigUtil();
	}

	@Test
	public void testSetLevel() {
		String loggerName = StringUtil.randomString();

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		_assertPriority(logger, _PRIORITY_ERROR);

		String childLoggerName = loggerName + ".child";

		Logger childLogger = (Logger)LogManager.getLogger(childLoggerName);

		_assertPriority(childLogger, _PRIORITY_ERROR);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_WARN));

		_assertPriority(logger, _PRIORITY_WARN);
		_assertPriority(childLogger, _PRIORITY_WARN);

		Log4jConfigUtil.setLevel(loggerName, _PRIORITY_DEBUG);

		_assertPriority(logger, _PRIORITY_DEBUG);
		_assertPriority(childLogger, _PRIORITY_DEBUG);

		Log4jConfigUtil.setLevel(childLoggerName, _PRIORITY_ERROR);

		_assertPriority(logger, _PRIORITY_DEBUG);
		_assertPriority(childLogger, _PRIORITY_ERROR);
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@Test
	public void testShutdownLog4J() {
		Logger logger = (Logger)LogManager.getRootLogger();

		Map<String, Appender> appenders = logger.getAppenders();

		Assert.assertTrue(
			"The root logger should include appenders", !appenders.isEmpty());

		Log4jConfigUtil.shutdownLog4J();

		Assert.assertFalse(
			"The root logger should not own appenders after shutting down",
			appenders.isEmpty());
	}

	private void _assertAppenders(Logger logger, String... appenderTypes) {
		LoggerConfig loggerConfig = logger.get();

		Map<String, Appender> appenders = loggerConfig.getAppenders();

		Assert.assertEquals(
			String.valueOf(appenders.keySet()), appenderTypes.length,
			appenders.size());

		List<String> appenderRefs = TransformUtil.unsafeTransform(
			loggerConfig.getAppenderRefs(), AppenderRef::getRef);

		Assert.assertEquals(
			appenderRefs.toString(), appenderTypes.length, appenderRefs.size());

		for (String appenderType : appenderTypes) {
			Assert.assertTrue(appenderRefs.contains(appenderType));
			Assert.assertTrue(appenders.containsKey(appenderType));
		}
	}

	private void _assertPriority(Logger logger, String priority) {
		if (priority.equals(_PRIORITY_ALL)) {
			Assert.assertTrue(
				"TRACE should be enabled if logging priority is ALL",
				logger.isTraceEnabled());

			return;
		}

		if (logger.isTraceEnabled()) {
			Assert.assertEquals(
				"Logging priority is wrong", priority, _PRIORITY_TRACE);
		}
		else if (logger.isDebugEnabled()) {
			Assert.assertEquals(
				"Logging priority is wrong", priority, _PRIORITY_DEBUG);
		}
		else if (logger.isInfoEnabled()) {
			Assert.assertEquals(
				"Logging priority is wrong", priority, _PRIORITY_INFO);
		}
		else if (logger.isWarnEnabled()) {
			Assert.assertEquals(
				"Logging priority is wrong", priority, _PRIORITY_WARN);
		}
		else if (logger.isErrorEnabled()) {
			Assert.assertEquals(
				"Logging priority is wrong", priority, _PRIORITY_ERROR);
		}
		else if (logger.isFatalEnabled()) {
			Assert.assertEquals(
				"Logging priority is wrong", priority, _PRIORITY_FATAL);
		}
		else {
			Assert.assertEquals(
				"Logging priority is wrong", priority, _PRIORITY_OFF);
		}
	}

	private String _generateCompanyLogRoutingAppenderConfigurationContent(
		String appenderName, String dirPattern, String fileNamePattern,
		String loggerName, String priority) {

		StringBundler sb = new StringBundler(17);

		sb.append("<?xml version=\"1.0\"?><Configuration strict=\"true\">");
		sb.append("<Appenders><Appender name=\"");
		sb.append(appenderName);
		sb.append("\" dirPattern=\"");
		sb.append(dirPattern);
		sb.append("\" type=\"CompanyLogRouting\">");
		sb.append("<FilePattern fileNamePattern=\"");
		sb.append(fileNamePattern);
		sb.append("\"><Layout type=\"PatternLayout\" /></FilePattern>");
		sb.append("<TimeBasedTriggeringPolicy /><DirectWriteRolloverStrategy ");
		sb.append("/></Appender></Appenders><Loggers><Logger level= \"");
		sb.append(priority);
		sb.append("\" name=\"");
		sb.append(loggerName);
		sb.append("\"><AppenderRef ref=\"");
		sb.append(appenderName);
		sb.append("\" /></Logger></Loggers></Configuration>");

		return sb.toString();
	}

	private String _generateLog4j1XMLConfigurationContent() {
		StringBundler sb = new StringBundler(5);

		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">");
		sb.append("<log4j:configuration xmlns:log4j=");
		sb.append("\"http://jakarta.apache.org/log4j/\">");
		sb.append("</log4j:configuration>");

		return sb.toString();
	}

	private String _generateXMLConfigurationContent(
		String loggerName, String priority, String... appenderTypes) {

		int initialCapacity =
			(appenderTypes.length == 0) ? 7 : (9 + (9 * appenderTypes.length));

		if (ArrayUtil.contains(appenderTypes, _APPENDER_TYPE_CONSOLE, false)) {
			initialCapacity = initialCapacity + 1;
		}

		if (ArrayUtil.contains(
				appenderTypes, _APPENDER_TYPE_COMPANY_WEB_ID_CONSOLE, false)) {

			initialCapacity = initialCapacity + 2;
		}

		StringBundler sb = new StringBundler(initialCapacity);

		sb.append("<?xml version=\"1.0\"?><Configuration strict=\"true\">");

		if (appenderTypes.length > 0) {
			sb.append("<Appenders>");

			for (String appenderType : appenderTypes) {
				sb.append("<Appender name=\"");
				sb.append(appenderType);
				sb.append("\" type=\"");
				sb.append(appenderType);
				sb.append("\">");

				if (appenderType.equals(
						_APPENDER_TYPE_COMPANY_WEB_ID_CONSOLE)) {

					sb.append("<Layout pattern=\"%X{company.webId} %m%n");
					sb.append("\" type=\"PatternLayout\"/>");
				}
				else if (appenderType.equals(_APPENDER_TYPE_CONSOLE)) {
					sb.append("<Layout type=\"PatternLayout\"/>");
				}

				sb.append("</Appender>");
			}

			sb.append("</Appenders>");
		}

		sb.append("<Loggers><Logger level= \"");
		sb.append(priority);
		sb.append("\" name=\"");
		sb.append(loggerName);
		sb.append("\">");

		for (String appenderType : appenderTypes) {
			sb.append("<AppenderRef ref=\"");
			sb.append(appenderType);
			sb.append("\" />");
		}

		sb.append("</Logger></Loggers></Configuration>");

		return sb.toString();
	}

	private void _testCompanyWebIdConsoleAppender(
		long[] availableCompanyIds, ByteArrayOutputStream byteArrayOutputStream,
		long companyId,
		MockedStatic<CompanyThreadLocal> companyThreadLocalMockedStatic,
		String expectedLogMessage, Logger logger, String logMessage,
		MockedStatic<PortalInstancePool> portalInstancePoolMockedStatic) {

		companyThreadLocalMockedStatic.when(
			CompanyThreadLocal::getCompanyId
		).thenReturn(
			companyId
		);

		portalInstancePoolMockedStatic.when(
			PortalInstancePool::getCompanyIds
		).thenReturn(
			availableCompanyIds
		);

		byteArrayOutputStream.reset();

		logger.log(Level.INFO, logMessage);

		String actualLogMessage = byteArrayOutputStream.toString();

		Assert.assertEquals(expectedLogMessage, actualLogMessage.trim());
	}

	private void _testGetCompanyLogDirectory(boolean enabled) throws Exception {
		long companyId = CompanyThreadLocal.getCompanyId();

		String loggerName = StringUtil.randomString();

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _PRIORITY_INFO));

		try {
			Log4jConfigUtil.getCompanyLogDirectory(companyId);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertEquals(
				"No company log routing appender defined",
				illegalStateException.getMessage());
		}

		File tempLogFileDir = null;

		try {
			Path path = Files.createTempDirectory(
				Log4jConfigUtilTest.class.getName());

			tempLogFileDir = path.toFile();

			String tempLogFileDirPathString = tempLogFileDir.getPath();

			Log4jConfigUtil.configureLog4J(
				_generateCompanyLogRoutingAppenderConfigurationContent(
					"COMPANY_LOG_ROUTING_TEXT_FILE",
					tempLogFileDirPathString + "/@company.id@",
					"liferay-@company.id@.%d{yyyy-MM-dd}.log", loggerName,
					_PRIORITY_INFO));

			Logger logger = (Logger)LogManager.getLogger(loggerName);

			logger.info("Test message");

			File companyLogDirectory = Log4jConfigUtil.getCompanyLogDirectory(
				companyId);

			File expectedCompanyLogDirectory = new File(
				tempLogFileDirPathString, String.valueOf(companyId));

			String expectedCompanyLogDirectoryPathString =
				expectedCompanyLogDirectory.getPath();

			Assert.assertEquals(
				"Company log directory should be " +
					expectedCompanyLogDirectoryPathString,
				expectedCompanyLogDirectoryPathString,
				companyLogDirectory.getPath());

			if (enabled) {
				Assert.assertTrue(companyLogDirectory.exists());
			}
			else {
				Assert.assertFalse(companyLogDirectory.exists());
			}
		}
		finally {
			if (tempLogFileDir != null) {
				for (File file : tempLogFileDir.listFiles()) {
					file.delete();
				}

				tempLogFileDir.delete();
			}
		}
	}

	private static final String _APPENDER_TYPE_COMPANY_WEB_ID_CONSOLE =
		"CompanyWebIdConsole";

	private static final String _APPENDER_TYPE_CONSOLE = "CONSOLE";

	private static final String _APPENDER_TYPE_NULL = "NULL";

	private static final String _PRIORITY_ALL = "ALL";

	private static final String _PRIORITY_DEBUG = "DEBUG";

	private static final String _PRIORITY_ERROR = "ERROR";

	private static final String _PRIORITY_FATAL = "FATAL";

	private static final String _PRIORITY_INFO = "INFO";

	private static final String _PRIORITY_OFF = "OFF";

	private static final String _PRIORITY_TRACE = "TRACE";

	private static final String _PRIORITY_WARN = "WARN";

}
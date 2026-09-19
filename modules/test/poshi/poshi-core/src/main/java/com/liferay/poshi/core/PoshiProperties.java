/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core;

import com.liferay.poshi.core.util.GetterUtil;
import com.liferay.poshi.core.util.ListUtil;
import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 */
public class PoshiProperties extends Properties {

	public static final String ACCESSIBILITY_STANDARDS_TAGS =
		"accessibility.standards.tags";

	public static final String BROWSER_CHROME_BIN_ARGS =
		"browser.chrome.bin.args";

	public static final String BROWSER_CHROME_BIN_FILE =
		"browser.chrome.bin.file";

	public static final String BROWSER_FIREFOX_BIN_FILE =
		"browser.firefox.bin.file";

	public static final String BROWSER_RESOLUTION = "browser.resolution";

	public static final String BROWSER_TYPE = "browser.type";

	public static final String BROWSER_VERSION = "browser.version";

	public static final String DEBUG_STACKTRACE = "debug.stacktrace";

	public static final String GENERATE_COMMAND_SIGNATURE =
		"generate.command.signature";

	public static final String GET_LOCATION_MAX_RETRIES =
		"get.location.max.retries";

	public static final String GET_LOCATION_TIMEOUT = "get.location.timeout";

	public static final String IGNORE_ERRORS = "ignore.errors";

	public static final String IGNORE_ERRORS_DELIMITER =
		"ignore.errors.delimiter";

	public static final String IGNORE_ERRORS_FILE_NAME =
		"ignore.errors.file.name";

	public static final String LIFERAY_PORTAL_BRANCH = "liferay.portal.branch";

	public static final String LIFERAY_PORTAL_BUNDLE = "liferay.portal.bundle";

	public static final String LOGGER_RESOURCES_URL = "logger.resources.url";

	public static final String OUTPUT_DIR_NAME = "output.dir.name";

	public static final String PORTAL_URL = "portal.url";

	public static final String POSHI_FILE_READ_THREAD_POOL =
		"poshi.file.read.thread.pool";

	public static final String POSHI_FILE_READ_TIMEOUT =
		"poshi.file.read.timeout";

	public static final String PRINT_JAVA_PROCESS_ON_FAIL =
		"print.java.process.on.fail";

	public static final String PROJECT_DIR = "project.dir";

	public static final String PROXY_SERVER_ENABLED = "proxy.server.enabled";

	public static final String PROXY_SERVER_PORT = "proxy.server.port";

	public static final String REPORT_TYPE = "report.type";

	public static final String SAVE_SCREENSHOT = "save.screenshot";

	public static final String SAVE_WEB_PAGE = "save.web.page";

	public static final String SELENIUM_CHROME_DRIVER_EXECUTABLE =
		"selenium.chrome.driver.executable";

	public static final String SELENIUM_DESIRED_CAPABILITIES_PLATFORM =
		"selenium.desired.capabilities.platform";

	public static final String SELENIUM_EDGE_DRIVER_EXECUTABLE =
		"selenium.edge.driver.executable";

	public static final String SELENIUM_EXECUTABLE_DIR_NAME =
		"selenium.executable.dir.name";

	public static final String SELENIUM_GECKO_DRIVER_EXECUTABLE =
		"selenium.gecko.driver.executable";

	public static final String SELENIUM_IE_DRIVER_EXECUTABLE =
		"selenium.ie.driver.executable";

	public static final String SELENIUM_REMOTE_DRIVER_ENABLED =
		"selenium.remote.driver.enabled";

	public static final String SELENIUM_REMOTE_DRIVER_URL =
		"selenium.remote.driver.url";

	public static final String TCAT_ADMIN_REPOSITORY = "tcat.admin.repository";

	public static final String TCAT_ENABLED = "tcat.enabled";

	public static final String TEST_ASSERT_CONSOLE_ERRORS =
		"test.assert.console.errors";

	public static final String TEST_ASSERT_JAVASCRIPT_ERRORS =
		"test.assert.javascript.errors";

	public static final String TEST_ASSERT_WARNING_EXCEPTIONS =
		"test.assert.warning.exceptions";

	public static final String TEST_BASE_DIR_NAME = "test.base.dir.name";

	public static final String TEST_BATCH_GROUP_IGNORE_REGEX =
		"test.batch.group.ignore.regex";

	public static final String TEST_BATCH_MAX_GROUP_SIZE =
		"test.batch.max.group.size";

	public static final String TEST_BATCH_MAX_SUBGROUP_SIZE =
		"test.batch.max.subgroup.size";

	public static final String TEST_BATCH_PROPERTY_QUERY =
		"test.batch.property.query";

	public static final String TEST_BATCH_RUN_TYPE = "test.batch.run.type";

	public static final String TEST_CASE_AVAILABLE_PROPERTY_NAMES =
		"test.case.available.property.names";

	public static final String TEST_CASE_REQUIRED_PROPERTY_NAMES =
		"test.case.required.property.names";

	public static final String TEST_CSV_REPORT_PROPERTY_NAMES =
		"test.csv.report.property.names";

	public static final String TEST_DEPENDENCIES_DIR_NAME =
		"test.dependencies.dir.name";

	public static final String TEST_DIRS = "test.dirs";

	public static final String TEST_JVM_MAX_RETRIES = "test.jvm.max.retries";

	public static final String TEST_LIFERAY_CONSOLE_LOG_FILE_NAME =
		"test.liferay.console.log.file.name";

	public static final String TEST_LIFERAY_CONSOLE_SHUT_DOWN_FILE_NAME =
		"test.liferay.console.shut.down.file.name";

	public static final String TEST_NAME = "test.name";

	public static final String TEST_POSHI_SCRIPT_VALIDATION =
		"test.poshi.script.validation";

	public static final String TEST_POSHI_WARNINGS_FILE_NAME =
		"test.poshi.warnings.file.name";

	public static final String TEST_RETRY_COMMAND_WAIT_TIME =
		"test.retry.command.wait.time";

	public static final String TEST_RUN_ENVIRONMENT = "test.run.environment";

	public static final String TEST_RUN_LOCALLY = "test.run.locally";

	public static final String TEST_RUN_THREAD_POOL_SIZE =
		"test.run.thread.pool.size";

	public static final String TEST_RUN_TYPE = "test.run.type";

	public static final String TEST_SKIP_TEAR_DOWN = "test.skip.tear.down";

	public static final String TEST_SUPPORT_DIRS = "test.support.dirs";

	public static final String TEST_TESTCASE_MAX_RETRIES =
		"test.testcase.max.retries";

	public static final String TIMEOUT_EXPLICIT_WAIT = "timeout.explicit.wait";

	public static final String TIMEOUT_IMPLICIT_WAIT = "timeout.implicit.wait";

	public static final String TIMEOUT_PAGE_LOAD_WAIT =
		"timeout.page.load.wait";

	public static final String VALIDATION_RESOURCE_FILE_TYPES =
		"validation.resource.file.types";

	public static synchronized void addThreadBasedPoshiProperties() {
		Thread thread = Thread.currentThread();

		if (_threadBasedPoshiProperties.containsKey(thread.getName())) {
			return;
		}

		Properties properties = new Properties(_poshiProperties);

		Class<?> clazz = PoshiProperties.class;

		try {
			for (Field field : clazz.getFields()) {
				if (Modifier.isFinal(field.getModifiers()) &&
					(field.get(field) instanceof String)) {

					String propertyName = (String)field.get(field);

					String propertyValue = System.getProperty(
						"thread." + propertyName);

					if (Validator.isNull(propertyValue)) {
						propertyValue = _classProperties.getProperty(
							"thread." + propertyName);
					}

					if (Validator.isNotNull(propertyValue)) {
						String[] propertyValues = propertyValue.split(",");

						int index = _threadBasedPoshiProperties.size();

						if (index < propertyValues.length) {
							properties.setProperty(
								propertyName, propertyValues[index]);
						}
					}
				}
			}

			_threadBasedPoshiProperties.put(
				thread.getName(), new PoshiProperties(properties));
		}
		catch (IllegalAccessException illegalAccessException) {
			illegalAccessException.printStackTrace();
		}
	}

	public static PoshiProperties getPoshiProperties() {
		Thread thread = Thread.currentThread();

		if (_poshiProperties.testRunType.equals("parallel") &&
			_threadBasedPoshiProperties.containsKey(thread.getName())) {

			return _threadBasedPoshiProperties.get(thread.getName());
		}

		return _poshiProperties;
	}

	public static void validateProperties(Properties properties) {
		for (String propertyName : properties.stringPropertyNames()) {
			String propertyValue = properties.getProperty(propertyName);

			validateProperty(propertyName, propertyValue);
		}
	}

	public static void validateProperty(
		String propertyName, String propertyValue) {

		if (propertyValue.contains(",")) {
			for (String additionalPropertyValue :
					ListUtil.newListFromString(propertyValue)) {

				validateProperty(propertyName, additionalPropertyValue);
			}

			return;
		}

		if (!_validPoshiPropertyValues.containsKey(propertyName)) {
			return;
		}

		String[] validPoshiPropertyValues = _validPoshiPropertyValues.get(
			propertyName);

		for (String validPoshiPropertyValue : validPoshiPropertyValues) {
			if (validPoshiPropertyValue.equals(propertyValue.trim())) {
				return;
			}
		}

		throw new IllegalArgumentException(
			"Illegal property value \"" + propertyValue +
				"\" for property name: " + propertyName +
					"\nValid property values: " + validPoshiPropertyValues);
	}

	public PoshiProperties() {
	}

	public PoshiProperties(Properties properties) {
		super(properties);

		printProperties(false);

		validateProperties(properties);
	}

	public void printProperties(boolean update) {
		List<String> keys = Collections.list(
			(Enumeration<String>)propertyNames());

		keys = ListUtil.sort(keys);

		if (update) {
			System.out.println("-- updated properties --");
		}
		else {
			System.out.println("-- listing properties --");
		}

		for (String key : keys) {
			System.out.println(key + "=" + getProperty(key));
		}

		System.out.println("");
	}

	public String accessibilityStandardsTags = getProperty(
		ACCESSIBILITY_STANDARDS_TAGS);
	public String browserChromeBinArgs = getProperty(BROWSER_CHROME_BIN_ARGS);
	public String browserChromeBinFile = getProperty(BROWSER_CHROME_BIN_FILE);
	public String browserFirefoxBinFile = getProperty(BROWSER_FIREFOX_BIN_FILE);
	public String browserResolution = getProperty(BROWSER_RESOLUTION);
	public String browserType = getProperty(BROWSER_TYPE);
	public String browserVersion = getProperty(BROWSER_VERSION);
	public Boolean debugStacktrace = GetterUtil.getBoolean(
		getProperty(DEBUG_STACKTRACE));
	public Boolean generateCommandSignature = GetterUtil.getBoolean(
		getProperty(GENERATE_COMMAND_SIGNATURE));
	public int getLocationMaxRetries = GetterUtil.getInteger(
		getProperty(GET_LOCATION_MAX_RETRIES));
	public int getLocationTimeout = GetterUtil.getInteger(
		getProperty(GET_LOCATION_TIMEOUT));
	public String ignoreErrors = getProperty(IGNORE_ERRORS);
	public String ignoreErrorsDelimiter = getProperty(IGNORE_ERRORS_DELIMITER);
	public String ignoreErrorsFileName = getProperty(IGNORE_ERRORS_FILE_NAME);
	public String liferayPortalBranch = getProperty(LIFERAY_PORTAL_BRANCH);
	public String liferayPortalBundle = getProperty(LIFERAY_PORTAL_BUNDLE);
	public String loggerResourcesURL = getProperty(LOGGER_RESOURCES_URL);
	public String outputDirName = getProperty(OUTPUT_DIR_NAME);
	public String portalURL = getProperty(PORTAL_URL);
	public int poshiFileReadThreadPool = GetterUtil.getInteger(
		getProperty(POSHI_FILE_READ_THREAD_POOL));
	public int poshiFileReadTimeout = GetterUtil.getInteger(
		getProperty(POSHI_FILE_READ_TIMEOUT));
	public String printJavaProcessOnFail = getProperty(
		PRINT_JAVA_PROCESS_ON_FAIL);
	public String projectDir = getProperty(PROJECT_DIR);
	public Boolean proxyServerEnabled = GetterUtil.getBoolean(
		getProperty(PROXY_SERVER_ENABLED));
	public int proxyServerPort = GetterUtil.getInteger(
		getProperty(PROXY_SERVER_PORT));
	public String reportType = getProperty(REPORT_TYPE);
	public boolean saveScreenshot = GetterUtil.getBoolean(
		getProperty(SAVE_SCREENSHOT));
	public boolean saveWebPage = GetterUtil.getBoolean(
		getProperty(SAVE_WEB_PAGE));
	public String seleniumChromeDriverExecutable = getProperty(
		SELENIUM_CHROME_DRIVER_EXECUTABLE);
	public String seleniumDesiredCapabilitiesPlatform = getProperty(
		SELENIUM_DESIRED_CAPABILITIES_PLATFORM);
	public String seleniumEdgeDriverExecutable = getProperty(
		SELENIUM_EDGE_DRIVER_EXECUTABLE);
	public String seleniumExecutableDirName = getProperty(
		SELENIUM_EXECUTABLE_DIR_NAME);
	public String seleniumGeckoDriverExecutable = getProperty(
		SELENIUM_GECKO_DRIVER_EXECUTABLE);
	public String seleniumIeDriverExecutable = getProperty(
		SELENIUM_IE_DRIVER_EXECUTABLE);
	public boolean seleniumRemoteDriverEnabled = GetterUtil.getBoolean(
		getProperty(SELENIUM_REMOTE_DRIVER_ENABLED));
	public String seleniumRemoteDriverURL = getProperty(
		SELENIUM_REMOTE_DRIVER_URL);
	public String tcatAdminRepository = getProperty(TCAT_ADMIN_REPOSITORY);
	public boolean tcatEnabled = GetterUtil.getBoolean(
		getProperty(TCAT_ENABLED));
	public boolean testAssertConsoleErrors = GetterUtil.getBoolean(
		getProperty(TEST_ASSERT_CONSOLE_ERRORS));
	public boolean testAssertJavaScriptErrors = GetterUtil.getBoolean(
		getProperty(TEST_ASSERT_JAVASCRIPT_ERRORS));
	public boolean testAssertWarningExceptions = GetterUtil.getBoolean(
		getProperty(TEST_ASSERT_WARNING_EXCEPTIONS));
	public String testBaseDirName = getProperty(TEST_BASE_DIR_NAME);
	public String testBatchGroupIgnoreRegex = getProperty(
		TEST_BATCH_GROUP_IGNORE_REGEX);
	public int testBatchMaxGroupSize = GetterUtil.getInteger(
		getProperty(TEST_BATCH_MAX_GROUP_SIZE));
	public int testBatchMaxSubgroupSize = GetterUtil.getInteger(
		getProperty(TEST_BATCH_MAX_SUBGROUP_SIZE));
	public String testBatchPropertyQuery = getProperty(
		TEST_BATCH_PROPERTY_QUERY);
	public String testBatchRunType = getProperty(TEST_BATCH_RUN_TYPE);
	public String[] testCSVReportPropertyNames = StringUtil.split(
		getProperty(TEST_CSV_REPORT_PROPERTY_NAMES));
	public String testCaseAvailablePropertyNames = getProperty(
		TEST_CASE_AVAILABLE_PROPERTY_NAMES);
	public String testCaseRequiredPropertyNames = getProperty(
		TEST_CASE_REQUIRED_PROPERTY_NAMES);
	public String testDependenciesDirName = getProperty(
		TEST_DEPENDENCIES_DIR_NAME);
	public String[] testDirs = StringUtil.split(getProperty(TEST_DIRS));
	public int testJVMMaxRetries = GetterUtil.getInteger(
		getProperty(TEST_JVM_MAX_RETRIES));
	public String testLiferayConsoleLogFileName = getProperty(
		TEST_LIFERAY_CONSOLE_LOG_FILE_NAME);
	public String testLiferayConsoleShutDownFileName = getProperty(
		TEST_LIFERAY_CONSOLE_LOG_FILE_NAME);
	public String testName = getProperty(TEST_NAME);
	public boolean testPoshiScriptValidation = GetterUtil.getBoolean(
		getProperty(TEST_POSHI_SCRIPT_VALIDATION));
	public String testPoshiWarningsFileName = getProperty(
		TEST_POSHI_WARNINGS_FILE_NAME);
	public int testRetryCommandWaitTime = GetterUtil.getInteger(
		getProperty(TEST_RETRY_COMMAND_WAIT_TIME));
	public String testRunEnvironment = getProperty(TEST_RUN_ENVIRONMENT);
	public boolean testRunLocally = GetterUtil.getBoolean(
		getProperty(TEST_RUN_LOCALLY));
	public int testRunThreadPoolSize = GetterUtil.getInteger(
		getProperty(TEST_RUN_THREAD_POOL_SIZE));
	public String testRunType = getProperty(TEST_RUN_TYPE);
	public boolean testSkipTearDown = GetterUtil.getBoolean(
		getProperty(TEST_SKIP_TEAR_DOWN));
	public String[] testSupportDirs = StringUtil.split(
		getProperty(TEST_SUPPORT_DIRS));
	public int testTestcaseMaxRetries = GetterUtil.getInteger(
		getProperty(TEST_TESTCASE_MAX_RETRIES));
	public int timeoutExplicitWait = GetterUtil.getInteger(
		getProperty(TIMEOUT_EXPLICIT_WAIT));
	public int timeoutImplicitWait = GetterUtil.getInteger(
		getProperty(TIMEOUT_IMPLICIT_WAIT));
	public int timeoutPageLoadWait = GetterUtil.getInteger(
		getProperty(TIMEOUT_PAGE_LOAD_WAIT));
	public String[] validationResourceFileTypes = StringUtil.split(
		getProperty(VALIDATION_RESOURCE_FILE_TYPES));

	private static final Properties _classProperties = new Properties();
	private static final PoshiProperties _poshiProperties;
	private static final Map<String, PoshiProperties>
		_threadBasedPoshiProperties = Collections.synchronizedMap(
			new HashMap<>());
	private static final Map<String, String[]> _validPoshiPropertyValues =
		new HashMap<String, String[]>() {
			{
				put(
					BROWSER_TYPE,
					new String[] {
						"android", "androidchrome", "chrome", "edge", "firefox",
						"internetexplorer", "iossafari", "safari"
					});
				put(DEBUG_STACKTRACE, new String[] {"false", "true"});
				put(GENERATE_COMMAND_SIGNATURE, new String[] {"false", "true"});
				put(PROXY_SERVER_ENABLED, new String[] {"false", "true"});
				put(REPORT_TYPE, new String[] {"test-properties", "usage"});
				put(SAVE_SCREENSHOT, new String[] {"false", "true"});
				put(SAVE_WEB_PAGE, new String[] {"false", "true"});
				put(TCAT_ENABLED, new String[] {"false", "true"});
				put(TEST_ASSERT_CONSOLE_ERRORS, new String[] {"false", "true"});
				put(
					TEST_ASSERT_JAVASCRIPT_ERRORS,
					new String[] {"false", "true"});
				put(
					TEST_ASSERT_WARNING_EXCEPTIONS,
					new String[] {"false", "true"});
				put(TEST_BATCH_RUN_TYPE, new String[] {"sequential", "single"});
				put(
					TEST_POSHI_SCRIPT_VALIDATION,
					new String[] {"false", "true"});
				put(TEST_RUN_LOCALLY, new String[] {"false", "true"});
				put(
					TEST_RUN_TYPE,
					new String[] {"parallel", "sequential", "single"});
				put(TEST_SKIP_TEAR_DOWN, new String[] {"false", "true"});
				put(
					VALIDATION_RESOURCE_FILE_TYPES,
					new String[] {"function", "macro", "path", "testcase"});
			}
		};

	static {
		Class<?> clazz = PoshiProperties.class;

		ClassLoader classLoader = clazz.getClassLoader();

		for (String propertiesFileName :
				Arrays.asList("poshi.properties", "poshi-ext.properties")) {

			InputStream inputStream = classLoader.getResourceAsStream(
				propertiesFileName);

			if (inputStream != null) {
				try {
					_classProperties.load(inputStream);
				}
				catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		Properties properties = new Properties();

		try {
			for (Field field : clazz.getFields()) {
				if (Modifier.isFinal(field.getModifiers()) &&
					(field.get(field) instanceof String)) {

					String propertyName = (String)field.get(field);

					String propertyValue = System.getProperty(propertyName);

					if (Validator.isNull(propertyValue)) {
						propertyValue = _classProperties.getProperty(
							propertyName);
					}

					if (Validator.isNotNull(propertyValue)) {
						properties.setProperty(propertyName, propertyValue);
					}
				}
			}
		}
		catch (IllegalAccessException illegalAccessException) {
			illegalAccessException.printStackTrace();
		}

		_poshiProperties = new PoshiProperties(properties);
	}

}
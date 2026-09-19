/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner;

import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.PoshiGetterUtil;
import com.liferay.poshi.core.PoshiProperties;
import com.liferay.poshi.core.PoshiStackTrace;
import com.liferay.poshi.core.PoshiValidation;
import com.liferay.poshi.core.PoshiVariablesContext;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.poshi.core.util.GetterUtil;
import com.liferay.poshi.core.util.Validator;
import com.liferay.poshi.runner.exception.PoshiRunnerWarningException;
import com.liferay.poshi.runner.logger.PoshiLogger;
import com.liferay.poshi.runner.logger.SummaryLogger;
import com.liferay.poshi.runner.selenium.LiferaySeleniumUtil;
import com.liferay.poshi.runner.selenium.WebDriverUtil;
import com.liferay.poshi.runner.util.ProxyUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Element;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Hashimoto
 * @author Karen Dang
 * @author Leslie Wong
 */
@RunWith(Parameterized.class)
public class PoshiRunner {

	@AfterClass
	public static void evaluateResults() throws IOException {
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, List<String>> testResult :
				_testResults.entrySet()) {

			List<String> testResultMessages = testResult.getValue();

			if (testResultMessages.size() == 1) {
				continue;
			}

			int passes = Collections.frequency(testResultMessages, "PASS");

			int failures = testResultMessages.size() - passes;

			if ((passes > 0) && (failures > 0)) {
				sb.append("\n");
				sb.append(testResult.getKey());
			}
		}

		if (sb.length() != 0) {
			FileUtil.write(
				FileUtil.getCanonicalPath(".") + "/test-results/flaky-tests",
				sb.toString());
		}
	}

	@Parameterized.Parameters(name = "{0}")
	public static List<String> getList() throws Exception {
		List<String> namespacedClassCommandNames = new ArrayList<>();

		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		List<String> testNames = Arrays.asList(
			poshiProperties.testName.split("\\s*,\\s*"));

		PoshiContext.readFiles(false);

		PoshiValidation.validate();

		for (String testName : testNames) {
			PoshiValidation.validate(testName);

			String namespace =
				PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
					testName);

			if (Validator.isNull(namespace)) {
				namespace = PoshiContext.getDefaultNamespace();
			}

			if (testName.contains("#")) {
				String classCommandName =
					PoshiGetterUtil.
						getClassCommandNameFromNamespacedClassCommandName(
							testName);

				namespacedClassCommandNames.add(
					namespace + "." + classCommandName);
			}
			else {
				String className =
					PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
						testName);

				Element rootElement = PoshiContext.getTestCaseRootElement(
					className, namespace);

				List<Element> commandElements = rootElement.elements("command");

				for (Element commandElement : commandElements) {
					namespacedClassCommandNames.add(
						namespace + "." + className + "#" +
							commandElement.attributeValue("name"));
				}
			}
		}

		return namespacedClassCommandNames;
	}

	public PoshiRunner(String namespacedClassCommandName) throws Exception {
		_testNamespacedClassCommandName = namespacedClassCommandName;

		_testNamespacedClassName =
			PoshiGetterUtil.
				getNamespacedClassNameFromNamespacedClassCommandName(
					_testNamespacedClassCommandName);
	}

	public String getTestNamespacedClassCommandName() {
		return _testNamespacedClassCommandName;
	}

	@Before
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("###");
		System.out.println("### " + _testNamespacedClassCommandName);
		System.out.println("###");
		System.out.println();

		_poshiLogger = new PoshiLogger(_testNamespacedClassCommandName);
		_summaryLogger = SummaryLogger.getSummaryLogger(
			_testNamespacedClassCommandName);

		_poshiRunnerExecutor = new PoshiRunnerExecutor(
			_poshiLogger, _summaryLogger);

		_poshiStackTrace = PoshiStackTrace.getPoshiStackTrace(
			_testNamespacedClassCommandName);

		FileUtil.delete(new File(_poshiProperties.outputDirName));

		try {
			_summaryLogger.startRunning();

			Properties properties =
				PoshiContext.getNamespacedClassCommandNameProperties(
					_testNamespacedClassCommandName);

			if (!GetterUtil.getBoolean(
					properties.getProperty("disable-webdriver"))) {

				WebDriverUtil.startWebDriver(_testNamespacedClassCommandName);
			}

			_runSetUp();
		}
		catch (WebDriverException webDriverException) {
			webDriverException.printStackTrace();

			throw webDriverException;
		}
		catch (Exception exception1) {
			LiferaySeleniumUtil.printJavaProcessStacktrace();

			StringBuilder sb = new StringBuilder();

			sb.append("TEST_SETUP_ERROR: ");
			sb.append(exception1.getMessage());

			Exception exception2 = new Exception(sb.toString(), exception1);

			_throwException(exception2);
		}
	}

	@After
	public void tearDown() throws Throwable {
		_summaryLogger.createSummaryReport();

		try {
			if (!_poshiProperties.testSkipTearDown) {
				_runTearDown();
			}
		}
		catch (Exception exception) {
			PoshiRunnerException poshiRunnerException =
				new PoshiRunnerException(exception, _poshiStackTrace);

			_poshiStackTrace.emptyStackTrace();

			poshiRunnerException.printStackTrace();

			PoshiRunnerWarningException.addException(
				new PoshiRunnerWarningException(
					"TEAR_DOWN_FAILURE: " + exception.getMessage(), exception));
		}
		finally {
			if (_poshiProperties.proxyServerEnabled) {
				ProxyUtil.stopBrowserMobProxy();
			}

			LiferaySeleniumUtil.writePoshiWarnings();

			_poshiLogger.createPoshiReport();

			WebDriverUtil.stopWebDriver(_testNamespacedClassCommandName);

			PoshiRunnerWarningException.clear();
			PoshiStackTrace.clear(_testNamespacedClassCommandName);
			PoshiVariablesContext.clear(_testNamespacedClassCommandName);
			SummaryLogger.clear(_testNamespacedClassCommandName);
		}
	}

	@Test
	public void test() throws Exception {
		try {
			_runCommand();

			LiferaySeleniumUtil.assertNoPoshiWarnings();
		}
		catch (Exception exception) {
			LiferaySeleniumUtil.printJavaProcessStacktrace();

			_throwException(exception);
		}
	}

	@Rule
	public RetryTestRule retryTestRule = new RetryTestRule();

	private void _runCommand() throws Exception {
		_poshiLogger.logNamespacedClassCommandName(
			_testNamespacedClassCommandName);

		_runNamespacedClassCommandName(_testNamespacedClassCommandName);
	}

	private void _runNamespacedClassCommandName(
			String namespacedClassCommandName)
		throws Exception {

		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName);

		String classCommandName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		Element commandElement = PoshiContext.getTestCaseCommandElement(
			classCommandName, namespace);

		if (commandElement != null) {
			_poshiStackTrace.startStackTrace(
				namespacedClassCommandName, "test-case");

			_poshiLogger.updateStatus(commandElement, "pending");

			_poshiRunnerExecutor.runTestCaseCommandElement(
				commandElement, namespacedClassCommandName);

			_poshiLogger.updateStatus(commandElement, "pass");

			_poshiStackTrace.emptyStackTrace();
		}
	}

	private void _runSetUp() throws Exception {
		_poshiLogger.logNamespacedClassCommandName(
			_testNamespacedClassName + "#set-up");

		_summaryLogger.startMajorSteps();

		_runNamespacedClassCommandName(_testNamespacedClassName + "#set-up");
	}

	private void _runTearDown() throws Exception {
		_poshiLogger.logNamespacedClassCommandName(
			_testNamespacedClassName + "#tear-down");

		_summaryLogger.startMajorSteps();

		_runNamespacedClassCommandName(_testNamespacedClassName + "#tear-down");
	}

	private void _throwException(Exception exception)
		throws PoshiRunnerException {

		PoshiRunnerException poshiRunnerException = new PoshiRunnerException(
			exception, _poshiStackTrace);

		_poshiStackTrace.emptyStackTrace();

		poshiRunnerException.printStackTrace();

		throw poshiRunnerException;
	}

	private static int _jvmRetryCount;
	private static final PoshiProperties _poshiProperties =
		PoshiProperties.getPoshiProperties();
	private static final Map<String, List<String>> _testResults =
		new HashMap<>();

	private PoshiLogger _poshiLogger;
	private PoshiRunnerExecutor _poshiRunnerExecutor;
	private PoshiStackTrace _poshiStackTrace;
	private SummaryLogger _summaryLogger;
	private final String _testNamespacedClassCommandName;
	private final String _testNamespacedClassName;

	private class RetryTestRule implements TestRule {

		public Statement apply(Statement statement, Description description) {
			return new RetryStatement(statement);
		}

		public class RetryStatement extends Statement {

			public RetryStatement(Statement statement) {
				_statement = statement;
			}

			@Override
			public void evaluate() throws Throwable {
				while (true) {
					try {
						_statement.evaluate();

						_testResultMessages.add("PASS");

						_testResults.put(
							_testNamespacedClassCommandName,
							_testResultMessages);

						return;
					}
					catch (Throwable throwable) {
						_testResultMessages.add(throwable.getMessage());

						if (!_isRetryable(throwable)) {
							_testResults.put(
								_testNamespacedClassCommandName,
								_testResultMessages);

							throw throwable;
						}

						_jvmRetryCount++;
						_testcaseRetryCount++;

						System.out.println(
							"Retrying test attempt " + _testcaseRetryCount +
								" of " +
									_poshiProperties.testTestcaseMaxRetries);
					}
				}
			}

			private String _getShortMessage(Throwable throwable) {
				String message = throwable.getMessage();

				if (throwable instanceof WebDriverException) {
					int index = message.indexOf("Build info:");

					message = message.substring(0, index);

					message = message.trim();
				}

				return message;
			}

			private boolean _isKnownFlakyIssue(Throwable throwable1) {
				List<Throwable> throwables = null;

				if (throwable1 instanceof MultipleFailureException) {
					MultipleFailureException multipleFailureException =
						(MultipleFailureException)throwable1;

					throwables = multipleFailureException.getFailures();
				}
				else {
					throwables = Arrays.asList(throwable1);
				}

				for (Throwable validRetryThrowable : _validRetryThrowables) {
					Class<?> validRetryThrowableClass =
						validRetryThrowable.getClass();
					String validRetryThrowableShortMessage = _getShortMessage(
						validRetryThrowable);

					for (Throwable throwable2 : throwables) {
						if (validRetryThrowableClass.equals(
								throwable2.getClass()) &&
							((validRetryThrowableShortMessage == null) ||
							 validRetryThrowableShortMessage.isEmpty() ||
							 validRetryThrowableShortMessage.equals(
								 _getShortMessage(throwable2)))) {

							return true;
						}
					}
				}

				return false;
			}

			private boolean _isRetryable(Throwable throwable) {
				if (_jvmRetryCount >= _poshiProperties.testJVMMaxRetries) {
					System.out.println(
						"Test retry attempts exceeded in Poshi Runner JVM");

					return false;
				}

				if (_isKnownFlakyIssue(throwable) || _isTestcaseRetryable()) {
					return true;
				}

				return false;
			}

			private boolean _isTestcaseRetryable() {
				if ((_testcaseRetryCount >=
						_poshiProperties.testTestcaseMaxRetries) ||
					_poshiProperties.testSkipTearDown ||
					(_poshiProperties.testTestcaseMaxRetries == 0)) {

					return false;
				}

				return true;
			}

			private final Statement _statement;
			private final List<String> _testResultMessages = new ArrayList<>();
			private int _testcaseRetryCount;
			private final Throwable[] _validRetryThrowables = {
				new TimeoutException(), new UnreachableBrowserException(null),
				new WebDriverException(
					"Timed out waiting 45 seconds for Firefox to start."),
				new WebDriverException(
					"unknown error: unable to discover open pages")
			};

		}

	}

}
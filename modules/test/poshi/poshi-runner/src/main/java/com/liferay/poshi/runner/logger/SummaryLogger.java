/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.logger;

import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.PoshiGetterUtil;
import com.liferay.poshi.core.PoshiProperties;
import com.liferay.poshi.core.PoshiStackTrace;
import com.liferay.poshi.core.PoshiVariablesContext;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;
import com.liferay.poshi.runner.exception.PoshiRunnerLoggerException;
import com.liferay.poshi.runner.util.HtmlUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public final class SummaryLogger {

	public static void clear(String testNamespacedClassCommandName) {
		SummaryLogger summaryLogger = _summaryLoggers.remove(
			testNamespacedClassCommandName);

		if (summaryLogger != null) {
			summaryLogger.stopRunning();
		}
	}

	public static synchronized SummaryLogger getSummaryLogger(
		String testNamespacedClassCommandName) {

		return _summaryLoggers.computeIfAbsent(
			testNamespacedClassCommandName, SummaryLogger::new);
	}

	public void createSummaryReport() throws Exception {
		String summaryHTMLContent = _readResource(
			"META-INF/resources/logger/html/summary.html");

		_summaryContentWrapperLoggerElement.addChildLoggerElement(
			_getSummaryContentLoggerElement());

		summaryHTMLContent = StringUtil.replace(
			summaryHTMLContent, "<div id=\"summaryContentContainer\" />",
			_summaryContentContainerLoggerElement.toString());

		summaryHTMLContent = StringUtil.replace(
			summaryHTMLContent, "<p id=\"summaryTestDescription\" />",
			String.valueOf(_getSummaryTestDescriptionLoggerElement()));

		summaryHTMLContent = StringUtil.replace(
			summaryHTMLContent, "<h3 id=\"summaryTestName\" />",
			String.valueOf(_getSummaryTestNameLoggerElement()));

		summaryHTMLContent = StringUtil.replace(
			summaryHTMLContent, "<ul id=\"summaryTitleContainer\" />",
			_summaryTitleContainerLoggerElement.toString());

		summaryHTMLContent = StringUtil.replace(
			summaryHTMLContent, "<script defer src=\"../js/update_images.js\"",
			"<script defer src=\"" + _poshiProperties.loggerResourcesURL +
				"/js/update_images.js\"");

		StringBuilder sb = new StringBuilder();

		sb.append(FileUtil.getCanonicalPath("."));
		sb.append("/test-results/");
		sb.append(
			StringUtil.replace(getTestNamespacedClassCommandName(), "#", "_"));
		sb.append("/summary.html");

		FileUtil.write(sb.toString(), summaryHTMLContent);
	}

	public void failSummary(
		Element element, String message, int screenshotNumber) {

		if (_isCurrentMajorStep(element)) {
			_causeBodyLoggerElement.setText(HtmlUtil.escape(message));

			_failStepLoggerElement(_majorStepLoggerElement);

			_summaryLogLoggerElement.addChildLoggerElement(
				_getScreenshotsLoggerElement(screenshotNumber));

			_stopMajorStep();
		}

		if (_isCurrentMinorStep(element)) {
			_causeBodyLoggerElement.setText(HtmlUtil.escape(message));

			_failStepLoggerElement(_minorStepLoggerElement);

			_stopMinorStep();
		}
	}

	public LoggerElement getSummarySnapshotLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("summary-log");
		loggerElement.setName("div");

		LoggerElement causeLoggerElement =
			_summaryLogLoggerElement.loggerElement("div", "cause");

		if (causeLoggerElement != null) {
			loggerElement.addChildLoggerElement(causeLoggerElement.copy());
		}

		LoggerElement stepsLoggerElement =
			_summaryLogLoggerElement.loggerElement("div", "steps");

		if (stepsLoggerElement != null) {
			stepsLoggerElement = stepsLoggerElement.copy();

			_removeUnneededStepsFromLoggerElement(stepsLoggerElement);

			loggerElement.addChildLoggerElement(stepsLoggerElement);
		}

		return loggerElement;
	}

	public String getTestNamespacedClassCommandName() {
		return _testNamespacedClassCommandName;
	}

	public void passSummary(Element element) {
		if (_isCurrentMajorStep(element)) {
			if (_containsMinorStepWarning) {
				_warnStepLoggerElement(_majorStepLoggerElement);

				_containsMinorStepWarning = false;
			}
			else {
				_passStepLoggerElement(_majorStepLoggerElement);
			}

			_stopMajorStep();
		}

		if (_isCurrentMinorStep(element)) {
			_passStepLoggerElement(_minorStepLoggerElement);

			_stopMinorStep();
		}
	}

	public void startMajorSteps() throws PoshiRunnerLoggerException {
		try {
			_causeBodyLoggerElement = _getCauseBodyLoggerElement();
			_majorStepsLoggerElement = _getMajorStepsLoggerElement();
			_summaryLogLoggerElement = _getSummaryLogLoggerElement();
		}
		catch (Throwable throwable) {
			throw new PoshiRunnerLoggerException(
				throwable.getMessage(), throwable);
		}
	}

	public void startRunning() {
		_containsMinorStepWarning = false;

		_summaryContentContainerLoggerElement = new LoggerElement(
			"summaryContentContainer");

		_summaryContentWrapperLoggerElement = new LoggerElement(
			"summaryContentWrapper");

		_summaryContentContainerLoggerElement.addChildLoggerElement(
			_summaryContentWrapperLoggerElement);

		_summaryTitleContainerLoggerElement = new LoggerElement(
			"summaryTitleContainer");

		_summaryTitleContainerLoggerElement.addChildLoggerElement(
			_getSummaryTitleLoggerElement("SUMMARY"));

		_summaryTitleContainerLoggerElement.setName("ul");

		_warningCount = 0;
	}

	public void startSummary(Element element) throws Exception {
		try {
			if (_isMajorStep(element)) {
				_startMajorStep(element);

				_majorStepLoggerElement = _getMajorStepLoggerElement(element);

				_majorStepsLoggerElement.addChildLoggerElement(
					_majorStepLoggerElement);

				_minorStepsLoggerElement = _getMinorStepsLoggerElement();

				_majorStepLoggerElement.addChildLoggerElement(
					_minorStepsLoggerElement);
			}

			if (_isMinorStep(element)) {
				_startMinorStep(element);

				_minorStepLoggerElement = _getMinorStepLoggerElement(element);

				_minorStepsLoggerElement.addChildLoggerElement(
					_minorStepLoggerElement);
			}
		}
		catch (Throwable throwable) {
			throw new PoshiRunnerLoggerException(
				throwable.getMessage(), throwable);
		}
	}

	public void stopRunning() {
		_stopMajorStep();
	}

	public void warnSummary(Element element, String message) {
		if (_isCurrentMajorStep(element)) {
			_causeBodyLoggerElement.setText(message);

			_warnStepLoggerElement(_majorStepLoggerElement);

			_stopMajorStep();
		}

		if (_isCurrentMinorStep(element)) {
			_causeBodyLoggerElement.setText(message);

			_warnStepLoggerElement(_minorStepLoggerElement);

			_containsMinorStepWarning = true;

			_warningCount++;

			_summaryContentContainerLoggerElement.addChildLoggerElement(
				_getSummaryContentLoggerElement());

			_summaryTitleContainerLoggerElement.addChildLoggerElement(
				_getSummaryTitleLoggerElement("WARNING #" + _warningCount));

			_stopMinorStep();
		}
	}

	private SummaryLogger(String testNamespacedClassCommandName) {
		_testNamespacedClassCommandName = testNamespacedClassCommandName;

		_poshiProperties = PoshiProperties.getPoshiProperties();
		_poshiStackTrace = PoshiStackTrace.getPoshiStackTrace(
			testNamespacedClassCommandName);
		_poshiVariablesContext = PoshiVariablesContext.getPoshiVariablesContext(
			testNamespacedClassCommandName);
	}

	private void _failStepLoggerElement(LoggerElement stepLoggerElement) {
		stepLoggerElement.addClassName("summary-failure");

		LoggerElement lineContainerLoggerElement =
			stepLoggerElement.loggerElement("div");

		if (lineContainerLoggerElement == null) {
			return;
		}

		lineContainerLoggerElement.addChildLoggerElement(
			_getStatusLoggerElement("FAILED"));
		lineContainerLoggerElement.setName("strong");
	}

	private LoggerElement _getButtonLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("btn header");
		loggerElement.setName("button");
		loggerElement.setText("+");

		return loggerElement;
	}

	private LoggerElement _getCauseBodyLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("cause-body");
		loggerElement.setName("pre");

		return loggerElement;
	}

	private LoggerElement _getCauseHeaderLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("cause-header");
		loggerElement.setName("h4");
		loggerElement.setText("Cause:");

		return loggerElement;
	}

	private LoggerElement _getCauseLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("cause");

		loggerElement.addChildLoggerElement(_getCauseHeaderLoggerElement());
		loggerElement.addChildLoggerElement(_causeBodyLoggerElement);

		return loggerElement;
	}

	private LoggerElement _getMajorStepLoggerElement(Element element)
		throws Exception {

		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("major-step");
		loggerElement.setName("li");

		loggerElement.addChildLoggerElement(_getButtonLoggerElement());

		loggerElement.addChildLoggerElement(
			_getStepDescriptionLoggerElement(element));

		return loggerElement;
	}

	private LoggerElement _getMajorStepsLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("major-steps");
		loggerElement.setName("ul");

		return loggerElement;
	}

	private LoggerElement _getMinorStepLoggerElement(Element element)
		throws Exception {

		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("minor-step");
		loggerElement.setName("li");

		loggerElement.addChildLoggerElement(
			_getStepDescriptionLoggerElement(element));

		return loggerElement;
	}

	private LoggerElement _getMinorStepsLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("content minor-steps");
		loggerElement.setName("ul");

		return loggerElement;
	}

	private LoggerElement _getScreenshotsAfterHeaderLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setText("After Failure:");
		loggerElement.setName("h5");

		return loggerElement;
	}

	private LoggerElement _getScreenshotsAfterLinkLoggerElement(
		int screenshotNumber) {

		LoggerElement loggerElement = new LoggerElement();

		StringBuilder sb = new StringBuilder();

		sb.append("screenshots/after");
		sb.append(screenshotNumber);
		sb.append(".jpg");

		loggerElement.setAttribute("href", sb.toString());

		loggerElement.setAttribute("title", "After Failure");
		loggerElement.setName("a");

		loggerElement.addChildLoggerElement(
			_getScreenshotsAfterThumbnailLoggerElement(screenshotNumber));

		return loggerElement;
	}

	private LoggerElement _getScreenshotsAfterThumbnailLoggerElement(
		int screenshotNumber) {

		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setAttribute("alt", "After Failure");

		StringBuilder sb = new StringBuilder();

		sb.append("screenshots/after");
		sb.append(screenshotNumber);
		sb.append(".jpg");

		loggerElement.setAttribute("src", sb.toString());

		loggerElement.setClassName("screenshots-thumbnail");
		loggerElement.setName("img");

		return loggerElement;
	}

	private LoggerElement _getScreenshotsBeforeHeaderLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setText("Before Failure:");
		loggerElement.setName("h5");

		return loggerElement;
	}

	private LoggerElement _getScreenshotsBeforeLinkLoggerElement(
		int screenshotNumber) {

		LoggerElement loggerElement = new LoggerElement();

		StringBuilder sb = new StringBuilder();

		sb.append("screenshots/before");
		sb.append(screenshotNumber);
		sb.append(".jpg");

		loggerElement.setAttribute("href", sb.toString());

		loggerElement.setAttribute("title", "Before Failure");
		loggerElement.setName("a");

		loggerElement.addChildLoggerElement(
			_getScreenshotsBeforeThumbnailLoggerElement(screenshotNumber));

		return loggerElement;
	}

	private LoggerElement _getScreenshotsBeforeThumbnailLoggerElement(
		int screenshotNumber) {

		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setAttribute("alt", "Before Failure");

		StringBuilder sb = new StringBuilder();

		sb.append("screenshots/before");
		sb.append(screenshotNumber);
		sb.append(".jpg");

		loggerElement.setAttribute("src", sb.toString());

		loggerElement.setClassName("screenshots-thumbnail");
		loggerElement.setName("img");

		return loggerElement;
	}

	private LoggerElement _getScreenshotsHeaderLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("screenshots-header");
		loggerElement.setName("h4");
		loggerElement.setText("Screenshots:");

		return loggerElement;
	}

	private LoggerElement _getScreenshotsLoggerElement(int screenshotNumber) {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("screenshots");

		loggerElement.addChildLoggerElement(
			_getScreenshotsHeaderLoggerElement());

		loggerElement.addChildLoggerElement(
			_getScreenshotsBeforeHeaderLoggerElement());
		loggerElement.addChildLoggerElement(
			_getScreenshotsBeforeLinkLoggerElement(screenshotNumber));

		loggerElement.addChildLoggerElement(
			_getScreenshotsAfterHeaderLoggerElement());
		loggerElement.addChildLoggerElement(
			_getScreenshotsAfterLinkLoggerElement(screenshotNumber));

		return loggerElement;
	}

	private LoggerElement _getStatusLoggerElement(String status) {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("status");
		loggerElement.setID(null);
		loggerElement.setName("span");
		loggerElement.setText(" --> " + status);

		return loggerElement;
	}

	private LoggerElement _getStepDescriptionLoggerElement(Element element)
		throws Exception {

		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("step-description");
		loggerElement.setText(_getSummary(element));

		return loggerElement;
	}

	private LoggerElement _getStepsHeaderLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("steps-header");
		loggerElement.setName("h4");
		loggerElement.setText("Steps:");

		return loggerElement;
	}

	private LoggerElement _getStepsLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("steps");

		loggerElement.addChildLoggerElement(_getStepsHeaderLoggerElement());
		loggerElement.addChildLoggerElement(_majorStepsLoggerElement);

		return loggerElement;
	}

	private String _getSummary(Element element) throws Exception {
		String summary = null;

		if (element.attributeValue("summary") != null) {
			summary = element.attributeValue("summary");
		}

		if (summary == null) {
			String namespacedClassCommandName = null;
			String classType = null;

			if (element.attributeValue("function") != null) {
				namespacedClassCommandName = element.attributeValue("function");
				classType = "function";
			}
			else if (element.attributeValue("function-summary") != null) {
				namespacedClassCommandName = element.attributeValue(
					"function-summary");
				classType = "function-summary";
			}
			else if (element.attributeValue("macro") != null) {
				namespacedClassCommandName = element.attributeValue("macro");
				classType = "macro";
			}
			else if (element.attributeValue("macro-summary") != null) {
				namespacedClassCommandName = element.attributeValue(
					"macro-summary");
				classType = "macro-summary";
			}
			else if (element.attributeValue("method") != null) {
				return element.attributeValue("method");
			}
			else {
				return null;
			}

			String classCommandName =
				PoshiGetterUtil.
					getClassCommandNameFromNamespacedClassCommandName(
						namespacedClassCommandName);

			String namespace = _poshiStackTrace.getCurrentNamespace();

			if (classType.startsWith("function")) {
				summary = PoshiContext.getFunctionCommandSummary(
					classCommandName, namespace);
			}
			else if (classType.startsWith("macro")) {
				summary = PoshiContext.getMacroCommandSummary(
					classCommandName, namespace);
			}
		}

		if (summary != null) {
			summary = HtmlUtil.escape(
				_poshiVariablesContext.getReplacedCommandVarsString(summary));

			return _replaceExecuteVars(summary, element);
		}

		return null;
	}

	private LoggerElement _getSummaryContentLoggerElement() {
		LoggerElement loggerElement = _summaryLogLoggerElement.copy();

		LoggerElement stepsLoggerElement = loggerElement.loggerElement("div");

		LoggerElement majorStepsLoggerElement =
			stepsLoggerElement.loggerElement("ul");

		List<LoggerElement> majorStepLoggerElements =
			majorStepsLoggerElement.loggerElements("li");

		for (int i = 0; i < majorStepLoggerElements.size(); i++) {
			LoggerElement majorStepLoggerElement = majorStepLoggerElements.get(
				i);

			boolean lastMajorStep = false;

			if (i >= (majorStepLoggerElements.size() - 1)) {
				lastMajorStep = true;
			}

			if (_containsMinorStepWarning && lastMajorStep) {
				_warnStepLoggerElement(majorStepLoggerElement);
			}

			String togglerClassNameSuffix = "collapsed";

			String majorStepClassName = majorStepLoggerElement.getClassName();

			if (lastMajorStep &&
				(majorStepClassName.contains("summary-failure") ||
				 majorStepClassName.contains("summary-warning"))) {

				togglerClassNameSuffix = "expanded";
			}

			LoggerElement buttonLoggerElement =
				majorStepLoggerElement.loggerElement("button");

			buttonLoggerElement.addClassName(
				"toggler-header-" + togglerClassNameSuffix);

			LoggerElement minorStepsLoggerElement =
				majorStepLoggerElement.loggerElement("ul");

			minorStepsLoggerElement.addClassName(
				"toggler-content-" + togglerClassNameSuffix);
		}

		return loggerElement;
	}

	private LoggerElement _getSummaryLogLoggerElement() {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("summary-log");
		loggerElement.setName("div");

		loggerElement.addChildLoggerElement(_getStepsLoggerElement());
		loggerElement.addChildLoggerElement(_getCauseLoggerElement());

		return loggerElement;
	}

	private LoggerElement _getSummaryTestDescriptionLoggerElement() {
		LoggerElement loggerElement = new LoggerElement(
			"summaryTestDescription");

		String testCaseDescription = PoshiContext.getTestCaseDescription(
			getTestNamespacedClassCommandName());

		if (Validator.isNull(testCaseDescription)) {
			testCaseDescription = "";
		}

		loggerElement.setName("p");
		loggerElement.setText(testCaseDescription);

		return loggerElement;
	}

	private LoggerElement _getSummaryTestNameLoggerElement() {
		LoggerElement loggerElement = new LoggerElement("summaryTestName");

		loggerElement.setName("h3");
		loggerElement.setText(getTestNamespacedClassCommandName());

		return loggerElement;
	}

	private LoggerElement _getSummaryTitleLinkLoggerElement(String title) {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setAttribute("href", "#");
		loggerElement.setName("a");
		loggerElement.setText(title);

		return loggerElement;
	}

	private LoggerElement _getSummaryTitleLoggerElement(String title) {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setName("li");

		loggerElement.addChildLoggerElement(
			_getSummaryTitleLinkLoggerElement(title));

		return loggerElement;
	}

	private boolean _isCurrentMajorStep(Element element) {
		if (element == _majorStepElement) {
			return true;
		}

		return false;
	}

	private boolean _isCurrentMinorStep(Element element) {
		if (element == _minorStepElement) {
			return true;
		}

		return false;
	}

	private boolean _isMajorStep(Element element) throws Exception {
		String summary = _getSummary(element);

		if (summary == null) {
			return false;
		}

		if (!Objects.equals(element.getName(), "condition") &&
			!Objects.equals(element.getName(), "execute") &&
			!Objects.equals(element.getName(), "task") &&
			!Objects.equals(element.getName(), "var")) {

			return false;
		}

		if (Validator.isNull(element.attributeValue("function")) &&
			Validator.isNull(element.attributeValue("function-summary")) &&
			Validator.isNull(element.attributeValue("macro")) &&
			Validator.isNull(element.attributeValue("macro-summary")) &&
			Validator.isNull(element.attributeValue("method")) &&
			Validator.isNull(element.attributeValue("summary"))) {

			return false;
		}

		if (_majorStepElement != null) {
			return false;
		}

		return true;
	}

	private boolean _isMinorStep(Element element) throws Exception {
		String summary = _getSummary(element);

		if ((summary == null) ||
			!Objects.equals(element.getName(), "execute") ||
			Validator.isNull(element.attributeValue("function")) ||
			(_minorStepElement != null) ||
			Validator.isNotNull(_majorStepElement.attributeValue("function"))) {

			return false;
		}

		return true;
	}

	private void _passStepLoggerElement(LoggerElement stepLoggerElement) {
		LoggerElement lineContainerLoggerElement =
			stepLoggerElement.loggerElement("div");

		lineContainerLoggerElement.addChildLoggerElement(
			_getStatusLoggerElement("PASSED"));
	}

	private String _readResource(String path) throws Exception {
		StringBuilder sb = new StringBuilder();

		ClassLoader classLoader = SummaryLogger.class.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(path);

		InputStreamReader inputStreamReader = new InputStreamReader(
			inputStream);

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}

		bufferedReader.close();

		return sb.toString();
	}

	private void _removeUnneededStepsFromLoggerElement(
		LoggerElement loggerElement) {

		LoggerElement majorStepsLoggerElement = loggerElement.loggerElement(
			"ul");

		List<LoggerElement> majorStepLoggerElements =
			majorStepsLoggerElement.loggerElements("li");

		Iterator<LoggerElement> iterator = majorStepLoggerElements.iterator();

		while (iterator.hasNext()) {
			LoggerElement majorStepLoggerElement = iterator.next();

			majorStepLoggerElement.removeChildLoggerElements("button");

			if (iterator.hasNext()) {
				majorStepLoggerElement.removeChildLoggerElements("ul");
			}
			else {
				if (_containsMinorStepWarning) {
					_warnStepLoggerElement(majorStepLoggerElement);
				}
				else {
					_failStepLoggerElement(majorStepLoggerElement);
				}
			}
		}
	}

	private String _replaceExecuteVars(String token, Element element)
		throws Exception {

		Matcher matcher = _pattern.matcher(token);

		while (matcher.find() &&
			   _poshiVariablesContext.containsKeyInExecuteMap(
				   matcher.group(1))) {

			String varName = matcher.group(1);

			String varValue = HtmlUtil.escape(
				_poshiVariablesContext.getStringFromExecuteMap(varName));

			if ((element.attributeValue("function") != null) &&
				varName.startsWith("locator")) {

				varName = StringUtil.replace(varName, "locator", "locator-key");

				String locatorKey =
					_poshiVariablesContext.getStringFromExecuteMap(varName);

				if (Validator.isNotNull(locatorKey)) {
					StringBuilder sb = new StringBuilder();

					sb.append("<em title=\"");
					sb.append(varValue);
					sb.append("\">");
					sb.append(locatorKey);
					sb.append("</em>");

					varValue = sb.toString();
				}
			}

			token = StringUtil.replace(token, matcher.group(), varValue);
		}

		return token;
	}

	private void _startMajorStep(Element element) {
		_majorStepElement = element;
	}

	private void _startMinorStep(Element element) {
		_minorStepElement = element;
	}

	private void _stopMajorStep() {
		_majorStepElement = null;
		_majorStepLoggerElement = null;
		_minorStepElement = null;
		_minorStepLoggerElement = null;
		_minorStepsLoggerElement = null;
	}

	private void _stopMinorStep() {
		_minorStepElement = null;
		_minorStepLoggerElement = null;
	}

	private void _warnStepLoggerElement(LoggerElement stepLoggerElement) {
		stepLoggerElement.addClassName("summary-warning");

		LoggerElement lineContainerLoggerElement =
			stepLoggerElement.loggerElement("div");

		if (lineContainerLoggerElement == null) {
			return;
		}

		lineContainerLoggerElement.addChildLoggerElement(
			_getStatusLoggerElement("WARNING"));
		lineContainerLoggerElement.setName("strong");
	}

	private static final Pattern _pattern = Pattern.compile("\\$\\{([^}]*)\\}");
	private static final Map<String, SummaryLogger> _summaryLoggers =
		new HashMap<>();

	private LoggerElement _causeBodyLoggerElement;
	private boolean _containsMinorStepWarning;
	private Element _majorStepElement;
	private LoggerElement _majorStepLoggerElement;
	private LoggerElement _majorStepsLoggerElement;
	private Element _minorStepElement;
	private LoggerElement _minorStepLoggerElement;
	private LoggerElement _minorStepsLoggerElement;
	private final PoshiProperties _poshiProperties;
	private final PoshiStackTrace _poshiStackTrace;
	private final PoshiVariablesContext _poshiVariablesContext;
	private LoggerElement _summaryContentContainerLoggerElement;
	private LoggerElement _summaryContentWrapperLoggerElement;
	private LoggerElement _summaryLogLoggerElement;
	private LoggerElement _summaryTitleContainerLoggerElement;
	private final String _testNamespacedClassCommandName;
	private int _warningCount;

}
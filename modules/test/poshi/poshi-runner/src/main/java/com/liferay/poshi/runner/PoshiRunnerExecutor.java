/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner;

import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.PoshiGetterUtil;
import com.liferay.poshi.core.PoshiProperties;
import com.liferay.poshi.core.PoshiStackTrace;
import com.liferay.poshi.core.PoshiVariablesContext;
import com.liferay.poshi.core.selenium.LiferaySelenium;
import com.liferay.poshi.core.selenium.LiferaySeleniumMethod;
import com.liferay.poshi.core.util.GetterUtil;
import com.liferay.poshi.core.util.Validator;
import com.liferay.poshi.runner.exception.PoshiRunnerWarningException;
import com.liferay.poshi.runner.logger.PoshiLogger;
import com.liferay.poshi.runner.logger.SummaryLogger;
import com.liferay.poshi.runner.selenium.WebDriverUtil;
import com.liferay.poshi.runner.util.TableUtil;
import com.liferay.poshi.runner.var.type.DefaultTable;
import com.liferay.poshi.runner.var.type.TableFactory;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import org.dom4j.Element;

import org.openqa.selenium.StaleElementReferenceException;

/**
 * @author Karen Dang
 * @author Michael Hashimoto
 * @author Peter Yoo
 */
public class PoshiRunnerExecutor {

	public PoshiRunnerExecutor(
		PoshiLogger poshiLogger, SummaryLogger summaryLogger) {

		_poshiLogger = poshiLogger;
		_summaryLogger = summaryLogger;

		_testNamespacedClassCommandName =
			poshiLogger.getTestNamespacedClassCommandName();

		_poshiProperties = PoshiProperties.getPoshiProperties();
		_poshiStackTrace = PoshiStackTrace.getPoshiStackTrace(
			_testNamespacedClassCommandName);
		_poshiVariablesContext = PoshiVariablesContext.getPoshiVariablesContext(
			_testNamespacedClassCommandName);
	}

	public boolean evaluateConditionalElement(Element element)
		throws Exception {

		_poshiStackTrace.setCurrentElement(element);

		boolean conditionalValue = false;

		String elementName = element.getName();

		if (elementName.equals("and")) {
			List<Element> andElements = element.elements();

			conditionalValue = true;

			for (Element andElement : andElements) {
				if (conditionalValue) {
					conditionalValue = evaluateConditionalElement(andElement);
				}

				if (!conditionalValue) {
					break;
				}
			}
		}
		else if (elementName.equals("condition")) {
			if (element.attributeValue("function") != null) {
				runFunctionExecuteElement(element);

				conditionalValue = (boolean)_returnObject;
			}
			else if (element.attributeValue("selenium") != null) {
				runSeleniumElement(element);

				conditionalValue = (boolean)_returnObject;
			}
		}
		else if (elementName.equals("contains")) {
			String string = _poshiVariablesContext.getReplacedCommandVarsString(
				element.attributeValue("string"));
			String substring =
				_poshiVariablesContext.getReplacedCommandVarsString(
					element.attributeValue("substring"));

			if (string.contains(substring)) {
				conditionalValue = true;
			}
		}
		else if (elementName.equals("equals")) {
			String arg1 = _poshiVariablesContext.getReplacedCommandVarsString(
				element.attributeValue("arg1"));

			String arg2 = _poshiVariablesContext.getReplacedCommandVarsString(
				element.attributeValue("arg2"));

			if (arg1.equals(arg2)) {
				conditionalValue = true;
			}
		}
		else if (elementName.equals("isset")) {
			if (_poshiVariablesContext.containsKeyInCommandMap(
					element.attributeValue("var"))) {

				conditionalValue = true;
			}
		}
		else if (elementName.equals("or")) {
			List<Element> orElements = element.elements();

			for (Element orElement : orElements) {
				if (!conditionalValue) {
					conditionalValue = evaluateConditionalElement(orElement);
				}

				if (conditionalValue) {
					break;
				}
			}
		}
		else if (elementName.equals("not")) {
			List<Element> notElements = element.elements();

			Element notElement = notElements.get(0);

			conditionalValue = !evaluateConditionalElement(notElement);
		}

		if (conditionalValue) {
			_poshiLogger.updateStatus(element, "pass");
		}
		else {
			_poshiLogger.updateStatus(element, "conditional-fail");
		}

		return conditionalValue;
	}

	public void evaluateLoopElement(Element element) {
		if (_inLoop) {
			String elementName = element.getName();

			if (elementName.equals("break")) {
				_hasBreak = true;
			}

			if (elementName.equals("continue")) {
				_hasContinue = true;
			}
		}
	}

	public String getTestNamespacedClassCommandName() {
		return _testNamespacedClassCommandName;
	}

	public boolean isOcularFunction(Element functionCommandElement) {
		List<Element> executeElements = functionCommandElement.elements(
			"execute");

		for (Element executeElement : executeElements) {
			String seleniumAttributeValue = executeElement.attributeValue(
				"selenium");

			if ((seleniumAttributeValue != null) &&
				seleniumAttributeValue.startsWith("ocularAssertElementImage")) {

				return true;
			}
		}

		return false;
	}

	public void parseElement(Element element) throws Exception {
		List<Element> childElements = element.elements();

		for (Element childElement : childElements) {
			if (_inLoop) {
				if (_hasBreak) {
					return;
				}

				if (_hasContinue) {
					continue;
				}
			}

			String childElementName = childElement.getName();

			if (childElementName.equals("break") ||
				childElementName.equals("continue")) {

				evaluateLoopElement(childElement);
			}
			else if (childElementName.equals("echo") ||
					 childElementName.equals("description")) {

				runEchoElement(childElement);
			}
			else if (childElementName.equals("execute")) {
				if (childElement.attributeValue("function") != null) {
					runFunctionExecuteElement(childElement);
				}
				else if (childElement.attributeValue("macro") != null) {
					runMacroExecuteElement(childElement, "macro");
				}
				else if (childElement.attributeValue("selenium") != null) {
					runSeleniumElement(childElement);
				}
				else if (childElement.attributeValue("test-case") != null) {
					runTestCaseExecuteElement(childElement);
				}
				else if (childElement.attributeValue("method") != null) {
					runMethodExecuteElement(childElement);
				}
			}
			else if (childElementName.equals("if")) {
				runIfElement(childElement);
			}
			else if (childElementName.equals("fail")) {
				runFailElement(childElement);
			}
			else if (childElementName.equals("for")) {
				runForElement(childElement);
			}
			else if (childElementName.equals("return")) {
				runReturnElement(childElement);
			}
			else if (childElementName.equals("take-screenshot")) {
				runTakeScreenshotElement(childElement);
			}
			else if (childElementName.equals("task")) {
				runTaskElement(childElement);
			}
			else if (childElementName.equals("var")) {
				runCommandVarElement(childElement, true);
			}
			else if (childElementName.equals("while")) {
				runWhileElement(childElement);
			}
		}
	}

	public void runCommandVarElement(
			Element element, boolean updateLoggerStatus)
		throws Exception {

		_poshiStackTrace.setCurrentElement(element);

		Object varValue = null;

		try {
			varValue = _getVarValue(element);
		}
		catch (Exception exception) {
			if (updateLoggerStatus) {
				if (Validator.isNotNull(element.attributeValue("method"))) {
					_poshiLogger.startCommand(element);

					_summaryLogger.startSummary(element);

					_summaryLogger.failSummary(
						element, exception.getMessage(),
						_poshiLogger.getDetailsLinkId());

					_poshiLogger.failCommand(element);
				}

				_poshiLogger.updateStatus(element, "fail");
			}

			throw exception;
		}

		if (varValue instanceof String) {
			varValue = _poshiVariablesContext.replaceCommandVars(
				(String)varValue);

			if (varValue instanceof String) {
				Matcher matcher = _variablePattern.matcher((String)varValue);

				if (matcher.matches()) {
					if (updateLoggerStatus) {
						_poshiLogger.updateStatus(element, "pass");
					}

					return;
				}
			}
		}

		String varName = element.attributeValue("name");

		_poshiVariablesContext.putIntoCommandMap(varName, varValue);

		String currentFilePath = _poshiStackTrace.getCurrentFilePath();

		if (currentFilePath.contains(".macro") ||
			currentFilePath.contains(".testcase")) {

			String staticValue = element.attributeValue("static");

			if ((staticValue != null) && staticValue.equals("true")) {
				_poshiVariablesContext.putIntoStaticMap(varName, varValue);
			}
		}

		if (updateLoggerStatus) {
			_poshiLogger.updateStatus(element, "pass");
		}
	}

	public void runEchoElement(Element element) throws Exception {
		_poshiStackTrace.setCurrentElement(element);

		_poshiLogger.logMessage(element);

		String message = element.attributeValue("message");

		if (message == null) {
			message = element.getText();
		}

		message = StringEscapeUtils.unescapeXml(message);

		System.out.println(_poshiVariablesContext.replaceCommandVars(message));
	}

	public void runExecuteVarElement(
			Element element, boolean updateLoggerStatus)
		throws Exception {

		_poshiStackTrace.setCurrentElement(element);

		String varName = element.attributeValue("name");

		if (_poshiVariablesContext.containsKeyInStaticMap(varName)) {
			if (updateLoggerStatus) {
				_poshiLogger.updateStatus(element, "fail");
			}

			throw new Exception(
				"Unable to set var '" + varName +
					"' as parameter of function. It is already set in the " +
						"static context.");
		}

		Object varValue = null;

		try {
			varValue = _getVarValue(element);
		}
		catch (Exception exception) {
			if (updateLoggerStatus) {
				_poshiLogger.updateStatus(element, "fail");
			}

			throw exception;
		}

		if (varValue instanceof String) {
			varValue = _poshiVariablesContext.replaceCommandVars(
				(String)varValue);

			if (varValue instanceof String) {
				varValue = _poshiVariablesContext.replaceExecuteVars(
					(String)varValue);

				if (varValue instanceof String) {
					Matcher matcher = _variablePattern.matcher(
						(String)varValue);

					if (matcher.matches() && varValue.equals(varValue)) {
						if (updateLoggerStatus) {
							_poshiLogger.updateStatus(element, "pass");
						}

						return;
					}
				}
			}
		}

		_poshiVariablesContext.putIntoExecuteMap(varName, varValue);

		if (updateLoggerStatus) {
			_poshiLogger.updateStatus(element, "pass");
		}
	}

	public void runFailElement(Element element) throws Exception {
		_poshiStackTrace.setCurrentElement(element);

		_poshiLogger.logMessage(element);

		String message = element.attributeValue("message");

		_poshiLogger.updateStatus(element, "fail");

		if (Validator.isNotNull(message)) {
			throw new Exception(
				_poshiVariablesContext.getReplacedCommandVarsString(message));
		}

		throw new Exception();
	}

	public void runForElement(Element element) throws Exception {
		_poshiStackTrace.setCurrentElement(element);

		_inLoop = true;

		String paramName = _poshiVariablesContext.getReplacedCommandVarsString(
			element.attributeValue("param"));

		if (element.attributeValue("list") != null) {
			String list = _poshiVariablesContext.getReplacedCommandVarsString(
				element.attributeValue("list"));

			String[] paramValues = list.split(",");

			for (String paramValue : paramValues) {
				_poshiVariablesContext.putIntoCommandMap(paramName, paramValue);

				parseElement(element);

				if (_hasContinue) {
					_hasContinue = false;

					continue;
				}

				if (_hasBreak) {
					_hasBreak = false;

					break;
				}
			}
		}
		else if (element.attributeValue("table") != null) {
			DefaultTable defaultTable =
				(DefaultTable)_poshiVariablesContext.replaceCommandVars(
					element.attributeValue("table"));

			List<List<String>> rows = defaultTable.getRows();

			Iterator<?> iterator = rows.iterator();

			while (iterator.hasNext()) {
				_poshiVariablesContext.putIntoCommandMap(
					paramName, iterator.next());

				parseElement(element);
			}
		}

		_inLoop = false;

		_poshiLogger.updateStatus(element, "pass");
	}

	public void runFunctionCommandElement(Element commandElement)
		throws Exception {

		_poshiStackTrace.setCurrentElement(commandElement);

		_poshiVariablesContext.pushCommandMap();

		try {
			parseElement(commandElement);
		}
		catch (Exception exception) {
			throw exception;
		}
		finally {
			_poshiVariablesContext.popCommandMap();
		}
	}

	public void runFunctionExecuteElement(Element executeElement)
		throws Exception {

		if (_functionExecuteElement == null) {
			_functionExecuteElement = executeElement;
		}

		_poshiStackTrace.setCurrentElement(executeElement);

		List<Element> executeVarElements = executeElement.elements("var");

		for (Element executeVarElement : executeVarElements) {
			runExecuteVarElement(executeVarElement, false);
		}

		_poshiStackTrace.setCurrentElement(executeElement);

		String namespacedClassCommandName = executeElement.attributeValue(
			"function");

		String classCommandName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		Exception exception = null;

		for (int i = 1; i <= PoshiContext.getFunctionMaxArgumentCount(); i++) {
			String locator = executeElement.attributeValue("locator" + i);

			if (locator == null) {
				locator =
					(String)_poshiVariablesContext.getObjectFromCommandMap(
						"locator" + i);
			}

			if (locator != null) {
				Matcher matcher = _locatorKeyPattern.matcher(locator);

				if (matcher.find() && !locator.contains("/")) {
					String pathClassName =
						_poshiVariablesContext.getReplacedCommandVarsString(
							PoshiGetterUtil.
								getClassNameFromNamespacedClassCommandName(
									locator));

					String locatorKey =
						_poshiVariablesContext.getReplacedCommandVarsString(
							PoshiGetterUtil.
								getCommandNameFromNamespacedClassCommandName(
									locator));

					_poshiVariablesContext.putIntoExecuteMap(
						"locator-key" + i, locatorKey);

					locator = PoshiContext.getPathLocator(
						pathClassName + "#" + locatorKey,
						_poshiStackTrace.
							getNamespaceFromNamespacedClassCommandName(
								locator));

					if (locator == null) {
						exception = new Exception(
							"No such locator key " + pathClassName + "#" +
								locatorKey);
					}

					locator = (String)_poshiVariablesContext.replaceExecuteVars(
						locator);
				}

				_poshiVariablesContext.putIntoExecuteMap(
					"locator" + i, locator);
			}

			Object value = executeElement.attributeValue("value" + i);

			if (value == null) {
				value = _poshiVariablesContext.getObjectFromCommandMap(
					"value" + i);
			}

			if (value == null) {
				continue;
			}

			_poshiVariablesContext.putIntoExecuteMap("value" + i, value);
		}

		if (_functionExecuteElement == executeElement) {
			_summaryLogger.startSummary(_functionExecuteElement);
		}

		_poshiLogger.startCommand(executeElement);

		_poshiStackTrace.pushStackTrace(executeElement);

		Element commandElement = PoshiContext.getFunctionCommandElement(
			classCommandName,
			_poshiStackTrace.getCurrentNamespace(namespacedClassCommandName));

		try {
			if (exception != null) {
				throw exception;
			}

			runFunctionCommandElement(commandElement);
		}
		catch (Throwable throwable) {
			String warningMessage = _getWarningFromThrowable(throwable);

			if (warningMessage != null) {
				_functionWarningMessage = warningMessage;
			}
			else {
				_poshiStackTrace.popStackTrace();

				if (_functionExecuteElement == executeElement) {
					_poshiStackTrace.setCurrentElement(executeElement);

					_summaryLogger.failSummary(
						_functionExecuteElement, throwable.getMessage(),
						_poshiLogger.getDetailsLinkId());

					if (isOcularFunction(commandElement)) {
						_poshiLogger.ocularCommand(_functionExecuteElement);
					}
					else {
						_poshiLogger.failCommand(_functionExecuteElement);
					}

					_functionExecuteElement = null;
					_functionWarningMessage = null;
				}

				throw throwable;
			}
		}

		_poshiStackTrace.popStackTrace();

		_poshiStackTrace.setCurrentElement(executeElement);

		if (_functionExecuteElement == executeElement) {
			if (_functionWarningMessage != null) {
				_summaryLogger.warnSummary(
					_functionExecuteElement, _functionWarningMessage);

				_poshiLogger.warnCommand(_functionExecuteElement);
			}
			else {
				_summaryLogger.passSummary(executeElement);

				_poshiLogger.passCommand(executeElement);
			}

			_functionExecuteElement = null;
			_functionWarningMessage = null;
		}
	}

	public void runIfElement(Element element) throws Exception {
		_poshiStackTrace.setCurrentElement(element);

		List<Element> ifChildElements = element.elements();

		Element ifConditionElement = ifChildElements.get(0);

		boolean condition = evaluateConditionalElement(ifConditionElement);

		if (condition) {
			Element ifThenElement = element.element("then");

			_poshiStackTrace.setCurrentElement(ifThenElement);

			parseElement(ifThenElement);

			_poshiLogger.updateStatus(ifThenElement, "pass");

			_poshiLogger.updateStatus(element, "pass");

			return;
		}

		_poshiLogger.updateStatus(element, "conditional-fail");

		if (element.element("elseif") != null) {
			List<Element> elseIfElements = element.elements("elseif");

			for (Element elseIfElement : elseIfElements) {
				_poshiStackTrace.setCurrentElement(elseIfElement);

				List<Element> elseIfChildElements = elseIfElement.elements();

				Element elseIfConditionElement = elseIfChildElements.get(0);

				condition = evaluateConditionalElement(elseIfConditionElement);

				if (condition) {
					Element elseIfThenElement = elseIfElement.element("then");

					_poshiStackTrace.setCurrentElement(elseIfThenElement);

					parseElement(elseIfThenElement);

					_poshiLogger.updateStatus(elseIfThenElement, "pass");

					_poshiLogger.updateStatus(elseIfElement, "pass");

					return;
				}

				_poshiLogger.updateStatus(elseIfElement, "conditional-fail");
			}
		}

		if (element.element("else") != null) {
			Element elseElement = element.element("else");

			_poshiStackTrace.setCurrentElement(elseElement);

			parseElement(elseElement);

			_poshiLogger.updateStatus(elseElement, "pass");
		}
	}

	public void runMacroCommandElement(
			Element commandElement, String namespacedClassCommandName)
		throws Exception {

		_poshiStackTrace.setCurrentElement(commandElement);

		String classCommandName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				classCommandName);

		String namespace = _poshiStackTrace.getCurrentNamespace(
			namespacedClassCommandName);

		List<Element> rootVarElements = PoshiContext.getRootVarElements(
			"macro", className, namespace);

		for (Element rootVarElement : rootVarElements) {
			if (Validator.isNotNull(rootVarElement.attributeValue("static"))) {
				_poshiVariablesContext.pushStaticVarsIntoCommandMap();
			}

			runRootVarElement(rootVarElement, true);
		}

		_poshiVariablesContext.pushCommandMap();

		if (commandElement == null) {
			throw new RuntimeException(
				"Nonexistent macro command: " + namespacedClassCommandName);
		}

		parseElement(commandElement);

		_poshiVariablesContext.popCommandMap();
	}

	public void runMacroExecuteElement(Element executeElement, String macroType)
		throws Exception {

		_poshiStackTrace.setCurrentElement(executeElement);

		String namespacedClassCommandName = executeElement.attributeValue(
			macroType);

		String classCommandName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		List<Element> executeVarElements = executeElement.elements("var");

		for (Element executeVarElement : executeVarElements) {
			runExecuteVarElement(executeVarElement, false);
		}

		_poshiStackTrace.pushStackTrace(executeElement);

		String namespace = _poshiStackTrace.getCurrentNamespace(
			namespacedClassCommandName);

		_summaryLogger.startSummary(executeElement);

		Element commandElement = PoshiContext.getMacroCommandElement(
			classCommandName, namespace);

		try {
			runMacroCommandElement(commandElement, namespacedClassCommandName);

			Element returnElement = executeElement.element("return");

			if (returnElement != null) {
				if (_macroReturnValue == null) {
					throw new RuntimeException(
						"No value was returned from macro command '" +
							namespacedClassCommandName + "'");
				}

				String returnName = returnElement.attributeValue("name");

				if (_poshiVariablesContext.containsKeyInStaticMap(returnName)) {
					_poshiVariablesContext.putIntoStaticMap(
						returnName, _macroReturnValue);
				}

				_poshiVariablesContext.putIntoCommandMap(
					returnName, _macroReturnValue);

				_macroReturnValue = null;
			}
		}
		catch (Exception exception) {
			_summaryLogger.failSummary(
				executeElement, exception.getMessage(),
				_poshiLogger.getDetailsLinkId());

			throw exception;
		}

		_summaryLogger.passSummary(executeElement);

		_poshiStackTrace.popStackTrace();

		_poshiLogger.updateStatus(executeElement, "pass");
	}

	public void runMethodExecuteElement(Element executeElement)
		throws Exception {

		_poshiStackTrace.setCurrentElement(executeElement);

		List<String> args = new ArrayList<>();

		List<Element> argElements = executeElement.elements("arg");

		for (Element argElement : argElements) {
			args.add(argElement.attributeValue("value"));
		}

		String className = executeElement.attributeValue("class");
		String methodName = executeElement.attributeValue("method");

		try {
			Object returnValue = PoshiGetterUtil.getMethodReturnValue(
				getTestNamespacedClassCommandName(), args, className,
				methodName, null);

			Element returnElement = executeElement.element("return");

			if (returnElement != null) {
				_poshiVariablesContext.putIntoCommandMap(
					returnElement.attributeValue("name"), returnValue);
			}

			_poshiLogger.startExternalMethodCommand(
				executeElement, args, returnValue);
		}
		catch (Throwable throwable) {
			_poshiLogger.startExternalMethodCommand(executeElement, args, null);

			_summaryLogger.startSummary(executeElement);

			_summaryLogger.failSummary(
				executeElement, throwable.getMessage(),
				_poshiLogger.getDetailsLinkId());

			_poshiLogger.updateStatus(executeElement, "fail");

			_poshiLogger.failCommand(executeElement);

			throw throwable;
		}

		_poshiLogger.updateStatus(executeElement, "pass");

		_poshiLogger.passCommand(executeElement);
	}

	public void runReturnElement(Element returnElement) throws Exception {
		_poshiStackTrace.setCurrentElement(returnElement);

		if (returnElement.attributeValue("value") != null) {
			String returnValue = returnElement.attributeValue("value");

			_macroReturnValue = _poshiVariablesContext.replaceCommandVars(
				returnValue);
		}

		_poshiLogger.updateStatus(returnElement, "pass");
	}

	public void runRootVarElement(Element element, boolean updateLoggerStatus)
		throws Exception {

		_poshiStackTrace.setCurrentElement(element);

		Object varValue = null;

		try {
			varValue = _getVarValue(element);
		}
		catch (Exception exception) {
			if (updateLoggerStatus) {
				_poshiLogger.updateStatus(element, "fail");
			}

			throw exception;
		}

		if (varValue instanceof String) {
			varValue = _poshiVariablesContext.replaceExecuteVars(
				(String)varValue);

			varValue = _poshiVariablesContext.replaceStaticVars(
				(String)varValue);

			if (varValue instanceof String) {
				Matcher matcher = _variablePattern.matcher((String)varValue);

				if (matcher.matches() && varValue.equals(varValue)) {
					if (updateLoggerStatus) {
						_poshiLogger.updateStatus(element, "pass");
					}

					return;
				}
			}
		}

		String varName = element.attributeValue("name");

		if (!_poshiVariablesContext.containsKeyInExecuteMap(varName)) {
			_poshiVariablesContext.putIntoExecuteMap(varName, varValue);
		}

		String currentFilePath = _poshiStackTrace.getCurrentFilePath();

		if (currentFilePath.contains(".testcase")) {
			String staticValue = element.attributeValue("static");

			if ((staticValue != null) && staticValue.equals("true") &&
				!_poshiVariablesContext.containsKeyInStaticMap(varName)) {

				_poshiVariablesContext.putIntoStaticMap(varName, varValue);
			}
		}

		if (updateLoggerStatus) {
			_poshiLogger.updateStatus(element, "pass");
		}
	}

	public void runSeleniumElement(Element executeElement) throws Exception {
		Properties properties =
			PoshiContext.getNamespacedClassCommandNameProperties(
				getTestNamespacedClassCommandName());

		if (GetterUtil.getBoolean(
				properties.getProperty("disable-webdriver"))) {

			throw new RuntimeException(
				"Unable to call Selenium method while WebDriver is disabled");
		}

		_poshiStackTrace.setCurrentElement(executeElement);

		String selenium = executeElement.attributeValue("selenium");

		LiferaySeleniumMethod liferaySeleniumMethod =
			PoshiContext.getLiferaySeleniumMethod(selenium);

		List<Object> arguments = new ArrayList<>();
		List<Class<?>> parameterClasses = new ArrayList<>();

		int parameterCount = liferaySeleniumMethod.getParameterCount();

		for (int i = 0; i < parameterCount; i++) {
			Object argument = executeElement.attributeValue(
				"argument" + (i + 1));

			if (argument == null) {
				List<String> parameterNames =
					liferaySeleniumMethod.getParameterNames();

				String parameterName = parameterNames.get(i);

				argument = _poshiVariablesContext.getObjectFromCommandMap(
					parameterName);
			}
			else {
				argument = _poshiVariablesContext.getReplacedCommandVarsObject(
					(String)argument);
			}

			Class<?> parameterTypeClass =
				liferaySeleniumMethod.getParameterTypes()[i];

			if (Objects.equals(
					parameterTypeClass.getName(), "java.lang.String") &&
				Validator.isNotNull(argument)) {

				argument = argument.toString();
			}

			arguments.add(argument);

			parameterClasses.add(liferaySeleniumMethod.getParameterTypes()[i]);
		}

		List<String> argumentsList = new ArrayList<>();

		for (Object argument : arguments) {
			if (argument == null) {
				continue;
			}

			argumentsList.add(argument.toString());
		}

		_poshiLogger.logSeleniumCommand(executeElement, argumentsList);

		LiferaySelenium liferaySelenium = WebDriverUtil.getLiferaySelenium(
			getTestNamespacedClassCommandName());

		Class<?> clazz = liferaySelenium.getClass();

		_returnObject = invokeLiferaySeleniumMethod(
			clazz.getMethod(
				selenium, parameterClasses.toArray(new Class<?>[0])),
			arguments.toArray(new Object[0]));
	}

	public void runTakeScreenshotElement(Element element) throws Exception {
		_poshiStackTrace.setCurrentElement(element);

		_poshiLogger.takeScreenshotCommand(element);
	}

	public void runTaskElement(Element element) throws Exception {
		_poshiStackTrace.setCurrentElement(element);

		try {
			_summaryLogger.startSummary(element);

			parseElement(element);
		}
		catch (Exception exception) {
			_summaryLogger.failSummary(
				element, exception.getMessage(),
				_poshiLogger.getDetailsLinkId());

			throw exception;
		}

		_summaryLogger.passSummary(element);

		_poshiLogger.updateStatus(element, "pass");
	}

	public void runTestCaseCommandElement(
			Element element, String namespacedClassCommandName)
		throws Exception {

		_poshiStackTrace.setCurrentElement(element);

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName);

		List<Element> rootVarElements = PoshiContext.getRootVarElements(
			"test-case", className, namespace);

		for (Element rootVarElement : rootVarElements) {
			if (Validator.isNotNull(rootVarElement.attributeValue("static"))) {
				_poshiVariablesContext.pushStaticVarsIntoCommandMap();
			}

			runRootVarElement(rootVarElement, false);
		}

		_poshiVariablesContext.pushCommandMap();

		parseElement(element);

		_poshiVariablesContext.popCommandMap();
	}

	public void runTestCaseExecuteElement(Element executeElement)
		throws Exception {

		_poshiStackTrace.setCurrentElement(executeElement);

		String namespacedClassCommandName = executeElement.attributeValue(
			"test-case");

		_poshiStackTrace.pushStackTrace(executeElement);

		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName);

		Element commandElement = PoshiContext.getTestCaseCommandElement(
			namespacedClassCommandName, namespace);

		runTestCaseCommandElement(commandElement, namespacedClassCommandName);

		_poshiStackTrace.popStackTrace();

		_poshiLogger.updateStatus(executeElement, "pass");
	}

	public void runWhileElement(Element element) throws Exception {
		_poshiStackTrace.setCurrentElement(element);

		_inLoop = true;

		int maxIterations = 15;

		if (element.attributeValue("max-iterations") != null) {
			maxIterations = GetterUtil.getInteger(
				element.attributeValue("max-iterations"));
		}

		List<Element> whileChildElements = element.elements();

		Element conditionElement = whileChildElements.get(0);

		Element thenElement = element.element("then");

		boolean conditionRun = false;

		for (int i = 0; i < maxIterations; i++) {
			if (!evaluateConditionalElement(conditionElement) || _hasBreak) {
				break;
			}

			if (_hasContinue) {
				continue;
			}

			conditionRun = true;

			_poshiStackTrace.setCurrentElement(thenElement);

			parseElement(thenElement);

			_poshiLogger.updateStatus(thenElement, "pass");
		}

		_inLoop = false;

		if (conditionRun) {
			_poshiLogger.updateStatus(element, "pass");
		}
		else {
			_poshiLogger.updateStatus(element, "conditional-fail");
		}
	}

	protected Object callWithTimeout(
			Callable<?> callable, String description, long timeoutSeconds)
		throws Exception {

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Future<?> future = executorService.submit(callable);

		executorService.shutdown();

		try {
			return future.get(timeoutSeconds, TimeUnit.SECONDS);
		}
		catch (ExecutionException executionException) {
			if (_poshiProperties.debugStacktrace) {
				throw executionException;
			}

			Throwable throwable = executionException.getCause();

			if (throwable instanceof Error) {
				throw (Error)throwable;
			}

			throw (Exception)throwable;
		}
		catch (InterruptedException | TimeoutException exception) {
			future.cancel(true);

			if (exception instanceof TimeoutException) {
				System.out.println(
					"Timed out after " + timeoutSeconds +
						" seconds while executing " + description);
			}

			throw new Exception(
				"An error occurred while executing " + description, exception);
		}
	}

	protected Object getVarMethodValue(
			String expression, String defaultNamespace)
		throws Exception {

		List<String> args = new ArrayList<>();

		int x = expression.indexOf("(");
		int y = expression.lastIndexOf(")");

		if ((x + 1) < y) {
			String parameterString = expression.substring(x + 1, y);

			Matcher parameterMatcher = _parameterPattern.matcher(
				parameterString);

			while (parameterMatcher.find()) {
				String parameterValue = parameterMatcher.group();

				if (parameterValue.startsWith("'") &&
					parameterValue.endsWith("'")) {

					parameterValue = parameterValue.substring(
						1, parameterValue.length() - 1);
				}

				Matcher matcher = _locatorKeyPattern.matcher(parameterValue);

				if (matcher.matches()) {
					String namespace = matcher.group("namespace");

					if (namespace == null) {
						parameterValue = PoshiContext.getPathLocator(
							parameterValue, defaultNamespace);
					}
					else {
						parameterValue = PoshiContext.getPathLocator(
							parameterValue, namespace);
					}
				}

				if (parameterValue.contains("\'")) {
					parameterValue = parameterValue.replaceAll("\\\\'", "'");
				}

				args.add(parameterValue);
			}
		}

		y = expression.indexOf("#");

		String className = expression.substring(0, y);
		String methodName = expression.substring(y + 1, x);

		Object object = null;

		if (className.equals("selenium")) {
			object = WebDriverUtil.getLiferaySelenium(
				getTestNamespacedClassCommandName());
		}

		return PoshiGetterUtil.getMethodReturnValue(
			getTestNamespacedClassCommandName(), args, className, methodName,
			object);
	}

	protected Object invokeLiferaySeleniumMethod(Method method, Object... args)
		throws Exception {

		LiferaySelenium liferaySelenium = WebDriverUtil.getLiferaySelenium(
			getTestNamespacedClassCommandName());

		String methodName = method.getName();

		Callable<Object> task = () -> {
			int maxRetries = 1;
			int retryCount = 0;

			while (true) {
				try {
					return method.invoke(liferaySelenium, args);
				}
				catch (Exception exception) {
					Throwable throwable = exception.getCause();

					if ((throwable instanceof StaleElementReferenceException) &&
						(retryCount < maxRetries)) {

						retryCount++;

						StringBuilder sb = new StringBuilder();

						sb.append("\nElement turned stale while running ");
						sb.append(methodName);
						sb.append(". Retrying in ");
						sb.append(_poshiProperties.testRetryCommandWaitTime);
						sb.append("seconds.");

						System.out.println(sb.toString());

						continue;
					}

					if (_poshiProperties.debugStacktrace) {
						throw exception;
					}

					if (throwable instanceof Error) {
						exception = new Exception(throwable.getMessage());

						exception.setStackTrace(throwable.getStackTrace());

						throw exception;
					}

					throw (Exception)throwable;
				}
			}
		};

		Long timeout = Long.valueOf(_poshiProperties.timeoutExplicitWait) + 60L;

		if (methodName.equals("antCommand") | methodName.equals("pause")) {
			timeout = 3600L;
		}

		return callWithTimeout(task, methodName, timeout);
	}

	private Object _getVarValue(Element element) throws Exception {
		Object varValue = element.attributeValue("value");

		if (varValue == null) {
			if (element.attributeValue("method") != null) {
				String methodName = element.attributeValue("method");

				try {
					varValue = getVarMethodValue(
						methodName, _poshiStackTrace.getCurrentNamespace());
				}
				catch (Exception exception) {
					Throwable throwable = exception.getCause();

					if ((throwable != null) &&
						(throwable.getMessage() != null)) {

						throw new Exception(throwable.getMessage(), exception);
					}

					throw exception;
				}
			}
			else if (element.attributeValue("type") != null) {
				String varType = element.attributeValue("type");

				if (varType.equals("Table")) {
					varValue = TableUtil.getRawDataListFromString(
						element.getText());
				}
				else if ((varType.equals("HashesTable") ||
						  varType.equals("RawTable") ||
						  varType.equals("RowsHashTable")) &&
						 (element.attributeValue("from") != null)) {

					Object varFrom = _poshiVariablesContext.replaceCommandVars(
						element.attributeValue("from"));

					if (!(varFrom instanceof List)) {
						StringBuilder sb = new StringBuilder();

						sb.append("Variable '");
						sb.append((String)varFrom);
						sb.append("' is not an instance of type 'List'");

						throw new IllegalArgumentException(sb.toString());
					}

					varValue = TableFactory.newTable(
						(List<List<String>>)varFrom, varType);
				}
			}
			else if (element.attributeValue("from") != null) {
				Object varFrom = _poshiVariablesContext.replaceCommandVars(
					element.attributeValue("from"));

				if (element.attributeValue("hash") != null) {
					LinkedHashMap<?, ?> varFromMap =
						(LinkedHashMap<?, ?>)varFrom;

					varValue = varFromMap.get(element.attributeValue("hash"));
				}
				else if (element.attributeValue("index") != null) {
					List<?> varFromList = (List<?>)varFrom;

					varValue = varFromList.get(
						GetterUtil.getInteger(element.attributeValue("index")));
				}
			}
			else {
				varValue = element.getText();
			}
		}

		return varValue;
	}

	private String _getWarningFromThrowable(Throwable throwable) {
		if (throwable instanceof PoshiRunnerWarningException) {
			return throwable.getMessage();
		}

		Throwable causeThrowable = throwable.getCause();

		if (causeThrowable != null) {
			return _getWarningFromThrowable(causeThrowable);
		}

		return null;
	}

	private static final Pattern _locatorKeyPattern = Pattern.compile(
		"((?<namespace>[\\w]+)\\.)?(\\w+)#(\\$\\{\\w+\\}|[A-Z0-9_]+)");
	private static final Pattern _parameterPattern = Pattern.compile(
		"('([^'\\\\]|\\\\.)*'|[^',\\s]+)");
	private static final Pattern _variablePattern = Pattern.compile(
		"\\$\\{([^}]*)\\}");

	private Element _functionExecuteElement;
	private String _functionWarningMessage;
	private boolean _hasBreak;
	private boolean _hasContinue;
	private boolean _inLoop;
	private Object _macroReturnValue;
	private final PoshiLogger _poshiLogger;
	private final PoshiProperties _poshiProperties;
	private final PoshiStackTrace _poshiStackTrace;
	private final PoshiVariablesContext _poshiVariablesContext;
	private Object _returnObject;
	private final SummaryLogger _summaryLogger;
	private final String _testNamespacedClassCommandName;

}
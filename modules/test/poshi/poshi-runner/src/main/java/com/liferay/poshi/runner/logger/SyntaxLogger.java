/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.logger;

import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.PoshiGetterUtil;
import com.liferay.poshi.core.PoshiStackTrace;
import com.liferay.poshi.core.util.Validator;
import com.liferay.poshi.runner.exception.PoshiRunnerLoggerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

/**
 * @author Leslie Wong
 */
public abstract class SyntaxLogger {

	public SyntaxLogger() {
	}

	public SyntaxLogger(String namespacedClassCommandName) throws Exception {
		generateSyntaxLog(namespacedClassCommandName);

		poshiStackTrace = PoshiStackTrace.getPoshiStackTrace(
			namespacedClassCommandName);
	}

	public void generateSyntaxLog(String namespacedClassCommandName)
		throws PoshiRunnerLoggerException {

		poshiStackTrace = PoshiStackTrace.getPoshiStackTrace(
			namespacedClassCommandName);

		try {
			_syntaxLogLoggerElement = new LoggerElement("syntaxLogContainer");

			_syntaxLogLoggerElement.setClassName("syntax-log-container");
			_syntaxLogLoggerElement.setName("ul");

			LoggerElement headerLoggerElement = new LoggerElement();

			headerLoggerElement.setClassName("header");
			headerLoggerElement.setName("li");

			LoggerElement lineContainerLoggerElement = new LoggerElement();

			lineContainerLoggerElement.setClassName("line-container");
			lineContainerLoggerElement.setID(null);
			lineContainerLoggerElement.setName("div");

			LoggerElement lineLoggerElement = new LoggerElement();

			lineLoggerElement.setClassName("test-case-command");
			lineLoggerElement.setID(null);
			lineLoggerElement.setName("h3");
			lineLoggerElement.setText(namespacedClassCommandName);

			lineContainerLoggerElement.addChildLoggerElement(lineLoggerElement);

			headerLoggerElement.addChildLoggerElement(
				lineContainerLoggerElement);

			LoggerElement childContainerLoggerElement = new LoggerElement();

			childContainerLoggerElement.setClassName("child-container");
			childContainerLoggerElement.setID(null);
			childContainerLoggerElement.setName("ul");

			String className =
				PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
					namespacedClassCommandName);
			String namespace =
				PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
					namespacedClassCommandName);

			Element setUpElement = PoshiContext.getTestCaseCommandElement(
				className + "#set-up", namespace);

			if (setUpElement != null) {
				poshiStackTrace.startStackTrace(
					namespace + "." + className + "#set-up", "test-case");

				childContainerLoggerElement.addChildLoggerElement(
					getLoggerElementFromElement(setUpElement));

				poshiStackTrace.emptyStackTrace();
			}

			poshiStackTrace.startStackTrace(
				namespacedClassCommandName, "test-case");

			String classCommandName =
				PoshiGetterUtil.
					getClassCommandNameFromNamespacedClassCommandName(
						namespacedClassCommandName);

			Element testCaseElement = PoshiContext.getTestCaseCommandElement(
				classCommandName, namespace);

			if (testCaseElement == null) {
				throw new PoshiRunnerLoggerException(
					"No test case element found for " + classCommandName);
			}

			childContainerLoggerElement.addChildLoggerElement(
				getLoggerElementFromElement(testCaseElement));

			poshiStackTrace.emptyStackTrace();

			Element tearDownElement = PoshiContext.getTestCaseCommandElement(
				className + "#tear-down", namespace);

			if (tearDownElement != null) {
				poshiStackTrace.startStackTrace(
					namespace + "." + className + "#tear-down", "test-case");

				childContainerLoggerElement.addChildLoggerElement(
					getLoggerElementFromElement(tearDownElement));

				poshiStackTrace.emptyStackTrace();
			}

			headerLoggerElement.addChildLoggerElement(
				childContainerLoggerElement);

			_syntaxLogLoggerElement.addChildLoggerElement(headerLoggerElement);
		}
		catch (Throwable throwable) {
			throw new PoshiRunnerLoggerException(
				throwable.getMessage(), throwable);
		}
	}

	public String getSyntaxLogText() {
		return _syntaxLogLoggerElement.toString();
	}

	public LoggerElement getSyntaxLoggerElement(String stackTrace) {
		return _loggerElements.get(stackTrace);
	}

	public abstract void updateStatus(Element element, String status);

	protected LoggerElement getBtnContainerLoggerElement(Element element) {
		LoggerElement btnContainerLoggerElement = new LoggerElement();

		btnContainerLoggerElement.setClassName("btn-container");
		btnContainerLoggerElement.setName("div");

		StringBuilder sb = new StringBuilder();

		sb.append(
			getLineNumberItemText(PoshiGetterUtil.getLineNumber(element)));

		if (isExecuteChildElementLogged(element)) {
			sb.append(getBtnItemText("btn-collapse"));
		}

		btnContainerLoggerElement.setText(sb.toString());

		return btnContainerLoggerElement;
	}

	protected final String getBtnItemText(String className) {
		LoggerElement loggerElement = new LoggerElement();

		if (className.equals("btn-collapse")) {
			loggerElement.setAttribute(
				"data-btnlinkid", "collapse-" + _btnLinkCollapseId);
		}
		else if (className.equals("btn-var")) {
			loggerElement.setAttribute(
				"data-btnlinkid", "var-" + _btnLinkVarId);
		}

		loggerElement.setClassName("btn " + className);
		loggerElement.setID(null);
		loggerElement.setName("button");

		return loggerElement.toString();
	}

	protected int getBtnLinkVarId() {
		return _btnLinkVarId;
	}

	protected LoggerElement getChildContainerLoggerElement() throws Exception {
		return getChildContainerLoggerElement(null, null);
	}

	protected LoggerElement getChildContainerLoggerElement(Element element)
		throws Exception {

		return getChildContainerLoggerElement(element, null);
	}

	protected LoggerElement getChildContainerLoggerElement(
			Element element, Element rootElement)
		throws Exception {

		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setAttribute(
			"data-btnlinkid", "collapse-" + _btnLinkCollapseId);
		loggerElement.setClassName("child-container collapse collapsible");
		loggerElement.setName("ul");

		if (rootElement != null) {
			List<Element> rootVarElements = rootElement.elements("var");

			for (Element rootVarElement : rootVarElements) {
				loggerElement.addChildLoggerElement(
					getVarLoggerElement(rootVarElement));
			}
		}

		if (element != null) {
			List<Element> childElements = element.elements();

			for (Element childElement : childElements) {
				String childElementName = childElement.getName();

				if (childElementName.equals("description") ||
					childElementName.equals("echo")) {

					loggerElement.addChildLoggerElement(
						getEchoLoggerElement(childElement));
				}
				else if (childElementName.equals("execute")) {
					if (childElement.attributeValue("function") != null) {
						loggerElement.addChildLoggerElement(
							getFunctionExecuteLoggerElement(childElement));
					}
					else if (childElement.attributeValue("macro") != null) {
						loggerElement.addChildLoggerElement(
							getMacroExecuteLoggerElement(
								childElement, "macro"));
					}
					else if (childElement.attributeValue("method") != null) {
						loggerElement.addChildLoggerElement(
							getMethodExecuteLoggerElement(childElement));
					}
					else if (childElement.attributeValue("test-case") != null) {
						loggerElement.addChildLoggerElement(
							getTestCaseExecuteLoggerElement(childElement));
					}
				}
				else if (childElementName.equals("fail")) {
					loggerElement.addChildLoggerElement(
						getFailLoggerElement(childElement));
				}
				else if (childElementName.equals("for") ||
						 childElementName.equals("task")) {

					loggerElement.addChildLoggerElement(
						getForLoggerElement(childElement));
				}
				else if (childElementName.equals("if")) {
					loggerElement.addChildLoggerElement(
						getIfLoggerElement(childElement));
				}
				else if (childElementName.equals("return")) {
					loggerElement.addChildLoggerElement(
						getReturnLoggerElement(childElement));
				}
				else if (childElementName.equals("take-screenshot")) {
					loggerElement.addChildLoggerElement(
						getTakeScreenshotLoggerElement(childElement));
				}
				else if (childElementName.equals("var")) {
					loggerElement.addChildLoggerElement(
						getVarLoggerElement(childElement));
				}
				else if (childElementName.equals("while")) {
					loggerElement.addChildLoggerElement(
						getWhileLoggerElement(childElement));
				}
			}
		}

		return loggerElement;
	}

	protected LoggerElement getEchoLoggerElement(Element element) {
		return getLineGroupLoggerElement("echo", element);
	}

	protected LoggerElement getFailLoggerElement(Element element) {
		return getLineGroupLoggerElement(element);
	}

	protected LoggerElement getForLoggerElement(Element element)
		throws Exception {

		return getLoggerElementFromElement(element);
	}

	protected LoggerElement getFunctionExecuteLoggerElement(Element element) {
		return getLineGroupLoggerElement("function", element);
	}

	protected abstract LoggerElement getIfLoggerElement(Element element)
		throws Exception;

	protected abstract LoggerElement getLineContainerLoggerElement(
		Element element);

	protected LoggerElement getLineGroupLoggerElement(Element element) {
		return getLineGroupLoggerElement(null, element);
	}

	protected LoggerElement getLineGroupLoggerElement(
		String className, Element element) {

		_btnLinkCollapseId++;
		_btnLinkVarId++;

		poshiStackTrace.setCurrentElement(element);

		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("line-group");
		loggerElement.setName("li");

		if (Validator.isNotNull(className)) {
			loggerElement.addClassName(className);
		}

		loggerElement.addChildLoggerElement(
			getBtnContainerLoggerElement(element));
		loggerElement.addChildLoggerElement(
			getLineContainerLoggerElement(element));

		_loggerElements.put(
			poshiStackTrace.getSimpleStackTraceMessage(), loggerElement);

		return loggerElement;
	}

	protected String getLineItemText(String className, String text) {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName(className);
		loggerElement.setID(null);
		loggerElement.setName("span");
		loggerElement.setText(text);

		return loggerElement.toString();
	}

	protected LoggerElement getLineNumberItem(int lineNumber) {
		LoggerElement loggerElement = new LoggerElement();

		loggerElement.setClassName("line-number");
		loggerElement.setID(null);
		loggerElement.setName("div");
		loggerElement.setText(String.valueOf(lineNumber));

		return loggerElement;
	}

	protected final String getLineNumberItemText(int lineNumber) {
		LoggerElement loggerElement = getLineNumberItem(lineNumber);

		return loggerElement.toString();
	}

	protected abstract LoggerElement getLoggerElementFromElement(
			Element element)
		throws Exception;

	protected LoggerElement getMacroExecuteLoggerElement(
			Element executeElement, String macroType)
		throws Exception {

		LoggerElement loggerElement = getLineGroupLoggerElement(
			"macro", executeElement);

		String classCommandName = executeElement.attributeValue(macroType);

		poshiStackTrace.pushStackTrace(executeElement);

		loggerElement.addChildLoggerElement(
			_getMacroCommandLoggerElement(classCommandName));

		poshiStackTrace.popStackTrace();

		return loggerElement;
	}

	protected LoggerElement getMethodExecuteLoggerElement(
			Element executeElement)
		throws Exception {

		return getLineGroupLoggerElement("method", executeElement);
	}

	protected LoggerElement getReturnLoggerElement(Element element) {
		return getLineGroupLoggerElement("return", element);
	}

	protected LoggerElement getTakeScreenshotLoggerElement(Element element) {
		return getLineGroupLoggerElement("take-screenshot", element);
	}

	protected LoggerElement getTestCaseCommandLoggerElement(
			String namespacedClassCommandName)
		throws Exception {

		Element commandElement = PoshiContext.getTestCaseCommandElement(
			namespacedClassCommandName,
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName));

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		Element rootElement = PoshiContext.getTestCaseRootElement(
			className,
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName));

		return getChildContainerLoggerElement(commandElement, rootElement);
	}

	protected LoggerElement getTestCaseExecuteLoggerElement(
			Element executeElement)
		throws Exception {

		LoggerElement loggerElement = getLineGroupLoggerElement(
			"test-case", executeElement);

		String namespacedClassCommandName = executeElement.attributeValue(
			"test-case");

		poshiStackTrace.pushStackTrace(executeElement);

		loggerElement.addChildLoggerElement(
			getTestCaseCommandLoggerElement(namespacedClassCommandName));

		poshiStackTrace.popStackTrace();

		return loggerElement;
	}

	protected LoggerElement getVarLoggerElement(Element element) {
		return getLineGroupLoggerElement("var", element);
	}

	protected abstract LoggerElement getWhileLoggerElement(Element element)
		throws Exception;

	protected boolean isExecuteChildElementLogged(Element element) {
		List<Element> childElements = element.elements();

		if ((!childElements.isEmpty() && !isExecutingFunction(element) &&
			 !isExecutingMethod(element)) ||
			isExecutingMacro(element) || isExecutingTestCase(element)) {

			return true;
		}

		return false;
	}

	protected boolean isExecuting(Element element) {
		if (isExecutingFunction(element) || isExecutingMacro(element) ||
			isExecutingMethod(element) || isExecutingTestCase(element)) {

			return true;
		}

		return false;
	}

	protected boolean isExecutingFunction(Element element) {
		if (element.attributeValue("function") != null) {
			return true;
		}

		return false;
	}

	protected boolean isExecutingMacro(Element element) {
		if (element.attributeValue("macro") != null) {
			return true;
		}

		return false;
	}

	protected boolean isExecutingMethod(Element element) {
		if (element.attributeValue("method") != null) {
			return true;
		}

		return false;
	}

	protected boolean isExecutingTestCase(Element element) {
		if (element.attributeValue("test-case") != null) {
			return true;
		}

		return false;
	}

	protected void updateElementStatus(Element element, String status) {
		poshiStackTrace.setCurrentElement(element);

		String stackTrace = poshiStackTrace.getSimpleStackTraceMessage();

		if (stackTrace.contains(".function")) {
			return;
		}

		LoggerElement loggerElement = getSyntaxLoggerElement(stackTrace);

		loggerElement.setAttribute("data-status01", status);
	}

	protected PoshiStackTrace poshiStackTrace;

	private LoggerElement _getMacroCommandLoggerElement(
			String namespacedClassCommandName)
		throws Exception {

		String classCommandName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);
		String namespace = poshiStackTrace.getCurrentNamespace(
			namespacedClassCommandName);

		Element commandElement = PoshiContext.getMacroCommandElement(
			classCommandName, namespace);

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		Element rootElement = PoshiContext.getMacroRootElement(
			className, namespace);

		return getChildContainerLoggerElement(commandElement, rootElement);
	}

	private int _btnLinkCollapseId;
	private int _btnLinkVarId;
	private final Map<String, LoggerElement> _loggerElements = new HashMap<>();
	private LoggerElement _syntaxLogLoggerElement;

}
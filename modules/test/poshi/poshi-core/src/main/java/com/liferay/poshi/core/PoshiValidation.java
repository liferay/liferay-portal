/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core;

import com.liferay.poshi.core.elements.PoshiElement;
import com.liferay.poshi.core.elements.PoshiElementException;
import com.liferay.poshi.core.script.PoshiScriptParserUtil;
import com.liferay.poshi.core.selenium.LiferaySeleniumMethod;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.ListUtil;
import com.liferay.poshi.core.util.OSDetector;
import com.liferay.poshi.core.util.PropsUtil;
import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

/**
 * @author Karen Dang
 * @author Michael Hashimoto
 */
public class PoshiValidation {

	public static void clearExceptions() {
		_exceptions.clear();
	}

	public static Set<Exception> getExceptions() {
		return _exceptions;
	}

	public static void main(String[] args) throws Exception {
		PoshiContext.readFiles();

		validate();
	}

	public static void validate() throws Exception {
		System.out.println("Start poshi validation.");

		long start = System.currentTimeMillis();

		validateProperties();

		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		ExecutorService executorService = Executors.newFixedThreadPool(
			poshiProperties.poshiFileReadThreadPool);

		for (final String finalFilePath : PoshiContext.getFilePaths()) {
			executorService.execute(
				new Runnable() {

					@Override
					public void run() {
						String filePath = finalFilePath;

						if (OSDetector.isWindows()) {
							filePath = StringUtil.replace(filePath, "/", "\\");
						}

						String className =
							PoshiGetterUtil.getClassNameFromFilePath(filePath);
						String classType =
							PoshiGetterUtil.getClassTypeFromFilePath(filePath);
						String namespace =
							PoshiContext.getNamespaceFromFilePath(filePath);

						if (classType.equals("function")) {
							Element element =
								PoshiContext.getFunctionRootElement(
									className, namespace);

							validateFunctionFile((PoshiElement)element);
						}
						else if (classType.equals("macro")) {
							Element element = PoshiContext.getMacroRootElement(
								className, namespace);

							validateMacroFile((PoshiElement)element);
						}
						else if (classType.equals("path")) {
							Element element = PoshiContext.getPathRootElement(
								className, namespace);

							validatePathFile(element, filePath);
						}
						else if (classType.equals("test-case")) {
							Element element =
								PoshiContext.getTestCaseRootElement(
									className, namespace);

							validateTestCaseFile((PoshiElement)element);
						}
					}

				});
		}

		executorService.shutdown();

		if (!executorService.awaitTermination(2, TimeUnit.MINUTES)) {
			throw new TimeoutException(
				"Timed out while validating Poshi files");
		}

		if (!_exceptions.isEmpty()) {
			_throwExceptions();
		}

		long duration = System.currentTimeMillis() - start;

		System.out.println("Completed poshi validation in " + duration + "ms.");
	}

	public static void validate(String testName) throws Exception {
		validateProperties();

		validateTestName(testName);

		if (!_exceptions.isEmpty()) {
			_throwExceptions();
		}
	}

	protected static String getPrimaryAttributeName(
		PoshiElement poshiElement, List<String> primaryAttributeNames) {

		return getPrimaryAttributeName(
			poshiElement, null, primaryAttributeNames);
	}

	protected static String getPrimaryAttributeName(
		PoshiElement poshiElement, List<String> multiplePrimaryAttributeNames,
		List<String> primaryAttributeNames) {

		validateHasPrimaryAttributeName(
			poshiElement, multiplePrimaryAttributeNames, primaryAttributeNames);

		for (String primaryAttributeName : primaryAttributeNames) {
			if (Validator.isNotNull(
					poshiElement.attributeValue(primaryAttributeName))) {

				return primaryAttributeName;
			}
		}

		return null;
	}

	protected static void parseElements(PoshiElement poshiElement) {
		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		List<String> possiblePoshiElementNames = Arrays.asList(
			"break", "continue", "description", "echo", "execute", "fail",
			"for", "if", "property", "return", "take-screenshot", "task", "var",
			"while");

		String filePath = _getFilePath(poshiElement);

		if (Validator.isNotNull(filePath) && filePath.endsWith(".function")) {
			possiblePoshiElementNames = Arrays.asList("execute", "if", "var");
		}

		for (PoshiElement childPoshiElement : childPoshiElements) {
			String poshiElementName = childPoshiElement.getName();

			if (!possiblePoshiElementNames.contains(poshiElementName)) {
				_exceptions.add(
					new PoshiElementException(
						childPoshiElement, "Invalid ", poshiElementName,
						" element"));
			}

			if (poshiElementName.equals("description") ||
				poshiElementName.equals("echo") ||
				poshiElementName.equals("fail")) {

				validateMessageElement(childPoshiElement);
			}
			else if (poshiElementName.equals("execute")) {
				validateExecuteElement(childPoshiElement);
			}
			else if (poshiElementName.equals("for")) {
				validateForElement(childPoshiElement);
			}
			else if (poshiElementName.equals("if")) {
				validateIfElement(childPoshiElement);
			}
			else if (poshiElementName.equals("property")) {
				validatePropertyElement(childPoshiElement);
			}
			else if (poshiElementName.equals("return")) {
				validateCommandReturnElement(childPoshiElement);
			}
			else if (poshiElementName.equals("take-screenshot")) {
				validateTakeScreenshotElement(childPoshiElement);
			}
			else if (poshiElementName.equals("task")) {
				validateTaskElement(childPoshiElement);
			}
			else if (poshiElementName.equals("var")) {
				validateVarElement(childPoshiElement);
			}
			else if (poshiElementName.equals("while")) {
				validateWhileElement(childPoshiElement);
			}
		}
	}

	protected static void validateArgElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		List<String> attributes = Arrays.asList("line-number", "value");

		validatePossibleAttributeNames(poshiElement, attributes);
		validateRequiredAttributeNames(poshiElement, attributes, filePath);
	}

	protected static void validateAttributeValue(
		PoshiElement poshiElement, String attributeValue) {

		if (attributeValue.contains("\"")) {
			int escapedQuoteCount = StringUtils.countMatches(
				attributeValue, "\\\"");
			int quoteCount = StringUtils.countMatches(attributeValue, "\"");

			if ((escapedQuoteCount != quoteCount) ||
				!((escapedQuoteCount % 2) == 0)) {

				_exceptions.add(
					new PoshiElementException(
						poshiElement,
						"Unescaped quotes in parameter value: " +
							attributeValue));
			}
		}
	}

	protected static void validateCommandElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		List<String> possibleAttributeNames = Arrays.asList(
			"arguments", "line-number", "name", "return", "summary",
			"summary-ignore");

		validatePossibleAttributeNames(poshiElement, possibleAttributeNames);

		validateRequiredAttributeNames(
			poshiElement, Arrays.asList("name"), filePath);

		List<PoshiElement> returnPoshiElements = poshiElement.toPoshiElements(
			PoshiGetterUtil.getAllChildElements(poshiElement, "return"));

		List<PoshiElement> commandReturnPoshiElements = new ArrayList<>();

		for (PoshiElement returnPoshiElement : returnPoshiElements) {
			Element parentElement = returnPoshiElement.getParent();

			if (!Objects.equals(parentElement.getName(), "execute")) {
				commandReturnPoshiElements.add(returnPoshiElement);
			}
		}

		String returnName = poshiElement.attributeValue("return");

		if (Validator.isNull(returnName)) {
			for (PoshiElement commandReturnPoshiElement :
					commandReturnPoshiElements) {

				String returnVariableName =
					commandReturnPoshiElement.attributeValue("name");
				String returnVariableValue =
					commandReturnPoshiElement.attributeValue("value");

				if (Validator.isNotNull(returnVariableName) &&
					Validator.isNotNull(returnVariableValue)) {

					_exceptions.add(
						new PoshiElementException(
							commandReturnPoshiElement,
							"No return variables were stated in command ",
							"declaration, but found return name-value ",
							"mapping"));
				}
			}
		}
		else {
			if (commandReturnPoshiElements.isEmpty()) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement,
						"Return variable was stated, but no returns were ",
						"found"));
			}
			else {
				for (PoshiElement commandReturnPoshiElement :
						commandReturnPoshiElements) {

					String returnVariableName =
						commandReturnPoshiElement.attributeValue("name");

					if (Validator.isNull(returnVariableName)) {
						_exceptions.add(
							new PoshiElementException(
								commandReturnPoshiElement,
								"Return variable was stated as '", returnName,
								"', but no 'name' attribute was found"));

						continue;
					}

					if (returnName.equals(returnVariableName)) {
						continue;
					}

					_exceptions.add(
						new PoshiElementException(
							commandReturnPoshiElement, "'", returnVariableName,
							"' not listed as a return variable"));
				}
			}
		}
	}

	protected static void validateCommandReturnElement(
		PoshiElement poshiElement) {

		String filePath = _getFilePath(poshiElement);

		validateHasNoChildElements(poshiElement);
		validatePossibleAttributeNames(
			poshiElement, Arrays.asList("line-number", "name", "value"));
		validateRequiredAttributeNames(
			poshiElement, Arrays.asList("line-number", "value"), filePath);
	}

	protected static void validateConditionElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		String elementName = poshiElement.getName();

		if (elementName.equals("contains")) {
			validateAttributeValue(
				poshiElement, poshiElement.attributeValue("string"));
			validateAttributeValue(
				poshiElement, poshiElement.attributeValue("substring"));
		}

		if (elementName.equals("and") || elementName.equals("or")) {
			validateHasChildElements(poshiElement, filePath);
			validateHasNoAttributes(poshiElement);

			List<PoshiElement> childPoshiElements =
				poshiElement.toPoshiElements(poshiElement.elements());

			Element parentElement = poshiElement.getParent();

			String parentElementName = parentElement.getName();

			int childElementCount = 2;

			if (parentElementName.equals("while")) {
				childElementCount = 1;
			}

			if (childPoshiElements.size() < childElementCount) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Too few child elements"));
			}

			for (PoshiElement childPoshiElement : childPoshiElements) {
				validateConditionElement(childPoshiElement);
			}
		}
		else if (elementName.equals("condition")) {
			List<String> primaryAttributeNames = Arrays.asList(
				"function", "selenium");

			String primaryAttributeName = getPrimaryAttributeName(
				poshiElement, primaryAttributeNames);

			if (Validator.isNull(primaryAttributeName)) {
				return;
			}

			if (primaryAttributeName.equals("function")) {
				List<String> possibleAttributeNames = Arrays.asList(
					"function", "line-number", "locator1", "value1");

				validatePossibleAttributeNames(
					poshiElement, possibleAttributeNames);
			}
			else if (primaryAttributeName.equals("selenium")) {
				List<String> possibleAttributeNames = Arrays.asList(
					"argument1", "argument2", "line-number", "selenium");

				validatePossibleAttributeNames(
					poshiElement, possibleAttributeNames);
			}

			List<PoshiElement> varPoshiElements = poshiElement.toPoshiElements(
				poshiElement.elements("var"));

			for (PoshiElement varPoshiElement : varPoshiElements) {
				validateVarElement(varPoshiElement);
			}
		}
		else if (elementName.equals("contains")) {
			List<String> attributeNames = Arrays.asList(
				"line-number", "string", "substring");

			validateHasNoChildElements(poshiElement);
			validatePossibleAttributeNames(poshiElement, attributeNames);
			validateRequiredAttributeNames(
				poshiElement, attributeNames, filePath);
		}
		else if (elementName.equals("equals")) {
			List<String> attributeNames = Arrays.asList(
				"arg1", "arg2", "line-number");

			validateHasNoChildElements(poshiElement);
			validatePossibleAttributeNames(poshiElement, attributeNames);
			validateRequiredAttributeNames(
				poshiElement, attributeNames, filePath);
		}
		else if (elementName.equals("isset")) {
			List<String> attributeNames = Arrays.asList("line-number", "var");

			validateHasNoChildElements(poshiElement);
			validatePossibleAttributeNames(poshiElement, attributeNames);
			validateRequiredAttributeNames(
				poshiElement, attributeNames, filePath);
		}
		else if (elementName.equals("not")) {
			validateHasChildElements(poshiElement, filePath);
			validateHasNoAttributes(poshiElement);
			validateNumberOfChildElements(poshiElement, 1, filePath);

			List<PoshiElement> childPoshiElements =
				poshiElement.toPoshiElements(poshiElement.elements());

			validateConditionElement(childPoshiElements.get(0));
		}
	}

	protected static void validateDefinitionElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		String elementName = poshiElement.getName();

		if (!Objects.equals(elementName, "definition")) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Root element name must be definition"));
		}

		String classType = PoshiGetterUtil.getClassTypeFromFilePath(filePath);

		if (classType.equals("function")) {
			List<String> possibleAttributeNames = Arrays.asList(
				"default", "line-number", "override", "summary",
				"summary-ignore");

			validatePossibleAttributeNames(
				poshiElement, possibleAttributeNames);

			validateRequiredAttributeNames(
				poshiElement, Arrays.asList("default"), filePath);
		}
		else if (classType.equals("macro")) {
			validateHasNoAttributes(poshiElement);
		}
		else if (classType.equals("testcase")) {
			List<String> possibleAttributeNames = Arrays.asList(
				"extends", "ignore", "ignore-command-names", "line-number");

			validatePossibleAttributeNames(
				poshiElement, possibleAttributeNames);
		}
	}

	protected static void validateDeprecatedFunction(
		PoshiElement poshiElement, String functionName) {

		URL filePathURL = poshiElement.getFilePathURL();

		String filePath = filePathURL.getFile();

		String deprecatedMethodName = _deprecatedMethodNames.get(functionName);

		if (deprecatedMethodName != null) {
			String className = PoshiGetterUtil.getClassNameFromFilePath(
				filePath);

			_deprecatedFunctionNames.add(className + "#" + functionName);

			_logger.warning(
				"Deprecated method \"selenium." + functionName +
					"\" should be replaced with " + deprecatedMethodName +
						" at:\n" + filePath + ":" +
							poshiElement.getPoshiScriptLineNumber());
		}

		if (_deprecatedFunctionNames.contains(functionName)) {
			_logger.warning(
				"Use of function \"" + functionName +
					"\" contains deprecated selenium method at:\n" + filePath +
						":" + poshiElement.getPoshiScriptLineNumber());
		}
	}

	protected static void validateElementName(
		PoshiElement poshiElement, List<String> possibleElementNames) {

		if (!possibleElementNames.contains(poshiElement.getName())) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Missing ", possibleElementNames,
					" element"));
		}
	}

	protected static void validateElseElement(PoshiElement poshiElement) {
		List<PoshiElement> elsePoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements("else"));

		if (elsePoshiElements.size() > 1) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Too many else elements"));
		}

		if (!elsePoshiElements.isEmpty()) {
			PoshiElement elseElement = elsePoshiElements.get(0);

			parseElements(elseElement);
		}
	}

	protected static void validateElseIfElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		validateHasChildElements(poshiElement, filePath);

		validateHasNoAttributes(poshiElement);
		validateNumberOfChildElements(poshiElement, 2, filePath);
		validateThenElement(poshiElement);

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		List<String> conditionTags = Arrays.asList(
			"and", "condition", "contains", "equals", "isset", "not", "or");

		PoshiElement conditionElement = childPoshiElements.get(0);

		String conditionElementName = conditionElement.getName();

		if (conditionTags.contains(conditionElementName)) {
			validateConditionElement(conditionElement);
		}
		else {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Invalid ", conditionElementName,
					" element"));
		}

		PoshiElement thenElement = (PoshiElement)poshiElement.element("then");

		validateHasChildElements(thenElement, filePath);
		validateHasNoAttributes(thenElement);

		parseElements(thenElement);
	}

	protected static void validateExecuteElement(PoshiElement poshiElement) {
		List<String> primaryAttributeNames = Arrays.asList(
			"function", "macro", "method", "selenium", "test-case");

		String filePath = _getFilePath(poshiElement);

		if (filePath.endsWith(".function")) {
			primaryAttributeNames = Arrays.asList("function", "selenium");
		}
		else if (filePath.endsWith(".macro")) {
			primaryAttributeNames = Arrays.asList(
				"function", "macro", "method");
		}
		else if (filePath.endsWith(".testcase")) {
			primaryAttributeNames = Arrays.asList(
				"function", "macro", "method", "test-case");
		}

		String primaryAttributeName = getPrimaryAttributeName(
			poshiElement, primaryAttributeNames);

		if (primaryAttributeName == null) {
			return;
		}

		if (primaryAttributeName.equals("function")) {
			List<String> possibleAttributeNames = Arrays.asList(
				"function", "line-number", "locator1", "locator2", "value1",
				"value2", "value3");

			String function = poshiElement.attributeValue("function");

			if (Validator.isNotNull(function) &&
				!filePath.endsWith(".function")) {

				validateDeprecatedFunction(poshiElement, function);
			}

			validatePossibleAttributeNames(
				poshiElement, possibleAttributeNames);

			validateFunctionContext(poshiElement);
		}
		else if (primaryAttributeName.equals("macro")) {
			List<String> possibleAttributeNames = Arrays.asList(
				"line-number", "macro");

			validatePossibleAttributeNames(
				poshiElement, possibleAttributeNames);

			validateMacroContext(poshiElement, "macro");
		}
		else if (primaryAttributeName.equals("method")) {
			validateMethodExecuteElement(poshiElement);
		}
		else if (primaryAttributeName.equals("selenium")) {
			List<String> possibleAttributeNames = Arrays.asList(
				"argument1", "argument2", "argument3", "line-number",
				"selenium");

			String seleniumAttributeValue = poshiElement.attributeValue(
				"selenium");

			LiferaySeleniumMethod liferaySeleniumMethod =
				PoshiContext.getLiferaySeleniumMethod(seleniumAttributeValue);

			if (liferaySeleniumMethod == null) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Nonexistent Selenium method"));
			}

			validateDeprecatedFunction(poshiElement, seleniumAttributeValue);

			validatePossibleAttributeNames(
				poshiElement, possibleAttributeNames);
		}
		else if (primaryAttributeName.equals("test-case")) {
			List<String> possibleAttributeNames = Arrays.asList(
				"line-number", "test-case");

			validatePossibleAttributeNames(
				poshiElement, possibleAttributeNames);

			validateTestCaseContext(poshiElement);
		}

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		if (!childPoshiElements.isEmpty()) {
			primaryAttributeNames = Arrays.asList(
				"function", "macro", "method", "selenium", "test-case");

			validateHasPrimaryAttributeName(
				poshiElement, primaryAttributeNames);

			List<String> possibleChildElementNames = Arrays.asList(
				"arg", "return", "var");

			for (PoshiElement childPoshiElement : childPoshiElements) {
				String childPoshiElementName = childPoshiElement.getName();

				if (!possibleChildElementNames.contains(
						childPoshiElementName)) {

					_exceptions.add(
						new PoshiElementException(
							childPoshiElement, "Invalid child element"));
				}
			}

			List<PoshiElement> argPoshiElements = poshiElement.toPoshiElements(
				poshiElement.elements("arg"));

			for (PoshiElement argPoshiElement : argPoshiElements) {
				validateArgElement(argPoshiElement);
			}

			List<PoshiElement> returnPoshiElements =
				poshiElement.toPoshiElements(poshiElement.elements("return"));

			if ((returnPoshiElements.size() > 1) &&
				primaryAttributeName.equals("macro")) {

				_exceptions.add(
					new PoshiElementException(
						poshiElement,
						"Only 1 child element 'return' is allowed"));
			}

			PoshiElement returnPoshiElement =
				(PoshiElement)poshiElement.element("return");

			if (returnPoshiElement != null) {
				if (primaryAttributeName.equals("macro")) {
					validateExecuteReturnMacroElement(returnPoshiElement);
				}
				else if (primaryAttributeName.equals("method")) {
					validateExecuteReturnMethodElement(returnPoshiElement);
				}
			}

			List<PoshiElement> varPoshiElements = poshiElement.toPoshiElements(
				poshiElement.elements("var"));
			List<String> varNames = new ArrayList<>();

			for (PoshiElement varPoshiElement : varPoshiElements) {
				validateVarElement(varPoshiElement);

				String varName = varPoshiElement.attributeValue("name");

				if (varNames.contains(varName)) {
					_exceptions.add(
						new PoshiElementException(
							poshiElement,
							"Duplicate variable name: " + varName));
				}

				varNames.add(varName);
			}
		}
	}

	protected static void validateExecuteReturnMacroElement(
		PoshiElement poshiElement) {

		List<String> attributeNames = Arrays.asList("line-number", "name");

		validateHasNoChildElements(poshiElement);
		validatePossibleAttributeNames(poshiElement, attributeNames);
		validateRequiredAttributeNames(
			poshiElement, attributeNames, _getFilePath(poshiElement));
	}

	protected static void validateExecuteReturnMethodElement(
		PoshiElement poshiElement) {

		List<String> attributeNames = Arrays.asList("line-number", "name");

		validateHasNoChildElements(poshiElement);
		validatePossibleAttributeNames(poshiElement, attributeNames);
		validateRequiredAttributeNames(
			poshiElement, attributeNames, _getFilePath(poshiElement));
	}

	protected static void validateForElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		validateHasChildElements(poshiElement, filePath);

		List<String> possibleAttributeNames = Arrays.asList(
			"line-number", "list", "param", "table");

		validatePossibleAttributeNames(poshiElement, possibleAttributeNames);

		List<String> requiredAttributeNames = Arrays.asList(
			"line-number", "param");

		validateRequiredAttributeNames(
			poshiElement, requiredAttributeNames, filePath);

		parseElements(poshiElement);
	}

	protected static void validateFunctionContext(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		String function = poshiElement.attributeValue("function");

		validateNamespacedClassCommandName(poshiElement, function, "function");

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				function);

		String namespace = PoshiContext.getNamespaceFromFilePath(filePath);

		int locatorCount = PoshiContext.getFunctionLocatorCount(
			className, namespace);

		for (int i = 0; i < locatorCount; i++) {
			String locator = poshiElement.attributeValue("locator" + (i + 1));

			if (locator != null) {
				Matcher matcher = _pattern.matcher(locator);

				if (locator.startsWith("css=") || !locator.contains("#") ||
					matcher.find()) {

					continue;
				}

				String pathName =
					PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
						locator);

				String defaultNamespace = PoshiContext.getDefaultNamespace();

				if (!PoshiContext.isRootElement("path", pathName, namespace) &&
					!PoshiContext.isRootElement(
						"path", pathName, defaultNamespace)) {

					_exceptions.add(
						new PoshiElementException(
							poshiElement, "Invalid path name ", pathName));
				}
				else if (!PoshiContext.isPathLocator(locator, namespace) &&
						 !PoshiContext.isPathLocator(
							 locator, defaultNamespace)) {

					_exceptions.add(
						new PoshiElementException(
							poshiElement, "Invalid path locator ", locator));
				}
			}
		}
	}

	protected static void validateFunctionFile(PoshiElement poshiElement) {
		validateDefinitionElement(poshiElement);

		String filePath = _getFilePath(poshiElement);

		validateHasChildElements(poshiElement, filePath);
		validateRequiredChildElementNames(
			poshiElement, Arrays.asList("command"), filePath);

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		for (PoshiElement childPoshiElement : childPoshiElements) {
			validateCommandElement(childPoshiElement);
			validateHasChildElements(childPoshiElement, filePath);

			parseElements(childPoshiElement);
		}
	}

	protected static void validateHasChildElements(
		Element element, String filePath) {

		List<Element> childElements = element.elements();

		if (childElements.isEmpty()) {
			_addException(element, "Missing child elements", filePath);
		}
	}

	protected static void validateHasMultiplePrimaryAttributeNames(
		PoshiElement poshiElement, List<String> attributeNames,
		List<String> multiplePrimaryAttributeNames) {

		if (!multiplePrimaryAttributeNames.equals(attributeNames)) {
			_exceptions.add(
				new PoshiElementException(poshiElement, "Too many attributes"));
		}
	}

	protected static void validateHasNoAttributes(PoshiElement poshiElement) {
		List<Attribute> attributes = poshiElement.attributes();

		if (!attributes.isEmpty()) {
			for (Attribute attribute : attributes) {
				String attributeName = attribute.getName();

				if (attributeName.equals("line-number")) {
					continue;
				}

				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Invalid ", attributeName, " attribute"));
			}
		}
	}

	protected static void validateHasNoChildElements(
		PoshiElement poshiElement) {

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		if (!childPoshiElements.isEmpty()) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Invalid child elements"));
		}
	}

	protected static void validateHasPrimaryAttributeName(
		PoshiElement poshiElement, List<String> primaryAttributeNames) {

		validateHasPrimaryAttributeName(
			poshiElement, null, primaryAttributeNames);
	}

	protected static void validateHasPrimaryAttributeName(
		PoshiElement poshiElement, List<String> multiplePrimaryAttributeNames,
		List<String> primaryAttributeNames) {

		List<String> attributeNames = new ArrayList<>();

		for (String primaryAttributeName : primaryAttributeNames) {
			if (Validator.isNotNull(
					poshiElement.attributeValue(primaryAttributeName))) {

				attributeNames.add(primaryAttributeName);
			}
		}

		if (attributeNames.isEmpty()) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Invalid or missing attribute"));
		}
		else if (attributeNames.size() > 1) {
			if (multiplePrimaryAttributeNames == null) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Too many attributes"));
			}
			else {
				validateHasMultiplePrimaryAttributeNames(
					poshiElement, attributeNames,
					multiplePrimaryAttributeNames);
			}
		}
	}

	protected static void validateHasRequiredPropertyElements(
		PoshiElement poshiElement) {

		List<String> requiredPropertyNames = new ArrayList<>(
			PoshiContext.getRequiredPoshiPropertyNames());

		List<PoshiElement> propertyPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements("property"));

		for (PoshiElement propertyPoshiElement : propertyPoshiElements) {
			validatePropertyElement(propertyPoshiElement);

			String propertyName = propertyPoshiElement.attributeValue("name");

			requiredPropertyNames.remove(propertyName);
		}

		if (requiredPropertyNames.isEmpty()) {
			return;
		}

		String filePath = _getFilePath(poshiElement);

		String namespace = PoshiContext.getNamespaceFromFilePath(filePath);

		String className = PoshiGetterUtil.getClassNameFromFilePath(filePath);

		String commandName = poshiElement.attributeValue("name");

		String namespacedClassCommandName =
			namespace + "." + className + "#" + commandName;

		Properties properties =
			PoshiContext.getNamespacedClassCommandNameProperties(
				namespacedClassCommandName);

		for (String requiredPropertyName : requiredPropertyNames) {
			if (!properties.containsKey(requiredPropertyName)) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement,
						className + "#" + commandName +
							" is missing required properties ",
						requiredPropertyNames.toString()));
			}
		}
	}

	protected static void validateIfElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		validateHasChildElements(poshiElement, filePath);

		validateHasNoAttributes(poshiElement);

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		List<String> conditionTags = Arrays.asList(
			"and", "condition", "contains", "equals", "isset", "not", "or");

		validateElseElement(poshiElement);
		validateThenElement(poshiElement);

		for (int i = 0; i < childPoshiElements.size(); i++) {
			PoshiElement childPoshiElement = childPoshiElements.get(i);

			String childPoshiElementName = childPoshiElement.getName();

			if (i == 0) {
				if (conditionTags.contains(childPoshiElementName)) {
					validateConditionElement(childPoshiElement);
				}
				else {
					_exceptions.add(
						new PoshiElementException(
							poshiElement,
							"Missing or invalid if condition element"));
				}
			}
			else if (childPoshiElementName.equals("else")) {
				validateHasChildElements(childPoshiElement, filePath);
				validateHasNoAttributes(childPoshiElement);

				parseElements(childPoshiElement);
			}
			else if (childPoshiElementName.equals("elseif")) {
				validateHasChildElements(childPoshiElement, filePath);
				validateHasNoAttributes(childPoshiElement);

				validateElseIfElement(childPoshiElement);
			}
			else if (childPoshiElementName.equals("then")) {
				validateHasChildElements(childPoshiElement, filePath);
				validateHasNoAttributes(childPoshiElement);

				parseElements(childPoshiElement);
			}
			else {
				_exceptions.add(
					new PoshiElementException(
						childPoshiElement, "Invalid ", childPoshiElementName,
						" element"));
			}
		}
	}

	protected static void validateMacroArguments(
		PoshiElement poshiElement, String macroName, String namespace) {

		Element commandElement = PoshiContext.getMacroCommandElement(
			macroName, namespace);

		String argumentsValue = commandElement.attributeValue("arguments");

		if (Validator.isNotNull(argumentsValue)) {
			List<String> arguments = ListUtil.newListFromString(argumentsValue);

			List<PoshiElement> varPoshiElements = poshiElement.toPoshiElements(
				poshiElement.elements("var"));
			List<String> variableNames = new ArrayList<>();

			for (PoshiElement varPoshiElement : varPoshiElements) {
				variableNames.add(varPoshiElement.attributeValue("name"));
			}

			for (String argument : arguments) {
				if (argument.matches("[\\w]+[\\s]*=[\\s]*null")) {
					continue;
				}

				if (!variableNames.contains(argument)) {
					_exceptions.add(
						new PoshiElementException(
							poshiElement, "Macro missing required variable ",
							argument));
				}
			}
		}
	}

	protected static void validateMacroCommandName(PoshiElement poshiElement) {
		String attributeName = poshiElement.attributeValue("name");

		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		if (poshiProperties.generateCommandSignature) {
			validateRequiredAttributeNames(
				poshiElement, Arrays.asList("summary"), poshiElement.getPath());
		}

		if (attributeName.contains("Url")) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Invalid macro command name: ", attributeName,
					". Use 'URL' instead of 'Url'"));
		}
	}

	protected static void validateMacroContext(
		PoshiElement poshiElement, String macroType) {

		String macroName = poshiElement.attributeValue(macroType);

		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				macroName);

		if (Validator.isNull(namespace)) {
			namespace = PoshiContext.getNamespaceFromFilePath(
				_getFilePath(poshiElement));
		}

		if (PoshiContext.isCommandElement("macro", macroName, namespace)) {
			validateMacroArguments(poshiElement, macroName, namespace);
		}

		validateNamespacedClassCommandName(poshiElement, macroName, "macro");
	}

	protected static void validateMacroFile(PoshiElement poshiElement) {
		validateDefinitionElement(poshiElement);

		String filePath = _getFilePath(poshiElement);

		validateHasChildElements(poshiElement, filePath);
		validateRequiredChildElementName(poshiElement, "command", filePath);

		List<PoshiElement> childElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		List<String> possibleTagElementNames = Arrays.asList("command", "var");

		for (PoshiElement childPoshiElement : childElements) {
			String childPoshiElementName = childPoshiElement.getName();

			if (!possibleTagElementNames.contains(childPoshiElementName)) {
				_exceptions.add(
					new PoshiElementException(
						childPoshiElement, "Invalid ", childPoshiElementName,
						" element"));
			}

			if (childPoshiElementName.equals("command")) {
				validateCommandElement(childPoshiElement);
				validateHasChildElements(childPoshiElement, filePath);
				validateMacroCommandName(childPoshiElement);

				parseElements(childPoshiElement);
			}
			else if (childPoshiElementName.equals("var")) {
				validateVarElement(childPoshiElement);
			}
		}
	}

	protected static void validateMessageElement(PoshiElement poshiElement) {
		List<String> possibleAttributeNames = Arrays.asList(
			"line-number", "message");

		validateHasNoChildElements(poshiElement);
		validatePossibleAttributeNames(poshiElement, possibleAttributeNames);

		if ((poshiElement.attributeValue("message") == null) &&
			Validator.isNull(poshiElement.getText())) {

			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Missing message attribute"));
		}
	}

	protected static void validateMethodExecuteElement(
		PoshiElement poshiElement) {

		String className = poshiElement.attributeValue("class");

		Class<?> clazz = null;

		String fullClassName = null;

		if (className.matches("[\\w]*")) {
			for (String packageName : UTIL_PACKAGE_NAMES) {
				try {
					clazz = Class.forName(packageName + "." + className);

					fullClassName = packageName + "." + className;

					break;
				}
				catch (Exception exception) {
				}
			}
		}
		else {
			try {
				clazz = Class.forName(className);

				fullClassName = className;
			}
			catch (Exception exception) {
			}
		}

		if (fullClassName == null) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Unable to find class ", className));

			return;
		}

		try {
			validateUtilityClassName(poshiElement, fullClassName);
		}
		catch (PoshiElementException poshiElementException) {
			_exceptions.add(poshiElementException);

			return;
		}

		String methodName = poshiElement.attributeValue("method");

		List<Method> possibleMethods = new ArrayList<>();

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		List<Method> completeMethods = Arrays.asList(clazz.getMethods());

		for (Method possibleMethod : completeMethods) {
			String possibleMethodName = possibleMethod.getName();

			Class<?>[] methodParameterTypes =
				possibleMethod.getParameterTypes();

			if (methodName.equals(possibleMethodName) &&
				(methodParameterTypes.length == childPoshiElements.size())) {

				possibleMethods.add(possibleMethod);
			}
		}

		for (PoshiElement childPoshiElement : childPoshiElements) {
			String nameAttributeValue = childPoshiElement.attributeValue(
				"name");

			if (Validator.isNotNull(nameAttributeValue)) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Parameter name ", nameAttributeValue,
						" is not required"));
			}
		}

		if (possibleMethods.isEmpty()) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Unable to find method ", fullClassName, "#",
					methodName));
		}
	}

	protected static void validateNamespacedClassCommandName(
		PoshiElement poshiElement, String namespacedClassCommandName,
		String classType) {

		String classCommandName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		String defaultNamespace = PoshiContext.getDefaultNamespace();

		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName);

		if (Validator.isNull(namespace)) {
			namespace = PoshiContext.getNamespaceFromFilePath(
				_getFilePath(poshiElement));
		}

		if (!PoshiContext.isRootElement(classType, className, namespace) &&
			!PoshiContext.isRootElement(
				classType, className, defaultNamespace)) {

			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Invalid ", classType, " class ", className));
		}

		if (!PoshiContext.isCommandElement(
				classType, classCommandName, namespace) &&
			!PoshiContext.isCommandElement(
				classType, classCommandName, defaultNamespace)) {

			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Invalid ", classType, " command ",
					namespacedClassCommandName));
		}
	}

	protected static void validateNumberOfChildElements(
		Element element, int number, String filePath) {

		List<Element> childElements = element.elements();

		if (childElements.isEmpty()) {
			_addException(element, "Missing child elements", filePath);
		}
		else if (childElements.size() > number) {
			_addException(element, "Too many child elements", filePath);
		}
		else if (childElements.size() < number) {
			_addException(element, "Too few child elements", filePath);
		}
	}

	protected static void validateOffElement(PoshiElement poshiElement) {
		List<PoshiElement> offPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements("off"));

		if (offPoshiElements.size() > 1) {
			_exceptions.add(
				new ValidationException(poshiElement, "Too many off elements"));
		}

		if (!offPoshiElements.isEmpty()) {
			PoshiElement offElement = offPoshiElements.get(0);

			validateHasChildElements(offElement, _getFilePath(poshiElement));
			validateHasNoAttributes(offElement);

			parseElements(offElement);
		}
	}

	protected static void validateOnElement(PoshiElement poshiElement) {
		List<PoshiElement> onPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements("off"));

		if (onPoshiElements.size() > 1) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Too many off elements"));
		}

		if (!onPoshiElements.isEmpty()) {
			PoshiElement onElement = onPoshiElements.get(0);

			validateHasChildElements(onElement, _getFilePath(poshiElement));
			validateHasNoAttributes(onElement);

			parseElements(onElement);
		}
	}

	protected static void validatePathFile(Element element, String filePath) {
		String className = PoshiGetterUtil.getClassNameFromFilePath(filePath);

		String rootElementName = element.getName();

		if (!Objects.equals(rootElementName, "html")) {
			_exceptions.add(
				new ValidationException(
					element, "Invalid ", rootElementName, " element\n",
					filePath));
		}

		validateHasChildElements(element, filePath);
		validateNumberOfChildElements(element, 2, filePath);
		validateRequiredChildElementNames(
			element, Arrays.asList("body", "head"), filePath);

		Element bodyElement = element.element("body");

		validateHasChildElements(bodyElement, filePath);
		validateNumberOfChildElements(bodyElement, 1, filePath);
		validateRequiredChildElementName(bodyElement, "table", filePath);

		Element tableElement = bodyElement.element("table");

		List<String> requiredTableAttributeNames = Arrays.asList(
			"border", "cellpadding", "cellspacing", "line-number");

		validateHasChildElements(tableElement, filePath);
		validateNumberOfChildElements(tableElement, 2, filePath);
		validateRequiredAttributeNames(
			tableElement, requiredTableAttributeNames, filePath);
		validateRequiredChildElementNames(
			tableElement, Arrays.asList("tbody", "thead"), filePath);

		Element tBodyElement = tableElement.element("tbody");

		List<Element> trElements = tBodyElement.elements();

		if (trElements != null) {
			for (Element trElement : trElements) {
				validateHasChildElements(trElement, filePath);
				validateNumberOfChildElements(trElement, 3, filePath);
				validateRequiredChildElementName(trElement, "td", filePath);

				List<Element> tdElements = trElement.elements();

				Element locatorElement = tdElements.get(1);

				String locator = locatorElement.getText();

				Element locatorKeyElement = tdElements.get(0);

				String locatorKey = locatorKeyElement.getText();

				if (Validator.isNull(locator) != Validator.isNull(locatorKey)) {
					_exceptions.add(
						new ValidationException(
							trElement, "Missing locator\n", filePath));
				}

				if (locatorKey.equals("EXTEND_ACTION_PATH")) {
					String namespace = PoshiContext.getNamespaceFromFilePath(
						filePath);

					Element pathRootElement = PoshiContext.getPathRootElement(
						locator, namespace);

					if (pathRootElement == null) {
						_exceptions.add(
							new ValidationException(
								trElement, "Nonexistent parent path file\n",
								filePath));
					}
				}
			}
		}

		Element theadElement = tableElement.element("thead");

		validateHasChildElements(theadElement, filePath);
		validateNumberOfChildElements(theadElement, 1, filePath);
		validateRequiredChildElementName(theadElement, "tr", filePath);

		Element trElement = theadElement.element("tr");

		validateHasChildElements(trElement, filePath);
		validateNumberOfChildElements(trElement, 1, filePath);
		validateRequiredChildElementName(trElement, "td", filePath);

		Element tdElement = trElement.element("td");

		validateRequiredAttributeNames(
			tdElement, Arrays.asList("colspan", "rowspan"), filePath);

		String theadClassName = tdElement.getText();

		if (Validator.isNull(theadClassName)) {
			_exceptions.add(
				new ValidationException(
					trElement, "Missing thead class name\n", filePath));
		}
		else if (!Objects.equals(theadClassName, className)) {
			_exceptions.add(
				new ValidationException(
					trElement, "Thead class name does not match file name\n",
					filePath));
		}

		Element headElement = element.element("head");

		validateHasChildElements(headElement, filePath);
		validateNumberOfChildElements(headElement, 1, filePath);
		validateRequiredChildElementName(headElement, "title", filePath);

		Element titleElement = headElement.element("title");

		if (!Objects.equals(titleElement.getText(), className)) {
			_exceptions.add(
				new ValidationException(
					titleElement, "File name and title are different\n",
					filePath));
		}
	}

	protected static void validatePossibleAttributeNames(
		PoshiElement poshiElement, List<String> possibleAttributeNames) {

		List<Attribute> attributes = poshiElement.attributes();

		for (Attribute attribute : attributes) {
			String attributeName = attribute.getName();

			if (!possibleAttributeNames.contains(attributeName)) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Invalid ", attributeName, " attribute"));
			}
		}
	}

	protected static void validatePossiblePropertyValues(
		PoshiElement propertyPoshiElement) {

		String propertyName = propertyPoshiElement.attributeValue("name");

		String testCaseAvailablePropertyValues = PropsUtil.get(
			"test.case.available.property.values[" + propertyName + "]");

		if (Validator.isNotNull(testCaseAvailablePropertyValues)) {
			List<String> possiblePropertyValues = Arrays.asList(
				StringUtil.split(testCaseAvailablePropertyValues));

			List<String> propertyValues = Arrays.asList(
				StringUtil.split(propertyPoshiElement.attributeValue("value")));

			for (String propertyValue : propertyValues) {
				if (!possiblePropertyValues.contains(propertyValue.trim())) {
					_exceptions.add(
						new PoshiElementException(
							propertyPoshiElement, "Invalid property value '",
							propertyValue.trim(), "' for property name '",
							propertyName.trim()));
				}
			}
		}
	}

	protected static void validateProperties() {
		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		if (Validator.isNull(poshiProperties.testCaseAvailablePropertyNames)) {
			return;
		}

		for (String testCaseAvailablePropertyName :
				StringUtil.split(
					poshiProperties.testCaseAvailablePropertyNames)) {

			String testCaseAvailablePropertyValues = PropsUtil.get(
				"test.case.available.property.values[" +
					testCaseAvailablePropertyName + "]");

			if (Validator.isNotNull(testCaseAvailablePropertyValues)) {
				List<String> uniquePropertyValues = new ArrayList<>();

				for (String propertyValue :
						StringUtil.split(testCaseAvailablePropertyValues)) {

					if (uniquePropertyValues.contains(propertyValue)) {
						_exceptions.add(
							new ValidationException(
								"Duplicate property value " + propertyValue +
									" in property name " +
										testCaseAvailablePropertyName));

						continue;
					}

					uniquePropertyValues.add(propertyValue);
				}
			}
		}
	}

	protected static void validatePropertyElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		List<String> attributeNames = new ArrayList<>(
			Arrays.asList("line-number", "name"));

		if (Validator.isNotNull(poshiElement.attributeValue("value"))) {
			attributeNames.add("value");
		}
		else {
			boolean hasValue = false;

			for (Node node : Dom4JUtil.toNodeList(poshiElement.content())) {
				if (node instanceof CDATA || node instanceof Text) {
					hasValue = true;
				}
			}

			if (!hasValue) {
				_addException(
					poshiElement,
					poshiElement.attributeValue("name") + " has no value",
					filePath);
			}
		}

		validateHasNoChildElements(poshiElement);
		validatePossibleAttributeNames(poshiElement, attributeNames);
		validateRequiredAttributeNames(poshiElement, attributeNames, filePath);
		validatePossiblePropertyValues(poshiElement);
	}

	protected static void validateRequiredAttributeNames(
		Element element, List<String> requiredAttributeNames, String filePath) {

		for (String requiredAttributeName : requiredAttributeNames) {
			if (requiredAttributeName.equals("line-number") &&
				(element instanceof PoshiElement)) {

				continue;
			}

			if (element.attributeValue(requiredAttributeName) == null) {
				_addException(
					element, "Missing " + requiredAttributeName + " attribute",
					filePath);
			}
		}
	}

	protected static void validateRequiredChildElementName(
		Element element, String requiredElementName, String filePath) {

		boolean found = false;

		List<Element> childElements = element.elements();

		for (Element childElement : childElements) {
			if (Objects.equals(childElement.getName(), requiredElementName)) {
				found = true;

				break;
			}
		}

		if (!found) {
			_addException(
				element,
				"Missing required " + requiredElementName + " child element",
				filePath);
		}
	}

	protected static void validateRequiredChildElementNames(
		Element element, List<String> requiredElementNames, String filePath) {

		for (String requiredElementName : requiredElementNames) {
			validateRequiredChildElementName(
				element, requiredElementName, filePath);
		}
	}

	protected static void validateSeleniumMethodAttributeValue(
		PoshiElement poshiElement, String methodAttributeValue) {

		Matcher seleniumGetterMethodMatcher =
			_seleniumGetterMethodPattern.matcher(methodAttributeValue);

		seleniumGetterMethodMatcher.find();

		String seleniumMethodName = seleniumGetterMethodMatcher.group(
			"methodName");

		if (seleniumMethodName.equals("getCurrentUrl")) {
			return;
		}

		LiferaySeleniumMethod liferaySeleniumMethod =
			PoshiContext.getLiferaySeleniumMethod(seleniumMethodName);

		if (liferaySeleniumMethod == null) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Invalid Selenium method name: \"",
					seleniumMethodName, "\"\n"));

			return;
		}

		int seleniumParameterCount = liferaySeleniumMethod.getParameterCount();

		List<String> methodParameterValues =
			PoshiScriptParserUtil.getMethodParameterValues(
				seleniumGetterMethodMatcher.group("methodParameters"),
				poshiElement);

		if (methodParameterValues.size() != seleniumParameterCount) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Expected ", seleniumParameterCount,
					" parameter(s) for method \"", seleniumMethodName,
					"\" but found ", methodParameterValues.size()));
		}

		for (String methodParameterValue : methodParameterValues) {
			Matcher invalidMethodParameterMatcher =
				_invalidMethodParameterPattern.matcher(methodParameterValue);

			if (invalidMethodParameterMatcher.find()) {
				String invalidSyntax = invalidMethodParameterMatcher.group(
					"invalidSyntax");

				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Invalid parameter syntax \"",
						invalidSyntax, "\"\n"));
			}
		}
	}

	protected static void validateTakeScreenshotElement(
		PoshiElement poshiElement) {

		validateHasNoAttributes(poshiElement);
		validateHasNoChildElements(poshiElement);
	}

	protected static void validateTaskElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		List<String> possibleAttributeNames = Arrays.asList(
			"line-number", "macro-summary", "summary");

		validateHasChildElements(poshiElement, filePath);
		validatePossibleAttributeNames(poshiElement, possibleAttributeNames);

		List<String> primaryAttributeNames = Arrays.asList(
			"macro-summary", "summary");

		validateHasPrimaryAttributeName(poshiElement, primaryAttributeNames);

		parseElements(poshiElement);
	}

	protected static void validateTestCaseContext(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		String namespace = PoshiContext.getNamespaceFromFilePath(filePath);

		String testName = poshiElement.attributeValue("test-case");

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				testName);

		if (className.equals("super")) {
			className = PoshiGetterUtil.getExtendedTestCaseName(filePath);
		}

		String commandName =
			PoshiGetterUtil.getCommandNameFromNamespacedClassCommandName(
				testName);

		validateTestName(namespace + "." + className + "#" + commandName);
	}

	protected static void validateTestCaseFile(PoshiElement poshiElement) {
		validateDefinitionElement(poshiElement);

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		String filePath = _getFilePath(poshiElement);

		if (Validator.isNull(poshiElement.attributeValue("extends"))) {
			validateHasChildElements(poshiElement, filePath);
			validateRequiredChildElementName(poshiElement, "command", filePath);
		}

		List<String> possibleTagElementNames = Arrays.asList(
			"command", "property", "set-up", "tear-down", "var");

		Set<String> propertyNames = new HashSet<>();

		for (PoshiElement childPoshiElement : childPoshiElements) {
			String childPoshiElementName = childPoshiElement.getName();

			if (!possibleTagElementNames.contains(childPoshiElementName)) {
				_exceptions.add(
					new PoshiElementException(
						childPoshiElement, "Invalid ", childPoshiElementName,
						" element"));
			}

			if (childPoshiElementName.equals("command")) {
				List<String> possibleAttributeNames = Arrays.asList(
					"annotations", "description", "disable-webdriver", "ignore",
					"known-issues", "line-number", "name", "priority");

				validateHasChildElements(childPoshiElement, filePath);
				validateHasRequiredPropertyElements(childPoshiElement);
				validatePossibleAttributeNames(
					childPoshiElement, possibleAttributeNames);
				validateRequiredAttributeNames(
					childPoshiElement, Arrays.asList("name"), filePath);

				parseElements(childPoshiElement);
			}
			else if (childPoshiElementName.equals("property")) {
				validatePropertyElement(childPoshiElement);

				String propertyName = childPoshiElement.attributeValue("name");

				if (!propertyNames.add(propertyName)) {
					_exceptions.add(
						new PoshiElementException(
							childPoshiElement, "Duplicate property name ",
							propertyName));
				}
			}
			else if (childPoshiElementName.equals("set-up") ||
					 childPoshiElementName.equals("tear-down")) {

				validateHasChildElements(childPoshiElement, filePath);
				validateHasNoAttributes(childPoshiElement);

				parseElements(childPoshiElement);
			}
			else if (childPoshiElementName.equals("var")) {
				validateVarElement(childPoshiElement);
			}
		}
	}

	protected static void validateTestName(String testName) {
		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				testName);

		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				testName);

		if (Validator.isNull(namespace)) {
			namespace = PoshiContext.getDefaultNamespace();
		}

		if (!PoshiContext.isRootElement("test-case", className, namespace)) {
			_exceptions.add(
				new ValidationException(
					"Invalid test case class " + namespace + "." + className));
		}
		else if (testName.contains("#")) {
			String classCommandName =
				PoshiGetterUtil.
					getClassCommandNameFromNamespacedClassCommandName(testName);

			if (!PoshiContext.isCommandElement(
					"test-case", classCommandName, namespace)) {

				String commandName =
					PoshiGetterUtil.
						getCommandNameFromNamespacedClassCommandName(testName);

				_exceptions.add(
					new ValidationException(
						"Invalid test case command " + commandName));
			}
		}
	}

	protected static void validateThenElement(PoshiElement poshiElement) {
		List<PoshiElement> thenPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements("then"));

		if (thenPoshiElements.isEmpty()) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Missing then element"));
		}
		else if (thenPoshiElements.size() > 1) {
			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Too many then elements"));
		}
	}

	protected static void validateUtilityClassName(
			PoshiElement poshiElement, String className)
		throws PoshiElementException {

		if (!className.startsWith("selenium")) {
			if (!className.contains(".")) {
				try {
					className = PoshiGetterUtil.getUtilityClassName(className);
				}
				catch (IllegalArgumentException illegalArgumentException) {
					throw new PoshiElementException(
						poshiElement, illegalArgumentException.getMessage());
				}
			}

			if (!PoshiGetterUtil.isValidUtilityClass(className)) {
				throw new PoshiElementException(
					poshiElement, className, " is an invalid utility class");
			}
		}
	}

	protected static void validateVarElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		validateHasNoChildElements(poshiElement);
		validateRequiredAttributeNames(
			poshiElement, Arrays.asList("name"), filePath);

		List<Attribute> attributes = poshiElement.attributes();

		int minimumAttributeSize = 1;

		if ((attributes.size() <= minimumAttributeSize) &&
			Validator.isNull(poshiElement.getText())) {

			_exceptions.add(
				new PoshiElementException(
					poshiElement, "Missing value attribute"));
		}

		List<String> possibleAttributeNames = new ArrayList<>();

		Collections.addAll(
			possibleAttributeNames,
			new String[] {
				"from", "hash", "index", "line-number", "method", "name",
				"type", "value"
			});

		if (filePath.contains(".macro")) {
			possibleAttributeNames.add("static");
		}

		Element parentElement = poshiElement.getParent();

		if (parentElement != null) {
			String parentElementName = parentElement.getName();

			if (filePath.contains(".testcase") &&
				parentElementName.equals("definition")) {

				possibleAttributeNames.add("static");
			}
		}

		validatePossibleAttributeNames(poshiElement, possibleAttributeNames);

		if (Validator.isNotNull(poshiElement.attributeValue("method"))) {
			String methodAttributeValue = poshiElement.attributeValue("method");

			int x = methodAttributeValue.indexOf("#");

			String className = methodAttributeValue.substring(0, x);

			if (className.equals("selenium")) {
				validateSeleniumMethodAttributeValue(
					poshiElement, methodAttributeValue);
			}

			try {
				validateUtilityClassName(poshiElement, className);
			}
			catch (PoshiElementException poshiElementException) {
				_exceptions.add(poshiElementException);
			}

			int expectedAttributeCount = 0;

			if (Validator.isNotNull(poshiElement.attributeValue("name"))) {
				expectedAttributeCount++;
			}

			if (PoshiGetterUtil.getLineNumber(poshiElement) != -1) {
				expectedAttributeCount++;
			}

			if (Validator.isNotNull(poshiElement.attributeValue("static"))) {
				expectedAttributeCount++;
			}

			if (attributes.size() < expectedAttributeCount) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Too few attributes"));
			}

			if (attributes.size() > expectedAttributeCount) {
				_exceptions.add(
					new PoshiElementException(
						poshiElement, "Too many attributes"));
			}
		}
	}

	protected static void validateWhileElement(PoshiElement poshiElement) {
		String filePath = _getFilePath(poshiElement);

		validateHasChildElements(poshiElement, filePath);

		validatePossibleAttributeNames(
			poshiElement, Arrays.asList("line-number", "max-iterations"));
		validateThenElement(poshiElement);

		List<String> conditionTags = Arrays.asList(
			"and", "condition", "contains", "equals", "isset", "not", "or");

		List<PoshiElement> childPoshiElements = poshiElement.toPoshiElements(
			poshiElement.elements());

		for (int i = 0; i < childPoshiElements.size(); i++) {
			PoshiElement childPoshiElement = childPoshiElements.get(i);

			String childPoshiElementName = childPoshiElement.getName();

			if (i == 0) {
				if (conditionTags.contains(childPoshiElementName)) {
					validateConditionElement(childPoshiElement);
				}
				else {
					_exceptions.add(
						new PoshiElementException(
							poshiElement, "Missing while condition element"));
				}
			}
			else if (childPoshiElementName.equals("then")) {
				validateHasChildElements(childPoshiElement, filePath);
				validateHasNoAttributes(childPoshiElement);

				parseElements(childPoshiElement);
			}
			else {
				_exceptions.add(
					new PoshiElementException(
						childPoshiElement, "Invalid ", childPoshiElementName,
						" element"));
			}
		}
	}

	protected static final String[] UTIL_PACKAGE_NAMES = {
		"com.liferay.poshi.core.util", "com.liferay.poshi.runner.util"
	};

	private static void _addException(
		Element element, String message, String filePath) {

		if (element instanceof PoshiElement) {
			_exceptions.add(
				new PoshiElementException((PoshiElement)element, message));

			return;
		}

		_exceptions.add(new ValidationException(element, message, filePath));
	}

	private static String _getFilePath(PoshiElement poshiElement) {
		URL filePathURL = poshiElement.getFilePathURL();

		String filePath = filePathURL.getPath();

		if (OSDetector.isWindows()) {
			if (filePath.startsWith("/")) {
				filePath = filePath.substring(1);
			}

			filePath = StringUtil.replace(filePath, "/", "\\");
		}

		return filePath;
	}

	private static void _throwExceptions() throws Exception {
		List<Exception> warnings = PoshiElementException.getWarnings(
			new ArrayList<>(_exceptions));

		if (!warnings.isEmpty()) {
			_throwWarnings(warnings);
		}

		List<Exception> filteredExceptions =
			PoshiElementException.getFilteredExceptions(
				new ArrayList<>(_exceptions));

		if (filteredExceptions.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("\n\n");
		sb.append(filteredExceptions.size());
		sb.append(" errors in POSHI\n\n");

		for (Exception exception : filteredExceptions) {
			sb.append(exception.getMessage());
			sb.append("\n\n");
		}

		System.out.println(sb.toString());

		throw new Exception();
	}

	private static void _throwWarnings(List<Exception> warnings) {
		for (Exception exception : warnings) {
			PoshiElementException poshiElementException =
				(PoshiElementException)exception;

			_logger.warning(poshiElementException.getSimpleMessage());
		}
	}

	private static final Logger _logger = Logger.getLogger(
		PoshiValidation.class.getName());

	private static final List<String> _deprecatedFunctionNames =
		new ArrayList<>();
	private static final Map<String, String> _deprecatedMethodNames =
		new Hashtable<String, String>() {
			{
				put("antCommand", "\"AntCommands.runCommand\"");
				put("assertAlert", "\"selenium.assertAlertText\"");
				put("assertNotPartialText", "\"selenium.assertTextMatches\"");
				put("assertPartialText", "\"selenium.assertTextMatches\"");
				put(
					"assertPartialTextCaseInsensitive",
					"\"selenium.assertTextMatches\"");
				put(
					"assertTextCaseInsensitive",
					"\"selenium.assertTextMatches\"");
				put("copyText", "\"selenium.getText\" (stored as a variable)");
				put(
					"copyValue",
					"\"selenium.getElementValue\" (stored as a variable)");
				put("getAttribute", "\"selenium.getWebElementAttribute\"");
				put("getEval", "\"selenium.getJavaScriptResult\"");
				put("paste", "a variable storing the desired value");
				put("robotType", "\"selenium.type\"");
				put(
					"robotTypeShortcut",
					"\"selenium.typeKeys/selenium.sendKeys\"");
				put("runScript", "\"selenium.executeJavaScript\"");
				put("typeAlloyEditor", "\"selenium.typeEditor\"");
				put("typeCKEditor", "\"selenium.typeEditor\"");
				put("waitForNotPartialText", "\"selenium.waitForTextMatches\"");
				put("waitForPartialText", "\"selenium.waitForTextMatches\"");
				put(
					"waitForPartialTextCaseInsensitive",
					"\"selenium.waitForTextMatches\"");
				put(
					"waitForTextCaseInsensitive",
					"\"selenium.waitForTextMatches\"");
			}
		};
	private static final Set<Exception> _exceptions = new HashSet<>();
	private static final Pattern _invalidMethodParameterPattern =
		Pattern.compile("(?<invalidSyntax>(?:locator|value)[1-3]?[\\s]*=)");
	private static final Pattern _pattern = Pattern.compile("\\$\\{([^}]*)\\}");
	private static final Pattern _seleniumGetterMethodPattern = Pattern.compile(
		"^selenium#(?<methodName>[A-z]+)" +
			"(?:\\((?<methodParameters>.*|)\\))?$");

	private static class ValidationException extends Exception {

		public ValidationException(Element element, Object... messageParts) {
			super(
				PoshiElementException.join(
					PoshiElementException.join(messageParts), ":",
					PoshiGetterUtil.getLineNumber(element)));
		}

		public ValidationException(String... messageParts) {
			super(PoshiElementException.join((Object[])messageParts));
		}

	}

}
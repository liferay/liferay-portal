/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core;

import com.google.common.reflect.ClassPath;

import com.liferay.poshi.core.elements.PoshiElement;
import com.liferay.poshi.core.elements.PoshiNode;
import com.liferay.poshi.core.elements.PoshiNodeFactory;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.ExternalMethod;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.poshi.core.util.GetterUtil;
import com.liferay.poshi.core.util.OSDetector;
import com.liferay.poshi.core.util.PropsUtil;
import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Karen Dang
 * @author Michael Hashimoto
 */
public class PoshiGetterUtil {

	public static List<Element> getAllChildElements(
		Element element, String elementName) {

		List<Element> allChildElements = new ArrayList<>();

		List<Element> childElements = element.elements();

		if (childElements.isEmpty()) {
			return allChildElements;
		}

		for (Element childElement : childElements) {
			String childElementName = childElement.getName();

			if (childElementName.equals(elementName)) {
				allChildElements.add(childElement);
			}

			allChildElements.addAll(
				getAllChildElements(childElement, elementName));
		}

		return allChildElements;
	}

	public static String getClassCommandNameFromNamespacedClassCommandName(
		String namespacedClassCommandName) {

		Matcher matcher = _namespacedClassCommandNamePattern.matcher(
			namespacedClassCommandName);

		if (matcher.find()) {
			String className = matcher.group("className");

			String commandName = matcher.group("commandName");

			if (Validator.isNotNull(commandName)) {
				return className + "#" + commandName;
			}

			return className;
		}

		throw new RuntimeException(
			"Unable to find class name and/or command name in " +
				namespacedClassCommandName);
	}

	public static String getClassNameFromFilePath(String filePath) {
		int x = filePath.lastIndexOf("/");
		int y = filePath.lastIndexOf(".");

		if ((x == -1) && OSDetector.isWindows()) {
			x = filePath.lastIndexOf("\\");
		}

		return filePath.substring(x + 1, y);
	}

	public static String getClassNameFromNamespacedClassCommandName(
		String namespacedClassCommandName) {

		Matcher matcher = _namespacedClassCommandNamePattern.matcher(
			namespacedClassCommandName);

		if (matcher.find()) {
			return matcher.group("className");
		}

		throw new RuntimeException(
			"Unable to find class name in " + namespacedClassCommandName);
	}

	public static String getClassNameFromNamespacedClassName(
		String namespacedClassName) {

		Matcher matcher = _namespacedClassCommandNamePattern.matcher(
			namespacedClassName);

		if (matcher.find()) {
			return matcher.group("className");
		}

		throw new RuntimeException(
			"Unable to find class name in " + namespacedClassName);
	}

	public static String getClassTypeFromFileExtension(String fileExtension) {
		String classType = fileExtension;

		if (fileExtension.equals("testcase")) {
			classType = "test-case";
		}

		return classType;
	}

	public static String getClassTypeFromFilePath(String filePath) {
		String fileExtension = getFileExtensionFromFilePath(filePath);

		return getClassTypeFromFileExtension(fileExtension);
	}

	public static String getCommandNameFromNamespacedClassCommandName(
		String namespacedClassCommandName) {

		Matcher matcher = _namespacedClassCommandNamePattern.matcher(
			namespacedClassCommandName);

		if (matcher.find()) {
			String commandName = matcher.group("commandName");

			if (Validator.isNull(commandName)) {
				return namespacedClassCommandName;
			}

			return commandName;
		}

		throw new RuntimeException(
			"Unable to find command name in " + namespacedClassCommandName);
	}

	public static String getExtendedTestCaseName() {
		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		String testName = poshiProperties.testName;

		Element rootElement = PoshiContext.getTestCaseRootElement(
			getClassNameFromNamespacedClassCommandName(testName),
			getNamespaceFromNamespacedClassCommandName(testName));

		return getExtendedTestCaseName(rootElement);
	}

	public static String getExtendedTestCaseName(Element rootElement) {
		return rootElement.attributeValue("extends");
	}

	public static String getExtendedTestCaseName(String filePath) {
		Element rootElement = PoshiContext.getTestCaseRootElement(
			getClassNameFromFilePath(filePath),
			PoshiContext.getNamespaceFromFilePath(filePath));

		return getExtendedTestCaseName(rootElement);
	}

	public static String getFileExtensionFromClassType(String classType) {
		String fileExtension = classType;

		if (fileExtension.equals("test-case")) {
			fileExtension = "testcase";
		}

		return fileExtension;
	}

	public static String getFileExtensionFromFilePath(String filePath) {
		int x = filePath.lastIndexOf(".");

		return filePath.substring(x + 1);
	}

	public static String getFileNameFromFilePath(String filePath) {
		String className = getClassNameFromFilePath(filePath);
		String fileExtension = getFileExtensionFromFilePath(filePath);

		return className + "." + fileExtension;
	}

	public static int getLineNumber(Element element) {
		if (element instanceof PoshiElement) {
			PoshiElement poshiElement = (PoshiElement)element;

			return poshiElement.getPoshiScriptLineNumber();
		}

		String lineNumber = element.attributeValue("line-number");

		if (lineNumber != null) {
			return Integer.valueOf(lineNumber);
		}

		return -1;
	}

	public static Object getMethodReturnValue(
			String testNamespacedCommandName, List<String> args,
			String className, String methodName, Object object)
		throws Exception {

		if (!className.equals("selenium")) {
			if (!className.contains(".")) {
				className = getUtilityClassName(className);
			}
			else {
				if (!isValidUtilityClass(className)) {
					throw new IllegalArgumentException(
						className + " is not a valid class name");
				}
			}
		}

		Object[] parameters = new Object[args.size()];

		for (int i = 0; i < args.size(); i++) {
			String arg = args.get(i);

			Matcher matcher = _variablePattern.matcher(arg);

			Object parameter = null;

			PoshiVariablesContext poshiVariablesContext =
				PoshiVariablesContext.getPoshiVariablesContext(
					testNamespacedCommandName);

			if (matcher.matches()) {
				parameter = poshiVariablesContext.getValueFromCommandMap(
					matcher.group(1));
			}
			else {
				parameter = poshiVariablesContext.replaceCommandVars(arg);
			}

			if (className.endsWith("MathUtil")) {
				parameter = GetterUtil.getLong(parameter);
			}
			else if (className.endsWith("StringUtil")) {
				parameter = String.valueOf(parameter);
			}

			parameters[i] = parameter;
		}

		Object returnObject = null;

		if (object != null) {
			returnObject = ExternalMethod.execute(
				methodName, object, parameters);
		}
		else {
			returnObject = ExternalMethod.execute(
				className, methodName, parameters);
		}

		return returnObject;
	}

	public static String getNamespacedClassNameFromNamespacedClassCommandName(
		String namespacedClassCommandName) {

		Matcher matcher = _namespacedClassCommandNamePattern.matcher(
			namespacedClassCommandName);

		if (matcher.find()) {
			String namespace = matcher.group("namespace");

			if (Validator.isNull(namespace)) {
				namespace = PoshiContext.getDefaultNamespace();
			}

			String className = matcher.group("className");

			return namespace + "." + className;
		}

		throw new RuntimeException(
			"Unable to find class name in " + namespacedClassCommandName);
	}

	public static String getNamespaceFromNamespacedClassCommandName(
		String namespacedClassCommandName) {

		Matcher matcher = _namespacedClassCommandNamePattern.matcher(
			namespacedClassCommandName);

		if (matcher.find()) {
			String namespace = matcher.group("namespace");

			if (Validator.isNull(namespace)) {
				return null;
			}

			return namespace;
		}

		throw new RuntimeException(
			"Unable to find namespace in " + namespacedClassCommandName);
	}

	public static String getNamespaceFromNamespacedClassName(
		String namespacedClassName) {

		Matcher matcher = _namespacedClassCommandNamePattern.matcher(
			namespacedClassName);

		if (matcher.find()) {
			String namespace = matcher.group("namespace");

			if (Validator.isNull(namespace)) {
				namespace = PoshiContext.getDefaultNamespace();
			}

			return namespace;
		}

		throw new RuntimeException(
			"Unable to find namespace in " + namespacedClassName);
	}

	public static String getProjectDirName() {
		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		return FileUtil.getCanonicalPath(poshiProperties.projectDir);
	}

	public static Element getRootElementFromURL(URL url) throws Exception {
		return getRootElementFromURL(url, true);
	}

	public static Element getRootElementFromURL(URL url, boolean addLineNumbers)
		throws Exception {

		String filePath = url.getFile();

		if (filePath.endsWith(".path")) {
			Dom4JUtil.validateDocument(url);

			return _preparePoshiXMLElement(url, addLineNumbers);
		}

		if (filePath.endsWith(".function") || filePath.endsWith(".macro") ||
			filePath.endsWith(".testcase")) {

			PoshiNode<?, ?> poshiNode = PoshiNodeFactory.newPoshiNodeFromFile(
				url);

			if (poshiNode instanceof PoshiElement) {
				return (Element)poshiNode;
			}
		}

		throw new Exception("Unable to parse Poshi file: " + filePath);
	}

	public static String getUtilityClassName(String simpleClassName) {
		String utilityClassName = _utilityClassMap.get(simpleClassName);

		if (utilityClassName != null) {
			return utilityClassName;
		}

		throw new IllegalArgumentException(
			simpleClassName + " is not a valid simple class name");
	}

	public static boolean isValidUtilityClass(String className) {
		return _utilityClassMap.containsValue(className);
	}

	private static Element _preparePoshiXMLElement(
			URL url, boolean addLineNumbers)
		throws Exception {

		return _preparePoshiXMLElement(url, FileUtil.read(url), addLineNumbers);
	}

	private static Element _preparePoshiXMLElement(
			URL url, String fileContent, boolean addLineNumbers)
		throws Exception {

		BufferedReader bufferedReader = new BufferedReader(
			new StringReader(fileContent));

		boolean cdata = false;
		String filePath = url.getFile();
		String line = null;
		int lineNumber = 1;
		StringBuilder sb = new StringBuilder();

		while ((line = bufferedReader.readLine()) != null) {
			Matcher matcher = _tagPattern.matcher(line);

			if (line.contains("<![CDATA[") || cdata) {
				if (line.contains("]]>")) {
					cdata = false;
				}
				else {
					cdata = true;
				}

				if (line.contains("<![CDATA[") && matcher.find() &&
					addLineNumbers) {

					for (String reservedTag : _reservedTags) {
						if (line.contains("<" + reservedTag)) {
							line = StringUtil.replace(
								line, matcher.group(),
								matcher.group() + " line-number=\"" +
									lineNumber + "\"");

							break;
						}
					}
				}
			}
			else if (matcher.find()) {
				boolean tagIsReservedTag = false;

				for (String reservedTag : _reservedTags) {
					if (line.contains("<" + reservedTag)) {
						if (addLineNumbers) {
							line = StringUtil.replace(
								line, matcher.group(),
								matcher.group() + " line-number=\"" +
									lineNumber + "\"");
						}

						tagIsReservedTag = true;

						break;
					}
				}

				if (!tagIsReservedTag) {
					int x = line.indexOf("<");

					int y = line.indexOf(" ", x);

					if (y == -1) {
						y = line.indexOf(">");

						if (y == -1) {
							y = line.indexOf(">");
						}
					}

					String tagName = line.substring(x + 1, y);

					throw new Exception(
						"Invalid \"" + tagName + "\" tag\n" + filePath + ":" +
							lineNumber);
				}
			}

			sb.append(line);

			if (cdata) {
				sb.append("\n");
			}

			lineNumber++;
		}

		String content = sb.toString();

		InputStream inputStream = new ByteArrayInputStream(
			content.getBytes("UTF-8"));

		SAXReader saxReader = new SAXReader();

		Document document = null;

		try {
			document = saxReader.read(inputStream);
		}
		catch (DocumentException documentException) {
			throw new Exception(
				documentException.getMessage() + "\nInvalid syntax in " +
					filePath,
				documentException);
		}

		return document.getRootElement();
	}

	private static final Pattern _namespacedClassCommandNamePattern =
		Pattern.compile(
			"((?<namespace>\\w+)\\.)?(?<className>\\w+)(\\#(?<commandName>" +
				"(\\w+(\\-\\w+)*|\\$\\{\\w+\\}|\\w+|\\s*\\w+)*))?");
	private static final List<String> _reservedTags = Arrays.asList(
		"and", "arg", "body", "case", "command", "condition", "contains",
		"default", "definition", "description", "echo", "else", "elseif",
		"equals", "execute", "fail", "for", "if", "head", "html", "isset",
		"not", "off", "on", "or", "property", "return", "set-up", "table",
		"take-screenshot", "task", "tbody", "td", "tear-down", "thead", "then",
		"title", "tr", "var", "while");
	private static final Pattern _tagPattern = Pattern.compile("<[a-z\\-]+");

	private static final Map<String, String> _utilityClassMap =
		new TreeMap<String, String>() {
			{
				try {
					ClassPath classPath = ClassPath.from(
						PropsUtil.class.getClassLoader());

					for (String packageName :
							PoshiValidation.UTIL_PACKAGE_NAMES) {

						for (ClassPath.ClassInfo classInfo :
								classPath.getTopLevelClasses(packageName)) {

							put(classInfo.getSimpleName(), classInfo.getName());
						}
					}
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		};

	private static final Pattern _variablePattern = Pattern.compile(
		"\\$\\{([^}]*)\\}");

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core;

import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dom4j.Element;

/**
 * @author Karen Dang
 * @author Michael Hashimoto
 */
public final class PoshiStackTrace {

	public static void clear(String classCommandName) {
		_poshiStackTraces.remove(classCommandName);
	}

	public static synchronized PoshiStackTrace getPoshiStackTrace(
		String classCommandName) {

		return _poshiStackTraces.computeIfAbsent(
			classCommandName, key -> new PoshiStackTrace());
	}

	public void emptyStackTrace() {
		while (!_stackTraceElements.isEmpty()) {
			_stackTraceElements.pop();
		}
	}

	public String getCurrentFilePath() {
		PoshiFilePath poshiFilePath = _poshiFilePaths.peek();

		return poshiFilePath.getFilePath();
	}

	public String getCurrentNamespace() {
		if (_poshiFilePaths.isEmpty()) {
			return PoshiContext.getDefaultNamespace();
		}

		String filePath = getCurrentFilePath();

		return PoshiContext.getNamespaceFromFilePath(filePath);
	}

	public String getCurrentNamespace(String namespacedClassCommandName) {
		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName);

		if (Validator.isNull(namespace)) {
			namespace = getCurrentNamespace();
		}

		return namespace;
	}

	public String getNamespaceFromNamespacedClassCommandName(
		String namespacedClassCommandName) {

		String namespace =
			PoshiGetterUtil.getNamespaceFromNamespacedClassCommandName(
				namespacedClassCommandName);

		if (Validator.isNull(namespace)) {
			namespace = getCurrentNamespace();
		}

		return namespace;
	}

	public String getSimpleStackTraceMessage() {
		StringBuilder sb = new StringBuilder();

		for (StackTraceElement stackTraceElement : _stackTraceElements) {
			String fileName = stackTraceElement.getFileName();

			if (fileName.contains(".function")) {
				continue;
			}

			sb.append(
				_getStackTraceLine(
					PoshiGetterUtil.getFileNameFromFilePath(fileName),
					stackTraceElement.getMethodName(),
					stackTraceElement.getLineNumber()));
		}

		PoshiFilePath poshiFilePath = _poshiFilePaths.peek();

		String currentFilePath = poshiFilePath.getFilePath();

		if (!currentFilePath.contains(".function")) {
			sb.append(
				_getStackTraceLine(
					PoshiGetterUtil.getFileNameFromFilePath(currentFilePath),
					poshiFilePath.getCommandName(),
					PoshiGetterUtil.getLineNumber(_currentElement)));
		}

		return sb.toString();
	}

	public StackTraceElement[] getStackTrace() {
		List<StackTraceElement> stackTraceElements = new ArrayList<>(
			(Stack<StackTraceElement>)_stackTraceElements.clone());

		PoshiFilePath poshiFilePath = _poshiFilePaths.peek();

		stackTraceElements.add(
			new StackTraceElement(
				poshiFilePath.getClassName(), poshiFilePath.getCommandName(),
				poshiFilePath.getFilePath(),
				PoshiGetterUtil.getLineNumber(_currentElement)));

		Collections.reverse(stackTraceElements);

		return stackTraceElements.toArray(new StackTraceElement[0]);
	}

	public String getStackTraceMessage(String msg) {
		StringBuilder sb = new StringBuilder();

		if (Validator.isNotNull(msg)) {
			sb.append(msg);
		}

		PoshiFilePath poshiFilePath = _poshiFilePaths.peek();

		sb.append("\n");

		sb.append(
			_getStackTraceLine(
				poshiFilePath.getFilePath(), poshiFilePath.getCommandName(),
				PoshiGetterUtil.getLineNumber(_currentElement)));

		Stack<StackTraceElement> stackTraceElements =
			(Stack<StackTraceElement>)_stackTraceElements.clone();

		while (!stackTraceElements.isEmpty()) {
			sb.append("\n");

			StackTraceElement stackTraceElement = stackTraceElements.pop();

			sb.append(_getStackTraceLine(stackTraceElement));
		}

		return sb.toString();
	}

	public void popStackTrace() {
		_poshiFilePaths.pop();
		_stackTraceElements.pop();
	}

	public void printStackTrace() {
		printStackTrace(null);
	}

	public void printStackTrace(String msg) {
		System.out.println(getStackTraceMessage(msg));
	}

	public void pushStackTrace(Element element) throws Exception {
		PoshiFilePath poshiFilePath = _poshiFilePaths.peek();

		_stackTraceElements.push(
			new StackTraceElement(
				poshiFilePath.getClassName(), poshiFilePath.getCommandName(),
				poshiFilePath.getFilePath(),
				PoshiGetterUtil.getLineNumber(element)));

		String namespacedClassCommandName = null;
		String classType = null;

		if (element.attributeValue("function") != null) {
			namespacedClassCommandName = element.attributeValue("function");
			classType = "function";
		}
		else if (element.attributeValue("macro") != null) {
			namespacedClassCommandName = element.attributeValue("macro");
			classType = "macro";
		}
		else if (element.attributeValue("test-case") != null) {
			namespacedClassCommandName = element.attributeValue("test-case");

			String className =
				PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
					namespacedClassCommandName);

			if (className.equals("super")) {
				className = PoshiGetterUtil.getExtendedTestCaseName();

				namespacedClassCommandName =
					namespacedClassCommandName.replaceFirst("super", className);
			}

			classType = "test-case";
		}
		else {
			printStackTrace();

			throw new Exception("Missing (function|macro|test-case) attribute");
		}

		_pushFilePath(namespacedClassCommandName, classType);
	}

	public void setCurrentElement(Element currentElement) {
		_currentElement = currentElement;
	}

	public void startStackTrace(String classCommandName, String classType) {
		_pushFilePath(classCommandName, classType);
	}

	private PoshiStackTrace() {
	}

	private String _getStackTraceLine(StackTraceElement stackTraceElement) {
		return _getStackTraceLine(
			stackTraceElement.getFileName(), stackTraceElement.getMethodName(),
			stackTraceElement.getLineNumber());
	}

	private String _getStackTraceLine(
		String filePath, String commandName, int lineNumber) {

		return StringUtil.combine(
			filePath, "[", commandName + "]:", String.valueOf(lineNumber));
	}

	private void _pushFilePath(
		String namespacedClassCommandName, String classType) {

		String classCommandName =
			PoshiGetterUtil.getClassCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		String className =
			PoshiGetterUtil.getClassNameFromNamespacedClassCommandName(
				classCommandName);

		String fileExtension = PoshiGetterUtil.getFileExtensionFromClassType(
			classType);

		String filePath = PoshiContext.getFilePathFromFileName(
			className + "." + fileExtension,
			getCurrentNamespace(namespacedClassCommandName));

		String commandName =
			PoshiGetterUtil.getCommandNameFromNamespacedClassCommandName(
				namespacedClassCommandName);

		_poshiFilePaths.push(
			new PoshiFilePath(className, commandName, filePath));
	}

	private static final Map<String, PoshiStackTrace> _poshiStackTraces =
		new HashMap<>();

	private Element _currentElement;
	private final Stack<PoshiFilePath> _poshiFilePaths = new Stack<>();
	private final Stack<StackTraceElement> _stackTraceElements = new Stack<>();

	private class PoshiFilePath {

		public PoshiFilePath(
			String className, String commandName, String filePath) {

			_className = className;
			_commandName = commandName;
			_filePath = filePath;
		}

		public String getClassName() {
			return _className;
		}

		public String getCommandName() {
			return _commandName;
		}

		public String getFilePath() {
			return _filePath;
		}

		private final String _className;
		private final String _commandName;
		private final String _filePath;

	}

}
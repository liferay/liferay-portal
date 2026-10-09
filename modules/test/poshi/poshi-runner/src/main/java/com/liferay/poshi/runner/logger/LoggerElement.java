/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.logger;

import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * @author Michael Hashimoto
 */
public class LoggerElement {

	public LoggerElement() {
	}

	public LoggerElement(String id) {
		_id = id;
	}

	public void addChildLoggerElement(LoggerElement childLoggerElement) {
		_childLoggerElements.add(childLoggerElement);
	}

	public void addClassName(String className) {
		setClassName(_className + " " + className);
	}

	public LoggerElement copy() {
		LoggerElement loggerElement = new LoggerElement();

		List<LoggerElement> childLoggerElements = loggerElements();

		for (LoggerElement childLoggerElement : childLoggerElements) {
			loggerElement.addChildLoggerElement(childLoggerElement.copy());
		}

		List<String> attributeNames = getAttributeNames();

		for (String attributeName : attributeNames) {
			loggerElement.setAttribute(
				attributeName, getAttributeValue(attributeName));
		}

		loggerElement.setClassName(getClassName());
		loggerElement.setName(getName());
		loggerElement.setText(getText());

		return loggerElement;
	}

	public List<String> getAttributeNames() {
		List<String> attributeNames = new ArrayList<>();

		for (String attributeName : _attributes.keySet()) {
			attributeNames.add(attributeName);
		}

		return attributeNames;
	}

	public String getAttributeValue(String key) {
		return _attributes.get(key);
	}

	public String getClassName() {
		return _className;
	}

	public String getID() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getText() {
		return _text;
	}

	public LoggerElement loggerElement(String name) {
		List<LoggerElement> loggerElements = loggerElements(name);

		if (!loggerElements.isEmpty()) {
			return loggerElements.get(0);
		}

		return null;
	}

	public LoggerElement loggerElement(String name, String className) {
		List<LoggerElement> loggerElements = loggerElements(name);

		for (LoggerElement loggerElement : loggerElements) {
			String childLoggerElementClassName = loggerElement.getClassName();

			List<String> childLoggerElementClassNames = Arrays.asList(
				StringUtil.split(childLoggerElementClassName, " "));

			if (childLoggerElementClassNames.contains(className)) {
				return loggerElement;
			}
		}

		return null;
	}

	public List<LoggerElement> loggerElements() {
		return _childLoggerElements;
	}

	public List<LoggerElement> loggerElements(String name) {
		List<LoggerElement> childLoggerElements = new ArrayList<>();

		for (LoggerElement childLoggerElement : _childLoggerElements) {
			if (!Objects.equals(childLoggerElement.getName(), name)) {
				continue;
			}

			childLoggerElements.add(childLoggerElement);
		}

		return childLoggerElements;
	}

	public void removeChildLoggerElements(String name) {
		List<LoggerElement> childLoggerElements = new ArrayList<>();

		for (LoggerElement childLoggerElement : _childLoggerElements) {
			if (!Objects.equals(childLoggerElement.getName(), name)) {
				continue;
			}

			childLoggerElements.add(childLoggerElement);
		}

		for (LoggerElement childLoggerElement : childLoggerElements) {
			_childLoggerElements.remove(childLoggerElement);
		}
	}

	public void removeClassName(String className) {
		String cleanedClassName = _className.replaceFirst(
			"(.*?)\\s*" + Matcher.quoteReplacement(className) + "\\s*(.*)",
			"$1 $2");

		setClassName(cleanedClassName.trim());
	}

	public void setAttribute(String attributeName, String attributeValue) {
		_attributes.put(attributeName, attributeValue);
	}

	public void setClassName(String className) {
		_className = _fixClassName(className);
	}

	public void setID(String id) {
		_id = id;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setText(String text) {
		_text = text;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("<");
		sb.append(_name);

		for (Map.Entry<String, String> entry : _attributes.entrySet()) {
			sb.append(" ");
			sb.append(entry.getKey());
			sb.append("=\"");
			sb.append(entry.getValue());
			sb.append("\"");
		}

		if (Validator.isNotNull(_className)) {
			sb.append(" class=\"");
			sb.append(_className);
			sb.append("\"");
		}

		if (Validator.isNotNull(_id)) {
			sb.append(" id=\"");
			sb.append(_id);
			sb.append("\"");
		}

		sb.append(">");

		boolean hasChildren = false;

		if (!_childLoggerElements.isEmpty()) {
			hasChildren = true;
		}

		boolean hasText = Validator.isNotNull(_text);

		if (hasChildren || hasText) {
			if (hasText) {
				sb.append(_text);
			}

			if (hasChildren) {
				for (LoggerElement childLoggerElement : _childLoggerElements) {
					sb.append(childLoggerElement.toString());
				}
			}
		}

		sb.append("</");
		sb.append(_name);
		sb.append(">");

		return sb.toString();
	}

	private String _fixClassName(String className) {
		String[] classNames = StringUtil.split(className, " ");

		Arrays.sort(classNames);

		return StringUtil.join(classNames, " ");
	}

	private final Map<String, String> _attributes = new HashMap<>();
	private final List<LoggerElement> _childLoggerElements = new ArrayList<>();
	private String _className = "";
	private String _id;
	private String _name = "div";
	private String _text = "";

}
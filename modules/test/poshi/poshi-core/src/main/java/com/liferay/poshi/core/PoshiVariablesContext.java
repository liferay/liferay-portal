/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core;

import com.liferay.poshi.core.util.ListUtil;
import com.liferay.poshi.core.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karen Dang
 * @author Michael Hashimoto
 */
public class PoshiVariablesContext {

	public static void clear(String classCommandName) {
		_poshiVariablesContexts.remove(classCommandName);
	}

	public static synchronized PoshiVariablesContext getPoshiVariablesContext(
		String classCommandName) {

		return _poshiVariablesContexts.computeIfAbsent(
			classCommandName, key -> new PoshiVariablesContext());
	}

	public boolean containsKeyInCommandMap(String key) {
		return _commandMap.containsKey(replaceCommandVars(key));
	}

	public boolean containsKeyInExecuteMap(String key) {
		return _executeMap.containsKey(replaceCommandVars(key));
	}

	public boolean containsKeyInStaticMap(String key) {
		return _staticMap.containsKey(replaceCommandVars(key));
	}

	public Object getObjectFromCommandMap(String key) {
		if (containsKeyInCommandMap((String)replaceCommandVars(key))) {
			return getValueFromCommandMap(key);
		}

		return null;
	}

	public Object getReplacedCommandVarsObject(String token) {
		if (token == null) {
			return null;
		}

		return replaceCommandVars(token);
	}

	public String getReplacedCommandVarsString(String token) {
		if (token == null) {
			return null;
		}

		Object tokenObject = replaceCommandVars(token);

		if (tokenObject instanceof List) {
			return ListUtil.toString((List)tokenObject);
		}

		return tokenObject.toString();
	}

	public String getStringFromExecuteMap(String key) {
		if (containsKeyInExecuteMap((String)replaceCommandVars(key))) {
			Object object = getValueFromExecuteMap(key);

			return object.toString();
		}

		return null;
	}

	public String getStringFromStaticMap(String key) {
		if (containsKeyInStaticMap((String)replaceStaticVars(key))) {
			Object object = getValueFromExecuteMap(key);

			return object.toString();
		}

		return null;
	}

	public Object getValueFromCommandMap(String key) {
		return _commandMap.get(replaceCommandVars(key));
	}

	public Object getValueFromExecuteMap(String key) {
		return _executeMap.get(replaceCommandVars(key));
	}

	public Object getValueFromStaticMap(String key) {
		return _staticMap.get(replaceCommandVars(key));
	}

	public void popCommandMap() {
		_commandMap = _commandMapStack.pop();

		_commandMap.putAll(_staticMap);

		_executeMap = new HashMap<>();
	}

	public void pushCommandMap() {
		_commandMapStack.push(_commandMap);

		_commandMap = _executeMap;

		_commandMap.putAll(_staticMap);

		_executeMap = new HashMap<>();
	}

	public void pushStaticVarsIntoCommandMap() {
		_commandMap.putAll(_staticMap);

		_commandMapStack.push(_commandMap);
	}

	public void putIntoCommandMap(String key, Object value) {
		if (value instanceof String) {
			_commandMap.put(
				(String)replaceCommandVars(key),
				replaceCommandVars((String)value));
		}
		else {
			_commandMap.put((String)replaceCommandVars(key), value);
		}

		if (containsKeyInStaticMap(key)) {
			putIntoStaticMap(key, value);
		}
	}

	public void putIntoExecuteMap(String key, Object value) {
		if (value instanceof String) {
			_executeMap.put(
				(String)replaceCommandVars(key),
				replaceCommandVars((String)value));
		}
		else {
			_executeMap.put((String)replaceCommandVars(key), value);
		}
	}

	public void putIntoStaticMap(String key, Object value) {
		if (value instanceof String) {
			_staticMap.put(
				(String)replaceCommandVars(key),
				replaceCommandVars((String)value));
		}
		else {
			_staticMap.put((String)replaceCommandVars(key), value);
		}
	}

	public Object replaceCommandVars(String token) {
		Matcher matcher = _pattern.matcher(token);

		if (matcher.matches() && _commandMap.containsKey(matcher.group(1))) {
			return getValueFromCommandMap(matcher.group(1));
		}

		matcher.reset();

		while (matcher.find() && _commandMap.containsKey(matcher.group(1))) {
			Object varValue = getObjectFromCommandMap(matcher.group(1));

			token = StringUtil.replace(
				token, matcher.group(), varValue.toString());
		}

		return token;
	}

	public Object replaceExecuteVars(String token) {
		Matcher matcher = _pattern.matcher(token);

		if (matcher.matches() && _executeMap.containsKey(matcher.group(1))) {
			return getValueFromExecuteMap(matcher.group(1));
		}

		matcher.reset();

		while (matcher.find() && _executeMap.containsKey(matcher.group(1))) {
			String varValue = getStringFromExecuteMap(matcher.group(1));

			token = StringUtil.replace(token, matcher.group(), varValue);
		}

		return token;
	}

	public Object replaceStaticVars(String token) {
		Matcher matcher = _pattern.matcher(token);

		if (matcher.matches() && _staticMap.containsKey(matcher.group(1))) {
			return getValueFromStaticMap(matcher.group(1));
		}

		matcher.reset();

		while (matcher.find() && _staticMap.containsKey(matcher.group(1))) {
			String varValue = getStringFromStaticMap(matcher.group(1));

			token = StringUtil.replace(token, matcher.group(), varValue);
		}

		return token;
	}

	private PoshiVariablesContext() {
	}

	private static final Pattern _pattern = Pattern.compile("\\$\\{([^}]*)\\}");
	private static final Map<String, PoshiVariablesContext>
		_poshiVariablesContexts = new HashMap<>();

	private Map<String, Object> _commandMap = new HashMap<>();
	private final Stack<Map<String, Object>> _commandMapStack = new Stack<>();
	private Map<String, Object> _executeMap = new HashMap<>();
	private final Map<String, Object> _staticMap = new HashMap<>();

}
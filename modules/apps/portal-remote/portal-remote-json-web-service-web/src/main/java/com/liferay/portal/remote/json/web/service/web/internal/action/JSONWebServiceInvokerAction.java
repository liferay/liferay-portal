/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal.action;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionsManager;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.lang.reflect.Array;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jodd.bean.BeanUtil;

import jodd.json.BeanSerializer;
import jodd.json.JsonContext;
import jodd.json.JsonSerializer;

/**
 * @author Igor Spasic
 * @author Eduardo Lundgren
 */
public class JSONWebServiceInvokerAction implements JSONWebServiceAction {

	public JSONWebServiceInvokerAction(
		JSONWebServiceActionsManager jsonWebServiceActionsManager,
		HttpServletRequest httpServletRequest) {

		_jsonWebServiceActionsManager = jsonWebServiceActionsManager;
		_httpServletRequest = httpServletRequest;

		String command = httpServletRequest.getParameter(Constants.CMD);

		if (command == null) {
			try {
				command = StreamUtil.toString(
					httpServletRequest.getInputStream());
			}
			catch (IOException ioException) {
				throw new IllegalArgumentException(ioException);
			}
		}

		_command = command;
	}

	@Override
	public Object invoke() throws Exception {
		Object command = JSONFactoryUtil.looseDeserialize(_command);

		List<Object> list = null;

		boolean batchMode = false;

		if (command instanceof List) {
			list = (List<Object>)command;

			batchMode = true;
		}
		else if (command instanceof Map) {
			list = new ArrayList<>(1);

			list.add(command);

			batchMode = false;
		}
		else {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < list.size(); i++) {
			Map<String, Map<String, Object>> map =
				(Map<String, Map<String, Object>>)list.get(i);

			if (map.isEmpty()) {
				throw new IllegalArgumentException();
			}

			Set<Map.Entry<String, Map<String, Object>>> entrySet =
				map.entrySet();

			Iterator<Map.Entry<String, Map<String, Object>>> iterator =
				entrySet.iterator();

			Map.Entry<String, Map<String, Object>> entry = iterator.next();

			Statement statement = _parseStatement(
				null, entry.getKey(), entry.getValue());

			Object result = _executeStatement(statement);

			list.set(i, result);
		}

		Object result = null;

		if (!batchMode) {
			result = list.get(0);
		}
		else {
			result = list;
		}

		return new InvokerResult(result);
	}

	public class InvokerResult implements JSONSerializable {

		public InvokerResult(Object result) {
			_result = result;
		}

		public JSONWebServiceInvokerAction getJSONWebServiceInvokerAction() {
			return JSONWebServiceInvokerAction.this;
		}

		public Object getResult() {
			return _result;
		}

		@Override
		public String toJSONString() {
			if (_result == null) {
				return JSONFactoryUtil.getNullJSON();
			}

			JSONSerializer jsonSerializer = createJSONSerializer();

			if (_includes != null) {
				for (String include : _includes) {
					jsonSerializer.include(include);
				}
			}

			return jsonSerializer.serialize(_result);
		}

		protected JSONSerializer createJSONSerializer() {
			return JSONFactoryUtil.createJSONSerializer();
		}

		private Object _result;

	}

	private void _addInclude(Statement statement, String name) {
		if (_includes == null) {
			_includes = new ArrayList<>();
		}

		StringBuilder sb = new StringBuilder();

		while (statement._parentStatement != null) {
			String statementName = statement.getName();

			statementName = statementName.substring(1);

			sb.insert(0, statementName + StringPool.PERIOD);

			statement = statement._parentStatement;
		}

		sb.append(name);

		String includeName = sb.toString();

		if (!_includes.contains(includeName)) {
			_includes.add(includeName);
		}
	}

	private Object _addVariableStatement(
			Statement variableStatement, Object result)
		throws Exception {

		Statement statement = variableStatement.getParentStatement();

		result = _populateFlags(statement, result);

		String name = variableStatement.getName();

		Object variableResult = _executeStatement(variableStatement);

		Map<String, Object> map = _convertObjectToMap(statement, result, null);

		if (!variableStatement.isInner()) {
			map.put(name.substring(1), variableResult);

			return map;
		}

		int index = name.indexOf(".$");

		String innerObjectName = name.substring(0, index);

		if (innerObjectName.contains(StringPool.PERIOD)) {
			throw new IllegalArgumentException(
				"Inner properties with more than 1 level are not supported");
		}

		Object innerObject = map.get(innerObjectName);

		String innerPropertyName = name.substring(index + 2);

		if (innerObject instanceof List) {
			List<Object> innerList = (List<Object>)innerObject;

			List<Object> newInnerList = new ArrayList<>(innerList.size());

			for (Object innerListElement : innerList) {
				Map<String, Object> newInnerListElement = _convertObjectToMap(
					statement, innerListElement, innerObjectName);

				newInnerListElement.put(innerPropertyName, variableResult);

				newInnerList.add(newInnerListElement);
			}

			map.put(innerObjectName, newInnerList);
		}
		else {
			Map<String, Object> innerMap = _convertObjectToMap(
				statement, innerObject, innerObjectName);

			innerMap.put(innerPropertyName, variableResult);

			map.put(innerObjectName, innerMap);
		}

		return map;
	}

	private Object _addVariableStatementList(
			Statement variableStatement, List<Object> resultList,
			List<Object> results)
		throws Exception {

		for (Object object : resultList) {
			List<Object> listObject = _convertObjectToList(object);

			if (listObject != null) {
				Object value = _addVariableStatementList(
					variableStatement, listObject, results);

				results.add(value);
			}
			else {
				Object value = _addVariableStatement(variableStatement, object);

				results.add(value);
			}
		}

		return results;
	}

	private List<Object> _convertObjectToList(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof List) {
			return (List<Object>)object;
		}

		if (object instanceof Iterable) {
			List<Object> list = new ArrayList<>();

			Iterable<?> iterable = (Iterable<?>)object;

			Iterator<?> iterator = iterable.iterator();

			while (iterator.hasNext()) {
				list.add(iterator.next());
			}

			return list;
		}

		Class<?> clazz = object.getClass();

		if (!clazz.isArray()) {
			return null;
		}

		Class<?> componentType = clazz.getComponentType();

		if (!componentType.isPrimitive()) {
			return ListUtil.fromArray((Object[])object);
		}

		List<Object> list = new ArrayList<>();

		for (int i = 0; i < Array.getLength(object); i++) {
			list.add(Array.get(object, i));
		}

		return list;
	}

	private Map<String, Object> _convertObjectToMap(
		final Statement statement, Object object, final String prefix) {

		if (object instanceof Map) {
			return (Map<String, Object>)object;
		}

		JsonContext jsonContext = _jsonSerializer.createJsonContext(null);
		final Map<String, Object> map = new LinkedHashMap<>();

		BeanSerializer beanSerializer = new BeanSerializer(
			jsonContext, object) {

			@Override
			protected void onSerializableProperty(
				String propertyName,
				@SuppressWarnings("rawtypes") Class propertyClass,
				Object value) {

				map.put(propertyName, value);

				String include = propertyName;

				if (prefix != null) {
					include = prefix + "." + include;
				}

				_addInclude(statement, include);
			}

		};

		beanSerializer.serialize();

		return map;
	}

	private Object _executeStatement(Statement statement) throws Exception {
		JSONWebServiceAction jsonWebServiceAction =
			_jsonWebServiceActionsManager.getJSONWebServiceAction(
				_httpServletRequest, statement.getMethod(), null,
				statement.getParameterMap());

		Object result = jsonWebServiceAction.invoke();

		result = _filterResult(statement, result);

		List<Statement> variableStatements = statement.getVariableStatements();

		if (variableStatements == null) {
			return result;
		}

		for (Statement variableStatement : variableStatements) {
			boolean innerStatement = variableStatement.isInner();

			if (innerStatement) {
				result = variableStatement.push(result);
			}

			List<Object> resultList = _convertObjectToList(result);

			if (resultList != null) {
				result = _addVariableStatementList(
					variableStatement, resultList, new ArrayList<Object>());

				variableStatement.setExecuted(true);

				if (innerStatement) {
					result = variableStatement.pop(result);
				}
			}
			else {
				if (innerStatement) {
					result = variableStatement.pop(result);
				}

				result = _addVariableStatement(variableStatement, result);

				variableStatement.setExecuted(true);
			}
		}

		return result;
	}

	private Object _filterResult(Statement statement, Object result) {
		List<Object> resultList = _convertObjectToList(result);

		if (resultList != null) {
			result = _filterResultList(
				statement, resultList, new ArrayList<Object>());
		}
		else {
			result = _filterResultObject(statement, result);
		}

		return result;
	}

	private Object _filterResultList(
		Statement statement, List<Object> resultList, List<Object> results) {

		for (Object object : resultList) {
			Object value = _filterResultObject(statement, object);

			results.add(value);
		}

		return results;
	}

	private Object _filterResultObject(Statement statement, Object result) {
		if (result == null) {
			return result;
		}

		String[] whitelist = statement.getWhitelist();

		if (whitelist == null) {
			return result;
		}

		Map<String, Object> map = _convertObjectToMap(statement, result, null);

		Map<String, Object> whitelistMap = new HashMap<>();

		for (String key : whitelist) {
			Object value = map.get(key);

			whitelistMap.put(key, value);
		}

		return whitelistMap;
	}

	private Statement _parseStatement(
		Statement parentStatement, String assignment,
		Map<String, Object> statementBody) {

		Statement statement = new Statement(parentStatement);

		_statements.add(statement);

		int x = assignment.indexOf(StringPool.EQUAL);

		if (x == -1) {
			statement.setMethod(assignment.trim());
		}
		else {
			String name = StringUtil.trim(assignment.substring(0, x));

			int y = name.indexOf(StringPool.OPEN_BRACKET);

			if (y != -1) {
				String whitelistString = name.substring(
					y + 1, name.length() - 1);

				String[] whiteList = StringUtil.split(whitelistString);

				for (int i = 0; i < whiteList.length; i++) {
					whiteList[i] = whiteList[i].trim();
				}

				statement.setWhitelist(whiteList);

				name = name.substring(0, y);
			}

			statement.setName(name);

			statement.setMethod(StringUtil.trim(assignment.substring(x + 1)));
		}

		HashMap<String, Object> parameterMap = new HashMap<>();

		statement.setParameterMap(parameterMap);

		for (Map.Entry<String, Object> entry : statementBody.entrySet()) {
			String key = entry.getKey();

			if (key.startsWith(StringPool.AT)) {
				String value = (String)entry.getValue();

				List<Map.Entry<String, String>> flags = statement.getFlags();

				if (flags == null) {
					flags = new ArrayList<>();

					statement.setFlags(flags);
				}

				Map.Entry<String, String> flag =
					new AbstractMap.SimpleImmutableEntry<>(
						key.substring(1), value);

				flags.add(flag);
			}
			else if (key.startsWith(StringPool.DOLLAR) || key.contains(".$")) {
				Map<String, Object> map = (Map<String, Object>)entry.getValue();

				List<Statement> variableStatements =
					statement.getVariableStatements();

				if (variableStatements == null) {
					variableStatements = new ArrayList<>();

					statement.setVariableStatements(variableStatements);
				}

				Statement variableStatement = _parseStatement(
					statement, key, map);

				variableStatements.add(variableStatement);
			}
			else {
				parameterMap.put(
					CamelCaseUtil.normalizeCamelCase(key), entry.getValue());
			}
		}

		return statement;
	}

	private Object _populateFlags(Statement statement, Object result) {
		List<Object> listResult = _convertObjectToList(result);

		if (listResult != null) {
			result = _populateFlagsList(
				statement.getName(), listResult, new ArrayList<Object>());
		}
		else {
			_populateFlagsObject(statement.getName(), result);
		}

		return result;
	}

	private List<Object> _populateFlagsList(
		String name, List<Object> list, List<Object> results) {

		for (Object object : list) {
			List<Object> listObject = _convertObjectToList(object);

			if (listObject != null) {
				Object value = _populateFlagsList(name, listObject, results);

				results.add(value);
			}
			else {
				_populateFlagsObject(name, object);

				results.add(object);
			}
		}

		return results;
	}

	private void _populateFlagsObject(String name, Object object) {
		if (name == null) {
			return;
		}

		String pushedName = null;

		int index = name.indexOf(CharPool.PERIOD);

		if (index != -1) {
			pushedName = name.substring(0, index + 1);
		}

		name = name.concat(StringPool.PERIOD);

		for (Statement statement : _statements) {
			if (statement.isExecuted()) {
				continue;
			}

			List<Map.Entry<String, String>> flags = statement.getFlags();

			if (flags == null) {
				continue;
			}

			for (Map.Entry<String, String> flag : flags) {
				String value = flag.getValue();

				if (value == null) {
					continue;
				}

				if (value.startsWith(name) &&
					(value.indexOf(CharPool.PERIOD, name.length()) == -1)) {

					Map<String, Object> parameterMap =
						statement.getParameterMap();

					Object propertyValue = BeanUtil.declared.getProperty(
						object, value.substring(name.length()));

					parameterMap.put(flag.getKey(), propertyValue);
				}
				else if (statement.isPushed() && value.startsWith(pushedName)) {
					Map<String, Object> parameterMap =
						statement.getParameterMap();

					Object propertyValue = BeanUtil.declared.getProperty(
						statement._pushTarget,
						value.substring(pushedName.length()));

					parameterMap.put(flag.getKey(), propertyValue);
				}
			}
		}
	}

	private static final JsonSerializer _jsonSerializer = new JsonSerializer();

	private final String _command;
	private final HttpServletRequest _httpServletRequest;
	private List<String> _includes;
	private final JSONWebServiceActionsManager _jsonWebServiceActionsManager;
	private final List<Statement> _statements = new ArrayList<>();

	private static class Statement {

		public List<Map.Entry<String, String>> getFlags() {
			return _flags;
		}

		public String getMethod() {
			return _method;
		}

		public String getName() {
			return _name;
		}

		public Map<String, Object> getParameterMap() {
			return _parameterMap;
		}

		public Statement getParentStatement() {
			return _parentStatement;
		}

		public List<Statement> getVariableStatements() {
			return _variableStatements;
		}

		public String[] getWhitelist() {
			return _whitelist;
		}

		public boolean isExecuted() {
			return _executed;
		}

		public boolean isInner() {
			return _inner;
		}

		public boolean isPushed() {
			if (_pushTarget != null) {
				return true;
			}

			return false;
		}

		public Object pop(Object result) {
			if (_pushTarget == null) {
				return null;
			}

			Statement statement = getParentStatement();

			String statementName = statement.getName();

			int index = statementName.lastIndexOf('.');

			String beanName = statementName.substring(index + 1);

			statementName = statementName.substring(0, index);

			statement.setName(statementName);

			setName(beanName + StringPool.PERIOD + getName());

			BeanUtil.declared.setProperty(_pushTarget, beanName, result);

			result = _pushTarget;

			_pushTarget = null;

			return result;
		}

		public Object push(Object result) {
			if (_parentStatement == null) {
				return null;
			}

			_pushTarget = result;

			Statement statement = getParentStatement();

			String variableName = getName();

			int index = variableName.indexOf(".$");

			String beanName = variableName.substring(0, index);

			result = BeanUtil.declared.getProperty(result, beanName);

			statement.setName(
				statement.getName() + StringPool.PERIOD + beanName);

			variableName = variableName.substring(index + 1);

			setName(variableName);

			return result;
		}

		public void setExecuted(boolean executed) {
			_executed = executed;
		}

		public void setFlags(List<Map.Entry<String, String>> flags) {
			_flags = flags;
		}

		public void setMethod(String method) {
			_method = method;
		}

		public void setName(String name) {
			if (name.contains(".$")) {
				_inner = true;
			}
			else {
				_inner = false;
			}

			_name = name;
		}

		public void setParameterMap(Map<String, Object> parameterMap) {
			_parameterMap = parameterMap;
		}

		public void setVariableStatements(List<Statement> variableStatements) {
			_variableStatements = variableStatements;
		}

		public void setWhitelist(String[] whitelist) {
			_whitelist = whitelist;
		}

		private Statement(Statement parentStatement) {
			_parentStatement = parentStatement;
		}

		private boolean _executed;
		private List<Map.Entry<String, String>> _flags;
		private boolean _inner;
		private String _method;
		private String _name;
		private Map<String, Object> _parameterMap;
		private Statement _parentStatement;
		private Object _pushTarget;
		private List<Statement> _variableStatements;
		private String[] _whitelist;

	}

}
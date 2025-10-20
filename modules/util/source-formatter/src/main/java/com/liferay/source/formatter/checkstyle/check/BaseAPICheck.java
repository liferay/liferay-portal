/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.util.PortalJSONObjectUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public abstract class BaseAPICheck extends BaseCheck {

	protected Map<String, Set<Integer>> addTypeName(
		Map<String, Set<Integer>> typeNamesMap, String typeName,
		int lineNumber) {

		return _addTypeName(typeNamesMap, typeName, lineNumber, null);
	}

	protected List<ConstructorCall> getConstructorCalls(
		DetailAST detailAST, List<String> excludeImportNames,
		boolean skipDeprecated) {

		List<ConstructorCall> constructorCalls = new ArrayList<>();

		List<DetailAST> literalNewDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.LITERAL_NEW);

		for (DetailAST literalNewDetailAST : literalNewDetailASTs) {
			if (skipDeprecated &&
				(hasDeprecatedParent(literalNewDetailAST) ||
				 hasSuppressDeprecationWarningsAnnotation(
					 literalNewDetailAST))) {

				continue;
			}

			DetailAST lparenDetailAST = literalNewDetailAST.findFirstToken(
				TokenTypes.LPAREN);

			if (lparenDetailAST == null) {
				continue;
			}

			String constructorTypeName = _getConstructorTypeName(
				literalNewDetailAST);

			if ((constructorTypeName != null) &&
				constructorTypeName.startsWith("com.liferay.") &&
				!excludeImportNames.contains(constructorTypeName)) {

				constructorCalls.add(
					new ConstructorCall(
						constructorTypeName,
						_getParameterTypeNames(literalNewDetailAST),
						literalNewDetailAST.getLineNo()));
			}
		}

		return constructorCalls;
	}

	protected List<JSONObject> getConstructorJSONObjects(
		ConstructorCall constructorCall, JSONObject javaClassesJSONObject) {

		List<JSONObject> constructorJSONObjects = new ArrayList<>();

		JSONObject classJSONObject = javaClassesJSONObject.getJSONObject(
			constructorCall.getTypeName());

		if (classJSONObject == null) {
			return constructorJSONObjects;
		}

		JSONArray constructorsJSONArray = classJSONObject.getJSONArray(
			"constructors");

		if (constructorsJSONArray == null) {
			return constructorJSONObjects;
		}

		List<String> parameterTypeNames =
			constructorCall.getParameterTypeNames();

		Iterator<JSONObject> iterator = constructorsJSONArray.iterator();

		outerLoop:
		while (iterator.hasNext()) {
			JSONObject constructorJSONObject = iterator.next();

			JSONArray parametersJSONArray = constructorJSONObject.getJSONArray(
				"parameters");

			if (parametersJSONArray == null) {
				if (parameterTypeNames.isEmpty()) {
					constructorJSONObjects.add(constructorJSONObject);
				}

				continue;
			}

			if (parametersJSONArray.length() != parameterTypeNames.size()) {
				continue;
			}

			for (int i = 0; i < parameterTypeNames.size(); i++) {
				String actualTypeName = parameterTypeNames.get(i);
				String methodTypeName = parametersJSONArray.getString(i);

				if (Validator.isNotNull(actualTypeName) &&
					!StringUtil.equalsIgnoreCase(
						actualTypeName, methodTypeName) &&
					!methodTypeName.equals("Object") &&
					(!_isNumeric(actualTypeName) ||
					 !_isNumeric(methodTypeName))) {

					continue outerLoop;
				}
			}

			constructorJSONObjects.add(constructorJSONObject);
		}

		return constructorJSONObjects;
	}

	protected synchronized JSONObject getJavaClassesJSONObject(String version)
		throws Exception {

		JSONObject javaClassesJSONObject = _javaClassesJSONObjectMap.get(
			version);

		if (javaClassesJSONObject != null) {
			return javaClassesJSONObject;
		}

		JSONObject portalJSONObject = null;

		if (version.equals(getBaseDirName())) {
			portalJSONObject = PortalJSONObjectUtil.getPortalJSONObject(
				version);
		}
		else {
			portalJSONObject =
				PortalJSONObjectUtil.getPortalJSONObjectByVersion(version);
		}

		if (portalJSONObject.has("javaClasses")) {
			javaClassesJSONObject = portalJSONObject.getJSONObject(
				"javaClasses");
		}
		else {
			javaClassesJSONObject = new JSONObjectImpl();
		}

		_javaClassesJSONObjectMap.put(version, javaClassesJSONObject);

		return javaClassesJSONObject;
	}

	protected List<MethodCall> getMethodCalls(
		DetailAST detailAST, List<String> excludeImportNames,
		boolean skipDeprecated) {

		List<MethodCall> methodCalls = new ArrayList<>();

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			if (skipDeprecated &&
				(hasDeprecatedParent(methodCallDetailAST) ||
				 hasSuppressDeprecationWarningsAnnotation(
					 methodCallDetailAST))) {

				continue;
			}

			String methodName = null;
			String variableTypeName = null;

			DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				methodName = firstChildDetailAST.getText();
				variableTypeName = StringBundler.concat(
					getPackageName(detailAST), ".",
					JavaSourceUtil.getClassName(getAbsolutePath()));
			}
			else if (firstChildDetailAST.getType() == TokenTypes.DOT) {
				DetailAST firstGrandChildDetailAST =
					firstChildDetailAST.getFirstChild();
				DetailAST lastGrandChildDetailAST =
					firstChildDetailAST.getLastChild();

				if ((firstGrandChildDetailAST.getType() != TokenTypes.IDENT) ||
					(lastGrandChildDetailAST.getType() != TokenTypes.IDENT)) {

					continue;
				}

				methodName = lastGrandChildDetailAST.getText();

				variableTypeName = getVariableTypeName(
					firstChildDetailAST, firstGrandChildDetailAST.getText(),
					false, false, true);
			}
			else {
				continue;
			}

			if (variableTypeName.startsWith("com.liferay.") &&
				!excludeImportNames.contains(variableTypeName)) {

				methodCalls.add(
					new MethodCall(
						methodName, variableTypeName,
						_getParameterTypeNames(methodCallDetailAST),
						methodCallDetailAST.getLineNo()));
			}
		}

		return methodCalls;
	}

	protected List<JSONObject> getMethodJSONObjects(
		MethodCall methodCall, JSONObject javaClassesJSONObject) {

		List<JSONObject> methodJSONObjects = new ArrayList<>();

		JSONObject classJSONObject = javaClassesJSONObject.getJSONObject(
			methodCall.getVariableTypeName());

		if (classJSONObject == null) {
			return methodJSONObjects;
		}

		JSONArray methodsJSONArray = classJSONObject.getJSONArray("methods");

		if (methodsJSONArray != null) {
			String methodName = methodCall.getName();
			List<String> parameterTypeNames =
				methodCall.getParameterTypeNames();

			Iterator<JSONObject> iterator = methodsJSONArray.iterator();

			outerLoop:
			while (iterator.hasNext()) {
				JSONObject methodJSONObject = iterator.next();

				if (!methodName.equals(methodJSONObject.getString("name"))) {
					continue;
				}

				JSONArray parametersJSONArray = methodJSONObject.getJSONArray(
					"parameters");

				if (parametersJSONArray == null) {
					if (parameterTypeNames.isEmpty()) {
						methodJSONObjects.add(methodJSONObject);
					}

					continue;
				}

				if (parametersJSONArray.length() != parameterTypeNames.size()) {
					continue;
				}

				for (int i = 0; i < parameterTypeNames.size(); i++) {
					String actualTypeName = parameterTypeNames.get(i);
					String methodTypeName = parametersJSONArray.getString(i);

					if (Validator.isNotNull(actualTypeName) &&
						!StringUtil.equalsIgnoreCase(
							actualTypeName, methodTypeName) &&
						!methodTypeName.equals("Object") &&
						(!_isNumeric(actualTypeName) ||
						 !_isNumeric(methodTypeName))) {

						continue outerLoop;
					}
				}

				methodJSONObjects.add(methodJSONObject);
			}
		}

		JSONArray extendedClassNamesJSONArray = classJSONObject.getJSONArray(
			"extendedClassNames");

		if (extendedClassNamesJSONArray != null) {
			Iterator<String> iterator = extendedClassNamesJSONArray.iterator();

			while (iterator.hasNext()) {
				String extendedClassName = iterator.next();

				if (extendedClassName.startsWith("com.liferay.")) {
					methodJSONObjects.addAll(
						getMethodJSONObjects(
							new MethodCall(
								methodCall.getName(), extendedClassName,
								methodCall.getParameterTypeNames(),
								methodCall.getLineNumber()),
							javaClassesJSONObject));
				}
			}
		}

		return methodJSONObjects;
	}

	protected Map<String, Set<Integer>> getTypeNamesMap(
		DetailAST detailAST, List<String> excludeImportNames,
		boolean skipDeprecated) {

		Map<String, Set<Integer>> typeNamesMap = new HashMap<>();

		List<DetailAST> clauseDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.EXTENDS_CLAUSE,
			TokenTypes.IMPLEMENTS_CLAUSE);

		for (DetailAST clauseDetailAST : clauseDetailASTs) {
			if (skipDeprecated &&
				(hasDeprecatedParent(clauseDetailAST) ||
				 hasSuppressDeprecationWarningsAnnotation(clauseDetailAST))) {

				continue;
			}

			List<DetailAST> childDetailASTs = getAllChildTokens(
				clauseDetailAST, false, TokenTypes.DOT, TokenTypes.IDENT);

			for (DetailAST childDetailAST : childDetailASTs) {
				if (childDetailAST.getType() == TokenTypes.IDENT) {
					typeNamesMap = _addTypeName(
						typeNamesMap,
						getVariableTypeName(
							childDetailAST, childDetailAST.getText(), false,
							false, true),
						childDetailAST.getLineNo(), excludeImportNames);

					continue;
				}

				DetailAST firstChildDetailAST = childDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
					typeNamesMap = _addTypeName(
						typeNamesMap,
						getVariableTypeName(
							firstChildDetailAST, firstChildDetailAST.getText(),
							false, false, true),
						firstChildDetailAST.getLineNo(), excludeImportNames);
				}
				else {
					FullIdent fullIdent = FullIdent.createFullIdent(
						childDetailAST);

					typeNamesMap = _addTypeName(
						typeNamesMap, fullIdent.getText(),
						firstChildDetailAST.getLineNo(), excludeImportNames);
				}
			}
		}

		List<DetailAST> typeDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.TYPE);

		for (DetailAST typeDetailAST : typeDetailASTs) {
			if (!skipDeprecated ||
				(!hasDeprecatedParent(typeDetailAST) &&
				 !hasSuppressDeprecationWarningsAnnotation(typeDetailAST))) {

				typeNamesMap = _addTypeName(
					typeNamesMap,
					getTypeName(typeDetailAST, false, false, true),
					typeDetailAST.getLineNo(), excludeImportNames);
			}
		}

		return typeNamesMap;
	}

	protected List<VariableCall> getVariableCalls(
		DetailAST detailAST, List<String> excludeImportNames,
		boolean skipDeprecated) {

		List<VariableCall> variableCalls = new ArrayList<>();

		List<DetailAST> dotDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.DOT);

		for (DetailAST dotDetailAST : dotDetailASTs) {
			if (skipDeprecated &&
				(hasDeprecatedParent(dotDetailAST) ||
				 hasSuppressDeprecationWarningsAnnotation(dotDetailAST))) {

				continue;
			}

			DetailAST firstChildDetailAST = dotDetailAST.getFirstChild();
			DetailAST lastChildDetailAST = dotDetailAST.getLastChild();

			if ((firstChildDetailAST.getType() != TokenTypes.IDENT) ||
				(lastChildDetailAST.getType() != TokenTypes.IDENT)) {

				continue;
			}

			String variableTypeName = getVariableTypeName(
				firstChildDetailAST, firstChildDetailAST.getText(), false,
				false, true);

			if (!variableTypeName.startsWith("com.liferay.") ||
				excludeImportNames.contains(variableTypeName)) {

				continue;
			}

			variableCalls.add(
				new VariableCall(
					lastChildDetailAST.getText(), variableTypeName,
					lastChildDetailAST.getLineNo()));
		}

		return variableCalls;
	}

	protected JSONObject getVariableJSONObject(
		VariableCall variableCall, JSONObject javaClassesJSONObject) {

		JSONObject classJSONObject = javaClassesJSONObject.getJSONObject(
			variableCall.getTypeName());

		if (classJSONObject == null) {
			return null;
		}

		JSONArray variablesJSONArray = classJSONObject.getJSONArray(
			"variables");

		if (variablesJSONArray == null) {
			return null;
		}

		Iterator<JSONObject> iterator = variablesJSONArray.iterator();

		while (iterator.hasNext()) {
			JSONObject variableJSONObject = iterator.next();

			if (Objects.equals(
					variableCall.getName(),
					variableJSONObject.getString("name"))) {

				return variableJSONObject;
			}
		}

		return null;
	}

	protected boolean hasDeprecatedParent(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			if (parentDetailAST == null) {
				return false;
			}

			if (((parentDetailAST.getType() == TokenTypes.CTOR_DEF) ||
				 (parentDetailAST.getType() == TokenTypes.METHOD_DEF) ||
				 (parentDetailAST.getType() == TokenTypes.VARIABLE_DEF)) &&
				AnnotationUtil.containsAnnotation(
					parentDetailAST, "Deprecated")) {

				return true;
			}

			parentDetailAST = parentDetailAST.getParent();
		}
	}

	protected boolean hasSuppressDeprecationWarningsAnnotation(
		DetailAST detailAST) {

		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			if (parentDetailAST == null) {
				return false;
			}

			if (parentDetailAST.findFirstToken(TokenTypes.MODIFIERS) != null) {
				DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
					parentDetailAST, "SuppressWarnings");

				if (annotationDetailAST != null) {
					List<DetailAST> literalStringDetailASTs = getAllChildTokens(
						annotationDetailAST, true, TokenTypes.STRING_LITERAL);

					for (DetailAST literalStringDetailAST :
							literalStringDetailASTs) {

						String s = literalStringDetailAST.getText();

						if (s.equals("\"deprecation\"")) {
							return true;
						}
					}
				}
			}

			parentDetailAST = parentDetailAST.getParent();
		}
	}

	protected class ConstructorCall {

		public ConstructorCall(
			String typeName, List<String> parameterTypeNames, int lineNumber) {

			_typeName = typeName;
			_parameterTypeNames = parameterTypeNames;
			_lineNumber = lineNumber;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public List<String> getParameterTypeNames() {
			return _parameterTypeNames;
		}

		public String getTypeName() {
			return _typeName;
		}

		private int _lineNumber;
		private final List<String> _parameterTypeNames;
		private final String _typeName;

	}

	protected class MethodCall {

		public MethodCall(
			String name, String variableTypeName,
			List<String> parameterTypeNames, int lineNumber) {

			_name = name;
			_variableTypeName = variableTypeName;
			_parameterTypeNames = parameterTypeNames;
			_lineNumber = lineNumber;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public String getName() {
			return _name;
		}

		public List<String> getParameterTypeNames() {
			return _parameterTypeNames;
		}

		public String getVariableTypeName() {
			return _variableTypeName;
		}

		private int _lineNumber;
		private final String _name;
		private final List<String> _parameterTypeNames;
		private final String _variableTypeName;

	}

	protected class VariableCall {

		public VariableCall(String name, String typeName, int lineNumber) {
			_name = name;
			_typeName = typeName;
			_lineNumber = lineNumber;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public String getName() {
			return _name;
		}

		public String getTypeName() {
			return _typeName;
		}

		private int _lineNumber;
		private final String _name;
		private final String _typeName;

	}

	private Map<String, Set<Integer>> _addTypeName(
		Map<String, Set<Integer>> typeNamesMap, String typeName, int lineNumber,
		List<String> excludeImportNames) {

		if ((excludeImportNames != null) &&
			(excludeImportNames.contains(typeName) ||
			 !typeName.startsWith("com.liferay."))) {

			return typeNamesMap;
		}

		Set<Integer> lineNumbers = typeNamesMap.get(typeName);

		if (lineNumbers == null) {
			lineNumbers = new HashSet<>();
		}

		lineNumbers.add(lineNumber);

		typeNamesMap.put(typeName, lineNumbers);

		return typeNamesMap;
	}

	private String _getConstructorTypeName(DetailAST literalNewDetailAST) {
		DetailAST identDetailAST = literalNewDetailAST.findFirstToken(
			TokenTypes.IDENT);

		if (identDetailAST != null) {
			return StringBundler.concat(
				JavaSourceUtil.getPackageName(
					identDetailAST.getText(), getPackageName(identDetailAST),
					getImportNames(identDetailAST)),
				".", identDetailAST.getText());
		}

		DetailAST dotDetailAST = literalNewDetailAST.findFirstToken(
			TokenTypes.DOT);

		if (dotDetailAST == null) {
			return null;
		}

		identDetailAST = dotDetailAST.findFirstToken(TokenTypes.IDENT);

		if (identDetailAST != null) {
			return StringBundler.concat(
				JavaSourceUtil.getPackageName(
					identDetailAST.getText(), getPackageName(identDetailAST),
					getImportNames(identDetailAST)),
				".", identDetailAST.getText());
		}

		return null;
	}

	private String _getParameterTypeName(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.IDENT) {
			return getVariableTypeName(
				detailAST, detailAST.getText(), true, true, true);
		}

		if (detailAST.getType() == TokenTypes.STRING_LITERAL) {
			return "java.lang.String";
		}

		if (detailAST.getType() == TokenTypes.CHAR_LITERAL) {
			return "char";
		}

		if ((detailAST.getType() == TokenTypes.LITERAL_FALSE) ||
			(detailAST.getType() == TokenTypes.LITERAL_TRUE) ||
			(detailAST.getType() == TokenTypes.LNOT)) {

			return "boolean";
		}

		if (detailAST.getType() == TokenTypes.PLUS) {
			DetailAST stringLiteralDetailAST = detailAST.findFirstToken(
				TokenTypes.STRING_LITERAL);

			if (stringLiteralDetailAST != null) {
				return "java.lang.String";
			}

			return null;
		}

		if (detailAST.getType() == TokenTypes.LITERAL_NEW) {
			String parameterTypeName = null;

			DetailAST firstChildDetailAST = detailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				parameterTypeName = getVariableTypeName(
					firstChildDetailAST, firstChildDetailAST.getText(), true,
					true, true);
			}
			else {
				parameterTypeName = _getParameterTypeName(firstChildDetailAST);
			}

			if (parameterTypeName == null) {
				return parameterTypeName;
			}

			DetailAST curDetailAST = firstChildDetailAST.getNextSibling();

			while (true) {
				if (curDetailAST.getType() != TokenTypes.ARRAY_DECLARATOR) {
					return parameterTypeName;
				}

				parameterTypeName += "[]";

				curDetailAST = curDetailAST.getFirstChild();
			}
		}

		if (detailAST.getType() == TokenTypes.INDEX_OP) {
			String parameterTypeName = _getParameterTypeName(
				detailAST.getFirstChild());

			if (parameterTypeName != null) {
				parameterTypeName = StringUtil.replaceLast(
					parameterTypeName, "[]", "");
			}

			return parameterTypeName;
		}

		if (detailAST.getType() == TokenTypes.TYPECAST) {
			return getTypeName(detailAST, true, true, true);
		}

		if (ArrayUtil.contains(
				UNARY_OPERATOR_TOKEN_TYPES, detailAST.getType())) {

			return _getParameterTypeName(detailAST.getFirstChild());
		}

		if (detailAST.getType() == TokenTypes.LITERAL_THIS) {
			DetailAST parentDetailAST = getParentWithTokenType(
				detailAST, TokenTypes.CLASS_DEF);

			if (parentDetailAST == null) {
				return null;
			}

			DetailAST identDetailAST = parentDetailAST.findFirstToken(
				TokenTypes.IDENT);

			return identDetailAST.getText();
		}

		if (ArrayUtil.contains(
				CONDITIONAL_OPERATOR_TOKEN_TYPES, detailAST.getType()) ||
			ArrayUtil.contains(
				RELATIONAL_OPERATOR_TOKEN_TYPES, detailAST.getType())) {

			return "boolean";
		}

		if (detailAST.getType() == TokenTypes.NUM_DOUBLE) {
			return "double";
		}

		if (detailAST.getType() == TokenTypes.NUM_FLOAT) {
			return "float";
		}

		if (detailAST.getType() == TokenTypes.NUM_INT) {
			return "int";
		}

		if (detailAST.getType() == TokenTypes.NUM_LONG) {
			return "long";
		}

		if ((detailAST.getType() == TokenTypes.LITERAL_BOOLEAN) ||
			(detailAST.getType() == TokenTypes.LITERAL_BYTE) ||
			(detailAST.getType() == TokenTypes.LITERAL_DOUBLE) ||
			(detailAST.getType() == TokenTypes.LITERAL_FLOAT) ||
			(detailAST.getType() == TokenTypes.LITERAL_INT) ||
			(detailAST.getType() == TokenTypes.LITERAL_LONG) ||
			(detailAST.getType() == TokenTypes.LITERAL_SHORT)) {

			return detailAST.getText();
		}

		if (detailAST.getType() == TokenTypes.QUESTION) {
			return _getParameterTypeName(detailAST.getLastChild());
		}

		return null;
	}

	private List<String> _getParameterTypeNames(DetailAST detailAST) {
		List<String> parameterTypeNames = new ArrayList<>();

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		for (DetailAST exprDetailAST : exprDetailASTs) {
			parameterTypeNames.add(
				_getParameterTypeName(exprDetailAST.getFirstChild()));
		}

		return parameterTypeNames;
	}

	private boolean _isNumeric(String typeName) {
		if (typeName.equals("double") || typeName.equals("float") ||
			typeName.equals("int") || typeName.equals("long")) {

			return true;
		}

		return false;
	}

	private final Map<String, JSONObject> _javaClassesJSONObjectMap =
		new HashMap<>();

}
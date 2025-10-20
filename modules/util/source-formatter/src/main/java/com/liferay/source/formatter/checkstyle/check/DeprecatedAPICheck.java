/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.source.formatter.check.util.SourceUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public class DeprecatedAPICheck extends BaseAPICheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST != null) ||
			AnnotationUtil.containsAnnotation(detailAST, "Deprecated")) {

			return;
		}

		try {
			JSONObject javaClassesJSONObject = getJavaClassesJSONObject(
				getBaseDirName());

			List<String> deprecatedImportNames = getDeprecatedImportNames(
				detailAST, javaClassesJSONObject);

			for (String deprecatedImportName : deprecatedImportNames) {
				if (hasUndeprecatedReference(detailAST, deprecatedImportName)) {
					log(
						getImportLineNumber(detailAST, deprecatedImportName),
						_MSG_DEPRECATED_TYPE_CALL, deprecatedImportName);
				}
			}

			_checkDeprecatedConstructors(
				detailAST, deprecatedImportNames, javaClassesJSONObject);
			_checkDeprecatedMethods(
				detailAST, deprecatedImportNames, javaClassesJSONObject);
			_checkDeprecatedTypes(
				detailAST, deprecatedImportNames, javaClassesJSONObject);
			_checkDeprecatedVariables(
				detailAST, deprecatedImportNames, javaClassesJSONObject);
		}
		catch (Exception exception) {
		}
	}

	protected List<ConstructorCall> getDeprecatedConstructorCalls(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		List<ConstructorCall> deprecatedConstructorCalls = new ArrayList<>();

		List<ConstructorCall> constructorCalls = getConstructorCalls(
			detailAST, deprecatedImportNames, true);

		outerLoop:
		for (ConstructorCall constructorCall : constructorCalls) {
			List<JSONObject> constructorJSONObjects = getConstructorJSONObjects(
				constructorCall, javaClassesJSONObject);

			if (constructorJSONObjects.isEmpty()) {
				continue;
			}

			for (JSONObject constructorJSONObject : constructorJSONObjects) {
				if (!constructorJSONObject.has("deprecated")) {
					continue outerLoop;
				}
			}

			deprecatedConstructorCalls.add(constructorCall);
		}

		return deprecatedConstructorCalls;
	}

	protected List<String> getDeprecatedImportNames(
		DetailAST detailAST, JSONObject javaClassesJSONObject) {

		List<String> deprecatedImportNames = new ArrayList<>();

		List<String> importNames = getImportNames(detailAST);

		for (String importName : importNames) {
			JSONObject classJSONObject = javaClassesJSONObject.getJSONObject(
				importName);

			if ((classJSONObject != null) &&
				classJSONObject.has("deprecated")) {

				deprecatedImportNames.add(importName);
			}
		}

		return deprecatedImportNames;
	}

	protected List<MethodCall> getDeprecatedMethodCalls(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		List<MethodCall> deprecatedMethodCalls = new ArrayList<>();

		List<MethodCall> methodCalls = getMethodCalls(
			detailAST, deprecatedImportNames, true);

		outerLoop:
		for (MethodCall methodCall : methodCalls) {
			List<JSONObject> methodJSONObjects = getMethodJSONObjects(
				methodCall, javaClassesJSONObject);

			if (methodJSONObjects.isEmpty()) {
				continue;
			}

			for (JSONObject methodJSONObject : methodJSONObjects) {
				if (!methodJSONObject.has("deprecated")) {
					continue outerLoop;
				}
			}

			deprecatedMethodCalls.add(methodCall);
		}

		return deprecatedMethodCalls;
	}

	protected Map<String, Set<Integer>> getDeprecatedTypeNamesMap(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		Map<String, Set<Integer>> deprecatedTypeNamesMap = new HashMap<>();

		Map<String, Set<Integer>> typeNamesMap = getTypeNamesMap(
			detailAST, deprecatedImportNames, true);

		for (Map.Entry<String, Set<Integer>> entry : typeNamesMap.entrySet()) {
			String typeName = entry.getKey();

			JSONObject classJSONObject = javaClassesJSONObject.getJSONObject(
				typeName);

			if ((classJSONObject != null) &&
				classJSONObject.has("deprecated")) {

				Set<Integer> lineNumbers = entry.getValue();

				for (int lineNumber : lineNumbers) {
					deprecatedTypeNamesMap = addTypeName(
						deprecatedTypeNamesMap, typeName, lineNumber);
				}
			}
		}

		return deprecatedTypeNamesMap;
	}

	protected List<VariableCall> getDeprecatedVariableCalls(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		List<VariableCall> deprecatedVariableCalls = new ArrayList<>();

		List<VariableCall> variableCalls = getVariableCalls(
			detailAST, deprecatedImportNames, true);

		for (VariableCall variableCall : variableCalls) {
			JSONObject variableJSONObject = getVariableJSONObject(
				variableCall, javaClassesJSONObject);

			if ((variableJSONObject != null) &&
				variableJSONObject.has("deprecated")) {

				deprecatedVariableCalls.add(variableCall);
			}
		}

		return deprecatedVariableCalls;
	}

	protected int getImportLineNumber(DetailAST detailAST, String importName) {
		FileContents fileContents = getFileContents();

		FileText fileText = fileContents.getText();

		String content = (String)fileText.getFullText();

		int x = content.indexOf(
			StringBundler.concat("import ", importName, ";"));

		if (x != -1) {
			return SourceUtil.getLineNumber(content, x);
		}

		return detailAST.getLineNo();
	}

	protected boolean hasUndeprecatedReference(
		DetailAST detailAST, String importName) {

		int x = importName.lastIndexOf(".");

		String className = importName.substring(x + 1);

		List<DetailAST> identDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.IDENT);

		for (DetailAST identDetailAST : identDetailASTs) {
			if (className.equals(identDetailAST.getText()) &&
				!hasDeprecatedParent(identDetailAST) &&
				!hasSuppressDeprecationWarningsAnnotation(identDetailAST)) {

				return true;
			}
		}

		return false;
	}

	private void _checkDeprecatedConstructors(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		List<ConstructorCall> deprecatedConstructorCalls =
			getDeprecatedConstructorCalls(
				detailAST, deprecatedImportNames, javaClassesJSONObject);

		for (ConstructorCall deprecatedConstructorCall :
				deprecatedConstructorCalls) {

			log(
				deprecatedConstructorCall.getLineNumber(),
				_MSG_DEPRECATED_CONSTRUCTOR_CALL,
				deprecatedConstructorCall.getTypeName());
		}
	}

	private void _checkDeprecatedMethods(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		List<MethodCall> deprecatedMethodCalls = getDeprecatedMethodCalls(
			detailAST, deprecatedImportNames, javaClassesJSONObject);

		for (MethodCall deprecatedMethodCall : deprecatedMethodCalls) {
			log(
				deprecatedMethodCall.getLineNumber(),
				_MSG_DEPRECATED_METHOD_CALL, deprecatedMethodCall.getName());
		}
	}

	private void _checkDeprecatedTypes(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		Map<String, Set<Integer>> deprecatedTypeNamesMap =
			getDeprecatedTypeNamesMap(
				detailAST, deprecatedImportNames, javaClassesJSONObject);

		for (Map.Entry<String, Set<Integer>> entry :
				deprecatedTypeNamesMap.entrySet()) {

			String typeName = entry.getKey();

			Set<Integer> lineNumbers = entry.getValue();

			for (int lineNumber : lineNumbers) {
				log(lineNumber, _MSG_DEPRECATED_TYPE_CALL, typeName);
			}
		}
	}

	private void _checkDeprecatedVariables(
		DetailAST detailAST, List<String> deprecatedImportNames,
		JSONObject javaClassesJSONObject) {

		List<VariableCall> deprecatedVariableCalls = getDeprecatedVariableCalls(
			detailAST, deprecatedImportNames, javaClassesJSONObject);

		for (VariableCall deprecatedVariableCall : deprecatedVariableCalls) {
			log(
				deprecatedVariableCall.getLineNumber(),
				_MSG_DEPRECATED_FIELD_CALL, deprecatedVariableCall.getName());
		}
	}

	private static final String _MSG_DEPRECATED_CONSTRUCTOR_CALL =
		"constructor.call.deprecated";

	private static final String _MSG_DEPRECATED_FIELD_CALL =
		"field.call.deprecated";

	private static final String _MSG_DEPRECATED_METHOD_CALL =
		"method.call.deprecated";

	private static final String _MSG_DEPRECATED_TYPE_CALL =
		"type.call.deprecated";

}
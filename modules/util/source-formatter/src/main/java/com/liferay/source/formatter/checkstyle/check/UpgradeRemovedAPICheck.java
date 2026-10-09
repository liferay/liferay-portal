/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public class UpgradeRemovedAPICheck extends BaseAPICheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (!isDirectChildOfCompilationUnit(detailAST) ||
			AnnotationUtil.containsAnnotation(detailAST, "Deprecated")) {

			return;
		}

		String upgradeFromVersion = getAttributeValue(
			SourceFormatterUtil.UPGRADE_FROM_VERSION);
		String upgradeToVersion = getAttributeValue(
			SourceFormatterUtil.UPGRADE_TO_RELEASE_VERSION);

		try {
			JSONObject upgradeFromJavaClassesJSONObject =
				getJavaClassesJSONObject(upgradeFromVersion);
			JSONObject upgradeToJavaClassesJSONObject =
				getJavaClassesJSONObject(upgradeToVersion);

			List<String> removedImportNames = _getRemovedImportNames(
				detailAST, upgradeFromJavaClassesJSONObject,
				upgradeToJavaClassesJSONObject, upgradeToVersion);

			_checkRemovedConstructors(
				detailAST, removedImportNames, upgradeFromJavaClassesJSONObject,
				upgradeToJavaClassesJSONObject, upgradeToVersion);
			_checkRemovedMethods(
				detailAST, removedImportNames, upgradeFromJavaClassesJSONObject,
				upgradeToJavaClassesJSONObject, upgradeToVersion);
			_checkRemovedTypes(
				detailAST, removedImportNames, upgradeFromJavaClassesJSONObject,
				upgradeToJavaClassesJSONObject, upgradeToVersion);
			_checkRemovedVariables(
				detailAST, removedImportNames, upgradeFromJavaClassesJSONObject,
				upgradeToJavaClassesJSONObject, upgradeToVersion);
		}
		catch (Exception exception) {
		}
	}

	private void _checkRemovedConstructors(
		DetailAST detailAST, List<String> removedImportNames,
		JSONObject upgradeFromJavaClassesJSONObject,
		JSONObject upgradeToJavaClassesJSONObject, String upgradeToVersion) {

		List<ConstructorCall> constructorCalls = getConstructorCalls(
			detailAST, removedImportNames, false);

		for (ConstructorCall constructorCall : constructorCalls) {
			List<JSONObject> upgradeFromConstructorJSONObjects =
				getConstructorJSONObjects(
					constructorCall, upgradeFromJavaClassesJSONObject);

			if (upgradeFromConstructorJSONObjects.isEmpty()) {
				continue;
			}

			JSONObject classJSONObject =
				upgradeToJavaClassesJSONObject.getJSONObject(
					constructorCall.getTypeName());

			if (classJSONObject == null) {
				_logRemovedClass(
					constructorCall.getTypeName(),
					constructorCall.getLineNumber(), upgradeToVersion,
					upgradeToJavaClassesJSONObject);
			}
			else {
				List<JSONObject> upgradeToConstructorJSONObjects =
					getConstructorJSONObjects(
						constructorCall, upgradeToJavaClassesJSONObject);

				if (upgradeToConstructorJSONObjects.isEmpty()) {
					log(
						constructorCall.getLineNumber(),
						_MSG_CONSTRUCTOR_NOT_FOUND,
						constructorCall.getTypeName(), upgradeToVersion);
				}
			}
		}
	}

	private void _checkRemovedMethods(
		DetailAST detailAST, List<String> removedImportNames,
		JSONObject upgradeFromJavaClassesJSONObject,
		JSONObject upgradeToJavaClassesJSONObject, String upgradeToVersion) {

		List<MethodCall> methodCalls = getMethodCalls(
			detailAST, removedImportNames, false);

		for (MethodCall methodCall : methodCalls) {
			List<JSONObject> upgradeFromMethodJSONObjects =
				getMethodJSONObjects(
					methodCall, upgradeFromJavaClassesJSONObject);

			if (upgradeFromMethodJSONObjects.isEmpty()) {
				continue;
			}

			JSONObject classJSONObject =
				upgradeToJavaClassesJSONObject.getJSONObject(
					methodCall.getVariableTypeName());

			if (classJSONObject == null) {
				_logRemovedClass(
					methodCall.getVariableTypeName(),
					methodCall.getLineNumber(), upgradeToVersion,
					upgradeToJavaClassesJSONObject);
			}
			else {
				List<JSONObject> upgradeToMethodJSONObjects =
					getMethodJSONObjects(
						methodCall, upgradeToJavaClassesJSONObject);

				if (upgradeToMethodJSONObjects.isEmpty()) {
					log(
						methodCall.getLineNumber(), _MSG_METHOD_NOT_FOUND,
						methodCall.getName(), methodCall.getVariableTypeName(),
						upgradeToVersion);
				}
			}
		}
	}

	private void _checkRemovedTypes(
		DetailAST detailAST, List<String> removedImportNames,
		JSONObject upgradeFromJavaClassesJSONObject,
		JSONObject upgradeToJavaClassesJSONObject, String upgradeToVersion) {

		Map<String, Set<Integer>> typeNamesMap = getTypeNamesMap(
			detailAST, removedImportNames, false);

		for (Map.Entry<String, Set<Integer>> entry : typeNamesMap.entrySet()) {
			String typeName = entry.getKey();

			JSONObject upgradeFromClassJSONObject =
				upgradeFromJavaClassesJSONObject.getJSONObject(typeName);
			JSONObject upgradeToClassJSONObject =
				upgradeToJavaClassesJSONObject.getJSONObject(typeName);

			if ((upgradeFromClassJSONObject == null) ||
				(upgradeToClassJSONObject != null)) {

				continue;
			}

			Set<Integer> lineNumbers = entry.getValue();

			for (int lineNumber : lineNumbers) {
				_logRemovedClass(
					typeName, lineNumber, upgradeToVersion,
					upgradeToJavaClassesJSONObject);
			}
		}
	}

	private void _checkRemovedVariables(
		DetailAST detailAST, List<String> removedImportNames,
		JSONObject upgradeFromJavaClassesJSONObject,
		JSONObject upgradeToJavaClassesJSONObject, String upgradeToVersion) {

		List<VariableCall> variableCalls = getVariableCalls(
			detailAST, removedImportNames, false);

		for (VariableCall variableCall : variableCalls) {
			JSONObject upgradeFromVariableJSONObject = getVariableJSONObject(
				variableCall, upgradeFromJavaClassesJSONObject);

			if (upgradeFromVariableJSONObject == null) {
				continue;
			}

			JSONObject classJSONObject =
				upgradeToJavaClassesJSONObject.getJSONObject(
					variableCall.getTypeName());

			if (classJSONObject == null) {
				_logRemovedClass(
					variableCall.getTypeName(), variableCall.getLineNumber(),
					upgradeToVersion, upgradeToJavaClassesJSONObject);
			}
			else {
				JSONObject upgradeToVariableJSONObject = getVariableJSONObject(
					variableCall, upgradeToJavaClassesJSONObject);

				if (upgradeToVariableJSONObject == null) {
					log(
						variableCall.getLineNumber(), _MSG_VARIABLE_NOT_FOUND,
						variableCall.getName(), variableCall.getTypeName(),
						upgradeToVersion);
				}
			}
		}
	}

	private List<String> _getRemovedImportNames(
		DetailAST detailAST, JSONObject upgradeFromJavaClassesJSONObject,
		JSONObject upgradeToJavaClassesJSONObject, String upgradeToVersion) {

		List<String> removedImportNames = new ArrayList<>();

		List<String> importNames = getImportNames(detailAST);

		for (String importName : importNames) {
			JSONObject upgradeFromClassJSONObject =
				upgradeFromJavaClassesJSONObject.getJSONObject(importName);
			JSONObject upgradeToClassJSONObject =
				upgradeToJavaClassesJSONObject.getJSONObject(importName);

			if ((upgradeFromClassJSONObject == null) ||
				(upgradeToClassJSONObject != null)) {

				continue;
			}

			_logRemovedClass(
				importName, detailAST.getLineNo(), upgradeToVersion,
				upgradeToJavaClassesJSONObject);

			removedImportNames.add(importName);
		}

		return removedImportNames;
	}

	private void _logRemovedClass(
		String typeName, int lineNumber, String upgradeToVersion,
		JSONObject upgradeToJavaClassesJSONObject) {

		int x = typeName.lastIndexOf(".");

		String s = typeName.substring(x);

		List<String> classNames = new ArrayList<>();

		for (String className : upgradeToJavaClassesJSONObject.keySet()) {
			if (!className.endsWith(s)) {
				continue;
			}

			classNames.add(className);
		}

		if (classNames.isEmpty()) {
			log(lineNumber, _MSG_CLASS_NOT_FOUND_1, typeName, upgradeToVersion);
		}
		else if (classNames.size() == 1) {
			log(
				lineNumber, _MSG_CLASS_NOT_FOUND_2, typeName, upgradeToVersion,
				classNames.get(0));
		}
		else {
			log(
				lineNumber, _MSG_CLASS_NOT_FOUND_3, typeName, upgradeToVersion,
				StringUtil.merge(classNames, StringPool.COMMA_AND_SPACE));
		}
	}

	private static final String _MSG_CLASS_NOT_FOUND_1 = "class.not.found.1";

	private static final String _MSG_CLASS_NOT_FOUND_2 = "class.not.found.2";

	private static final String _MSG_CLASS_NOT_FOUND_3 = "class.not.found.3";

	private static final String _MSG_CONSTRUCTOR_NOT_FOUND =
		"constructor.not.found";

	private static final String _MSG_METHOD_NOT_FOUND = "method.not.found";

	private static final String _MSG_VARIABLE_NOT_FOUND = "variable.not.found";

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class ChainingCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF,
			TokenTypes.RPAREN
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.RPAREN) {
			_checkChainingOnParentheses(detailAST);
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		_checkChainingOnMethodCalls(detailAST);
	}

	private void _checkChainingOnMethodCalls(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST != null) {
				List<DetailAST> childMethodCallDetailASTs = getAllChildTokens(
					dotDetailAST, false, TokenTypes.METHOD_CALL);

				// Only check the method that is first in the chain

				if (!childMethodCallDetailASTs.isEmpty()) {
					continue;
				}
			}

			ChainInformation chainInformation = getChainInformation(
				methodCallDetailAST);

			List<String> chainedMethodNames = chainInformation.getMethodNames();

			_checkRequiredChaining(methodCallDetailAST, chainedMethodNames);

			int chainSize = chainedMethodNames.size();

			if (chainSize > 3) {
				_checkChainOrder(methodCallDetailAST, chainedMethodNames);
			}
		}
	}

	private void _checkChainingOnParentheses(DetailAST detailAST) {
		if (_isInsideConstructorThisCall(detailAST) ||
			hasParentWithTokenType(detailAST, TokenTypes.SUPER_CTOR_CALL)) {

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		DetailAST previousSiblingDetailAST = detailAST.getPreviousSibling();

		if (previousSiblingDetailAST.getType() != TokenTypes.TYPECAST) {
			log(detailAST, _MSG_AVOID_PARENTHESES_CHAINING);
		}
		else if (isAttributeValue(_APPLY_TO_TYPE_CAST_KEY)) {
			log(detailAST, _MSG_AVOID_TYPE_CAST_CHAINING);
		}
	}

	private void _checkChainOrder(
		DetailAST methodCallDetailAST, List<String> chainedMethodNames) {

		if (!Objects.equals(chainedMethodNames.get(0), "status") ||
			!Objects.equals(
				chainedMethodNames.get(chainedMethodNames.size() - 1),
				"build") ||
			!Objects.equals(
				getClassOrVariableName(methodCallDetailAST), "Response")) {

			return;
		}

		List<String> middleMethodNames = chainedMethodNames.subList(
			1, chainedMethodNames.size() - 1);

		String unsortedNames = middleMethodNames.toString();

		Collections.sort(middleMethodNames);

		if (!unsortedNames.equals(middleMethodNames.toString())) {
			log(methodCallDetailAST, _MSG_UNSORTED_RESPONSE);
		}
	}

	private void _checkRequiredChaining(
		DetailAST methodCallDetailAST, List<String> chainedMethodNames) {

		String classOrVariableName = getClassOrVariableName(
			methodCallDetailAST);

		if (classOrVariableName == null) {
			return;
		}

		String variableTypeName = getVariableTypeName(
			methodCallDetailAST, classOrVariableName, false);

		String fullyQualifiedClassName = variableTypeName;

		for (String importName : getImportNames(methodCallDetailAST)) {
			if (importName.endsWith("." + variableTypeName)) {
				fullyQualifiedClassName = importName;

				break;
			}
		}

		List<String> requiredChainingMethodNames = null;

		if (fullyQualifiedClassName.equals("org.json.JSONObject")) {
			requiredChainingMethodNames = Arrays.asList(
				"put", "putOnce", "putOpt");
		}
		else if (fullyQualifiedClassName.startsWith(
					"com.liferay.frontend.data.set.view.table.") &&
				 fullyQualifiedClassName.endsWith("FDSTableSchemaField")) {

			requiredChainingMethodNames = Arrays.asList(
				"setActionId", "setContentRenderer",
				"setContentRendererClientExtension",
				"setContentRendererModuleURL", "setFieldName", "setLabel",
				"setLocalizeLabel", "setSortable", "setSortingOrder");
		}
		else {
			requiredChainingMethodNames = _getRequiredChainingMethodNames(
				fullyQualifiedClassName);
		}

		if (requiredChainingMethodNames == null) {
			return;
		}

		String methodName = chainedMethodNames.get(
			chainedMethodNames.size() - 1);

		if (!requiredChainingMethodNames.contains(methodName)) {
			return;
		}

		DetailAST topLevelMethodCallDetailAST = getTopLevelMethodCallDetailAST(
			methodCallDetailAST);

		DetailAST parentDetailAST = topLevelMethodCallDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		DetailAST nextMethodCallDetailAST =
			nextSiblingDetailAST.getFirstChild();

		if (nextMethodCallDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return;
		}

		while (true) {
			DetailAST firstChildDetailAST =
				nextMethodCallDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				break;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
				break;
			}

			nextMethodCallDetailAST = firstChildDetailAST;
		}

		if (classOrVariableName.equals(
				getClassOrVariableName(nextMethodCallDetailAST)) &&
			requiredChainingMethodNames.contains(
				getMethodName(nextMethodCallDetailAST))) {

			log(
				methodCallDetailAST, _MSG_REQUIRED_CHAINING,
				classOrVariableName + "." + methodName);
		}
	}

	private JavaClass _getJavaClass(String requiredChainingClassFileName) {
		File file = SourceFormatterUtil.getFile(
			getBaseDirName(), requiredChainingClassFileName, getMaxDirLevel());

		try {
			if (file != null) {
				return JavaClassParser.parseJavaClass(
					requiredChainingClassFileName, FileUtil.read(file));
			}

			String portalBranchName = getAttributeValue(
				SourceFormatterUtil.GIT_LIFERAY_PORTAL_BRANCH);

			if (Validator.isNull(portalBranchName)) {
				return null;
			}

			URL url = new URL(
				StringBundler.concat(
					SourceFormatterUtil.GIT_LIFERAY_PORTAL_URL,
					portalBranchName, StringPool.SLASH,
					requiredChainingClassFileName));

			return JavaClassParser.parseJavaClass(
				requiredChainingClassFileName, URLUtil.toString(url));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private List<String> _getRequiredChainingMethodNames(
		String fullyQualifiedClassName) {

		if (_requiredChainingMethodNamesMap != null) {
			return _requiredChainingMethodNamesMap.get(fullyQualifiedClassName);
		}

		_requiredChainingMethodNamesMap = new HashMap<>();

		List<String> requiredChainingClassFileNames = getAttributeValues(
			_REQUIRED_CHAINING_CLASS_FILE_NAMES_KEY);

		for (String requiredChainingClassFileName :
				requiredChainingClassFileNames) {

			JavaClass javaClass = _getJavaClass(requiredChainingClassFileName);

			if (javaClass == null) {
				continue;
			}

			List<String> requiredChainingMethodNames = new ArrayList<>();

			for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
				if (!(javaTerm instanceof JavaMethod)) {
					continue;
				}

				JavaMethod javaMethod = (JavaMethod)javaTerm;

				if (!javaMethod.isPublic()) {
					continue;
				}

				JavaSignature javaSignature = javaMethod.getSignature();

				if (Objects.equals(
						javaClass.getName(), javaSignature.getReturnType())) {

					requiredChainingMethodNames.add(javaMethod.getName());
				}
			}

			_requiredChainingMethodNamesMap.put(
				javaClass.getPackageName() + "." + javaClass.getName(),
				requiredChainingMethodNames);
		}

		return _requiredChainingMethodNamesMap.get(fullyQualifiedClassName);
	}

	private boolean _isInsideConstructorThisCall(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (parentDetailAST != null) {
			String parentDetailASTText = parentDetailAST.getText();

			if ((parentDetailAST.getType() == TokenTypes.CTOR_CALL) &&
				parentDetailASTText.equals("this")) {

				return true;
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		return false;
	}

	private static final String _APPLY_TO_TYPE_CAST_KEY = "applyToTypeCast";

	private static final String _MSG_AVOID_PARENTHESES_CHAINING =
		"chaining.avoid.parentheses";

	private static final String _MSG_AVOID_TYPE_CAST_CHAINING =
		"chaining.avoid.type.cast";

	private static final String _MSG_REQUIRED_CHAINING = "chaining.required";

	private static final String _MSG_UNSORTED_RESPONSE = "response.unsorted";

	private static final String _REQUIRED_CHAINING_CLASS_FILE_NAMES_KEY =
		"requiredChainingClassFileNames";

	private static final Log _log = LogFactoryUtil.getLog(ChainingCheck.class);

	private Map<String, List<String>> _requiredChainingMethodNamesMap;

}
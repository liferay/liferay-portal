/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class JSONUtilCheck extends BaseChainedMethodCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.ASSIGN, TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.METHOD_CALL) {
			_checkChainedPutCalls(detailAST);
			_checkStringValueOfCalls(detailAST);
			_checkToJSONStringCalls(detailAST);

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.EXPR) &&
			(parentDetailAST.getType() != TokenTypes.VARIABLE_DEF)) {

			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		DetailAST methodCallDetailAST = _getMethodCallDetailAST(
			detailAST, parentDetailAST);

		if (methodCallDetailAST == null) {
			return;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if (elistDetailAST.getChildCount() > 0) {
			return;
		}

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		FullIdent fullIdent1 = FullIdent.createFullIdent(firstChildDetailAST);

		String methodName = fullIdent1.getText();

		if (!methodName.equals("JSONFactoryUtil.createJSONArray") &&
			!methodName.equals("JSONFactoryUtil.createJSONObject")) {

			return;
		}

		String variableName = getVariableName(detailAST, parentDetailAST);

		if (variableName == null) {
			return;
		}

		while (true) {
			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if (nextSiblingDetailAST == null) {
				return;
			}

			FullIdent fullIdent2 = getMethodCallFullIdent(
				nextSiblingDetailAST, variableName, "put");

			if (fullIdent2 != null) {
				log(
					detailAST, _MSG_USE_JSON_UTIL_PUT, methodName,
					fullIdent1.getLineNo(), variableName + ".put",
					fullIdent2.getLineNo(), "JSONUtil.put");
			}

			if (containsVariableName(
					nextSiblingDetailAST, variableName, true)) {

				return;
			}
		}
	}

	private void _checkChainedPutCalls(DetailAST methodCallDetailAST) {
		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		if (!Objects.equals(fullIdent.getText(), "JSONUtil.put")) {
			return;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if (elistDetailAST.getChildCount() != 1) {
			return;
		}

		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return;
		}

		DetailAST nextSiblingDetailAST = methodCallDetailAST.getNextSibling();

		if ((nextSiblingDetailAST.getType() == TokenTypes.IDENT) &&
			Objects.equals(nextSiblingDetailAST.getText(), "put")) {

			log(methodCallDetailAST, _MSG_USE_JSON_UTIL_PUT_ALL);
		}
	}

	private void _checkStringValueOfCalls(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		if (!Objects.equals(fullIdent.getText(), "String.valueOf")) {
			return;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST == null) {
			return;
		}

		firstChildDetailAST = elistDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return;
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			firstChildDetailAST, true, TokenTypes.METHOD_CALL);

		if (methodCallDetailASTs.isEmpty()) {
			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				return;
			}

			fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

			if (Objects.equals(fullIdent.getText(), "JSONUtil.put") ||
				Objects.equals(fullIdent.getText(), "JSONUtil.putAll")) {

				log(detailAST, _MSG_USE_JSON_UTIL_TO_STRING_1);
			}

			return;
		}

		List<String> chainedMethodNames = new ArrayList<>();

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST != null) {
				List<DetailAST> childMethodCallDetailASTs = getAllChildTokens(
					dotDetailAST, false, TokenTypes.METHOD_CALL);

				if (!childMethodCallDetailASTs.isEmpty()) {
					continue;
				}
			}

			BaseCheck.ChainInformation chainInformation = getChainInformation(
				methodCallDetailAST);

			chainedMethodNames = chainInformation.getMethodNames();
		}

		for (String chainedMethodName : chainedMethodNames) {
			if (!chainedMethodName.equals("put") &&
				!chainedMethodName.equals("putAll")) {

				return;
			}
		}

		DetailAST methodCallDetailAST = methodCallDetailASTs.get(
			methodCallDetailASTs.size() - 1);

		firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		String methodCall = fullIdent.getText();

		if (methodCall.startsWith("JSONUtil.")) {
			log(detailAST, _MSG_USE_JSON_UTIL_TO_STRING_1);
		}
	}

	private void _checkToJSONStringCalls(DetailAST detailAST) {
		if (!StringUtil.equals("toJSONString", getMethodName(detailAST))) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		if (methodCallDetailASTs.size() == 1) {
			DetailAST methodCallDetailAST = methodCallDetailASTs.get(0);

			DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

			if ((firstChildDetailAST != null) &&
				(firstChildDetailAST.getType() == TokenTypes.IDENT) &&
				_isJSONTypeMethodCall(
					detailAST, getMethodName(methodCallDetailAST))) {

				log(detailAST, _MSG_USE_JSON_UTIL_TO_STRING_2);
			}

			return;
		}

		if (ArrayUtil.contains(
				_VARIABLE_TYPE_NAMES,
				getVariableTypeName(
					detailAST, getVariableName(detailAST), false)) ||
			_isJSONUtilCall(detailAST)) {

			log(detailAST, _MSG_USE_JSON_UTIL_TO_STRING_2);
		}
	}

	private DetailAST _getMethodCallDetailAST(
		DetailAST assignDetailAST, DetailAST parentDetailAST) {

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		DetailAST assignValueDetailAST = null;

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			assignValueDetailAST = firstChildDetailAST.getNextSibling();
		}
		else {
			assignValueDetailAST = firstChildDetailAST.getFirstChild();
		}

		if ((assignValueDetailAST != null) &&
			(assignValueDetailAST.getType() == TokenTypes.METHOD_CALL)) {

			return assignValueDetailAST;
		}

		return null;
	}

	private boolean _isJSONTypeMethodCall(
		DetailAST detailAST, String methodName) {

		if (Validator.isNull(methodName)) {
			return false;
		}

		DetailAST parentDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.OBJBLOCK);

		List<DetailAST> childDetailASTs = getAllChildTokens(
			parentDetailAST, false, TokenTypes.METHOD_DEF);

		for (DetailAST childDetailAST : childDetailASTs) {
			if (StringUtil.equals(getName(childDetailAST), methodName) &&
				ArrayUtil.contains(
					_VARIABLE_TYPE_NAMES, getTypeName(childDetailAST, false))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isJSONUtilCall(DetailAST detailAST) {
		if (detailAST.getChildCount() == 0) {
			return false;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String methodCall = fullIdent.getText();

			if (methodCall.startsWith("JSONUtil.")) {
				return true;
			}
		}

		return _isJSONUtilCall(firstChildDetailAST);
	}

	private static final String _MSG_USE_JSON_UTIL_PUT = "json.util.put.use";

	private static final String _MSG_USE_JSON_UTIL_PUT_ALL =
		"json.util.put.all.use";

	private static final String _MSG_USE_JSON_UTIL_TO_STRING_1 =
		"json.util.to.string.use.1";

	private static final String _MSG_USE_JSON_UTIL_TO_STRING_2 =
		"json.util.to.string.use.2";

	private static final String[] _VARIABLE_TYPE_NAMES = {
		"JSONArray", "JSONObject"
	};

}
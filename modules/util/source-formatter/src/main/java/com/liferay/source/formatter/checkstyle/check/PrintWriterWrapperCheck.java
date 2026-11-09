/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class PrintWriterWrapperCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_NEW};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("-test-util/") ||
			absolutePath.contains("/modules/sdk/") ||
			absolutePath.contains("/modules/third-party/") ||
			absolutePath.contains("/modules/util/") ||
			absolutePath.contains("/portal-test") ||
			absolutePath.contains("/src/test/") ||
			absolutePath.contains("/src/testIntegration/") ||
			!ArrayUtil.contains(
				new String[] {"PrintWriter", "UnsyncPrintWriter"},
				getName(detailAST))) {

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.ASSIGN) {
			_checkAssignment(detailAST, parentDetailAST.getFirstChild());

			return;
		}

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST grandParentDetailAST = parentDetailAST.getParent();

		if (grandParentDetailAST.getType() == TokenTypes.ASSIGN) {
			DetailAST variableDefinitionDetailAST =
				grandParentDetailAST.getParent();

			if (!TokenUtil.isOfType(
					variableDefinitionDetailAST, TokenTypes.RESOURCE,
					TokenTypes.VARIABLE_DEF)) {

				return;
			}

			_checkVariable(detailAST, variableDefinitionDetailAST);
		}
		else if (grandParentDetailAST.getType() == TokenTypes.ELIST) {
			DetailAST greatGrandParentDetailAST =
				grandParentDetailAST.getParent();

			if (greatGrandParentDetailAST.getType() != TokenTypes.METHOD_CALL) {
				return;
			}

			_checkArgument(
				detailAST, parentDetailAST, greatGrandParentDetailAST);
		}
	}

	private void _checkArgument(
		DetailAST literalNewDetailAST, DetailAST exprDetailAST,
		DetailAST methodCallDetailAST) {

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			methodCallDetailAST);

		int index = parameterExprDetailASTs.indexOf(exprDetailAST);

		if (index == -1) {
			return;
		}

		DetailAST parentDetailAST = getParentWithTokenType(
			literalNewDetailAST, TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF,
			TokenTypes.LITERAL_NEW, TokenTypes.RECORD_DEF);

		if ((parentDetailAST == null) ||
			(parentDetailAST.getType() == TokenTypes.LITERAL_NEW)) {

			return;
		}

		DetailAST objBlockDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> methodDefinitionDetailASTs = ListUtil.filter(
			getAllChildTokens(objBlockDetailAST, false, TokenTypes.METHOD_DEF),
			methodDefinitionDetailAST -> {
				if (!StringUtil.equals(
						firstChildDetailAST.getText(),
						getName(methodDefinitionDetailAST))) {

					return false;
				}

				List<DetailAST> parameterDefinitionDetailASTs =
					getParameterDefs(methodDefinitionDetailAST);

				return parameterDefinitionDetailASTs.size() ==
					parameterExprDetailASTs.size();
			});

		if (methodDefinitionDetailASTs.isEmpty()) {
			return;
		}

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			List<DetailAST> parameterDefinitionDetailASTs = getParameterDefs(
				methodDefinitionDetailAST);

			if (!_isPrintOnly(parameterDefinitionDetailASTs.get(index))) {
				return;
			}

			DetailAST modifiersDetailAST =
				methodDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

			if (!modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_PRIVATE)) {

				return;
			}
		}

		log(literalNewDetailAST, _MSG_AVOID_PRINT_WRITER_WRAPPER);
	}

	private void _checkAssignment(
		DetailAST literalNewDetailAST, DetailAST detailAST) {

		DetailAST variableDefinitionDetailAST = null;

		if (_isThisDot(detailAST)) {
			variableDefinitionDetailAST = _getFieldDefinitionDetailAST(
				literalNewDetailAST, detailAST.getLastChild());
		}
		else if (detailAST.getType() == TokenTypes.IDENT) {
			variableDefinitionDetailAST = getVariableDefinitionDetailAST(
				literalNewDetailAST, detailAST.getText());
		}

		if (variableDefinitionDetailAST != null) {
			_checkVariable(literalNewDetailAST, variableDefinitionDetailAST);
		}
	}

	private void _checkVariable(
		DetailAST detailAST, DetailAST variableDefinitionDetailAST) {

		String typeName = getTypeName(
			variableDefinitionDetailAST.findFirstToken(TokenTypes.TYPE), false);

		if (typeName.equals("Writer") ||
			_isPrintOnly(variableDefinitionDetailAST)) {

			log(detailAST, _MSG_AVOID_PRINT_WRITER_WRAPPER);
		}
	}

	private DetailAST _getFieldDefinitionDetailAST(
		DetailAST literalNewDetailAST, DetailAST detailAST) {

		if (detailAST.getType() != TokenTypes.IDENT) {
			return null;
		}

		DetailAST parentDetailAST = getParentWithTokenType(
			literalNewDetailAST, TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF,
			TokenTypes.LITERAL_NEW, TokenTypes.RECORD_DEF);

		if ((parentDetailAST == null) ||
			(parentDetailAST.getType() == TokenTypes.LITERAL_NEW)) {

			return null;
		}

		DetailAST objBlockDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		for (DetailAST variableDefinitionDetailAST :
				getAllChildTokens(
					objBlockDetailAST, false, TokenTypes.VARIABLE_DEF)) {

			if (StringUtil.equals(
					detailAST.getText(),
					getName(variableDefinitionDetailAST))) {

				return variableDefinitionDetailAST;
			}
		}

		return null;
	}

	private boolean _isPrintOnly(DetailAST detailAST) {
		for (DetailAST identDetailAST :
				getVariableCallerDetailASTs(detailAST)) {

			DetailAST parentDetailAST = identDetailAST.getParent();

			if (_isThisDot(parentDetailAST)) {
				identDetailAST = parentDetailAST;

				parentDetailAST = parentDetailAST.getParent();
			}

			if ((parentDetailAST.getType() == TokenTypes.ASSIGN) &&
				equals(parentDetailAST.getFirstChild(), identDetailAST)) {

				continue;
			}

			if (parentDetailAST.getType() == TokenTypes.DOT) {
				if (!equals(parentDetailAST.getFirstChild(), identDetailAST) ||
					!TokenUtil.isOfType(
						parentDetailAST.getParent(), TokenTypes.METHOD_CALL)) {

					return false;
				}

				DetailAST methodNameDetailAST = parentDetailAST.getLastChild();

				if (ArrayUtil.contains(
						new String[] {
							"append", "close", "flush", "print", "println",
							"write"
						},
						methodNameDetailAST.getText())) {

					continue;
				}

				return false;
			}

			if (TokenUtil.isOfType(
					parentDetailAST, TokenTypes.EQUAL, TokenTypes.NOT_EQUAL) &&
				(parentDetailAST.findFirstToken(TokenTypes.LITERAL_NULL) !=
					null)) {

				continue;
			}

			return false;
		}

		return true;
	}

	private boolean _isThisDot(DetailAST detailAST) {
		if (detailAST.getType() != TokenTypes.DOT) {
			return false;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_THIS) {
			return true;
		}

		return false;
	}

	private static final String _MSG_AVOID_PRINT_WRITER_WRAPPER =
		"print.writer.wrapper.avoid";

}
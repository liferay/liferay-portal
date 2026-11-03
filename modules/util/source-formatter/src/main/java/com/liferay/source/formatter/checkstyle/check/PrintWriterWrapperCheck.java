/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.util.List;
import java.util.Objects;

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

		DetailAST grandparentDetailAST = parentDetailAST.getParent();

		if (grandparentDetailAST.getType() == TokenTypes.ASSIGN) {
			DetailAST variableDefinitionDetailAST =
				grandparentDetailAST.getParent();

			if (TokenUtil.isOfType(
					variableDefinitionDetailAST, TokenTypes.RESOURCE,
					TokenTypes.VARIABLE_DEF)) {

				_checkVariable(detailAST, variableDefinitionDetailAST);
			}
		}
		else if (grandparentDetailAST.getType() == TokenTypes.ELIST) {
			_checkArgument(
				detailAST, parentDetailAST, grandparentDetailAST.getParent());
		}
	}

	private void _checkArgument(
		DetailAST detailAST, DetailAST exprDetailAST,
		DetailAST methodCallDetailAST) {

		if (methodCallDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return;
		}

		DetailAST nameDetailAST = methodCallDetailAST.getFirstChild();

		if (nameDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			methodCallDetailAST);

		int index = parameterExprDetailASTs.indexOf(exprDetailAST);

		DetailAST classDefinitionDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF,
			TokenTypes.LITERAL_NEW, TokenTypes.RECORD_DEF);

		if ((index == -1) || (classDefinitionDetailAST == null) ||
			(classDefinitionDetailAST.getType() == TokenTypes.LITERAL_NEW)) {

			return;
		}

		String methodName = nameDetailAST.getText();
		boolean printOnly = false;

		DetailAST objBlockDetailAST = classDefinitionDetailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		for (DetailAST methodDefinitionDetailAST :
				getAllChildTokens(
					objBlockDetailAST, false, TokenTypes.METHOD_DEF)) {

			if (!methodName.equals(getName(methodDefinitionDetailAST))) {
				continue;
			}

			List<DetailAST> parameterDefinitionDetailASTs = getParameterDefs(
				methodDefinitionDetailAST);

			if (parameterDefinitionDetailASTs.size() !=
					parameterExprDetailASTs.size()) {

				continue;
			}

			DetailAST modifiersDetailAST =
				methodDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

			DetailAST privateDetailAST = modifiersDetailAST.findFirstToken(
				TokenTypes.LITERAL_PRIVATE);

			if ((privateDetailAST == null) ||
				!_isPrintOnly(parameterDefinitionDetailASTs.get(index))) {

				return;
			}

			printOnly = true;
		}

		if (printOnly) {
			log(detailAST, _MSG_WRITE_DIRECTLY);
		}
	}

	private void _checkAssignment(
		DetailAST detailAST, DetailAST assigneeDetailAST) {

		DetailAST variableDefinitionDetailAST = null;

		if (_isThisDot(assigneeDetailAST)) {
			variableDefinitionDetailAST = _getFieldDefinitionDetailAST(
				detailAST, assigneeDetailAST.getLastChild());
		}
		else if (assigneeDetailAST.getType() == TokenTypes.IDENT) {
			variableDefinitionDetailAST = getVariableDefinitionDetailAST(
				detailAST, assigneeDetailAST.getText());
		}

		if (variableDefinitionDetailAST != null) {
			_checkVariable(detailAST, variableDefinitionDetailAST);
		}
	}

	private void _checkVariable(
		DetailAST detailAST, DetailAST variableDefinitionDetailAST) {

		String typeName = getTypeName(
			variableDefinitionDetailAST.findFirstToken(TokenTypes.TYPE), false);

		if (typeName.equals("Writer") ||
			_isPrintOnly(variableDefinitionDetailAST)) {

			log(detailAST, _MSG_WRITE_DIRECTLY);
		}
	}

	private DetailAST _getFieldDefinitionDetailAST(
		DetailAST detailAST, DetailAST nameDetailAST) {

		if (nameDetailAST.getType() != TokenTypes.IDENT) {
			return null;
		}

		DetailAST classDefinitionDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF,
			TokenTypes.LITERAL_NEW, TokenTypes.RECORD_DEF);

		if ((classDefinitionDetailAST == null) ||
			(classDefinitionDetailAST.getType() == TokenTypes.LITERAL_NEW)) {

			return null;
		}

		DetailAST objBlockDetailAST = classDefinitionDetailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		for (DetailAST variableDefinitionDetailAST :
				getAllChildTokens(
					objBlockDetailAST, false, TokenTypes.VARIABLE_DEF)) {

			if (Objects.equals(
					nameDetailAST.getText(),
					getName(variableDefinitionDetailAST))) {

				return variableDefinitionDetailAST;
			}
		}

		return null;
	}

	private boolean _isPrintOnly(DetailAST variableDefinitionDetailAST) {
		for (DetailAST identDetailAST :
				getVariableCallerDetailASTs(variableDefinitionDetailAST)) {

			DetailAST parentDetailAST = identDetailAST.getParent();

			if (_isThisDot(parentDetailAST)) {
				identDetailAST = parentDetailAST;

				parentDetailAST = parentDetailAST.getParent();
			}

			if (parentDetailAST.getType() == TokenTypes.DOT) {
				DetailAST grandparentDetailAST = parentDetailAST.getParent();
				DetailAST methodNameDetailAST = parentDetailAST.getLastChild();

				if (!equals(parentDetailAST.getFirstChild(), identDetailAST) ||
					(grandparentDetailAST.getType() !=
						TokenTypes.METHOD_CALL) ||
					!ArrayUtil.contains(
						new String[] {
							"append", "close", "flush", "print", "println",
							"write"
						},
						methodNameDetailAST.getText())) {

					return false;
				}

				continue;
			}

			if ((parentDetailAST.getType() == TokenTypes.ASSIGN) &&
				equals(parentDetailAST.getFirstChild(), identDetailAST)) {

				continue;
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

	private static final String _MSG_WRITE_DIRECTLY = "writer.write.directly";

}
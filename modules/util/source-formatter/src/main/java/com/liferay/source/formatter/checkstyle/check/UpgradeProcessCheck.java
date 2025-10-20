/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;

/**
 * @author Qi Zhang
 */
public class UpgradeProcessCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST != null) || !_isUpgradeProcess(detailAST)) {
			return;
		}

		for (DetailAST literalIfDetailAST :
				getAllChildTokens(detailAST, true, TokenTypes.LITERAL_IF)) {

			if (_isUnnecessaryIfStatement(literalIfDetailAST)) {
				log(literalIfDetailAST, _MSG_UNNECESSARY_IF_STATEMENT);

				return;
			}
		}

		String absolutePath = getAbsolutePath();

		if (absolutePath.contains(
				"/portal-impl/src/com/liferay/portal/upgrade/v6_") ||
			absolutePath.contains(
				"/portal-impl/src/com/liferay/portal/upgrade/v7_0_")) {

			return;
		}

		List<DetailAST> methodDefDetailASTs = getAllChildTokens(
			detailAST.findFirstToken(TokenTypes.OBJBLOCK), false,
			TokenTypes.METHOD_DEF);

		DetailAST doUpgradeMethodDefDetailAST = null;

		for (DetailAST methodDefDetailAST : methodDefDetailASTs) {
			if (StringUtil.equals(getName(methodDefDetailAST), "doUpgrade") &&
				AnnotationUtil.containsAnnotation(
					methodDefDetailAST, "Override")) {

				doUpgradeMethodDefDetailAST = methodDefDetailAST;

				break;
			}
		}

		if (doUpgradeMethodDefDetailAST == null) {
			return;
		}

		DetailAST slistDetailAST = doUpgradeMethodDefDetailAST.findFirstToken(
			TokenTypes.SLIST);

		if (slistDetailAST.getChildCount() == 1) {
			return;
		}

		if ((methodDefDetailASTs.size() == 1) &&
			_isUnnecessaryUpgradeProcessClass(slistDetailAST)) {

			log(
				detailAST, _MSG_UNNECESSARY_CLASS,
				JavaSourceUtil.getClassName(absolutePath));

			return;
		}

		_checkPostUpgradeSteps(slistDetailAST);
		_checkPreUpgradeSteps(slistDetailAST);
	}

	private void _checkPostUpgradeSteps(DetailAST detailAST) {
		DetailAST lastChildDetailAST = detailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.RCURLY) {
			return;
		}

		DetailAST previousSiblingDetailAST =
			lastChildDetailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		previousSiblingDetailAST =
			previousSiblingDetailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		DetailAST firstChildDetailAST =
			previousSiblingDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return;
		}

		String methodName = getMethodName(firstChildDetailAST);

		if (ArrayUtil.contains(_ALTER_METHOD_NAMES, methodName) &&
			(previousSiblingDetailAST.getPreviousSibling() != null)) {

			log(
				firstChildDetailAST,
				_MSG_MOVE_UPGRADE_STEP_INSIDE_POST_UPGRADE_STEPS, methodName);
		}
	}

	private void _checkPreUpgradeSteps(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return;
		}

		String methodName = getMethodName(firstChildDetailAST);

		if (ArrayUtil.contains(_ALTER_METHOD_NAMES, methodName)) {
			log(
				firstChildDetailAST,
				_MSG_MOVE_UPGRADE_STEP_INSIDE_PRE_UPGRADE_STEPS, methodName);
		}
	}

	private boolean _containsOnlyAlterMethodCalls(
		DetailAST detailAST, String tableName, String columnName) {

		if (detailAST == null) {
			return false;
		}

		DetailAST lastChildDetailAST = detailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.RCURLY) {
			return false;
		}

		DetailAST previousSiblingDetailAST =
			lastChildDetailAST.getPreviousSibling();

		while (previousSiblingDetailAST != null) {
			if ((previousSiblingDetailAST.getType() != TokenTypes.EXPR) &&
				(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

				return false;
			}

			if (previousSiblingDetailAST.getType() == TokenTypes.EXPR) {
				DetailAST firstChildDetailAST =
					previousSiblingDetailAST.getFirstChild();

				if ((firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) ||
					!ArrayUtil.contains(
						_ALTER_METHOD_NAMES,
						getMethodName(firstChildDetailAST))) {

					return false;
				}

				if (Validator.isNotNull(columnName) &&
					Validator.isNotNull(tableName) &&
					!_hasSameTableNameAndColumnName(
						firstChildDetailAST, tableName, columnName)) {

					return false;
				}
			}

			previousSiblingDetailAST =
				previousSiblingDetailAST.getPreviousSibling();
		}

		return true;
	}

	private boolean _containsOnlyRunSQLCalls(DetailAST detailAST) {
		if (detailAST == null) {
			return false;
		}

		DetailAST lastChildDetailAST = detailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.RCURLY) {
			return false;
		}

		DetailAST previousSiblingDetailAST =
			lastChildDetailAST.getPreviousSibling();

		while (previousSiblingDetailAST != null) {
			if ((previousSiblingDetailAST.getType() != TokenTypes.EXPR) &&
				(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

				return false;
			}

			if (previousSiblingDetailAST.getType() == TokenTypes.EXPR) {
				DetailAST firstChildDetailAST =
					previousSiblingDetailAST.getFirstChild();

				if ((firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) ||
					!StringUtil.equals(
						getMethodName(firstChildDetailAST), "runSQL")) {

					return false;
				}
			}

			previousSiblingDetailAST =
				previousSiblingDetailAST.getPreviousSibling();
		}

		return true;
	}

	private String _getParameterName(DetailAST detailAST) {
		if (detailAST == null) {
			return null;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
			return null;
		}

		return firstChildDetailAST.getText();
	}

	private boolean _hasSameTableNameAndColumnName(
		DetailAST detailAST, String tableName, String columnName) {

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			return false;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		firstChildDetailAST = elistDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR) ||
			!StringUtil.equals(
				tableName, _getParameterName(firstChildDetailAST))) {

			return false;
		}

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.COMMA)) {

			return false;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.EXPR) ||
			!StringUtil.equals(
				columnName, _getParameterName(nextSiblingDetailAST))) {

			return false;
		}

		return true;
	}

	private boolean _isUnnecessaryIfStatement(DetailAST detailAST) {
		DetailAST exprDetailAST = detailAST.findFirstToken(TokenTypes.EXPR);

		if (exprDetailAST.getChildCount() != 1) {
			return false;
		}

		DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.LNOT) {
			firstChildDetailAST = firstChildDetailAST.getFirstChild();
		}

		if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return false;
		}

		String methodName = getMethodName(firstChildDetailAST);

		if (!methodName.equals("hasColumn") &&
			!methodName.equals("hasColumnType")) {

			return false;
		}

		DetailAST elistDetailAST = firstChildDetailAST.findFirstToken(
			TokenTypes.ELIST);

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		if (exprDetailASTs.size() < 2) {
			return false;
		}

		String columnName = _getParameterName(exprDetailASTs.get(1));
		String tableName = _getParameterName(exprDetailASTs.get(0));

		if (Validator.isNull(columnName) || Validator.isNull(tableName)) {
			return false;
		}

		return _containsOnlyAlterMethodCalls(
			detailAST.findFirstToken(TokenTypes.SLIST), tableName, columnName);
	}

	private boolean _isUnnecessaryUpgradeProcessClass(DetailAST detailAST) {
		if (_containsOnlyAlterMethodCalls(detailAST, null, null) ||
			_containsOnlyRunSQLCalls(detailAST)) {

			return true;
		}

		return false;
	}

	private boolean _isUpgradeProcess(DetailAST detailAST) {
		DetailAST extendsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST == null) {
			return false;
		}

		DetailAST firstChildDetailAST = extendsClauseDetailAST.getFirstChild();

		if ((firstChildDetailAST.getType() != TokenTypes.IDENT) ||
			!StringUtil.equals(
				getName(extendsClauseDetailAST), "UpgradeProcess")) {

			return false;
		}

		return true;
	}

	private static final String[] _ALTER_METHOD_NAMES = {
		"alterColumnName", "alterColumnType", "alterTableAddColumn",
		"alterTableDropColumn"
	};

	private static final String
		_MSG_MOVE_UPGRADE_STEP_INSIDE_POST_UPGRADE_STEPS =
			"upgrade.step.move.inside.post.upgrade.steps";

	private static final String
		_MSG_MOVE_UPGRADE_STEP_INSIDE_PRE_UPGRADE_STEPS =
			"upgrade.step.move.inside.pre.upgrade.steps";

	private static final String _MSG_UNNECESSARY_CLASS = "class.unnecessary";

	private static final String _MSG_UNNECESSARY_IF_STATEMENT =
		"if.statement.unnecessary";

}
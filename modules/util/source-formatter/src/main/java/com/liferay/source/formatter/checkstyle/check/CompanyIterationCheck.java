/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class CompanyIterationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_FOR, TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.LITERAL_FOR) {
			_checkForLoop(detailAST);
		}
		else {
			_checkMethodCall(detailAST);
		}
	}

	private void _checkForLoop(DetailAST literalForDetailAST) {
		if (_isCoreUpgrade()) {
			return;
		}

		DetailAST forEachClauseDetailAST = literalForDetailAST.findFirstToken(
			TokenTypes.FOR_EACH_CLAUSE);

		if (forEachClauseDetailAST == null) {
			return;
		}

		DetailAST variableDefinitionDetailAST =
			forEachClauseDetailAST.findFirstToken(TokenTypes.VARIABLE_DEF);

		String typeName = getTypeName(
			variableDefinitionDetailAST.findFirstToken(TokenTypes.TYPE), true);

		DetailAST nameDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		String variableName = nameDetailAST.getText();

		if (typeName.equals("Company")) {
			log(
				literalForDetailAST, _MSG_USE_COMPANY_LOCAL_SERVICE_FOR_LOOP,
				"forEachCompany", typeName + " " + variableName);
		}
		else if ((typeName.equals("Long") || typeName.equals("long")) &&
				 variableName.equals("companyId")) {

			log(
				literalForDetailAST, _MSG_USE_COMPANY_LOCAL_SERVICE_FOR_LOOP,
				"forEachCompanyId", typeName + " " + variableName);
		}
	}

	private void _checkMethodCall(DetailAST methodCallDetailAST) {
		Set<String> methodNames = _methodNamesMap.get(
			getClassOrVariableName(methodCallDetailAST));

		if ((methodNames == null) ||
			!methodNames.contains(getMethodName(methodCallDetailAST))) {

			return;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		DetailAST exprDetailAST = elistDetailAST.getLastChild();

		String stringLiteral = _getStringLiteral(exprDetailAST);

		if (stringLiteral.isEmpty()) {
			DetailAST identDetailAST = exprDetailAST.findFirstToken(
				TokenTypes.IDENT);

			if (identDetailAST == null) {
				return;
			}

			String name = identDetailAST.getText();

			DetailAST variableDefinitionDetailAST =
				getVariableDefinitionDetailAST(methodCallDetailAST, name, true);

			if ((variableDefinitionDetailAST == null) ||
				(variableDefinitionDetailAST.getType() ==
					TokenTypes.PARAMETER_DEF)) {

				return;
			}

			DetailAST assignDetailAST =
				variableDefinitionDetailAST.findFirstToken(TokenTypes.ASSIGN);

			if (assignDetailAST == null) {
				return;
			}

			stringLiteral = _getStringLiteral(
				assignDetailAST.findFirstToken(TokenTypes.EXPR));
		}

		Matcher matcher = _selectCompanySQLPattern.matcher(stringLiteral);

		if (matcher.find()) {
			if (stringLiteral.contains(" where ")) {
				return;
			}

			if (_isCoreUpgrade()) {
				if (Objects.equals(matcher.group(1), "companyId")) {
					log(methodCallDetailAST, _MSG_USE_PORTAL_INSTANCES);
				}
			}
			else {
				log(methodCallDetailAST, _MSG_USE_COMPANY_LOCAL_SERVICE_SQL);
			}
		}
	}

	private String _getStringLiteral(DetailAST detailAST) {
		List<DetailAST> stringLiteralDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.STRING_LITERAL);

		StringBundler sb = new StringBundler(stringLiteralDetailASTs.size());

		for (DetailAST stringLiteralDetailAST : stringLiteralDetailASTs) {
			sb.append(stringLiteralDetailAST.getText());
		}

		return sb.toString();
	}

	private boolean _isCoreUpgrade() {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("com/liferay/portal/") &&
			absolutePath.contains("/upgrade/")) {

			return true;
		}

		return false;
	}

	private static final String _MSG_USE_COMPANY_LOCAL_SERVICE_FOR_LOOP =
		"company.local.service.use.for.loop";

	private static final String _MSG_USE_COMPANY_LOCAL_SERVICE_SQL =
		"company.local.service.use.sql";

	private static final String _MSG_USE_PORTAL_INSTANCES =
		"portal.instances.use";

	private static final Map<String, Set<String>> _methodNamesMap =
		HashMapBuilder.<String, Set<String>>put(
			"AutoBatchPreparedStatementUtil",
			SetUtil.fromArray(
				"autoBatch", "autoBatchWithResults", "concurrentAutoBatch")
		).put(
			"connection", SetUtil.fromArray("prepareCall", "prepareStatement")
		).build();
	private static final Pattern _selectCompanySQLPattern = Pattern.compile(
		"select\\s+(.+)\\s+from\\s+Company");

}
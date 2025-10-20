/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Alan Huang
 */
public class TestClassMissingLiferayUnitTestRuleCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		String absolutePath = getAbsolutePath();

		if (!absolutePath.contains("/test/") ||
			!absolutePath.endsWith("Test.java") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		List<String> pathNames = getAttributeValues(_PATH_NAMES_WHITELIST_KEY);

		for (String pathName : pathNames) {
			if (!absolutePath.contains(pathName)) {
				continue;
			}

			DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
				detailAST, "RunWith");

			if (annotationDetailAST != null) {
				List<DetailAST> literalClassDetailASTs = getAllChildTokens(
					annotationDetailAST, true, TokenTypes.LITERAL_CLASS);

				for (DetailAST literalClassDetailAST : literalClassDetailASTs) {
					DetailAST identDetailAST =
						literalClassDetailAST.getPreviousSibling();

					String className = identDetailAST.getText();

					if (className.equals("MockitoJUnitRunner") ||
						className.equals("PowerMockRunner")) {

						return;
					}
				}
			}

			DetailAST objBlockDetailAST = detailAST.findFirstToken(
				TokenTypes.OBJBLOCK);

			List<DetailAST> variableDefinitionDetailASTs = getAllChildTokens(
				objBlockDetailAST, false, TokenTypes.VARIABLE_DEF);

			for (DetailAST variableDefinitionDetailAST :
					variableDefinitionDetailASTs) {

				DetailAST typeDetailAST =
					variableDefinitionDetailAST.findFirstToken(TokenTypes.TYPE);

				DetailAST identDetailAST = typeDetailAST.getFirstChild();

				if (identDetailAST.getType() != TokenTypes.IDENT) {
					continue;
				}

				String variableType = identDetailAST.getText();

				if (variableType.equals("LiferayUnitTestRule")) {
					return;
				}

				List<DetailAST> dotDetailASTs = getAllChildTokens(
					variableDefinitionDetailAST, true, TokenTypes.DOT);

				FullIdent fullIdent = null;

				for (DetailAST dotDetailAST : dotDetailASTs) {
					fullIdent = FullIdent.createFullIdent(dotDetailAST);

					if (Objects.equals(
							fullIdent.getText(),
							"LiferayUnitTestRule.INSTANCE")) {

						return;
					}
				}
			}

			List<String> importNames = getImportNames(detailAST);

			if (!importNames.contains(
					"com.liferay.portal.test.rule.LiferayUnitTestRule")) {

				log(detailAST, _MSG_REQUIRE_TEST_RULE);
			}
		}
	}

	private static final String _MSG_REQUIRE_TEST_RULE = "test.rule.missing";

	private static final String _PATH_NAMES_WHITELIST_KEY =
		"pathNamesWhitelist";

}
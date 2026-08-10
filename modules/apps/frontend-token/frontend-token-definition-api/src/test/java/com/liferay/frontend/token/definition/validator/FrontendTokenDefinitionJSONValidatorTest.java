/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.validator;

import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Anderson Luiz
 * @author Thiago Buarque
 */
public class FrontendTokenDefinitionJSONValidatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidate() throws IOException, JSONValidatorException {
		_frontendTokenDefinitionJSONValidator.validate("");
		_frontendTokenDefinitionJSONValidator.validate("{}");
		_frontendTokenDefinitionJSONValidator.validate(
			URLUtil.toString(
				FrontendTokenDefinitionJSONValidatorTest.class.getResource(
					"dependencies/frontend-token-definition.json")));
		_frontendTokenDefinitionJSONValidator.validate(
			URLUtil.toString(
				FrontendTokenDefinitionJSONValidatorTest.class.getResource(
					"dependencies/frontend-token-definition-empty-frontend-" +
						"token-categories.json")));

		try {
			_frontendTokenDefinitionJSONValidator.validate(
				URLUtil.toString(
					FrontendTokenDefinitionJSONValidatorTest.class.getResource(
						"dependencies/frontend-token-definition-no-frontend-" +
							"token-sets.json")));

			Assert.fail();
		}
		catch (JSONValidatorException jsonValidatorException) {
			String message = jsonValidatorException.getMessage();

			Assert.assertTrue(
				message.contains("required key [frontendTokenSets] not found"));
		}
	}

	private final FrontendTokenDefinitionJSONValidator
		_frontendTokenDefinitionJSONValidator =
			new FrontendTokenDefinitionJSONValidator();

}
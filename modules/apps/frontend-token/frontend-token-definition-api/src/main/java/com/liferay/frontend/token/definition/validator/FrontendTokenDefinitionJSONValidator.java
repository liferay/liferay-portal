/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.validator;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.portal.json.validator.JSONValidator;
import com.liferay.portal.json.validator.JSONValidatorException;

/**
 * @author Anderson Luiz
 * @author Thiago Buarque
 */
public class FrontendTokenDefinitionJSONValidator {

	public void validate(String frontendTokenDefinitionJSON)
		throws JSONValidatorException {

		_jsonValidator.validate(frontendTokenDefinitionJSON);
	}

	private static final JSONValidator _jsonValidator = new JSONValidator(
		FrontendTokenDefinition.class.getResource(
			"frontend-token-definition.schema.json"));

}
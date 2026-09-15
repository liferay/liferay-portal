/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.function.Function;

/**
 * @author Nathaly Gomes
 * @author Nícolas Moura
 */
public class ObjectDefinitionUtil {

	public static String getDescription(ObjectDefinition objectDefinition) {
		return _getDescription(
			languageId -> objectDefinition.getDescription(languageId, false),
			objectDefinition.getDefaultLanguageId());
	}

	public static String getDescription(
		ObjectDefinition objectDefinition, ObjectField objectField) {

		return _getDescription(
			languageId -> objectField.getDescription(languageId, false),
			objectDefinition.getDefaultLanguageId());
	}

	private static String _getDescription(
		Function<String, String> descriptionFunction,
		String defaultLanguageId) {

		String description = LocalizationUtil.getLocalization(
			descriptionFunction, LocaleUtil.toLanguageId(LocaleUtil.US),
			defaultLanguageId);

		if (Validator.isNull(description)) {
			return null;
		}

		return description;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Nathaly Gomes
 */
public class ObjectFieldDescriptionUtil {

	public static String getDescription(
		ObjectDefinition objectDefinition, ObjectField objectField) {

		String englishDescription = objectField.getDescription(
			LocaleUtil.US, false);

		if (Validator.isNotNull(englishDescription)) {
			return englishDescription;
		}

		String defaultLanguageDescription = objectField.getDescription(
			objectDefinition.getDefaultLanguageId(), false);

		if (Validator.isNotNull(defaultLanguageDescription)) {
			return defaultLanguageDescription;
		}

		return null;
	}

}
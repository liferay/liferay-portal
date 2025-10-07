/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Szimko
 */
public class NestedFieldsContextUtil {

	public static int limitDepth(int nestedFieldsDepth) {
		return Math.max(
			Math.min(
				nestedFieldsDepth,
				PropsValues.OBJECT_NESTED_FIELDS_MAX_QUERY_DEPTH),
			1);
	}

	public static List<String> toList(String nestedFields) {
		if (Validator.isNotNull(nestedFields)) {
			return Arrays.asList(nestedFields.split("\\s*,\\s*"));
		}

		return Collections.emptyList();
	}

}
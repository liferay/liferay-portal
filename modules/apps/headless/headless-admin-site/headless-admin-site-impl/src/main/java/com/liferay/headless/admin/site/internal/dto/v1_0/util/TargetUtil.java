/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class TargetUtil {

	public static final Map<String, String> externalToInternalValuesMap =
		HashMapBuilder.put(
			"Blank", "_blank"
		).put(
			"Self", "_self"
		).build();

	public static String toExternalValue(String value) {
		Set<String> externalValues = externalToInternalValuesMap.keySet();

		if (Objects.equals(value, "_parent") || Objects.equals(value, "_top")) {
			value = "_self";
		}

		for (String externalValue : externalValues) {
			if (Objects.equals(
					value, externalToInternalValuesMap.get(externalValue))) {

				return externalValue;
			}
		}

		return null;
	}

	public static String toInternalValue(String label) {
		return externalToInternalValuesMap.get(label);
	}

}
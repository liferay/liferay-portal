/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.util;

import com.liferay.portal.kernel.json.JSONUtil;

/**
 * @author Carolina Barbosa
 */
public class NumericInputMaskDDMFormFieldTypeUtil {

	public static String getJSON(String value) {
		if (JSONUtil.isJSONObject(value)) {
			return value;
		}

		return JSONUtil.put(
			"append", ""
		).put(
			"appendType", "prefix"
		).put(
			"decimalPlaces", 2
		).put(
			"symbols",
			JSONUtil.put(
				"decimalSymbol", "."
			).put(
				"thousandsSeparator", "none"
			)
		).toString();
	}

}
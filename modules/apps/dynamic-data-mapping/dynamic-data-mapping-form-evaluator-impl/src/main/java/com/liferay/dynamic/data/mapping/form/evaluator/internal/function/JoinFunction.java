/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Leonardo Barros
 */
public class JoinFunction
	implements DDMExpressionFunction.Function1<JSONArray, String> {

	public static final String NAME = "join";

	@Override
	public String apply(JSONArray jsonArray) {
		StringBundler sb = new StringBundler((jsonArray.length() * 2) - 1);

		for (int i = 0; i < jsonArray.length(); i++) {
			sb.append(GetterUtil.getString(jsonArray.get(i)));

			if (i >= (jsonArray.length() - 1)) {
				continue;
			}

			sb.append(CharPool.COMMA);
		}

		return sb.toString();
	}

	@Override
	public String getName() {
		return NAME;
	}

}
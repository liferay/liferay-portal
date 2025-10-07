/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class IsEmptyFunction
	implements DDMExpressionFunction.Function1<Object, Boolean> {

	public static final String NAME = "isEmpty";

	@Override
	public Boolean apply(Object parameter) {
		if (parameter == null) {
			return true;
		}

		if (isArray(parameter)) {
			Object[] values = (Object[])parameter;

			for (Object value : values) {
				if (Validator.isNotNull(value)) {
					return false;
				}
			}
		}

		if (parameter instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)parameter;

			return jsonArray.length() == 0;
		}

		if (parameter instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)parameter;

			return jsonObject.length() == 0;
		}

		if (parameter instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)parameter;

			if (MapUtil.isEmpty(map)) {
				return true;
			}

			for (Object object : map.values()) {
				if (Validator.isNotNull(object)) {
					return false;
				}
			}

			return true;
		}

		return Validator.isNull(parameter);
	}

	@Override
	public String getName() {
		return NAME;
	}

	protected boolean isArray(Object parameter) {
		Class<?> clazz = parameter.getClass();

		return clazz.isArray();
	}

}
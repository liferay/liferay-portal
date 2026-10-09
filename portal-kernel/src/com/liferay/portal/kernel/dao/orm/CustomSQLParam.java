/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 */
public class CustomSQLParam {

	public CustomSQLParam(String sql, Object value) {
		_sql = sql;
		_value = value;
	}

	public String getSQL() {
		return _sql;
	}

	public void process(QueryPos queryPos) {
		if (_value instanceof Long) {
			Long valueLong = (Long)_value;

			if (valueLong != null) {
				queryPos.add(valueLong);
			}
		}
		else if (_value instanceof Long[]) {
			Long[] valueArray = (Long[])_value;

			for (Long l : valueArray) {
				if (l == null) {
					continue;
				}

				queryPos.add(l);
			}
		}
		else if (_value instanceof String) {
			String valueString = (String)_value;

			if (Validator.isNotNull(valueString)) {
				queryPos.add(valueString);
			}
		}
		else if (_value instanceof String[]) {
			String[] valueArray = (String[])_value;

			for (String s : valueArray) {
				if (Validator.isNull(s)) {
					continue;
				}

				queryPos.add(s);
			}
		}
	}

	private final String _sql;
	private final Object _value;

}
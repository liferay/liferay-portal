/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.function.Function;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public class DefaultSQLTransformer implements SQLTransformer {

	public DefaultSQLTransformer(SQLTransformerLogic sqlTransformerLogic) {
		_functions = sqlTransformerLogic.getFunctions();
	}

	@Override
	public String transform(String sql) {
		if ((_functions == null) || (sql == null)) {
			return sql;
		}

		String transformedSQL = sql;

		for (Function<String, String> function : _functions) {
			transformedSQL = function.apply(transformedSQL);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Original SQL: " + sql);
			_log.debug("Transformed SQL: " + transformedSQL);
		}

		return transformedSQL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultSQLTransformer.class);

	private final Function<String, String>[] _functions;

}
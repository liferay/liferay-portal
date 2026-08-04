/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.kernel.dao.db.DBType;

/**
 * @author Eric Yan
 */
public class HibernateSQLFunctions {

	public static final String CAST_CLOB_TEXT = "cast_clob_text";

	public static String getCastClobTextSQL(DBType dbType) {
		if (dbType == DBType.DB2) {
			return "CAST(?1 AS VARCHAR(2000))";
		}
		else if (dbType == DBType.HYPERSONIC) {
			return "CONVERT(?1, SQL_VARCHAR)";
		}
		else if (dbType == DBType.ORACLE) {
			return "DBMS_LOB.SUBSTR(?1, 4000, 1)";
		}
		else if (dbType == DBType.POSTGRESQL) {
			return "CAST(?1 AS TEXT)";
		}
		else if (dbType == DBType.SQLSERVER) {
			return "CAST(?1 AS NVARCHAR(MAX))";
		}

		return "?1";
	}

}
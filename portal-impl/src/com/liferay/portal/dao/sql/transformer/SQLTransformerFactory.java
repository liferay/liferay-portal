/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBType;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public class SQLTransformerFactory {

	public static SQLTransformer getSQLTransformer(DB db) {
		DBType dbType = db.getDBType();

		SQLTransformerLogic sqlTransformerLogic = null;

		if (dbType == DBType.DB2) {
			sqlTransformerLogic = new DB2SQLTransformerLogic(db);
		}
		else if (dbType == DBType.HYPERSONIC) {
			sqlTransformerLogic = new HypersonicSQLTransformerLogic(db);
		}
		else if ((dbType == DBType.MARIADB) || (dbType == DBType.MYSQL)) {
			sqlTransformerLogic = new MySQLSQLTransformerLogic(db);
		}
		else if (dbType == DBType.ORACLE) {
			sqlTransformerLogic = new OracleSQLTransformerLogic(db);
		}
		else if (dbType == DBType.POSTGRESQL) {
			sqlTransformerLogic = new PostgreSQLTransformerLogic(db);
		}
		else if (dbType == DBType.SQLSERVER) {
			sqlTransformerLogic = new SQLServerSQLTransformerLogic(db);
		}
		else {
			return sql -> sql;
		}

		return new DefaultSQLTransformer(sqlTransformerLogic);
	}

}
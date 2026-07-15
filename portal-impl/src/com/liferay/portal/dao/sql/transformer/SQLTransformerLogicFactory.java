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
public class SQLTransformerLogicFactory {

	public static SQLTransformerLogic getSQLTransformerLogic(DB db) {
		DBType dbType = db.getDBType();

		if (dbType == DBType.DB2) {
			return new DB2SQLTransformerLogic(db);
		}
		else if (dbType == DBType.HYPERSONIC) {
			return new HypersonicSQLTransformerLogic(db);
		}
		else if ((dbType == DBType.MARIADB) || (dbType == DBType.MYSQL)) {
			return new MySQLSQLTransformerLogic(db);
		}
		else if (dbType == DBType.ORACLE) {
			return new OracleSQLTransformerLogic(db);
		}
		else if (dbType == DBType.POSTGRESQL) {
			return new PostgreSQLTransformerLogic(db);
		}
		else if (dbType == DBType.SQLSERVER) {
			return new SQLServerSQLTransformerLogic(db);
		}

		return () -> null;
	}

}
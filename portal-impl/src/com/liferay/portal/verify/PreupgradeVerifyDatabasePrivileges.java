/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;

import java.sql.PreparedStatement;

/**
 * @author Jorge Avalos
 */
public class PreupgradeVerifyDatabasePrivileges
	extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		DB db = DBManagerUtil.getDB();

		DBInspector dbInspector = new DBInspector(connection);

		try {
			if (dbInspector.hasTable("TEMP_TABLE")) {
				db.runSQL("drop table TEMP_TABLE");
			}

			db.runSQL("create table TEMP_TABLE (column1 int not null)");

			db.updateIndexes(
				connection, "TEMP_TABLE",
				"create index ix_temp on TEMP_TABLE (column1)", true);

			alterTableAddColumn("TEMP_TABLE", "column2", "int");

			db.runSQL("insert into TEMP_TABLE(column1, column2) values (1,1)");

			db.runSQL("update TEMP_TABLE set column2 = 2 where column1 = 1");

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select 1 from TEMP_TABLE where column1 = 1");

			preparedStatement.executeQuery();

			db.runSQL("delete from TEMP_TABLE where column1 = 1");

			db.runSQL("drop table TEMP_TABLE");
		}
		catch (Exception exception) {
			throw new VerifyException(
				"Database user is missing privileges: " +
					exception.getMessage());
		}
	}

}
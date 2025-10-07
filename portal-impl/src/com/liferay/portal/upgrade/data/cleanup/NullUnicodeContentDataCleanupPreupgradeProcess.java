/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;

import java.sql.PreparedStatement;

/**
 * @author Luis Ortiz
 */
public class NullUnicodeContentDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		if (dbInspector.hasColumn("DDMContent", "data_")) {
			_cleanUp("data_", dbInspector, "DDMContent");
		}

		if (dbInspector.hasColumn("JournalArticle", "content")) {
			_cleanUp("content", dbInspector, "JournalArticle");
		}
	}

	private void _cleanUp(
			String columnName, DBInspector dbInspector, String tableName)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		String likeClause = "'%\\\\u0000%'";
		String newData = "''";
		String oldData = "'\\u0000'";

		if ((db.getDBType() == DBType.DB2) ||
			(db.getDBType() == DBType.ORACLE)) {

			likeClause = "'%\\u0000%'";
		}
		else if (db.getDBType() == DBType.SQLSERVER) {
			likeClause = "N'%\\u0000%'";
			newData = "N" + newData;
			oldData = "N" + oldData;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName, " set ", columnName, " = replace(",
					columnName, ", ", oldData, ", ", newData, ") where ",
					columnName, " like ", likeClause))) {

			int count = preparedStatement.executeUpdate();

			DataCleanupLoggingUtil.logUpdate(
				_log, count, dbInspector.normalizeName(tableName),
				dbInspector.normalizeName(columnName), null,
				"it had invalid characters");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NullUnicodeContentDataCleanupPreupgradeProcess.class);

}
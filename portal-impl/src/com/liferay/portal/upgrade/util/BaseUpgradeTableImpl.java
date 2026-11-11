/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.util;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;

/**
 * @author Alexander Chow
 * @author Brian Wing Shun Chan
 */
public abstract class BaseUpgradeTableImpl extends Table {

	public BaseUpgradeTableImpl(String tableName) {
		super(tableName);
	}

	public String[] getIndexesSQL() {
		return _indexesSQL;
	}

	public boolean isAllowUniqueIndexes() {
		return _allowUniqueIndexes;
	}

	public boolean isDeleteTempFile() {
		return _deleteTempFile;
	}

	public void setAllowUniqueIndexes(boolean allowUniqueIndexes) {
		_allowUniqueIndexes = allowUniqueIndexes;
	}

	@Override
	public void setCreateSQL(String createSQL) throws Exception {
		if (_calledUpdateTable) {
			throw new UpgradeException(
				"setCreateSQL is called after updateTable");
		}

		super.setCreateSQL(createSQL);
	}

	public void setDeleteTempFile(boolean deleteTempFile) {
		_deleteTempFile = deleteTempFile;
	}

	public void setIndexesSQL(String[] indexesSQL) {
		_indexesSQL = indexesSQL;
	}

	public void updateTable() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			updateTable(connection, connection, true);
		}
	}

	protected void updateTable(
			Connection sourceConnection, Connection targetConnection,
			boolean deleteSource)
		throws Exception {

		_calledUpdateTable = true;

		generateTempFile(sourceConnection);

		String tempFileName = getTempFileName();

		try {
			DB db = DBManagerUtil.getDB();

			if (Validator.isNotNull(tempFileName) && deleteSource) {
				db.runSQL(sourceConnection, getDeleteSQL());
			}

			String createSQL = getCreateSQL();

			if (Validator.isNotNull(createSQL)) {
				if (deleteSource) {
					db.runSQL(sourceConnection, "drop table " + getTableName());
				}

				db.runSQL(targetConnection, createSQL);
			}

			populateTable(targetConnection);

			String[] indexesSQL = getIndexesSQL();

			for (String indexSQL : indexesSQL) {
				if (!isAllowUniqueIndexes() &&
					indexSQL.contains("create unique index")) {

					indexSQL = StringUtil.replace(
						indexSQL, "create unique index ", "create index ");
				}

				try {
					db.runSQLTemplate(targetConnection, indexSQL, false);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception.getMessage() + ": " + indexSQL);
					}
				}
			}
		}
		finally {
			if (Validator.isNotNull(tempFileName) && _deleteTempFile) {
				FileUtil.delete(tempFileName);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseUpgradeTableImpl.class);

	private boolean _allowUniqueIndexes;
	private boolean _calledUpdateTable;
	private boolean _deleteTempFile;
	private String[] _indexesSQL = new String[0];

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.extender.internal.upgrade.release;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.hibernate.DialectUtil;
import com.liferay.portal.upgrade.release.SchemaCreator;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Dictionary;

import javax.sql.DataSource;

import org.osgi.framework.Bundle;

/**
 * @author Shuyang Zhou
 */
public class SchemaCreatorImpl implements SchemaCreator {

	public SchemaCreatorImpl(Bundle bundle, DataSource dataSource) {
		_bundle = bundle;
		_dataSource = dataSource;
	}

	@Override
	public void create() throws UpgradeException {
		_db = DBManagerUtil.getDB(
			DialectUtil.getDialect(_dataSource), _dataSource);

		try {
			_db.process(
				companyId -> {
					if (_log.isInfoEnabled() &&
						Validator.isNotNull(companyId)) {

						_log.info(
							StringBundler.concat(
								toString(), StringPool.SPACE,
								_bundle.getSymbolicName(), "#", companyId));
					}

					_create();
				});
		}
		catch (Exception exception) {
			throw new UpgradeException(exception);
		}
	}

	@Override
	public String getBundleSymbolicName() {
		return _bundle.getSymbolicName();
	}

	@Override
	public String getSchemaVersion() {
		Dictionary<String, String> headers = _bundle.getHeaders(
			StringPool.BLANK);

		return GetterUtil.getString(
			headers.get("Liferay-Require-SchemaVersion"), "1.0.0");
	}

	private void _create() throws UpgradeException {
		String indexesSQL = DBResourceUtil.getModuleIndexesSQL(_bundle);
		String sequencesSQL = DBResourceUtil.getModuleSequencesSQL(_bundle);
		String tablesSQL = DBResourceUtil.getModuleTablesSQL(_bundle);

		try (Connection connection = _dataSource.getConnection()) {
			if (tablesSQL != null) {
				try {
					_db.runSQLTemplate(connection, tablesSQL, true);
				}
				catch (Exception exception) {
					throw new UpgradeException(
						StringBundler.concat(
							"Bundle ", _bundle,
							" has invalid content in tables.sql:\n", tablesSQL),
						exception);
				}
			}

			if (sequencesSQL != null) {
				try {
					_db.runSQLTemplate(connection, sequencesSQL, true);
				}
				catch (Exception exception) {
					throw new UpgradeException(
						StringBundler.concat(
							"Bundle ", _bundle,
							" has invalid content in sequences.sql:\n",
							sequencesSQL),
						exception);
				}
			}

			if (indexesSQL != null) {
				try {
					_db.runSQLTemplate(connection, indexesSQL, true);
				}
				catch (Exception exception) {
					throw new UpgradeException(
						StringBundler.concat(
							"Bundle ", _bundle,
							" has invalid content in indexes.sql:\n",
							indexesSQL),
						exception);
				}
			}
		}
		catch (SQLException sqlException) {
			throw new UpgradeException(sqlException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SchemaCreatorImpl.class);

	private final Bundle _bundle;
	private final DataSource _dataSource;
	private DB _db;

}
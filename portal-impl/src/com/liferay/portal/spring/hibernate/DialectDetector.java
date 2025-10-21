/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.jdbc.util.DBInfo;
import com.liferay.portal.dao.jdbc.util.DBInfoUtil;
import com.liferay.portal.dao.orm.hibernate.MariaDBDialect;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

/**
 * @author Brian Wing Shun Chan
 */
public class DialectDetector {

	public static Dialect getDialect(DataSource dataSource) {
		Dialect dialect = null;

		DBInfo dbInfo = DBInfoUtil.getDBInfo(dataSource);

		int dbMajorVersion = dbInfo.getMajorVersion();
		int dbMinorVersion = dbInfo.getMinorVersion();
		String dbName = dbInfo.getName();

		String dialectKey = null;

		try {
			dialectKey = StringBundler.concat(
				dbName, StringPool.COLON, dbMajorVersion, StringPool.COLON,
				dbMinorVersion);

			dialect = _dialects.get(dialectKey);

			if (dialect != null) {
				return dialect;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Determine dialect for ", dbName, " ", dbMajorVersion,
						".", dbMinorVersion));
			}

			if (dbName.startsWith("HSQL")) {
				dialect = new HSQLDialect();

				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Liferay is configured to use Hypersonic as its ",
							"database. Do NOT use Hypersonic in production. ",
							"Hypersonic is an embedded database useful for ",
							"development and demonstration purposes. The ",
							"database settings can be changed in ",
							"portal-ext.properties."));
				}
			}
			else if (dbName.startsWith("DB2") && (dbMajorVersion >= 9)) {
				dialect = new DB2Dialect();
			}
			else if (dbName.startsWith("MariaDB")) {
				dialect = new MariaDBDialect();
			}
			else if (dbName.startsWith("Microsoft")) {
				dialect = new SQLServerDialect();
			}
			else if (dbName.startsWith("Oracle")) {
				dialect = new OracleDialect();
			}
			else {
				try (Connection connection = dataSource.getConnection()) {
					DialectResolver dialectResolver =
						new StandardDialectResolver();

					dialect = dialectResolver.resolveDialect(
						new DatabaseMetaDataDialectResolutionInfoAdapter(
							connection.getMetaData()));
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (dialect == null) {
			throw new RuntimeException("No dialect found");
		}
		else if (dialectKey != null) {
			if (_log.isInfoEnabled()) {
				Class<?> clazz = dialect.getClass();

				_log.info(
					StringBundler.concat(
						"Using dialect ", clazz.getName(), " for ", dbName, " ",
						dbMajorVersion, ".", dbMinorVersion));
			}

			_dialects.put(dialectKey, dialect);
		}

		return dialect;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DialectDetector.class);

	private static final Map<String, Dialect> _dialects =
		new ConcurrentHashMap<>();

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.jdbc.util.DBInfo;
import com.liferay.portal.dao.jdbc.util.DBInfoUtil;
import com.liferay.portal.dao.orm.hibernate.DB2Dialect;
import com.liferay.portal.dao.orm.hibernate.HSQLDialect;
import com.liferay.portal.dao.orm.hibernate.MariaDBDialect;
import com.liferay.portal.dao.orm.hibernate.Oracle10gDialect;
import com.liferay.portal.dao.orm.hibernate.SQLServer2005Dialect;
import com.liferay.portal.dao.orm.hibernate.SQLServer2008Dialect;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.Connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.hibernate.dialect.DB2400Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

/**
 * @author Brian Wing Shun Chan
 */
public class DialectUtil {

	public static Dialect getDialect(DataSource dataSource) {
		DBInfo dbInfo = DBInfoUtil.getDBInfo(dataSource);

		Dialect dialect = _dialects.get(dbInfo);

		if (dialect != null) {
			return dialect;
		}

		int dbMajorVersion = dbInfo.getMajorVersion();
		int dbMinorVersion = dbInfo.getMinorVersion();
		String dbName = dbInfo.getName();

		try {
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
			else if (dbName.startsWith("Microsoft") && (dbMajorVersion == 9)) {
				dialect = new SQLServer2005Dialect();
			}
			else if (dbName.startsWith("Microsoft") && (dbMajorVersion >= 10)) {
				dialect = new SQLServer2008Dialect();
			}
			else if (dbName.startsWith("Oracle") && (dbMajorVersion >= 10)) {
				dialect = new Oracle10gDialect();
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
			String msg = GetterUtil.getString(exception.getMessage());

			if (msg.contains("explicitly set for database: DB2")) {
				dialect = new DB2400Dialect();

				if (_log.isWarnEnabled()) {
					_log.warn(
						"DB2400Dialect was dynamically chosen as the " +
							"Hibernate dialect for DB2. This can be " +
								"overriden in portal.properties");
				}
			}
			else {
				_log.error(exception);
			}
		}

		if (dialect == null) {
			throw new RuntimeException("No dialect found");
		}

		if (_log.isInfoEnabled()) {
			Class<?> clazz = dialect.getClass();

			_log.info(
				StringBundler.concat(
					"Using dialect ", clazz.getName(), " for ", dbName, " ",
					dbMajorVersion, ".", dbMinorVersion));
		}

		_dialects.put(dbInfo, dialect);

		return dialect;
	}

	public static void setDialect(DataSource dataSource, Dialect dialect) {
		_dialects.put(DBInfoUtil.getDBInfo(dataSource), dialect);
	}

	private static final Log _log = LogFactoryUtil.getLog(DialectUtil.class);

	private static final Map<DBInfo, Dialect> _dialects =
		new ConcurrentHashMap<>();

}
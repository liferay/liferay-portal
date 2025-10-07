/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.jdbc.util.DBInfo;
import com.liferay.portal.dao.jdbc.util.DBInfoUtil;
import com.liferay.portal.dao.orm.hibernate.DialectImpl;
import com.liferay.portal.dao.orm.hibernate.MariaDBDialect;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactory;
import com.liferay.portal.kernel.dao.db.DBManager;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.spring.hibernate.DialectDetector;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.SQLServerDialect;

/**
 * @author Alexander Chow
 * @author Brian Wing Shun Chan
 */
@SuppressWarnings("deprecation")
public class DBManagerImpl implements DBManager {

	public DBManagerImpl() {
		ServiceLoader<DBFactory> serviceLoader = ServiceLoader.load(
			DBFactory.class, DBManagerImpl.class.getClassLoader());

		for (DBFactory dbFactory : serviceLoader) {
			_dbFactories.put(dbFactory.getDBType(), dbFactory);
		}
	}

	@Override
	public DB getDB() {
		if (_db == null) {
			try {
				if (_log.isInfoEnabled()) {
					_log.info("Using dialect " + PropsValues.HIBERNATE_DIALECT);
				}

				Dialect dialect = (Dialect)InstanceFactory.newInstance(
					PropsValues.HIBERNATE_DIALECT);

				setDB(
					getDB(
						getDBType(dialect),
						InfrastructureUtil.getDataSource()));
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return _db;
	}

	@Override
	public DB getDB(DBType dbType, DataSource dataSource) {
		DBFactory dbCreator = _dbFactories.get(dbType);

		if (dbCreator == null) {
			throw new IllegalArgumentException(
				"Unsupported database type " + dbType);
		}

		if (dataSource == null) {
			return dbCreator.create(0, 0);
		}

		DBInfo dbInfo = DBInfoUtil.getDBInfo(dataSource);

		return dbCreator.create(
			dbInfo.getMajorVersion(), dbInfo.getMinorVersion());
	}

	@Override
	public int getDBInMaxParameters() {
		if (_databaseInMaxParameters != 0) {
			return _databaseInMaxParameters;
		}

		DBType dbType = getDBType();

		_databaseInMaxParameters = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.DATABASE_IN_MAX_PARAMETERS,
				new Filter(dbType.getName())),
			Integer.MAX_VALUE);

		return _databaseInMaxParameters;
	}

	@Override
	public int getDBMaxParameters() {
		if (_databaseMaxParameters != 0) {
			return _databaseMaxParameters;
		}

		DBType dbType = getDBType();

		_databaseMaxParameters = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.DATABASE_MAX_PARAMETERS,
				new Filter(dbType.getName())),
			Integer.MAX_VALUE);

		return _databaseMaxParameters;
	}

	@Override
	public DBType getDBType() {
		if (_db == null) {
			getDB();
		}

		return _db.getDBType();
	}

	@Override
	public DBType getDBType(DataSource dataSource) {
		return getDBType(DialectDetector.getDialect(dataSource));
	}

	@Override
	public DBType getDBType(Object dialect) {
		if (dialect instanceof DialectImpl) {
			DialectImpl dialectImpl = (DialectImpl)dialect;

			dialect = dialectImpl.getWrappedDialect();
		}

		if (dialect instanceof DB2Dialect) {
			return DBType.DB2;
		}

		if (dialect instanceof HSQLDialect) {
			return DBType.HYPERSONIC;
		}

		if (dialect instanceof MariaDBDialect) {
			return DBType.MARIADB;
		}

		if (dialect instanceof MySQLDialect) {
			return DBType.MYSQL;
		}

		if (dialect instanceof Oracle8iDialect ||
			dialect instanceof Oracle9Dialect) {

			return DBType.ORACLE;
		}

		if (dialect instanceof PostgreSQL82Dialect) {
			return DBType.POSTGRESQL;
		}

		if (dialect instanceof SQLServerDialect) {
			return DBType.SQLSERVER;
		}

		throw new IllegalArgumentException("Unknown dialect type " + dialect);
	}

	@Override
	public Set<DBType> getDBTypes() {
		return new LinkedHashSet<>(_dbFactories.keySet());
	}

	@Override
	public void setDB(DB db) {
		_db = db;

		if (_log.isDebugEnabled()) {
			Class<?> clazz = _db.getClass();

			_log.debug(
				StringBundler.concat(
					"Using DB implementation ", clazz.getName(), " for ",
					db.getDBType()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(DBManagerImpl.class);

	private int _databaseInMaxParameters;
	private int _databaseMaxParameters;
	private DB _db;
	private final EnumMap<DBType, DBFactory> _dbFactories = new EnumMap<>(
		DBType.class);

}
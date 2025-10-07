/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.DBTypeToSQLMap;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.PreupgradeVerifyDatabasePrivileges;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class PreupgradeVerifyDatabasePrivilegesTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();
		_dataSource = InfrastructureUtil.getDataSource();
		_db = DBManagerUtil.getDB();
		_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			PortalInstancePool.getDefaultCompanyId());
	}

	@AfterClass
	public static void tearDownClass() {
		if (_safeCloseable != null) {
			_safeCloseable.close();
		}
	}

	@Before
	public void setUp() throws Exception {
		if ((_db.getDBType() == DBType.DB2) ||
			(_db.getDBType() == DBType.ORACLE)) {

			return;
		}

		_createTestUser();
	}

	@After
	public void tearDown() throws Exception {
		if ((_db.getDBType() == DBType.DB2) ||
			(_db.getDBType() == DBType.ORACLE)) {

			return;
		}

		InfrastructureUtil.setDataSource(_dataSource);

		if (_db.getDBType() == DBType.POSTGRESQL) {
			DBInspector dbInspector = new DBInspector(
				DataAccess.getConnection());

			_db.runSQL(
				StringBundler.concat(
					"revoke all privileges on schema ", dbInspector.getSchema(),
					" from test"));
		}

		if (_testUserDataSource != null) {
			DataSourceFactoryUtil.destroyDataSource(_testUserDataSource);
		}

		_db.runSQL("drop user test");

		if (_db.getDBType() == DBType.SQLSERVER) {
			_db.runSQL("drop login test");
		}
	}

	@Test
	public void testVerifyAlterTablePrivilege() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.SQLSERVER));

		_revokePrivileges("alter");

		_initTestDatasource();

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			if (_db.getDBType() == DBType.SQLSERVER) {
				_verifyException(exception, "does not exist");
			}
			else {
				_verifyException(exception, "ALTER");
			}
		}
		finally {
			InfrastructureUtil.setDataSource(_dataSource);
		}
	}

	@Test
	public void testVerifyCreateTablePrivilege() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.POSTGRESQL) ||
			(_db.getDBType() == DBType.SQLSERVER));

		_revokePrivileges("create");

		_initTestDatasource();

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			if (_db.getDBType() == DBType.POSTGRESQL) {
				_verifyException(exception, "ERROR: permission denied");
			}
			else {
				_verifyException(exception, "CREATE");
			}
		}
	}

	@Test
	public void testVerifyDeleteRowPrivilege() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.SQLSERVER));

		_revokePrivileges("delete");

		_initTestDatasource();

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			_verifyException(exception, "DELETE");
		}
		finally {
			InfrastructureUtil.setDataSource(_dataSource);
		}
	}

	@Test
	public void testVerifyInsertRowPrivilege() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.SQLSERVER));

		_revokePrivileges("insert");

		_initTestDatasource();

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			_verifyException(exception, "INSERT");
		}
		finally {
			InfrastructureUtil.setDataSource(_dataSource);
		}
	}

	@Test
	public void testVerifySelectRowPrivilege() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.SQLSERVER));

		_revokePrivileges("select");

		_initTestDatasource();

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			_verifyException(exception, "SELECT");
		}
		finally {
			InfrastructureUtil.setDataSource(_dataSource);
		}
	}

	@Test
	public void testVerifyUpdateRowPrivilege() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.SQLSERVER));

		_revokePrivileges("update");

		_initTestDatasource();

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			_verifyException(exception, "UPDATE");
		}
		finally {
			InfrastructureUtil.setDataSource(_dataSource);
		}
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PreupgradeVerifyDatabasePrivileges();
	}

	private void _createTestUser() throws Exception {
		DBInspector dbInspector = new DBInspector(DataAccess.getConnection());

		if (_db.getDBType() == DBType.POSTGRESQL) {
			_db.runSQL(
				StringBundler.concat(
					"revoke create on schema ", dbInspector.getSchema(),
					" from public"));
		}

		if (_db.getDBType() == DBType.SQLSERVER) {
			_db.runSQL(
				StringBundler.concat(
					"create login [test] with password = 'test', ",
					"default_database = [", dbInspector.getCatalog(),
					"], check_policy = off"));
		}

		DBTypeToSQLMap dbTypeToSQLMap = new DBTypeToSQLMap(
			"create user 'test'@'%' identified BY 'test'");

		dbTypeToSQLMap.add(
			DBType.POSTGRESQL,
			"create user test with encrypted password 'test' noinherit");

		dbTypeToSQLMap.add(
			DBType.SQLSERVER,
			"create user [test] for login [test] with default_schema = " +
				dbInspector.getSchema());

		_db.runSQL(_connection, dbTypeToSQLMap);

		dbTypeToSQLMap = new DBTypeToSQLMap(
			"grant alter, create, delete, drop, index, insert, select, " +
				"update on *.* to 'test'@'%'");

		dbTypeToSQLMap.add(
			DBType.POSTGRESQL,
			StringBundler.concat(
				"grant usage, create on schema ", dbInspector.getSchema(),
				" to test"));

		if (_db.getDBType() == DBType.SQLSERVER) {
			_db.runSQL("grant create table to test");
		}

		dbTypeToSQLMap.add(
			DBType.SQLSERVER,
			StringBundler.concat(
				"grant alter, delete, insert, select, update  on schema::",
				dbInspector.getSchema(), " to test"));

		_db.runSQL(_connection, dbTypeToSQLMap);
	}

	private void _initTestDatasource() throws Exception {
		_testUserDataSource = DataSourceFactoryUtil.initDataSource(
			PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME,
			PropsValues.JDBC_DEFAULT_URL, "test", "test", StringPool.BLANK);

		InfrastructureUtil.setDataSource(_testUserDataSource);
	}

	private void _revokePrivileges(String privilege) throws Exception {
		DBInspector dbInspector = new DBInspector(DataAccess.getConnection());

		DBTypeToSQLMap dbTypeToSQLMap = new DBTypeToSQLMap(
			StringBundler.concat(
				"revoke ", privilege, " on *.* from 'test'@'%'"));

		dbTypeToSQLMap.add(
			DBType.POSTGRESQL,
			StringBundler.concat(
				"revoke ", privilege, " on schema ", dbInspector.getSchema(),
				" from test"));

		if (privilege.equals("create")) {
			dbTypeToSQLMap.add(
				DBType.SQLSERVER, "revoke create table from test");
		}
		else {
			dbTypeToSQLMap.add(
				DBType.SQLSERVER,
				StringBundler.concat(
					"revoke ", privilege, " on schema::",
					dbInspector.getSchema(), " from test"));
		}

		_db.runSQL(_connection, dbTypeToSQLMap);
	}

	private void _verifyException(Exception exception, String expectedMessage) {
		String message = exception.getMessage();

		Assert.assertTrue(message.contains(expectedMessage));
	}

	private static Connection _connection;
	private static DataSource _dataSource;
	private static DB _db;
	private static SafeCloseable _safeCloseable;
	private static DataSource _testUserDataSource;

}
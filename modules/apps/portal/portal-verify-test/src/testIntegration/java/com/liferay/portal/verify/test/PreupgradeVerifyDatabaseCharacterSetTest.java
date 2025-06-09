/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.verify.PreupgradeVerifyDatabaseCharacterSet;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class PreupgradeVerifyDatabaseCharacterSetTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_db = DBManagerUtil.getDB();

		_dataSource = InfrastructureUtil.getDataSource();

		if ((_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.MARIADB)) {

			_db.runSQL(
				"create database unsupported_character_set_db default " +
					"character set latin1");
		}
		else if (_db.getDBType() == DBType.POSTGRESQL) {
			_db.runSQL(
				"create database unsupported_character_set_db encoding " +
					"'LATIN1' lc_ctype 'C' lc_collate 'C' template template0");
		}
		else {
			return;
		}

		_unsupportedCharacterSetDataSource =
			DataSourceFactoryUtil.initDataSource(
				PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME, _getSchemaURL(),
				PropsValues.JDBC_DEFAULT_USERNAME,
				PropsValues.JDBC_DEFAULT_PASSWORD, StringPool.BLANK);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(_connection);

		if (_unsupportedCharacterSetDataSource != null) {
			DataSourceFactoryUtil.destroyDataSource(
				_unsupportedCharacterSetDataSource);

			_db.runSQL("drop database unsupported_character_set_db");
		}
	}

	@Test
	public void testVerifyMixedCharacterSet() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.MARIADB));

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				RandomTestUtil.nextLong());

		DBInspector dbInspector = new DBInspector(DataAccess.getConnection());

		String tableName = dbInspector.normalizeName("TestTable");

		serviceComponent.setMvccVersion(0);
		serviceComponent.setBuildNamespace("com.liferay.test.service.impl");
		serviceComponent.setData(
			StringBundler.concat("<![CDATA[create table ", tableName, " ("));

		_serviceComponentLocalService.addServiceComponent(serviceComponent);

		_db.runSQL(
			StringBundler.concat(
				"create table ", tableName,
				" (testColumn VARCHAR(75) primary key) ", "COLLATE utf8_bin"));

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			_verifyException(
				exception, "Mixed database character set and collation:");
		}
		finally {
			_serviceComponentLocalService.deleteServiceComponent(
				serviceComponent);

			_db.runSQL("drop table " + tableName);
		}
	}

	@Test
	public void testVerifyUnsupportedCharacterSet() {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MYSQL) ||
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.POSTGRESQL));

		try {
			InfrastructureUtil.setDataSource(
				_unsupportedCharacterSetDataSource);

			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			_verifyException(exception, "Unsupported database character set: ");
		}
		finally {
			InfrastructureUtil.setDataSource(_dataSource);
		}
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PreupgradeVerifyDatabaseCharacterSet();
	}

	private static String _getSchemaURL() throws Exception {
		return StringUtil.replace(
			PropsValues.JDBC_DEFAULT_URL, _connection.getCatalog(),
			"unsupported_character_set_db");
	}

	private void _verifyException(Exception exception, String expectedMessage) {
		Assert.assertNotNull(exception);

		String message = exception.getMessage();

		Assert.assertTrue(message.contains(expectedMessage));
	}

	private static Connection _connection;
	private static DataSource _dataSource;
	private static DB _db;

	@Inject
	private static ServiceComponentLocalService _serviceComponentLocalService;

	private static DataSource _unsupportedCharacterSetDataSource;

}
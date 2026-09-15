/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.PreupgradeVerifyDatabaseCharacterSet;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.sql.Connection;

import java.util.Collections;
import java.util.List;

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

		if ((_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL)) {

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

		_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			PortalInstancePool.getDefaultCompanyId());
		_unsupportedCharacterSetDataSource =
			DataSourceFactoryUtil.initDataSource(
				PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME, _getSchemaURL(),
				PropsValues.JDBC_DEFAULT_USERNAME,
				PropsValues.JDBC_DEFAULT_PASSWORD, StringPool.BLANK);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(_connection);

		if (_safeCloseable != null) {
			_safeCloseable.close();
		}

		if (_unsupportedCharacterSetDataSource != null) {
			DataSourceFactoryUtil.destroyDataSource(
				_unsupportedCharacterSetDataSource);

			_db.runSQL("drop database unsupported_character_set_db");
		}
	}

	@Test
	public void testVerifyMixedCharacterSet() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL));

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				RandomTestUtil.nextLong());

		DBInspector dbInspector = new DBInspector(_connection);

		String tableName = dbInspector.normalizeName("TestTable");

		serviceComponent.setMvccVersion(0);
		serviceComponent.setBuildNamespace("com.liferay.test.service.impl");
		serviceComponent.setData(
			StringBundler.concat("<![CDATA[create table ", tableName, " ("));

		_serviceComponentLocalService.addServiceComponent(serviceComponent);

		_db.runSQL(
			StringBundler.concat(
				"create table ", tableName,
				" (testColumn VARCHAR(75) primary key) collate utf8_bin"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PreupgradeVerifyDatabaseCharacterSet.class.getName(),
				LoggerTestUtil.WARN)) {

			testVerify();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message.contains(
					"Mixed character set and collation: " + tableName));
		}
		finally {
			_serviceComponentLocalService.deleteServiceComponent(
				serviceComponent);

			_db.runSQL("drop table " + tableName);
		}
	}

	@Test
	public void testVerifyMixedCharacterSetObjectTables() throws Exception {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL));

		User user = UserTestUtil.getAdminUser(
			CompanyThreadLocal.getCompanyId());

		ObjectField objectField = new TextObjectFieldBuilder(
		).userId(
			user.getUserId()
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).localized(
			true
		).name(
			"localizedField"
		).build();

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField),
				ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField),
				ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, user.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		_db.runSQL("drop table " + objectDefinition1.getDBTableName());
		_db.runSQL("drop table " + objectDefinition1.getExtensionDBTableName());
		_db.runSQL(
			"drop table " + objectDefinition1.getLocalizationDBTableName());
		_db.runSQL("drop table " + objectRelationship.getDBTableName());

		String createTableSQL =
			"create table %s (testColumn VARCHAR(75) primary key) collate " +
				"utf8_bin";

		_db.runSQL(
			String.format(createTableSQL, objectDefinition1.getDBTableName()));
		_db.runSQL(
			String.format(
				createTableSQL, objectDefinition1.getExtensionDBTableName()));
		_db.runSQL(
			String.format(
				createTableSQL,
				objectDefinition1.getLocalizationDBTableName()));
		_db.runSQL(
			String.format(createTableSQL, objectRelationship.getDBTableName()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PreupgradeVerifyDatabaseCharacterSet.class.getName(),
				LoggerTestUtil.WARN)) {

			testVerify();

			DBInspector dbInspector = new DBInspector(_connection);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 4, logEntries.size());

			String messages = logEntries.toString();

			_assertNormalizeName(
				dbInspector, messages, objectDefinition1.getDBTableName());
			_assertNormalizeName(
				dbInspector, messages,
				objectDefinition1.getExtensionDBTableName());
			_assertNormalizeName(
				dbInspector, messages,
				objectDefinition1.getLocalizationDBTableName());
			_assertNormalizeName(
				dbInspector, messages, objectRelationship.getDBTableName());
		}
	}

	@Test
	public void testVerifyUnsupportedCharacterSet() {
		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL) ||
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

	private void _assertNormalizeName(
			DBInspector dbInspector, String messages, String tableName)
		throws Exception {

		Assert.assertTrue(
			messages.contains(
				"Mixed character set and collation: " +
					dbInspector.normalizeName(tableName)));
	}

	private void _verifyException(Exception exception, String expectedMessage) {
		String message = exception.getMessage();

		Assert.assertTrue(message.contains(expectedMessage));
	}

	private static Connection _connection;
	private static DataSource _dataSource;
	private static DB _db;
	private static SafeCloseable _safeCloseable;
	private static DataSource _unsupportedCharacterSetDataSource;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}
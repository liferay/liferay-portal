/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.test.util;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.db.DBPartitionDB;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.VirtualHostImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.Set;

import javax.sql.DataSource;

import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Alberto Chaparro
 */
public abstract class BaseDBPartitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	public static void assume() {
		Assume.assumeTrue(DBPartition.isPartitionEnabled());

		if (db == null) {
			db = DBManagerUtil.getDB();
		}

		Assume.assumeTrue(db.isSupportsDBPartition());
	}

	protected static void addDBPartitions() throws Exception {
		CurrentConnection defaultCurrentConnection =
			CurrentConnectionUtil.getCurrentConnection();

		try {
			CurrentConnection currentConnection = dataSource -> connection;

			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				currentConnection);

			for (long companyId : COMPANY_IDS) {
				DBPartitionUtil.addDBPartition(companyId);

				PortalInstancePool.add(
					new CompanyImpl() {
						{
							setCompanyId(companyId);
							setWebId("Test" + companyId);
						}
					});
			}

			_clearCaches(COMPANY_IDS);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				defaultCurrentConnection);
		}
	}

	protected static void createControlTable(String tableName)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			db.runSQL(
				"create table " + tableName +
					" (testColumn bigint primary key)");
		}

		if (_controlTableNames == null) {
			_controlTableNames = ReflectionTestUtil.getFieldValue(
				DBInspector.class, "_controlTableNames");
		}

		_controlTableNames.add(StringUtil.toLowerCase(TEST_CONTROL_TABLE_NAME));
	}

	protected static void createIndex(String tableName) throws Exception {
		db.runSQL(getCreateIndexSQL(tableName));
	}

	protected static void createUniqueIndex(String tableName) throws Exception {
		db.runSQL(
			StringBundler.concat(
				"create unique index ", TEST_INDEX_NAME, " on ", tableName,
				" (testColumn)"));
	}

	protected static void deletePartitionRequiredData() throws Exception {
		try (Statement statement = connection.createStatement()) {
			for (long companyId : COMPANY_IDS) {
				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							companyId)) {

					statement.execute(
						"delete from Company where companyId = " + companyId);

					statement.execute(
						"delete from User_ where companyId = " + companyId);

					statement.execute(
						"delete from VirtualHost where companyId = " +
							companyId);
				}

				PortalInstancePool.remove(companyId);
			}
		}
	}

	protected static void dropControlTable(String tableName) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			dropTable(tableName);
		}

		if (_controlTableNames != null) {
			_controlTableNames.remove(StringUtil.toLowerCase(tableName));
		}
	}

	protected static void dropIndex(String tableName) throws Exception {
		db.runSQL(
			StringBundler.concat(
				"drop index ", TEST_INDEX_NAME, " on ", tableName));
	}

	protected static void dropSchemas() throws Exception {
		for (long companyId : COMPANY_IDS) {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(getPartitionName(companyId)));
		}
	}

	protected static void dropTable(String tableName) throws Exception {
		db.runSQL("drop table if exists " + tableName + " cascade");
	}

	protected static void extractDBPartitions() throws Exception {
		extractDBPartitions(COMPANY_IDS);
	}

	protected static void extractDBPartitions(long[] companyIds)
		throws Exception {

		_executeOnDBPartitions(companyIds, DBPartitionUtil::extractDBPartition);
	}

	protected static String getCreateIndexSQL(String tableName) {
		return StringBundler.concat(
			"create index ", TEST_INDEX_NAME, " on ", tableName,
			" (testColumn)");
	}

	protected static String getCreateTableSQL(String tableName) {
		return "create table " + tableName +
			" (testColumn bigint primary key, companyId bigint)";
	}

	protected static String getExtractedPartitionName(long companyId) {
		return ReflectionTestUtil.invoke(
			DBPartitionUtil.class, "_getExtractedPartitionName",
			new Class<?>[] {long.class}, companyId);
	}

	protected static String getPartitionName(long companyId) {
		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			return defaultPartitionName;
		}

		String databasePartitionSchemaNamePrefix =
			ReflectionTestUtil.getFieldValue(
				DBPartitionUtil.class,
				"_DATABASE_PARTITION_SCHEMA_NAME_PREFIX");

		return databasePartitionSchemaNamePrefix + companyId;
	}

	protected static void insertDBPartitions() throws Exception {
		CurrentConnection defaultCurrentConnection =
			CurrentConnectionUtil.getCurrentConnection();

		try {
			CurrentConnection currentConnection = dataSource -> connection;

			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				currentConnection);

			for (long companyId : COMPANY_IDS) {
				DBPartitionUtil.insertDBPartition(companyId);
			}
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				defaultCurrentConnection);
		}
	}

	protected static void insertPartitionData() throws Exception {
		for (long companyId : COMPANY_IDS) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);
				PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						"insert into Group_ (mvccVersion, ctCollectionId, " +
							"companyId, groupId, classNameId, classPK) " +
								"values (?, ?, ?, ?, ?, ?)");
				PreparedStatement preparedStatement2 =
					connection.prepareStatement(
						"insert into PasswordPolicy (mvccVersion, " +
							"passwordPolicyId, companyId, defaultPolicy) " +
								"values (?, ?, ?, ?)");
				PreparedStatement preparedStatement3 =
					connection.prepareStatement(
						"insert into Role_ (mvccVersion, ctCollectionId, " +
							"roleId, companyId, name, type_) values (?, ?, " +
								"?, ?, ?, ?)");
				PreparedStatement preparedStatement4 =
					connection.prepareStatement(
						"insert into User_ (userId, companyId, screenName, " +
							"emailAddress, languageId, timeZoneId, type_) " +
								"values (?, ?, ?, ?, ?, ?, ?)")) {

				preparedStatement1.setLong(1, 0);
				preparedStatement1.setLong(2, 0);
				preparedStatement1.setLong(3, companyId);
				preparedStatement1.setInt(4, 1);
				preparedStatement1.setLong(
					5, ClassNameLocalServiceUtil.getClassNameId(Company.class));
				preparedStatement1.setLong(6, companyId);

				preparedStatement1.executeUpdate();

				preparedStatement2.setLong(1, 0);
				preparedStatement2.setLong(2, 1);
				preparedStatement2.setLong(3, companyId);
				preparedStatement2.setBoolean(4, true);

				preparedStatement2.executeUpdate();

				for (int i = 0; i < ROLE_NAMES.length; i++) {
					preparedStatement3.setLong(1, 0);
					preparedStatement3.setLong(2, 0);
					preparedStatement3.setLong(3, i + 1);
					preparedStatement3.setLong(4, companyId);
					preparedStatement3.setString(5, ROLE_NAMES[i]);
					preparedStatement3.setLong(6, 1);

					preparedStatement3.executeUpdate();
				}

				preparedStatement4.setLong(1, 1);
				preparedStatement4.setLong(2, companyId);
				preparedStatement4.setString(3, "Test");
				preparedStatement4.setString(4, "test@test.com");
				preparedStatement4.setString(5, "en_US");
				preparedStatement4.setString(6, "UTC");
				preparedStatement4.setInt(7, UserConstants.TYPE_GUEST);

				preparedStatement4.executeUpdate();
			}
		}
	}

	protected static void insertPartitionRequiredData() throws Exception {
		for (long companyId : COMPANY_IDS) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);
				PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						"insert into Company (companyId, mx, webId) values " +
							"(?, ?, ?)");
				PreparedStatement preparedStatement2 =
					connection.prepareStatement(
						StringBundler.concat(
							"insert into VirtualHost (ctCollectionId, ",
							"virtualHostId, companyId, layoutSetId, hostname, ",
							"defaultVirtualHost) values (?, ?, ?, ?, ?, ?)"))) {

				preparedStatement1.setLong(1, companyId);
				preparedStatement1.setString(2, "liferay.com");
				preparedStatement1.setString(3, "Test" + companyId);

				preparedStatement1.executeUpdate();

				preparedStatement2.setLong(1, 0L);
				preparedStatement2.setLong(2, RandomTestUtil.randomLong());
				preparedStatement2.setLong(3, companyId);
				preparedStatement2.setLong(4, 0L);
				preparedStatement2.setString(5, "test" + companyId);
				preparedStatement2.setBoolean(6, true);

				preparedStatement2.executeUpdate();
			}

			Company company = new CompanyImpl();

			company.setCompanyId(companyId);
			company.setWebId("Test" + companyId);

			PortalInstancePool.add(company);
		}
	}

	protected static boolean isCopyableQuartzTable(String tableName) {
		return ReflectionTestUtil.invoke(
			DBPartitionUtil.class, "_isCopyableQuartzTable",
			new Class<?>[] {String.class}, tableName);
	}

	protected static void removeDBPartitions() throws Exception {
		removeDBPartitions(COMPANY_IDS);
	}

	protected static void removeDBPartitions(long[] companyIds)
		throws Exception {

		_executeOnDBPartitions(companyIds, DBPartitionUtil::removeDBPartition);

		for (long companyId : companyIds) {
			PortalInstancePool.remove(companyId);
		}

		_clearCaches(companyIds);
	}

	protected static void setUpClass() throws Exception {
		connection = DataAccess.getConnection();

		dbInspector = new DBInspector(connection);

		dbPartitionDB = ReflectionTestUtil.getFieldValue(
			DBPartitionUtil.class, "_dbPartitionDB");
		defaultPartitionName = ReflectionTestUtil.getFieldValue(
			DBPartitionUtil.class, "_defaultPartitionName");
	}

	protected static void setUpDBPartitions() throws Exception {
		addDBPartitions();

		insertPartitionRequiredData();

		insertPartitionData();
	}

	protected static void tearDownDBPartitions() throws Exception {
		deletePartitionRequiredData();

		removeDBPartitions();
	}

	protected void createAndPopulateControlTable(String tableName)
		throws Exception {

		createControlTable(tableName);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId());
			Statement statement = connection.createStatement()) {

			statement.execute("insert into " + tableName + " values (1)");
		}
	}

	protected void createAndPopulateTable(String tableName) throws Exception {
		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement()) {

			statement.execute(getCreateTableSQL(tableName));

			statement.execute(
				StringBundler.concat(
					"insert into ", tableName, " values (1, ",
					CompanyThreadLocal.getCompanyId(), ")"));
		}
	}

	protected static final long[] COMPANY_IDS = {123456789L, 987654321L};

	protected static final String[] ROLE_NAMES = {
		"Administrator", "Owner", "User"
	};

	protected static final String TEST_CONTROL_TABLE_NAME = "TestControlTable";

	protected static final String TEST_CONTROL_TABLE_NEW_COLUMN =
		"testControlTableNewColumn";

	protected static final String TEST_INDEX_NAME = "IX_Test";

	protected static final String TEST_TABLE_NAME = "TestTable";

	@Inject
	protected static CompanyLocalService companyLocalService;

	protected static Connection connection;
	protected static DB db;
	protected static DBInspector dbInspector;
	protected static DBPartitionDB dbPartitionDB;
	protected static String defaultPartitionName;

	@Inject
	protected static Portal portal;

	private static void _clearCaches(long[] companyIds) throws Exception {
		EntityCacheUtil.clearCache(CompanyImpl.class);
		EntityCacheUtil.clearCache(VirtualHostImpl.class);

		for (long companyId : companyIds) {
			PortalCacheHelperUtil.removePortalCaches(
				PortalCacheManagerNames.MULTI_VM, companyId);
		}
	}

	private static void _executeOnDBPartitions(
			long[] companyIds,
			UnsafeFunction<Long, Boolean, PortalException> unsafeFunction)
		throws Exception {

		CurrentConnection defaultCurrentConnection =
			CurrentConnectionUtil.getCurrentConnection();

		try {
			CurrentConnection currentConnection = dataSource -> connection;

			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				currentConnection);

			for (long companyId : companyIds) {
				unsafeFunction.apply(companyId);
			}
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				defaultCurrentConnection);
		}
	}

	private static Set<String> _controlTableNames;

}
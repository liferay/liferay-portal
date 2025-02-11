/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class DBPartitionUtilTest extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();
	}

	@Before
	public void setUp() throws Exception {
		for (long companyId : COMPANY_IDS) {
			db.runSQL(
				dbPartitionDB.getCreatePartitionSQL(
					connection, getPartitionName(companyId)));
		}

		_scheduleJob(PortalInstancePool.getDefaultCompanyId(), _JOB_NAME_1);
		_scheduleJob(PortalInstancePool.getDefaultCompanyId(), _JOB_NAME_2);
	}

	@After
	public void tearDown() throws Exception {
		dropSchemas();

		_schedulerEngine.delete(_JOB_GROUP_NAME, StorageType.PERSISTED);
	}

	@Test
	public void testAccessCompanyByCompanyThreadLocal() throws Exception {
		for (long companyId : COMPANY_IDS) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.
						setInitializingCompanyIdWithSafeCloseable(companyId);
				Connection connection = DataAccess.getConnection();
				Statement statement = connection.createStatement()) {

				createAndPopulateTable(TEST_TABLE_NAME);

				statement.execute("select 1 from " + TEST_TABLE_NAME);
			}
		}
	}

	@Test
	public void testAccessDefaultCompanyByCompanyThreadLocal()
		throws SQLException {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					portal.getDefaultCompanyId());
			Connection connection = DataAccess.getConnection();
			Statement statement = connection.createStatement()) {

			statement.execute("select 1 from CompanyInfo");
		}
	}

	@Test
	public void testAddDBPartition() throws Exception {
		addDBPartitions();

		try (Statement statement = connection.createStatement()) {
			for (long companyId : COMPANY_IDS) {
				statement.execute(
					"select 1 from " + getPartitionName(companyId) +
						".CompanyInfo");
			}
		}
		finally {
			removeDBPartitions();
		}
	}

	@Test
	public void testAddDefaultDBPartition() throws PortalException {
		Assert.assertFalse(
			DBPartitionUtil.addDBPartition(portal.getDefaultCompanyId()));
	}

	@Test
	public void testCopyDBPartition() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		CurrentConnection defaultCurrentConnection =
			CurrentConnectionUtil.getCurrentConnection();

		try (SafeCloseable safeCloseable1 =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			CurrentConnection currentConnection = dataSource -> connection;

			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				currentConnection);

			addDBPartitions();

			insertPartitionRequiredData();

			_scheduleJob(COMPANY_IDS[0], _JOB_NAME_1);

			String testObjectTableNamePrefix = dbInspector.normalizeName(
				"TestObjectTable_x_");

			try (SafeCloseable safeCloseable2 =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						COMPANY_IDS[0])) {

				createAndPopulateTable(
					testObjectTableNamePrefix + COMPANY_IDS[0]);

				_populateResourcePermissionTable(COMPANY_IDS[0]);
			}

			Assert.assertTrue(
				DBPartitionUtil.copyDBPartition(COMPANY_IDS[0], companyId));

			List<String> fromTableNames = _getObjectNames(
				"TABLE", getPartitionName(COMPANY_IDS[0]));

			Assert.assertTrue(
				fromTableNames.remove(
					testObjectTableNamePrefix + COMPANY_IDS[0]));
			Assert.assertTrue(
				fromTableNames.add(testObjectTableNamePrefix + companyId));

			List<String> toTableNames = _getObjectNames(
				"TABLE", getPartitionName(companyId));

			Assert.assertEquals(
				toTableNames.toString(), fromTableNames.size(),
				toTableNames.size());
			Assert.assertTrue(fromTableNames.containsAll(toTableNames));

			Assert.assertEquals(
				_JOBS_COUNT + 2, _getJobsCount(defaultPartitionName));
			Assert.assertEquals(1, _getJobsCountByCompany(companyId));

			_assertJobMessage(companyId, _JOB_NAME_1);
			_assertJobMessage(COMPANY_IDS[0], _JOB_NAME_1);

			Assert.assertEquals(
				_getObjectNames("VIEW", getPartitionName(COMPANY_IDS[0])),
				_getObjectNames("VIEW", getPartitionName(companyId)));

			for (String fromTableName : fromTableNames) {
				String toTableName = fromTableName;

				if (fromTableName.equals(
						testObjectTableNamePrefix + companyId)) {

					fromTableName = testObjectTableNamePrefix + COMPANY_IDS[0];
				}

				Assert.assertEquals(
					toTableName, _getCount(COMPANY_IDS[0], fromTableName),
					_getCount(companyId, toTableName));
			}

			_assertResourcePermissionTable(companyId);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				CurrentConnectionUtil.class, "_currentConnection",
				defaultCurrentConnection);

			removeDBPartitions(new long[] {companyId});

			deletePartitionRequiredData();

			removeDBPartitions();
		}
	}

	@Test
	public void testExtractAndInsertDBPartition() throws Exception {
		try {
			int companyCount = _getDefaultSchemaCount("Company");
			int virtualHostCount = _getDefaultSchemaCount("VirtualHost");

			addDBPartitions();
			insertPartitionRequiredData();

			HashMap<Long, List<String>> viewNames = new HashMap<>();
			HashMap<Long, Integer> tablesCount = new HashMap<>();

			for (long companyId : COMPANY_IDS) {
				viewNames.put(
					companyId,
					_getObjectNames("VIEW", getPartitionName(companyId)));
				tablesCount.put(
					companyId, _getTablesCount(getPartitionName(companyId)));

				_scheduleJob(companyId, _JOB_NAME_1);
			}

			Assert.assertEquals(
				COMPANY_IDS.length + _JOBS_COUNT,
				_getJobsCount(defaultPartitionName));

			extractDBPartitions();

			for (long companyId : COMPANY_IDS) {
				Assert.assertEquals(
					COMPANY_IDS.length + _JOBS_COUNT,
					_getJobsCount(getPartitionName(companyId)));
			}

			Assert.assertEquals(
				_JOBS_COUNT + COMPANY_IDS.length,
				_getJobsCount(defaultPartitionName));

			Assert.assertEquals(
				companyCount + COMPANY_IDS.length,
				_getDefaultSchemaCount("Company"));
			Assert.assertEquals(
				virtualHostCount + COMPANY_IDS.length,
				_getDefaultSchemaCount("VirtualHost"));

			try {
				insertDBPartitions();

				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertTrue(
					exception instanceof IllegalArgumentException);

				deletePartitionRequiredData();
				removeDBPartitions();
			}

			insertDBPartitions();

			Assert.assertEquals(
				companyCount + COMPANY_IDS.length,
				_getDefaultSchemaCount("Company"));
			Assert.assertEquals(
				virtualHostCount + COMPANY_IDS.length,
				_getDefaultSchemaCount("VirtualHost"));

			for (long companyId : COMPANY_IDS) {
				Assert.assertEquals(
					viewNames.get(companyId),
					_getObjectNames("VIEW", getPartitionName(companyId)));
				Assert.assertEquals(
					(int)tablesCount.get(companyId),
					_getTablesCount(getPartitionName(companyId)));
				Assert.assertEquals(1, _getJobsCountByCompany(companyId));
			}

			Assert.assertEquals(
				COMPANY_IDS.length + _JOBS_COUNT,
				_getJobsCount(defaultPartitionName));
		}
		finally {
			for (long companyId : COMPANY_IDS) {
				db.runSQL(
					dbPartitionDB.getDropPartitionSQL(
						getExtractedPartitionName(companyId)));
			}

			deletePartitionRequiredData();
			removeDBPartitions();
		}
	}

	@Test
	public void testExtractDBPartition() throws Exception {
		addDBPartitions();

		insertPartitionRequiredData();

		try {
			HashMap<Long, List<String>> viewNames = new HashMap<>();
			HashMap<Long, Integer> tablesCount = new HashMap<>();

			for (long companyId : COMPANY_IDS) {
				List<String> views = _getObjectNames(
					"VIEW", getPartitionName(companyId));

				viewNames.put(companyId, views);

				Assert.assertNotEquals(0, views.size());

				tablesCount.put(
					companyId, _getTablesCount(getPartitionName(companyId)));

				_scheduleJob(companyId, _JOB_NAME_1);
			}

			Assert.assertEquals(
				COMPANY_IDS.length + _JOBS_COUNT,
				_getJobsCount(defaultPartitionName));

			extractDBPartitions();

			for (long companyId : COMPANY_IDS) {
				String extractedPartitionName = getExtractedPartitionName(
					companyId);
				List<String> views = viewNames.get(companyId);

				Assert.assertEquals(
					tablesCount.get(companyId) + views.size(),
					_getTablesCount(extractedPartitionName));

				Assert.assertEquals(
					(int)tablesCount.get(companyId),
					_getTablesCount(getPartitionName(companyId)));
				Assert.assertEquals(0, _getViewsCount(extractedPartitionName));
				Assert.assertEquals(
					views.size(), _getViewsCount(getPartitionName(companyId)));

				for (String viewName : viewNames.get(companyId)) {
					if (!isCopyableQuartzTable(viewName)) {
						Assert.assertEquals(
							viewName + " count",
							_getCount(
								PortalInstancePool.getDefaultCompanyId(),
								viewName),
							_getCount(
								companyId, extractedPartitionName, viewName));
					}
					else if (StringUtil.equalsIgnoreCase(
								viewName, "QUARTZ_JOB_DETAILS") ||
							 StringUtil.equalsIgnoreCase(
								 viewName, "QUARTZ_SIMPROP_TRIGGERS") ||
							 StringUtil.equalsIgnoreCase(
								 viewName, "QUARTZ_TRIGGERS")) {

						Assert.assertEquals(
							viewName + " count", 1,
							_getCount(
								companyId, extractedPartitionName, viewName));
					}
					else {
						Assert.assertEquals(
							viewName + " count", 0,
							_getCount(
								companyId, extractedPartitionName, viewName));
					}
				}

				Assert.assertEquals(1, _getJobsCount(extractedPartitionName));
			}
		}
		finally {
			for (long companyId : COMPANY_IDS) {
				db.runSQL(
					dbPartitionDB.getDropPartitionSQL(
						getExtractedPartitionName(companyId)));
			}

			deletePartitionRequiredData();

			removeDBPartitions();
		}
	}

	@Test
	public void testForEachCompanyId() throws Exception {
		boolean originalDatabasePartitionThreadPoolEnabled =
			ReflectionTestUtil.getFieldValue(
				DBPartitionUtil.class,
				"_DATABASE_PARTITION_THREAD_POOL_ENABLED");

		try {
			addDBPartitions();

			insertPartitionRequiredData();

			_testForEachCompanyId(false);
			_testForEachCompanyId(true);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				DBPartitionUtil.class,
				"_DATABASE_PARTITION_THREAD_POOL_ENABLED",
				originalDatabasePartitionThreadPoolEnabled);

			deletePartitionRequiredData();
			removeDBPartitions();
		}
	}

	@Test
	public void testRemoveDBPartition() throws Exception {
		addDBPartitions();

		for (long companyId : COMPANY_IDS) {
			_scheduleJob(companyId, _JOB_NAME_1);
		}

		Assert.assertEquals(
			COMPANY_IDS.length + _JOBS_COUNT,
			_getJobsCount(defaultPartitionName));

		removeDBPartitions();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getCatalogs()) {
			while (resultSet.next()) {
				String catalogName = resultSet.getString("TABLE_CAT");

				for (long companyId : COMPANY_IDS) {
					Assert.assertNotEquals(
						getPartitionName(companyId), catalogName);
				}
			}
		}

		try (ResultSet resultSet = databaseMetaData.getSchemas()) {
			while (resultSet.next()) {
				String schemaName = resultSet.getString("TABLE_SCHEM");

				for (long companyId : COMPANY_IDS) {
					Assert.assertNotEquals(
						getPartitionName(companyId), schemaName);
				}
			}
		}

		Assert.assertEquals(_JOBS_COUNT, _getJobsCount(defaultPartitionName));
	}

	private void _assertJobMessage(long companyId, String jobName)
		throws Exception {

		String companyJobName = StringBundler.concat(
			jobName, StringPool.AT, companyId);

		SchedulerResponse schedulerResponse = _schedulerEngine.getScheduledJob(
			companyJobName, _JOB_GROUP_NAME, StorageType.PERSISTED);

		Message message = schedulerResponse.getMessage();

		Assert.assertEquals(companyId, message.getLong("companyId"));
		Assert.assertEquals(companyJobName, message.getString("JOB_NAME"));
	}

	private void _assertResourcePermissionTable(long companyId)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId);
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select primKey, primKeyId from ResourcePermission");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(companyId, resultSet.getLong("primKey"));
			Assert.assertEquals(companyId, resultSet.getLong("primKeyId"));
		}
	}

	private int _getCount(long companyId, String tableName) throws Exception {
		return _getCount(companyId, getPartitionName(companyId), tableName);
	}

	private int _getCount(
			long companyId, String partitionName, String tableName)
		throws Exception {

		String whereClause = StringPool.BLANK;

		if (dbInspector.hasColumn(tableName, "companyId")) {
			whereClause = " where companyId = " + companyId;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(1) from ", partitionName, StringPool.PERIOD,
					tableName, whereClause));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		}

		throw new Exception("Table does not exist");
	}

	private int _getDefaultSchemaCount(String tableName) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(1) from " + tableName);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		}

		throw new Exception("Table does not exist");
	}

	private int _getJobsCount(String partitionName) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(1) from ", partitionName,
					".QUARTZ_JOB_DETAILS where JOB_GROUP = '", _JOB_GROUP_NAME,
					"'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		}

		throw new Exception("Table does not exist");
	}

	private int _getJobsCountByCompany(long companyId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(1) from ", getPartitionName(companyId),
					".QUARTZ_JOB_DETAILS where JOB_GROUP = '", _JOB_GROUP_NAME,
					"' and JOB_NAME like '%@", companyId, "'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		}

		throw new Exception("Table does not exist");
	}

	private List<String> _getObjectNames(
			String objectType, String partitionName)
		throws Exception {

		List<String> objectNames = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getTables(
				dbPartitionDB.getCatalog(connection, partitionName),
				dbPartitionDB.getSchema(connection, partitionName), null,
				new String[] {objectType})) {

			while (resultSet.next()) {
				objectNames.add(resultSet.getString("TABLE_NAME"));
			}
		}

		return objectNames;
	}

	private int _getTablesCount(String partitionName) throws Exception {
		List<String> tableNames = _getObjectNames("TABLE", partitionName);

		return tableNames.size();
	}

	private int _getViewsCount(String partitionName) throws Exception {
		List<String> viewNames = _getObjectNames("VIEW", partitionName);

		return viewNames.size();
	}

	private void _populateResourcePermissionTable(Long companyId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into ResourcePermission (resourcePermissionId, " +
					"scope, primKey, primKeyId) values (?, ?, ?, ?)")) {

			preparedStatement.setLong(1, 1);
			preparedStatement.setLong(2, 1);
			preparedStatement.setString(3, companyId.toString());
			preparedStatement.setLong(4, companyId);

			preparedStatement.executeUpdate();
		}
	}

	private void _scheduleJob(long companyId, String jobName) throws Exception {
		String companyJobName = StringBundler.concat(
			jobName, StringPool.AT, companyId);

		Trigger trigger = _triggerFactory.createTrigger(
			companyJobName, _JOB_GROUP_NAME, null, null, 1, TimeUnit.DAY);

		Message message = new Message();

		message.put("JOB_NAME", companyJobName);
		message.put("companyId", companyId);

		_schedulerEngine.schedule(
			trigger, StringPool.BLANK, _JOB_GROUP_NAME, message,
			StorageType.PERSISTED);
	}

	private void _testForEachCompanyId(
			boolean databasePartitionThreadPoolEnabled)
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			DBPartitionUtil.class, "_DATABASE_PARTITION_THREAD_POOL_ENABLED",
			databasePartitionThreadPoolEnabled);

		List<Long> companyIds = new CopyOnWriteArrayList<>();

		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				Assert.assertEquals(
					companyId, CompanyThreadLocal.getCompanyId());

				Assert.assertTrue(CompanyThreadLocal.isLocked());

				companyIds.add(companyId);
			});

		Assert.assertEquals(
			companyIds.toString(), _getDefaultSchemaCount("Company"),
			companyIds.size());

		Assert.assertEquals(
			companyIds.toString(),
			(Long)PortalInstancePool.getDefaultCompanyId(), companyIds.get(0));
	}

	private static final String _JOB_GROUP_NAME = "liferay/test";

	private static final String _JOB_NAME_1 = "testjob1";

	private static final String _JOB_NAME_2 = "testjob2";

	private static final int _JOBS_COUNT = 2;

	@Inject(
		filter = "component.name=com.liferay.portal.scheduler.quartz.internal.QuartzSchedulerEngine"
	)
	private static SchedulerEngine _schedulerEngine;

	@Inject(
		filter = "component.name=com.liferay.portal.scheduler.quartz.internal.QuartzTriggerFactory"
	)
	private static TriggerFactory _triggerFactory;

}
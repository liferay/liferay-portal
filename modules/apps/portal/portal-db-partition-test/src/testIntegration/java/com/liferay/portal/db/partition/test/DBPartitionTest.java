/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.persistence.PortletPersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.model.impl.ClassNameImpl;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.service.impl.ClassNameLocalServiceImpl;
import com.liferay.portal.service.impl.ResourceActionLocalServiceImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class DBPartitionTest extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		createControlTable(TEST_CONTROL_TABLE_NAME);

		BaseDBPartitionTestCase.setUpDBPartitions();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		BaseDBPartitionTestCase.tearDownDBPartitions();

		dropControlTable(TEST_CONTROL_TABLE_NAME);
	}

	@After
	public void tearDown() throws Exception {
		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				if (dbInspector.hasIndex(
						TEST_CONTROL_TABLE_NAME, TEST_INDEX_NAME)) {

					dropIndex(TEST_CONTROL_TABLE_NAME);
				}

				dropTable(TEST_TABLE_NAME);
				_counterLocalService.reset(_CLASS_NAME);
			});
	}

	@Test
	public void testAddIndexControlTable() throws Exception {
		DBPartitionUtil.forEachCompanyId(
			companyId -> createIndex(TEST_CONTROL_TABLE_NAME));

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			Assert.assertTrue(
				dbInspector.hasIndex(TEST_CONTROL_TABLE_NAME, TEST_INDEX_NAME));
		}
	}

	@Test
	public void testAddIndexControlTableSystemCompany() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					CompanyConstants.SYSTEM)) {

			createIndex(TEST_CONTROL_TABLE_NAME);

			Assert.assertTrue(
				dbInspector.hasIndex(TEST_CONTROL_TABLE_NAME, TEST_INDEX_NAME));
		}
	}

	@Test
	public void testAddUniqueIndexControlTable() throws Exception {
		DBPartitionUtil.forEachCompanyId(
			companyId -> createUniqueIndex(TEST_CONTROL_TABLE_NAME));

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			Assert.assertTrue(
				dbInspector.hasIndex(TEST_CONTROL_TABLE_NAME, TEST_INDEX_NAME));
		}
	}

	@Test
	public void testAlterControlTable() throws Exception {
		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> db.runSQL(
					StringBundler.concat(
						"alter table ", TEST_CONTROL_TABLE_NAME, " add column ",
						TEST_CONTROL_TABLE_NEW_COLUMN, " bigint")));

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						PortalInstancePool.getDefaultCompanyId())) {

				Assert.assertTrue(
					dbInspector.hasColumn(
						TEST_CONTROL_TABLE_NAME,
						TEST_CONTROL_TABLE_NEW_COLUMN));
			}
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					if (dbInspector.hasColumn(
							TEST_CONTROL_TABLE_NAME,
							TEST_CONTROL_TABLE_NEW_COLUMN)) {

						db.runSQL(
							StringBundler.concat(
								"alter table ", TEST_CONTROL_TABLE_NAME,
								" drop column ",
								TEST_CONTROL_TABLE_NEW_COLUMN));
					}
				});
		}
	}

	@Test
	public void testCollideClassNameId() throws Exception {
		long classNameId = 1000000000L;

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					ClassName className = new ClassNameImpl();

					className.setClassNameId(classNameId);
					className.setValue("class.name." + companyId);

					_classNameLocalService.addClassName(className);
				});

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					ClassName className =
						_classNameLocalService.fetchByClassNameId(classNameId);

					Assert.assertEquals(
						classNameId, className.getClassNameId());
					Assert.assertEquals(
						"class.name." + companyId, className.getValue());
				});
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _classNameLocalService.deleteClassName(
					classNameId));
		}
	}

	@Test
	public void testCopyClassName() throws Exception {
		String classNameValue = "";
		long classNameId = 0;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select value, classNameId from ClassName_ order by " +
							"classNameId asc limit 1; ");
				ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.next()) {
					classNameValue = resultSet.getString(1);
					classNameId = resultSet.getLong(2);
				}
			}
		}

		ClassName nullClassName = ReflectionTestUtil.getFieldValue(
			ClassNameLocalServiceImpl.class, "_nullClassName");

		long finalClassNameId = classNameId;
		String finalClassNameValue = classNameValue;

		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				ClassName className = _classNameLocalService.fetchClassName(
					finalClassNameValue);

				Assert.assertNotEquals(nullClassName, className);
				Assert.assertEquals(
					finalClassNameId, className.getClassNameId());
				Assert.assertEquals(finalClassNameValue, className.getValue());
			});
	}

	@Test
	public void testCopyConfiguration() throws Exception {
		for (long companyId : COMPANY_IDS) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				int rowCount = -1;

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							"select count(1) from Configuration_");
					ResultSet resultSet = preparedStatement.executeQuery()) {

					if (resultSet.next()) {
						rowCount = resultSet.getInt(1);
					}
				}

				Assert.assertEquals(0, rowCount);
			}
		}
	}

	@Test
	public void testCopyResourceAction() throws Exception {
		EntityCacheUtil.clearCache(ResourceActionImpl.class);

		Map<String, ResourceAction> resourceActions =
			ReflectionTestUtil.getFieldValue(
				ResourceActionLocalServiceImpl.class, "_resourceActions");

		resourceActions.clear();

		String actionId = "";
		long bitwiseValue = 0;
		String name = "";
		long resourceActionId = 0;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select resourceActionId, name, actionId, " +
							"bitwiseValue from ResourceAction order by " +
								"resourceActionId asc limit 1;");
				ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.next()) {
					actionId = resultSet.getString(3);
					bitwiseValue = resultSet.getLong(4);
					name = resultSet.getString(2);
					resourceActionId = resultSet.getLong(1);
				}
			}
		}

		String finalActionId = actionId;
		long finalBitwiseValue = bitwiseValue;
		String finalName = name;
		long finalResourceActionId = resourceActionId;

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					ResourceAction resourceAction =
						_resourceActionLocalService.fetchResourceAction(
							finalName, finalActionId);

					Assert.assertNotNull(resourceAction);
					Assert.assertEquals(
						finalBitwiseValue, resourceAction.getBitwiseValue());
					Assert.assertEquals(
						finalResourceActionId,
						resourceAction.getResourceActionId());
				});
		}
		finally {
			EntityCacheUtil.clearCache(ResourceActionImpl.class);

			resourceActions.clear();
		}
	}

	@Test
	public void testCounterGetNames() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			_counterLocalService.increment(_CLASS_NAME);

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					List<String> counterNames = _counterLocalService.getNames();

					if (companyId.equals(COMPANY_IDS[0])) {
						Assert.assertTrue(counterNames.contains(_CLASS_NAME));
					}
					else {
						Assert.assertFalse(counterNames.contains(_CLASS_NAME));
					}
				});
		}
	}

	@Test
	public void testCounterIncrement() throws Exception {
		Map<Long, Long> counterSizes = new HashMap<>();

		DBPartitionUtil.forEachCompanyId(
			companyId -> counterSizes.put(
				companyId, _counterLocalService.increment()));

		DBPartitionUtil.forEachCompanyId(
			companyId -> Assert.assertEquals(
				counterSizes.get(companyId) + 1,
				_counterLocalService.increment()));
	}

	@Test
	public void testCounterIncrementWithName() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			Assert.assertEquals(
				1, _counterLocalService.increment(getClass().getName()));

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					if (companyId.equals(COMPANY_IDS[0])) {
						Assert.assertEquals(
							2, _counterLocalService.increment(_CLASS_NAME));
					}
					else {
						Assert.assertEquals(
							1, _counterLocalService.increment(_CLASS_NAME));
					}
				});
		}
	}

	@Test
	public void testCounterIncrementWithNameAndSize() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			Assert.assertEquals(
				10, _counterLocalService.increment(_CLASS_NAME, 10));

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					if (companyId.equals(COMPANY_IDS[0])) {
						Assert.assertEquals(
							20,
							_counterLocalService.increment(_CLASS_NAME, 10));
					}
					else {
						Assert.assertEquals(
							10,
							_counterLocalService.increment(_CLASS_NAME, 10));
					}
				});
		}
	}

	@Test
	public void testCounterRename() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			try {
				DBPartitionUtil.forEachCompanyId(
					companyId -> _counterLocalService.increment(_CLASS_NAME));

				_counterLocalService.rename(_CLASS_NAME, _CLASS_NAME + ".test");

				DBPartitionUtil.forEachCompanyId(
					companyId -> {
						List<String> counterNames =
							_counterLocalService.getNames();

						if (companyId.equals(COMPANY_IDS[0])) {
							Assert.assertFalse(
								counterNames.contains(_CLASS_NAME));
							Assert.assertTrue(
								counterNames.contains(_CLASS_NAME + ".test"));
						}
						else {
							Assert.assertFalse(
								counterNames.contains(_CLASS_NAME + ".test"));
							Assert.assertTrue(
								counterNames.contains(_CLASS_NAME));
						}
					});
			}
			finally {
				DBPartitionUtil.forEachCompanyId(
					companyId -> _counterLocalService.reset(
						_CLASS_NAME + ".test"));
			}
		}
	}

	@Test
	public void testCounterReset() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			DBPartitionUtil.forEachCompanyId(
				companyId -> _counterLocalService.increment(_CLASS_NAME));

			_counterLocalService.reset(_CLASS_NAME);

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					List<String> counterNames = _counterLocalService.getNames();

					if (companyId.equals(COMPANY_IDS[0])) {
						Assert.assertFalse(counterNames.contains(_CLASS_NAME));
					}
					else {
						Assert.assertTrue(counterNames.contains(_CLASS_NAME));
					}
				});
		}
	}

	@Test
	public void testCounterResetWithIncrement() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					COMPANY_IDS[0])) {

			DBPartitionUtil.forEachCompanyId(
				companyId -> _counterLocalService.increment(_CLASS_NAME));

			_counterLocalService.reset(_CLASS_NAME, 100);

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					if (companyId.equals(COMPANY_IDS[0])) {
						Assert.assertEquals(
							101, _counterLocalService.increment(_CLASS_NAME));
					}
					else {
						Assert.assertEquals(
							2, _counterLocalService.increment(_CLASS_NAME));
					}
				});
		}
	}

	@Test
	public void testDatabasePartitionSchemaNamePrefixes() throws Exception {
		String[] databasePartitionSchemaNamePrefixes = {
			ReflectionTestUtil.getFieldValue(
				DBPartitionUtil.class,
				"_DATABASE_EXPORTED_PARTITION_SCHEMA_NAME_PREFIX"),
			ReflectionTestUtil.getFieldValue(
				DBPartitionUtil.class,
				"_DATABASE_EXPORTED_PARTITION_SCHEMA_NAME_PREFIX")
		};

		for (String databasePartitionSchemaNamePrefix :
				databasePartitionSchemaNamePrefixes) {

			String databasePartitionSchemaNamePrefixSQL = StringBundler.concat(
				"drop view if exists ", databasePartitionSchemaNamePrefix,
				COMPANY_IDS[0], ".TestView");

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						CompanyConstants.SYSTEM)) {

				db.runSQL(databasePartitionSchemaNamePrefixSQL);
			}

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						PortalInstancePool.getDefaultCompanyId())) {

				db.runSQL(databasePartitionSchemaNamePrefixSQL);
			}

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						COMPANY_IDS[0])) {

				db.runSQL(databasePartitionSchemaNamePrefixSQL);

				Assert.fail();
			}
			catch (UnsupportedOperationException
						unsupportedOperationException) {

				Assert.assertEquals(
					"Unsupported SQL: " + databasePartitionSchemaNamePrefixSQL,
					unsupportedOperationException.getMessage());
			}
		}
	}

	@Test
	public void testDeployRemotePortlet() throws Exception {
		String portletName = RandomTestUtil.randomString();

		try {
			_deployRemotePortlet(CompanyConstants.SYSTEM, portletName);

			Portlet portlet = _portletLocalService.getPortletById(portletName);

			DBPartitionUtil.forEachCompanyId(
				companyId -> Assert.assertEquals(
					portlet, _portletLocalService.getPortletById(portletName)));
		}
		finally {
			Portlet portlet = _portletLocalService.getPortletById(portletName);

			_portletLocalService.destroyRemotePortlet(portlet);
		}

		try {
			_deployRemotePortlet(TestPropsValues.getCompanyId(), portletName);

			long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();

			_deployRemotePortlet(defaultCompanyId, portletName);

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						TestPropsValues.getCompanyId())) {

				Portlet portlet = _portletLocalService.getPortletById(
					portletName);

				Assert.assertEquals(
					TestPropsValues.getCompanyId(), portlet.getCompanyId());
			}

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						defaultCompanyId)) {

				Portlet portlet = _portletLocalService.getPortletById(
					portletName);

				Assert.assertEquals(defaultCompanyId, portlet.getCompanyId());
			}

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						COMPANY_IDS[0])) {

				Assert.assertNull(
					_portletLocalService.getPortletById(portletName));
			}
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					Portlet portlet = _portletLocalService.getPortletById(
						companyId, portletName);

					if (portlet != null) {
						_portletLocalService.destroyRemotePortlet(portlet);
					}
				});
		}
	}

	@Test
	public void testDropIndexControlTable() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			createIndex(TEST_CONTROL_TABLE_NAME);
		}

		DBPartitionUtil.forEachCompanyId(
			companyId -> dropIndex(TEST_CONTROL_TABLE_NAME));

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			Assert.assertTrue(
				!dbInspector.hasIndex(
					TEST_CONTROL_TABLE_NAME, TEST_INDEX_NAME));
		}
	}

	@Test
	public void testGetClassName() throws Exception {
		Set<ClassName> classNames = Collections.synchronizedSet(
			Collections.newSetFromMap(new IdentityHashMap<>()));

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> Assert.assertTrue(
					classNames.add(
						_classNameLocalService.getClassName(
							"class.name.test"))));

			Assert.assertEquals(
				classNames.toString(), companyLocalService.getCompaniesCount(),
				classNames.size());
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _classNameLocalService.deleteClassName(
					_classNameLocalService.fetchClassName("class.name.test")));
		}
	}

	@Test
	public void testGetClassNameIdsSupplier() throws Exception {
		_assertClassNameIds(
			classNameIds -> {
				for (long classNameId :
						_classNameLocalService.getClassNameIdsSupplier(
							new String[] {"class.name.test"}
						).get()) {

					classNameIds.add(classNameId);
				}
			});
	}

	@Test
	public void testGetClassNameIdSupplier() throws Exception {
		_assertClassNameIds(
			classNameIds -> classNameIds.add(
				_classNameLocalService.getClassNameIdSupplier(
					"class.name.test"
				).get()));
	}

	@Test
	public void testGetResourceAction() throws Exception {
		Set<ResourceAction> resourceActions = new CopyOnWriteArraySet<>();

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					CounterLocalServiceUtil.increment(
						ResourceAction.class.getName(),
						RandomTestUtil.randomInt());

					_resourceActionLocalService.addResourceAction(
						"resource.action.test", "TEST", companyId);

					_resourceActionLocalService.checkResourceActions(
						"resource.action.test",
						Collections.singletonList("TEST"));
				});

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					ResourceAction resourceAction =
						_resourceActionLocalService.getResourceAction(
							"resource.action.test", "TEST");

					Assert.assertTrue(resourceActions.add(resourceAction));

					Assert.assertEquals(
						(long)companyId, resourceAction.getBitwiseValue());
				});

			Assert.assertEquals(
				resourceActions.toString(),
				companyLocalService.getCompaniesCount(),
				resourceActions.size());
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					ResourceAction resourceAction =
						_resourceActionLocalService.fetchResourceAction(
							"resource.action.test", "TEST");

					if (resourceAction != null) {
						_resourceActionLocalService.deleteResourceAction(
							resourceAction);
					}
				});
		}
	}

	@Test(expected = PortalException.class)
	public void testIllegalDatabasePartitionSchemaNamePrefix()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DATABASE_PARTITION_SCHEMA_NAME_PREFIX",
					"VeryLongIdentifier")) {

			DBPartitionUtil.checkDatabasePartitionSchemaNamePrefix();
		}
	}

	@Test
	public void testInitResourceActions() throws Exception {
		DBPartitionUtil.forEachCompanyId(
			companyId -> StartupHelperUtil.initResourceActions());
	}

	@Test
	public void testRegenerateViews() throws Exception {
		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> db.runSQL(
					StringBundler.concat(
						"alter table ", TEST_CONTROL_TABLE_NAME, " add column ",
						TEST_CONTROL_TABLE_NEW_COLUMN, " bigint")));

			DBPartitionUtil.forEachCompanyId(
				companyId -> Assert.assertTrue(
					dbInspector.hasColumn(
						TEST_CONTROL_TABLE_NAME,
						TEST_CONTROL_TABLE_NEW_COLUMN)));
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					if (dbInspector.hasColumn(
							TEST_CONTROL_TABLE_NAME,
							TEST_CONTROL_TABLE_NEW_COLUMN)) {

						db.runSQL(
							StringBundler.concat(
								"alter table ", TEST_CONTROL_TABLE_NAME,
								" drop column ",
								TEST_CONTROL_TABLE_NEW_COLUMN));
					}
				});
		}
	}

	@Test
	public void testUpdateIndexes() throws Exception {
		DataSource dataSource = InfrastructureUtil.getDataSource();

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					createAndPopulateTable(TEST_TABLE_NAME);

					Assert.assertFalse(
						dbInspector.hasIndex(TEST_TABLE_NAME, TEST_INDEX_NAME));

					try (Connection connection = dataSource.getConnection()) {
						db.updateIndexes(
							connection, TEST_TABLE_NAME,
							getCreateIndexSQL(TEST_TABLE_NAME), true);
					}

					Assert.assertTrue(
						dbInspector.hasIndex(TEST_TABLE_NAME, TEST_INDEX_NAME));
				});
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> dropTable(TEST_TABLE_NAME));
		}
	}

	@Test
	public void testUpdateIndexOnControlTable() throws Exception {
		DataSource dataSource = InfrastructureUtil.getDataSource();

		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				try (Connection connection = dataSource.getConnection();
					LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
						"com.liferay.portal.dao.db.BaseDB",
						LoggerTestUtil.INFO)) {

					db.updateIndexes(
						connection, "Company",
						"create index " + TEST_INDEX_NAME +
							" on Company (logoId, companyId);",
						false);

					List<LogEntry> logEntries = logCapture.getLogEntries();

					long expectedLogEntriesCount = 0;

					if (companyId == PortalInstancePool.getDefaultCompanyId()) {
						expectedLogEntriesCount = 1;
					}

					Assert.assertEquals(
						logEntries.toString(), expectedLogEntriesCount,
						logEntries.size());
				}
				finally {
					if (dbInspector.hasIndex("Company", TEST_INDEX_NAME)) {
						db.runSQL(
							"drop index " + TEST_INDEX_NAME + " on Company");
					}
				}
			});
	}

	@Test
	public void testUpgrade() throws Exception {
		DBPartitionUpgradeProcess dbPartitionUpgradeProcess =
			new DBPartitionUpgradeProcess();

		dbPartitionUpgradeProcess.upgrade();

		long[] expectedCompanyIds = PortalInstancePool.getCompanyIds();

		Arrays.sort(expectedCompanyIds);

		long[] actualCompanyIds = dbPartitionUpgradeProcess.getCompanyIds();

		Arrays.sort(actualCompanyIds);

		Assert.assertArrayEquals(expectedCompanyIds, actualCompanyIds);
	}

	public class DBPartitionUpgradeProcess extends UpgradeProcess {

		public long[] getCompanyIds() {
			return ArrayUtil.toArray(_companyIds.toArray(new Long[0]));
		}

		@Override
		protected void doUpgrade() throws Exception {
			_companyIds.add(CompanyThreadLocal.getCompanyId());
		}

		private volatile List<Long> _companyIds = new CopyOnWriteArrayList<>();

	}

	private void _assertClassNameIds(Consumer<Set<Long>> consumer)
		throws Exception {

		Set<Long> classNameIds = Collections.synchronizedSet(
			Collections.newSetFromMap(new IdentityHashMap<>()));

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> consumer.accept(classNameIds));

			Assert.assertEquals(
				classNameIds.toString(),
				companyLocalService.getCompaniesCount(), classNameIds.size());
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _classNameLocalService.deleteClassName(
					_classNameLocalService.fetchClassName("class.name.test")));
		}
	}

	private void _deployRemotePortlet(long companyId, String portletName)
		throws Exception {

		Portlet portlet = _portletPersistence.create(0);

		portlet.setCompanyId(companyId);
		portlet.setPortletId(portletName);

		companyId = (companyId == CompanyConstants.SYSTEM) ?
			PortalInstancePool.getDefaultCompanyId() : companyId;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			_portletLocalService.deployRemotePortlet(
				new long[] {companyId}, portlet,
				new String[] {"category.hidden"}, true, true);
		}
	}

	private static final String _CLASS_NAME = DBPartitionTest.class.getName();

	@Inject
	private static ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPersistence _portletPersistence;

}
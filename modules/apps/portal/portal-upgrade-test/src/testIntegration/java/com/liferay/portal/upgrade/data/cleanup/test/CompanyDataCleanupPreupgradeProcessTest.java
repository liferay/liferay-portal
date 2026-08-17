/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.instance.lifecycle.PortalInstanceLifecycleManager;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.DataCleanupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.CompanyDataCleanupPreupgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Luis Ortiz
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CompanyDataCleanupPreupgradeProcessTest
	extends CompanyDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNamesSavepointSafeCloseable =
			DataCleanupTestUtil.getClassNamesSavepointSafeCloseable();

		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		_resourceActionsSavepointSafeCloseable =
			DataCleanupTestUtil.getResourceActionsSavepointSafeCloseable();
	}

	@After
	public void tearDown() throws Exception {
		_classNamesSavepointSafeCloseable.close();

		DataAccess.cleanUp(_connection);

		_resourceActionsSavepointSafeCloseable.close();
	}

	@Test
	public void testUpdateCompanyIdByGroupId() throws Exception {
		long companyId = RandomTestUtil.nextLong();

		runSQL(
			StringBundler.concat(
				"insert into Company (companyId, webId) values (", companyId,
				", '", companyId, "')"));

		long groupId = RandomTestUtil.nextLong();

		runSQL(
			StringBundler.concat(
				"insert into Group_ (groupId, companyId, name) values (",
				groupId, ", ", companyId, ", '", groupId, "')"));

		String tableName = "test_cleanup_" + RandomTestUtil.randomString();

		runSQL(
			StringBundler.concat(
				"create table ", tableName,
				" (companyId LONG, groupId LONG, id_ LONG not null primary ",
				"key)"));

		runSQL(
			StringBundler.concat(
				"insert into ", tableName,
				" (companyId, groupId, id_) values (0, ", groupId, ", 1)"));
		runSQL(
			StringBundler.concat(
				"insert into ", tableName,
				" (companyId, groupId, id_) values (null, ", groupId, ", 2)"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CompanyDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", _dbInspector.normalizeName(tableName),
						", 2 rows updated column ",
						_dbInspector.normalizeName("companyId"),
						" because missing values were populated from table ",
						_dbInspector.normalizeName("Group_"))));

			try (PreparedStatement preparedStatement =
					_connection.prepareStatement(
						"select companyId from " + tableName +
							" where id_ in (1, 2) order by id_");

				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					Assert.assertEquals(
						companyId, resultSet.getLong("companyId"));
				}
			}
		}
		finally {
			dropTable(_dbInspector.normalizeName(tableName));

			runSQL("delete from Company where companyId = " + companyId);
			runSQL("delete from Group_ where groupId = " + groupId);
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		Set<String> tableNames = SetUtil.fromList(
			_dbInspector.getTableNames(null));

		String webId = RandomTestUtil.randomString() + "test.com";

		Company company = _companyLocalService.addCompany(
			null, webId, webId, "test.com", 0, true, true, null, null, null,
			null, null, null);

		long companyId = company.getCompanyId();

		List<String> objectTableNames = Arrays.asList(
			"l_" + companyId + "_test", "o_" + companyId + "_test",
			"test_x_" + companyId);

		for (String objectTableName : objectTableNames) {
			runSQL(
				"create table " + objectTableName +
					" (id_ LONG not null primary key)");
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			Bundle bundle = FrameworkUtil.getBundle(
				CompanyDataCleanupPreupgradeProcessTest.class);

			BundleContext bundleContext = bundle.getBundleContext();

			PortalInstanceLifecycleManager portalInstanceLifecycleManager =
				bundleContext.getService(
					bundleContext.getServiceReference(
						PortalInstanceLifecycleManager.class));

			portalInstanceLifecycleManager.unregisterCompany(company);

			runSQL("delete from Company where companyId = " + companyId);
		}
		finally {
			PortalInstancePool.remove(companyId);
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CompanyDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			List<String> messages = logCapture.getMessages();

			for (String objectTableName : objectTableNames) {
				Assert.assertFalse(_dbInspector.hasTable(objectTableName));
				Assert.assertTrue(
					messages.contains(
						StringBundler.concat(
							"Table ",
							_dbInspector.normalizeName(objectTableName),
							" was dropped because it belonged to a ",
							"nonexistent company: ", companyId)));
			}

			Assert.assertEquals(
				tableNames, SetUtil.fromList(_dbInspector.getTableNames(null)));
		}
		finally {
			for (String objectTableName : objectTableNames) {
				dropTable(_dbInspector.normalizeName(objectTableName));
			}

			runSQL("delete from SystemEvent where companyId = " + companyId);
		}
	}

	private SafeCloseable _classNamesSavepointSafeCloseable;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Connection _connection;
	private DBInspector _dbInspector;

	@Inject
	private MultiVMPool _multiVMPool;

	private SafeCloseable _resourceActionsSavepointSafeCloseable;

}
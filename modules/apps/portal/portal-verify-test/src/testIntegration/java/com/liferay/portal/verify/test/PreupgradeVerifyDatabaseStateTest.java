/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.verify.PreupgradeVerifyDatabaseState;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Set;
import java.util.TreeSet;

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
public class PreupgradeVerifyDatabaseStateTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			_currentSchemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(connection);

			PortalUpgradeProcess.updateSchemaVersion(
				connection, _TEST_SCHEMA_VERSION);
		}

		if (DBPartition.isPartitionEnabled()) {
			_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				PortalInstancePool.getDefaultCompanyId());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			PortalUpgradeProcess.updateSchemaVersion(
				connection, _currentSchemaVersion);
		}
		finally {
			if (_safeCloseable != null) {
				_safeCloseable.close();
			}
		}
	}

	@Test
	public void testVerifyPreupgradeMissingTable() throws SQLException {
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

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Missing tables detected: [" + tableName + "]",
				exception.getMessage());
		}
		finally {
			_serviceComponentLocalService.deleteServiceComponent(
				serviceComponent);
		}
	}

	@Test
	public void testVerifyPreupgradeMissingView() throws Exception {
		Assume.assumeTrue(DBPartition.isPartitionEnabled());

		_renameView("Release_", "Release_backup");

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			DBInspector dbInspector = new DBInspector(
				DataAccess.getConnection());

			String viewName = dbInspector.normalizeName("Release_");

			Assert.assertEquals(
				StringBundler.concat(
					"Missing views detected: [", viewName, "] in company ",
					TestPropsValues.getCompanyId()),
				exception.getMessage());
		}
		finally {
			_renameView("Release_backup", "Release_");
		}
	}

	@Test
	public void testVerifyPreupgradePartiallyUpgradedTable()
		throws SQLException {

		ServiceComponent serviceComponent = _getServiceComponent();

		String originalData = serviceComponent.getData();

		try {
			serviceComponent.setData(RandomTestUtil.randomString());

			serviceComponent =
				_serviceComponentLocalService.updateServiceComponent(
					serviceComponent);

			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			DBInspector dbInspector = new DBInspector(
				DataAccess.getConnection());

			Set<String> tableNames = DBResourceUtil.parseCreateTableSQL(
				dbInspector, originalData);

			Assert.assertEquals(
				"Stale tables from a previous upgrade detected: " +
					new TreeSet<>(tableNames),
				exception.getMessage());
		}
		finally {
			serviceComponent.setData(originalData);

			_serviceComponentLocalService.updateServiceComponent(
				serviceComponent);
		}
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PreupgradeVerifyDatabaseState();
	}

	private ServiceComponent _getServiceComponent() {
		for (ServiceComponent serviceComponent :
				_serviceComponentLocalService.getLatestServiceComponents()) {

			String buildNamespace = serviceComponent.getBuildNamespace();

			if (buildNamespace.startsWith("com.liferay")) {
				return serviceComponent;
			}
		}

		return null;
	}

	private void _renameView(String fromViewName, String toViewName)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			DB db = DBManagerUtil.getDB();

			if (db.getDBType() == DBType.MYSQL) {
				db.runSQL(
					StringBundler.concat(
						"rename table ", fromViewName, " to ", toViewName));
			}
			else {
				db.runSQL(
					StringBundler.concat(
						"alter view ", fromViewName, " rename to ",
						toViewName));
			}
		}
	}

	private static final Version _TEST_SCHEMA_VERSION = new Version(0, 0, 0);

	private static Version _currentSchemaVersion;
	private static SafeCloseable _safeCloseable;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}
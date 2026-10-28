/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.upgrade.util.UpgradePartitionedControlTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sofía Mendoza Gutiérrez
 */
@RunWith(Arquillian.class)
public class UpgradePartitionedControlTableTest
	extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		BaseDBPartitionTestCase.setUpDBPartitions();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		BaseDBPartitionTestCase.tearDownDBPartitions();
	}

	@Test
	public void testUpgrade() throws Exception {
		try {
			createAndPopulateControlTable(TEST_TABLE_NAME);

			_createViewSQL(TEST_TABLE_NAME);

			UpgradeProcess upgradeProcess = new UpgradePartitionedControlTable(
				TEST_TABLE_NAME);

			upgradeProcess.upgrade();

			DataSource dataSource = InfrastructureUtil.getDataSource();

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					Assert.assertTrue(dbInspector.hasTable(TEST_TABLE_NAME));

					try (Connection connection = dataSource.getConnection()) {
						try (PreparedStatement preparedStatement =
								connection.prepareStatement(
									"select count(1) as count from " +
										TEST_TABLE_NAME);

							ResultSet resultSet =
								preparedStatement.executeQuery()) {

							long count = 0;

							if (resultSet.next()) {
								count = resultSet.getLong("count");
							}

							Assert.assertEquals(1, count);
						}

						try (PreparedStatement preparedStatement =
								connection.prepareStatement(
									StringBundler.concat(
										"select testColumn from ",
										TEST_TABLE_NAME,
										" where testColumn = ?"))) {

							preparedStatement.setLong(1, 1L);

							try (ResultSet resultSet =
									preparedStatement.executeQuery()) {

								Assert.assertTrue(resultSet.next());
							}
						}
					}
				});
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> dropTable(TEST_TABLE_NAME));
		}
	}

	private void _createViewSQL(String viewName) throws Exception {
		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					defaultCompanyId)) {

			for (long companyId : PortalInstancePool.getCompanyIds()) {
				if (companyId == defaultCompanyId) {
					continue;
				}

				db.runSQL(
					dbPartitionDB.getCreateViewSQL(
						defaultPartitionName, getPartitionName(companyId),
						viewName));
			}
		}
	}

}
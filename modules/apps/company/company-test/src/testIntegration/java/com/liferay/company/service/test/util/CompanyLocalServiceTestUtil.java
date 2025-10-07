/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.service.test.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.db.partition.db.DBPartitionDB;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.cm.PersistenceManager;

import org.junit.Assert;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Luis Ortiz
 */
public class CompanyLocalServiceTestUtil {

	public static void assertConfiguration(
			ConfigurationAdmin configurationAdmin,
			PersistenceManager persistenceManager, String pid, boolean exists)
		throws Exception {

		if (exists) {
			Assert.assertNotNull(
				configurationAdmin.listConfigurations(
					"(service.pid=" + pid + ")"));

			Assert.assertTrue(persistenceManager.exists(pid));

			return;
		}

		Assert.assertNull(
			configurationAdmin.listConfigurations("(service.pid=" + pid + ")"));

		Assert.assertFalse(persistenceManager.exists(pid));
	}

	public static void checkStandaloneDBPartitionTables(
			Connection connection, DBPartitionDB dbPartitionDB,
			String partitionName, String... expectedTableNames)
		throws Exception {

		List<String> tableNames = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getTables(
				dbPartitionDB.getCatalog(connection, partitionName),
				dbPartitionDB.getSchema(connection, partitionName), null,
				new String[] {"TABLE"})) {

			while (resultSet.next()) {
				tableNames.add(
					StringUtil.toUpperCase(resultSet.getString("TABLE_NAME")));
			}
		}

		for (String expectedTableName : expectedTableNames) {
			Assert.assertTrue(
				tableNames.contains(StringUtil.toUpperCase(expectedTableName)));
		}
	}

	public static Configuration createFactoryConfiguration(
			ConfigurationAdmin configurationAdmin, long companyId)
		throws Exception {

		String pid = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			pid = ConfigurationTestUtil.createFactoryConfiguration(
				CompanyLocalServiceTestUtil.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", companyId
				).put(
					"test", RandomTestUtil.randomString()
				).build());
		}

		return configurationAdmin.getConfiguration(pid);
	}

	public static long[] getCompanyIdsBySQL() {
		return ReflectionTestUtil.invoke(
			PortalInstancePool.class, "_getCompanyIdsBySQL", null, null);
	}

	public static String getExportedPartitionName(long companyId) {
		return ReflectionTestUtil.invoke(
			DBPartitionUtil.class, "_getExportedPartitionName",
			new Class<?>[] {long.class}, companyId);
	}

	public static String getPartitionName(long companyId) {
		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			return ReflectionTestUtil.getFieldValue(
				DBPartitionUtil.class, "_defaultPartitionName");
		}

		return PropsValues.DATABASE_PARTITION_SCHEMA_NAME_PREFIX + companyId;
	}

}
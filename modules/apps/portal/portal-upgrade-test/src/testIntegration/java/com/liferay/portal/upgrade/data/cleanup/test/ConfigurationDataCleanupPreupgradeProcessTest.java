/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.ConfigurationDataCleanupPreupgradeProcess;

import java.sql.Connection;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class ConfigurationDataCleanupPreupgradeProcessTest
	extends ConfigurationDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		_originalCacheEnabled = ReflectionTestUtil.getAndSetFieldValue(
			PortalInstancePool.class, "_cacheEnabled", false);
	}

	@After
	public void tearDown() throws Exception {
		DataAccess.cleanUp(_connection);

		ReflectionTestUtil.setFieldValue(
			PortalInstancePool.class, "_cacheEnabled", _originalCacheEnabled);
	}

	@Test
	public void testUpgrade() throws Exception {
		connection = DataAccess.getConnection();

		_test(0, _getNonexistentCompanyId(), "companyId", "Company");

		connection = DataAccess.getConnection();

		_test(
			TestPropsValues.getCompanyId(), _getNonexistentCompanyId(),
			"companyId", "Company");

		connection = DataAccess.getConnection();

		_test(
			TestPropsValues.getGroupId(), _getNonexistentGroupId(), "groupId",
			"Group_");
	}

	private long _getNonexistentCompanyId() throws Exception {
		Set<Long> companyIds = SetUtil.fromArray(
			PortalInstancePool.getCompanyIds());

		while (true) {
			long nonexistentCompanyId = RandomTestUtil.randomLong();

			if (!companyIds.contains(nonexistentCompanyId)) {
				return nonexistentCompanyId;
			}
		}
	}

	private long _getNonexistentGroupId() throws Exception {
		Set<Long> groupIds = SetUtil.fromArray(getGroupIds());

		while (true) {
			long nonexistentGroupId = RandomTestUtil.randomLong();

			if (!groupIds.contains(nonexistentGroupId)) {
				return nonexistentGroupId;
			}
		}
	}

	private void _test(
			long existentPrimaryKey, long nonexistentPrimaryKey,
			String primaryKeyColumnName, String tableName)
		throws Exception {

		String existentConfigurationId = null;
		String nonexistentConfigurationId = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ConfigurationDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			existentConfigurationId =
				ConfigurationTestUtil.createFactoryConfiguration(
					ConfigurationDataCleanupPreupgradeProcessTest.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						primaryKeyColumnName, existentPrimaryKey
					).build());
			nonexistentConfigurationId =
				ConfigurationTestUtil.createFactoryConfiguration(
					ConfigurationDataCleanupPreupgradeProcessTest.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						primaryKeyColumnName, nonexistentPrimaryKey
					).build());

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertFalse(
				messages.contains(
					StringBundler.concat(
						"Table Configuration_, 1 row deleted because ",
						existentConfigurationId, " has scope ",
						primaryKeyColumnName, StringPool.SPACE,
						existentPrimaryKey, " that was not found in ",
						_dbInspector.normalizeName(tableName), ".",
						_dbInspector.normalizeName(primaryKeyColumnName))));

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table Configuration_, 1 row deleted because ",
						nonexistentConfigurationId, " has scope ",
						primaryKeyColumnName, StringPool.SPACE,
						nonexistentPrimaryKey, " that was not found in ",
						_dbInspector.normalizeName(tableName), ".",
						_dbInspector.normalizeName(primaryKeyColumnName))));
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(existentConfigurationId);
			ConfigurationTestUtil.deleteConfiguration(
				nonexistentConfigurationId);
		}
	}

	private Connection _connection;
	private DBInspector _dbInspector;
	private boolean _originalCacheEnabled;

}
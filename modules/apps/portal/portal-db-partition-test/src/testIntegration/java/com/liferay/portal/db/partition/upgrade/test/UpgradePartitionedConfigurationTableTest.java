/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;

/**
 * @author Luis Ortiz
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class UpgradePartitionedConfigurationTableTest
	extends BaseDBPartitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			TransactionalTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		_companyId = PortalInstancePool.getDefaultCompanyId();

		_dataSource = InfrastructureUtil.getDataSource();
	}

	@Test
	public void testUpgradeProcess() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(_companyId)) {

			for (long companyId : PortalInstancePool.getCompanyIds()) {
				if (companyId == _companyId) {
					continue;
				}

				String partitionName = getPartitionName(companyId);

				db.runSQL(
					dbPartitionDB.getDropTableSQL(
						partitionName, "Configuration_"));
				db.runSQL(
					dbPartitionDB.getCreateViewSQL(
						defaultPartitionName, partitionName, "Configuration_"));
			}
		}

		VirtualHost virtualHost = _virtualHostLocalService.getVirtualHost(
			TestPropsValues.COMPANY_WEB_ID);

		Map<Long, ConfigurationEntry> validConfigurationEntries =
			HashMapBuilder.<Long, ConfigurationEntry>put(
				_companyId,
				new ConfigurationEntry(
					ExtendedObjectClassDefinition.Scope.SYSTEM, null)
			).put(
				0L,
				new ConfigurationEntry(
					ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE,
					RandomTestUtil.randomLong())
			).put(
				virtualHost.getCompanyId(),
				() -> {
					Group group = GroupLocalServiceUtil.getGroup(
						virtualHost.getCompanyId(), GroupConstants.GUEST);

					return new ConfigurationEntry(
						ExtendedObjectClassDefinition.Scope.GROUP,
						group.getGroupId());
				}
			).put(
				virtualHost.getCompanyId(),
				new ConfigurationEntry(
					ExtendedObjectClassDefinition.Scope.COMPANY,
					virtualHost.getCompanyId())
			).build();

		long randomCompanyId = RandomTestUtil.randomLong();
		long randomGroupId = RandomTestUtil.randomLong();

		Map<Long, ConfigurationEntry> invalidConfigurationEntries =
			HashMapBuilder.<Long, ConfigurationEntry>put(
				randomCompanyId,
				new ConfigurationEntry(
					ExtendedObjectClassDefinition.Scope.COMPANY,
					randomCompanyId)
			).put(
				randomGroupId,
				new ConfigurationEntry(
					ExtendedObjectClassDefinition.Scope.GROUP, randomGroupId)
			).build();

		try {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						PortalInstancePool.getDefaultCompanyId());
				PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"insert into Configuration_ (configurationId, " +
							"dictionary) values (?, ?)")) {

				for (ConfigurationEntry configurationEntry :
						validConfigurationEntries.values()) {

					preparedStatement.setString(1, configurationEntry.getPid());
					preparedStatement.setString(
						2, configurationEntry.getDictionary());

					preparedStatement.addBatch();
				}

				for (ConfigurationEntry configurationEntry :
						invalidConfigurationEntries.values()) {

					preparedStatement.setString(1, configurationEntry.getPid());
					preparedStatement.setString(
						2, configurationEntry.getDictionary());

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}

			Bundle bundle = BundleUtil.getBundle(
				SystemBundleUtil.getBundleContext(),
				"com.liferay.portal.configuration.persistence.impl");

			Class<?> upgradeProcessClass = bundle.loadClass(
				_UPGRADE_CLASS_NAME);

			UpgradeProcess upgradeProcess =
				(UpgradeProcess)upgradeProcessClass.newInstance();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_UPGRADE_CLASS_NAME, LoggerTestUtil.INFO)) {

				upgradeProcess.upgrade();

				String logMessage = StringUtil.merge(
					logCapture.getLogEntries(), StringPool.NEW_LINE);

				for (Map.Entry<Long, ConfigurationEntry>
						configurationEntryEntry :
							invalidConfigurationEntries.entrySet()) {

					ConfigurationEntry configurationEntry =
						configurationEntryEntry.getValue();

					ExtendedObjectClassDefinition.Scope scope =
						configurationEntry.getScope();

					String scopeValue = scope.getValue();

					Assert.assertTrue(
						logMessage,
						logMessage.contains(
							StringBundler.concat(
								StringUtil.upperCaseFirstLetter(scopeValue),
								" scope configuration with ID ",
								configurationEntry.getPid(),
								" has been removed because the ", scopeValue,
								" ID ", configurationEntryEntry.getKey(),
								" does not exist")));
				}
			}

			for (Map.Entry<Long, ConfigurationEntry> configurationEntryEntry :
					validConfigurationEntries.entrySet()) {

				ConfigurationEntry configurationEntry =
					configurationEntryEntry.getValue();
				Long companyId = configurationEntryEntry.getKey();

				DBPartitionUtil.forEachCompanyId(
					currentCompanyId -> {
						try (Connection connection =
								_dataSource.getConnection();

							PreparedStatement preparedStatement =
								connection.prepareStatement(
									"select dictionary from Configuration_ " +
										"where configurationId = ?")) {

							preparedStatement.setString(
								1, configurationEntry.getPid());

							try (ResultSet resultSet =
									preparedStatement.executeQuery()) {

								if ((companyId == CompanyConstants.SYSTEM) ||
									Objects.equals(
										currentCompanyId, companyId)) {

									Assert.assertTrue(resultSet.next());
									Assert.assertEquals(
										configurationEntry.getDictionary(),
										resultSet.getString("dictionary"));
								}
								else {
									Assert.assertFalse(resultSet.next());
								}
							}
						}
					});
			}
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				currentCompanyId -> {
					try (Connection connection = DataAccess.getConnection();

						PreparedStatement preparedStatement =
							connection.prepareStatement(
								StringBundler.concat(
									"delete from Configuration_ where ",
									"configurationId like '%",
									UpgradePartitionedConfigurationTableTest.
										class.getName(),
									"%'"))) {

						preparedStatement.executeUpdate();
					}
				});
		}
	}

	private String _convertDictionaryValue(Object value) {
		if (value instanceof Long) {
			return StringBundler.concat("L\"", value, StringPool.QUOTE);
		}

		return StringBundler.concat(StringPool.QUOTE, value, StringPool.QUOTE);
	}

	private static final String _UPGRADE_CLASS_NAME =
		"com.liferay.portal.configuration.persistence.internal.upgrade." +
			"v2_0_0.ConfigurationDBPartitionUpgradeProcess";

	private static long _companyId;
	private static DataSource _dataSource;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

	private class ConfigurationEntry {

		public ConfigurationEntry(
			ExtendedObjectClassDefinition.Scope scope, Object scopeValue) {

			_scope = scope;

			String dictionary = StringPool.BLANK;

			if (scopeValue != null) {
				dictionary = StringBundler.concat(
					scope.getPropertyKey(), StringPool.EQUAL,
					_convertDictionaryValue(scopeValue));
			}

			dictionary = StringBundler.concat(
				dictionary, StringPool.NEW_LINE, RandomTestUtil.randomString(),
				StringPool.EQUAL, StringPool.QUOTE,
				RandomTestUtil.randomString(), StringPool.QUOTE);

			_dictionary = dictionary;

			_pid =
				UpgradePartitionedConfigurationTableTest.class.getName() + "~" +
					RandomTestUtil.randomString();
		}

		public String getDictionary() {
			return _dictionary;
		}

		public String getPid() {
			return _pid;
		}

		public ExtendedObjectClassDefinition.Scope getScope() {
			return _scope;
		}

		private final String _dictionary;
		private final String _pid;
		private final ExtendedObjectClassDefinition.Scope _scope;

	}

}
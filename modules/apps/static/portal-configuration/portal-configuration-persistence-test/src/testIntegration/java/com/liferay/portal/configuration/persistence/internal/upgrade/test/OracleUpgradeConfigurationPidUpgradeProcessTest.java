/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Objects;

import org.apache.felix.cm.file.ConfigurationHandler;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio Alonso
 */
@RunWith(Arquillian.class)
public class OracleUpgradeConfigurationPidUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(DBManagerUtil.getDBType() == DBType.ORACLE);
	}

	@BeforeClass
	public static void setUpClass() {
		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						if (Objects.equals(
								clazz.getName(),
								"com.liferay.portal.configuration." +
									"persistence.internal.upgrade.v1_0_0." +
										"ConfigurationUpgradeProcess")) {

							_upgradeConfigurationPidUpgradeProcess =
								(UpgradeProcess)upgradeStep;
						}
					}
				}

			});
	}

	@Test
	public void testUpgradeConfigurationWithLongServicePid() throws Exception {
		try {
			_testUpgradeConfigurationWithLongServicePid();
		}
		catch (SQLException sqlException1) {
			try {
				_testUpgradeConfigurationWithLongServicePid();
			}
			catch (SQLException sqlException2) {
				Assert.assertTrue(
					"SQLException is not ORA-00001 caused by ORA-12899",
					(sqlException1.getErrorCode() == 12899) &&
					(sqlException2.getErrorCode() == 1));
			}
		}
		finally {
			_removeConfiguration(_SERVICE_FACTORY_PID);
		}
	}

	private void _removeConfiguration(String serviceFactoryPid)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"delete from Configuration_ where configurationId like '" +
				serviceFactoryPid + "%'");
	}

	private void _testUpgradeConfigurationWithLongServicePid()
		throws Exception {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(
			unsyncByteArrayOutputStream,
			HashMapDictionaryBuilder.put(
				FileInstallConstants.FELIX_FILE_INSTALL_FILENAME,
				_SERVICE_FACTORY_PID + ".default.config"
			).put(
				"service.factoryPid", _SERVICE_FACTORY_PID
			).put(
				"service.pid", _SERVICE_FACTORY_PID
			).build());

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"insert into Configuration_ (configurationId, " +
						"dictionary) values(?, ?)")) {

			preparedStatement.setString(1, _SERVICE_FACTORY_PID + ".instance1");
			preparedStatement.setString(
				2, unsyncByteArrayOutputStream.toString());

			preparedStatement.addBatch();

			String longServicePid = new String(new char[512]);

			longServicePid = StringUtil.replace(longServicePid, '\0', 'P');

			preparedStatement.setString(1, longServicePid + ".instance1");

			preparedStatement.setString(
				2, unsyncByteArrayOutputStream.toString());

			preparedStatement.addBatch();

			preparedStatement.executeBatch();
		}
	}

	private static final String _SERVICE_FACTORY_PID = "test.configuration";

	private static UpgradeProcess _upgradeConfigurationPidUpgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.portal.configuration.persistence.internal.upgrade.registry.ConfigurationPersistenceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

}
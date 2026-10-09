/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade.v2_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.felix.cm.PersistenceManager;
import org.apache.felix.cm.file.ConfigurationHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class ConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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

						if (!Objects.equals(clazz.getName(), _CLASS_NAME)) {
							continue;
						}

						_configurationUpgradeProcess =
							(UpgradeProcess)upgradeStep;
					}
				}

			});
	}

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@After
	public void tearDown() throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _deleteConfiguration());
		}
		else {
			_companyLocalService.forEachCompany(
				company -> _deleteConfiguration());
		}
	}

	@Test
	public void testDoUpgrade() throws Exception {
		_testDoUpgrade();
		_testDoUpgradeWhenDatabasePartitionIsEnabled();
	}

	private void _assertConfiguration() throws Exception {
		long companyId = CompanyThreadLocal.getCompanyId();

		long count = 0;

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select dictionary from Configuration_ where ",
					"configurationId like '", _CONFIGURATION_ID, "%' and ",
					"dictionary like '%companyId=%", companyId, "%'"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				count += 1;

				Dictionary<String, Object> dictionary = _toDictionary(
					resultSet.getString("dictionary"));

				Assert.assertEquals(
					companyId,
					dictionary.get(
						ExtendedObjectClassDefinition.Scope.COMPANY.
							getPropertyKey()));
			}
		}

		Assert.assertEquals(1, count);

		String configurationId = _configurationIds.get(companyId);

		Dictionary<?, ?> dictionary = _persistenceManager.load(configurationId);

		Assert.assertEquals(
			companyId,
			dictionary.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()));
	}

	private void _createConfiguration() throws Exception {
		String factoryPid = _CONFIGURATION_ID + ".scoped";

		String configurationId =
			factoryPid + "~" + RandomTestUtil.randomString();

		long companyId = CompanyThreadLocal.getCompanyId();

		Group group = _groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.configuration.admin.web.internal.configuration." +
					"persistence.listener." +
						"ConfigurationImportGlobalConfigurationModelListener",
				LoggerTestUtil.ERROR)) {

			_persistenceManager.store(
				configurationId,
				HashMapDictionaryBuilder.<String, Object>put(
					"groupId", group.getGroupId()
				).put(
					"key", "value"
				).put(
					"service.factoryPid", factoryPid
				).put(
					"service.pid", configurationId
				).build());
		}

		_configurationIds.put(companyId, configurationId);
	}

	private void _deleteConfiguration() throws IOException, SQLException {
		String configurationId = _configurationIds.remove(
			CompanyThreadLocal.getCompanyId());

		if (configurationId != null) {
			_persistenceManager.delete(configurationId);
		}

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"delete from Configuration_ where configurationId like '" +
				_CONFIGURATION_ID + "%'");
	}

	private void _testDoUpgrade() throws Exception {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		_companyLocalService.forEachCompany(company -> _createConfiguration());

		_configurationUpgradeProcess.upgrade();

		_companyLocalService.forEachCompany(company -> _assertConfiguration());
	}

	private void _testDoUpgradeWhenDatabasePartitionIsEnabled()
		throws Exception {

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		DBPartitionUtil.forEachCompanyId(companyId -> _createConfiguration());

		_configurationUpgradeProcess.upgrade();

		DBPartitionUtil.forEachCompanyId(companyId -> _assertConfiguration());
	}

	private Dictionary<String, Object> _toDictionary(String dictionaryString)
		throws IOException {

		UnsyncByteArrayInputStream unsyncByteArrayInputStream =
			new UnsyncByteArrayInputStream(
				dictionaryString.getBytes(StandardCharsets.UTF_8));

		return ConfigurationHandler.read(unsyncByteArrayInputStream);
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.configuration.persistence.internal.upgrade." +
			"v2_0_1.ConfigurationUpgradeProcess";

	private static final String _CONFIGURATION_ID =
		RandomTestUtil.randomString();

	private static UpgradeProcess _configurationUpgradeProcess;

	@Inject(
		filter = "(&(component.name=com.liferay.portal.configuration.persistence.internal.upgrade.registry.ConfigurationPersistenceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private final Map<Long, String> _configurationIds = new HashMap<>();

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PersistenceManager _persistenceManager;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class AuditConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, "AuditConfigurationUpgradeProcess");
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			AuditConfiguration.class.getName());
		ConfigurationTestUtil.deleteConfiguration(
			PersistentAuditMessageProcessorConfiguration.class.getName());

		_deleteCompanyConfigurations();
	}

	@Test
	public void testUpgrade() throws Exception {
		_testUpgrade(false);
		_testUpgrade(true);
	}

	@Test
	public void testUpgradeWhenCompanyAuditIsAlreadyConfigured()
		throws Exception {

		_saveSystemConfiguration(
			AuditConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", false
			).build());

		_configurationProvider.saveCompanyConfiguration(
			AuditConfiguration.class, TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).build());

		_upgradeProcess.upgrade();

		Configuration configuration = _fetchCompanyConfiguration(
			AuditConfiguration.class, TestPropsValues.getCompanyId());

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertTrue(GetterUtil.getBoolean(properties.get("enabled")));
	}

	@Test
	public void testUpgradeWhenPersistentAuditMessageProcessorBufferSizeIsCustomized()
		throws Exception {

		_saveSystemConfiguration(
			PersistentAuditMessageProcessorConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"bufferSize", _BUFFER_SIZE
			).build());

		_upgradeProcess.upgrade();

		Configuration configuration = _fetchCompanyConfiguration(
			PersistentAuditMessageProcessorConfiguration.class,
			TestPropsValues.getCompanyId());

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertEquals(
			_BUFFER_SIZE, GetterUtil.getInteger(properties.get("bufferSize")));
		Assert.assertNull(properties.get("enabled"));
		Assert.assertNull(properties.get("flushInterval"));
	}

	@Test
	public void testUpgradeWhenPersistentAuditMessageProcessorValuesAreDefault()
		throws Exception {

		_saveSystemConfiguration(
			PersistentAuditMessageProcessorConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"bufferSize", 2000
			).put(
				"enabled", true
			).put(
				"flushInterval", 60000
			).build());

		_upgradeProcess.upgrade();

		Assert.assertNull(
			_fetchCompanyConfiguration(
				PersistentAuditMessageProcessorConfiguration.class,
				TestPropsValues.getCompanyId()));
	}

	private void _deleteCompanyConfigurations() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				_configurationProvider.deleteCompanyConfiguration(
					AuditConfiguration.class, companyId);
				_configurationProvider.deleteCompanyConfiguration(
					PersistentAuditMessageProcessorConfiguration.class,
					companyId);
			});
	}

	private Configuration _fetchCompanyConfiguration(
			Class<?> clazz, long companyId)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(&(%s=%s.scoped)(%s=%d))",
				ConfigurationAdmin.SERVICE_FACTORYPID, clazz.getName(),
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				companyId));

		if (ArrayUtil.isEmpty(configurations)) {
			return null;
		}

		return configurations[0];
	}

	private void _saveSystemConfiguration(
			Class<?> clazz, Dictionary<String, Object> properties)
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(clazz.getName(), properties);
	}

	private void _testUpgrade(boolean enabled) throws Exception {
		_saveSystemConfiguration(
			AuditConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", enabled
			).build());

		_upgradeProcess.upgrade();

		Configuration configuration = _fetchCompanyConfiguration(
			AuditConfiguration.class, TestPropsValues.getCompanyId());

		if (enabled) {
			Assert.assertNull(configuration);
		}
		else {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			Assert.assertFalse(
				GetterUtil.getBoolean(properties.get("enabled")));
		}

		_deleteCompanyConfigurations();
	}

	private static final int _BUFFER_SIZE = 500;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.router.internal.upgrade.registry.AuditRouterUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
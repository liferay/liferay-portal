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

		_saveSystemConfiguration(false);

		_configurationProvider.saveCompanyConfiguration(
			AuditConfiguration.class, TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).build());

		_upgradeProcess.upgrade();

		AuditConfiguration auditConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId());

		Assert.assertTrue(auditConfiguration.enabled());
	}

	private void _deleteCompanyConfigurations() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> _configurationProvider.deleteCompanyConfiguration(
				AuditConfiguration.class, companyId));
	}

	private Configuration _fetchCompanyConfiguration(long companyId)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(&(%s=%s.scoped)(%s=%d))",
				ConfigurationAdmin.SERVICE_FACTORYPID,
				AuditConfiguration.class.getName(),
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				companyId));

		if (ArrayUtil.isEmpty(configurations)) {
			return null;
		}

		return configurations[0];
	}

	private void _saveSystemConfiguration(boolean enabled) throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			AuditConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", enabled
			).build());
	}

	private void _testUpgrade(boolean enabled) throws Exception {
		_saveSystemConfiguration(enabled);

		_upgradeProcess.upgrade();

		Configuration configuration = _fetchCompanyConfiguration(
			TestPropsValues.getCompanyId());

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
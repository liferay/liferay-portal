/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.upgrade.v2_1_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.constants.LegacySitemapIndexPropsKeys;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class XMLSitemapIndexEnabledConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyLocalServiceUtil.getCompany(
			TestPropsValues.getCompanyId());
	}

	@Before
	public void setUp() throws Exception {
		_xmlSitemapIndexEnabled = PropsUtil.get(
			LegacySitemapIndexPropsKeys.XML_SITEMAP_INDEX_ENABLED);
	}

	@After
	public void tearDown() throws Exception {
		PropsUtil.set(
			LegacySitemapIndexPropsKeys.XML_SITEMAP_INDEX_ENABLED,
			_xmlSitemapIndexEnabled);

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format("(%s=%s*)", Constants.SERVICE_PID, _PID));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			configuration.delete();
		}
	}

	@Test
	public void testUpgradeWithXMLSitemapIndexDisabled() throws Exception {
		PropsUtil.set(
			LegacySitemapIndexPropsKeys.XML_SITEMAP_INDEX_ENABLED,
			Boolean.FALSE.toString());

		_runUpgrade();

		_assertConfiguration(new Long[0], true, true, true, false);
	}

	@Test
	public void testUpgradeWithXMLSitemapIndexDisabledAndNondefaultConfigurations()
		throws Exception {

		PropsUtil.set(
			LegacySitemapIndexPropsKeys.XML_SITEMAP_INDEX_ENABLED,
			Boolean.FALSE.toString());

		long groupId = RandomTestUtil.randomLong();

		_configurationProvider.saveCompanyConfiguration(
			_company.getCompanyId(), _PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companySitemapGroupIds", new String[] {String.valueOf(groupId)}
			).put(
				"includeCategories", false
			).put(
				"includePages", false
			).put(
				"includeWebContent", false
			).build());

		_runUpgrade();

		_assertConfiguration(new Long[] {groupId}, false, false, false, false);
	}

	@Test
	public void testUpgradeWithXMLSitemapIndexEnabled() throws Exception {
		PropsUtil.set(
			LegacySitemapIndexPropsKeys.XML_SITEMAP_INDEX_ENABLED,
			Boolean.TRUE.toString());

		_runUpgrade();

		_assertConfiguration(new Long[0], true, true, true, true);
	}

	private void _assertConfiguration(
			Long[] expectedSitemapGroupIds,
			boolean expectedIncludeCategoriesCompanyEnabled,
			boolean expectedIncludePagesCompanyEnabled,
			boolean expectedIncludeWebContentCompanyEnabled,
			boolean expectedXMLSitemapIndexEnabled)
		throws Exception {

		Long[] sitemapGroupIds =
			_sitemapConfigurationManager.getCompanySitemapGroupIds(
				_company.getCompanyId());

		Assert.assertEquals(
			Arrays.toString(sitemapGroupIds), expectedSitemapGroupIds.length,
			sitemapGroupIds.length);

		if (expectedSitemapGroupIds.length > 0) {
			Assert.assertEquals(expectedSitemapGroupIds[0], sitemapGroupIds[0]);
		}

		Assert.assertEquals(
			expectedIncludeCategoriesCompanyEnabled,
			_sitemapConfigurationManager.includeCategoriesCompanyEnabled(
				_company.getCompanyId()));
		Assert.assertEquals(
			expectedIncludePagesCompanyEnabled,
			_sitemapConfigurationManager.includePagesCompanyEnabled(
				_company.getCompanyId()));
		Assert.assertEquals(
			expectedIncludeWebContentCompanyEnabled,
			_sitemapConfigurationManager.includeWebContentCompanyEnabled(
				_company.getCompanyId()));
		Assert.assertEquals(
			expectedXMLSitemapIndexEnabled,
			_sitemapConfigurationManager.xmlSitemapIndexCompanyEnabled(
				_company.getCompanyId()));
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.internal.upgrade.v2_1_2." +
			"XMLSitemapIndexEnabledConfigurationUpgradeProcess";

	private static final String _PID =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static Company _company;

	@Inject
	private static SitemapConfigurationManager _sitemapConfigurationManager;

	@Inject(
		filter = "(&(component.name=com.liferay.site.internal.upgrade.registry.SiteServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private static String _xmlSitemapIndexEnabled;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject
	private MultiVMPool _multiVMPool;

}
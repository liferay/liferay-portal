/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.settings.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Dictionary;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class MailSettingCompanyConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_storedMailSettingCompanyConfiguration =
			_configurationAdmin.getConfiguration(
				"com.liferay.mail.settings.configuration." +
					"MailSettingCompanyConfiguration",
				StringPool.QUESTION);

		_storedProperties =
			_storedMailSettingCompanyConfiguration.getProperties();

		_configurationProvider.deleteSystemConfiguration(
			MailSettingCompanyConfiguration.class);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_configurationProvider.saveSystemConfiguration(
			MailSettingCompanyConfiguration.class, _storedProperties);
	}

	@Before
	public void setUp() throws Exception {
		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.mail.settings.internal.upgrade.v1_0_0." +
				"MailSettingCompanyConfigurationUpgradeProcess");

		_testCompanyIdPortletPreferences = _prefsProps.getPreferences(
			TestPropsValues.getCompanyId());

		_testSystemIdPortletPreferences = _prefsProps.getPreferences();
	}

	@After
	public void tearDown() throws Exception {
		_configurationProvider.deleteCompanyConfiguration(
			MailSettingCompanyConfiguration.class,
			TestPropsValues.getCompanyId());
		_configurationProvider.deleteSystemConfiguration(
			MailSettingCompanyConfiguration.class);
	}

	@Test
	public void testUpgradeMailSettingCompanyConfigurationWithCompanyId()
		throws Exception {

		String stringValue = RandomTestUtil.randomString();
		String intValue = String.valueOf(RandomTestUtil.randomInt());

		_populatePreferences(
			_testCompanyIdPortletPreferences, stringValue, intValue);

		ConfigurationTestUtil.updateFactoryConfiguration(
			MailSettingCompanyConfiguration.class.getName() + ".scoped~",
			_upgradeProcess::upgrade);

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(&(", ConfigurationAdmin.SERVICE_FACTORYPID, StringPool.EQUAL,
				MailSettingCompanyConfiguration.class.getName(),
				".scoped)(companyId=", TestPropsValues.getCompanyId(), "))"));

		_assertConfiguration(stringValue, intValue, configurations[0]);
	}

	@Test
	public void testUpgradeMailSettingCompanyConfigurationWithSystemId()
		throws Exception {

		String stringValue = RandomTestUtil.randomString();
		String intValue = String.valueOf(RandomTestUtil.randomInt());

		_populatePreferences(
			_testSystemIdPortletPreferences, stringValue, intValue);

		Configuration configuration = ConfigurationTestUtil.updateConfiguration(
			MailSettingCompanyConfiguration.class.getName(),
			_upgradeProcess::upgrade);

		_assertConfiguration(stringValue, intValue, configuration);
	}

	private void _assertConfiguration(
		String stringValue, String intValue, Configuration configuration) {

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertEquals(
			stringValue,
			GetterUtil.getString(
				properties.get("additionalJavaMailProperties")));
		Assert.assertTrue(
			GetterUtil.getBoolean(
				properties.get("enablePOPServerNotifications")));
		Assert.assertFalse(
			GetterUtil.getBoolean(properties.get("enableStartTLS")));
		Assert.assertEquals(
			intValue, GetterUtil.getString(properties.get("incomingPOPPort")));
		Assert.assertEquals(
			stringValue,
			GetterUtil.getString(properties.get("incomingPOPServer")));
		Assert.assertEquals(
			intValue, GetterUtil.getString(properties.get("outgoingSMTPPort")));
		Assert.assertEquals(
			stringValue,
			GetterUtil.getString(properties.get("outgoingSMTPServer")));
		Assert.assertEquals(
			stringValue, GetterUtil.getString(properties.get("popPassword")));
		Assert.assertEquals(
			stringValue, GetterUtil.getString(properties.get("popUserName")));

		Assert.assertEquals(
			stringValue, GetterUtil.getString(properties.get("smtpPassword")));

		Assert.assertEquals(
			stringValue, GetterUtil.getString(properties.get("smtpUserName")));

		Assert.assertEquals(
			stringValue, GetterUtil.getString(properties.get("storeProtocol")));
		Assert.assertEquals(
			stringValue,
			GetterUtil.getString(properties.get("transportProtocol")));
	}

	private void _populatePreferences(
			PortletPreferences portletPreferences, String stringValue,
			String intValue)
		throws Exception {

		portletPreferences.setValue(
			"mail.session.mail.advanced.properties", stringValue);
		portletPreferences.setValue("mail.session.mail.pop3.host", stringValue);
		portletPreferences.setValue(
			"mail.session.mail.pop3.password", stringValue);
		portletPreferences.setValue("mail.session.mail.pop3.port", intValue);
		portletPreferences.setValue("mail.session.mail.pop3.user", stringValue);
		portletPreferences.setValue("mail.session.mail.smtp.host", stringValue);
		portletPreferences.setValue(
			"mail.session.mail.smtp.password", stringValue);
		portletPreferences.setValue("mail.session.mail.smtp.port", intValue);
		portletPreferences.setValue(
			"mail.session.mail.smtp.starttls.enable", "false");
		portletPreferences.setValue("mail.session.mail.smtp.user", stringValue);
		portletPreferences.setValue(
			"mail.session.mail.store.protocol", stringValue);
		portletPreferences.setValue(
			"mail.session.mail.transport.protocol", stringValue);
		portletPreferences.setValue("pop.server.notifications.enabled", "true");

		portletPreferences.store();
	}

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private static ConfigurationProvider _configurationProvider;

	private static Configuration _storedMailSettingCompanyConfiguration;
	private static Dictionary<String, Object> _storedProperties;

	@Inject
	private PrefsProps _prefsProps;

	private PortletPreferences _testCompanyIdPortletPreferences;
	private PortletPreferences _testSystemIdPortletPreferences;
	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "(&(component.name=com.liferay.mail.settings.internal.upgrade.registry.MailSettingUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
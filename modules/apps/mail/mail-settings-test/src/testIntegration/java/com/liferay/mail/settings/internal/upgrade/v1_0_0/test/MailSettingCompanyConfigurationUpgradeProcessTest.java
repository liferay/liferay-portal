/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.settings.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
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

		_upgradeProcess.upgrade();

		Thread.sleep(2000);

		MailSettingCompanyConfiguration mailSettingCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				MailSettingCompanyConfiguration.class,
				TestPropsValues.getCompanyId());

		_assertConfiguration(
			stringValue, intValue, mailSettingCompanyConfiguration);
	}

	@Test
	public void testUpgradeMailSettingCompanyConfigurationWithSystemId()
		throws Exception {

		String stringValue = RandomTestUtil.randomString();
		String intValue = String.valueOf(RandomTestUtil.randomInt());

		_populatePreferences(
			_testSystemIdPortletPreferences, stringValue, intValue);

		_upgradeProcess.upgrade();

		Thread.sleep(2000);

		MailSettingCompanyConfiguration mailSettingCompanyConfiguration =
			_configurationProvider.getSystemConfiguration(
				MailSettingCompanyConfiguration.class);

		_assertConfiguration(
			stringValue, intValue, mailSettingCompanyConfiguration);
	}

	private void _assertConfiguration(
		String stringValue, String intValue,
		MailSettingCompanyConfiguration mailSettingCompanyConfiguration) {

		Assert.assertTrue(
			mailSettingCompanyConfiguration.enablePOPServerNotifications());
		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.incomingPOPServer());
		Assert.assertEquals(
			intValue, mailSettingCompanyConfiguration.incomingPOPPort());

		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.storeProtocol());
		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.transportProtocol());
		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.popUserName());

		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.popPassword());
		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.outgoingSMTPServer());
		Assert.assertEquals(
			intValue, mailSettingCompanyConfiguration.outgoingSMTPPort());

		Assert.assertFalse(mailSettingCompanyConfiguration.enableStartTLS());
		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.smtpUserName());
		Assert.assertEquals(
			stringValue, mailSettingCompanyConfiguration.smtpPassword());

		Assert.assertEquals(
			stringValue,
			mailSettingCompanyConfiguration.additionalJavaMailProperties());
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
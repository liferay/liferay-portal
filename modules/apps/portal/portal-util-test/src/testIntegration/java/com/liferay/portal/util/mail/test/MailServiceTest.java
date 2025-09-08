/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.mail.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.mail.Session;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eric Yan
 */
@RunWith(Arquillian.class)
public class MailServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testEnablePOPServerNotificationsIsolatedPerCompany()
		throws Exception {

		long companyId1 = RandomTestUtil.randomLong();
		long companyId2 = RandomTestUtil.randomLong();

		MailSettingCompanyConfiguration mailSettingCompanyConfiguration1;
		MailSettingCompanyConfiguration mailSettingCompanyConfiguration2;

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper1 =
					new CompanyConfigurationTemporarySwapper(
						companyId1,
						"com.liferay.mail.settings.configuration." +
							"MailSettingCompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enablePOPServerNotifications", "false"
						).build());
			CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper2 =
					new CompanyConfigurationTemporarySwapper(
						companyId2,
						"com.liferay.mail.settings.configuration." +
							"MailSettingCompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enablePOPServerNotifications", "false"
						).build())) {

			mailSettingCompanyConfiguration1 =
				_configurationProvider.getCompanyConfiguration(
					MailSettingCompanyConfiguration.class, companyId1);

			mailSettingCompanyConfiguration2 =
				_configurationProvider.getCompanyConfiguration(
					MailSettingCompanyConfiguration.class, companyId2);

			Assert.assertFalse(
				mailSettingCompanyConfiguration1.
					enablePOPServerNotifications());

			Assert.assertFalse(
				mailSettingCompanyConfiguration2.
					enablePOPServerNotifications());

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							companyId1,
							"com.liferay.mail.settings.configuration." +
								"MailSettingCompanyConfiguration",
							HashMapDictionaryBuilder.<String, Object>put(
								"enablePOPServerNotifications", "true"
							).build())) {

				mailSettingCompanyConfiguration1 =
					_configurationProvider.getCompanyConfiguration(
						MailSettingCompanyConfiguration.class, companyId1);

				mailSettingCompanyConfiguration2 =
					_configurationProvider.getCompanyConfiguration(
						MailSettingCompanyConfiguration.class, companyId2);

				Assert.assertTrue(
					mailSettingCompanyConfiguration1.
						enablePOPServerNotifications());

				Assert.assertFalse(
					mailSettingCompanyConfiguration2.
						enablePOPServerNotifications());
			}
		}
	}

	@Test
	public void testGetSessionWithCompanyId() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String smtpHost = "test.local";

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						companyId,
						"com.liferay.mail.settings.configuration." +
							"MailSettingCompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"outgoingSMTPServer", smtpHost
						).build())) {

			Session session = _mailService.getSession(companyId);

			Assert.assertEquals(
				smtpHost, session.getProperty("mail.smtp.host"));

			session = _mailService.getSession(_portal.getDefaultCompanyId());

			MailSettingCompanyConfiguration mailSettingCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					MailSettingCompanyConfiguration.class,
					_portal.getDefaultCompanyId());

			Assert.assertEquals(
				mailSettingCompanyConfiguration.outgoingSMTPServer(),
				session.getProperty("mail.smtp.host"));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject
	private MailService _mailService;

	@Inject(filter = "mvc.command.name=/server_admin/edit_server")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}
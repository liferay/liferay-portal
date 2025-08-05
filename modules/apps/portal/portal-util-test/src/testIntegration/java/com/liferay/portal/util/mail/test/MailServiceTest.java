/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.mail.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.kernel.service.MailService;

import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
	public void testGetSessionWithCompanyId() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String systemSmtpHost = "test.system.local";
		String instanceSmtpHost = "test.instance.local";


		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.mail.setting.internal.configuration.MailSettingConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"outgoingSMTPServer", systemSmtpHost
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							"com.liferay.mail.setting.internal.configuration.MailSettingConfiguration",
							HashMapDictionaryBuilder.<String, Object>put(
								"outgoingSMTPServer", instanceSmtpHost
							).build())) {

				Session session = _mailService.getSession(companyId);

				Assert.assertEquals(
					instanceSmtpHost, session.getProperty("mail.smtp.host"));
			}

			Session session = _mailService.getSession(
				_portal.getDefaultCompanyId());

			Assert.assertEquals(
				systemSmtpHost, session.getProperty("mail.smtp.host"));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private MailService _mailService;

	@Inject(filter = "mvc.command.name=/server_admin/edit_server")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}
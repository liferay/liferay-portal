/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.mail.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.mail.Session;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletPreferences;

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

		_setCompanyPortletPreferences(
			companyId1, PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED, "false");
		_setCompanyPortletPreferences(
			companyId2, PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED, "false");

		Assert.assertFalse(
			PrefsPropsUtil.getBoolean(
				companyId1, PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED));
		Assert.assertFalse(
			PrefsPropsUtil.getBoolean(
				companyId2, PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"popServerNotificationsEnabled", "true");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateMail",
			new Class<?>[] {ActionRequest.class, PortletPreferences.class},
			mockLiferayPortletActionRequest,
			PrefsPropsUtil.getPreferences(companyId1));

		Assert.assertTrue(
			PrefsPropsUtil.getBoolean(
				companyId1, PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED));
		Assert.assertFalse(
			PrefsPropsUtil.getBoolean(
				companyId2, PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED));
	}

	@Test
	public void testGetSessionWithCompanyId() {
		long companyId = RandomTestUtil.randomLong();
		String smtpHost = "test.local";

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter("smtpHost", smtpHost);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateMail",
			new Class<?>[] {ActionRequest.class, PortletPreferences.class},
			mockLiferayPortletActionRequest,
			PrefsPropsUtil.getPreferences(companyId));

		Session session = _mailService.getSession(companyId);

		Assert.assertEquals(smtpHost, session.getProperty("mail.smtp.host"));

		session = _mailService.getSession(_portal.getDefaultCompanyId());

		Assert.assertEquals(
			PropsValues.MAIL_SESSION_MAIL_SMTP_HOST,
			session.getProperty("mail.smtp.host"));
	}

	private void _setCompanyPortletPreferences(
			long companyId, String key, String value)
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			companyId);

		portletPreferences.setValue(key, value);

		portletPreferences.store();
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
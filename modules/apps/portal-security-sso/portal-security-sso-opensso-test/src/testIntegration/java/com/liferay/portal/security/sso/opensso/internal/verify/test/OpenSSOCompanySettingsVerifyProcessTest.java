/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.opensso.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.opensso.constants.LegacyOpenSSOPropsKeys;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOConfigurationKeys;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseCompanySettingsVerifyProcessTestCase;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Brian Greenwald
 */
@RunWith(Arquillian.class)
public class OpenSSOCompanySettingsVerifyProcessTest
	extends BaseCompanySettingsVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void doVerify(
		PortletPreferences portletPreferences, Settings settings) {

		for (String key : LegacyOpenSSOPropsKeys.OPENSSO_KEYS) {
			Assert.assertTrue(
				Validator.isNull(
					portletPreferences.getValue(key, StringPool.BLANK)));
		}

		Assert.assertEquals(
			"testEmailAddressAttr",
			settings.getValue(
				OpenSSOConfigurationKeys.EMAIL_ADDRESS_ATTR, StringPool.BLANK));
		Assert.assertTrue(
			GetterUtil.getBoolean(
				settings.getValue(
					OpenSSOConfigurationKeys.AUTH_ENABLED, StringPool.FALSE)));
		Assert.assertEquals(
			"testFirstNameAttr",
			settings.getValue(
				OpenSSOConfigurationKeys.FIRST_NAME_ATTR, StringPool.BLANK));
		Assert.assertTrue(
			GetterUtil.getBoolean(
				settings.getValue(
					OpenSSOConfigurationKeys.IMPORT_FROM_LDAP,
					StringPool.FALSE)));
		Assert.assertEquals(
			"testLastNameAttr",
			settings.getValue(
				OpenSSOConfigurationKeys.LAST_NAME_ATTR, StringPool.BLANK));
		Assert.assertEquals(
			"http://test.com/login/url",
			settings.getValue(
				OpenSSOConfigurationKeys.LOGIN_URL, StringPool.BLANK));
		Assert.assertTrue(
			GetterUtil.getBoolean(
				settings.getValue(
					OpenSSOConfigurationKeys.LOGOUT_ON_SESSION_EXPIRATION,
					StringPool.FALSE)));
		Assert.assertEquals(
			"http://test.com/logout/url",
			settings.getValue(
				OpenSSOConfigurationKeys.LOGOUT_URL, StringPool.BLANK));
		Assert.assertEquals(
			"testScreenNameAttr",
			settings.getValue(
				OpenSSOConfigurationKeys.SCREEN_NAME_ATTR, StringPool.BLANK));
		Assert.assertEquals(
			"http://test.com/service/url",
			settings.getValue(
				OpenSSOConfigurationKeys.SERVICE_URL, StringPool.BLANK));
	}

	@Override
	protected String getSettingsId() {
		return OpenSSOConstants.SERVICE_NAME;
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return _verifyProcess;
	}

	@Override
	protected void populateLegacyProperties(
		UnicodeProperties unicodeProperties) {

		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_EMAIL_ADDRESS_ATTR,
			"testEmailAddressAttr");
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_AUTH_ENABLED, StringPool.TRUE);
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_FIRST_NAME_ATTR,
			"testFirstNameAttr");
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_IMPORT_FROM_LDAP, StringPool.TRUE);
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_LAST_NAME_ATTR, "testLastNameAttr");
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_LOGIN_URL,
			"http://test.com/login/url");
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_LOGOUT_ON_SESSION_EXPIRATION,
			StringPool.TRUE);
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_LOGOUT_URL,
			"http://test.com/logout/url");
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_SCREEN_NAME_ATTR,
			"testScreenNameAttr");
		unicodeProperties.put(
			LegacyOpenSSOPropsKeys.OPENSSO_SERVICE_URL,
			"http://test.com/service/url");
	}

	@Inject(
		filter = "component.name=com.liferay.portal.security.sso.opensso.internal.verify.OpenSSOCompanySettingsVerifyProcess"
	)
	private VerifyProcess _verifyProcess;

}
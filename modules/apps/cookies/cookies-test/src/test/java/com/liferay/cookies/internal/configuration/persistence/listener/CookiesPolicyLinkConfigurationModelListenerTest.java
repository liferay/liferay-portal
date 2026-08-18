/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.persistence.listener;

import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class CookiesPolicyLinkConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOnBeforeSaveInvalidPolicyLink() throws Exception {
		try (MockedStatic<ResourceBundleUtil> resourceBundleUtilMockedStatic =
				Mockito.mockStatic(ResourceBundleUtil.class)) {

			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class, "privacyPolicyLink",
				"//liferay.com");
			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class, "privacyPolicyLink",
				"JavaScript:alert(document.domain)");
			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class, "privacyPolicyLink",
				"data:text/html,<script>alert(1)</script>");
			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class, "privacyPolicyLink",
				"javascript:alert(document.domain)//cm-xss");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class, "cookiePolicyLink",
				"//liferay.com");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class, "cookiePolicyLink",
				"data:text/html,<script>alert(1)</script>");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class, "cookiePolicyLink",
				"javascript:alert(1)//cm-xss2");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class, "cookiePolicyLink",
				"vbscript:msgbox(1)");
		}
	}

	@Test
	public void testOnBeforeSaveValidPolicyLink() throws Exception {
		_assertValidPolicyLink("cookiePolicyLink", "/web/guest/cookie-policy");
		_assertValidPolicyLink("cookiePolicyLink", "HTTPS://liferay.com");
		_assertValidPolicyLink("cookiePolicyLink", "http://liferay.com");
		_assertValidPolicyLink("cookiePolicyLink", "https://liferay.com");
		_assertValidPolicyLink("cookiePolicyLink", StringPool.BLANK);
		_assertValidPolicyLink("cookiePolicyLink", null);
		_assertValidPolicyLink("privacyPolicyLink", "/web/guest/privacy");
		_assertValidPolicyLink("privacyPolicyLink", "HTTPS://liferay.com");
		_assertValidPolicyLink("privacyPolicyLink", "http://liferay.com");
		_assertValidPolicyLink("privacyPolicyLink", "https://liferay.com");
		_assertValidPolicyLink("privacyPolicyLink", StringPool.BLANK);
		_assertValidPolicyLink("privacyPolicyLink", null);
	}

	private void _assertInvalidPolicyLink(
		Class<?> configurationClass, String name, String policyLink) {

		ConfigurationModelListenerException
			configurationModelListenerException = Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> _configurationModelListener.onBeforeSave(
					configurationClass.getName(),
					_createProperties(name, policyLink)));

		Assert.assertEquals(
			configurationClass,
			configurationModelListenerException.configurationClass);
	}

	private void _assertValidPolicyLink(String name, String policyLink)
		throws Exception {

		_configurationModelListener.onBeforeSave(
			CookiesBannerConfiguration.class.getName(),
			_createProperties(name, policyLink));
	}

	private Dictionary<String, Object> _createProperties(
		String name, String policyLink) {

		if (policyLink == null) {
			return new HashMapDictionary<>();
		}

		return HashMapDictionaryBuilder.<String, Object>put(
			name, policyLink
		).build();
	}

	private final ConfigurationModelListener _configurationModelListener =
		new CookiesPolicyLinkConfigurationModelListener();

}
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
				CookiesBannerConfiguration.class, "//liferay.com",
				"privacyPolicyLink");
			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class, "/\\liferay.com",
				"privacyPolicyLink");
			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class,
				"JavaScript:alert(document.domain)", "privacyPolicyLink");
			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class,
				"data:text/html,<script>alert(1)</script>",
				"privacyPolicyLink");
			_assertInvalidPolicyLink(
				CookiesBannerConfiguration.class,
				"javascript:alert(document.domain)//cm-xss",
				"privacyPolicyLink");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class, "//liferay.com",
				"cookiePolicyLink");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class, "/\\liferay.com",
				"cookiePolicyLink");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class,
				"data:text/html,<script>alert(1)</script>", "cookiePolicyLink");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class,
				"javascript:alert(1)//cm-xss2", "cookiePolicyLink");
			_assertInvalidPolicyLink(
				CookiesConsentConfiguration.class, "vbscript:msgbox(1)",
				"cookiePolicyLink");
		}
	}

	@Test
	public void testOnBeforeSaveValidPolicyLink() throws Exception {
		_assertValidPolicyLink(
			CookiesBannerConfiguration.class, "/web/guest/privacy",
			"privacyPolicyLink");
		_assertValidPolicyLink(
			CookiesBannerConfiguration.class, "HTTPS://liferay.com",
			"privacyPolicyLink");
		_assertValidPolicyLink(
			CookiesBannerConfiguration.class, StringPool.BLANK,
			"privacyPolicyLink");
		_assertValidPolicyLink(
			CookiesBannerConfiguration.class, "http://liferay.com",
			"privacyPolicyLink");
		_assertValidPolicyLink(
			CookiesBannerConfiguration.class, "https://liferay.com",
			"privacyPolicyLink");
		_assertValidPolicyLink(
			CookiesConsentConfiguration.class, "/web/guest/cookie-policy",
			"cookiePolicyLink");
		_assertValidPolicyLink(
			CookiesConsentConfiguration.class, "HTTPS://liferay.com",
			"cookiePolicyLink");
		_assertValidPolicyLink(
			CookiesConsentConfiguration.class, StringPool.BLANK,
			"cookiePolicyLink");
		_assertValidPolicyLink(
			CookiesConsentConfiguration.class, "http://liferay.com",
			"cookiePolicyLink");
		_assertValidPolicyLink(
			CookiesConsentConfiguration.class, "https://liferay.com",
			"cookiePolicyLink");

		_assertValidPolicyLink(
			CookiesBannerConfiguration.class, "privacyPolicyLink");
		_assertValidPolicyLink(
			CookiesConsentConfiguration.class, "cookiePolicyLink");
	}

	private void _assertInvalidPolicyLink(
		Class<?> configurationClass, String policyLink, String propertyName) {

		ConfigurationModelListenerException
			configurationModelListenerException = Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> _configurationModelListener.onBeforeSave(
					configurationClass.getName(),
					_createProperties(policyLink, propertyName)));

		Assert.assertEquals(
			configurationClass,
			configurationModelListenerException.configurationClass);
	}

	private void _assertValidPolicyLink(
			Class<?> configurationClass, String propertyName)
		throws Exception {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		_configurationModelListener.onBeforeSave(
			configurationClass.getName(), properties);

		Assert.assertNull(properties.get(propertyName));
	}

	private void _assertValidPolicyLink(
			Class<?> configurationClass, String policyLink, String propertyName)
		throws Exception {

		Dictionary<String, Object> properties = _createProperties(
			policyLink, propertyName);

		_configurationModelListener.onBeforeSave(
			configurationClass.getName(), properties);

		Assert.assertEquals(policyLink, properties.get(propertyName));
	}

	private Dictionary<String, Object> _createProperties(
		String policyLink, String propertyName) {

		return HashMapDictionaryBuilder.<String, Object>put(
			propertyName, policyLink
		).build();
	}

	private final ConfigurationModelListener _configurationModelListener =
		new CookiesPolicyLinkConfigurationModelListener();

}
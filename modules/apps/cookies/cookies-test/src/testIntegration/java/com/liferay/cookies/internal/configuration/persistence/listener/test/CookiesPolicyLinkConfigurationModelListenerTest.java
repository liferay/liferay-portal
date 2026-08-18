/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class CookiesPolicyLinkConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			CookiesBannerConfiguration.class.getName());
		ConfigurationTestUtil.deleteConfiguration(
			CookiesConsentConfiguration.class.getName());
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		_testOnBeforeSave(
			CookiesBannerConfiguration.class, "privacyPolicyLink",
			"/web/guest/privacy");
		_testOnBeforeSave(
			CookiesConsentConfiguration.class, "cookiePolicyLink",
			"https://liferay.com/cookie-policy");
	}

	private Object _fetchPolicyLink(Class<?> configurationClass, String name)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.pid=" + configurationClass.getName() + ")");

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		return properties.get(name);
	}

	private void _saveConfiguration(
			Class<?> configurationClass, String name, String policyLink)
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(
			configurationClass.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				name, policyLink
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).build());
	}

	private void _testOnBeforeSave(
			Class<?> configurationClass, String name, String policyLink)
		throws Exception {

		_saveConfiguration(configurationClass, name, policyLink);

		Assert.assertEquals(
			policyLink, _fetchPolicyLink(configurationClass, name));
		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _saveConfiguration(
				configurationClass, name, "//liferay.com"));
		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _saveConfiguration(
				configurationClass, name,
				"data:text/html,<script>alert(1)</script>"));
		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _saveConfiguration(
				configurationClass, name,
				"javascript:alert(document.domain)//cm-xss"));

		Assert.assertEquals(
			policyLink, _fetchPolicyLink(configurationClass, name));
	}

	@Inject
	private ConfigurationAdmin _configurationAdmin;

}
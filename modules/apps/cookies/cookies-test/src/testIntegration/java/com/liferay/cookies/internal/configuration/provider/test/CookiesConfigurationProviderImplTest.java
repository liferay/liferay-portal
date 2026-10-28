/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alvaro Saugar
 */
@RunWith(Arquillian.class)
public class CookiesConfigurationProviderImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testGetCookiesBannerConfiguration() throws Exception {
		String systemPrivacyPolicyLink = "/" + RandomTestUtil.randomString();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					CookiesBannerConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"privacyPolicyLink", systemPrivacyPolicyLink
					).build())) {

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setScopeGroupId(_group.getGroupId());

			CookiesBannerConfiguration cookiesBannerConfiguration =
				_cookiesConfigurationProvider.getCookiesBannerConfiguration(
					themeDisplay);

			Assert.assertEquals(
				systemPrivacyPolicyLink,
				cookiesBannerConfiguration.privacyPolicyLink());

			String groupPrivacyPolicyLink = "/" + RandomTestUtil.randomString();

			try (GroupConfigurationTemporarySwapper
					groupConfigurationTemporarySwapper =
						new GroupConfigurationTemporarySwapper(
							_group.getGroupId(),
							CookiesBannerConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"privacyPolicyLink", groupPrivacyPolicyLink
							).build())) {

				cookiesBannerConfiguration =
					_cookiesConfigurationProvider.getCookiesBannerConfiguration(
						themeDisplay);

				Assert.assertEquals(
					groupPrivacyPolicyLink,
					cookiesBannerConfiguration.privacyPolicyLink());
			}
		}
	}

	@Test
	public void testGetCookiesConsentConfiguration() throws Exception {
		String systemCookiePolicyLink = "/" + RandomTestUtil.randomString();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					CookiesConsentConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"cookiePolicyLink", systemCookiePolicyLink
					).build())) {

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setScopeGroupId(_group.getGroupId());

			CookiesConsentConfiguration cookiesConsentConfiguration =
				_cookiesConfigurationProvider.getCookiesConsentConfiguration(
					themeDisplay);

			Assert.assertEquals(
				systemCookiePolicyLink,
				cookiesConsentConfiguration.cookiePolicyLink());

			String groupCookiePolicyLink = "/" + RandomTestUtil.randomString();

			try (GroupConfigurationTemporarySwapper
					groupConfigurationTemporarySwapper =
						new GroupConfigurationTemporarySwapper(
							_group.getGroupId(),
							CookiesConsentConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"cookiePolicyLink", groupCookiePolicyLink
							).build())) {

				cookiesConsentConfiguration =
					_cookiesConfigurationProvider.
						getCookiesConsentConfiguration(themeDisplay);

				Assert.assertEquals(
					groupCookiePolicyLink,
					cookiesConsentConfiguration.cookiePolicyLink());
			}
		}
	}

	@Test
	public void testIsCookiesPreferenceHandlingActive() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		long groupId = _group.getGroupId();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					CookiesPreferenceHandlingConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"active", true
					).put(
						"enabled", true
					).build())) {

			Assert.assertTrue(
				_cookiesConfigurationProvider.isCookiesPreferenceHandlingActive(
					ExtendedObjectClassDefinition.Scope.COMPANY, companyId));
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					CookiesPreferenceHandlingConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"active", true
					).put(
						"enabled", true
					).build());
			CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						companyId,
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"active", false
						).put(
							"enabled", true
						).build())) {

			Assert.assertFalse(
				_cookiesConfigurationProvider.isCookiesPreferenceHandlingActive(
					ExtendedObjectClassDefinition.Scope.COMPANY, companyId));

			Assert.assertTrue(
				_cookiesConfigurationProvider.isCookiesPreferenceHandlingActive(
					ExtendedObjectClassDefinition.Scope.SYSTEM, 0));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						companyId,
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"active", true
						).put(
							"enabled", true
						).build())) {

			Assert.assertTrue(
				_cookiesConfigurationProvider.isCookiesPreferenceHandlingActive(
					ExtendedObjectClassDefinition.Scope.GROUP, groupId));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						companyId,
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"active", false
						).put(
							"enabled", true
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						groupId,
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"active", true
						).put(
							"enabled", true
						).build())) {

			Assert.assertTrue(
				_cookiesConfigurationProvider.isCookiesPreferenceHandlingActive(
					ExtendedObjectClassDefinition.Scope.GROUP, groupId));

			Assert.assertFalse(
				_cookiesConfigurationProvider.isCookiesPreferenceHandlingActive(
					ExtendedObjectClassDefinition.Scope.COMPANY, companyId));
		}
	}

	@Inject
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	private Group _group;

}
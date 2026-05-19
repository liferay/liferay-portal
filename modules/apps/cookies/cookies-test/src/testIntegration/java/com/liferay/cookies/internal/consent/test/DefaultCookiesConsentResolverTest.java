/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.consent.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.consent.CookiesConsentResolver;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class DefaultCookiesConsentResolverTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testHasConsent() throws Exception {
		Assert.assertTrue(
			_cookiesConsentResolver.hasConsent(
				CookiesConstants.CONSENT_TYPE_NECESSARY,
				_createMockHttpServletRequest()));

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest();

		mockHttpServletRequest.setCookies(
			new Cookie(CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL, "true"));

		Assert.assertTrue(
			_cookiesConsentResolver.hasConsent(
				CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
				mockHttpServletRequest));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"explicitConsentMode", false
						).build())) {

			Assert.assertTrue(
				_cookiesConsentResolver.hasConsent(
					CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
					_createMockHttpServletRequest()));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"explicitConsentMode", true
						).build())) {

			Assert.assertFalse(
				_cookiesConsentResolver.hasConsent(
					CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
					_createMockHttpServletRequest()));
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		return mockHttpServletRequest;
	}

	@Inject(
		filter = "component.name=com.liferay.cookies.internal.consent.DefaultCookiesConsentResolver"
	)
	private CookiesConsentResolver _cookiesConsentResolver;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.consent.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.cookies.consent.CookiesConsentChecker;
import com.liferay.cookies.consent.CookiesConsentCheckerResolver;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import org.junit.After;
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
public class CookiesConsentCheckerResolverImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		if (_group != null) {
			GroupTestUtil.deleteGroup(_group);
		}
	}

	@Test
	public void testGetCookiesConsentChecker() throws Exception {
		CookiesConsentChecker cookiesConsentChecker =
			_cookiesConsentCheckerResolver.getCookiesConsentChecker(null);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						ConsentManagementPlatformConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequest();

			Assert.assertSame(
				cookiesConsentChecker,
				_cookiesConsentCheckerResolver.getCookiesConsentChecker(
					mockHttpServletRequest));

			mockHttpServletRequest.setCookies(
				new Cookie(
					CookiesConstants.NAME_CONSENT_STATE,
					URLEncoder.encode(
						JSONUtil.put(
							CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL, false
						).toString(),
						StandardCharsets.UTF_8)),
				new Cookie(
					CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL, "true"));

			Assert.assertTrue(
				cookiesConsentChecker.hasConsent(
					CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
					mockHttpServletRequest));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						ConsentManagementPlatformConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			Assert.assertSame(
				_consentManagementPlatformCookiesConsentChecker,
				_cookiesConsentCheckerResolver.getCookiesConsentChecker(
					_createMockHttpServletRequest()));
		}

		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(),
						ConsentManagementPlatformConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			Assert.assertSame(
				_consentManagementPlatformCookiesConsentChecker,
				_cookiesConsentCheckerResolver.getCookiesConsentChecker(
					mockHttpServletRequest));
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
		filter = "component.name=com.liferay.consent.management.platform.integration.internal.cookies.consent.ConsentManagementPlatformCookiesConsentChecker"
	)
	private CookiesConsentChecker
		_consentManagementPlatformCookiesConsentChecker;

	@Inject
	private CookiesConsentCheckerResolver _cookiesConsentCheckerResolver;

	private Group _group;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class EnableThirdPartyCookiesBottomJSDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInclude() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"globalPrivacyControlEnabled", true
						).build())) {

			String content = _include(_createMockHttpServletRequest("1"));

			Assert.assertFalse(
				content, content.contains("runThirdPartyCookiesInterval("));
			Assert.assertTrue(
				content, content.contains("suppressThirdPartyCookies("));

			content = _include(_createMockHttpServletRequest(null));

			Assert.assertTrue(
				content, content.contains("runThirdPartyCookiesInterval("));
			Assert.assertFalse(
				content, content.contains("suppressThirdPartyCookies("));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"globalPrivacyControlEnabled", false
						).build())) {

			String content = _include(_createMockHttpServletRequest("1"));

			Assert.assertTrue(
				content, content.contains("runThirdPartyCookiesInterval("));
			Assert.assertFalse(
				content, content.contains("suppressThirdPartyCookies("));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).put(
							"globalPrivacyControlEnabled", true
						).build())) {

			String content = _include(_createMockHttpServletRequest("1"));

			Assert.assertTrue(content, content.isEmpty());
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
			String secGPCHeader)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (secGPCHeader != null) {
			mockHttpServletRequest.addHeader("Sec-GPC", secGPCHeader);
		}

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setServerName(StringPool.BLANK);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private String _include(MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			mockHttpServletRequest, mockHttpServletResponse,
			RandomTestUtil.randomString());

		return mockHttpServletResponse.getContentAsString();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.cookies.banner.web.internal.servlet.taglib.EnableThirdPartyCookiesBottomJSDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

}
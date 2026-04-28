/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Christian Moura
 */
@FeatureFlag("LPD-75064")
@RunWith(Arquillian.class)
public class EnableThirdPartyCookiesBottomJSDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();

		_companyConfigurationPid =
			ConfigurationTestUtil.createFactoryConfiguration(
				_SCOPED_FACTORY_PID,
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", _companyId
				).build());
	}

	@After
	public void tearDown() throws Exception {
		if (_companyConfigurationPid != null) {
			ConfigurationTestUtil.deleteFactoryConfiguration(
				_companyConfigurationPid, _SCOPED_FACTORY_PID);
		}
	}

	@Test
	public void testInclude() throws Exception {
		try (ConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new ConfigurationTemporarySwapper(
						_companyConfigurationPid,
						HashMapDictionaryBuilder.<String, Object>put(
							"companyId", _companyId
						).put(
							"enabled", true
						).put(
							"globalPrivacyControlEnabled", true
						).build())) {

			String output = _include(_createMockHttpServletRequest("1"));

			Assert.assertFalse(
				output, output.contains("runThirdPartyCookiesInterval("));
			Assert.assertTrue(
				output, output.contains("suppressThirdPartyCookies("));

			output = _include(_createMockHttpServletRequest(null));

			Assert.assertTrue(
				output, output.contains("runThirdPartyCookiesInterval("));
			Assert.assertFalse(
				output, output.contains("suppressThirdPartyCookies("));
		}

		try (ConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new ConfigurationTemporarySwapper(
						_companyConfigurationPid,
						HashMapDictionaryBuilder.<String, Object>put(
							"companyId", _companyId
						).put(
							"enabled", true
						).put(
							"globalPrivacyControlEnabled", false
						).build())) {

			String output = _include(_createMockHttpServletRequest("1"));

			Assert.assertTrue(
				output, output.contains("runThirdPartyCookiesInterval("));
			Assert.assertFalse(
				output, output.contains("suppressThirdPartyCookies("));
		}

		try (ConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new ConfigurationTemporarySwapper(
						_companyConfigurationPid,
						HashMapDictionaryBuilder.<String, Object>put(
							"companyId", _companyId
						).put(
							"enabled", false
						).put(
							"globalPrivacyControlEnabled", true
						).build())) {

			String output = _include(_createMockHttpServletRequest("1"));

			Assert.assertTrue(output, output.isEmpty());
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
			String secGpcHeader)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.COMPANY_ID, _companyId);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_companyLocalService.fetchCompany(_companyId));
		themeDisplay.setServerName(StringPool.BLANK);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		if (secGpcHeader != null) {
			mockHttpServletRequest.addHeader("Sec-GPC", secGpcHeader);
		}

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

	private static final String _SCOPED_FACTORY_PID =
		CookiesPreferenceHandlingConfiguration.class.getName() + ".scoped";

	private String _companyConfigurationPid;
	private long _companyId;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.cookies.banner.web.internal.servlet.taglib.EnableThirdPartyCookiesBottomJSDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

}
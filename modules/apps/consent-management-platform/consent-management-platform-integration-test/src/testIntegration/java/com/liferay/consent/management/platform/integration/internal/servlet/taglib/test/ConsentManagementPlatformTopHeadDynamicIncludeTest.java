/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
public class ConsentManagementPlatformTopHeadDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testInclude() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			ConsentManagementPlatformConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"consentMappingScript", _SCRIPT_CONSENT_MAPPING
			).put(
				"enabled", true
			).put(
				"providerName", RandomTestUtil.randomString()
			).put(
				"scriptTag", _SCRIPT_TAG
			).build());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			_createMockHttpServletRequest(null), mockHttpServletResponse,
			StringPool.BLANK);

		String content = mockHttpServletResponse.getContentAsString();

		int scriptTagIndex = content.indexOf(_SCRIPT_TAG);

		Assert.assertTrue(scriptTagIndex >= 0);

		int consentMappingScriptIndex = content.indexOf(
			_SCRIPT_CONSENT_MAPPING);

		Assert.assertTrue(consentMappingScriptIndex >= 0);
		Assert.assertTrue(scriptTagIndex < consentMappingScriptIndex);

		Assert.assertEquals(0, StringUtil.count(content, "nonce=\""));

		mockHttpServletResponse = new MockHttpServletResponse();

		String nonce = RandomTestUtil.randomString();

		_dynamicInclude.include(
			_createMockHttpServletRequest(nonce), mockHttpServletResponse,
			StringPool.BLANK);

		content = mockHttpServletResponse.getContentAsString();

		String nonceAttribute = "nonce=\"" + nonce + "\"";

		Assert.assertEquals(3, StringUtil.count(content, nonceAttribute));
		Assert.assertTrue(content.contains("<SCRIPT " + nonceAttribute));
		Assert.assertTrue(content.contains("<link " + nonceAttribute));
		Assert.assertTrue(content.contains("<script " + nonceAttribute));
	}

	private MockHttpServletRequest _createMockHttpServletRequest(String nonce)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setAttribute(
			"com.liferay.portal.security.content.security.policy.internal." +
				"ContentSecurityPolicyNonceManager#NONCE",
			nonce);

		return mockHttpServletRequest;
	}

	private static final String _SCRIPT_CONSENT_MAPPING =
		"<SCRIPT id=\"liferay-cmp-consent-mapping\">/* mapping */</SCRIPT>";

	private static final String _SCRIPT_TAG = StringBundler.concat(
		"<link as=\"script\" href=\"https://consent.cookiebot.com/uc.js\" ",
		"rel=\"preload\"><script data-cbid=\"000000\" id=\"Cookiebot\" ",
		"src=\"https://consent.cookiebot.com/uc.js\" ",
		"type=\"text/javascript\"></script>");

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.consent.management.platform.integration.internal.servlet.taglib.ConsentManagementPlatformTopHeadDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

}
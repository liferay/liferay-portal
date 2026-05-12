/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.global.privacy.control.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.global.privacy.control.GlobalPrivacyControlProvider;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class GlobalPrivacyControlProviderImplTest {

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
	public void testIsEnabled() throws Exception {
		Assert.assertFalse(
			_globalPrivacyControlProvider.isEnabled(
				_createMockHttpServletRequest(false, null)));

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

			Assert.assertFalse(
				_globalPrivacyControlProvider.isEnabled(
					_createMockHttpServletRequest(false, null)));
		}

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

			Assert.assertTrue(
				_globalPrivacyControlProvider.isEnabled(
					_createMockHttpServletRequest(false, null)));
		}
	}

	@Test
	public void testIsSignalActive() throws Exception {
		Assert.assertFalse(
			_globalPrivacyControlProvider.isSignalActive(
				_createMockHttpServletRequest(false, "1")));

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

			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(false, "1")));
		}

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

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(false, " 1 ")));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(false, "0")));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(false, "0, 1")));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(false, "true")));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(false, null)));

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequest(false, "0");

			mockHttpServletRequest.addHeader("Sec-GPC", "1");

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					mockHttpServletRequest));

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(true, "1")));
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
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"globalPrivacyControlEnabled", true
						).build())) {

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(true, "1")));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"globalPrivacyControlEnabled", true
						).build());
			GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(),
						CookiesPreferenceHandlingConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).put(
							"globalPrivacyControlEnabled", false
						).build())) {

			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(true, "1")));
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
			boolean includeLayout, String secGPCHeader)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (secGPCHeader != null) {
			mockHttpServletRequest.addHeader("Sec-GPC", secGPCHeader);
		}

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		if (includeLayout) {
			if (_group == null) {
				_group = GroupTestUtil.addGroup();

				_layout = LayoutTestUtil.addTypePortletLayout(_group);
			}

			mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);
		}

		return mockHttpServletRequest;
	}

	@Inject
	private GlobalPrivacyControlProvider _globalPrivacyControlProvider;

	private Group _group;
	private Layout _layout;

}
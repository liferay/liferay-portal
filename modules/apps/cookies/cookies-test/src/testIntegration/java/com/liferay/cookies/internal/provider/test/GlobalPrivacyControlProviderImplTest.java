/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.provider.GlobalPrivacyControlProvider;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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

/**
 * @author Christian Moura
 */
@FeatureFlag("LPD-75064")
@RunWith(Arquillian.class)
public class GlobalPrivacyControlProviderImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();
	}

	@After
	public void tearDown() throws Exception {
		if (_companyConfigurationPid != null) {
			ConfigurationTestUtil.deleteFactoryConfiguration(
				_companyConfigurationPid, _SCOPED_FACTORY_PID);
		}

		if (_groupConfigurationPid != null) {
			ConfigurationTestUtil.deleteFactoryConfiguration(
				_groupConfigurationPid, _SCOPED_FACTORY_PID);
		}

		if (_group != null) {
			GroupTestUtil.deleteGroup(_group);
		}
	}

	@FeatureFlag(enable = false, value = "LPD-75064")
	@Test
	public void testFeatureFlagDisabled() throws Exception {
		_saveCompanyConfiguration(true, true);

		Assert.assertFalse(
			_globalPrivacyControlProvider.isEnabled(
				_createMockHttpServletRequest(null)));
		Assert.assertFalse(
			_globalPrivacyControlProvider.isSignalActive(
				_createMockHttpServletRequest("1")));
	}

	@Test
	public void testIsEnabled() throws Exception {
		Assert.assertFalse(
			_globalPrivacyControlProvider.isEnabled(
				_createMockHttpServletRequest(null)));

		_saveCompanyConfiguration(false, true);

		Assert.assertFalse(
			_globalPrivacyControlProvider.isEnabled(
				_createMockHttpServletRequest(null)));

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

			Assert.assertFalse(
				_globalPrivacyControlProvider.isEnabled(
					_createMockHttpServletRequest(null)));
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
							"globalPrivacyControlEnabled", true
						).build())) {

			Assert.assertTrue(
				_globalPrivacyControlProvider.isEnabled(
					_createMockHttpServletRequest(null)));
			Assert.assertTrue(
				_globalPrivacyControlProvider.isEnabled(
					_createMockHttpServletRequest("0")));
			Assert.assertTrue(
				_globalPrivacyControlProvider.isEnabled(
					_createMockHttpServletRequest("invalid")));
		}
	}

	@Test
	public void testIsSignalActive() throws Exception {
		Assert.assertFalse(
			_globalPrivacyControlProvider.isSignalActive(
				_createMockHttpServletRequest("1")));

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					CookiesPreferenceHandlingConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"globalPrivacyControlEnabled", true
					).build())) {

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest("1")));
		}

		_saveCompanyConfiguration(false, true);

		Assert.assertFalse(
			_globalPrivacyControlProvider.isSignalActive(
				_createMockHttpServletRequest("1")));

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

			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest("1")));
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
							"globalPrivacyControlEnabled", true
						).build())) {

			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(null)));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest("0")));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest("true")));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest("0, 1")));
			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest("1, 0")));
			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest("1")));
			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequest(" 1 ")));

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequest("0");

			mockHttpServletRequest.addHeader("Sec-GPC", "1");

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					mockHttpServletRequest));

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequestWithLayout("1")));
		}

		_groupConfigurationPid =
			ConfigurationTestUtil.createFactoryConfiguration(
				_SCOPED_FACTORY_PID,
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", _companyId
				).put(
					"enabled", false
				).put(
					"globalPrivacyControlEnabled", false
				).put(
					"groupId", _group.getGroupId()
				).build());

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
						).build());
			ConfigurationTemporarySwapper groupConfigurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_groupConfigurationPid,
					HashMapDictionaryBuilder.<String, Object>put(
						"companyId", _companyId
					).put(
						"enabled", true
					).put(
						"globalPrivacyControlEnabled", true
					).put(
						"groupId", _group.getGroupId()
					).build())) {

			Assert.assertTrue(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequestWithLayout("1")));
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
							"globalPrivacyControlEnabled", true
						).build());
			ConfigurationTemporarySwapper groupConfigurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_groupConfigurationPid,
					HashMapDictionaryBuilder.<String, Object>put(
						"companyId", _companyId
					).put(
						"enabled", true
					).put(
						"globalPrivacyControlEnabled", false
					).put(
						"groupId", _group.getGroupId()
					).build())) {

			Assert.assertFalse(
				_globalPrivacyControlProvider.isSignalActive(
					_createMockHttpServletRequestWithLayout("1")));
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest(
		String secGpcHeader) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (secGpcHeader != null) {
			mockHttpServletRequest.addHeader("Sec-GPC", secGpcHeader);
		}

		mockHttpServletRequest.setAttribute(WebKeys.COMPANY_ID, _companyId);

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _createMockHttpServletRequestWithLayout(
			String secGpcHeader)
		throws Exception {

		if (_group == null) {
			_group = GroupTestUtil.addGroup();

			_layout = LayoutTestUtil.addTypePortletLayout(_group);
		}

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest(secGpcHeader);

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		return mockHttpServletRequest;
	}

	private void _saveCompanyConfiguration(
			boolean cookiesPreferenceHandlingEnabled,
			boolean globalPrivacyControlEnabled)
		throws Exception {

		_companyConfigurationPid =
			ConfigurationTestUtil.createFactoryConfiguration(
				_SCOPED_FACTORY_PID,
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", _companyId
				).put(
					"enabled", cookiesPreferenceHandlingEnabled
				).put(
					"globalPrivacyControlEnabled", globalPrivacyControlEnabled
				).build());
	}

	private static final String _SCOPED_FACTORY_PID =
		CookiesPreferenceHandlingConfiguration.class.getName() + ".scoped";

	private String _companyConfigurationPid;
	private long _companyId;

	@Inject
	private GlobalPrivacyControlProvider _globalPrivacyControlProvider;

	private Group _group;
	private String _groupConfigurationPid;
	private Layout _layout;

}
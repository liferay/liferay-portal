/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.configuration.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;
import com.liferay.segments.configuration.SegmentsConfiguration;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SegmentsConfigurationProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetCompanyConfigurationURL() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setAttribute(
			WebKeys.USER_ID, TestPropsValues.getUserId());

		String configurationURL =
			_segmentsConfigurationProvider.getCompanyConfigurationURL(
				mockHttpServletRequest);

		Assert.assertTrue(
			configurationURL.contains(
				"factoryPid=com.liferay.segments.configuration." +
					"SegmentsCompanyConfiguration"));
	}

	@Test
	public void testGetCompanyConfigurationURLWithoutPermission()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		User user = UserTestUtil.addUser(TestPropsValues.getGroupId());

		mockHttpServletRequest.setAttribute(WebKeys.USER_ID, user.getUserId());

		Assert.assertNull(
			_segmentsConfigurationProvider.getCompanyConfigurationURL(
				mockHttpServletRequest));

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testGetConfigurationURL() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setAttribute(
			WebKeys.USER_ID, TestPropsValues.getUserId());

		String configurationURL =
			_segmentsConfigurationProvider.getConfigurationURL(
				mockHttpServletRequest);

		Assert.assertTrue(
			configurationURL.contains(
				"factoryPid=com.liferay.segments.configuration." +
					"SegmentsConfiguration"));
	}

	@Test
	public void testGetConfigurationURLWithoutPermission() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		User user = UserTestUtil.addUser(TestPropsValues.getGroupId());

		mockHttpServletRequest.setAttribute(WebKeys.USER_ID, user.getUserId());

		Assert.assertNull(
			_segmentsConfigurationProvider.getConfigurationURL(
				mockHttpServletRequest));

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testIsRoleSegmentationEnabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).build())) {

			Assert.assertTrue(
				_segmentsConfigurationProvider.isRoleSegmentationEnabled());
		}
	}

	@Test
	public void testIsRoleSegmentationEnabledWithCompanyId() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"roleSegmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				Assert.assertTrue(
					_segmentsConfigurationProvider.isRoleSegmentationEnabled(
						TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsRoleSegmentationEnabledWithCompanyIdAndCompanyConfigurationDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).build())) {

			Configuration configuration =
				_configurationAdmin.createFactoryConfiguration(
					SegmentsCompanyConfiguration.class.getName() + ".scoped",
					StringPool.QUESTION);

			configuration.update(
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", TestPropsValues.getCompanyId()
				).put(
					"roleSegmentationEnabled", false
				).build());

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"roleSegmentationEnabled", false
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				Assert.assertFalse(
					_segmentsConfigurationProvider.isRoleSegmentationEnabled(
						TestPropsValues.getCompanyId()));
			}
			finally {
				configuration.delete();
			}
		}
	}

	@Test
	public void testIsRoleSegmentationEnabledWithCompanyIdAndCompanyConfigurationEnabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"roleSegmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				Assert.assertTrue(
					_segmentsConfigurationProvider.isRoleSegmentationEnabled(
						TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsRoleSegmentationEnabledWithCompanyIdAndRoleSegmentationDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", false
					).build())) {

			Assert.assertFalse(
				_segmentsConfigurationProvider.isRoleSegmentationEnabled(
					TestPropsValues.getCompanyId()));
		}
	}

	@Test
	public void testIsRoleSegmentationEnabledWithConfigurationDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", false
					).build())) {

			Assert.assertFalse(
				_segmentsConfigurationProvider.isRoleSegmentationEnabled());
		}
	}

	@Test
	public void testIsSegmentationEnabled() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			Assert.assertTrue(
				_segmentsConfigurationProvider.isSegmentationEnabled());
		}
	}

	@Test
	public void testIsSegmentationEnabledWithCompanyId() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				Assert.assertTrue(
					_segmentsConfigurationProvider.isSegmentationEnabled(
						TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsSegmentationEnabledWithCompanyIdAndCompanyConfigurationDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			Configuration configuration =
				_configurationAdmin.createFactoryConfiguration(
					SegmentsCompanyConfiguration.class.getName() + ".scoped",
					StringPool.QUESTION);

			configuration.update(
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", TestPropsValues.getCompanyId()
				).put(
					"roleSegmentationEnabled", false
				).build());

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", false
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				Assert.assertFalse(
					_segmentsConfigurationProvider.isSegmentationEnabled(
						TestPropsValues.getCompanyId()));
			}
			finally {
				configuration.delete();
			}
		}
	}

	@Test
	public void testIsSegmentationEnabledWithCompanyIdAndCompanyConfigurationEnabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", true
					).build())) {

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							SegmentsCompanyConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"segmentationEnabled", true
							).build())) {

				_segmentsConfigurationProvider.
					clearSegmentsCompanyConfigurations();

				Assert.assertTrue(
					_segmentsConfigurationProvider.isSegmentationEnabled(
						TestPropsValues.getCompanyId()));
			}
		}
	}

	@Test
	public void testIsSegmentationEnabledWithCompanyIdAndSegmentationDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", false
					).build())) {

			Assert.assertFalse(
				_segmentsConfigurationProvider.isSegmentationEnabled(
					TestPropsValues.getCompanyId()));
		}
	}

	@Test
	public void testIsSegmentationEnabledWithConfigurationDisabled()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", false
					).build())) {

			Assert.assertFalse(
				_segmentsConfigurationProvider.isSegmentationEnabled());
		}
	}

	@Test
	public void testIsSegmentsCompanyConfigurationDefined() throws Exception {
		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				SegmentsCompanyConfiguration.class.getName() + ".scoped",
				StringPool.QUESTION);

		try {
			configuration.update(
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", TestPropsValues.getCompanyId()
				).put(
					"roleSegmentationEnabled", false
				).build());

			Assert.assertTrue(
				_segmentsConfigurationProvider.
					isSegmentsCompanyConfigurationDefined(
						TestPropsValues.getCompanyId()));
		}
		finally {
			configuration.delete();
		}
	}

	@Test
	public void testResetSegmentsCompanyConfiguration() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"segmentationEnabled", false
					).build())) {

			_segmentsConfigurationProvider.updateSegmentsCompanyConfiguration(
				TestPropsValues.getCompanyId(),
				new SegmentsCompanyConfiguration() {

					@Override
					public boolean roleSegmentationEnabled() {
						return false;
					}

					@Override
					public boolean segmentationEnabled() {
						return true;
					}

				});

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					"(service.pid=" +
						SegmentsCompanyConfiguration.class.getName() + "*)");

			Assert.assertEquals(
				Arrays.toString(configurations), 1, configurations.length);

			_segmentsConfigurationProvider.resetSegmentsCompanyConfiguration(
				TestPropsValues.getCompanyId());

			configurations = _configurationAdmin.listConfigurations(
				"(service.pid=" + SegmentsCompanyConfiguration.class.getName() +
					"*)");

			Assert.assertNull(configurations);
		}
	}

	@Test
	public void testUpdateSegmentsCompanyConfiguration() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					SegmentsConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).put(
						"segmentationEnabled", false
					).build())) {

			_segmentsConfigurationProvider.updateSegmentsCompanyConfiguration(
				TestPropsValues.getCompanyId(),
				new SegmentsCompanyConfiguration() {

					@Override
					public boolean roleSegmentationEnabled() {
						return true;
					}

					@Override
					public boolean segmentationEnabled() {
						return false;
					}

				});

			Assert.assertTrue(
				_segmentsConfigurationProvider.isRoleSegmentationEnabled());
			Assert.assertFalse(
				_segmentsConfigurationProvider.isSegmentationEnabled());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Inject
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.configuration.client.dto.v1_0.SiteConfiguration;
import com.liferay.headless.admin.configuration.client.problem.Problem;
import com.liferay.headless.admin.configuration.client.resource.v1_0.SiteConfigurationResource;
import com.liferay.headless.admin.configuration.test.configuration.TestConfiguration;
import com.liferay.headless.admin.configuration.test.configuration.TestFactoryConfiguration;
import com.liferay.headless.admin.configuration.test.util.ConfigurationTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-65399"), @FeatureFlag("LPS-155284")}
)
@RunWith(Arquillian.class)
public class SiteConfigurationResourceTest
	extends BaseSiteConfigurationResourceTestCase {

	@BeforeClass
	public static void setUpClass() {
		_safeCloseables.add(
			ReflectionTestUtil.invoke(
				_settingsLocatorHelper, "_registerConfigurationBeanClass",
				new Class<?>[] {Class.class}, TestConfiguration.class));
		_safeCloseables.add(
			ReflectionTestUtil.invoke(
				_settingsLocatorHelper, "_registerConfigurationBeanClass",
				new Class<?>[] {Class.class}, TestFactoryConfiguration.class));
	}

	@AfterClass
	public static void tearDownClass() {
		for (SafeCloseable safeCloseable : _safeCloseables) {
			safeCloseable.close();
		}
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_userSiteConfigurationResource = SiteConfigurationResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Override
	@Test
	public void testGetSiteSiteConfiguration() throws Exception {
		super.testGetSiteSiteConfiguration();

		_testGetSiteSiteConfigurationFromConfigurationScreen();
		_testGetSiteSiteConfigurationWithoutPermission();
		_testGetSiteSiteConfigurationWithPasswordKey();
	}

	@Override
	@Test
	public void testPostSiteSiteConfiguration() throws Exception {
		super.testPostSiteSiteConfiguration();

		SiteConfiguration randomSiteConfiguration =
			_randomSiteConfigurationFromConfigurationScreen();

		SiteConfiguration postSiteConfiguration =
			testPostSiteSiteConfiguration_addSiteConfiguration(
				randomSiteConfiguration);

		assertEquals(randomSiteConfiguration, postSiteConfiguration);
		assertValid(postSiteConfiguration);
	}

	@Override
	@Test
	public void testPutSiteSiteConfiguration() throws Exception {
		super.testPutSiteSiteConfiguration();

		SiteConfiguration postSiteConfiguration =
			siteConfigurationResource.postSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(),
				_randomSiteConfigurationFromConfigurationScreen());

		SiteConfiguration randomSiteConfiguration =
			_randomSiteConfigurationFromConfigurationScreen();

		SiteConfiguration putSiteConfiguration =
			siteConfigurationResource.putSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(),
				postSiteConfiguration.getExternalReferenceCode(),
				randomSiteConfiguration);

		assertEquals(randomSiteConfiguration, putSiteConfiguration);
		assertValid(putSiteConfiguration);

		SiteConfiguration getSiteConfiguration =
			siteConfigurationResource.getSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(),
				putSiteConfiguration.getExternalReferenceCode());

		assertEquals(randomSiteConfiguration, getSiteConfiguration);
		assertValid(getSiteConfiguration);

		_testPutSiteSiteConfigurationWithoutPermission();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"properties"};
	}

	@Override
	protected SiteConfiguration randomSiteConfiguration() throws Exception {
		return new SiteConfiguration() {
			{
				externalReferenceCode =
					ConfigurationTestUtil.TEST_FACTORY_CONFIGURATION_PID;
				properties =
					ConfigurationTestUtil.
						getRandomTestFactoryConfigurationProperties(
							"groupKey",
							StringBundler.concat(
								testCompany.getWebId(), "--",
								testGroup.getGroupKey()));
			}
		};
	}

	@Override
	protected SiteConfiguration
			testGetSiteSiteConfiguration_addSiteConfiguration()
		throws Exception {

		return siteConfigurationResource.postSiteSiteConfiguration(
			testGroup.getExternalReferenceCode(),
			new SiteConfiguration() {
				{
					externalReferenceCode =
						ConfigurationTestUtil.TEST_CONFIGURATION_PID;
					properties =
						ConfigurationTestUtil.
							getRandomTestConfigurationProperties(
								"groupKey",
								StringBundler.concat(
									testCompany.getWebId(), "--",
									testGroup.getGroupKey()));
				}
			});
	}

	@Override
	protected SiteConfiguration
			testPostSiteSiteConfiguration_addSiteConfiguration(
				SiteConfiguration siteConfiguration)
		throws Exception {

		return siteConfigurationResource.postSiteSiteConfiguration(
			testGroup.getExternalReferenceCode(), siteConfiguration);
	}

	private SiteConfiguration _randomSiteConfigurationFromConfigurationScreen()
		throws Exception {

		return new SiteConfiguration() {
			{
				externalReferenceCode = "group-configuration-key";
				properties =
					ConfigurationTestUtil.
						getRandomConfigurationScreenProperties(
							"groupKey",
							StringBundler.concat(
								testCompany.getWebId(), "--",
								testGroup.getGroupKey()));
			}
		};
	}

	private void _testGetSiteSiteConfigurationFromConfigurationScreen()
		throws Exception {

		SiteConfiguration siteConfiguration =
			_randomSiteConfigurationFromConfigurationScreen();

		try {
			siteConfigurationResource.getSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(),
				siteConfiguration.getExternalReferenceCode());
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}

		SiteConfiguration postSiteConfiguration =
			siteConfigurationResource.postSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(), siteConfiguration);

		SiteConfiguration getSiteConfiguration =
			siteConfigurationResource.getSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(),
				postSiteConfiguration.getExternalReferenceCode());

		assertEquals(postSiteConfiguration, getSiteConfiguration);
		assertValid(getSiteConfiguration);
	}

	private void _testGetSiteSiteConfigurationWithoutPermission()
		throws Exception {

		try {
			_userSiteConfigurationResource.getSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(),
				ConfigurationTestUtil.TEST_CONFIGURATION_PID);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteSiteConfigurationWithPasswordKey()
		throws Exception {

		PropsUtil.set(
			PropsKeys.MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES, "true");

		SiteConfiguration siteConfiguration =
			testGetSiteSiteConfiguration_addSiteConfiguration();

		siteConfiguration = siteConfigurationResource.getSiteSiteConfiguration(
			testGroup.getExternalReferenceCode(),
			siteConfiguration.getExternalReferenceCode());

		Map<String, Object> properties = siteConfiguration.getProperties();

		Assert.assertNotNull(properties.get("passwordStringKey"));

		PropsUtil.set(
			PropsKeys.MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES, "false");

		siteConfiguration = siteConfigurationResource.getSiteSiteConfiguration(
			testGroup.getExternalReferenceCode(),
			siteConfiguration.getExternalReferenceCode());

		properties = siteConfiguration.getProperties();

		Assert.assertNull(properties.get("passwordStringKey"));
	}

	private void _testPutSiteSiteConfigurationWithoutPermission()
		throws Exception {

		SiteConfiguration siteConfiguration = randomSiteConfiguration();

		try {
			_userSiteConfigurationResource.putSiteSiteConfiguration(
				testGroup.getExternalReferenceCode(),
				siteConfiguration.getExternalReferenceCode(),
				siteConfiguration);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private static final List<SafeCloseable> _safeCloseables =
		new ArrayList<>();

	@Inject
	private static SettingsLocatorHelper _settingsLocatorHelper;

	private SiteConfigurationResource _userSiteConfigurationResource;

}
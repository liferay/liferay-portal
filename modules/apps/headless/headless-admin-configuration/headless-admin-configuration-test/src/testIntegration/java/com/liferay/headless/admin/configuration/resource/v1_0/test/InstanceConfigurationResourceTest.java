/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.configuration.client.dto.v1_0.InstanceConfiguration;
import com.liferay.headless.admin.configuration.client.pagination.Page;
import com.liferay.headless.admin.configuration.client.pagination.Pagination;
import com.liferay.headless.admin.configuration.client.problem.Problem;
import com.liferay.headless.admin.configuration.client.resource.v1_0.InstanceConfigurationResource;
import com.liferay.headless.admin.configuration.test.configuration.TestConfiguration;
import com.liferay.headless.admin.configuration.test.configuration.TestFactoryConfiguration;
import com.liferay.headless.admin.configuration.test.util.ConfigurationTestUtil;
import com.liferay.petra.lang.SafeCloseable;
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
public class InstanceConfigurationResourceTest
	extends BaseInstanceConfigurationResourceTestCase {

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

		_userInstanceConfigurationResource =
			InstanceConfigurationResource.builder(
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
	public void testGetInstanceConfiguration() throws Exception {
		super.testGetInstanceConfiguration();

		_testGetInstanceConfigurationFromConfigurationScreen();
		_testGetInstanceConfigurationWithoutPermission();
		_testGetInstanceConfigurationWithPasswordKey();
	}

	@Override
	@Test
	public void testGetInstanceConfigurationsPage() throws Exception {
		Page<InstanceConfiguration> page =
			instanceConfigurationResource.getInstanceConfigurationsPage(
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		InstanceConfiguration instanceConfiguration1 =
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				randomInstanceConfiguration());
		InstanceConfiguration instanceConfiguration2 =
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				randomInstanceConfiguration());

		page = instanceConfigurationResource.getInstanceConfigurationsPage(
			Pagination.of(1, (int)totalCount + 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			instanceConfiguration1,
			(List<InstanceConfiguration>)page.getItems());
		assertContains(
			instanceConfiguration2,
			(List<InstanceConfiguration>)page.getItems());
		assertValid(
			page, testGetInstanceConfigurationsPage_getExpectedActions());
	}

	@Override
	@Test
	public void testPostInstanceConfiguration() throws Exception {
		super.testPostInstanceConfiguration();

		InstanceConfiguration randomInstanceConfiguration =
			_randomInstanceConfigurationFromConfigurationScreen();

		InstanceConfiguration postInstanceConfiguration =
			testPostInstanceConfiguration_addInstanceConfiguration(
				randomInstanceConfiguration);

		assertEquals(randomInstanceConfiguration, postInstanceConfiguration);
		assertValid(postInstanceConfiguration);
	}

	@Override
	@Test
	public void testPutInstanceConfiguration() throws Exception {
		super.testPutInstanceConfiguration();

		InstanceConfiguration postInstanceConfiguration =
			instanceConfigurationResource.postInstanceConfiguration(
				_randomInstanceConfigurationFromConfigurationScreen());

		InstanceConfiguration randomInstanceConfiguration =
			_randomInstanceConfigurationFromConfigurationScreen();

		InstanceConfiguration putInstanceConfiguration =
			instanceConfigurationResource.putInstanceConfiguration(
				postInstanceConfiguration.getExternalReferenceCode(),
				randomInstanceConfiguration);

		assertEquals(randomInstanceConfiguration, putInstanceConfiguration);
		assertValid(putInstanceConfiguration);

		InstanceConfiguration getInstanceConfiguration =
			instanceConfigurationResource.getInstanceConfiguration(
				putInstanceConfiguration.getExternalReferenceCode());

		assertEquals(randomInstanceConfiguration, getInstanceConfiguration);
		assertValid(getInstanceConfiguration);

		_testPutInstanceConfigurationWithoutPermission();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"properties"};
	}

	@Override
	protected InstanceConfiguration randomInstanceConfiguration()
		throws Exception {

		return new InstanceConfiguration() {
			{
				externalReferenceCode =
					ConfigurationTestUtil.TEST_FACTORY_CONFIGURATION_PID;
				properties =
					ConfigurationTestUtil.
						getRandomTestFactoryConfigurationProperties(
							"companyWebId", testCompany.getWebId());
			}
		};
	}

	@Override
	protected InstanceConfiguration
			testGetInstanceConfiguration_addInstanceConfiguration()
		throws Exception {

		return instanceConfigurationResource.postInstanceConfiguration(
			new InstanceConfiguration() {
				{
					externalReferenceCode =
						ConfigurationTestUtil.TEST_CONFIGURATION_PID;
					properties =
						ConfigurationTestUtil.
							getRandomTestConfigurationProperties(
								"companyWebId", testCompany.getWebId());
				}
			});
	}

	@Override
	protected InstanceConfiguration
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				InstanceConfiguration instanceConfiguration)
		throws Exception {

		return instanceConfigurationResource.postInstanceConfiguration(
			instanceConfiguration);
	}

	@Override
	protected InstanceConfiguration
			testPostInstanceConfiguration_addInstanceConfiguration(
				InstanceConfiguration instanceConfiguration)
		throws Exception {

		return instanceConfigurationResource.postInstanceConfiguration(
			instanceConfiguration);
	}

	@Override
	protected InstanceConfiguration
			testPutInstanceConfiguration_addInstanceConfiguration()
		throws Exception {

		return instanceConfigurationResource.postInstanceConfiguration(
			randomInstanceConfiguration());
	}

	private InstanceConfiguration
			_randomInstanceConfigurationFromConfigurationScreen()
		throws Exception {

		return new InstanceConfiguration() {
			{
				externalReferenceCode = "company-configuration-key";
				properties =
					ConfigurationTestUtil.
						getRandomConfigurationScreenProperties(
							"companyWebId", testCompany.getWebId());
			}
		};
	}

	private void _testGetInstanceConfigurationFromConfigurationScreen()
		throws Exception {

		InstanceConfiguration instanceConfiguration =
			_randomInstanceConfigurationFromConfigurationScreen();

		try {
			instanceConfigurationResource.getInstanceConfiguration(
				instanceConfiguration.getExternalReferenceCode());
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}

		InstanceConfiguration postInstanceConfiguration =
			instanceConfigurationResource.postInstanceConfiguration(
				instanceConfiguration);

		InstanceConfiguration getInstanceConfiguration =
			instanceConfigurationResource.getInstanceConfiguration(
				postInstanceConfiguration.getExternalReferenceCode());

		assertEquals(postInstanceConfiguration, getInstanceConfiguration);
		assertValid(getInstanceConfiguration);
	}

	private void _testGetInstanceConfigurationWithoutPermission()
		throws Exception {

		try {
			_userInstanceConfigurationResource.getInstanceConfiguration(
				ConfigurationTestUtil.TEST_CONFIGURATION_PID);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetInstanceConfigurationWithPasswordKey()
		throws Exception {

		PropsUtil.set(
			PropsKeys.MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES, "true");

		InstanceConfiguration instanceConfiguration =
			testGetInstanceConfiguration_addInstanceConfiguration();

		instanceConfiguration =
			instanceConfigurationResource.getInstanceConfiguration(
				instanceConfiguration.getExternalReferenceCode());

		Map<String, Object> properties = instanceConfiguration.getProperties();

		Assert.assertNotNull(properties.get("passwordStringKey"));

		PropsUtil.set(
			PropsKeys.MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES, "false");

		instanceConfiguration =
			instanceConfigurationResource.getInstanceConfiguration(
				instanceConfiguration.getExternalReferenceCode());

		properties = instanceConfiguration.getProperties();

		Assert.assertNull(properties.get("passwordStringKey"));
	}

	private void _testPutInstanceConfigurationWithoutPermission()
		throws Exception {

		InstanceConfiguration instanceConfiguration =
			randomInstanceConfiguration();

		try {
			_userInstanceConfigurationResource.putInstanceConfiguration(
				instanceConfiguration.getExternalReferenceCode(),
				instanceConfiguration);

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

	private InstanceConfigurationResource _userInstanceConfigurationResource;

}
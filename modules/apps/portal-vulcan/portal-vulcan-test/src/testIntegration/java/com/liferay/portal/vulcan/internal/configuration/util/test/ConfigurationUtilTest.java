/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class ConfigurationUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company1 = CompanyTestUtil.addCompany();
		_company2 = CompanyTestUtil.addCompany();
	}

	@After
	public void tearDown() throws Exception {
		for (Configuration configuration : _configurations) {
			configuration.delete();
		}

		_configurations.clear();
	}

	@Test
	public void testGetApplicationsWhenCompanyRESTEnabledIsFalse()
		throws Exception {

		_createCompanyConfiguration(_company1, _COMPANY_FACTORY_PID, false);

		Assert.assertFalse(_hasApplication(_company1));
	}

	@Test
	public void testGetApplicationsWhenCompanyRESTEnabledIsFalseForAnotherCompany()
		throws Exception {

		boolean applicationPresent = _hasApplication(_company1);

		_createCompanyConfiguration(_company2, _COMPANY_FACTORY_PID, false);

		Assert.assertEquals(applicationPresent, _hasApplication(_company1));
	}

	@Test
	public void testGetApplicationsWhenCompanyRESTEnabledIsTrueOverridingSystemRESTEnabledIsFalse()
		throws Exception {

		_createSystemConfiguration(false);

		_createCompanyConfiguration(_company1, _COMPANY_FACTORY_PID, true);

		Assert.assertTrue(_hasApplication(_company1));
	}

	@Test
	public void testGetApplicationsWhenScopedCompanyRESTEnabledIsFalse()
		throws Exception {

		_createCompanyConfiguration(
			_company1, _COMPANY_FACTORY_PID + ".scoped", false);

		Assert.assertFalse(_hasApplication(_company1));
	}

	@Test
	public void testGetApplicationsWhenSystemRESTEnabledIsFalse()
		throws Exception {

		_createSystemConfiguration(false);

		Assert.assertFalse(_hasApplication(_company1));
	}

	private void _createCompanyConfiguration(
			Company company, String factoryPid, boolean restEnabled)
		throws Exception {

		_createConfiguration(
			factoryPid,
			HashMapDictionaryBuilder.<String, Object>put(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				company.getCompanyId()
			).put(
				"path", _PATH
			).put(
				"restEnabled", restEnabled
			).build());
	}

	private void _createConfiguration(
			String factoryPid, Dictionary<String, Object> properties)
		throws Exception {

		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				factoryPid, StringPool.QUESTION);

		configuration.update(properties);

		_configurations.add(configuration);
	}

	private void _createSystemConfiguration(boolean restEnabled)
		throws Exception {

		_createConfiguration(
			"com.liferay.portal.vulcan.internal.configuration." +
				"VulcanConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"path", _PATH
			).put(
				"restEnabled", restEnabled
			).build());
	}

	private boolean _hasApplication(Company company) {
		for (HeadlessApplicationProvider.Application application :
				_headlessApplicationProvider.getApplications(
					company.getCompanyId())) {

			if (_PATH.equals(application.getBasePath())) {
				return true;
			}
		}

		return false;
	}

	private static final String _COMPANY_FACTORY_PID =
		"com.liferay.portal.vulcan.internal.configuration." +
			"VulcanCompanyConfiguration";

	private static final String _PATH = "/headless-admin-user";

	private static Company _company1;
	private static Company _company2;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private final List<Configuration> _configurations = new ArrayList<>();

	@Inject
	private HeadlessApplicationProvider _headlessApplicationProvider;

}
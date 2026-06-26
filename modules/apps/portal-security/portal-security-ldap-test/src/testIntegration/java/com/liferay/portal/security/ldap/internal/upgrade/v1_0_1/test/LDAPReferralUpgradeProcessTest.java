/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.upgrade.v1_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.ldap.SafePortalLDAP;
import com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Dictionary;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class LDAPReferralUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.portal.security.ldap.internal.upgrade.v1_0_1." +
				"LDAPReferralUpgradeProcess");

		Bundle bundle = FrameworkUtil.getBundle(
			LDAPReferralUpgradeProcessTest.class);

		_bundleContext = bundle.getBundleContext();

		ServiceReference<ConfigurationAdmin>
			configurationAdminServiceReference =
				_bundleContext.getServiceReference(ConfigurationAdmin.class);

		_configurationAdmin = _bundleContext.getService(
			configurationAdminServiceReference);

		ServiceReference<SafePortalLDAP> serviceReference =
			_bundleContext.getServiceReference(SafePortalLDAP.class);

		_componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				serviceReference.getBundle(),
				"com.liferay.portal.security.ldap.internal.configuration." +
					"LDAPConfigurationListener");

		_enabled = _serviceComponentRuntime.isComponentEnabled(
			_componentDescriptionDTO);

		if (_enabled) {
			Promise<?> promise = _serviceComponentRuntime.disableComponent(
				_componentDescriptionDTO);

			promise.getValue();
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (_enabled) {
			Promise<?> promise = _serviceComponentRuntime.enableComponent(
				_componentDescriptionDTO);

			promise.getValue();
		}
	}

	@After
	public void tearDown() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=" + SystemLDAPConfiguration.class.getName() +
				")");

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		for (Configuration configuration : configurations) {
			configuration.delete();
		}
	}

	@Test
	public void testUpgradeThrowsReferral() throws Exception {
		long followCompanyId = RandomTestUtil.randomLong();

		_createConfiguration(followCompanyId, LDAPConstants.REFERRAL_FOLLOW);

		long ignoreCompanyId = RandomTestUtil.randomLong();

		_createConfiguration(ignoreCompanyId, LDAPConstants.REFERRAL_IGNORE);

		long legacyThrowsCompanyId = RandomTestUtil.randomLong();

		_createConfiguration(legacyThrowsCompanyId, "throws");

		long throwCompanyId = RandomTestUtil.randomLong();

		_createConfiguration(throwCompanyId, LDAPConstants.REFERRAL_THROW);

		_upgradeProcess.upgrade();

		Assert.assertEquals(
			LDAPConstants.REFERRAL_FOLLOW, _getReferral(followCompanyId));
		Assert.assertEquals(
			LDAPConstants.REFERRAL_IGNORE, _getReferral(ignoreCompanyId));
		Assert.assertEquals(
			LDAPConstants.REFERRAL_THROW, _getReferral(legacyThrowsCompanyId));
		Assert.assertEquals(
			LDAPConstants.REFERRAL_THROW, _getReferral(throwCompanyId));
	}

	private void _createConfiguration(long companyId, String referral)
		throws Exception {

		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				SystemLDAPConfiguration.class.getName(), StringPool.QUESTION);

		configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				LDAPConstants.COMPANY_ID, companyId
			).put(
				LDAPConstants.REFERRAL, referral
			).build());
	}

	private String _getReferral(long companyId) throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getScopedFilterString(
				companyId, SystemLDAPConfiguration.class.getName(),
				ExtendedObjectClassDefinition.Scope.COMPANY, companyId));

		if (ArrayUtil.isEmpty(configurations)) {
			return null;
		}

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		return (String)properties.get(LDAPConstants.REFERRAL);
	}

	private static BundleContext _bundleContext;
	private static ComponentDescriptionDTO _componentDescriptionDTO;
	private static ConfigurationAdmin _configurationAdmin;
	private static boolean _enabled;

	@Inject
	private static ServiceComponentRuntime _serviceComponentRuntime;

	private static UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.portal.security.ldap.internal.upgrade.registry.LDAPServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class FIPSSessionConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			FIPSSessionConfiguration.class.getName());
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Company company = _companyLocalService.getCompany(
				TestPropsValues.getCompanyId());

			_portalInstanceLifecycleListener.portalInstanceRegistered(company);

			UserTestUtil.setUser(TestPropsValues.getUser());

			Dictionary<String, Object> properties = _getProperties(
				24, FIPSConstants.TIME_UNIT_HOURS, 30,
				FIPSConstants.TIME_UNIT_MINUTES);

			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(), properties));

			Role role = _roleLocalService.fetchRole(
				company.getCompanyId(), RoleConstants.CRYPTO_OFFICER);

			User user = UserTestUtil.addUser(company);

			_roleLocalService.addUserRoles(
				user.getUserId(), new long[] {role.getRoleId()});

			UserTestUtil.setUser(user);

			ConfigurationTestUtil.saveConfiguration(
				FIPSSessionConfiguration.class.getName(), properties);

			FIPSSessionConfiguration fipsSessionConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FIPSSessionConfiguration.class,
					TestPropsValues.getCompanyId());

			Assert.assertEquals(24, fipsSessionConfiguration.maximumAge());
			Assert.assertEquals(30, fipsSessionConfiguration.idleTimeout());
			Assert.assertEquals(
				FIPSConstants.TIME_UNIT_HOURS,
				fipsSessionConfiguration.maximumAgeTimeUnit());
			Assert.assertEquals(
				FIPSConstants.TIME_UNIT_MINUTES,
				fipsSessionConfiguration.idleTimeoutTimeUnit());

			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						-1, FIPSConstants.TIME_UNIT_MINUTES, 15,
						FIPSConstants.TIME_UNIT_MINUTES)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						0, FIPSConstants.TIME_UNIT_MINUTES, 15,
						FIPSConstants.TIME_UNIT_MINUTES)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						30, FIPSConstants.TIME_UNIT_DAYS, 13,
						FIPSConstants.TIME_UNIT_HOURS)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						31, FIPSConstants.TIME_UNIT_DAYS, 15,
						FIPSConstants.TIME_UNIT_MINUTES)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						721, FIPSConstants.TIME_UNIT_HOURS, 15,
						FIPSConstants.TIME_UNIT_MINUTES)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						43201, FIPSConstants.TIME_UNIT_MINUTES, 15,
						FIPSConstants.TIME_UNIT_MINUTES)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						43200, FIPSConstants.TIME_UNIT_MINUTES, -1,
						FIPSConstants.TIME_UNIT_MINUTES)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						43200, FIPSConstants.TIME_UNIT_MINUTES, 0,
						FIPSConstants.TIME_UNIT_MINUTES)));
			Assert.assertThrows(
				ConfigurationModelListenerException.class,
				() -> ConfigurationTestUtil.saveConfiguration(
					FIPSSessionConfiguration.class.getName(),
					_getProperties(
						43200, FIPSConstants.TIME_UNIT_MINUTES, 721,
						FIPSConstants.TIME_UNIT_MINUTES)));
		}
	}

	private Dictionary<String, Object> _getProperties(
		int maximumAge, String maximumAgeTimeUnit, int idleTimeout,
		String idleTimeoutTimeUnit) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"idleTimeout", idleTimeout
		).put(
			"idleTimeoutTimeUnit", idleTimeoutTimeUnit
		).put(
			"maximumAge", maximumAge
		).put(
			"maximumAgeTimeUnit", maximumAgeTimeUnit
		).build();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject(
		filter = "component.name=com.liferay.portal.security.fips.internal.instance.lifecycle.FIPSPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private RoleLocalService _roleLocalService;

}
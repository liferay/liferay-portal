/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Christian Moura
 */
public class AuditConfigurationUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@FeatureFlag("LPD-6417")
	@Test
	public void testIsEnabled() {
		_testIsEnabled(CompanyConstants.SYSTEM, false);
		_testIsEnabled(CompanyConstants.SYSTEM, true);
		_testIsEnabled(RandomTestUtil.randomLong(), false);
		_testIsEnabled(RandomTestUtil.randomLong(), true);

		_testIsEnabledWhenConfigurationIsUnavailable(
			CompanyConstants.SYSTEM, new ConfigurationException());
		_testIsEnabledWhenConfigurationIsUnavailable(
			CompanyConstants.SYSTEM, new RuntimeException());
		_testIsEnabledWhenConfigurationIsUnavailable(
			RandomTestUtil.randomLong(), new ConfigurationException());
		_testIsEnabledWhenConfigurationIsUnavailable(
			RandomTestUtil.randomLong(), new RuntimeException());
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testIsEnabledWhenFeatureFlagIsDisabled() {
		_testIsEnabledWhenFeatureFlagIsDisabled(false);
		_testIsEnabledWhenFeatureFlagIsDisabled(true);
	}

	private AuditConfiguration _createAuditConfiguration(boolean enabled) {
		AuditConfiguration auditConfiguration = Mockito.mock(
			AuditConfiguration.class);

		Mockito.when(
			auditConfiguration.enabled()
		).thenReturn(
			enabled
		);

		return auditConfiguration;
	}

	private void _testIsEnabled(long companyId, boolean enabled) {
		try (MockedStatic<ConfigurationProviderUtil>
				configurationProviderUtilMockedStatic = Mockito.mockStatic(
					ConfigurationProviderUtil.class)) {

			AuditConfiguration auditConfiguration = _createAuditConfiguration(
				enabled);

			if (companyId == CompanyConstants.SYSTEM) {
				configurationProviderUtilMockedStatic.when(
					() -> ConfigurationProviderUtil.getSystemConfiguration(
						AuditConfiguration.class)
				).thenReturn(
					auditConfiguration
				);
			}
			else {
				configurationProviderUtilMockedStatic.when(
					() -> ConfigurationProviderUtil.getCompanyConfiguration(
						AuditConfiguration.class, companyId)
				).thenReturn(
					auditConfiguration
				);
			}

			Assert.assertEquals(
				enabled, AuditConfigurationUtil.isEnabled(companyId));
		}
	}

	private void _testIsEnabledWhenConfigurationIsUnavailable(
		long companyId, Exception exception) {

		try (MockedStatic<ConfigurationProviderUtil>
				configurationProviderUtilMockedStatic = Mockito.mockStatic(
					ConfigurationProviderUtil.class)) {

			if (companyId == CompanyConstants.SYSTEM) {
				configurationProviderUtilMockedStatic.when(
					() -> ConfigurationProviderUtil.getSystemConfiguration(
						AuditConfiguration.class)
				).thenThrow(
					exception
				);
			}
			else {
				configurationProviderUtilMockedStatic.when(
					() -> ConfigurationProviderUtil.getCompanyConfiguration(
						AuditConfiguration.class, companyId)
				).thenThrow(
					exception
				);
			}

			Assert.assertTrue(AuditConfigurationUtil.isEnabled(companyId));
		}
	}

	private void _testIsEnabledWhenFeatureFlagIsDisabled(boolean enabled) {
		try (MockedStatic<ConfigurationProviderUtil>
				configurationProviderUtilMockedStatic = Mockito.mockStatic(
					ConfigurationProviderUtil.class)) {

			AuditConfiguration companyAuditConfiguration =
				_createAuditConfiguration(!enabled);
			AuditConfiguration systemAuditConfiguration =
				_createAuditConfiguration(enabled);

			configurationProviderUtilMockedStatic.when(
				() -> ConfigurationProviderUtil.getCompanyConfiguration(
					Mockito.eq(AuditConfiguration.class), Mockito.anyLong())
			).thenReturn(
				companyAuditConfiguration
			);

			configurationProviderUtilMockedStatic.when(
				() -> ConfigurationProviderUtil.getSystemConfiguration(
					AuditConfiguration.class)
			).thenReturn(
				systemAuditConfiguration
			);

			Assert.assertEquals(
				enabled,
				AuditConfigurationUtil.isEnabled(RandomTestUtil.randomLong()));
		}
	}

}
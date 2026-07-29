/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class AuditPortalSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetScope() throws Exception {
		Set<String> scopes = SetUtil.fromArray(
			ExtendedObjectClassDefinition.Scope.COMPANY.getValue(),
			ExtendedObjectClassDefinition.Scope.SYSTEM.getValue());

		Map<String, ConfigurationScreen> configurationScreens =
			_getConfigurationScreens();

		Assert.assertEquals(scopes, configurationScreens.keySet());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testIsVisible() throws Exception {
		Map<String, ConfigurationScreen> configurationScreens =
			_getConfigurationScreens();

		Assert.assertTrue(
			_isVisible(
				configurationScreens,
				ExtendedObjectClassDefinition.Scope.COMPANY));
		Assert.assertTrue(
			_isVisible(
				configurationScreens,
				ExtendedObjectClassDefinition.Scope.SYSTEM));
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testIsVisibleWhenFeatureFlagIsDisabled() throws Exception {
		Map<String, ConfigurationScreen> configurationScreens =
			_getConfigurationScreens();

		Assert.assertFalse(
			_isVisible(
				configurationScreens,
				ExtendedObjectClassDefinition.Scope.COMPANY));
		Assert.assertTrue(
			_isVisible(
				configurationScreens,
				ExtendedObjectClassDefinition.Scope.SYSTEM));
	}

	private Map<String, ConfigurationScreen> _getConfigurationScreens()
		throws Exception {

		Map<String, ConfigurationScreen> configurationScreens = new HashMap<>();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		for (ServiceReference<ConfigurationScreen> serviceReference :
				bundleContext.getServiceReferences(
					ConfigurationScreen.class, null)) {

			ConfigurationScreen configurationScreen = bundleContext.getService(
				serviceReference);

			String key = configurationScreen.getKey();

			if (key.startsWith(AuditConfiguration.class.getName())) {
				configurationScreens.put(
					configurationScreen.getScope(), configurationScreen);
			}
		}

		return configurationScreens;
	}

	private boolean _isVisible(
		Map<String, ConfigurationScreen> configurationScreens,
		ExtendedObjectClassDefinition.Scope scope) {

		ConfigurationScreen configurationScreen = configurationScreens.get(
			scope.getValue());

		return configurationScreen.isVisible();
	}

}
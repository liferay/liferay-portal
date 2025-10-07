/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.upgrade;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.internal.upgrade.v1_0_0.SamlKeyStorePropertiesUpgradeProcess;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Carlos Sierra
 */
public class SamlKeyStorePropertiesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpgradeWithEmptyProperty() throws Exception {
		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		PrefsProps prefsProps = Mockito.mock(PrefsProps.class);

		Mockito.when(
			prefsProps.getString("saml.keystore.manager.impl")
		).thenReturn(
			""
		);

		SamlKeyStorePropertiesUpgradeProcess
			samlKeyStorePropertiesUpgradeProcess =
				new SamlKeyStorePropertiesUpgradeProcess(
					configurationAdmin, prefsProps);

		samlKeyStorePropertiesUpgradeProcess.doUpgrade();

		Mockito.verifyNoInteractions(configurationAdmin);
	}

	@Test
	public void testUpgradeWithNullProperty() throws Exception {
		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		PrefsProps prefsProps = Mockito.mock(PrefsProps.class);

		Mockito.when(
			prefsProps.getString("saml.keystore.manager.impl")
		).thenReturn(
			null
		);

		SamlKeyStorePropertiesUpgradeProcess
			samlKeyStorePropertiesUpgradeProcess =
				new SamlKeyStorePropertiesUpgradeProcess(
					configurationAdmin, prefsProps);

		samlKeyStorePropertiesUpgradeProcess.doUpgrade();

		Mockito.verifyNoInteractions(configurationAdmin);
	}

	@Test
	public void testUpgradeWithProperty() throws Exception {
		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		Configuration configuration = Mockito.mock(Configuration.class);

		Mockito.when(
			configurationAdmin.getConfiguration(
				Mockito.anyString(), Mockito.eq(StringPool.QUESTION))
		).thenReturn(
			configuration
		);

		PrefsProps prefsProps = Mockito.mock(PrefsProps.class);

		String samlKeyStoreManagerImpl = RandomTestUtil.randomString();

		Mockito.when(
			prefsProps.getString("saml.keystore.manager.impl")
		).thenReturn(
			samlKeyStoreManagerImpl
		);

		SamlKeyStorePropertiesUpgradeProcess
			samlKeyStorePropertiesUpgradeProcess =
				new SamlKeyStorePropertiesUpgradeProcess(
					configurationAdmin, prefsProps);

		samlKeyStorePropertiesUpgradeProcess.doUpgrade();

		ConfigurationAdmin verifyConfigurationAdmin = Mockito.verify(
			configurationAdmin, Mockito.times(1));

		verifyConfigurationAdmin.getConfiguration(
			Mockito.eq(
				"com.liferay.saml.runtime.configuration." +
					"SamlKeyStoreManagerConfiguration"),
			Mockito.eq(StringPool.QUESTION));

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			"KeyStoreManager.target",
			"(component.name=" + samlKeyStoreManagerImpl + ")");

		Configuration verifyConfiguration = Mockito.verify(
			configuration, Mockito.times(1));

		verifyConfiguration.update(Mockito.eq(properties));
	}

}
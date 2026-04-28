/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.upgrade.v1_1_0.test;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.internal.upgrade.v1_1_0.LDAPServerCredentialEncryptionUpgradeProcess;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jorge García Jiménez
 */
public class LDAPServerCredentialEncryptionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_configurationAdmin = Mockito.mock(ConfigurationAdmin.class);

		_upgradeProcess = new LDAPServerCredentialEncryptionUpgradeProcess(
			_configurationAdmin);
	}

	@Test
	public void testDoUpgradeIsNoOpWhenNoConfigurations() throws Exception {
		Mockito.when(
			_configurationAdmin.listConfigurations(
				String.format(
					"(%s=%s)", ConfigurationAdmin.SERVICE_FACTORYPID,
					_CLASS_NAME))
		).thenReturn(
			null
		);

		ReflectionTestUtil.invoke(_upgradeProcess, "doUpgrade", null);

		Mockito.verify(
			_configurationAdmin
		).listConfigurations(
			String.format(
				"(%s=%s)", ConfigurationAdmin.SERVICE_FACTORYPID, _CLASS_NAME)
		);

		Mockito.verifyNoMoreInteractions(_configurationAdmin);
	}

	@Test
	public void testDoUpgradeResavesPlaintextCredential() throws Exception {
		Configuration configuration = Mockito.mock(Configuration.class);

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("securityCredential", "secret");

		Mockito.when(
			configuration.getProperties()
		).thenReturn(
			properties
		);

		Mockito.when(
			_configurationAdmin.listConfigurations(
				String.format(
					"(%s=%s)", ConfigurationAdmin.SERVICE_FACTORYPID,
					_CLASS_NAME))
		).thenReturn(
			new Configuration[] {configuration}
		);

		ReflectionTestUtil.invoke(_upgradeProcess, "doUpgrade", null);

		Mockito.verify(
			configuration
		).update(
			properties
		);
	}

	@Test
	public void testDoUpgradeSkipsAlreadyEncryptedCredential()
		throws Exception {

		Configuration configuration = Mockito.mock(Configuration.class);

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("securityCredential", "{ENC}cipher");

		Mockito.when(
			configuration.getProperties()
		).thenReturn(
			properties
		);

		Mockito.when(
			_configurationAdmin.listConfigurations(
				String.format(
					"(%s=%s)", ConfigurationAdmin.SERVICE_FACTORYPID,
					_CLASS_NAME))
		).thenReturn(
			new Configuration[] {configuration}
		);

		ReflectionTestUtil.invoke(_upgradeProcess, "doUpgrade", null);

		Mockito.verify(
			configuration, Mockito.never()
		).update(
			Mockito.<Dictionary<String, ?>>any()
		);
	}

	@Test
	public void testDoUpgradeSkipsNullProperties() throws Exception {
		Configuration configuration = Mockito.mock(Configuration.class);

		Mockito.when(
			configuration.getProperties()
		).thenReturn(
			null
		);

		Mockito.when(
			_configurationAdmin.listConfigurations(
				String.format(
					"(%s=%s)", ConfigurationAdmin.SERVICE_FACTORYPID,
					_CLASS_NAME))
		).thenReturn(
			new Configuration[] {configuration}
		);

		ReflectionTestUtil.invoke(_upgradeProcess, "doUpgrade", null);

		Mockito.verify(
			configuration, Mockito.never()
		).update(
			Mockito.<Dictionary<String, ?>>any()
		);
	}

	@Test
	public void testDoUpgradeSwallowsUpdateException() throws Exception {
		Configuration configuration = Mockito.mock(Configuration.class);

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("securityCredential", "secret");

		Mockito.when(
			configuration.getProperties()
		).thenReturn(
			properties
		);

		Mockito.when(
			configuration.getPid()
		).thenReturn(
			"pid"
		);

		Mockito.doThrow(
			new RuntimeException("nope")
		).when(
			configuration
		).update(
			Mockito.<Dictionary<String, ?>>any()
		);

		Mockito.when(
			_configurationAdmin.listConfigurations(
				String.format(
					"(%s=%s)", ConfigurationAdmin.SERVICE_FACTORYPID,
					_CLASS_NAME))
		).thenReturn(
			new Configuration[] {configuration}
		);

		ReflectionTestUtil.invoke(_upgradeProcess, "doUpgrade", null);
	}

	private static final String _CLASS_NAME =
		LDAPServerConfiguration.class.getName();

	private ConfigurationAdmin _configurationAdmin;
	private LDAPServerCredentialEncryptionUpgradeProcess _upgradeProcess;

}
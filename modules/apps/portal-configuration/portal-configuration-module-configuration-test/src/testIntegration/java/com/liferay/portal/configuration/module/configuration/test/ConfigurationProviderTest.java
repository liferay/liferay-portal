/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.module.configuration.test;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class ConfigurationProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_properties = new Hashtable<>();
	}

	@After
	public void tearDown() throws Exception {
		for (Configuration configuration : _configurations.values()) {
			if (configuration == null) {
				continue;
			}

			configuration.delete();
		}
	}

	@Test
	public void testDeleteCompanyConfiguration() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.COMPANY;

		_createFactoryConfiguration(null, _PID, scope, companyId);

		Assert.assertEquals(
			1, _getFactoryConfigurationsCount(_PID, scope, companyId));

		_configurationProvider.deleteCompanyConfiguration(
			TestConfiguration.class, companyId);

		Assert.assertEquals(
			0, _getFactoryConfigurationsCount(_PID, scope, companyId));
	}

	@Test
	public void testDeleteGroupConfiguration() throws Exception {
		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.GROUP;

		long groupId1 = RandomTestUtil.randomLong();
		long groupId2 = RandomTestUtil.randomLong();

		_createFactoryConfiguration(
			CompanyThreadLocal.getCompanyId(), _PID, scope, groupId1);
		_createFactoryConfiguration(
			CompanyThreadLocal.getCompanyId(), _PID, scope, groupId2);

		Assert.assertEquals(
			2, _getFactoryConfigurationsCount(_PID, scope, null));

		_configurationProvider.deleteGroupConfiguration(
			TestConfiguration.class, CompanyThreadLocal.getCompanyId(),
			groupId1);

		Assert.assertEquals(
			1, _getFactoryConfigurationsCount(_PID, scope, null));

		_configurationProvider.deleteGroupConfiguration(
			TestConfiguration.class, CompanyThreadLocal.getCompanyId(),
			groupId2);

		Assert.assertEquals(
			0, _getFactoryConfigurationsCount(_PID, scope, null));
	}

	@Test
	public void testDeletePortletInstanceConfiguration() throws Exception {
		String portletInstanceId = RandomTestUtil.randomString();

		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE;

		_createFactoryConfiguration(null, _PID, scope, portletInstanceId);

		Assert.assertEquals(
			1, _getFactoryConfigurationsCount(_PID, scope, portletInstanceId));

		_configurationProvider.deletePortletInstanceConfiguration(
			TestConfiguration.class, portletInstanceId);

		Assert.assertEquals(
			0, _getFactoryConfigurationsCount(_PID, scope, portletInstanceId));
	}

	@Test
	public void testDeleteSystemConfiguration() throws Exception {
		_createConfiguration(_PID);

		Assert.assertEquals(1, _getConfigurationsCount(_PID));

		_configurationProvider.deleteSystemConfiguration(
			TestConfiguration.class);

		Assert.assertEquals(0, _getConfigurationsCount(_PID));
	}

	@Test
	public void testSaveCompanyConfiguration() throws Exception {
		_properties.put("key1", "companyValue1");
		_properties.put("key2", "companyValue2");

		long companyId = RandomTestUtil.randomLong();

		_configurationProvider.saveCompanyConfiguration(
			TestConfiguration.class, companyId, _properties);

		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.COMPANY;

		Configuration configuration = _getFactoryConfiguration(
			null, _PID, scope, companyId);

		_assertFactoryPropertyValues(
			_properties, configuration.getProperties(), scope.getPropertyKey(),
			companyId);

		_configurationProvider.saveCompanyConfiguration(
			companyId, _PID, _properties);

		configuration = _getFactoryConfiguration(null, _PID, scope, companyId);

		_assertFactoryPropertyValues(
			_properties, configuration.getProperties(), scope.getPropertyKey(),
			companyId);
	}

	@Test
	public void testSaveGroupConfiguration() throws Exception {
		_testSaveGroupConfiguration();
		_testSaveGroupConfigurationWithMultipleCompanies();
	}

	@Test
	public void testSavePortletInstanceConfiguration() throws Exception {
		_properties.put("key1", "portletInstanceValue1");
		_properties.put("key2", "portletInstanceValue2");

		String portletInstanceId = RandomTestUtil.randomString();

		_configurationProvider.savePortletInstanceConfiguration(
			TestConfiguration.class, portletInstanceId, _properties);

		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE;

		Configuration configuration = _getFactoryConfiguration(
			null, _PID, scope, portletInstanceId);

		_assertFactoryPropertyValues(
			_properties, configuration.getProperties(), scope.getPropertyKey(),
			portletInstanceId);
	}

	@Test
	public void testSaveSystemConfiguration() throws Exception {
		_properties.put("key1", "systemValue1");
		_properties.put("key2", "systemValue2");

		_configurationProvider.saveSystemConfiguration(
			TestConfiguration.class, _properties);

		Configuration configuration = _getConfiguration(_PID);

		_assertPropertyValues(_properties, configuration.getProperties());
	}

	private void _assertFactoryPropertyValues(
		Dictionary<String, Object> properties,
		Dictionary<String, Object> configurationProperties, String expectedKey,
		Object expectedValue) {

		Assert.assertEquals(
			expectedValue, configurationProperties.get(expectedKey));

		_assertPropertyValues(properties, configurationProperties);
	}

	private void _assertPropertyValues(
		Dictionary<String, Object> properties,
		Dictionary<String, Object> configurationProperties) {

		Assert.assertNotNull(configurationProperties);

		for (Enumeration<String> enumeration = properties.keys();
			 enumeration.hasMoreElements();) {

			String key = enumeration.nextElement();

			Assert.assertEquals(
				properties.get(key), configurationProperties.get(key));
		}
	}

	private void _createConfiguration(String pid) throws Exception {
		_properties.put("key1", "value1");
		_properties.put("key2", "value2");

		ConfigurationTestUtil.saveConfiguration(
			_getConfiguration(pid), _properties);
	}

	private void _createFactoryConfiguration(
			Serializable companyId, String factoryPid,
			ExtendedObjectClassDefinition.Scope scope, Serializable scopePK)
		throws Exception {

		_properties.put(scope.getPropertyKey(), scopePK);

		if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
			_properties.put(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				companyId);
		}

		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				factoryPid + ".scoped", StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(configuration, _properties);
	}

	private Configuration _getConfiguration(String pid) throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			pid, StringPool.QUESTION);

		if (configuration != null) {
			_configurations.put(configuration.getPid(), configuration);
		}

		return configuration;
	}

	private int _getConfigurationsCount(String pid) throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, Constants.SERVICE_PID,
				StringPool.EQUAL, pid, StringPool.CLOSE_PARENTHESIS));

		if (configurations == null) {
			return 0;
		}

		return configurations.length;
	}

	private Configuration _getFactoryConfiguration(
			Serializable companyId, String factoryPid,
			ExtendedObjectClassDefinition.Scope scope, Serializable scopePK)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getScopedFilterString(
				companyId, factoryPid, scope, scopePK));

		if (configurations != null) {
			Configuration configuration = configurations[0];

			_configurations.put(configuration.getPid(), configuration);

			return configuration;
		}

		return null;
	}

	private int _getFactoryConfigurationsCount(
			String pid, ExtendedObjectClassDefinition.Scope scope,
			Serializable scopePK)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getScopedFilterString(
				CompanyThreadLocal.getCompanyId(), pid, scope, scopePK));

		if (configurations == null) {
			return 0;
		}

		return configurations.length;
	}

	private void _testSaveGroupConfiguration() throws Exception {
		_properties.put("key1", "groupValue1");
		_properties.put("key2", "groupValue2");

		long groupId1 = RandomTestUtil.randomLong();
		long groupId2 = RandomTestUtil.randomLong();

		_configurationProvider.saveGroupConfiguration(
			TestConfiguration.class, CompanyThreadLocal.getCompanyId(),
			groupId1, _properties);
		_configurationProvider.saveGroupConfiguration(
			TestConfiguration.class, CompanyThreadLocal.getCompanyId(),
			groupId2, new HashMapDictionary<>());

		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.GROUP;

		Configuration configuration = _getFactoryConfiguration(
			CompanyThreadLocal.getCompanyId(), _PID, scope, groupId1);

		_assertFactoryPropertyValues(
			_properties, configuration.getProperties(), scope.getPropertyKey(),
			groupId1);

		configuration = _getFactoryConfiguration(
			CompanyThreadLocal.getCompanyId(), _PID, scope, groupId2);

		_assertFactoryPropertyValues(
			new HashMapDictionary<>(), configuration.getProperties(),
			scope.getPropertyKey(), groupId2);

		_properties.put("key3", "groupValue3");

		_configurationProvider.saveGroupConfiguration(
			CompanyThreadLocal.getCompanyId(), groupId1, _PID, _properties);

		configuration = _getFactoryConfiguration(
			CompanyThreadLocal.getCompanyId(), _PID, scope, groupId1);

		_assertFactoryPropertyValues(
			_properties, configuration.getProperties(), scope.getPropertyKey(),
			groupId1);

		configuration = _getFactoryConfiguration(
			CompanyThreadLocal.getCompanyId(), _PID, scope, groupId2);

		_assertFactoryPropertyValues(
			new HashMapDictionary<>(), configuration.getProperties(),
			scope.getPropertyKey(), groupId2);
	}

	private void _testSaveGroupConfigurationWithMultipleCompanies()
		throws Exception {

		long groupId = RandomTestUtil.randomLong();

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		long companyId1 = RandomTestUtil.randomLong();

		_configurationProvider.saveGroupConfiguration(
			TestConfiguration.class, companyId1, groupId, properties);

		long companyId2 = RandomTestUtil.randomLong();

		_configurationProvider.saveGroupConfiguration(
			TestConfiguration.class, companyId2, groupId, properties);

		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.GROUP;

		Configuration configuration = _getFactoryConfiguration(
			companyId1, _PID, scope, groupId);

		properties.put(
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			companyId1);

		_assertFactoryPropertyValues(
			properties, configuration.getProperties(), scope.getPropertyKey(),
			groupId);

		configuration = _getFactoryConfiguration(
			companyId2, _PID, scope, groupId);

		properties.put(
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			companyId2);

		_assertFactoryPropertyValues(
			properties, configuration.getProperties(), scope.getPropertyKey(),
			groupId);
	}

	private static final String _PID = "test.pid";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	private final Map<String, Configuration> _configurations = new HashMap<>();
	private Dictionary<String, Object> _properties;

	@Meta.OCD(id = "test.pid")
	private interface TestConfiguration {

		@Meta.AD
		public String key1();

		@Meta.AD
		public String key2();

	}

}
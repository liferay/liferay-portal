/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Method;

import java.util.Dictionary;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class CSPOSGiCommandsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testResetContentSecurityPolicyConfiguration() throws Exception {
		Company company = CompanyTestUtil.addCompany(false);

		_testResetContentSecurityPolicyConfiguration(
			company.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", company.getCompanyId()
			).build(),
			ExtendedObjectClassDefinition.Scope.COMPANY);

		Group group = GroupTestUtil.addGroup();

		_testResetContentSecurityPolicyConfiguration(
			group.getGroupId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"groupId", group.getGroupId()
			).build(),
			ExtendedObjectClassDefinition.Scope.GROUP);

		_testResetContentSecurityPolicyConfiguration(
			CompanyConstants.SYSTEM, new HashMapDictionary<>(),
			ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	private void _createConfiguration(
			Dictionary<String, Object> properties,
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		properties = HashMapDictionaryBuilder.<String, Object>putAll(
			properties
		).put(
			"enabled", false
		).put(
			"excludedPaths", "/api"
		).put(
			"policy", "script-src 'unsafe-inline';"
		).build();

		if (scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM)) {
			ConfigurationTestUtil.saveConfiguration(
				_configurationAdmin.getConfiguration(
					_CLASS_NAME, StringPool.QUESTION),
				properties);

			return;
		}

		ConfigurationTestUtil.createFactoryConfiguration(
			_CLASS_NAME + ".scoped", properties);
	}

	private void _resetConfiguration(
			long id, ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Class<?> clazz = _osgiCommands.getClass();

		Method method = null;
		Object[] arguments = null;

		if (scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM)) {
			method = clazz.getMethod(_functionNames.get(scope));
		}
		else {
			method = clazz.getMethod(_functionNames.get(scope), long.class);
			arguments = new Object[] {id};
		}

		method.invoke(_osgiCommands, arguments);
	}

	private void _testResetContentSecurityPolicyConfiguration(
			long id, Dictionary<String, Object> properties,
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		_createConfiguration(properties, scope);

		_resetConfiguration(id, scope);

		String filterString = null;

		if (scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM)) {
			filterString = StringBundler.concat(
				"(&(service.pid=", _CLASS_NAME, "))");
		}
		else {
			filterString = StringBundler.concat(
				"(&(service.factoryPid=", _CLASS_NAME, ".scoped)(",
				scope.getPropertyKey(), "=", id, "))");
		}

		Assert.assertNull(_configurationAdmin.listConfigurations(filterString));
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.security.content.security.policy.internal." +
			"configuration.ContentSecurityPolicyConfiguration";

	@Inject(filter = "osgi.command.scope=csp")
	private static OSGiCommands _osgiCommands;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private final Map<ExtendedObjectClassDefinition.Scope, String>
		_functionNames = HashMapBuilder.put(
			ExtendedObjectClassDefinition.Scope.COMPANY,
			"resetCompanyConfiguration"
		).put(
			ExtendedObjectClassDefinition.Scope.GROUP, "resetGroupConfiguration"
		).put(
			ExtendedObjectClassDefinition.Scope.SYSTEM,
			"resetSystemConfiguration"
		).build();

}
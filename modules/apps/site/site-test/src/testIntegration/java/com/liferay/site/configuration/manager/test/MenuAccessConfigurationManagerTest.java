/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.configuration.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.configuration.MenuAccessConfiguration;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import java.util.Arrays;
import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class MenuAccessConfigurationManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupConfigurationTemporarySwapper =
			new GroupConfigurationTemporarySwapper(
				_group.getGroupId(), MenuAccessConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"showControlMenuByRole", true
				).build());
	}

	@After
	public void tearDown() throws Exception {
		_groupConfigurationTemporarySwapper.close();
	}

	@Test
	public void testAddAccessRoleToControlMenu() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(),
			new String[] {String.valueOf(role.getRoleId())}, true);

		_assertMenuAccessConfiguration(
			new String[] {String.valueOf(role.getRoleId())});
	}

	@Test
	public void testUpdateMenuAccessConfigurationWithDefaultRoles()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), null, true);

		Role administratorRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.ADMINISTRATOR);

		Role siteAdministratorRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		_assertMenuAccessConfiguration(
			new String[] {
				String.valueOf(administratorRole.getRoleId()),
				String.valueOf(siteAdministratorRole.getRoleId())
			});
	}

	@Test
	public void testUpdateMenuAccessConfigurationWithNoAccess()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[0], true);

		_assertMenuAccessConfiguration(new String[0]);
	}

	private void _assertMenuAccessConfiguration(
			String[] expectedAccessToControlMenuRoleIds)
		throws Exception {

		String filterString = StringBundler.concat(
			"(&(service.factoryPid=", MenuAccessConfiguration.class.getName(),
			".scoped)(",
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(), "=",
			_group.getGroupId(), "))");

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			filterString);

		Assert.assertNotNull(configurations);
		Assert.assertEquals(
			Arrays.toString(configurations), 1, configurations.length);

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		String[] actualAccessToControlMenuRoleIds = (String[])properties.get(
			"accessToControlMenuRoleIds");

		Assert.assertArrayEquals(
			Arrays.toString(actualAccessToControlMenuRoleIds),
			expectedAccessToControlMenuRoleIds,
			actualAccessToControlMenuRoleIds);

		Assert.assertTrue(
			GetterUtil.getBoolean(properties.get("showControlMenuByRole")));
	}

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	private GroupConfigurationTemporarySwapper
		_groupConfigurationTemporarySwapper;

	@Inject
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

	@Inject
	private RoleLocalService _roleLocalService;

}
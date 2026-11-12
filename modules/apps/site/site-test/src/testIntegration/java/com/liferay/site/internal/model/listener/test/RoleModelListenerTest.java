/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.configuration.MenuAccessConfiguration;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import java.util.Arrays;
import java.util.Dictionary;

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
public class RoleModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_configurationProvider.saveGroupConfiguration(
			MenuAccessConfiguration.class, _group.getCompanyId(),
			_group.getGroupId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accessToControlMenuRoleExternalReferenceCodes", new String[0]
			).put(
				"showControlMenuByRole", true
			).build());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testAddOtherRoleTypes() throws Exception {
		_roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_ACCOUNT,
			null, _serviceContext);
		_roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_DEPOT,
			null, _serviceContext);
		_roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_PROVIDER,
			null, _serviceContext);
		_roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null,
			RoleConstants.TYPE_ORGANIZATION, null, _serviceContext);
		_roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null,
			RoleConstants.TYPE_PUBLICATIONS, null, _serviceContext);

		_assertConfiguration(new String[0]);
	}

	@Test
	public void testAddRole() throws Exception {
		_roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_SITE,
			null, _serviceContext);

		_assertConfiguration(new String[0]);
	}

	@Test
	public void testDeleteRole() throws Exception {
		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_REGULAR,
			null, _serviceContext);

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[] {role.getExternalReferenceCode()},
			true);

		_assertConfiguration(new String[] {role.getExternalReferenceCode()});

		_roleLocalService.deleteRole(role);

		_assertConfiguration(new String[0]);
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			StringUtil.randomString(), null, null, RoleConstants.TYPE_SITE,
			null, _serviceContext);

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[] {role.getExternalReferenceCode()},
			true);

		role = _roleLocalService.updateExternalReferenceCode(
			role, RandomTestUtil.randomString());

		_assertConfiguration(new String[] {role.getExternalReferenceCode()});
	}

	private void _assertConfiguration(String[] expectedRolesCanSeeControlMenu)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				_group.getCompanyId(), _group.getGroupId(),
				MenuAccessConfiguration.class.getName(), null));

		Assert.assertNotNull(configurations);
		Assert.assertEquals(
			Arrays.toString(configurations), 1, configurations.length);

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		String[] accessToControlMenuRoleExternalReferenceCodes =
			(String[])properties.get(
				"accessToControlMenuRoleExternalReferenceCodes");

		Assert.assertArrayEquals(
			Arrays.toString(accessToControlMenuRoleExternalReferenceCodes),
			expectedRolesCanSeeControlMenu,
			accessToControlMenuRoleExternalReferenceCodes);

		Assert.assertTrue(
			GetterUtil.getBoolean(properties.get("showControlMenuByRole")));
	}

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

}
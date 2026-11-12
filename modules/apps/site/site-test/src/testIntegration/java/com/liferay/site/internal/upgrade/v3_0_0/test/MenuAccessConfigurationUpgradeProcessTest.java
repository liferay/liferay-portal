/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.upgrade.v3_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.site.configuration.MenuAccessConfiguration;

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
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class MenuAccessConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		_testUpgrade();
		_testUpgradeWithoutRoleIds();
	}

	private Dictionary<String, Object> _getProperties() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				_group.getCompanyId(), _group.getGroupId(),
				MenuAccessConfiguration.class.getName(), null));

		Assert.assertEquals(
			Arrays.toString(configurations), 1, configurations.length);

		Configuration configuration = configurations[0];

		return configuration.getProperties();
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private void _testUpgrade() throws Exception {
		Role administratorRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.ADMINISTRATOR);
		Role deletedRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_roleLocalService.deleteRole(deletedRole);

		_configurationProvider.saveGroupConfiguration(
			MenuAccessConfiguration.class, _group.getCompanyId(),
			_group.getGroupId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accessToControlMenuRoleIds",
				new String[] {
					String.valueOf(administratorRole.getRoleId()),
					String.valueOf(deletedRole.getRoleId()),
					String.valueOf(role.getRoleId())
				}
			).put(
				"showControlMenuByRole", true
			).build());

		_runUpgrade();

		Dictionary<String, Object> properties = _getProperties();

		String[] accessToControlMenuRoleExternalReferenceCodes =
			(String[])properties.get(
				"accessToControlMenuRoleExternalReferenceCodes");

		Assert.assertArrayEquals(
			Arrays.toString(accessToControlMenuRoleExternalReferenceCodes),
			new String[] {
				administratorRole.getExternalReferenceCode(),
				role.getExternalReferenceCode()
			},
			accessToControlMenuRoleExternalReferenceCodes);

		Assert.assertNull(properties.get("accessToControlMenuRoleIds"));
		Assert.assertTrue(
			GetterUtil.getBoolean(properties.get("showControlMenuByRole")));
	}

	private void _testUpgradeWithoutRoleIds() throws Exception {
		_configurationProvider.saveGroupConfiguration(
			MenuAccessConfiguration.class, _group.getCompanyId(),
			_group.getGroupId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"showControlMenuByRole", true
			).build());

		_runUpgrade();

		Dictionary<String, Object> properties = _getProperties();

		Assert.assertNull(
			properties.get("accessToControlMenuRoleExternalReferenceCodes"));
		Assert.assertTrue(
			GetterUtil.getBoolean(properties.get("showControlMenuByRole")));
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.internal.upgrade.v3_0_0." +
			"MenuAccessConfigurationUpgradeProcess";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.site.internal.upgrade.registry.SiteServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
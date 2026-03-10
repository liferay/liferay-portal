/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.internal.upgrade.v2_11_5.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommercePermissionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getCompanyId(), group.getGroupId(), user.getUserId());
	}

	@Test
	public void testUpgrade() throws Exception {
		_resourceActionLocalService.addResourceAction(
			CommerceInventoryConstants.RESOURCE_NAME, "MANAGE_INVENTORY", 1L);

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), _serviceContext.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, _serviceContext);

		_resourcePermissionLocalService.addResourcePermission(
			_serviceContext.getCompanyId(),
			CommerceInventoryConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_serviceContext.getCompanyId()), role.getRoleId(),
			"MANAGE_INVENTORY");

		_runUpgrade();

		CacheRegistryUtil.clear();

		_entityCache.clearCache(ResourceActionImpl.class);
		_finderCache.clearCache(ResourceActionImpl.class);

		_resourceActionLocalService.checkResourceActions();

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions("MANAGE_INVENTORY");

		Assert.assertEquals(
			resourceActions.toString(), 0, resourceActions.size());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_serviceContext.getCompanyId(),
				CommerceInventoryConstants.RESOURCE_NAME,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_serviceContext.getCompanyId()),
				role.getRoleId(),
				CommerceInventoryActionKeys.VIEW_INVENTORIES));

		for (String actionId :
				new String[] {
					ActionKeys.DELETE, ActionKeys.PERMISSIONS,
					ActionKeys.UPDATE, ActionKeys.VIEW
				}) {

			Assert.assertNotNull(
				_resourceActionLocalService.fetchResourceAction(
					CommerceInventoryWarehouse.class.getName(), actionId));
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					_serviceContext.getCompanyId(),
					CommerceInventoryWarehouse.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(_serviceContext.getCompanyId()),
					role.getRoleId(), actionId));
		}
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

	private static final String _CLASS_NAME =
		"com.liferay.commerce.inventory.internal.upgrade.v2_11_5." +
			"CommercePermissionUpgradeProcess";

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.inventory.internal.upgrade.registry.CommerceInventoryServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
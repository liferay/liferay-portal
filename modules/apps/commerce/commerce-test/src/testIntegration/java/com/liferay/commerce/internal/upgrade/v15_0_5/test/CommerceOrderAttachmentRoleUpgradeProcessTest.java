/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_0_5.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CommerceOrderAttachmentRoleUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_removeResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_removeResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT);
		_removeResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.VIEW);
		_removeResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT);
		_removeResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.VIEW);
		_removeResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT);
		_removeResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_removeResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);
		_removeResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(), companyId,
			AccountRoleConstants.ROLE_NAME_ORDER_ADMINISTRATOR,
			ResourceConstants.SCOPE_COMPANY, ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_removeResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME, companyId,
			AccountRoleConstants.ROLE_NAME_ORDER_ADMINISTRATOR,
			ResourceConstants.SCOPE_COMPANY,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);

		_runUpgrade();

		CacheRegistryUtil.clear();

		_entityCache.clearCache(ResourceActionImpl.class);
		_finderCache.clearCache(ResourceActionImpl.class);

		_resourceActionLocalService.checkResourceActions();

		_assertResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_assertResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT);
		_assertResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.VIEW);
		_assertResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT);
		_assertResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.VIEW);
		_assertResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT);
		_assertResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_assertResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME,
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);
		_assertResourcePermission(
			companyId, CommerceOrderAttachment.class.getName(), companyId,
			AccountRoleConstants.ROLE_NAME_ORDER_ADMINISTRATOR,
			ResourceConstants.SCOPE_COMPANY, ActionKeys.DELETE,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW);
		_assertResourcePermission(
			companyId, CommerceOrderConstants.RESOURCE_NAME, companyId,
			AccountRoleConstants.ROLE_NAME_ORDER_ADMINISTRATOR,
			ResourceConstants.SCOPE_COMPANY,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT,
			CommerceOrderActionKeys.VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);
	}

	private void _assertResourcePermission(
			long companyId, String name, long primKey, String roleName,
			int scope, String... actionIds)
		throws Exception {

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		if (role == null) {
			return;
		}

		for (String actionId : actionIds) {
			Assert.assertTrue(
				_resourcePermissionLocalService.hasResourcePermission(
					companyId, name, scope, String.valueOf(primKey),
					role.getRoleId(), actionId));
		}
	}

	private void _removeResourcePermission(
			long companyId, String name, long primKey, String roleName,
			int scope, String... actionIds)
		throws Exception {

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		if (role == null) {
			return;
		}

		for (String actionId : actionIds) {
			_resourcePermissionLocalService.removeResourcePermission(
				companyId, name, scope, String.valueOf(primKey),
				role.getRoleId(), actionId);
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
		"com.liferay.commerce.internal.upgrade.v15_0_5." +
			"CommerceOrderAttachmentRoleUpgradeProcess";

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

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.internal.upgrade.registry.CommerceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
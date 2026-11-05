/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v15_1_7.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CommerceReturnRoleUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		Assume.assumeTrue(
			FeatureFlagManagerUtil.isEnabled(
				TestPropsValues.getCompanyId(), "LPD-10562"));

		long companyId = TestPropsValues.getCompanyId();

		ObjectDefinition commerceReturnObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "CommerceReturn");

		_removeResourcePermission(
			companyId, commerceReturnObjectDefinition.getClassName(), companyId,
			AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
			ResourceConstants.SCOPE_COMPANY, ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.UPDATE_DISCUSSION);

		ObjectDefinition commerceReturnItemObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "CommerceReturnItem");

		_removeResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.ADD_DISCUSSION);
		_removeResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.ADD_DISCUSSION);
		_removeResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.ADD_DISCUSSION);
		_removeResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			companyId, AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
			ResourceConstants.SCOPE_COMPANY, ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.UPDATE_DISCUSSION);

		_runUpgrade();

		CacheRegistryUtil.clear();

		_entityCache.clearCache(ResourceActionImpl.class);
		_finderCache.clearCache(ResourceActionImpl.class);

		_resourceActionLocalService.checkResourceActions();

		_assertResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.ADD_DISCUSSION);
		_assertResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.ADD_DISCUSSION);
		_assertResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER,
			ResourceConstants.SCOPE_GROUP_TEMPLATE, ActionKeys.ADD_DISCUSSION);
		_assertResourcePermission(
			companyId, commerceReturnItemObjectDefinition.getClassName(),
			companyId, AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
			ResourceConstants.SCOPE_COMPANY, ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.UPDATE_DISCUSSION);
		_assertResourcePermission(
			companyId, commerceReturnObjectDefinition.getClassName(), companyId,
			AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
			ResourceConstants.SCOPE_COMPANY, ActionKeys.ADD_DISCUSSION,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.UPDATE_DISCUSSION);
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
		"com.liferay.commerce.internal.upgrade.v15_1_7." +
			"CommerceReturnRoleUpgradeProcess";

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

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
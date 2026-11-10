/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v13_0_1.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.constants.CommerceCurrencyActionKeys;
import com.liferay.commerce.payment.constants.CommercePaymentEntryActionKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class ReturnsManagerRoleUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_role = _roleLocalService.fetchRole(
			_group.getCompanyId(),
			AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER);

		if (_role == null) {
			_role = _roleLocalService.addRole(
				null, TestPropsValues.getUserId(), null, 0,
				AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER,
				Collections.singletonMap(
					LocaleUtil.US,
					AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER),
				null, RoleConstants.TYPE_REGULAR, null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		_removeResourcePermissions();

		_runUpgrade();

		Assert.assertTrue(
			ListUtil.exists(
				_resourcePermissionLocalService.getRoles(
					_group.getCompanyId(), CommerceChannel.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(_group.getCompanyId()), ActionKeys.UPDATE),
				role -> StringUtil.equals(
					role.getName(),
					AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER)));
		Assert.assertTrue(
			ListUtil.exists(
				_resourcePermissionLocalService.getRoles(
					_group.getCompanyId(), CommerceChannel.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(_group.getCompanyId()), ActionKeys.VIEW),
				role -> StringUtil.equals(
					role.getName(),
					AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER)));
		Assert.assertTrue(
			ListUtil.exists(
				_resourcePermissionLocalService.getRoles(
					_group.getCompanyId(), CommercePortletKeys.COMMERCE_PAYMENT,
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(_group.getCompanyId()),
					ActionKeys.ACCESS_IN_CONTROL_PANEL),
				role -> StringUtil.equals(
					role.getName(),
					AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER)));
		Assert.assertTrue(
			ListUtil.exists(
				_resourcePermissionLocalService.getRoles(
					_group.getCompanyId(), "com.liferay.commerce.currency",
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(_group.getCompanyId()),
					CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES),
				role -> StringUtil.equals(
					role.getName(),
					AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER)));
		Assert.assertTrue(
			ListUtil.exists(
				_resourcePermissionLocalService.getRoles(
					_group.getCompanyId(), "com.liferay.commerce.payment",
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(_group.getCompanyId()),
					CommercePaymentEntryActionKeys.ADD_REFUND),
				role -> StringUtil.equals(
					role.getName(),
					AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER)));
	}

	private void _removeResourcePermissions() throws Exception {
		Role role = _roleLocalService.fetchRole(
			_group.getCompanyId(),
			AccountRoleConstants.ROLE_NAME_RETURNS_MANAGER);

		if (role == null) {
			return;
		}

		_resourcePermissionLocalService.removeResourcePermission(
			_group.getCompanyId(), CommerceChannel.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			ActionKeys.UPDATE);
		_resourcePermissionLocalService.removeResourcePermission(
			_group.getCompanyId(), CommerceChannel.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);
		_resourcePermissionLocalService.removeResourcePermission(
			_group.getCompanyId(), CommercePortletKeys.COMMERCE_PAYMENT,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
		_resourcePermissionLocalService.removeResourcePermission(
			_group.getCompanyId(), "com.liferay.commerce.currency",
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			CommerceCurrencyActionKeys.MANAGE_COMMERCE_CURRENCIES);
		_resourcePermissionLocalService.removeResourcePermission(
			_group.getCompanyId(), "com.liferay.commerce.payment",
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			CommercePaymentEntryActionKeys.ADD_REFUND);
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
		"com.liferay.commerce.internal.upgrade.v13_0_1." +
			"ReturnsManagerRoleUpgradeProcess";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.internal.upgrade.registry.CommerceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.on.demand.admin.constants.OnDemandAdminPortletKeys;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class OnDemandAdminControlPanelEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testHasAccessPermission() throws Exception {
		Assert.assertTrue(
			_onDemandAdminControlPanelEntry.hasAccessPermission(
				_permissionCheckerFactory.create(TestPropsValues.getUser()),
				null, _onDemandAdminPortlet));

		Group defaultCompanyGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CONTROL_PANEL);
		User defaultCompanyUser = UserTestUtil.addUser();

		Assert.assertFalse(
			_onDemandAdminControlPanelEntry.hasAccessPermission(
				_permissionCheckerFactory.create(defaultCompanyUser),
				defaultCompanyGroup, _onDemandAdminPortlet));

		Role defaultCompanyRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			defaultCompanyRole, OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);

		_userLocalService.addRoleUser(
			defaultCompanyRole.getRoleId(), defaultCompanyUser);

		Assert.assertTrue(
			_onDemandAdminControlPanelEntry.hasAccessPermission(
				_permissionCheckerFactory.create(defaultCompanyUser),
				defaultCompanyGroup, _onDemandAdminPortlet));

		Company company = CompanyTestUtil.addCompany();

		User companyAdminUser = UserTestUtil.addCompanyAdminUser(company);

		Assert.assertFalse(
			_onDemandAdminControlPanelEntry.hasAccessPermission(
				_permissionCheckerFactory.create(companyAdminUser), null,
				_onDemandAdminPortlet));

		Group group = _groupLocalService.getGroup(
			company.getCompanyId(), GroupConstants.CONTROL_PANEL);
		User user = UserTestUtil.addUser(company);

		Assert.assertFalse(
			_onDemandAdminControlPanelEntry.hasAccessPermission(
				_permissionCheckerFactory.create(user), group,
				_onDemandAdminPortlet));

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), companyAdminUser.getUserId(), null,
			0, RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null, null);

		RoleTestUtil.addResourcePermission(
			role, OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(company.getCompanyId()),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);

		_userLocalService.addRoleUser(role.getRoleId(), user);

		Assert.assertFalse(
			_onDemandAdminControlPanelEntry.hasAccessPermission(
				_permissionCheckerFactory.create(user), group,
				_onDemandAdminPortlet));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "jakarta.portlet.name=" + OnDemandAdminPortletKeys.ON_DEMAND_ADMIN
	)
	private ControlPanelEntry _onDemandAdminControlPanelEntry;

	@Inject(
		filter = "jakarta.portlet.name=" + OnDemandAdminPortletKeys.ON_DEMAND_ADMIN
	)
	private Portlet _onDemandAdminPortlet;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
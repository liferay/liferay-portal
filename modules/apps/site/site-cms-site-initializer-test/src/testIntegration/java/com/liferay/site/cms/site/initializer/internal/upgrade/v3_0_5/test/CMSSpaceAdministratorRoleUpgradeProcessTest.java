/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_5.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSRolesConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Veronica Gonzalez
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CMSSpaceAdministratorRoleUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);
	}

	@Test
	public void testUpgrade() throws Exception {
		Role role = _fetchRole();

		_removeViewResourcePermission("/broken-links", role);
		_removeViewResourcePermission("/dashboard", role);
		_removeViewResourcePermission("/missing-required-data", role);

		_upgrade();

		Assert.assertTrue(_hasViewResourcePermission("/broken-links", role));
		Assert.assertTrue(_hasViewResourcePermission("/dashboard", role));
		Assert.assertTrue(
			_hasViewResourcePermission("/missing-required-data", role));
	}

	@Test
	public void testUpgradeWhenRoleIsMissing() throws Exception {
		_roleLocalService.deleteRole(_fetchRole());

		_upgrade();

		Role role = _fetchRole();

		Assert.assertTrue(_hasViewResourcePermission("/broken-links", role));
		Assert.assertTrue(_hasViewResourcePermission("/dashboard", role));
		Assert.assertTrue(
			_hasViewResourcePermission("/missing-required-data", role));
	}

	private Role _fetchRole() throws Exception {
		return _roleLocalService.fetchRoleByExternalReferenceCode(
			CMSRolesConstants.CMS_SPACE_ADMINISTRATOR_EXTERNAL_REFERENCE_CODE,
			TestPropsValues.getCompanyId());
	}

	private long _getPlid(String friendlyURL) throws Exception {
		Layout layout = _layoutLocalService.getFriendlyURLLayout(
			_group.getGroupId(), false, friendlyURL);

		return layout.getPlid();
	}

	private boolean _hasViewResourcePermission(String friendlyURL, Role role)
		throws Exception {

		return _resourcePermissionLocalService.hasResourcePermission(
			TestPropsValues.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_getPlid(friendlyURL)), role.getRoleId(),
			ActionKeys.VIEW);
	}

	private void _removeViewResourcePermission(String friendlyURL, Role role)
		throws Exception {

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_getPlid(friendlyURL)), role.getRoleId(),
			ActionKeys.VIEW);

		Assert.assertFalse(_hasViewResourcePermission(friendlyURL, role));
	}

	private void _upgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_5." +
				"CMSSpaceAdministratorRoleUpgradeProcess");

		upgradeProcess.upgrade();
	}

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
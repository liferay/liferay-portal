/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.security.permission.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class DepotRoleContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_depotEntry = _addDepotEntry();

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testConnectedSiteMemberRoleAssignment() throws Exception {
		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		DepotTestUtil.withSiteMember(
			_group,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				long[] roleIds = permissionChecker.getRoleIds(
					user.getUserId(), _depotEntry.getGroupId());

				Role role = _roleLocalService.getRole(
					_depotEntry.getCompanyId(),
					DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER);

				Assert.assertTrue(
					ArrayUtil.contains(roleIds, role.getRoleId()));
			});
	}

	@Test
	public void testGetRoles() throws Exception {
		DepotTestUtil.withAssetLibraryMember(
			_depotEntry,
			user -> {
				PermissionChecker permissionChecker =
					_permissionCheckerFactory.create(user);

				long[] roleIds = permissionChecker.getRoleIds(
					user.getUserId(), _depotEntry.getGroupId());

				Role role = _roleLocalService.getRole(
					_depotEntry.getCompanyId(),
					DepotRolesConstants.ASSET_LIBRARY_MEMBER);

				Assert.assertTrue(
					ArrayUtil.contains(roleIds, role.getRoleId()));
			});
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private RoleLocalService _roleLocalService;

}
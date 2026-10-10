/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry;
import com.liferay.portal.tools.service.builder.test.service.FilterPrimaryEntryLocalService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class FilterPrimaryEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testFilterFindByGroupId() throws Exception {
		_group = GroupTestUtil.addGroup();

		_filterPrimaryEntry =
			_filterPrimaryEntryLocalService.addFilterPrimaryEntry(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				RandomTestUtil.randomLong(), TestPropsValues.getUserId());

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), FilterPrimaryEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_filterPrimaryEntry.getResourcePrimKey()),
			guestRole.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_userLocalService.getGuestUser(
					TestPropsValues.getCompanyId()))) {

			_testFilterFindByGroupId(false);
			_testFilterFindByGroupId(true);
		}
	}

	private void _testFilterFindByGroupId(
			boolean permissionsInMemoryFilterEnabled)
		throws Exception {

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					_filterPrimaryEntryLocalService.getBasePersistence(),
					"_permissionsInMemoryFilterEnabled",
					permissionsInMemoryFilterEnabled)) {

			Assert.assertEquals(
				Collections.singletonList(_filterPrimaryEntry),
				_filterPrimaryEntryLocalService.filterFindByGroupId(
					_group.getGroupId()));
			Assert.assertEquals(
				1,
				_filterPrimaryEntryLocalService.filterCountByGroupId(
					_group.getGroupId()));
		}
	}

	@DeleteAfterTestRun
	private FilterPrimaryEntry _filterPrimaryEntry;

	@Inject
	private FilterPrimaryEntryLocalService _filterPrimaryEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
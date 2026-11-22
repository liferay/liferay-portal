/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.service.persistence.SharingEntryUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.POWER_USER);
	}

	@Test
	public void testDeletingGroupDeletesSharingEntries() throws Exception {
		long classPK = _group.getGroupId();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		try {
			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), 0, 0, _groupUser.getUserId(),
				_classNameLocalService.getClassNameId(Group.class.getName()),
				classPK, _group.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), 0,
				userGroup.getUserGroupId(), 0,
				_classNameLocalService.getClassNameId(Group.class.getName()),
				classPK, _group.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			List<SharingEntry> groupSharingEntries = _getGroupSharingEntries(
				_group.getGroupId());

			Assert.assertEquals(
				groupSharingEntries.toString(), 2, groupSharingEntries.size());

			_groupLocalService.deleteGroup(_group.getGroupId());

			groupSharingEntries = _getGroupSharingEntries(_group.getGroupId());

			Assert.assertEquals(
				groupSharingEntries.toString(), 0, groupSharingEntries.size());
		}
		finally {
			_userGroupLocalService.deleteUserGroup(userGroup);
		}
	}

	@Test
	public void testDeletingGroupDoesNotDeleteOtherGroupSharingEntries()
		throws Exception {

		Group group2 = GroupTestUtil.addGroup();

		try {
			long classNameId = _classNameLocalService.getClassNameId(
				Group.class.getName());

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId());

			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), 0, 0, _groupUser.getUserId(),
				classNameId, _group.getGroupId(), _group.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null, serviceContext);

			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), 0, 0, _groupUser.getUserId(),
				classNameId, group2.getGroupId(), group2.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null, serviceContext);

			List<SharingEntry> groupSharingEntries1 = _getGroupSharingEntries(
				_group.getGroupId());

			Assert.assertEquals(
				groupSharingEntries1.toString(), 1,
				groupSharingEntries1.size());

			List<SharingEntry> groupSharingEntries2 = _getGroupSharingEntries(
				group2.getGroupId());

			Assert.assertEquals(
				groupSharingEntries2.toString(), 1,
				groupSharingEntries2.size());

			_groupLocalService.deleteGroup(_group.getGroupId());

			groupSharingEntries1 = _getGroupSharingEntries(_group.getGroupId());

			Assert.assertEquals(
				groupSharingEntries1.toString(), 0,
				groupSharingEntries1.size());

			groupSharingEntries2 = _getGroupSharingEntries(group2.getGroupId());

			Assert.assertEquals(
				groupSharingEntries2.toString(), 1,
				groupSharingEntries2.size());
		}
		finally {
			_groupLocalService.deleteGroup(group2);
		}
	}

	private List<SharingEntry> _getGroupSharingEntries(long groupId)
		throws Exception {

		return TransactionTestUtil.invoke(
			SharingEntryUtil.getPersistence(),
			sharingEntryPersistence -> sharingEntryPersistence.findByGroupId(
				groupId));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private User _groupUser;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

}
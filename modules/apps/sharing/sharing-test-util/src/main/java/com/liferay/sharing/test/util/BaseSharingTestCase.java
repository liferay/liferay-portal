/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.permission.contributor.PermissionSQLContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.SharingPermissionChecker;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Sergio González
 * @author Alejandro Tardín
 */
public abstract class BaseSharingTestCase<T extends ClassedModel> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groupUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.POWER_USER);

		_powerUserRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.POWER_USER);
	}

	@Test
	public void testAdminCanShareWithAddDiscussion() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser(), permissionChecker)) {

			_assertContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.ADD_DISCUSSION);
		}
	}

	@Test
	public void testAdminCanShareWithUpdate() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser(), permissionChecker)) {

			_assertContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.UPDATE);
		}
	}

	@Test
	public void testAdminCanShareWithView() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser(), permissionChecker)) {

			_assertContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.VIEW);
		}
	}

	@Test
	public void testDeletingSharedModelDeletesSharingEntries()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, serviceContext);

		List<SharingEntry> toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				_groupUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());

		deleteModel(model);

		toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				_groupUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 0, toUserSharingEntries.size());
	}

	@Test
	public void testDeletingSharedModelDoesNotDeleteOtherSharingEntries()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model1 = getModel(TestPropsValues.getUser(), _group);

		long classNameId1 = _classNameLocalService.getClassNameId(
			model1.getModelClassName());
		long classPK1 = (Long)model1.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			classNameId1, classPK1, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, serviceContext);

		T model2 = getModel(TestPropsValues.getUser(), _group);

		long classNameId2 = _classNameLocalService.getClassNameId(
			model2.getModelClassName());
		long classPK2 = (Long)model2.getPrimaryKeyObj();

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			classNameId2, classPK2, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, serviceContext);

		List<SharingEntry> toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				_groupUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 2, toUserSharingEntries.size());

		deleteModel(model1);

		toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				_groupUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());
		Assert.assertEquals(sharingEntry, toUserSharingEntries.get(0));
	}

	@Test
	public void testInlinePermissions() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			Assert.assertEquals(0, getModelCount(_group));

			T model = getModel(TestPropsValues.getUser(), _group);

			long classPK = (long)model.getPrimaryKeyObj();

			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
				_classNameLocalService.getClassNameId(
					model.getModelClassName()),
				classPK, _group.getGroupId(), true,
				Collections.singletonList(SharingEntryAction.VIEW), null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			Assert.assertEquals(1, getModelCount(_group));
		}
	}

	@Test
	public void testInlinePermissionsToUserGroup() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			Assert.assertEquals(0, getModelCount(_group));

			T model = getModel(TestPropsValues.getUser(), _group);

			long classPK = (long)model.getPrimaryKeyObj();

			_userGroupLocalService.addUserUserGroup(
				_groupUser.getUserId(), userGroup);

			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), userGroup.getUserGroupId(),
				0,
				_classNameLocalService.getClassNameId(
					model.getModelClassName()),
				classPK, _group.getGroupId(), true,
				Collections.singletonList(SharingEntryAction.VIEW), null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			Assert.assertEquals(1, getModelCount(_group));
		}
		finally {
			_userGroupLocalService.deleteUserUserGroup(
				_groupUser.getUserId(), userGroup);

			_userGroupLocalService.deleteUserGroup(userGroup);
		}
	}

	@Test
	public void testModelClassNameReturnsPermissionSQL() throws Exception {
		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();
		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser(), permissionChecker)) {

			T model = getModel(TestPropsValues.getUser(), _group);

			PermissionSQLContributor permissionSQLContributor =
				getPermissionSQLContributor();

			_userGroupLocalService.addUserUserGroup(
				TestPropsValues.getUserId(), userGroup1);
			_userGroupLocalService.addUserUserGroup(
				TestPropsValues.getUserId(), userGroup2);

			Assert.assertEquals(
				StringBundler.concat(
					"1234 IN (SELECT SharingEntry.classPK FROM SharingEntry ",
					"WHERE ((SharingEntry.toUserGroupId IN ( ",
					StringUtil.merge(
						new long[] {
							userGroup1.getUserGroupId(),
							userGroup2.getUserGroupId()
						},
						","),
					")) OR (SharingEntry.toUserId = ",
					TestPropsValues.getUserId(),
					")) AND (SharingEntry.classNameId = ",
					_classNameLocalService.getClassNameId(
						model.getModelClassName()),
					"))"),
				permissionSQLContributor.getPermissionSQL(
					model.getModelClassName(), "1234", null, null));
		}
		finally {
			_userGroupLocalService.deleteUserUserGroup(
				TestPropsValues.getUserId(), userGroup1);

			_userGroupLocalService.deleteUserGroup(userGroup1);

			_userGroupLocalService.deleteUserUserGroup(
				TestPropsValues.getUserId(), userGroup2);

			_userGroupLocalService.deleteUserGroup(userGroup2);
		}
	}

	@Test
	public void testMovingToRecycleBinSharedModelDoesNotDeleteSharingEntries()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, serviceContext);

		List<SharingEntry> toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				_groupUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());

		moveModelToTrash(model);

		toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				_groupUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());
	}

	@Test
	public void testUserWithAddDiscussionAndViewSharingEntryActionCanAddDiscussionPrivateModel()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(
				SharingEntryAction.ADD_DISCUSSION, SharingEntryAction.VIEW),
			null, serviceContext);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertContainsPermission(
				permissionChecker, model, ActionKeys.ADD_DISCUSSION);
		}
	}

	@Test
	public void testUserWithAddDiscussionAndViewSharingEntryActionCannotUpdatePrivateModel()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(
				SharingEntryAction.ADD_DISCUSSION, SharingEntryAction.VIEW),
			null, serviceContext);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsPermission(
				permissionChecker, model, ActionKeys.UPDATE);
		}
	}

	@Test
	public void testUserWithAddDiscussionPermissionCannotShareWithUpdate()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(
					_powerUserRole, ActionKeys.ADD_DISCUSSION);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.UPDATE);
		}
	}

	@Test
	public void testUserWithAddDiscussionPermissionCannotShareWithView()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(
					_powerUserRole, ActionKeys.ADD_DISCUSSION);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.VIEW);
		}
	}

	@Test
	public void testUserWithAddDiscussionPermissionCanShareWithAddDiscussion()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(
					_powerUserRole, ActionKeys.ADD_DISCUSSION);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.ADD_DISCUSSION);
		}
	}

	@Test
	public void testUserWithoutAddDiscussionPermissionCannotShareWithAddDiscussion()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.ADD_DISCUSSION);
		}
	}

	@Test
	public void testUserWithoutAddDiscussionSharingEntryActionCannotAddDiscussionPrivateModel()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				ActionKeys.ADD_DISCUSSION);
		}
	}

	@Test
	public void testUserWithoutSharingCannotViewPrivateModel()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				ActionKeys.VIEW);
		}
	}

	@Test
	public void testUserWithoutUpdatePermissionCannotShareWithUpdate()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.UPDATE);
		}
	}

	@Test
	public void testUserWithoutUpdateSharingEntryActionCannotUpdatePrivateModel()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				ActionKeys.UPDATE);
		}
	}

	@Test
	public void testUserWithoutViewPermissionCannotShareWithView()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.VIEW);
		}
	}

	@Test
	public void testUserWithoutViewSharingEntryActionCannotViewPrivateModel()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				ActionKeys.VIEW);
		}
	}

	@Test
	public void testUserWithUpdateAndViewSharingEntryActionCannotAddDiscussionPrivateModel()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, serviceContext);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsPermission(
				permissionChecker, model, ActionKeys.ADD_DISCUSSION);
		}
	}

	@Test
	public void testUserWithUpdateAndViewSharingEntryActionCanUpdatePrivateModel()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, serviceContext);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertContainsPermission(
				permissionChecker, model, ActionKeys.UPDATE);
		}
	}

	@Test
	public void testUserWithUpdatePermissionCannotShareWithAddDiscussion()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(
					_powerUserRole, ActionKeys.UPDATE);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.ADD_DISCUSSION);
		}
	}

	@Test
	public void testUserWithUpdatePermissionCannotShareWithView()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(
					_powerUserRole, ActionKeys.UPDATE);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.VIEW);
		}
	}

	@Test
	public void testUserWithUpdatePermissionCanShareWithUpdate()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(
					_powerUserRole, ActionKeys.UPDATE);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.UPDATE);
		}
	}

	@Test
	public void testUserWithViewPermissionCannotShareWithAddDiscussion()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(_powerUserRole, ActionKeys.VIEW);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.ADD_DISCUSSION);
		}
	}

	@Test
	public void testUserWithViewPermissionCannotShareWithUpdate()
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(_powerUserRole, ActionKeys.VIEW);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.UPDATE);
		}
	}

	@Test
	public void testUserWithViewPermissionCanShareWithView() throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (AddModelResourcePermission addModelResourcePermission =
				new AddModelResourcePermission(_powerUserRole, ActionKeys.VIEW);
			ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertContainsSharingPermission(
				permissionChecker, getModel(TestPropsValues.getUser(), _group),
				SharingEntryAction.VIEW);
		}
	}

	@Test
	public void testUserWithViewSharingEntryActionCannotViewPendingModel()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getPendingModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null, serviceContext);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertNotContainsPermission(
				permissionChecker, model, ActionKeys.VIEW);
		}
	}

	@Test
	public void testUserWithViewSharingEntryActionCanViewPrivateModel()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		T model = getModel(TestPropsValues.getUser(), _group);

		long classPK = (Long)model.getPrimaryKeyObj();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, _groupUser.getUserId(),
			_classNameLocalService.getClassNameId(model.getModelClassName()),
			classPK, _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null, serviceContext);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_groupUser);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_groupUser, permissionChecker)) {

			_assertContainsPermission(
				permissionChecker, model, ActionKeys.VIEW);
		}
	}

	protected abstract void deleteModel(T model) throws PortalException;

	protected abstract String getClassName();

	protected abstract T getModel(User user, Group group)
		throws PortalException;

	protected abstract int getModelCount(Group group) throws PortalException;

	protected abstract ModelResourcePermission<T> getModelResourcePermission();

	protected abstract T getPendingModel(User user, Group group)
		throws PortalException;

	protected abstract PermissionSQLContributor getPermissionSQLContributor();

	protected abstract SharingPermissionChecker getSharingPermissionChecker();

	protected abstract void moveModelToTrash(T model) throws PortalException;

	private void _assertContainsPermission(
			PermissionChecker permissionChecker, T model, String actionId)
		throws PortalException {

		ModelResourcePermission<T> modelResourcePermission =
			getModelResourcePermission();

		Assert.assertTrue(
			modelResourcePermission.contains(
				permissionChecker, model, actionId));
	}

	private void _assertContainsSharingPermission(
			PermissionChecker permissionChecker, T model,
			SharingEntryAction sharingEntryAction)
		throws PortalException {

		SharingPermissionChecker sharingPermissionChecker =
			getSharingPermissionChecker();

		Assert.assertTrue(
			sharingPermissionChecker.hasPermission(
				permissionChecker, (Long)model.getPrimaryKeyObj(),
				_group.getGroupId(),
				Collections.singletonList(sharingEntryAction)));
	}

	private void _assertNotContainsPermission(
			PermissionChecker permissionChecker, T model, String actionId)
		throws PortalException {

		ModelResourcePermission<T> modelResourcePermission =
			getModelResourcePermission();

		Assert.assertFalse(
			modelResourcePermission.contains(
				permissionChecker, model, actionId));
	}

	private void _assertNotContainsSharingPermission(
			PermissionChecker permissionChecker, T model,
			SharingEntryAction sharingEntryAction)
		throws PortalException {

		SharingPermissionChecker sharingPermissionChecker =
			getSharingPermissionChecker();

		Assert.assertFalse(
			sharingPermissionChecker.hasPermission(
				permissionChecker, (Long)model.getPrimaryKeyObj(),
				_group.getGroupId(),
				Collections.singletonList(sharingEntryAction)));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSharingTestCase.class);

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private User _groupUser;
	private Role _powerUserRole;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	private class AddModelResourcePermission implements AutoCloseable {

		public AddModelResourcePermission(Role role, String... actionKeys) {
			_role = role;
			_actionKeys = actionKeys;

			for (String actionKey : actionKeys) {
				try {
					_resourcePermissionLocalService.addResourcePermission(
						role.getCompanyId(), getClassName(),
						ResourceConstants.SCOPE_GROUP_TEMPLATE,
						String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
						role.getRoleId(), actionKey);
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}
		}

		@Override
		public void close() throws Exception {
			for (String actionKey : _actionKeys) {
				_resourcePermissionLocalService.removeResourcePermission(
					_role.getCompanyId(), getClassName(),
					ResourceConstants.SCOPE_GROUP_TEMPLATE,
					String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
					_role.getRoleId(), actionKey);
			}
		}

		private final String[] _actionKeys;
		private final Role _role;

	}

}
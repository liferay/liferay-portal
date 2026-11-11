/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.message.boards.internal.comment.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.marketing.platform.test.util.CMPTestUtil;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class MBDiscussionPermissionImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();
		_siteUser1 = UserTestUtil.addUser(_group.getGroupId());
		_siteUser2 = UserTestUtil.addUser(_group.getGroupId());
	}

	@Test
	@TestInfo("LPD-100338")
	public void testUserCanUpdateAndDeleteHisCommentInCMPProject()
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));
			PrincipalThreadLocal.setName(_user.getUserId());

			long commentId = _addCMPProjectComment(_siteUser1);

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(_siteUser1);

			Assert.assertTrue(
				_discussionPermission.hasUpdatePermission(
					permissionChecker, commentId));
			Assert.assertTrue(
				_discussionPermission.hasDeletePermission(
					permissionChecker, commentId));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Test
	@TestInfo("LPD-100338")
	public void testUserCannotUpdateSomeoneElseCommentInCMPProject()
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));
			PrincipalThreadLocal.setName(_user.getUserId());

			long commentId = _addCMPProjectComment(_siteUser1);

			Comment comment = _commentManager.fetchComment(commentId);

			_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), comment.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(comment.getClassPK()), _role.getRoleId(),
				new String[] {
					ActionKeys.DELETE_DISCUSSION, ActionKeys.UPDATE_DISCUSSION,
					ActionKeys.VIEW
				});

			RoleLocalServiceUtil.addUserRole(
				_siteUser2.getUserId(), _role.getRoleId());

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(_siteUser2);

			Assert.assertFalse(
				_discussionPermission.hasUpdatePermission(
					permissionChecker, commentId));
			Assert.assertTrue(
				_discussionPermission.hasDeletePermission(
					permissionChecker, commentId));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	private long _addCMPProjectComment(User user) throws Exception {
		CMPTestUtil.getOrAddGroup(MBDiscussionPermissionImplTest.class);

		ObjectEntry objectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		_depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			objectEntry.getGroupId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		return _commentManager.addComment(
			user.getUserId(), objectEntry.getGroupId(),
			objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
			StringUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					objectEntry.getGroupId(), user.getUserId())));
	}

	@Inject(
		filter = "component.name=com.liferay.message.boards.comment.internal.MBCommentManagerImpl"
	)
	private CommentManager _commentManager;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DiscussionPermission _discussionPermission;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _siteUser1;

	@DeleteAfterTestRun
	private User _siteUser2;

	private User _user;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class BlogsEntryDiscussionPermissionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_user = UserTestUtil.addUser();
	}

	@Test
	@TestInfo({"LPD-113649", "LPS-136786", "LPS-136787"})
	public void testHasAddPermission() throws Exception {
		Assert.assertTrue(
			_hasAddPermission(
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(_group.getCompanyId()))));
		Assert.assertTrue(
			_hasAddPermission(PermissionCheckerFactoryUtil.create(_user)));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, BlogsEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_blogsEntry.getEntryId()),
			ActionKeys.ADD_DISCUSSION);

		Assert.assertFalse(
			_hasAddPermission(
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(_group.getCompanyId()))));
		Assert.assertFalse(
			_hasAddPermission(PermissionCheckerFactoryUtil.create(_user)));
	}

	@Test
	@TestInfo({"LPD-113649", "LPS-136784"})
	public void testHasDeletePermission() throws Exception {
		long commentId = _addComment();

		Assert.assertFalse(
			_discussionPermission.hasDeletePermission(
				PermissionCheckerFactoryUtil.create(_user), commentId));
		Assert.assertTrue(
			_discussionPermission.hasDeletePermission(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()),
				commentId));
	}

	@Test
	@TestInfo({"LPD-113649", "LPS-136784"})
	public void testHasUpdatePermission() throws Exception {
		long commentId = _addComment();

		Assert.assertFalse(
			_discussionPermission.hasUpdatePermission(
				PermissionCheckerFactoryUtil.create(_user), commentId));
		Assert.assertTrue(
			_discussionPermission.hasUpdatePermission(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()),
				commentId));
	}

	private long _addComment() throws Exception {
		return _commentManager.addComment(
			_user.getUserId(), _group.getGroupId(), BlogsEntry.class.getName(),
			_blogsEntry.getEntryId(), RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(
					_group, _user.getUserId())));
	}

	private boolean _hasAddPermission(PermissionChecker permissionChecker)
		throws Exception {

		return _discussionPermission.hasAddPermission(
			permissionChecker, _group.getCompanyId(), _group.getGroupId(),
			BlogsEntry.class.getName(), _blogsEntry.getEntryId());
	}

	private BlogsEntry _blogsEntry;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CommentManager _commentManager;

	@Inject
	private DiscussionPermission _discussionPermission;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
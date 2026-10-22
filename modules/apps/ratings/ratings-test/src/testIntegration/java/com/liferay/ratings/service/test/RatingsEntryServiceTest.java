/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;
import com.liferay.ratings.kernel.service.RatingsEntryServiceUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class RatingsEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user1 = UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER);

		_user2 = UserTestUtil.addUser();
	}

	@Test
	@TestInfo("LPD-110149")
	public void testDeleteEntry() throws Exception {
		_testDeleteEntry();
		_testDeleteEntryWithoutViewPermission();
	}

	@Test
	@TestInfo("LPD-110149")
	public void testUpdateEntry() throws Exception {
		_testUpdateEntry();
		_testUpdateEntryWithComment();
		_testUpdateEntryWithCommentWithoutViewPermission();
		_testUpdateEntryWithUnregisteredClassName();
		_testUpdateEntryWithoutAssetViewPermission();
		_testUpdateEntryWithoutModelResourcePermission();
		_testUpdateEntryWithoutViewPermission();
	}

	private BlogsEntry _addBlogsEntry(boolean viewable) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(viewable);
		serviceContext.setAddGuestPermissions(viewable);

		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private long _addComment(BlogsEntry blogsEntry) throws Exception {
		return CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId(),
			RandomTestUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId())));
	}

	private void _assertMustHaveViewPermission(
		PrincipalException.MustHavePermission mustHavePermission,
		String className, long classPK) {

		Assert.assertEquals(className, mustHavePermission.resourceName);
		Assert.assertEquals(classPK, mustHavePermission.resourceId);
		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW}, mustHavePermission.actionId);
	}

	private void _testDeleteEntry() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(true);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user1.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			RatingsEntryServiceUtil.deleteEntry(
				BlogsEntry.class.getName(), blogsEntry.getEntryId());
		}

		Assert.assertNull(
			RatingsEntryLocalServiceUtil.fetchEntry(
				_user1.getUserId(), BlogsEntry.class.getName(),
				blogsEntry.getEntryId()));
	}

	private void _testDeleteEntryWithoutViewPermission() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(false);

		RatingsEntryLocalServiceUtil.updateEntry(
			_user1.getUserId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), 1,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			_assertMustHaveViewPermission(
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> RatingsEntryServiceUtil.deleteEntry(
						BlogsEntry.class.getName(), blogsEntry.getEntryId())),
				BlogsEntry.class.getName(), blogsEntry.getEntryId());
		}

		Assert.assertNotNull(
			RatingsEntryLocalServiceUtil.fetchEntry(
				_user1.getUserId(), BlogsEntry.class.getName(),
				blogsEntry.getEntryId()));
	}

	private void _testUpdateEntry() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(true);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			RatingsEntry ratingsEntry = RatingsEntryServiceUtil.updateEntry(
				BlogsEntry.class.getName(), blogsEntry.getEntryId(), 1);

			Assert.assertEquals(1, ratingsEntry.getScore(), 0.001);
			Assert.assertEquals(_user1.getUserId(), ratingsEntry.getUserId());
		}
	}

	private void _testUpdateEntryWithComment() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(true);

		long commentId = _addComment(blogsEntry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			RatingsEntry ratingsEntry = RatingsEntryServiceUtil.updateEntry(
				_CLASS_NAME_MB_DISCUSSION, commentId, 1);

			Assert.assertEquals(1, ratingsEntry.getScore(), 0.001);
			Assert.assertEquals(commentId, ratingsEntry.getClassPK());
		}
	}

	private void _testUpdateEntryWithCommentWithoutViewPermission()
		throws Exception {

		BlogsEntry blogsEntry = _addBlogsEntry(false);

		long commentId = _addComment(blogsEntry);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			_assertMustHaveViewPermission(
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> RatingsEntryServiceUtil.updateEntry(
						_CLASS_NAME_MB_DISCUSSION, commentId, 1)),
				_CLASS_NAME_MB_DISCUSSION, commentId);
		}

		Assert.assertNull(
			RatingsStatsLocalServiceUtil.fetchStats(
				_CLASS_NAME_MB_DISCUSSION, commentId));
	}

	private void _testUpdateEntryWithUnregisteredClassName() throws Exception {
		String className = RandomTestUtil.randomString();

		long classPK = RandomTestUtil.randomLong();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			_assertMustHaveViewPermission(
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> RatingsEntryServiceUtil.updateEntry(
						className, classPK, 1)),
				className, classPK);
		}

		Assert.assertNull(
			RatingsStatsLocalServiceUtil.fetchStats(className, classPK));
	}

	private void _testUpdateEntryWithoutAssetViewPermission() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			_assertMustHaveViewPermission(
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> RatingsEntryServiceUtil.updateEntry(
						User.class.getName(), _user2.getUserId(), 1)),
				User.class.getName(), _user2.getUserId());
		}

		Assert.assertNull(
			RatingsStatsLocalServiceUtil.fetchStats(
				User.class.getName(), _user2.getUserId()));
	}

	private void _testUpdateEntryWithoutModelResourcePermission()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			RatingsEntry ratingsEntry = RatingsEntryServiceUtil.updateEntry(
				User.class.getName(), _user1.getUserId(), 1);

			Assert.assertEquals(1, ratingsEntry.getScore(), 0.001);
			Assert.assertEquals(_user1.getUserId(), ratingsEntry.getClassPK());
		}
	}

	private void _testUpdateEntryWithoutViewPermission() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(false);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			_assertMustHaveViewPermission(
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> RatingsEntryServiceUtil.updateEntry(
						BlogsEntry.class.getName(), blogsEntry.getEntryId(),
						1)),
				BlogsEntry.class.getName(), blogsEntry.getEntryId());
		}

		Assert.assertNull(
			RatingsStatsLocalServiceUtil.fetchStats(
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));
	}

	private static final String _CLASS_NAME_MB_DISCUSSION =
		"com.liferay.message.boards.model.MBDiscussion";

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

}
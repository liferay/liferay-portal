/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.subscriptions.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBDiscussionLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class CommentsSubscriptionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_creatorUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER);
	}

	@Test
	public void testCanSubscribeToCommentsWithViewPermissions()
		throws Exception {

		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			_creatorUser.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _creatorUser.getUserId()));

		Assert.assertTrue(
			_discussionPermission.hasSubscribePermission(
				PermissionCheckerFactoryUtil.create(_user),
				_group.getCompanyId(), _group.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));
	}

	@Test
	public void testCannotSubscribeToCommentsWithoutViewPermissions()
		throws Exception {

		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			_creatorUser.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _creatorUser.getUserId()));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, BlogsEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(blogsEntry.getEntryId()), ActionKeys.VIEW);

		Assert.assertFalse(
			_discussionPermission.hasSubscribePermission(
				PermissionCheckerFactoryUtil.create(
					UserLocalServiceUtil.getGuestUser(_group.getCompanyId())),
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));
	}

	@Test
	public void testSubscriptionMBDiscussionForAuthorWhenAddingMBMessage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _creatorUser.getUserId());

		BlogsTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			_creatorUser.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		MBDiscussionLocalServiceUtil.subscribeDiscussion(
			_creatorUser.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId());

		addDiscussionMessage(
			_creatorUser.getUserId(), serviceContext, blogsEntry);

		Assert.assertEquals(0, MailServiceTestUtil.getInboxSize());
	}

	@Test
	public void testSubscriptionMBDiscussionForAuthorWhenUpdatingMBMessage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, _creatorUser.getUserId());

		BlogsTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			_creatorUser.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		MBMessage mbMessage = addDiscussionMessage(
			_creatorUser.getUserId(), serviceContext, blogsEntry);

		MBDiscussionLocalServiceUtil.subscribeDiscussion(
			_creatorUser.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId());

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.UPDATE);

		MBMessageLocalServiceUtil.updateDiscussionMessage(
			_creatorUser.getUserId(), mbMessage.getMessageId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(50),
			serviceContext);

		Assert.assertEquals(0, MailServiceTestUtil.getInboxSize());
	}

	@Test
	public void testSubscriptionMBDiscussionWhenAddingMBMessage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _creatorUser.getUserId());

		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			_creatorUser.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		MBDiscussionLocalServiceUtil.subscribeDiscussion(
			_creatorUser.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId());

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		addDiscussionMessage(
			TestPropsValues.getUserId(), serviceContext, blogsEntry);

		Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());
	}

	@Test
	public void testSubscriptionMBDiscussionWhenUpdatingMBMessage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _creatorUser.getUserId());

		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			_creatorUser.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		MBMessage message = addDiscussionMessage(
			TestPropsValues.getUserId(), serviceContext, blogsEntry);

		MBDiscussionLocalServiceUtil.subscribeDiscussion(
			_user.getUserId(), _group.getGroupId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId());

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.UPDATE);

		MBMessageLocalServiceUtil.updateDiscussionMessage(
			_creatorUser.getUserId(), message.getMessageId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(50),
			serviceContext);

		Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());
	}

	protected MBMessage addDiscussionMessage(
			long userId, ServiceContext serviceContext, BlogsEntry entry)
		throws Exception {

		MBMessageDisplay messageDisplay =
			MBMessageLocalServiceUtil.getDiscussionMessageDisplay(
				TestPropsValues.getUserId(), _group.getGroupId(),
				BlogsEntry.class.getName(), entry.getEntryId(),
				WorkflowConstants.STATUS_APPROVED);

		MBThread thread = messageDisplay.getThread();

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		return MBMessageLocalServiceUtil.addDiscussionMessage(
			null, userId, RandomTestUtil.randomString(), _group.getGroupId(),
			BlogsEntry.class.getName(), entry.getEntryId(),
			thread.getThreadId(), MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@DeleteAfterTestRun
	private User _creatorUser;

	@Inject
	private DiscussionPermission _discussionPermission;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}
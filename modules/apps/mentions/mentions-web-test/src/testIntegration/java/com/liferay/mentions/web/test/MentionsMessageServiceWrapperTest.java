/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserNotificationEventTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-104086")
@RunWith(Arquillian.class)
public class MentionsMessageServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Test
	public void testUpdateStatus() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		MBMessageDisplay mbMessageDisplay =
			_mbMessageLocalService.getDiscussionMessageDisplay(
				TestPropsValues.getUserId(), group.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId(),
				WorkflowConstants.STATUS_APPROVED);

		MBThread mbThread = mbMessageDisplay.getThread();

		User user1 = UserTestUtil.addUser(group.getGroupId());
		User user2 = UserTestUtil.addUser(group.getGroupId());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addGroupUserGroup(group.getGroupId(), userGroup);

		_userLocalService.addUserGroupUser(userGroup.getUserGroupId(), user2);

		_mbMessageLocalService.addDiscussionMessage(
			null, TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), mbThread.getThreadId(),
			MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
			RandomTestUtil.randomString(),
			StringBundler.concat(
				"@", user1.getScreenName(),
				" <span class=\"mention mention-usergroup-",
				userGroup.getExternalReferenceCode(), "\">@",
				userGroup.getName(), "</span>"),
			serviceContext);

		List<UserNotificationEvent> userNotificationEvents =
			UserNotificationEventTestUtil.getUserNotificationEvents(
				user1.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationFeedEntry userNotificationFeedEntry =
			UserNotificationManagerUtil.interpret(
				StringPool.BLANK, userNotificationEvents.get(0),
				ServiceContextTestUtil.getServiceContext());

		String body = userNotificationFeedEntry.getBody();

		Assert.assertTrue(body.contains(" mentioned you in a comment in a "));

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user2.getUserId()));

		_blogsEntryLocalService.deleteEntry(blogsEntry);
		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	@TestInfo("LPD-117732")
	public void testUpdateStatusOnMessage() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		User user = UserTestUtil.addUser(group.getGroupId());

		MBMessage mbMessage = _mbMessageLocalService.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(),
			StringBundler.concat(
				"<span class=\"mention mention-user-",
				user.getExternalReferenceCode(), "\">@", user.getScreenName(),
				"</span>"),
			"html", Collections.emptyList(), false, 0.0, false, serviceContext);

		Assert.assertEquals(
			1,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_mbMessageLocalService.deleteMessage(mbMessage);
		_userLocalService.deleteUser(user);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	@TestInfo("LPD-117804")
	public void testUpdateStatusWithLayoutWithoutViewPermissionOnMessage()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(layout, MBPortletKeys.MESSAGE_BOARDS);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(group.getCompanyId()), group,
			layout);

		themeDisplay.setPpid(MBPortletKeys.MESSAGE_BOARDS);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);
		serviceContext.setRequest(mockHttpServletRequest);

		User user = UserTestUtil.addUser();

		MBMessage mbMessage = _mbMessageLocalService.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(),
			StringBundler.concat(
				"<span class=\"mention mention-user-",
				user.getExternalReferenceCode(), "\">@", user.getScreenName(),
				"</span>"),
			"html", Collections.emptyList(), false, 0.0, false, serviceContext);

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_mbMessageLocalService.deleteMessage(mbMessage);
		_userLocalService.deleteUser(user);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	@TestInfo("LPD-116828")
	public void testUpdateStatusWithoutViewPermission() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		MBMessageDisplay mbMessageDisplay =
			_mbMessageLocalService.getDiscussionMessageDisplay(
				TestPropsValues.getUserId(), group.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId(),
				WorkflowConstants.STATUS_APPROVED);

		MBThread mbThread = mbMessageDisplay.getThread();

		User user1 = UserTestUtil.addUser();
		User user2 = UserTestUtil.addUser();

		_mbMessageLocalService.addDiscussionMessage(
			null, TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), mbThread.getThreadId(),
			MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
			RandomTestUtil.randomString(),
			StringBundler.concat(
				"@", user1.getScreenName(),
				" <span class=\"mention mention-user-",
				user2.getExternalReferenceCode(), "\">@", user2.getScreenName(),
				"</span>"),
			serviceContext);

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user1.getUserId()));
		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user2.getUserId()));

		_blogsEntryLocalService.deleteEntry(blogsEntry);
		_userLocalService.deleteUser(user1);
		_userLocalService.deleteUser(user2);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	@TestInfo("LPD-117732")
	public void testUpdateStatusWithoutViewPermissionOnMessage()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		User user = UserTestUtil.addUser();

		MBMessage mbMessage = _mbMessageLocalService.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(),
			StringBundler.concat(
				"<span class=\"mention mention-user-",
				user.getExternalReferenceCode(), "\">@", user.getScreenName(),
				"</span>"),
			"html", Collections.emptyList(), false, 0.0, false, serviceContext);

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_mbMessageLocalService.deleteMessage(mbMessage);
		_userLocalService.deleteUser(user);

		GroupTestUtil.deleteGroup(group);
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private MBMessageLocalService _mbMessageLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
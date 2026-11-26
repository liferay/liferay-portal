/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Brian Wulbern
 */
@FeatureFlag("LPD-104086")
@RunWith(Arquillian.class)
public class MentionsBlogsEntryServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-117732")
	public void testUpdateStatus() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		User user = UserTestUtil.addUser(group.getGroupId());

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			StringBundler.concat(
				"<span class=\"mention mention-user-",
				user.getExternalReferenceCode(), "\">@", user.getScreenName(),
				"</span>"),
			serviceContext);

		Assert.assertEquals(
			1,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_blogsEntryLocalService.deleteEntry(blogsEntry);
		_userLocalService.deleteUser(user);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	@TestInfo("LPD-117804")
	public void testUpdateStatusWithLayoutWithoutViewPermission()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(layout, BlogsPortletKeys.BLOGS);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(group.getCompanyId()), group,
			layout);

		themeDisplay.setPpid(BlogsPortletKeys.BLOGS);

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

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			StringBundler.concat(
				"<span class=\"mention mention-user-",
				user.getExternalReferenceCode(), "\">@", user.getScreenName(),
				"</span>"),
			serviceContext);

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_blogsEntryLocalService.deleteEntry(blogsEntry);
		_userLocalService.deleteUser(user);

		GroupTestUtil.deleteGroup(group);
	}

	@Test
	@TestInfo("LPD-117732")
	public void testUpdateStatusWithoutViewPermission() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGuestPermissions(false);

		User user = UserTestUtil.addUser();

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			StringBundler.concat(
				"<span class=\"mention mention-user-",
				user.getExternalReferenceCode(), "\">@", user.getScreenName(),
				"</span>"),
			serviceContext);

		Assert.assertEquals(
			0,
			_userNotificationEventLocalService.getUserNotificationEventsCount(
				user.getUserId()));

		_blogsEntryLocalService.deleteEntry(blogsEntry);
		_userLocalService.deleteUser(user);

		GroupTestUtil.deleteGroup(group);
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
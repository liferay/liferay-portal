/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item.action.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class PreviewImageBlogsEntryContentDashboardItemActionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);
	}

	@Test
	public void testGetContentDashboardItemAction() throws Exception {
		ServiceContext originalServiceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			FileEntry fileEntry = _getTempFileEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			ImageSelector imageSelector = new ImageSelector(
				FileUtil.getBytes(fileEntry.getContentStream()),
				fileEntry.getTitle(), fileEntry.getMimeType(),
				StringPool.BLANK);

			BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new Date(), true, true,
				new String[0], StringPool.BLANK, imageSelector, imageSelector,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			ThemeDisplay themeDisplay = _getThemeDisplay(
				mockHttpServletRequest);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			ContentDashboardItemAction contentDashboardItemAction =
				_contentDashboardItemActionProvider.
					getContentDashboardItemAction(
						blogsEntry, mockHttpServletRequest);

			Assert.assertEquals(
				blogsEntry.getCoverImageURL(themeDisplay),
				contentDashboardItemAction.getURL());
		}
		finally {
			ServiceContextThreadLocal.pushServiceContext(
				originalServiceContext);
		}
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(
			"preview-image", _contentDashboardItemActionProvider.getKey());
	}

	@Test
	public void testGetType() {
		Assert.assertEquals(
			ContentDashboardItemAction.Type.PREVIEW_IMAGE,
			_contentDashboardItemActionProvider.getType());
	}

	@Test
	public void testIsShow() throws Exception {
		ServiceContext originalServiceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			FileEntry fileEntry = _getTempFileEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			ImageSelector imageSelector = new ImageSelector(
				FileUtil.getBytes(fileEntry.getContentStream()),
				fileEntry.getTitle(), fileEntry.getMimeType(),
				StringPool.BLANK);

			BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new Date(), true, true,
				new String[0], StringPool.BLANK, imageSelector, imageSelector,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			ThemeDisplay themeDisplay = _getThemeDisplay(
				mockHttpServletRequest);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			Assert.assertTrue(
				_contentDashboardItemActionProvider.isShow(
					blogsEntry, mockHttpServletRequest));
		}
		finally {
			ServiceContextThreadLocal.pushServiceContext(
				originalServiceContext);
		}
	}

	@Test
	public void testIsShowWithoutPermissions() throws Exception {
		User user = UserTestUtil.addUser();

		try {
			BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new Date(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			ThemeDisplay themeDisplay = _getThemeDisplay(
				mockHttpServletRequest);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			themeDisplay.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			Assert.assertTrue(
				!_contentDashboardItemActionProvider.isShow(
					blogsEntry, mockHttpServletRequest));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private FileEntry _getTempFileEntry(
			long userId, String title, ServiceContext serviceContext)
		throws PortalException {

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/content/dashboard/blogs/internal/item/action" +
				"/provider/test/dependencies/test.jpg");

		return TempFileEntryUtil.addTempFileEntry(
			serviceContext.getScopeGroupId(), userId,
			BlogsEntry.class.getName(), title, inputStream,
			MimeTypesUtil.getContentType(title));
	}

	private ThemeDisplay _getThemeDisplay(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.blogs.internal.item.action.provider.PreviewImageBlogsEntryContentDashboardItemActionProvider"
	)
	private ContentDashboardItemActionProvider
		_contentDashboardItemActionProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private UserLocalService _userLocalService;

}
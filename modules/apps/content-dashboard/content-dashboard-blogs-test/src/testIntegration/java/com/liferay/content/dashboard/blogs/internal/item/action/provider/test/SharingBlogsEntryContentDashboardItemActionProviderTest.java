/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item.action.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

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
public class SharingBlogsEntryContentDashboardItemActionProviderTest {

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

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testGetContentDashboardItemAction() throws Exception {
		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		ContentDashboardItemAction contentDashboardItemAction =
			_contentDashboardItemActionProvider.getContentDashboardItemAction(
				blogsEntry, mockHttpServletRequest);

		String url = contentDashboardItemAction.getURL();

		Assert.assertTrue(Validator.isNotNull(url));

		Assert.assertTrue(
			url.contains("param_classPK=" + blogsEntry.getEntryId()));
		Assert.assertTrue(url.contains("param_mvcPath=/sharing_button.jsp"));
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(
			"share", _contentDashboardItemActionProvider.getKey());
	}

	@Test
	public void testGetType() {
		Assert.assertEquals(
			ContentDashboardItemAction.Type.SHARING_BUTTON,
			_contentDashboardItemActionProvider.getType());
	}

	@Test
	public void testIsShow() throws Exception {
		BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockPortletRequest mockPortletRequest =
			new MockLiferayResourceRequest();

		mockPortletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		Assert.assertTrue(
			_contentDashboardItemActionProvider.isShow(
				blogsEntry, mockHttpServletRequest));
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
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.blogs.internal.item.action.provider.SharingBlogsEntryContentDashboardItemActionProvider"
	)
	private ContentDashboardItemActionProvider
		_contentDashboardItemActionProvider;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private UserLocalService _userLocalService;

}
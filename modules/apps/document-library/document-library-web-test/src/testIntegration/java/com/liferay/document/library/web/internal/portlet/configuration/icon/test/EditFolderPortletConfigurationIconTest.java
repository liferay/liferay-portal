/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.configuration.icon.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class EditFolderPortletConfigurationIconTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testIsShow() throws Exception {
		_testIsShow();
		_testIsShowRootFolderWithAdvancedUpdatePermission();
		_testIsShowRootFolderWithUpdatePermission();
	}

	private PortletRequest _getPortletRequest() throws Exception {
		PortletRequest portletRequest = new MockPortletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		portletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, httpServletRequest);

		portletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return portletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(LayoutTestUtil.addTypePortletLayout(_group));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private void _testIsShow() throws Exception {
		Assert.assertTrue(
			_portletConfigurationIcon.isShow(_getPortletRequest()));
	}

	private void _testIsShowRootFolderWithAdvancedUpdatePermission()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), "com.liferay.document.library",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			role.getRoleId(), new String[] {ActionKeys.ADVANCED_UPDATE});

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertTrue(
				_portletConfigurationIcon.isShow(_getPortletRequest()));
		}
	}

	private void _testIsShowRootFolderWithUpdatePermission() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), "com.liferay.document.library",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertFalse(
				_portletConfigurationIcon.isShow(_getPortletRequest()));
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.portlet.configuration.icon.EditFolderPortletConfigurationIcon"
	)
	private PortletConfigurationIcon _portletConfigurationIcon;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
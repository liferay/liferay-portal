/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.product.navigation.control.menu.manager.ProductNavigationControlMenuManager;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class ProductNavigationControlMenuManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testIsShowControlMenuWithAdministratorInContentLayout()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[0], true);

		Assert.assertTrue(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(
					Collections.emptyMap(), TestPropsValues.getUser())));
	}

	@Test
	public void testIsShowControlMenuWithAdministratorInContentLayoutLockedLayoutMVCRenderCommand()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[0], true);

		String mvcRenderCommandName =
			_portal.getPortletNamespace(LayoutAdminPortletKeys.GROUP_PAGES) +
				"mvcRenderCommandName";

		Assert.assertFalse(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(
					HashMapBuilder.put(
						mvcRenderCommandName, "/layout_admin/locked_layout"
					).build(),
					TestPropsValues.getUser())));
	}

	@Test
	public void testIsShowControlMenuWithAdministratorInContentLayoutPreviewMode()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[0], true);

		Assert.assertFalse(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(
					HashMapBuilder.put(
						"p_l_mode", Constants.PREVIEW
					).build(),
					TestPropsValues.getUser())));
	}

	@Test
	public void testIsShowControlMenuWithNormalUserWithoutRoleAccessInContentLayout()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[0], true);

		User user = UserTestUtil.addUser();

		Assert.assertFalse(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(Collections.emptyMap(), user)));
	}

	@Test
	public void testIsShowControlMenuWithNormalUserWithRoleAccessInContentLayout()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(),
			new String[] {String.valueOf(role.getRoleId())}, true);

		User user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user.getUserId(), role);

		Assert.assertTrue(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(Collections.emptyMap(), user)));
	}

	@Test
	public void testIsShowControlMenuWithRandomUserInAdminLayout()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[0], true);

		Assert.assertTrue(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(
					Collections.emptyMap(), TestPropsValues.getUser())));
	}

	@Test
	public void testIsShowControlMenuWithSiteAdministratorInContentLayout()
		throws Exception {

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(), new String[0], true);

		Assert.assertTrue(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(
					Collections.emptyMap(), TestPropsValues.getUser())));
	}

	@Test
	public void testIsShowControlMenuWithUserWithUserGroupWithRoleAccessInContentLayout()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_roleLocalService.addGroupRole(userGroup.getGroupId(), role);

		_menuAccessConfigurationManager.updateMenuAccessConfiguration(
			_group.getGroupId(),
			new String[] {String.valueOf(role.getRoleId())}, true);

		User user = UserTestUtil.addUser();

		_userLocalService.addUserGroupUser(userGroup.getUserGroupId(), user);

		Assert.assertTrue(
			_productNavigationControlMenuManager.isShowControlMenu(
				_getHttpServletRequest(Collections.emptyMap(), user)));
	}

	private HttpServletRequest _getHttpServletRequest(
			Map<String, ?> params, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(_layout);
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameters(params);

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

	@Inject
	private Portal _portal;

	@Inject
	private ProductNavigationControlMenuManager
		_productNavigationControlMenuManager;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class PrivateGroupFriendlyURLServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group, true);

		Class<?> clazz = _privateGroupFriendlyURLServlet.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		clazz = classLoader.loadClass(
			"com.liferay.friendly.url.internal.servlet.FriendlyURLServlet" +
				"$Redirect");

		_redirectConstructor = clazz.getConstructor(String.class);

		_initPrivateGroupFriendlyURLServlet();
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetRedirectWithPrivateLayoutForAdminUser()
		throws Throwable {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		try {
			Assert.assertEquals(
				_redirectConstructor.newInstance(_getURL(_layout)),
				ReflectionTestUtil.invoke(
					_privateGroupFriendlyURLServlet, "getRedirect",
					new Class<?>[] {
						HttpServletRequest.class, HttpServletResponse.class,
						String.class
					},
					new MockHttpServletRequest(), new MockHttpServletResponse(),
					_getPath(_group, _layout)));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test(expected = NoSuchLayoutException.class)
	public void testGetRedirectWithPrivateLayoutForGuestUser()
		throws Throwable {

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(company.getGuestUser()));

		try {
			ReflectionTestUtil.invoke(
				_privateGroupFriendlyURLServlet, "getRedirect",
				new Class<?>[] {
					HttpServletRequest.class, HttpServletResponse.class,
					String.class
				},
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				_getPath(_group, _layout));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test(expected = NoSuchLayoutException.class)
	public void testGetRedirectWithPrivateLayoutForRoleWithoutPermissions()
		throws Throwable {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		try {
			RoleTestUtil.addResourcePermission(
				role.getName(), Layout.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_layout.getPlid()), ActionKeys.VIEW);

			Assert.assertEquals(
				_redirectConstructor.newInstance(_getURL(_layout)),
				ReflectionTestUtil.invoke(
					_privateGroupFriendlyURLServlet, "getRedirect",
					new Class<?>[] {
						HttpServletRequest.class, HttpServletResponse.class,
						String.class
					},
					new MockHttpServletRequest(), new MockHttpServletResponse(),
					_getPath(_group, _layout)));

			RoleTestUtil.removeResourcePermission(
				role.getName(), Layout.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_layout.getPlid()), ActionKeys.VIEW);

			ReflectionTestUtil.invoke(
				_privateGroupFriendlyURLServlet, "getRedirect",
				new Class<?>[] {
					HttpServletRequest.class, HttpServletResponse.class,
					String.class
				},
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				_getPath(_group, _layout));
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testGetRedirectWithPrivateLayoutForSiteMember()
		throws Throwable {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER)));

		try {
			Assert.assertEquals(
				_redirectConstructor.newInstance(_getURL(_layout)),
				ReflectionTestUtil.invoke(
					_privateGroupFriendlyURLServlet, "getRedirect",
					new Class<?>[] {
						HttpServletRequest.class, HttpServletResponse.class,
						String.class
					},
					new MockHttpServletRequest(), new MockHttpServletResponse(),
					_getPath(_group, _layout)));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test(expected = NoSuchLayoutException.class)
	public void testGetRedirectWithPrivateLayoutForUserWithoutPermissions()
		throws Throwable {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(UserTestUtil.addUser()));

		try {
			ReflectionTestUtil.invoke(
				_privateGroupFriendlyURLServlet, "getRedirect",
				new Class<?>[] {
					HttpServletRequest.class, HttpServletResponse.class,
					String.class
				},
				new MockHttpServletRequest(), new MockHttpServletResponse(),
				_getPath(_group, _layout));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private String _getPath(Group group, Layout layout) {
		return group.getFriendlyURL() + layout.getFriendlyURL();
	}

	private String _getURL(Layout layout) {
		return "/c/portal/layout?p_l_id=" + layout.getPlid() +
			"&p_v_l_s_g_id=0";
	}

	private void _initPrivateGroupFriendlyURLServlet() throws Exception {
		if (_privateGroupFriendlyURLServlet.getServletConfig() != null) {
			return;
		}

		MockServletConfig mockServletConfig = new MockServletConfig(
			ServletContextPool.get(_portal.getServletContextName()));

		mockServletConfig.addInitParameter(
			"servlet.init.private", Boolean.TRUE.toString());
		mockServletConfig.addInitParameter(
			"servlet.init.user", Boolean.FALSE.toString());

		_privateGroupFriendlyURLServlet.init(mockServletConfig);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.friendly.url.internal.servlet.PrivateGroupFriendlyURLServlet"
	)
	private Servlet _privateGroupFriendlyURLServlet;

	private Constructor<?> _redirectConstructor;

	@Inject
	private UserLocalService _userLocalService;

}
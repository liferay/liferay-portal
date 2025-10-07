/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutQueryStringComposite;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class PortalImplGetLayoutFriendlyURLSeparatorCompositeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testNullFriendlyURL() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addTypePortletLayout(_group);

		User user = _userLocalService.fetchGuestUser(_group.getCompanyId());

		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			new MockHttpServletRequest(Method.GET, "/"), layout, false, user);
		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			new MockHttpServletRequest(Method.GET, "/"), layout, true, user);
	}

	@Test
	public void testNullFriendlyURLFirstLayoutWithoutGuestPermission()
		throws Exception {

		_addLayoutWithoutPermission();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addTypePortletLayout(_group);

		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			new MockHttpServletRequest(Method.GET, "/"), layout, false,
			_userLocalService.fetchGuestUser(_group.getCompanyId()));
	}

	@Test
	public void testNullFriendlyURLFirstLayoutWithoutGuestPermissionLoginPromptEnabled()
		throws Exception {

		Layout layout = _addLayoutWithoutPermission();

		LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addTypePortletLayout(_group);

		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			new MockHttpServletRequest(Method.GET, "/"), layout, true,
			_userLocalService.fetchGuestUser(_group.getCompanyId()));
	}

	@Test
	public void testNullFriendlyURLFirstLayoutWithoutPermission()
		throws Exception {

		_addLayoutWithoutPermission(true);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addTypePortletLayout(_group);

		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			new MockHttpServletRequest(Method.GET, "/"), layout, false,
			UserTestUtil.addUser(_group.getGroupId()));
	}

	@Test
	public void testNullFriendlyURLFirstLayoutWithoutPermissionLoginPromptEnabled()
		throws Exception {

		_addLayoutWithoutPermission(true);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addTypePortletLayout(_group);

		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			new MockHttpServletRequest(Method.GET, "/"), layout, true,
			UserTestUtil.addUser(_group.getGroupId()));
	}

	@Test
	@TestInfo("LPD-62227")
	public void testNullFriendlyURLFirstLayoutWithoutPermissionNoSuchLayoutException()
		throws Exception {

		_addLayoutWithoutPermission(true);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addTypePortletLayout(_group);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(Method.GET, "/");

		mockHttpServletRequest.setAttribute(
			NoSuchLayoutException.class.getName(), Boolean.TRUE.toString());

		User guestUser = _userLocalService.fetchGuestUser(
			_group.getCompanyId());

		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			mockHttpServletRequest, layout, false, guestUser);
		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			mockHttpServletRequest, layout, true, guestUser);

		User user = UserTestUtil.addUser(_group.getGroupId());

		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			mockHttpServletRequest, layout, false, user);
		_assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			mockHttpServletRequest, layout, true, user);
	}

	private Layout _addLayoutWithoutPermission() throws Exception {
		return _addLayoutWithoutPermission(false);
	}

	private Layout _addLayoutWithoutPermission(boolean removeSiteMember)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), ActionKeys.VIEW);

		if (removeSiteMember) {
			RoleTestUtil.removeResourcePermission(
				RoleConstants.SITE_MEMBER, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), ActionKeys.VIEW);
		}

		return layout;
	}

	private void _assertGetActualLayoutQueryStringCompositeWithNullFriendlyURL(
			HttpServletRequest httpServletRequest, Layout layout,
			boolean promptEnabled, User user)
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID_AUTH_LOGIN_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"promptEnabled", promptEnabled
						).build())) {

			UserTestUtil.setUser(user);

			LayoutQueryStringComposite layoutQueryStringComposite =
				_portal.getActualLayoutQueryStringComposite(
					_group.getGroupId(), false, null, Collections.emptyMap(),
					HashMapBuilder.<String, Object>put(
						"request", httpServletRequest
					).build());

			Layout curLayout = layoutQueryStringComposite.getLayout();

			Assert.assertEquals(layout.getPlid(), curLayout.getPlid());
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	private static final String _PID_AUTH_LOGIN_CONFIGURATION =
		"com.liferay.login.web.internal.configuration.AuthLoginConfiguration";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}
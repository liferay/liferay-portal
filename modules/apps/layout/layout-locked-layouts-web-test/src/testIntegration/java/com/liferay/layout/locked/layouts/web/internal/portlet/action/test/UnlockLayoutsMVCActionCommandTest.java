/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class UnlockLayoutsMVCActionCommandTest {

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

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testCannotUnlockLockedPageAsUserWithNonadminRole()
		throws Exception {

		_layoutLockManager.getLock(_layout, TestPropsValues.getUserId());

		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		try {
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				new MockLiferayPortletActionRequest();

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setPermissionChecker(
				PermissionThreadLocal.getPermissionChecker());
			themeDisplay.setScopeGroupId(_group.getGroupId());

			mockLiferayPortletActionRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			mockLiferayPortletActionRequest.setParameter(
				"plid", String.valueOf(_layout.getPlid()));

			ReflectionTestUtil.invoke(
				_mvcActionCommand, "doProcessAction",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testUnlockLockedPages() throws Exception {
		_layoutLockManager.getLock(_layout, TestPropsValues.getUserId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"plid", String.valueOf(_layout.getPlid()));

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"unlockLayoutsRequestProcessed"));
	}

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject(filter = "mvc.command.name=/layout_locked_layouts/unlock_layouts")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private UserLocalService _userLocalService;

}
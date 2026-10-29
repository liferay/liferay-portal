/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class DepotAdminSelectRoleDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFilterGroupRolesWithCompanyAdmin() throws Exception {
		DepotAdminSelectRoleDisplayContext.Step2 step2 =
			new DepotAdminSelectRoleDisplayContext.Step2(
				_mockGroup(true), _mockRenderRequest(),
				Mockito.mock(RenderResponse.class), Mockito.mock(User.class));

		List<Role> roles = ReflectionTestUtil.invoke(
			step2, "_filterGroupRoles", new Class<?>[] {List.class},
			Arrays.asList(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR),
				_mockRole(
					DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER),
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER),
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_MEMBER),
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_OWNER)));

		Assert.assertEquals(
			Arrays.asList(
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
				DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
				DepotRolesConstants.ASSET_LIBRARY_OWNER),
			ListUtil.toList(roles, Role::getName));
	}

	@Test
	public void testGetDepotGroups() throws Exception {
		User user = Mockito.mock(User.class);

		Group depotGroup = _mockGroup(true);
		Group siteGroup = _mockGroup(false);

		Mockito.when(
			user.getGroups()
		).thenReturn(
			Arrays.asList(depotGroup, siteGroup)
		);

		DepotAdminSelectRoleDisplayContext.Step1 step1 =
			new DepotAdminSelectRoleDisplayContext.Step1(
				_mockRenderRequest(), Mockito.mock(RenderResponse.class), user);

		Assert.assertEquals(
			Collections.singletonList(depotGroup),
			ReflectionTestUtil.invoke(
				step1, "_getDepotGroups", new Class<?>[0]));
	}

	private Group _mockGroup(boolean depot) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			depot
		);

		return group;
	}

	private RenderRequest _mockRenderRequest() {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.isCompanyAdmin()
		).thenReturn(
			true
		);

		themeDisplay.setPermissionChecker(permissionChecker);

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		return renderRequest;
	}

	private Role _mockRole(String name) {
		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			name
		);

		return role;
	}

}
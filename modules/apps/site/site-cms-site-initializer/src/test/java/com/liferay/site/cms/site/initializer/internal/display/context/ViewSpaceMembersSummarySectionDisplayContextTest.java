/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Roberto Díaz
 */
public class ViewSpaceMembersSummarySectionDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-89584")
	public void testGetCreationMenu() throws Exception {
		CreationMenu creationMenu = _getCreationMenu(false);

		Assert.assertTrue(creationMenu.isEmpty());

		creationMenu = _getCreationMenu(true);

		Assert.assertFalse(creationMenu.isEmpty());
	}

	private CreationMenu _getCreationMenu(boolean hasAssignMembersPermission)
		throws Exception {

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		Mockito.when(
			groupLocalService.getGroup(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(Group.class)
		);

		ModelResourcePermission<Group> groupModelResourcePermission =
			Mockito.mock(ModelResourcePermission.class);

		Mockito.when(
			groupModelResourcePermission.contains(
				Mockito.any(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			hasAssignMembersPermission
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());

		ViewSpaceMembersSummarySectionDisplayContext
			viewSpaceMembersSummarySectionDisplayContext =
				new ViewSpaceMembersSummarySectionDisplayContext(
					RandomTestUtil.randomLong(), groupLocalService,
					groupModelResourcePermission, mockHttpServletRequest,
					Mockito.mock(Language.class),
					Mockito.mock(UserGroupLocalService.class),
					Mockito.mock(UserLocalService.class));

		return viewSpaceMembersSummarySectionDisplayContext.getCreationMenu();
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class ProductMenuSidebarStagingDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetProps() throws Exception {
		Group group = Mockito.mock(Group.class);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.isStagingGroup()
		).thenReturn(
			true
		);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		ReflectionTestUtil.setFieldValue(
			themeDisplay, "_permissionChecker", permissionChecker);

		ReflectionTestUtil.setFieldValue(themeDisplay, "_scopeGroup", group);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		GroupURLProvider groupURLProvider = Mockito.mock(
			GroupURLProvider.class);

		Group liveGroup = Mockito.mock(Group.class);

		String liveURL = RandomTestUtil.randomString();

		Mockito.when(
			groupURLProvider.getLiveGroupURL(
				Mockito.eq(liveGroup), Mockito.nullable(PortletRequest.class))
		).thenReturn(
			liveURL
		);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					ProductMenuSidebarStagingDisplayContext.class,
					"_groupURLProviderSnapshot",
					new Snapshot<GroupURLProvider>(
						ProductMenuSidebarStagingDisplayContext.class,
						GroupURLProvider.class) {

						@Override
						public GroupURLProvider get() {
							return groupURLProvider;
						}

					});
			MockedStatic<GroupPermissionUtil> groupPermissionUtilMockedStatic =
				Mockito.mockStatic(GroupPermissionUtil.class);
			MockedStatic<StagingUtil> stagingUtilMockedStatic =
				Mockito.mockStatic(StagingUtil.class)) {

			groupPermissionUtilMockedStatic.when(
				() -> GroupPermissionUtil.contains(
					permissionChecker, group, ActionKeys.MANAGE_STAGING)
			).thenReturn(
				true
			);

			stagingUtilMockedStatic.when(
				() -> StagingUtil.getLiveGroup(groupId)
			).thenReturn(
				liveGroup
			);

			ProductMenuSidebarStagingDisplayContext
				productMenuSidebarStagingDisplayContext =
					new ProductMenuSidebarStagingDisplayContext(
						httpServletRequest);

			Map<String, Object> props =
				productMenuSidebarStagingDisplayContext.getProps();

			Assert.assertEquals("staging", props.get("current"));
			Assert.assertEquals(liveURL, props.get("liveURL"));
			Assert.assertEquals(Boolean.FALSE, props.get("remote"));
			Assert.assertEquals(StringPool.BLANK, props.get("stagingURL"));
		}
	}

}
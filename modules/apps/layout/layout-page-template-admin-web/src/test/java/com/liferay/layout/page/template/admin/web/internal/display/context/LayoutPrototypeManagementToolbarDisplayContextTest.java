/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class LayoutPrototypeManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpHttpServletRequest();
		_setUpPermissionChecks();
	}

	@After
	public void tearDown() {
		_portalPermissionUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-89086")
	public void testIsShowCreationMenu() throws Exception {
		for (boolean featureFlagEnabled : new boolean[] {false, true}) {
			try (MockedStatic<FeatureFlagManagerUtil>
					featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
						FeatureFlagManagerUtil.class)) {

				featureFlagManagerUtilMockedStatic.when(
					() -> FeatureFlagManagerUtil.isEnabled(
						Mockito.anyLong(), Mockito.eq("LPD-76864"))
				).thenReturn(
					featureFlagEnabled
				);

				LayoutPrototypeManagementToolbarDisplayContext
					layoutPrototypeManagementToolbarDisplayContext =
						new LayoutPrototypeManagementToolbarDisplayContext(
							_httpServletRequest,
							_getMockLiferayPortletRenderRequest(),
							new MockLiferayPortletRenderResponse(),
							Mockito.mock(LayoutPrototypeDisplayContext.class));

				Assert.assertEquals(
					featureFlagEnabled,
					layoutPrototypeManagementToolbarDisplayContext.
						isShowCreationMenu());
			}
		}
	}

	private MockLiferayPortletRenderRequest
		_getMockLiferayPortletRenderRequest() {

		return new MockLiferayPortletRenderRequest() {

			@Override
			public Portlet getPortlet() {
				Portlet portlet = Mockito.mock(Portlet.class);

				PortletApp portletApp = Mockito.mock(PortletApp.class);

				Mockito.when(
					portlet.getPortletApp()
				).thenReturn(
					portletApp
				);

				return portlet;
			}

		};
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpPermissionChecks() {
		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		_portalPermissionUtilMockedStatic.when(
			() -> PortalPermissionUtil.contains(
				Mockito.any(PermissionChecker.class),
				Mockito.eq(ActionKeys.ADD_LAYOUT_PROTOTYPE))
		).thenReturn(
			true
		);
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<PortalPermissionUtil>
		_portalPermissionUtilMockedStatic = Mockito.mockStatic(
			PortalPermissionUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
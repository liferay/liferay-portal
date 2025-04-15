/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
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
 * @author Eudaldo Alonso
 */
public class MasterLayoutManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpHttpServletRequest();
		_setUpLayoutPageTemplateEntryPermission();
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_layoutPageTemplateEntryPermissionMockedStatic.close();
	}

	@Test
	@TestInfo("LPS-102207")
	public void testGetAvailableActionsForDraftLayoutPageTemplateEntry()
		throws Exception {

		MasterLayoutManagementToolbarDisplayContext
			masterLayoutManagementToolbarDisplayContext =
				new MasterLayoutManagementToolbarDisplayContext(
					_httpServletRequest, _getMockLiferayPortletActionRequest(),
					new MockLiferayPortletRenderResponse(),
					Mockito.mock(MasterLayoutDisplayContext.class));

		Mockito.when(
			_layoutPageTemplateEntry.isDraft()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			Validator.isNull(
				masterLayoutManagementToolbarDisplayContext.getAvailableActions(
					_layoutPageTemplateEntry)));
	}

	private MockLiferayPortletActionRequest
		_getMockLiferayPortletActionRequest() {

		return new MockLiferayPortletActionRequest() {

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

	private void _setUpLayoutPageTemplateEntryPermission() {
		_layoutPageTemplateEntryPermissionMockedStatic.when(
			() -> LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutPageTemplateEntry,
				ActionKeys.DELETE)
		).thenReturn(
			false
		);
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry =
		Mockito.mock(LayoutPageTemplateEntry.class);
	private final MockedStatic<LayoutPageTemplateEntryPermission>
		_layoutPageTemplateEntryPermissionMockedStatic = Mockito.mockStatic(
			LayoutPageTemplateEntryPermission.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
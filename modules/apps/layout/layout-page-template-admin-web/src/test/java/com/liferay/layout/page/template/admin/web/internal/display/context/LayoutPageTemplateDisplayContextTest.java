/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

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
public class LayoutPageTemplateDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-104842")
	public void testIsHideCollectionsPanel() {
		_testIsHideCollectionsPanel(false);
		_testIsHideCollectionsPanel(true);
	}

	private void _setUpDesignLibraryScope(boolean designLibraryScope) {
		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			designLibraryScope
		);
	}

	private void _setUpThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	private void _testIsHideCollectionsPanel(boolean designLibraryScope) {
		_setUpDesignLibraryScope(designLibraryScope);

		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext =
			new LayoutPageTemplateDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Assert.assertEquals(
			designLibraryScope,
			layoutPageTemplateDisplayContext.isHideCollectionsPanel());
	}

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);

}
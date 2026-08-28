/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.content.internal.events;

import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ColorSchemeFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
public class HistoryLayoutModeThemePreActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_historyLayoutModeThemePreAction =
			new HistoryLayoutModeThemePreAction();

		ReflectionTestUtil.setFieldValue(
			_historyLayoutModeThemePreAction, "_themeLocalService",
			_themeLocalService);
	}

	@Test
	@TestInfo("LPD-103339")
	public void testRun() throws Exception {
		_historyLayoutModeThemePreAction.run(
			_mockHttpServletRequest, _mockHttpServletResponse);

		Mockito.verifyNoInteractions(_themeLocalService);

		_setUpThemeDisplay();

		_mockHttpServletRequest.setParameter(
			"p_l_mode", RandomTestUtil.randomString());

		_historyLayoutModeThemePreAction.run(
			_mockHttpServletRequest, _mockHttpServletResponse);

		Mockito.verifyNoInteractions(_themeLocalService);
		Mockito.verifyNoInteractions(_themeDisplay);

		_mockHttpServletRequest.setParameter("p_l_mode", Constants.HISTORY);

		_historyLayoutModeThemePreAction.run(
			_mockHttpServletRequest, _mockHttpServletResponse);

		Mockito.verifyNoInteractions(_themeLocalService);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			_themeDisplay.getLayout()
		).thenReturn(
			layout
		);

		_historyLayoutModeThemePreAction.run(
			_mockHttpServletRequest, _mockHttpServletResponse);

		Mockito.verifyNoInteractions(_themeLocalService);

		Mockito.when(
			layout.isDraftLayout()
		).thenReturn(
			true
		);

		_historyLayoutModeThemePreAction.run(
			_mockHttpServletRequest, _mockHttpServletResponse);

		Mockito.verifyNoInteractions(_themeLocalService);

		Mockito.when(
			layout.isTypeContent()
		).thenReturn(
			true
		);

		Theme theme = _getTheme();

		ColorScheme colorScheme = _getColorScheme(theme.getThemeId());

		try (MockedStatic<ColorSchemeFactoryUtil>
				colorSchemeFactoryUtilMockedStatic =
					_getColorSchemeFactoryUtilMockedStatic(
						colorScheme.getColorSchemeId());
			MockedStatic<PrefsPropsUtil> prefsPropsUtilMockedStatic =
				_getPrefsPropsUtilMockedStatic(theme.getThemeId())) {

			_historyLayoutModeThemePreAction.run(
				_mockHttpServletRequest, _mockHttpServletResponse);
		}

		Assert.assertEquals(
			colorScheme,
			_mockHttpServletRequest.getAttribute(WebKeys.COLOR_SCHEME));

		Assert.assertEquals(
			theme, _mockHttpServletRequest.getAttribute(WebKeys.THEME));

		Mockito.verify(
			_themeDisplay
		).setLookAndFeel(
			theme, colorScheme
		);
	}

	private ColorScheme _getColorScheme(String themeId) {
		ColorScheme colorScheme = Mockito.mock(ColorScheme.class);

		Mockito.when(
			colorScheme.getColorSchemeId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_themeLocalService.getColorScheme(
				_themeDisplay.getCompanyId(), themeId,
				colorScheme.getColorSchemeId())
		).thenReturn(
			colorScheme
		);

		return colorScheme;
	}

	private MockedStatic<ColorSchemeFactoryUtil>
		_getColorSchemeFactoryUtilMockedStatic(String colorSchemeId) {

		MockedStatic<ColorSchemeFactoryUtil>
			colorSchemeFactoryUtilMockedStatic = Mockito.mockStatic(
				ColorSchemeFactoryUtil.class);

		colorSchemeFactoryUtilMockedStatic.when(
			ColorSchemeFactoryUtil::getDefaultRegularColorSchemeId
		).thenReturn(
			colorSchemeId
		);

		return colorSchemeFactoryUtilMockedStatic;
	}

	private MockedStatic<PrefsPropsUtil> _getPrefsPropsUtilMockedStatic(
		String themeId) {

		MockedStatic<PrefsPropsUtil> prefsPropsUtilMockedStatic =
			Mockito.mockStatic(PrefsPropsUtil.class);

		prefsPropsUtilMockedStatic.when(
			() -> PrefsPropsUtil.getString(
				_themeDisplay.getCompanyId(),
				PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID)
		).thenReturn(
			themeId
		);

		return prefsPropsUtilMockedStatic;
	}

	private Theme _getTheme() {
		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getThemeId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_themeLocalService.getTheme(
				_themeDisplay.getCompanyId(), theme.getThemeId())
		).thenReturn(
			theme
		);

		return theme;
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	private HistoryLayoutModeThemePreAction _historyLayoutModeThemePreAction;
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final ThemeLocalService _themeLocalService = Mockito.mock(
		ThemeLocalService.class);

}
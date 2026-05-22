/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.servlet.taglib.util;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

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
public class LayoutActionDropdownItemsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);

		_layoutActionDropdownItemsProvider =
			new LayoutActionDropdownItemsProvider(
				_httpServletRequest, null, null, null, null);
	}

	@Test
	@TestInfo("LPD-89086")
	public void testIsShowMakeACopyAction() throws Exception {
		_assertIsShowMakeACopyAction(LayoutConstants.TYPE_CONTENT, false, true);
		_assertIsShowMakeACopyAction(LayoutConstants.TYPE_CONTENT, true, true);
		_assertIsShowMakeACopyAction(
			LayoutConstants.TYPE_FULL_PAGE_APPLICATION, false, false);
		_assertIsShowMakeACopyAction(
			LayoutConstants.TYPE_FULL_PAGE_APPLICATION, true, true);
		_assertIsShowMakeACopyAction(LayoutConstants.TYPE_PANEL, false, false);
		_assertIsShowMakeACopyAction(LayoutConstants.TYPE_PANEL, true, true);
		_assertIsShowMakeACopyAction(
			LayoutConstants.TYPE_PORTLET, false, false);
		_assertIsShowMakeACopyAction(LayoutConstants.TYPE_PORTLET, true, true);
	}

	private void _assertIsShowMakeACopyAction(
			String type, boolean featureFlagEnabled, boolean expected)
		throws Exception {

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-76864"))
			).thenReturn(
				featureFlagEnabled
			);

			Layout layout = Mockito.mock(Layout.class);

			Mockito.when(
				layout.getType()
			).thenReturn(
				type
			);

			Assert.assertEquals(
				expected,
				ReflectionTestUtil.invoke(
					_layoutActionDropdownItemsProvider,
					"_isShowMakeACopyAction", new Class<?>[] {Layout.class},
					layout));
		}
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private LayoutActionDropdownItemsProvider
		_layoutActionDropdownItemsProvider;

}
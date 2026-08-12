/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.product.navigation.control.menu;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Ambrín Chaudhary
 */
public class OmniSearchProductNavigationControlMenuEntryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_featureFlagManagerUtilMockedStatic.close();
	}

	@Test
	public void testIsShow() throws Exception {
		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-78171"))
		).thenReturn(
			true
		);

		Assert.assertFalse(
			_omniSearchProductNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(Constants.EDIT, true)));
		Assert.assertFalse(
			_omniSearchProductNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(Constants.HISTORY, true)));
		Assert.assertFalse(
			_omniSearchProductNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(Constants.VIEW, false)));
		Assert.assertTrue(
			_omniSearchProductNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(Constants.VIEW, true)));

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-78171"))
		).thenReturn(
			false
		);

		Assert.assertFalse(
			_omniSearchProductNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(Constants.VIEW, true)));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		String layoutMode, boolean signedIn) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			signedIn
		);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter("p_l_mode", layoutMode);

		return mockHttpServletRequest;
	}

	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);
	private final OmniSearchProductNavigationControlMenuEntry
		_omniSearchProductNavigationControlMenuEntry =
			new OmniSearchProductNavigationControlMenuEntry();

}
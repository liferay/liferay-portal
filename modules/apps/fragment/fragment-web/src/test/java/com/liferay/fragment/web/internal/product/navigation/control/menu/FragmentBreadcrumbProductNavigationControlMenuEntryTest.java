/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.product.navigation.control.menu;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.web.internal.util.DesignLibraryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuWebKeys;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentBreadcrumbProductNavigationControlMenuEntryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getPortletDisplay()
		).thenReturn(
			_portletDisplay
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
	}

	@Test
	public void testIsShow() throws Exception {
		_testIsShowWhenNotInFragmentPortlet();
		_testIsShowWhenScopeIsNotDesignLibrary();
		_testIsShowInFragmentPortletAndDesignLibraryScope();
	}

	private void _testIsShowInFragmentPortletAndDesignLibraryScope()
		throws Exception {

		Mockito.when(
			_portletDisplay.getPortletName()
		).thenReturn(
			FragmentPortletKeys.FRAGMENT
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_fragmentBreadcrumbProductNavigationControlMenuEntry.isShow(
				_mockHttpServletRequest));

		Assert.assertEquals(
			Boolean.TRUE,
			_mockHttpServletRequest.getAttribute(
				ProductNavigationControlMenuWebKeys.HIDE_PORTLET_HEADER));
	}

	private void _testIsShowWhenNotInFragmentPortlet() throws Exception {
		Mockito.when(
			_portletDisplay.getPortletName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			true
		);

		Assert.assertFalse(
			_fragmentBreadcrumbProductNavigationControlMenuEntry.isShow(
				_mockHttpServletRequest));

		Assert.assertNull(
			_mockHttpServletRequest.getAttribute(
				ProductNavigationControlMenuWebKeys.HIDE_PORTLET_HEADER));
	}

	private void _testIsShowWhenScopeIsNotDesignLibrary() throws Exception {
		Mockito.when(
			_portletDisplay.getPortletName()
		).thenReturn(
			FragmentPortletKeys.FRAGMENT
		);

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			false
		);

		Assert.assertFalse(
			_fragmentBreadcrumbProductNavigationControlMenuEntry.isShow(
				_mockHttpServletRequest));

		Assert.assertNull(
			_mockHttpServletRequest.getAttribute(
				ProductNavigationControlMenuWebKeys.HIDE_PORTLET_HEADER));
	}

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final FragmentBreadcrumbProductNavigationControlMenuEntry
		_fragmentBreadcrumbProductNavigationControlMenuEntry =
			new FragmentBreadcrumbProductNavigationControlMenuEntry();
	private final Group _group = Mockito.mock(Group.class);
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final PortletDisplay _portletDisplay = Mockito.mock(
		PortletDisplay.class);

}
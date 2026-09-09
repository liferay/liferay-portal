/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.servlet.taglib;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.control.menu.manager.ProductNavigationControlMenuManager;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Thiago Buarque
 */
public class CMSToolbarOmniSearchDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_requestDispatcher = Mockito.mock(RequestDispatcher.class);

		_servletContext = Mockito.mock(ServletContext.class);

		Mockito.when(
			_servletContext.getRequestDispatcher("/cms_toolbar.jsp")
		).thenReturn(
			_requestDispatcher
		);

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-78171"))
		).thenReturn(
			true
		);
	}

	@After
	public void tearDown() {
		_featureFlagManagerUtilMockedStatic.close();
	}

	@Test
	public void testIncludeWhenControlMenuIsShown() throws Exception {
		_include(_getMockHttpServletRequest(true, true), true);

		_assertNotIncluded();
	}

	@Test
	public void testIncludeWhenFeatureFlagIsDisabled() throws Exception {
		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-78171"))
		).thenReturn(
			false
		);

		_include(_getMockHttpServletRequest(true, true), false);

		_assertNotIncluded();
	}

	@Test
	public void testIncludeWhenGroupIsCMS() throws Exception {
		_include(_getMockHttpServletRequest(true, true), false);

		Mockito.verify(
			_requestDispatcher, Mockito.times(1)
		).include(
			Mockito.any(), Mockito.any()
		);
	}

	@Test
	public void testIncludeWhenGroupIsNotCMS() throws Exception {
		_include(_getMockHttpServletRequest(false, true), false);

		_assertNotIncluded();
	}

	@Test
	public void testIncludeWhenThemeDisplayIsNull() throws Exception {
		_include(new MockHttpServletRequest(), false);

		_assertNotIncluded();
	}

	@Test
	public void testIncludeWhenUserIsNotSignedIn() throws Exception {
		_include(_getMockHttpServletRequest(true, false), false);

		_assertNotIncluded();
	}

	@Test
	public void testRegister() {
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry =
			Mockito.mock(DynamicInclude.DynamicIncludeRegistry.class);

		_getCMSToolbarOmniSearchDynamicInclude(
			false
		).register(
			dynamicIncludeRegistry
		);

		Mockito.verify(
			dynamicIncludeRegistry, Mockito.times(1)
		).register(
			"/html/common/themes/bottom.jsp#post"
		);
	}

	private void _assertNotIncluded() throws Exception {
		Mockito.verify(
			_requestDispatcher, Mockito.never()
		).include(
			Mockito.any(), Mockito.any()
		);
	}

	private CMSToolbarOmniSearchDynamicInclude
		_getCMSToolbarOmniSearchDynamicInclude(boolean showControlMenu) {

		CMSToolbarOmniSearchDynamicInclude cmsToolbarOmniSearchDynamicInclude =
			new CMSToolbarOmniSearchDynamicInclude();

		ProductNavigationControlMenuManager
			productNavigationControlMenuManager = Mockito.mock(
				ProductNavigationControlMenuManager.class);

		Mockito.when(
			productNavigationControlMenuManager.isShowControlMenu(Mockito.any())
		).thenReturn(
			showControlMenu
		);

		ReflectionTestUtil.setFieldValue(
			cmsToolbarOmniSearchDynamicInclude,
			"_productNavigationControlMenuManager",
			productNavigationControlMenuManager);

		ReflectionTestUtil.setFieldValue(
			cmsToolbarOmniSearchDynamicInclude, "_servletContext",
			_servletContext);

		return cmsToolbarOmniSearchDynamicInclude;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		boolean cms, boolean signedIn) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.isCMS()
		).thenReturn(
			cms
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			signedIn
		);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private void _include(
			MockHttpServletRequest mockHttpServletRequest,
			boolean showControlMenu)
		throws Exception {

		_getCMSToolbarOmniSearchDynamicInclude(
			showControlMenu
		).include(
			mockHttpServletRequest, new MockHttpServletResponse(),
			"/html/common/themes/bottom.jsp#post"
		);
	}

	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);
	private RequestDispatcher _requestDispatcher;
	private ServletContext _servletContext;

}
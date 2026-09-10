/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.test.util.BasePanelAppNavigationItemsTestCase;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationRegistryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Mario Leandro
 */
public class UsersPanelAppNavigationItemsTest
	extends BasePanelAppNavigationItemsTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getBundleContext()
		).thenReturn(
			Mockito.mock(BundleContext.class)
		);

		_frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundle
		);

		Mockito.when(
			_otherScreenNavigationCategory.getCategoryKey()
		).thenReturn(
			"user-groups"
		);

		Mockito.when(
			_otherScreenNavigationCategory.getLabel(LocaleUtil.ENGLISH)
		).thenReturn(
			"User Groups"
		);

		Mockito.when(
			_otherScreenNavigationCategory.getLabel(LocaleUtil.SPAIN)
		).thenReturn(
			"User Groups-es"
		);

		Mockito.when(
			_screenNavigationCategory.getCategoryKey()
		).thenReturn(
			"organizations"
		);

		Mockito.when(
			_screenNavigationCategory.getLabel(LocaleUtil.ENGLISH)
		).thenReturn(
			"Organizations"
		);

		Mockito.when(
			_screenNavigationCategory.getLabel(LocaleUtil.SPAIN)
		).thenReturn(
			"Organizations-es"
		);

		Mockito.when(
			themeDisplay.getUser()
		).thenReturn(
			_user
		);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		try (MockedStatic<ScreenNavigationRegistryUtil> mockedStatic =
				Mockito.mockStatic(ScreenNavigationRegistryUtil.class)) {

			mockedStatic.when(
				() ->
					ScreenNavigationRegistryUtil.getScreenNavigationCategories(
						Mockito.anyString(), Mockito.any(), Mockito.any())
			).thenReturn(
				List.of(
					_screenNavigationCategory, _otherScreenNavigationCategory)
			);

			List<PanelAppNavigationItem> panelAppNavigationItems =
				_usersPanelApp.getPanelAppNavigationItems(httpServletRequest);

			assertCanonicalNames(
				panelAppNavigationItems, "Organizations", "User Groups");

			String[] expectedCategoryKeys = {"organizations", "user-groups"};

			for (int i = 0; i < expectedCategoryKeys.length; i++) {
				assertParameterValue(
					expectedCategoryKeys[i], panelAppNavigationItems.get(i),
					"screenNavigationCategoryKey");
			}
		}
	}

	@Test
	public void testGetPanelAppNavigationItemsQueriesTheUsersNavigation()
		throws Exception {

		try (MockedStatic<ScreenNavigationRegistryUtil> mockedStatic =
				Mockito.mockStatic(ScreenNavigationRegistryUtil.class)) {

			mockedStatic.when(
				() ->
					ScreenNavigationRegistryUtil.getScreenNavigationCategories(
						Mockito.anyString(), Mockito.any(), Mockito.any())
			).thenReturn(
				List.of(_screenNavigationCategory)
			);

			_usersPanelApp.getPanelAppNavigationItems(httpServletRequest);

			ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
				String.class);

			mockedStatic.verify(
				() ->
					ScreenNavigationRegistryUtil.getScreenNavigationCategories(
						argumentCaptor.capture(), Mockito.eq(_user),
						Mockito.isNull()));

			Assert.assertEquals(
				UserScreenNavigationEntryConstants.
					SCREEN_NAVIGATION_KEY_USERS_AND_ORGANIZATIONS,
				argumentCaptor.getValue());
		}
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private final ScreenNavigationCategory _otherScreenNavigationCategory =
		Mockito.mock(ScreenNavigationCategory.class);
	private final ScreenNavigationCategory _screenNavigationCategory =
		Mockito.mock(ScreenNavigationCategory.class);
	private final User _user = Mockito.mock(User.class);

	private final UsersPanelApp _usersPanelApp = new UsersPanelApp() {

		@Override
		public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
			return _portletURL;
		}

		private final PortletURL _portletURL = new MockLiferayPortletURL();

	};

}
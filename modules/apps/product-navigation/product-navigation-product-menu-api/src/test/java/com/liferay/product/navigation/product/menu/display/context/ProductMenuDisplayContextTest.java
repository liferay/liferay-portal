/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.applications.menu.configuration.ApplicationsMenuInstanceConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Anderson Luiz
 */
public class ProductMenuDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, _mockPortletRequest);
		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_mockPortletRequest.setAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_HELPER, _panelCategoryHelper);
		_mockPortletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, _mockHttpServletRequest);
		_mockPortletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	@Test
	public void testShouldReturnOnlyPanelCategoriesThatContainPanelApps() {
		_assertGetPanelCategories(
			productMenuDisplayContext -> {
				List<PanelCategory> childPanelCategories =
					productMenuDisplayContext.getChildPanelCategories();

				Assert.assertEquals(
					childPanelCategories.toString(), 5,
					childPanelCategories.size());

				Mockito.verify(
					_panelCategoryHelper
				).getChildPanelCategories(
					PanelCategoryKeys.APPLICATIONS_MENU, _themeDisplay
				);

				Mockito.verify(
					_panelCategoryHelper, Mockito.times(3)
				).getAllPanelApps(
					ArgumentMatchers.matches("application-panel*")
				);
			},
			false);
	}

	@Test
	public void testShouldReturnOnlyRootPanelCategoriesWhenApplicationsMenuIsEnabled() {
		_assertGetPanelCategories(
			productMenuDisplayContext -> {
				List<PanelCategory> childPanelCategories =
					productMenuDisplayContext.getChildPanelCategories();

				Assert.assertEquals(
					childPanelCategories.toString(), 3,
					childPanelCategories.size());

				Mockito.verify(
					_panelCategoryHelper
				).getChildPanelCategories(
					PanelCategoryKeys.ROOT, _themeDisplay
				);

				Mockito.verify(
					_panelCategoryHelper, Mockito.never()
				).getChildPanelCategories(
					PanelCategoryKeys.APPLICATIONS_MENU, _themeDisplay
				);

				Mockito.verify(
					_panelCategoryHelper, Mockito.never()
				).getAllPanelApps(
					ArgumentMatchers.matches("application-panel*")
				);
			},
			true);
	}

	private void _assertGetPanelCategories(
		Consumer<ProductMenuDisplayContext> consumer,
		boolean enableApplicationsMenu) {

		try (MockedStatic<ConfigurationProviderUtil>
				configurationProviderUtilMockedStatic = Mockito.mockStatic(
					ConfigurationProviderUtil.class);
			MockedStatic<PortalUtil> portalUtilMockedStatic =
				Mockito.mockStatic(PortalUtil.class)) {

			configurationProviderUtilMockedStatic.when(
				() -> ConfigurationProviderUtil.getCompanyConfiguration(
					ArgumentMatchers.any(), ArgumentMatchers.anyLong())
			).thenReturn(
				_applicationsMenuInstanceConfiguration
			);

			portalUtilMockedStatic.when(
				() -> PortalUtil.getHttpServletRequest(ArgumentMatchers.any())
			).thenReturn(
				_mockHttpServletRequest
			);

			Mockito.when(
				_applicationsMenuInstanceConfiguration.enableApplicationsMenu()
			).thenReturn(
				enableApplicationsMenu
			);

			Mockito.when(
				_panelCategoryHelper.getAllPanelApps(
					ArgumentMatchers.anyString())
			).thenReturn(
				Collections.singletonList(Mockito.mock(PanelApp.class))
			);

			Mockito.when(
				_panelCategoryHelper.getAllPanelApps(
					ArgumentMatchers.matches("panel2"))
			).thenReturn(
				Collections.emptyList()
			);

			Mockito.when(
				_panelCategoryHelper.getChildPanelCategories(
					PanelCategoryKeys.ROOT, _themeDisplay)
			).thenReturn(
				_rootPanelCategories
			);

			Mockito.when(
				_panelCategoryHelper.getChildPanelCategories(
					PanelCategoryKeys.APPLICATIONS_MENU, _themeDisplay)
			).thenReturn(
				_applicationsMenuPanelCategories
			);

			consumer.accept(new ProductMenuDisplayContext(_mockPortletRequest));
		}
	}

	private PanelCategory _mockPanelCategory(String key) {
		PanelCategory panelCategory = Mockito.mock(PanelCategory.class);

		Mockito.when(
			panelCategory.getKey()
		).thenReturn(
			key
		);

		return panelCategory;
	}

	private final ApplicationsMenuInstanceConfiguration
		_applicationsMenuInstanceConfiguration = Mockito.mock(
			ApplicationsMenuInstanceConfiguration.class);
	private final List<PanelCategory> _applicationsMenuPanelCategories =
		Arrays.asList(
			_mockPanelCategory("application-panel1"),
			_mockPanelCategory("application-panel2"),
			_mockPanelCategory("application-panel3"));
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final MockLiferayResourceRequest _mockPortletRequest =
		new MockLiferayResourceRequest();
	private final PanelCategoryHelper _panelCategoryHelper = Mockito.mock(
		PanelCategoryHelper.class);
	private final List<PanelCategory> _rootPanelCategories = new ArrayList<>(
		Arrays.asList(
			_mockPanelCategory("root-panel1"),
			_mockPanelCategory("root-panel2"),
			_mockPanelCategory("root-panel3")));
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

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
public class LayoutPageTemplateManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpHttpServletRequest();
		_setUpLanguageUtil();
		_setUpLayoutPageTemplateEntryPermission();
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
		_layoutPageTemplateEntryPermissionMockedStatic.close();
	}

	@Test
	@TestInfo("LPS-106212")
	public void testGetAvailableActionsForDraftLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateManagementToolbarDisplayContext
			layoutPageTemplateManagementToolbarDisplayContext =
				_getLayoutPageTemplateManagementToolbarDisplayContext();

		Mockito.when(
			_layoutPageTemplateEntry.isDraft()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			Validator.isNull(
				layoutPageTemplateManagementToolbarDisplayContext.
					getAvailableActions(_layoutPageTemplateEntry)));
	}

	@Test
	@TestInfo("LPS-106212")
	public void testGetAvailableActionsForWidgetPageTemplate()
		throws Exception {

		LayoutPageTemplateManagementToolbarDisplayContext
			layoutPageTemplateManagementToolbarDisplayContext =
				_getLayoutPageTemplateManagementToolbarDisplayContext();

		Mockito.when(
			_layoutPageTemplateEntry.getLayoutPrototypeId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Assert.assertTrue(
			Validator.isNull(
				layoutPageTemplateManagementToolbarDisplayContext.
					getAvailableActions(_layoutPageTemplateEntry)));
	}

	@Test
	@TestInfo({"LPD-89086", "LPD-104842"})
	public void testGetCreationMenu() throws Exception {
		LayoutPageTemplateManagementToolbarDisplayContext
			layoutPageTemplateManagementToolbarDisplayContext =
				_getLayoutPageTemplateManagementToolbarDisplayContext();

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			_testGetCreationMenu(
				false, featureFlagManagerUtilMockedStatic,
				layoutPageTemplateManagementToolbarDisplayContext);
			_testGetCreationMenu(
				true, featureFlagManagerUtilMockedStatic,
				layoutPageTemplateManagementToolbarDisplayContext);
		}

		_testGetCreationMenuInDesignLibraryGroup(
			layoutPageTemplateManagementToolbarDisplayContext);
	}

	private LayoutPageTemplateManagementToolbarDisplayContext
		_getLayoutPageTemplateManagementToolbarDisplayContext() {

		return new LayoutPageTemplateManagementToolbarDisplayContext(
			_httpServletRequest, _getMockLiferayPortletActionRequest(),
			new MockLiferayPortletRenderResponse(),
			Mockito.mock(LayoutPageTemplateDisplayContext.class));
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

	private List<DropdownItem> _getPrimaryDropdownItems(
		LayoutPageTemplateManagementToolbarDisplayContext
			layoutPageTemplateManagementToolbarDisplayContext) {

		CreationMenu creationMenu =
			layoutPageTemplateManagementToolbarDisplayContext.getCreationMenu();

		return (List<DropdownItem>)creationMenu.get("primaryItems");
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
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

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	private void _testGetCreationMenu(
		boolean featureFlagEnabled,
		MockedStatic<FeatureFlagManagerUtil> featureFlagManagerUtilMockedStatic,
		LayoutPageTemplateManagementToolbarDisplayContext
			layoutPageTemplateManagementToolbarDisplayContext) {

		featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-76864"))
		).thenReturn(
			featureFlagEnabled
		);

		List<DropdownItem> primaryDropdownItems = _getPrimaryDropdownItems(
			layoutPageTemplateManagementToolbarDisplayContext);

		if (featureFlagEnabled) {
			Assert.assertEquals(
				primaryDropdownItems.toString(), 2,
				primaryDropdownItems.size());

			DropdownItem primaryDropdownItem = primaryDropdownItems.get(1);

			Assert.assertEquals(
				Boolean.TRUE, primaryDropdownItem.get("deprecated"));
		}
		else {
			Assert.assertEquals(
				primaryDropdownItems.toString(), 1,
				primaryDropdownItems.size());
		}
	}

	private void _testGetCreationMenuInDesignLibraryGroup(
		LayoutPageTemplateManagementToolbarDisplayContext
			layoutPageTemplateManagementToolbarDisplayContext) {

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			true
		);

		List<DropdownItem> primaryDropdownItems = _getPrimaryDropdownItems(
			layoutPageTemplateManagementToolbarDisplayContext);

		Assert.assertEquals(
			primaryDropdownItems.toString(), 1, primaryDropdownItems.size());

		DropdownItem primaryDropdownItem = primaryDropdownItems.get(0);

		Map<String, Object> data = (Map<String, Object>)primaryDropdownItem.get(
			"data");

		Assert.assertEquals("addLayoutPageTemplateEntry", data.get("action"));
		Assert.assertTrue(data.containsKey("addPageTemplateURL"));

		Assert.assertNull(primaryDropdownItem.get("href"));
	}

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry =
		Mockito.mock(LayoutPageTemplateEntry.class);
	private final MockedStatic<LayoutPageTemplateEntryPermission>
		_layoutPageTemplateEntryPermissionMockedStatic = Mockito.mockStatic(
			LayoutPageTemplateEntryPermission.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.portlet.action;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ResourceRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class GetNavigationItemsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_getNavigationItemsMVCResourceCommand, "_jsonFactory",
			JSONFactoryUtil.getJSONFactory());
		ReflectionTestUtil.setFieldValue(
			_getNavigationItemsMVCResourceCommand, "_panelAppRegistry",
			_panelAppRegistry);
		ReflectionTestUtil.setFieldValue(
			_getNavigationItemsMVCResourceCommand, "_panelCategoryHelper",
			_panelCategoryHelper);
		ReflectionTestUtil.setFieldValue(
			_getNavigationItemsMVCResourceCommand, "_portal", _portal);

		Mockito.when(
			_portal.getHttpServletRequest(_resourceRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			_resourceRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_resourceRequest.getParameter("selectedPortletId")
		).thenReturn(
			_SELECTED_PORTLET_ID
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	@Test
	public void testGetNavigationItemsJSONObjectFiltersTheApplications()
		throws Exception {

		_setUpActivePanelCategory();

		_getNavigationItemsJSONObject();

		Mockito.verify(
			_panelAppRegistry, Mockito.times(1)
		).getPanelApps(
			_PANEL_CATEGORY_KEY, _permissionChecker, _group
		);

		Mockito.verify(
			_panelAppRegistry, Mockito.never()
		).getPanelApps(
			Mockito.anyString()
		);
	}

	@Test
	public void testGetNavigationItemsJSONObjectIsEmptyWithoutAnActiveCategory()
		throws Exception {

		Mockito.when(
			_panelCategoryHelper.getActivePanelCategory(
				PanelCategoryKeys.APPLICATIONS_MENU, _SELECTED_PORTLET_ID,
				_themeDisplay)
		).thenReturn(
			null
		);

		JSONObject navigationItemsJSONObject = _getNavigationItemsJSONObject();

		Assert.assertEquals(0, navigationItemsJSONObject.length());
	}

	@Test
	public void testGetNavigationItemsJSONObjectOmitsEmptyApplications()
		throws Exception {

		_setUpActivePanelCategory();

		PanelApp emptyPanelApp = _createPanelApp(
			Collections.emptyList(), "emptyPortletId");

		PanelApp panelApp = _createPanelApp(
			List.of(
				new PanelAppNavigationItem(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())),
			"portletId");

		Mockito.when(
			_panelAppRegistry.getPanelApps(
				_PANEL_CATEGORY_KEY, _permissionChecker, _group)
		).thenReturn(
			List.of(emptyPanelApp, panelApp)
		);

		JSONObject navigationItemsJSONObject = _getNavigationItemsJSONObject();

		Assert.assertEquals(1, navigationItemsJSONObject.length());
		Assert.assertFalse(navigationItemsJSONObject.has("emptyPortletId"));
		Assert.assertNotNull(navigationItemsJSONObject.get("portletId"));
	}

	@Test
	public void testGetNavigationItemsJSONObjectSetsTheItemIds()
		throws Exception {

		_setUpActivePanelCategory();

		PanelApp panelApp = _createPanelApp(
			List.of(
				new PanelAppNavigationItem(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString()),
				new PanelAppNavigationItem(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())),
			"portletId");

		Mockito.when(
			_panelAppRegistry.getPanelApps(
				_PANEL_CATEGORY_KEY, _permissionChecker, _group)
		).thenReturn(
			List.of(panelApp)
		);

		JSONObject navigationItemsJSONObject = _getNavigationItemsJSONObject();

		JSONArray navigationItemsJSONArray =
			navigationItemsJSONObject.getJSONArray("portletId");

		Assert.assertEquals(
			"portletId_0",
			navigationItemsJSONArray.getJSONObject(
				0
			).getString(
				"id"
			));
		Assert.assertEquals(
			"portletId_1",
			navigationItemsJSONArray.getJSONObject(
				1
			).getString(
				"id"
			));
	}

	private PanelApp _createPanelApp(
			List<PanelAppNavigationItem> panelAppNavigationItems,
			String portletId)
		throws Exception {

		PanelApp panelApp = Mockito.mock(PanelApp.class);

		Mockito.when(
			panelApp.getPanelAppNavigationItems(_httpServletRequest)
		).thenReturn(
			panelAppNavigationItems
		);

		Mockito.when(
			panelApp.getPortletId()
		).thenReturn(
			portletId
		);

		return panelApp;
	}

	private JSONObject _getNavigationItemsJSONObject() throws Exception {
		return ReflectionTestUtil.invoke(
			_getNavigationItemsMVCResourceCommand,
			"_getNavigationItemsJSONObject",
			new Class<?>[] {ResourceRequest.class}, _resourceRequest);
	}

	private void _setUpActivePanelCategory() {
		PanelCategory panelCategory = Mockito.mock(PanelCategory.class);

		Mockito.when(
			panelCategory.getKey()
		).thenReturn(
			_PANEL_CATEGORY_KEY
		);

		Mockito.when(
			_panelCategoryHelper.getActivePanelCategory(
				PanelCategoryKeys.APPLICATIONS_MENU, _SELECTED_PORTLET_ID,
				_themeDisplay)
		).thenReturn(
			panelCategory
		);

		Mockito.when(
			_panelCategoryHelper.getChildPanelCategories(
				_PANEL_CATEGORY_KEY, _themeDisplay)
		).thenReturn(
			Collections.emptyList()
		);
	}

	private static final String _PANEL_CATEGORY_KEY =
		RandomTestUtil.randomString();

	private static final String _SELECTED_PORTLET_ID =
		RandomTestUtil.randomString();

	private final GetNavigationItemsMVCResourceCommand
		_getNavigationItemsMVCResourceCommand =
			new GetNavigationItemsMVCResourceCommand();
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final PanelAppRegistry _panelAppRegistry = Mockito.mock(
		PanelAppRegistry.class);
	private final PanelCategoryHelper _panelCategoryHelper = Mockito.mock(
		PanelCategoryHelper.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ResourceRequest _resourceRequest = Mockito.mock(
		ResourceRequest.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
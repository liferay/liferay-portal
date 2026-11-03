/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.internal.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class SideNavigationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_sideNavigationDisplayContext = Mockito.mock(
			SideNavigationDisplayContext.class, Mockito.CALLS_REAL_METHODS);

		ReflectionTestUtil.setFieldValue(
			_sideNavigationDisplayContext, "_httpServletRequest",
			Mockito.mock(HttpServletRequest.class));

		_panelAppRegistry = Mockito.mock(PanelAppRegistry.class);

		ReflectionTestUtil.setFieldValue(
			_sideNavigationDisplayContext, "_panelAppRegistry",
			_panelAppRegistry);

		ReflectionTestUtil.setFieldValue(
			_sideNavigationDisplayContext, "_panelCategory",
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL));

		_panelCategoryHelper = Mockito.mock(PanelCategoryHelper.class);

		ReflectionTestUtil.setFieldValue(
			_sideNavigationDisplayContext, "_panelCategoryHelper",
			_panelCategoryHelper);

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setLocale(LocaleUtil.US);

		_group = Mockito.mock(Group.class);

		ReflectionTestUtil.setFieldValue(_themeDisplay, "_scopeGroup", _group);

		ReflectionTestUtil.setFieldValue(
			_sideNavigationDisplayContext, "_themeDisplay", _themeDisplay);
	}

	@Test
	public void testGetLabelReturnsTheSiteNameForSiteAdministration()
		throws Exception {

		_setUpSiteAdministration();

		Mockito.when(
			_group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			"Liferay"
		);

		Assert.assertEquals(
			"Liferay",
			ReflectionTestUtil.invoke(
				_sideNavigationDisplayContext, "_getLabel", new Class<?>[0]));
	}

	@Test
	public void testGetPropsItemsFlattensAScopeIntoAMarkerAndItsApplications()
		throws Exception {

		_setUpPanelApps(
			PanelCategoryKeys.CONTROL_PANEL_SYSTEM,
			_createPanelApp("serverAdmin"));
		_setUpRootChildPanelCategories(
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL_SYSTEM));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertEquals(propsItems.toString(), 2, propsItems.size());

		Map<String, Object> scopePropsItem = propsItems.get(0);

		Assert.assertNull(scopePropsItem.get("items"));
		Assert.assertEquals("system", scopePropsItem.get("scope"));
		Assert.assertEquals(Boolean.TRUE, scopePropsItem.get("scopeMarker"));

		Map<String, Object> propsItem = propsItems.get(1);

		Assert.assertEquals("serverAdmin", propsItem.get("id"));
		Assert.assertEquals("system", propsItem.get("scope"));
	}

	@Test
	public void testGetPropsItemsGroupsTheSiteAdministrationCategories()
		throws Exception {

		_setUpSiteAdministration();

		_setUpPanelApps(
			"site_administration.content", _createPanelApp("blogs"));
		_setUpRootChildPanelCategories(
			_createPanelCategory("site_administration.content"));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertEquals(propsItems.toString(), 1, propsItems.size());

		Map<String, Object> propsItem = propsItems.get(0);

		Assert.assertEquals("site_administration.content", propsItem.get("id"));
		Assert.assertNotNull(propsItem.get("items"));
		Assert.assertNull(propsItem.get("scope"));
	}

	@Test
	public void testGetPropsItemsKeepsAPlainCategoryGrouped() throws Exception {
		_setUpPanelApps(
			PanelCategoryKeys.CONTROL_PANEL_USERS, _createPanelApp("roles"));
		_setUpRootChildPanelCategories(
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL_USERS));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertEquals(propsItems.toString(), 1, propsItems.size());

		Map<String, Object> propsItem = propsItems.get(0);

		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_USERS, propsItem.get("id"));
		Assert.assertNotNull(propsItem.get("items"));
		Assert.assertNull(propsItem.get("scopeMarker"));
	}

	@Test
	public void testGetPropsItemsLeavesATrailingCategoryWithoutAScope()
		throws Exception {

		_setUpPanelApps(
			PanelCategoryKeys.CONTROL_PANEL_INSTANCE,
			_createPanelApp("instanceSettings"));
		_setUpPanelApps(
			PanelCategoryKeys.CONTROL_PANEL_USERS, _createPanelApp("roles"));
		_setUpRootChildPanelCategories(
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL_INSTANCE),
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL_USERS));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Map<String, Object> propsItem = propsItems.get(propsItems.size() - 1);

		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_USERS, propsItem.get("id"));
		Assert.assertNull(propsItem.get("scope"));
	}

	@Test
	public void testGetPropsItemsOmitsAScopeWithoutApplications() {
		_setUpRootChildPanelCategories(
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL_SYSTEM));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertTrue(propsItems.toString(), propsItems.isEmpty());
	}

	@Test
	public void testGetPropsItemsOrdersEveryScopeBeforeTheOtherCategories()
		throws Exception {

		_setUpPanelApps(
			PanelCategoryKeys.CONTROL_PANEL_SYSTEM,
			_createPanelApp("serverAdmin"));
		_setUpPanelApps(
			PanelCategoryKeys.CONTROL_PANEL_USERS, _createPanelApp("roles"));
		_setUpRootChildPanelCategories(
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL_USERS),
			_createPanelCategory(PanelCategoryKeys.CONTROL_PANEL_SYSTEM));

		List<Map<String, Object>> propsItems = _getPropsItems();

		Assert.assertEquals(propsItems.toString(), 3, propsItems.size());

		Map<String, Object> scopePropsItem = propsItems.get(0);

		Assert.assertEquals(Boolean.TRUE, scopePropsItem.get("scopeMarker"));

		Map<String, Object> applicationPropsItem = propsItems.get(1);

		Assert.assertEquals("serverAdmin", applicationPropsItem.get("id"));

		Map<String, Object> categoryPropsItem = propsItems.get(2);

		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_USERS, categoryPropsItem.get("id"));
	}

	private PanelApp _createPanelApp(String portletId) throws Exception {
		PanelApp panelApp = Mockito.mock(PanelApp.class);

		Mockito.when(
			panelApp.getPortletId()
		).thenReturn(
			portletId
		);

		Mockito.when(
			panelApp.getLabel(Mockito.nullable(Locale.class))
		).thenReturn(
			portletId
		);

		Mockito.when(
			panelApp.getPortletURL(Mockito.nullable(HttpServletRequest.class))
		).thenReturn(
			Mockito.mock(PortletURL.class)
		);

		return panelApp;
	}

	private PanelCategory _createPanelCategory(String key) {
		PanelCategory panelCategory = Mockito.mock(PanelCategory.class);

		Mockito.when(
			panelCategory.getKey()
		).thenReturn(
			key
		);

		Mockito.when(
			panelCategory.getLabel(Mockito.nullable(Locale.class))
		).thenReturn(
			key
		);

		return panelCategory;
	}

	private List<Map<String, Object>> _getPropsItems() {
		return ReflectionTestUtil.invoke(
			_sideNavigationDisplayContext, "_getPropsItems", new Class<?>[0]);
	}

	private void _setUpPanelApps(
		String panelCategoryKey, PanelApp... panelApps) {

		Mockito.when(
			_panelAppRegistry.getPanelApps(
				Mockito.eq(panelCategoryKey),
				Mockito.nullable(PermissionChecker.class), Mockito.eq(_group))
		).thenReturn(
			List.of(panelApps)
		);
	}

	private void _setUpRootChildPanelCategories(
		PanelCategory... panelCategories) {

		PanelCategory panelCategory = ReflectionTestUtil.getFieldValue(
			_sideNavigationDisplayContext, "_panelCategory");

		Mockito.when(
			_panelCategoryHelper.getChildPanelCategories(
				panelCategory.getKey(), _themeDisplay)
		).thenReturn(
			List.of(panelCategories)
		);
	}

	private void _setUpSiteAdministration() {
		ReflectionTestUtil.setFieldValue(
			_sideNavigationDisplayContext, "_panelCategory",
			_createPanelCategory(PanelCategoryKeys.SITE_ADMINISTRATION));
		ReflectionTestUtil.setFieldValue(
			_sideNavigationDisplayContext, "_siteAdministration", true);
	}

	private Group _group;
	private PanelAppRegistry _panelAppRegistry;
	private PanelCategoryHelper _panelCategoryHelper;
	private SideNavigationDisplayContext _sideNavigationDisplayContext;
	private ThemeDisplay _themeDisplay;

}
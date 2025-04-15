/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockPortletPreferences;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuService;

import jakarta.portlet.PortletPreferences;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class SiteNavigationMenuConfigurationActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_setUpPortletPreferences();
	}

	@Test
	@TestInfo("LPD-37038")
	public void testUpdateDisplayStyleGroupPreferencesWithDifferentScope()
		throws Exception {

		Group group = _getGroup(RandomTestUtil.randomLong());

		_setUpSiteNavigationMenuConfigurationAction(group, null, null);

		_siteNavigationMenuConfigurationAction.postProcess(
			_COMPANY_ID, _getMockActionRequest(RandomTestUtil.randomLong()),
			_portletPreferences);

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			_portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null));
		Assert.assertEquals(
			_DISPLAY_STYLE_GROUP_ID,
			_portletPreferences.getValue("displayStyleGroupId", null));
		Assert.assertEquals(
			_DISPLAY_STYLE_GROUP_KEY,
			_portletPreferences.getValue("displayStyleGroupKey", null));
	}

	@Test
	public void testUpdateDisplayStyleGroupPreferencesWithSameScope()
		throws Exception {

		Group group = _getGroup(RandomTestUtil.randomLong());

		_setUpSiteNavigationMenuConfigurationAction(group, null, null);

		_siteNavigationMenuConfigurationAction.postProcess(
			_COMPANY_ID, _getMockActionRequest(group.getGroupId()),
			_portletPreferences);

		Assert.assertNull(
			_portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null));
		Assert.assertEquals(
			_DISPLAY_STYLE_GROUP_ID,
			_portletPreferences.getValue("displayStyleGroupId", null));
		Assert.assertEquals(
			_DISPLAY_STYLE_GROUP_KEY,
			_portletPreferences.getValue("displayStyleGroupKey", null));
	}

	@Test
	public void testUpdateRootMenuItemPreferences() throws Exception {
		String rootMenuItemExternalReferenceCode =
			RandomTestUtil.randomString();

		_setUpSiteNavigationMenuConfigurationAction(
			null, null, rootMenuItemExternalReferenceCode);

		_siteNavigationMenuConfigurationAction.postProcess(
			_COMPANY_ID, _getMockActionRequest(RandomTestUtil.randomLong()),
			_portletPreferences);

		Assert.assertEquals(
			rootMenuItemExternalReferenceCode,
			_portletPreferences.getValue(
				"rootMenuItemExternalReferenceCode", null));
		Assert.assertEquals(
			_ROOT_MENU_ITEM_ID,
			_portletPreferences.getValue("rootMenuItemId", null));
	}

	@Test
	public void testUpdateSiteNavigationMenuPreferences() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		String siteNavigationMenuExternalReferenceCode =
			RandomTestUtil.randomString();

		_setUpSiteNavigationMenuConfigurationAction(
			_getGroup(groupId), siteNavigationMenuExternalReferenceCode, null);

		_siteNavigationMenuConfigurationAction.postProcess(
			_COMPANY_ID, _getMockActionRequest(groupId), _portletPreferences);

		Assert.assertEquals(
			siteNavigationMenuExternalReferenceCode,
			_portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", null));
		Assert.assertNull(
			_portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode", null));
		Assert.assertEquals(
			_SITE_NAVIGATION_MENU_ITEM_ID,
			_portletPreferences.getValue("siteNavigationMenuId", null));
	}

	@Test
	public void testUpdateSiteNavigationMenuPreferencesWithDifferentScope()
		throws Exception {

		Group group = _getGroup(RandomTestUtil.randomLong());
		String siteNavigationMenuExternalReferenceCode =
			RandomTestUtil.randomString();

		_setUpSiteNavigationMenuConfigurationAction(
			group, siteNavigationMenuExternalReferenceCode, null);

		_siteNavigationMenuConfigurationAction.postProcess(
			_COMPANY_ID, _getMockActionRequest(RandomTestUtil.randomLong()),
			_portletPreferences);

		Assert.assertEquals(
			siteNavigationMenuExternalReferenceCode,
			_portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", null));
		Assert.assertEquals(
			group.getExternalReferenceCode(),
			_portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode", null));
		Assert.assertEquals(
			_SITE_NAVIGATION_MENU_ITEM_ID,
			_portletPreferences.getValue("siteNavigationMenuId", null));
	}

	private Group _getGroup(long groupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.getGroupKey()
		).thenReturn(
			_DISPLAY_STYLE_GROUP_KEY
		);

		return group;
	}

	private GroupLocalService _getGroupLocalService(Group group)
		throws Exception {

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		if (group != null) {
			Mockito.when(
				groupLocalService.getGroup(group.getGroupId())
			).thenReturn(
				group
			);
		}

		return groupLocalService;
	}

	private MockActionRequest _getMockActionRequest(long groupId)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		themeDisplay.setCompany(company);

		themeDisplay.setScopeGroupId(groupId);

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		mockActionRequest.setParameter(
			"preferences--displayStyleGroupKey--", _DISPLAY_STYLE_GROUP_KEY);

		return mockActionRequest;
	}

	private SiteNavigationMenuItemLocalService
		_getSiteNavigationMenuItemLocalService(
			String siteNavigationMenuItemExternalReferenceCode) {

		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService =
			Mockito.mock(SiteNavigationMenuItemLocalService.class);

		if (Validator.isNull(siteNavigationMenuItemExternalReferenceCode)) {
			return siteNavigationMenuItemLocalService;
		}

		SiteNavigationMenuItem siteNavigationMenuItem = Mockito.mock(
			SiteNavigationMenuItem.class);

		Mockito.when(
			siteNavigationMenuItem.getExternalReferenceCode()
		).thenReturn(
			siteNavigationMenuItemExternalReferenceCode
		);

		Mockito.when(
			siteNavigationMenuItemLocalService.fetchSiteNavigationMenuItem(
				Mockito.anyLong())
		).thenReturn(
			siteNavigationMenuItem
		);

		return siteNavigationMenuItemLocalService;
	}

	private SiteNavigationMenuService _getSiteNavigationMenuService(
			Group group, String siteNavigationMenuExternalReferenceCode)
		throws Exception {

		SiteNavigationMenuService siteNavigationMenuService = Mockito.mock(
			SiteNavigationMenuService.class);

		if (Validator.isNull(siteNavigationMenuExternalReferenceCode)) {
			return siteNavigationMenuService;
		}

		SiteNavigationMenu siteNavigationMenu = Mockito.mock(
			SiteNavigationMenu.class);

		Mockito.when(
			siteNavigationMenu.getExternalReferenceCode()
		).thenReturn(
			siteNavigationMenuExternalReferenceCode
		);

		long groupId = RandomTestUtil.randomLong();

		if (group != null) {
			groupId = group.getGroupId();
		}

		Mockito.when(
			siteNavigationMenu.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			siteNavigationMenuService.fetchSiteNavigationMenu(Mockito.anyLong())
		).thenReturn(
			siteNavigationMenu
		);

		return siteNavigationMenuService;
	}

	private void _setUpGroupLocalServiceUtil(Group group) throws Exception {
		_groupLocalServiceUtilMockedStatic.reset();

		if (group == null) {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(
					Mockito.anyLong(), Mockito.anyString())
			).thenReturn(
				null
			);

			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(Mockito.anyLong())
			).thenReturn(
				null
			);
		}
		else {
			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(
					_COMPANY_ID, group.getGroupKey())
			).thenReturn(
				group
			);

			_groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(group.getGroupId())
			).thenReturn(
				group
			);
		}
	}

	private void _setUpPortletPreferences() throws Exception {
		_portletPreferences = new MockPortletPreferences();

		_portletPreferences.setValue(
			"displayStyleGroupId", _DISPLAY_STYLE_GROUP_ID);
		_portletPreferences.setValue(
			"displayStyleGroupKey", _DISPLAY_STYLE_GROUP_KEY);
		_portletPreferences.setValue("rootMenuItemId", _ROOT_MENU_ITEM_ID);
		_portletPreferences.setValue("rootMenuItemType", "select");
		_portletPreferences.setValue(
			"siteNavigationMenuId", _SITE_NAVIGATION_MENU_ITEM_ID);
	}

	private void _setUpSiteNavigationMenuConfigurationAction(
			Group group, String siteNavigationMenuExternalReferenceCode,
			String siteNavigationMenuItemExternalReferenceCode)
		throws Exception {

		_setUpGroupLocalServiceUtil(group);

		_siteNavigationMenuConfigurationAction =
			new SiteNavigationMenuConfigurationAction();

		_siteNavigationMenuConfigurationAction.groupLocalService =
			_getGroupLocalService(group);
		_siteNavigationMenuConfigurationAction.
			siteNavigationMenuItemLocalService =
				_getSiteNavigationMenuItemLocalService(
					siteNavigationMenuItemExternalReferenceCode);
		_siteNavigationMenuConfigurationAction.siteNavigationMenuService =
			_getSiteNavigationMenuService(
				group, siteNavigationMenuExternalReferenceCode);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _DISPLAY_STYLE_GROUP_ID = String.valueOf(
		RandomTestUtil.randomLong());

	private static final String _DISPLAY_STYLE_GROUP_KEY =
		RandomTestUtil.randomString();

	private static final String _ROOT_MENU_ITEM_ID = String.valueOf(
		RandomTestUtil.randomLong());

	private static final String _SITE_NAVIGATION_MENU_ITEM_ID = String.valueOf(
		RandomTestUtil.randomLong());

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private PortletPreferences _portletPreferences;
	private SiteNavigationMenuConfigurationAction
		_siteNavigationMenuConfigurationAction;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockPortletPreferences;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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

		_setUpGroupLocalServiceUtil(group);

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

		_setUpGroupLocalServiceUtil(group);

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
	@TestInfo("LPD-107168")
	public void testUpdateExternalReferenceCodePreferences() throws Exception {
		_setUpGroupLocalServiceUtil(null);

		_siteNavigationMenuConfigurationAction.postProcess(
			_COMPANY_ID, _getMockActionRequest(RandomTestUtil.randomLong()),
			_portletPreferences);

		Assert.assertEquals(
			_ROOT_MENU_ITEM_EXTERNAL_REFERENCE_CODE,
			_portletPreferences.getValue(
				"rootMenuItemExternalReferenceCode", null));
		Assert.assertNull(_portletPreferences.getValue("rootMenuItemId", null));
		Assert.assertEquals(
			_SITE_NAVIGATION_MENU_EXTERNAL_REFERENCE_CODE,
			_portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", null));
		Assert.assertNull(
			_portletPreferences.getValue("siteNavigationMenuId", null));

		_portletPreferences.setValue(
			"rootMenuItemType", RandomTestUtil.randomString());

		_siteNavigationMenuConfigurationAction.postProcess(
			_COMPANY_ID, _getMockActionRequest(RandomTestUtil.randomLong()),
			_portletPreferences);

		Assert.assertNull(
			_portletPreferences.getValue(
				"rootMenuItemExternalReferenceCode", null));
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
		_portletPreferences.setValue(
			"rootMenuItemExternalReferenceCode",
			_ROOT_MENU_ITEM_EXTERNAL_REFERENCE_CODE);
		_portletPreferences.setValue(
			"rootMenuItemId", String.valueOf(RandomTestUtil.randomLong()));
		_portletPreferences.setValue("rootMenuItemType", "select");
		_portletPreferences.setValue(
			"siteNavigationMenuExternalReferenceCode",
			_SITE_NAVIGATION_MENU_EXTERNAL_REFERENCE_CODE);
		_portletPreferences.setValue(
			"siteNavigationMenuId",
			String.valueOf(RandomTestUtil.randomLong()));
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _DISPLAY_STYLE_GROUP_ID = String.valueOf(
		RandomTestUtil.randomLong());

	private static final String _DISPLAY_STYLE_GROUP_KEY =
		RandomTestUtil.randomString();

	private static final String _ROOT_MENU_ITEM_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final String _SITE_NAVIGATION_MENU_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private PortletPreferences _portletPreferences;
	private final SiteNavigationMenuConfigurationAction
		_siteNavigationMenuConfigurationAction =
			new SiteNavigationMenuConfigurationAction();

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.dsr.site.initializer.internal.constants.DSRConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
 * @author Stefano Motta
 */
public class ViewRoomsSectionDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.eq("delete"))
		).thenReturn(
			"Delete"
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.eq("edit"))
		).thenReturn(
			"Edit"
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq("new-digital-sales-room"))
		).thenReturn(
			"New Digital Sales Room"
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.ENGLISH
		);

		Mockito.when(
			_themeDisplay.getPathMain()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_themeDisplay.getPortalURL()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_objectDefinition.getLabel(Mockito.any(Locale.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_objectDefinition.getObjectDefinitionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
	}

	@Test
	public void testGetAPIURL() throws Exception {
		ViewRoomsSectionDisplayContext viewRoomsSectionDisplayContext =
			new ViewRoomsSectionDisplayContext(
				_getMockHttpServletRequest(), _objectDefinition,
				Mockito.mock(ObjectEntryService.class));

		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&",
				"filter=objectDefinitionId eq ",
				_objectDefinition.getObjectDefinitionId(),
				"&nestedFields=embedded,r_accountToDSRRooms_accountEntryId"),
			viewRoomsSectionDisplayContext.getAPIURL());
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		ObjectEntryService objectEntryService = Mockito.mock(
			ObjectEntryService.class);

		Mockito.when(
			objectEntryService.hasPortletResourcePermission(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			false
		).thenReturn(
			true
		);

		ViewRoomsSectionDisplayContext viewRoomsSectionDisplayContext =
			new ViewRoomsSectionDisplayContext(
				_getMockHttpServletRequest(), _objectDefinition,
				objectEntryService);

		Assert.assertNull(viewRoomsSectionDisplayContext.getCreationMenu());

		CreationMenu creationMenu =
			viewRoomsSectionDisplayContext.getCreationMenu();

		Assert.assertNotNull(creationMenu);

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(dropdownItems.toString(), 1, dropdownItems.size());

		DropdownItem dropdownItem = dropdownItems.get(0);

		Assert.assertEquals("forms", dropdownItem.get("icon"));
		Assert.assertEquals(
			"New Digital Sales Room", dropdownItem.get("label"));

		HashMap<String, Object> dropdownItemData =
			(HashMap<String, Object>)dropdownItem.get("data");

		Assert.assertEquals(
			"createDigitalSalesRoom", dropdownItemData.get("action"));
		Assert.assertEquals(
			String.valueOf(_objectDefinition.getObjectDefinitionId()),
			dropdownItemData.get("objectDefinitionId"));
		Assert.assertEquals(
			_objectDefinition.getLabel(LocaleUtil.ENGLISH),
			dropdownItemData.get("title"));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		ViewRoomsSectionDisplayContext viewRoomsSectionDisplayContext =
			new ViewRoomsSectionDisplayContext(
				_getMockHttpServletRequest(), _objectDefinition,
				Mockito.mock(ObjectEntryService.class));

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			viewRoomsSectionDisplayContext.getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 2,
			fdsActionDropdownItems.size());

		_assertFDSActionDropdownItem(
			StringBundler.concat(
				DSRConstants.DSR_FRIENDLY_URL, "/edit_room?siteId=",
				"{embedded.siteId}"),
			"pencil", "edit", "Edit", null, "update", null,
			fdsActionDropdownItems.get(0));
		_assertFDSActionDropdownItem(
			"#", "trash", "delete", "Delete", "delete", "delete", null,
			fdsActionDropdownItems.get(1));
	}

	private void _assertFDSActionDropdownItem(
		String expectedHref, String expectedIcon, String expectedId,
		String expectedLabel, String expectedMethod,
		String expectedPermissionKey,
		Map<String, Object> expectedVisibilityFilters,
		FDSActionDropdownItem fdsActionDropdownItem) {

		Map<String, Object> data =
			(Map<String, Object>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(expectedHref, fdsActionDropdownItem.get("href"));
		Assert.assertEquals(expectedIcon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(expectedId, data.get("id"));
		Assert.assertEquals(expectedLabel, fdsActionDropdownItem.get("label"));
		Assert.assertEquals(expectedMethod, data.get("method"));
		Assert.assertEquals(expectedPermissionKey, data.get("permissionKey"));
		Assert.assertEquals(
			expectedVisibilityFilters, data.get("visibilityFilters"));
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		return mockHttpServletRequest;
	}

	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
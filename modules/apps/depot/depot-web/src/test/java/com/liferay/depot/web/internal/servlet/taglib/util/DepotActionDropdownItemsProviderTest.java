/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.servlet.taglib.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.web.internal.roles.admin.group.type.contributor.DepotEntryPermission;
import com.liferay.depot.web.internal.util.DepotEntryURLUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class DepotActionDropdownItemsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_depotEntryPermissionMockedStatic = Mockito.mockStatic(
			DepotEntryPermission.class);
		_depotEntryURLUtilMockedStatic = Mockito.mockStatic(
			DepotEntryURLUtil.class);
		_languageUtilMockedStatic = Mockito.mockStatic(LanguageUtil.class);

		_depotEntryURLUtilMockedStatic.when(
			() -> DepotEntryURLUtil.getDeleteDepotEntryActionURL(
				Mockito.anyLong(), Mockito.any(),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			Mockito.mock(ActionURL.class)
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		PortalUtil portalUtil = new PortalUtil();
		Portal portal = Mockito.mock(Portal.class);
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(_permissionChecker);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			portal.getHttpServletRequest(
				Mockito.any(LiferayPortletRequest.class))
		).thenReturn(
			httpServletRequest
		);

		portalUtil.setPortal(portal);
	}

	@After
	public void tearDown() {
		_depotEntryPermissionMockedStatic.close();
		_depotEntryURLUtilMockedStatic.close();
		_languageUtilMockedStatic.close();
	}

	@Test
	public void testGetActionDropdownItems() {
		_testGetActionDropdownItemsWithUpdatePermission();
		_testGetActionDropdownItemsWithoutUpdatePermission();
	}

	private List<DropdownItem> _getActionDropdownItems() {
		DepotActionDropdownItemsProvider depotActionDropdownItemsProvider =
			new DepotActionDropdownItemsProvider(
				_depotEntry, Mockito.mock(LiferayPortletRequest.class),
				Mockito.mock(LiferayPortletResponse.class));

		return depotActionDropdownItemsProvider.getActionDropdownItems();
	}

	private void _mockDepotEntryPermission(String... actionIds) {
		_depotEntryPermissionMockedStatic.when(
			() -> DepotEntryPermission.contains(
				Mockito.eq(_permissionChecker), Mockito.anyLong(),
				Mockito.anyString())
		).thenAnswer(
			invocation -> ArrayUtil.contains(
				actionIds, invocation.getArgument(2))
		);
	}

	private void _testGetActionDropdownItemsWithUpdatePermission() {
		_mockDepotEntryPermission(
			ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE);

		Assert.assertEquals(
			Arrays.asList("edit", "delete", "permissions"),
			TransformUtil.transform(
				_getActionDropdownItems(),
				dropdownItem -> GetterUtil.getString(
					dropdownItem.get("label"))));
	}

	private void _testGetActionDropdownItemsWithoutUpdatePermission() {
		_mockDepotEntryPermission(ActionKeys.VIEW);

		Assert.assertNull(_getActionDropdownItems());
	}

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private MockedStatic<DepotEntryPermission>
		_depotEntryPermissionMockedStatic;
	private MockedStatic<DepotEntryURLUtil> _depotEntryURLUtilMockedStatic;
	private MockedStatic<LanguageUtil> _languageUtilMockedStatic;
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}
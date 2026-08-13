/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gabriel Prates
 */
public class ViewDesignLibraryAdminDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			ViewDesignLibraryAdminDisplayContext.class,
			"_depotPortletResourcePermissionSnapshot",
			new Snapshot<PortletResourcePermission>(
				ViewDesignLibraryAdminDisplayContext.class,
				PortletResourcePermission.class) {

				@Override
				public PortletResourcePermission get() {
					return _portletResourcePermission;
				}

			});

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.eq(_mockHttpServletRequest), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		Mockito.when(
			_liferayPortletResponse.createRenderURL()
		).thenReturn(
			Mockito.mock(LiferayPortletURL.class)
		);

		_viewDesignLibraryAdminDisplayContext =
			new ViewDesignLibraryAdminDisplayContext(
				_mockHttpServletRequest, _liferayPortletResponse);
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
	}

	@Test
	public void testGetBulkActionDropdownItems() {
		List<DropdownItem> bulkActionDropdownItems =
			_viewDesignLibraryAdminDisplayContext.getBulkActionDropdownItems();

		Assert.assertEquals(
			bulkActionDropdownItems.toString(), 1,
			bulkActionDropdownItems.size());

		DropdownItem dropdownItem = bulkActionDropdownItems.get(0);

		Assert.assertEquals("delete", dropdownItem.get("label"));
		Assert.assertEquals("trash", dropdownItem.get("icon"));

		Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
			"data");

		Assert.assertEquals("delete", data.get("id"));
		Assert.assertNull(data.get("method"));
	}

	@Test
	public void testGetEmptyState() {
		_testGetEmptyStateWhenUserDoesNotHaveAddPermission();
		_testGetEmptyStateWhenUserHasAddPermission();
	}

	@Test
	public void testGetFDSAdditionalProps() {
		_testGetFDSAdditionalProps(false);
		_testGetFDSAdditionalProps(true);
	}

	private void _setUpAddDepotEntryPermission(boolean canAddDesignLibrary) {
		long scopeGroupId = RandomTestUtil.randomLong();

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			scopeGroupId
		);

		Mockito.when(
			_portletResourcePermission.contains(
				_permissionChecker, scopeGroupId,
				DepotActionKeys.ADD_DEPOT_ENTRY)
		).thenReturn(
			canAddDesignLibrary
		);
	}

	private void _testGetEmptyStateWhenUserDoesNotHaveAddPermission() {
		_setUpAddDepotEntryPermission(false);

		Map<String, Object> emptyState =
			_viewDesignLibraryAdminDisplayContext.getEmptyState();

		Assert.assertEquals(StringPool.BLANK, emptyState.get("description"));
	}

	private void _testGetEmptyStateWhenUserHasAddPermission() {
		_setUpAddDepotEntryPermission(true);

		Map<String, Object> emptyState =
			_viewDesignLibraryAdminDisplayContext.getEmptyState();

		Assert.assertEquals(
			"click-new-to-create-your-first-design-library",
			emptyState.get("description"));
	}

	private void _testGetFDSAdditionalProps(boolean canAddDesignLibrary) {
		_setUpAddDepotEntryPermission(canAddDesignLibrary);

		Map<String, Object> fdsAdditionalProps =
			_viewDesignLibraryAdminDisplayContext.getFDSAdditionalProps();

		Assert.assertEquals(
			canAddDesignLibrary, fdsAdditionalProps.get("canAddDesignLibrary"));
	}

	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PortletResourcePermission _portletResourcePermission =
		Mockito.mock(PortletResourcePermission.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private ViewDesignLibraryAdminDisplayContext
		_viewDesignLibraryAdminDisplayContext;

}
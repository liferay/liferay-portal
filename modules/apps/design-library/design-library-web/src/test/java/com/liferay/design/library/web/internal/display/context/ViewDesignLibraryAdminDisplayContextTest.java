/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.constants.DepotActionKeys;
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
		_setUpHttpServletRequest();
		_setUpLanguageUtil();
		_setUpPortletURLMocks();
		_setUpViewDesignLibraryAdminDisplayContext();
	}

	@After
	public void tearDown() {
		_languageUtilMockedStatic.close();
	}

	@Test
	public void testGetEmptyStateWhenCanAddDesignLibrary() throws Exception {
		_setUpAddDepotEntryPermission(true);

		Map<String, Object> emptyState =
			_viewDesignLibraryAdminDisplayContext.getEmptyState();

		Assert.assertEquals(
			"click-new-to-create-your-first-design-library",
			emptyState.get("description"));
	}

	@Test
	public void testGetEmptyStateWhenCannotAddDesignLibrary() throws Exception {
		_setUpAddDepotEntryPermission(false);

		Map<String, Object> emptyState =
			_viewDesignLibraryAdminDisplayContext.getEmptyState();

		Assert.assertEquals(StringPool.BLANK, emptyState.get("description"));
	}

	@Test
	public void testGetFDSAdditionalPropsWhenCanAddDesignLibrary()
		throws Exception {

		_assertCanAddDesignLibrary(true);
	}

	@Test
	public void testGetFDSAdditionalPropsWhenCannotAddDesignLibrary()
		throws Exception {

		_assertCanAddDesignLibrary(false);
	}

	private void _assertCanAddDesignLibrary(boolean addDepotEntryPermission) {
		_setUpAddDepotEntryPermission(addDepotEntryPermission);

		Map<String, Object> fdsAdditionalProps =
			_viewDesignLibraryAdminDisplayContext.getFDSAdditionalProps();

		Assert.assertEquals(
			addDepotEntryPermission,
			fdsAdditionalProps.get("canAddDesignLibrary"));
	}

	private void _setUpAddDepotEntryPermission(
		boolean addDepotEntryPermission) {

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
			addDepotEntryPermission
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	private void _setUpLanguageUtil() {
		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.eq(_mockHttpServletRequest), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);
	}

	private void _setUpPortletURLMocks() {
		Mockito.when(
			_liferayPortletResponse.createRenderURL()
		).thenReturn(
			_liferayPortletURL
		);
	}

	private void _setUpViewDesignLibraryAdminDisplayContext() {
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

		_viewDesignLibraryAdminDisplayContext =
			new ViewDesignLibraryAdminDisplayContext(
				_mockHttpServletRequest, _liferayPortletResponse);
	}

	private final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
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
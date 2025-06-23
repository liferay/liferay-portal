/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Bárbara Cabrera
 */
public class FragmentDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpHttpServletRequest();
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_fragmentPermissionMockedStatic.close();
	}

	@Test
	public void testGetAvailableActionsForMarketplaceFragmentCompositionWithoutPermissions() {
		_setUpFragmentPermission(false);

		FragmentDisplayContext fragmentDisplayContext =
			new FragmentDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Mockito.when(
			_fragmentComposition.isMarketplace()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			Validator.isNull(
				fragmentDisplayContext.getAvailableActions(
					_fragmentComposition)));
	}

	@Test
	public void testGetAvailableActionsForMarketplaceFragmentCompositionWithPermissions() {
		_setUpFragmentPermission(true);

		FragmentDisplayContext fragmentDisplayContext =
			new FragmentDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Mockito.when(
			_fragmentComposition.isMarketplace()
		).thenReturn(
			true
		);

		String availableActions = fragmentDisplayContext.getAvailableActions(
			_fragmentComposition);

		Assert.assertFalse(
			availableActions.contains("copySelectedFragmentEntries"));
		Assert.assertTrue(
			availableActions.contains(
				"deleteFragmentCompositionsAndFragmentEntries"));
		Assert.assertFalse(
			availableActions.contains(
				"exportFragmentCompositionsAndFragmentEntries"));
		Assert.assertTrue(
			availableActions.contains(
				"moveFragmentCompositionsAndFragmentEntries"));
	}

	@Test
	public void testGetAvailableActionsForMarketplaceFragmentEntryWithoutPermissions() {
		_setUpFragmentPermission(false);

		FragmentDisplayContext fragmentDisplayContext =
			new FragmentDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Mockito.when(
			_fragmentEntry.isMarketplace()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			Validator.isNull(
				fragmentDisplayContext.getAvailableActions(_fragmentEntry)));
	}

	@Test
	public void testGetAvailableActionsForMarketplaceFragmentEntryWithPermissions() {
		_setUpFragmentPermission(true);

		FragmentDisplayContext fragmentDisplayContext =
			new FragmentDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Mockito.when(
			_fragmentEntry.isMarketplace()
		).thenReturn(
			true
		);

		String availableActions = fragmentDisplayContext.getAvailableActions(
			_fragmentEntry);

		Assert.assertFalse(
			availableActions.contains("copySelectedFragmentEntries"));
		Assert.assertTrue(
			availableActions.contains(
				"deleteFragmentCompositionsAndFragmentEntries"));
		Assert.assertFalse(
			availableActions.contains(
				"exportFragmentCompositionsAndFragmentEntries"));
		Assert.assertTrue(
			availableActions.contains(
				"moveFragmentCompositionsAndFragmentEntries"));
	}

	private void _setUpFragmentPermission(boolean condition) {
		_fragmentPermissionMockedStatic.when(
			() -> FragmentPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)
		).thenReturn(
			condition
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);
	}

	private final FragmentComposition _fragmentComposition = Mockito.mock(
		FragmentComposition.class);
	private final FragmentEntry _fragmentEntry = Mockito.mock(
		FragmentEntry.class);
	private final MockedStatic<FragmentPermission>
		_fragmentPermissionMockedStatic = Mockito.mockStatic(
			FragmentPermission.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
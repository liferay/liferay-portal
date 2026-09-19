/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
	@TestInfo("LPD-79101")
	public void testGetFragmentCollectionFromDifferentGroup() {
		FragmentCollection fragmentCollection = Mockito.mock(
			FragmentCollection.class);

		Mockito.when(
			fragmentCollection.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		long fragmentCollectionId = RandomTestUtil.randomLong();

		Mockito.when(
			_httpServletRequest.getParameter("fragmentCollectionId")
		).thenReturn(
			String.valueOf(fragmentCollectionId)
		);

		Mockito.when(
			_httpServletRequest.getParameter("fragmentCollectionKey")
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		try (MockedStatic<FragmentCollectionLocalServiceUtil>
				fragmentCollectionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(
						FragmentCollectionLocalServiceUtil.class)) {

			fragmentCollectionLocalServiceUtilMockedStatic.when(
				() ->
					FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
						fragmentCollectionId)
			).thenReturn(
				fragmentCollection
			);

			FragmentDisplayContext fragmentDisplayContext =
				new FragmentDisplayContext(
					_httpServletRequest, _renderRequest, _renderResponse);

			Assert.assertNull(fragmentDisplayContext.getFragmentCollection());
		}
	}

	@Test
	@TestInfo("LPD-79101")
	public void testGetFragmentCollectionFromSameGroup() {
		long fragmentCollectionId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();

		FragmentCollection fragmentCollection = Mockito.mock(
			FragmentCollection.class);

		Mockito.when(
			fragmentCollection.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_httpServletRequest.getParameter("fragmentCollectionId")
		).thenReturn(
			String.valueOf(fragmentCollectionId)
		);

		Mockito.when(
			_httpServletRequest.getParameter("fragmentCollectionKey")
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			groupId
		);

		try (MockedStatic<FragmentCollectionLocalServiceUtil>
				fragmentCollectionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(
						FragmentCollectionLocalServiceUtil.class)) {

			fragmentCollectionLocalServiceUtilMockedStatic.when(
				() ->
					FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
						fragmentCollectionId)
			).thenReturn(
				fragmentCollection
			);

			FragmentDisplayContext fragmentDisplayContext =
				new FragmentDisplayContext(
					_httpServletRequest, _renderRequest, _renderResponse);

			Assert.assertNotNull(
				fragmentDisplayContext.getFragmentCollection());
		}
	}

	@Test
	@TestInfo("LPD-91054")
	public void testGetFragmentCollectionFromSystemGroup() {
		long fragmentCollectionId = RandomTestUtil.randomLong();

		FragmentCollection fragmentCollection = Mockito.mock(
			FragmentCollection.class);

		Mockito.when(
			fragmentCollection.getGroupId()
		).thenReturn(
			CompanyConstants.SYSTEM
		);

		Mockito.when(
			_httpServletRequest.getParameter("fragmentCollectionId")
		).thenReturn(
			String.valueOf(fragmentCollectionId)
		);

		Mockito.when(
			_httpServletRequest.getParameter("fragmentCollectionKey")
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_themeDisplay.getCompanyGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		try (MockedStatic<FragmentCollectionLocalServiceUtil>
				fragmentCollectionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(
						FragmentCollectionLocalServiceUtil.class)) {

			fragmentCollectionLocalServiceUtilMockedStatic.when(
				() ->
					FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
						fragmentCollectionId)
			).thenReturn(
				fragmentCollection
			);

			FragmentDisplayContext fragmentDisplayContext =
				new FragmentDisplayContext(
					_httpServletRequest, _renderRequest, _renderResponse);

			Assert.assertNotNull(
				fragmentDisplayContext.getFragmentCollection());
		}
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
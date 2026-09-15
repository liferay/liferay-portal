/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.document.library.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.display.context.util.SharingDropdownItemFactory;
import com.liferay.sharing.display.context.util.SharingJavaScriptFactory;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.service.SharingEntryLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
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
public class SharingDLViewFileVersionDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_portalUtilMockedStatic.close();
		_repositoryUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassNameId(Mockito.anyString())
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_fileEntry.getFileEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_fileEntry.getRepositoryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	@Test
	public void testGetActionDropdownItemsWithExternalRepository()
		throws Exception {

		_setUpExternalRepository(true);
		_setUpSharePermission(true);
		_setUpShowShareAction();

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic =
					_getFeatureFlagManagerUtilMockedStatic()) {

			SharingDLViewFileVersionDisplayContext
				sharingDLViewFileVersionDisplayContext =
					_createDisplayContext();

			List<DropdownItem> dropdownItems =
				sharingDLViewFileVersionDisplayContext.getActionDropdownItems();

			Assert.assertTrue(dropdownItems.isEmpty());

			Mockito.verifyNoInteractions(_sharingDropdownItemFactory);
		}
	}

	@Test
	public void testGetActionDropdownItemsWithoutExternalRepository()
		throws Exception {

		_setUpExternalRepository(false);
		_setUpSharePermission(true);
		_setUpShowShareAction();

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic =
					_getFeatureFlagManagerUtilMockedStatic()) {

			SharingDLViewFileVersionDisplayContext
				sharingDLViewFileVersionDisplayContext =
					_createDisplayContext();

			List<DropdownItem> dropdownItems =
				sharingDLViewFileVersionDisplayContext.getActionDropdownItems();

			Assert.assertEquals(
				dropdownItems.toString(), 1, dropdownItems.size());

			Mockito.verify(
				_sharingDropdownItemFactory
			).createShareDropdownItem(
				Mockito.anyString(), Mockito.anyLong(),
				Mockito.any(HttpServletRequest.class)
			);
		}
	}

	@Test
	public void testIsSharingLinkVisibleWithExternalRepository()
		throws Exception {

		_setUpExternalRepository(true);
		_setUpSharePermission(true);

		Assert.assertFalse(_createDisplayContext().isSharingLinkVisible());

		Mockito.verifyNoInteractions(_sharingPermission);
	}

	@Test
	public void testIsSharingLinkVisibleWithoutExternalRepository()
		throws Exception {

		_setUpExternalRepository(false);
		_setUpSharePermission(true);

		Assert.assertTrue(_createDisplayContext().isSharingLinkVisible());
	}

	@Test
	public void testIsSharingLinkVisibleWithoutExternalRepositoryAndWithoutSharePermission()
		throws Exception {

		_setUpExternalRepository(false);
		_setUpSharePermission(false);

		Assert.assertFalse(_createDisplayContext().isSharingLinkVisible());
	}

	private SharingDLViewFileVersionDisplayContext _createDisplayContext()
		throws Exception {

		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			Mockito.mock(DLViewFileVersionDisplayContext.class);

		Mockito.when(
			dlViewFileVersionDisplayContext.getActionDropdownItems()
		).thenReturn(
			new ArrayList<>()
		);

		return new SharingDLViewFileVersionDisplayContext(
			dlViewFileVersionDisplayContext, _httpServletRequest,
			Mockito.mock(HttpServletResponse.class), _fileEntry,
			Mockito.mock(FileVersion.class),
			Mockito.mock(SharingEntryLocalService.class),
			_sharingDropdownItemFactory,
			Mockito.mock(SharingJavaScriptFactory.class), _sharingPermission,
			_sharingConfiguration);
	}

	private MockedStatic<FeatureFlagManagerUtil>
		_getFeatureFlagManagerUtilMockedStatic() {

		MockedStatic<FeatureFlagManagerUtil>
			featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
				FeatureFlagManagerUtil.class);

		featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			false
		);

		return featureFlagManagerUtilMockedStatic;
	}

	private void _setUpExternalRepository(boolean externalRepository) {
		_repositoryUtilMockedStatic.when(
			() -> RepositoryUtil.isExternalRepository(Mockito.anyLong())
		).thenReturn(
			externalRepository
		);
	}

	private void _setUpSharePermission(boolean sharePermission)
		throws Exception {

		Mockito.when(
			_sharingConfiguration.isEnabled()
		).thenReturn(
			sharePermission
		);

		Mockito.when(
			_sharingPermission.containsSharePermission(
				Mockito.any(PermissionChecker.class), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			sharePermission
		);
	}

	private void _setUpShowShareAction() {
		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.when(
			portletDisplay.getPortletName()
		).thenReturn(
			DLPortletKeys.DOCUMENT_LIBRARY_ADMIN
		);

		Mockito.when(
			_themeDisplay.getPortletDisplay()
		).thenReturn(
			portletDisplay
		);

		Mockito.when(
			_themeDisplay.isSignedIn()
		).thenReturn(
			true
		);
	}

	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private static final MockedStatic<RepositoryUtil>
		_repositoryUtilMockedStatic = Mockito.mockStatic(RepositoryUtil.class);

	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final SharingConfiguration _sharingConfiguration = Mockito.mock(
		SharingConfiguration.class);
	private final SharingDropdownItemFactory _sharingDropdownItemFactory =
		Mockito.mock(SharingDropdownItemFactory.class);
	private final SharingPermission _sharingPermission = Mockito.mock(
		SharingPermission.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
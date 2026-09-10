/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.document.library.internal.display.context;

import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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

		return new SharingDLViewFileVersionDisplayContext(
			null, _httpServletRequest, Mockito.mock(HttpServletResponse.class),
			_fileEntry, Mockito.mock(FileVersion.class),
			Mockito.mock(SharingEntryLocalService.class),
			Mockito.mock(SharingDropdownItemFactory.class),
			Mockito.mock(SharingJavaScriptFactory.class), _sharingPermission,
			_sharingConfiguration);
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

	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private static final MockedStatic<RepositoryUtil>
		_repositoryUtilMockedStatic = Mockito.mockStatic(RepositoryUtil.class);

	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final SharingConfiguration _sharingConfiguration = Mockito.mock(
		SharingConfiguration.class);
	private final SharingPermission _sharingPermission = Mockito.mock(
		SharingPermission.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
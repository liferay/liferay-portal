/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.logic;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.PortletURL;

import java.net.URI;
import java.net.URISyntaxException;

import org.assertj.core.api.AbstractUriAssert;
import org.assertj.core.api.Assertions;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marco Galluzzi
 */
public class UIItemsBuilderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpGroupPermissionUtil();
		_setUpPortletURLUtil();
		_setUpStagedModelDataHandlerRegistryUtil();
		_setUpStagingGroupHelper();

		_dlurlHelper = Mockito.mock(DLURLHelper.class);

		Mockito.when(
			_dlurlHelper.getDownloadURL(
				Mockito.nullable(FileEntry.class),
				Mockito.nullable(FileVersion.class),
				Mockito.nullable(ThemeDisplay.class), Mockito.anyString(),
				Mockito.anyBoolean(), Mockito.anyBoolean())
		).thenReturn(
			"http://localhost/"
		);

		_fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			_fileEntry.getFolderId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_fileVersion = Mockito.mock(FileVersion.class);

		Mockito.when(
			_fileEntry.getLatestFileVersion()
		).thenReturn(
			_fileVersion
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@AfterClass
	public static void tearDownClass() {
		_groupPermissionUtilMockedStatic.close();
		_portletURLUtilMockedStatic.close();
		_stagedModelDataHandlerRegistryUtilMockedStatic.close();
		_stagingGroupHelperUtilMockedStatic.close();
	}

	@Test
	public void testCancelCheckoutDropdownItem() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		UIItemsBuilder uiItemsBuilder = _getUIItemsBuilder(themeDisplay);

		DropdownItem cancelCheckoutDropdownItem =
			uiItemsBuilder.createCancelCheckoutDropdownItem();

		_assertHref(cancelCheckoutDropdownItem);
	}

	@Test
	public void testCheckinDropdownItem() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		UIItemsBuilder uiItemsBuilder = _getUIItemsBuilder(themeDisplay);

		DropdownItem cancelCheckoutDropdownItem =
			uiItemsBuilder.createCheckinDropdownItem();

		_assertHref(cancelCheckoutDropdownItem);
	}

	@Test
	public void testCreateCancelCheckoutDropdownItem() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		UIItemsBuilder uiItemsBuilder = _getUIItemsBuilder(themeDisplay);

		DropdownItem cancelCheckoutDropdownItem =
			uiItemsBuilder.createCancelCheckoutDropdownItem();

		_assertHref(cancelCheckoutDropdownItem);
	}

	@Test
	public void testCreateDownloadDropdownItemWithDoAsUserIdParameter()
		throws URISyntaxException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		String doAsUserId = RandomTestUtil.randomString();

		themeDisplay.setDoAsUserId(doAsUserId);

		UIItemsBuilder uiItemsBuilder = _getUIItemsBuilder(themeDisplay);

		DropdownItem dropdownItem = uiItemsBuilder.createDownloadDropdownItem();

		AbstractUriAssert<?> abstractUriAssert = Assertions.assertThat(
			new URI((String)dropdownItem.get("href")));

		abstractUriAssert.hasParameter("doAsUserId", doAsUserId);
	}

	@Test
	public void testCreateDownloadDropdownItemWithoutDoAsUserIdParameter()
		throws URISyntaxException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setDoAsUserId(null);

		UIItemsBuilder uiItemsBuilder = _getUIItemsBuilder(themeDisplay);

		DropdownItem dropdownItem = uiItemsBuilder.createDownloadDropdownItem();

		AbstractUriAssert<?> abstractUriAssert = Assertions.assertThat(
			new URI((String)dropdownItem.get("href")));

		abstractUriAssert.hasNoParameter("doAsUserId");
	}

	@Test
	public void testIsPublishActionAvailable() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		UIItemsBuilder uiItemsBuilder = _getUIItemsBuilder(themeDisplay);

		_addExportImportPortletInfoPermission(themeDisplay, false);

		Assert.assertFalse(uiItemsBuilder.isPublishActionAvailable());

		_addExportImportPortletInfoPermission(themeDisplay, true);

		Assert.assertTrue(uiItemsBuilder.isPublishActionAvailable());
	}

	private static void _setUpGroupPermissionUtil() {
		PropsUtil.setProps(Mockito.mock(Props.class));

		_groupPermissionUtilMockedStatic = Mockito.mockStatic(
			GroupPermissionUtil.class);
	}

	private static void _setUpPortletURLUtil() throws Exception {
		_portletURLUtilMockedStatic = Mockito.mockStatic(PortletURLUtil.class);

		Mockito.when(
			PortletURLUtil.getCurrent(
				Mockito.any(LiferayPortletRequest.class),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private static void _setUpStagedModelDataHandlerRegistryUtil() {
		_stagedModelDataHandlerRegistryUtilMockedStatic = Mockito.mockStatic(
			StagedModelDataHandlerRegistryUtil.class);

		StagedModelDataHandler<FileEntry> stagedModelDataHandler = Mockito.mock(
			StagedModelDataHandler.class);

		Mockito.when(
			stagedModelDataHandler.getExportableStatuses()
		).thenReturn(
			new int[] {0}
		);

		Mockito.<StagedModelDataHandler>when(
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				FileEntry.class.getName())
		).thenReturn(
			stagedModelDataHandler
		);
	}

	private static void _setUpStagingGroupHelper() {
		_stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
			StagingGroupHelperUtil.class);

		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			StagingGroupHelperUtil.getStagingGroupHelper()
		).thenReturn(
			stagingGroupHelper
		);

		Mockito.when(
			stagingGroupHelper.isStagingGroup(Mockito.anyLong())
		).thenReturn(
			true
		);

		Mockito.when(
			stagingGroupHelper.isStagedPortlet(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			true
		);
	}

	private void _addExportImportPortletInfoPermission(
		ThemeDisplay themeDisplay, boolean contains) {

		_groupPermissionUtilMockedStatic.when(
			() -> GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO)
		).thenReturn(
			contains
		);
	}

	private void _assertHref(DropdownItem dropdownItem) {
		String href = (String)dropdownItem.get("href");

		Assert.assertNotNull(href);

		Assert.assertTrue(
			href.contains("param_folderId=" + _fileEntry.getFolderId()));
	}

	private UIItemsBuilder _getUIItemsBuilder(ThemeDisplay themeDisplay) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		LiferayPortletRequest liferayPortletRequest = Mockito.mock(
			LiferayPortletRequest.class);

		Mockito.when(
			liferayPortletRequest.getPortletName()
		).thenReturn(
			DLPortletKeys.DOCUMENT_LIBRARY_ADMIN
		);

		String attributeName = StringBundler.concat(
			DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, StringPool.DASH,
			WebKeys.CURRENT_PORTLET_URL);

		liferayPortletRequest.setAttribute(
			attributeName, Mockito.mock(PortletURL.class));

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, liferayPortletRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		VersioningStrategy versioningStrategy = Mockito.mock(
			VersioningStrategy.class);

		Mockito.when(
			versioningStrategy.isOverridable()
		).thenReturn(
			false
		);

		return new UIItemsBuilder(
			mockHttpServletRequest, _fileEntry, _fileVersion, null,
			versioningStrategy, _dlurlHelper);
	}

	private static DLURLHelper _dlurlHelper;
	private static FileEntry _fileEntry;
	private static FileVersion _fileVersion;
	private static MockedStatic<GroupPermissionUtil>
		_groupPermissionUtilMockedStatic;
	private static MockedStatic<PortletURLUtil> _portletURLUtilMockedStatic;
	private static MockedStatic<StagedModelDataHandlerRegistryUtil>
		_stagedModelDataHandlerRegistryUtilMockedStatic;
	private static MockedStatic<StagingGroupHelperUtil>
		_stagingGroupHelperUtilMockedStatic;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.document.library.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextFactory;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class SharingDLViewFileVersionDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@FeatureFlag("LPS-197477")
	@Test
	public void testGetActionDropdownItems() throws PortalException {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), "text/plain", StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringPool.BLANK, "test".getBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_testGetActionDropdownItemsWithSharingDisabled(fileEntry);
		_testGetActionDropdownItemsWithSharingEnabled(fileEntry);
	}

	private void _assertCopyLinkDropdownItem(
		DropdownItem dropdownItem, FileEntry fileEntry) {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertEquals(
			StringBundler.concat(
				"javascript:Liferay.Sharing.copyLink('/documents",
				FriendlyURLResolverConstants.URL_SEPARATOR_FILE_ENTRY,
				StringUtil.toLowerCase(_group.getGroupKey()), "/",
				friendlyURLEntry.getUrlTitle(), "')"),
			dropdownItem.get("href"));

		Assert.assertEquals("link", dropdownItem.get("icon"));
		Assert.assertEquals(
			LanguageUtil.get(LocaleUtil.getDefault(), "copy-link"),
			dropdownItem.get("label"));
		Assert.assertEquals("item", dropdownItem.get("type"));
	}

	private void _assertShareDropdownItem(
		DropdownItem dropdownItem, FileEntry fileEntry) {

		Assert.assertEquals(
			StringBundler.concat(
				"javascript:Liferay.Sharing.share(",
				_portal.getClassNameId(DLFileEntry.class.getName()), ", ",
				fileEntry.getFileEntryId(), ", 'Share\\x20",
				fileEntry.getTitle(), "')"),
			dropdownItem.get("href"));
		Assert.assertEquals("users", dropdownItem.get("icon"));
		Assert.assertEquals(
			LanguageUtil.get(LocaleUtil.getDefault(), "invite-to-collaborate"),
			dropdownItem.get("label"));
		Assert.assertEquals("item", dropdownItem.get("type"));
	}

	private List<DropdownItem> _getDropdownItems(
			FileEntry fileEntry, boolean signedIn)
		throws PortalException {

		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			new MockLiferayPortletRenderRequest());
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setSignedIn(signedIn);
		themeDisplay.setSiteGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_dlDisplayContextFactory.getDLViewFileVersionDisplayContext(
				new TestParentDLViewFileVersionDisplayContext(),
				mockHttpServletRequest, new MockHttpServletResponse(),
				fileEntry.getFileVersion());

		return dlViewFileVersionDisplayContext.getActionDropdownItems();
	}

	private void _testGetActionDropdownItemsWithSharingDisabled(
			FileEntry fileEntry)
		throws PortalException {

		List<DropdownItem> actionDropdownItems = _getDropdownItems(
			fileEntry, false);

		Assert.assertEquals(
			actionDropdownItems.toString(), 1, actionDropdownItems.size());

		_assertCopyLinkDropdownItem(actionDropdownItems.get(0), fileEntry);
	}

	private void _testGetActionDropdownItemsWithSharingEnabled(
			FileEntry fileEntry)
		throws PortalException {

		List<DropdownItem> actionDropdownItems = _getDropdownItems(
			fileEntry, true);

		Assert.assertEquals(
			actionDropdownItems.toString(), 1, actionDropdownItems.size());

		DropdownItem dropdownItem = actionDropdownItems.get(0);

		DropdownItemList dropdownItemList = (DropdownItemList)dropdownItem.get(
			"items");

		Assert.assertEquals(2, dropdownItemList.size());

		_assertShareDropdownItem(dropdownItemList.get(0), fileEntry);
		_assertCopyLinkDropdownItem(dropdownItemList.get(1), fileEntry);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject(
		filter = "component.name=com.liferay.sharing.document.library.internal.display.context.SharingDLDisplayContextFactory"
	)
	private DLDisplayContextFactory _dlDisplayContextFactory;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	private class TestParentDLViewFileVersionDisplayContext
		implements DLViewFileVersionDisplayContext {

		@Override
		public List<DropdownItem> getActionDropdownItems()
			throws PortalException {

			return new ArrayList<>();
		}

		@Override
		public String getCssClassFileMimeType() {
			return null;
		}

		@Override
		public DDMFormValues getDDMFormValues(DDMStructure ddmStructure)
			throws PortalException {

			return null;
		}

		@Override
		public DDMFormValues getDDMFormValues(long ddmStorageId)
			throws PortalException {

			return null;
		}

		@Override
		public List<DDMStructure> getDDMStructures() throws PortalException {
			return null;
		}

		@Override
		public int getDDMStructuresCount() throws PortalException {
			return 0;
		}

		@Override
		public String getDiscussionClassName() {
			return null;
		}

		@Override
		public long getDiscussionClassPK() {
			return 0;
		}

		@Override
		public String getDiscussionLabel(Locale locale) {
			return null;
		}

		@Override
		public UUID getUuid() {
			return null;
		}

		@Override
		public boolean hasApprovedVersion() {
			return false;
		}

		@Override
		public boolean hasPreview() {
			return false;
		}

		@Override
		public boolean isDownloadLinkVisible() throws PortalException {
			return false;
		}

		@Override
		public boolean isVersionInfoVisible() throws PortalException {
			return false;
		}

		@Override
		public void renderPreview(
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse)
			throws IOException, ServletException {
		}

	}

}
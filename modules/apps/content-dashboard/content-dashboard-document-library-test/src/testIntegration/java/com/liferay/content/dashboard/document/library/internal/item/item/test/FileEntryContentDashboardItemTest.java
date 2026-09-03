/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.item.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.VersionableContentDashboardItem;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.net.URL;
import java.net.URLEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class FileEntryContentDashboardItemTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		PrincipalThreadLocal.setName(_originalName);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_serviceContext.setRequest(_getMockHttpServletRequest());
	}

	@Test
	public void testGetAllContentDashboardItemVersionsOneVersion()
		throws Exception {

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			versionableContentDashboardItem.getAllContentDashboardItemVersions(
				_getMockHttpServletRequest());

		Assert.assertEquals(
			contentDashboardItemVersions.toString(), 1,
			contentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion =
			contentDashboardItemVersions.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.getDefault(), "approved"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals("1.0", contentDashboardItemVersion.getVersion());
		Assert.assertEquals("success", contentDashboardItemVersion.getStyle());
	}

	@Test
	public void testGetAllContentDashboardItemVersionsTwoVersions()
		throws Exception {

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(2);

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			versionableContentDashboardItem.getAllContentDashboardItemVersions(
				_getMockHttpServletRequest());

		Assert.assertEquals(
			contentDashboardItemVersions.toString(), 2,
			contentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion =
			contentDashboardItemVersions.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.getDefault(), "approved"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals("1.1", contentDashboardItemVersion.getVersion());
		Assert.assertEquals("success", contentDashboardItemVersion.getStyle());
	}

	@Test
	public void testGetAssetCategories() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			_serviceContext);

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory1.getCategoryId()});

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(2);

		List<AssetCategory> assetCategories =
			versionableContentDashboardItem.getAssetCategories();

		Assert.assertEquals(
			assetCategories.toString(), 1, assetCategories.size());

		AssetCategory assetCategory2 = assetCategories.get(0);

		Assert.assertEquals(
			assetCategory1.getTitle(), assetCategory2.getTitle());
	}

	@Test
	public void testGetAssetCategoriesByVocabulary() throws Exception {
		AssetVocabulary assetVocabulary1 =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "assetCategory1",
			assetVocabulary1.getVocabularyId(), _serviceContext);

		AssetVocabulary assetVocabulary2 =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "assetCategory2",
			assetVocabulary2.getVocabularyId(), _serviceContext);

		_serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(2);

		List<AssetCategory> assetCategories =
			versionableContentDashboardItem.getAssetCategories(
				assetVocabulary2.getVocabularyId());

		Assert.assertEquals(
			assetCategories.toString(), 1, assetCategories.size());

		AssetCategory assetCategory3 = assetCategories.get(0);

		Assert.assertEquals(
			"assetCategory2", assetCategory3.getTitle(LocaleUtil.getDefault()));
	}

	@Test
	public void testGetAssetTags() throws Exception {
		_serviceContext.setAssetTagNames(new String[] {"tag1", "tag2", "tag3"});

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg",
			MimeTypesUtil.getExtensionContentType("image/jpg"), new byte[0],
			null, null, null, _serviceContext);

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		List<AssetTag> assetTags =
			versionableContentDashboardItem.getAssetTags();

		Assert.assertEquals(assetTags.toString(), 3, assetTags.size());

		AssetTag assetTag = assetTags.get(0);

		Assert.assertEquals("tag1", assetTag.getName());
	}

	@Test
	public void testGetContentDashboardItemActions() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		List<ContentDashboardItemAction> contentDashboardItemActions =
			versionableContentDashboardItem.getContentDashboardItemActions(
				_getMockHttpServletRequest(),
				ContentDashboardItemAction.Type.VIEW);

		Assert.assertEquals(
			contentDashboardItemActions.toString(), 1,
			contentDashboardItemActions.size());
	}

	@Test
	public void testGetContentDashboardItemSubtype() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		ContentDashboardItemSubtype contentDashboardItemSubtype =
			versionableContentDashboardItem.getContentDashboardItemSubtype();

		Assert.assertEquals(
			"Basic Document (Image)",
			contentDashboardItemSubtype.getLabel(LocaleUtil.US));
	}

	@Test
	public void testGetCreateDate() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertNotNull(versionableContentDashboardItem.getCreateDate());
	}

	@Test
	public void testGetDefaultContentDashboardItemAction() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		ContentDashboardItemAction contentDashboardItemAction =
			versionableContentDashboardItem.
				getDefaultContentDashboardItemAction(
					_getMockHttpServletRequest());

		Assert.assertEquals(
			ContentDashboardItemAction.Type.VIEW,
			contentDashboardItemAction.getType());
	}

	@Test
	public void testGetDefaultContentDashboardItemActionWithFileEntryWithoutPermissions()
		throws Exception {

		_serviceContext.setAddGroupPermissions(false);
		_serviceContext.setAddGuestPermissions(false);

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		User user = UserTestUtil.addUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(user);

		_serviceContext.setRequest(_getMockHttpServletRequest(user));

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.content.dashboard.document.library.internal." +
					"item.FileEntryContentDashboardItem",
				LoggerTestUtil.ERROR)) {

			ContentDashboardItemAction contentDashboardItemAction =
				versionableContentDashboardItem.
					getDefaultContentDashboardItemAction(
						mockHttpServletRequest);

			Assert.assertEquals(
				ContentDashboardItemAction.Type.VIEW,
				contentDashboardItemAction.getType());
		}
	}

	@Test
	public void testGetDefaultLocale() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertEquals(
			LocaleUtil.getDefault(),
			versionableContentDashboardItem.getDefaultLocale());
	}

	@Test
	public void testGetDescription() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertEquals(
			"description",
			versionableContentDashboardItem.getDescription(
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetInfoItemReference() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertNotNull(
			versionableContentDashboardItem.getInfoItemReference());
	}

	@Test
	public void testGetLatestContentDashboardItemVersions() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(2);

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			versionableContentDashboardItem.
				getLatestContentDashboardItemVersions(LocaleUtil.getDefault());

		Assert.assertEquals(
			contentDashboardItemVersions.toString(), 1,
			contentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion =
			contentDashboardItemVersions.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.getDefault(), "approved"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals("1.1", contentDashboardItemVersion.getVersion());
		Assert.assertEquals("success", contentDashboardItemVersion.getStyle());
	}

	@Test
	public void testGetLatestContentDashboardItemVersionsWithExpiredDLFileEntry()
		throws Exception {

		FileEntry fileEntry = _getFileEntry(3);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntry.getFileEntryId());

		_dlFileEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), dlFileEntry,
			dlFileEntry.getLatestFileVersion(true),
			WorkflowConstants.STATUS_EXPIRED, _serviceContext,
			Collections.emptyMap());

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			versionableContentDashboardItem.
				getLatestContentDashboardItemVersions(LocaleUtil.getDefault());

		Assert.assertEquals(
			contentDashboardItemVersions.toString(), 2,
			contentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion =
			contentDashboardItemVersions.get(0);

		Assert.assertEquals(
			_language.get(LocaleUtil.getDefault(), "approved"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals("1.1", contentDashboardItemVersion.getVersion());
		Assert.assertEquals("success", contentDashboardItemVersion.getStyle());

		contentDashboardItemVersion = contentDashboardItemVersions.get(1);

		Assert.assertEquals(
			_language.get(LocaleUtil.getDefault(), "expired"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals("1.2", contentDashboardItemVersion.getVersion());
		Assert.assertEquals("danger", contentDashboardItemVersion.getStyle());
	}

	@Test
	public void testGetModifiedDate() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertNotNull(versionableContentDashboardItem.getModifiedDate());
	}

	@Test
	public void testGetScopeName() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertNotNull(
			versionableContentDashboardItem.getScopeName(
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetSpecificInformationList() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_assertSpecificInformationList(
			null, "jpg", null, "0 B", _getVersionableContentDashboardItem(1));
		_assertSpecificInformationList(
			_language.get(LocaleUtil.getDefault(), "square"), "jpg", "225x225",
			"7 KB",
			_getVersionableContentDashboardItem("dependencies/225x225.jpg", 1));
		_assertSpecificInformationList(
			_language.get(LocaleUtil.getDefault(), "tall"), "jpg", "183x275",
			"6 KB",
			_getVersionableContentDashboardItem("dependencies/183x275.jpg", 1));
		_assertSpecificInformationList(
			_language.get(LocaleUtil.getDefault(), "tall"), "jpg", "183x275",
			"6 KB",
			_getVersionableContentDashboardItem(
				"dependencies/small_image.jpg", 1));
		_assertSpecificInformationList(
			_language.get(LocaleUtil.getDefault(), "wide"), "jpg", "277x182",
			"8 KB",
			_getVersionableContentDashboardItem("dependencies/277x182.jpg", 1));
		_assertSpecificInformationList(
			_language.get(LocaleUtil.getDefault(), "wide"), "jpg", "500x333",
			"42 KB",
			_getVersionableContentDashboardItem(
				"dependencies/medium_image.jpg", 1));
		_assertSpecificInformationList(
			_language.get(LocaleUtil.getDefault(), "wide"), "jpg", "1920x1080",
			"281 KB",
			_getVersionableContentDashboardItem(
				"dependencies/large_image.jpg", 1));
	}

	@Test
	public void testGetTitle() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertEquals(
			"example.jpg",
			versionableContentDashboardItem.getTitle(LocaleUtil.getDefault()));
	}

	@Test
	public void testGetTypeLabel() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertEquals(
			_language.get(LocaleUtil.getDefault(), "document"),
			versionableContentDashboardItem.getTypeLabel(
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetUserId() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertEquals(
			TestPropsValues.getUserId(),
			versionableContentDashboardItem.getUserId());
	}

	@Test
	public void testGetUserIdWithLatestApprovedFileVersion() throws Exception {
		User user = UserTestUtil.addUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		FileEntry fileEntry = _getFileEntry(2, user.getUserId());

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		Assert.assertEquals(
			user.getUserId(), versionableContentDashboardItem.getUserId());

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testGetUserIdWithPendingFileVersion() throws Exception {
		User user = UserTestUtil.addUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		FileEntry fileEntry = _getFileEntryWithPendingFileVersion(
			user.getUserId());

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		Assert.assertEquals(
			TestPropsValues.getUserId(),
			versionableContentDashboardItem.getUserId());

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testGetUserName() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertEquals(
			"Test Test", versionableContentDashboardItem.getUserName());
	}

	@Test
	public void testGetUserNameWithLatestApprovedFileVersion()
		throws Exception {

		User user = UserTestUtil.addUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		FileEntry fileEntry = _getFileEntry(2, user.getUserId());

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		Assert.assertEquals(
			user.getFullName(), versionableContentDashboardItem.getUserName());

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testGetUserNameWithPendingFileVersion() throws Exception {
		User user = UserTestUtil.addUser(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		FileEntry fileEntry = _getFileEntryWithPendingFileVersion(
			user.getUserId());

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		User originalUser = TestPropsValues.getUser();

		Assert.assertEquals(
			originalUser.getFullName(),
			versionableContentDashboardItem.getUserName());

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testGetViewVersionsURL() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"example.jpg", MimeTypesUtil.getExtensionContentType("image/jpg"),
			new byte[0], null, null, null, _serviceContext);

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		String viewVersionsURL =
			versionableContentDashboardItem.getViewVersionsURL(
				_getMockHttpServletRequest());

		Assert.assertNotNull(viewVersionsURL);

		String mvcRenderCommandNameEncoded = URLEncoder.encode(
			"/document_library/view_file_entry_history", "UTF-8");

		Assert.assertTrue(
			viewVersionsURL.contains(
				"mvcRenderCommandName=" + mvcRenderCommandNameEncoded));

		Assert.assertTrue(
			viewVersionsURL.contains(
				"fileEntryId=" + fileEntry.getFileEntryId()));
	}

	@Test
	public void testIsShowContentDashboardItemVersions() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertTrue(
			versionableContentDashboardItem.isShowContentDashboardItemVersions(
				_getMockHttpServletRequest()));
	}

	@Test
	public void testIsViewableWithLayoutPageTemplateEntry() throws Exception {
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				_getVersionableContentDashboardItem(1);

		Assert.assertTrue(
			versionableContentDashboardItem.isViewable(
				_getMockHttpServletRequest()));
	}

	@Test
	public void testIsViewableWithoutLayoutPageTemplateEntry()
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"example.jpg", MimeTypesUtil.getExtensionContentType("image/jpg"),
			new byte[0], null, null, null, _serviceContext);

		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem =
				(VersionableContentDashboardItem<FileEntry>)
					_contentDashboardItemFactory.create(
						fileEntry.getFileEntryId());

		Assert.assertFalse(
			versionableContentDashboardItem.isViewable(
				_getMockHttpServletRequest()));
	}

	private void _assertSpecificInformationList(
		String expectedAspectRatio, String expectedExtension,
		String expectedResolution, String expectedSize,
		VersionableContentDashboardItem<FileEntry>
			versionableContentDashboardItem) {

		List<ContentDashboardItem.SpecificInformation<?>> specificInformations =
			versionableContentDashboardItem.getSpecificInformationList(
				LocaleUtil.getDefault());

		ContentDashboardItem.SpecificInformation<?>
			aspectRatioSpecificInformation = _getSpecificInformation(
				"content-dashboard-aspect-ratio", specificInformations);

		Assert.assertEquals(
			expectedAspectRatio, aspectRatioSpecificInformation.getValue());

		ContentDashboardItem.SpecificInformation<?>
			extensionSpecificInformation = _getSpecificInformation(
				"extension", specificInformations);

		Assert.assertEquals(
			expectedExtension, extensionSpecificInformation.getValue());

		ContentDashboardItem.SpecificInformation<?>
			resolutionSpecificInformation = _getSpecificInformation(
				"resolution", specificInformations);

		Assert.assertEquals(
			expectedResolution, resolutionSpecificInformation.getValue());

		ContentDashboardItem.SpecificInformation<?> sizeSpecificInformation =
			_getSpecificInformation("size", specificInformations);

		Assert.assertEquals(expectedSize, sizeSpecificInformation.getValue());

		Assert.assertTrue(
			ListUtil.exists(
				specificInformations,
				specificInformation -> Objects.equals(
					specificInformation.getKey(), "file-name")));

		ContentDashboardItem.SpecificInformation<URL>
			webDAVSpecificInformation =
				(ContentDashboardItem.SpecificInformation<URL>)
					_getSpecificInformation(
						"web-dav-url", specificInformations);

		String url = String.valueOf(webDAVSpecificInformation.getValue());

		Assert.assertTrue(url.contains("webdav"));

		Assert.assertEquals(
			"webdav-help", webDAVSpecificInformation.getHelpText());
	}

	private FileEntry _getFileEntry(
			byte[] bytes, String fileName, int numVersions, long userId)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileName, MimeTypesUtil.getExtensionContentType(fileName), fileName,
			StringPool.BLANK, "description", StringPool.BLANK, bytes, null,
			null, null, _serviceContext);

		DLFileEntryType basicDocumentDLFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(FileEntry.class.getName()),
			basicDocumentDLFileEntryType.getFileEntryTypeKey(), true,
			WorkflowConstants.STATUS_APPROVED);

		if (numVersions > 1) {
			for (int i = 1; i < numVersions; i++) {
				fileEntry = _dlAppLocalService.updateFileEntry(
					userId, fileEntry.getFileEntryId(), fileEntry.getFileName(),
					fileEntry.getMimeType(), fileEntry.getTitle(),
					StringUtil.randomString(), fileEntry.getDescription(),
					RandomTestUtil.randomString(),
					DLVersionNumberIncrease.MINOR, fileEntry.getContentStream(),
					fileEntry.getSize(), fileEntry.getDisplayDate(),
					fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
					_serviceContext);
			}
		}

		return fileEntry;
	}

	private FileEntry _getFileEntry(int numVersions) throws Exception {
		return _getFileEntry(
			new byte[0], "example.jpg", numVersions,
			TestPropsValues.getUserId());
	}

	private FileEntry _getFileEntry(int numVersions, long userId)
		throws Exception {

		return _getFileEntry(new byte[0], "example.jpg", numVersions, userId);
	}

	private FileEntry _getFileEntry(String fileName, int numVersions)
		throws Exception {

		return _getFileEntry(
			FileUtil.getBytes(getClass(), fileName), fileName, numVersions,
			TestPropsValues.getUserId());
	}

	private FileEntry _getFileEntryWithPendingFileVersion(long userId)
		throws Exception {

		Folder folder = _getSingleApproverFolder();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getMockHttpServletRequest());
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), folder.getFolderId(),
			RandomTestUtil.randomString() + ".jpg",
			MimeTypesUtil.getExtensionContentType("jpg"), new byte[0], null,
			null, null, serviceContext);

		_updateStatus(
			fileEntry.getFileVersion(), WorkflowConstants.STATUS_APPROVED,
			serviceContext);

		return _dlAppLocalService.updateFileEntry(
			userId, fileEntry.getFileEntryId(), fileEntry.getFileName(),
			fileEntry.getMimeType(), fileEntry.getTitle(),
			StringUtil.randomString(), fileEntry.getDescription(),
			RandomTestUtil.randomString(), DLVersionNumberIncrease.MINOR,
			fileEntry.getContentStream(), fileEntry.getSize(),
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), serviceContext);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		return _getMockHttpServletRequest(TestPropsValues.getUser());
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay(
			mockHttpServletRequest, user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private Folder _getSingleApproverFolder() throws Exception {
		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, _serviceContext);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			"restrictionType", DLFolderConstants.RESTRICTION_TYPE_WORKFLOW);
		serviceContext.setAttribute(
			"workflowDefinition" +
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL,
			"Single Approver@1");

		return _dlAppLocalService.updateFolder(
			folder.getFolderId(), folder.getParentFolderId(), folder.getName(),
			folder.getDescription(), serviceContext);
	}

	private ContentDashboardItem.SpecificInformation<?> _getSpecificInformation(
		String key,
		List<ContentDashboardItem.SpecificInformation<?>>
			specificInformations) {

		for (ContentDashboardItem.SpecificInformation<?> specificInformation :
				specificInformations) {

			if (Objects.equals(specificInformation.getKey(), key)) {
				return specificInformation;
			}
		}

		return null;
	}

	private ThemeDisplay _getThemeDisplay(
			HttpServletRequest httpServletRequest, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private VersionableContentDashboardItem<FileEntry>
			_getVersionableContentDashboardItem(int numVersions)
		throws Exception {

		FileEntry fileEntry = _getFileEntry(numVersions);

		return (VersionableContentDashboardItem<FileEntry>)
			_contentDashboardItemFactory.create(fileEntry.getFileEntryId());
	}

	private VersionableContentDashboardItem<FileEntry>
			_getVersionableContentDashboardItem(
				String fileName, int numVersions)
		throws Exception {

		FileEntry fileEntry = _getFileEntry(fileName, numVersions);

		return (VersionableContentDashboardItem<FileEntry>)
			_contentDashboardItemFactory.create(fileEntry.getFileEntryId());
	}

	private void _updateStatus(
			FileVersion fileVersion, int status, ServiceContext serviceContext)
		throws Exception {

		WorkflowHandler<DLFileEntry> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				DLFileEntry.class.getName());

		workflowHandler.updateStatus(
			status,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
				String.valueOf(fileVersion.getFileVersionId())
			).put(
				WorkflowConstants.CONTEXT_USER_ID,
				String.valueOf(TestPropsValues.getUserId())
			).put(
				"serviceContext", serviceContext
			).build());
	}

	private static String _originalName;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.document.library.internal.item.FileEntryContentDashboardItemFactory"
	)
	private ContentDashboardItemFactory _contentDashboardItemFactory;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseWorkflowedStagedModelDataHandlerTestCase;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class BlogsEntryStagedModelDataHandlerTest
	extends BaseWorkflowedStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testImportedCoverImage() throws Exception {
		initExport();

		BlogsEntry entry = _addBlogsEntryWithCoverImage();

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);

			BlogsEntry importedEntry = (BlogsEntry)getStagedModel(
				entry.getUuid(), liveGroup);

			FileEntry coverImageFileEntry =
				PortletFileRepositoryUtil.getPortletFileEntry(
					importedEntry.getCoverImageFileEntryId());

			Folder coverImageFileEntryFolder = coverImageFileEntry.getFolder();

			Assert.assertEquals(
				liveGroup.getGroupId(), coverImageFileEntry.getGroupId());

			Assert.assertEquals(
				liveGroup.getGroupId(), coverImageFileEntryFolder.getGroupId());
		}
	}

	@Test
	public void testImportedCoverImageAfterUpdate() throws Exception {
		initExport();

		BlogsEntry entry = _addBlogsEntryWithCoverImage();

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		Long coverImageFileEntryId = null;

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);

			BlogsEntry importedEntry = (BlogsEntry)getStagedModel(
				entry.getUuid(), liveGroup);

			coverImageFileEntryId = importedEntry.getCoverImageFileEntryId();
		}

		initExport();

		BlogsEntry updatedEntry = _updateBlogsEntry(entry);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, updatedEntry);

		BlogsEntry importedUpdatedEntry = null;

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedUpdatedEntry =
				(BlogsEntry)readExportedStagedModel(updatedEntry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedUpdatedEntry);

			importedUpdatedEntry = (BlogsEntry)getStagedModel(
				updatedEntry.getUuid(), liveGroup);
		}

		Assert.assertEquals(
			coverImageFileEntryId.longValue(),
			importedUpdatedEntry.getCoverImageFileEntryId());
	}

	@Test
	public void testImportedFriendlyURLCategoriesAfterUpdate()
		throws Exception {

		initExport();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			new long[] {assetCategory.getCategoryId()});

		BlogsEntry entry = _addBlogsEntry(
			null, new ImageSelector(StringUtil.randomString()), serviceContext);

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);

			BlogsEntry importedEntry = (BlogsEntry)getStagedModel(
				entry.getUuid(), liveGroup);

			FriendlyURLEntry mainFriendlyURLEntry =
				_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
					_portal.getClassNameId(BlogsEntry.class.getName()),
					importedEntry.getEntryId());

			Assert.assertNotNull(mainFriendlyURLEntry);

			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				_portal.getClassNameId(FriendlyURLEntry.class.getName()),
				mainFriendlyURLEntry.getFriendlyURLEntryId());

			Assert.assertNotNull(assetEntry);

			List<AssetCategory> assetCategories = assetEntry.getCategories();

			Assert.assertTrue(ListUtil.isNotEmpty(assetCategories));

			Assert.assertEquals(
				assetCategories.toString(), 1, assetCategories.size());

			AssetCategory importedAssetCategory = assetCategories.get(0);

			Assert.assertEquals(
				assetCategory.getName(), importedAssetCategory.getName());
		}
	}

	@Test
	@TestInfo("LPD-107693")
	public void testImportedFriendlyURLEntriesAfterDelete() throws Exception {
		initExport();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		BlogsEntry entry = _addBlogsEntry(
			null, new ImageSelector(StringUtil.randomString()), serviceContext);

		FriendlyURLEntry oldFriendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_portal.getClassNameId(BlogsEntry.class.getName()),
				entry.getEntryId());

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				stagingGroup.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class.getName()),
				entry.getEntryId(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				serviceContext);

		entry.setUrlTitle(friendlyURLEntry.getUrlTitle());

		entry = BlogsEntryLocalServiceUtil.updateBlogsEntry(entry);

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);
		}

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			oldFriendlyURLEntry.getFriendlyURLEntryId(),
			oldFriendlyURLEntry.getDefaultLanguageId());

		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);

			BlogsEntry importedEntry = (BlogsEntry)getStagedModel(
				entry.getUuid(), liveGroup);

			List<FriendlyURLEntry> friendlyURLEntries =
				_friendlyURLEntryLocalService.getFriendlyURLEntries(
					liveGroup.getGroupId(),
					_portal.getClassNameId(BlogsEntry.class.getName()),
					importedEntry.getEntryId());

			Assert.assertEquals(
				friendlyURLEntries.toString(), 1, friendlyURLEntries.size());

			FriendlyURLEntry importedFriendlyURLEntry = friendlyURLEntries.get(
				0);

			Assert.assertEquals(
				friendlyURLEntry.getUuid(), importedFriendlyURLEntry.getUuid());

			Assert.assertNull(
				BlogsEntryLocalServiceUtil.fetchEntry(
					liveGroup.getGroupId(), oldFriendlyURLEntry.getUrlTitle()));
		}
	}

	@Test
	public void testImportedSmallImage() throws Exception {
		initExport();

		BlogsEntry entry = _addBlogsEntryWithSmallImage();

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			Assert.assertNotNull(exportedEntry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);

			BlogsEntry importedEntry = (BlogsEntry)getStagedModel(
				entry.getUuid(), liveGroup);

			FileEntry smallImageFileEntry =
				PortletFileRepositoryUtil.getPortletFileEntry(
					importedEntry.getSmallImageFileEntryId());

			Folder smallImageFileEntryFolder = smallImageFileEntry.getFolder();

			Assert.assertEquals(
				liveGroup.getGroupId(), smallImageFileEntry.getGroupId());

			Assert.assertEquals(
				liveGroup.getGroupId(), smallImageFileEntryFolder.getGroupId());
		}
	}

	@Test
	public void testImportedSmallImageAfterUpdate() throws Exception {
		initExport();

		BlogsEntry entry = _addBlogsEntryWithSmallImage();

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		Long smallImageFileEntryId = null;

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);

			BlogsEntry importedEntry = (BlogsEntry)getStagedModel(
				entry.getUuid(), liveGroup);

			smallImageFileEntryId = importedEntry.getSmallImageFileEntryId();
		}

		initExport();

		BlogsEntry updatedEntry = _updateBlogsEntry(entry);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, updatedEntry);

		BlogsEntry importedUpdatedEntry = null;

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedUpdatedEntry =
				(BlogsEntry)readExportedStagedModel(updatedEntry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedUpdatedEntry);

			importedUpdatedEntry = (BlogsEntry)getStagedModel(
				updatedEntry.getUuid(), liveGroup);
		}

		Assert.assertEquals(
			smallImageFileEntryId.longValue(),
			importedUpdatedEntry.getSmallImageFileEntryId());
	}

	@Test
	public void testImportedSmallImageURL() throws Exception {
		initExport();

		BlogsEntry entry = _addBlogsEntry(
			null, new ImageSelector(StringUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		StagedModelDataHandlerUtil.exportStagedModel(portletDataContext, entry);

		try (SafeCloseable safeCloseable = initImportWithSafeCloseable()) {
			BlogsEntry exportedEntry = (BlogsEntry)readExportedStagedModel(
				entry);

			Assert.assertNotNull(exportedEntry);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedEntry);

			BlogsEntry importedEntry = (BlogsEntry)getStagedModel(
				entry.getUuid(), liveGroup);

			Assert.assertTrue(importedEntry.isSmallImage());
			Assert.assertEquals(
				entry.getSmallImageURL(), importedEntry.getSmallImageURL());
		}
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel addStagedModelWithExternalReferenceCode(
			Group group, String externalReferenceCode,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return BlogsEntryLocalServiceUtil.addEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), true, true,
			new String[0], StringPool.BLANK, null, null,
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));
	}

	@Override
	protected List<StagedModel> addWorkflowedStagedModels(Group group)
		throws Exception {

		List<StagedModel> stagedModels = new ArrayList<>();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		BlogsEntry approvedEntry = BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		stagedModels.add(approvedEntry);

		BlogsEntry pendingEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), false,
			serviceContext);

		stagedModels.add(pendingEntry);

		return stagedModels;
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return BlogsEntryLocalServiceUtil.getBlogsEntryByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return BlogsEntry.class;
	}

	@Override
	protected boolean isCommentableStagedModel() {
		return true;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		BlogsEntry entry = (BlogsEntry)stagedModel;
		BlogsEntry importedEntry = (BlogsEntry)importedStagedModel;

		Assert.assertEquals(
			entry.getExternalReferenceCode(),
			importedEntry.getExternalReferenceCode());
		Assert.assertEquals(entry.getTitle(), importedEntry.getTitle());
		Assert.assertEquals(entry.getSubtitle(), importedEntry.getSubtitle());
		Assert.assertEquals(entry.getUrlTitle(), importedEntry.getUrlTitle());
		Assert.assertEquals(
			entry.getDescription(), importedEntry.getDescription());

		Calendar displayDateCalendar = Calendar.getInstance();
		Calendar importedDisplayDateCalendar = Calendar.getInstance();

		displayDateCalendar.setTime(entry.getDisplayDate());
		importedDisplayDateCalendar.setTime(importedEntry.getDisplayDate());

		displayDateCalendar.set(Calendar.SECOND, 0);
		displayDateCalendar.set(Calendar.MILLISECOND, 0);

		importedDisplayDateCalendar.set(Calendar.SECOND, 0);
		importedDisplayDateCalendar.set(Calendar.MILLISECOND, 0);

		Assert.assertEquals(displayDateCalendar, importedDisplayDateCalendar);

		Assert.assertEquals(
			entry.isAllowPingbacks(), importedEntry.isAllowPingbacks());
		Assert.assertEquals(
			entry.isAllowTrackbacks(), importedEntry.isAllowTrackbacks());
		Assert.assertEquals(
			StringUtil.trim(entry.getTrackbacks()),
			StringUtil.trim(importedEntry.getTrackbacks()));
		Assert.assertEquals(
			entry.getCoverImageCaption(), importedEntry.getCoverImageCaption());
		Assert.assertEquals(entry.isSmallImage(), importedEntry.isSmallImage());
	}

	private BlogsEntry _addBlogsEntry(
			ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws Exception {

		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), true, true,
			new String[0], StringPool.BLANK, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	private BlogsEntry _addBlogsEntryWithCoverImage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		InputStream inputStream = _getInputStream();

		String mimeType = MimeTypesUtil.getContentType(_IMAGE_TITLE);

		ImageSelector imageSelector = new ImageSelector(
			FileUtil.getBytes(inputStream), _IMAGE_TITLE, mimeType,
			_IMAGE_CROP_REGION);

		return _addBlogsEntry(imageSelector, null, serviceContext);
	}

	private BlogsEntry _addBlogsEntryWithSmallImage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		InputStream inputStream = _getInputStream();

		String mimeType = MimeTypesUtil.getContentType(_IMAGE_TITLE);

		ImageSelector imageSelector = new ImageSelector(
			FileUtil.getBytes(inputStream), _IMAGE_TITLE, mimeType,
			StringPool.BLANK);

		return _addBlogsEntry(null, imageSelector, serviceContext);
	}

	private InputStream _getInputStream() {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			"com/liferay/blogs/dependencies/test.jpg");
	}

	private BlogsEntry _updateBlogsEntry(BlogsEntry blogsEntry)
		throws Exception {

		return BlogsEntryLocalServiceUtil.updateEntry(
			blogsEntry.getUserId(), blogsEntry.getEntryId(),
			StringUtil.randomString(), StringUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private static final String _IMAGE_CROP_REGION =
		"{\"height\": 10, \"width\": 10, \"x\": 0, \"y\": 0}";

	private static final String _IMAGE_TITLE = "test.jpg";

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private Portal _portal;

}
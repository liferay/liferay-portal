/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.test.util.AssetListStagingTestUtil;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryLocalServiceUtil;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class AssetListEntryStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		setUpPermissionThreadLocal();

		_liveGroup = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testAssetListAssetEntrySubtypeAfterRepublished()
		throws Exception {

		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_stagingGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup, TestPropsValues.getUserId());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
				ddmStructure.getStructureId(), null,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

		AssetListEntry stagingAssetListEntry =
			AssetListTestUtil.addAssetListEntry(_stagingGroup.getGroupId());

		stagingAssetListEntry.setAssetEntrySubtype(
			String.valueOf(dlFileEntryType.getPrimaryKey()));
		stagingAssetListEntry.setAssetEntryType(DLFileEntry.class.getName());

		stagingAssetListEntry =
			_assetListEntryLocalService.updateAssetListEntry(
				stagingAssetListEntry);

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_stagingGroup.getGroupId(),
			_updateAssetEntry(
				_createDLFileEntry(
					dlFileEntryType.getFileEntryTypeId(),
					_stagingGroup.getGroupId(), serviceContext),
				_stagingGroup.getGroupId()),
			stagingAssetListEntry, SegmentsEntryConstants.ID_DEFAULT);

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_stagingGroup.getGroupId(),
				_updateAssetEntry(
					_createDLFileEntry(
						dlFileEntryType.getFileEntryTypeId(),
						_stagingGroup.getGroupId(), serviceContext),
					_stagingGroup.getGroupId()),
				stagingAssetListEntry, SegmentsEntryConstants.ID_DEFAULT);

		AssetListStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		AssetListEntry liveGroupAssetListEntry =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				stagingAssetListEntry.getUuid(), _liveGroup.getGroupId());

		Assert.assertEquals(
			liveGroupAssetListEntry.getAssetEntryType(),
			stagingAssetListEntry.getAssetEntryType());
		Assert.assertNotEquals(
			liveGroupAssetListEntry.getAssetEntrySubtype(),
			stagingAssetListEntry.getAssetEntrySubtype());

		_assetListEntryAssetEntryRelLocalService.
			deleteAssetListEntryAssetEntryRel(assetListEntryAssetEntryRel);

		List<String> portletIds = ListUtil.fromArray(
			AssetListPortletKeys.ASSET_LIST, DLPortletKeys.DOCUMENT_LIBRARY);

		AssetListStagingTestUtil.publishLayouts(
			_stagingGroup, _liveGroup,
			ExportImportConfigurationParameterMapFactoryUtil.buildParameterMap(
				PortletDataHandlerKeys.DATA_STRATEGY_MIRROR, true, false, true,
				false, false, true, true, true, true, true, null, true, false,
				portletIds, true, null, ExportImportDateUtil.RANGE_ALL, true,
				true, UserIdStrategy.CURRENT_USER_ID));

		liveGroupAssetListEntry =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				stagingAssetListEntry.getUuid(), _liveGroup.getGroupId());

		Assert.assertEquals(
			liveGroupAssetListEntry.getAssetEntryType(),
			stagingAssetListEntry.getAssetEntryType());
		Assert.assertNotEquals(
			liveGroupAssetListEntry.getAssetEntrySubtype(),
			stagingAssetListEntry.getAssetEntrySubtype());
	}

	@Test
	public void testAssetListCopiedWhenLocalStagingActivated()
		throws PortalException {

		AssetListEntry liveAssetListEntry = AssetListTestUtil.addAssetListEntry(
			_liveGroup.getGroupId());

		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		AssetListEntry stagingAssetListEntry =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				liveAssetListEntry.getUuid(), _stagingGroup.getGroupId());

		Assert.assertNotNull(stagingAssetListEntry);
	}

	@Test
	public void testPublishCreateAssetList() throws PortalException {
		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		List<AssetListEntry> originalLiveAssetListEntries =
			_assetListEntryLocalService.getAssetListEntries(
				_liveGroup.getGroupId());

		AssetListTestUtil.addAssetListEntry(_stagingGroup.getGroupId());

		AssetListStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		List<AssetListEntry> actualLiveAssetListEntries =
			_assetListEntryLocalService.getAssetListEntries(
				_liveGroup.getGroupId());

		Assert.assertEquals(
			actualLiveAssetListEntries.toString(),
			originalLiveAssetListEntries.size() + 1,
			actualLiveAssetListEntries.size());
	}

	@Test
	public void testPublishCreateAssetListWithSegments()
		throws PortalException {

		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_stagingGroup.getGroupId());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_stagingGroup.getGroupId());

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_stagingGroup.getGroupId(), assetListEntry,
				segmentsEntry.getSegmentsEntryId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).put(
			"groupIds", String.valueOf(_liveGroup.getGroupId())
		).build();

		assetListEntrySegmentsEntryRel.setTypeSettings(
			unicodeProperties.toString());

		_assetListEntrySegmentsEntryRelLocalService.
			updateAssetListEntrySegmentsEntryRel(
				assetListEntrySegmentsEntryRel);

		AssetListStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		AssetListEntry liveAssetListEntry =
			_assetListEntryLocalService.getAssetListEntryByUuidAndGroupId(
				assetListEntry.getUuid(), _liveGroup.getGroupId());

		SegmentsEntry liveSegmentsEntry =
			_segmentsEntryLocalService.getSegmentsEntryByUuidAndGroupId(
				segmentsEntry.getUuid(), _liveGroup.getGroupId());

		AssetListEntrySegmentsEntryRel liveAssetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					liveAssetListEntry.getAssetListEntryId(),
					liveSegmentsEntry.getSegmentsEntryId());

		Assert.assertNotNull(liveAssetListEntrySegmentsEntryRel);

		UnicodeProperties liveUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				liveAssetListEntry.getTypeSettings(
					liveSegmentsEntry.getSegmentsEntryId())
			).build();

		long groupId = GetterUtil.getLong(
			liveUnicodeProperties.get("groupIds"));

		Assert.assertEquals(_liveGroup.getGroupId(), groupId);
	}

	@Test
	public void testPublishDeleteAssetList() throws PortalException {
		AssetListEntry liveAssetListEntry = AssetListTestUtil.addAssetListEntry(
			_liveGroup.getGroupId());

		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		List<AssetListEntry> originalLiveAssetListEntries =
			_assetListEntryLocalService.getAssetListEntries(
				_liveGroup.getGroupId());

		AssetListEntry stagingAssetListEntry =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				liveAssetListEntry.getUuid(), _stagingGroup.getGroupId());

		_assetListEntryLocalService.deleteAssetListEntry(stagingAssetListEntry);

		AssetListStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		List<AssetListEntry> actualLiveAssetListEntries =
			_assetListEntryLocalService.getAssetListEntries(
				_liveGroup.getGroupId());

		Assert.assertEquals(
			actualLiveAssetListEntries.toString(),
			originalLiveAssetListEntries.size() - 1,
			actualLiveAssetListEntries.size());
	}

	@Test
	public void testPublishDeleteAssetListWithSegments()
		throws PortalException {

		AssetListEntry liveAssetListEntry = AssetListTestUtil.addAssetListEntry(
			_liveGroup.getGroupId());

		SegmentsEntry liveSegmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_liveGroup.getGroupId());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_liveGroup.getGroupId(), liveAssetListEntry,
			liveSegmentsEntry.getSegmentsEntryId());

		int originalLiveAssetListEntrySegmentsEntryRelsCount =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRelsCount(
					liveAssetListEntry.getAssetListEntryId());

		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		AssetListEntry stagingAssetListEntry =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				liveAssetListEntry.getUuid(), _stagingGroup.getGroupId());

		SegmentsEntry stagingSegmentsEntry =
			SegmentsEntryLocalServiceUtil.fetchSegmentsEntryByUuidAndGroupId(
				liveSegmentsEntry.getUuid(), _stagingGroup.getGroupId());

		_assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRel(
				stagingAssetListEntry.getAssetListEntryId(),
				stagingSegmentsEntry.getSegmentsEntryId());

		AssetListStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		int liveAssetListEntrySegmentsEntryRelsCount =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRelsCount(
					liveAssetListEntry.getAssetListEntryId());

		Assert.assertEquals(
			originalLiveAssetListEntrySegmentsEntryRelsCount - 1,
			liveAssetListEntrySegmentsEntryRelsCount);
	}

	@Test
	public void testPublishManualAssetListWithUnstagedPortletAssetEntry()
		throws Exception {

		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		_liveGroup = _groupLocalService.getGroup(_liveGroup.getGroupId());

		UnicodeProperties typeSettingsUnicodeProperties =
			_liveGroup.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			StagingUtil.getStagedPortletId(JournalPortletKeys.JOURNAL),
			Boolean.FALSE.toString());

		_liveGroup = _groupLocalService.updateGroup(
			_liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_liveGroup.getGroupId(), 0);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		AssetListEntry stagingAssetListEntry =
			AssetListTestUtil.addAssetListEntry(
				_stagingGroup.getGroupId(),
				AssetListEntryTypeConstants.TYPE_MANUAL);

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_stagingGroup.getGroupId(), assetEntry, stagingAssetListEntry,
			SegmentsEntryConstants.ID_DEFAULT);

		AssetListStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		AssetListEntry liveAssetListEntry =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				stagingAssetListEntry.getUuid(), _liveGroup.getGroupId());

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					liveAssetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetListEntryAssetEntryRels.toString(), 1,
			assetListEntryAssetEntryRels.size());

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRels.get(0);

		Assert.assertEquals(
			assetEntry.getEntryId(),
			assetListEntryAssetEntryRel.getAssetEntryId());
	}

	@Test
	public void testPublishUpdateAssetList() throws PortalException {
		AssetListEntry liveAsset = AssetListTestUtil.addAssetListEntry(
			_liveGroup.getGroupId(), "Test Title Original");

		_stagingGroup = AssetListStagingTestUtil.enableLocalStaging(
			_liveGroup, true);

		AssetListEntry stagingAsset =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				liveAsset.getUuid(), _stagingGroup.getGroupId());

		Assert.assertEquals(stagingAsset.getTitle(), liveAsset.getTitle());

		stagingAsset = _assetListEntryLocalService.updateAssetListEntry(
			stagingAsset.getAssetListEntryId(), "Test Title Edit");

		AssetListStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		liveAsset =
			_assetListEntryLocalService.fetchAssetListEntryByUuidAndGroupId(
				stagingAsset.getUuid(), _liveGroup.getGroupId());

		Assert.assertEquals(stagingAsset.getTitle(), liveAsset.getTitle());
	}

	protected void setUpPermissionThreadLocal() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
	}

	private DLFileEntry _createDLFileEntry(
			long fileEntryTypeId, long groupId, ServiceContext serviceContext)
		throws Exception {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), groupId, groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null, fileEntryTypeId, null,
			null, new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
			serviceContext);

		return _dlFileEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), dlFileEntry,
			dlFileEntry.getLatestFileVersion(true),
			WorkflowConstants.STATUS_APPROVED, serviceContext,
			Collections.emptyMap());
	}

	private AssetEntry _updateAssetEntry(DLFileEntry dlFileEntry, long groupId)
		throws Exception {

		return _assetEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), groupId, dlFileEntry.getCreateDate(),
			dlFileEntry.getModifiedDate(), DLFileEntry.class.getName(),
			dlFileEntry.getFileEntryId(), dlFileEntry.getUuid(),
			dlFileEntry.getFileEntryTypeId(), null, null, true, true, null,
			null, null, dlFileEntry.getExpirationDate(), ContentTypes.TEXT_HTML,
			dlFileEntry.getTitle(), dlFileEntry.getDescription(),
			dlFileEntry.getDescription(), null, null, 0, 0, 0.0D);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private PermissionChecker _originalPermissionChecker;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@DeleteAfterTestRun
	private Group _stagingGroup;

}
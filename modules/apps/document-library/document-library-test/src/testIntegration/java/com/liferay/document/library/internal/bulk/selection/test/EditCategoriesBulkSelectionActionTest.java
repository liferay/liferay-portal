/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.bulk.selection.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class EditCategoriesBulkSelectionActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		_assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		_assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
	}

	@Test
	@TestInfo({"LPD-113588", "LPS-94955", "LPS-94956"})
	public void testExecute() throws Exception {
		FileEntry fileEntry1 = _addFileEntry();
		FileEntry fileEntry2 = _addFileEntry();

		_execute(
			true, new long[] {_assetCategory1.getCategoryId()}, new long[0],
			fileEntry1, fileEntry2);

		_assertAssetCategoryIds(fileEntry1, _assetCategory1.getCategoryId());
		_assertAssetCategoryIds(fileEntry2, _assetCategory1.getCategoryId());

		FileEntry fileEntry3 = _addFileEntry(
			_assetCategory1.getCategoryId(), _assetCategory2.getCategoryId());
		FileEntry fileEntry4 = _addFileEntry(
			_assetCategory1.getCategoryId(), _assetCategory3.getCategoryId());

		_execute(
			true, new long[0], new long[] {_assetCategory1.getCategoryId()},
			fileEntry3, fileEntry4);

		_assertAssetCategoryIds(fileEntry3, _assetCategory2.getCategoryId());
		_assertAssetCategoryIds(fileEntry4, _assetCategory3.getCategoryId());
	}

	private FileEntry _addFileEntry(long... assetCategoryIds) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			serviceContext);
	}

	private void _assertAssetCategoryIds(
			FileEntry fileEntry, long... assetCategoryIds)
		throws Exception {

		fileEntry = _dlAppLocalService.getFileEntry(fileEntry.getFileEntryId());

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(),
			_dlAssetHelper.getAssetClassPK(
				fileEntry, fileEntry.getLatestFileVersion()));

		long[] actualAssetCategoryIds = assetEntry.getCategoryIds();

		Arrays.sort(actualAssetCategoryIds);

		Arrays.sort(assetCategoryIds);

		Assert.assertArrayEquals(assetCategoryIds, actualAssetCategoryIds);
	}

	private void _execute(
			boolean append, long[] toAddCategoryIds, long[] toRemoveCategoryIds,
			FileEntry... fileEntries)
		throws Exception {

		BulkSelection<FileEntry> bulkSelection = _bulkSelectionFactory.create(
			HashMapBuilder.put(
				"rowIdsFileEntry",
				TransformUtil.transform(
					fileEntries,
					fileEntry -> String.valueOf(fileEntry.getFileEntryId()),
					String.class)
			).build());

		_bulkSelectionAction.execute(
			TestPropsValues.getUser(),
			bulkSelection.toAssetEntryBulkSelection(),
			HashMapBuilder.<String, Serializable>put(
				"append", append
			).put(
				"toAddCategoryIds", ArrayUtil.toArray(toAddCategoryIds)
			).put(
				"toRemoveCategoryIds", ArrayUtil.toArray(toRemoveCategoryIds)
			).build());
	}

	private AssetCategory _assetCategory1;
	private AssetCategory _assetCategory2;
	private AssetCategory _assetCategory3;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject(filter = "bulk.selection.action.key=edit.categories")
	private BulkSelectionAction<AssetEntry> _bulkSelectionAction;

	@Inject(
		filter = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	)
	private BulkSelectionFactory<FileEntry> _bulkSelectionFactory;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAssetHelper _dlAssetHelper;

	@DeleteAfterTestRun
	private Group _group;

}
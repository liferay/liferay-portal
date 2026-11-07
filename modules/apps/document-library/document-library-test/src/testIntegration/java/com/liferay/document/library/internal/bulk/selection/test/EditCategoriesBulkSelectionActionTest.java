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
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
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

		_category1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		_category2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		_category3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
	}

	@Test
	@TestInfo({"LPD-113588", "LPS-94955", "LPS-94956"})
	public void testExecute() throws Exception {
		FileEntry fileEntry1 = _addFileEntry();
		FileEntry fileEntry2 = _addFileEntry();

		_execute(
			true, new long[] {_category1.getCategoryId()}, new long[0],
			fileEntry1, fileEntry2);

		_assertCategoryIds(fileEntry1, _category1.getCategoryId());
		_assertCategoryIds(fileEntry2, _category1.getCategoryId());

		FileEntry fileEntry3 = _addFileEntry(
			_category1.getCategoryId(), _category2.getCategoryId());
		FileEntry fileEntry4 = _addFileEntry(
			_category1.getCategoryId(), _category3.getCategoryId());

		_execute(
			true, new long[0], new long[] {_category1.getCategoryId()},
			fileEntry3, fileEntry4);

		_assertCategoryIds(fileEntry3, _category2.getCategoryId());
		_assertCategoryIds(fileEntry4, _category3.getCategoryId());
	}

	private FileEntry _addFileEntry(long... categoryIds) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetCategoryIds(categoryIds);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			serviceContext);
	}

	private void _assertCategoryIds(FileEntry fileEntry, long... categoryIds)
		throws Exception {

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		long[] actualCategoryIds = assetEntry.getCategoryIds();

		Arrays.sort(actualCategoryIds);

		Arrays.sort(categoryIds);

		Assert.assertArrayEquals(categoryIds, actualCategoryIds);
	}

	private void _execute(
			boolean append, long[] toAddCategoryIds, long[] toRemoveCategoryIds,
			FileEntry... fileEntries)
		throws Exception {

		String[] fileEntryIds = new String[fileEntries.length];

		for (int i = 0; i < fileEntries.length; i++) {
			fileEntryIds[i] = String.valueOf(fileEntries[i].getFileEntryId());
		}

		BulkSelection<FileEntry> bulkSelection = _bulkSelectionFactory.create(
			HashMapBuilder.put(
				"rowIdsFileEntry", fileEntryIds
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

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject(filter = "bulk.selection.action.key=edit.categories")
	private BulkSelectionAction<AssetEntry> _bulkSelectionAction;

	@Inject(
		filter = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	)
	private BulkSelectionFactory<FileEntry> _bulkSelectionFactory;

	private AssetCategory _category1;
	private AssetCategory _category2;
	private AssetCategory _category3;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

}
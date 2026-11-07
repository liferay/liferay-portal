/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.bulk.selection.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
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
public class EditTagsBulkSelectionActionTest {

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

	@Test
	@TestInfo({"LPD-113588", "LPS-91586", "LPS-91749"})
	public void testExecute() throws Exception {
		FileEntry fileEntry1 = _addFileEntry("tagone");
		FileEntry fileEntry2 = _addFileEntry("tagone");
		FileEntry fileEntry3 = _addFileEntry();

		_execute(true, new String[] {"tagtwo"}, new String[0], fileEntry1);

		_assertTagNames(fileEntry1, "tagone", "tagtwo");
		_assertTagNames(fileEntry2, "tagone");

		_execute(
			true, new String[] {"tagthree"}, new String[] {"tagone"},
			fileEntry1, fileEntry2, fileEntry3);

		_assertTagNames(fileEntry1, "tagthree", "tagtwo");
		_assertTagNames(fileEntry2, "tagthree");
		_assertTagNames(fileEntry3, "tagthree");

		FileEntry fileEntry4 = _addFileEntry();
		FileEntry fileEntry5 = _addFileEntry();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_dlAppService.checkOutFileEntry(
			fileEntry4.getFileEntryId(), serviceContext);
		_dlAppService.checkOutFileEntry(
			fileEntry5.getFileEntryId(), serviceContext);

		_execute(
			true, new String[] {"tagone"}, new String[0], fileEntry4,
			fileEntry5);

		_assertTagNames(fileEntry4, "tagone");
		_assertTagNames(fileEntry5, "tagone");

		FileEntry fileEntry6 = _addFileEntry("tag1");
		FileEntry fileEntry7 = _addFileEntry("tag2");
		FileEntry fileEntry8 = _addFileEntry("tag3");

		_execute(
			false, new String[] {"newtag"}, new String[] {"tag1", "tag3"},
			fileEntry6, fileEntry8);

		_assertTagNames(fileEntry6, "newtag");
		_assertTagNames(fileEntry7, "tag2");
		_assertTagNames(fileEntry8, "newtag");

		_execute(
			false, new String[0], new String[] {"newtag", "tag2"}, fileEntry6,
			fileEntry7);

		_assertTagNames(fileEntry6);
		_assertTagNames(fileEntry7);
		_assertTagNames(fileEntry8, "newtag");
	}

	private FileEntry _addFileEntry(String... tagNames) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetTagNames(tagNames);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			serviceContext);
	}

	private void _assertTagNames(FileEntry fileEntry, String... tagNames)
		throws Exception {

		fileEntry = _dlAppLocalService.getFileEntry(fileEntry.getFileEntryId());

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(),
			_dlAssetHelper.getAssetClassPK(
				fileEntry, fileEntry.getLatestFileVersion()));

		String[] actualTagNames = assetEntry.getTagNames();

		Arrays.sort(actualTagNames);

		Arrays.sort(tagNames);

		Assert.assertArrayEquals(tagNames, actualTagNames);
	}

	private void _execute(
			boolean append, String[] toAddTagNames, String[] toRemoveTagNames,
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
				"toAddTagNames", toAddTagNames
			).put(
				"toRemoveTagNames", toRemoveTagNames
			).build());
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject(filter = "bulk.selection.action.key=edit.tags")
	private BulkSelectionAction<AssetEntry> _bulkSelectionAction;

	@Inject(
		filter = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	)
	private BulkSelectionFactory<FileEntry> _bulkSelectionFactory;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLAssetHelper _dlAssetHelper;

	@DeleteAfterTestRun
	private Group _group;

}
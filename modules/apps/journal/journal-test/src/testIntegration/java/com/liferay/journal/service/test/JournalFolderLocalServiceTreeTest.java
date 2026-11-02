/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.TreeModel;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.local.service.tree.test.util.BaseLocalServiceTreeTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shinn Lok
 */
@RunWith(Arquillian.class)
public class JournalFolderLocalServiceTreeTest
	extends BaseLocalServiceTreeTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testJournalFolderTreePathWhenMovingFolderWithSubfolder()
		throws Exception {

		List<JournalFolder> folders = new ArrayList<>();

		JournalFolder folderA = _journalFolderFixture.addFolder(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"Folder A");

		folders.add(folderA);

		JournalFolder folderAA = _journalFolderFixture.addFolder(
			group.getGroupId(), folderA.getFolderId(), "Folder AA");

		folders.add(folderAA);

		JournalFolder folderAAA = _journalFolderFixture.addFolder(
			group.getGroupId(), folderAA.getFolderId(), "Folder AAA");

		folders.add(folderAAA);

		_journalFolderLocalService.moveFolder(
			folderAA.getFolderId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		for (JournalFolder folder : folders) {
			folder = _journalFolderLocalService.fetchFolder(
				folder.getFolderId());

			Assert.assertEquals(folder.buildTreePath(), folder.getTreePath());
		}
	}

	@Test
	public void testSearchTreePath() throws Exception {
		Indexer<JournalFolder> indexer = _indexerRegistry.getIndexer(
			JournalFolder.class);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		Hits hits = indexer.search(searchContext);

		int initialFoldersCount = hits.getLength();

		JournalFolder folderA = _journalFolderFixture.addFolder(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"Folder A");

		JournalFolder folderB = _journalFolderFixture.addFolder(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"Folder B");

		_journalFolderFixture.addFolder(
			group.getGroupId(), folderA.getFolderId(), "Folder AA");

		_journalFolderFixture.addFolder(
			group.getGroupId(), folderB.getFolderId(), "Folder BA");

		hits = indexer.search(searchContext);

		Assert.assertEquals(
			hits.toString(), initialFoldersCount + 4, hits.getLength());

		searchContext.setFolderIds(new long[] {folderA.getFolderId()});

		hits = indexer.search(searchContext);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());
	}

	@Override
	protected TreeModel addTreeModel(TreeModel parentTreeModel)
		throws Exception {

		long parentFolderId = JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (parentTreeModel != null) {
			JournalFolder folder = (JournalFolder)parentTreeModel;

			parentFolderId = folder.getFolderId();
		}

		JournalFolder folder = _journalFolderFixture.addFolder(
			group.getGroupId(), parentFolderId, RandomTestUtil.randomString());

		folder.setTreePath("/0/");

		return _journalFolderLocalService.updateJournalFolder(folder);
	}

	@Override
	protected void deleteTreeModel(TreeModel treeModel) throws Exception {
		JournalFolder folder = (JournalFolder)treeModel;

		_journalFolderLocalService.deleteFolder(folder);
	}

	@Override
	protected TreeModel getTreeModel(long primaryKey) throws Exception {
		return _journalFolderLocalService.getFolder(primaryKey);
	}

	@Override
	protected void rebuildTree() throws Exception {
		_journalFolderLocalService.rebuildTree(
			TestPropsValues.getCompanyId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, StringPool.SLASH,
			false);
	}

	@Inject
	private static JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private IndexerRegistry _indexerRegistry;

	private final JournalFolderFixture _journalFolderFixture =
		new JournalFolderFixture(_journalFolderLocalService);

}
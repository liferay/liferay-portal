/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderServiceUtil;
import com.liferay.bookmarks.test.util.BookmarksTestUtil;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.test.util.lar.BasePortletDataHandlerTestCase;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class BookmarksPortletDataHandlerTest
	extends BasePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		super.setUp();
	}

	@Test
	public void testDeleteAllFolders() throws Exception {
		Group group = GroupTestUtil.addGroup();

		BookmarksFolder parentFolder = BookmarksTestUtil.addFolder(
			group.getGroupId(), "parent");

		BookmarksFolder childFolder = BookmarksTestUtil.addFolder(
			group.getGroupId(), parentFolder.getFolderId(), "child");

		BookmarksFolderServiceUtil.moveFolderToTrash(
			childFolder.getPrimaryKey());

		BookmarksFolderServiceUtil.moveFolderToTrash(
			parentFolder.getPrimaryKey());

		BookmarksFolderServiceUtil.deleteFolder(parentFolder.getFolderId());

		GroupLocalServiceUtil.deleteGroup(group);

		List<BookmarksFolder> folders =
			BookmarksFolderLocalServiceUtil.getFolders(group.getGroupId());

		Assert.assertEquals(folders.toString(), 0, folders.size());
	}

	@Override
	protected void addStagedModels() throws Exception {
		BookmarksFolder folder1 = BookmarksTestUtil.addFolder(
			stagingGroup.getGroupId(), RandomTestUtil.randomString());

		BookmarksTestUtil.addFolder(
			stagingGroup.getGroupId(), RandomTestUtil.randomString());

		BookmarksTestUtil.addEntry(
			folder1.getFolderId(), true,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));
	}

	@Override
	protected DataLevel getDataLevel() {
		return DataLevel.PORTLET_INSTANCE;
	}

	@Override
	protected String[] getDataPortletPreferences() {
		return new String[] {"rootFolderId"};
	}

	@Override
	protected String getPortletId() {
		return BookmarksPortletKeys.BOOKMARKS;
	}

	@Override
	protected List<StagedModel> getStagedModels() {
		List<StagedModel> stagedModels = new ArrayList<>();

		List<BookmarksFolder> folders =
			BookmarksFolderLocalServiceUtil.getFolders(
				portletDataContext.getGroupId());

		for (BookmarksFolder folder : folders) {
			stagedModels.addAll(
				BookmarksEntryLocalServiceUtil.getEntries(
					portletDataContext.getGroupId(), folder.getFolderId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		stagedModels.addAll(folders);

		return stagedModels;
	}

	@Override
	protected boolean isDataPortalLevel() {
		return false;
	}

	@Override
	protected boolean isDataPortletInstanceLevel() {
		return true;
	}

	@Override
	protected boolean isDataSiteLevel() {
		return false;
	}

	@Override
	protected boolean isGetExportModelCountTested() {
		return true;
	}

}
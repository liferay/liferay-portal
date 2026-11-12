/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.social.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.bookmarks.test.util.BookmarksTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.social.activity.test.util.BaseSocialActivityInterpreterTestCase;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class BookmarksFolderActivityInterpreterTest
	extends BaseSocialActivityInterpreterTestCase {

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

	@Override
	protected void addActivities() throws Exception {
		_folder = BookmarksTestUtil.addFolder(group.getGroupId(), "Folder");

		_bookmarksFolderLocalService.moveFolderToTrash(
			TestPropsValues.getUserId(), _folder.getFolderId());

		_bookmarksFolderLocalService.restoreFolderFromTrash(
			TestPropsValues.getUserId(), _folder.getFolderId());
	}

	@Override
	protected SocialActivityInterpreter getActivityInterpreter() {
		return _socialActivityInterpreter;
	}

	@Override
	protected int[] getActivityTypes() {
		return new int[] {
			SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			SocialActivityConstants.TYPE_RESTORE_FROM_TRASH
		};
	}

	@Override
	protected boolean isSupportsRename() {
		return false;
	}

	@Override
	protected void moveModelsToTrash() throws Exception {
		_bookmarksFolderLocalService.moveFolderToTrash(
			TestPropsValues.getUserId(), _folder.getFolderId());
	}

	@Override
	protected void renameModels() {
	}

	@Override
	protected void restoreModelsFromTrash() throws Exception {
		_bookmarksFolderLocalService.restoreFolderFromTrash(
			TestPropsValues.getUserId(), _folder.getFolderId());
	}

	@Inject
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

	private BookmarksFolder _folder;

	@Inject(
		filter = "model.class.name=com.liferay.bookmarks.model.BookmarksFolder"
	)
	private SocialActivityInterpreter _socialActivityInterpreter;

}
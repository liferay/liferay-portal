/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael C. Han
 */
@RunWith(Arquillian.class)
public class JournalFolderLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddFolder() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		JournalFolder journalFolder = _addJournalFolder(
			user.getUserId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_assertJournalFolder(journalFolder, user);
	}

	@Test
	public void testGetFoldersAndArticlesCount() throws Exception {
		JournalFolder parentJournalFolder = _addJournalFolder();

		_addApprovedJournalArticle(parentJournalFolder);

		JournalFolder journalFolder = _addJournalFolder(
			TestPropsValues.getUserId(), parentJournalFolder.getFolderId());

		_addExpiredJournalArticle(
			journalFolder,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		_addDeletedJournalArticle(journalFolder);

		List<Long> folderIds = Arrays.asList(
			parentJournalFolder.getFolderId(), journalFolder.getFolderId());

		_testGetFoldersAndArticlesCount(
			1, folderIds, _group.getGroupId(),
			WorkflowConstants.STATUS_APPROVED);
		_testGetFoldersAndArticlesCount(
			1, folderIds, _group.getGroupId(),
			WorkflowConstants.STATUS_EXPIRED);
		_testGetFoldersAndArticlesCount(
			1, folderIds, _group.getGroupId(),
			WorkflowConstants.STATUS_IN_TRASH);
	}

	@Test
	public void testUpdateFolder() throws Exception {
		JournalFolder journalFolder = _addJournalFolder();

		User user = UserTestUtil.addGroupAdminUser(_group);

		journalFolder = _journalFolderLocalService.updateFolder(
			user.getUserId(), _group.getGroupId(), journalFolder.getFolderId(),
			journalFolder.getParentFolderId(), journalFolder.getName(),
			journalFolder.getDescription(), new long[0],
			JournalFolderConstants.RESTRICTION_TYPE_INHERIT, false,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId()));

		_assertJournalFolder(journalFolder, user);
	}

	private JournalArticle _addApprovedJournalArticle(
			JournalFolder journalFolder)
		throws Exception {

		return JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(),
			Collections.emptyMap());
	}

	private JournalArticle _addDeletedJournalArticle(
			JournalFolder journalFolder)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(),
			Collections.emptyMap());

		return _journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), journalArticle);
	}

	private JournalArticle _addExpiredJournalArticle(
			JournalFolder journalFolder, ServiceContext serviceContext)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(),
			Collections.emptyMap());

		return _journalArticleLocalService.expireArticle(
			TestPropsValues.getUserId(), _group.getGroupId(),
			journalArticle.getArticleId(), journalArticle.getVersion(), null,
			serviceContext);
	}

	private JournalFolder _addJournalFolder() throws Exception {
		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		return journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());
	}

	private JournalFolder _addJournalFolder(long userId, long parentFolderId)
		throws Exception {

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		return journalFolderFixture.addFolder(
			userId, _group.getGroupId(), parentFolderId,
			RandomTestUtil.randomString());
	}

	private void _assertJournalFolder(JournalFolder journalFolder, User user) {
		Assert.assertEquals(
			journalFolder.getStatusByUserId(), user.getUserId());
		Assert.assertEquals(
			journalFolder.getStatusByUserName(), user.getFullName());
	}

	private void _testGetFoldersAndArticlesCount(
		long expectedCount, List<Long> folderIds, long groupId, int status) {

		Assert.assertEquals(
			expectedCount,
			_journalFolderLocalService.getFoldersAndArticlesCount(
				groupId, folderIds, status));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

}
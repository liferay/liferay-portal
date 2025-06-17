/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.performance.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class JournalFolderServicePerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		_group = GroupTestUtil.addGroup();

		_journalFolder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		for (int i = 0; i < _BATCH_SIZE; i++) {
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), _journalFolder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());
		}

		for (int i = 0; i < _BATCH_SIZE; i++) {
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());
		}

		_user = UserTestUtil.addUser(_group.getGroupId());

		UserTestUtil.setUser(_user);
	}

	@After
	public void tearDown() throws Exception {
		_ctCollectionLocalService.deleteCTCollection(_ctCollection);

		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testGetDDMStructures() throws Exception {
		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				5000, "Production")) {

			_journalFolderService.getDDMStructures(
				new long[] {_group.getGroupId()},
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_journalFolder.getRestrictionType(), null);

			_journalFolderService.getDDMStructures(
				new long[] {_group.getGroupId()}, _journalFolder.getFolderId(),
				_journalFolder.getRestrictionType(), null);
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				5000, "Publication");
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_journalFolderService.getDDMStructures(
				new long[] {_group.getGroupId()},
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_journalFolder.getRestrictionType(), null);

			_journalFolderService.getDDMStructures(
				new long[] {_group.getGroupId()}, _journalFolder.getFolderId(),
				_journalFolder.getRestrictionType(), null);
		}
	}

	@Test
	public void testGetFoldersAndArticles() throws Exception {
		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				5000, "Production")) {

			_journalFolderService.getFoldersAndArticles(
				_group.getGroupId(), TestPropsValues.getUserId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			_journalFolderService.getFoldersAndArticles(
				_group.getGroupId(), TestPropsValues.getUserId(),
				_journalFolder.getFolderId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(
				5000, "Publication");
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_journalFolderService.getFoldersAndArticles(
				_group.getGroupId(), TestPropsValues.getUserId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			_journalFolderService.getFoldersAndArticles(
				_group.getGroupId(), TestPropsValues.getUserId(),
				_journalFolder.getFolderId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
	}

	private static final int _BATCH_SIZE = 500;

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	private Group _group;
	private JournalFolder _journalFolder;

	@Inject
	private JournalFolderService _journalFolderService;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private User _user;

}
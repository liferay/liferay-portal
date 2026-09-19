/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class JournalIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setCompanyId(TestPropsValues.getCompanyId());

		UserTestUtil.setUser(TestPropsValues.getUser());

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				TestPropsValues.getUserId(), true);

		_originalPortalPreferencesXML = PortletPreferencesFactoryUtil.toXML(
			portalPreferences);

		portalPreferences.setValue(
			"", "indexAllArticleVersionsEnabled", "true");
		portalPreferences.setValue(
			"", "expireAllArticleVersionsEnabled", "true");

		PortalPreferencesLocalServiceUtil.updatePreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			PortletPreferencesFactoryUtil.toXML(portalPreferences));
	}

	@After
	public void tearDown() throws Exception {
		PortalPreferencesLocalServiceUtil.updatePreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			_originalPortalPreferencesXML);
	}

	@Test
	public void testAddArticleApprove() throws Exception {
		addArticle(true);
	}

	@Test
	public void testAddArticleNotApprove() throws Exception {
		addArticle(false);
	}

	@Test
	public void testCopyArticle() throws Exception {
		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setKeywords("Architectural");

		assertSearchCount(0, _group.getGroupId(), searchContext);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title",
			"Liferay Architectural Approach", true);

		assertSearchCount(1, _group.getGroupId(), searchContext);

		JournalArticleLocalServiceUtil.copyArticle(
			TestPropsValues.getUserId(), _group.getGroupId(),
			article.getArticleId(), StringPool.BLANK, true,
			article.getVersion());

		assertSearchCount(2, _group.getGroupId(), searchContext);
	}

	@Test
	public void testDeleteAllArticleVersion() throws Exception {
		articleVersions(true, true);
	}

	@Test
	public void testDeleteArticleVersion() throws Exception {
		articleVersions(true, false);
	}

	@Test
	public void testDeleteArticles() throws Exception {
		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setKeywords("Architectural");

		assertSearchCount(0, _group.getGroupId(), searchContext);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle article1 = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title",
			"Liferay Architectural Approach", true);

		assertSearchCount(1, _group.getGroupId(), searchContext);

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			"Architectural Approach");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		JournalTestUtil.updateArticle(
			article1, article1.getTitleMap(), content, false, true,
			serviceContext);

		JournalArticle article2 = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title",
			"Apple Architectural Tablet", true);

		assertSearchCount(2, _group.getGroupId(), searchContext);

		content = DDMStructureTestUtil.getSampleStructuredContent(
			"Architectural Tablet");

		JournalTestUtil.updateArticle(
			article2, article2.getTitleMap(), content, false, true,
			serviceContext);

		JournalArticleLocalServiceUtil.deleteArticles(_group.getGroupId());

		assertSearchCount(0, _group.getGroupId(), searchContext);
	}

	@Test
	public void testExpireAllArticleVersions() throws Exception {
		articleVersions(false, true);
	}

	@Test
	public void testExpireArticleVersion() throws Exception {
		articleVersions(false, false);
	}

	@Test
	public void testIndexVersions() throws Exception {
		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		assertSearchCount(0, _group.getGroupId(), searchContext);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		String content = "Liferay Architectural Approach";

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title", content, true);

		assertSearchCount(
			1, _group.getGroupId(), false, WorkflowConstants.STATUS_ANY,
			searchContext);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		article = JournalTestUtil.updateArticle(
			article, article.getTitleMap(), article.getContent(), false, true,
			serviceContext);

		assertSearchCount(
			2, _group.getGroupId(), false, WorkflowConstants.STATUS_ANY,
			searchContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalTestUtil.updateArticle(
			article, article.getTitleMap(), article.getContent(), false, true,
			serviceContext);

		assertSearchCount(
			3, _group.getGroupId(), false, WorkflowConstants.STATUS_ANY,
			searchContext);
	}

	@Test
	public void testIndexVersionsDelete() throws Exception {
		indexVersions(true, false);
	}

	@Test
	public void testIndexVersionsDeleteAll() throws Exception {
		indexVersions(true, true);
	}

	@Test
	public void testIndexVersionsExpire() throws Exception {
		indexVersions(false, false);
	}

	@Test
	public void testIndexVersionsExpireAll() throws Exception {
		indexVersions(false, true);
	}

	@Test
	public void testMoveArticle() throws Exception {
		moveArticle(false);
	}

	@Test
	public void testMoveArticleFromTrash() throws Exception {
		moveArticle(true);
	}

	@Test
	public void testMoveArticleToTrashAndRestore() throws Exception {
		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setKeywords("Architectural");

		assertSearchCount(0, _group.getGroupId(), searchContext);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title",
			"Liferay Architectural Approach", true);

		assertSearchCount(1, _group.getGroupId(), searchContext);

		article = JournalArticleLocalServiceUtil.moveArticleToTrash(
			TestPropsValues.getUserId(), article);

		assertSearchCount(0, _group.getGroupId(), searchContext);
		assertSearchCount(
			1, _group.getGroupId(), true, WorkflowConstants.STATUS_IN_TRASH,
			searchContext);

		JournalArticleLocalServiceUtil.restoreArticleFromTrash(
			TestPropsValues.getUserId(), article);

		assertSearchCount(1, _group.getGroupId(), searchContext);
		assertSearchCount(
			0, _group.getGroupId(), true, WorkflowConstants.STATUS_IN_TRASH,
			searchContext);
	}

	@Test
	public void testRemoveArticleLocale() throws Exception {
		SearchContext searchContext1 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext1.setKeywords("Arquitectura");
		searchContext1.setLocale(LocaleUtil.SPAIN);

		assertSearchCount(0, _group.getGroupId(), searchContext1);

		SearchContext searchContext2 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext2.setKeywords("Architectural");
		searchContext2.setLocale(LocaleUtil.SPAIN);

		assertSearchCount(0, _group.getGroupId(), searchContext2);

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.GERMANY, "Titel"
		).put(
			LocaleUtil.SPAIN, "Titulo"
		).put(
			LocaleUtil.US, "Title"
		).build();

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), titleMap, titleMap,
			HashMapBuilder.put(
				LocaleUtil.GERMANY, "Liferay Architektur Ansatz"
			).put(
				LocaleUtil.SPAIN, "Liferay Arquitectura Aproximacion"
			).put(
				LocaleUtil.US, "Liferay Architectural Approach"
			).build(),
			true);

		assertSearchCount(1, _group.getGroupId(), searchContext1);

		JournalArticleLocalServiceUtil.removeArticleLocale(
			_group.getGroupId(), article.getArticleId(), article.getVersion(),
			LocaleUtil.toLanguageId(LocaleUtil.SPAIN));

		assertSearchCount(0, _group.getGroupId(), searchContext1);
		assertSearchCount(1, _group.getGroupId(), searchContext2);
	}

	@Test
	public void testUpdateArticleAndApprove() throws Exception {
		updateArticle(true);
	}

	@Test
	public void testUpdateArticleAndDraft() throws Exception {
		updateArticle(false);
	}

	@Test
	public void testUpdateArticleTranslation() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		SearchContext searchContext1 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext1.setKeywords("Arquitectura");
		searchContext1.setLocale(LocaleUtil.SPAIN);

		assertSearchCount(0, _group.getGroupId(), searchContext1);

		SearchContext searchContext2 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext2.setKeywords("Apple");
		searchContext2.setLocale(LocaleUtil.SPAIN);

		assertSearchCount(0, _group.getGroupId(), searchContext2);

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.GERMANY, "Titel"
		).put(
			LocaleUtil.SPAIN, "Titulo"
		).put(
			LocaleUtil.US, "Title"
		).build();

		Map<Locale, String> contentMap = HashMapBuilder.put(
			LocaleUtil.GERMANY, "Liferay Architektur Ansatz"
		).put(
			LocaleUtil.SPAIN, "Liferay Arquitectura Aproximacion"
		).put(
			LocaleUtil.US, "Liferay Architectural Approach"
		).build();

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), titleMap, titleMap, contentMap, true);

		assertSearchCount(1, _group.getGroupId(), searchContext1);

		contentMap.put(LocaleUtil.SPAIN, "Apple manzana tablet");

		String defaultLanguageId = LanguageUtil.getLanguageId(
			LocaleUtil.getDefault());

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			contentMap, defaultLanguageId);

		article = JournalArticleLocalServiceUtil.updateArticleTranslation(
			_group.getGroupId(), article.getArticleId(), article.getVersion(),
			LocaleUtil.SPAIN, article.getTitle(LocaleUtil.SPAIN),
			article.getDescription(LocaleUtil.SPAIN), content, null,
			serviceContext);

		assertSearchCount(0, _group.getGroupId(), searchContext2);

		User user = UserTestUtil.addUser(_group.getGroupId(), LocaleUtil.SPAIN);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		JournalArticleLocalServiceUtil.updateArticle(
			user.getUserId(), article.getGroupId(), article.getFolderId(),
			article.getArticleId(), article.getVersion(), article.getContent(),
			serviceContext);

		assertSearchCount(1, _group.getGroupId(), searchContext2);
	}

	protected void addArticle(boolean approve) throws Exception {
		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setKeywords("Architectural");

		assertSearchCount(0, _group.getGroupId(), searchContext);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title",
			"Liferay Architectural Approach", approve);

		if (approve) {
			assertSearchCount(1, _group.getGroupId(), searchContext);
		}
		else {
			assertSearchCount(0, _group.getGroupId(), searchContext);
		}
	}

	protected JournalArticle addJournalWithDDMStructure(
			long folderId, String keywords, ServiceContext serviceContext)
		throws Exception {

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm("name");

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			serviceContext.getScopeGroupId(), JournalArticle.class.getName(),
			ddmForm);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			serviceContext.getScopeGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			"name", keywords);

		return JournalTestUtil.addArticleWithXMLContent(
			folderId, content, ddmStructure.getStructureKey(),
			ddmTemplate.getTemplateKey(), serviceContext);
	}

	protected void articleVersions(boolean delete, boolean all)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		SearchContext searchContext1 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext1.setKeywords("Architectural");

		assertSearchCount(0, _group.getGroupId(), searchContext1);

		SearchContext searchContext2 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext2.setKeywords("Apple");

		assertSearchCount(0, _group.getGroupId(), searchContext2);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title",
			"Liferay Architectural Approach", true);

		assertSearchCount(1, _group.getGroupId(), searchContext1);

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			"Apple tablet");

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		article = JournalTestUtil.updateArticle(
			article, article.getTitleMap(), content, false, true,
			serviceContext);

		assertSearchCount(0, _group.getGroupId(), searchContext1);
		assertSearchCount(1, _group.getGroupId(), searchContext2);

		if (all) {
			if (delete) {
				JournalArticleLocalServiceUtil.deleteArticle(
					_group.getGroupId(), article.getArticleId(),
					serviceContext);
			}
			else {
				JournalArticleLocalServiceUtil.expireArticle(
					TestPropsValues.getUserId(), _group.getGroupId(),
					article.getArticleId(), article.getUrlTitle(),
					serviceContext);
			}

			assertSearchCount(0, _group.getGroupId(), searchContext1);
		}
		else {
			if (delete) {
				JournalArticleLocalServiceUtil.deleteArticle(article);
			}
			else {
				JournalArticleLocalServiceUtil.expireArticle(
					TestPropsValues.getUserId(), _group.getGroupId(),
					article.getArticleId(), article.getVersion(),
					article.getUrlTitle(), serviceContext);
			}

			assertSearchCount(1, _group.getGroupId(), searchContext1);
		}

		assertSearchCount(0, _group.getGroupId(), searchContext2);
	}

	protected void assertSearchCount(
			int expectedCount, long groupId, boolean head, int status,
			SearchContext searchContext)
		throws Exception {

		Hits hits = search(groupId, head, status, searchContext);

		Assert.assertEquals(hits.toString(), expectedCount, hits.getLength());
	}

	protected void assertSearchCount(
			int expectedCount, long groupId, SearchContext searchContext)
		throws Exception {

		Hits hits = search(groupId, searchContext);

		Assert.assertEquals(hits.toString(), expectedCount, hits.getLength());
	}

	protected void indexVersions(boolean delete, boolean all) throws Exception {
		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		assertSearchCount(0, _group.getGroupId(), searchContext);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		String content = "Liferay Architectural Approach";

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title", content, true);

		assertSearchCount(1, _group.getGroupId(), searchContext);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		article = JournalTestUtil.updateArticle(
			article, article.getTitleMap(), article.getContent(), false, true,
			serviceContext);

		assertSearchCount(
			2, _group.getGroupId(), false, WorkflowConstants.STATUS_ANY,
			searchContext);

		if (delete) {
			if (all) {
				JournalArticleLocalServiceUtil.deleteArticle(
					_group.getGroupId(), article.getArticleId(),
					serviceContext);

				assertSearchCount(
					0, _group.getGroupId(), false, WorkflowConstants.STATUS_ANY,
					searchContext);
			}
			else {
				JournalArticleLocalServiceUtil.deleteArticle(article);

				assertSearchCount(
					1, _group.getGroupId(), false, WorkflowConstants.STATUS_ANY,
					searchContext);
			}
		}
		else {
			if (all) {
				JournalArticleLocalServiceUtil.expireArticle(
					TestPropsValues.getUserId(), _group.getGroupId(),
					article.getArticleId(), article.getUrlTitle(),
					serviceContext);
			}
			else {
				JournalArticleLocalServiceUtil.expireArticle(
					TestPropsValues.getUserId(), _group.getGroupId(),
					article.getArticleId(), article.getVersion(),
					article.getUrlTitle(), serviceContext);
			}

			assertSearchCount(
				2, _group.getGroupId(), false, WorkflowConstants.STATUS_ANY,
				searchContext);
		}
	}

	protected void moveArticle(boolean moveToTrash) throws Exception {
		SearchContext searchContext1 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext1.setKeywords("Architectural");

		JournalFolder folder1 = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		searchContext1.setFolderIds(new long[] {folder1.getFolderId()});

		assertSearchCount(0, _group.getGroupId(), searchContext1);

		SearchContext searchContext2 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext2.setKeywords("Architectural");

		JournalFolder folder2 = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		searchContext2.setFolderIds(new long[] {folder2.getFolderId()});

		assertSearchCount(0, _group.getGroupId(), searchContext2);

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder1.getFolderId(), "title",
			"Liferay Architectural Approach", true);

		assertSearchCount(1, _group.getGroupId(), searchContext1);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		if (moveToTrash) {
			JournalFolderLocalServiceUtil.moveFolderToTrash(
				TestPropsValues.getUserId(), folder1.getFolderId());

			assertSearchCount(0, _group.getGroupId(), searchContext1);

			article = JournalArticleLocalServiceUtil.getArticle(
				article.getId());

			JournalArticleLocalServiceUtil.moveArticleFromTrash(
				TestPropsValues.getUserId(), _group.getGroupId(), article,
				folder2.getFolderId(), serviceContext);
		}
		else {
			JournalArticleLocalServiceUtil.moveArticle(
				_group.getGroupId(), article.getArticleId(),
				folder2.getFolderId(), serviceContext);
		}

		assertSearchCount(0, _group.getGroupId(), searchContext1);
		assertSearchCount(1, _group.getGroupId(), searchContext2);
	}

	protected Hits search(
			long groupId, boolean head, int status, SearchContext searchContext)
		throws Exception {

		Indexer<JournalArticle> indexer = IndexerRegistryUtil.getIndexer(
			JournalArticle.class);

		searchContext.setAttribute("head", head);
		searchContext.setAttribute("status", status);
		searchContext.setGroupIds(new long[] {groupId});

		return indexer.search(searchContext);
	}

	protected Hits search(long groupId, SearchContext searchContext)
		throws Exception {

		return search(
			groupId, true, WorkflowConstants.STATUS_APPROVED, searchContext);
	}

	protected void updateArticle(boolean approve) throws Exception {
		SearchContext searchContext1 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext1.setKeywords("Architectural");

		assertSearchCount(0, _group.getGroupId(), searchContext1);

		SearchContext searchContext2 = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext2.setKeywords("Apple");

		assertSearchCount(0, _group.getGroupId(), searchContext2);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), folder.getFolderId(), "title",
			"Liferay Architectural Approach", true);

		assertSearchCount(1, _group.getGroupId(), searchContext1);

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			"Apple tablet");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		if (!approve) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}
		else {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}

		JournalTestUtil.updateArticle(
			article, article.getTitleMap(), content, false, true,
			serviceContext);

		if (approve) {
			assertSearchCount(0, _group.getGroupId(), searchContext1);
			assertSearchCount(1, _group.getGroupId(), searchContext2);
		}
		else {
			assertSearchCount(1, _group.getGroupId(), searchContext1);
			assertSearchCount(0, _group.getGroupId(), searchContext2);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	private String _originalPortalPreferencesXML;

}
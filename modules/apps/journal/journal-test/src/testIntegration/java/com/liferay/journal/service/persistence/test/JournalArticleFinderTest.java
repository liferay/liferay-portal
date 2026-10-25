/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.persistence.JournalArticleFinder;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.comparator.ArticleCreateDateComparator;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.journal.util.comparator.ArticleIDComparator;
import com.liferay.journal.util.comparator.ArticleModifiedDateComparator;
import com.liferay.journal.util.comparator.ArticleReviewDateComparator;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 * @author László Csontos
 */
@RunWith(Arquillian.class)
public class JournalArticleFinderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.SUPPORTS, "com.liferay.journal.service"));

	@Before
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		_folder = JournalTestUtil.addFolder(_group.getGroupId(), "Folder 1");

		_basicWebContentDDMStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		_basicWebContentDDMTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), _basicWebContentDDMStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), _folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			"<title>Article 1</title>",
			_basicWebContentDDMStructure.getStructureKey(),
			_basicWebContentDDMTemplate.getTemplateKey());

		_articles.add(article);

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), "Folder 2");

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), _ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		article = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			"<title>Article 2</title>", _ddmStructure.getStructureKey(),
			ddmTemplate.getTemplateKey());

		_articles.add(article);

		article = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			"<title>Article 3</title>",
			_basicWebContentDDMStructure.getStructureKey(),
			_basicWebContentDDMTemplate.getTemplateKey());

		_articles.add(article);

		article.setUserId(_USER_ID);

		Calendar calendar = new GregorianCalendar();

		calendar.add(Calendar.DATE, -1);

		article.setExpirationDate(calendar.getTime());
		article.setReviewDate(calendar.getTime());

		article = JournalArticleLocalServiceUtil.updateJournalArticle(article);

		JournalArticleLocalServiceUtil.moveArticleToTrash(
			TestPropsValues.getUserId(), article);

		article = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), folder.getFolderId(),
			PortalUtil.getClassNameId(DDMStructure.class),
			"<title>Article 4</title>", _ddmStructure.getStructureKey(),
			ddmTemplate.getTemplateKey());

		_articles.add(article);

		_folderIds.clear();

		_folderIds.add(_folder.getFolderId());
		_folderIds.add(folder.getFolderId());

		_article = _articles.get(0);
	}

	@Test
	public void testDraftArticles() throws Exception {
		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		testQueryByG_C(
			_group.getGroupId(), Collections.<Long>emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, queryDefinition, 2);

		queryDefinition.setOwnerUserId(TestPropsValues.getUserId());

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), _folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			"<title>Article 1</title>",
			_basicWebContentDDMStructure.getStructureKey(),
			_basicWebContentDDMTemplate.getTemplateKey());

		article.setUserId(_USER_ID);
		article.setStatus(WorkflowConstants.STATUS_DRAFT);

		article = JournalArticleLocalServiceUtil.updateJournalArticle(article);

		_articles.add(article);

		queryDefinition.setIncludeOwner(true);
		queryDefinition.setOwnerUserId(_USER_ID);
		queryDefinition.setStatus(WorkflowConstants.STATUS_APPROVED);

		testQueryByG_C(
			_group.getGroupId(), Collections.<Long>emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, queryDefinition, 3);

		queryDefinition.setIncludeOwner(false);

		testQueryByG_C(
			_group.getGroupId(), Collections.<Long>emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, queryDefinition, 0);
	}

	@Test
	public void testFindByG_F_C_L() throws Exception {
		List<Long> folderIds = new ArrayList<>();

		folderIds.add(_folder.getFolderId());

		QueryDefinition<JournalArticle> queryDefinition =
			new QueryDefinition<>();

		queryDefinition.setIncludeOwner(true);
		queryDefinition.setOwnerUserId(TestPropsValues.getUserId());
		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);

		List<JournalArticle> articles =
			_journalArticleFinder.filterFindByG_F_C_L(
				_group.getGroupId(), folderIds,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				LocaleUtil.getSiteDefault(), queryDefinition);

		Assert.assertEquals(articles.toString(), 1, articles.size());

		Assert.assertEquals(articles.get(0), _article);
	}

	@Test
	public void testLocalizedQueryByG_F_L() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), "Localized folder 1");

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folder.getFolderId());

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.FRANCE, "AA"
		).put(
			LocaleUtil.US, "FF"
		).build();

		JournalTestUtil.addArticle(
			_group.getGroupId(), folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, titleMap, titleMap,
			titleMap, LocaleUtil.US, true, true, serviceContext);

		titleMap = HashMapBuilder.put(
			LocaleUtil.FRANCE, "BB"
		).put(
			LocaleUtil.US, "EE"
		).build();

		JournalTestUtil.addArticle(
			_group.getGroupId(), folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, titleMap, titleMap,
			titleMap, LocaleUtil.US, true, true, serviceContext);

		titleMap = HashMapBuilder.put(
			LocaleUtil.FRANCE, "CC"
		).put(
			LocaleUtil.US, "DD"
		).build();

		JournalTestUtil.addArticle(
			_group.getGroupId(), folder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, titleMap, titleMap,
			titleMap, LocaleUtil.US, true, true, serviceContext);

		QueryDefinition<JournalArticle> queryDefinition =
			new QueryDefinition<>();

		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);

		int actualCount = _journalArticleFinder.countByG_F(
			_group.getGroupId(), folderIds, queryDefinition);

		Assert.assertEquals(3, actualCount);

		testLocalizedQueryByG_F_L(
			_group.getGroupId(), folderIds, LocaleUtil.FRANCE, true, "AA", "BB",
			"CC");
		testLocalizedQueryByG_F_L(
			_group.getGroupId(), folderIds, LocaleUtil.FRANCE, false, "CC",
			"BB", "AA");
		testLocalizedQueryByG_F_L(
			_group.getGroupId(), folderIds, LocaleUtil.US, true, "DD", "EE",
			"FF");
		testLocalizedQueryByG_F_L(
			_group.getGroupId(), folderIds, LocaleUtil.US, false, "FF", "EE",
			"DD");
	}

	@Test
	public void testQueryByG_F() throws Exception {

		// Status any

		QueryDefinition<JournalArticle> queryDefinition =
			new QueryDefinition<>();

		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);

		testQueryByG_F(_group.getGroupId(), _folderIds, queryDefinition, 4);

		// Status in trash

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH);

		testQueryByG_F(_group.getGroupId(), _folderIds, queryDefinition, 1);

		// Status not in trash

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);

		testQueryByG_F(_group.getGroupId(), _folderIds, queryDefinition, 3);

		// Comparators

		testQueryByG_F(ArticleCreateDateComparator.getInstance(true));
		testQueryByG_F(ArticleCreateDateComparator.getInstance(false));
		testQueryByG_F(ArticleDisplayDateComparator.getInstance(true));
		testQueryByG_F(ArticleDisplayDateComparator.getInstance(false));
		testQueryByG_F(ArticleIDComparator.getInstance(true));
		testQueryByG_F(ArticleIDComparator.getInstance(false));
		testQueryByG_F(ArticleModifiedDateComparator.getInstance(true));
		testQueryByG_F(ArticleModifiedDateComparator.getInstance(false));
		testQueryByG_F(ArticleReviewDateComparator.getInstance(true));
		testQueryByG_F(ArticleReviewDateComparator.getInstance(false));
		testQueryByG_F(ArticleVersionComparator.getInstance(true));
		testQueryByG_F(ArticleVersionComparator.getInstance(false));
	}

	@Test
	public void testQueryByG_F_C() throws Exception {

		// Status any (constructor), which is status not in trash

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		queryDefinition.setOwnerUserId(TestPropsValues.getUserId());

		testQueryByG_C(
			_group.getGroupId(), Collections.<Long>emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, queryDefinition, 2);

		// Status any

		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);
		queryDefinition.setOwnerUserId(_USER_ID);

		testQueryByG_C(
			_group.getGroupId(), Collections.<Long>emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, queryDefinition, 1);

		// Status in trash

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH);
		queryDefinition.setOwnerUserId(_USER_ID);

		testQueryByG_C(
			_group.getGroupId(), Collections.<Long>emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, queryDefinition, 1);

		// Status not in trash

		queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);
		queryDefinition.setOwnerUserId(_USER_ID);

		testQueryByG_C(
			_group.getGroupId(), Collections.<Long>emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, queryDefinition, 0);
	}

	protected void prepareSortedArticles() throws Exception {
		Calendar calendar = new GregorianCalendar();

		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2014);

		for (int i = 0; i < _articles.size(); ++i) {
			calendar.add(Calendar.DATE, 1);

			JournalArticle article = _articles.get(i);

			article = JournalArticleLocalServiceUtil.getArticle(
				article.getId());

			article.setCreateDate(calendar.getTime());
			article.setModifiedDate(calendar.getTime());
			article.setArticleId("a" + i);
			article.setVersion(i);
			article.setDisplayDate(calendar.getTime());
			article.setReviewDate(calendar.getTime());

			article = JournalArticleLocalServiceUtil.updateJournalArticle(
				article);

			_articles.set(i, article);
		}
	}

	protected void testLocalizedQueryByG_F_L(
			long groupId, List<Long> folderIds, Locale locale,
			boolean ascending, String... expectedTitles)
		throws Exception {

		QueryDefinition<JournalArticle> queryDefinition =
			new QueryDefinition<>();

		queryDefinition.setOrderByComparator(
			OrderByComparatorFactoryUtil.create(
				"JournalArticleLocalization", "title", ascending));
		queryDefinition.setStatus(WorkflowConstants.STATUS_ANY);

		List<JournalArticle> articles = _journalArticleFinder.filterFindByG_F_L(
			groupId, folderIds, locale, queryDefinition);

		int actualCount = articles.size();

		Assert.assertEquals(expectedTitles.length, actualCount);

		String[] actualTitles = new String[actualCount];

		for (int i = 0; i < actualCount; ++i) {
			JournalArticle article = articles.get(i);

			actualTitles[i] = article.getTitle(locale);
		}

		Assert.assertArrayEquals(expectedTitles, actualTitles);
	}

	protected void testQueryByG_C(
			long groupId, List<Long> folderIds, long classNameId,
			QueryDefinition<JournalArticle> queryDefinition, int expectedCount)
		throws Exception {

		int actualCount = _journalArticleFinder.filterCountByG_F_C(
			groupId, folderIds, classNameId, queryDefinition);

		Assert.assertEquals(expectedCount, actualCount);
	}

	protected void testQueryByG_F(
			long groupId, List<Long> folderIds,
			QueryDefinition<JournalArticle> queryDefinition, int expectedCount)
		throws Exception {

		int actualCount = _journalArticleFinder.countByG_F(
			groupId, folderIds, queryDefinition);

		Assert.assertEquals(expectedCount, actualCount);

		Locale siteDefaultLocale = LocaleUtil.getSiteDefault();

		List<JournalArticle> articles = _journalArticleFinder.filterFindByG_F_L(
			groupId, folderIds, siteDefaultLocale, queryDefinition);

		actualCount = articles.size();

		Assert.assertEquals(expectedCount, actualCount);
	}

	protected void testQueryByG_F(
			OrderByComparator<JournalArticle> orderByComparator)
		throws Exception {

		prepareSortedArticles();

		QueryDefinition<JournalArticle> queryDefinition =
			new QueryDefinition<>();

		queryDefinition.setOrderByComparator(orderByComparator);

		List<JournalArticle> expectedArticles = null;

		if (orderByComparator.isAscending()) {
			expectedArticles = _articles;
		}
		else {
			expectedArticles = new ArrayList<>(_articles);

			Collections.reverse(expectedArticles);
		}

		Locale siteDefaultLocale = LocaleUtil.getSiteDefault();

		List<JournalArticle> actualArticles =
			_journalArticleFinder.filterFindByG_F_L(
				_group.getGroupId(), _folderIds, siteDefaultLocale,
				queryDefinition);

		Assert.assertEquals(expectedArticles, actualArticles);
	}

	private static final long _USER_ID = 1234L;

	private JournalArticle _article;
	private final List<JournalArticle> _articles = new ArrayList<>();
	private DDMStructure _basicWebContentDDMStructure;
	private DDMTemplate _basicWebContentDDMTemplate;
	private DDMStructure _ddmStructure;
	private JournalFolder _folder;
	private final List<Long> _folderIds = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleFinder _journalArticleFinder;

}
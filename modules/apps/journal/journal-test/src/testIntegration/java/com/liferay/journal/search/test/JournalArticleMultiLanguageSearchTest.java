/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleDescription;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
@Sync
public class JournalArticleMultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_indexer = indexerRegistry.getIndexer(JournalArticle.class);

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			ddmStructureLocalService, journalArticleLocalService, portal);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_groups = _userSearchFixture.getGroups();
		_users = _userSearchFixture.getUsers();

		_group = _userSearchFixture.addGroup();

		_user = _userSearchFixture.addUser(
			RandomTestUtil.randomString(), _group);
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleSearchFixture.tearDown();

		_userSearchFixture.tearDown();
	}

	@Test
	public void testContent() throws Exception {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		assertSearchMatchesAllArticles(LocaleUtil.US, US_CONTENT);
	}

	@Test
	public void testContentWithAlternateDisplayLanguage() {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		assertSearchMatchesAllArticles(LocaleUtil.NETHERLANDS, US_CONTENT);
	}

	@Test
	public void testDescription() {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		assertSearchMatchesAllArticles(LocaleUtil.US, US_DESCRIPTION);
	}

	@Test
	public void testDescriptionWithAlternateDisplayLanguage() {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		assertSearchMatchesAllArticles(LocaleUtil.NETHERLANDS, US_DESCRIPTION);
	}

	@Test
	public void testEmptySearch() {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		SearchContext searchContext = getSearchContext(LocaleUtil.US);

		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH, Boolean.TRUE);

		assertSearchMatchesAllArticles(searchContext);
	}

	@Test
	public void testEmptySearchWithAlternateDisplayLanguage() {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		SearchContext searchContext = getSearchContext(LocaleUtil.NETHERLANDS);

		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH, Boolean.TRUE);

		assertSearchMatchesAllArticles(searchContext);
	}

	@Test
	public void testTitle() throws Exception {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		assertSearchMatchesAllArticles(LocaleUtil.US, US_TITLE);
	}

	@Test
	public void testTitleWithAlternateDisplayLanguage() throws Exception {
		addArticlesWithEnglishWordsInUsAndNlTranslations();

		assertSearchMatchesAllArticles(LocaleUtil.NETHERLANDS, US_TITLE);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void addArticlesWithEnglishWordsInUsAndNlTranslations() {
		addJournalArticle(
			new JournalArticleContent() {
				{
					put(LocaleUtil.NETHERLANDS, NL_CONTENT);
					put(LocaleUtil.US, US_CONTENT);

					setDefaultLocale(LocaleUtil.US);
					setName("content");
				}
			},
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, NL_DESCRIPTION);
					put(LocaleUtil.US, US_DESCRIPTION);
				}
			},
			new JournalArticleTitle() {
				{
					put(LocaleUtil.NETHERLANDS, NL_TITLE);
					put(LocaleUtil.US, US_TITLE);
				}
			});

		addJournalArticle(
			new JournalArticleContent() {
				{
					put(LocaleUtil.US, US_CONTENT);

					setDefaultLocale(LocaleUtil.US);
					setName("content");
				}
			},
			new JournalArticleDescription() {
				{
					put(LocaleUtil.US, US_DESCRIPTION);
				}
			},
			new JournalArticleTitle() {
				{
					put(LocaleUtil.US, US_TITLE);
				}
			});

		addJournalArticle(
			new JournalArticleContent() {
				{
					put(LocaleUtil.NETHERLANDS, US_CONTENT);

					setDefaultLocale(LocaleUtil.NETHERLANDS);
					setName("content");
				}
			},
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, US_DESCRIPTION);
				}
			},
			new JournalArticleTitle() {
				{
					put(LocaleUtil.NETHERLANDS, US_TITLE);
				}
			});
	}

	protected JournalArticle addJournalArticle(
		JournalArticleContent journalArticleContentParam,
		JournalArticleDescription journalArticleDescriptionParam,
		JournalArticleTitle journalArticleTitleParam) {

		return _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(journalArticleContentParam);
					setJournalArticleDescription(
						journalArticleDescriptionParam);
					setJournalArticleTitle(journalArticleTitleParam);
					setUserId(_user.getUserId());
				}
			});
	}

	protected void assertSearchMatchesAllArticles(
		Locale locale, String searchTerm) {

		assertSearchMatchesAllArticles(getSearchContext(locale, searchTerm));
	}

	protected void assertSearchMatchesAllArticles(SearchContext searchContext) {
		Hits hits = search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.ARTICLE_ID,
			TransformUtil.transform(
				_journalArticles, JournalArticle::getArticleId));
	}

	protected SearchContext getSearchContext(Locale locale) {
		try {
			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(TestPropsValues.getCompanyId());
			searchContext.setGroupIds(new long[] {_group.getGroupId()});
			searchContext.setLocale(locale);

			QueryConfig queryConfig = searchContext.getQueryConfig();

			queryConfig.setLocale(locale);

			return searchContext;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected SearchContext getSearchContext(Locale locale, String searchTerm) {
		SearchContext searchContext = getSearchContext(locale);

		searchContext.setKeywords(searchTerm);

		return searchContext;
	}

	protected Hits search(SearchContext searchContext) {
		try {
			return _indexer.search(searchContext);
		}
		catch (SearchException searchException) {
			throw new RuntimeException(searchException);
		}
	}

	protected static final String ES_TITLE = "ingles";

	protected static final String NL_CONTENT = "inhoud";

	protected static final String NL_DESCRIPTION = "beschrijving";

	protected static final String NL_TITLE = "engels";

	protected static final String US_CONTENT = "content";

	protected static final String US_DESCRIPTION = "description";

	protected static final String US_TITLE = "english";

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected IndexerRegistry indexerRegistry;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected Portal portal;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private Indexer<JournalArticle> _indexer;
	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private User _user;
	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}
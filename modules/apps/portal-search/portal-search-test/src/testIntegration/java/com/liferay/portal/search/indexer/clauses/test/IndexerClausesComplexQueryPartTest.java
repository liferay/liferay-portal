/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.indexer.clauses.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.search.BlogsEntryBlueprint.BlogsEntryBlueprintBuilder;
import com.liferay.blogs.test.util.search.BlogsEntrySearchFixture;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprintBuilder;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class IndexerClausesComplexQueryPartTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(!_isSearchEngine("Solr"));

		BlogsEntrySearchFixture blogsEntrySearchFixture =
			new BlogsEntrySearchFixture(blogsEntryLocalService);

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		JournalArticleSearchFixture journalArticleSearchFixture =
			new JournalArticleSearchFixture(
				ddmStructureLocalService, journalArticleLocalService, portal);

		_blogsEntries = blogsEntrySearchFixture.getBlogsEntries();
		_blogsEntrySearchFixture = blogsEntrySearchFixture;
		_group = groupSearchFixture.addGroup(new GroupBlueprint());
		_groups = groupSearchFixture.getGroups();
		_journalArticles = journalArticleSearchFixture.getJournalArticles();
		_journalArticleSearchFixture = journalArticleSearchFixture;
		_user = TestPropsValues.getUser();
	}

	@Test
	public void testDefaultIndexer1() throws Exception {
		Assert.assertEquals(
			"class com.liferay.portal.search.internal.indexer.DefaultIndexer",
			String.valueOf(journalArticleIndexer.getClass()));

		addJournalArticle("Gamma Article");
		addJournalArticle("Omega Article");

		_consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				JournalArticle.class
			).queryString(
				"gamma"
			);

		_query = QueriesUtil.match(_TITLE_EN_US, "omega");

		assertSearch("[Gamma Article]");
		assertSearch("[Gamma Article]", should());
		assertSearch("[]", must());
		assertSearch("[Gamma Article, Omega Article]", shouldAdditive());
		assertSearch("[Omega Article]", mustAdditive());

		assertSearch("[Gamma Article, Omega Article]", withoutIndexerClauses());
		assertSearch(
			"[Gamma Article, Omega Article]", should(),
			withoutIndexerClauses());
		assertSearch("[Omega Article]", must(), withoutIndexerClauses());
		assertSearch(
			"[Gamma Article, Omega Article]", shouldAdditive(),
			withoutIndexerClauses());
		assertSearch(
			"[Omega Article]", mustAdditive(), withoutIndexerClauses());
	}

	@Test
	public void testDefaultIndexer2() throws Exception {
		Assert.assertEquals(
			"class com.liferay.portal.search.internal.indexer.DefaultIndexer",
			String.valueOf(blogsEntryIndexer.getClass()));

		addBlogsEntry("Gamma Blog");
		addBlogsEntry("Omega Blog");

		_consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				BlogsEntry.class
			).queryString(
				"gamma"
			);

		_query = QueriesUtil.match(_TITLE_EN_US, "omega");

		assertSearch("[Gamma Blog]");
		assertSearch("[Gamma Blog]", should());
		assertSearch("[]", must());
		assertSearch("[Gamma Blog, Omega Blog]", shouldAdditive());
		assertSearch("[Omega Blog]", mustAdditive());

		assertSearch("[Gamma Blog, Omega Blog]", withoutIndexerClauses());
		assertSearch(
			"[Gamma Blog, Omega Blog]", should(), withoutIndexerClauses());
		assertSearch("[Omega Blog]", must(), withoutIndexerClauses());
		assertSearch(
			"[Gamma Blog, Omega Blog]", shouldAdditive(),
			withoutIndexerClauses());
		assertSearch("[Omega Blog]", mustAdditive(), withoutIndexerClauses());
	}

	@Test
	public void testFacetedSearcher() throws Exception {
		addBlogsEntry("Gamma Blog");
		addBlogsEntry("Omega Blog");
		addJournalArticle("Gamma Article");
		addJournalArticle("Omega Article");
		addMessage("Gamma Message");
		addMessage("Omega Message");

		_consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				BlogsEntry.class, JournalArticle.class
			).queryString(
				"gamma"
			);

		_query = QueriesUtil.match(_TITLE_EN_US, "omega");

		assertSearch("[Gamma Article, Gamma Blog]");
		assertSearch("[Gamma Article, Gamma Blog]", should());
		assertSearch("[]", must());
		assertSearch(
			"[Gamma Article, Gamma Blog, Omega Article, Omega Blog, Omega " +
				"Message]",
			shouldAdditive());
		assertSearch(
			"[Omega Article, Omega Blog, Omega Message]", mustAdditive());

		assertSearch(
			"[Gamma Article, Gamma Blog, Omega Article, Omega Blog]",
			withoutIndexerClauses());
		assertSearch(
			"[Gamma Article, Gamma Blog, Omega Article, Omega Blog]", should(),
			withoutIndexerClauses());
		assertSearch(
			"[Omega Article, Omega Blog]", must(), withoutIndexerClauses());
		assertSearch(
			"[Gamma Article, Gamma Blog, Omega Article, Omega Blog, Omega " +
				"Message]",
			shouldAdditive(), withoutIndexerClauses());
		assertSearch(
			"[Omega Article, Omega Blog, Omega Message]", mustAdditive(),
			withoutIndexerClauses());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected BlogsEntry addBlogsEntry(String title) {
		return _blogsEntrySearchFixture.addBlogsEntry(
			BlogsEntryBlueprintBuilder.builder(
			).content(
				RandomTestUtil.randomString()
			).groupId(
				_group.getGroupId()
			).title(
				title
			).userId(
				_user.getUserId()
			).build());
	}

	protected JournalArticle addJournalArticle(String title) {
		return _journalArticleSearchFixture.addArticle(
			JournalArticleBlueprintBuilder.builder(
			).groupId(
				_group.getGroupId()
			).journalArticleContent(
				new JournalArticleContent() {
					{
						put(LocaleUtil.US, RandomTestUtil.randomString());

						setDefaultLocale(LocaleUtil.US);
						setName("content");
					}
				}
			).journalArticleTitle(
				new JournalArticleTitle() {
					{
						put(LocaleUtil.US, title);
					}
				}
			).userId(
				_user.getUserId()
			).build());
	}

	protected MBMessage addMessage(String title) throws Exception {
		return mbMessageLocalService.addMessage(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			_group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			0L, MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, title,
			RandomTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			null, false, 0.0, false, _createServiceContext());
	}

	protected void assertSearch(
		String expected, Consumer<SearchRequestBuilder>... consumers) {

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).withSearchRequestBuilder(
				ArrayUtil.append(consumers, _consumer)
			).build());

		String requestString = searchResponse.getRequestString();

		String evidence = "match_all";

		if (requestString.contains(evidence)) {
			Assert.assertEquals("NOT " + evidence, requestString);
		}

		DocumentsAssert.assertValuesIgnoreRelevance(
			requestString, searchResponse.getDocuments(), _TITLE_EN_US,
			expected);
	}

	protected ComplexQueryPart getComplexQueryPart(Query query) {
		return _complexQueryPartBuilderFactory.builder(
		).occur(
			"must"
		).query(
			query
		).build();
	}

	protected Consumer<SearchRequestBuilder> must() {
		return withPart("must", _query);
	}

	protected Consumer<SearchRequestBuilder> mustAdditive() {
		return withPartAdditive("must", _query);
	}

	protected Consumer<SearchRequestBuilder> should() {
		return withPart("should", _query);
	}

	protected Consumer<SearchRequestBuilder> shouldAdditive() {
		return withPartAdditive("should", _query);
	}

	protected Consumer<SearchRequestBuilder> withPart(
		String occur, Query query) {

		return searchRequestBuilder -> searchRequestBuilder.addComplexQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).occur(
				occur
			).query(
				query
			).build());
	}

	protected Consumer<SearchRequestBuilder> withPartAdditive(
		String occur, Query query) {

		return searchRequestBuilder -> searchRequestBuilder.addComplexQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).additive(
				true
			).occur(
				occur
			).query(
				query
			).build());
	}

	protected Consumer<SearchRequestBuilder> withoutIndexerClauses() {
		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true));
	}

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected static Portal portal;

	@Inject(filter = "indexer.class.name=com.liferay.blogs.model.BlogsEntry")
	protected Indexer<BlogsEntry> blogsEntryIndexer;

	@Inject
	protected BlogsEntryLocalService blogsEntryLocalService;

	@Inject(
		filter = "indexer.class.name=com.liferay.journal.model.JournalArticle"
	)
	protected Indexer<JournalArticle> journalArticleIndexer;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected MBMessageLocalService mbMessageLocalService;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	private ServiceContext _createServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	private boolean _isSearchEngine(String vendor) {
		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		return Objects.equals(searchEngine.getVendor(), vendor);
	}

	private static final String _TITLE_EN_US = StringBundler.concat(
		Field.TITLE, StringPool.UNDERLINE, LocaleUtil.US);

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	private BlogsEntrySearchFixture _blogsEntrySearchFixture;

	@Inject
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	private Consumer<SearchRequestBuilder> _consumer;

	@Inject
	private FacetedSearcherManager _facetedSearcherManager;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private MatchQuery _query;

	@Inject
	private SearchEngineHelper _searchEngineHelper;

	@Inject
	private Sorts _sorts;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}
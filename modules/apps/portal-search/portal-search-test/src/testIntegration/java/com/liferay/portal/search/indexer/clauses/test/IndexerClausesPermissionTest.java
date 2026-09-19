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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserBlueprint;
import com.liferay.users.admin.test.util.search.UserBlueprintImpl.UserBlueprintBuilderImpl;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.time.Month;

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
public class IndexerClausesPermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Assume.assumeTrue(!_isSearchEngine("Solr"));

		BlogsEntrySearchFixture blogsEntrySearchFixture =
			new BlogsEntrySearchFixture(blogsEntryLocalService);

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		JournalArticleSearchFixture journalArticleSearchFixture =
			new JournalArticleSearchFixture(
				ddmStructureLocalService, journalArticleLocalService, portal);
		UserSearchFixture userSearchFixture = new UserSearchFixture();

		_blogsEntries = blogsEntrySearchFixture.getBlogsEntries();
		_blogsEntrySearchFixture = blogsEntrySearchFixture;
		_group = group;
		_groups = groupSearchFixture.getGroups();
		_journalArticles = journalArticleSearchFixture.getJournalArticles();
		_journalArticleSearchFixture = journalArticleSearchFixture;
		_users = userSearchFixture.getUsers();
		_user1 = addUser("user1", group, userSearchFixture);
		_user2 = addUser("user2", group, userSearchFixture);
	}

	@Test
	public void testDefaultIndexer1() throws Exception {
		Assert.assertEquals(
			"class com.liferay.portal.search.internal.indexer.DefaultIndexer",
			String.valueOf(journalArticleIndexer.getClass()));

		addJournalArticle(_user1, "Gamma Article");

		Consumer<SearchRequestBuilder> consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				JournalArticle.class
			).queryString(
				"gamma"
			);

		assertSearch("[Gamma Article]", withPermissionCheck(_user1), consumer);

		assertSearch("[]", withPermissionCheck(_user2), consumer);

		assertSearch(
			"[Gamma Article]", withoutIndexerClauses(),
			withPermissionCheck(_user1), consumer);

		assertSearch(
			"[]", withoutIndexerClauses(), withPermissionCheck(_user2),
			consumer);
	}

	@Test
	public void testDefaultIndexer2() throws Exception {
		Assert.assertEquals(
			"class com.liferay.portal.search.internal.indexer.DefaultIndexer",
			String.valueOf(blogsEntryIndexer.getClass()));

		addBlogsEntry(_user1, "Gamma Blog");

		Consumer<SearchRequestBuilder> consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				BlogsEntry.class
			).queryString(
				"gamma"
			);

		assertSearch("[Gamma Blog]", withPermissionCheck(_user1), consumer);

		assertSearch("[]", withPermissionCheck(_user2), consumer);

		assertSearch(
			"[Gamma Blog]", withoutIndexerClauses(),
			withPermissionCheck(_user1), consumer);

		assertSearch(
			"[]", withoutIndexerClauses(), withPermissionCheck(_user2),
			consumer);
	}

	@Test
	public void testFacetedSearcher() throws Exception {
		addBlogsEntry(_user1, "Gamma Blog");
		addJournalArticle(_user1, "Gamma Article");

		Consumer<SearchRequestBuilder> consumer =
			searchRequestBuilder -> searchRequestBuilder.modelIndexerClasses(
				BlogsEntry.class, JournalArticle.class
			).queryString(
				"gamma"
			);

		assertSearch(
			"[Gamma Article, Gamma Blog]", withPermissionCheck(_user1),
			consumer);

		assertSearch("[]", withPermissionCheck(_user2), consumer);

		assertSearch(
			"[Gamma Article, Gamma Blog]", withoutIndexerClauses(),
			withPermissionCheck(_user1), consumer);

		assertSearch(
			"[]", withoutIndexerClauses(), withPermissionCheck(_user2),
			consumer);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected BlogsEntry addBlogsEntry(User user, String title) {
		return _blogsEntrySearchFixture.addBlogsEntry(
			BlogsEntryBlueprintBuilder.builder(
			).content(
				RandomTestUtil.randomString()
			).groupId(
				_group.getGroupId()
			).serviceContext(
				createServiceContext(_group, user)
			).title(
				title
			).userId(
				user.getUserId()
			).build());
	}

	protected JournalArticle addJournalArticle(User user, String title) {
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
			).serviceContext(
				createServiceContext(_group, user)
			).userId(
				user.getUserId()
			).build());
	}

	protected User addUser(
		String screenName, Group group, UserSearchFixture userSearchFixture) {

		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			new UserBlueprintBuilderImpl();

		String password = RandomTestUtil.randomString();

		return userSearchFixture.addUser(
			userBlueprintBuilder.birthdayDay(
				1
			).birthdayMonth(
				Month.JANUARY.getValue()
			).birthdayYear(
				1970
			).companyId(
				group.getCompanyId()
			).emailAddress(
				screenName + "@example.com"
			).firstName(
				RandomTestUtil.randomString()
			).groupIds(
				group.getGroupId()
			).lastName(
				RandomTestUtil.randomString()
			).locale(
				LocaleUtil.US
			).password1(
				password
			).password2(
				password
			).screenName(
				screenName
			));
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
				consumers
			).build());

		String requestString = searchResponse.getRequestString();

		String evidence = "{\"terms\":{\"groupRoleId\":";

		if (!requestString.contains(evidence)) {
			Assert.assertEquals(evidence, requestString);
		}

		DocumentsAssert.assertValuesIgnoreRelevance(
			requestString, searchResponse.getDocuments(), _TITLE_EN_US,
			expected);
	}

	protected ModelPermissions createModelPermissions() {
		ModelPermissions modelPermissions =
			ModelPermissionsFactory.createForAllResources();

		modelPermissions.addRolePermissions(
			RoleConstants.OWNER, ActionKeys.VIEW);

		return modelPermissions;
	}

	protected ServiceContext createServiceContext(Group group, User user) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setCompanyId(group.getCompanyId());
		serviceContext.setModelPermissions(createModelPermissions());
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	protected Consumer<SearchRequestBuilder> withPermissionCheck(User user) {
		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setUserId(user.getUserId()));
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

	@Inject
	protected ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory;

	@Inject(
		filter = "indexer.class.name=com.liferay.journal.model.JournalArticle"
	)
	protected Indexer<JournalArticle> journalArticleIndexer;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected PermissionCheckerFactory permissionCheckerFactory;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	private boolean _isSearchEngine(String vendor) {
		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		return Objects.equals(searchEngine.getVendor(), vendor);
	}

	private static final String _TITLE_EN_US = StringBundler.concat(
		Field.TITLE, StringPool.UNDERLINE, LocaleUtil.US);

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	private BlogsEntrySearchFixture _blogsEntrySearchFixture;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	@Inject
	private SearchEngineHelper _searchEngineHelper;

	private User _user1;
	private User _user2;

	@DeleteAfterTestRun
	private List<User> _users;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleDescription;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.collapse.CollapseBuilderFactory;
import com.liferay.portal.search.collapse.InnerHitBuilderFactory;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.rescore.RescoreBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class SearchRequestBuilderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			_ddmStructureLocalService, _journalArticleLocalService, _portal);

		_journalArticleSearchFixture.setUp();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_users = _userSearchFixture.getUsers();

		_addGroupAndUser();
	}

	@After
	public void tearDown() throws Exception {
		_userSearchFixture.tearDown();
	}

	@Test
	public void testAddPostFilterQueryPart() throws Exception {
		_addUser("alpha", "omega", "alpha");
		_addUser("alpha", "omega", "omega");
		_addUser("alpha", "omega", "phi");
		_addUser("zeta", "omega", "sigma");

		String queryString = "omega";

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).queryString(
				queryString
			);

		_assertSearch(
			"[alpha omega, alpha omega, alpha omega, zeta omega]", "userName",
			searchRequestBuilder);

		searchRequestBuilder.postFilterQuery(
			QueriesUtil.term("firstName", "alpha"));

		_assertSearch(
			"[alpha omega, alpha omega, alpha omega]", "userName",
			searchRequestBuilder);

		searchRequestBuilder.postFilterQuery(
			null
		).addPostFilterQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).occur(
				"filter"
			).query(
				QueriesUtil.term("firstName", "alpha")
			).build()
		);

		_assertSearch(
			"[alpha omega, alpha omega, alpha omega]", "userName",
			searchRequestBuilder);

		searchRequestBuilder.postFilterQuery(
			null
		).addPostFilterQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).occur(
				"filter"
			).query(
				QueriesUtil.term("screenName", "alpha")
			).build()
		);

		_assertSearch("[alpha omega]", "userName", searchRequestBuilder);
	}

	@Test
	public void testAddPostFilterQueryPartAdditive() throws Exception {
		_addUser("alpha", "delta", "alpha");
		_addUser("alpha", "delta", "omega");
		_addUser("alpha", "delta", "gamma");
		_addUser("omega", "delta", "sigma");

		String queryString = "delta";

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).queryString(
				queryString
			).addPostFilterQueryPart(
				_complexQueryPartBuilderFactory.builder(
				).occur(
					"should"
				).query(
					QueriesUtil.term("screenName", "alpha")
				).build()
			);

		_assertSearch("[alpha delta]", "userName", searchRequestBuilder);

		searchRequestBuilder.addPostFilterQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).occur(
				"should"
			).query(
				QueriesUtil.term("screenName", "sigma")
			).build());

		_assertSearch(
			"[alpha delta, omega delta]", "userName", searchRequestBuilder);
	}

	@Ignore
	@Test
	public void testAddRescore() throws Exception {
		_addUser("alpha", "delta", "AlphaDelta");
		_addUser("beta", "delta", "BetaDelta");
		_addUser("gamma", "delta", "GammaDelta");

		_assertSearch(
			"[alpha delta, beta delta, gamma delta]", "userName", "delta",
			(List<Rescore>)null);

		List<Rescore> rescores = Arrays.asList(
			_buildRescore("userName", "beta delta"));

		_assertSearch(
			"[beta delta, alpha delta, gamma delta]", "userName", "delta",
			rescores);

		rescores = Arrays.asList(
			_buildRescore("userName", "beta delta"),
			_buildRescore("userName", "gamma delta"));

		_assertSearch(
			"[beta delta, gamma delta, alpha delta]", "userName", "delta",
			rescores);
	}

	@Test
	public void testAddSort() throws Exception {
		_addUser("firstName2", "lastName3", "name1");
		_addUser("firstName3", "lastName1", "name2");
		_addUser("firstName1", "lastName2", "name3");

		FieldSort fieldSort = _sorts.field("screenName", SortOrder.DESC);

		_assertSearch("[name3, name2, name1]", "screenName", "name", fieldSort);

		fieldSort = _sorts.field("userName", SortOrder.ASC);

		_assertSearch(
			"[firstname1 lastname2, firstname2 lastname3, firstname3 " +
				"lastname1]",
			"userName", "name", fieldSort);
	}

	@Test
	public void testCollapse() throws Exception {
		_addJournalArticle("stars", "stars", "stars");
		_addJournalArticle("stars", "stars", "stars");

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).collapse(
				_collapseBuilderFactory.builder(
				).field(
					"localized_title_en_US_sortable.keyword_lowercase"
				).build()
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).modelIndexerClassNames(
				JournalArticle.class.getCanonicalName()
			).queryString(
				"stars"
			);

		_assertSearch("[stars]", "title_en_US", searchRequestBuilder);
	}

	@Test
	public void testModelIndexerClassNames() throws Exception {
		_addUser("epsilon", "lambda1", "epsilon");
		_addUser("theta", "lambda2", "theta");
		_addUser("kappa", "lambda3", "kappa");

		String queryString = "lambda";

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClassNames(
				User.class.getCanonicalName()
			).queryString(
				queryString
			);

		_assertSearch(
			"[epsilon lambda1, kappa lambda3, theta lambda2]", "userName",
			searchRequestBuilder);

		searchRequestBuilder.modelIndexerClassNames(
			User.class.getCanonicalName(), UserGroup.class.getCanonicalName());

		_assertSearch(
			"[epsilon lambda1, kappa lambda3, theta lambda2]", "userName",
			searchRequestBuilder);

		searchRequestBuilder.modelIndexerClassNames(
			UserGroup.class.getCanonicalName());

		_assertSearch("[]", "userName", searchRequestBuilder);
	}

	@Test
	public void testModelIndexerClassNamesNotCoreModel() throws Exception {
		_addJournalArticle("epsilon", "epsilon", "lambda1");
		_addJournalArticle("theta", "theta", "lambda2");
		_addJournalArticle("kappa", "kappa", "lambda3");

		String queryString = "lambda";

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClassNames(
				JournalArticle.class.getCanonicalName()
			).queryString(
				queryString
			);

		_assertSearch(
			"[lambda1, lambda2, lambda3]", "title_en_US", searchRequestBuilder);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Rule
	public TestName testName = new TestName();

	private void _addGroupAndUser() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		_group = groupSearchFixture.addGroup(new GroupBlueprint());

		_groups = groupSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(_user));
	}

	private void _addJournalArticle(
		String content, String description, String title) {

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.US, content);

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleDescription(
						new JournalArticleDescription() {
							{
								put(LocaleUtil.US, description);
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, title);
							}
						});
				}
			});
	}

	private void _addUser(String firstName, String lastName, String screenName)
		throws Exception {

		String[] assetTagNames = {};

		_userSearchFixture.addUser(
			screenName, firstName, lastName, LocaleUtil.US, _group,
			assetTagNames);
	}

	private void _assertSearch(
		String expected, String fieldName,
		SearchRequestBuilder searchRequestBuilder) {

		if (!_isElasticsearch()) {
			return;
		}

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			fieldName, expected);
	}

	private void _assertSearch(
		String expected, String fieldName, String queryString,
		List<Rescore> rescores) {

		if (!_isElasticsearch()) {
			return;
		}

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).queryString(
				queryString
			);

		if (rescores != null) {
			for (Rescore rescore : rescores) {
				searchRequestBuilder.addRescore(rescore);
			}
		}

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		DocumentsAssert.assertValues(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			fieldName, expected);
	}

	private void _assertSearch(
		String expected, String fieldName, String queryString, Sort sort) {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).queryString(
				queryString
			).addSort(
				sort
			).build());

		DocumentsAssert.assertValues(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			fieldName, expected);
	}

	private Rescore _buildRescore(String fieldName, String value) {
		return _rescoreBuilderFactory.builder(
			QueriesUtil.match(fieldName, value)
		).windowSize(
			100
		).build();
	}

	private boolean _isElasticsearch() {
		return Objects.equals(_searchEngine.getVendor(), "Elasticsearch");
	}

	@Inject
	private CollapseBuilderFactory _collapseBuilderFactory;

	@Inject
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject
	private InnerHitBuilderFactory _innerHitBuilderFactory;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private Portal _portal;

	@Inject
	private RescoreBuilderFactory _rescoreBuilderFactory;

	@Inject
	private SearchEngine _searchEngine;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	@Inject
	private Sorts _sorts;

	private User _user;
	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}
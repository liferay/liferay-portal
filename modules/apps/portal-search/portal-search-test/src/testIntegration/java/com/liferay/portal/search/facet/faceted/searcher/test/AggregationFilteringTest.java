/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.search.BlogsEntryBlueprint.BlogsEntryBlueprintBuilder;
import com.liferay.blogs.test.util.search.BlogsEntrySearchFixture;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.site.SiteFacetFactory;
import com.liferay.portal.search.facet.type.AssetEntriesFacetFactory;
import com.liferay.portal.search.facet.user.UserFacetFactory;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FacetsAssert;
import com.liferay.portal.search.test.util.SearchMapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Bryan Engler
 */
@RunWith(Arquillian.class)
@Sync
public class AggregationFilteringTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		BlogsEntrySearchFixture blogsEntrySearchFixture =
			new BlogsEntrySearchFixture(blogsEntryLocalService);

		FileEntrySearchFixture fileEntrySearchFixture =
			new FileEntrySearchFixture(dlAppLocalService);

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		JournalArticleSearchFixture journalArticleSearchFixture =
			new JournalArticleSearchFixture(
				ddmStructureLocalService, journalArticleLocalService, portal);

		_blogsEntries = blogsEntrySearchFixture.getBlogsEntries();
		_blogsEntrySearchFixture = blogsEntrySearchFixture;
		_fileEntrySearchFixture = fileEntrySearchFixture;
		_group1 = groupSearchFixture.addGroup(new GroupBlueprint());
		_group2 = groupSearchFixture.addGroup(new GroupBlueprint());
		_groups = groupSearchFixture.getGroups();
		_journalArticles = journalArticleSearchFixture.getJournalArticles();
		_journalArticleSearchFixture = journalArticleSearchFixture;
		_user1 = addUser();
		_user2 = addUser();
		_user3 = addUser();
	}

	@After
	public void tearDown() throws Exception {
		_fileEntrySearchFixture.tearDown();
	}

	@Test
	public void testAvoidResidualDataFromDDMStructureLocalServiceTest()
		throws Exception {

		// See LPS-58543

		String keyword = "To Do";

		index(keyword);

		assertSearch(
			new Expectations() {
				{
					groupFrequencies = SearchMapUtil.join(
						toMap(_group1, 5), toMap(_group2, 4));
					typeFrequencies = SearchMapUtil.join(
						toMap(BlogsEntry.class, 3), toMap(DLFileEntry.class, 3),
						toMap(JournalArticle.class, 3));
					userFrequencies = SearchMapUtil.join(
						toMap(_user1, 4), toMap(_user2, 3), toMap(_user3, 2));
				}
			});
	}

	@Test
	public void testSelectNone() throws Exception {
		String keyword = RandomTestUtil.randomString();

		index(keyword);

		assertSearch(
			new Expectations() {
				{
					groupFrequencies = SearchMapUtil.join(
						toMap(_group1, 5), toMap(_group2, 4));
					typeFrequencies = SearchMapUtil.join(
						toMap(BlogsEntry.class, 3), toMap(DLFileEntry.class, 3),
						toMap(JournalArticle.class, 3));
					userFrequencies = SearchMapUtil.join(
						toMap(_user1, 4), toMap(_user2, 3), toMap(_user3, 2));
				}
			});
	}

	@Test
	public void testSelectOneGroupOneUser() throws Exception {
		String keyword = RandomTestUtil.randomString();

		index(keyword);

		assertSearch(
			new Expectations() {
				{
					groupFrequencies = SearchMapUtil.join(
						toMap(_group1, 3), toMap(_group2, 1));
					selectGroups = new Group[] {_group2};
					selectUsers = new User[] {_user1};
					typeFrequencies = toMap(JournalArticle.class, 1);
					userFrequencies = SearchMapUtil.join(
						toMap(_user1, 1), toMap(_user2, 2), toMap(_user3, 1));
				}
			});
	}

	@Test
	public void testSelectOneGroupOneUserOneType() throws Exception {
		String keyword = RandomTestUtil.randomString();

		index(keyword);

		assertSearch(
			new Expectations() {
				{
					groupFrequencies = SearchMapUtil.join(
						toMap(_group1, 1), toMap(_group2, 1));
					selectGroups = new Group[] {_group2};
					selectTypes = new Class<?>[] {JournalArticle.class};
					selectUsers = new User[] {_user1};
					typeFrequencies = toMap(JournalArticle.class, 1);
					userFrequencies = toMap(_user1, 1);
				}
			});
	}

	@Test
	public void testSelectOneUser() throws Exception {
		String keyword = RandomTestUtil.randomString();

		index(keyword);

		assertSearch(
			new Expectations() {
				{
					groupFrequencies = SearchMapUtil.join(
						toMap(_group1, 3), toMap(_group2, 1));

					selectUsers = new User[] {_user1};
					typeFrequencies = SearchMapUtil.join(
						toMap(BlogsEntry.class, 1), toMap(DLFileEntry.class, 1),
						toMap(JournalArticle.class, 2));
					userFrequencies = SearchMapUtil.join(
						toMap(_user1, 4), toMap(_user2, 3), toMap(_user3, 2));
				}
			});
	}

	@Test
	public void testSelectOneUserOneType() throws Exception {
		String keyword = RandomTestUtil.randomString();

		index(keyword);

		assertSearch(
			new Expectations() {
				{
					groupFrequencies = SearchMapUtil.join(
						toMap(_group1, 1), toMap(_group2, 1));

					selectTypes = new Class<?>[] {JournalArticle.class};
					selectUsers = new User[] {_user1};
					typeFrequencies = SearchMapUtil.join(
						toMap(BlogsEntry.class, 1), toMap(DLFileEntry.class, 1),
						toMap(JournalArticle.class, 2));
					userFrequencies = SearchMapUtil.join(
						toMap(_user1, 2), toMap(_user3, 1));
				}
			});
	}

	@Test
	public void testSelectOneUserTwoTypes() throws Exception {
		String keyword = RandomTestUtil.randomString();

		index(keyword);

		assertSearch(
			new Expectations() {
				{
					groupFrequencies = SearchMapUtil.join(
						toMap(_group1, 2), toMap(_group2, 1));
					selectTypes = new Class<?>[] {
						DLFileEntry.class, JournalArticle.class
					};

					selectUsers = new User[] {_user1};
					typeFrequencies = SearchMapUtil.join(
						toMap(BlogsEntry.class, 1), toMap(DLFileEntry.class, 1),
						toMap(JournalArticle.class, 2));
					userFrequencies = SearchMapUtil.join(
						toMap(_user1, 3), toMap(_user2, 1), toMap(_user3, 2));
				}
			});
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected static String[] getClassNames(Class<?>... classes) {
		return TransformUtil.transform(classes, Class::getName, String.class);
	}

	protected static String[] getGroupIdStrings(Group... groups) {
		return TransformUtil.transform(
			groups, group -> String.valueOf(group.getGroupId()), String.class);
	}

	protected static String[] getUserIds(User... users) {
		return TransformUtil.transform(
			users, user -> String.valueOf(user.getUserId()), String.class);
	}

	protected static <K, V> Map<K, V> toMap(K key, V value) {
		return Collections.singletonMap(key, value);
	}

	protected void addBlogsEntry(Group group, User user, String title) {
		_blogsEntrySearchFixture.addBlogsEntry(
			BlogsEntryBlueprintBuilder.builder(
			).content(
				RandomTestUtil.randomString()
			).groupId(
				group.getGroupId()
			).title(
				title
			).userId(
				user.getUserId()
			).build());
	}

	protected void addFileEntry(Group group, User user, String keyword) {
		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setGroupId(group.getGroupId());
					setTitle(
						keyword + StringPool.SPACE +
							RandomTestUtil.randomString());
					setUserId(user.getUserId());
				}
			});
	}

	protected void addJournalArticle(Group group, User user, String keyword) {
		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(
									LocaleUtil.US,
									RandomTestUtil.randomString());

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, keyword);
							}
						});
					setUserId(user.getUserId());
				}
			});
	}

	protected User addUser() throws Exception {
		User user = UserTestUtil.addUser();

		_users.add(user);

		return user;
	}

	protected void assertSearch(Expectations expectations) throws Exception {
		SearchContext searchContext = getSearchContext(_keyword);

		searchContext.addFacet(
			createSiteFacet(expectations.selectGroups, searchContext));
		searchContext.addFacet(
			createTypeFacet(expectations.selectTypes, searchContext));
		searchContext.addFacet(
			createUserFacet(expectations.selectUsers, searchContext));

		FacetedSearcher facetedSearcher =
			facetedSearcherManager.createFacetedSearcher();

		Hits hits = facetedSearcher.search(searchContext);

		Map<String, Integer> groupFrequenciesMap = new HashMap<>();

		for (Map.Entry<Group, Integer> entry :
				expectations.groupFrequencies.entrySet()) {

			Group group = entry.getKey();

			groupFrequenciesMap.put(
				String.valueOf(group.getGroupId()), entry.getValue());
		}

		FacetsAssert.assertFrequencies(
			Field.GROUP_ID, searchContext, hits, groupFrequenciesMap);

		Map<String, Integer> typeFrequenciesMap = new HashMap<>();

		for (Map.Entry<Class<?>, Integer> entry :
				expectations.typeFrequencies.entrySet()) {

			Class<?> clazz = entry.getKey();

			typeFrequenciesMap.put(clazz.getName(), entry.getValue());
		}

		FacetsAssert.assertFrequencies(
			Field.ENTRY_CLASS_NAME, searchContext, hits, typeFrequenciesMap);

		Map<String, Integer> userFrequenciesMap = new HashMap<>();

		for (Map.Entry<User, Integer> entry :
				expectations.userFrequencies.entrySet()) {

			User user = entry.getKey();

			userFrequenciesMap.put(
				String.valueOf(user.getUserId()), entry.getValue());
		}

		FacetsAssert.assertFrequencies(
			Field.USER_ID, searchContext, hits, userFrequenciesMap);
	}

	protected Facet createSiteFacet(
		Group[] groups, SearchContext searchContext) {

		Facet facet = siteFacetFactory.newInstance(searchContext);

		facet.select(getGroupIdStrings(groups));

		return facet;
	}

	protected Facet createTypeFacet(
		Class[] classes, SearchContext searchContext) {

		Facet facet = assetEntriesFacetFactory.newInstance(searchContext);

		facet.select(getClassNames(classes));

		return facet;
	}

	protected Facet createUserFacet(User[] users, SearchContext searchContext) {
		Facet facet = userFacetFactory.newInstance(searchContext);

		facet.select(getUserIds(users));

		return facet;
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(TestPropsValues.getCompanyId());
		searchContext.setGroupIds(
			TransformUtil.transformToLongArray(_groups, Group::getGroupId));
		searchContext.setKeywords(keywords);
		searchContext.setUserId(TestPropsValues.getUserId());

		return searchContext;
	}

	protected void index(String keyword) throws Exception {
		_keyword = keyword;

		addBlogsEntry(_group1, _user1, keyword);
		addBlogsEntry(_group1, _user2, keyword);
		addBlogsEntry(_group2, _user2, keyword);

		addFileEntry(_group1, _user1, keyword);
		addFileEntry(_group2, _user2, keyword);
		addFileEntry(_group2, _user3, keyword);

		addJournalArticle(_group1, _user1, keyword);
		addJournalArticle(_group2, _user1, keyword);
		addJournalArticle(_group1, _user3, keyword);
	}

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected static Portal portal;

	@Inject
	protected AssetEntriesFacetFactory assetEntriesFacetFactory;

	@Inject
	protected BlogsEntryLocalService blogsEntryLocalService;

	@Inject
	protected DLAppLocalService dlAppLocalService;

	@Inject
	protected FacetedSearcherManager facetedSearcherManager;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected SiteFacetFactory siteFacetFactory;

	@Inject
	protected UserFacetFactory userFacetFactory;

	protected static class Expectations {

		protected Map<Group, Integer> groupFrequencies;
		protected Group[] selectGroups = {};
		protected Class<?>[] selectTypes = {};
		protected User[] selectUsers = {};
		protected Map<Class<?>, Integer> typeFrequencies;
		protected Map<User, Integer> userFrequencies;

	}

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	private BlogsEntrySearchFixture _blogsEntrySearchFixture;
	private FileEntrySearchFixture _fileEntrySearchFixture;
	private Group _group1;
	private Group _group2;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private String _keyword;
	private User _user1;
	private User _user2;
	private User _user3;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}
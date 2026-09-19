/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprintBuilder;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.ExpandoTableSearchFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class JournalArticleExpandoSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			ddmStructureLocalService, journalArticleLocalService, portal);

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		ExpandoTableSearchFixture expandoTableSearchFixture =
			new ExpandoTableSearchFixture(
				classNameLocalService, expandoColumnLocalService,
				expandoTableLocalService);

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		_expandoColumns = expandoTableSearchFixture.getExpandoColumns();
		_expandoTables = expandoTableSearchFixture.getExpandoTables();
		_expandoTableSearchFixture = expandoTableSearchFixture;

		_group = group;
		_groups = groupSearchFixture.getGroups();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testQueryString() throws Exception {
		addJournalArticle("Software Engineer");

		assertSearch(
			searchRequestBuilder -> searchRequestBuilder.queryString(
				"Engineer"),
			"[Software Engineer]");
	}

	@Test
	public void testSearchContextAttributeParamsExpandoAttributes()
		throws Exception {

		addJournalArticle("Software Engineer");

		assertSearch(
			searchRequestBuilder -> searchRequestBuilder.queryString(
				"Nonexistent"
			).withSearchContext(
				searchContext -> putParamsExpandoAttributes(
					"Engineer", searchContext)
			),
			"[Software Engineer]");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void addJournalArticle(String expandoValue) throws Exception {
		_expandoTableSearchFixture.addExpandoColumn(
			JournalArticle.class, ExpandoColumnConstants.INDEX_TYPE_KEYWORD,
			_EXPANDO_COLUMN);

		_journalArticleSearchFixture.addArticle(
			JournalArticleBlueprintBuilder.builder(
			).expandoBridgeAttributes(
				Collections.singletonMap(_EXPANDO_COLUMN, expandoValue)
			).groupId(
				_group.getGroupId()
			).journalArticleContent(
				new JournalArticleContent() {
					{
						put(LocaleUtil.US, "alpha");

						setDefaultLocale(LocaleUtil.US);
						setName("content");
					}
				}
			).journalArticleTitle(
				new JournalArticleTitle() {
					{
						put(LocaleUtil.US, "gamma");
					}
				}
			).build());
	}

	protected void assertSearch(
		Consumer<SearchRequestBuilder> consumer, String expected) {

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClasses(
				JournalArticle.class
			).withSearchRequestBuilder(
				consumer
			).build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			"expando__keyword__custom_fields__" + _EXPANDO_COLUMN, expected);
	}

	protected void putParamsExpandoAttributes(
		String string, SearchContext searchContext) {

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params == null) {
			params = new LinkedHashMap<>();

			searchContext.setAttribute("params", params);
		}

		params.put("expandoAttributes", string);
	}

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected static Portal portal;

	@Inject
	protected ClassNameLocalService classNameLocalService;

	@Inject
	protected ExpandoColumnLocalService expandoColumnLocalService;

	@Inject
	protected ExpandoTableLocalService expandoTableLocalService;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	private static final String _EXPANDO_COLUMN = "expandoColumn";

	@DeleteAfterTestRun
	private List<ExpandoColumn> _expandoColumns;

	private ExpandoTableSearchFixture _expandoTableSearchFixture;

	@DeleteAfterTestRun
	private List<ExpandoTable> _expandoTables;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

}
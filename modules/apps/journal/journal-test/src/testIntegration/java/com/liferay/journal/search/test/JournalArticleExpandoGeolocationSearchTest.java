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
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.geolocation.GeoBuilders;
import com.liferay.portal.search.query.QueriesUtil;
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

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class JournalArticleExpandoGeolocationSearchTest {

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
	public void testGeoDistanceFilter() throws Exception {
		Assume.assumeFalse(_isSearchEngine("Solr"));

		addJournalArticleWithTwoExpandoColumns(
			"Software Engineer", _EXPANDO_COLUMN, ExpandoColumnConstants.STRING,
			_GEOLOCATION_VALUE, _EXPANDO_COLUMN_GEOLOCATION,
			ExpandoColumnConstants.GEOLOCATION);

		assertSearch(
			searchRequestBuilder -> searchRequestBuilder.addComplexQueryPart(
				complexQueryPartBuilderFactory.builder(
				).occur(
					"filter"
				).query(
					QueriesUtil.geoDistance(
						_EXPANDO_COLUMN_GEOLOCATION_FULL_NAME,
						GeoBuilders.INSTANCE.geoLocationPoint(34.01, -117.42),
						GeoBuilders.INSTANCE.geoDistance(1000))
				).build()
			).emptySearchEnabled(
				true
			),
			"[Software Engineer]");
		assertSearch(
			searchRequestBuilder -> searchRequestBuilder.addComplexQueryPart(
				complexQueryPartBuilderFactory.builder(
				).occur(
					"filter"
				).query(
					QueriesUtil.geoDistance(
						_EXPANDO_COLUMN_GEOLOCATION_FULL_NAME,
						GeoBuilders.INSTANCE.geoLocationPoint(34.01, -117.42),
						GeoBuilders.INSTANCE.geoDistance(100))
				).build()
			).emptySearchEnabled(
				true
			),
			"[]");
	}

	@Test
	public void testGeolocationComboQueryString() throws Exception {
		addJournalArticleWithTwoExpandoColumns(
			"Software Engineer", _EXPANDO_COLUMN, ExpandoColumnConstants.STRING,
			_GEOLOCATION_VALUE, _EXPANDO_COLUMN_GEOLOCATION,
			ExpandoColumnConstants.GEOLOCATION);

		assertSearch(
			searchRequestBuilder -> searchRequestBuilder.queryString(
				"Engineer"),
			"[Software Engineer]");

		assertGeolocationExpandoFieldIndexed();

		assertNoGeolocationExpandoClauseInSearchQuery(
			searchRequestBuilder -> searchRequestBuilder.queryString("alpha"));
	}

	@Test
	public void testGeolocationQueryString() throws Exception {
		addJournalArticleWithSingleExpandoColumn(
			_GEOLOCATION_VALUE, _EXPANDO_COLUMN_GEOLOCATION,
			ExpandoColumnConstants.GEOLOCATION);

		assertNoGeolocationExpandoClauseInSearchQuery(
			searchRequestBuilder -> searchRequestBuilder.queryString("alpha"));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void addJournalArticleWithExpandoMap(
		Map<String, Serializable> expandoMap) {

		_journalArticleSearchFixture.addArticle(
			JournalArticleBlueprintBuilder.builder(
			).expandoBridgeAttributes(
				expandoMap
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

	protected void addJournalArticleWithSingleExpandoColumn(
			String expandoValue, String expandoColumn, int expandoColumnType)
		throws Exception {

		addJournalExpandoColumn(expandoColumn, expandoColumnType);

		addJournalArticleWithExpandoMap(
			Collections.singletonMap(expandoColumn, expandoValue));
	}

	protected void addJournalArticleWithTwoExpandoColumns(
			String expandoValue1, String expandoColumn1, int expandoColumnType1,
			String expandoValue2, String expandoColumn2, int expandoColumnType2)
		throws Exception {

		addJournalExpandoColumn(expandoColumn1, expandoColumnType1);
		addJournalExpandoColumn(expandoColumn2, expandoColumnType2);

		addJournalArticleWithExpandoMap(
			HashMapBuilder.<String, Serializable>put(
				expandoColumn1, expandoValue1
			).put(
				expandoColumn2, expandoValue2
			).build());
	}

	protected void addJournalExpandoColumn(
			String expandoColumn, int expandoColumnType)
		throws Exception {

		_expandoTableSearchFixture.addExpandoColumn(
			JournalArticle.class, ExpandoColumnConstants.INDEX_TYPE_KEYWORD,
			expandoColumnType, expandoColumn);
	}

	protected void assertGeolocationExpandoFieldIndexed() {
		String expected = _GEOLOCATION_EXPECTED;

		if (_isSearchEngine("Solr")) {
			expected = _GEOLOCATION_EXPECTED_SOLR;
		}

		assertSearch(
			searchRequestBuilder -> searchRequestBuilder.queryString("alpha"),
			_EXPANDO_COLUMN_GEOLOCATION_FULL_NAME, expected);
	}

	protected void assertNoGeolocationExpandoClauseInSearchQuery(
		Consumer<SearchRequestBuilder> consumer) {

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

		String requestString = searchResponse.getRequestString();

		Assert.assertFalse(
			requestString.contains(_EXPANDO_COLUMN_GEOLOCATION_FULL_NAME));
		Assert.assertFalse(
			requestString.contains(
				"expando__keyword__custom_fields__" +
					_EXPANDO_COLUMN_GEOLOCATION));
		Assert.assertFalse(
			requestString.contains(
				"expando__custom_fields__" + _EXPANDO_COLUMN_GEOLOCATION));
	}

	protected void assertSearch(
		Consumer<SearchRequestBuilder> consumer, String expected) {

		assertSearch(
			consumer, "expando__keyword__custom_fields__" + _EXPANDO_COLUMN,
			expected);
	}

	protected void assertSearch(
		Consumer<SearchRequestBuilder> consumer, String fieldName,
		String expected) {

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
			fieldName, expected);
	}

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected static Portal portal;

	@Inject
	protected static SearchEngineHelper searchEngineHelper;

	@Inject
	protected ClassNameLocalService classNameLocalService;

	@Inject
	protected ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory;

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

	private boolean _isSearchEngine(String engine) {
		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		String vendor = searchEngine.getVendor();

		return vendor.equals(engine);
	}

	private static final String _EXPANDO_COLUMN = "expandoColumn";

	private static final String _EXPANDO_COLUMN_GEOLOCATION = "location";

	private static final String _EXPANDO_COLUMN_GEOLOCATION_FULL_NAME =
		"expando__keyword__custom_fields__location_geolocation";

	private static final String _GEOLOCATION_EXPECTED =
		"[(34.013727866113186,-117.42460448294878)]";

	private static final String _GEOLOCATION_EXPECTED_SOLR =
		"[34.013727866113186,-117.42460448294878]";

	private static final String _GEOLOCATION_VALUE =
		"{\"latitude\":34.013727866113186, \"longitude\":-117.42460448294878}";

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
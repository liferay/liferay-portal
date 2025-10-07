/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.elasticsearch7.internal.SearchHitDocumentTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.ElasticsearchAggregationTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.pipeline.ElasticsearchPipelineAggregationTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.facet.DefaultFacetTranslator;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetTranslator;
import com.liferay.portal.search.elasticsearch7.internal.facet.NestedFacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.facet.RangeFacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.filter.ElasticsearchFilterTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.search.response.SearchResponseTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.ElasticsearchSortFieldTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.ElasticsearchSortFieldTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.stats.DefaultStatsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.suggest.ElasticsearchSuggesterTranslator;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.filter.ComplexQueryBuilderFactory;
import com.liferay.portal.search.internal.aggregation.AggregationResultsImpl;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.facet.ModifiedFacetImpl;
import com.liferay.portal.search.internal.facet.NestedFacetImpl;
import com.liferay.portal.search.internal.filter.ComplexQueryBuilderFactoryImpl;
import com.liferay.portal.search.internal.geolocation.GeoBuildersImpl;
import com.liferay.portal.search.internal.groupby.GroupByResponseFactoryImpl;
import com.liferay.portal.search.internal.highlight.HighlightFieldBuilderFactoryImpl;
import com.liferay.portal.search.internal.hits.SearchHitBuilderFactoryImpl;
import com.liferay.portal.search.internal.hits.SearchHitsBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.groupby.GroupByRequestFactoryImpl;
import com.liferay.portal.search.internal.legacy.stats.StatsRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.stats.StatsResultsTranslatorImpl;
import com.liferay.portal.search.internal.query.QueriesImpl;
import com.liferay.portal.search.internal.stats.StatsResponseBuilderFactoryImpl;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Michael C. Han
 */
public class SearchRequestExecutorFixture {

	public SearchRequestExecutor getSearchRequestExecutor() {
		return _searchRequestExecutor;
	}

	public void setUp() {
		ElasticsearchQueryTranslator elasticsearchQueryTranslator =
			new ElasticsearchQueryTranslator();

		ElasticsearchSortFieldTranslatorFixture
			elasticsearchSortFieldTranslatorFixture =
				new ElasticsearchSortFieldTranslatorFixture(
					elasticsearchQueryTranslator);

		StatsTranslator statsTranslator = new DefaultStatsTranslator();

		ReflectionTestUtil.setFieldValue(
			statsTranslator, "_statsResponseBuilderFactory",
			new StatsResponseBuilderFactoryImpl());

		_searchRequestExecutor = _createSearchRequestExecutor(
			createComplexQueryBuilderFactory(new QueriesImpl()),
			_elasticsearchClientResolver, elasticsearchQueryTranslator,
			elasticsearchSortFieldTranslatorFixture.
				getElasticsearchSortFieldTranslator(),
			_facetProcessor, new StatsRequestBuilderFactoryImpl(),
			statsTranslator);
	}

	public void tearDown() {
		_serviceRegistrations.forEach(
			serviceRegistration -> serviceRegistration.unregister());

		ReflectionTestUtil.invoke(
			_defaultFacetTranslator, "deactivate", new Class<?>[0]);
	}

	protected static CommonSearchSourceBuilderAssembler
		createCommonSearchSourceBuilderAssembler(
			ElasticsearchQueryTranslator elasticsearchQueryTranslator,
			FacetProcessor<?> facetProcessor, StatsTranslator statsTranslator,
			ComplexQueryBuilderFactory complexQueryBuilderFactory) {

		CommonSearchSourceBuilderAssembler commonSearchSourceBuilderAssembler =
			new CommonSearchSourceBuilderAssemblerImpl();

		ElasticsearchAggregationTranslatorFixture
			elasticsearchAggregationTranslatorFixture =
				new ElasticsearchAggregationTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_aggregationTranslator",
			elasticsearchAggregationTranslatorFixture.
				getElasticsearchAggregationTranslator());

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_complexQueryBuilderFactory",
			complexQueryBuilderFactory);

		ElasticsearchQueryTranslatorFixture
			legacyElasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		com.liferay.portal.search.elasticsearch7.internal.legacy.query.
			ElasticsearchQueryTranslator legacyElasticsearchQueryTranslator =
				legacyElasticsearchQueryTranslatorFixture.
					getElasticsearchQueryTranslator();

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_facetTranslator",
			_createFacetTranslator(
				facetProcessor, legacyElasticsearchQueryTranslator));

		ElasticsearchFilterTranslatorFixture
			elasticsearchFilterTranslatorFixture =
				new ElasticsearchFilterTranslatorFixture(
					legacyElasticsearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_filterTranslator",
			elasticsearchFilterTranslatorFixture.
				getElasticsearchFilterTranslator());

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_legacyQueryTranslator",
			legacyElasticsearchQueryTranslator);

		ElasticsearchPipelineAggregationTranslatorFixture
			elasticsearchPipelineAggregationTranslatorFixture =
				new ElasticsearchPipelineAggregationTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler,
			"_pipelineAggregationTranslator",
			elasticsearchPipelineAggregationTranslatorFixture.
				getElasticsearchPipelineAggregationTranslator());

		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_queryTranslator",
			elasticsearchQueryTranslator);
		ReflectionTestUtil.setFieldValue(
			commonSearchSourceBuilderAssembler, "_statsTranslator",
			statsTranslator);

		return commonSearchSourceBuilderAssembler;
	}

	protected static ComplexQueryBuilderFactory
		createComplexQueryBuilderFactory(Queries queries) {

		ComplexQueryBuilderFactoryImpl complexQueryBuilderFactoryImpl =
			new ComplexQueryBuilderFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			complexQueryBuilderFactoryImpl, "_queries", queries);

		return complexQueryBuilderFactoryImpl;
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	protected void setFacetProcessor(FacetProcessor<?> facetProcessor) {
		_facetProcessor = facetProcessor;
	}

	private static FacetTranslator _createFacetTranslator(
		FacetProcessor<?> facetProcessor,
		QueryTranslator<QueryBuilder> queryTranslator) {

		_defaultFacetTranslator = new DefaultFacetTranslator();

		ReflectionTestUtil.invoke(
			_defaultFacetTranslator, "activate",
			new Class<?>[] {BundleContext.class}, _bundleContext);

		if (facetProcessor != null) {
			ReflectionTestUtil.setFieldValue(
				_defaultFacetTranslator, "_defaultFacetProcessor",
				(FacetProcessor<SearchRequestBuilder>)facetProcessor);
		}
		else {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					(Class<FacetProcessor<SearchRequestBuilder>>)
						(Class<?>)FacetProcessor.class,
					new RangeFacetProcessor(),
					MapUtil.singletonDictionary(
						"class.name", ModifiedFacetImpl.class.getName())));

			_serviceRegistrations.add(
				_bundleContext.registerService(
					(Class<FacetProcessor<SearchRequestBuilder>>)
						(Class<?>)FacetProcessor.class,
					new NestedFacetProcessor(),
					MapUtil.singletonDictionary(
						"class.name", NestedFacetImpl.class.getName())));
		}

		ElasticsearchFilterTranslatorFixture
			elasticsearchFilterTranslatorFixture =
				new ElasticsearchFilterTranslatorFixture(queryTranslator);

		ReflectionTestUtil.setFieldValue(
			_defaultFacetTranslator, "_filterTranslator",
			elasticsearchFilterTranslatorFixture.
				getElasticsearchFilterTranslator());

		return _defaultFacetTranslator;
	}

	private ClosePointInTimeRequestExecutor
		_createClosePointInTimeRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		ClosePointInTimeRequestExecutor closePointInTimeRequestExecutor =
			new ClosePointInTimeRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			closePointInTimeRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return closePointInTimeRequestExecutor;
	}

	private CountSearchRequestExecutor _createCountSearchRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		CommonSearchSourceBuilderAssembler commonSearchSourceBuilderAssembler,
		StatsTranslator statsTranslator) {

		CountSearchRequestExecutor countSearchRequestExecutor =
			new CountSearchRequestExecutorImpl();

		CommonSearchResponseAssembler commonSearchResponseAssembler =
			new CommonSearchResponseAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			commonSearchResponseAssembler, "_statsTranslator", statsTranslator);

		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutor, "_commonSearchResponseAssembler",
			commonSearchResponseAssembler);

		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutor, "_commonSearchSourceBuilderAssembler",
			commonSearchSourceBuilderAssembler);
		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return countSearchRequestExecutor;
	}

	private MultisearchSearchRequestExecutor
		_createMultisearchSearchRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver,
			SearchSearchRequestAssembler searchSearchRequestAssembler,
			SearchSearchResponseAssembler searchSearchResponseAssembler) {

		MultisearchSearchRequestExecutor multisearchSearchRequestExecutor =
			new MultisearchSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			multisearchSearchRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			multisearchSearchRequestExecutor, "_searchSearchRequestAssembler",
			searchSearchRequestAssembler);
		ReflectionTestUtil.setFieldValue(
			multisearchSearchRequestExecutor, "_searchSearchResponseAssembler",
			searchSearchResponseAssembler);

		return multisearchSearchRequestExecutor;
	}

	private OpenPointInTimeRequestExecutor
		_createOpenPointInTimeRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		OpenPointInTimeRequestExecutor openPointInTimeRequestExecutor =
			new OpenPointInTimeRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			openPointInTimeRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return openPointInTimeRequestExecutor;
	}

	private SearchRequestExecutor _createSearchRequestExecutor(
		ComplexQueryBuilderFactory complexQueryBuilderFactory,
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchQueryTranslator elasticsearchQueryTranslator,
		ElasticsearchSortFieldTranslator elasticsearchSortFieldTranslator,
		FacetProcessor<?> facetProcessor,
		StatsRequestBuilderFactory statsRequestBuilderFactory,
		StatsTranslator statsTranslator) {

		SearchRequestExecutor searchRequestExecutor =
			new ElasticsearchSearchRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_closePointInTimeRequestExecutor",
			_createClosePointInTimeRequestExecutor(
				elasticsearchClientResolver));

		CommonSearchSourceBuilderAssembler commonSearchSourceBuilderAssembler =
			createCommonSearchSourceBuilderAssembler(
				elasticsearchQueryTranslator, facetProcessor, statsTranslator,
				complexQueryBuilderFactory);

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_countSearchRequestExecutor",
			_createCountSearchRequestExecutor(
				elasticsearchClientResolver, commonSearchSourceBuilderAssembler,
				statsTranslator));

		SearchSearchRequestAssembler searchSearchRequestAssembler =
			_createSearchSearchRequestAssembler(
				elasticsearchQueryTranslator, elasticsearchSortFieldTranslator,
				commonSearchSourceBuilderAssembler, statsRequestBuilderFactory,
				statsTranslator);

		SearchSearchResponseAssembler searchSearchResponseAssembler =
			_createSearchSearchResponseAssembler(
				statsRequestBuilderFactory, statsTranslator);

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_multisearchSearchRequestExecutor",
			_createMultisearchSearchRequestExecutor(
				elasticsearchClientResolver, searchSearchRequestAssembler,
				searchSearchResponseAssembler));

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_openPointInTimeRequestExecutor",
			_createOpenPointInTimeRequestExecutor(elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_searchSearchRequestExecutor",
			_createSearchSearchRequestExecutor(
				elasticsearchClientResolver, searchSearchRequestAssembler,
				searchSearchResponseAssembler));
		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_suggestSearchRequestExecutor",
			_createSuggestSearchRequestExecutor(elasticsearchClientResolver));

		return searchRequestExecutor;
	}

	private SearchSearchRequestAssembler _createSearchSearchRequestAssembler(
		ElasticsearchQueryTranslator elasticsearchQueryTranslator,
		ElasticsearchSortFieldTranslator elasticsearchSortFieldTranslator,
		CommonSearchSourceBuilderAssembler commonSearchSourceBuilderAssembler,
		StatsRequestBuilderFactory statsRequestBuilderFactory,
		StatsTranslator statsTranslator) {

		SearchSearchRequestAssembler searchSearchRequestAssembler =
			new SearchSearchRequestAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_commonSearchSourceBuilderAssembler",
			commonSearchSourceBuilderAssembler);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_groupByRequestFactory",
			new GroupByRequestFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_queryTranslator",
			elasticsearchQueryTranslator);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_sortFieldTranslator",
			elasticsearchSortFieldTranslator);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_statsRequestBuilderFactory",
			statsRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_statsTranslator", statsTranslator);

		return searchSearchRequestAssembler;
	}

	private SearchSearchRequestExecutor _createSearchSearchRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		SearchSearchRequestAssembler searchSearchRequestAssembler,
		SearchSearchResponseAssembler searchSearchResponseAssembler) {

		SearchSearchRequestExecutor searchSearchRequestExecutor =
			new SearchSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			searchSearchRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestExecutor, "_searchSearchRequestAssembler",
			searchSearchRequestAssembler);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestExecutor, "_searchSearchResponseAssembler",
			searchSearchResponseAssembler);

		return searchSearchRequestExecutor;
	}

	private SearchSearchResponseAssembler _createSearchSearchResponseAssembler(
		StatsRequestBuilderFactory statsRequestBuilderFactory,
		StatsTranslator statsTranslator) {

		SearchSearchResponseAssembler searchSearchResponseAssembler =
			new SearchSearchResponseAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_aggregationResults",
			new AggregationResultsImpl());

		CommonSearchResponseAssembler commonSearchResponseAssembler =
			new CommonSearchResponseAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			commonSearchResponseAssembler, "_statsTranslator", statsTranslator);

		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_commonSearchResponseAssembler",
			commonSearchResponseAssembler);

		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_documentBuilderFactory",
			new DocumentBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_geoBuilders",
			new GeoBuildersImpl());
		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_highlightFieldBuilderFactory",
			new HighlightFieldBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_searchHitBuilderFactory",
			new SearchHitBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_searchHitsBuilderFactory",
			new SearchHitsBuilderFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_searchResponseTranslator",
			new SearchResponseTranslator(
				new GroupByResponseFactoryImpl(),
				new SearchHitDocumentTranslatorImpl(),
				statsRequestBuilderFactory, new StatsResultsTranslatorImpl(),
				statsTranslator));

		return searchSearchResponseAssembler;
	}

	private SuggestSearchRequestExecutor _createSuggestSearchRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		SuggestSearchRequestExecutor suggestSearchRequestExecutor =
			new SuggestSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			suggestSearchRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			suggestSearchRequestExecutor, "_suggesterTranslator",
			_elasticsearchSuggesterTranslator);

		return suggestSearchRequestExecutor;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static DefaultFacetTranslator _defaultFacetTranslator;
	private static final List
		<ServiceRegistration<FacetProcessor<SearchRequestBuilder>>>
			_serviceRegistrations = new ArrayList<>();

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private final ElasticsearchSuggesterTranslator
		_elasticsearchSuggesterTranslator =
			new ElasticsearchSuggesterTranslator(null);
	private FacetProcessor<?> _facetProcessor;
	private SearchRequestExecutor _searchRequestExecutor;

}
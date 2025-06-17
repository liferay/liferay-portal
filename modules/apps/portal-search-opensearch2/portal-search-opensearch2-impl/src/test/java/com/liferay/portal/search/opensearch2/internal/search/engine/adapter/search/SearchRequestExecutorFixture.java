/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
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
import com.liferay.portal.search.opensearch2.internal.aggregation.OpenSearchAggregationTranslatorFixture;
import com.liferay.portal.search.opensearch2.internal.aggregation.OpenSearchPipelineAggregationTranslatorFixture;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.facet.FacetProcessor;
import com.liferay.portal.search.opensearch2.internal.facet.FacetTranslator;
import com.liferay.portal.search.opensearch2.internal.facet.FacetTranslatorImpl;
import com.liferay.portal.search.opensearch2.internal.facet.NestedFacetProcessor;
import com.liferay.portal.search.opensearch2.internal.facet.RangeFacetProcessor;
import com.liferay.portal.search.opensearch2.internal.filter.OpenSearchFilterTranslatorFixture;
import com.liferay.portal.search.opensearch2.internal.groupby.GroupByTranslatorImpl;
import com.liferay.portal.search.opensearch2.internal.highlight.HighlightTranslator;
import com.liferay.portal.search.opensearch2.internal.legacy.hits.HitDocumentTranslatorImpl;
import com.liferay.portal.search.opensearch2.internal.query.OpenSearchQueryTranslator;
import com.liferay.portal.search.opensearch2.internal.query.OpenSearchQueryTranslatorFixture;
import com.liferay.portal.search.opensearch2.internal.search.response.SearchResponseTranslator;
import com.liferay.portal.search.opensearch2.internal.search.response.SearchResponseTranslatorImpl;
import com.liferay.portal.search.opensearch2.internal.sort.OpenSearchSortFieldTranslator;
import com.liferay.portal.search.opensearch2.internal.sort.OpenSearchSortFieldTranslatorFixture;
import com.liferay.portal.search.opensearch2.internal.stats.StatsTranslator;
import com.liferay.portal.search.opensearch2.internal.stats.StatsTranslatorImpl;
import com.liferay.portal.search.opensearch2.internal.suggest.OpenSearchSuggesterTranslatorFixture;
import com.liferay.portal.search.query.Queries;

import java.util.ArrayList;
import java.util.List;

import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;
import org.opensearch.client.opensearch.core.SearchRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class SearchRequestExecutorFixture {

	public SearchRequestExecutor getSearchRequestExecutor() {
		return _searchRequestExecutor;
	}

	public void setUp() {
		OpenSearchQueryTranslatorFixture openSearchQueryTranslatorFixture =
			new OpenSearchQueryTranslatorFixture();

		OpenSearchQueryTranslator openSearchQueryTranslator =
			openSearchQueryTranslatorFixture.getOpenSearchQueryTranslator();

		OpenSearchSortFieldTranslatorFixture
			openSearchSortFieldTranslatorFixture =
				new OpenSearchSortFieldTranslatorFixture(
					openSearchQueryTranslator);

		StatsTranslator statsTranslator = new StatsTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			statsTranslator, "_statsResponseBuilderFactory",
			new StatsResponseBuilderFactoryImpl());

		_searchRequestExecutor = _createSearchRequestExecutor(
			createComplexQueryBuilderFactory(new QueriesImpl()),
			_facetProcessor, _openSearchConnectionManager,
			openSearchQueryTranslator,
			openSearchSortFieldTranslatorFixture.
				getOpenSearchSortFieldTranslator(),
			new StatsRequestBuilderFactoryImpl(), statsTranslator);
	}

	public void tearDown() {
		_serviceRegistrations.forEach(
			serviceRegistration -> serviceRegistration.unregister());

		ReflectionTestUtil.invoke(
			_facetTranslatorImpl, "deactivate", new Class<?>[0]);
	}

	protected static CommonSearchRequestBuilderAssembler
		createCommonSearchRequestBuilderAssembler(
			ComplexQueryBuilderFactory complexQueryBuilderFactory,
			FacetProcessor<?> facetProcessor,
			OpenSearchQueryTranslator openSearchQueryTranslator,
			StatsTranslator statsTranslator) {

		CommonSearchRequestBuilderAssembler
			commonSearchRequestBuilderAssembler =
				new CommonSearchRequestBuilderAssemblerImpl();

		OpenSearchAggregationTranslatorFixture
			openSearchAggregationTranslatorFixture =
				new OpenSearchAggregationTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler, "_aggregationTranslator",
			openSearchAggregationTranslatorFixture.
				getOpenSearchAggregationTranslator());

		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler, "_complexQueryBuilderFactory",
			complexQueryBuilderFactory);

		com.liferay.portal.search.opensearch2.internal.legacy.query.
			OpenSearchQueryTranslatorFixture
				legacyOpenSearchQueryTranslatorFixture =
					new com.liferay.portal.search.opensearch2.internal.legacy.
						query.OpenSearchQueryTranslatorFixture();

		com.liferay.portal.search.opensearch2.internal.legacy.query.
			OpenSearchQueryTranslator legacyOpenSearchQueryTranslator =
				legacyOpenSearchQueryTranslatorFixture.
					getOpenSearchQueryTranslator();

		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler, "_facetTranslator",
			_createFacetTranslator(
				facetProcessor, legacyOpenSearchQueryTranslator));

		OpenSearchFilterTranslatorFixture openSearchFilterTranslatorFixture =
			new OpenSearchFilterTranslatorFixture(
				legacyOpenSearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler, "_filterTranslator",
			openSearchFilterTranslatorFixture.getOpenSearchFilterTranslator());

		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler, "_legacyQueryTranslator",
			legacyOpenSearchQueryTranslator);

		OpenSearchPipelineAggregationTranslatorFixture
			openSearchPipelineAggregationTranslatorFixture =
				new OpenSearchPipelineAggregationTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler,
			"_pipelineAggregationTranslator",
			openSearchPipelineAggregationTranslatorFixture.
				getOpenSearchPipelineAggregationTranslator());

		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler, "_queryTranslator",
			openSearchQueryTranslator);
		ReflectionTestUtil.setFieldValue(
			commonSearchRequestBuilderAssembler, "_statsTranslator",
			statsTranslator);

		return commonSearchRequestBuilderAssembler;
	}

	protected static ComplexQueryBuilderFactory
		createComplexQueryBuilderFactory(Queries queries) {

		ComplexQueryBuilderFactoryImpl complexQueryBuilderFactoryImpl =
			new ComplexQueryBuilderFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			complexQueryBuilderFactoryImpl, "_queries", queries);

		return complexQueryBuilderFactoryImpl;
	}

	protected void setFacetProcessor(FacetProcessor<?> facetProcessor) {
		_facetProcessor = facetProcessor;
	}

	protected void setOpenSearchConnectionManager(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	private static FacetTranslator _createFacetTranslator(
		FacetProcessor<?> facetProcessor,
		QueryTranslator<QueryVariant> queryTranslator) {

		_facetTranslatorImpl = new FacetTranslatorImpl();

		ReflectionTestUtil.invoke(
			_facetTranslatorImpl, "activate",
			new Class<?>[] {BundleContext.class}, _bundleContext);

		if (facetProcessor != null) {
			ReflectionTestUtil.setFieldValue(
				_facetTranslatorImpl, "_defaultFacetProcessor",
				(FacetProcessor<SearchRequest.Builder>)facetProcessor);
		}
		else {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					(Class<FacetProcessor<SearchRequest.Builder>>)
						(Class<?>)FacetProcessor.class,
					new RangeFacetProcessor(),
					MapUtil.singletonDictionary(
						"class.name", ModifiedFacetImpl.class.getName())));

			_serviceRegistrations.add(
				_bundleContext.registerService(
					(Class<FacetProcessor<SearchRequest.Builder>>)
						(Class<?>)FacetProcessor.class,
					new NestedFacetProcessor(),
					MapUtil.singletonDictionary(
						"class.name", NestedFacetImpl.class.getName())));
		}

		OpenSearchFilterTranslatorFixture openSearchFilterTranslatorFixture =
			new OpenSearchFilterTranslatorFixture(queryTranslator);

		ReflectionTestUtil.setFieldValue(
			_facetTranslatorImpl, "_filterTranslator",
			openSearchFilterTranslatorFixture.getOpenSearchFilterTranslator());

		return _facetTranslatorImpl;
	}

	private ClosePointInTimeRequestExecutor
		_createClosePointInTimeRequestExecutor(
			OpenSearchConnectionManager openSearchConnectionManager) {

		ClosePointInTimeRequestExecutor closePointInTimeRequestExecutor =
			new ClosePointInTimeRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			closePointInTimeRequestExecutor, "_openSearchConnectionManager",
			openSearchConnectionManager);

		return closePointInTimeRequestExecutor;
	}

	private CountSearchRequestExecutor _createCountSearchRequestExecutor(
		CommonSearchRequestBuilderAssembler commonSearchRequestBuilderAssembler,
		OpenSearchConnectionManager openSearchConnectionManager,
		StatsTranslator statsTranslator) {

		CountSearchRequestExecutor countSearchRequestExecutor =
			new CountSearchRequestExecutorImpl();

		CommonSearchResponseAssembler commonSearchResponseAssembler =
			new CommonSearchResponseAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			commonSearchResponseAssembler, "_statsTranslator", statsTranslator);

		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutor, "_commonSearchRequestBuilderAssembler",
			commonSearchRequestBuilderAssembler);
		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutor, "_commonSearchResponseAssembler",
			commonSearchResponseAssembler);
		ReflectionTestUtil.setFieldValue(
			countSearchRequestExecutor, "_openSearchConnectionManager",
			openSearchConnectionManager);

		return countSearchRequestExecutor;
	}

	private MultisearchSearchRequestExecutor
		_createMultisearchSearchRequestExecutor(
			OpenSearchConnectionManager openSearchConnectionManager,
			SearchSearchRequestAssembler searchSearchRequestAssembler,
			SearchSearchResponseAssembler searchSearchResponseAssembler) {

		MultisearchSearchRequestExecutor multisearchSearchRequestExecutor =
			new MultisearchSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			multisearchSearchRequestExecutor, "_openSearchConnectionManager",
			openSearchConnectionManager);
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
			OpenSearchConnectionManager openSearchConnectionManager) {

		OpenPointInTimeRequestExecutor openPointInTimeRequestExecutor =
			new OpenPointInTimeRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			openPointInTimeRequestExecutor, "_openSearchConnectionManager",
			openSearchConnectionManager);

		return openPointInTimeRequestExecutor;
	}

	private SearchRequestExecutor _createSearchRequestExecutor(
		ComplexQueryBuilderFactory complexQueryBuilderFactory,
		FacetProcessor<?> facetProcessor,
		OpenSearchConnectionManager openSearchConnectionManager,
		OpenSearchQueryTranslator openSearchQueryTranslator,
		OpenSearchSortFieldTranslator openSearchSortFieldTranslator,
		StatsRequestBuilderFactory statsRequestBuilderFactory,
		StatsTranslator statsTranslator) {

		SearchRequestExecutor searchRequestExecutor =
			new OpenSearchSearchRequestExecutor();

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_closePointInTimeRequestExecutor",
			_createClosePointInTimeRequestExecutor(
				openSearchConnectionManager));

		CommonSearchRequestBuilderAssembler
			commonSearchRequestBuilderAssembler =
				createCommonSearchRequestBuilderAssembler(
					complexQueryBuilderFactory, facetProcessor,
					openSearchQueryTranslator, statsTranslator);

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_countSearchRequestExecutor",
			_createCountSearchRequestExecutor(
				commonSearchRequestBuilderAssembler,
				openSearchConnectionManager, statsTranslator));

		SearchSearchRequestAssembler searchSearchRequestAssembler =
			_createSearchSearchRequestAssembler(
				commonSearchRequestBuilderAssembler, openSearchQueryTranslator,
				openSearchSortFieldTranslator, statsRequestBuilderFactory,
				statsTranslator);

		SearchSearchResponseAssembler searchSearchResponseAssembler =
			_createSearchSearchResponseAssembler(
				statsRequestBuilderFactory, statsTranslator);

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_multisearchSearchRequestExecutor",
			_createMultisearchSearchRequestExecutor(
				openSearchConnectionManager, searchSearchRequestAssembler,
				searchSearchResponseAssembler));

		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_openPointInTimeRequestExecutor",
			_createOpenPointInTimeRequestExecutor(openSearchConnectionManager));
		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_searchSearchRequestExecutor",
			_createSearchSearchRequestExecutor(
				openSearchConnectionManager, searchSearchRequestAssembler,
				searchSearchResponseAssembler));
		ReflectionTestUtil.setFieldValue(
			searchRequestExecutor, "_suggestSearchRequestExecutor",
			_createSuggestSearchRequestExecutor(openSearchConnectionManager));

		return searchRequestExecutor;
	}

	private SearchSearchRequestAssembler _createSearchSearchRequestAssembler(
		CommonSearchRequestBuilderAssembler commonSearchRequestBuilderAssembler,
		OpenSearchQueryTranslator openSearchQueryTranslator,
		OpenSearchSortFieldTranslator openSearchSortFieldTranslator,
		StatsRequestBuilderFactory statsRequestBuilderFactory,
		StatsTranslator statsTranslator) {

		SearchSearchRequestAssembler searchSearchRequestAssembler =
			new SearchSearchRequestAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler,
			"_commonSearchRequestBuilderAssembler",
			commonSearchRequestBuilderAssembler);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_groupByRequestFactory",
			new GroupByRequestFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_groupByTranslator",
			new GroupByTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_highlightTranslator",
			new HighlightTranslator());
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_queryTranslator",
			openSearchQueryTranslator);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_sortFieldTranslator",
			openSearchSortFieldTranslator);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_statsRequestBuilderFactory",
			statsRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			searchSearchRequestAssembler, "_statsTranslator", statsTranslator);

		return searchSearchRequestAssembler;
	}

	private SearchSearchRequestExecutor _createSearchSearchRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager,
		SearchSearchRequestAssembler searchSearchRequestAssembler,
		SearchSearchResponseAssembler searchSearchResponseAssembler) {

		SearchSearchRequestExecutor searchSearchRequestExecutor =
			new SearchSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			searchSearchRequestExecutor, "_openSearchConnectionManager",
			openSearchConnectionManager);
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

		CommonSearchResponseAssembler commonSearchResponseAssembler =
			new CommonSearchResponseAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			commonSearchResponseAssembler, "_statsTranslator", statsTranslator);

		SearchResponseTranslator searchResponseTranslator =
			new SearchResponseTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			searchResponseTranslator, "_groupByResponseFactory",
			new GroupByResponseFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			searchResponseTranslator, "_hitDocumentTranslator",
			new HitDocumentTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			searchResponseTranslator, "_statsRequestBuilderFactory",
			statsRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			searchResponseTranslator, "_statsResultsTranslator",
			new StatsResultsTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			searchResponseTranslator, "_statsTranslator", statsTranslator);

		ReflectionTestUtil.setFieldValue(
			searchSearchResponseAssembler, "_aggregationResults",
			new AggregationResultsImpl());
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
			searchResponseTranslator);

		return searchSearchResponseAssembler;
	}

	private SuggestSearchRequestExecutor _createSuggestSearchRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		SuggestSearchRequestExecutor suggestSearchRequestExecutor =
			new SuggestSearchRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			suggestSearchRequestExecutor, "_openSearchConnectionManager",
			openSearchConnectionManager);

		OpenSearchSuggesterTranslatorFixture
			openSearchSuggesterTranslatorFixture =
				new OpenSearchSuggesterTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			suggestSearchRequestExecutor, "_suggesterTranslator",
			openSearchSuggesterTranslatorFixture.
				getOpenSearchSuggesterTranslator());

		return suggestSearchRequestExecutor;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static FacetTranslatorImpl _facetTranslatorImpl;
	private static final List
		<ServiceRegistration<FacetProcessor<SearchRequest.Builder>>>
			_serviceRegistrations = new ArrayList<>();

	private FacetProcessor<?> _facetProcessor;
	private OpenSearchConnectionManager _openSearchConnectionManager;
	private SearchRequestExecutor _searchRequestExecutor;

}
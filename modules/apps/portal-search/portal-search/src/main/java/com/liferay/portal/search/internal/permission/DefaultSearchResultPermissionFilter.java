/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.permission;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchResourceActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.RelatedEntryIndexer;
import com.liferay.portal.kernel.search.RelatedEntryIndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchResultPermissionFilter;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.FacetPostProcessor;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.kernel.search.facet.collector.DefaultTermCollector;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.configuration.DefaultSearchResultPermissionFilterConfiguration;
import com.liferay.portal.search.facet.nested.NestedFacet;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.SearchHitsBuilder;
import com.liferay.portal.search.hits.SearchHitsBuilderFactory;
import com.liferay.portal.search.internal.facet.FacetImpl;
import com.liferay.portal.search.internal.facet.NestedFacetImpl;
import com.liferay.portal.search.internal.facet.SimpleFacetCollector;
import com.liferay.portal.search.internal.hits.SearchHitsBuilderFactoryImpl;
import com.liferay.portal.search.internal.searcher.SearchResponseImpl;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang.time.StopWatch;

/**
 * @author Tina Tian
 */
public class DefaultSearchResultPermissionFilter
	implements SearchResultPermissionFilter {

	public DefaultSearchResultPermissionFilter(
		FacetPostProcessor facetPostProcessor, IndexerRegistry indexerRegistry,
		PermissionChecker permissionChecker,
		RelatedEntryIndexerRegistry relatedEntryIndexerRegistry,
		Function<SearchContext, Hits> searchFunction,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		DefaultSearchResultPermissionFilterConfiguration
			defaultSearchResultPermissionFilterConfiguration) {

		_facetPostProcessor = facetPostProcessor;
		_indexerRegistry = indexerRegistry;
		_permissionChecker = permissionChecker;
		_relatedEntryIndexerRegistry = relatedEntryIndexerRegistry;
		_searchFunction = searchFunction;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;

		_accurateCountThreshold =
			defaultSearchResultPermissionFilterConfiguration.
				permissionFilteredSearchResultAccurateCountThreshold();
		_searchQueryResultWindowLimit =
			defaultSearchResultPermissionFilterConfiguration.
				searchQueryResultWindowLimit();
		_timeLimit =
			defaultSearchResultPermissionFilterConfiguration.
				permissionFilteringTimeLimit();
	}

	@Override
	public Hits search(SearchContext searchContext) {
		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (!queryConfig.isAllFieldsSelected()) {
			queryConfig.setSelectedFieldNames(
				_getSelectedFieldNames(queryConfig.getSelectedFieldNames()));
		}

		int end = searchContext.getEnd();
		int start = searchContext.getStart();

		if ((end == QueryUtil.ALL_POS) && (start == QueryUtil.ALL_POS)) {
			Hits hits = _getHits(searchContext);

			if (!_isGroupAdmin(searchContext)) {
				_filterHits(null, hits, searchContext);

				_updateSearchHits(hits, searchContext);
			}

			return hits;
		}

		if ((start < 0) || (start > end)) {
			return new HitsImpl();
		}

		if (_isGroupAdmin(searchContext)) {
			return _getHits(searchContext);
		}

		SlidingWindowSearcher slidingWindowSearcher =
			new SlidingWindowSearcher();

		return slidingWindowSearcher.search(start, end, searchContext);
	}

	private int _filterHits(
		SlidingWindowSearcher.FacetCountHelper facetCountHelper, Hits hits,
		SearchContext searchContext) {

		Map<String, Boolean> companyScopeViewPermissions = new HashMap<>();
		List<Document> docs = new ArrayList<>();
		List<Document> excludeDocs = new ArrayList<>();
		List<Float> scores = new ArrayList<>();

		boolean companyAdmin = _permissionChecker.isCompanyAdmin(
			_permissionChecker.getCompanyId());
		int status = GetterUtil.getInteger(
			searchContext.getAttribute(Field.STATUS),
			WorkflowConstants.STATUS_APPROVED);

		Document[] documents = hits.getDocs();

		for (int i = 0; i < documents.length; i++) {
			if (_isIncludeDocument(
					documents[i], _permissionChecker.getCompanyId(),
					companyAdmin, status, companyScopeViewPermissions)) {

				docs.add(documents[i]);
				scores.add(hits.score(i));
			}
			else {
				excludeDocs.add(documents[i]);
			}
		}

		if (!excludeDocs.isEmpty()) {
			Map<String, Facet> facets = searchContext.getFacets();

			if (facetCountHelper != null) {
				facets = facetCountHelper.getFacets();
			}

			for (Facet facet : facets.values()) {
				_facetPostProcessor.exclude(excludeDocs, facet);
			}
		}

		hits.setDocs(docs.toArray(new Document[0]));
		hits.setScores(ArrayUtil.toFloatArray(scores));
		hits.setSearchTime(
			(float)(System.currentTimeMillis() - hits.getStart()) /
				Time.SECOND);
		hits.setLength(hits.getLength() - excludeDocs.size());

		return excludeDocs.size();
	}

	private Hits _getHits(SearchContext searchContext) {
		if ((searchContext != null) &&
			(searchContext.getEnd() != QueryUtil.ALL_POS)) {

			int end = searchContext.getEnd();

			int start = searchContext.getStart();

			if (start == QueryUtil.ALL_POS) {
				start = 0;
			}

			int searchResultWindow = end - start;

			if (searchResultWindow > _searchQueryResultWindowLimit) {
				throw new SystemException(
					StringBundler.concat(
						"Search result window size of ", searchResultWindow,
						" exceeds the configured limit of ",
						_searchQueryResultWindowLimit));
			}
		}

		return _searchFunction.apply(searchContext);
	}

	private String[] _getSelectedFieldNames(String[] selectedFieldNames) {
		Set<String> set = SetUtil.fromArray(selectedFieldNames);

		Collections.addAll(set, _PERMISSION_SELECTED_FIELD_NAMES);

		return set.toArray(new String[0]);
	}

	private Boolean _hasCompanyScopeViewPermission(String className) {
		try {
			ResourcePermissionLocalService resourcePermissionLocalService =
				ResourcePermissionLocalServiceUtil.getService();

			if (resourcePermissionLocalService == null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Skipping company resource check because resource " +
							"permission service is not available");
				}

				return false;
			}

			if (resourcePermissionLocalService.hasResourcePermission(
					_permissionChecker.getCompanyId(), className,
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(_permissionChecker.getCompanyId()),
					_permissionChecker.getRoleIds(
						_permissionChecker.getUserId(), 0),
					ActionKeys.VIEW)) {

				return true;
			}
		}
		catch (NoSuchResourceActionException noSuchResourceActionException) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"No company scoped resource permissions found for class " +
						"name " + className,
					noSuchResourceActionException);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private boolean _isGroupAdmin(SearchContext searchContext) {
		long groupId = GetterUtil.getLong(
			searchContext.getAttribute(Field.GROUP_ID));

		if ((groupId == 0) || !_permissionChecker.isGroupAdmin(groupId)) {
			return false;
		}

		return true;
	}

	private boolean _isIncludeDocument(
		Document document, long companyId, boolean companyAdmin, int status,
		Map<String, Boolean> companyScopeViewPermissions) {

		long entryCompanyId = GetterUtil.getLong(
			document.get(Field.COMPANY_ID));

		if (entryCompanyId != companyId) {
			return false;
		}

		if (companyAdmin) {
			return true;
		}

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);

		boolean hasCompanyScopeViewPermission =
			companyScopeViewPermissions.computeIfAbsent(
				entryClassName, this::_hasCompanyScopeViewPermission);

		if (hasCompanyScopeViewPermission) {
			return true;
		}

		Indexer<?> indexer = _indexerRegistry.getIndexer(entryClassName);

		if ((indexer == null) || !indexer.isFilterSearch()) {
			return true;
		}

		long entryClassPK = GetterUtil.getLong(
			document.get(Field.ENTRY_CLASS_PK));

		try {
			if (indexer.hasPermission(
					_permissionChecker, entryClassName, entryClassPK,
					ActionKeys.VIEW)) {

				List<RelatedEntryIndexer> relatedEntryIndexers =
					_relatedEntryIndexerRegistry.getRelatedEntryIndexers(
						entryClassName);

				if (ListUtil.isNotEmpty(relatedEntryIndexers)) {
					for (RelatedEntryIndexer relatedEntryIndexer :
							relatedEntryIndexers) {

						if (!relatedEntryIndexer.isVisibleRelatedEntry(
								entryClassPK, status)) {

							return false;
						}
					}
				}

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private void _updateSearchHits(Hits hits, SearchContext searchContext) {
		SearchResponseImpl searchResponseImpl =
			(SearchResponseImpl)searchContext.getAttribute("search.response");

		if (searchResponseImpl == null) {
			return;
		}

		SearchHits searchHits = searchResponseImpl.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		if (searchHitsList.isEmpty()) {
			return;
		}

		Document[] documents = hits.getDocs();

		SearchHitsBuilderFactory searchHitsBuilderFactory =
			new SearchHitsBuilderFactoryImpl();

		SearchHitsBuilder searchHitsBuilder =
			searchHitsBuilderFactory.getSearchHitsBuilder();

		if (documents.length == 0) {
			searchResponseImpl.setSearchHits(searchHitsBuilder.build());

			return;
		}

		List<String> ids = new ArrayList<>();

		ArrayUtil.isNotEmptyForEach(
			documents, document -> ids.add(document.get("uid")));

		searchHitsList.removeIf(searchHit -> !ids.contains(searchHit.getId()));

		searchHitsBuilder.addSearchHits(searchHitsList);
		searchHitsBuilder.maxScore(searchHits.getMaxScore());
		searchHitsBuilder.searchTime(searchHits.getSearchTime());
		searchHitsBuilder.totalHits(hits.getLength());

		searchResponseImpl.setSearchHits(searchHitsBuilder.build());
	}

	private static final String[] _PERMISSION_SELECTED_FIELD_NAMES = {
		Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
		Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultSearchResultPermissionFilter.class);

	private final int _accurateCountThreshold;
	private final FacetPostProcessor _facetPostProcessor;
	private final IndexerRegistry _indexerRegistry;
	private final PermissionChecker _permissionChecker;
	private final RelatedEntryIndexerRegistry _relatedEntryIndexerRegistry;
	private final Function<SearchContext, Hits> _searchFunction;
	private final int _searchQueryResultWindowLimit;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final long _timeLimit;

	private class SlidingWindowSearcher {

		public Hits search(int start, int end, SearchContext searchContext) {
			if (_log.isDebugEnabled()) {
				_log.debug("Starting sliding window searches");
			}

			int docsCollectedCount = 0;
			FacetCountHelper facetCountHelper = null;
			StopWatch hitFilteringStopWatch = new StopWatch();
			long hitsStart = 0;
			int originalHitsSize = 0;
			int recalculatedHitsSize = 0;
			int searchesExecuted = 0;
			SlidingWindowHelper slidingWindowHelper = new SlidingWindowHelper(
				start, end);
			int slidingWindowStart = 0;
			int totalDocsNeededCount = end;

			StopWatch slidingWindowStopWatch = new StopWatch();

			slidingWindowStopWatch.start();

			while (true) {
				int amplificationFactor = (int)Math.pow(2, searchesExecuted);

				if (amplificationFactor > PropsValues.INDEX_SEARCH_LIMIT) {
					amplificationFactor = PropsValues.INDEX_SEARCH_LIMIT;
				}

				int remainingDocsNeededCount =
					totalDocsNeededCount - docsCollectedCount;

				int slidingWindowSize =
					remainingDocsNeededCount * amplificationFactor;

				int slidingWindowEnd = slidingWindowStart + slidingWindowSize;

				boolean extendedToAccurateCountThreshold = false;

				if (slidingWindowEnd < _accurateCountThreshold) {
					extendedToAccurateCountThreshold = true;

					slidingWindowSize =
						_accurateCountThreshold - slidingWindowStart;

					slidingWindowEnd = slidingWindowStart + slidingWindowSize;
				}

				boolean searchQueryResultWindowLimited = false;

				if ((slidingWindowSize > _searchQueryResultWindowLimit) &&
					(_searchQueryResultWindowLimit > 0)) {

					searchQueryResultWindowLimited = true;

					slidingWindowSize = _searchQueryResultWindowLimit;

					slidingWindowEnd = slidingWindowStart + slidingWindowSize;
				}

				boolean limitedByIndexSearchLimit = false;

				if (slidingWindowEnd > PropsValues.INDEX_SEARCH_LIMIT) {
					limitedByIndexSearchLimit = true;

					slidingWindowSize =
						PropsValues.INDEX_SEARCH_LIMIT - slidingWindowStart;

					slidingWindowEnd = slidingWindowStart + slidingWindowSize;
				}

				if (_log.isDebugEnabled()) {
					_log.debug(
						_getMessage(
							amplificationFactor,
							extendedToAccurateCountThreshold,
							limitedByIndexSearchLimit, remainingDocsNeededCount,
							searchQueryResultWindowLimited, slidingWindowSize));
				}

				searchContext.setEnd(slidingWindowEnd);

				searchContext.setStart(slidingWindowStart);

				_setSearchRequestFromAndSize(searchContext);

				Hits hits = _getHits(searchContext);

				if (searchesExecuted == 0) {
					facetCountHelper = new FacetCountHelper(
						searchContext.getFacets());
					hitsStart = hits.getStart();
					originalHitsSize = hits.getLength();
					recalculatedHitsSize = hits.getLength();
				}

				Document[] preFilteredDocs = hits.getDocs();

				if (searchesExecuted == 0) {
					hitFilteringStopWatch.start();
				}
				else {
					hitFilteringStopWatch.resume();
				}

				recalculatedHitsSize -= _filterHits(
					facetCountHelper, hits, searchContext);

				hitFilteringStopWatch.suspend();

				docsCollectedCount = _collectDocumentsAndScores(
					hits, slidingWindowHelper);

				if (_stopSearching(
						docsCollectedCount, originalHitsSize, preFilteredDocs,
						slidingWindowEnd, slidingWindowSize,
						slidingWindowStopWatch, totalDocsNeededCount)) {

					_updateHits(
						hits, hitsStart, recalculatedHitsSize,
						slidingWindowHelper, slidingWindowStopWatch,
						totalDocsNeededCount);

					hitFilteringStopWatch.resume();

					_updateSearchHits(hits, searchContext);

					hitFilteringStopWatch.suspend();

					_mergeFacets(facetCountHelper, searchContext);

					if (_log.isDebugEnabled()) {
						slidingWindowStopWatch.stop();

						StringBundler sb = new StringBundler(8);

						sb.append(searchesExecuted + 1);
						sb.append(" sliding window searches took ");
						sb.append(slidingWindowStopWatch.getTime());
						sb.append(" ms (");
						sb.append(
							slidingWindowStopWatch.getTime() -
								hitFilteringStopWatch.getTime());
						sb.append(" ms spent searching, ");
						sb.append(hitFilteringStopWatch.getTime());
						sb.append(" ms spent filtering results)");

						_log.debug(sb.toString());
					}

					return hits;
				}

				slidingWindowStart = slidingWindowEnd;

				searchesExecuted++;
			}
		}

		private int _collectDocumentsAndScores(
			Hits hits, SlidingWindowHelper slidingWindowHelper) {

			Document[] postFilteredDocs = hits.getDocs();

			for (int i = 0; i < postFilteredDocs.length; i++) {
				if (!slidingWindowHelper.add(hits.doc(i), hits.score(i))) {
					break;
				}
			}

			return slidingWindowHelper.getTotalDocs();
		}

		private String _getAggregationName(Facet facet) {
			if (facet instanceof com.liferay.portal.search.facet.Facet) {
				com.liferay.portal.search.facet.Facet osgiFacet =
					(com.liferay.portal.search.facet.Facet)facet;

				return osgiFacet.getAggregationName();
			}

			return facet.getFieldName();
		}

		private String _getMessage(
			int amplificationFactor, boolean extendedToAccurateCountThreshold,
			boolean limitedByIndexSearchLimit, int remainingDocsNeededCount,
			boolean searchQueryResultWindowLimited, int slidingWindowSize) {

			StringBundler sb = new StringBundler(13);

			sb.append("Results needed: ");
			sb.append(remainingDocsNeededCount);
			sb.append(", amplification factor: ");
			sb.append(amplificationFactor);
			sb.append(", query size: ");
			sb.append(slidingWindowSize);

			if (extendedToAccurateCountThreshold || limitedByIndexSearchLimit ||
				searchQueryResultWindowLimited) {

				sb.append(" (");
			}

			List<String> messages = new ArrayList<>();

			if (extendedToAccurateCountThreshold) {
				messages.add("extended to accurate count threshold");
			}

			if (searchQueryResultWindowLimited) {
				messages.add("limited by search query result window limit");
			}

			if (limitedByIndexSearchLimit) {
				messages.add("limited by index search limit");
			}

			if (!messages.isEmpty()) {
				for (String message : messages) {
					sb.append(message);
					sb.append(", ");
				}

				sb.setIndex(sb.index() - 1);
			}

			if (sb.index() > 6) {
				sb.append(")");
			}

			return sb.toString();
		}

		private void _mergeFacets(
			FacetCountHelper facetCountHelper, SearchContext searchContext) {

			Map<String, Facet> facets = searchContext.getFacets();

			for (Facet facet : facets.values()) {
				Facet helperFacet = facetCountHelper.getFacet(
					_getAggregationName(facet));

				FacetCollector helperFacetCollector =
					helperFacet.getFacetCollector();

				FacetCollector facetCollector = facet.getFacetCollector();

				List<TermCollector> termCollectors =
					facetCollector.getTermCollectors();

				List<TermCollector> newTermCollectors = new ArrayList<>();

				for (TermCollector termCollector : termCollectors) {
					String term = termCollector.getTerm();

					TermCollector helperTermCollector =
						helperFacetCollector.getTermCollector(term);

					int frequency = helperTermCollector.getFrequency();

					if (frequency >= 0) {
						newTermCollectors.add(
							new DefaultTermCollector(term, frequency));
					}
				}

				facet.setFacetCollector(
					new SimpleFacetCollector(
						facetCollector.getFieldName(), newTermCollectors));
			}
		}

		private void _setSearchRequestFromAndSize(SearchContext searchContext) {
			SearchRequestBuilder searchRequestBuilder =
				_searchRequestBuilderFactory.builder(searchContext);

			searchRequestBuilder.from(searchContext.getStart());
			searchRequestBuilder.size(
				searchContext.getEnd() - searchContext.getStart());
		}

		private boolean _stopSearching(
			int docsCollectedCount, int originalHitsSize,
			Document[] preFilteredDocs, int slidingWindowEnd,
			int slidingWindowSize, StopWatch slidingWindowStopWatch,
			int totalDocsNeededCount) {

			if ((slidingWindowEnd >= originalHitsSize) ||
				(preFilteredDocs.length < slidingWindowSize)) {

				return true;
			}

			if (slidingWindowEnd < _accurateCountThreshold) {
				return false;
			}

			if ((docsCollectedCount == totalDocsNeededCount) ||
				(slidingWindowEnd == PropsValues.INDEX_SEARCH_LIMIT) ||
				_timeLimitReached(slidingWindowStopWatch)) {

				return true;
			}

			return false;
		}

		private boolean _timeLimitReached(StopWatch slidingWindowStopWatch) {
			if ((_timeLimit > 0) &&
				(slidingWindowStopWatch.getTime() > _timeLimit)) {

				return true;
			}

			return false;
		}

		private void _updateHits(
			Hits hits, long hitsStart, int recalculatedHitsSize,
			SlidingWindowHelper slidingWindowHelper,
			StopWatch slidingWindowStopWatch, int totalDocsNeededCount) {

			Tuple documentsAndScoresTuple =
				slidingWindowHelper.getDocumentsAndScoresTuple();

			List<Document> documents =
				(List<Document>)documentsAndScoresTuple.getObject(0);

			hits.setDocs(documents.toArray(new Document[0]));

			hits.setScores(
				ArrayUtil.toFloatArray(
					(List<Float>)documentsAndScoresTuple.getObject(1)));

			int size = Math.max(recalculatedHitsSize, documents.size());

			if (_timeLimitReached(slidingWindowStopWatch) &&
				(slidingWindowHelper.getTotalDocs() < totalDocsNeededCount)) {

				size = slidingWindowHelper.getTotalDocs();
			}

			hits.setLength(size);

			hits.setSearchTime(
				(float)(System.currentTimeMillis() - hitsStart) / Time.SECOND);
		}

		private class FacetCountHelper {

			public FacetCountHelper(Map<String, Facet> facets) {
				for (Facet searchContextFacet : facets.values()) {
					Facet facet = new FacetImpl(
						searchContextFacet.getFieldName(), null);

					if (searchContextFacet instanceof NestedFacet) {
						NestedFacet nestedFacet =
							(NestedFacet)searchContextFacet;

						NestedFacetImpl nestedFacetImpl = new NestedFacetImpl(
							searchContextFacet.getFieldName(), null);

						nestedFacetImpl.setFilterField(
							nestedFacet.getFilterField());
						nestedFacetImpl.setFilterValue(
							nestedFacet.getFilterValue());
						nestedFacetImpl.setPath(nestedFacet.getPath());

						facet = nestedFacetImpl;
					}
					else if (searchContextFacet instanceof RangeFacet) {
						facet = new RangeFacet(null);

						facet.setFieldName(searchContextFacet.getFieldName());
					}

					List<TermCollector> termCollectors = new ArrayList<>();

					FacetCollector facetCollector =
						searchContextFacet.getFacetCollector();

					for (TermCollector termCollector :
							facetCollector.getTermCollectors()) {

						termCollectors.add(
							new DefaultTermCollector(
								termCollector.getTerm(),
								termCollector.getFrequency()));
					}

					facet.setFacetCollector(
						new SimpleFacetCollector(
							searchContextFacet.getFieldName(), termCollectors));

					_facets.put(_getAggregationName(searchContextFacet), facet);
				}
			}

			public Facet getFacet(String fieldName) {
				return _facets.get(fieldName);
			}

			public Map<String, Facet> getFacets() {
				return _facets;
			}

			private Map<String, Facet> _facets = new HashMap<>();

		}

		private class SlidingWindowHelper {

			public SlidingWindowHelper(int start, int end) {
				_start = start;
				_end = end;

				_documents = new CircularFifoQueue<>(Math.max(1, end - start));
				_scores = new CircularFifoQueue<>(Math.max(1, end - start));
			}

			public boolean add(Document document, Float score) {
				if (_totalDocs == _end) {
					return false;
				}

				if (_documents.isAtFullCapacity()) {
					_documentsDiscarded++;
				}

				_documents.add(document);
				_scores.add(score);

				_totalDocs++;

				return true;
			}

			public Tuple getDocumentsAndScoresTuple() {
				List<Document> documents = new ArrayList<>();
				List<Float> scores = new ArrayList<>();

				if (_totalDocs < _start) {
					return new Tuple(documents, scores);
				}

				for (int i = _start - _documentsDiscarded;
					 i < _documents.size(); i++) {

					documents.add(_documents.get(i));
					scores.add(_scores.get(i));
				}

				return new Tuple(documents, scores);
			}

			public int getTotalDocs() {
				return _totalDocs;
			}

			private final CircularFifoQueue<Document> _documents;
			private int _documentsDiscarded;
			private final int _end;
			private final CircularFifoQueue<Float> _scores;
			private final int _start;
			private int _totalDocs;

		}

	}

}
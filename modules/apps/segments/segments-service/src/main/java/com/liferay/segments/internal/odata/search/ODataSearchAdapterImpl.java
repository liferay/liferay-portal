/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.odata.search;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.search.generic.TermRangeQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.Filter;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.segments.odata.search.ODataSearchAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = ODataSearchAdapter.class)
public class ODataSearchAdapterImpl implements ODataSearchAdapter {

	@Override
	public Hits search(
			long companyId, FilterParser filterParser, String filterString,
			String className, EntityModel entityModel, Locale locale, int start,
			int end)
		throws PortalException {

		try {
			SearchContext searchContext = _createSearchContext(companyId);

			return search(
				_indexerRegistry.getIndexer(className), searchContext,
				_getBooleanQuery(
					filterString, entityModel, filterParser, locale),
				start, end);
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to search with filter " + filterString, exception);
		}
	}

	@Override
	public int searchCount(
			long companyId, FilterParser filterParser, String filterString,
			String className, EntityModel entityModel, Locale locale)
		throws PortalException {

		try {
			SearchContext searchContext = _createSearchContext(companyId);

			Indexer<?> indexer = _indexerRegistry.getIndexer(className);

			searchContext.setBooleanClauses(
				new BooleanClause[] {
					_getBooleanClause(
						_getBooleanQuery(
							filterString, entityModel, filterParser, locale))
				});

			return (int)indexer.searchCount(searchContext);
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to search with filter " + filterString, exception);
		}
	}

	protected Hits search(
			Indexer<?> indexer, SearchContext searchContext,
			BooleanQuery booleanQuery, int start, int end)
		throws PortalException {

		List<Document> documentsList = new ArrayList<>();

		if (end == QueryUtil.ALL_POS) {
			end = Integer.MAX_VALUE;
		}

		int indexSearchLimit = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.INDEX_SEARCH_LIMIT));
		Document lastDocument = null;

		Sort sort = new Sort(Field.ENTRY_CLASS_PK, Sort.LONG_TYPE, false);

		searchContext.setSorts(sort);

		if (start == QueryUtil.ALL_POS) {
			start = 0;
		}

		while (start != end) {
			searchContext.setBooleanClauses(
				new BooleanClause[] {
					_getBooleanClause(
						_getLastDocumentBooleanQuery(
							booleanQuery, lastDocument, sort.getFieldName()))
				});
			searchContext.setEnd(Math.min(end, indexSearchLimit));
			searchContext.setStart(Math.min(start, indexSearchLimit - 1));

			Hits hits = indexer.search(searchContext);

			Document[] documents = hits.getDocs();

			if (documents.length == 0) {
				break;
			}

			if (start < indexSearchLimit) {
				Collections.addAll(documentsList, documents);

				if (end < indexSearchLimit) {
					break;
				}
			}

			lastDocument = documents[documents.length - 1];

			start = Math.max(0, start - indexSearchLimit);
			end = Math.max(0, end - indexSearchLimit);
		}

		Hits hits = new HitsImpl();

		hits.setDocs(documentsList.toArray(new Document[0]));
		hits.setLength(documentsList.size());
		hits.setStart(0);

		return hits;
	}

	private SearchContext _createSearchContext(long companyId) {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setGroupIds(new long[] {-1L});

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private BooleanClause<Query> _getBooleanClause(BooleanQuery booleanQuery)
		throws PortalException {

		return BooleanClauseFactoryUtil.create(
			booleanQuery, BooleanClauseOccur.MUST.getName());
	}

	private BooleanQuery _getBooleanQuery(
			String filterString, EntityModel entityModel,
			FilterParser filterParser, Locale locale)
		throws Exception {

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		booleanQuery.add(new MatchAllQuery(), BooleanClauseOccur.MUST);

		BooleanFilter booleanFilter = new BooleanFilter();

		com.liferay.portal.kernel.search.filter.Filter filter =
			_getSearchFilter(filterString, entityModel, filterParser, locale);

		if (filter != null) {
			booleanFilter.add(filter, BooleanClauseOccur.MUST);
		}

		booleanQuery.setPreBooleanFilter(booleanFilter);

		return booleanQuery;
	}

	private BooleanQuery _getLastDocumentBooleanQuery(
			BooleanQuery booleanQuery, Document lastDocument, String sortField)
		throws ParseException {

		if (lastDocument == null) {
			return booleanQuery;
		}

		if (!lastDocument.hasField(sortField)) {
			throw new IllegalArgumentException(
				"Missing " + sortField + " in the last document");
		}

		BooleanQuery lastDocumentBooleanQuery = new BooleanQueryImpl();

		lastDocumentBooleanQuery.add(booleanQuery, BooleanClauseOccur.MUST);

		TermRangeQuery termRangeQuery = new TermRangeQueryImpl(
			sortField, lastDocument.get(sortField), null, false, true);

		lastDocumentBooleanQuery.add(termRangeQuery, BooleanClauseOccur.MUST);

		return lastDocumentBooleanQuery;
	}

	private com.liferay.portal.kernel.search.filter.Filter _getSearchFilter(
			String filterString, EntityModel entityModel,
			FilterParser filterParser, Locale locale)
		throws Exception {

		Filter filter = new Filter(filterParser.parse(filterString));

		if (filter == Filter.emptyFilter()) {
			return null;
		}

		try {
			return _expressionConvert.convert(
				filter.getExpression(), locale, entityModel);
		}
		catch (Exception exception) {
			throw new InvalidFilterException(
				"Invalid filter: " + exception.getMessage(), exception);
		}
	}

	@Reference(
		target = "(result.class.name=com.liferay.portal.kernel.search.filter.Filter)"
	)
	private ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
		_expressionConvert;

	@Reference
	private IndexerRegistry _indexerRegistry;

}
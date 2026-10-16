/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.field;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Pagination;

/**
 * @author Mikel Lorza
 */
public class MissingRequiredFieldsAssetSearcher {

	public MissingRequiredFieldsAssetSearcher(
		Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	public long getCount(long companyId, Long[] groupIds) {
		SearchResponse searchResponse = _searcher.search(
			_getSearchRequestBuilder(
				companyId, groupIds
			).size(
				0
			).build());

		return searchResponse.getCount();
	}

	public SearchResponse search(
		long companyId, Long[] groupIds, String languageId,
		Pagination pagination, String search, Sort[] sorts) {

		SearchRequestBuilder searchRequestBuilder = _getSearchRequestBuilder(
			companyId, groupIds);

		searchRequestBuilder.addSelectedFieldNames(
			Field.ENTRY_CLASS_PK, "missingRequiredFieldsCount",
			"objectDefinitionId", "objectEntryTitle",
			Field.getLocalizedName(languageId, "objectEntryTitle")
		).from(
			pagination.getStartPosition()
		).size(
			pagination.getPageSize()
		);

		if (ArrayUtil.isNotEmpty(sorts)) {
			searchRequestBuilder.withSearchContext(
				searchContext -> searchContext.setSorts(sorts));
		}

		if (search != null) {
			searchRequestBuilder.queryString(search);
		}

		return _searcher.search(searchRequestBuilder.build());
	}

	private SearchRequestBuilder _getSearchRequestBuilder(
		long companyId, Long[] groupIds) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		TermsQuery termsQuery = QueriesUtil.terms("cms_section");

		termsQuery.addValues("contents", "files");

		booleanQuery.addFilterQueryClauses(
			termsQuery, QueriesUtil.exists("missingRequiredFieldsCount"),
			QueriesUtil.term("rootDescendantNode", false));

		return _searchRequestBuilderFactory.builder(
		).companyId(
			companyId
		).emptySearchEnabled(
			true
		).groupIds(
			ArrayUtil.toArray(groupIds)
		).query(
			booleanQuery
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				Field.STATUS, WorkflowConstants.STATUS_ANY)
		);
	}

	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final Searcher _searcher;

}
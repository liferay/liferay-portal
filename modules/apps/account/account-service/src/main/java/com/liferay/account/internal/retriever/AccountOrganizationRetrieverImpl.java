/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.retriever;

import com.liferay.account.retriever.AccountOrganizationRetriever;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.SortFieldBuilder;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = AccountOrganizationRetriever.class)
public class AccountOrganizationRetrieverImpl
	implements AccountOrganizationRetriever {

	@Override
	public BaseModelSearchResult<Organization> searchAccountOrganizations(
		long accountEntryId, String keywords, int cur, int delta,
		String sortField, boolean reverse) {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequest(
				accountEntryId, keywords, cur, delta, sortField, reverse));

		SearchHits searchHits = searchResponse.getSearchHits();

		List<Organization> organizations = TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				long organizationId = document.getLong("organizationId");

				return _organizationLocalService.getOrganization(
					organizationId);
			});

		return new BaseModelSearchResult<>(
			organizations, searchResponse.getTotalHits());
	}

	private SearchRequest _getSearchRequest(
		long accountEntryId, String keywords, int cur, int delta,
		String sortField, boolean reverse) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
			Organization.class.getName()
		).emptySearchEnabled(
			true
		).highlightEnabled(
			false
		).withSearchContext(
			searchContext -> _populateSearchContext(
				searchContext, accountEntryId, keywords)
		);

		if (cur != QueryUtil.ALL_POS) {
			searchRequestBuilder.from(cur);
			searchRequestBuilder.size(delta);
		}

		if (Validator.isNotNull(sortField)) {
			SortOrder sortOrder = SortOrder.ASC;

			if (reverse) {
				sortOrder = SortOrder.DESC;
			}

			FieldSort fieldSort = _sorts.field(
				_sortFieldBuilder.getSortField(Organization.class, sortField),
				sortOrder);

			searchRequestBuilder.sorts(fieldSort);
		}

		return searchRequestBuilder.build();
	}

	private void _populateSearchContext(
		SearchContext searchContext, long accountEntryId, String keywords) {

		searchContext.setCompanyId(CompanyThreadLocal.getCompanyId());

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		searchContext.setAttribute(
			"accountEntryIds", new long[] {accountEntryId});
	}

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private SortFieldBuilder _sortFieldBuilder;

	@Reference
	private Sorts _sorts;

}
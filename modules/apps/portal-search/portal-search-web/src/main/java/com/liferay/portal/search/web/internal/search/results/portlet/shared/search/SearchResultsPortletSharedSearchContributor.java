/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet.shared.search;

import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.web.constants.SearchResultsPortletKeys;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferences;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	property = "jakarta.portlet.name=" + SearchResultsPortletKeys.SEARCH_RESULTS,
	service = PortletSharedSearchContributor.class
)
public class SearchResultsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchResultsPortletPreferences searchResultsPortletPreferences =
			new SearchResultsPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				searchResultsPortletPreferences.getFederatedSearchKey());

		_paginate(
			searchResultsPortletPreferences, portletSharedSearchSettings,
			searchRequestBuilder);

		if (searchResultsPortletPreferences.isHighlightEnabled()) {
			searchRequestBuilder.highlightEnabled(true);

			String[] fieldsToDisplay = SearchStringUtil.splitAndUnquote(
				searchResultsPortletPreferences.getFieldsToDisplay());

			searchRequestBuilder.highlightFields(fieldsToDisplay);
		}
	}

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private void _paginate(
		SearchResultsPortletPreferences searchResultsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		SearchRequestBuilder searchRequestBuilder) {

		String paginationStartParameterName =
			searchResultsPortletPreferences.getPaginationStartParameterName();

		portletSharedSearchSettings.setPaginationStartParameterName(
			paginationStartParameterName);
		searchRequestBuilder.paginationStartParameterName(
			paginationStartParameterName);

		int paginationDelta = GetterUtil.getInteger(
			portletSharedSearchSettings.getParameter(
				searchResultsPortletPreferences.
					getPaginationDeltaParameterName()),
			searchResultsPortletPreferences.getPaginationDelta());

		portletSharedSearchSettings.setPaginationDelta(paginationDelta);
		searchRequestBuilder.size(paginationDelta);

		SearchContext searchContext = searchRequestBuilder.withSearchContextGet(
			Function.identity());

		int paginationStart = GetterUtil.getInteger(
			portletSharedSearchSettings.getParameter(
				paginationStartParameterName));

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			paginationStart, paginationDelta);

		searchContext.setEnd(startAndEnd[1]);
		searchContext.setStart(startAndEnd[0]);

		if (paginationStart > 0) {
			portletSharedSearchSettings.setPaginationStart(paginationStart);

			searchRequestBuilder.from((paginationStart - 1) * paginationDelta);
		}
	}

}
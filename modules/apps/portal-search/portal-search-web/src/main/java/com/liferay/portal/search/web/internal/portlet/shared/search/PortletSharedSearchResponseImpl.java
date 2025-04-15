/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.shared.search;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.portlet.shared.task.helper.PortletSharedRequestHelper;
import com.liferay.portal.search.web.internal.search.request.SearchResponseImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author André de Oliveira
 */
public class PortletSharedSearchResponseImpl
	implements PortletSharedSearchResponse {

	public PortletSharedSearchResponseImpl(
		SearchResponseImpl searchResponseImpl,
		PortletSharedRequestHelper portletSharedRequestHelper) {

		_searchResponseImpl = searchResponseImpl;
		_portletSharedRequestHelper = portletSharedRequestHelper;
	}

	@Override
	public List<Document> getDocuments() {
		SearchResponse searchResponse = _searchResponseImpl.getSearchResponse();

		return searchResponse.getDocuments71();
	}

	@Override
	public Facet getFacet(String name) {
		SearchResponse searchResponse = getSearchResponse();

		return searchResponse.withFacetContextGet(
			facetContext -> facetContext.getFacet(name));
	}

	@Override
	public SearchResponse getFederatedSearchResponse(
		String federatedSearchKey) {

		return _searchResponseImpl.getFederatedSearchResponse(
			federatedSearchKey);
	}

	@Override
	public String getKeywords() {
		return _searchResponseImpl.getKeywords();
	}

	@Override
	public int getPaginationDelta() {
		return _searchResponseImpl.getPaginationDelta();
	}

	@Override
	public int getPaginationStart() {
		return _searchResponseImpl.getPaginationStart();
	}

	@Override
	public String getParameter(String name, RenderRequest renderRequest) {
		return _portletSharedRequestHelper.getParameter(name, renderRequest);
	}

	@Override
	public String[] getParameterValues(
		String name, RenderRequest renderRequest) {

		return _portletSharedRequestHelper.getParameterValues(
			name, renderRequest);
	}

	@Override
	public PortletPreferences getPortletPreferences(
		RenderRequest renderRequest) {

		return renderRequest.getPreferences();
	}

	@Override
	public List<String> getRelatedQueriesSuggestions() {
		return _searchResponseImpl.getRelatedQueriesSuggestions();
	}

	@Override
	public SearchResponse getSearchResponse() {
		return _searchResponseImpl.getSearchResponse();
	}

	@Override
	public SearchSettings getSearchSettings() {
		return _searchResponseImpl.getSearchSettings();
	}

	@Override
	public String getSpellCheckSuggestion() {
		return _searchResponseImpl.getSpellCheckSuggestion();
	}

	@Override
	public ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	@Override
	public int getTotalHits() {
		return _searchResponseImpl.getTotalHits();
	}

	private final PortletSharedRequestHelper _portletSharedRequestHelper;
	private final SearchResponseImpl _searchResponseImpl;

}
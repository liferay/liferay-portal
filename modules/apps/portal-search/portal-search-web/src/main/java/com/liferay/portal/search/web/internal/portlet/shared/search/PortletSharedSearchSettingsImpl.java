/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.shared.search;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.portlet.shared.task.helper.PortletSharedRequestHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.search.web.search.request.SearchSettings;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

/**
 * @author André de Oliveira
 */
public class PortletSharedSearchSettingsImpl
	implements PortletSharedSearchSettings {

	public PortletSharedSearchSettingsImpl(
		SearchSettings searchSettings, String portletId,
		PortletPreferences portletPreferences,
		PortletSharedRequestHelper portletSharedRequestHelper,
		RenderRequest renderRequest) {

		_searchSettings = searchSettings;
		_portletId = portletId;
		_portletPreferences = portletPreferences;
		_portletSharedRequestHelper = portletSharedRequestHelper;
		_renderRequest = renderRequest;

		_searchRequestBuilder = searchSettings.getSearchRequestBuilder();
	}

	@Override
	public void addCondition(BooleanClause<Query> booleanClause) {
		_searchSettings.addCondition(booleanClause);
	}

	@Override
	public void addFacet(Facet facet) {
		_searchSettings.addFacet(facet);
	}

	@Override
	public SearchRequestBuilder getFederatedSearchRequestBuilder(
		String federatedSearchKey) {

		return _searchSettings.getFederatedSearchRequestBuilder(
			federatedSearchKey);
	}

	@Override
	public String getKeywordsParameterName() {
		return _searchSettings.getKeywordsParameterName();
	}

	@Override
	public Integer getPaginationDelta() {
		return _searchSettings.getPaginationDelta();
	}

	@Override
	public String getPaginationDeltaParameterName() {
		return _searchSettings.getPaginationDeltaParameterName();
	}

	@Override
	public Integer getPaginationStart() {
		return _searchSettings.getPaginationStart();
	}

	@Override
	public String getPaginationStartParameterName() {
		return _searchSettings.getPaginationStartParameterName();
	}

	@Override
	public String getParameter(String name) {
		return _portletSharedRequestHelper.getParameter(name, _renderRequest);
	}

	@Override
	public String[] getParameterValues(String name) {
		return (String[])GetterUtil.getObject(
			_portletSharedRequestHelper.getParameterValues(
				name, _renderRequest),
			new String[0]);
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	@Override
	public PortletPreferences getPortletPreferences() {
		return _portletPreferences;
	}

	@Override
	public QueryConfig getQueryConfig() {
		return _searchSettings.getQueryConfig();
	}

	@Override
	public RenderRequest getRenderRequest() {
		return _renderRequest;
	}

	@Override
	public String getScope() {
		return _searchSettings.getScope();
	}

	@Override
	public String getScopeParameterName() {
		return _searchSettings.getScopeParameterName();
	}

	@Override
	public SearchContext getSearchContext() {
		return _searchSettings.getSearchContext();
	}

	@Override
	public SearchRequestBuilder getSearchRequestBuilder() {
		return _searchRequestBuilder;
	}

	@Override
	public ThemeDisplay getThemeDisplay() {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(_renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	@Override
	public Boolean isIncludeAttachments() {
		return _searchSettings.isIncludeAttachments();
	}

	@Override
	public void setIncludeAttachments(boolean includeAttachments) {
		_searchSettings.setIncludeAttachments(includeAttachments);
	}

	@Override
	public void setKeywords(String keywords) {
		_searchSettings.setKeywords(keywords);
	}

	@Override
	public void setKeywordsParameterName(String keywordsParameterName) {
		_searchSettings.setKeywordsParameterName(keywordsParameterName);
	}

	@Override
	public void setPaginationDelta(int delta) {
		_searchSettings.setPaginationDelta(delta);
	}

	@Override
	public void setPaginationDeltaParameterName(String deltaParameterName) {
		_searchSettings.setPaginationDeltaParameterName(deltaParameterName);
	}

	@Override
	public void setPaginationStart(int paginationStart) {
		_searchSettings.setPaginationStart(paginationStart);
	}

	@Override
	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_searchSettings.setPaginationStartParameterName(
			paginationStartParameterName);
	}

	@Override
	public void setScope(String scope) {
		_searchSettings.setScope(scope);
	}

	@Override
	public void setScopeParameterName(String scopeParameterName) {
		_searchSettings.setScopeParameterName(scopeParameterName);
	}

	private final String _portletId;
	private final PortletPreferences _portletPreferences;
	private final PortletSharedRequestHelper _portletSharedRequestHelper;
	private final RenderRequest _renderRequest;
	private final SearchRequestBuilder _searchRequestBuilder;
	private final SearchSettings _searchSettings;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.context.SearchContextFactory;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.constants.SearchPortletParameterNames;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.facet.AssetEntriesSearchFacet;
import com.liferay.portal.search.web.internal.facet.util.SearchFacetRegistryUtil;
import com.liferay.portal.search.web.internal.portlet.SearchPortletSearchResultPreferences;
import com.liferay.portal.search.web.internal.search.request.SearchRequestImpl;
import com.liferay.portal.search.web.internal.search.request.SearchResponseImpl;
import com.liferay.portal.search.web.search.request.SearchSettings;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Eudaldo Alonso
 */
public class SearchDisplayContext {

	public SearchDisplayContext(
			RenderRequest renderRequest, PortletPreferences portletPreferences,
			Portal portal, Language language, Searcher searcher,
			IndexSearchPropsValues indexSearchPropsValues,
			PortletURLFactory portletURLFactory,
			SummaryBuilderFactory summaryBuilderFactory,
			SearchContextFactory searchContextFactory,
			SearchRequestBuilderFactory searchRequestBuilderFactory,
			JSONFactory jsonFactory)
		throws PortletException {

		_renderRequest = renderRequest;
		_portletPreferences = portletPreferences;
		_indexSearchPropsValues = indexSearchPropsValues;
		_portletURLFactory = portletURLFactory;
		_summaryBuilderFactory = summaryBuilderFactory;
		_searchContextFactory = searchContextFactory;

		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		SearchResultPreferences searchResultPreferences =
			new SearchPortletSearchResultPreferences(
				portletPreferences, themeDisplaySupplier);

		_searchResultPreferences = searchResultPreferences;

		_themeDisplaySupplier = themeDisplaySupplier;

		String keywords = getKeywords();

		if (keywords == null) {
			_hits = null;
			_keywords = null;
			_queryString = null;
			_searchContainer = null;
			_searchContext = null;

			return;
		}

		_keywords = new Keywords(keywords);

		HttpServletRequest httpServletRequest = portal.getHttpServletRequest(
			renderRequest);

		String emptyResultMessage = language.format(
			httpServletRequest,
			"no-results-were-found-that-matched-the-keywords-x",
			"<strong>" + HtmlUtil.escape(keywords) + "</strong>", false);

		SearchContainer<Document> searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null, emptyResultMessage);

		long[] assetCategoryIds = StringUtil.split(
			ParamUtil.getString(httpServletRequest, "category"), 0L);
		String[] assetTagNames = StringUtil.split(
			ParamUtil.getString(httpServletRequest, "tag"));
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContext searchContext = searchContextFactory.getSearchContext(
			assetCategoryIds, assetTagNames, themeDisplay.getCompanyId(),
			ParamUtil.getString(httpServletRequest, "keywords"),
			themeDisplay.getLayout(), themeDisplay.getLocale(),
			httpServletRequest.getParameterMap(),
			themeDisplay.getScopeGroupId(), themeDisplay.getTimeZone(),
			themeDisplay.getUserId());

		_resetScope(searchContext);

		boolean luceneSyntax = isUseAdvancedSearchSyntax();

		if (!luceneSyntax) {
			luceneSyntax = _keywords.isLuceneSyntax();
		}

		if (luceneSyntax) {
			searchContext.setAttribute(
				SearchContextAttributes.ATTRIBUTE_KEY_LUCENE_SYNTAX,
				Boolean.TRUE);
		}

		searchContext.setEntryClassNames(
			_getEntryClassNames(getSearchConfiguration(), jsonFactory));
		searchContext.setKeywords(_keywords.getKeywords());

		SearchRequestImpl searchRequestImpl = new SearchRequestImpl(
			() -> searchContext, searchContainerOptions -> searchContainer,
			searcher, searchRequestBuilderFactory);

		searchRequestImpl.addSearchSettingsContributor(
			this::_contributeSearchSettings);

		SearchResponseImpl searchResponseImpl = searchRequestImpl.search();

		SearchResponse searchResponse = searchResponseImpl.getSearchResponse();

		_hits = searchResponse.withHitsGet(Function.identity());
		_queryString = searchResponse.getRequestString();

		_searchContainer = searchContainer;
		_searchContext = searchContext;
	}

	public int getCollatedSpellCheckResultDisplayThreshold() {
		if (_collatedSpellCheckResultDisplayThreshold != null) {
			return _collatedSpellCheckResultDisplayThreshold;
		}

		int collatedSpellCheckResultScoresThreshold =
			_indexSearchPropsValues.
				getCollatedSpellCheckResultScoresThreshold();

		_collatedSpellCheckResultDisplayThreshold = GetterUtil.getInteger(
			_portletPreferences.getValue(
				"collatedSpellCheckResultDisplayThreshold", null),
			collatedSpellCheckResultScoresThreshold);

		if (_collatedSpellCheckResultDisplayThreshold < 0) {
			_collatedSpellCheckResultDisplayThreshold =
				collatedSpellCheckResultScoresThreshold;
		}

		return _collatedSpellCheckResultDisplayThreshold;
	}

	public List<SearchFacet> getEnabledSearchFacets() {
		if (_enabledSearchFacets != null) {
			return _enabledSearchFacets;
		}

		_enabledSearchFacets = ListUtil.filter(
			getSearchFacets(),
			searchFacet -> isDisplayFacet(searchFacet.getClassName()));

		return _enabledSearchFacets;
	}

	public Hits getHits() {
		return _hits;
	}

	public String getKeywords() {
		return ParamUtil.getString(
			_renderRequest, SearchPortletParameterNames.KEYWORDS, null);
	}

	public PortletURL getPortletURL() throws PortletException {
		return _portletURLFactory.getPortletURL();
	}

	public PortletURLFactory getPortletURLFactory() {
		return _portletURLFactory;
	}

	public QueryConfig getQueryConfig() {
		if (_queryConfig != null) {
			return _queryConfig;
		}

		_queryConfig = new QueryConfig();

		_queryConfig.setCollatedSpellCheckResultEnabled(
			isCollatedSpellCheckResultEnabled());
		_queryConfig.setCollatedSpellCheckResultScoresThreshold(
			getCollatedSpellCheckResultDisplayThreshold());
		_queryConfig.setHighlightEnabled(
			_searchResultPreferences.isHighlightEnabled());
		_queryConfig.setQueryIndexingEnabled(isQueryIndexingEnabled());
		_queryConfig.setQueryIndexingThreshold(getQueryIndexingThreshold());
		_queryConfig.setQuerySuggestionEnabled(isQuerySuggestionEnabled());
		_queryConfig.setQuerySuggestionScoresThreshold(
			getQuerySuggestionDisplayThreshold());
		_queryConfig.setQuerySuggestionMax(getQuerySuggestionMax());

		return _queryConfig;
	}

	public int getQueryIndexingThreshold() {
		if (_queryIndexingThreshold != null) {
			return _queryIndexingThreshold;
		}

		_queryIndexingThreshold = GetterUtil.getInteger(
			_portletPreferences.getValue("queryIndexingThreshold", null),
			_indexSearchPropsValues.getQueryIndexingThreshold());

		if (_queryIndexingThreshold < 0) {
			_queryIndexingThreshold =
				_indexSearchPropsValues.getQueryIndexingThreshold();
		}

		return _queryIndexingThreshold;
	}

	public String getQueryString() {
		return _queryString;
	}

	public int getQuerySuggestionDisplayThreshold() {
		if (_querySuggestionDisplayThreshold != null) {
			return _querySuggestionDisplayThreshold;
		}

		_querySuggestionDisplayThreshold = GetterUtil.getInteger(
			_portletPreferences.getValue(
				"querySuggestionDisplayThreshold", null),
			_indexSearchPropsValues.getQuerySuggestionScoresThreshold());

		if (_querySuggestionDisplayThreshold < 0) {
			_querySuggestionDisplayThreshold =
				_indexSearchPropsValues.getQuerySuggestionScoresThreshold();
		}

		return _querySuggestionDisplayThreshold;
	}

	public int getQuerySuggestionMax() {
		if (_querySuggestionMax != null) {
			return _querySuggestionMax;
		}

		_querySuggestionMax = GetterUtil.getInteger(
			_portletPreferences.getValue("querySuggestionMax", null),
			_indexSearchPropsValues.getQuerySuggestionMax());

		if (_querySuggestionMax <= 0) {
			_querySuggestionMax =
				_indexSearchPropsValues.getQuerySuggestionMax();
		}

		return _querySuggestionMax;
	}

	public String[] getQueryTerms() {
		Hits hits = getHits();

		return hits.getQueryTerms();
	}

	public String getSearchConfiguration() {
		if (_searchConfiguration != null) {
			return _searchConfiguration;
		}

		_searchConfiguration = _portletPreferences.getValue(
			"searchConfiguration", StringPool.BLANK);

		return _searchConfiguration;
	}

	public SearchContainer<Document> getSearchContainer() {
		return _searchContainer;
	}

	public SearchContext getSearchContext() {
		return _searchContext;
	}

	public List<SearchFacet> getSearchFacets() {
		return SearchFacetRegistryUtil.getSearchFacets();
	}

	public SearchResultPreferences getSearchResultPreferences() {
		return _searchResultPreferences;
	}

	public long getSearchScopeGroupId() {
		SearchScope searchScope = getSearchScope();

		if (searchScope == SearchScope.EVERYTHING) {
			return 0;
		}

		ThemeDisplay themeDisplay = getThemeDisplay();

		return themeDisplay.getScopeGroupId();
	}

	public String getSearchScopeParameterString() {
		SearchScope searchScope = getSearchScope();

		if (searchScope == null) {
			searchScope = SearchScopePreference.THIS_SITE.getSearchScope();
		}

		return searchScope.getParameterString();
	}

	public String getSearchScopePreferenceString() {
		if (_searchScopePreferenceString != null) {
			return _searchScopePreferenceString;
		}

		_searchScopePreferenceString = _portletPreferences.getValue(
			"searchScope", StringPool.BLANK);

		return _searchScopePreferenceString;
	}

	public SummaryBuilderFactory getSummaryBuilderFactory() {
		return _summaryBuilderFactory;
	}

	public boolean isCollatedSpellCheckResultEnabled() {
		if (_collatedSpellCheckResultEnabled != null) {
			return _collatedSpellCheckResultEnabled;
		}

		_collatedSpellCheckResultEnabled = GetterUtil.getBoolean(
			_portletPreferences.getValue(
				"collatedSpellCheckResultEnabled", null));

		return _collatedSpellCheckResultEnabled;
	}

	public boolean isDisplayFacet(String className) {
		return GetterUtil.getBoolean(
			_portletPreferences.getValue(className, null), true);
	}

	public boolean isDisplayMainQuery() {
		if (_displayMainQuery != null) {
			return _displayMainQuery;
		}

		_displayMainQuery = GetterUtil.getBoolean(
			_portletPreferences.getValue("displayMainQuery", null));

		return _displayMainQuery;
	}

	public boolean isDisplayOpenSearchResults() {
		if (_displayOpenSearchResults != null) {
			return _displayOpenSearchResults;
		}

		_displayOpenSearchResults = GetterUtil.getBoolean(
			_portletPreferences.getValue("displayOpenSearchResults", null));

		return _displayOpenSearchResults;
	}

	public boolean isDisplayResultsInDocumentForm() {
		return _searchResultPreferences.isDisplayResultsInDocumentForm();
	}

	public boolean isDLLinkToViewURL() {
		if (_dlLinkToViewURL != null) {
			return _dlLinkToViewURL;
		}

		_dlLinkToViewURL = false;

		return _dlLinkToViewURL;
	}

	public boolean isHighlightEnabled() {
		QueryConfig queryConfig = getQueryConfig();

		return queryConfig.isHighlightEnabled();
	}

	public boolean isIncludeSystemPortlets() {
		if (_includeSystemPortlets != null) {
			return _includeSystemPortlets;
		}

		_includeSystemPortlets = false;

		return _includeSystemPortlets;
	}

	public boolean isQueryIndexingEnabled() {
		if (_queryIndexingEnabled != null) {
			return _queryIndexingEnabled;
		}

		_queryIndexingEnabled = GetterUtil.getBoolean(
			_portletPreferences.getValue("queryIndexingEnabled", null),
			_indexSearchPropsValues.isQueryIndexingEnabled());

		return _queryIndexingEnabled;
	}

	public boolean isQuerySuggestionEnabled() {
		if (_querySuggestionEnabled != null) {
			return _querySuggestionEnabled;
		}

		_querySuggestionEnabled = GetterUtil.getBoolean(
			_portletPreferences.getValue("querySuggestionEnabled", null),
			_indexSearchPropsValues.isQuerySuggestionEnabled());

		return _querySuggestionEnabled;
	}

	public boolean isSearchScopePreferenceEverythingAvailable() {
		ThemeDisplay themeDisplay = getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		return !group.isStagingGroup();
	}

	public boolean isSearchScopePreferenceLetTheUserChoose() {
		SearchScopePreference searchScopePreference =
			getSearchScopePreference();

		if (searchScopePreference ==
				SearchScopePreference.LET_THE_USER_CHOOSE) {

			return true;
		}

		return false;
	}

	public boolean isShowMenu() {
		for (SearchFacet searchFacet : getSearchFacets()) {
			if (isDisplayFacet(searchFacet.getClassName())) {
				return true;
			}
		}

		return false;
	}

	public boolean isUseAdvancedSearchSyntax() {
		if (_useAdvancedSearchSyntax != null) {
			return _useAdvancedSearchSyntax;
		}

		_useAdvancedSearchSyntax = GetterUtil.getBoolean(
			_portletPreferences.getValue("useAdvancedSearchSyntax", null));

		return _useAdvancedSearchSyntax;
	}

	public boolean isViewInContext() {
		return _searchResultPreferences.isViewInContext();
	}

	protected SearchScope getSearchScope() {
		String scopeString = ParamUtil.getString(
			_renderRequest, SearchPortletParameterNames.SCOPE);

		if (Validator.isNotNull(scopeString)) {
			return SearchScope.getSearchScope(scopeString);
		}

		SearchScopePreference searchScopePreference =
			getSearchScopePreference();

		return searchScopePreference.getSearchScope();
	}

	protected SearchScopePreference getSearchScopePreference() {
		return SearchScopePreference.getSearchScopePreference(
			getSearchScopePreferenceString());
	}

	protected ThemeDisplay getThemeDisplay() {
		return _themeDisplaySupplier.getThemeDisplay();
	}

	private void _addEnabledSearchFacets(
		SearchRequestBuilder searchRequestBuilder) {

		ThemeDisplay themeDisplay = _themeDisplaySupplier.getThemeDisplay();

		long companyId = themeDisplay.getCompanyId();

		for (SearchFacet searchFacet : getEnabledSearchFacets()) {
			Facet facet = searchRequestBuilder.withSearchContextGet(
				searchContext -> _getFacet(
					searchFacet, companyId, searchContext));

			if (facet != null) {
				searchRequestBuilder.withFacetContext(
					facetContext -> facetContext.addFacet(facet));
			}
		}
	}

	private void _contributeSearchSettings(SearchSettings searchSettings) {
		searchSettings.setKeywords(_keywords.getKeywords());

		QueryConfig queryConfig = searchSettings.getQueryConfig();

		queryConfig.setCollatedSpellCheckResultEnabled(
			isCollatedSpellCheckResultEnabled());
		queryConfig.setCollatedSpellCheckResultScoresThreshold(
			getCollatedSpellCheckResultDisplayThreshold());
		queryConfig.setHighlightEnabled(isHighlightEnabled());
		queryConfig.setQueryIndexingEnabled(isQueryIndexingEnabled());
		queryConfig.setQueryIndexingThreshold(getQueryIndexingThreshold());
		queryConfig.setQuerySuggestionEnabled(isQuerySuggestionEnabled());
		queryConfig.setQuerySuggestionScoresThreshold(
			getQuerySuggestionDisplayThreshold());
		queryConfig.setQuerySuggestionMax(getQuerySuggestionMax());

		_addEnabledSearchFacets(searchSettings.getSearchRequestBuilder());

		_filterByThisSite(searchSettings);
	}

	private void _filterByThisSite(SearchSettings searchSettings) {
		long groupId = getSearchScopeGroupId();

		if (groupId == 0) {
			return;
		}

		SearchContext searchContext = searchSettings.getSearchContext();

		searchContext.setGroupIds(new long[] {groupId});
	}

	private String[] _getEntryClassNames(
		String configuration, JSONFactory jsonFactory) {

		if (Validator.isNull(configuration)) {
			return null;
		}

		JSONObject configurationJSONObject;

		try {
			configurationJSONObject = jsonFactory.createJSONObject(
				configuration);
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse configuration", jsonException.getCause());
			}

			return null;
		}

		JSONArray jsonArray = configurationJSONObject.getJSONArray("facets");

		if (jsonArray == null) {
			return null;
		}

		for (int i = 0; i < (jsonArray.length() - 1); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (!Objects.equals(
					AssetEntriesSearchFacet.class.getName(),
					jsonObject.getString("id"))) {

				continue;
			}

			JSONObject dataJSONObject = jsonObject.getJSONObject("data");

			if (dataJSONObject == null) {
				continue;
			}

			JSONArray valuesJSONArray = dataJSONObject.getJSONArray("values");

			if (valuesJSONArray == null) {
				continue;
			}

			return ArrayUtil.toStringArray(valuesJSONArray);
		}

		return null;
	}

	private Facet _getFacet(
		SearchFacet searchFacet, long companyId, SearchContext searchContext) {

		try {
			searchFacet.init(
				companyId, getSearchConfiguration(), searchContext);
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return searchFacet.getFacet();
	}

	private void _resetScope(SearchContext searchContext) {
		searchContext.setGroupIds(null);

		Map<String, Serializable> attributes = searchContext.getAttributes();

		attributes.remove("groupId", "0");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchDisplayContext.class);

	private Integer _collatedSpellCheckResultDisplayThreshold;
	private Boolean _collatedSpellCheckResultEnabled;
	private Boolean _displayMainQuery;
	private Boolean _displayOpenSearchResults;
	private Boolean _dlLinkToViewURL;
	private List<SearchFacet> _enabledSearchFacets;
	private final Hits _hits;
	private Boolean _includeSystemPortlets;
	private final IndexSearchPropsValues _indexSearchPropsValues;
	private final Keywords _keywords;
	private final PortletPreferences _portletPreferences;
	private final PortletURLFactory _portletURLFactory;
	private QueryConfig _queryConfig;
	private Boolean _queryIndexingEnabled;
	private Integer _queryIndexingThreshold;
	private final String _queryString;
	private Integer _querySuggestionDisplayThreshold;
	private Boolean _querySuggestionEnabled;
	private Integer _querySuggestionMax;
	private final RenderRequest _renderRequest;
	private String _searchConfiguration;
	private final SearchContainer<Document> _searchContainer;
	private final SearchContext _searchContext;
	private final SearchContextFactory _searchContextFactory;
	private final SearchResultPreferences _searchResultPreferences;
	private String _searchScopePreferenceString;
	private final SummaryBuilderFactory _summaryBuilderFactory;
	private final ThemeDisplaySupplier _themeDisplaySupplier;
	private Boolean _useAdvancedSearchSyntax;

}
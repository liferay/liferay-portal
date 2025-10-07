/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.bar.portlet.display.context;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.search.web.internal.search.bar.portlet.configuration.SearchBarPortletInstanceConfiguration;

import java.util.Map;

/**
 * @author André de Oliveira
 */
public class SearchBarPortletDisplayContext {

	public String getCurrentSiteSearchScopeParameterString() {
		return _currentSiteSearchScopeParameterString;
	}

	public String getDestinationFriendlyURL() {
		return _destinationFriendlyURL;
	}

	public long getDisplayStyleGroupId() {
		return _displayStyleGroupId;
	}

	public String getEverythingSearchScopeParameterString() {
		return _everythingSearchScopeParameterString;
	}

	public String getInputPlaceholder() {
		return _inputPlaceholder;
	}

	public String getKeywords() {
		return _keywords;
	}

	public String getKeywordsParameterName() {
		return _keywordsParameterName;
	}

	public String getPaginationStartParameterName() {
		return _paginationStartParameterName;
	}

	public Map<String, Object> getReactData() {
		return HashMapBuilder.<String, Object>put(
			"destinationFriendlyURL", getDestinationFriendlyURL()
		).put(
			"emptySearchEnabled", isEmptySearchEnabled()
		).put(
			"initialKeywords", getKeywords()
		).put(
			"inputPlaceholder", getInputPlaceholder()
		).put(
			"isDXP", ReleaseInfo.isDXP()
		).put(
			"isSearchExperiencesSupported", isSearchExperiencesSupported()
		).put(
			"keywordsParameterName", getKeywordsParameterName()
		).put(
			"letUserChooseScope", isLetTheUserChooseTheSearchScope()
		).put(
			"paginationStartParameterName", getPaginationStartParameterName()
		).put(
			"retainFacetSelections", isRetainFacetSelections()
		).put(
			"scopeParameterName", getScopeParameterName()
		).put(
			"scopeParameterStringCurrentSite",
			getCurrentSiteSearchScopeParameterString()
		).put(
			"scopeParameterStringEverything",
			getEverythingSearchScopeParameterString()
		).put(
			"searchURL", getSearchURL()
		).put(
			"selectedEverythingSearchScope", isSelectedEverythingSearchScope()
		).put(
			"suggestionsContributorConfiguration",
			getSuggestionsContributorConfiguration()
		).put(
			"suggestionsDisplayThreshold", getSuggestionsDisplayThreshold()
		).put(
			"suggestionsURL", getSuggestionsURL()
		).build();
	}

	public String getScopeParameterName() {
		return _scopeParameterName;
	}

	public String getScopeParameterValue() {
		return _scopeParameterValue;
	}

	public SearchBarPortletInstanceConfiguration
		getSearchBarPortletInstanceConfiguration() {

		return _searchBarPortletInstanceConfiguration;
	}

	public String getSearchURL() {
		return _searchURL;
	}

	public String getSuggestionsContributorConfiguration() {
		return _suggestionsContributorConfiguration;
	}

	public int getSuggestionsDisplayThreshold() {
		return _suggestionsDisplayThreshold;
	}

	public String getSuggestionsURL() {
		return _suggestionsURL;
	}

	public boolean isAvailableEverythingSearchScope() {
		return _availableEverythingSearchScope;
	}

	public boolean isDestinationUnreachable() {
		return _destinationUnreachable;
	}

	public boolean isDisplayIncludeAttachments() {
		return _displayIncludeAttachments;
	}

	public boolean isDisplayWarningIgnoredConfiguration() {
		return _displayWarningIgnoredConfiguration;
	}

	public boolean isEmptySearchEnabled() {
		return _emptySearchEnabled;
	}

	public boolean isLetTheUserChooseTheSearchScope() {
		return _letTheUserChooseTheSearchScope;
	}

	public boolean isRenderNothing() {
		return _renderNothing;
	}

	public boolean isRetainFacetSelections() {
		return _retainFacetSelections;
	}

	public boolean isSearchExperiencesSupported() {
		return _searchExperiencesSupported;
	}

	public boolean isSelectedCurrentSiteSearchScope() {
		return _selectedCurrentSiteSearchScope;
	}

	public boolean isSelectedEverythingSearchScope() {
		return _selectedEverythingSearchScope;
	}

	public boolean isSuggestionsEnabled() {
		return _suggestionsEnabled;
	}

	public boolean isSuggestionsEndpointEnabled() {
		return _suggestionsEndpointEnabled;
	}

	public void setAvailableEverythingSearchScope(
		boolean availableEverythingSearchScope) {

		_availableEverythingSearchScope = availableEverythingSearchScope;
	}

	public void setCurrentSiteSearchScopeParameterString(
		String searchScopeCurrentSiteParameterString) {

		_currentSiteSearchScopeParameterString =
			searchScopeCurrentSiteParameterString;
	}

	public void setDestinationFriendlyURL(String destinationFriendlyURL) {
		_destinationFriendlyURL = destinationFriendlyURL;
	}

	public void setDestinationUnreachable(boolean destinationUnreachable) {
		_destinationUnreachable = destinationUnreachable;
	}

	public void setDisplayIncludeAttachments(
		boolean displayIncludeAttachments) {

		_displayIncludeAttachments = displayIncludeAttachments;
	}

	public void setDisplayStyleGroupId(long displayStyleGroupId) {
		_displayStyleGroupId = displayStyleGroupId;
	}

	public void setDisplayWarningIgnoredConfiguration(
		boolean displayWarningIgnoredConfiguration) {

		_displayWarningIgnoredConfiguration =
			displayWarningIgnoredConfiguration;
	}

	public void setEmptySearchEnabled(boolean emptySearchEnabled) {
		_emptySearchEnabled = emptySearchEnabled;
	}

	public void setEverythingSearchScopeParameterString(
		String searchScopeEverythingParameterString) {

		_everythingSearchScopeParameterString =
			searchScopeEverythingParameterString;
	}

	public void setInputPlaceholder(String inputPlaceholder) {
		_inputPlaceholder = inputPlaceholder;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setKeywordsParameterName(String keywordsParameterName) {
		_keywordsParameterName = keywordsParameterName;
	}

	public void setLetTheUserChooseTheSearchScope(
		boolean letTheUserChooseTheSearchScope) {

		_letTheUserChooseTheSearchScope = letTheUserChooseTheSearchScope;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setRenderNothing(boolean renderNothing) {
		_renderNothing = renderNothing;
	}

	public void setRetainFacetSelections(boolean retainFacetSelections) {
		_retainFacetSelections = retainFacetSelections;
	}

	public void setScopeParameterName(String scopeParameterName) {
		_scopeParameterName = scopeParameterName;
	}

	public void setScopeParameterValue(String scopeParameterValue) {
		_scopeParameterValue = scopeParameterValue;
	}

	public void setSearchBarPortletInstanceConfiguration(
		SearchBarPortletInstanceConfiguration
			searchBarPortletInstanceConfiguration) {

		_searchBarPortletInstanceConfiguration =
			searchBarPortletInstanceConfiguration;
	}

	public void setSearchExperiencesSupported(
		boolean searchExperiencesSupported) {

		_searchExperiencesSupported = searchExperiencesSupported;
	}

	public void setSearchURL(String searchURL) {
		_searchURL = searchURL;
	}

	public void setSelectedCurrentSiteSearchScope(
		boolean selectedCurrentSiteSearchScope) {

		_selectedCurrentSiteSearchScope = selectedCurrentSiteSearchScope;
	}

	public void setSelectedEverythingSearchScope(
		boolean selectedEverythingSearchScope) {

		_selectedEverythingSearchScope = selectedEverythingSearchScope;
	}

	public void setSuggestionsContributorConfiguration(
		String suggestionsContributorConfiguration) {

		_suggestionsContributorConfiguration =
			suggestionsContributorConfiguration;
	}

	public void setSuggestionsDisplayThreshold(
		int suggestionsDisplayThreshold) {

		_suggestionsDisplayThreshold = suggestionsDisplayThreshold;
	}

	public void setSuggestionsEnabled(boolean suggestionsEnabled) {
		_suggestionsEnabled = suggestionsEnabled;
	}

	public void setSuggestionsEndpointEnabled(
		boolean suggestionsEndpointEnabled) {

		_suggestionsEndpointEnabled = suggestionsEndpointEnabled;
	}

	public void setSuggestionsURL(String suggestionsURL) {
		_suggestionsURL = suggestionsURL;
	}

	private boolean _availableEverythingSearchScope;
	private String _currentSiteSearchScopeParameterString;
	private String _destinationFriendlyURL;
	private boolean _destinationUnreachable;
	private boolean _displayIncludeAttachments;
	private long _displayStyleGroupId;
	private boolean _displayWarningIgnoredConfiguration;
	private boolean _emptySearchEnabled;
	private String _everythingSearchScopeParameterString;
	private String _inputPlaceholder;
	private String _keywords;
	private String _keywordsParameterName;
	private boolean _letTheUserChooseTheSearchScope;
	private String _paginationStartParameterName;
	private boolean _renderNothing;
	private boolean _retainFacetSelections;
	private String _scopeParameterName;
	private String _scopeParameterValue;
	private SearchBarPortletInstanceConfiguration
		_searchBarPortletInstanceConfiguration;
	private boolean _searchExperiencesSupported;
	private String _searchURL;
	private boolean _selectedCurrentSiteSearchScope;
	private boolean _selectedEverythingSearchScope;
	private String _suggestionsContributorConfiguration;
	private int _suggestionsDisplayThreshold;
	private boolean _suggestionsEnabled;
	private boolean _suggestionsEndpointEnabled;
	private String _suggestionsURL;

}
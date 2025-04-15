/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.bar.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;
import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author André de Oliveira
 */
public class SearchBarPortletPreferencesImpl
	extends BasePortletPreferences implements SearchBarPortletPreferences {

	public SearchBarPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		super(portletPreferences);
	}

	@Override
	public String getDestination() {
		return getString(
			SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION,
			StringPool.BLANK);
	}

	@Override
	public String getFederatedSearchKey() {
		return getString(
			SearchBarPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public String getKeywordsParameterName() {
		return getString(
			SearchBarPortletPreferences.PREFERENCE_KEY_KEYWORDS_PARAMETER_NAME,
			"q");
	}

	@Override
	public String getScopeParameterName() {
		return getString(
			SearchBarPortletPreferences.PREFERENCE_KEY_SCOPE_PARAMETER_NAME,
			"scope");
	}

	@Override
	public SearchScopePreference getSearchScopePreference() {
		return SearchScopePreference.getSearchScopePreference(
			getString(
				SearchBarPortletPreferences.PREFERENCE_KEY_SEARCH_SCOPE,
				StringPool.BLANK));
	}

	@Override
	public String getSearchScopePreferenceString() {
		SearchScopePreference searchScopePreference =
			getSearchScopePreference();

		return searchScopePreference.getPreferenceString();
	}

	@Override
	public boolean isIncludeAttachments() {
		return getBoolean(
			SearchBarPortletPreferences.PREFERENCE_KEY_INCLUDE_ATTACHMENTS,
			false);
	}

	@Override
	public boolean isInvisible() {
		return getBoolean(
			SearchBarPortletPreferences.PREFERENCE_KEY_INVISIBLE, false);
	}

	@Override
	public boolean isShowStagedResults() {
		return getBoolean(
			SearchBarPortletPreferences.PREFERENCE_KEY_SHOW_STAGED_RESULTS,
			false);
	}

	@Override
	public boolean isSuggestionsEnabled() {
		return getBoolean(
			SearchBarPortletPreferences.PREFERENCE_KEY_SUGGESTIONS_ENABLED,
			true);
	}

	@Override
	public boolean isUseAdvancedSearchSyntax() {
		return getBoolean(
			SearchBarPortletPreferences.
				PREFERENCE_KEY_USE_ADVANCED_SEARCH_SYNTAX,
			false);
	}

}
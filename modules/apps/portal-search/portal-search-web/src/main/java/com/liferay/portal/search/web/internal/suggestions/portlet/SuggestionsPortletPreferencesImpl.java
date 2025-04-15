/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.suggestions.portlet;

import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author Adam Brandizzi
 */
public class SuggestionsPortletPreferencesImpl
	extends BasePortletPreferences implements SuggestionsPortletPreferences {

	public SuggestionsPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		super(portletPreferences);
	}

	@Override
	public int getQueryIndexingThreshold() {
		return getInteger(PREFERENCE_KEY_QUERY_INDEXING_THRESHOLD, 50);
	}

	@Override
	public int getRelatedQueriesSuggestionsDisplayThreshold() {
		return getInteger(
			PREFERENCE_KEY_RELATED_QUERIES_SUGGESTIONS_DISPLAY_THRESHOLD, 50);
	}

	@Override
	public int getRelatedQueriesSuggestionsMax() {
		return getInteger(PREFERENCE_KEY_RELATED_QUERIES_SUGGESTIONS_MAX, 10);
	}

	@Override
	public int getSpellCheckSuggestionDisplayThreshold() {
		return getInteger(
			PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_DISPLAY_THRESHOLD, 50);
	}

	@Override
	public boolean isQueryIndexingEnabled() {
		return getBoolean(PREFERENCE_KEY_QUERY_INDEXING_ENABLED, false);
	}

	@Override
	public boolean isRelatedQueriesSuggestionsEnabled() {
		return getBoolean(
			PREFERENCE_KEY_RELATED_QUERIES_SUGGESTIONS_ENABLED, false);
	}

	@Override
	public boolean isSpellCheckSuggestionEnabled() {
		return getBoolean(PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_ENABLED, false);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.suggestions.portlet.shared.search;

import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.search.web.internal.suggestions.constants.SuggestionsPortletKeys;
import com.liferay.portal.search.web.internal.suggestions.portlet.SuggestionsPortletPreferences;
import com.liferay.portal.search.web.internal.suggestions.portlet.SuggestionsPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 */
@Component(
	property = "jakarta.portlet.name=" + SuggestionsPortletKeys.SUGGESTIONS,
	service = PortletSharedSearchContributor.class
)
public class SuggestionsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SuggestionsPortletPreferences suggestionsPortletPreferences =
			new SuggestionsPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		_setUpQueryIndexing(
			suggestionsPortletPreferences, portletSharedSearchSettings);
		_setUpRelatedSuggestions(
			suggestionsPortletPreferences, portletSharedSearchSettings);
		_setUpSpellCheckSuggestion(
			suggestionsPortletPreferences, portletSharedSearchSettings);
	}

	private void _setUpQueryIndexing(
		SuggestionsPortletPreferences suggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setQueryIndexingEnabled(
			suggestionsPortletPreferences.isQueryIndexingEnabled());
		queryConfig.setQueryIndexingThreshold(
			suggestionsPortletPreferences.getQueryIndexingThreshold());
	}

	private void _setUpRelatedSuggestions(
		SuggestionsPortletPreferences suggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setQuerySuggestionEnabled(
			suggestionsPortletPreferences.isRelatedQueriesSuggestionsEnabled());
		queryConfig.setQuerySuggestionScoresThreshold(
			suggestionsPortletPreferences.
				getRelatedQueriesSuggestionsDisplayThreshold());
		queryConfig.setQuerySuggestionMax(
			suggestionsPortletPreferences.getRelatedQueriesSuggestionsMax());
	}

	private void _setUpSpellCheckSuggestion(
		SuggestionsPortletPreferences suggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setCollatedSpellCheckResultEnabled(
			suggestionsPortletPreferences.isSpellCheckSuggestionEnabled());
		queryConfig.setCollatedSpellCheckResultScoresThreshold(
			suggestionsPortletPreferences.
				getSpellCheckSuggestionDisplayThreshold());
	}

}
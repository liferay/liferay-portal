/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author André de Oliveira
 */
public class IndexSearchPropsValuesImpl implements IndexSearchPropsValues {

	@Override
	public int getCollatedSpellCheckResultScoresThreshold() {
		return PropsValues.
			INDEX_SEARCH_COLLATED_SPELL_CHECK_RESULT_SCORES_THRESHOLD;
	}

	@Override
	public int getQueryIndexingThreshold() {
		return PropsValues.INDEX_SEARCH_QUERY_INDEXING_THRESHOLD;
	}

	@Override
	public int getQuerySuggestionMax() {
		return PropsValues.INDEX_SEARCH_QUERY_SUGGESTION_MAX;
	}

	@Override
	public int getQuerySuggestionScoresThreshold() {
		return PropsValues.INDEX_SEARCH_QUERY_SUGGESTION_SCORES_THRESHOLD;
	}

	@Override
	public boolean isCollatedSpellCheckResultEnabled() {
		return PropsValues.INDEX_SEARCH_COLLATED_SPELL_CHECK_RESULT_ENABLED;
	}

	@Override
	public boolean isQueryIndexingEnabled() {
		return PropsValues.INDEX_SEARCH_QUERY_INDEXING_ENABLED;
	}

	@Override
	public boolean isQuerySuggestionEnabled() {
		return PropsValues.INDEX_SEARCH_QUERY_SUGGESTION_ENABLED;
	}

}
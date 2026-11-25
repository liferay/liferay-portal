/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.similar.results.web.internal.util.SearchStringUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class SimilarResultsPortletPreferencesImpl
	implements SimilarResultsPortletPreferences {

	public SimilarResultsPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		_portletPreferences = portletPreferences;
	}

	@Override
	public String getAnalyzer() {
		return _getStringNullable(PREFERENCE_KEY_ANALYZER);
	}

	@Override
	public String getDocType() {
		return _getStringNullable(PREFERENCE_KEY_DOC_TYPE);
	}

	@Override
	public String getFederatedSearchKey() {
		return _getString(PREFERENCE_KEY_FEDERATED_SEARCH_KEY, "morelikethis");
	}

	@Override
	public String getFields() {
		return _getStringNullable(PREFERENCE_KEY_FIELDS);
	}

	@Override
	public String getIndexName() {
		return _getStringNullable(PREFERENCE_KEY_INDEX_NAME);
	}

	@Override
	public String getLinkBehavior() {
		return _getString(PREFERENCE_KEY_LINK_BEHAVIOR, "show-content");
	}

	@Override
	public Integer getMaxDocFrequency() {
		return _getIntegerNullable(PREFERENCE_KEY_MAX_DOC_FREQUENCY);
	}

	@Override
	public Integer getMaxItemDisplay() {
		return _getInteger(PREFERENCE_KEY_MAX_ITEM_DISPLAY, 10);
	}

	@Override
	public Integer getMaxQueryTerms() {
		return _getIntegerNullable(PREFERENCE_KEY_MAX_QUERY_TERMS);
	}

	@Override
	public Integer getMaxWordLength() {
		return _getIntegerNullable(PREFERENCE_KEY_MAX_WORD_LENGTH);
	}

	@Override
	public Integer getMinDocFrequency() {
		return _getIntegerNullable(PREFERENCE_KEY_MIN_DOC_FREQUENCY);
	}

	@Override
	public String getMinShouldMatch() {
		return _getStringNullable(PREFERENCE_KEY_MIN_SHOULD_MATCH);
	}

	@Override
	public Integer getMinTermFrequency() {
		return _getIntegerNullable(PREFERENCE_KEY_MIN_TERM_FREQUENCY);
	}

	@Override
	public Integer getMinWordLength() {
		return _getIntegerNullable(PREFERENCE_KEY_MIN_WORD_LENGTH);
	}

	@Override
	public String getSearchScope() {
		return _getString(PREFERENCE_KEY_SEARCH_SCOPE, "this-site");
	}

	@Override
	public String getStopWords() {
		return _getStringNullable(PREFERENCE_KEY_STOP_WORDS);
	}

	@Override
	public Float getTermBoost() {
		String string = _getStringNullable(PREFERENCE_KEY_TERM_BOOST);

		if (string == null) {
			return null;
		}

		return GetterUtil.getFloat(string);
	}

	private int _getInteger(String key, int defaultValue) {
		return GetterUtil.getInteger(_getStringNullable(key), defaultValue);
	}

	private Integer _getIntegerNullable(String key) {
		String stringValue = _getStringNullable(key);

		if (stringValue == null) {
			return null;
		}

		return GetterUtil.getInteger(stringValue);
	}

	private String _getString(String key, String defaultValue) {
		String string = _getStringNullable(key);

		if (string == null) {
			return defaultValue;
		}

		return string;
	}

	private String _getStringNullable(String key) {
		if (_portletPreferences == null) {
			return null;
		}

		return SearchStringUtil.maybe(
			_portletPreferences.getValue(key, StringPool.BLANK));
	}

	private final PortletPreferences _portletPreferences;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.query.field;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.analysis.KeywordTokenizer;
import com.liferay.portal.search.internal.analysis.IndexAnalyzerUtil;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @author André de Oliveira
 */
public class FullTextQueryBuilder {

	public FullTextQueryBuilder(KeywordTokenizer keywordTokenizer) {
		_keywordTokenizer = keywordTokenizer;
	}

	public Query build(String field, String keywords) {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		List<String> tokens = _keywordTokenizer.tokenize(keywords);

		List<String> phrases = new ArrayList<>(tokens.size());
		List<String> words = new ArrayList<>(tokens.size());

		for (String token : tokens) {
			if (StringUtil.startsWith(token, CharPool.QUOTE)) {
				phrases.add(StringUtil.unquote(token));
			}
			else {
				words.add(token);
			}
		}

		for (String phrase : phrases) {
			booleanQuery.addMustQueryClauses(_createPhraseQuery(field, phrase));
		}

		if (!words.isEmpty()) {
			_addSentenceQueries(
				field, StringUtil.merge(words, StringPool.SPACE), booleanQuery);
		}

		booleanQuery.addShouldQueryClauses(
			_createExactMatchQuery(field, keywords));

		return booleanQuery;
	}

	public void setAutocomplete(boolean autocomplete) {
		_autocomplete = autocomplete;
	}

	public void setExactMatchBoost(float exactMatchBoost) {
		_exactMatchBoost = exactMatchBoost;
	}

	public void setMaxExpansions(int maxExpansions) {
		_maxExpansions = maxExpansions;
	}

	public void setProximitySlop(int proximitySlop) {
		_proximitySlop = proximitySlop;
	}

	private void _addSentenceQueries(
		String field, String sentence, BooleanQuery booleanQuery) {

		booleanQuery.addMustQueryClauses(
			_createMandatoryQuery(field, sentence));

		if (_proximitySlop != null) {
			booleanQuery.addShouldQueryClauses(
				_createProximityQuery(field, sentence));
		}
	}

	private Query _createAutocompleteQuery(String field, String value) {
		PhraseQueryBuilder builder = new PhraseQueryBuilder();

		builder.setMaxExpansions(_maxExpansions);
		builder.setPrefix(true);

		return builder.build(field, value);
	}

	private Query _createExactMatchQuery(String field, String keywords) {
		PhraseQueryBuilder builder = new PhraseQueryBuilder();

		builder.setBoost(_exactMatchBoost);

		return builder.build(field, keywords);
	}

	private Query _createMandatoryQuery(String field, String sentence) {
		Query matchQuery = _createMatchQuery(field, sentence);

		if (!_autocomplete) {
			return matchQuery;
		}

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addShouldQueryClauses(matchQuery);

		booleanQuery.addShouldQueryClauses(
			_createAutocompleteQuery(field, sentence));

		return booleanQuery;
	}

	private Query _createMatchQuery(String field, String value) {
		return QueriesUtil.match(field, value);
	}

	private Query _createPhraseQuery(String field, String phrase) {
		PhraseQueryBuilder builder = new PhraseQueryBuilder();

		builder.setTrailingStarAware(true);

		Query query = builder.build(field, phrase);

		String analyzer = IndexAnalyzerUtil.getIndexAnalyzer(field);

		if (analyzer == null) {
			return query;
		}

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addShouldQueryClauses(query);

		builder.setAnalyzer(analyzer);

		booleanQuery.addShouldQueryClauses(builder.build(field, phrase));

		return booleanQuery;
	}

	private Query _createProximityQuery(String field, String value) {
		PhraseQueryBuilder builder = new PhraseQueryBuilder();

		builder.setSlop(_proximitySlop);

		return builder.build(field, value);
	}

	private boolean _autocomplete;
	private float _exactMatchBoost;
	private final KeywordTokenizer _keywordTokenizer;
	private Integer _maxExpansions;
	private Integer _proximitySlop;

}
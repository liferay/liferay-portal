/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.query;

import com.liferay.petra.string.StringBundler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides support for parsing raw, human readable query syntax. No
 * transformation is made on user input.
 *
 * <p>
 * The actual query syntax and any further processing are dependent on your
 * search engine's implementation details. Consult your search provider's
 * documentation for more information.
 * </p>
 *
 * @author Michael C. Han
 */
public class SimpleStringQuery extends Query {

	public SimpleStringQuery(String query) {
		_query = query;
	}

	@Override
	public <T> T accept(QueryVisitor<T> queryVisitor) {
		return queryVisitor.visit(this);
	}

	public void addField(String field, float boost) {
		_fieldBoostMap.put(field, boost);
	}

	public void addFields(String... fields) {
		for (String field : fields) {
			_fieldBoostMap.put(field, null);
		}
	}

	public Boolean getAnalyzeWildcard() {
		return _analyzeWildcard;
	}

	public String getAnalyzer() {
		return _analyzer;
	}

	public Boolean getAutoGenerateSynonymsPhraseQuery() {
		return _autoGenerateSynonymsPhraseQuery;
	}

	public Operator getDefaultOperator() {
		return _defaultOperator;
	}

	public Map<String, Float> getFieldBoostMap() {
		return Collections.unmodifiableMap(_fieldBoostMap);
	}

	public Integer getFuzzyMaxExpansions() {
		return _fuzzyMaxExpansions;
	}

	public Integer getFuzzyPrefixLength() {
		return _fuzzyPrefixLength;
	}

	public Boolean getFuzzyTranspositions() {
		return _fuzzyTranspositions;
	}

	public Boolean getLenient() {
		return _lenient;
	}

	public String getQuery() {
		return _query;
	}

	public String getQuoteFieldSuffix() {
		return _quoteFieldSuffix;
	}

	public void setAnalyzeWildcard(Boolean analyzeWildcard) {
		_analyzeWildcard = analyzeWildcard;
	}

	public void setAnalyzer(String analyzer) {
		_analyzer = analyzer;
	}

	public void setAutoGenerateSynonymsPhraseQuery(
		Boolean autoGenerateSynonymsPhraseQuery) {

		_autoGenerateSynonymsPhraseQuery = autoGenerateSynonymsPhraseQuery;
	}

	public void setDefaultOperator(Operator defaultOperator) {
		_defaultOperator = defaultOperator;
	}

	public void setFuzzyMaxExpansions(Integer fuzzyMaxExpansions) {
		_fuzzyMaxExpansions = fuzzyMaxExpansions;
	}

	public void setFuzzyPrefixLength(Integer fuzzyPrefixLength) {
		_fuzzyPrefixLength = fuzzyPrefixLength;
	}

	public void setFuzzyTranspositions(Boolean fuzzyTranspositions) {
		_fuzzyTranspositions = fuzzyTranspositions;
	}

	public void setLenient(Boolean lenient) {
		_lenient = lenient;
	}

	public void setQuoteFieldSuffix(String quoteFieldSuffix) {
		_quoteFieldSuffix = quoteFieldSuffix;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{className=");

		Class<?> clazz = getClass();

		sb.append(clazz.getSimpleName());

		sb.append(", query=");
		sb.append(_query);
		sb.append("}");

		return sb.toString();
	}

	private static final long serialVersionUID = 1L;

	private Boolean _analyzeWildcard;
	private String _analyzer;
	private Boolean _autoGenerateSynonymsPhraseQuery;
	private Operator _defaultOperator;
	private final Map<String, Float> _fieldBoostMap = new HashMap<>();
	private Integer _fuzzyMaxExpansions;
	private Integer _fuzzyPrefixLength;
	private Boolean _fuzzyTranspositions;
	private Boolean _lenient;
	private final String _query;
	private String _quoteFieldSuffix;

}
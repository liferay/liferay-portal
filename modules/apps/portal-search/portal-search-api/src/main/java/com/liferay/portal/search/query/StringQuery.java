/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.query;

import com.liferay.petra.string.StringBundler;

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
 * @author Bruno Farache
 * @author Petteri Karttunen
 */
public class StringQuery extends Query {

	public StringQuery(String query) {
		_query = query;
	}

	@Override
	public <T> T accept(QueryVisitor<T> queryVisitor) {
		return queryVisitor.visit(this);
	}

	public void addField(String field) {
		_fieldsBoosts.put(field, null);
	}

	public void addField(String field, Float boost) {
		_fieldsBoosts.put(field, boost);
	}

	public Boolean getAllowLeadingWildcard() {
		return _allowLeadingWildcard;
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

	public String getDefaultField() {
		return _defaultField;
	}

	public Operator getDefaultOperator() {
		return _defaultOperator;
	}

	public Boolean getEnablePositionIncrements() {
		return _enablePositionIncrements;
	}

	public Boolean getEscape() {
		return _escape;
	}

	public Map<String, Float> getFieldsBoosts() {
		return _fieldsBoosts;
	}

	public Float getFuzziness() {
		return _fuzziness;
	}

	public Integer getFuzzyMaxExpansions() {
		return _fuzzyMaxExpansions;
	}

	public Integer getFuzzyPrefixLength() {
		return _fuzzyPrefixLength;
	}

	public String getFuzzyRewrite() {
		return _fuzzyRewrite;
	}

	public Boolean getFuzzyTranspositions() {
		return _fuzzyTranspositions;
	}

	public Boolean getLenient() {
		return _lenient;
	}

	public Integer getMaxDeterminedStates() {
		return _maxDeterminedStates;
	}

	public String getMinimumShouldMatch() {
		return _minimumShouldMatch;
	}

	public Integer getPhraseSlop() {
		return _phraseSlop;
	}

	public String getQuery() {
		return _query;
	}

	public String getQuoteAnalyzer() {
		return _quoteAnalyzer;
	}

	public String getQuoteFieldSuffix() {
		return _quoteFieldSuffix;
	}

	public String getRewrite() {
		return _rewrite;
	}

	public Float getTieBreaker() {
		return _tieBreaker;
	}

	public String getTimeZone() {
		return _timeZone;
	}

	public void setAllowLeadingWildcard(Boolean allowLeadingWildcard) {
		_allowLeadingWildcard = allowLeadingWildcard;
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

	public void setDefaultField(String defaultField) {
		_defaultField = defaultField;
	}

	public void setDefaultOperator(Operator defaultOperator) {
		_defaultOperator = defaultOperator;
	}

	public void setEnablePositionIncrements(Boolean enablePositionIncrements) {
		_enablePositionIncrements = enablePositionIncrements;
	}

	public void setEscape(boolean escape) {
		_escape = escape;
	}

	public void setFuzziness(Float fuzziness) {
		_fuzziness = fuzziness;
	}

	public void setFuzzyMaxExpansions(Integer fuzzyMaxExpansions) {
		_fuzzyMaxExpansions = fuzzyMaxExpansions;
	}

	public void setFuzzyPrefixLength(Integer fuzzyPrefixLength) {
		_fuzzyPrefixLength = fuzzyPrefixLength;
	}

	public void setFuzzyRewrite(String fuzzyRewrite) {
		_fuzzyRewrite = fuzzyRewrite;
	}

	public void setFuzzyTranspositions(Boolean fuzzyTranspositions) {
		_fuzzyTranspositions = fuzzyTranspositions;
	}

	public void setLenient(Boolean lenient) {
		_lenient = lenient;
	}

	public void setMaxDeterminedStates(Integer maxDeterminedStates) {
		_maxDeterminedStates = maxDeterminedStates;
	}

	public void setMinimumShouldMatch(String minimumShouldMatch) {
		_minimumShouldMatch = minimumShouldMatch;
	}

	public void setPhraseSlop(Integer phraseSlop) {
		_phraseSlop = phraseSlop;
	}

	public void setQuoteAnalyzer(String quoteAnalyzer) {
		_quoteAnalyzer = quoteAnalyzer;
	}

	public void setQuoteFieldSuffix(String quoteFieldSuffix) {
		_quoteFieldSuffix = quoteFieldSuffix;
	}

	public void setRewrite(String rewrite) {
		_rewrite = rewrite;
	}

	public void setTieBreaker(float tieBreaker) {
		_tieBreaker = tieBreaker;
	}

	public void setTimeZone(String timeZone) {
		_timeZone = timeZone;
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

	private Boolean _allowLeadingWildcard;
	private Boolean _analyzeWildcard;
	private String _analyzer;
	private Boolean _autoGenerateSynonymsPhraseQuery;
	private String _defaultField;
	private Operator _defaultOperator;
	private Boolean _enablePositionIncrements;
	private Boolean _escape;
	private final Map<String, Float> _fieldsBoosts = new HashMap<>();
	private Float _fuzziness;
	private Integer _fuzzyMaxExpansions;
	private Integer _fuzzyPrefixLength;
	private String _fuzzyRewrite;
	private Boolean _fuzzyTranspositions;
	private Boolean _lenient;
	private Integer _maxDeterminedStates;
	private String _minimumShouldMatch;
	private Integer _phraseSlop;
	private final String _query;
	private String _quoteAnalyzer;
	private String _quoteFieldSuffix;
	private String _rewrite;
	private Float _tieBreaker;
	private String _timeZone;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.query;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.DisMaxQuery;
import com.liferay.portal.kernel.search.generic.FuzzyQuery;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.MoreLikeThisQuery;
import com.liferay.portal.kernel.search.generic.MultiMatchQuery;
import com.liferay.portal.kernel.search.generic.NestedQuery;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.search.query.QueryVisitor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.solr8.internal.filter.FilterTranslator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.client.solrj.util.ClientUtils;

/**
 * @author João Victor Alves
 */
public abstract class BaseQueryVisitor implements QueryVisitor<Query> {

	@Override
	public Query visitQuery(BooleanQuery booleanQuery) {
		org.apache.lucene.search.BooleanQuery.Builder booleanQueryBuilder =
			new org.apache.lucene.search.BooleanQuery.Builder();

		List
			<com.liferay.portal.kernel.search.BooleanClause
				<com.liferay.portal.kernel.search.Query>> clauses =
					booleanQuery.clauses();

		for (com.liferay.portal.kernel.search.BooleanClause
				<com.liferay.portal.kernel.search.Query> booleanClause :
					clauses) {

			Query query = _translate(booleanClause.getClause(), this);

			if (query != null) {
				booleanQueryBuilder.add(
					query, _translate(booleanClause.getBooleanClauseOccur()));
			}
		}

		BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

		if (booleanFilter == null) {
			return _addBoost(booleanQuery, booleanQueryBuilder.build());
		}

		org.apache.lucene.search.BooleanQuery.Builder
			wrapperBooleanQueryBuilder =
				new org.apache.lucene.search.BooleanQuery.Builder();

		if (!clauses.isEmpty()) {
			wrapperBooleanQueryBuilder.add(
				booleanQueryBuilder.build(), BooleanClause.Occur.MUST);
		}

		FilterTranslator<Query> filterTranslator =
			_filterTranslatorSnapshot.get();

		wrapperBooleanQueryBuilder.add(
			filterTranslator.translate(booleanFilter),
			BooleanClause.Occur.MUST);

		return _addBoost(booleanQuery, wrapperBooleanQueryBuilder.build());
	}

	@Override
	public Query visitQuery(DisMaxQuery disMaxQuery) {
		Collection<Query> queries = new HashSet<>();

		for (com.liferay.portal.kernel.search.Query query :
				disMaxQuery.getQueries()) {

			queries.add(query.accept(this));
		}

		Query query = new DisjunctionMaxQuery(
			queries, GetterUtil.getFloat(disMaxQuery.getTieBreaker()));

		if (!disMaxQuery.isDefaultBoost()) {
			return new BoostQuery(query, disMaxQuery.getBoost());
		}

		return query;
	}

	@Override
	public Query visitQuery(FuzzyQuery fuzzyQuery) {
		Term term = new Term(fuzzyQuery.getField(), fuzzyQuery.getValue());

		int maxEdits = GetterUtil.getInteger(fuzzyQuery.getMaxEdits());
		int prefixLength = GetterUtil.getInteger(fuzzyQuery.getPrefixLength());
		int maxExpansions = GetterUtil.getInteger(
			fuzzyQuery.getMaxExpansions(), 50);

		if (!fuzzyQuery.isDefaultBoost()) {
			fuzzyQuery.setBoost(fuzzyQuery.getBoost());
		}

		return new org.apache.lucene.search.FuzzyQuery(
			term, maxEdits, prefixLength, maxExpansions, false);
	}

	@Override
	public Query visitQuery(MatchAllQuery matchAllQuery) {
		Query query = new MatchAllDocsQuery();

		if (!matchAllQuery.isDefaultBoost()) {
			return new BoostQuery(query, matchAllQuery.getBoost());
		}

		return query;
	}

	@Override
	public Query visitQuery(MatchQuery matchQuery) {
		Query query = _translateMatchQuery(matchQuery);

		if (!matchQuery.isDefaultBoost()) {
			return new BoostQuery(query, matchQuery.getBoost());
		}

		return query;
	}

	@Override
	public Query visitQuery(MoreLikeThisQuery moreLikeThisQuery) {
		List<String> fields = moreLikeThisQuery.getFields();

		org.apache.lucene.queries.mlt.MoreLikeThisQuery
			luceneMoreLikeThisQuery =
				new org.apache.lucene.queries.mlt.MoreLikeThisQuery(
					moreLikeThisQuery.getLikeText(),
					fields.toArray(new String[0]), new KeywordAnalyzer(),
					fields.get(0));

		if (moreLikeThisQuery.getMaxQueryTerms() != null) {
			luceneMoreLikeThisQuery.setMaxQueryTerms(
				moreLikeThisQuery.getMaxQueryTerms());
		}

		if (moreLikeThisQuery.getMinDocFrequency() != null) {
			luceneMoreLikeThisQuery.setMinDocFreq(
				moreLikeThisQuery.getMinDocFrequency());
		}

		if (moreLikeThisQuery.getMinTermFrequency() != null) {
			luceneMoreLikeThisQuery.setMinTermFrequency(
				moreLikeThisQuery.getMinTermFrequency());
		}

		Set<String> stopWords = moreLikeThisQuery.getStopWords();

		if (!stopWords.isEmpty()) {
			luceneMoreLikeThisQuery.setStopWords(stopWords);
		}

		if (!moreLikeThisQuery.isDefaultBoost()) {
			return new BoostQuery(
				luceneMoreLikeThisQuery, moreLikeThisQuery.getBoost());
		}

		return luceneMoreLikeThisQuery;
	}

	@Override
	public Query visitQuery(MultiMatchQuery multiMatchQuery) {
		org.apache.lucene.search.BooleanQuery.Builder builder =
			new org.apache.lucene.search.BooleanQuery.Builder();

		for (String field : multiMatchQuery.getFields()) {
			builder.add(
				_translate(field, multiMatchQuery), BooleanClause.Occur.SHOULD);
		}

		return builder.build();
	}

	@Override
	public Query visitQuery(NestedQuery nestedQuery) {
		throw new UnsupportedOperationException(
			"Nested query not supported in Solr");
	}

	@Override
	public Query visitQuery(StringQuery stringQuery) {
		return new Query() {

			@Override
			public boolean equals(Object object) {
				String query = stringQuery.getQuery();

				return query.equals(object);
			}

			@Override
			public int hashCode() {
				return Objects.hashCode(stringQuery.getQuery());
			}

			@Override
			public String toString(String field) {
				return stringQuery.getQuery();
			}

		};
	}

	@Override
	public Query visitQuery(TermQuery termQuery) {
		QueryTerm queryTerm = termQuery.getQueryTerm();

		String value = queryTerm.getValue();

		if (value.isEmpty()) {
			value = StringPool.DOUBLE_APOSTROPHE;
		}

		Query query = new org.apache.lucene.search.TermQuery(
			new Term(
				_escapeTermQuery(queryTerm.getField()),
				ClientUtils.escapeQueryChars(value)));

		if (!termQuery.isDefaultBoost()) {
			return new BoostQuery(query, termQuery.getBoost());
		}

		return query;
	}

	@Override
	public Query visitQuery(TermRangeQuery termRangeQuery) {
		Query query = org.apache.lucene.search.TermRangeQuery.newStringRange(
			termRangeQuery.getField(), termRangeQuery.getLowerTerm(),
			termRangeQuery.getUpperTerm(), termRangeQuery.includesLower(),
			termRangeQuery.includesUpper());

		if (!termRangeQuery.isDefaultBoost()) {
			return new BoostQuery(query, termRangeQuery.getBoost());
		}

		return query;
	}

	@Override
	public Query visitQuery(WildcardQuery wildcardQuery) {
		QueryTerm queryTerm = wildcardQuery.getQueryTerm();

		Query query = new org.apache.lucene.search.WildcardQuery(
			new Term(
				_escapeSpaces(queryTerm.getField()),
				_escapeWildcardQuery(queryTerm.getValue())));

		if (!wildcardQuery.isDefaultBoost()) {
			return new BoostQuery(query, wildcardQuery.getBoost());
		}

		return query;
	}

	private Query _addBoost(BooleanQuery booleanQuery, Query query) {
		if (!booleanQuery.isDefaultBoost()) {
			return new BoostQuery(query, booleanQuery.getBoost());
		}

		return query;
	}

	private String _defuseUpperCaseLuceneBooleanOperators(String value) {
		value = StringUtil.replace(value, "AND", "and");
		value = StringUtil.replace(value, "OR", "or");
		value = StringUtil.replace(value, "NOT", "not");

		return value;
	}

	private String _encloseMultiword(String value, String open, String close) {
		if (value.indexOf(CharPool.SPACE) == -1) {
			return value;
		}

		return open + value + close;
	}

	private String _escapeSpaces(String value) {
		return StringUtil.replace(
			value, CharPool.SPACE, StringPool.BACK_SLASH + StringPool.SPACE);
	}

	private String _escapeTermQuery(String value) {
		return StringUtil.replace(
			value, CharPool.SPACE, StringPool.BACK_SLASH + StringPool.SPACE);
	}

	private String _escapeWildcardQuery(String value) {
		int x = 0;
		int y = 0;

		int length = value.length();

		StringBuilder sb = new StringBuilder(length * 2);

		while (y < length) {
			char c = value.charAt(y);

			if ((c == CharPool.QUESTION) || (c == CharPool.SPACE) ||
				(c == CharPool.STAR)) {

				sb.append(QueryParser.escape(value.substring(x, y)));

				if (c == CharPool.SPACE) {
					sb.append(CharPool.BACK_SLASH);
				}

				sb.append(c);

				x = y + 1;
			}

			y++;
		}

		sb.append(QueryParser.escape(value.substring(x)));

		return sb.toString();
	}

	private BooleanClause.Occur _translate(
		BooleanClauseOccur booleanClauseOccur) {

		if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
			return BooleanClause.Occur.MUST;
		}
		else if (booleanClauseOccur.equals(BooleanClauseOccur.MUST_NOT)) {
			return BooleanClause.Occur.MUST_NOT;
		}
		else if (booleanClauseOccur.equals(BooleanClauseOccur.SHOULD)) {
			return BooleanClause.Occur.SHOULD;
		}

		throw new IllegalArgumentException();
	}

	private Query _translate(
		com.liferay.portal.kernel.search.Query query,
		QueryVisitor<Query> queryVisitor) {

		return query.accept(queryVisitor);
	}

	private Query _translate(String field, MultiMatchQuery multiMatchQuery) {
		Query query = _translate(
			field, multiMatchQuery.getType(),
			ClientUtils.escapeQueryChars(multiMatchQuery.getValue()),
			multiMatchQuery.getSlop());

		Map<String, Float> boostMap = multiMatchQuery.getFieldsBoosts();

		Float boost = boostMap.get(field);

		if (boost != null) {
			return new BoostQuery(query, boost);
		}

		return query;
	}

	private Query _translate(
		String field, MultiMatchQuery.Type type, String value, Integer slop) {

		if (type == MultiMatchQuery.Type.PHRASE) {
			if (slop == null) {
				return new PhraseQuery(field, value);
			}

			return new PhraseQuery(slop, field, value);
		}
		else if (type == MultiMatchQuery.Type.PHRASE_PREFIX) {
			return new PrefixQuery(new Term(field, value));
		}

		return new org.apache.lucene.search.TermQuery(new Term(field, value));
	}

	private Query _translateMatchQuery(MatchQuery matchQuery) {
		String field = matchQuery.getField();

		MatchQuery.Type matchQueryType = matchQuery.getType();
		String value = matchQuery.getValue();

		if (value.startsWith(StringPool.QUOTE) &&
			value.endsWith(StringPool.QUOTE)) {

			matchQueryType = MatchQuery.Type.PHRASE;

			value = StringUtil.unquote(value);

			if (value.endsWith(StringPool.STAR)) {
				matchQueryType = MatchQuery.Type.PHRASE_PREFIX;
			}
		}

		value = _trimStars(value);

		value = QueryParser.escape(value);

		value = _defuseUpperCaseLuceneBooleanOperators(value);

		if (matchQueryType == null) {
			matchQueryType = MatchQuery.Type.BOOLEAN;
		}

		if (matchQueryType == MatchQuery.Type.BOOLEAN) {
			return _translateQueryTypeBoolean(field, value);
		}
		else if (matchQueryType == MatchQuery.Type.PHRASE) {
			return _translateQueryTypePhrase(
				field, value, matchQuery.getSlop());
		}
		else if (matchQueryType == MatchQuery.Type.PHRASE_PREFIX) {
			return _translateQueryTypePhrasePrefix(field, value);
		}

		throw new IllegalArgumentException(
			"Invalid match query type: " + matchQueryType);
	}

	private Query _translateQueryTypeBoolean(String field, String value) {
		value = _encloseMultiword(
			value, StringPool.OPEN_PARENTHESIS, StringPool.CLOSE_PARENTHESIS);

		return new org.apache.lucene.search.TermQuery(new Term(field, value));
	}

	private Query _translateQueryTypePhrase(
		String field, String value, Integer slop) {

		PhraseQuery.Builder builder = new PhraseQuery.Builder();

		builder.add(new Term(field, value));

		if (slop != null) {
			builder.setSlop(slop);
		}

		return builder.build();
	}

	private Query _translateQueryTypePhrasePrefix(String field, String value) {
		value = value.concat(StringPool.STAR);

		value = _encloseMultiword(
			value, StringPool.OPEN_PARENTHESIS + StringPool.PLUS,
			StringPool.CLOSE_PARENTHESIS);

		return new org.apache.lucene.search.TermQuery(new Term(field, value));
	}

	private String _trimStars(String value) {
		if (value.equals(StringPool.STAR)) {
			return value;
		}

		if (value.startsWith(StringPool.STAR)) {
			value = value.substring(1);
		}

		if (value.endsWith(StringPool.STAR)) {
			value = value.substring(0, value.length() - 1);
		}

		return value;
	}

	private static final Snapshot<FilterTranslator<Query>>
		_filterTranslatorSnapshot = new Snapshot<>(
			BaseQueryVisitor.class, Snapshot.cast(FilterTranslator.class),
			"(search.engine.impl=Solr)", true);

}
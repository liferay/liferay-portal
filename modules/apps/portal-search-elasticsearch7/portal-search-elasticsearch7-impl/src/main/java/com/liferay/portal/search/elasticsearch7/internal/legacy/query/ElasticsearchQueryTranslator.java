/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.legacy.query;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.search.generic.DisMaxQuery;
import com.liferay.portal.kernel.search.generic.FuzzyQuery;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.MoreLikeThisQuery;
import com.liferay.portal.kernel.search.generic.MultiMatchQuery;
import com.liferay.portal.kernel.search.generic.NestedQuery;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.search.query.QueryVisitor;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.util.DocumentTypes;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.query.ZeroTermsQueryOption;

/**
 * @author André de Oliveira
 * @author Miguel Angelo Caldas Gallindo
 */
public class ElasticsearchQueryTranslator
	implements QueryTranslator<QueryBuilder>, QueryVisitor<QueryBuilder> {

	public ElasticsearchQueryTranslator(IndexNameBuilder indexNameBuilder) {
		_indexNameBuilder = indexNameBuilder;
	}

	@Override
	public QueryBuilder translate(Query query, SearchContext searchContext) {
		QueryBuilder queryBuilder = query.accept(this);

		if (queryBuilder == null) {
			queryBuilder = QueryBuilders.queryStringQuery(query.toString());
		}

		return queryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(BooleanQuery booleanQuery) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		List<BooleanClause<Query>> clauses = booleanQuery.clauses();

		for (BooleanClause<Query> clause : clauses) {
			_addClause(clause, boolQueryBuilder, this);
		}

		if (!booleanQuery.isDefaultBoost()) {
			boolQueryBuilder.boost(booleanQuery.getBoost());
		}

		BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

		if (booleanFilter == null) {
			return boolQueryBuilder;
		}

		// LPS-86537 The following conversion is present for backwards
		// compatibility with how Liferay's Indexer frameworks handles queries.
		// Ideally, we do not wrap the BooleanQuery with another BooleanQuery.

		BoolQueryBuilder wrapperBoolQueryBuilder = QueryBuilders.boolQuery();

		if (!clauses.isEmpty()) {
			wrapperBoolQueryBuilder.must(boolQueryBuilder);
		}

		FilterTranslator<QueryBuilder> filterTranslator =
			_filterTranslatorSnapshot.get();

		if (filterTranslator == null) {
			_log.error(
				"Unable to translate boolean filter " + booleanFilter +
					" because filter translator is null");

			return boolQueryBuilder;
		}

		QueryBuilder filterQueryBuilder = filterTranslator.translate(
			booleanFilter, null);

		wrapperBoolQueryBuilder.filter(filterQueryBuilder);

		return wrapperBoolQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(DisMaxQuery disMaxQuery) {
		DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();

		if (!disMaxQuery.isDefaultBoost()) {
			disMaxQueryBuilder.boost(disMaxQuery.getBoost());
		}

		for (Query query : disMaxQuery.getQueries()) {
			QueryBuilder queryBuilder = query.accept(this);

			disMaxQueryBuilder.add(queryBuilder);
		}

		if (disMaxQuery.getTieBreaker() != null) {
			disMaxQueryBuilder.tieBreaker(disMaxQuery.getTieBreaker());
		}

		return disMaxQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(FuzzyQuery fuzzyQuery) {
		FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery(
			fuzzyQuery.getField(), fuzzyQuery.getValue());

		if (fuzzyQuery.getFuzziness() != null) {
			fuzzyQueryBuilder.fuzziness(
				Fuzziness.build(fuzzyQuery.getFuzziness()));
		}

		if (fuzzyQuery.getMaxExpansions() != null) {
			fuzzyQueryBuilder.maxExpansions(fuzzyQuery.getMaxExpansions());
		}

		if (fuzzyQuery.getPrefixLength() != null) {
			fuzzyQueryBuilder.prefixLength(fuzzyQuery.getPrefixLength());
		}

		if (!fuzzyQuery.isDefaultBoost()) {
			fuzzyQueryBuilder.boost(fuzzyQuery.getBoost());
		}

		return fuzzyQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(MatchAllQuery matchAllQuery) {
		MatchAllQueryBuilder matchAllQueryBuilder =
			QueryBuilders.matchAllQuery();

		if (!matchAllQuery.isDefaultBoost()) {
			matchAllQueryBuilder.boost(matchAllQuery.getBoost());
		}

		return matchAllQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(MatchQuery matchQuery) {
		String field = matchQuery.getField();

		MatchQuery.Type type = matchQuery.getType();
		String value = matchQuery.getValue();

		if (value.startsWith(StringPool.QUOTE) &&
			value.endsWith(StringPool.QUOTE)) {

			type = MatchQuery.Type.PHRASE;

			value = StringUtil.unquote(value);

			if (value.endsWith(StringPool.STAR)) {
				type = MatchQuery.Type.PHRASE_PREFIX;
			}
		}

		if ((type == null) || (type == MatchQuery.Type.BOOLEAN)) {
			return _translateMatchQuery(field, value, matchQuery);
		}
		else if (type == MatchQuery.Type.PHRASE) {
			return _translateMatchPhraseQuery(field, value, matchQuery);
		}
		else if (type == MatchQuery.Type.PHRASE_PREFIX) {
			return _translateMatchPhrasePrefixQuery(field, value, matchQuery);
		}

		throw new IllegalArgumentException("Invalid match query type: " + type);
	}

	@Override
	public QueryBuilder visitQuery(MoreLikeThisQuery moreLikeThisQuery) {
		List<MoreLikeThisQueryBuilder.Item> likeItems = new ArrayList<>();

		if (moreLikeThisQuery.getDocumentUIDs() != null) {
			String type = moreLikeThisQuery.getType();

			if (Validator.isNotNull(type)) {
				type = DocumentTypes.LIFERAY;
			}

			for (String documentUID : moreLikeThisQuery.getDocumentUIDs()) {
				MoreLikeThisQueryBuilder.Item moreLikeThisQueryBuilderItem =
					new MoreLikeThisQueryBuilder.Item(
						_indexNameBuilder.getIndexName(
							moreLikeThisQuery.getCompanyId()),
						type, documentUID);

				likeItems.add(moreLikeThisQueryBuilderItem);
			}
		}

		List<String> fields = moreLikeThisQuery.getFields();

		String[] fieldsArray = null;

		if (!fields.isEmpty()) {
			fieldsArray = fields.toArray(new String[0]);
		}

		List<String> likeTexts = new ArrayList<>();

		if (Validator.isNotNull(moreLikeThisQuery.getLikeText())) {
			likeTexts.add(moreLikeThisQuery.getLikeText());
		}

		MoreLikeThisQueryBuilder moreLikeThisQueryBuilder =
			QueryBuilders.moreLikeThisQuery(
				fieldsArray, likeTexts.toArray(new String[0]),
				likeItems.toArray(new MoreLikeThisQueryBuilder.Item[0]));

		if (Validator.isNotNull(moreLikeThisQuery.getAnalyzer())) {
			moreLikeThisQueryBuilder.analyzer(moreLikeThisQuery.getAnalyzer());
		}

		if (moreLikeThisQuery.getMaxDocFrequency() != null) {
			moreLikeThisQueryBuilder.maxDocFreq(
				moreLikeThisQuery.getMaxDocFrequency());
		}

		if (moreLikeThisQuery.getMaxQueryTerms() != null) {
			moreLikeThisQueryBuilder.maxQueryTerms(
				moreLikeThisQuery.getMaxQueryTerms());
		}

		if (moreLikeThisQuery.getMaxWordLength() != null) {
			moreLikeThisQueryBuilder.maxWordLength(
				moreLikeThisQuery.getMaxWordLength());
		}

		if (moreLikeThisQuery.getMinDocFrequency() != null) {
			moreLikeThisQueryBuilder.minDocFreq(
				moreLikeThisQuery.getMinDocFrequency());
		}

		if (Validator.isNotNull(moreLikeThisQuery.getMinShouldMatch())) {
			moreLikeThisQueryBuilder.minimumShouldMatch(
				moreLikeThisQuery.getMinShouldMatch());
		}

		if (moreLikeThisQuery.getMinTermFrequency() != null) {
			moreLikeThisQueryBuilder.minTermFreq(
				moreLikeThisQuery.getMinTermFrequency());
		}

		if (moreLikeThisQuery.getMinWordLength() != null) {
			moreLikeThisQueryBuilder.minWordLength(
				moreLikeThisQuery.getMinWordLength());
		}

		if (!moreLikeThisQuery.isDefaultBoost()) {
			moreLikeThisQueryBuilder.boost(moreLikeThisQuery.getBoost());
		}

		Collection<String> stopWords = moreLikeThisQuery.getStopWords();

		if (!stopWords.isEmpty()) {
			moreLikeThisQueryBuilder.stopWords(
				stopWords.toArray(new String[0]));
		}

		if (moreLikeThisQuery.getTermBoost() != null) {
			moreLikeThisQueryBuilder.boostTerms(
				moreLikeThisQuery.getTermBoost());
		}

		if (moreLikeThisQuery.isIncludeInput() != null) {
			moreLikeThisQueryBuilder.include(
				moreLikeThisQuery.isIncludeInput());
		}

		return moreLikeThisQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(MultiMatchQuery multiMatchQuery) {
		MultiMatchQueryBuilder multiMatchQueryBuilder =
			QueryBuilders.multiMatchQuery(
				multiMatchQuery.getValue(), StringPool.EMPTY_ARRAY);

		if (Validator.isNotNull(multiMatchQuery.getAnalyzer())) {
			multiMatchQueryBuilder.analyzer(multiMatchQuery.getAnalyzer());
		}

		if (multiMatchQuery.getCutOffFrequency() != null) {
			multiMatchQueryBuilder.cutoffFrequency(
				multiMatchQuery.getCutOffFrequency());
		}

		Map<String, Float> fieldsBoosts = multiMatchQuery.getFieldsBoosts();

		for (String fields : multiMatchQuery.getFields()) {
			Float fieldBoost = null;

			if (fieldsBoosts != null) {
				fieldBoost = fieldsBoosts.get(fields);
			}

			if (fieldBoost != null) {
				multiMatchQueryBuilder.field(fields, fieldBoost);
			}
			else {
				multiMatchQueryBuilder.field(fields);
			}
		}

		if (multiMatchQuery.getFuzziness() != null) {
			multiMatchQueryBuilder.fuzziness(
				Fuzziness.build(multiMatchQuery.getFuzziness()));
		}

		if (multiMatchQuery.getFuzzyRewriteMethod() != null) {
			String multiMatchQueryFuzzyRewriteMethod =
				MatchQueryTranslatorUtil.translate(
					multiMatchQuery.getFuzzyRewriteMethod());

			multiMatchQueryBuilder.fuzzyRewrite(
				multiMatchQueryFuzzyRewriteMethod);
		}

		if (multiMatchQuery.getMaxExpansions() != null) {
			multiMatchQueryBuilder.maxExpansions(
				multiMatchQuery.getMaxExpansions());
		}

		if (Validator.isNotNull(multiMatchQuery.getMinShouldMatch())) {
			multiMatchQueryBuilder.minimumShouldMatch(
				multiMatchQuery.getMinShouldMatch());
		}

		if (multiMatchQuery.getOperator() != null) {
			Operator matchQueryBuilderOperator =
				MatchQueryTranslatorUtil.translate(
					multiMatchQuery.getOperator());

			multiMatchQueryBuilder.operator(matchQueryBuilderOperator);
		}

		if (multiMatchQuery.getPrefixLength() != null) {
			multiMatchQueryBuilder.prefixLength(
				multiMatchQuery.getPrefixLength());
		}

		if (multiMatchQuery.getSlop() != null) {
			multiMatchQueryBuilder.slop(multiMatchQuery.getSlop());
		}

		if (multiMatchQuery.getType() != null) {
			MultiMatchQueryBuilder.Type multiMatchQueryBuilderType = _translate(
				multiMatchQuery.getType());

			multiMatchQueryBuilder.type(multiMatchQueryBuilderType);
		}

		if (multiMatchQuery.getZeroTermsQuery() != null) {
			ZeroTermsQueryOption zeroTermsQueryOption =
				MatchQueryTranslatorUtil.translate(
					multiMatchQuery.getZeroTermsQuery());

			multiMatchQueryBuilder.zeroTermsQuery(zeroTermsQueryOption);
		}

		if (!multiMatchQuery.isDefaultBoost()) {
			multiMatchQueryBuilder.boost(multiMatchQuery.getBoost());
		}

		if (multiMatchQuery.isLenient() != null) {
			multiMatchQueryBuilder.lenient(multiMatchQuery.isLenient());
		}

		return multiMatchQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(NestedQuery nestedQuery) {
		Query query = nestedQuery.getQuery();

		QueryBuilder queryBuilder = query.accept(this);

		NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(
			nestedQuery.getPath(), queryBuilder, ScoreMode.Total);

		if (!nestedQuery.isDefaultBoost()) {
			nestedQueryBuilder.boost(nestedQuery.getBoost());
		}

		return nestedQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(StringQuery stringQuery) {
		QueryStringQueryBuilder queryStringQueryBuilder =
			QueryBuilders.queryStringQuery(stringQuery.getQuery());

		if (!stringQuery.isDefaultBoost()) {
			queryStringQueryBuilder.boost(stringQuery.getBoost());
		}

		return queryStringQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(TermQuery termQuery) {
		QueryTerm queryTerm = termQuery.getQueryTerm();

		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(
			queryTerm.getField(), queryTerm.getValue());

		if (!termQuery.isDefaultBoost()) {
			termQueryBuilder.boost(termQuery.getBoost());
		}

		return termQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(TermRangeQuery termRangeQuery) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			termRangeQuery.getField());

		rangeQueryBuilder.from(termRangeQuery.getLowerTerm());
		rangeQueryBuilder.includeLower(termRangeQuery.includesLower());
		rangeQueryBuilder.includeUpper(termRangeQuery.includesUpper());
		rangeQueryBuilder.to(termRangeQuery.getUpperTerm());

		if (!termRangeQuery.isDefaultBoost()) {
			rangeQueryBuilder.boost(termRangeQuery.getBoost());
		}

		return rangeQueryBuilder;
	}

	@Override
	public QueryBuilder visitQuery(WildcardQuery wildcardQuery) {
		QueryTerm queryTerm = wildcardQuery.getQueryTerm();

		WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(
			queryTerm.getField(), queryTerm.getValue());

		if (!wildcardQuery.isDefaultBoost()) {
			wildcardQueryBuilder.boost(wildcardQuery.getBoost());
		}

		return wildcardQueryBuilder;
	}

	private void _addClause(
		BooleanClause<Query> clause, BoolQueryBuilder boolQuery,
		QueryVisitor<QueryBuilder> queryVisitor) {

		BooleanClauseOccur booleanClauseOccur = clause.getBooleanClauseOccur();

		Query query = clause.getClause();

		QueryBuilder queryBuilder = query.accept(queryVisitor);

		if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
			boolQuery.must(queryBuilder);

			return;
		}

		if (booleanClauseOccur.equals(BooleanClauseOccur.MUST_NOT)) {
			boolQuery.mustNot(queryBuilder);

			return;
		}

		if (booleanClauseOccur.equals(BooleanClauseOccur.SHOULD)) {
			boolQuery.should(queryBuilder);

			return;
		}

		throw new IllegalArgumentException();
	}

	private MultiMatchQueryBuilder.Type _translate(
		MultiMatchQuery.Type multiMatchQueryType) {

		if (multiMatchQueryType == MultiMatchQuery.Type.BEST_FIELDS) {
			return MultiMatchQueryBuilder.Type.BEST_FIELDS;
		}
		else if (multiMatchQueryType == MultiMatchQuery.Type.CROSS_FIELDS) {
			return MultiMatchQueryBuilder.Type.CROSS_FIELDS;
		}
		else if (multiMatchQueryType == MultiMatchQuery.Type.MOST_FIELDS) {
			return MultiMatchQueryBuilder.Type.MOST_FIELDS;
		}
		else if (multiMatchQueryType == MultiMatchQuery.Type.PHRASE) {
			return MultiMatchQueryBuilder.Type.PHRASE;
		}
		else if (multiMatchQueryType == MultiMatchQuery.Type.PHRASE_PREFIX) {
			return MultiMatchQueryBuilder.Type.PHRASE_PREFIX;
		}

		throw new IllegalArgumentException(
			"Invalid multi match query type: " + multiMatchQueryType);
	}

	private QueryBuilder _translateMatchPhrasePrefixQuery(
		String field, String value, MatchQuery matchQuery) {

		MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder =
			QueryBuilders.matchPhrasePrefixQuery(field, value);

		if (Validator.isNotNull(matchQuery.getAnalyzer())) {
			matchPhrasePrefixQueryBuilder.analyzer(matchQuery.getAnalyzer());
		}

		if (matchQuery.getMaxExpansions() != null) {
			matchPhrasePrefixQueryBuilder.maxExpansions(
				matchQuery.getMaxExpansions());
		}

		if (matchQuery.getSlop() != null) {
			matchPhrasePrefixQueryBuilder.slop(matchQuery.getSlop());
		}

		if (!matchQuery.isDefaultBoost()) {
			matchPhrasePrefixQueryBuilder.boost(matchQuery.getBoost());
		}

		return matchPhrasePrefixQueryBuilder;
	}

	private QueryBuilder _translateMatchPhraseQuery(
		String field, String value, MatchQuery matchQuery) {

		MatchPhraseQueryBuilder matchPhraseQueryBuilder =
			QueryBuilders.matchPhraseQuery(field, value);

		if (Validator.isNotNull(matchQuery.getAnalyzer())) {
			matchPhraseQueryBuilder.analyzer(matchQuery.getAnalyzer());
		}

		if (matchQuery.getSlop() != null) {
			matchPhraseQueryBuilder.slop(matchQuery.getSlop());
		}

		if (!matchQuery.isDefaultBoost()) {
			matchPhraseQueryBuilder.boost(matchQuery.getBoost());
		}

		return matchPhraseQueryBuilder;
	}

	private QueryBuilder _translateMatchQuery(
		String field, String value, MatchQuery matchQuery) {

		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(
			field, value);

		if (Validator.isNotNull(matchQuery.getAnalyzer())) {
			matchQueryBuilder.analyzer(matchQuery.getAnalyzer());
		}

		if (matchQuery.getCutOffFrequency() != null) {
			matchQueryBuilder.cutoffFrequency(matchQuery.getCutOffFrequency());
		}

		if (matchQuery.getFuzziness() != null) {
			matchQueryBuilder.fuzziness(
				Fuzziness.build(matchQuery.getFuzziness()));
		}

		if (matchQuery.getFuzzyRewriteMethod() != null) {
			String matchQueryFuzzyRewrite = MatchQueryTranslatorUtil.translate(
				matchQuery.getFuzzyRewriteMethod());

			matchQueryBuilder.fuzzyRewrite(matchQueryFuzzyRewrite);
		}

		if (matchQuery.getMaxExpansions() != null) {
			matchQueryBuilder.maxExpansions(matchQuery.getMaxExpansions());
		}

		if (Validator.isNotNull(matchQuery.getMinShouldMatch())) {
			matchQueryBuilder.minimumShouldMatch(
				matchQuery.getMinShouldMatch());
		}

		if (matchQuery.getOperator() != null) {
			Operator operator = MatchQueryTranslatorUtil.translate(
				matchQuery.getOperator());

			matchQueryBuilder.operator(operator);
		}

		if (matchQuery.getPrefixLength() != null) {
			matchQueryBuilder.prefixLength(matchQuery.getPrefixLength());
		}

		if (matchQuery.getZeroTermsQuery() != null) {
			ZeroTermsQueryOption zeroTermsQueryOption =
				MatchQueryTranslatorUtil.translate(
					matchQuery.getZeroTermsQuery());

			matchQueryBuilder.zeroTermsQuery(zeroTermsQueryOption);
		}

		if (!matchQuery.isDefaultBoost()) {
			matchQueryBuilder.boost(matchQuery.getBoost());
		}

		if (matchQuery.isFuzzyTranspositions() != null) {
			matchQueryBuilder.fuzzyTranspositions(
				matchQuery.isFuzzyTranspositions());
		}

		if (matchQuery.isLenient() != null) {
			matchQueryBuilder.lenient(matchQuery.isLenient());
		}

		return matchQueryBuilder;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchQueryTranslator.class);

	private static final Snapshot<FilterTranslator<QueryBuilder>>
		_filterTranslatorSnapshot = new Snapshot<>(
			ElasticsearchQueryTranslator.class,
			Snapshot.cast(FilterTranslator.class),
			"(search.engine.impl=Elasticsearch)", true);

	private final IndexNameBuilder _indexNameBuilder;

}
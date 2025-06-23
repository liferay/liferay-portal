/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.legacy.query;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.util.QueryUtil;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ChildScoreMode;
import org.opensearch.client.opensearch._types.query_dsl.Like;
import org.opensearch.client.opensearch._types.query_dsl.LikeDocument;
import org.opensearch.client.opensearch._types.query_dsl.MatchPhrasePrefixQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchPhraseQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.QueryStringQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch._types.query_dsl.TextQueryType;
import org.opensearch.client.opensearch._types.query_dsl.ZeroTermsQuery;

/**
 * @author André de Oliveira
 * @author Miguel Angelo Caldas Gallindo
 * @author Petteri Karttunen
 */
public class OpenSearchQueryTranslator
	implements QueryTranslator<QueryVariant>, QueryVisitor<QueryVariant> {

	public OpenSearchQueryTranslator(IndexNameBuilder indexNameBuilder) {
		_indexNameBuilder = indexNameBuilder;
	}

	@Override
	public QueryVariant translate(
		com.liferay.portal.kernel.search.Query query,
		SearchContext searchContext) {

		QueryVariant queryVariant = query.accept(this);

		if (queryVariant != null) {
			return queryVariant;
		}

		return QueryBuilders.queryString(
		).query(
			query.toString()
		).build();
	}

	@Override
	public QueryVariant visitQuery(BooleanQuery booleanQuery) {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		List<BooleanClause<com.liferay.portal.kernel.search.Query>> clauses =
			booleanQuery.clauses();

		for (BooleanClause<com.liferay.portal.kernel.search.Query> clause :
				clauses) {

			_addBooleanClause(clause, boolQueryBuilder);
		}

		if (!booleanQuery.isDefaultBoost()) {
			boolQueryBuilder.boost(booleanQuery.getBoost());
		}

		BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

		if (booleanFilter == null) {
			return boolQueryBuilder.build();
		}

		// LPS-86537 The following conversion is present for backwards
		// compatibility with how Liferay's Indexer frameworks handles queries.
		// Ideally, we do not wrap the BooleanQuery with another BooleanQuery.

		BoolQuery.Builder wrapperBoolQueryBuilder = QueryBuilders.bool();

		if (!clauses.isEmpty()) {
			wrapperBoolQueryBuilder.must(new Query(boolQueryBuilder.build()));
		}

		FilterTranslator<QueryVariant> filterTranslator =
			_filterTranslatorSnapshot.get();

		if (filterTranslator == null) {
			_log.error(
				"Unable to translate boolean filter " + booleanFilter +
					" because filter translator is null");

			return boolQueryBuilder.build();
		}

		Query filterQuery = new Query(
			filterTranslator.translate(booleanFilter, null));

		wrapperBoolQueryBuilder.filter(filterQuery);

		return wrapperBoolQueryBuilder.build();
	}

	@Override
	public QueryVariant visitQuery(DisMaxQuery disMaxQuery) {
		org.opensearch.client.opensearch._types.query_dsl.DisMaxQuery.Builder
			builder = QueryBuilders.disMax();

		if (!disMaxQuery.isDefaultBoost()) {
			builder.boost(disMaxQuery.getBoost());
		}

		for (com.liferay.portal.kernel.search.Query query :
				disMaxQuery.getQueries()) {

			builder.queries(new Query(query.accept(this)));
		}

		SetterUtil.setNotNullFloatAsDouble(
			builder::tieBreaker, disMaxQuery.getTieBreaker());

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(FuzzyQuery fuzzyQuery) {
		org.opensearch.client.opensearch._types.query_dsl.FuzzyQuery.Builder
			builder = QueryBuilders.fuzzy();

		if (!fuzzyQuery.isDefaultBoost()) {
			builder.boost(fuzzyQuery.getBoost());
		}

		builder.field(fuzzyQuery.getField());

		SetterUtil.setNotNullValueAsString(
			builder::fuzziness, fuzzyQuery.getFuzziness());
		SetterUtil.setNotNullInteger(
			builder::maxExpansions, fuzzyQuery.getMaxExpansions());
		SetterUtil.setNotNullInteger(
			builder::prefixLength, fuzzyQuery.getPrefixLength());

		builder.value(FieldValue.of(fuzzyQuery.getValue()));

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(MatchAllQuery matchAllQuery) {
		org.opensearch.client.opensearch._types.query_dsl.MatchAllQuery.Builder
			builder = QueryBuilders.matchAll();

		if (!matchAllQuery.isDefaultBoost()) {
			builder.boost(matchAllQuery.getBoost());
		}

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(MatchQuery matchQuery) {
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
			return _translateMatchQuery(field, matchQuery, value);
		}
		else if (type == MatchQuery.Type.PHRASE) {
			return _translateMatchPhraseQuery(field, matchQuery, value);
		}
		else if (type == MatchQuery.Type.PHRASE_PREFIX) {
			return _translateMatchPhrasePrefixQuery(field, matchQuery, value);
		}

		throw new IllegalArgumentException("Invalid match query type " + type);
	}

	@Override
	public QueryVariant visitQuery(MoreLikeThisQuery moreLikeThisQuery) {
		org.opensearch.client.opensearch._types.query_dsl.MoreLikeThisQuery.
			Builder builder = QueryBuilders.moreLikeThis();

		SetterUtil.setNotBlankString(
			builder::analyzer, moreLikeThisQuery.getAnalyzer());

		if (!moreLikeThisQuery.isDefaultBoost()) {
			builder.boost(moreLikeThisQuery.getBoost());
		}

		SetterUtil.setNotNullFloatAsDouble(
			builder::boostTerms, moreLikeThisQuery.getTermBoost());

		builder.fields(moreLikeThisQuery.getFields());

		SetterUtil.setNotNullBoolean(
			builder::include, moreLikeThisQuery.isIncludeInput());

		builder.like(_getLikes(moreLikeThisQuery));

		SetterUtil.setNotNullInteger(
			builder::maxDocFreq, moreLikeThisQuery.getMaxDocFrequency());
		SetterUtil.setNotNullInteger(
			builder::maxQueryTerms, moreLikeThisQuery.getMaxQueryTerms());
		SetterUtil.setNotNullInteger(
			builder::maxWordLength, moreLikeThisQuery.getMaxWordLength());
		SetterUtil.setNotNullInteger(
			builder::minDocFreq, moreLikeThisQuery.getMinDocFrequency());
		SetterUtil.setNotBlankString(
			builder::minimumShouldMatch, moreLikeThisQuery.getMinShouldMatch());
		SetterUtil.setNotNullInteger(
			builder::minTermFreq, moreLikeThisQuery.getMinTermFrequency());
		SetterUtil.setNotNullInteger(
			builder::minWordLength, moreLikeThisQuery.getMinWordLength());

		Collection<String> stopWords = moreLikeThisQuery.getStopWords();

		if (!stopWords.isEmpty()) {
			builder.stopWords(ListUtil.fromCollection(stopWords));
		}

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(MultiMatchQuery multiMatchQuery) {
		org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery.
			Builder builder = QueryBuilders.multiMatch();

		SetterUtil.setNotBlankString(
			builder::analyzer, multiMatchQuery.getAnalyzer());

		if (!multiMatchQuery.isDefaultBoost()) {
			builder.boost(multiMatchQuery.getBoost());
		}

		SetterUtil.setNotNullFloatAsDouble(
			builder::cutoffFrequency, multiMatchQuery.getCutOffFrequency());

		builder.fields(
			QueryUtil.fieldsBoostsToFieldsWithBoosts(
				multiMatchQuery.getFieldsBoosts()));

		SetterUtil.setNotBlankString(
			builder::fuzziness, multiMatchQuery.getFuzziness());

		if (multiMatchQuery.getFuzzyRewriteMethod() != null) {
			builder.fuzzyRewrite(
				_translateMatchQueryRewriteMethod(
					multiMatchQuery.getFuzzyRewriteMethod()));
		}

		SetterUtil.setNotNullBoolean(
			builder::lenient, multiMatchQuery.isLenient());
		SetterUtil.setNotNullInteger(
			builder::maxExpansions, multiMatchQuery.getMaxExpansions());
		SetterUtil.setNotBlankString(
			builder::minimumShouldMatch, multiMatchQuery.getMinShouldMatch());

		if (multiMatchQuery.getOperator() != null) {
			builder.operator(
				_translateMatchQueryOperator(multiMatchQuery.getOperator()));
		}

		SetterUtil.setNotNullInteger(
			builder::prefixLength, multiMatchQuery.getPrefixLength());

		builder.query(multiMatchQuery.getValue());

		SetterUtil.setNotNullInteger(builder::slop, multiMatchQuery.getSlop());

		if (multiMatchQuery.getType() != null) {
			builder.type(
				_translateMultiMatchQueryType(multiMatchQuery.getType()));
		}

		if (multiMatchQuery.getZeroTermsQuery() != null) {
			builder.zeroTermsQuery(
				_translateMatchQueryZeroTermsQuery(
					multiMatchQuery.getZeroTermsQuery()));
		}

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(NestedQuery nestedQuery) {
		org.opensearch.client.opensearch._types.query_dsl.NestedQuery.Builder
			builder = QueryBuilders.nested();

		if (!nestedQuery.isDefaultBoost()) {
			builder.boost(nestedQuery.getBoost());
		}

		builder.path(nestedQuery.getPath());

		com.liferay.portal.kernel.search.Query query = nestedQuery.getQuery();

		builder.query(new Query(query.accept(this)));

		builder.scoreMode(ChildScoreMode.Sum);

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(StringQuery stringQuery) {
		QueryStringQuery.Builder builder = QueryBuilders.queryString();

		if (!stringQuery.isDefaultBoost()) {
			builder.boost(stringQuery.getBoost());
		}

		builder.query(stringQuery.getQuery());

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(TermQuery termQuery) {
		org.opensearch.client.opensearch._types.query_dsl.TermQuery.Builder
			builder = QueryBuilders.term();

		if (!termQuery.isDefaultBoost()) {
			builder.boost(termQuery.getBoost());
		}

		QueryTerm queryTerm = termQuery.getQueryTerm();

		builder.field(queryTerm.getField());
		builder.value(FieldValue.of(queryTerm.getValue()));

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(TermRangeQuery termRangeQuery) {
		RangeQuery.Builder builder = QueryBuilders.range();

		if (!termRangeQuery.isDefaultBoost()) {
			builder.boost(termRangeQuery.getBoost());
		}

		builder.field(termRangeQuery.getField());

		QueryUtil.setRanges(
			builder, termRangeQuery.includesLower(),
			termRangeQuery.includesUpper(), termRangeQuery.getLowerTerm(),
			termRangeQuery.getUpperTerm());

		return builder.build();
	}

	@Override
	public QueryVariant visitQuery(WildcardQuery wildcardQuery) {
		org.opensearch.client.opensearch._types.query_dsl.WildcardQuery.Builder
			builder = QueryBuilders.wildcard();

		if (!wildcardQuery.isDefaultBoost()) {
			builder.boost(wildcardQuery.getBoost());
		}

		QueryTerm queryTerm = wildcardQuery.getQueryTerm();

		builder.field(queryTerm.getField());
		builder.value(queryTerm.getValue());

		return builder.build();
	}

	private void _addBooleanClause(
		BooleanClause<com.liferay.portal.kernel.search.Query> booleanClause,
		BoolQuery.Builder builder) {

		BooleanClauseOccur booleanClauseOccur =
			booleanClause.getBooleanClauseOccur();

		com.liferay.portal.kernel.search.Query query =
			booleanClause.getClause();

		Query translatedQuery = new Query(query.accept(this));

		if (booleanClauseOccur.equals(BooleanClauseOccur.MUST)) {
			builder.must(translatedQuery);

			return;
		}

		if (booleanClauseOccur.equals(BooleanClauseOccur.MUST_NOT)) {
			builder.mustNot(translatedQuery);

			return;
		}

		if (booleanClauseOccur.equals(BooleanClauseOccur.SHOULD)) {
			builder.should(translatedQuery);

			return;
		}

		throw new IllegalArgumentException(
			"Invalid Boolean clause occur " + booleanClauseOccur);
	}

	private List<Like> _getLikes(MoreLikeThisQuery moreLikeThisQuery) {
		List<Like> likes = new ArrayList<>();

		if (moreLikeThisQuery.getDocumentUIDs() == null) {
			return likes;
		}

		String indexName = _indexNameBuilder.getIndexName(
			moreLikeThisQuery.getCompanyId());

		for (String documentUID : moreLikeThisQuery.getDocumentUIDs()) {
			likes.add(
				Like.of(
					like -> like.document(
						LikeDocument.of(
							likeDocument -> likeDocument.id(
								documentUID
							).index(
								indexName
							)))));
		}

		if (Validator.isNotNull(moreLikeThisQuery.getLikeText())) {
			likes.add(
				Like.of(like -> like.text(moreLikeThisQuery.getLikeText())));
		}

		return likes;
	}

	private QueryVariant _translateMatchPhrasePrefixQuery(
		String field, MatchQuery matchQuery, String value) {

		MatchPhrasePrefixQuery.Builder builder =
			QueryBuilders.matchPhrasePrefix();

		SetterUtil.setNotBlankString(
			builder::analyzer, matchQuery.getAnalyzer());

		if (!matchQuery.isDefaultBoost()) {
			builder.boost(matchQuery.getBoost());
		}

		builder.field(field);

		SetterUtil.setNotNullInteger(
			builder::maxExpansions, matchQuery.getMaxExpansions());

		builder.query(value);

		SetterUtil.setNotNullInteger(builder::slop, matchQuery.getSlop());

		return builder.build();
	}

	private QueryVariant _translateMatchPhraseQuery(
		String field, MatchQuery matchQuery, String value) {

		MatchPhraseQuery.Builder builder = QueryBuilders.matchPhrase();

		SetterUtil.setNotBlankString(
			builder::analyzer, matchQuery.getAnalyzer());

		if (!matchQuery.isDefaultBoost()) {
			builder.boost(matchQuery.getBoost());
		}

		builder.field(field);
		builder.query(value);

		SetterUtil.setNotNullInteger(builder::slop, matchQuery.getSlop());

		return builder.build();
	}

	private QueryVariant _translateMatchQuery(
		String field, MatchQuery matchQuery, String value) {

		org.opensearch.client.opensearch._types.query_dsl.MatchQuery.Builder
			builder = QueryBuilders.match();

		SetterUtil.setNotBlankString(
			builder::analyzer, matchQuery.getAnalyzer());

		if (!matchQuery.isDefaultBoost()) {
			builder.boost(matchQuery.getBoost());
		}

		SetterUtil.setNotNullFloatAsDouble(
			builder::cutoffFrequency, matchQuery.getCutOffFrequency());

		builder.field(field);

		SetterUtil.setNotNullValueAsString(
			builder::fuzziness, matchQuery.getFuzziness());

		if (matchQuery.getFuzzyRewriteMethod() != null) {
			builder.fuzzyRewrite(
				_translateMatchQueryRewriteMethod(
					matchQuery.getFuzzyRewriteMethod()));
		}

		SetterUtil.setNotNullBoolean(
			builder::fuzzyTranspositions, matchQuery.isFuzzyTranspositions());

		SetterUtil.setNotNullBoolean(builder::lenient, matchQuery.isLenient());

		SetterUtil.setNotNullInteger(
			builder::maxExpansions, matchQuery.getMaxExpansions());
		SetterUtil.setNotBlankString(
			builder::minimumShouldMatch, matchQuery.getMinShouldMatch());

		if (matchQuery.getOperator() != null) {
			builder.operator(
				_translateMatchQueryOperator(matchQuery.getOperator()));
		}

		SetterUtil.setNotNullInteger(
			builder::prefixLength, matchQuery.getPrefixLength());

		builder.query(FieldValue.of(value));

		if (matchQuery.getZeroTermsQuery() != null) {
			builder.zeroTermsQuery(
				_translateMatchQueryZeroTermsQuery(
					matchQuery.getZeroTermsQuery()));
		}

		return builder.build();
	}

	private Operator _translateMatchQueryOperator(
		MatchQuery.Operator matchQueryOperator) {

		if (matchQueryOperator == MatchQuery.Operator.AND) {
			return Operator.And;
		}
		else if (matchQueryOperator == MatchQuery.Operator.OR) {
			return Operator.Or;
		}

		throw new IllegalArgumentException(
			"Invalid operator " + matchQueryOperator);
	}

	private String _translateMatchQueryRewriteMethod(
		MatchQuery.RewriteMethod rewriteMethod) {

		if (rewriteMethod == MatchQuery.RewriteMethod.CONSTANT_SCORE_AUTO) {
			return "constant_score_auto";
		}
		else if (rewriteMethod ==
					MatchQuery.RewriteMethod.CONSTANT_SCORE_BOOLEAN) {

			return "constant_score_boolean";
		}
		else if (rewriteMethod ==
					MatchQuery.RewriteMethod.CONSTANT_SCORE_FILTER) {

			return "constant_score_filter";
		}
		else if (rewriteMethod == MatchQuery.RewriteMethod.SCORING_BOOLEAN) {
			return "scoring_boolean";
		}
		else if (rewriteMethod == MatchQuery.RewriteMethod.TOP_TERMS_BOOST_N) {
			return "top_terms_boost_N";
		}
		else if (rewriteMethod == MatchQuery.RewriteMethod.TOP_TERMS_N) {
			return "top_terms_N";
		}

		throw new IllegalArgumentException(
			"Invalid rewrite method " + rewriteMethod);
	}

	private ZeroTermsQuery _translateMatchQueryZeroTermsQuery(
		MatchQuery.ZeroTermsQuery zeroTermsQuery) {

		if (zeroTermsQuery == MatchQuery.ZeroTermsQuery.ALL) {
			return ZeroTermsQuery.All;
		}
		else if (zeroTermsQuery == MatchQuery.ZeroTermsQuery.NONE) {
			return ZeroTermsQuery.None;
		}

		throw new IllegalArgumentException(
			"Invalid zero terms query " + zeroTermsQuery);
	}

	private TextQueryType _translateMultiMatchQueryType(
		MultiMatchQuery.Type type) {

		if (type == MultiMatchQuery.Type.BEST_FIELDS) {
			return TextQueryType.BestFields;
		}
		else if (type == MultiMatchQuery.Type.CROSS_FIELDS) {
			return TextQueryType.CrossFields;
		}
		else if (type == MultiMatchQuery.Type.MOST_FIELDS) {
			return TextQueryType.MostFields;
		}
		else if (type == MultiMatchQuery.Type.PHRASE) {
			return TextQueryType.Phrase;
		}
		else if (type == MultiMatchQuery.Type.PHRASE_PREFIX) {
			return TextQueryType.PhrasePrefix;
		}

		throw new IllegalArgumentException(
			"Invalid multi match query type " + type);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchQueryTranslator.class);

	private static final Snapshot<FilterTranslator<QueryVariant>>
		_filterTranslatorSnapshot = new Snapshot<>(
			OpenSearchQueryTranslator.class,
			Snapshot.cast(FilterTranslator.class),
			"(search.engine.impl=OpenSearch)", true);

	private final IndexNameBuilder _indexNameBuilder;

}
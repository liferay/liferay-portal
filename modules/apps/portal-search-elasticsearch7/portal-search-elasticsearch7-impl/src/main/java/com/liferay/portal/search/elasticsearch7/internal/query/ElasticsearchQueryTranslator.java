/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.query;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.geolocation.ElasticsearchShapeTranslator;
import com.liferay.portal.search.elasticsearch7.internal.geolocation.GeoLocationPointTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.function.score.ElasticsearchScoreFunctionTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.geolocation.GeoExecTypeTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.geolocation.GeoValidationMethodTranslator;
import com.liferay.portal.search.elasticsearch7.internal.script.ScriptTranslator;
import com.liferay.portal.search.elasticsearch7.internal.util.DocumentTypes;
import com.liferay.portal.search.elasticsearch7.internal.util.QueryUtil;
import com.liferay.portal.search.geolocation.GeoDistance;
import com.liferay.portal.search.geolocation.GeoLocationPoint;
import com.liferay.portal.search.geolocation.Shape;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.BoostingQuery;
import com.liferay.portal.search.query.CommonTermsQuery;
import com.liferay.portal.search.query.ConstantScoreQuery;
import com.liferay.portal.search.query.DateRangeTermQuery;
import com.liferay.portal.search.query.DisMaxQuery;
import com.liferay.portal.search.query.ExistsQuery;
import com.liferay.portal.search.query.FunctionScoreQuery;
import com.liferay.portal.search.query.FuzzyQuery;
import com.liferay.portal.search.query.GeoBoundingBoxQuery;
import com.liferay.portal.search.query.GeoDistanceQuery;
import com.liferay.portal.search.query.GeoDistanceRangeQuery;
import com.liferay.portal.search.query.GeoPolygonQuery;
import com.liferay.portal.search.query.GeoShapeQuery;
import com.liferay.portal.search.query.IdsQuery;
import com.liferay.portal.search.query.MatchAllQuery;
import com.liferay.portal.search.query.MatchPhrasePrefixQuery;
import com.liferay.portal.search.query.MatchPhraseQuery;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.query.MoreLikeThisQuery;
import com.liferay.portal.search.query.MultiMatchQuery;
import com.liferay.portal.search.query.NestedQuery;
import com.liferay.portal.search.query.Operator;
import com.liferay.portal.search.query.PercolateQuery;
import com.liferay.portal.search.query.PrefixQuery;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.QueryTranslator;
import com.liferay.portal.search.query.QueryVisitor;
import com.liferay.portal.search.query.RangeTermQuery;
import com.liferay.portal.search.query.RegexQuery;
import com.liferay.portal.search.query.ScriptQuery;
import com.liferay.portal.search.query.SimpleStringQuery;
import com.liferay.portal.search.query.StringQuery;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.query.TermsSetQuery;
import com.liferay.portal.search.query.WildcardQuery;
import com.liferay.portal.search.query.WrapperQuery;
import com.liferay.portal.search.query.function.score.ScoreFunction;
import com.liferay.portal.search.query.function.score.ScoreFunctionTranslator;
import com.liferay.portal.search.query.geolocation.ShapeRelation;
import com.liferay.portal.search.query.geolocation.SpatialStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.lucene.search.join.ScoreMode;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.BoostingQueryBuilder;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.GeoPolygonQueryBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.index.query.TermsSetQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.query.ZeroTermsQueryOption;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.legacygeo.builders.ShapeBuilder;
import org.elasticsearch.percolator.PercolateQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.xcontent.XContentType;

/**
 * @author Michael C. Han
 */
public class ElasticsearchQueryTranslator
	implements QueryTranslator<QueryBuilder>, QueryVisitor<QueryBuilder> {

	@Override
	public QueryBuilder translate(Query query) {
		QueryBuilder queryBuilder = query.accept(this);

		if (queryBuilder == null) {
			queryBuilder = QueryBuilders.queryStringQuery(query.toString());
		}

		return queryBuilder;
	}

	@Override
	public QueryBuilder visit(BooleanQuery booleanQuery) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		_processQueryClause(
			booleanQuery.getMustQueryClauses(), this, boolQueryBuilder::must);

		_processQueryClause(
			booleanQuery.getMustNotQueryClauses(), this,
			boolQueryBuilder::mustNot);

		_processQueryClause(
			booleanQuery.getShouldQueryClauses(), this,
			boolQueryBuilder::should);

		_processQueryClause(
			booleanQuery.getFilterQueryClauses(), this,
			boolQueryBuilder::filter);

		if (booleanQuery.getAdjustPureNegative() != null) {
			boolQueryBuilder.adjustPureNegative(
				booleanQuery.getAdjustPureNegative());
		}

		if (booleanQuery.getMinimumShouldMatch() != null) {
			boolQueryBuilder.minimumShouldMatch(
				booleanQuery.getMinimumShouldMatch());
		}

		boolQueryBuilder.queryName(booleanQuery.getQueryName());

		return _addBoost(booleanQuery, boolQueryBuilder);
	}

	@Override
	public QueryBuilder visit(BoostingQuery boostingQuery) {
		Query positiveQuery = boostingQuery.getPositiveQuery();

		QueryBuilder positiveQueryBuilder = positiveQuery.accept(this);

		Query negativeQuery = boostingQuery.getNegativeQuery();

		QueryBuilder negativeQueryBuilder = negativeQuery.accept(this);

		BoostingQueryBuilder boostingQueryBuilder = QueryBuilders.boostingQuery(
			positiveQueryBuilder, negativeQueryBuilder);

		Float negativeBoost = boostingQuery.getNegativeBoost();

		if (negativeBoost != null) {
			boostingQueryBuilder.negativeBoost(negativeBoost);
		}

		return _addBoost(boostingQuery, boostingQueryBuilder);
	}

	@Override
	public QueryBuilder visit(CommonTermsQuery commonTermsQuery) {
		CommonTermsQueryBuilder commonTermsQueryBuilder =
			QueryBuilders.commonTermsQuery(
				commonTermsQuery.getField(), commonTermsQuery.getText());

		if (commonTermsQuery.getAnalyzer() != null) {
			commonTermsQueryBuilder.analyzer(commonTermsQuery.getAnalyzer());
		}

		if (commonTermsQuery.getCutoffFrequency() != null) {
			commonTermsQueryBuilder.cutoffFrequency(
				commonTermsQuery.getCutoffFrequency());
		}

		if (commonTermsQuery.getHighFreqMinimumShouldMatch() != null) {
			commonTermsQueryBuilder.highFreqMinimumShouldMatch(
				commonTermsQuery.getHighFreqMinimumShouldMatch());
		}

		if (commonTermsQuery.getHighFreqOperator() != null) {
			commonTermsQueryBuilder.highFreqOperator(
				_translate(commonTermsQuery.getHighFreqOperator()));
		}

		if (commonTermsQuery.getLowFreqMinimumShouldMatch() != null) {
			commonTermsQueryBuilder.highFreqMinimumShouldMatch(
				commonTermsQuery.getLowFreqMinimumShouldMatch());
		}

		if (commonTermsQuery.getLowFreqOperator() != null) {
			commonTermsQueryBuilder.lowFreqOperator(
				_translate(commonTermsQuery.getLowFreqOperator()));
		}

		return _addBoost(commonTermsQuery, commonTermsQueryBuilder);
	}

	@Override
	public QueryBuilder visit(ConstantScoreQuery constantScoreQuery) {
		Query query = constantScoreQuery.getQuery();

		QueryBuilder queryBuilder = query.accept(this);

		return _addBoost(
			constantScoreQuery, QueryBuilders.constantScoreQuery(queryBuilder));
	}

	@Override
	public QueryBuilder visit(DateRangeTermQuery dateRangeTermQuery) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			dateRangeTermQuery.getField());

		if (dateRangeTermQuery.getDateFormat() != null) {
			rangeQueryBuilder.format(dateRangeTermQuery.getDateFormat());
		}

		rangeQueryBuilder.from(dateRangeTermQuery.getLowerBound());
		rangeQueryBuilder.includeLower(dateRangeTermQuery.isIncludesLower());
		rangeQueryBuilder.includeUpper(dateRangeTermQuery.isIncludesUpper());

		TimeZone timeZone = dateRangeTermQuery.getTimeZone();

		if (timeZone != null) {
			rangeQueryBuilder.timeZone(timeZone.getID());
		}

		rangeQueryBuilder.to(dateRangeTermQuery.getUpperBound());

		return _addBoost(dateRangeTermQuery, rangeQueryBuilder);
	}

	@Override
	public QueryBuilder visit(DisMaxQuery disMaxQuery) {
		DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();

		for (Query query : disMaxQuery.getQueries()) {
			QueryBuilder queryBuilder = query.accept(this);

			disMaxQueryBuilder.add(queryBuilder);
		}

		if (disMaxQuery.getTieBreaker() != null) {
			disMaxQueryBuilder.tieBreaker(disMaxQuery.getTieBreaker());
		}

		return _addBoost(disMaxQuery, disMaxQueryBuilder);
	}

	@Override
	public QueryBuilder visit(ExistsQuery existsQuery) {
		return _addBoost(
			existsQuery, QueryBuilders.existsQuery(existsQuery.getField()));
	}

	@Override
	public QueryBuilder visit(FunctionScoreQuery functionScoreQuery) {
		QueryBuilder queryBuilder = translate(functionScoreQuery.getQuery());

		FunctionScoreQueryBuilder functionScoreQueryBuilder =
			QueryBuilders.functionScoreQuery(
				queryBuilder,
				TransformUtil.transformToArray(
					functionScoreQuery.getFilterQueryScoreFunctionHolders(),
					filterQueryScoreFunctionHolder -> _translateFilterFunction(
						filterQueryScoreFunctionHolder, this,
						_translateScoreFunction(
							filterQueryScoreFunctionHolder.getScoreFunction())),
					FunctionScoreQueryBuilder.FilterFunctionBuilder.class));

		if (functionScoreQuery.getMinScore() != null) {
			functionScoreQueryBuilder.setMinScore(
				functionScoreQuery.getMinScore());
		}

		if (functionScoreQuery.getMaxBoost() != null) {
			functionScoreQueryBuilder.maxBoost(
				functionScoreQuery.getMaxBoost());
		}

		if (functionScoreQuery.getScoreMode() != null) {
			functionScoreQueryBuilder.scoreMode(
				_translate(functionScoreQuery.getScoreMode()));
		}

		if (functionScoreQuery.getCombineFunction() != null) {
			functionScoreQueryBuilder.boostMode(
				_combineFunctionTranslator.translate(
					functionScoreQuery.getCombineFunction()));
		}

		return _addBoost(functionScoreQuery, functionScoreQueryBuilder);
	}

	@Override
	public QueryBuilder visit(FuzzyQuery fuzzyQuery) {
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

		if (fuzzyQuery.getRewrite() != null) {
			fuzzyQueryBuilder.rewrite(fuzzyQuery.getRewrite());
		}

		if (fuzzyQuery.getTranspositions() != null) {
			fuzzyQueryBuilder.transpositions(fuzzyQuery.getTranspositions());
		}

		return _addBoost(fuzzyQuery, fuzzyQueryBuilder);
	}

	@Override
	public QueryBuilder visit(GeoBoundingBoxQuery geoBoundingBoxQuery) {
		GeoBoundingBoxQueryBuilder geoBoundingBoxQueryBuilder =
			QueryBuilders.geoBoundingBoxQuery(geoBoundingBoxQuery.getField());

		geoBoundingBoxQueryBuilder.setCorners(
			GeoLocationPointTranslator.translate(
				geoBoundingBoxQuery.getTopLeftGeoLocationPoint()),
			GeoLocationPointTranslator.translate(
				geoBoundingBoxQuery.getBottomRightGeoLocationPoint()));

		if (geoBoundingBoxQuery.getGeoExecType() != null) {
			geoBoundingBoxQueryBuilder.type(
				_geoExecTypeTranslator.translate(
					geoBoundingBoxQuery.getGeoExecType()));
		}

		if (geoBoundingBoxQuery.getGeoValidationMethod() != null) {
			geoBoundingBoxQueryBuilder.setValidationMethod(
				_geoValidationMethodTranslator.translate(
					geoBoundingBoxQuery.getGeoValidationMethod()));
		}

		if (geoBoundingBoxQuery.getIgnoreUnmapped() != null) {
			geoBoundingBoxQueryBuilder.ignoreUnmapped(
				geoBoundingBoxQuery.getIgnoreUnmapped());
		}

		return _addBoost(geoBoundingBoxQuery, geoBoundingBoxQueryBuilder);
	}

	@Override
	public QueryBuilder visit(GeoDistanceQuery geoDistanceQuery) {
		GeoDistanceQueryBuilder geoDistanceQueryBuilder =
			QueryBuilders.geoDistanceQuery(geoDistanceQuery.getField());

		geoDistanceQueryBuilder.distance(
			String.valueOf(geoDistanceQuery.getGeoDistance()));

		GeoLocationPoint pinGeoLocationPoint =
			geoDistanceQuery.getPinGeoLocationPoint();

		geoDistanceQueryBuilder.point(
			pinGeoLocationPoint.getLatitude(),
			pinGeoLocationPoint.getLongitude());

		if (geoDistanceQuery.getGeoValidationMethod() != null) {
			geoDistanceQueryBuilder.setValidationMethod(
				_geoValidationMethodTranslator.translate(
					geoDistanceQuery.getGeoValidationMethod()));
		}

		if (geoDistanceQuery.getIgnoreUnmapped() != null) {
			geoDistanceQueryBuilder.ignoreUnmapped(
				geoDistanceQuery.getIgnoreUnmapped());
		}

		return _addBoost(geoDistanceQuery, geoDistanceQueryBuilder);
	}

	@Override
	public QueryBuilder visit(GeoDistanceRangeQuery geoDistanceRangeQuery) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			geoDistanceRangeQuery.getField());

		GeoDistance geoDistanceLowerBound =
			geoDistanceRangeQuery.getLowerBoundGeoDistance();

		rangeQueryBuilder.from(geoDistanceLowerBound.toString());

		rangeQueryBuilder.includeLower(geoDistanceRangeQuery.isIncludesLower());
		rangeQueryBuilder.includeUpper(geoDistanceRangeQuery.isIncludesUpper());

		GeoDistance geoDistanceUpperBound =
			geoDistanceRangeQuery.getUpperBoundGeoDistance();

		rangeQueryBuilder.to(geoDistanceUpperBound.toString());

		if (geoDistanceRangeQuery.getShapeRelation() != null) {
			ShapeRelation shapeRelation =
				geoDistanceRangeQuery.getShapeRelation();

			String shapeRelationName = shapeRelation.name();

			rangeQueryBuilder.relation(
				StringUtil.toLowerCase(shapeRelationName));
		}

		return _addBoost(geoDistanceRangeQuery, rangeQueryBuilder);
	}

	@Override
	public QueryBuilder visit(GeoPolygonQuery geoPolygonQuery) {
		GeoPolygonQueryBuilder geoPolygonQueryBuilder =
			QueryBuilders.geoPolygonQuery(
				geoPolygonQuery.getField(),
				TransformUtil.transform(
					geoPolygonQuery.getGeoLocationPoints(),
					GeoLocationPointTranslator::translate));

		if (geoPolygonQuery.getGeoValidationMethod() != null) {
			geoPolygonQueryBuilder.setValidationMethod(
				_geoValidationMethodTranslator.translate(
					geoPolygonQuery.getGeoValidationMethod()));
		}

		if (geoPolygonQuery.getIgnoreUnmapped() != null) {
			geoPolygonQueryBuilder.ignoreUnmapped(
				geoPolygonQuery.getIgnoreUnmapped());
		}

		return _addBoost(geoPolygonQuery, geoPolygonQueryBuilder);
	}

	@Override
	public QueryBuilder visit(GeoShapeQuery geoShapeQuery) {
		GeoShapeQueryBuilder geoShapeQueryBuilder = _translateQuery(
			geoShapeQuery);

		if (geoShapeQuery.getIgnoreUnmapped() != null) {
			geoShapeQueryBuilder.ignoreUnmapped(
				geoShapeQuery.getIgnoreUnmapped());
		}

		if (geoShapeQuery.getShapeRelation() != null) {
			geoShapeQueryBuilder.relation(
				_translate(geoShapeQuery.getShapeRelation()));
		}

		if (geoShapeQuery.getSpatialStrategy() != null) {
			geoShapeQueryBuilder.strategy(
				_translate(geoShapeQuery.getSpatialStrategy()));
		}

		return _addBoost(geoShapeQuery, geoShapeQueryBuilder);
	}

	@Override
	public QueryBuilder visit(IdsQuery idsQuery) {
		IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery();

		if (idsQuery.getBoost() != null) {
			idsQueryBuilder.boost(idsQuery.getBoost());
		}

		Set<String> ids = idsQuery.getIds();

		idsQueryBuilder.addIds(ids.toArray(new String[0]));

		idsQueryBuilder.queryName(idsQuery.getQueryName());

		Set<String> types = idsQuery.getTypes();

		idsQueryBuilder.types(types.toArray(new String[0]));

		return _addBoost(idsQuery, idsQueryBuilder);
	}

	@Override
	public QueryBuilder visit(MatchAllQuery matchAllQuery) {
		return _addBoost(matchAllQuery, QueryBuilders.matchAllQuery());
	}

	@Override
	public QueryBuilder visit(MatchPhrasePrefixQuery matchPhrasePrefixQuery) {
		MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder =
			QueryBuilders.matchPhrasePrefixQuery(
				matchPhrasePrefixQuery.getField(),
				matchPhrasePrefixQuery.getValue());

		if (matchPhrasePrefixQuery.getAnalyzer() != null) {
			matchPhrasePrefixQueryBuilder.analyzer(
				matchPhrasePrefixQuery.getAnalyzer());
		}

		if (matchPhrasePrefixQuery.getSlop() != null) {
			matchPhrasePrefixQueryBuilder.slop(
				matchPhrasePrefixQuery.getSlop());
		}

		if (matchPhrasePrefixQuery.getMaxExpansions() != null) {
			matchPhrasePrefixQueryBuilder.maxExpansions(
				matchPhrasePrefixQuery.getMaxExpansions());
		}

		return _addBoost(matchPhrasePrefixQuery, matchPhrasePrefixQueryBuilder);
	}

	@Override
	public QueryBuilder visit(MatchPhraseQuery matchPhraseQuery) {
		MatchPhraseQueryBuilder matchPhraseQueryBuilder =
			QueryBuilders.matchPhraseQuery(
				matchPhraseQuery.getField(), matchPhraseQuery.getValue());

		if (matchPhraseQuery.getAnalyzer() != null) {
			matchPhraseQueryBuilder.analyzer(matchPhraseQuery.getAnalyzer());
		}

		if (matchPhraseQuery.getSlop() != null) {
			matchPhraseQueryBuilder.slop(matchPhraseQuery.getSlop());
		}

		return _addBoost(matchPhraseQuery, matchPhraseQueryBuilder);
	}

	@Override
	public QueryBuilder visit(MatchQuery matchQuery) {
		return _addBoost(matchQuery, _translate(matchQuery));
	}

	@Override
	public QueryBuilder visit(MoreLikeThisQuery moreLikeThisQuery) {
		List<MoreLikeThisQueryBuilder.Item> likeItems = new ArrayList<>();

		if (SetUtil.isNotEmpty(moreLikeThisQuery.getDocumentIdentifiers())) {
			Set<MoreLikeThisQuery.DocumentIdentifier> documentIdentifiers =
				moreLikeThisQuery.getDocumentIdentifiers();

			documentIdentifiers.forEach(
				documentIdentifier -> {
					String type = documentIdentifier.getType();

					if (Validator.isNull(type)) {
						type = moreLikeThisQuery.getType();
					}

					if (Validator.isNull(type)) {
						type = DocumentTypes.LIFERAY;
					}

					MoreLikeThisQueryBuilder.Item moreLikeThisQueryBuilderItem =
						new MoreLikeThisQueryBuilder.Item(
							documentIdentifier.getIndex(), type,
							documentIdentifier.getId());

					likeItems.add(moreLikeThisQueryBuilderItem);
				});
		}

		List<String> fields = moreLikeThisQuery.getFields();

		String[] fieldsArray = null;

		if (!fields.isEmpty()) {
			fieldsArray = fields.toArray(new String[0]);
		}

		List<String> likeTexts = moreLikeThisQuery.getLikeTexts();

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

		return _addBoost(moreLikeThisQuery, moreLikeThisQueryBuilder);
	}

	@Override
	public QueryBuilder visit(MultiMatchQuery multiMatchQuery) {
		MultiMatchQueryBuilder multiMatchQueryBuilder =
			QueryBuilders.multiMatchQuery(multiMatchQuery.getValue());

		if (Validator.isNotNull(multiMatchQuery.getAnalyzer())) {
			multiMatchQueryBuilder.analyzer(multiMatchQuery.getAnalyzer());
		}

		if (multiMatchQuery.getCutOffFrequency() != null) {
			multiMatchQueryBuilder.cutoffFrequency(
				multiMatchQuery.getCutOffFrequency());
		}

		Map<String, Float> fieldsBoosts = multiMatchQuery.getFieldsBoosts();

		for (Map.Entry<String, Float> entry : fieldsBoosts.entrySet()) {
			Float boost = entry.getValue();
			String field = entry.getKey();

			if (boost == null) {
				multiMatchQueryBuilder.field(field);
			}
			else {
				multiMatchQueryBuilder.field(field, boost);
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
			org.elasticsearch.index.query.Operator matchQueryBuilderOperator =
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

		if (multiMatchQuery.isLenient() != null) {
			multiMatchQueryBuilder.lenient(multiMatchQuery.isLenient());
		}

		return _addBoost(multiMatchQuery, multiMatchQueryBuilder);
	}

	@Override
	public QueryBuilder visit(NestedQuery nestedQuery) {
		Query query = nestedQuery.getQuery();

		QueryBuilder queryBuilder = query.accept(this);

		return _addBoost(
			nestedQuery,
			QueryBuilders.nestedQuery(
				nestedQuery.getPath(), queryBuilder, ScoreMode.Total));
	}

	@Override
	public QueryBuilder visit(PercolateQuery percolateQuery) {
		List<String> documentJSONs = percolateQuery.getDocumentJSONs();

		List<BytesReference> bytesArrays = new ArrayList<>();

		documentJSONs.forEach(
			documentJSON -> bytesArrays.add(new BytesArray(documentJSON)));

		return _addBoost(
			percolateQuery,
			new PercolateQueryBuilder(
				percolateQuery.getField(), bytesArrays, XContentType.JSON));
	}

	@Override
	public QueryBuilder visit(PrefixQuery prefixQuery) {
		PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery(
			prefixQuery.getField(), prefixQuery.getPrefix());

		if (prefixQuery.getRewrite() != null) {
			prefixQueryBuilder.rewrite(prefixQuery.getRewrite());
		}

		return _addBoost(prefixQuery, prefixQueryBuilder);
	}

	@Override
	public QueryBuilder visit(RangeTermQuery rangeTermQuery) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			rangeTermQuery.getField());

		rangeQueryBuilder.from(rangeTermQuery.getLowerBound());
		rangeQueryBuilder.includeLower(rangeTermQuery.isIncludesLower());
		rangeQueryBuilder.includeUpper(rangeTermQuery.isIncludesUpper());
		rangeQueryBuilder.to(rangeTermQuery.getUpperBound());

		return _addBoost(rangeTermQuery, rangeQueryBuilder);
	}

	@Override
	public QueryBuilder visit(RegexQuery regexQuery) {
		RegexpQueryBuilder regexpQueryBuilder = QueryBuilders.regexpQuery(
			regexQuery.getField(), regexQuery.getRegex());

		if (regexQuery.getMaxDeterminedStates() != null) {
			regexpQueryBuilder.maxDeterminizedStates(
				regexQuery.getMaxDeterminedStates());
		}

		if (regexQuery.getRegexFlags() != null) {
			regexpQueryBuilder.flags(regexQuery.getRegexFlags());
		}

		if (regexQuery.getRewrite() != null) {
			regexpQueryBuilder.rewrite(regexQuery.getRewrite());
		}

		return _addBoost(regexQuery, regexpQueryBuilder);
	}

	@Override
	public QueryBuilder visit(ScriptQuery scriptQuery) {
		return _addBoost(
			scriptQuery,
			QueryBuilders.scriptQuery(
				_scriptTranslator.translate(scriptQuery.getScript())));
	}

	@Override
	public QueryBuilder visit(SimpleStringQuery simpleStringQuery) {
		SimpleQueryStringBuilder simpleQueryStringBuilder =
			QueryBuilders.simpleQueryStringQuery(simpleStringQuery.getQuery());

		if (simpleStringQuery.getAnalyzer() != null) {
			simpleQueryStringBuilder.analyzer(simpleStringQuery.getAnalyzer());
		}

		if (simpleStringQuery.getAnalyzeWildcard() != null) {
			simpleQueryStringBuilder.analyzeWildcard(
				simpleStringQuery.getAnalyzeWildcard());
		}

		if (simpleStringQuery.getAutoGenerateSynonymsPhraseQuery() != null) {
			simpleQueryStringBuilder.autoGenerateSynonymsPhraseQuery(
				simpleStringQuery.getAutoGenerateSynonymsPhraseQuery());
		}

		Map<String, Float> fieldBoostMap = simpleStringQuery.getFieldBoostMap();

		if (MapUtil.isNotEmpty(fieldBoostMap)) {
			for (Map.Entry<String, Float> entry : fieldBoostMap.entrySet()) {
				Float value = entry.getValue();

				if (value != null) {
					simpleQueryStringBuilder.field(entry.getKey(), value);
				}
				else {
					simpleQueryStringBuilder.field(entry.getKey());
				}
			}
		}

		if (simpleStringQuery.getDefaultOperator() != null) {
			Operator operator = simpleStringQuery.getDefaultOperator();

			if (operator == Operator.OR) {
				simpleQueryStringBuilder.defaultOperator(
					org.elasticsearch.index.query.Operator.OR);
			}
			else if (operator == Operator.AND) {
				simpleQueryStringBuilder.defaultOperator(
					org.elasticsearch.index.query.Operator.AND);
			}
			else {
				throw new IllegalArgumentException(
					"Invalid operator: " + operator);
			}
		}

		if (simpleStringQuery.getFuzzyMaxExpansions() != null) {
			simpleQueryStringBuilder.fuzzyMaxExpansions(
				simpleStringQuery.getFuzzyMaxExpansions());
		}

		if (simpleStringQuery.getFuzzyPrefixLength() != null) {
			simpleQueryStringBuilder.fuzzyPrefixLength(
				simpleStringQuery.getFuzzyPrefixLength());
		}

		if (simpleStringQuery.getFuzzyTranspositions() != null) {
			simpleQueryStringBuilder.fuzzyTranspositions(
				simpleStringQuery.getFuzzyTranspositions());
		}

		if (simpleStringQuery.getLenient() != null) {
			simpleQueryStringBuilder.lenient(simpleStringQuery.getLenient());
		}

		if (simpleStringQuery.getQuoteFieldSuffix() != null) {
			simpleQueryStringBuilder.quoteFieldSuffix(
				simpleStringQuery.getQuoteFieldSuffix());
		}

		return _addBoost(simpleStringQuery, simpleQueryStringBuilder);
	}

	@Override
	public QueryBuilder visit(StringQuery stringQuery) {
		QueryStringQueryBuilder queryStringQueryBuilder =
			QueryBuilders.queryStringQuery(stringQuery.getQuery());

		if (stringQuery.getAllowLeadingWildcard() != null) {
			queryStringQueryBuilder.allowLeadingWildcard(
				stringQuery.getAllowLeadingWildcard());
		}

		if (stringQuery.getAnalyzer() != null) {
			queryStringQueryBuilder.analyzer(stringQuery.getAnalyzer());
		}

		if (stringQuery.getAnalyzeWildcard() != null) {
			queryStringQueryBuilder.analyzeWildcard(
				stringQuery.getAnalyzeWildcard());
		}

		if (stringQuery.getAutoGenerateSynonymsPhraseQuery() != null) {
			queryStringQueryBuilder.autoGenerateSynonymsPhraseQuery(
				stringQuery.getAutoGenerateSynonymsPhraseQuery());
		}

		if (stringQuery.getDefaultField() != null) {
			queryStringQueryBuilder.defaultField(stringQuery.getDefaultField());
		}

		if (stringQuery.getDefaultOperator() != null) {
			Operator operator = stringQuery.getDefaultOperator();

			if (operator == Operator.OR) {
				queryStringQueryBuilder.defaultOperator(
					org.elasticsearch.index.query.Operator.OR);
			}
			else if (operator == Operator.AND) {
				queryStringQueryBuilder.defaultOperator(
					org.elasticsearch.index.query.Operator.AND);
			}
			else {
				throw new IllegalArgumentException(
					"Invalid operator: " + operator);
			}
		}

		if (stringQuery.getEnablePositionIncrements() != null) {
			queryStringQueryBuilder.enablePositionIncrements(
				stringQuery.getEnablePositionIncrements());
		}

		if (stringQuery.getEscape() != null) {
			queryStringQueryBuilder.escape(stringQuery.getEscape());
		}

		Map<String, Float> fieldsBoosts = stringQuery.getFieldsBoosts();

		for (Map.Entry<String, Float> entry : fieldsBoosts.entrySet()) {
			Float boost = entry.getValue();
			String field = entry.getKey();

			if (boost == null) {
				queryStringQueryBuilder.field(field);
			}
			else {
				queryStringQueryBuilder.field(field, boost);
			}
		}

		if (stringQuery.getFuzziness() != null) {
			queryStringQueryBuilder.fuzziness(
				Fuzziness.build(stringQuery.getFuzziness()));
		}

		if (stringQuery.getFuzzyMaxExpansions() != null) {
			queryStringQueryBuilder.fuzzyMaxExpansions(
				stringQuery.getFuzzyMaxExpansions());
		}

		if (stringQuery.getFuzzyPrefixLength() != null) {
			queryStringQueryBuilder.fuzzyPrefixLength(
				stringQuery.getFuzzyPrefixLength());
		}

		if (stringQuery.getFuzzyRewrite() != null) {
			queryStringQueryBuilder.fuzzyRewrite(stringQuery.getFuzzyRewrite());
		}

		if (stringQuery.getFuzzyTranspositions() != null) {
			queryStringQueryBuilder.fuzzyTranspositions(
				stringQuery.getFuzzyTranspositions());
		}

		if (stringQuery.getLenient() != null) {
			queryStringQueryBuilder.lenient(stringQuery.getLenient());
		}

		if (stringQuery.getMaxDeterminedStates() != null) {
			queryStringQueryBuilder.maxDeterminizedStates(
				stringQuery.getMaxDeterminedStates());
		}

		if (stringQuery.getMinimumShouldMatch() != null) {
			queryStringQueryBuilder.minimumShouldMatch(
				stringQuery.getMinimumShouldMatch());
		}

		if (stringQuery.getPhraseSlop() != null) {
			queryStringQueryBuilder.phraseSlop(stringQuery.getPhraseSlop());
		}

		if (stringQuery.getQuoteAnalyzer() != null) {
			queryStringQueryBuilder.quoteAnalyzer(
				stringQuery.getQuoteAnalyzer());
		}

		if (stringQuery.getQuoteFieldSuffix() != null) {
			queryStringQueryBuilder.quoteFieldSuffix(
				stringQuery.getQuoteFieldSuffix());
		}

		if (stringQuery.getRewrite() != null) {
			queryStringQueryBuilder.rewrite(stringQuery.getRewrite());
		}

		if (stringQuery.getTieBreaker() != null) {
			queryStringQueryBuilder.tieBreaker(stringQuery.getTieBreaker());
		}

		if (stringQuery.getTimeZone() != null) {
			queryStringQueryBuilder.timeZone(stringQuery.getTimeZone());
		}

		return _addBoost(stringQuery, queryStringQueryBuilder);
	}

	@Override
	public QueryBuilder visit(TermQuery termQuery) {
		return _addBoost(
			termQuery,
			QueryBuilders.termQuery(
				termQuery.getField(), termQuery.getValue()));
	}

	@Override
	public QueryBuilder visit(TermsQuery termsQuery) {
		return _addBoost(
			termsQuery,
			QueryUtil.translateTerms(
				termsQuery.getField(), termsQuery.getValues()));
	}

	@Override
	public QueryBuilder visit(TermsSetQuery termsSetQuery) {
		TermsSetQueryBuilder termsSetQueryBuilder = new TermsSetQueryBuilder(
			termsSetQuery.getFieldName(),
			ListUtil.toList(termsSetQuery.getValues()));

		if (!Validator.isBlank(termsSetQuery.getMinimumShouldMatchField())) {
			termsSetQueryBuilder.setMinimumShouldMatchField(
				termsSetQuery.getMinimumShouldMatchField());
		}

		if (termsSetQuery.getMinimumShouldMatchScript() != null) {
			Script script = _scriptTranslator.translate(
				termsSetQuery.getMinimumShouldMatchScript());

			termsSetQueryBuilder.setMinimumShouldMatchScript(script);
		}

		return _addBoost(termsSetQuery, termsSetQueryBuilder);
	}

	@Override
	public QueryBuilder visit(WildcardQuery wildcardQuery) {
		WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(
			wildcardQuery.getField(), wildcardQuery.getValue());

		if (wildcardQuery.getRewrite() != null) {
			wildcardQueryBuilder.rewrite(wildcardQuery.getRewrite());
		}

		return _addBoost(wildcardQuery, wildcardQueryBuilder);
	}

	@Override
	public QueryBuilder visit(WrapperQuery wrapperQuery) {
		return _addBoost(
			wrapperQuery, QueryBuilders.wrapperQuery(wrapperQuery.getSource()));
	}

	protected interface QueryBuilderConsumer {

		public void accept(QueryBuilder queryBuilder);

	}

	private QueryBuilder _addBoost(Query query, QueryBuilder queryBuilder) {
		if (query.getBoost() != null) {
			queryBuilder.boost(query.getBoost());
		}

		return queryBuilder;
	}

	private void _processQueryClause(
		List<Query> queryClauses, QueryVisitor<QueryBuilder> queryVisitor,
		QueryBuilderConsumer queryBuilderConsumer) {

		for (Query query : queryClauses) {
			QueryBuilder queryBuilder = query.accept(queryVisitor);

			queryBuilderConsumer.accept(queryBuilder);
		}
	}

	private
		org.elasticsearch.common.lucene.search.function.FunctionScoreQuery.
			ScoreMode _translate(FunctionScoreQuery.ScoreMode scoreMode) {

		if (scoreMode == FunctionScoreQuery.ScoreMode.AVG) {
			return org.elasticsearch.common.lucene.search.function.
				FunctionScoreQuery.ScoreMode.AVG;
		}
		else if (scoreMode == FunctionScoreQuery.ScoreMode.FIRST) {
			return org.elasticsearch.common.lucene.search.function.
				FunctionScoreQuery.ScoreMode.FIRST;
		}
		else if (scoreMode == FunctionScoreQuery.ScoreMode.MAX) {
			return org.elasticsearch.common.lucene.search.function.
				FunctionScoreQuery.ScoreMode.MAX;
		}
		else if (scoreMode == FunctionScoreQuery.ScoreMode.MIN) {
			return org.elasticsearch.common.lucene.search.function.
				FunctionScoreQuery.ScoreMode.MIN;
		}
		else if (scoreMode == FunctionScoreQuery.ScoreMode.MULTIPLY) {
			return org.elasticsearch.common.lucene.search.function.
				FunctionScoreQuery.ScoreMode.MULTIPLY;
		}
		else if (scoreMode == FunctionScoreQuery.ScoreMode.SUM) {
			return org.elasticsearch.common.lucene.search.function.
				FunctionScoreQuery.ScoreMode.SUM;
		}

		throw new IllegalArgumentException(
			"Invalid FunctionScoreQuery.ScoreMode: " + scoreMode);
	}

	private QueryBuilder _translate(MatchQuery matchQuery) {
		String field = matchQuery.getField();

		MatchQuery.Type type = matchQuery.getType();
		Object value = matchQuery.getValue();

		if (value instanceof String) {
			String stringValue = (String)value;

			if (stringValue.startsWith(StringPool.QUOTE) &&
				stringValue.endsWith(StringPool.QUOTE)) {

				type = MatchQuery.Type.PHRASE;

				stringValue = StringUtil.unquote(stringValue);

				if (stringValue.endsWith(StringPool.STAR)) {
					type = MatchQuery.Type.PHRASE_PREFIX;
				}
			}

			if (type == MatchQuery.Type.PHRASE) {
				return _translateMatchPhraseQuery(
					field, stringValue, matchQuery);
			}
			else if (type == MatchQuery.Type.PHRASE_PREFIX) {
				return _translateMatchPhrasePrefixQuery(
					field, stringValue, matchQuery);
			}
		}

		if ((type == null) || (type == MatchQuery.Type.BOOLEAN)) {
			return _translateMatchQuery(field, value, matchQuery);
		}

		throw new IllegalArgumentException("Invalid match query type: " + type);
	}

	private MultiMatchQueryBuilder.Type _translate(
		MultiMatchQuery.Type multiMatchQueryType) {

		if (multiMatchQueryType == MultiMatchQuery.Type.BEST_FIELDS) {
			return MultiMatchQueryBuilder.Type.BEST_FIELDS;
		}
		else if (multiMatchQueryType == MultiMatchQuery.Type.BOOL_PREFIX) {
			return MultiMatchQueryBuilder.Type.BOOL_PREFIX;
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

	private org.elasticsearch.index.query.Operator _translate(
		Operator matchQueryOperator) {

		if (matchQueryOperator == Operator.AND) {
			return org.elasticsearch.index.query.Operator.AND;
		}
		else if (matchQueryOperator == Operator.OR) {
			return org.elasticsearch.index.query.Operator.AND;
		}

		throw new IllegalArgumentException(
			"Invalid operator: " + matchQueryOperator);
	}

	private org.elasticsearch.common.geo.ShapeRelation _translate(
		ShapeRelation shapeRelation) {

		if (shapeRelation == ShapeRelation.CONTAINS) {
			return org.elasticsearch.common.geo.ShapeRelation.CONTAINS;
		}

		if (shapeRelation == ShapeRelation.DISJOINT) {
			return org.elasticsearch.common.geo.ShapeRelation.DISJOINT;
		}

		if (shapeRelation == ShapeRelation.INTERSECTS) {
			return org.elasticsearch.common.geo.ShapeRelation.INTERSECTS;
		}

		if (shapeRelation == ShapeRelation.WITHIN) {
			return org.elasticsearch.common.geo.ShapeRelation.WITHIN;
		}

		throw new IllegalArgumentException(
			"Invalid ShapeRelation: " + shapeRelation);
	}

	private org.elasticsearch.common.geo.SpatialStrategy _translate(
		SpatialStrategy spatialStrategy) {

		if (spatialStrategy == SpatialStrategy.RECURSIVE) {
			return org.elasticsearch.common.geo.SpatialStrategy.RECURSIVE;
		}

		if (spatialStrategy == SpatialStrategy.TERM) {
			return org.elasticsearch.common.geo.SpatialStrategy.TERM;
		}

		throw new IllegalArgumentException(
			"Invalid SpatialStrategy: " + spatialStrategy);
	}

	private FunctionScoreQueryBuilder.FilterFunctionBuilder
		_translateFilterFunction(
			FunctionScoreQuery.FilterQueryScoreFunctionHolder
				filterQueryScoreFunctionHolder,
			QueryTranslator<QueryBuilder> queryTranslator,
			ScoreFunctionBuilder<?> scoreFunctionBuilder) {

		if (filterQueryScoreFunctionHolder.getFilterQuery() == null) {
			return new FunctionScoreQueryBuilder.FilterFunctionBuilder(
				scoreFunctionBuilder);
		}

		return new FunctionScoreQueryBuilder.FilterFunctionBuilder(
			queryTranslator.translate(
				filterQueryScoreFunctionHolder.getFilterQuery()),
			scoreFunctionBuilder);
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

		return matchPhraseQueryBuilder;
	}

	private QueryBuilder _translateMatchQuery(
		String field, Object value, MatchQuery matchQuery) {

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
			org.elasticsearch.index.query.Operator operator =
				MatchQueryTranslatorUtil.translate(matchQuery.getOperator());

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

		if (matchQuery.isFuzzyTranspositions() != null) {
			matchQueryBuilder.fuzzyTranspositions(
				matchQuery.isFuzzyTranspositions());
		}

		if (matchQuery.isLenient() != null) {
			matchQueryBuilder.lenient(matchQuery.isLenient());
		}

		return matchQueryBuilder;
	}

	private GeoShapeQueryBuilder _translateQuery(GeoShapeQuery geoShapeQuery) {
		if (geoShapeQuery.getIndexedShapeId() != null) {
			GeoShapeQueryBuilder geoShapeQueryBuilder =
				QueryBuilders.geoShapeQuery(
					geoShapeQuery.getField(), geoShapeQuery.getIndexedShapeId(),
					geoShapeQuery.getIndexedShapeType());

			if (geoShapeQuery.getIndexedShapeIndex() != null) {
				geoShapeQueryBuilder.indexedShapeIndex(
					geoShapeQuery.getIndexedShapeIndex());
			}

			if (geoShapeQuery.getIndexedShapePath() != null) {
				geoShapeQueryBuilder.indexedShapePath(
					geoShapeQuery.getIndexedShapePath());
			}

			if (geoShapeQuery.getIndexedShapeRouting() != null) {
				geoShapeQueryBuilder.indexedShapeRouting(
					geoShapeQuery.getIndexedShapeRouting());
			}

			return geoShapeQueryBuilder;
		}

		Shape shape = geoShapeQuery.getShape();

		ShapeBuilder shapeBuilder = shape.accept(_elasticsearchShapeTranslator);

		return new GeoShapeQueryBuilder(
			geoShapeQuery.getField(), shapeBuilder.buildGeometry());
	}

	private ScoreFunctionBuilder<?> _translateScoreFunction(
		ScoreFunction scoreFunction) {

		ScoreFunctionBuilder<?> scoreFunctionBuilder = scoreFunction.accept(
			_scoreFunctionTranslator);

		if (scoreFunction.getWeight() != null) {
			scoreFunctionBuilder.setWeight(scoreFunction.getWeight());
		}

		return scoreFunctionBuilder;
	}

	private final CombineFunctionTranslator _combineFunctionTranslator =
		new CombineFunctionTranslator();
	private final ElasticsearchShapeTranslator _elasticsearchShapeTranslator =
		new ElasticsearchShapeTranslator();
	private final GeoExecTypeTranslator _geoExecTypeTranslator =
		new GeoExecTypeTranslator();
	private final GeoValidationMethodTranslator _geoValidationMethodTranslator =
		new GeoValidationMethodTranslator();
	private final ScoreFunctionTranslator<ScoreFunctionBuilder<?>>
		_scoreFunctionTranslator = new ElasticsearchScoreFunctionTranslator();
	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}
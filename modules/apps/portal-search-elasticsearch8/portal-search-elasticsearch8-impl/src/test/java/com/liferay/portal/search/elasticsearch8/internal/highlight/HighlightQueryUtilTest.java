/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.highlight;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Olivia Yu
 */
public class HighlightQueryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetHighlightQueryKeepsExactPhraseQuery() {
		Query query = _createMatchPhraseQuery(null);

		Assert.assertSame(query, HighlightQueryUtil.getHighlightQuery(query));
	}

	@Test
	public void testGetHighlightQueryKeepsFilterQueryClauses() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		Query filterQuery = _createMatchQuery();

		boolQueryBuilder.filter(filterQuery);

		boolQueryBuilder.must(_createMatchQuery());
		boolQueryBuilder.should(_createMatchPhraseQuery(50));

		BoolQuery boolQuery = _getBoolQuery(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));

		Assert.assertEquals(List.of(filterQuery), boolQuery.filter());

		List<Query> shouldQueries = boolQuery.should();

		Assert.assertTrue(shouldQueries.toString(), shouldQueries.isEmpty());
	}

	@Test
	public void testGetHighlightQueryKeepsMustNotQueryClauses() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		Query query = _createMatchQuery();

		boolQueryBuilder.must(_createMatchQuery());
		boolQueryBuilder.mustNot(query);

		BoolQuery boolQuery = _getBoolQuery(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));

		Assert.assertEquals(List.of(query), boolQuery.mustNot());
	}

	@Test
	public void testGetHighlightQueryKeepsProximityQueryInMustQueryClauses() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		Query proximityQuery = _createMatchPhraseQuery(50);

		boolQueryBuilder.must(proximityQuery);

		BoolQuery boolQuery = _getBoolQuery(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));

		Assert.assertEquals(List.of(proximityQuery), boolQuery.must());
	}

	@Test
	public void testGetHighlightQueryKeepsQueryWithoutProximityQuery() {
		Query query = _createMatchQuery();

		Assert.assertSame(query, HighlightQueryUtil.getHighlightQuery(query));
	}

	@Test
	public void testGetHighlightQueryKeepsStandaloneProximityQuery() {
		Query query = _createMatchPhraseQuery(50);

		Assert.assertSame(query, HighlightQueryUtil.getHighlightQuery(query));
	}

	@Test
	public void testGetHighlightQueryRemovesProximityQueryFromBooleanQuery() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		Query exactPhraseQuery = _createMatchPhraseQuery(null);

		Query matchQuery = _createMatchQuery();

		boolQueryBuilder.must(matchQuery);

		boolQueryBuilder.should(_createMatchPhraseQuery(50), exactPhraseQuery);

		BoolQuery boolQuery = _getBoolQuery(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));

		Assert.assertEquals(List.of(matchQuery), boolQuery.must());
		Assert.assertEquals(List.of(exactPhraseQuery), boolQuery.should());
	}

	@Test
	public void testGetHighlightQueryRemovesProximityQueryFromInnerBooleanQuery() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
		BoolQuery.Builder innerBoolQueryBuilder = QueryBuilders.bool();

		Query matchQuery = _createMatchQuery();

		innerBoolQueryBuilder.must(matchQuery);

		innerBoolQueryBuilder.should(_createMatchPhraseQuery(50));

		boolQueryBuilder.should(new Query(innerBoolQueryBuilder.build()));

		BoolQuery boolQuery = _getBoolQuery(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));

		List<Query> queries = boolQuery.should();

		BoolQuery innerBoolQuery = _getBoolQuery(queries.get(0));

		Assert.assertEquals(List.of(matchQuery), innerBoolQuery.must());

		List<Query> shouldQueries = innerBoolQuery.should();

		Assert.assertTrue(shouldQueries.toString(), shouldQueries.isEmpty());
	}

	@Test
	public void testGetHighlightQueryRemovesProximityQueryFromShouldUnderMust() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
		BoolQuery.Builder innerBoolQueryBuilder = QueryBuilders.bool();
		BoolQuery.Builder middleBoolQueryBuilder = QueryBuilders.bool();

		Query matchQuery = _createMatchQuery();

		innerBoolQueryBuilder.must(matchQuery);

		innerBoolQueryBuilder.should(_createMatchPhraseQuery(50));

		middleBoolQueryBuilder.should(new Query(innerBoolQueryBuilder.build()));

		boolQueryBuilder.must(new Query(middleBoolQueryBuilder.build()));

		BoolQuery boolQuery = _getBoolQuery(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));

		List<Query> queries = boolQuery.must();

		BoolQuery middleBoolQuery = _getBoolQuery(queries.get(0));

		queries = middleBoolQuery.should();

		BoolQuery innerBoolQuery = _getBoolQuery(queries.get(0));

		Assert.assertEquals(List.of(matchQuery), innerBoolQuery.must());

		List<Query> shouldQueries = innerBoolQuery.should();

		Assert.assertTrue(shouldQueries.toString(), shouldQueries.isEmpty());
	}

	@Test
	public void testGetHighlightQueryRemovesProximityQueryLeavingNothing() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		boolQueryBuilder.should(_createMatchPhraseQuery(50));

		Assert.assertNull(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));
	}

	@Test
	public void testGetHighlightQueryReturnsNullForNullQuery() {
		Assert.assertNull(HighlightQueryUtil.getHighlightQuery(null));
	}

	@Test
	public void testGetHighlightQueryReturnsNullWhenOnlyFiltersSurvive() {
		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		boolQueryBuilder.filter(_createMatchQuery());
		boolQueryBuilder.should(_createMatchPhraseQuery(50));

		Assert.assertNull(
			HighlightQueryUtil.getHighlightQuery(
				new Query(boolQueryBuilder.build())));
	}

	private Query _createMatchPhraseQuery(Integer slop) {
		MatchPhraseQuery.Builder matchPhraseQueryBuilder =
			new MatchPhraseQuery.Builder();

		matchPhraseQueryBuilder.field(_FIELD_NAME);
		matchPhraseQueryBuilder.query(_KEYWORDS);
		matchPhraseQueryBuilder.slop(slop);

		return new Query(matchPhraseQueryBuilder.build());
	}

	private Query _createMatchQuery() {
		MatchQuery.Builder matchQueryBuilder = new MatchQuery.Builder();

		matchQueryBuilder.field(_FIELD_NAME);
		matchQueryBuilder.query(_KEYWORDS);

		return new Query(matchQueryBuilder.build());
	}

	private BoolQuery _getBoolQuery(Query query) {
		Assert.assertTrue(String.valueOf(query), query.isBool());

		return query.bool();
	}

	private static final String _FIELD_NAME = RandomTestUtil.randomString();

	private static final String _KEYWORDS = RandomTestUtil.randomString();

}
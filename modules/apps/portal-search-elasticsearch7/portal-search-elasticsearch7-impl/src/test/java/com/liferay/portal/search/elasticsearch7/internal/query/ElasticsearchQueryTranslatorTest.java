/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.query;

import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch7.internal.filter.ElasticsearchFilterTranslator;
import com.liferay.portal.search.elasticsearch7.internal.filter.ElasticsearchFilterTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.util.QueryUtil;
import com.liferay.portal.search.internal.query.BooleanQueryImpl;
import com.liferay.portal.search.internal.query.CommonTermsQueryImpl;
import com.liferay.portal.search.internal.query.FuzzyQueryImpl;
import com.liferay.portal.search.internal.query.MatchAllQueryImpl;
import com.liferay.portal.search.internal.query.MoreLikeThisQueryImpl;
import com.liferay.portal.search.internal.query.TermQueryImpl;
import com.liferay.portal.search.internal.query.TermsQueryImpl;
import com.liferay.portal.search.internal.query.WildcardQueryImpl;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public class ElasticsearchQueryTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ElasticsearchFilterTranslatorFixture
			elasticsearchFilterTranslatorFixture =
				new ElasticsearchFilterTranslatorFixture(
					new ElasticsearchQueryTranslatorFixture(
					).getElasticsearchQueryTranslator());

		_elasticsearchFilterTranslator =
			elasticsearchFilterTranslatorFixture.
				getElasticsearchFilterTranslator();

		_elasticsearchQueryTranslator = new ElasticsearchQueryTranslator();
	}

	@Test
	public void testTranslateBoostCommonTermsQuery() {
		_assertBoost(new CommonTermsQueryImpl("test", "test"));
	}

	@Test
	public void testTranslateBoostFuzzyQuery() {
		_assertBoost(new FuzzyQueryImpl("test", "test"));
	}

	@Test
	public void testTranslateBoostMatchAllQuery() {
		_assertBoost(new MatchAllQueryImpl());
	}

	@Test
	public void testTranslateBoostMoreLikeThisQueryStringQuery() {
		_assertBoost(
			new MoreLikeThisQueryImpl(Collections.emptyList(), "test"));
	}

	@Test
	public void testTranslateBoostTermQuery() {
		_assertBoost(new TermQueryImpl("test", "test"));
	}

	@Test
	public void testTranslateBoostWildcardQuery() {
		_assertBoost(new WildcardQueryImpl("test", "test"));
	}

	@Test
	public void testTranslateInnerBoostBooleanQuery() {
		BooleanQuery booleanQuery = new BooleanQueryImpl();

		Query query = new MatchAllQueryImpl();

		query.setBoost(_BOOST);

		booleanQuery.addMustQueryClauses(query);

		QueryBuilder queryBuilder = _elasticsearchQueryTranslator.translate(
			booleanQuery);

		BoolQueryBuilder boolQueryBuilder = (BoolQueryBuilder)queryBuilder;

		List<QueryBuilder> mustQueryBuilders = boolQueryBuilder.must();

		QueryBuilder innerQueryBuilder = mustQueryBuilders.get(0);

		Assert.assertEquals(
			innerQueryBuilder.toString(), String.valueOf(_BOOST),
			String.valueOf(innerQueryBuilder.boost()));
	}

	@Test
	public void testTranslateTermsFilterExceedingMaxAllowedTerms() {
		TermsFilter termsFilter = new TermsFilter("groupId");

		termsFilter.addValues("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

		Integer maxTermsCount = QueryUtil.maxTermsCount;

		QueryUtil.maxTermsCount = 10;

		_assertTermsCount(1, termsFilter);

		QueryUtil.maxTermsCount = 5;

		_assertTermsCount(2, termsFilter);

		QueryUtil.maxTermsCount = 3;

		_assertTermsCount(4, termsFilter);

		QueryUtil.maxTermsCount = maxTermsCount;
	}

	@Test
	public void testTranslateTermsQueryExceedingMaxAllowedTerms() {
		TermsQuery termsQuery = new TermsQueryImpl("groupId");

		termsQuery.addValues("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

		Integer maxTermsCount = QueryUtil.maxTermsCount;

		QueryUtil.maxTermsCount = 10;

		_assertTermsCount(1, termsQuery);

		QueryUtil.maxTermsCount = 5;

		_assertTermsCount(2, termsQuery);

		QueryUtil.maxTermsCount = 3;

		_assertTermsCount(4, termsQuery);

		QueryUtil.maxTermsCount = maxTermsCount;
	}

	private void _assertBoost(Query query) {
		query.setBoost(_BOOST);

		QueryBuilder queryBuilder = _elasticsearchQueryTranslator.translate(
			query);

		Assert.assertEquals(
			queryBuilder.toString(), String.valueOf(_BOOST),
			String.valueOf(queryBuilder.boost()));
	}

	private void _assertTermsCount(int expected, TermsFilter termsFilter) {
		String queryString = _elasticsearchFilterTranslator.visit(
			termsFilter
		).toString();

		Assert.assertEquals(
			queryString, expected, StringUtil.count(queryString, "terms"));
	}

	private void _assertTermsCount(int expected, TermsQuery termsQuery) {
		String queryString = _elasticsearchQueryTranslator.visit(
			termsQuery
		).toString();

		Assert.assertEquals(
			queryString, expected, StringUtil.count(queryString, "terms"));
	}

	private static final Float _BOOST = 1.5F;

	private ElasticsearchFilterTranslator _elasticsearchFilterTranslator;
	private ElasticsearchQueryTranslator _elasticsearchQueryTranslator;

}
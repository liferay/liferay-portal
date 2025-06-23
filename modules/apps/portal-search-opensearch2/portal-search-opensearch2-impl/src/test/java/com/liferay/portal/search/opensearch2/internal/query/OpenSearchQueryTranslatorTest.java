/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.query;

import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.internal.query.BooleanQueryImpl;
import com.liferay.portal.search.internal.query.CommonTermsQueryImpl;
import com.liferay.portal.search.internal.query.FuzzyQueryImpl;
import com.liferay.portal.search.internal.query.MatchAllQueryImpl;
import com.liferay.portal.search.internal.query.MoreLikeThisQueryImpl;
import com.liferay.portal.search.internal.query.MultiMatchQueryImpl;
import com.liferay.portal.search.internal.query.TermQueryImpl;
import com.liferay.portal.search.internal.query.TermsQueryImpl;
import com.liferay.portal.search.internal.query.WildcardQueryImpl;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.filter.OpenSearchFilterTranslator;
import com.liferay.portal.search.opensearch2.internal.filter.OpenSearchFilterTranslatorFixture;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;
import com.liferay.portal.search.opensearch2.internal.util.QueryUtil;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;

/**
 * @author Bryan Engler
 */
public class OpenSearchQueryTranslatorTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static final OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		OpenSearchFilterTranslatorFixture openSearchFilterTranslatorFixture =
			new OpenSearchFilterTranslatorFixture(
				new com.liferay.portal.search.opensearch2.internal.legacy.query.
					OpenSearchQueryTranslator(null));

		_openSearchFilterTranslator =
			openSearchFilterTranslatorFixture.getOpenSearchFilterTranslator();

		OpenSearchQueryTranslatorFixture openSearchQueryTranslatorFixture =
			new OpenSearchQueryTranslatorFixture();

		_openSearchQueryTranslator =
			openSearchQueryTranslatorFixture.getOpenSearchQueryTranslator();
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
	public void testTranslateBoostMultiMatchQuery() {
		_assertBoost(new MultiMatchQueryImpl("test", new HashMap<>()));
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

		org.opensearch.client.opensearch._types.query_dsl.Query
			openSearchQuery =
				new org.opensearch.client.opensearch._types.query_dsl.Query(
					_openSearchQueryTranslator.translate(booleanQuery));

		BoolQuery boolQuery = openSearchQuery.bool();

		List<org.opensearch.client.opensearch._types.query_dsl.Query>
			mustQueries = boolQuery.must();

		org.opensearch.client.opensearch._types.query_dsl.Query
			innerOpenSearchQuery = mustQueries.get(0);

		String jsonp = JsonpUtil.toString(innerOpenSearchQuery);

		Assert.assertTrue(
			jsonp, jsonp.contains("\"boost\":" + String.valueOf(_BOOST)));
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

		org.opensearch.client.opensearch._types.query_dsl.Query
			openSearchQuery =
				new org.opensearch.client.opensearch._types.query_dsl.Query(
					_openSearchQueryTranslator.translate(query));

		String jsonp = JsonpUtil.toString(openSearchQuery);

		Assert.assertTrue(
			jsonp, jsonp.contains("\"boost\":" + String.valueOf(_BOOST)));
	}

	private void _assertTermsCount(int expected, TermsFilter termsFilter) {
		String jsonp = JsonpUtil.toString(
			new org.opensearch.client.opensearch._types.query_dsl.Query(
				_openSearchFilterTranslator.visit(termsFilter)));

		Assert.assertEquals(jsonp, expected, StringUtil.count(jsonp, "terms"));
	}

	private void _assertTermsCount(int expected, TermsQuery termsQuery) {
		String jsonp = JsonpUtil.toString(
			new org.opensearch.client.opensearch._types.query_dsl.Query(
				_openSearchQueryTranslator.translate(termsQuery)));

		Assert.assertEquals(jsonp, expected, StringUtil.count(jsonp, "terms"));
	}

	private static final Float _BOOST = 1.5F;

	private OpenSearchFilterTranslator _openSearchFilterTranslator;
	private OpenSearchQueryTranslator _openSearchQueryTranslator;

}
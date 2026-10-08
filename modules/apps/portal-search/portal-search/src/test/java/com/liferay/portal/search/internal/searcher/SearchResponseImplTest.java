/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.searcher;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.SearchHitsBuilder;
import com.liferay.portal.search.hits.TotalHitsRelation;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class SearchResponseImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDefaultsAreNullSafe() {
		SearchResponse searchResponse = new SearchResponseImpl(
			new SearchContext());

		_assertIs(searchResponse.getAggregationResult(null), _nullValue());
		_assertIs(searchResponse.getAggregationResultsMap(), _emptyMap());
		_assertIs(searchResponse.getCount(), _zeroLong());
		_assertIs(searchResponse.getDocuments71(), emptyList());
		_assertIs(searchResponse.getDocuments(), emptyList());
		_assertIs(searchResponse.getFederatedSearchKey(), _blank());
		_assertIs(
			searchResponse.getFederatedSearchResponse(null),
			same(searchResponse));
		_assertIs(
			searchResponse.getFederatedSearchResponses(),
			values -> Assert.assertTrue(values.isEmpty()));
		_assertIs(searchResponse.getGroupByResponses(), emptyList());
		_assertIs(searchResponse.getRequest(), _nullValue());
		_assertIs(searchResponse.getRequestString(), _blank());
		_assertIs(searchResponse.getResponseString(), _blank());
		_assertIs(
			searchResponse.getSearchHits(), _instanceOf(SearchHits.class));
		_assertIs(searchResponse.getStatsResponseMap(), _emptyMap());
		_assertIs(searchResponse.getTotalHits(), _zeroInt());
		_assertIs(
			searchResponse.getTotalHitsRelation(), same(TotalHitsRelation.EQ));
	}

	@Test
	public void testTotalHitsRelationComesFromSearchHits() {
		SearchResponseImpl searchResponseImpl = new SearchResponseImpl(
			new SearchContext());

		SearchHitsBuilder searchHitsBuilder = new SearchHitsBuilder();

		searchResponseImpl.setSearchHits(
			searchHitsBuilder.totalHitsRelation(
				TotalHitsRelation.GTE
			).build());

		Assert.assertEquals(
			TotalHitsRelation.GTE, searchResponseImpl.getTotalHitsRelation());

		searchResponseImpl.setSearchHits(
			searchHitsBuilder.totalHitsRelation(
				TotalHitsRelation.EQ
			).build());

		Assert.assertEquals(
			TotalHitsRelation.EQ, searchResponseImpl.getTotalHitsRelation());
	}

	protected static Consumer<List<?>> emptyList() {
		return list -> Assert.assertEquals("[]", String.valueOf(list));
	}

	protected static Consumer<Object> same(Object expected) {
		return actual -> Assert.assertSame(expected, actual);
	}

	private <T> void _assertIs(T actual, Consumer<T> consumer) {
		consumer.accept(actual);
	}

	private Consumer<String> _blank() {
		return string -> Assert.assertEquals(StringPool.BLANK, string);
	}

	private Consumer<Map<String, ?>> _emptyMap() {
		return map -> Assert.assertEquals("{}", String.valueOf(map));
	}

	private Consumer<Object> _instanceOf(Class<?> clazz) {
		return object -> Assert.assertTrue(clazz.isInstance(object));
	}

	private Consumer<Object> _nullValue() {
		return object -> Assert.assertNull(object);
	}

	private Consumer<Integer> _zeroInt() {
		return value -> Assert.assertEquals(0, value.intValue());
	}

	private Consumer<Long> _zeroLong() {
		return value -> Assert.assertEquals(0, value.longValue());
	}

}
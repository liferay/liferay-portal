/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.highlight;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.function.Consumer;

import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchPhraseQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;

/**
 * @author Olivia Yu
 */
public class HighlightQueryUtil {

	public static Query getHighlightQuery(Query query) {
		if (query == null) {
			return null;
		}

		return _rewriteQuery(query);
	}

	private static boolean _addQueryClauses(
		Consumer<List<Query>> consumer, List<Query> queries) {

		List<Query> newQueries = TransformUtil.transform(
			queries, HighlightQueryUtil::_rewriteQuery);

		if (ListUtil.isEmpty(newQueries)) {
			return false;
		}

		consumer.accept(newQueries);

		return true;
	}

	private static boolean _isProximityQuery(Query query) {
		if (!query.isMatchPhrase()) {
			return false;
		}

		MatchPhraseQuery matchPhraseQuery = query.matchPhrase();

		if (matchPhraseQuery.slop() == null) {
			return false;
		}

		return true;
	}

	private static Query _rewriteQuery(Query query) {
		if (!query.isBool()) {
			return query;
		}

		BoolQuery boolQuery = query.bool();

		BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

		boolean hasScoringClauses = _addQueryClauses(
			boolQueryBuilder::must, boolQuery.must());

		hasScoringClauses |= _addQueryClauses(
			boolQueryBuilder::should,
			ListUtil.filter(
				boolQuery.should(),
				clauseQuery -> !_isProximityQuery(clauseQuery)));

		if (!hasScoringClauses) {
			return null;
		}

		_addQueryClauses(boolQueryBuilder::filter, boolQuery.filter());
		_addQueryClauses(boolQueryBuilder::mustNot, boolQuery.mustNot());

		return new Query(boolQueryBuilder.build());
	}

}
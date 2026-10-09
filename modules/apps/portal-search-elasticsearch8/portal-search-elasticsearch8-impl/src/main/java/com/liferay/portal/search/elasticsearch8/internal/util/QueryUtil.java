/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.util;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQueryBase;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Gustavo Lima
 */
public class QueryUtil {

	public static Integer maxTermsCount = 65536;

	public static List<String> fieldsBoostsToFieldsWithBoosts(
		Map<String, Float> fieldsBoosts) {

		if (MapUtil.isEmpty(fieldsBoosts)) {
			return Collections.emptyList();
		}

		List<String> fieldsWithBoosts = new ArrayList<>();

		MapUtil.isNotEmptyForEach(
			fieldsBoosts,
			(key, value) -> {
				if (value == null) {
					value = 1.0F;
				}

				fieldsWithBoosts.add(key + "^" + value);
			});

		return fieldsWithBoosts;
	}

	public static void setRanges(
		RangeQueryBase.AbstractBuilder builder, boolean includesLower,
		boolean includesUpper, Object lowerTerm, Object upperTerm) {

		if (lowerTerm != null) {
			if (includesLower) {
				builder.gte(lowerTerm);
			}
			else {
				builder.gt(lowerTerm);
			}
		}

		if (upperTerm != null) {
			if (includesUpper) {
				builder.lte(upperTerm);
			}
			else {
				builder.lt(upperTerm);
			}
		}
	}

	public static QueryVariant translateTerms(
		Float boost, String field, String[] terms) {

		if (terms.length <= maxTermsCount) {
			return _getTermsQuery(boost, field, terms);
		}

		BoolQuery.Builder builder = QueryBuilders.bool();

		List<String> termsList = new ArrayList<>();

		for (String term : terms) {
			termsList.add(term);

			if (termsList.size() != maxTermsCount) {
				continue;
			}

			builder.should(
				_getTermsQuery(
					boost, field, termsList.toArray(new String[0])
				)._toQuery());

			termsList.clear();
		}

		if (!termsList.isEmpty()) {
			builder.should(
				_getTermsQuery(
					boost, field, termsList.toArray(new String[0])
				)._toQuery());
		}

		return builder.build();
	}

	private static TermsQuery _getTermsQuery(
		Float boost, String field, String[] values) {

		TermsQuery.Builder builder = QueryBuilders.terms();

		if (boost != null) {
			SetterUtil.setNotNullFloat(builder::boost, boost);
		}

		builder.field(field);

		builder.terms(
			termsQueryField -> {
				List<FieldValue> fieldValues = new ArrayList<>();

				ListUtil.isNotEmptyForEach(
					Arrays.asList(values),
					value -> fieldValues.add(FieldValue.of(value)));

				return termsQueryField.value(fieldValues);
			});

		return builder.build();
	}

}
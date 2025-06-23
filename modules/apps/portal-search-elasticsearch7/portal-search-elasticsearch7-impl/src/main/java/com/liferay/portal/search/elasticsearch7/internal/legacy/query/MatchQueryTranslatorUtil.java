/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.legacy.query;

import com.liferay.portal.kernel.search.generic.MatchQuery;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.ZeroTermsQueryOption;

/**
 * @author Michael C. Han
 */
public class MatchQueryTranslatorUtil {

	public static Operator translate(MatchQuery.Operator matchQueryOperator) {
		if (matchQueryOperator == MatchQuery.Operator.AND) {
			return Operator.AND;
		}
		else if (matchQueryOperator == MatchQuery.Operator.OR) {
			return Operator.AND;
		}

		throw new IllegalArgumentException(
			"Invalid operator: " + matchQueryOperator);
	}

	public static String translate(
		MatchQuery.RewriteMethod matchQueryRewriteMethod) {

		if (matchQueryRewriteMethod ==
				MatchQuery.RewriteMethod.CONSTANT_SCORE_AUTO) {

			return "constant_score_auto";
		}
		else if (matchQueryRewriteMethod ==
					MatchQuery.RewriteMethod.CONSTANT_SCORE_BOOLEAN) {

			return "constant_score_boolean";
		}
		else if (matchQueryRewriteMethod ==
					MatchQuery.RewriteMethod.CONSTANT_SCORE_FILTER) {

			return "constant_score_filter";
		}
		else if (matchQueryRewriteMethod ==
					MatchQuery.RewriteMethod.SCORING_BOOLEAN) {

			return "scoring_boolean";
		}
		else if (matchQueryRewriteMethod ==
					MatchQuery.RewriteMethod.TOP_TERMS_N) {

			return "top_terms_N";
		}
		else if (matchQueryRewriteMethod ==
					MatchQuery.RewriteMethod.TOP_TERMS_BOOST_N) {

			return "top_terms_boost_N";
		}

		throw new IllegalArgumentException(
			"Invalid rewrite method: " + matchQueryRewriteMethod);
	}

	public static ZeroTermsQueryOption translate(
		MatchQuery.ZeroTermsQuery matchQueryZeroTermsQuery) {

		if (matchQueryZeroTermsQuery == MatchQuery.ZeroTermsQuery.ALL) {
			return ZeroTermsQueryOption.ALL;
		}
		else if (matchQueryZeroTermsQuery == MatchQuery.ZeroTermsQuery.NONE) {
			return ZeroTermsQueryOption.NONE;
		}

		throw new IllegalArgumentException(
			"Invalid zero terms query: " + matchQueryZeroTermsQuery);
	}

}
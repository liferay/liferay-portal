/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.util;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.rest.search.filter.BaseFilterVisitor;
import com.liferay.portal.search.rest.search.query.BaseQueryVisitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class FilterUtil {

	public static int[] getStatuses(Filter filter) {
		if (filter == null) {
			return new int[0];
		}

		Collection<Integer> statuses = new HashSet<>();

		filter.accept(
			new BaseFilterVisitor<>() {

				@Override
				public Object visit(BooleanFilter booleanFilter) {
					List<BooleanClause<Filter>> mustBooleanClauses =
						booleanFilter.getMustBooleanClauses();

					mustBooleanClauses.forEach(this::_visitFilterBooleanClause);

					List<BooleanClause<Filter>> shouldBooleanClauses =
						booleanFilter.getShouldBooleanClauses();

					shouldBooleanClauses.forEach(
						this::_visitFilterBooleanClause);

					return null;
				}

				@Override
				public Object visit(QueryFilter queryFilter) {
					Query query = queryFilter.getQuery();

					query.accept(
						new BaseQueryVisitor<>() {

							@Override
							public Object visitQuery(
								BooleanQuery booleanQuery) {

								List<BooleanClause<Query>> booleanClauses =
									booleanQuery.clauses();

								booleanClauses.forEach(
									this::_visitQueryBooleanClause);

								return null;
							}

							@Override
							public Object visitQuery(TermQuery termQuery) {
								QueryTerm queryTerm = termQuery.getQueryTerm();

								if (Objects.equals(
										queryTerm.getField(), "status")) {

									statuses.add(
										GetterUtil.getInteger(
											queryTerm.getValue()));
								}

								return null;
							}

							private void _visitQueryBooleanClause(
								BooleanClause<Query> booleanClause) {

								if (Objects.equals(
										BooleanClauseOccur.SHOULD,
										booleanClause.
											getBooleanClauseOccur())) {

									Query query = booleanClause.getClause();

									query.accept(this);
								}
							}

						});

					return null;
				}

				private void _visitFilterBooleanClause(
					BooleanClause<Filter> booleanClause) {

					Filter filter = booleanClause.getClause();

					filter.accept(this);
				}

			});

		return ArrayUtil.toIntArray(statuses);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.search.query;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.generic.DisMaxQuery;
import com.liferay.portal.kernel.search.generic.FuzzyQuery;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.MoreLikeThisQuery;
import com.liferay.portal.kernel.search.generic.MultiMatchQuery;
import com.liferay.portal.kernel.search.generic.NestedQuery;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.search.query.QueryVisitor;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseQueryVisitor<T> implements QueryVisitor<T> {

	@Override
	public T visitQuery(BooleanQuery booleanQuery) {
		return null;
	}

	@Override
	public T visitQuery(DisMaxQuery disMaxQuery) {
		return null;
	}

	@Override
	public T visitQuery(FuzzyQuery fuzzyQuery) {
		return null;
	}

	@Override
	public T visitQuery(MatchAllQuery matchAllQuery) {
		return null;
	}

	@Override
	public T visitQuery(MatchQuery matchQuery) {
		return null;
	}

	@Override
	public T visitQuery(MoreLikeThisQuery moreLikeThisQuery) {
		return null;
	}

	@Override
	public T visitQuery(MultiMatchQuery multiMatchQuery) {
		return null;
	}

	@Override
	public T visitQuery(NestedQuery nestedQuery) {
		return null;
	}

	@Override
	public T visitQuery(StringQuery stringQuery) {
		return null;
	}

	@Override
	public T visitQuery(TermQuery termQuery) {
		return null;
	}

	@Override
	public T visitQuery(TermRangeQuery termRangeQuery) {
		return null;
	}

	@Override
	public T visitQuery(WildcardQuery wildcardQuery) {
		return null;
	}

}
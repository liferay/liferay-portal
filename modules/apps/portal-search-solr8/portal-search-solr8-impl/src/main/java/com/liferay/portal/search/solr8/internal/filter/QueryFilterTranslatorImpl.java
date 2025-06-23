/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.filter;

import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.query.QueryVisitor;
import com.liferay.portal.search.solr8.internal.query.BaseQueryVisitor;

import org.apache.lucene.search.Query;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = QueryFilterTranslator.class)
public class QueryFilterTranslatorImpl implements QueryFilterTranslator {

	@Override
	public Query translate(QueryFilter queryFilter) {
		com.liferay.portal.kernel.search.Query query = queryFilter.getQuery();

		return query.accept(_queryVisitor);
	}

	private final QueryVisitor<Query> _queryVisitor = new BaseQueryVisitor() {
	};

}
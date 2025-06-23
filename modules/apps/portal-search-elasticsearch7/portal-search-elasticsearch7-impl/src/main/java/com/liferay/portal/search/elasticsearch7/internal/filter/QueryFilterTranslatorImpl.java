/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.filter;

import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.index.IndexNameBuilder;

import org.elasticsearch.index.query.QueryBuilder;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = QueryFilterTranslator.class)
public class QueryFilterTranslatorImpl implements QueryFilterTranslator {

	@Override
	public QueryBuilder translate(QueryFilter queryFilter) {
		return _queryTranslator.translate(queryFilter.getQuery(), null);
	}

	@Activate
	protected void activate() {
		_queryTranslator = new ElasticsearchQueryTranslator(_indexNameBuilder);
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private QueryTranslator<QueryBuilder> _queryTranslator;

}
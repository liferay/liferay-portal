/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.legacy.query;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.filter.ElasticsearchFilterTranslatorFixture;

import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author Michael C. Han
 */
public class ElasticsearchQueryTranslatorFixture {

	public ElasticsearchQueryTranslatorFixture() {
		ElasticsearchFilterTranslatorFixture
			elasticsearchFilterTranslatorFixture =
				new ElasticsearchFilterTranslatorFixture(
					_elasticsearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			_elasticsearchQueryTranslator, "_filterTranslatorSnapshot",
			new Snapshot<FilterTranslator<QueryBuilder>>(null, null) {

				public FilterTranslator<QueryBuilder> get() {
					return elasticsearchFilterTranslatorFixture.
						getElasticsearchFilterTranslator();
				}

			});
	}

	public ElasticsearchQueryTranslator getElasticsearchQueryTranslator() {
		return _elasticsearchQueryTranslator;
	}

	private final ElasticsearchQueryTranslator _elasticsearchQueryTranslator =
		new ElasticsearchQueryTranslator(null);

}
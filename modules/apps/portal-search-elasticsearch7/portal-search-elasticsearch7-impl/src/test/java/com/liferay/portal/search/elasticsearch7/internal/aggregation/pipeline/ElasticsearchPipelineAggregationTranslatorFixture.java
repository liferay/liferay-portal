/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.aggregation.pipeline;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.ElasticsearchSortFieldTranslatorFixture;

/**
 * @author Michael C. Han
 */
public class ElasticsearchPipelineAggregationTranslatorFixture {

	public ElasticsearchPipelineAggregationTranslatorFixture() {
		ElasticsearchPipelineAggregationTranslator
			elasticsearchPipelineAggregationTranslator =
				new ElasticsearchPipelineAggregationTranslator();

		_injectSortFieldTranslators(elasticsearchPipelineAggregationTranslator);

		_elasticsearchPipelineAggregationTranslator =
			elasticsearchPipelineAggregationTranslator;
	}

	public ElasticsearchPipelineAggregationTranslator
		getElasticsearchPipelineAggregationTranslator() {

		return _elasticsearchPipelineAggregationTranslator;
	}

	private void _injectSortFieldTranslators(
		ElasticsearchPipelineAggregationTranslator
			elasticsearchPipelineAggregationTranslator) {

		ElasticsearchSortFieldTranslatorFixture
			elasticsearchSortFieldTranslatorFixture =
				new ElasticsearchSortFieldTranslatorFixture(
					new ElasticsearchQueryTranslator());

		ReflectionTestUtil.setFieldValue(
			elasticsearchPipelineAggregationTranslator, "_sortFieldTranslator",
			elasticsearchSortFieldTranslatorFixture.
				getElasticsearchSortFieldTranslator());
	}

	private final ElasticsearchPipelineAggregationTranslator
		_elasticsearchPipelineAggregationTranslator;

}
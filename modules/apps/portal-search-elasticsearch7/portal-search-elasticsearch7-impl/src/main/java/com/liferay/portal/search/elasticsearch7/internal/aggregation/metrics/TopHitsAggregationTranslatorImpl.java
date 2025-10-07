/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.aggregation.metrics;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.highlight.HighlightTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.script.ScriptTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.ElasticsearchSortFieldTranslator;
import com.liferay.portal.search.query.QueryTranslator;
import com.liferay.portal.search.script.ScriptField;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortFieldTranslator;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = TopHitsAggregationTranslator.class)
public class TopHitsAggregationTranslatorImpl
	implements TopHitsAggregationTranslator {

	@Override
	public TopHitsAggregationBuilder translate(
		TopHitsAggregation topHitsAggregation,
		AggregationTranslator<AggregationBuilder> aggregationTranslator,
		PipelineAggregationTranslator<PipelineAggregationBuilder>
			pipelineAggregationTranslator) {

		TopHitsAggregationBuilder topHitsAggregationBuilder =
			AggregationBuilders.topHits(topHitsAggregation.getName());

		if (topHitsAggregation.getExplain() != null) {
			topHitsAggregationBuilder.explain(topHitsAggregation.getExplain());
		}

		if (ListUtil.isNotEmpty(topHitsAggregation.getSelectedFields())) {
			List<String> selectedFields =
				topHitsAggregation.getSelectedFields();

			selectedFields.forEach(topHitsAggregationBuilder::docValueField);
		}

		if (topHitsAggregation.getFetchSource() != null) {
			topHitsAggregationBuilder.fetchSource(
				topHitsAggregation.getFetchSource());

			if (topHitsAggregation.getFetchSource() &&
				((topHitsAggregation.getFetchSourceInclude() != null) ||
				 (topHitsAggregation.getFetchSourceExclude() != null))) {

				topHitsAggregationBuilder.fetchSource(
					topHitsAggregation.getFetchSourceInclude(),
					topHitsAggregation.getFetchSourceExclude());
			}
		}

		if (topHitsAggregation.getFrom() != null) {
			topHitsAggregationBuilder.from(topHitsAggregation.getFrom());
		}

		if (topHitsAggregation.getHighlight() != null) {
			HighlightBuilder highlightBuilder = _highlightTranslator.translate(
				topHitsAggregation.getHighlight(), _queryTranslator);

			topHitsAggregationBuilder.highlighter(highlightBuilder);
		}

		if (topHitsAggregation.getScriptFields() != null) {
			List<ScriptField> scriptFields =
				topHitsAggregation.getScriptFields();

			List<SearchSourceBuilder.ScriptField>
				searchSourceBuilderScriptFields = new ArrayList<>(
					scriptFields.size());

			scriptFields.forEach(
				scriptField -> {
					Script script = _scriptTranslator.translate(
						scriptField.getScript());

					SearchSourceBuilder.ScriptField
						searchSourceBuilderScriptField =
							new SearchSourceBuilder.ScriptField(
								scriptField.getField(), script,
								scriptField.isIgnoreFailure());

					searchSourceBuilderScriptFields.add(
						searchSourceBuilderScriptField);
				});

			topHitsAggregationBuilder.scriptFields(
				searchSourceBuilderScriptFields);
		}

		if (topHitsAggregation.getSize() != null) {
			topHitsAggregationBuilder.size(topHitsAggregation.getSize());
		}

		if (ListUtil.isNotEmpty(topHitsAggregation.getSortFields())) {
			List<Sort> sorts = topHitsAggregation.getSortFields();

			List<SortBuilder<?>> sortBuilders = new ArrayList<>(sorts.size());

			sorts.forEach(
				sort -> sortBuilders.add(_sortFieldTranslator.translate(sort)));

			topHitsAggregationBuilder.sorts(sortBuilders);
		}

		if (topHitsAggregation.getTrackScores() != null) {
			topHitsAggregationBuilder.trackScores(
				topHitsAggregation.getTrackScores());
		}

		if (topHitsAggregation.getVersion() != null) {
			topHitsAggregationBuilder.version(topHitsAggregation.getVersion());
		}

		return topHitsAggregationBuilder;
	}

	private final HighlightTranslator _highlightTranslator =
		new HighlightTranslator();
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new ElasticsearchQueryTranslator();
	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();
	private final SortFieldTranslator<SortBuilder<?>> _sortFieldTranslator =
		new ElasticsearchSortFieldTranslator();

}
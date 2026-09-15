/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.HistogramBucket;
import com.liferay.osb.faro.rest.dto.v1_0.Metric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse$Metric",
	service = DTOConverter.class
)
public class MetricDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupChannelEventMetricsResponse.Metric, Metric> {

	@Override
	public String getContentType() {
		return Metric.class.getSimpleName();
	}

	@Override
	public Metric toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupChannelEventMetricsResponse.Metric metric) {

		if (metric == null) {
			return null;
		}

		return new Metric() {
			{
				setHistogramBuckets(
					() -> _toHistogramBuckets(metric.getHistogram()));
				setPreviousValue(metric::getPreviousValue);
				setTrendClassification(
					() -> {
						GetWorkspaceGroupChannelEventMetricsResponse.Trend
							trend = metric.getTrend();

						if (trend == null) {
							return null;
						}

						return trend.getTrendClassification();
					});
				setTrendPercentage(
					() -> {
						GetWorkspaceGroupChannelEventMetricsResponse.Trend
							trend = metric.getTrend();

						if (trend == null) {
							return null;
						}

						return trend.getPercentage();
					});
				setValue(metric::getValue);
			}
		};
	}

	private HistogramBucket[] _toHistogramBuckets(
		GetWorkspaceGroupChannelEventMetricsResponse.Histogram histogram) {

		if (histogram == null) {
			return null;
		}

		List<GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric>
			histogramMetrics = histogram.getMetrics();

		if (histogramMetrics == null) {
			return null;
		}

		return TransformUtil.transformToArray(
			histogramMetrics,
			histogramMetric -> new HistogramBucket() {
				{
					setKey(histogramMetric::getKey);
					setValue(histogramMetric::getValue);
				}
			},
			HistogramBucket.class);
	}

}
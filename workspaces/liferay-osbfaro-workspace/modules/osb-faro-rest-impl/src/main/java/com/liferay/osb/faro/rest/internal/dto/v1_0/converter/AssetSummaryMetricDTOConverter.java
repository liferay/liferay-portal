/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AssetSummaryMetric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAssetSummariesPageResponse;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAssetSummariesPageResponse$AssetSummaryMetric",
	service = DTOConverter.class
)
public class AssetSummaryMetricDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupChannelAssetSummariesPageResponse.AssetSummaryMetric,
		 AssetSummaryMetric> {

	@Override
	public String getContentType() {
		return AssetSummaryMetric.class.getSimpleName();
	}

	@Override
	public AssetSummaryMetric toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupChannelAssetSummariesPageResponse.AssetSummaryMetric
			assetSummaryMetric) {

		if (assetSummaryMetric == null) {
			return null;
		}

		return new AssetSummaryMetric() {
			{
				setAssetId(assetSummaryMetric::getAssetId);
				setAssetTitle(assetSummaryMetric::getAssetTitle);
				setAssetType(assetSummaryMetric::getAssetType);
				setDownloads(
					() -> _value(assetSummaryMetric.getDownloadsMetric()));
				setDownloadsTrendPercentage(
					() -> _trendPercentage(
						assetSummaryMetric.getDownloadsMetric()));
				setImpressions(
					() -> _value(assetSummaryMetric.getImpressionsMetric()));
				setImpressionsTrendPercentage(
					() -> _trendPercentage(
						assetSummaryMetric.getImpressionsMetric()));
				setReads(() -> _value(assetSummaryMetric.getReadsMetric()));
				setReadsTrendPercentage(
					() -> _trendPercentage(
						assetSummaryMetric.getReadsMetric()));
				setViews(() -> _value(assetSummaryMetric.getViewsMetric()));
				setViewsTrendPercentage(
					() -> _trendPercentage(
						assetSummaryMetric.getViewsMetric()));
			}
		};
	}

	private Double _trendPercentage(
		GetWorkspaceGroupChannelAssetSummariesPageResponse.Metric metric) {

		if ((metric == null) || (metric.getTrend() == null)) {
			return null;
		}

		return metric.getTrend(
		).getPercentage();
	}

	private Double _value(
		GetWorkspaceGroupChannelAssetSummariesPageResponse.Metric metric) {

		if (metric == null) {
			return null;
		}

		return metric.getValue();
	}

}
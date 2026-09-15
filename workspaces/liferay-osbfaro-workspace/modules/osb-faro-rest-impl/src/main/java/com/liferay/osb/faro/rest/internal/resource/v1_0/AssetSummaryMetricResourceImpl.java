/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.AssetSummaryMetric;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroPaginationUtil;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAssetSummariesPageResponse;
import com.liferay.osb.faro.rest.resource.v1_0.AssetSummaryMetricResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-summary-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetSummaryMetricResource.class
)
public class AssetSummaryMetricResourceImpl
	extends BaseAssetSummaryMetricResourceImpl {

	@Override
	public Page<AssetSummaryMetric> getWorkspaceGroupChannelAssetSummariesPage(
			Long groupId, String channelId, String accountId,
			String individualId, String rangeEnd, String rangeKey,
			String rangeStart, String search, String segmentId,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		int cur = FaroPaginationUtil.getCur(pagination);
		int delta = FaroPaginationUtil.getDelta(pagination);

		GetWorkspaceGroupChannelAssetSummariesPageResponse
			getWorkspaceGroupChannelAssetSummariesPageResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupChannelAssetSummariesPageResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupChannelAssetSummariesPage",
					HashMapBuilder.<String, Object>put(
						"accountIds", _toIds(accountId)
					).put(
						"channelId", channelId
					).put(
						"individualIds", _toIds(individualId)
					).put(
						"keywords", search
					).put(
						"rangeEnd", rangeEnd
					).put(
						"rangeKey", TimeRange.getRangeKey(rangeKey)
					).put(
						"rangeStart", rangeStart
					).put(
						"segmentIds", _toIds(segmentId)
					).put(
						"size", delta
					).put(
						"sort",
						FaroPaginationUtil.toGraphQLSort(
							new Sort("viewsMetric", 6, true), sorts)
					).put(
						"start", (cur - 1) * delta
					).build());

		GetWorkspaceGroupChannelAssetSummariesPageResponse.AssetSummaryMetricBag
			assetSummaryMetricBag =
				getWorkspaceGroupChannelAssetSummariesPageResponse.
					getAssetSummaryMetricBag();

		if (assetSummaryMetricBag == null) {
			return Page.of(Collections.emptyList(), pagination, 0);
		}

		Integer total = assetSummaryMetricBag.getTotal();

		if (total == null) {
			total = 0;
		}

		return Page.of(
			transform(
				assetSummaryMetricBag.getAssetSummaryMetrics(),
				assetSummaryMetric -> _assetSummaryMetricDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						assetSummaryMetric.getAssetId(),
						contextAcceptLanguage.getPreferredLocale()),
					assetSummaryMetric)),
			pagination, total);
	}

	private List<String> _toIds(String id) {
		if (Validator.isNull(id)) {
			return null;
		}

		return Collections.singletonList(id);
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.AssetSummaryMetricDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelAssetSummariesPageResponse.AssetSummaryMetric,
		 AssetSummaryMetric> _assetSummaryMetricDTOConverter;

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}
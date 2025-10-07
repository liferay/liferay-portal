/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext} from 'react';

import {Context} from '../../Context';
import useFetch from '../../hooks/useFetch';
import {AssetTypes, Individuals, MetricName} from '../../types/global';
import {buildQueryString} from '../../utils/buildQueryString';
import {assetMetrics} from '../../utils/metrics';
import {BaseOverviewMetrics, OverviewMetricsData} from '../BaseOverviewMetrics';
import {RangeSelectors} from '../RangeSelectorsDropdown';

interface Data extends OverviewMetricsData {
	assetId: string;
	assetType: AssetTypes;
}

export type AssetMetricProps = {
	assetId: string;
	assetType: string;
	groupId: string;
	individual: Individuals;
	rangeSelector: RangeSelectors;
	selectedMetrics: MetricName[];
};

const ContentDashboardOverviewMetrics = () => {
	const {assetId, assetType, filters, groupId} = useContext(Context);
	const queryString = buildQueryString({
		assetId,
		identityType: filters.individual,
		rangeKey: filters.rangeSelector.rangeKey,
		selectedMetrics: assetMetrics[assetType as AssetTypes],
	});

	const {data, loading} = useFetch<Data>(
		`/o/analytics-reports-rest/v1.0/${groupId}/asset-metrics/${assetType}${queryString}`
	);

	if (loading) {
		return <ClayLoadingIndicator className="my-5" />;
	}

	if (!data) {
		return null;
	}

	return <BaseOverviewMetrics data={data} />;
};

export default ContentDashboardOverviewMetrics;

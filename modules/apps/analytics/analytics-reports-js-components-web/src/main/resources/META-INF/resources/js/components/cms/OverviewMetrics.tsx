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

export type AssetMetricProps = {
	assetId: string;
	assetType: string;
	groupId: string;
	individual: Individuals;
	rangeSelector: RangeSelectors;
	selectedMetrics: MetricName[];
};

const CMSOverviewMetrics = () => {
	const {
		externalReferenceCode,
		filters,
		objectEntryFolderExternalReferenceCode,
	} = useContext(Context);

	const queryString = buildQueryString({
		externalReferenceCode,
		groupId: filters.channel,
		rangeKey: filters.rangeSelector.rangeKey,
		selectedMetrics:
			assetMetrics[objectEntryFolderExternalReferenceCode as AssetTypes],
	});

	const {data, loading} = useFetch<OverviewMetricsData>(
		`/o/analytics-cms-rest/v1.0/object-entry-metric${queryString}`
	);

	if (loading) {
		return <ClayLoadingIndicator />;
	}

	if (!data) {
		return null;
	}

	return (
		<BaseOverviewMetrics
			className="d-flex flex-row justify-content-between"
			data={data}
			small
		/>
	);
};

export default CMSOverviewMetrics;
